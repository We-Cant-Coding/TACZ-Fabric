package com.tacz.guns.event;

import com.tacz.guns.resource.network.CommonGunPackNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class EnterServerEvent {

    public static void onLoggedInServer(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            CommonGunPackNetwork.syncClient(serverPlayer);
        }
    }
}
