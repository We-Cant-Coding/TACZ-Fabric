package com.tacz.guns.mixin.client.forge;

import com.tacz.guns.forge.InputEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onMouseButtonPre(long window, int button, int action, int mods, CallbackInfo ci) {
        if (new InputEvent.MouseButton.Pre(button, action, mods).post()) {
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", at = @At("TAIL"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == this.client.getWindow().getHandle()) {
            new InputEvent.MouseButton.Post(button, action, mods).post();
        }
    }
}
