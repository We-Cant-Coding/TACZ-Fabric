package com.tacz.guns.api.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public interface IGunOperator {
    /**
     * LivingEntity implements this interface by means of Mixin.
     */
    static IGunOperator fromLivingEntity(LivingEntity entity) {
        return (IGunOperator) entity;
    }

    /**
     * Get the coolness of the shots synchronized from the server side
     */
    long getSynShootCoolDown();

    /**
     * Getting cooldowns for melee combat synchronized from the server (mainly bayonets)
     */
    long getSynMeleeCoolDown();

    /**
     * Get the cooling of the cut gun synchronized from the server side
     */
    long getSynDrawCoolDown();

    /**
     * Get the cooling of the manual bullet change synchronized from the server side
     */
    long getSynBoltCoolDown();

    /**
     * Get the status of the bullet change synchronized from the server
     */
    ReloadState getSynReloadState();

    /**
     * Get the targeting progress synchronized from the server
     */
    float getSynAimingProgress();

    /**
     * Gets whether the entity is being targeted.
     * Note that this method is not equivalent to getSynAimingProgress() > 0.
     * Aiming progress increases if the player is aiming, otherwise aiming progress decreases.
     */
    boolean getSynIsAiming();

    /**
     * Gets the length of time a player runs with a gun.
     * The maximum will not be greater than the sprintTime set in the gun data,
     * and the minimum will not be less than 0.
     */
    float getSynSprintTime();

    /**
     * Initialize individual data for gun operations, such as changeover cooldowns, fire cooldowns, and so on.
     */
    void initialData();

    /**
     * Server-side Gun Cutting Logic
     */
    void draw(Supplier<ItemStack> itemStackSupplier);

    /**
     * Server-side bolt pulling logic
     */
    void bolt();

    /**
     * Server-side bullet change logic
     */
    void reload();

    /**
     * Logic for switching firing modes on the server side
     */
    void fireSelect();

    /**
     * Server-side logic for adjusting the multiplier
     */
    void zoom();

    /**
     * The logic of server-side melee combat (bayonets)
     */
    void melee();

    /**
     * From the position of the entity, fire in the specified direction
     *
     * @param pitch Pitch angle in the direction of fire (i.e., xRot )
     * @param yaw   Yaw angle in the direction of fire (i.e. yRot )
     * @return Results of this shooting
     */
    ShootResult shoot(Supplier<Float> pitch, Supplier<Float> yaw);

    /**
     * server-side, whether this operator is affected by the number of munitions
     *
     * @return If false, then no ammo is checked when firing, either in the player's backpack or in the firearm.
     */
    boolean needCheckAmmo();

    /**
     * Service side, whether firing consumes ammunition or not
     *
     * @return If false, then firing does not deplete the gun's ammo.
     */
    boolean consumesAmmoOrNot();

    /**
     * Server-side, application targeting logic
     *
     * @param isAim whether or not to aim
     */
    void aim(boolean isAim);
}
