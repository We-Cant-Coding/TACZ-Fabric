package com.tacz.guns.item;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.item.nbt.GunItemDataAccessor;
import com.tacz.guns.command.sub.DebugCommand;
import com.tacz.guns.config.common.GunConfig;
import com.tacz.guns.debug.GunMeleeDebug;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.packets.s2c.event.GunFireS2CPacket;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.attachment.AttachmentData;
import com.tacz.guns.resource.pojo.data.attachment.EffectData;
import com.tacz.guns.resource.pojo.data.attachment.MeleeData;
import com.tacz.guns.resource.pojo.data.attachment.Silence;
import com.tacz.guns.resource.pojo.data.gun.*;
import com.tacz.guns.sound.SoundManager;
import com.tacz.guns.util.AttachmentDataUtils;
import com.tacz.guns.util.CycleTaskHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 现代枪的逻辑实现
 */
public class ModernKineticGunItem extends AbstractGunItem implements GunItemDataAccessor {
    public static final String TYPE_NAME = "modern_kinetic";

    public ModernKineticGunItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void bolt(ItemStack gunItem) {
        if (this.getCurrentAmmoCount(gunItem) > 0) {
            this.reduceCurrentAmmoCount(gunItem);
            this.setBulletInBarrel(gunItem, true);
        }
    }

    @Override
    public void shoot(ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, boolean tracer, LivingEntity shooter) {
        Identifier gunId = getGunId(gunItem);
        Optional<CommonGunIndex> gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
        if (gunIndexOptional.isEmpty()) {
            return;
        }
        CommonGunIndex gunIndex = gunIndexOptional.get();
        // 散射影响
        InaccuracyType inaccuracyState = InaccuracyType.getInaccuracyType(shooter);
        final float[] inaccuracy = new float[]{gunIndex.getGunData().getInaccuracy(inaccuracyState)};

        // 消音器影响
        final int[] soundDistance = new int[]{GunConfig.DEFAULT_GUN_FIRE_SOUND_DISTANCE.get()};
        final boolean[] useSilenceSound = new boolean[]{false};

        // 配件属性的读取计算
        AttachmentDataUtils.getAllAttachmentData(gunItem, gunIndex.getGunData(), attachmentData -> calculateAttachmentData(attachmentData, inaccuracyState, inaccuracy, soundDistance, useSilenceSound));
        inaccuracy[0] = Math.max(0, inaccuracy[0]);

        BulletData bulletData = gunIndex.getBulletData();
        Identifier ammoId = gunIndex.getGunData().getAmmoId();
        FireMode fireMode = getFireMode(gunItem);
        // 子弹飞行速度
        float speed = MathHelper.clamp(bulletData.getSpeed() / 20, 0, Float.MAX_VALUE);
        // 弹丸数量
        int bulletAmount = Math.max(bulletData.getBulletAmount(), 1);
        // 连发数量
        int cycles = fireMode == FireMode.BURST ? gunIndex.getGunData().getBurstData().getCount() : 1;
        // 连发间隔
        long period = fireMode == FireMode.BURST ? gunIndex.getGunData().getBurstShootInterval() : 1;
        // 是否消耗弹药
        boolean consumeAmmo = IGunOperator.fromLivingEntity(shooter).consumesAmmoOrNot();

        // 将连发任务委托到循环任务工具
        CycleTaskHelper.addCycleTask(() -> {
            // 如果射击者死亡，取消射击
            if (shooter.isDead()) {
                return false;
            }
            // 削减弹药数
            if (consumeAmmo) {
                Bolt boltType = gunIndex.getGunData().getBolt();
                boolean hasAmmoInBarrel = this.hasBulletInBarrel(gunItem) && boltType != Bolt.OPEN_BOLT;
                int ammoCount = this.getCurrentAmmoCount(gunItem) + (hasAmmoInBarrel ? 1 : 0);
                if (ammoCount <= 0) {
                    return false;
                }
            }
            // 触发击发事件
            boolean fire = !new GunFireEvent(shooter, gunItem, LogicalSide.SERVER).post();
            if (fire) {
                NetworkHandler.sendToTrackingEntity(new GunFireS2CPacket(shooter.getId(), gunItem), shooter);
                if (consumeAmmo) {
                    // 削减弹药
                    this.reduceAmmo(gunItem);
                }
                // 生成子弹
                World world = shooter.getWorld();
                for (int i = 0; i < bulletAmount; i++) {
                    this.doSpawnBulletEntity(world, shooter, pitch.get(), yaw.get(), speed, inaccuracy[0], ammoId, gunId, tracer, bulletData);
                }
                // 播放枪声
                if (soundDistance[0] > 0) {
                    String soundId = useSilenceSound[0] ? SoundManager.SILENCE_3P_SOUND : SoundManager.SHOOT_3P_SOUND;
                    SoundManager.sendSoundToNearby(shooter, soundDistance[0], gunId, soundId, 0.8f, 0.9f + shooter.getRandom().nextFloat() * 0.125f);
                }
            }
            return true;
        }, period, cycles);
    }

