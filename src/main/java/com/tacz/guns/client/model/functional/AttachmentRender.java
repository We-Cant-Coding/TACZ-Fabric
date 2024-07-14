package com.tacz.guns.client.model.functional;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.client.model.BedrockAttachmentModel;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.client.renderer.item.AttachmentItemRenderer;
import com.tacz.guns.client.resource.index.ClientAttachmentSkinIndex;
import com.tacz.guns.util.RenderDistance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.EnumMap;

public class AttachmentRender implements IFunctionalRenderer {
    private final BedrockGunModel bedrockGunModel;
    private final AttachmentType type;

    public AttachmentRender(BedrockGunModel bedrockGunModel, AttachmentType type) {
        this.bedrockGunModel = bedrockGunModel;
        this.type = type;
    }

    public static void renderAttachment(ItemStack attachmentItem, MatrixStack poseStack, ModelTransformationMode transformType, int light, int overlay) {
        poseStack.translate(0, -1.5, 0);
        if (attachmentItem.getItem() instanceof IAttachment iAttachment) {
            Identifier attachmentId = iAttachment.getAttachmentId(attachmentItem);
            TimelessAPI.getClientAttachmentIndex(attachmentId).ifPresentOrElse(attachmentIndex -> {
                Identifier skinId = iAttachment.getSkinId(attachmentItem);
                ClientAttachmentSkinIndex skinIndex = attachmentIndex.getSkinIndex(skinId);
                if (skinIndex != null) {
                    // 有皮肤则渲染皮肤
                    BedrockAttachmentModel model = skinIndex.getModel();
                    Identifier texture = skinIndex.getTexture();
                    RenderLayer renderType = RenderLayer.getEntityCutout(texture);
                    model.render(poseStack, transformType, renderType, light, overlay);
                } else {
                    // 没有皮肤，渲染默认模型
                    BedrockAttachmentModel model = attachmentIndex.getAttachmentModel();
                    Identifier texture = attachmentIndex.getModelTexture();
                    // 这里是枪械里的配件渲染，没有模型材质就不渲染
                    if (model != null && texture != null) {
                        // 调用低模
                        Pair<BedrockAttachmentModel, Identifier> lodModel = attachmentIndex.getLodModel();
                        // 有低模、在高模渲染范围外、不是第一人称
                        if (lodModel != null && !RenderDistance.inRenderHighPolyModelDistance(poseStack) && !transformType.isFirstPerson()) {
                            model = lodModel.getLeft();
                            texture = lodModel.getRight();
                        }
                        RenderLayer renderType = RenderLayer.getEntityCutout(texture);
                        model.render(poseStack, transformType, renderType, light, overlay);
                    }
                }
            }, () -> {
                // 没有对应的 attachmentIndex，渲染黑紫材质以提醒
                VertexConsumerProvider bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                VertexConsumer buffer = bufferSource.getBuffer(RenderLayer.getEntityTranslucent(MissingSprite.getMissingSpriteId()));
                AttachmentItemRenderer.SLOT_ATTACHMENT_MODEL.render(poseStack, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            });
        }
    }

    @Override
    public void render(MatrixStack poseStack, VertexConsumer vertexBuffer, ModelTransformationMode transformType, int light, int overlay) {
        EnumMap<AttachmentType, ItemStack> currentAttachmentItem = bedrockGunModel.getCurrentAttachmentItem();
        ItemStack attachmentItem = currentAttachmentItem.get(type);
        if (attachmentItem != null && !attachmentItem.isEmpty()) {
            Matrix3f normal = new Matrix3f(poseStack.peek().getNormalMatrix());
            Matrix4f pose = new Matrix4f(poseStack.peek().getPositionMatrix());
            //和枪械模型共用顶点缓冲的都需要代理到渲染结束后渲染
            bedrockGunModel.delegateRender((poseStack1, vertexBuffer1, transformType1, light1, overlay1) -> {
                MatrixStack poseStack2 = new MatrixStack();
                poseStack2.peek().getNormalMatrix().mul(normal);
                poseStack2.peek().getPositionMatrix().mul(pose);
                // 渲染配件
                renderAttachment(attachmentItem, poseStack2, transformType, light, overlay);
            });
        }
    }
}
