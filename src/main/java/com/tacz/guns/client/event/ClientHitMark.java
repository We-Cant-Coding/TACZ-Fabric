package com.tacz.guns.client.event;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.entity.TargetMinecart;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ClientHitMark implements EntityHurtByGunEvent, EntityKillByGunEvent {
    public static long lastHitTimestamp = 0;
    public static float damageAmount = 0;

    @Override
    public ActionResult entityHurtByGun(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier, EnvType side) {
        if (side != EnvType.CLIENT) {
            return ActionResult.PASS;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.equals(attacker) && hurtEntity != null) {
            RenderCrosshairEvent.markHitTimestamp();
            if (isHeadShot) {
                RenderCrosshairEvent.markHeadShotTimestamp();
                TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> SoundPlayManager.playHeadHitSound(player, index));
            } else {
                TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> SoundPlayManager.playFleshHitSound(player, index));
            }

            if (hurtEntity instanceof TargetMinecart) {
                if (System.currentTimeMillis() - lastHitTimestamp < RenderConfig.DAMAGE_COUNTER_RESET_TIME.get()) {
                    damageAmount += (baseAmount * headshotMultiplier);
                } else {
                    damageAmount = (baseAmount * headshotMultiplier);
                }
                float distance = player.distanceTo(hurtEntity);
                player.sendMessage(Text.translatable("message.tacz.target_minecart.hit", String.format("%.1f", damageAmount), String.format("%.2f", distance)), true);

                lastHitTimestamp = System.currentTimeMillis();
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult entityKillByGun(@Nullable LivingEntity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, boolean isHeadShot, EnvType side) {
        if (side != EnvType.CLIENT) {
            return ActionResult.PASS;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.equals(attacker)) {
            RenderCrosshairEvent.markKillTimestamp();
            KillAmountOverlay.markTimestamp();
            TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> SoundPlayManager.playKillSound(player, index));
            if (isHeadShot) {
                RenderCrosshairEvent.markHeadShotTimestamp();
            }
        }
        return ActionResult.PASS;
    }
}
