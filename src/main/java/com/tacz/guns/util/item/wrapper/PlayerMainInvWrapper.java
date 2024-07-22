package com.tacz.guns.util.item.wrapper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class PlayerMainInvWrapper extends RangedWrapper {
    private final PlayerInventory inventoryPlayer;

    public PlayerMainInvWrapper(PlayerInventory inv) {
        super(new InvWrapper(inv), 0, inv.main.size());
        this.inventoryPlayer = inv;
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest.getCount() != stack.getCount()) {
            ItemStack inSlot = this.getStackInSlot(slot);
            if (!inSlot.isEmpty()) {
                if (this.getInventoryPlayer().player.getWorld().isClient) {
                    inSlot.setBobbingAnimationTime(5);
                } else if (this.getInventoryPlayer().player instanceof ServerPlayerEntity) {
                    this.getInventoryPlayer().player.currentScreenHandler.sendContentUpdates();
                }
            }
        }

        return rest;
    }

    public PlayerInventory getInventoryPlayer() {
        return this.inventoryPlayer;
    }
}
