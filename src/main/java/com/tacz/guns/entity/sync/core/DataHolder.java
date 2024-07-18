package com.tacz.guns.entity.sync.core;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataHolder implements Component {
    public Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>();
    private boolean dirty = false;

    @SuppressWarnings("unchecked")
    public <E extends Entity, T> boolean set(E entity, SyncedDataKey<?, ?> key, T value) {
        DataEntry<E, T> entry = (DataEntry<E, T>) this.dataMap.computeIfAbsent(key, DataEntry::new);
        if (!entry.getValue().equals(value)) {
            boolean dirty = !entity.getWorld().isClient() && entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE;
            entry.setValue(value, dirty);
            this.dirty = dirty;
            return true;
        }
        return false;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <E extends Entity, T> T get(SyncedDataKey<E, T> key) {
        return (T) this.dataMap.computeIfAbsent(key, DataEntry::new).getValue();
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clean() {
        this.dirty = false;
        this.dataMap.forEach((key, entry) -> entry.clean());
    }

    public List<DataEntry<?, ?>> gatherDirty() {
        return this.dataMap.values().stream().filter(DataEntry::isDirty).filter(entry -> entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE).collect(Collectors.toList());
    }

    public List<DataEntry<?, ?>> gatherAll() {
        return this.dataMap.values().stream().filter(entry -> entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE).collect(Collectors.toList());
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        if (tag.contains("dataMap", NbtElement.LIST_TYPE)) {
            NbtList list = tag.getList("dataMap", NbtElement.COMPOUND_TYPE);
            dataMap.clear();
            list.forEach(entryTag -> {
                NbtCompound keyTag = (NbtCompound) entryTag;
                Identifier classKey = Identifier.tryParse(keyTag.getString("ClassKey"));
                Identifier dataKey = Identifier.tryParse(keyTag.getString("DataKey"));
                NbtElement value = keyTag.get("Value");
                SyncedClassKey<?> syncedClassKey = SyncedEntityData.instance().getClassKey(classKey);
                if (syncedClassKey == null) {
                    return;
                }
                SyncedDataKey<?, ?> syncedDataKey = SyncedEntityData.instance().getKey(syncedClassKey, dataKey);
                if (syncedDataKey == null || !syncedDataKey.save()) {
                    return;
                }
                DataEntry<?, ?> entry = new DataEntry<>(syncedDataKey);
                entry.readValue(value);
                dataMap.put(syncedDataKey, entry);
            });
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        NbtList list = new NbtList();
        dataMap.forEach((key, entry) -> {
            if (key.save()) {
                NbtCompound keyTag = new NbtCompound();
                keyTag.putString("ClassKey", key.classKey().id().toString());
                keyTag.putString("DataKey", key.id().toString());
                keyTag.put("Value", entry.writeValue());
                list.add(keyTag);
            }
        });
        tag.put("dataMap", list);
    }
}
