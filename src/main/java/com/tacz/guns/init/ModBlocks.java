package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.block.GunSmithTableBlock;
import com.tacz.guns.block.StatueBlock;
import com.tacz.guns.block.TargetBlock;
import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static Block GUN_SMITH_TABLE = register("gun_smith_table", new GunSmithTableBlock());
    public static Block TARGET = register("target", new TargetBlock());
    public static Block STATUE = register("statue", new StatueBlock());

    public static BlockEntityType<GunSmithTableBlockEntity> GUN_SMITH_TABLE_BE = register("gun_smith_table", GunSmithTableBlockEntity.TYPE);
    public static BlockEntityType<TargetBlockEntity> TARGET_BE = register("target", TargetBlockEntity.TYPE);
    public static BlockEntityType<StatueBlockEntity> STATUE_BE = register("statue", StatueBlockEntity.TYPE);

    public static void init() {
    }

    private static Block register(String path, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(GunMod.MOD_ID, path), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, BlockEntityType<T> entityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(GunMod.MOD_ID, path), entityType);
    }
}
