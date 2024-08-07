package com.tacz.guns.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IHandshakeMessage extends FabricPacket {

    @Nullable
    IResponsePacket handle(ClientConnection connection, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder);

    interface IResponsePacket {
        void write(PacketByteBuf buf);

        void read(PacketByteBuf buf);

        void handle(PacketSender sender);

        Identifier getId();
    }
}
