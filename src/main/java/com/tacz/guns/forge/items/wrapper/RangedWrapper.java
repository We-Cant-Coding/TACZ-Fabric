package com.tacz.guns.forge.items.wrapper;

import com.google.common.base.Preconditions;
import com.tacz.guns.forge.items.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RangedWrapper implements IItemHandlerModifiable {
    private final IItemHandlerModifiable compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    public int getSlots() {
        return this.maxSlot - this.minSlot;
    }

    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return this.checkSlot(slot) ? this.compose.getStackInSlot(slot + this.minSlot) : ItemStack.EMPTY;
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.checkSlot(slot) ? this.compose.insertItem(slot + this.minSlot, stack, simulate) : stack;
    }

    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.checkSlot(slot) ? this.compose.extractItem(slot + this.minSlot, amount, simulate) : ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (this.checkSlot(slot)) {
            this.compose.setStackInSlot(slot + this.minSlot, stack);
        }

    }

    public int getSlotLimit(int slot) {
        return this.checkSlot(slot) ? this.compose.getSlotLimit(slot + this.minSlot) : 0;
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.checkSlot(slot) ? this.compose.isItemValid(slot + this.minSlot, stack) : false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + this.minSlot < this.maxSlot;
    }
}
