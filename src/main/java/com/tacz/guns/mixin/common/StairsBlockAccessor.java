package com.tacz.guns.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StairsBlock.class)
public interface StairsBlockAccessor {
    @Accessor
    BlockState getBaseBlockState();

    @Accessor
    Block getBaseBlock();
}
