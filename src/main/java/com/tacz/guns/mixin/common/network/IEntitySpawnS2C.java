package com.tacz.guns.mixin.common.network;

import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public interface IEntitySpawnS2C {
    @Nullable
    PacketByteBuf tacz$buf();
}