    @Override
    public void melee(LivingEntity user, ItemStack gunItem) {
        Identifier gunId = this.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(gunIndex -> {
            GunMeleeData meleeData = gunIndex.getGunData().getMeleeData();
            float distance = meleeData.getDistance();

            Identifier muzzleId = this.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            MeleeData muzzleData = getMeleeData(muzzleId);
            if (muzzleData != null) {
                doMelee(user, distance, muzzleData.getDistance(), muzzleData.getRangeAngle(), muzzleData.getKnockback(), muzzleData.getDamage(), muzzleData.getEffects());
                return;
            }

            Identifier stockId = this.getAttachmentId(gunItem, AttachmentType.STOCK);
            MeleeData stockData = getMeleeData(stockId);
            if (stockData != null) {
                doMelee(user, distance, stockData.getDistance(), stockData.getRangeAngle(), stockData.getKnockback(), stockData.getDamage(), stockData.getEffects());
                return;
            }

            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            if (defaultData == null) {
                return;
            }
            doMelee(user, distance, defaultData.getDistance(), defaultData.getRangeAngle(), defaultData.getKnockback(), defaultData.getDamage(), Collections.emptyList());
        });
    }

    private void doMelee(LivingEntity user, float gunDistance, float meleeDistance, float rangeAngle, float knockback, float damage, List<EffectData> effects) {
        // 枪长 + 刺刀长 = 总长
        double distance = gunDistance + meleeDistance;
        float xRot = (float) Math.toRadians(-user.getPitch());
        float yRot = (float) Math.toRadians(-user.getYaw());
        // 视角向量
        Vec3d eyeVec = new Vec3d(0, 0, 1).rotateX(xRot).rotateY(yRot).normalize().multiply(distance);
        // 球心坐标
        Vec3d centrePos = user.getEyePos().subtract(eyeVec);
        // 先获取范围内所有的实体
        List<LivingEntity> entityList = user.getWorld().getNonSpectatingEntities(LivingEntity.class, user.getBoundingBox().expand(distance));
        // 而后检查是否在锥形范围内
        for (LivingEntity living : entityList) {
            // 先计算出球心->目标向量
            Vec3d targetVec = living.getEyePos().subtract(centrePos);
            // 目标到球心距离
            double targetLength = targetVec.length();
            // 距离在一倍距离之内的，在玩家背后，不进行伤害
            if (targetLength < distance) {
                continue;
            }
            // 计算出向量夹角
            double degree = Math.toDegrees(Math.acos(targetVec.dotProduct(eyeVec) / (targetLength * distance)));
            // 向量夹角在范围内的，才能进行伤害
            if (degree < (rangeAngle / 2)) {
                doPerLivingHurt(user, living, knockback, damage, effects);
            }
        }

        // 玩家扣饱食度
        if (user instanceof PlayerEntity player) {
            player.addExhaustion(0.1F);
        }

        // Debug 模式
        if (DebugCommand.DEBUG) {
            GunMeleeDebug.showRange(user, (int) Math.round(distance), centrePos, eyeVec, rangeAngle);
        }
    }

