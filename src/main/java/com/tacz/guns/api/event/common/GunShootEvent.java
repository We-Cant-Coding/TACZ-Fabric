package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface GunShootEvent {
    Event<GunShootEvent> EVENT = EventFactory.createArrayBacked(GunShootEvent.class,
            (listeners) -> ((shooter, gunItemStack, side) -> {
                for (GunShootEvent listener : listeners) {
                    ActionResult result = listener.gunShoot(shooter, gunItemStack, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }));

    ActionResult gunShoot(LivingEntity shooter, ItemStack gunItemStack, EnvType side);
}
