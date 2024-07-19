package com.tacz.guns.network;

import com.tacz.guns.network.message.*;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.World;

import java.util.Objects;

public class NetworkHandler {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerShoot.TYPE, ClientMessagePlayerShoot::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerReloadGun.TYPE, ClientMessagePlayerReloadGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerFireSelect.TYPE, ClientMessagePlayerFireSelect::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerAim.TYPE, ClientMessagePlayerAim::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerDrawGun.TYPE, ClientMessagePlayerDrawGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageCraft.TYPE, ClientMessageCraft::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerZoom.TYPE, ClientMessagePlayerZoom::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageRefitGun.TYPE, ClientMessageRefitGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerBoltGun.TYPE, ClientMessagePlayerBoltGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageUnloadAttachment.TYPE, ClientMessageUnloadAttachment::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerMelee.TYPE, ClientMessagePlayerMelee::handle);

        if (EnvironmentUtil.isClient()) {
            NetworkClientInitializer.init();
        }
    }

    @Environment(EnvType.CLIENT)
    public static <T extends FabricPacket> void sendToServer(T message) {
        ClientPlayNetworking.send(message);
    }

    public static <T extends FabricPacket> void sendToClientPlayer(T message, PlayerEntity player) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, message);
    }

    /**
     * Sent to all players listening to this entity
     */
    public static <T extends FabricPacket> void sendToTrackingEntityAndSelf(Entity centerEntity, T message) {
        ((ServerChunkManager)centerEntity.getEntityWorld().getChunkManager())
                .sendToNearbyPlayers(centerEntity, toVanillaPacket(message));
    }

    public static <T extends FabricPacket> void sendToAllPlayers(MinecraftServer server, T message) {
        server.getPlayerManager().sendToAll(toVanillaPacket(message));
    }

    public static <T extends FabricPacket> void sendToTrackingEntity(T message, final Entity centerEntity) {
        ((ServerChunkManager)centerEntity.getEntityWorld().getChunkManager())
                .sendToOtherNearbyPlayers(centerEntity, toVanillaPacket(message));
    }

    public static <T extends FabricPacket> void sendToDimension(T message, final Entity centerEntity) {
        RegistryKey<World> dimension = centerEntity.getWorld().getRegistryKey();
        var server = centerEntity.getServer();
        if (server != null) {
            server.getPlayerManager().sendToDimension(toVanillaPacket(message), dimension);
        }
    }

    public static Packet<ClientPlayPacketListener> getEntitySpawningPacket(Entity entity) {
        return toVanillaPacket(new ServerMessageSpawnEntity(entity));
    }

    public static <T extends FabricPacket> Packet<ClientPlayPacketListener> toVanillaPacket(T packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");

        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        return ServerPlayNetworking.createS2CPacket(packet.getType().getId(), buf);
    }
}
