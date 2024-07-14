package com.tacz.guns.init;

import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.event.client.RenderTickEvent;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.event.*;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.client.gui.overlay.InteractKeyTextOverlay;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import com.tacz.guns.forge.RenderGuiOverlayEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ModEvents {

    public static void init() {
        var cameraSetupEvent = new CameraSetupEvent();
        GunFireEvent.EVENT.register(cameraSetupEvent);
        BeforeRenderHandEvent.EVENT.register(cameraSetupEvent);

        var clientHitMark = new ClientHitMark();
        EntityHurtByGunEvent.EVENT.register(clientHitMark);
        EntityHurtByGunEvent.EVENT.register(clientHitMark);

        var firstPersonRenderGunEvent = new FirstPersonRenderGunEvent();
        GunFireEvent.EVENT.register(firstPersonRenderGunEvent);
        RenderItemInHandBobEvent.BOB_VIEW_EVENT.register(firstPersonRenderGunEvent);

        EntityHurtByGunEvent.EVENT.register(new PlayerHurtByGunEvent());

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            var event = new RenderGuiOverlayEvent();
            PreventsHotbarEvent.onRenderHotbarEvent(event);
            if (event.isCanceled()) return;
            GunHudOverlay.onHudRender(context);
            InteractKeyTextOverlay.onHudRender(context);
            KillAmountOverlay.onHudRender(context);
        });

        RenderTickEvent.EVENT.register(new RenderCrosshairEvent());

        var tickAnimationEvent = new TickAnimationEvent();
        var inventoryEvent = new InventoryEvent();

        ClientTickEvents.START_CLIENT_TICK.register(tickAnimationEvent);
        ClientTickEvents.START_CLIENT_TICK.register(inventoryEvent);
        ClientTickEvents.END_CLIENT_TICK.register(tickAnimationEvent);
        ClientTickEvents.END_CLIENT_TICK.register(inventoryEvent);
    }
}
