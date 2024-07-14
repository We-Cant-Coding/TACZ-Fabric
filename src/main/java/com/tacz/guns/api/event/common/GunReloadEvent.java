package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface GunReloadEvent {
    Event<GunReloadEvent> EVENT = EventFactory.createArrayBacked(GunReloadEvent.class,
            (listeners) -> (entity, gunItemStack, side) -> {
                for (GunReloadEvent listener : listeners) {
                    ActionResult result = listener.gunReload(entity, gunItemStack, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult gunReload(LivingEntity entity, ItemStack gunItemStack, EnvType side);
}
