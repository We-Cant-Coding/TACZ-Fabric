package com.tacz.guns.client.model.functional;

import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.client.model.papi.PapiManager;
import com.tacz.guns.client.resource.pojo.display.gun.TextShow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class TextShowRender implements IFunctionalRenderer {
    private final BedrockGunModel bedrockGunModel;
    private final TextShow textShow;
    private final ItemStack gunStack;

    public TextShowRender(BedrockGunModel bedrockGunModel, TextShow textShow, ItemStack gunStack) {
        this.bedrockGunModel = bedrockGunModel;
        this.textShow = textShow;
        this.gunStack = gunStack;
    }

    @Override
    public void render(MatrixStack poseStack, VertexConsumer vertexBuffer, ModelTransformationMode transformType, int light, int overlay) {
        if (!transformType.isFirstPerson()) {
            return;
        }
        String text = PapiManager.getTextShow(textShow.getTextKey(), gunStack);
        if (StringUtils.isBlank(text)) {
            return;
        }
        poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
        Matrix3f normal = new Matrix3f(poseStack.peek().getNormalMatrix());
        Matrix4f pose = new Matrix4f(poseStack.peek().getPositionMatrix());

        // 和枪械模型共用顶点缓冲的都需要代理到渲染结束后渲染
        bedrockGunModel.delegateRender((poseStack1, vertexBuffer1, transformType1, light1, overlay1) -> {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            boolean shadow = textShow.isShadow();
            int color = textShow.getColorInt();
            float scale = textShow.getScale();
            int packLight = LightmapTextureManager.pack(textShow.getTextLight(), textShow.getTextLight());
            int width = textRenderer.getWidth(text);
            int xOffset;
            switch (textShow.getAlign()) {
                case CENTER -> xOffset = width / 2;
                case RIGHT -> xOffset = width;
                default -> xOffset = 0;
            }

            MatrixStack poseStack2 = new MatrixStack();
            poseStack2.peek().getNormalMatrix().mul(normal);
            poseStack2.peek().getPositionMatrix().mul(pose);
            poseStack2.scale(2 / 300f * scale, -2 / 300f * scale, -2 / 300f);

            VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            textRenderer.draw(text, -xOffset, -textRenderer.fontHeight / 2f, color, shadow, poseStack2.peek().getPositionMatrix(), bufferSource, TextRenderer.TextLayerType.NORMAL, 0, packLight);
            bufferSource.draw();
        });
    }
}
