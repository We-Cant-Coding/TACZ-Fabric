package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface GunMeleeEvent {
    Event<GunMeleeEvent> EVENT = EventFactory.createArrayBacked(GunMeleeEvent.class,
            (listeners) -> (shooter, gunItemStack, side) -> {
                for (GunMeleeEvent listener : listeners) {
                    ActionResult result = listener.gunMelee(shooter, gunItemStack, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult gunMelee(LivingEntity shooter, ItemStack gunItemStack, EnvType side);
}
