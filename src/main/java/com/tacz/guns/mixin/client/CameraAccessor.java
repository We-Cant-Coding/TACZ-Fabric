package com.tacz.guns.mixin.client;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor
    void setYaw(float yaw);

    @Accessor
    void setPitch(float pitch);
}
