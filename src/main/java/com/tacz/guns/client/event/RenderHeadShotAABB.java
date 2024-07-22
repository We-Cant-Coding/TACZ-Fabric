package com.tacz.guns.client.event;

import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.config.util.HeadShotAABBConfigRead;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class RenderHeadShotAABB {

    public static void onRenderEntity(LivingEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffers) {
        boolean canRender = MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes();
        if (!canRender) {
            return;
        }
        if (!RenderConfig.HEAD_SHOT_DEBUG_HITBOX.get()) {
            return;
        }
        Identifier entityId = Registries.ENTITY_TYPE.getId(entity.getType());
        Box aabb = HeadShotAABBConfigRead.getAABB(entityId);
        if (aabb == null) {
            float width = entity.getWidth();
            float eyeHeight = entity.getStandingEyeHeight();
            // 扩张 0.01，避免和原版显示重合
            aabb = new Box(-width / 2, eyeHeight - 0.25, -width / 2, width / 2, eyeHeight + 0.25, width / 2).expand(0.01);
        }
        VertexConsumer buffer = buffers.getBuffer(RenderLayer.getLines());
        WorldRenderer.drawBox(matrixStack, buffer, aabb, 1.0F, 1.0F, 0.0F, 1.0F);
    }
}
