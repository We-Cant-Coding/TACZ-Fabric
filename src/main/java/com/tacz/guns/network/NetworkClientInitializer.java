package com.tacz.guns.network;

import com.tacz.guns.network.message.*;
import com.tacz.guns.network.message.event.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

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
}
