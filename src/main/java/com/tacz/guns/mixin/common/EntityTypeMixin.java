package com.tacz.guns.mixin.common;

import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(method = "alwaysUpdateVelocity", at = @At("RETURN"), cancellable = true)
    private void alwaysUpdateVelocity(CallbackInfoReturnable<Boolean> cir) {
        EntityType<?> type = (EntityType<?>) (Object) this;
        cir.setReturnValue(cir.getReturnValue() && type != EntityKineticBullet.TYPE);
    }
}
