package com.tacz.guns.forge.items.wrapper;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper {
    public EntityArmorInvWrapper(LivingEntity entity) {
        super(entity, EquipmentSlot.Type.ARMOR);
    }
}
