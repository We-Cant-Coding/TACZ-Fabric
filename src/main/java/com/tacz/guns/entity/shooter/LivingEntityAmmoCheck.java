package com.tacz.guns.entity.shooter;

import com.tacz.guns.config.common.GunConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LivingEntityAmmoCheck {
    private final LivingEntity shooter;

    public LivingEntityAmmoCheck(LivingEntity shooter) {
        this.shooter = shooter;
    }

    public boolean needCheckAmmo() {
        if (shooter instanceof PlayerEntity player) {
            return !player.isCreative();
        }
        return true;
    }

    public boolean consumesAmmoOrNot() {
        if (shooter instanceof PlayerEntity player) {
            return !player.isCreative() || GunConfig.CREATIVE_PLAYER_CONSUME_AMMO.get();
        }
        return true;
    }
}
