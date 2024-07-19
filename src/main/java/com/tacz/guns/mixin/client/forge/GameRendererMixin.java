package com.tacz.guns.mixin.client.forge;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.event.CameraSetupEvent;
import com.tacz.guns.forge.ViewportEvent;
import com.tacz.guns.mixin.client.CameraAccessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow private boolean renderingPanorama;

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 2))
    private Quaternionf renderWorld(RotationAxis axis, float deg, float tickDelta, long limitTime, MatrixStack matrices, @Local Camera camera) {
        var event = new ViewportEvent.ComputeCameraAngles(This(), camera, tickDelta, camera.getYaw(), camera.getPitch(), 0.0F);

        // Calling events
        CameraSetupEvent.applyLevelCameraAnimation(event);
        CameraSetupEvent.applyCameraRecoil();

        ((CameraAccessor) camera).setYaw(event.getYaw());
        ((CameraAccessor) camera).setPitch(event.getPitch());
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(event.getRoll()));
        return axis.rotationDegrees(camera.getPitch());
    }

    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir, @Local double d) {
        if (!renderingPanorama) {
            var event = new ViewportEvent.ComputeFov(This(), camera, tickDelta, d, changingFov);

            // Calling events
            CameraSetupEvent.applyScopeMagnification(event);
            CameraSetupEvent.applyGunModelFovModifying(event);

            cir.setReturnValue(event.getFOV());
        }
    }

    @Unique
    private GameRenderer This() {
        return (GameRenderer) (Object) this;
    }
}
