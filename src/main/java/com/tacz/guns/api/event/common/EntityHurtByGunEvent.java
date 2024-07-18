package com.tacz.guns.api.event.common;

import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Events triggered when a creature is damaged by a firearm bullet
 */
public class EntityHurtByGunEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onEntityHurtByGun(event);
    });
    protected @Nullable Entity hurtEntity;
    protected @Nullable LivingEntity attacker;
    protected Identifier gunId;
    protected float baseAmount;
    protected boolean isHeadShot;
    protected float headshotMultiplier;
    private final LogicalSide logicalSide;

    protected EntityHurtByGunEvent(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier, LogicalSide logicalSide) {
        this.hurtEntity = hurtEntity;
        this.attacker = attacker;
        this.gunId = gunId;
        this.baseAmount = baseAmount;
        this.isHeadShot = isHeadShot;
        this.headshotMultiplier = headshotMultiplier;
        this.logicalSide = logicalSide;
    }

    /**
     * An event that is triggered before an entity is shot and the damage is determined,
     * allowing you to set the damage attribute of the shot
     */
    public static class Pre extends EntityHurtByGunEvent {
        public static final Event<PreCallback> EVENT = EventFactory.createArrayBacked(PreCallback.class, callbacks -> event -> {
            for (PreCallback e : callbacks) e.onEntityHurtByGun(event);
        });

        public Pre(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier, LogicalSide logicalSide) {
            super(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, logicalSide);
            this.headshotMultiplier = headshotMultiplier;
        }

        public final void setHurtEntity(@Nullable Entity hurtEntity) {
            this.hurtEntity = hurtEntity;
        }

        public final void setAttacker(@Nullable LivingEntity attacker) {
            this.attacker = attacker;
        }

        public final void setGunId(Identifier gunId) {
            this.gunId = gunId;
        }

        public final void setBaseAmount(float baseAmount) {
            this.baseAmount = baseAmount;
        }

        public final void setHeadshot(boolean headshot) {
            this.isHeadShot = headshot;
        }

        public final void setHeadshotMultiplier(float headshotMultiplier) {
            this.headshotMultiplier = headshotMultiplier;
        }

        @Override
        public void sendEvent() {
            super.sendEvent();
            EVENT.invoker().onEntityHurtByGun(this);
        }
    }

    /**
     * 实体受到枪击，伤害判定结束但没有死亡后触发的事件
     * @see EntityKillByGunEvent 实体因枪击致死时触发的事件
     */
    public static class Post extends EntityHurtByGunEvent {
        public static final Event<PostCallback> EVENT = EventFactory.createArrayBacked(PostCallback.class, callbacks -> event -> {
            for (PostCallback e : callbacks) e.onEntityHurtByGun(event);
        });

        public Post(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier, LogicalSide logicalSide) {
            super(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, logicalSide);
        }

        @Override
        public void sendEvent() {
            super.sendEvent();
            EVENT.invoker().onEntityHurtByGun(this);
        }
    }

    @Nullable
    public Entity getHurtEntity() {
        return hurtEntity;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return attacker;
    }

    public Identifier getGunId() {
        return gunId;
    }

    @ApiStatus.Obsolete
    public float getAmount() {
        return baseAmount * headshotMultiplier;
    }

    public float getBaseAmount() {
        return baseAmount;
    }

    public float getHeadshotMultiplier() {
        return headshotMultiplier;
    }

    public boolean isHeadShot() {
        return isHeadShot;
    }

    public LogicalSide getLogicalSide() {
        return logicalSide;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onEntityHurtByGun(this);
    }

    public interface Callback {
        void onEntityHurtByGun(EntityHurtByGunEvent event);
    }

    public interface PreCallback {
        void onEntityHurtByGun(EntityHurtByGunEvent.Pre event);
    }

    public interface PostCallback {
        void onEntityHurtByGun(EntityHurtByGunEvent.Post event);
    }
}
