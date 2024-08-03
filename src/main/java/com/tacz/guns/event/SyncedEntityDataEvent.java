package com.tacz.guns.event;

import com.tacz.guns.entity.sync.core.*;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.packets.s2c.UpdateEntityDataS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SyncedEntityDataEvent {

    public static void onStartTracking(Entity entity, ServerPlayerEntity player) {
        if (!player.getWorld().isClient()) {
            DataHolder holder = SyncedEntityData.instance().getDataHolder(entity);
            if (holder != null) {
                List<DataEntry<?, ?>> entries = holder.gatherAll();
                entries.removeIf(entry -> !entry.getKey().syncMode().isTracking());
                if (!entries.isEmpty()) {
                    NetworkHandler.sendToClientPlayer(new UpdateEntityDataS2CPacket(entity.getId(), entries), player);
                }
            }
        }
    }

    public static boolean onPlayerJoinWorld(Entity entity, World world, boolean ignoredLoadedFromDisk) {
        if (entity instanceof PlayerEntity player && !world.isClient()) {
            DataHolder holder = SyncedEntityData.instance().getDataHolder(player);
            if (holder != null) {
                List<DataEntry<?, ?>> entries = holder.gatherAll();
                if (!entries.isEmpty()) {
                    NetworkHandler.sendToClientPlayer(new UpdateEntityDataS2CPacket(player.getId(), entries), player);
                }
            }
        }
        // To cancel an event, you must leave the return value false
        return true;
    }

    public static void copyFromPlayer(ServerPlayerEntity original, ServerPlayerEntity player, boolean alive) {
        DataHolder oldHolder = SyncedEntityData.instance().getDataHolder(original);
        if (oldHolder == null) {
            return;
        }
        DataHolder newHolder = SyncedEntityData.instance().getDataHolder(player);
        if (newHolder == null) {
            return;
        }
        Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>(oldHolder.dataMap);
        if (!alive) {
            dataMap.entrySet().removeIf(entry -> !entry.getKey().persistent());
        }
        newHolder.dataMap = dataMap;
    }

    public static void onServerTick(MinecraftServer ignoredServer) {
        SyncedEntityData instance = SyncedEntityData.instance();

        if (!instance.isDirty()) {
            return;
        }
        List<Entity> dirtyEntities = instance.getDirtyEntities();
        if (dirtyEntities.isEmpty()) {
            instance.setDirty(false);
            return;
        }
        for (Entity entity : dirtyEntities) {
            DataHolder holder = instance.getDataHolder(entity);
            if (holder == null || !holder.isDirty()) {
                continue;
            }
            List<DataEntry<?, ?>> entries = holder.gatherDirty();
            if (entries.isEmpty()) {
                continue;
            }
            List<DataEntry<?, ?>> selfEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isSelf()).collect(Collectors.toList());
            if (!selfEntries.isEmpty() && entity instanceof ServerPlayerEntity) {
                NetworkHandler.sendToClientPlayer(new UpdateEntityDataS2CPacket(entity.getId(), selfEntries), (ServerPlayerEntity) entity);
            }
            List<DataEntry<?, ?>> trackingEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isTracking()).collect(Collectors.toList());
            if (!trackingEntries.isEmpty()) {
                NetworkHandler.sendToTrackingEntity(new UpdateEntityDataS2CPacket(entity.getId(), trackingEntries), entity);
            }
            holder.clean();
        }
        dirtyEntities.clear();
        instance.setDirty(false);
    }
}
