package com.tacz.guns.entity;

import com.mojang.authlib.GameProfile;
import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.entity.ITargetEntity;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.config.common.OtherConfig;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.init.ModSounds;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.event.ServerMessageGunHurt;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetMinecart extends AbstractMinecartEntity implements ITargetEntity {
    public static EntityType<TargetMinecart> TYPE = EntityType.Builder.<TargetMinecart>create(TargetMinecart::new, SpawnGroup.MISC)
            .setDimensions(0.75F, 2.4F)
            .maxTrackingRange(8)
            .build("target_minecart");

    private @Nullable GameProfile gameProfile = null;

    public TargetMinecart(EntityType<TargetMinecart> type, World world) {
        super(type, world);
    }

    public TargetMinecart(World level, double x, double y, double z) {
        super(TYPE, level, x, y, z);
    }

    @Override
    public void onProjectileHit(Entity entity, EntityHitResult result, DamageSource source, float damage) {
        if (this.getWorld().isClient() || this.isRemoved()) {
            return;
        }
        if (!(source.isIndirect())) {
            return;
        }
        Entity sourceEntity = source.getAttacker();
        if (sourceEntity instanceof PlayerEntity player) {
            this.setDamageWobbleSide(-1);
            this.setDamageWobbleTicks(10);
            this.scheduleVelocityUpdate();
            this.setDamageWobbleStrength(10);
            double dis = this.getPos().distanceTo(sourceEntity.getPos());
            player.sendMessage(Text.translatable("message.tacz-fabric.target_minecart.hit", String.format("%.1f", damage), String.format("%.2f", dis)), true);
            // 原版的声音传播距离由 volume 决定
            // 当声音大于 1 时，距离为 = 16 * volume
            float volume = OtherConfig.TARGET_SOUND_DISTANCE.get() / 16.0f;
            volume = Math.max(volume, 0);
            getWorld().playSoundFromEntity(null, this, ModSounds.TARGET_HIT, SoundCategory.BLOCKS, volume, this.getWorld().random.nextFloat() * 0.1F + 0.9F);

            if (entity instanceof EntityKineticBullet projectile) {
                boolean isHeadshot = false;
                float headshotMultiplier = 1;
                new EntityHurtByGunEvent.Post(this, player, projectile.getGunId(), damage, isHeadshot, headshotMultiplier, LogicalSide.SERVER).post();
                NetworkHandler.sendToDimension(new ServerMessageGunHurt(this.getId(), player.getId(), projectile.getGunId(), damage, isHeadshot, headshotMultiplier), this);
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.isIn(DamageTypeTags.IS_EXPLOSION) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean shouldRender(double distance) {
        double size = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(size)) {
            size = 1.0;
        }
        size *= RenderConfig.TARGET_RENDER_DISTANCE.get() * getRenderDistanceMultiplier();
        return distance < size * size;
    }

    @Override
    public void dropItems(DamageSource source) {
        this.remove(Entity.RemovalReason.KILLED);
        if (this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack itemStack = new ItemStack(ModItems.TARGET_MINECART);
            if (this.hasCustomName()) {
                itemStack.setCustomName(this.getCustomName());
            }
            this.dropStack(itemStack);
        }
    }

    @Override
    public Item getItem() {
        return ModItems.TARGET_MINECART;
    }

    @Override
    public ItemStack getPickBlockStack() {
        ItemStack itemStack = new ItemStack(ModItems.TARGET_MINECART);
        if (this.hasCustomName()) {
            itemStack.setCustomName(this.getCustomName());
        }
        return itemStack;
    }

    @Nullable
    public GameProfile getGameProfile() {
        if (this.gameProfile == null && this.getCustomName() != null) {
            this.gameProfile = new GameProfile(null, this.getCustomName().getString());
            SkullBlockEntity.loadProperties(this.gameProfile, gameProfile -> this.gameProfile = gameProfile);
        }
        return gameProfile;
    }

    @Override
    @NotNull
    public BlockState getDefaultContainedBlock() {
        return ModBlocks.TARGET.getDefaultState();
    }

    @NotNull
    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    protected double getMaxSpeed() {
        return 0.2F;
    }
}
