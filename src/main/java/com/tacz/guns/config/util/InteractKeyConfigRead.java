package com.tacz.guns.config.util;

import com.google.common.collect.Lists;
import com.tacz.guns.GunMod;
import com.tacz.guns.config.sync.SyncConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumMap;
import java.util.List;

public class InteractKeyConfigRead {
    private static final EnumMap<Type, List<Identifier>> WHITELIST = new EnumMap<>(Type.class);
    private static final EnumMap<Type, List<Identifier>> BLACKLIST = new EnumMap<>(Type.class);
    private static final TagKey<Block> WHITELIST_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(GunMod.MOD_ID, "interact_key/whitelist"));
    private static final TagKey<Block> BLACKLIST_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(GunMod.MOD_ID, "interact_key/blacklist"));
    private static final TagKey<EntityType<?>> WHITELIST_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(GunMod.MOD_ID, "interact_key/whitelist"));
    private static final TagKey<EntityType<?>> BLACKLIST_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(GunMod.MOD_ID, "interact_key/blacklist"));

    public static void init() {
        WHITELIST.clear();
        BLACKLIST.clear();
        handleConfigData(SyncConfig.INTERACT_KEY_WHITELIST_BLOCKS.get(), WHITELIST, Type.BLOCK);
        handleConfigData(SyncConfig.INTERACT_KEY_WHITELIST_ENTITIES.get(), WHITELIST, Type.ENTITY);
        handleConfigData(SyncConfig.INTERACT_KEY_BLACKLIST_BLOCKS.get(), BLACKLIST, Type.BLOCK);
        handleConfigData(SyncConfig.INTERACT_KEY_BLACKLIST_ENTITIES.get(), BLACKLIST, Type.ENTITY);
    }

    public static boolean canInteractBlock(BlockState block) {
        Identifier blockId = Registries.BLOCK.getId(block.getBlock());
        // 先检查黑名单
        if (BLACKLIST.containsKey(Type.BLOCK) && BLACKLIST.get(Type.BLOCK).contains(blockId)) {
            return false;
        }
        if (block.isIn(BLACKLIST_BLOCKS)) {
            return false;
        }
        // 再检查白名单
        if (WHITELIST.containsKey(Type.BLOCK) && WHITELIST.get(Type.BLOCK).contains(blockId)) {
            return true;
        }
        return block.isIn(WHITELIST_BLOCKS);
    }

    public static boolean canInteractEntity(Entity entity) {
        Identifier entityId = Registries.ENTITY_TYPE.getId(entity.getType());
        // 先检查黑名单
        if (BLACKLIST.containsKey(Type.ENTITY) && BLACKLIST.get(Type.ENTITY).contains(entityId)) {
            return false;
        }
        if (entity.getType().isIn(BLACKLIST_ENTITIES)) {
            return false;
        }
        // 再检查白名单
        if (WHITELIST.containsKey(Type.ENTITY) && WHITELIST.get(Type.ENTITY).contains(entityId)) {
            return true;
        }
        return entity.getType().isIn(WHITELIST_ENTITIES);
    }

    private static void handleConfigData(List<String> configData, EnumMap<Type, List<Identifier>> storeList, Type type) {
        configData.forEach(data -> {
            if (data.isEmpty()) {
                return;
            }
            if (StringUtils.isBlank(data)) {
                return;
            }
            Identifier id = new Identifier(data);
            storeList.computeIfAbsent(type, t -> Lists.newArrayList()).add(id);
        });
    }

    public enum Type {
        BLOCK, ENTITY;
    }
}
