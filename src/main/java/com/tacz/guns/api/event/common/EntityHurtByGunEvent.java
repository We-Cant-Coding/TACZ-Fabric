package com.tacz.guns.api.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface EntityHurtByGunEvent {
    Event<EntityHurtByGunEvent> EVENT = EventFactory.createArrayBacked(EntityHurtByGunEvent.class,
            (listeners) -> (hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, side) -> {
                for (EntityHurtByGunEvent listener : listeners) {
                    ActionResult result = listener.entityHurtByGun(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, side);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });
    Event<Pre> PRE_EVENT = EventFactory.createArrayBacked(Pre.class,
            (listeners) -> (hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, side) -> {
                for (Pre listener : listeners) {
                    Result result = listener.pre(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, side);

                    if (result.isCancelled()) {
                        return result;
                    }
                }

                return new Result(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier);
            });
    Event<Post> POST_EVENT = EventFactory.createArrayBacked(Post.class,
            (listeners) -> (hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, side) -> {
                for (Post listener : listeners) {
                    Result result = listener.post(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier, side);

                    if (result.isCancelled()) {
                        return result;
                    }
                }

                return new Result(hurtEntity, attacker, gunId, baseAmount, isHeadShot, headshotMultiplier);
            });

    ActionResult entityHurtByGun(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier, EnvType side);

    public static interface Pre {
        Result pre(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float amount, boolean isHeadShot, float headshotMultiplier, EnvType side);
    }

    public static interface Post {
        Result post(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float amount, boolean isHeadShot, float headshotMultiplier, EnvType side);
    }

    public static class Result {
        protected @Nullable Entity hurtEntity;
        protected @Nullable LivingEntity attacker;
        protected Identifier gunId;
        protected float baseAmount;
        protected boolean isHeadShot;
        protected float headshotMultiplier;
        private boolean cancelled = false;

        protected Result(@Nullable Entity hurtEntity, @Nullable LivingEntity attacker, Identifier gunId, float baseAmount, boolean isHeadShot, float headshotMultiplier) {
            this.hurtEntity = hurtEntity;
            this.attacker = attacker;
            this.gunId = gunId;
            this.baseAmount = baseAmount;
            this.isHeadShot = isHeadShot;
            this.headshotMultiplier = headshotMultiplier;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
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
    }
}
