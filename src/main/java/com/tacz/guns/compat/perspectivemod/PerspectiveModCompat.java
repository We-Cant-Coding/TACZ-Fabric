package com.tacz.guns.compat.perspectivemod;

import net.fabricmc.loader.api.FabricLoader;
import pm.c7.perspective.PerspectiveMod;

public class PerspectiveModCompat {
    private static final String MOD_ID = "perspectivemod";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static boolean getPerspectiveEnabled() {
        if (!INSTALLED) return false;
        return PerspectiveMod.INSTANCE.perspectiveEnabled;
    }
}
