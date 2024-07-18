package com.tacz.guns.client.init;

import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.renderer.item.AmmoItemRenderer;
import com.tacz.guns.client.renderer.item.AttachmentItemRenderer;
import com.tacz.guns.client.renderer.item.GunItemRenderer;
import com.tacz.guns.client.renderer.item.GunSmithTableItemRenderer;
import com.tacz.guns.init.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.registry.Registries;

public class ModItemRenderer {

    public static void itemRenderers() {
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.AMMO, new AmmoItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ATTACHMENT, new AttachmentItemRenderer());
        Registries.ITEM.forEach(item -> {
            if (item instanceof AbstractGunItem) {
                BuiltinItemRendererRegistry.INSTANCE.register(item, new GunItemRenderer());
            }
        });
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.GUN_SMITH_TABLE, new GunSmithTableItemRenderer());
    }
}
