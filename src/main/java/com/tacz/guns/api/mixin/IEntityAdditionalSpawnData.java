package com.tacz.guns.api.mixin;

import net.minecraft.network.PacketByteBuf;

public interface IEntityAdditionalSpawnData {
    void writeSpawnData(PacketByteBuf buf);

    void readSpawnData(PacketByteBuf buf);
}
