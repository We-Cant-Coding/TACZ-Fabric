package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IGun;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface AmmoItemDataAccessor extends IAmmo {
    String AMMO_ID_TAG = "AmmoId";

    @Override
    @NotNull
    default Identifier getAmmoId(ItemStack ammo) {
        NbtCompound nbt = ammo.getOrCreateNbt();
        if (nbt.contains(AMMO_ID_TAG, NbtElement.STRING_TYPE)) {
            Identifier gunId = Identifier.tryParse(nbt.getString(AMMO_ID_TAG));
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_AMMO_ID);
        }
        return DefaultAssets.EMPTY_AMMO_ID;
    }

    @Override
    default void setAmmoId(ItemStack ammo, @Nullable Identifier ammoId) {
        NbtCompound nbt = ammo.getOrCreateNbt();
        if (ammoId != null) {
            nbt.putString(AMMO_ID_TAG, ammoId.toString());
            return;
        }
        nbt.putString(AMMO_ID_TAG, DefaultAssets.DEFAULT_AMMO_ID.toString());
    }

    @Override
    default boolean isAmmoOfGun(ItemStack gun, ItemStack ammo) {
        if (gun.getItem() instanceof IGun iGun && ammo.getItem() instanceof IAmmo iAmmo) {
            Identifier gunId = iGun.getGunId(gun);
            Identifier ammoId = iAmmo.getAmmoId(ammo);
            return TimelessAPI.getCommonGunIndex(gunId).map(gunIndex -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
        }
        return false;
    }
}
