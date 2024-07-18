package com.tacz.guns.api.event.client;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public class RenderTickEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onRenderTick(event);
    });

    private final MinecraftClient client;


    public RenderTickEvent(MinecraftClient client) {
        this.client = client;
    }

    public MinecraftClient getClient() {
        return client;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onRenderTick(this);
    }

    public interface Callback {
        void onRenderTick(RenderTickEvent event);
    }
}
