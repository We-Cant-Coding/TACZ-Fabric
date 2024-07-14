package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface GunDrawEvent {
    Event<GunDrawEvent> EVENT = EventFactory.createArrayBacked(GunDrawEvent.class,
            (listeners) -> (shooter, previousGunItem, gunItemStack, side) -> {
                for (GunDrawEvent listener : listeners) {
                    ActionResult result = listener.gunDraw(shooter, previousGunItem, gunItemStack, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult gunDraw(LivingEntity entity, ItemStack previousGunItem, ItemStack currentGunItem, EnvType side);
}
