package com.tacz.guns.client.model.functional;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.resource.pojo.display.gun.MuzzleFlash;
import com.tacz.guns.compat.iris.IrisCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MuzzleFlashRender implements IFunctionalRenderer {
    private static final SlotModel MUZZLE_FLASH_MODEL = new SlotModel(true);
    /**
     * 50ms 显示时间
     */
    private static final long TIME_RANGE = 50;
    public static boolean isSelf = false;
    private static long shootTimeStamp = -1;
    private static boolean muzzleFlashStartMark = false;
    private static float muzzleFlashRandomRotate = 0;
    private static Matrix3f muzzleFlashNormal = new Matrix3f();
    private static Matrix4f muzzleFlashPose = new Matrix4f();

    private final BedrockGunModel bedrockGunModel;

    public MuzzleFlashRender(BedrockGunModel bedrockGunModel) {
        this.bedrockGunModel = bedrockGunModel;
    }

    public static void onShoot() {
        // 记录开火时间戳
        shootTimeStamp = System.currentTimeMillis();
        // 记录枪口火焰启动标记
        muzzleFlashStartMark = true;
        // 随机给予枪口火焰的旋转
        muzzleFlashRandomRotate = (float) (Math.random() * 360);
    }

    private static void renderMuzzleFlash(Identifier gunId, MatrixStack matrixStack, BedrockModel bedrockModel, long time) {
        TimelessAPI.getClientGunIndex(gunId).ifPresent(gunIndex -> {
            MuzzleFlash muzzleFlash = gunIndex.getMuzzleFlash();
            if (muzzleFlash == null) {
                return;
            }
            if (muzzleFlashStartMark) {
                muzzleFlashNormal = new Matrix3f(matrixStack.peek().getNormalMatrix());
                muzzleFlashPose = new Matrix4f(matrixStack.peek().getPositionMatrix());
            }
            bedrockModel.delegateRender((poseStack1, vertexConsumer1, transformType1, light, overlay) -> doRender(light, overlay, muzzleFlash, time));
        });
    }

    private static void doRender(int light, int overlay, MuzzleFlash muzzleFlash, long time) {
        if (muzzleFlashNormal != null && muzzleFlashPose != null) {
            float scale = 0.5f * muzzleFlash.getScale();
            float scaleTime = TIME_RANGE / 2.0f;
            scale = time < scaleTime ? (scale * (time / scaleTime)) : scale;
            muzzleFlashStartMark = false;
            VertexConsumerProvider multiBufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            // 推送到指定位置
            MatrixStack poseStack2 = new MatrixStack();
            poseStack2.peek().getNormalMatrix().mul(muzzleFlashNormal);
            poseStack2.peek().getPositionMatrix().mul(muzzleFlashPose);

            // 先渲染一遍半透明背景
            poseStack2.push();
            {
                poseStack2.scale(scale, scale, scale);
                poseStack2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(muzzleFlashRandomRotate));
                poseStack2.translate(0, -1, 0);
                RenderLayer renderTypeBg = RenderLayer.getEntityTranslucent(muzzleFlash.getTexture());
                MUZZLE_FLASH_MODEL.render(poseStack2, multiBufferSource.getBuffer(renderTypeBg), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            poseStack2.pop();

            // 然后渲染发光效果
            poseStack2.push();
            {
                poseStack2.scale(scale / 2, scale / 2, scale / 2);
                poseStack2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(muzzleFlashRandomRotate));
                poseStack2.translate(0, -0.9, 0);
                RenderLayer renderTypeLight = RenderLayer.getEnergySwirl(muzzleFlash.getTexture(), 1, 1);
                MUZZLE_FLASH_MODEL.render(poseStack2, multiBufferSource.getBuffer(renderTypeLight), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            poseStack2.pop();
        }
    }

    @Override
    public void render(MatrixStack poseStack, VertexConsumer vertexBuffer, ModelTransformationMode transformType, int light, int overlay) {
        if (IrisCompat.isRenderShadow()) {
            return;
        }
        if (!isSelf) {
            return;
        }
        long time = System.currentTimeMillis() - shootTimeStamp;
        if (time > TIME_RANGE) {
            return;
        }
        ItemStack currentGunItem = bedrockGunModel.getCurrentGunItem();
        IGun iGun = IGun.getIGunOrNull(currentGunItem);
        if (iGun == null) {
            return;
        }
        Identifier gunId = iGun.getGunId(currentGunItem);
        // 如果安装了消音器，则不渲染枪口火光
        ItemStack muzzleAttachment = bedrockGunModel.getCurrentAttachmentItem().get(AttachmentType.MUZZLE);
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(muzzleAttachment);
        if (iAttachment != null) {
            TimelessAPI.getCommonAttachmentIndex(iAttachment.getAttachmentId(muzzleAttachment)).ifPresent(index -> {
                if (index.getData().getSilence() != null) {
                    return;
                }
                renderMuzzleFlash(gunId, poseStack, bedrockGunModel, time);
            });
        } else {
            renderMuzzleFlash(gunId, poseStack, bedrockGunModel, time);
        }
    }
}
