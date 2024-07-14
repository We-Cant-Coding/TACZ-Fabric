package com.tacz.guns.entity.sync.core;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataHolderCapabilityProvider implements ICapabilitySerializable<NbtList> {
    public static final Capability<DataHolder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private final DataHolder holder = new DataHolder();
    private final LazyOptional<DataHolder> optional = LazyOptional.of(() -> this.holder);

    public void invalidate() {
        this.optional.invalidate();
    }

    @Override
    public NbtList serializeNBT() {
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
        return list;
    }

    @Override
    public void deserializeNBT(NbtList listTag) {
        this.holder.dataMap.clear();
        listTag.forEach(entryTag -> {
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, this.optional);
    }
}
