package com.tacz.guns.client.renderer.item;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.pojo.TransformScale;
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
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.client.render.model.json.ModelTransformationMode.*;

public class GunItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final SlotModel SLOT_GUN_MODEL = new SlotModel();

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
        if (!(stack.getItem() instanceof IGun iGun)) {
            return;
        }
        Identifier gunId = iGun.getGunId(stack);
        poseStack.push();
        TimelessAPI.getClientGunIndex(gunId).ifPresentOrElse(gunIndex -> {
            // 第一人称就不渲染了，交给别的地方
            if (transformType == FIRST_PERSON_LEFT_HAND || transformType == FIRST_PERSON_RIGHT_HAND) {
                return;
            }
            // 第三人称副手也不渲染了
            if (transformType == THIRD_PERSON_LEFT_HAND) {
                return;
            }
            // GUI 特殊渲染
            if (transformType == GUI) {
                poseStack.translate(0.5, 1.5, 0.5);
                poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
                VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(gunIndex.getSlotTexture()));
                SLOT_GUN_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                return;
            }
            // 剩下的渲染
            BedrockGunModel gunModel;
            Identifier gunTexture;
            Pair<BedrockGunModel, Identifier> lodModel = gunIndex.getLodModel();
            if (lodModel == null || RenderDistance.inRenderHighPolyModelDistance(poseStack)) {
                gunModel = gunIndex.getGunModel();
                gunTexture = gunIndex.getModelTexture();
            } else {
                gunModel = lodModel.getLeft();
                gunTexture = lodModel.getRight();
            }
            // 移动到模型原点
            poseStack.translate(0.5, 2, 0.5);
            // 反转模型
            poseStack.scale(-1, -1, 1);
            // 应用定位组的变换（位移和旋转，不包括缩放）
            applyPositioningTransform(transformType, gunIndex.getTransform().getScale(), gunModel, poseStack);
            // 应用 display 数据中的缩放
            applyScaleTransform(transformType, gunIndex.getTransform().getScale(), poseStack);
            // 渲染枪械模型
            RenderLayer renderType = RenderLayer.getEntityCutout(gunTexture);
            gunModel.render(poseStack, stack, transformType, renderType, pPackedLight, pPackedOverlay);
        }, () -> {
            // 没有这个 gunID，渲染个错误材质提醒别人
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            VertexConsumer buffer = pBuffer.getBuffer(RenderLayer.getEntityTranslucent(MissingSprite.getMissingSpriteId()));
            SLOT_GUN_MODEL.render(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        });
        poseStack.pop();
    }

    private void applyPositioningTransform(ModelTransformationMode transformType, TransformScale scale, BedrockGunModel model, MatrixStack poseStack) {
        switch (transformType) {
            case FIXED -> applyPositioningNodeTransform(model.getFixedOriginPath(), poseStack, scale.getFixed());
            case GROUND -> applyPositioningNodeTransform(model.getGroundOriginPath(), poseStack, scale.getGround());
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> applyPositioningNodeTransform(model.getThirdPersonHandOriginPath(), poseStack, scale.getThirdPerson());
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
