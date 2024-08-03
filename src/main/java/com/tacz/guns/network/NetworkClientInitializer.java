package com.tacz.guns.network;

import com.tacz.guns.mixin.client.network.ClientLoginNetworkHandlerAccessor;
import com.tacz.guns.network.packets.s2c.event.*;
import com.tacz.guns.network.packets.s2c.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class NetworkClientInitializer {

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SoundS2CPacket.TYPE, SoundS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CraftS2CPacket.TYPE, CraftS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(RefreshRefitScreenS2CPacket.TYPE, RefreshRefitScreenS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SwapItemS2CPacket.TYPE, SwapItemS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(LevelUpS2CPacket.TYPE, LevelUpS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(UpdateEntityDataS2CPacket.TYPE, UpdateEntityDataS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncGunPackS2CPacket.TYPE, SyncGunPackS2CPacket::handle);

        ClientPlayNetworking.registerGlobalReceiver(GunHurtS2CPacket.TYPE, GunHurtS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunKillS2CPacket.TYPE, GunKillS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunDrawS2CPacket.TYPE, GunDrawS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunFireS2CPacket.TYPE, GunFireS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunFireSelectS2CPacket.TYPE, GunFireSelectS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunMeleeS2CPacket.TYPE, GunMeleeS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunReloadS2CPacket.TYPE, GunReloadS2CPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(GunShootS2CPacket.TYPE, GunShootS2CPacket::handle);
    }

    @Environment(EnvType.CLIENT)
    public static <T extends IMessage> void registerHandshake(PacketType<T> type) {
        ClientLoginNetworking.registerGlobalReceiver(type.getId(), (client, handler, buf, listenerAdder) -> {
            T packet = type.read(buf);
            ClientConnection connection = ((ClientLoginNetworkHandlerAccessor) handler).getConnection();
            PacketByteBuf response = packet.handle(connection, listenerAdder);
            return CompletableFuture.completedFuture(response);
        });
    }
}
