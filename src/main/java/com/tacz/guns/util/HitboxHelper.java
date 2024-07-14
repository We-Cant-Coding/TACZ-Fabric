package com.tacz.guns.util;

import com.tacz.guns.api.entity.ITargetEntity;
import com.tacz.guns.config.common.OtherConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.WeakHashMap;

public class HitboxHelper {
    // 玩家位置缓存表
    private static final WeakHashMap<PlayerEntity, LinkedList<Vec3d>> PLAYER_POSITION = new WeakHashMap<>();
    // 玩家命中箱缓存表
    private static final WeakHashMap<PlayerEntity, LinkedList<Box>> PLAYER_HITBOXES = new WeakHashMap<>();
    // 玩家速度缓存表
    private static final WeakHashMap<PlayerEntity, LinkedList<Vec3d>> PLAYER_VELOCITY = new WeakHashMap<>();
    // 命中箱缓存 Tick 上限
    private static final int SAVE_TICK = MathHelper.floor(OtherConfig.SERVER_HITBOX_LATENCY_MAX_SAVE_MS.get() / 1000 * 20 + 0.5);

    public static void onPlayerTick(PlayerEntity player) {
        if (player.isSpectator()) {
            PLAYER_POSITION.remove(player);
            PLAYER_HITBOXES.remove(player);
            PLAYER_VELOCITY.remove(player);
            return;
        }
        LinkedList<Vec3d> positions = PLAYER_POSITION.computeIfAbsent(player, p -> new LinkedList<>());
        LinkedList<Box> boxes = PLAYER_HITBOXES.computeIfAbsent(player, p -> new LinkedList<>());
        LinkedList<Vec3d> velocities = PLAYER_VELOCITY.computeIfAbsent(player, p -> new LinkedList<>());
        positions.addFirst(player.getPos());
        boxes.addFirst(player.getBoundingBox());
        velocities.addFirst(getPlayerVelocity(player));
        // Position is used for speed calculations, so only 2 positions need to be cached
        if (positions.size() > 2) {
            positions.removeLast();
        }
        // Hitbox and velocity cache quantity limits
        if (boxes.size() > SAVE_TICK) {
            boxes.removeLast();
            velocities.removeLast();
        }
    }

    public static void onPlayerLoggedOut(PlayerEntity player) {
        PLAYER_POSITION.remove(player);
        PLAYER_HITBOXES.remove(player);
        PLAYER_VELOCITY.remove(player);
    }

    public static Vec3d getPlayerVelocity(PlayerEntity entity) {
        LinkedList<Vec3d> positions = PLAYER_POSITION.computeIfAbsent(entity, player -> new LinkedList<>());
        if (positions.size() > 1) {
            Vec3d currPos = positions.getFirst();
            Vec3d prevPos = positions.getLast();
            return new Vec3d(currPos.x - prevPos.x, currPos.y - prevPos.y, currPos.z - prevPos.z);
        }

        return new Vec3d(0, 0, 0);
    }

    public static Box getBoundingBox(PlayerEntity entity, int ping) {
        if (PLAYER_HITBOXES.containsKey(entity)) {
            LinkedList<Box> boxes = PLAYER_HITBOXES.get(entity);
            int index = MathHelper.clamp(ping, 0, boxes.size() - 1);
            return boxes.get(index);
        }

        return entity.getBoundingBox();
    }

    public static Vec3d getVelocity(PlayerEntity entity, int ping) {
        if (PLAYER_VELOCITY.containsKey(entity)) {
            LinkedList<Vec3d> velocities = PLAYER_VELOCITY.get(entity);
            int index = MathHelper.clamp(ping, 0, velocities.size() - 1);
            return velocities.get(index);
        }

        return getPlayerVelocity(entity);
    }

    public static Box getFixedBoundingBox(Entity entity, Entity owner) {
        Box boundingBox = entity.getBoundingBox();
        Vec3d velocity = new Vec3d(entity.getX() - entity.lastRenderX, entity.getY() - entity.lastRenderY, entity.getZ() - entity.lastRenderZ);
        // hitbox Delay compensation. Only if the shooter is a player (and the person hit is also a player) will this delay compensation be computed
        if (OtherConfig.SERVER_HITBOX_LATENCY_FIX.get() && entity instanceof ServerPlayerEntity player && owner instanceof ServerPlayerEntity serverPlayerOwner) {
            int ping = MathHelper.floor((serverPlayerOwner.pingMilliseconds / 1000.0) * 20.0 + 0.5);
            boundingBox = getBoundingBox(player, ping);
            velocity = getVelocity(player, ping);
        }
        // Deformation of hitbox due to application crouching
        double expandHeight = entity instanceof PlayerEntity && !entity.isInSneakingPose() ? 0.0625 : 0.0;
        boundingBox = boundingBox.stretch(0, expandHeight, 0);
        // Extend the hitbox somewhat according to the speed
        boundingBox = boundingBox.stretch(velocity);
        // Player hitbox correction, can be adjusted via Config
        double playerHitboxOffset = OtherConfig.SERVER_HITBOX_OFFSET.get();
        if (entity instanceof ServerPlayerEntity) {
            if (entity.getVehicle() != null) {
                boundingBox = boundingBox.offset(velocity.multiply(playerHitboxOffset / 2, playerHitboxOffset / 2, playerHitboxOffset / 2));
            }
            boundingBox = boundingBox.offset(velocity.multiply(playerHitboxOffset, playerHitboxOffset, playerHitboxOffset));
        }
        // A Hitbox offset applied uniformly to all entities with a constant value derived from experimentation.
        if (entity.getVehicle() != null || entity instanceof ITargetEntity) {
            boundingBox = boundingBox.offset(velocity.multiply(-2.5, -2.5, -2.5));
        }
        boundingBox = boundingBox.offset(velocity.multiply(-5, -5, -5));
        return boundingBox;
    }
}
