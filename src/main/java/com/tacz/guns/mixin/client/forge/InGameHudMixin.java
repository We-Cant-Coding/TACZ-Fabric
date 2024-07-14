package com.tacz.guns.mixin.client.forge;

import com.tacz.guns.client.event.RenderCrosshairEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext context, CallbackInfo ci) {
        AtomicBoolean canceled = new AtomicBoolean(false);
        RenderCrosshairEvent.onRenderOverlay(context, canceled);
        if (canceled.get()) {
            ci.cancel();
        }
    }
}
