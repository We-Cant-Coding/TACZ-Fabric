package com.tacz.guns.client.event;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.input.InteractKey;
import com.tacz.guns.api.client.event.InputEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class ClientPreventGunClick {
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        // 当交互键按下时，允许交互
        if (InteractKey.INTERACT_KEY.isPressed()) {
            return;
        }
        // 只要主手有枪，那么禁止交互
        ItemStack itemInHand = player.getStackInHand(Hand.MAIN_HAND);
        if (itemInHand.getItem() instanceof IGun) {
            // 展示框可以交互
            HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof ItemFrameEntity) {
                return;
            }
            // 这个设置为 false 就能阻止客户端粒子的生成
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
