package com.tacz.guns.network;

import com.tacz.guns.GunMod;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandshakeNetworking {
    private static final List<PacketInfo<? extends IHandshakeMessage>> HANDSHAKE_PACKETS = new ArrayList<>();
    private static final Map<Identifier, Class<? extends IHandshakeMessage.IResponsePacket>> RES_PACKETS = new HashMap<>();

    public static void init() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            for (PacketInfo<? extends IHandshakeMessage> packetInfo : HANDSHAKE_PACKETS) {
                try {
                    Class<? extends IHandshakeMessage> packetClass = packetInfo.packetClass;
                    IHandshakeMessage packet = packetClass.getDeclaredConstructor().newInstance();
                    sender.sendPacket(packet);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    GunMod.LOGGER.error("{} Handshake packet processing error", packetInfo.type.getId().toString(), e);
                }
            }
        });
    }

    public static <T extends IHandshakeMessage.IResponsePacket> void register(@NotNull Identifier id, @NotNull Class<T> resPacketClass) {
        RES_PACKETS.put(id, resPacketClass);
    }

    public static <T extends IHandshakeMessage> void register(@NotNull PacketType<T> type, @NotNull Class<T> packetClass) {
        HANDSHAKE_PACKETS.add(new PacketInfo<>(type, packetClass));

        ServerLoginNetworking.registerGlobalReceiver(type.getId(), (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (!understood) return; // The client is likely a vanilla client.
            try {
                if (buf.readableBytes() > 0) {
                    Identifier packetId = buf.readIdentifier();
                    Class<? extends IHandshakeMessage.IResponsePacket> ackPacketClass = RES_PACKETS.get(packetId);
                    if (ackPacketClass == null) {
                        GunMod.LOGGER.error("{} Is the handshake response packet registered?", packetId.toString());
                        return;
                    }
                    IHandshakeMessage.IResponsePacket packet = ackPacketClass.getDeclaredConstructor().newInstance();
                    packet.read(buf);
                    packet.handle(responseSender);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                GunMod.LOGGER.error("Handshake response packet processing error in {}", type.getId().toString(), e);
            }
        });

        if (EnvironmentUtil.isClient()) {
            NetworkClientInitializer.registerHandshake(type);
        }
    }

    public record PacketInfo<T extends IHandshakeMessage>(PacketType<T> type, Class<T> packetClass) { }
}
