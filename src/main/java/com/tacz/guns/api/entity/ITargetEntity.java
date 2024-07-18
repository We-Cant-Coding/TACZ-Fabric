package com.tacz.guns.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;

/**
 * Used for special entities that are not {@link LivingEntity} but can be hit by bullets.
 */
public interface ITargetEntity {
    /**
     * @param projectile projectile entity
     * @param result     Position of the hitting entity
     * @param source     Type of source of injury
     * @param damage     damage value
     */
    void onProjectileHit(Entity projectile, EntityHitResult result, DamageSource source, float damage);
}
