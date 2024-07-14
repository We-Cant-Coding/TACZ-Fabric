package com.tacz.guns.util.block;

import com.tacz.guns.config.common.AmmoConfig;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class BlockRayTrace {
    private static final Predicate<BlockState> IGNORES = input -> input != null &&
            ((input.getBlock() instanceof LeavesBlock) ||
                    (input.getBlock() instanceof FenceBlock) ||
                    (input.isOf(Blocks.IRON_BARS)) ||
                    (input.getBlock() instanceof FenceGateBlock));

    public static BlockHitResult rayTraceBlocks(World world, RaycastContext context) {
        return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            // 这里添加判断方块是否可以穿透，如果可以穿透则返回 null
            List<String> ids = AmmoConfig.PASS_THROUGH_BLOCKS.get();
            Identifier blockId = Registries.BLOCK.getId(blockState.getBlock());
            if (ids.contains(blockId.toString())) {
                return null;
            }
            // 硬编码控制某些方块
            if (IGNORES.test(blockState)) {
                return null;
            }
            return getBlockHitResult(world, rayTraceContext, blockPos, blockState);
        }, (rayTraceContext) -> {
            Vec3d vec3 = rayTraceContext.getStart().subtract(rayTraceContext.getEnd());
            return BlockHitResult.createMissed(rayTraceContext.getEnd(), Direction.getFacing(vec3.x, vec3.y, vec3.z), BlockPos.ofFloored(rayTraceContext.getEnd()));
        });
    }

    @Nullable
    private static BlockHitResult getBlockHitResult(World world, RaycastContext rayTraceContext, BlockPos blockPos, BlockState blockState) {
        FluidState fluidState = world.getFluidState(blockPos);
        Vec3d startVec = rayTraceContext.getStart();
        Vec3d endVec = rayTraceContext.getEnd();
        VoxelShape blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
        BlockHitResult blockResult = world.raycastBlock(startVec, endVec, blockPos, blockShape, blockState);
        VoxelShape fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
        BlockHitResult fluidResult = fluidShape.raycast(startVec, endVec, blockPos);
        double blockDistance = blockResult == null ? Double.MAX_VALUE : rayTraceContext.getStart().squaredDistanceTo(blockResult.getPos());
        double fluidDistance = fluidResult == null ? Double.MAX_VALUE : rayTraceContext.getStart().squaredDistanceTo(fluidResult.getPos());
        return blockDistance <= fluidDistance ? blockResult : fluidResult;
    }

    private static <T> T performRayTrace(RaycastContext context, BiFunction<RaycastContext, BlockPos, T> hitFunction, Function<RaycastContext, T> missFactory) {
        Vec3d startVec = context.getStart();
        Vec3d endVec = context.getEnd();
        if (!startVec.equals(endVec)) {
            double startX = MathHelper.lerp(-0.0000001, endVec.x, startVec.x);
            double startY = MathHelper.lerp(-0.0000001, endVec.y, startVec.y);
            double startZ = MathHelper.lerp(-0.0000001, endVec.z, startVec.z);
            double endX = MathHelper.lerp(-0.0000001, startVec.x, endVec.x);
            double endY = MathHelper.lerp(-0.0000001, startVec.y, endVec.y);
            double endZ = MathHelper.lerp(-0.0000001, startVec.z, endVec.z);

            int blockX = MathHelper.floor(endX);
            int blockY = MathHelper.floor(endY);
            int blockZ = MathHelper.floor(endZ);

            BlockPos.Mutable mutablePos = new BlockPos.Mutable(blockX, blockY, blockZ);
            T t = hitFunction.apply(context, mutablePos);
            if (t != null) {
                return t;
            }

            double deltaX = startX - endX;
            double deltaY = startY - endY;
            double deltaZ = startZ - endZ;
            int signX = MathHelper.sign(deltaX);
            int signY = MathHelper.sign(deltaY);
            int signZ = MathHelper.sign(deltaZ);
            double d9 = signX == 0 ? Double.MAX_VALUE : (double) signX / deltaX;
            double d10 = signY == 0 ? Double.MAX_VALUE : (double) signY / deltaY;
            double d11 = signZ == 0 ? Double.MAX_VALUE : (double) signZ / deltaZ;
            double d12 = d9 * (signX > 0 ? 1.0D - MathHelper.fractionalPart(endX) : MathHelper.fractionalPart(endX));
            double d13 = d10 * (signY > 0 ? 1.0D - MathHelper.fractionalPart(endY) : MathHelper.fractionalPart(endY));
            double d14 = d11 * (signZ > 0 ? 1.0D - MathHelper.fractionalPart(endZ) : MathHelper.fractionalPart(endZ));

            while (d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
                if (d12 < d13) {
                    if (d12 < d14) {
                        blockX += signX;
                        d12 += d9;
                    } else {
                        blockZ += signZ;
                        d14 += d11;
                    }
                } else if (d13 < d14) {
                    blockY += signY;
                    d13 += d10;
                } else {
                    blockZ += signZ;
                    d14 += d11;
                }

                T t1 = hitFunction.apply(context, mutablePos.set(blockX, blockY, blockZ));
                if (t1 != null) {
                    return t1;
                }
            }
        }
        return missFactory.apply(context);
    }
}
