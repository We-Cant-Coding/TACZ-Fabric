package com.tacz.guns.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IMessage extends FabricPacket {

    @Nullable
    PacketByteBuf handle(ClientConnection connection, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder);
}
