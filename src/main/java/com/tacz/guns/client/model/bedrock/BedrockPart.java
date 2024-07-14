package com.tacz.guns.client.model.bedrock;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class BedrockPart {
    @Nullable
    public final String name;
    public final ObjectList<BedrockCube> cubes = new ObjectArrayList<>();
    public final ObjectList<BedrockPart> children = new ObjectArrayList<>();
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public boolean visible = true;
    public boolean illuminated = false;
    public boolean mirror;
    /**
     * 通常用于动画旋转。
     */
    public Quaternionf additionalQuaternion = new Quaternionf(0, 0, 0, 1);
    public float xScale = 1;
    public float yScale = 1;
    public float zScale = 1;
    protected BedrockPart parent;
    private float initRotX;
    private float initRotY;
    private float initRotZ;

    public BedrockPart(@Nullable String name) {
        this.name = name;
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void render(MatrixStack matrixStack, ModelTransformationMode transformType, VertexConsumer consumer, int light, int overlay) {
        this.render(matrixStack, transformType, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(MatrixStack matrixStack, ModelTransformationMode transformType, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        int cubePackedLight = light;
        if (illuminated) {
            // 最大亮度
            cubePackedLight = LightmapTextureManager.pack(15, 15);
        }
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                matrixStack.push();
                this.translateAndRotateAndScale(matrixStack);
                this.compile(matrixStack.peek(), consumer, cubePackedLight, overlay, red, green, blue, alpha);

                for (BedrockPart part : this.children) {
                    part.render(matrixStack, transformType, consumer, cubePackedLight, overlay, red, green, blue, alpha);
                }

                matrixStack.pop();
            }
        }
    }

    public void translateAndRotateAndScale(MatrixStack matrixStack) {
        matrixStack.translate(this.offsetX, this.offsetY, this.offsetZ);
        matrixStack.translate((this.x / 16.0F), (this.y / 16.0F), (this.z / 16.0F));
        if (this.zRot != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(this.zRot));
        }
        if (this.yRot != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(this.yRot));
        }
        if (this.xRot != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(this.xRot));
        }
        matrixStack.multiply(additionalQuaternion);
        matrixStack.scale(xScale, yScale, zScale);
    }

    public void compile(MatrixStack.Entry entry, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        for (BedrockCube bedrockCube : this.cubes) {
            bedrockCube.compile(entry, consumer, light, overlay, red, green, blue, alpha);
        }
    }

    public BedrockCube getRandomCube(Random random) {
        return this.cubes.get(random.nextInt(this.cubes.size()));
    }

    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }

    public void setInitRotationAngle(float x, float y, float z) {
        this.initRotX = x;
        this.initRotY = y;
        this.initRotZ = z;
    }

    public float getInitRotX() {
        return initRotX;
    }

    public float getInitRotY() {
        return initRotY;
    }

    public float getInitRotZ() {
        return initRotZ;
    }

    public void addChild(BedrockPart model) {
        this.children.add(model);
    }

    public BedrockPart getParent() {
        return parent;
    }
}
