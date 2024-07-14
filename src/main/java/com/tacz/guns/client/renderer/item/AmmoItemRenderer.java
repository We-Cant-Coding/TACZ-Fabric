package com.tacz.guns.client.renderer.item;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.client.model.BedrockAmmoModel;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.pojo.TransformScale;
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
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.client.render.model.json.ModelTransformationMode.GUI;

public class AmmoItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final SlotModel SLOT_AMMO_MODEL = new SlotModel();

    private static void applyPositioningNodeTransform(List<BedrockPart> nodePath, MatrixStack poseStack, Vector3f scale) {
        if (nodePath == null) {
            return;
        }
        if (scale == null) {
            scale = new Vector3f(1, 1, 1);
        }
        // 应用定位组的反向位移、旋转，使定位组的位置就是渲染中心
        poseStack.translate(0, 1.5, 0);
        for (int i = nodePath.size() - 1; i >= 0; i--) {
            BedrockPart t = nodePath.get(i);
            poseStack.multiply(RotationAxis.NEGATIVE_X.rotation(t.xRot));
            poseStack.multiply(RotationAxis.NEGATIVE_Y.rotation(t.yRot));
            poseStack.multiply(RotationAxis.NEGATIVE_Z.rotation(t.zRot));
            if (t.getParent() != null) {
                poseStack.translate(-t.x * scale.x() / 16.0F, -t.y * scale.y() / 16.0F, -t.z * scale.z() / 16.0F);
            } else {
                poseStack.translate(-t.x * scale.x() / 16.0F, (1.5F - t.y / 16.0F) * scale.y(), -t.z * scale.z() / 16.0F);
            }
        }
        poseStack.translate(0, -1.5, 0);
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider pBuffer, int pPackedLight, int pPackedOverlay) {
        if (!(stack.getItem() instanceof IAmmo iAmmo)) {
            return;
        }
        Identifier ammoId = iAmmo.getAmmoId(stack);
        poseStack.push();
        TimelessAPI.getClientAmmoIndex(ammoId).ifPresentOrElse(ammoIndex -> {
            // 先获取 3D 模型，如果为空，统一使用 GUI 渲染
            BedrockAmmoModel ammoModel = ammoIndex.getAmmoModel();
            Identifier modelTexture = ammoIndex.getModelTextureLocation();
            // GUI 特殊渲染
            if (transformType == GUI || ammoModel == null || modelTexture == null) {
                poseStack.translate(0.5, 1.5, 0.5);
                poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(ammoIndex.getSlotTextureLocation()));
                SLOT_AMMO_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                return;
            }
            // 剩下的渲染
            // 移动到模型原点
            poseStack.translate(0.5, 2, 0.5);
            // 反转模型
            poseStack.scale(-1, -1, 1);
            // 应用定位组的变换（位移和旋转，不包括缩放）
            applyPositioningTransform(transformType, ammoIndex.getTransform().getScale(), ammoModel, poseStack);
            // 应用 display 数据中的缩放
            applyScaleTransform(transformType, ammoIndex.getTransform().getScale(), poseStack);
            // 渲染子弹盒模型
            RenderLayer renderType = RenderLayer.getEntityCutout(modelTexture);
            ammoModel.render(poseStack, transformType, renderType, pPackedLight, pPackedOverlay);
        }, () -> {
            // 没有这个 ammoID，渲染个错误材质提醒别人
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(MissingSprite.getMissingSpriteId()));
            SLOT_AMMO_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        });
        poseStack.pop();
    }

    private void applyPositioningTransform(ModelTransformationMode transformType, TransformScale scale, BedrockAmmoModel model, MatrixStack poseStack) {
        switch (transformType) {
            case FIXED -> applyPositioningNodeTransform(model.getFixedOriginPath(), poseStack, scale.getFixed());
            case GROUND -> applyPositioningNodeTransform(model.getGroundOriginPath(), poseStack, scale.getGround());
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND ->
                    applyPositioningNodeTransform(model.getThirdPersonHandOriginPath(), poseStack, scale.getThirdPerson());
        }
    }

    private void applyScaleTransform(ModelTransformationMode transformType, TransformScale scale, MatrixStack poseStack) {
        if (scale == null) {
            return;
        }
        Vector3f vector3f = null;
        switch (transformType) {
            case FIXED -> vector3f = scale.getFixed();
            case GROUND -> vector3f = scale.getGround();
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> vector3f = scale.getThirdPerson();
        }
        if (vector3f != null) {
            poseStack.translate(0, 1.5, 0);
            poseStack.scale(vector3f.x(), vector3f.y(), vector3f.z());
            poseStack.translate(0, -1.5, 0);
        }
    }
}
