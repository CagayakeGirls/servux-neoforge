package team.cagayakegirls.servux.network;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import fi.dy.masa.servux.Servux;
import fi.dy.masa.servux.network.IPluginServerPlayHandler;

/**
 * Native NeoForge transport for the Servux server networking API.
 *
 * <p>Payload codecs must be queued before NeoForge fires
 * {@link RegisterPayloadHandlersEvent}. Receiver callbacks can be registered and
 * unregistered at runtime, matching the old global-receiver behavior.</p>
 */
public final class NeoForgeServerNetworking
{
    // Match MaFgLib's transport version; Servux packet protocol versions are separate.
    private static final String NETWORK_VERSION = "1";
    private static final Map<Identifier, PayloadRegistration<?>> PAYLOADS = new LinkedHashMap<>();
    private static final Map<Identifier, IPayloadHandler<?>> RECEIVERS = new ConcurrentHashMap<>();
    private static boolean initialized;
    private static boolean registrationClosed;

    private NeoForgeServerNetworking() {}

    public static synchronized void initialize(IEventBus modEventBus)
    {
        if (initialized == false)
        {
            modEventBus.addListener(RegisterPayloadHandlersEvent.class, NeoForgeServerNetworking::registerPayloads);
            initialized = true;
        }
    }

    public static synchronized <T extends CustomPacketPayload> boolean registerPlayPayload(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            int direction)
    {
        if (registrationClosed)
        {
            Servux.LOGGER.error("Cannot register payload [{}] after NeoForge payload registration has closed", type.id());
            return false;
        }

        PayloadRegistration<?> existing = PAYLOADS.get(type.id());

        if (existing != null)
        {
            if (existing.type != type || existing.codec != codec)
            {
                Servux.LOGGER.error("Payload ID [{}] is already registered with a different type or codec", type.id());
                return false;
            }

            existing.addDirection(direction);
            return true;
        }

        PAYLOADS.put(type.id(), new PayloadRegistration<>(type, codec, direction));
        return true;
    }

    public static synchronized <T extends CustomPacketPayload> boolean registerPlayReceiver(
            CustomPacketPayload.Type<T> type,
            IPayloadHandler<T> receiver)
    {
        PayloadRegistration<?> registration = PAYLOADS.get(type.id());

        if (registration == null || registration.acceptsServerbound() == false)
        {
            throw new IllegalArgumentException("No serverbound play payload is registered for " + type.id());
        }

        return RECEIVERS.putIfAbsent(type.id(), receiver) == null;
    }

    @Nullable
    public static IPayloadHandler<?> unregisterPlayReceiver(Identifier id)
    {
        return RECEIVERS.remove(id);
    }

    public static Set<Identifier> getPlayReceivers()
    {
        return Collections.unmodifiableSet(RECEIVERS.keySet());
    }

    public static synchronized Set<Identifier> getSendablePayloads(ServerPlayer player)
    {
        Set<Identifier> result = ConcurrentHashMap.newKeySet();

        for (PayloadRegistration<?> registration : PAYLOADS.values())
        {
            if (registration.clientbound && canSend(player, registration.type))
            {
                result.add(registration.type.id());
            }
        }

        return Collections.unmodifiableSet(result);
    }

    public static boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type)
    {
        return player.connection != null && NetworkRegistry.hasChannel(player.connection, type.id());
    }

    public static void send(ServerPlayer player, CustomPacketPayload payload)
    {
        PacketDistributor.sendToPlayer(player, payload);
    }

    private static synchronized void registerPayloads(RegisterPayloadHandlersEvent event)
    {
        // PayloadRegistrar runs handlers on the main thread by default.
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION).optional();

        for (PayloadRegistration<?> registration : PAYLOADS.values())
        {
            registration.register(registrar);
        }

        registrationClosed = true;
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void receive(T payload, IPayloadContext context)
    {
        IPayloadHandler<T> receiver = (IPayloadHandler<T>) RECEIVERS.get(payload.type().id());

        if (receiver != null)
        {
            receiver.handle(payload, context);
        }
    }

    private static void ignoreClientbound(CustomPacketPayload payload, IPayloadContext context)
    {
        Servux.LOGGER.debug("Ignoring clientbound payload [{}] on the Servux server transport", payload.type().id());
    }

    private static final class PayloadRegistration<T extends CustomPacketPayload>
    {
        private final CustomPacketPayload.Type<T> type;
        private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
        private boolean clientbound;
        private boolean serverbound;

        private PayloadRegistration(CustomPacketPayload.Type<T> type,
                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                    int direction)
        {
            this.type = type;
            this.codec = codec;
            this.addDirection(direction);
        }

        private void addDirection(int direction)
        {
            switch (direction)
            {
                case IPluginServerPlayHandler.TO_SERVER, IPluginServerPlayHandler.FROM_CLIENT -> this.serverbound = true;
                case IPluginServerPlayHandler.FROM_SERVER, IPluginServerPlayHandler.TO_CLIENT -> this.clientbound = true;
                default ->
                {
                    this.clientbound = true;
                    this.serverbound = true;
                }
            }
        }

        private boolean acceptsServerbound()
        {
            return this.serverbound;
        }

        private void register(PayloadRegistrar registrar)
        {
            if (this.clientbound && this.serverbound)
            {
                registrar.playBidirectional(this.type, this.codec,
                        NeoForgeServerNetworking::receive,
                        NeoForgeServerNetworking::ignoreClientbound);
            }
            else if (this.serverbound)
            {
                registrar.playToServer(this.type, this.codec, NeoForgeServerNetworking::receive);
            }
            else
            {
                registrar.playToClient(this.type, this.codec, NeoForgeServerNetworking::ignoreClientbound);
            }
        }
    }
}
