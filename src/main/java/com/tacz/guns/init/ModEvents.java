package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.event.*;
import com.tacz.guns.event.ammo.BellRing;
import com.tacz.guns.event.ammo.DestroyGlassBlock;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ModEvents {

    public static void init() {
        ServerPlayConnectionEvents.DISCONNECT.register(new HitboxHelperEvent());

        var loadingConfigEvent = new LoadingConfigEvent();
        ModConfigEvents.loading(GunMod.MOD_ID).register(loadingConfigEvent);
        ModConfigEvents.reloading(GunMod.MOD_ID).register(loadingConfigEvent);

        ServerPlayerEvents.AFTER_RESPAWN.register(new PlayerRespawnEvent());

        var serverTickEvent = new ServerTickEvent();
        ServerTickEvents.START_SERVER_TICK.register(serverTickEvent);
        ServerTickEvents.END_SERVER_TICK.register(serverTickEvent);

        var syncedEntityDataEvent = new SyncedEntityDataEvent();
        ServerTickEvents.START_SERVER_TICK.register(syncedEntityDataEvent);
        ServerPlayerEvents.COPY_FROM.register(syncedEntityDataEvent);
        EntityTrackingEvents.START_TRACKING.register(syncedEntityDataEvent);

        // ammo
        var bellRing = new BellRing();
        AmmoHitBlockEvent.EVENT.register(bellRing);

        var destroyGlassBlock = new DestroyGlassBlock();
        AmmoHitBlockEvent.EVENT.register(destroyGlassBlock);
    }
}
