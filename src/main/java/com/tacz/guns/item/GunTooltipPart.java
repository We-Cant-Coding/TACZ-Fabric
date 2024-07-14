package com.tacz.guns.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public enum GunTooltipPart {
    DESCRIPTION,
    AMMO_INFO,
    BASE_INFO,
    EXTRA_DAMAGE_INFO,
    UPGRADES_TIP,
    PACK_INFO;

    private final int mask = 1 << this.ordinal();

    public int getMask() {
        return this.mask;
    }

    public static int getHideFlags(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains("HideFlags", NbtElement.NUMBER_TYPE)) {
            return tag.getInt("HideFlags");
        }
        return 0;
    }

    public static void setHideFlags(ItemStack stack, int mask) {
        stack.getOrCreateNbt().putInt("HideFlags", mask);
    }
}
