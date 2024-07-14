package com.tacz.guns.inventory.tooltip;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

public record AmmoBoxTooltip(ItemStack ammoBox, ItemStack ammo, int count) implements TooltipData {
}
