package com.tacz.guns.client.model;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

public interface IFunctionalRenderer {
    void render(MatrixStack matrixStack, VertexConsumer vertexBuffer, ModelTransformationMode transformType, int light, int overlay);
}
