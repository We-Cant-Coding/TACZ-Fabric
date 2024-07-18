package com.tacz.guns.forge.items;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemHandlerModifiable extends IItemHandler {
    void setStackInSlot(int var1, @NotNull ItemStack var2);
}
