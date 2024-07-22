package com.tacz.guns.util.item.wrapper;

import com.tacz.guns.util.item.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CombinedInvWrapper implements IItemHandlerModifiable {
    protected final IItemHandlerModifiable[] itemHandler;
    protected final int[] baseIndex;
    protected final int slotCount;

    public CombinedInvWrapper(IItemHandlerModifiable... itemHandler) {
        this.itemHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;

        for(int i = 0; i < itemHandler.length; ++i) {
            index += itemHandler[i].getSlots();
            this.baseIndex[i] = index;
        }

        this.slotCount = index;
    }

    protected int getIndexForSlot(int slot) {
        if (slot >= 0) {
            for (int i = 0; i < this.baseIndex.length; ++i) {
                if (slot - this.baseIndex[i] < 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index) {
        return index >= 0 && index < this.itemHandler.length ? this.itemHandler[index] : (IItemHandlerModifiable)EmptyHandler.INSTANCE;
    }

    protected int getSlotFromIndex(int slot, int index) {
        return index > 0 && index < this.baseIndex.length ? slot - this.baseIndex[index - 1] : slot;
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        handler.setStackInSlot(slot, stack);
    }

    public int getSlots() {
        return this.slotCount;
    }

    @NotNull
    public ItemStack getStackInSlot(int slot) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slot);
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }

    public int getSlotLimit(int slot) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(slot, index);
        return handler.isItemValid(localSlot, stack);
    }
}
