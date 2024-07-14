package com.tacz.guns.entity.sync.core;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.Validate;

public class DataEntry<E extends Entity, T> {
    private final SyncedDataKey<E, T> key;
    private T value;
    private boolean dirty;

    public DataEntry(SyncedDataKey<E, T> key) {
        this.key = key;
        this.value = key.defaultValueSupplier().get();
    }

    public static DataEntry<?, ?> read(PacketByteBuf buffer) {
        SyncedDataKey<?, ?> key = SyncedEntityData.instance().getKey(buffer.readVarInt());
        Validate.notNull(key, "Synced key does not exist for id");
        DataEntry<?, ?> entry = new DataEntry<>(key);
        entry.readValue(buffer);
        return entry;
    }

    public SyncedDataKey<E, T> getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value, boolean dirty) {
        this.value = value;
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clean() {
        this.dirty = false;
    }

    public void write(PacketByteBuf buffer) {
        int id = SyncedEntityData.instance().getInternalId(this.key);
        buffer.writeVarInt(id);
        this.key.serializer().write(buffer, this.value);
    }

    public void readValue(PacketByteBuf buffer) {
        this.value = this.getKey().serializer().read(buffer);
    }

    public NbtElement writeValue() {
        return this.key.serializer().write(this.value);
    }

    public void readValue(NbtElement nbt) {
        this.value = this.key.serializer().read(nbt);
    }
}
