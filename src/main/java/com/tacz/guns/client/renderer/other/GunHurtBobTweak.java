package com.tacz.guns.client.renderer.other;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class GunHurtBobTweak {
    private static long hurtByGunTimeStamp = -1L;
    private static float lastTweakMultiplier = 0.05f;

    public static boolean onHurtBobTweak(ClientPlayerEntity player, MatrixStack matrixStack, float partialTicks) {
        // 原版受伤的时长是 500 ms，所以如果大于 500 ms，那么说明不是子弹造成的伤害了
        if (System.currentTimeMillis() - hurtByGunTimeStamp > 500) {
            // 返回 false，让程序调用原版受伤晃动
            return false;
        }
        float zRot = (float) player.hurtTime - partialTicks;
        if (zRot < 0) {
            return true;
        }
        zRot /= (float) player.maxHurtTime;
        zRot = MathHelper.sin(zRot * zRot * zRot * zRot * (float) Math.PI);
        float yRot = player.getDamageTiltYaw();

        yRot = yRot * lastTweakMultiplier;
        zRot = zRot * lastTweakMultiplier;

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yRot));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-zRot * 14.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
        return true;
    }

    public static void markTimestamp(float tweakMultiplier) {
        hurtByGunTimeStamp = System.currentTimeMillis();
        lastTweakMultiplier = tweakMultiplier;
    }
}
