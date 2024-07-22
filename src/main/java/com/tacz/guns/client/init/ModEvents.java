package com.tacz.guns.client.init;

import com.tacz.guns.api.client.event.*;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.event.*;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.client.gui.overlay.InteractKeyTextOverlay;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import com.tacz.guns.client.input.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ModEvents {
    public static void init() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            GunHudOverlay.onHudRender(context);
            InteractKeyTextOverlay.onHudRender(context);
            KillAmountOverlay.onHudRender(context);
        });

        RenderTickEvent.EVENT.register(RefitTransform::tickInterpolation);

        ViewportEvent.ComputeCameraAngles.EVENT.register(CameraSetupEvent::applyLevelCameraAnimation);
        BeforeRenderHandEvent.EVENT.register(CameraSetupEvent::onBeforeRenderHand);
        ViewportEvent.ComputeFov.EVENT.register(CameraSetupEvent::applyScopeMagnification);
        ViewportEvent.ComputeFov.EVENT.register(CameraSetupEvent::applyGunModelFovModifying);
        GunFireEvent.EVENT.register(CameraSetupEvent::initialCameraRecoil);
        ViewportEvent.ComputeCameraAngles.EVENT.register(CameraSetupEvent::applyCameraRecoil);

        EntityHurtByGunEvent.EVENT.register(ClientHitMark::onEntityHurtByGun);
        EntityKillByGunEvent.EVENT.register(ClientHitMark::onEntityKillByGun);

        InputEvent.InteractionKeyMappingTriggered.EVENT.register(ClientPreventGunClick::onClickInput);

        RenderHandEvent.EVENT.register(FirstPersonRenderGunEvent::onRenderHand);
        RenderItemInHandBobEvent.BobView.EVENT.register(FirstPersonRenderGunEvent::cancelItemInHandViewBobbing);
        GunFireEvent.EVENT.register(FirstPersonRenderGunEvent::onGunFire);

        ClientTickEvents.START_CLIENT_TICK.register(InventoryEvent::onPlayerChangeSelect);
        ClientTickEvents.END_CLIENT_TICK.register(InventoryEvent::onPlayerChangeSelect);
        SwapItemWithOffHand.EVENT.register(InventoryEvent::onSwapItemWithOffHand);

        EntityHurtByGunEvent.EVENT.register(PlayerHurtByGunEvent::onPlayerHurtByGun);

        RenderTickEvent.EVENT.register(RenderCrosshairEvent::onRenderTick);

        ClientTickEvents.START_CLIENT_TICK.register(TickAnimationEvent::tickAnimation);
        ClientTickEvents.END_CLIENT_TICK.register(TickAnimationEvent::tickAnimation);

        // keybinding
        ClientTickEvents.START_CLIENT_TICK.register(AimKey::cancelAim);
        ClientTickEvents.START_CLIENT_TICK.register(ShootKey::autoShoot);

        InputEvent.Key.EVENT.register(ConfigKey::onOpenConfig);
        InputEvent.Key.EVENT.register(FireSelectKey::onFireSelectKeyPress);
        InputEvent.Key.EVENT.register(InspectKey::onInspectPress);
        InputEvent.Key.EVENT.register(InteractKey::onInteractKeyPress);
        InputEvent.Key.EVENT.register(MeleeKey::onMeleeKeyPress);
        InputEvent.Key.EVENT.register(RefitKey::onRefitPress);
        InputEvent.Key.EVENT.register(ReloadKey::onReloadPress);
        InputEvent.Key.EVENT.register(ZoomKey::onZoomKeyPress);

        InputEvent.MouseButton.Post.EVENT.register(AimKey::onAimPress);
        InputEvent.MouseButton.Post.EVENT.register(FireSelectKey::onFireSelectMousePress);
        InputEvent.MouseButton.Post.EVENT.register(InteractKey::onInteractMousePress);
        InputEvent.MouseButton.Post.EVENT.register(MeleeKey::onMeleeMousePress);
        InputEvent.MouseButton.Post.EVENT.register(ShootKey::semiShoot);
        InputEvent.MouseButton.Post.EVENT.register(ZoomKey::onZoomMousePress);
    }
}
