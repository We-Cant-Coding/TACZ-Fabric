package com.tacz.guns.client.model;

import com.tacz.guns.client.model.bedrock.BedrockPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * visible的优先级低于FunctionalBedrockPart，当visible为false的时候，仍然会执行functionalRenderers
 */
public class FunctionalBedrockPart extends BedrockPart {
    public @Nullable Function<BedrockPart, IFunctionalRenderer> functionalRenderer;

    public FunctionalBedrockPart(@Nullable Function<BedrockPart, IFunctionalRenderer> functionalRenderer, @NotNull String name) {
        super(name);
        this.functionalRenderer = functionalRenderer;
    }

    public FunctionalBedrockPart(@Nullable Function<BedrockPart, IFunctionalRenderer> functionalRenderer, @NotNull BedrockPart part) {
        super(part.name);
        this.cubes.addAll(part.cubes);
        this.children.addAll(part.children);
        this.x = part.x;
        this.y = part.y;
        this.z = part.z;
        this.xRot = part.xRot;
        this.yRot = part.yRot;
        this.zRot = part.zRot;
        this.offsetX = part.offsetX;
        this.offsetY = part.offsetY;
        this.offsetZ = part.offsetZ;
        this.visible = part.visible;
        this.mirror = part.mirror;
        this.setInitRotationAngle(part.getInitRotX(), part.getInitRotY(), part.getInitRotZ());
        this.xScale = part.xScale;
        this.yScale = part.yScale;
        this.zScale = part.zScale;
        this.functionalRenderer = functionalRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, ModelTransformationMode transformType, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        int cubePackedLight = light;
        if (illuminated) {
            // 最大亮度
            cubePackedLight = LightmapTextureManager.pack(15, 15);
        }

        matrixStack.push();
        this.translateAndRotateAndScale(matrixStack);

        if (functionalRenderer != null) {
            @Nullable IFunctionalRenderer renderer = functionalRenderer.apply(this);
            if (renderer != null) {
                renderer.render(matrixStack, consumer, transformType, cubePackedLight, overlay);
            } else {
                if (this.visible) {
                    super.compile(matrixStack.peek(), consumer, cubePackedLight, overlay, red, green, blue, alpha);
                    for (BedrockPart part : this.children) {
                        part.render(matrixStack, transformType, consumer, cubePackedLight, overlay, red, green, blue, alpha);
                    }
                }
            }
        } else {
            if (this.visible) {
                super.compile(matrixStack.peek(), consumer, cubePackedLight, overlay, red, green, blue, alpha);
                for (BedrockPart part : this.children) {
                    part.render(matrixStack, transformType, consumer, cubePackedLight, overlay, red, green, blue, alpha);
                }
            }
        }
        matrixStack.pop();
    }
}
