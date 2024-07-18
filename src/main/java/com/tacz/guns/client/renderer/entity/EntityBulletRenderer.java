package com.tacz.guns.client.renderer.entity;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.model.BedrockAmmoModel;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.resource.InternalAssetLoader;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class EntityBulletRenderer extends EntityRenderer<EntityKineticBullet> {
    public EntityBulletRenderer(EntityRendererFactory.Context pContext) {
        super(pContext);
    }

    public static Optional<BedrockModel> getModel() {
        return InternalAssetLoader.getBedrockModel(InternalAssetLoader.DEFAULT_BULLET_MODEL);
    }

    @Override
    public void render(EntityKineticBullet bullet, float entityYaw, float partialTicks, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight) {
        Identifier gunId = bullet.getGunId();
        Optional<ClientGunIndex> optionalClientGunIndex = TimelessAPI.getClientGunIndex(gunId);
        if (optionalClientGunIndex.isEmpty()) {
            return;
        }
        float @Nullable [] gunTracerColor = optionalClientGunIndex.get().getTracerColor();
        Identifier ammoId = bullet.getAmmoId();
        TimelessAPI.getClientAmmoIndex(ammoId).ifPresent(ammoIndex -> {
            BedrockAmmoModel ammoEntityModel = ammoIndex.getAmmoEntityModel();
            Identifier textureLocation = ammoIndex.getAmmoEntityTextureLocation();
            if (ammoEntityModel != null && textureLocation != null) {
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(partialTicks, bullet.prevYaw, bullet.getYaw()) - 180.0F));
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(partialTicks, bullet.prevPitch, bullet.getPitch())));
                poseStack.push();
                poseStack.translate(0, 1.5, 0);
                poseStack.scale(-1, -1, 1);
                ammoEntityModel.render(poseStack, ModelTransformationMode.GROUND, RenderLayer.getEntityTranslucentCull(textureLocation), packedLight, OverlayTexture.DEFAULT_UV);
                poseStack.pop();
            }

            // 曳光弹发光
            if (bullet.isTracerAmmo()) {
                float[] tracerColor = Objects.requireNonNullElse(gunTracerColor, ammoIndex.getTracerColor());
                renderTracerAmmo(bullet, tracerColor, partialTicks, poseStack, packedLight);
            }
        });
    }

    public void renderTracerAmmo(EntityKineticBullet bullet, float[] tracerColor, float partialTicks, MatrixStack poseStack, int packedLight) {
        getModel().ifPresent(model -> {
            Entity shooter = bullet.getOwner();
            if (shooter == null) {
                return;
            }
            poseStack.push();
            {
                float width = 0.005f;
                Vec3d bulletPosition = bullet.getLerpedPos(partialTicks);
                double trailLength = 0.85 * bullet.getVelocity().length();
                double disToEye = bulletPosition.distanceTo(shooter.getCameraPosVec(partialTicks));
                trailLength = Math.min(trailLength, disToEye * 0.8);
                if (this.dispatcher.gameOptions.getPerspective().isFirstPerson() && bullet.getOwner() instanceof ClientPlayerEntity) {
                    // 自己打的曳光弹在第一人称的渲染委托给 FirstPersonRenderGunEvent
                    poseStack.pop();
                    return;
                } else {
                    width *= (float) Math.max(1.0, disToEye / 3.5);
                    poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(partialTicks, bullet.prevYaw, bullet.getYaw()) - 180.0F));
                    poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(partialTicks, bullet.prevPitch, bullet.getPitch())));
                    poseStack.translate(0, 0, trailLength / 2.0);
                    poseStack.scale(width, width, (float) trailLength);
                }
                // 距离两格外才渲染，只在前 5 tick 判定
                double bulletDistance = bulletPosition.distanceTo(shooter.getEyePos());
                if (bullet.age >= 5 || bulletDistance > 2) {
                    RenderLayer type = RenderLayer.getEnergySwirl(InternalAssetLoader.DEFAULT_BULLET_TEXTURE, 15, 15);
                    model.render(poseStack, ModelTransformationMode.NONE, type, packedLight, OverlayTexture.DEFAULT_UV,
                            tracerColor[0], tracerColor[1], tracerColor[2], 1);
                }
            }
            poseStack.pop();
        });
    }

    @Override
    protected int getBlockLight(@NotNull EntityKineticBullet entityBullet, @NotNull BlockPos blockPos) {
        return 15;
    }

    @Override
    public boolean shouldRender(EntityKineticBullet bullet, Frustum camera, double pCamX, double pCamY, double pCamZ) {
        Box aabb = bullet.getVisibilityBoundingBox().expand(0.5);
        if (aabb.isNaN() || aabb.getAverageSideLength() == 0) {
            aabb = new Box(bullet.getX() - 2.0, bullet.getY() - 2.0, bullet.getZ() - 2.0, bullet.getX() + 2.0, bullet.getY() + 2.0, bullet.getZ() + 2.0);
        }
        return camera.isVisible(aabb);
    }

    @Override
    public Identifier getTexture(@NotNull EntityKineticBullet entity) {
        return null;
    }
}
