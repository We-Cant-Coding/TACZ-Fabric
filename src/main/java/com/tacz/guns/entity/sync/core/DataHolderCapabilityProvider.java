package com.tacz.guns.entity.sync.core;

import com.tacz.guns.GunMod;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class DataHolderCapabilityProvider implements Component {
    public static final Marker MARKER = MarkerFactory.getMarker("TACZ_DATAHOLDER");
    public static final ComponentKey<DataHolderCapabilityProvider> CAPABILITY = ComponentRegistry.getOrCreate(new Identifier(GunMod.MOD_ID, "synced_entity_data"), DataHolderCapabilityProvider.class);
    private final DataHolder holder = new DataHolder();
    private boolean isValid = true;

    public void invalidate() {
        this.isValid = false;
    }

    public boolean isValid() {
        return this.isValid;
    }

    @Nullable
    public DataHolder getDataHolder() {
        return isValid ? holder : null;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbt) {
        if (isValid) {
            NbtList list = new NbtList();
            this.holder.dataMap.forEach((key, entry) -> {
                if (key.save()) {
                    NbtCompound keyTag = new NbtCompound();
                    keyTag.putString("ClassKey", key.classKey().id().toString());
                    keyTag.putString("DataKey", key.id().toString());
                    keyTag.put("Value", entry.writeValue());
                    list.add(keyTag);
                }
            });
            nbt.put("DataHolderList", list);
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbt) {
        if (nbt.contains("DataHolderList", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("DataHolderList", NbtElement.COMPOUND_TYPE); // 10 is the ID for CompoundTag
            this.holder.dataMap.clear();
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
                this.holder.dataMap.put(syncedDataKey, entry);
            });
        }
    }
}
