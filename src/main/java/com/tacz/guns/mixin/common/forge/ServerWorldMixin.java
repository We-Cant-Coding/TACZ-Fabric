package com.tacz.guns.mixin.common.forge;

import com.tacz.guns.event.SyncedEntityDataEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "addPlayer", at = @At("HEAD"))
    private void onAddPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        SyncedEntityDataEvent.onPlayerJoinWorld(player, ((ServerWorld) (Object) this));
    }
}
