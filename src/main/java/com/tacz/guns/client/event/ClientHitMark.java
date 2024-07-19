package com.tacz.guns.client.event;

import com.tacz.guns.api.LogicalSide;
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

public class ClientHitMark implements EntityHurtByGunEvent.Callback, EntityKillByGunEvent.Callback {
    public static long lastHitTimestamp = 0;
    public static float damageAmount = 0;

    @Override
    public void onEntityHurtByGun(EntityHurtByGunEvent event) {
        LogicalSide logicalSide = event.getLogicalSide();
        if (logicalSide != LogicalSide.CLIENT) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Entity hurtEntity = event.getHurtEntity();
        if (player != null && player.equals(attacker) && hurtEntity!=null) {
            Identifier gunId = event.getGunId();
            RenderCrosshairEvent.markHitTimestamp();
            if (event.isHeadShot()) {
                RenderCrosshairEvent.markHeadShotTimestamp();
                TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> SoundPlayManager.playHeadHitSound(player, index));
            } else {
                TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> SoundPlayManager.playFleshHitSound(player, index));
            }

            if(hurtEntity instanceof TargetMinecart){
                if(System.currentTimeMillis() - lastHitTimestamp < RenderConfig.DAMAGE_COUNTER_RESET_TIME.get()) {
                    damageAmount += event.getAmount();
                } else {
                    damageAmount = event.getAmount();
                }
                float distance = player.distanceTo(event.getHurtEntity());
                player.sendMessage(Text.translatable("message.tacz-fabric.target_minecart.hit", String.format("%.1f", damageAmount), String.format("%.2f", distance)), true);

                lastHitTimestamp = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onEntityKillByGun(EntityKillByGunEvent event) {
        LogicalSide logicalSide = event.getLogicalSide();
        if (logicalSide != LogicalSide.CLIENT) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.equals(attacker)) {
            RenderCrosshairEvent.markKillTimestamp();
            KillAmountOverlay.markTimestamp();
            TimelessAPI.getClientGunIndex(event.getGunId()).ifPresent(index -> SoundPlayManager.playKillSound(player, index));
            if (event.isHeadShot()) {
                RenderCrosshairEvent.markHeadShotTimestamp();
            }
        }
    }
}
