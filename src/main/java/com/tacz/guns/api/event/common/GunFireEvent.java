package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface GunFireEvent {
    Event<GunFireEvent> EVENT = EventFactory.createArrayBacked(GunFireEvent.class,
            (listeners) -> (shooter, gunItemStack, side) -> {
                for (GunFireEvent listener : listeners) {
                    ActionResult result = listener.gunFire(shooter, gunItemStack, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult gunFire(LivingEntity shooter, ItemStack gunItemStack, EnvType side);
}
