package com.tacz.guns.api.client.other;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public interface IThirdPersonAnimation {
    /**
     * Third-person animation: Holding a firearm in the main hand.
     *
     * @param entity   The entity holding the firearm.
     * @param rightArm The model of the right arm.
     * @param leftArm  The model of the left arm.
     * @param body     The model of the body.
     * @param head     The model of the head.
     */
    void animateGunHold(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head);

    /**
     * Third-person animation: Aiming with a firearm.
     *
     * @param entity      The entity holding the firearm.
     * @param rightArm    The model of the right arm.
     * @param leftArm     The model of the left arm.
     * @param body        The model of the body.
     * @param head        The model of the head.
     * @param aimProgress Aim progress, ranging from 0 to 1.
     */
    void animateGunAim(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float aimProgress);
}
