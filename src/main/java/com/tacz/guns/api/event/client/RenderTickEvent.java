package com.tacz.guns.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public interface RenderTickEvent {
    Event<RenderTickEvent> EVENT = EventFactory.createArrayBacked(RenderTickEvent.class,
            (listeners) -> (client) -> {
                for (RenderTickEvent listener : listeners) {
                    ActionResult result = listener.renderTick(client);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });


    ActionResult renderTick(MinecraftClient client);
}
