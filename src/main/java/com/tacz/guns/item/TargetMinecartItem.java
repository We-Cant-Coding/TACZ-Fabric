package com.tacz.guns.item;

import com.tacz.guns.entity.TargetMinecart;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

public class TargetMinecartItem extends Item {
    public TargetMinecartItem() {
        super((new Item.Settings()).maxCount(1));
    }

    @NotNull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World level = context.getWorld();
        BlockPos blockpos = context.getBlockPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        } else {
            ItemStack itemstack = context.getStack();
            if (!level.isClient) {
                RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock baseRailBlock ? blockstate.get(baseRailBlock.getShapeProperty()) : RailShape.NORTH_SOUTH;
                double yOffset = 0;
                if (railshape.isAscending()) {
                    yOffset = 0.5;
                }
                TargetMinecart targetMinecart = new TargetMinecart(level, (double) blockpos.getX() + 0.5, (double) blockpos.getY() + 0.0625 + yOffset, (double) blockpos.getZ() + 0.5);
                if (itemstack.hasCustomName()) {
                    targetMinecart.setCustomName(itemstack.getName());
                }
                level.spawnEntity(targetMinecart);
                level.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }
            itemstack.decrement(1);
            return ActionResult.success(level.isClient);
        }
    }
}
