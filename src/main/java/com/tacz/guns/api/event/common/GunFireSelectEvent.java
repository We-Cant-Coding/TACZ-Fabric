package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface GunFireSelectEvent {
    Event<GunFireSelectEvent> EVENT = EventFactory.createArrayBacked(GunFireSelectEvent.class,
            (listeners) -> (shooter, gunItemStack, side) -> {
                for (GunFireSelectEvent listener : listeners) {
                    ActionResult result = listener.gunFireSelect(shooter, gunItemStack, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult gunFireSelect(LivingEntity shooter, ItemStack gunItemStack, EnvType side);
}