    private static void doPerLivingHurt(LivingEntity user, LivingEntity target, float knockback, float damage, List<EffectData> effects) {
        if (target.equals(user)) {
            return;
        }
        target.takeKnockback(knockback, (float) Math.sin(Math.toRadians(user.getYaw())), (float) -Math.cos(Math.toRadians(user.getYaw())));
        if (user instanceof PlayerEntity player) {
            target.damage(user.getDamageSources().playerAttack(player), damage);
        } else {
            target.damage(user.getDamageSources().mobAttack(user), damage);
        }
        if (!target.isAlive()) {
            return;
        }
        for (EffectData data : effects) {
            StatusEffect mobEffect = Registries.STATUS_EFFECT.get(data.getEffectId());
            if (mobEffect == null) {
                continue;
            }
            int time = Math.max(0, data.getTime() * 20);
            int amplifier = Math.max(0, data.getAmplifier());
            StatusEffectInstance effectInstance = new StatusEffectInstance(mobEffect, time, amplifier, false, data.isHideParticles());
            target.addStatusEffect(effectInstance);
        }
        if (user.getWorld() instanceof ServerWorld serverLevel) {
            int count = (int) (damage * 0.5);
            serverLevel.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), count, 0.1, 0, 0.1, 0.2);
        }
    }

    @Override
    public void fireSelect(ItemStack gunItem) {
        Identifier gunId = this.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).map(gunIndex -> {
            FireMode fireMode = this.getFireMode(gunItem);
            List<FireMode> fireModeSet = gunIndex.getGunData().getFireModeSet();
            // 即使玩家拿的是没有的 FireMode，这里也能切换到正常情况
            int nextIndex = (fireModeSet.indexOf(fireMode) + 1) % fireModeSet.size();
            FireMode nextFireMode = fireModeSet.get(nextIndex);
            this.setFireMode(gunItem, nextFireMode);
            return nextFireMode;
        });
    }

    @Override
    public void reloadAmmo(ItemStack gunItem, int ammoCount, boolean loadBarrel) {
        Identifier gunId = getGunId(gunItem);
        Bolt boltType = TimelessAPI.getCommonGunIndex(gunId).map(index -> index.getGunData().getBolt()).orElse(null);
        this.setCurrentAmmoCount(gunItem, ammoCount);
        if (loadBarrel && (boltType == Bolt.MANUAL_ACTION || boltType == Bolt.CLOSED_BOLT)) {
            this.reduceCurrentAmmoCount(gunItem);
            this.setBulletInBarrel(gunItem, true);
        }
    }

    /**
     * 生成子弹实体
     */
    protected void doSpawnBulletEntity(World world, LivingEntity shooter, float pitch, float yaw, float speed, float inaccuracy, Identifier ammoId, Identifier gunId, boolean tracer, BulletData bulletData) {
        EntityKineticBullet bullet = new EntityKineticBullet(world, shooter, ammoId, gunId, tracer, bulletData);
        bullet.setVelocity(bullet, pitch, yaw, 0.0F, speed, inaccuracy);
        world.spawnEntity(bullet);
    }

    @Override
    public int getLevel(int exp) {
        return 0;
    }

    @Override
    public int getExp(int level) {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    /**
     * 将枪内的弹药数减少。
     *
     * @param currentGunItem 枪械物品
     */
    protected void reduceAmmo(ItemStack currentGunItem) {
        Bolt boltType = TimelessAPI.getCommonGunIndex(getGunId(currentGunItem)).map(index -> index.getGunData().getBolt()).orElse(null);
        if (boltType == null) {
            return;
        }
        if (boltType == Bolt.MANUAL_ACTION) {
            this.setBulletInBarrel(currentGunItem, false);
        } else if (boltType == Bolt.CLOSED_BOLT) {
            if (this.getCurrentAmmoCount(currentGunItem) > 0) {
                this.reduceCurrentAmmoCount(currentGunItem);
            } else {
                this.setBulletInBarrel(currentGunItem, false);
            }
        } else {
            this.reduceCurrentAmmoCount(currentGunItem);
        }
    }

    private void calculateAttachmentData(AttachmentData attachmentData, InaccuracyType inaccuracyState, float[] inaccuracy, int[] soundDistance, boolean[] useSilenceSound) {
        // 影响除瞄准外所有的不准确度
        if (!inaccuracyState.isAim()) {
            inaccuracy[0] += attachmentData.getInaccuracyAddend();
        }
        Silence silence = attachmentData.getSilence();
        if (silence != null) {
            soundDistance[0] += silence.getDistanceAddend();
            if (silence.isUseSilenceSound()) {
                useSilenceSound[0] = true;
            }
        }
    }

    @Nullable
    private MeleeData getMeleeData(Identifier attachmentId) {
        if (DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            return null;
        }
        return TimelessAPI.getCommonAttachmentIndex(attachmentId).map(index -> index.getData().getMeleeData()).orElse(null);
    }
}
