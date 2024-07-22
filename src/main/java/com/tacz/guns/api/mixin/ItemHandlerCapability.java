package com.tacz.guns.api.mixin;

import com.tacz.guns.util.item.IItemHandler;
import com.tacz.guns.util.LazyOptional;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface ItemHandlerCapability {

    default LazyOptional<IItemHandler> tacz$getItemHandler(@Nullable Direction facing) {
        return LazyOptional.empty();
    }

    default void tacz$invalidateItemHandler() {
    }

    default void tacz$reviveItemHandler() {
    }
}
