package com.tacz.guns.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;

public interface BeforeRenderHandEvent {
    Event<BeforeRenderHandEvent> EVENT = EventFactory.createArrayBacked(BeforeRenderHandEvent.class,
            (listeners) -> (matrixStack) -> {
                for (BeforeRenderHandEvent listener : listeners) {
                    ActionResult result = listener.beforeRenderHand(matrixStack);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult beforeRenderHand(MatrixStack matrixStack);
}
