package com.tacz.guns.block;

import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.util.DirectionUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableBlock extends BlockWithEntity {
    public static final VoxelShape BLOCK_AABB = Block.createCuboidShape(0, 0, 0, 16, 15, 16);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;

    public GunSmithTableBlock() {
        super(AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOD).strength(2.0F, 3.0F).pistonBehavior(PistonBehavior.DESTROY).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(PART, BedPart.FOOT));
    }

    private static Direction getNeighbourDirection(BedPart bedPart, Direction direction) {
        return bedPart == BedPart.FOOT ? DirectionUtil.getRight(direction) : DirectionUtil.getLeft(direction);
    }

    @Override
    public ActionResult onUse(BlockState pState, World level, BlockPos pos, PlayerEntity player, Hand pHand, BlockHitResult pHit) {
        if (level.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GunSmithTableBlockEntity gunSmithTable) {
                player.openHandledScreen(gunSmithTable);
            }
            return ActionResult.CONSUME;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState blockState) {
        return new GunSmithTableBlockEntity(pos, blockState);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction direction = context.getHorizontalPlayerFacing();
        BlockPos clickedPos = context.getBlockPos();
        BlockPos relative = clickedPos.offset(DirectionUtil.getRight(direction));
        World level = context.getWorld();
        if (level.getBlockState(relative).canReplace(context) && level.getWorldBorder().contains(relative)) {
            return this.getDefaultState().with(FACING, direction);
        }
        return null;
    }

    @Override
    public void onBreak(World level, BlockPos pos, BlockState blockState, PlayerEntity player) {
        if (!level.isClient && player.isCreative()) {
            BedPart bedPart = blockState.get(PART);
            if (bedPart == BedPart.FOOT) {
                BlockPos blockpos = pos.offset(getNeighbourDirection(bedPart, blockState.get(FACING)));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.isOf(this) && blockstate.get(PART) == BedPart.HEAD) {
                    level.setBlockState(blockpos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    level.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockpos, Block.getRawIdFromState(blockstate));
                }
            }
        }
        super.onBreak(level, pos, blockState, player);
    }

    @Override
    public void onPlaced(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(worldIn, pos, state, placer, stack);
        if (!worldIn.isClient) {
            BlockPos relative = pos.offset(DirectionUtil.getRight(state.get(FACING)));
            worldIn.setBlockState(relative, state.with(PART, BedPart.HEAD), Block.NOTIFY_ALL);
            worldIn.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(worldIn, pos, Block.NOTIFY_ALL);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState facingState, WorldAccess level, BlockPos currentPos, BlockPos facingPos) {
        if (direction == getNeighbourDirection(state.get(PART), state.get(FACING))) {
            return facingState.isOf(this) && facingState.get(PART) != state.get(PART) ? state : Blocks.AIR.getDefaultState();
        } else {
            return super.getStateForNeighborUpdate(state, direction, facingState, level, currentPos, facingPos);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState pState) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return BLOCK_AABB;
    }
}
