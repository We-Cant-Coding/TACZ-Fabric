package com.tacz.guns.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;

public interface RenderItemInHandBobEvent {
    Event<BobHurt> BOB_HURT_EVENT = EventFactory.createArrayBacked(BobHurt.class,
            (listeners) -> () -> {
                for (BobHurt listener : listeners) {
                    ActionResult result = listener.bobHurt();

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });
    Event<BobView> BOB_VIEW_EVENT = EventFactory.createArrayBacked(BobView.class,
            (listeners) -> () -> {
                for (BobView listener : listeners) {
                    ActionResult result = listener.bobView();

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    public static interface BobHurt {
        ActionResult bobHurt();
    }

    public static interface BobView {
        ActionResult bobView();
    }
}
