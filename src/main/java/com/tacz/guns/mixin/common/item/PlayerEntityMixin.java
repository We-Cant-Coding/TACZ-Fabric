package com.tacz.guns.mixin.common.item;

import com.mojang.authlib.GameProfile;
import com.tacz.guns.util.item.IItemHandler;
import com.tacz.guns.util.item.wrapper.*;
import com.tacz.guns.util.LazyOptional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow @Final private PlayerInventory inventory;

    @Unique
    private LazyOptional<IItemHandler> playerMainHandler;
    @Unique
    private LazyOptional<IItemHandler> playerEquipmentHandler;
    @Unique
    private LazyOptional<IItemHandler> playerJoinedHandler;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initClass(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        this.playerMainHandler = LazyOptional.of(() ->
                new PlayerMainInvWrapper(this.inventory));
        this.playerEquipmentHandler = LazyOptional.of(() ->
                new CombinedInvWrapper(new PlayerArmorInvWrapper(this.inventory), new PlayerOffhandInvWrapper(this.inventory)));
        this.playerJoinedHandler = LazyOptional.of(() ->
                new PlayerInvWrapper(this.inventory));
    }

    @Override
    public LazyOptional<IItemHandler> tacz$getItemHandler(@Nullable Direction facing) {
        if (isAlive()) {
            if (facing == null) {
                return this.playerJoinedHandler.cast();
            }

            if (facing.getAxis().isVertical()) {
                return this.playerMainHandler.cast();
            }

            if (facing.getAxis().isHorizontal()) {
                return this.playerEquipmentHandler.cast();
            }
        }

        return super.tacz$getItemHandler(facing);
    }
}
