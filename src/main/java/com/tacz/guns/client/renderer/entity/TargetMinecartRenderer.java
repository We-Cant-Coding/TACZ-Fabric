package com.tacz.guns.client.renderer.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.InternalAssetLoader;
import com.tacz.guns.entity.TargetMinecart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.RotationAxis;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class TargetMinecartRenderer extends MinecartEntityRenderer<TargetMinecart> {
    private static final String HEAD_NAME = "head";
    private static final String HEAD_2_NAME = "head2";

    public TargetMinecartRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, EntityModelLayers.TNT_MINECART);
        this.shadowRadius = 0.25F;
    }

    public static Optional<BedrockModel> getModel() {
        return InternalAssetLoader.getBedrockModel(InternalAssetLoader.TARGET_MINECART_MODEL_LOCATION);
    }

    @Override
    public Identifier getTexture(TargetMinecart minecart) {
        return InternalAssetLoader.ENTITY_EMPTY_TEXTURE;
    }

    @Override
    protected void renderBlock(TargetMinecart targetMinecart, float pPartialTicks, BlockState pState, MatrixStack stack, VertexConsumerProvider buffer, int pPackedLight) {
        getModel().ifPresent(model -> {
            BedrockPart headModel = model.getNode(HEAD_NAME);
            BedrockPart head2Model = model.getNode(HEAD_2_NAME);
            headModel.visible = false;
            head2Model.visible = false;

            stack.push();
            stack.translate(0.5, 1.875, 0.5);
            stack.scale(1.5f, 1.5f, 1.5f);
            stack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180));
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
            RenderLayer renderType = RenderLayer.getEntityTranslucent(InternalAssetLoader.TARGET_MINECART_TEXTURE_LOCATION);
            model.render(stack, ModelTransformationMode.NONE, renderType, pPackedLight, OverlayTexture.DEFAULT_UV);
            if (targetMinecart.getGameProfile() != null) {
                stack.translate(0, 1, -4.5 / 16d);
                MinecraftClient minecraft = MinecraftClient.getInstance();
                GameProfile gameProfile = targetMinecart.getGameProfile();
                var map = minecraft.getSkinProvider().getTextures(gameProfile);
                Identifier skin;
                if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    skin = minecraft.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                } else {
                    skin = DefaultSkinHelper.getTexture(Uuids.getUuidFromProfile(gameProfile));
                }
                headModel.visible = true;
                RenderLayer skullRenderType = RenderLayer.getEntityTranslucentCull(skin);
                headModel.render(stack, ModelTransformationMode.NONE, buffer.getBuffer(skullRenderType), pPackedLight, OverlayTexture.DEFAULT_UV);

                head2Model.visible = true;
                stack.translate(0, 0, 0.01);
                head2Model.render(stack, ModelTransformationMode.NONE, buffer.getBuffer(skullRenderType), pPackedLight, OverlayTexture.DEFAULT_UV);
            }
            stack.pop();
        });
    }
}