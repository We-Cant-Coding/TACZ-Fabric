package com.tacz.guns.client.renderer.other;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.pojo.display.gun.LayerGunShow;
import com.tacz.guns.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HumanoidOffhandRender {
    public static void renderGun(LivingEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        renderOffhandGun(entity, matrixStack, buffer, packedLight);
        renderHotbarGun(entity, matrixStack, buffer, packedLight);
    }

    private static void renderOffhandGun(LivingEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        ItemStack itemStack = entity.getOffHandStack();
        if (itemStack.isEmpty()) {
            return;
        }
        IGun iGun = IGun.getIGunOrNull(itemStack);
        if (iGun == null) {
            return;
        }
        Identifier gunId = iGun.getGunId(itemStack);
        TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> {
            LayerGunShow offhandShow = index.getOffhandShow();
            renderGunItem(entity, matrixStack, buffer, packedLight, itemStack, offhandShow);
        });
    }

    private static void renderHotbarGun(LivingEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (i == inventory.selectedSlot) {
                continue;
            }
            ItemStack stack = inventory.getStack(i);
            renderHotbarGun(entity, matrixStack, buffer, packedLight, stack, i);
        }
    }

    private static void renderHotbarGun(LivingEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight, ItemStack itemStack, int inventoryIndex) {
        if (itemStack.isEmpty()) {
            return;
        }
        IGun iGun = IGun.getIGunOrNull(itemStack);
        if (iGun == null) {
            return;
        }
        Identifier gunId = iGun.getGunId(itemStack);
        TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> {
            var hotbarShow = index.getHotbarShow();
            if (hotbarShow == null || hotbarShow.isEmpty()) {
                return;
            }
            if (!hotbarShow.containsKey(inventoryIndex)) {
                return;
            }
            LayerGunShow gunShow = hotbarShow.get(inventoryIndex);
            renderGunItem(entity, matrixStack, buffer, packedLight, itemStack, gunShow);
        });
    }

    private static void renderGunItem(LivingEntity entity, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight, ItemStack itemStack, LayerGunShow offhandShow) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        Vector3f pos = offhandShow.getPos();
        Vector3f rotate = offhandShow.getRotate();
        Vector3f scale = offhandShow.getScale();
        matrixStack.push();
        matrixStack.translate(-pos.x() / 16f, 1.5 - pos.y() / 16f, pos.z() / 16f);
        matrixStack.scale(-scale.x(), -scale.y(), scale.z());
        Quaternionf rotation = new Quaternionf();
        MathUtil.toQuaternion((float) Math.toRadians(rotate.x), (float) Math.toRadians(rotate.y), (float) Math.toRadians(rotate.z), rotation);
        matrixStack.multiply(rotation);
        renderer.renderItem(itemStack, ModelTransformationMode.FIXED, packedLight, OverlayTexture.DEFAULT_UV, matrixStack, buffer, entity.getWorld(), entity.getId());
        matrixStack.pop();
    }
}
