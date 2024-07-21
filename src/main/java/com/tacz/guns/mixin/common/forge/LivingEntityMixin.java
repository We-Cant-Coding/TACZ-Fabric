package com.tacz.guns.mixin.common.forge;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.KnockBackModifier;
import com.tacz.guns.forge.items.IItemHandler;
import com.tacz.guns.forge.items.wrapper.EntityEquipmentInvWrapper;
import com.tacz.guns.forge.util.LazyOptional;
import com.tacz.guns.api.mixin.ItemHandlerCapability;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ItemHandlerCapability {
    @Shadow public abstract boolean isAlive();

    @Unique
    private LazyOptional<?>[] handlers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initClass(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        handlers = EntityEquipmentInvWrapper.create(This());
    }

    @Override
    public LazyOptional<IItemHandler> tacz$getItemHandlerCapability(@Nullable Direction facing) {
        if (isAlive()) {
            if (facing == null) {
                return this.handlers[2].cast();
            }

            if (facing.getAxis().isVertical()) {
                return this.handlers[0].cast();
            }

            if (facing.getAxis().isHorizontal()) {
                return this.handlers[1].cast();
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public void tacz$invalidateItemHandlerCaps() {
        for (LazyOptional<?> handler : this.handlers) {
            handler.invalidate();
        }
    }

    @Override
    public void tacz$reviveItemHandlerCaps() {
        this.handlers = EntityEquipmentInvWrapper.create(This());
    }

    @Unique
    private LivingEntity This() {
        return (LivingEntity) (Object) this;
    }
}