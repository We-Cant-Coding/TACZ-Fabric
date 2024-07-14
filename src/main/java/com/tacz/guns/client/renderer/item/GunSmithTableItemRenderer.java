package com.tacz.guns.client.renderer.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class GunSmithTableItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider pBuffer, int pPackedLight, int pPackedOverlay) {
        GunSmithTableRenderer.getModel().ifPresent(model -> {
            Identifier texture = GunSmithTableRenderer.getTextureLocation();
            poseStack.push();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            RenderLayer renderType = RenderLayer.entityTranslucent(texture);
            model.render(poseStack, transformType, renderType, pPackedLight, pPackedOverlay);
            poseStack.pop();
        });
    }
}
