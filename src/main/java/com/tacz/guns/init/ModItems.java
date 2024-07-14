package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.item.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static ModernKineticGunItem MODERN_KINETIC_GUN = register("modern_kinetic_gun", new ModernKineticGunItem());
    public static Item AMMO = register("ammo", new AmmoItem());
    public static AttachmentItem ATTACHMENT = register("attachment", new AttachmentItem());
    public static Item GUN_SMITH_TABLE = register("gun_smith_table", new GunSmithTableItem());
    public static Item TARGET = register("target", new BlockItem(ModBlocks.TARGET, new Item.Settings()));
    public static Item STATUE = register("statue", new BlockItem(ModBlocks.STATUE, new Item.Settings()));
    public static Item AMMO_BOX = register("ammo_box", new AmmoBoxItem());
    public static Item TARGET_MINECART = register("target_minecart", new TargetMinecartItem());

    private static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(GunMod.MOD_ID, path), item);
    }
}
