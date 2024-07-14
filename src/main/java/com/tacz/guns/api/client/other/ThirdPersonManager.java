package com.tacz.guns.api.client.other;

import com.google.common.collect.Maps;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

/**
 * Simple third person gun animation Manager
 */
public final class ThirdPersonManager {
    private static final Map<String, IThirdPersonAnimation> CACHE = Maps.newHashMap();
    private static final String RESERVED_DEFAULT_NAME = "default";
    private static final IThirdPersonAnimation DEFAULT = new IThirdPersonAnimation() {
        @Override
        public void animateGunHold(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head) {
            rightArm.yaw = -0.3F + head.yaw;
            leftArm.yaw = 0.8F + head.yaw;
            rightArm.pitch = -1.4F + head.pitch;
            leftArm.pitch = -1.4F + head.pitch;
        }

        @Override
        public void animateGunAim(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float aimProgress) {
            float lerp1 = MathHelper.lerp(aimProgress, 0.3f, 0.35f);
            float lerp2 = MathHelper.lerp(aimProgress, 1.4f, 1.6f);
            rightArm.yaw = -lerp1 + head.yaw;
            leftArm.yaw = 0.8F + head.yaw;
            rightArm.pitch = -lerp2 + head.pitch;
            leftArm.pitch = -lerp2 + head.pitch;
        }
    };

    public static void registerDefault() {
        CACHE.put(RESERVED_DEFAULT_NAME, DEFAULT);
    }

    public static void register(String name, IThirdPersonAnimation animation) {
        if (name.equals(RESERVED_DEFAULT_NAME)) {
            return;
        }
        CACHE.put(name, animation);
    }

    public static IThirdPersonAnimation getAnimation(String name) {
        return CACHE.getOrDefault(name, DEFAULT);
    }
}
