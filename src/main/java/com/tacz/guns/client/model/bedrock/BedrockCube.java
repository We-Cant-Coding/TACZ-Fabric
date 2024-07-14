package com.tacz.guns.client.model.bedrock;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface BedrockCube {
    void compile(MatrixStack.Entry entry, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha);
}
