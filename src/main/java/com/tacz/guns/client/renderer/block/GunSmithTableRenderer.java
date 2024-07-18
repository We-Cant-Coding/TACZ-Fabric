package com.tacz.guns.client.renderer.block;

import com.tacz.guns.block.GunSmithTableBlock;
import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.resource.InternalAssetLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.Optional;

public class GunSmithTableRenderer implements BlockEntityRenderer<GunSmithTableBlockEntity> {
    public GunSmithTableRenderer(BlockEntityRendererFactory.Context context) {
    }

    public static Optional<BedrockModel> getModel() {
        return InternalAssetLoader.getBedrockModel(InternalAssetLoader.SMITH_TABLE_MODEL_LOCATION);
    }

    public static Identifier getTextureLocation() {
        return InternalAssetLoader.SMITH_TABLE_TEXTURE_LOCATION;
    }

    @Override
    public void render(GunSmithTableBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        getModel().ifPresent(model -> {
            BlockState blockState = blockEntity.getCachedState();
            if (blockState.get(GunSmithTableBlock.PART).equals(BedPart.HEAD)) {
                return;
            }
            Direction facing = blockState.get(GunSmithTableBlock.FACING);
            poseStack.push();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            poseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90 - facing.getHorizontal() * 90));
            RenderLayer renderType = RenderLayer.getEntityTranslucent(InternalAssetLoader.SMITH_TABLE_TEXTURE_LOCATION);
            model.render(poseStack, ModelTransformationMode.NONE, renderType, combinedLightIn, combinedOverlayIn);
            poseStack.pop();
        });
    }

    @Override
    public boolean rendersOutsideBoundingBox(GunSmithTableBlockEntity blockEntity) {
        return true;
    }
}