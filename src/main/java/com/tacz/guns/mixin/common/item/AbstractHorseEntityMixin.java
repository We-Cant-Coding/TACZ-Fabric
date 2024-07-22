package com.tacz.guns.mixin.common.item;

import com.tacz.guns.util.item.IItemHandler;
import com.tacz.guns.util.item.wrapper.InvWrapper;
import com.tacz.guns.util.LazyOptional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity {

    @Shadow protected SimpleInventory items;
    @Unique
    private LazyOptional<?> itemHandler = null;

    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onChestedStatusChanged", at = @At("TAIL"))
    private void createInventory(CallbackInfo ci) {
        this.itemHandler = LazyOptional.of(() -> new InvWrapper(this.items));
    }

    @Override
    public LazyOptional<IItemHandler> tacz$getItemHandler(@Nullable Direction facing) {
        return isAlive() && itemHandler != null ? itemHandler.cast() : super.tacz$getItemHandler(facing);
    }

    @Override
    public void tacz$invalidateItemHandler() {
        super.tacz$invalidateItemHandler();
        if (this.itemHandler != null) {
            LazyOptional<?> oldHandler = this.itemHandler;
            this.itemHandler = null;
            oldHandler.invalidate();
        }
    }
}
