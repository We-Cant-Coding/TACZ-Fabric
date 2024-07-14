package com.tacz.guns.item;

import com.tacz.guns.init.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class GunSmithTableItem extends BlockItem {
    public GunSmithTableItem() {
        super(ModBlocks.GUN_SMITH_TABLE, (new Item.Settings()).maxCount(1));
    }
}
