package com.tacz.guns.compat.shouldersurfing;

import net.fabricmc.loader.api.FabricLoader;

public final class ShoulderSurfingCompat {
    private static final String MOD_ID = "shouldersurfing";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static boolean showCrosshair() {
        if (INSTALLED) {
            return ShoulderSurfingCompatInner.showCrosshair();
        }
        return false;
    }
}
