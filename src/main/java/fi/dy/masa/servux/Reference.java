package fi.dy.masa.servux;

import fi.dy.masa.servux.util.StringUtils;
import net.minecraft.SharedConstants;
import team.cagayakegirls.servux.utils.ModPlatform;

import java.nio.file.Path;

public class Reference
{
    public static final String MOD_ID = "servux";
    public static final String MOD_NAME = "Servux";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    public static final String MC_VERSION = SharedConstants.getCurrentVersion().id();
    public static final String MOD_TYPE = "neoforge";
    public static final String MOD_STRING = MOD_ID + "-" + MOD_TYPE + "-" + MC_VERSION + "-" + MOD_VERSION;
    public static final boolean DEV_DEBUG = false;
	public static final boolean ANSI_MODE = DEV_DEBUG;

    public static final Path DEFAULT_RUN_DIR = ModPlatform.getGameDir();
    public static final Path DEFAULT_CONFIG_DIR = ModPlatform.getConfigDir();
}
