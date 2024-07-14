package com.tacz.guns.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tacz.guns.mixin.client.interfaces.FramebufferStencil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.IntBuffer;

@Mixin(Framebuffer.class)
public abstract class FramebufferMixin implements FramebufferStencil {
    @Shadow public abstract void resize(int width, int height, boolean getError);

    @Shadow public int viewportWidth;
    @Shadow public int viewportHeight;
    @Unique
    private boolean stencilEnabled = false;

    @Redirect(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0))
    private void initFbo_texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, IntBuffer pixels) {
        if (!this.stencilEnabled) {
            GlStateManager._texImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
        } else {
            GlStateManager._texImage2D(target, level, 36013, width, height, border, 34041, 36269, pixels);
        }
    }

    @Redirect(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", ordinal = 1))
    private void initFbo_glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        if (!stencilEnabled) {
            GlStateManager._glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
        } else {
            GlStateManager._glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
            GlStateManager._glFramebufferTexture2D(target, 36128, textureTarget, texture, level);
        }
    }

    @Override
    public void tacz$enableStencil() {
        if (!stencilEnabled) {
            stencilEnabled = true;
            resize(viewportWidth, viewportHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    @Override
    public boolean tacz$isStencilEnabled() {
        return stencilEnabled;
    }
}
