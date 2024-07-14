package com.tacz.guns.entity.sync.core;

import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;

/**
 * Author: MrCrayfish.
 * Open source at <a href="https://github.com/MrCrayfish/Framework">Github</a> under LGPL License.
 */
public interface IDataSerializer<T> {
    void write(PacketByteBuf buf, T value);

    T read(PacketByteBuf buf);

    NbtElement write(T value);

    T read(NbtElement nbt);
}
