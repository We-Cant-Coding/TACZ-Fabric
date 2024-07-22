package com.tacz.guns.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.event.PreventsHotbarEvent;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import com.tacz.guns.compat.immediatelyfast.ImmediatelyFastCompat;
import com.tacz.guns.init.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    private void renderHotbarItemPre(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        ImmediatelyFastCompat.renderHotbarItem(stack, true);
    }

    @Inject(method = "renderHotbarItem", at = @At("RETURN"))
    private void renderHotbarItemPost(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        ImmediatelyFastCompat.renderHotbarItem(stack, false);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        PreventsHotbarEvent.onRenderHotbarEvent(cancelled);
        if (cancelled.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", ordinal = 1, shift = At.Shift.BEFORE, remap = false))
    private void renderCrosshairPre(DrawContext context, float tickDelta, CallbackInfo ci) {
        RenderCrosshairEvent.onRenderOverlay(context, MinecraftClient.getInstance().getWindow());
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext context, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        if (!IGun.mainhandHoldGun(player)) {
            return;
        }

        RenderSystem.defaultBlendFunc();

        ci.cancel();
    }
}
