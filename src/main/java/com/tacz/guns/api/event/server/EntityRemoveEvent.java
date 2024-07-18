package com.tacz.guns.api.event.server;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public class EntityRemoveEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onEntityRemove(event);
    });

    private final Entity entity;

    public EntityRemoveEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onEntityRemove(this);
    }

    public interface Callback {
        void onEntityRemove(EntityRemoveEvent event);
    }
}
