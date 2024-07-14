package com.tacz.guns.resource.pojo.data.gun;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public enum InaccuracyType {
    /**
     * stand still
     */
    @SerializedName("stand")
    STAND,
    /**
     * mobility
     */
    @SerializedName("move")
    MOVE,
    /**
     * Sneaking, thought to be a half-crouch in other FPS games
     */
    @SerializedName("sneak")
    SNEAK,
    /**
     * Get down. The original does get down.
     */
    @SerializedName("lie")
    LIE,
    /**
     * targeting state
     */
    @SerializedName("aim")
    AIM;

    /**
     * Get current inaccuracy status
     *
     * @param livingEntity shooters
     * @return Inadmissibility
     */
    public static InaccuracyType getInaccuracyType(LivingEntity livingEntity) {
        float aimingProgress = IGunOperator.fromLivingEntity(livingEntity).getSynAimingProgress();
        // Aiming has the highest priority
        if (aimingProgress == 1.0f) {
            return InaccuracyType.AIM;
        }
        // MOJANG's marvelous design, the name of the downward-facing pose is SWIMMING!
        if (!livingEntity.isSwimming() && livingEntity.getPose() == EntityPose.SWIMMING) {
            return InaccuracyType.LIE;
        }
        if (livingEntity.getPose() == EntityPose.CROUCHING) {
            return InaccuracyType.SNEAK;
        }
        if (isMove(livingEntity)) {
            return InaccuracyType.MOVE;
        }
        return InaccuracyType.STAND;
    }

    public static Map<InaccuracyType, Float> getDefaultInaccuracy() {
        Map<InaccuracyType, Float> inaccuracy = Maps.newHashMap();
        inaccuracy.put(InaccuracyType.STAND, 5f);
        inaccuracy.put(InaccuracyType.MOVE, 5.75f);
        inaccuracy.put(InaccuracyType.SNEAK, 3.5f);
        inaccuracy.put(InaccuracyType.LIE, 2.5f);
        inaccuracy.put(InaccuracyType.AIM, 0.15f);
        return inaccuracy;
    }

    private static boolean isMove(LivingEntity livingEntity) {
        // FIXME This judgment doesn't always work, and it's very problematic.
        double distance = Math.abs(livingEntity.horizontalSpeed - livingEntity.prevHorizontalSpeed);
        return distance > 0.05f;
    }

    public boolean isAim() {
        return this == AIM;
    }
}
