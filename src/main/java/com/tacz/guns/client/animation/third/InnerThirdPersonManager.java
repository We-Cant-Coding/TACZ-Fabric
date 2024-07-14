package com.tacz.guns.client.animation.third;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.other.ThirdPersonManager;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class InnerThirdPersonManager {
    public static void setRotationAnglesHead(LivingEntity entityIn, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float limbSwingAmount) {
        // 游戏暂停时不进行动画计算，否则会 StackOverflow
        if (MinecraftClient.getInstance().isPaused()) {
            return;
        }
        if (entityIn instanceof IGunOperator operator) {
            ItemStack mainHandItem = entityIn.getMainHandStack();
            IGun iGun = IGun.getIGunOrNull(mainHandItem);
            if (iGun == null) {
                PlayerAnimatorCompat.stopAllAnimation(entityIn);
                return;
            }
            // 睡觉、爬梯、游泳、鞘翅飞行不播放第三人称动画
            if (entityIn.getPose() == EntityPose.SLEEPING || entityIn.isClimbing() || entityIn.isSwimming() || entityIn.getPose() == EntityPose.FALL_FLYING) {
                PlayerAnimatorCompat.stopAllAnimation(entityIn);
                return;
            }

            TimelessAPI.getClientGunIndex(iGun.getGunId(mainHandItem)).ifPresent(index -> {
                if (PlayerAnimatorCompat.hasPlayerAnimator3rd(entityIn, index)) {
                    PlayerAnimatorCompat.playAnimation(entityIn, index, limbSwingAmount);
                } else {
                    playVanillaAnimation(entityIn, rightArm, leftArm, body, head, operator, index);
                }
            });
        }
    }

    private static void playVanillaAnimation(LivingEntity entityIn, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, IGunOperator operator, ClientGunIndex index) {
        String animation = index.getThirdPersonAnimation();
        float aimingProgress = operator.getSynAimingProgress();
        if (aimingProgress <= 0) {
            ThirdPersonManager.getAnimation(animation).animateGunHold(entityIn, rightArm, leftArm, body, head);
        } else {
            ThirdPersonManager.getAnimation(animation).animateGunAim(entityIn, rightArm, leftArm, body, head, aimingProgress);
        }
    }
}
