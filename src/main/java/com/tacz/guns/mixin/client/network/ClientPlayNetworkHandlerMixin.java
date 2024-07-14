package com.tacz.guns.mixin.client.network;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.mixin.network.IEntitySpawnS2C;
import com.tacz.guns.network.IEntityAdditionalSpawnData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onSpawnPacket(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V", shift = At.Shift.AFTER))
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity instanceof IEntityAdditionalSpawnData spawnData) {
            if (packet instanceof IEntitySpawnS2C) {
                spawnData.readSpawnData(((IEntitySpawnS2C) packet).tacz$buf());
            }
        }
    }
}
