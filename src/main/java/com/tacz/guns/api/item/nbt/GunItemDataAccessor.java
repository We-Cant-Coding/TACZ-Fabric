package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface GunItemDataAccessor extends IGun {
    String GUN_ID_TAG = "GunId";
    String GUN_FIRE_MODE_TAG = "GunFireMode";
    String GUN_HAS_BULLET_IN_BARREL = "HasBulletInBarrel";
    String GUN_CURRENT_AMMO_COUNT_TAG = "GunCurrentAmmoCount";
    String GUN_ATTACHMENT_BASE = "Attachment";
    String GUN_EXP_TAG = "GunLevelExp";
    String GUN_DUMMY_AMMO = "DummyAmmo";
    String GUN_MAX_DUMMY_AMMO = "MaxDummyAmmo";
    String GUN_ATTACHMENT_LOCK = "AttachmentLock";

    @Override
    default boolean useDummyAmmo(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        return nbt.contains(GUN_DUMMY_AMMO, NbtElement.INT_TYPE);
    }

    @Override
    default int getDummyAmmoAmount(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        return Math.max(0, nbt.getInt(GUN_DUMMY_AMMO));
    }

    @Override
    default void setDummyAmmoAmount(ItemStack gun, int amount) {
        NbtCompound nbt = gun.getOrCreateNbt();
        nbt.putInt(GUN_DUMMY_AMMO, Math.max(amount, 0));
    }

    @Override
    default void addDummyAmmoAmount(ItemStack gun, int amount) {
        if (!useDummyAmmo(gun)) {
            return;
        }
        if (!hasMaxDummyAmmo(gun)) {
            return;
        }
        NbtCompound nbt = gun.getOrCreateNbt();
        amount = Math.min(getDummyAmmoAmount(gun) + amount, getMaxDummyAmmoAmount(gun));
        nbt.putInt(GUN_DUMMY_AMMO, Math.max(amount, 0));
    }

    @Override
    default boolean hasMaxDummyAmmo(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        return nbt.contains(GUN_MAX_DUMMY_AMMO, NbtElement.INT_TYPE);
    }

    @Override
    default int getMaxDummyAmmoAmount(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        return Math.max(0, nbt.getInt(GUN_MAX_DUMMY_AMMO));
    }

    @Override
    default void setMaxDummyAmmoAmount(ItemStack gun, int amount) {
        NbtCompound nbt = gun.getOrCreateNbt();
        nbt.putInt(GUN_MAX_DUMMY_AMMO, Math.max(amount, 0));
    }

    @Override
    default boolean hasAttachmentLock(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_ATTACHMENT_LOCK, NbtElement.BYTE_TYPE)) {
            return nbt.getBoolean(GUN_ATTACHMENT_LOCK);
        }
        return false;
    }

    @Override
    default void setAttachmentLock(ItemStack gun, boolean lock) {
        NbtCompound nbt = gun.getOrCreateNbt();
        nbt.putBoolean(GUN_ATTACHMENT_LOCK, lock);
    }

    @Override
    @NotNull
    default Identifier getGunId(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_ID_TAG, NbtElement.STRING_TYPE)) {
            Identifier gunId = Identifier.tryParse(nbt.getString(GUN_ID_TAG));
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_GUN_ID);
        }
        return DefaultAssets.EMPTY_GUN_ID;
    }

    @Override
    default void setGunId(ItemStack gun, @Nullable Identifier gunId) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (gunId != null) {
            nbt.putString(GUN_ID_TAG, gunId.toString());
        }
    }

    @Override
    default int getLevel(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_EXP_TAG, NbtElement.INT_TYPE)) {
            return getLevel(nbt.getInt(GUN_EXP_TAG));
        }
        return 0;
    }

    @Override
    default int getExp(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_EXP_TAG, NbtElement.INT_TYPE)) {
            return nbt.getInt(GUN_EXP_TAG);
        }
        return 0;
    }

    @Override
    default int getExpToNextLevel(ItemStack gun) {
        int exp = getExp(gun);
        int level = getLevel(exp);
        if (level >= getMaxLevel()) {
            return 0;
        }
        int nextLevelExp = getExp(level + 1);
        return nextLevelExp - exp;
    }

    @Override
    default int getExpCurrentLevel(ItemStack gun) {
        int exp = getExp(gun);
        int level = getLevel(exp);
        if (level <= 0) {
            return exp;
        } else {
            return exp - getExp(level - 1);
        }
    }

    @Override
    default FireMode getFireMode(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_FIRE_MODE_TAG, NbtElement.STRING_TYPE)) {
            return FireMode.valueOf(nbt.getString(GUN_FIRE_MODE_TAG));
        }
        return FireMode.UNKNOWN;
    }

    @Override
    default void setFireMode(ItemStack gun, @Nullable FireMode fireMode) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (fireMode != null) {
            nbt.putString(GUN_FIRE_MODE_TAG, fireMode.name());
            return;
        }
        nbt.putString(GUN_FIRE_MODE_TAG, FireMode.UNKNOWN.name());
    }

    @Override
    default int getCurrentAmmoCount(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_CURRENT_AMMO_COUNT_TAG, NbtElement.INT_TYPE)) {
            return nbt.getInt(GUN_CURRENT_AMMO_COUNT_TAG);
        }
        return 0;
    }

    @Override
    default void setCurrentAmmoCount(ItemStack gun, int ammoCount) {
        NbtCompound nbt = gun.getOrCreateNbt();
        nbt.putInt(GUN_CURRENT_AMMO_COUNT_TAG, Math.max(ammoCount, 0));
    }

    @Override
    default void reduceCurrentAmmoCount(ItemStack gun) {
        setCurrentAmmoCount(gun, getCurrentAmmoCount(gun) - 1);
    }

    @Override
    @Nullable
    default NbtCompound getAttachmentTag(ItemStack gun, AttachmentType type) {
        if (!allowAttachmentType(gun, type)) {
            return null;
        }
        NbtCompound nbt = gun.getOrCreateNbt();
        String key = GUN_ATTACHMENT_BASE + type.name();
        if (nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
            NbtCompound allItemStackTag = nbt.getCompound(key);
            if (allItemStackTag.contains("tag", NbtElement.COMPOUND_TYPE)) {
                return allItemStackTag.getCompound("tag");
            }
        }
        return null;
    }

    @Override
    @NotNull
    default ItemStack getAttachment(ItemStack gun, AttachmentType type) {
        if (!allowAttachmentType(gun, type)) {
            return ItemStack.EMPTY;
        }
        NbtCompound nbt = gun.getOrCreateNbt();
        String key = GUN_ATTACHMENT_BASE + type.name();
        if (nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
            return ItemStack.fromNbt(nbt.getCompound(key));
        }
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    default Identifier getAttachmentId(ItemStack gun, AttachmentType type) {
        NbtCompound attachmentTag = this.getAttachmentTag(gun, type);
        if (attachmentTag != null) {
            return AttachmentItemDataAccessor.getAttachmentIdFromTag(attachmentTag);
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    @Override
    default void installAttachment(@NotNull ItemStack gun, @NotNull ItemStack attachment) {
        if (!allowAttachment(gun, attachment)) {
            return;
        }
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(attachment);
        if (iAttachment == null) {
            return;
        }
        NbtCompound nbt = gun.getOrCreateNbt();
        String key = GUN_ATTACHMENT_BASE + iAttachment.getType(attachment).name();
        NbtCompound attachmentTag = new NbtCompound();
        attachment.writeNbt(attachmentTag);
        nbt.put(key, attachmentTag);
    }

    @Override
    default void unloadAttachment(@NotNull ItemStack gun, AttachmentType type) {
        if (!allowAttachmentType(gun, type)) {
            return;
        }
        NbtCompound nbt = gun.getOrCreateNbt();
        String key = GUN_ATTACHMENT_BASE + type.name();
        NbtCompound attachmentTag = new NbtCompound();
        ItemStack.EMPTY.writeNbt(attachmentTag);
        nbt.put(key, attachmentTag);
    }

    @Override
    default float getAimingZoom(ItemStack gunItem) {
        float zoom = 1;
        Identifier scopeId = this.getAttachmentId(gunItem, AttachmentType.SCOPE);
        if (!DefaultAssets.isEmptyAttachmentId(scopeId)) {
            NbtCompound attachmentTag = this.getAttachmentTag(gunItem, AttachmentType.SCOPE);
            int zoomNumber = AttachmentItemDataAccessor.getZoomNumberFromTag(attachmentTag);
            float[] zooms = TimelessAPI.getClientAttachmentIndex(scopeId).map(ClientAttachmentIndex::getZoom).orElse(null);
            if (zooms != null) {
                zoom = zooms[zoomNumber % zooms.length];
            }
        } else {
            Identifier gunId = this.getGunId(gunItem);
            zoom = TimelessAPI.getClientGunIndex(gunId).map(ClientGunIndex::getIronZoom).orElse(1f);
        }
        return zoom;
    }

    @Override
    default boolean hasBulletInBarrel(ItemStack gun) {
        NbtCompound nbt = gun.getOrCreateNbt();
        if (nbt.contains(GUN_HAS_BULLET_IN_BARREL, NbtElement.BYTE_TYPE)) {
            return nbt.getBoolean(GUN_HAS_BULLET_IN_BARREL);
        }
        return false;
    }

    @Override
    default void setBulletInBarrel(ItemStack gun, boolean bulletInBarrel) {
        NbtCompound nbt = gun.getOrCreateNbt();
        nbt.putBoolean(GUN_HAS_BULLET_IN_BARREL, bulletInBarrel);
    }
}
