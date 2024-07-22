package com.tacz.guns.client.event;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateMachine;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TickAnimationEvent {

    public static void tickAnimation(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        IClientPlayerGunOperator clientGunOperator = IClientPlayerGunOperator.fromLocalPlayer(player);
        ItemStack mainhandItem = player.getMainHandStack();
        if (!(mainhandItem.getItem() instanceof IGun iGun)) {
            return;
        }
        Identifier gunId = iGun.getGunId(mainhandItem);
        TimelessAPI.getClientGunIndex(gunId).ifPresent(gunIndex -> {
            GunAnimationStateMachine animationStateMachine = gunIndex.getAnimationStateMachine();
            if (animationStateMachine == null) {
                return;
            }
            animationStateMachine.setAiming(clientGunOperator.getClientAimingProgress(1) == 1f);
            boolean isShooting = clientGunOperator.getClientShootCoolDown() > 0;

            if (isShooting) {
                // 如果玩家正在射击，只能处于 idle 状态
                animationStateMachine.onShooterIdle();
            } else if (!player.shouldSlowDown() && player.isSprinting()) {
                // 如果玩家正在移动，播放移动动画，否则播放 idle 动画
                animationStateMachine.setOnGround(player.isOnGround()).onShooterRun(player.horizontalSpeed);
            } else if (!player.shouldSlowDown() && player.input.getMovementInput().length() > 0.01) {
                animationStateMachine.setOnGround(player.isOnGround()).onShooterWalk(player.input, player.horizontalSpeed);
            } else {
                animationStateMachine.onShooterIdle();
            }

            Bolt boltType = gunIndex.getGunData().getBolt();
            int ammoCount = iGun.getCurrentAmmoCount(mainhandItem) + (iGun.hasBulletInBarrel(mainhandItem) && boltType != Bolt.OPEN_BOLT ? 1 : 0);
            if (ammoCount < 1) {
                animationStateMachine.onGunCatchBolt();
            } else {
                animationStateMachine.onGunReleaseBolt();
            }
            animationStateMachine.onIdleHoldingPose();
        });
    }
}
