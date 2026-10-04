package team.cagayakegirls.servux;

import fi.dy.masa.servux.Reference;
import fi.dy.masa.servux.Servux;
import team.cagayakegirls.servux.network.NeoForgeServerNetworking;
import team.cagayakegirls.servux.permissions.PermissionsHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = Reference.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class ServuxForged {
    public ServuxForged(IEventBus modEventBus) {
        NeoForgeServerNetworking.initialize(modEventBus);
        PermissionsHelper.onInitialize();
        new Servux().onInitialize();
    }
}
