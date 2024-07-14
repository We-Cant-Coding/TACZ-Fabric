package com.tacz.guns.client.event;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PlayerHurtByGunEvent implements EntityHurtByGunEvent {

    @Override
    public ActionResult entityHurtByGun(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier, EnvType side) {
        if (side != EnvType.CLIENT) {
            return ActionResult.PASS;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        // 当受伤的是自己的时候，触发受伤晃动的调整参数
        if (player != null && player.equals(hurtEntity)) {
            TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
                float tweakMultiplier = index.getGunData().getHurtBobTweakMultiplier();
                GunHurtBobTweak.markTimestamp(tweakMultiplier);
            });
        }
    }
}
