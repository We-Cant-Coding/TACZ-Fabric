package com.tacz.guns.api.event.common;

import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EntityKillByGunEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onEntityKillByGun(event);
    });

    private final @Nullable LivingEntity killedEntity;
    private final @Nullable LivingEntity attacker;
    private final Identifier gunId;
    private final boolean isHeadShot;
    private final LogicalSide logicalSide;

    public EntityKillByGunEvent(@Nullable LivingEntity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, boolean isHeadShot, LogicalSide logicalSide) {
        this.killedEntity = hurtEntity;
        this.attacker = attacker;
        this.gunId = gunId;
        this.isHeadShot = isHeadShot;
        this.logicalSide = logicalSide;
    }

    @Nullable
    public LivingEntity getKilledEntity() {
        return killedEntity;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return attacker;
    }

    public Identifier getGunId() {
        return gunId;
    }

    public boolean isHeadShot() {
        return isHeadShot;
    }

    public LogicalSide getLogicalSide() {
        return logicalSide;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onEntityKillByGun(this);
    }

    public interface Callback {
        void onEntityKillByGun(EntityKillByGunEvent event);
    }
}
