package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface EntityKillByGunEvent {
    Event<EntityKillByGunEvent> EVENT = EventFactory.createArrayBacked(EntityKillByGunEvent.class,
            (listeners) -> (hurtEntity, attacker, gunId, isHeadShot, side) -> {
                for (EntityKillByGunEvent listener : listeners) {
                    ActionResult result = listener.entityKillByGun(hurtEntity, attacker, gunId, isHeadShot, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult entityKillByGun(@Nullable LivingEntity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, boolean isHeadShot, EnvType side);
}
