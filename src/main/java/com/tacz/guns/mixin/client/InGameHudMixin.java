package com.tacz.guns.mixin.client;

import com.tacz.guns.client.event.PreventsHotbarEvent;
import com.tacz.guns.forge.RenderGuiOverlayEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        var event = new RenderGuiOverlayEvent();
        PreventsHotbarEvent.onRenderHotbarEvent(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
