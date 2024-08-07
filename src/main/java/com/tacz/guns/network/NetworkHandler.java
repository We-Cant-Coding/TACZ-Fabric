package com.tacz.guns.network;

import com.tacz.guns.network.packets.c2s.*;
import com.tacz.guns.network.packets.c2s.handshake.AcknowledgeC2SPacket;
import com.tacz.guns.network.packets.s2c.handshake.SyncedEntityDataMappingS2CPacket;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
        ServerPlayNetworking.registerGlobalReceiver(PlayerShootC2SPacket.TYPE, PlayerShootC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerReloadGunC2SPacket.TYPE, PlayerReloadGunC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerFireSelectC2SPacket.TYPE, PlayerFireSelectC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerAimC2SPacket.TYPE, PlayerAimC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerDrawGunC2SPacket.TYPE, PlayerDrawGunC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(CraftC2SPacket.TYPE, CraftC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerZoomC2SPacket.TYPE, PlayerZoomC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(RefitGunC2SPacket.TYPE, RefitGunC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerBoltGunC2SPacket.TYPE, PlayerBoltGunC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(UnloadAttachmentC2SPacket.TYPE, UnloadAttachmentC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(PlayerMeleeC2SPacket.TYPE, PlayerMeleeC2SPacket::handle);

        HandshakeNetworking.register(AcknowledgeC2SPacket.ID, AcknowledgeC2SPacket.class);
        HandshakeNetworking.register(SyncedEntityDataMappingS2CPacket.TYPE, SyncedEntityDataMappingS2CPacket.class);

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

    public static <T extends FabricPacket> Packet<ClientPlayPacketListener> toVanillaPacket(T packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");

        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        return ServerPlayNetworking.createS2CPacket(packet.getType().getId(), buf);
    }
}
