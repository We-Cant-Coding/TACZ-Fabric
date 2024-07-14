package com.tacz.guns.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface SwapItemWithOffHand {
    Event<SwapItemWithOffHand> EVENT = EventFactory.createArrayBacked(SwapItemWithOffHand.class,
            (listeners) -> () -> {
                for (SwapItemWithOffHand listener : listeners) {
                    ActionResult result = listener.swapItemWithOffHand();

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult swapItemWithOffHand();
}
