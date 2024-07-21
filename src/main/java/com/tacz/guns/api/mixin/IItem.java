package com.tacz.guns.api.mixin;

import net.minecraft.item.ItemStack;

public interface IItem {

    int getMaxCount(ItemStack stack);
}
