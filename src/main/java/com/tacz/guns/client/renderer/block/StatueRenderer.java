package com.tacz.guns.client.renderer.block;

import com.tacz.guns.block.TargetBlock;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.resource.InternalAssetLoader;
import com.tacz.guns.config.client.RenderConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class StatueRenderer implements BlockEntityRenderer<StatueBlockEntity> {
    public StatueRenderer(BlockEntityRendererFactory.Context context) {
    }

    public static Optional<BedrockModel> getModel() {
        return InternalAssetLoader.getBedrockModel(InternalAssetLoader.STATUE_MODEL_LOCATION);
    }

    @Override
    public void render(StatueBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        getModel().ifPresent(model -> {
            World level = blockEntity.getWorld();
            if (level == null) {
                return;
            }

            poseStack.push();
            {
                BlockState blockState = blockEntity.getCachedState();
                Direction facing = blockState.get(TargetBlock.FACING);

                poseStack.translate(0.5, 1.5, 0.5);

                poseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((facing.getHorizontal() + 2) % 4 * 90));
                poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));

                RenderLayer renderType = RenderLayer.getEntityTranslucent(getTextureLocation());
                model.render(poseStack, ModelTransformationMode.NONE, renderType, combinedLightIn, combinedOverlayIn);

                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0, -0.875, -1.2);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

                double offset = Math.sin(Util.getMeasuringTimeMs() / 500.0) * 0.1;
                poseStack.translate(0, offset, 0);

                ItemStack stack = blockEntity.getGunItem();

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        stack,
                        ModelTransformationMode.FIXED,
                        LightmapTextureManager.pack(15, 15),
                        OverlayTexture.DEFAULT_UV,
                        poseStack,
                        bufferIn,
                        level,
                        0
                );
            }
            poseStack.pop();
        });
    }

    public static Identifier getTextureLocation() {
        return InternalAssetLoader.STATUE_TEXTURE_LOCATION;
    }

    @Override
    public int getRenderDistance() {
        return RenderConfig.TARGET_RENDER_DISTANCE.get();
    }

    @Override
    public boolean rendersOutsideBoundingBox(StatueBlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean isInRenderDistance(StatueBlockEntity pBlockEntity, Vec3d pCameraPos) {
        return Vec3d.ofCenter(pBlockEntity.getPos().up()).isInRange(pCameraPos, this.getRenderDistance());
    }
}