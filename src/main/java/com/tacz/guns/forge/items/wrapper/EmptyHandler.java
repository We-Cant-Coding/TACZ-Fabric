package com.tacz.guns.forge.items.wrapper;

import com.tacz.guns.forge.items.IItemHandler;
import com.tacz.guns.forge.items.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EmptyHandler implements IItemHandlerModifiable {
    public static final IItemHandler INSTANCE = new EmptyHandler();

    public int getSlots() {
        return 0;
    }

    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
    }

    public int getSlotLimit(int slot) {
        return 0;
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}
