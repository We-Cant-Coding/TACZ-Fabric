package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

public class BeforeRenderHandEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onBeforeRenderHand(event);
    });

    private final MatrixStack poseStack;

    public BeforeRenderHandEvent(MatrixStack poseStack) {
        this.poseStack = poseStack;
    }

    public MatrixStack getPoseStack() {
        return poseStack;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onBeforeRenderHand(this);
    }

    public interface Callback {
        void onBeforeRenderHand(BeforeRenderHandEvent event);
    }
}
