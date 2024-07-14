package com.tacz.guns.client.renderer.item;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.client.model.BedrockAttachmentModel;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.index.ClientAttachmentSkinIndex;
import com.tacz.guns.util.RenderDistance;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class AttachmentItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final SlotModel SLOT_ATTACHMENT_MODEL = new SlotModel();

    @Override
    public void render(@NotNull ItemStack stack, @NotNull ModelTransformationMode transformType, @NotNull MatrixStack poseStack, @NotNull VertexConsumerProvider pBuffer, int pPackedLight, int pPackedOverlay) {
        if (stack.getItem() instanceof IAttachment iAttachment) {
            Identifier attachmentId = iAttachment.getAttachmentId(stack);
            poseStack.push();
            TimelessAPI.getClientAttachmentIndex(attachmentId).ifPresentOrElse(attachmentIndex -> {
                // GUI 特殊渲染
                if (transformType == ModelTransformationMode.GUI) {
                    poseStack.translate(0.5, 1.5, 0.5);
                    poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                    VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(attachmentIndex.getSlotTexture()));
                    SLOT_ATTACHMENT_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                    return;
                }
                poseStack.translate(0.5, 2, 0.5);
                // 反转模型
                poseStack.scale(-1, -1, 1);
                if (transformType == ModelTransformationMode.FIXED) {
                    poseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90f));
                }
                Identifier skinId = iAttachment.getSkinId(stack);
                ClientAttachmentSkinIndex skinIndex = attachmentIndex.getSkinIndex(skinId);
                if (skinIndex != null) {
                    // 有皮肤则渲染皮肤
                    BedrockAttachmentModel model = skinIndex.getModel();
                    Identifier texture = skinIndex.getTexture();
                    RenderLayer renderType = RenderLayer.getEntityCutout(texture);
                    model.render(poseStack, transformType, renderType, pPackedLight, pPackedOverlay);
                } else {
                    // 没有皮肤，渲染默认模型
                    this.renderDefaultAttachment(transformType, poseStack, pBuffer, pPackedLight, pPackedOverlay, attachmentIndex);
                }
            }, () -> {
                // 没有这个 attachmentId，渲染黑紫材质以提醒
                poseStack.translate(0.5, 1.5, 0.5);
                poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(MissingSprite.getMissingSpriteId()));
                SLOT_ATTACHMENT_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            });
            poseStack.pop();
        }
    }

    private void renderDefaultAttachment(@NotNull ModelTransformationMode transformType, @NotNull MatrixStack poseStack, @NotNull VertexConsumerProvider pBuffer, int pPackedLight, int pPackedOverlay, ClientAttachmentIndex attachmentIndex) {
        BedrockAttachmentModel model = attachmentIndex.getAttachmentModel();
        Identifier texture = attachmentIndex.getModelTexture();
        // 有模型？正常渲染
        if (model != null && texture != null) {
            // 调用低模
            Pair<BedrockAttachmentModel, Identifier> lodModel = attachmentIndex.getLodModel();
            // 有低模、在高模渲染范围外、不是第一人称
            if (lodModel != null && !RenderDistance.inRenderHighPolyModelDistance(poseStack) && !transformType.isFirstPerson()) {
                model = lodModel.getLeft();
                texture = lodModel.getRight();
            }
            RenderLayer renderType = RenderLayer.getEntityCutout(texture);
            model.render(poseStack, transformType, renderType, pPackedLight, pPackedOverlay);
        }
        // 否则，以 GUI 形式渲染
        else {
            poseStack.translate(0, 0.5, 0);
            // 展示框里显示正常
            if (transformType == ModelTransformationMode.FIXED) {
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            }
            VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(attachmentIndex.getSlotTexture()));
            SLOT_ATTACHMENT_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
