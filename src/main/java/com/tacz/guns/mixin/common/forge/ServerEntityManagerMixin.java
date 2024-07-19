package com.tacz.guns.mixin.common.forge;

import com.tacz.guns.event.SyncedEntityDataEvent;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerEntityManager.class)
public class ServerEntityManagerMixin<T extends EntityLike> {

    @Inject(method = "addEntity(Lnet/minecraft/world/entity/EntityLike;Z)Z", at = @At("HEAD"))
    private void onAddEntity(T entity, boolean existing, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Entity e) {
            SyncedEntityDataEvent.onPlayerJoinWorld(e, e.getWorld());
        }
    }
}
