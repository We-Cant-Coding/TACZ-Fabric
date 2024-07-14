package com.tacz.guns.debug;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class GunMeleeDebug {
    public static void showRange(LivingEntity user, int distance, Vec3d centrePos, Vec3d eyeVec, float rangeAngle) {
        if (!(user.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        // 起点坐标
        int half = distance / 2;
        Vec3d startPos = user.getEyePos().subtract(half, half, half);
        // 先尝试生成 distance^3 范围的坐标
        for (int i = 0; i < distance * 2; i++) {
            for (int j = 0; j < distance * 2; j++) {
                for (int k = 0; k < distance * 2; k++) {
                    // 待检查的坐标
                    Vec3d tmpPos = startPos.add(i / 2.0, j / 2.0, k / 2.0);
                    // 待检查的坐标->球心向量
                    Vec3d targetVec = tmpPos.subtract(centrePos);
                    // 目标到球心距离
                    double targetLength = targetVec.length();
                    // 距离在一倍距离之内的，在玩家背后，不进行生成
                    if (targetLength < distance) {
                        continue;
                    }
                    // 计算出向量夹角
                    double degree = Math.toDegrees(Math.acos(targetVec.dotProduct(eyeVec) / (targetLength * distance)));
                    // 向量夹角在范围内的，生成粒子
                    if (degree < (rangeAngle / 2)) {
                        serverWorld.spawnParticles(ParticleTypes.FLAME, tmpPos.x, tmpPos.y, tmpPos.z, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
