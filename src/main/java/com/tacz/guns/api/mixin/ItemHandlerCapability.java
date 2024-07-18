package com.tacz.guns.api.mixin;

import com.tacz.guns.forge.items.IItemHandler;
import com.tacz.guns.forge.util.LazyOptional;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface ItemHandlerCapability {

    default LazyOptional<IItemHandler> tacz$getItemHandlerCapability(@Nullable Direction facing) {
        return LazyOptional.empty();
    }

    default void tacz$invalidateItemHandlerCaps() {
    }

    default void tacz$reviveItemHandlerCaps() {
    };
}
