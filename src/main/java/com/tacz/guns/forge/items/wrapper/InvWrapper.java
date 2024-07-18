package com.tacz.guns.forge.items.wrapper;

import com.tacz.guns.forge.items.IItemHandlerModifiable;
import com.tacz.guns.forge.items.ItemHandlerHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record InvWrapper(Inventory inv) implements IItemHandlerModifiable {
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            InvWrapper that = (InvWrapper) o;
            return this.inv().equals(that.inv());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.inv().hashCode();
    }

    public int getSlots() {
        return this.inv().size();
    }

    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return this.inv().getStack(slot);
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = this.inv().getStack(slot);
            int m;
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxCount(), this.getSlotLimit(slot))) {
                    return stack;
                } else if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                    return stack;
                } else if (!this.inv().isValid(slot, stack)) {
                    return stack;
                } else {
                    m = Math.min(stack.getMaxCount(), this.getSlotLimit(slot)) - stackInSlot.getCount();
                    ItemStack copy;
                    if (stack.getCount() <= m) {
                        if (!simulate) {
                            copy = stack.copy();
                            copy.increment(stackInSlot.getCount());
                            this.inv().setStack(slot, copy);
                            this.inv().markDirty();
                        }

                        return ItemStack.EMPTY;
                    } else {
                        stack = stack.copy();
                        if (!simulate) {
                            copy = stack.split(m);
                            copy.increment(stackInSlot.getCount());
                            this.inv().setStack(slot, copy);
                            this.inv().markDirty();
                        } else {
                            stack.decrement(m);
                        }
                        return stack;
                    }
                }
            } else if (!this.inv().isValid(slot, stack)) {
                return stack;
            } else {
                m = Math.min(stack.getMaxCount(), this.getSlotLimit(slot));
                if (m < stack.getCount()) {
                    stack = stack.copy();
                    if (!simulate) {
                        this.inv().setStack(slot, stack.split(m));
                        this.inv().markDirty();
                    } else {
                        stack.decrement(m);
                    }
                    return stack;
                } else {
                    if (!simulate) {
                        this.inv().setStack(slot, stack);
                        this.inv().markDirty();
                    }

                    return ItemStack.EMPTY;
                }
            }
        }
    }

    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = this.inv().getStack(slot);
            if (stackInSlot.isEmpty()) {
                return ItemStack.EMPTY;
            } else if (simulate) {
                if (stackInSlot.getCount() < amount) {
                    return stackInSlot.copy();
                } else {
                    ItemStack copy = stackInSlot.copy();
                    copy.setCount(amount);
                    return copy;
                }
            } else {
                int m = Math.min(stackInSlot.getCount(), amount);
                ItemStack decrStackSize = this.inv().removeStack(slot, m);
                this.inv().markDirty();
                return decrStackSize;
            }
        }
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.inv().setStack(slot, stack);
    }

    public int getSlotLimit(int slot) {
        return this.inv().getMaxCountPerStack();
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.inv().isValid(slot, stack);
    }
}
