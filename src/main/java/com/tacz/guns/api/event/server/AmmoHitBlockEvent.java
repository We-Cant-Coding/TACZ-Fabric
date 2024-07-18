package com.tacz.guns.api.event.server;

import com.tacz.guns.api.event.GunBaseEvent;
import com.tacz.guns.entity.EntityKineticBullet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class AmmoHitBlockEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onAmmoHitBlock(event);
    });

    private final World level;
    private final BlockHitResult hitResult;
    private final BlockState state;
    private final EntityKineticBullet ammo;

    public AmmoHitBlockEvent(World level, BlockHitResult hitResult, BlockState state, EntityKineticBullet ammo) {
        this.level = level;
        this.hitResult = hitResult;
        this.state = state;
        this.ammo = ammo;
    }

    public World getLevel() {
        return level;
    }

    public BlockHitResult getHitResult() {
        return hitResult;
    }

    public BlockState getState() {
        return state;
    }

    public EntityKineticBullet getAmmo() {
        return ammo;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onAmmoHitBlock(this);
    }

    public interface Callback {
        void onAmmoHitBlock(AmmoHitBlockEvent event);
    }
}
