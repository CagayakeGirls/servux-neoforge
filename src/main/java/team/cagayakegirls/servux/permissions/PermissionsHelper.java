package team.cagayakegirls.servux.permissions;

import fi.dy.masa.servux.Reference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class PermissionsHelper {
    private static final List<PermissionNode<?>> ALL_NODES = new ArrayList<>();
    private static final Map<String, PermissionNode<Boolean>> NODE_MAP = new HashMap<>();
    private static boolean initialized;

    // Configurable OP thresholds must be supplied on each query, including after a config reload.
    public static final PermissionDynamicContextKey<PermissionLevel> DEFAULT_REQUIRED_LEVEL =
            new PermissionDynamicContextKey<>(PermissionLevel.class, "servux_default_required_level",
                    level -> Integer.toString(level.id()));

    // Main permissions
    public static final PermissionNode<Boolean> MAIN_ADMIN = registerBoolean("main.admin", 3);
    public static final PermissionNode<Boolean> MAIN_EASY_PLACE = registerBoolean("main.easy_place", 0);

    // Entity data permissions
    public static final PermissionNode<Boolean> ENTITY_DATA = registerBoolean("entity_data.data_provider", 0);
    public static final PermissionNode<Boolean> ENTITY_NBT_QUERY_OVERRIDE = registerBoolean("entity_data.nbt_query_override", 2);
    public static final PermissionNode<Boolean> ENTITY_NBT_ALLOW_PLAYER_INVENTORY = registerBoolean("entity_data.nbt_allow_player_inventory", 2);
    public static final PermissionNode<Boolean> ENTITY_NBT_ALLOW_PLAYER_ENDER_ITEMS = registerBoolean("entity_data.nbt_allow_player_ender_items", 2);

    // HUD data permissions
    public static final PermissionNode<Boolean> HUD_DATA = registerBoolean("hud_data.data_provider", 0);
    public static final PermissionNode<Boolean> HUD_DATA_WEATHER = registerBoolean("hud_data.weather", 0);
    public static final PermissionNode<Boolean> HUD_DATA_SEED = registerBoolean("hud_data.seed", 2);
    public static final PermissionNode<Boolean> HUD_DATA_LOGGER = registerBoolean("hud_data.logger", 0);
    public static final PermissionNode<Boolean> HUD_DATA_LOGGER_TPS = registerBoolean("hud_data.logger.tps", 0);
    public static final PermissionNode<Boolean> HUD_DATA_LOGGER_MOB_CAPS = registerBoolean("hud_data.logger.mob_caps", 0);

    // Litematic data permissions
    public static final PermissionNode<Boolean> LITEMATIC_DATA = registerBoolean("litematic_data.data_provider", 0);
    public static final PermissionNode<Boolean> LITEMATIC_DATA_PASTE = registerBoolean("litematic_data.paste", 0);
    public static final PermissionNode<Boolean> LITEMATIC_DATA_TASK_FILL = registerBoolean("litematic_data.task.fill", 0);
    public static final PermissionNode<Boolean> LITEMATIC_DATA_TASK_DELETE = registerBoolean("litematic_data.task.delete", 0);

    // Structure bounding boxes permissions
    public static final PermissionNode<Boolean> STRUCTURE_BOUNDING_BOXES = registerBoolean("structure_bounding_boxes.data_provider", 0);

    // Tweaks data permissions
    public static final PermissionNode<Boolean> TWEAKS_DATA = registerBoolean("tweaks_data.data_provider", 0);

    // Command permissions
    public static final PermissionNode<Boolean> COMMANDS = registerBoolean("command", 3);
    public static final PermissionNode<Boolean> COMMANDS_ABOUT = registerBoolean("command.about", 3);
    public static final PermissionNode<Boolean> COMMANDS_RELOAD = registerBoolean("command.reload", 4);
    public static final PermissionNode<Boolean> COMMANDS_SAVE = registerBoolean("command.save", 4);
    public static final PermissionNode<Boolean> COMMANDS_SET = registerBoolean("command.set", 4);
    public static final PermissionNode<Boolean> COMMANDS_INFO = registerBoolean("command.info", 4);
    public static final PermissionNode<Boolean> COMMANDS_LIST = registerBoolean("command.list", 4);

    public static synchronized void onInitialize() {
        if (!initialized) {
            NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, event -> event.addNodes(ALL_NODES));
            initialized = true;
        }
    }

    @Nullable
    public static PermissionNode<Boolean> getNode(String fullNodeName) {
        // Servux's existing identifiers use a colon; NeoForge permission names use dots.
        return NODE_MAP.get(fullNodeName.replace(':', '.'));
    }

    public static @NonNull Boolean getPermission(ServerPlayer player, PermissionNode<Boolean> node, PermissionDynamicContext<?>... contexts) {
        return PermissionAPI.getPermission(player, node, contexts);
    }

    public static @NonNull Boolean getOfflinePermission(UUID uuid, PermissionNode<Boolean> node, PermissionDynamicContext<?>... contexts) {
        return PermissionAPI.getOfflinePermission(uuid, node, contexts);
    }

    public static boolean check(@NotNull Entity entity, @NotNull String permission, PermissionLevel defaultRequiredLevel) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(permission, "permission");
        Objects.requireNonNull(defaultRequiredLevel, "permissionLevel");
        if (entity instanceof ServerPlayer player) {
            return checkPlayerPermission(player, permission, defaultRequiredLevel);
        }
        if (entity instanceof Player player) {
            return defaultRequiredLevel == PermissionLevel.ALL ||
                    player.permissions().hasPermission(new Permission.HasCommandLevel(defaultRequiredLevel));
        }
        // Fabric's entity permission context assigns level zero to non-player entities.
        return PermissionLevel.ALL.isEqualOrHigherThan(defaultRequiredLevel);
    }

    public static @NotNull Predicate<CommandSourceStack> require(@NotNull String permission, PermissionLevel defaultRequiredLevel) {
        Objects.requireNonNull(permission, "permission");
        Objects.requireNonNull(defaultRequiredLevel, "defaultRequiredLevel");
        return player -> check(player, permission, defaultRequiredLevel);
    }

    public static boolean check(CommandSourceStack source, String node, @NotNull PermissionLevel defaultRequiredLevel) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(node, "node");
        Objects.requireNonNull(defaultRequiredLevel, "defaultRequiredLevel");
        ServerPlayer player = source.getPlayer();

        if (player != null) {
            return checkPlayerPermission(player, node, defaultRequiredLevel);
        }

        // For non-player command sources (e.g. command blocks, server console),
        // use the permission level directly
        Permission permission = new Permission.HasCommandLevel(defaultRequiredLevel);
        return source.permissions().hasPermission(permission);
    }

    private static boolean checkPlayerPermission(ServerPlayer player, String node, @NotNull PermissionLevel defaultRequiredLevel) {
        @Nullable PermissionNode<Boolean> permNode = getNode(node);

        if (permNode != null && PermissionAPI.getRegisteredNodes().contains(permNode)) {
            return PermissionAPI.getPermission(player, permNode,
                    DEFAULT_REQUIRED_LEVEL.createContext(defaultRequiredLevel));
        }

        // Fall back to level-based permission check
        Permission permission = new Permission.HasCommandLevel(defaultRequiredLevel);
        return defaultRequiredLevel == PermissionLevel.ALL || player.permissions().hasPermission(permission);
    }

    private static PermissionNode<Boolean> registerBoolean(String name, int defaultLevel) {
        PermissionNode<Boolean> node = new PermissionNode<>(
                Identifier.fromNamespaceAndPath(Reference.MOD_ID, name),
                PermissionTypes.BOOLEAN,
                (player, uuid, contexts) -> {
                    PermissionLevel requiredLevel = PermissionLevel.byId(defaultLevel);
                    for (PermissionDynamicContext<?> context : contexts) {
                        if (DEFAULT_REQUIRED_LEVEL.equals(context.getDynamic())) {
                            requiredLevel = (PermissionLevel) context.getValue();
                            break;
                        }
                    }
                    return player != null && (requiredLevel == PermissionLevel.ALL ||
                            player.permissions().hasPermission(new Permission.HasCommandLevel(requiredLevel)));
                },
                DEFAULT_REQUIRED_LEVEL
        );
        ALL_NODES.add(node);
        NODE_MAP.put(node.getNodeName(), node);
        return node;
    }
}
