package com.tacz.guns.api.event.server;

import com.tacz.guns.entity.EntityKineticBullet;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface AmmoHitBlockEvent {
    Event<AmmoHitBlockEvent> EVENT = EventFactory.createArrayBacked(AmmoHitBlockEvent.class,
            (listeners) -> (world, hitResult, state, ammo) -> {
                for (AmmoHitBlockEvent listener : listeners) {
                    ActionResult result = listener.ammoHitBlock(world, hitResult, state, ammo);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult ammoHitBlock(World world, BlockHitResult hitResult, BlockState state, EntityKineticBullet ammo);
}
