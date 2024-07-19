package com.tacz.guns.event.ammo;

import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class BellRing implements AmmoHitBlockEvent.Callback {
    @Override
    public void onAmmoHitBlock(AmmoHitBlockEvent event) {
        World level = event.getLevel();
        BlockState state = event.getState();
        BlockHitResult hitResult = event.getHitResult();
        if (state.getBlock() instanceof BellBlock bell) {
            bell.ring(level, hitResult.getBlockPos(), hitResult.getSide());
        }
    }
}
