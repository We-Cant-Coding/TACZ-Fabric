package com.tacz.guns.mixin.client;

import com.tacz.guns.client.event.ReloadResourceEvent;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {

    @Inject(method = "upload", at = @At("TAIL"))
    private void uploadPost(SpriteLoader.StitchResult stitchResult, CallbackInfo ci) {
        ReloadResourceEvent.onTextureStitchEventPost((SpriteAtlasTexture) (Object) this);
    }
}
