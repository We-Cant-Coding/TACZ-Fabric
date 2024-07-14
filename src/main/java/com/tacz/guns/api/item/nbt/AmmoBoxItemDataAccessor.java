package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public interface AmmoBoxItemDataAccessor extends IAmmoBox {
    String AMMO_ID_TAG = "AmmoId";
    String AMMO_COUNT_TAG = "AmmoCount";
    String CREATIVE_TAG = "Creative";
    String ALL_TYPE_CREATIVE_TAG = "AllTypeCreative";
    String LEVEL_TAG = "Level";

    @Override
    default Identifier getAmmoId(ItemStack ammoBox) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        if (tag.contains(AMMO_ID_TAG, NbtElement.STRING_TYPE)) {
            return new Identifier(tag.getString(AMMO_ID_TAG));
        }
        return DefaultAssets.EMPTY_AMMO_ID;
    }

    @Override
    default void setAmmoId(ItemStack ammoBox, Identifier ammoId) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        tag.putString(AMMO_ID_TAG, ammoId.toString());
    }

    @Override
    default int getAmmoCount(ItemStack ammoBox) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        if (isAllTypeCreative(ammoBox) || isCreative(ammoBox)) {
            return Integer.MAX_VALUE;
        }
        if (tag.contains(AMMO_COUNT_TAG, NbtElement.INT_TYPE)) {
            return tag.getInt(AMMO_COUNT_TAG);
        }
        return 0;
    }

    @Override
    default void setAmmoCount(ItemStack ammoBox, int count) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        if (isCreative(ammoBox)) {
            tag.putInt(AMMO_COUNT_TAG, Integer.MAX_VALUE);
            return;
        }
        tag.putInt(AMMO_COUNT_TAG, count);
    }

    @Override
    default boolean isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox) {
        if (gun.getItem() instanceof IGun iGun && ammoBox.getItem() instanceof IAmmoBox iAmmoBox) {
            if (isAllTypeCreative(ammoBox)) {
                return true;
            }
            Identifier ammoId = iAmmoBox.getAmmoId(ammoBox);
            if (ammoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                return false;
            }
            Identifier gunId = iGun.getGunId(gun);
            return TimelessAPI.getCommonGunIndex(gunId).map(gunIndex -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
        }
        return false;
    }

    @Override
    default ItemStack setAmmoLevel(ItemStack ammoBox, int level) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        tag.putInt(LEVEL_TAG, Math.max(level, 0));
        return ammoBox;
    }

    @Override
    default int getAmmoLevel(ItemStack ammoBox) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        if (tag.contains(LEVEL_TAG, NbtElement.INT_TYPE)) {
            return tag.getInt(LEVEL_TAG);
        }
        return 0;
    }

    @Override
    default boolean isCreative(ItemStack ammoBox) {
        NbtCompound tag = ammoBox.getNbt();
        if (tag != null && tag.contains(CREATIVE_TAG, NbtElement.BYTE_TYPE)) {
            return tag.getBoolean(CREATIVE_TAG);
        }
        return false;
    }

    @Override
    default boolean isAllTypeCreative(ItemStack ammoBox) {
        NbtCompound tag = ammoBox.getNbt();
        if (tag != null && tag.contains(ALL_TYPE_CREATIVE_TAG, NbtElement.BYTE_TYPE)) {
            return tag.getBoolean(ALL_TYPE_CREATIVE_TAG);
        }
        return false;
    }

    @Override
    default ItemStack setCreative(ItemStack ammoBox, boolean isAllType) {
        NbtCompound tag = ammoBox.getOrCreateNbt();
        if (isAllType) {
            // 移除可能存在的创造模式标签
            if (tag.contains(CREATIVE_TAG, NbtElement.BYTE_TYPE)) {
                tag.remove(CREATIVE_TAG);
            }
            tag.putBoolean(ALL_TYPE_CREATIVE_TAG, true);
            return ammoBox;
        }
        // 移除可能存在的全类型标签
        if (tag.contains(ALL_TYPE_CREATIVE_TAG, NbtElement.BYTE_TYPE)) {
            tag.remove(ALL_TYPE_CREATIVE_TAG);
        }
        tag.putBoolean(CREATIVE_TAG, true);
        return ammoBox;
    }
}
