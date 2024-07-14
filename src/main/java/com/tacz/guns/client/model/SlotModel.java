package com.tacz.guns.client.model;


import com.tacz.guns.client.model.bedrock.BedrockCubePerFace;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.pojo.model.FaceUVsItem;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SlotModel extends EntityModel<Entity> {
    private final BedrockPart bone;

    public SlotModel(boolean illuminated) {
        bone = new BedrockPart("slot");
        bone.setPos(8.0F, 24.0F, -10.0F);
        bone.cubes.add(new BedrockCubePerFace(-16.0F, -16.0F, 9.5F, 16.0F, 16.0F, 0, 0, 16, 16, FaceUVsItem.singleSouthFace()));
        bone.illuminated = illuminated;
    }

    public SlotModel() {
        this(false);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        bone.render(matrices, ModelTransformationMode.GUI, vertices, light, overlay);
    }
}
