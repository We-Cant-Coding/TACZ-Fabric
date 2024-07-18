package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class RenderLevelBobEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onRenderLevelBob(event);
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onRenderLevelBob(this);
    }

    public static class BobHurt extends RenderLevelBobEvent {
        public static final Event<BobHurtCallback> EVENT = EventFactory.createArrayBacked(BobHurtCallback.class, callbacks -> event -> {
            for (BobHurtCallback e : callbacks) e.onBobHurt(event);
        });

        @Override
        public void sendEvent() {
            super.sendEvent();
            EVENT.invoker().onBobHurt(this);
        }
    }

    public static class BobView extends RenderLevelBobEvent {
        public static final Event<BobViewCallback> EVENT = EventFactory.createArrayBacked(BobViewCallback.class, callbacks -> event -> {
            for (BobViewCallback e : callbacks) e.onBobView(event);
        });

        @Override
        public void sendEvent() {
            super.sendEvent();
            EVENT.invoker().onBobView(this);
        }
    }

    public interface Callback {
        void onRenderLevelBob(RenderLevelBobEvent event);
    }

    public interface BobHurtCallback {
        void onBobHurt(RenderLevelBobEvent.BobHurt event);
    }

    public interface BobViewCallback {
        void onBobView(RenderLevelBobEvent.BobView event);
    }
}
