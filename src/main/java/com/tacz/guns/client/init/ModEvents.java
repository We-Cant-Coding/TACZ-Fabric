package com.tacz.guns.client.init;

import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.api.event.client.RenderTickEvent;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.event.*;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.client.gui.overlay.InteractKeyTextOverlay;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import com.tacz.guns.client.input.*;
import com.tacz.guns.forge.InputEvent;
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
        EntityKillByGunEvent.EVENT.register(clientHitMark);

        var firstPersonRenderGunEvent = new FirstPersonRenderGunEvent();
        GunFireEvent.EVENT.register(firstPersonRenderGunEvent);
        RenderItemInHandBobEvent.BobView.EVENT.register(firstPersonRenderGunEvent);

        EntityHurtByGunEvent.EVENT.register(new PlayerHurtByGunEvent());

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            GunHudOverlay.onHudRender(context);
            InteractKeyTextOverlay.onHudRender(context);
            KillAmountOverlay.onHudRender(context);
        });

        RenderTickEvent.EVENT.register(new RenderCrosshairEvent());
        RenderTickEvent.EVENT.register(RefitTransform::tickInterpolation);

        var inventoryEvent = new InventoryEvent();
        SwapItemWithOffHand.EVENT.register(inventoryEvent);
        ClientTickEvents.START_CLIENT_TICK.register(inventoryEvent);
        ClientTickEvents.END_CLIENT_TICK.register(inventoryEvent);

        var tickAnimationEvent = new TickAnimationEvent();
        ClientTickEvents.START_CLIENT_TICK.register(tickAnimationEvent);
        ClientTickEvents.END_CLIENT_TICK.register(tickAnimationEvent);

        var aimKey = new AimKey();
        InputEvent.MouseButton.Post.EVENT.register(aimKey);
        ClientTickEvents.START_CLIENT_TICK.register(aimKey);

        var configKey = new ConfigKey();
        InputEvent.Key.EVENT.register(configKey);

        var fireSelectKey = new FireSelectKey();
        InputEvent.Key.EVENT.register(fireSelectKey);
        InputEvent.MouseButton.Post.EVENT.register(fireSelectKey);

        var inspectKey = new InspectKey();
        InputEvent.Key.EVENT.register(inspectKey);

        var interactKey = new InteractKey();
        InputEvent.Key.EVENT.register(interactKey);
        InputEvent.MouseButton.Post.EVENT.register(interactKey);

        var meleeKey = new MeleeKey();
        InputEvent.Key.EVENT.register(meleeKey);
        InputEvent.MouseButton.Post.EVENT.register(meleeKey);

        var refitKey = new RefitKey();
        InputEvent.Key.EVENT.register(refitKey);

        var reloadKey = new ReloadKey();
        InputEvent.Key.EVENT.register(reloadKey);

        var shootKey = new ShootKey();
        ClientTickEvents.START_CLIENT_TICK.register(shootKey);
        InputEvent.MouseButton.Post.EVENT.register(shootKey);

        var zoomKey = new ZoomKey();
        InputEvent.Key.EVENT.register(zoomKey);
        InputEvent.MouseButton.Post.EVENT.register(zoomKey);
    }
}
