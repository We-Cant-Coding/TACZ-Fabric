package com.tacz.guns.mixin.common;

import com.tacz.guns.compat.carryon.BlackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.config.ConfigLoader;

@Mixin(value = ConfigLoader.class, remap = false)
public class CarryOnMixin {

    @Inject(method = "onConfigLoaded", at = @At("TAIL"), remap = false)
    private static void onConfigLoaded(CallbackInfo ci) {
        BlackList.addBlackList();
    }
}
