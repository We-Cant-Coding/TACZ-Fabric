package com.tacz.guns.api.item;

import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * No gun logic is included here, only the various nbt accesses for guns. <br>
 * You can see the gun logic at {@link AbstractGunItem}
 */
public interface IGun {
    /**
     * @return Returns an explicitly converted instance if the item type is IGun, otherwise returns null.
     */
    @Nullable
    static IGun getIGunOrNull(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.getItem() instanceof IGun iGun) {
            return iGun;
        }
        return null;
    }

    /**
     * Whether or not the owner is holding a gun
     */
    static boolean mainhandHoldGun(LivingEntity livingEntity) {
        return livingEntity.getMainHandStack().getItem() instanceof IGun;
    }

    /**
     * Obtain the firing mode of the primary handgun
     */
    static FireMode getMainhandFireMode(LivingEntity livingEntity) {
        ItemStack mainhandItem = livingEntity.getMainHandStack();
        if (mainhandItem.getItem() instanceof IGun iGun) {
            return iGun.getFireMode(mainhandItem);
        }
        return FireMode.UNKNOWN;
    }

    /**
     * Get targeting magnification
     */
    float getAimingZoom(ItemStack gunItem);

    /**
     * Whether or not to use a "virtual spare" instead of the actual ammo in the backpack when changing ammo in a firearm.
     */
    boolean useDummyAmmo(ItemStack gun);

    /**
     * Get the current "virtual spare" number of rounds for your firearm.
     */
    int getDummyAmmoAmount(ItemStack gun);

    /**
     * Set the current number of "virtual spares" for the firearm.
     */
    void setDummyAmmoAmount(ItemStack gun, int amount);

    /**
     * Adds the current "virtual spare" number of rounds for firearms
     */
    void addDummyAmmoAmount(ItemStack gun, int amount);

    /**
     * Check if the maximum number of "virtual spares" is set.
     */
    boolean hasMaxDummyAmmo(ItemStack gun);

    /**
     * Get the current maximum number of "Virtual Spare Rounds" for the firearm.
     */
    int getMaxDummyAmmoAmount(ItemStack gun);

    /**
     * Set the current maximum number of "Virtual Spare Rounds" for the firearm.
     */
    void setMaxDummyAmmoAmount(ItemStack gun, int amount);

    /**
     * Access to "accessory locks" on firearms
     */
    boolean hasAttachmentLock(ItemStack gun);

    /**
     * Setting "accessory locks" on firearms.
     */
    void setAttachmentLock(ItemStack gun, boolean locked);

    /**
     * Get Gun ID
     */
    @NotNull
    Identifier getGunId(ItemStack gun);

    /**
     * Setting the Gun ID
     */
    void setGunId(ItemStack gun, @Nullable Identifier gunId);

    /**
     * Gets the level corresponding to the entered experience value.
     *
     * @param exp experience value
     * @return corresponding grade
     */
    int getLevel(int exp);

    /**
     * The minimum number of experience points required to get the level entered.
     *
     * @param level hierarchy
     * @return Minimum required experience value
     */
    int getExp(int level);

    /**
     * Returns the maximum level allowed.
     *
     * @return Maximum level
     */
    int getMaxLevel();

    /**
     * Get the current level of the firearm
     */
    int getLevel(ItemStack gun);

    /**
     * Get the full value of accumulated experience.
     *
     * @param gun Enter the item
     * @return Full experience value
     */
    int getExp(ItemStack gun);

    /**
     * Get the experience value needed to get to the next level.
     *
     * @param gun Enter the item
     * @return The amount of experience needed to get to the next level. Returns 0 if the level has been maxed out
     */
    int getExpToNextLevel(ItemStack gun);

    /**
     * Gets the experience value already accumulated at the current level.
     *
     * @param gun Enter the item
     * @return Experience value already accumulated at current level
     */
    int getExpCurrentLevel(ItemStack gun);

    /**
     * Obtaining a fire pattern
     *
     * @param gun gun
     * @return firefighting mode
     */
    FireMode getFireMode(ItemStack gun);

    /**
     * Setting the firing mode
     */
    void setFireMode(ItemStack gun, @Nullable FireMode fireMode);

    /**
     * Get current gun ammo count
     */
    int getCurrentAmmoCount(ItemStack gun);

    /**
     * Setting the current gun ammo count
     */
    void setCurrentAmmoCount(ItemStack gun, int ammoCount);

    /**
     * Reduce the number of ammo for one current firearm
     */
    void reduceCurrentAmmoCount(ItemStack gun);

    /**
     * Gets the accessories of the specified type for the current firearm
     */
    @NotNull
    ItemStack getAttachment(ItemStack gun, AttachmentType type);

    /**
     * Get the NBT data of the current firearm for the specified type of accessory.
     *
     * @return If null, then there is no accessory data
     */
    @Nullable
    NbtCompound getAttachmentTag(ItemStack gun, AttachmentType type);

    /**
     * Get the accessory ID of the gun
     * <p>
     * If it doesn't exist, return DefaultAssets.EMPTY_ATTACHMENT_ID;
     */
    @NotNull
    Identifier getAttachmentId(ItemStack gun, AttachmentType type);

    /**
     * Mounting accessories
     */
    void installAttachment(@NotNull ItemStack gun, @NotNull ItemStack attachment);

    /**
     * Unloading accessories
     */
    void unloadAttachment(@NotNull ItemStack gun, AttachmentType type);

    /**
     * Whether the firearm is allowed to be fitted with this accessory
     */
    boolean allowAttachment(ItemStack gun, ItemStack attachmentItem);

    /**
     * Whether the firearm allows a certain type of accessory
     */
    boolean allowAttachmentType(ItemStack gun, AttachmentType type);

    /**
     * The presence or absence of cartridges in the barrel for firearms that are chambered for firing
     */
    boolean hasBulletInBarrel(ItemStack gun);

    /**
     * Sets the presence or absence of bullets in the barrel of a firearm that is chambered for firing.
     */
    void setBulletInBarrel(ItemStack gun, boolean bulletInBarrel);
}