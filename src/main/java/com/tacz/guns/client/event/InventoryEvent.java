package com.tacz.guns.client.event;

import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventoryEvent {
    // 用于切枪逻辑
    private static int oldHotbarSelected = -1;
    private static ItemStack oldHotbarSelectItem = ItemStack.EMPTY;

    public static void onPlayerChangeSelect(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        // 玩家切换选中框的情况
        if (oldHotbarSelected != inventory.selectedSlot) {
            if (oldHotbarSelected == -1) {
                IClientPlayerGunOperator.fromLocalPlayer(player).draw(ItemStack.EMPTY);
            } else {
                IClientPlayerGunOperator.fromLocalPlayer(player).draw(inventory.getStack(oldHotbarSelected));
            }
            oldHotbarSelected = inventory.selectedSlot;
            oldHotbarSelectItem = inventory.getStack(inventory.selectedSlot).copy();
            return;
        }
        // 玩家选中的物品改变的情况
        ItemStack currentItem = inventory.getStack(inventory.selectedSlot);
        if (!ItemStack.areEqual(oldHotbarSelectItem, currentItem)) {
            if (!isSame(oldHotbarSelectItem, currentItem)) {
                IClientPlayerGunOperator.fromLocalPlayer(player).draw(oldHotbarSelectItem);
            }
            oldHotbarSelectItem = currentItem.copy();
        }
    }

    public static void onSwapItemWithOffHand(SwapItemWithOffHand event) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        IClientPlayerGunOperator.fromLocalPlayer(player).draw(player.getOffHandStack());
    }

    public static void onPlayerLoggedOut() {
        // 离开游戏时重置客户端 draw 状态
        oldHotbarSelected = -1;
        oldHotbarSelectItem = ItemStack.EMPTY;
    }

    private static boolean isSame(ItemStack i, ItemStack j) {
        IGun iGun1 = IGun.getIGunOrNull(i);
        IGun iGun2 = IGun.getIGunOrNull(j);
        if (iGun1 != null && iGun2 != null) {
            return iGun1.getGunId(i).equals(iGun2.getGunId(j));
        }
        if (i.isEmpty() || j.isEmpty()) {
            return i.isEmpty() && j.isEmpty();
        }
        return ItemStack.areEqual(i, j);
    }
}
