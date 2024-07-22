package com.tacz.guns.client.event;

import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.client.renderer.other.GunHurtBobTweak;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PlayerHurtByGunEvent {

    public static void onPlayerHurtByGun(EntityHurtByGunEvent event) {
        LogicalSide logicalSide = event.getLogicalSide();
        if (logicalSide != LogicalSide.CLIENT) {
            return;
        }
        Entity hurtEntity = event.getHurtEntity();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        // 当受伤的是自己的时候，触发受伤晃动的调整参数
        if (player != null && player.equals(hurtEntity)) {
            Identifier gunId = event.getGunId();
            TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
                float tweakMultiplier = index.getGunData().getHurtBobTweakMultiplier();
                GunHurtBobTweak.markTimestamp(tweakMultiplier);
            });
        }
    }
}
