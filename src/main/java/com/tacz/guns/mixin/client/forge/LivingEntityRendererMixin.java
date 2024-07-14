package com.tacz.guns.mixin.client.forge;

import com.tacz.guns.client.event.RenderHeadShotAABB;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private <T extends LivingEntity> void renderPost(T livingEntity, float pEntityYaw, float pPartialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int pPackedLight, CallbackInfo ci) {
        RenderHeadShotAABB.onRenderEntity(livingEntity, vertexConsumerProvider, matrixStack);
    }
}
