package com.tacz.guns.mixin.client;

import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.client.event.RenderLevelBobEvent;
import com.tacz.guns.client.renderer.other.GunHurtBobTweak;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    private boolean tacz$useFovSetting;

    @Shadow
    public abstract MinecraftClient getClient();

    @Shadow
    public abstract void render(float pPartialTicks, long pNanoTime, boolean pRenderLevel);

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void onBobHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        // 取消受伤导致的视角摇晃
        if (this.getClient().getCameraEntity() instanceof ClientPlayerEntity player && !player.isDead()) {
            if (GunHurtBobTweak.onHurtBobTweak(player, matrices, tickDelta)) {
                ci.cancel();
                return;
            }
        }
        // 触发其他事件
        boolean cancel;
        if (!tacz$useFovSetting) {
            cancel = new RenderItemInHandBobEvent.BobHurt().post();
        } else {
            cancel = new RenderLevelBobEvent.BobHurt().post();
        }
        if (cancel) {
            ci.cancel();
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void onBobView(MatrixStack pMatrixStack, float pPartialTicks, CallbackInfo ci) {
        boolean cancel;
        if (!tacz$useFovSetting) {
            cancel = new RenderItemInHandBobEvent.BobView().post();
        } else {
            cancel = new RenderLevelBobEvent.BobView().post();
        }
        if (cancel) {
            ci.cancel();
        }
    }

    /**
     * is a hack implementation. This is because the getFov method is only called when building the projection matrix.
     * So you can determine whether you are currently ready to render Level or HandWithItem based on pUseFovSetting in getFov.
     * The reason why we don't just mixin the renderItemInHand method is because the content of this method was drastically changed after installing Optifine.
     */
    @Inject(method = "getFov", at = @At("HEAD"))
    public void switchRenderType(Camera pActiveRenderInfo, float pPartialTicks, boolean pUseFOVSetting, CallbackInfoReturnable<Double> cir) {
        this.tacz$useFovSetting = pUseFOVSetting;
    }
}
