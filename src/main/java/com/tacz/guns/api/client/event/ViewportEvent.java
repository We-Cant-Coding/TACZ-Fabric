package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.jetbrains.annotations.ApiStatus;

public abstract class ViewportEvent extends GunBaseEvent {
    private final GameRenderer renderer;
    private final Camera camera;
    private final double partialTick;

    @ApiStatus.Internal
    public ViewportEvent(GameRenderer renderer, Camera camera, double partialTick) {
        this.renderer = renderer;
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public GameRenderer getRenderer() {
        return this.renderer;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public double getPartialTick() {
        return this.partialTick;
    }

    public static class ComputeFov extends ViewportEvent {
        public static final Event<FovCallback> EVENT = EventFactory.createArrayBacked(FovCallback.class, callbacks -> event -> {
            for (FovCallback e : callbacks) e.onFov(event);
        });

        private final boolean usedConfiguredFov;
        private double fov;

        @ApiStatus.Internal
        public ComputeFov(GameRenderer renderer, Camera camera, double renderPartialTicks, double fov, boolean usedConfiguredFov) {
            super(renderer, camera, renderPartialTicks);
            this.usedConfiguredFov = usedConfiguredFov;
            this.setFOV(fov);
        }

        public double getFOV() {
            return this.fov;
        }

        public void setFOV(double fov) {
            this.fov = fov;
        }

        public boolean usedConfiguredFov() {
            return this.usedConfiguredFov;
        }

        @Override
        public void sendEvent() {
            EVENT.invoker().onFov(this);
        }
    }

    public static class ComputeCameraAngles extends ViewportEvent {
        public static final Event<CameraCallback> EVENT = EventFactory.createArrayBacked(CameraCallback.class, callbacks -> event -> {
            for (CameraCallback e : callbacks) e.onCamera(event);
        });

        private float yaw;
        private float pitch;
        private float roll;

        @ApiStatus.Internal
        public ComputeCameraAngles(GameRenderer renderer, Camera camera, double renderPartialTicks, float yaw, float pitch, float roll) {
            super(renderer, camera, renderPartialTicks);
            this.setYaw(yaw);
            this.setPitch(pitch);
            this.setRoll(roll);
        }

        public float getYaw() {
            return this.yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getRoll() {
            return this.roll;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }

        @Override
        public void sendEvent() {
            EVENT.invoker().onCamera(this);
        }
    }


    public interface FovCallback {
        void onFov(ComputeFov event);
    }

    public interface CameraCallback {
        void onCamera(ComputeCameraAngles event);
    }
}
