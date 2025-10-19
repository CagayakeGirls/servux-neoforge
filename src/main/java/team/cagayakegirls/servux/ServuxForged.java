package team.cagayakegirls.servux;

import fi.dy.masa.servux.Reference;
import fi.dy.masa.servux.Servux;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Reference.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class ServuxForged {
    public ServuxForged() {
        new Servux().onInitialize();
    }
}
