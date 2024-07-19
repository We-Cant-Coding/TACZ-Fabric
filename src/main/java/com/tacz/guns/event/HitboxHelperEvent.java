package com.tacz.guns.event;

import com.tacz.guns.util.HitboxHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HitboxHelperEvent implements ServerPlayConnectionEvents.Disconnect {

    /**
     * Move to
     * {@link com.tacz.guns.mixin.common.PlayerEntityMixin#tickEnd(CallbackInfo)}
     */
    public static void onPlayerTick() {
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        HitboxHelper.onPlayerLoggedOut(handler.player);
    }
}
