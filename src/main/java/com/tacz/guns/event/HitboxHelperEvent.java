package com.tacz.guns.event;

import com.tacz.guns.config.common.OtherConfig;
import com.tacz.guns.util.HitboxHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class HitboxHelperEvent {

    public static void onPlayerTick(PlayerEntity player) {
        if (!OtherConfig.SERVER_HITBOX_LATENCY_FIX.get()) {
            return;
        }
        // event.side == LogicalSide.SERVER == player instanceof ServerPlayerEntity
        if (player instanceof ServerPlayerEntity) {
            HitboxHelper.onPlayerTick(player);
        }

    }

    public static void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer ignoredServer) {
        HitboxHelper.onPlayerLoggedOut(handler.player);
    }
}
