package com.tacz.guns.client.model.functional;

import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.IFunctionalRenderer;
import com.tacz.guns.util.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RightHandRender implements IFunctionalRenderer {
    private final BedrockGunModel bedrockGunModel;

    public RightHandRender(BedrockGunModel bedrockGunModel) {
        this.bedrockGunModel = bedrockGunModel;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer vertexBuffer, ModelTransformationMode transformType, int light, int overlay) {
        if (transformType.isFirstPerson()) {
            if (!bedrockGunModel.getRenderHand()) {
                return;
            }
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
            Matrix3f normal = new Matrix3f(matrixStack.peek().getNormalMatrix());
            Matrix4f pose = new Matrix4f(matrixStack.peek().getPositionMatrix());
            //Vertex buffers shared with gun models need to be proxied to the end of the render to render the
            bedrockGunModel.delegateRender((poseStack1, vertexBuffer1, transformType1, light1, overlay1) -> {
                MatrixStack matrixStack2 = new MatrixStack();
                matrixStack2.peek().getNormalMatrix().mul(normal);
                matrixStack2.peek().getPositionMatrix().mul(pose);
                RenderHelper.renderFirstPersonArm(MinecraftClient.getInstance().player, Arm.RIGHT, matrixStack2, light1);
                MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().draw();
            });
        }
    }
}
