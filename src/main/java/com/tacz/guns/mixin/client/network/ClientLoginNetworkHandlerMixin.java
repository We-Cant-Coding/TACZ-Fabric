package com.tacz.guns.mixin.client.network;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import com.tacz.guns.api.mixin.SyncedEntityDataMapping;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class)
public class ClientLoginNetworkHandlerMixin {
    @Shadow @Final private ClientConnection connection;
    @Unique
    private static final Marker HANDSHAKE = MarkerFactory.getMarker("TACZ_HANDSHAKE");

    @Inject(method = "onSuccess", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;setState(Lnet/minecraft/network/NetworkState;)V", shift = At.Shift.AFTER))
    private void onSuccess(LoginSuccessS2CPacket packet, CallbackInfo ci) {
        GunMod.LOGGER.debug(HANDSHAKE, "Received synced key mappings from server");
        if (!SyncedEntityData.instance().updateMappings((((SyncedEntityDataMapping) packet).tacz$getKeymap()))) {
            connection.disconnect(Text.literal("Connection closed - [TacZ] Received unknown synced data keys."));
        }
    }
}
