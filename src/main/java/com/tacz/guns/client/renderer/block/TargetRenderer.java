package com.tacz.guns.client.renderer.block;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.tacz.guns.block.TargetBlock;
import com.tacz.guns.block.entity.TargetBlockEntity;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.InternalAssetLoader;
import com.tacz.guns.config.client.RenderConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.Optional;

public class TargetRenderer implements BlockEntityRenderer<TargetBlockEntity> {
    private static final String UPPER_NAME = "target_upper";
    private static final String HEAD_NAME = "head";

    public TargetRenderer(BlockEntityRendererFactory.Context context) {
    }

    public static Optional<BedrockModel> getModel() {
        return InternalAssetLoader.getBedrockModel(InternalAssetLoader.TARGET_MODEL_LOCATION);
    }

    @Override
    public void render(TargetBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        getModel().ifPresent(model -> {
            BlockState blockState = blockEntity.getCachedState();
            Direction facing = blockState.get(TargetBlock.FACING);
            BedrockPart headModel = model.getNode(HEAD_NAME);
            BedrockPart upperModel = model.getNode(UPPER_NAME);
            float deg = -MathHelper.lerp(partialTick, blockEntity.oRot, blockEntity.rot);
            upperModel.xRot = (float) Math.toRadians(deg);
            headModel.visible = false;

            poseStack.push();
            poseStack.translate(0.5, 0.225, 0.5);
            poseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(facing.getHorizontal() * 90));
            poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            poseStack.translate(0, -1.275, 0.0125);
            RenderLayer renderType = RenderLayer.getEntityTranslucent(InternalAssetLoader.TARGET_TEXTURE_LOCATION);
            model.render(poseStack, ModelTransformationMode.NONE, renderType, combinedLightIn, combinedOverlayIn);
            if (blockEntity.getOwner() != null) {
                poseStack.translate(0, 1.25, 0);
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(deg));
                MinecraftClient minecraft = MinecraftClient.getInstance();
                var map = minecraft.getSkinProvider().getTextures(blockEntity.getOwner());
                Identifier skin;
                if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    skin = minecraft.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                } else {
                    skin = DefaultSkinHelper.getTexture(Uuids.getUuidFromProfile(blockEntity.getOwner()));
                }
                headModel.visible = true;
                RenderLayer skullRenderType = RenderLayer.getEntityTranslucentCull(skin);
                headModel.render(poseStack, ModelTransformationMode.NONE, bufferIn.getBuffer(skullRenderType), combinedLightIn, OverlayTexture.DEFAULT_UV);
            }
            poseStack.pop();
        });
    }

    @Override
    public int getRenderDistance() {
        return RenderConfig.TARGET_RENDER_DISTANCE.get();
    }

    @Override
    public boolean rendersOutsideBoundingBox(TargetBlockEntity blockEntity) {
        return true;
    }
}