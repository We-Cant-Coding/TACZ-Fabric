package com.tacz.guns.crafting;

import net.minecraft.item.ItemStack;

public record GunSmithTableResult(ItemStack result, String group) {
    public static final String GUN = "gun";
    public static final String AMMO = "ammo";
    public static final String ATTACHMENT = "attachment";

}
