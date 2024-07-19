package com.tacz.guns.event;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class EnterServerEvent {
    /**
     * Move to
     * {@link com.tacz.guns.mixin.common.PlayerManagerMixin#onPlayerConnect(ClientConnection, ServerPlayerEntity, CallbackInfo)}
     */
    public static void onLoggedInServer() {
    }
}
