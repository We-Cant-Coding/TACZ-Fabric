package com.tacz.guns.network;

import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class HandshakeNetworking {
    private static final List<IMessage> PACKETS = new ArrayList<>();

    public static void init() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            PACKETS.forEach(sender::sendPacket);
        });
    }

    public static <T extends IMessage> void register(PacketType<T> type, T packet, LoginResponseCallback resCallback) {
        PACKETS.add(packet);

        ServerLoginNetworking.registerGlobalReceiver(type.getId(), (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (!understood) return; // The client is likely a vanilla client.
            resCallback.handle(buf, responseSender);
        });

        if (EnvironmentUtil.isClient()) {
            NetworkClientInitializer.registerHandshake(type);
        }
    }

    public interface LoginResponseCallback {
        void handle(PacketByteBuf buf, PacketSender responseSender);
    }
}
