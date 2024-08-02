package com.tacz.guns.network;

import com.tacz.guns.mixin.client.network.ClientLoginNetworkHandlerAccessor;
import com.tacz.guns.network.message.*;
import com.tacz.guns.network.message.event.*;
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
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSound.TYPE, ServerMessageSound::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageCraft.TYPE, ServerMessageCraft::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageRefreshRefitScreen.TYPE, ServerMessageRefreshRefitScreen::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSwapItem.TYPE, ServerMessageSwapItem::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageLevelUp.TYPE, ServerMessageLevelUp::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageUpdateEntityData.TYPE, ServerMessageUpdateEntityData::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSyncGunPack.TYPE, ServerMessageSyncGunPack::handle);

        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunHurt.TYPE, ServerMessageGunHurt::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunKill.TYPE, ServerMessageGunKill::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunDraw.TYPE, ServerMessageGunDraw::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunFire.TYPE, ServerMessageGunFire::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunFireSelect.TYPE, ServerMessageGunFireSelect::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunMelee.TYPE, ServerMessageGunMelee::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunReload.TYPE, ServerMessageGunReload::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunShoot.TYPE, ServerMessageGunShoot::handle);
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
