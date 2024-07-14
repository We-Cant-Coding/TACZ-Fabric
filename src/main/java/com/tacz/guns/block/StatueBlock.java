package com.tacz.guns.block;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class StatueBlock extends BlockWithEntity {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public StatueBlock() {
        super(Settings.create().sounds(BlockSoundGroup.STONE).strength(2.0F, 3.0F).pistonBehavior(PistonBehavior.DESTROY).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(FACING, Direction.NORTH)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> blockEntityType) {
        return state.get(HALF).equals(DoubleBlockHalf.LOWER) && level.isClient() ? checkType(blockEntityType, ModBlocks.STATUE_BE, StatueBlockEntity::clientTick) : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.get(HALF) == DoubleBlockHalf.LOWER ? new StatueBlockEntity(pPos, pState) : null;
    }

    @Override
    public ActionResult onUse(BlockState pState, World level, BlockPos pos, PlayerEntity player, Hand pHand, BlockHitResult pHit) {
        if (level.isClient()) {
            return ActionResult.SUCCESS;
        } else {
            if (pState.get(HALF) == DoubleBlockHalf.UPPER) {
                pos = pos.down();
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StatueBlockEntity statueBlockEntity) {
                ItemStack stack = player.getStackInHand(pHand);
                if (stack.getItem() instanceof IGun) {
                    statueBlockEntity.setGun(stack);
                    stack.decrement(1);
                    return ActionResult.SUCCESS;
                }

                if (stack.isEmpty()) {
                    statueBlockEntity.dropItem();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction direction = context.getHorizontalPlayerFacing().getOpposite();
        BlockPos clickedPos = context.getBlockPos();
        BlockPos above = clickedPos.up();
        World level = context.getWorld();
        if (level.getBlockState(above).canReplace(context) && level.getWorldBorder().contains(above)) {
            return this.getDefaultState().with(FACING, direction);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (!world.isClient) {
            BlockPos above = pos.up();
            world.setBlockState(above, state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.get(HALF);

        if (facing.getAxis() == Direction.Axis.Y) {
            if (half.equals(DoubleBlockHalf.LOWER) && facing == Direction.UP || half.equals(DoubleBlockHalf.UPPER) && facing == Direction.DOWN) {
                // 拆一半另外一半跟着没
                if (!facingState.isOf(this)) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }

        return state;
    }

    @Override
    public void onStateReplaced(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.isOf(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof StatueBlockEntity statueBlockEntity) {
                statueBlockEntity.dropItem();
            }
            super.onStateReplaced(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
