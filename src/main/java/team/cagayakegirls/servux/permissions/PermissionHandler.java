package team.cagayakegirls.servux.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.Nullable;
import fi.dy.masa.servux.Reference;
import org.jspecify.annotations.NonNull;

public class PermissionHandler
{
    private static final List<PermissionNode<?>> ALL_NODES = new ArrayList<>();
    private static final Map<String, PermissionNode<Boolean>> NODE_MAP = new HashMap<>();

    // Main permissions
    public static final PermissionNode<Boolean> MAIN_ADMIN = registerBoolean("main.admin");
    public static final PermissionNode<Boolean> MAIN_EASY_PLACE = registerBoolean("main.easy_place");

    // Entity data permissions
    public static final PermissionNode<Boolean> ENTITY_DATA = registerBoolean("entity_data");
    public static final PermissionNode<Boolean> ENTITY_NBT_QUERY_OVERRIDE = registerBoolean("entity_data.nbt_query_override");
    public static final PermissionNode<Boolean> ENTITY_NBT_ALLOW_PLAYER_INVENTORY = registerBoolean("entity_data.nbt_allow_player_inventory");
    public static final PermissionNode<Boolean> ENTITY_NBT_ALLOW_PLAYER_ENDER_ITEMS = registerBoolean("entity_data.nbt_allow_player_ender_items");

    // HUD data permissions
    public static final PermissionNode<Boolean> HUD_DATA = registerBoolean("hud_data");
    public static final PermissionNode<Boolean> HUD_DATA_WEATHER = registerBoolean("hud_data.weather");
    public static final PermissionNode<Boolean> HUD_DATA_SEED = registerBoolean("hud_data.seed");
    public static final PermissionNode<Boolean> HUD_DATA_LOGGER = registerBoolean("hud_data.logger");

    // Litematic data permissions
    public static final PermissionNode<Boolean> LITEMATIC_DATA = registerBoolean("litematic_data");
    public static final PermissionNode<Boolean> LITEMATIC_DATA_PASTE = registerBoolean("litematic_data.paste");

    // Structure bounding boxes permissions
    public static final PermissionNode<Boolean> STRUCTURE_BOUNDING_BOXES = registerBoolean("structure_bounding_boxes");

    // Tweaks data permissions
    public static final PermissionNode<Boolean> TWEAKS_DATA = registerBoolean("tweaks_data");

    // Command permissions
    public static final PermissionNode<Boolean> COMMANDS = registerBoolean("commands");
    public static final PermissionNode<Boolean> COMMANDS_RELOAD = registerBoolean("commands.reload");
    public static final PermissionNode<Boolean> COMMANDS_SAVE = registerBoolean("commands.save");
    public static final PermissionNode<Boolean> COMMANDS_SET = registerBoolean("commands.set");
    public static final PermissionNode<Boolean> COMMANDS_INFO = registerBoolean("commands.info");
    public static final PermissionNode<Boolean> COMMANDS_LIST = registerBoolean("commands.list");

    private static PermissionNode<Boolean> registerBoolean(String name) {
        PermissionNode<Boolean> node = new PermissionNode<>(
                Identifier.fromNamespaceAndPath(Reference.MOD_ID, name),
                PermissionTypes.BOOLEAN,
                (player, uuid, contexts) -> null // Default: no override, use level-based fallback
        );
        ALL_NODES.add(node);
        NODE_MAP.put(Reference.MOD_ID + "." + name, node);
        return node;
    }


    public static void onInitialize() {
        NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, event -> {
            event.addNodes(ALL_NODES);
        });
    }

    @Nullable
    public static PermissionNode<Boolean> getNode(String fullNodeName) {
        return NODE_MAP.get(fullNodeName);
    }

    public static @NonNull Boolean getPermission(ServerPlayer player, PermissionNode<Boolean> node, PermissionDynamicContext<?>... contexts) {
        return PermissionAPI.getPermission(player, node, contexts);
    }

    public static @NonNull Boolean getOfflinePermission(UUID uuid, PermissionNode<Boolean> node, PermissionDynamicContext<?>... contexts) {
        return PermissionAPI.getOfflinePermission(uuid, node, contexts);
    }
}
