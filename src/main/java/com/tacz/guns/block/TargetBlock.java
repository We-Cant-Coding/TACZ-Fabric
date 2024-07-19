package com.tacz.guns.block;

import com.mojang.authlib.GameProfile;
import com.tacz.guns.block.entity.TargetBlockEntity;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class TargetBlock extends BlockWithEntity {
    public static final IntProperty OUTPUT_POWER = Properties.POWER;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty STAND = BooleanProperty.of("stand");
    public static final VoxelShape BOX_BOTTOM_STAND_X = VoxelShapes.union(Block.createCuboidShape(6, 0, 6, 10, 16, 10), Block.createCuboidShape(6, 13, 2, 10, 16, 14));
    public static final VoxelShape BOX_BOTTOM_STAND_Z = VoxelShapes.union(Block.createCuboidShape(6, 0, 6, 10, 16, 10), Block.createCuboidShape(2, 13, 6, 14, 16, 10));
    public static final VoxelShape BOX_BOTTOM_DOWN = Block.createCuboidShape(6, 0, 6, 10, 4, 10);
    public static final VoxelShape BOX_UPPER_X = Block.createCuboidShape(6, 0, 2, 10, 16, 14);
    public static final VoxelShape BOX_UPPER_Z = Block.createCuboidShape(2, 0, 6, 14, 16, 10);

    public TargetBlock() {
        super(Settings.create().sounds(BlockSoundGroup.WOOD).strength(2.0F, 3.0F).pistonBehavior(PistonBehavior.DESTROY).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER).with(STAND, true).with(OUTPUT_POWER, 0));
    }

    public static int getRedstoneStrength(BlockHitResult hit, boolean isUpperBlock) {
        // 击中下方，恒为 1
        if (!isUpperBlock) {
            return 1;
        }
        Vec3d hitLocation = hit.getPos();
        Direction direction = hit.getSide();
        // 标靶中心为 (0.5, 0.32, 0.5)
        double x = Math.abs(MathHelper.fractionalPart(hitLocation.x) - 0.5);
        double y = Math.abs(MathHelper.fractionalPart(hitLocation.y) - 0.32);
        double z = Math.abs(MathHelper.fractionalPart(hitLocation.z) - 0.5);
        Direction.Axis axis = direction.getAxis();
        double distance;
        if (axis == Direction.Axis.Y) {
            distance = Math.max(x, z);
        } else if (axis == Direction.Axis.Z) {
            distance = Math.max(x, y);
        } else {
            distance = Math.max(y, z);
        }
        // 离开中心 0.25 单位就是最低分？
        double percent = MathHelper.clamp((0.25 - distance) / 0.25, 0, 1);
        return Math.max(1, MathHelper.ceil(15 * percent));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return state.get(HALF).equals(DoubleBlockHalf.LOWER) && world.isClient() ? checkType(type, ModBlocks.TARGET_BE, TargetBlockEntity::clientTick) : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, STAND, OUTPUT_POWER);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState blockState) {
        if (blockState.get(HALF).equals(DoubleBlockHalf.LOWER)) {
            return new TargetBlockEntity(pos, blockState);
        } else {
            return null;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        boolean stand = state.get(STAND);
        boolean axis = state.get(FACING).getAxis().equals(Direction.Axis.X);
        if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
            return stand ? (axis ? BOX_UPPER_X : BOX_UPPER_Z) : VoxelShapes.empty();
        }
        return stand ? (axis ? BOX_BOTTOM_STAND_X : BOX_BOTTOM_STAND_Z) : BOX_BOTTOM_DOWN;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
        // 计划刻的内容
        if (!state.get(STAND)) {
            level.setBlockState(pos, state.with(STAND, true).with(OUTPUT_POWER, 0), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (hit.getSide().getOpposite().equals(state.get(FACING))) {
            if (state.get(HALF).equals(DoubleBlockHalf.LOWER)) {
                world.getBlockEntity(hit.getBlockPos(), TargetBlockEntity.TYPE).ifPresent(e -> e.hit(world, state, hit, false));
            } else if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                world.getBlockEntity(hit.getBlockPos().down(), TargetBlockEntity.TYPE).ifPresent(e -> e.hit(world, state, hit, true));
            }

            if (!world.isClient() && projectile.getOwner() instanceof PlayerEntity player && state.get(STAND)) {
                if (projectile instanceof EntityKineticBullet bullet) {
                    String formattedDamage = String.format("%.1f", bullet.getDamage(hit.getPos()));
                    String formattedDistance = String.format("%.2f", hit.getPos().distanceTo(player.getPos()));
                    player.sendMessage(Text.translatable("message.tacz-fabric.target_minecart.hit", formattedDamage, formattedDistance), true);
                }

            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.get(HALF);
        boolean stand = state.get(STAND);

        if (facing.getAxis() == Direction.Axis.Y) {
            if (half.equals(DoubleBlockHalf.LOWER) && facing == Direction.UP || half.equals(DoubleBlockHalf.UPPER) && facing == Direction.DOWN) {
                // 拆一半另外一半跟着没
                if (!facingState.isOf(this)) {
                    return Blocks.AIR.getDefaultState();
                }
                // 同步击倒状态
                if (facingState.get(STAND) != stand) {
                    return state.with(STAND, facingState.get(STAND)).with(OUTPUT_POWER, facingState.get(OUTPUT_POWER));
                }
            }
        }

        // 底下方块没了也拆掉
        if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canPlaceAt(level, currentPos)) {
            return Blocks.AIR.getDefaultState();
        } else {
            return state;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction direction = context.getHorizontalPlayerFacing();
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
            if (stack.hasCustomName()) {
                BlockEntity blockentity = world.getBlockEntity(pos);
                if (blockentity instanceof TargetBlockEntity e) {
                    GameProfile gameprofile = new GameProfile(null, stack.getName().getString());
                    e.setOwner(gameprofile);
                    e.setCustomName(stack.getName());
                    e.refresh();
                }
            }
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        BlockPos blockPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockEntity blockentity = world.getBlockEntity(blockPos);
        if (blockentity instanceof TargetBlockEntity e) {
            return new ItemStack(this).setCustomName(e.getCustomName());
        }
        return super.getPickStack(world, pos, state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView level, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = level.getBlockState(blockpos);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        }
        return blockstate.isOf(this);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
        return blockState.get(OUTPUT_POWER);
    }

    @Override
    public void onBlockAdded(BlockState state, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClient() && !state.isOf(oldState.getBlock())) {
            if (state.get(OUTPUT_POWER) > 0 && !level.getBlockTickScheduler().isQueued(pos, this)) {
                level.setBlockState(pos, state.with(OUTPUT_POWER, 0), Block.FORCE_STATE | Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
