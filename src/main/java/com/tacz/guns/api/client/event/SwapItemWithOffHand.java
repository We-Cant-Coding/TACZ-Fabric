package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class SwapItemWithOffHand extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onSwapItemWithOffHand(event);
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onSwapItemWithOffHand(this);
    }

    public interface Callback {
        void onSwapItemWithOffHand(SwapItemWithOffHand event);
    }
}
