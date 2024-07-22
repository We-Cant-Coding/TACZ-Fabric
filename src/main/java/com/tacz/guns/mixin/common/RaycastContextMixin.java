package com.tacz.guns.mixin.common;

import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RaycastContext.class)
public class RaycastContextMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ShapeContext;of(Lnet/minecraft/entity/Entity;)Lnet/minecraft/block/ShapeContext;"))
    private ShapeContext contextRedirect(Entity entity) {
        return entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
    }
}
