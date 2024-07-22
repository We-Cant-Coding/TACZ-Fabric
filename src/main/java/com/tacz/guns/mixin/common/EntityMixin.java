package com.tacz.guns.mixin.common;

import com.tacz.guns.api.event.server.EntityRemoveEvent;
import com.tacz.guns.api.mixin.ItemHandlerCapability;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow private World world;

    @Inject(method = "remove", at = @At("TAIL"))
    private void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!world.isClient) {
            new EntityRemoveEvent(This()).post();
        }
        if (This() instanceof ItemHandlerCapability) {
            ((ItemHandlerCapability) This()).tacz$invalidateItemHandler();
        }
    }

    @Unique
    private Entity This() {
        return (Entity) (Object) this;
    }
}
