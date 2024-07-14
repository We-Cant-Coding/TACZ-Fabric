package com.tacz.guns.init;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class ModItemRenderer {

    public static void register() {
        BuiltinItemRendererRegistry.INSTANCE.register();
    }
}
