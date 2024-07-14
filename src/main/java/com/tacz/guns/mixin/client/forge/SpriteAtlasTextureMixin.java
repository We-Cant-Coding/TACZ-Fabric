package com.tacz.guns.mixin.client.forge;

import com.tacz.guns.client.event.ReloadResourceEvent;
import com.tacz.guns.forge.TextureStitchEvent;
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
        var event = new TextureStitchEvent((SpriteAtlasTexture) (Object) this);
        ReloadResourceEvent.onTextureStitchEventPost(event);
    }
}
