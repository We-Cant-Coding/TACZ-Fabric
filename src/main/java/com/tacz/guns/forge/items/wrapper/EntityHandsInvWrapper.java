package com.tacz.guns.forge.items.wrapper;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class EntityHandsInvWrapper extends EntityEquipmentInvWrapper {
    public EntityHandsInvWrapper(LivingEntity entity) {
        super(entity, EquipmentSlot.Type.HAND);
    }
}
