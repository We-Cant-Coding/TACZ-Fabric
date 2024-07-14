package com.tacz.guns.util.block;

import com.google.common.collect.Sets;
import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.util.HitboxHelper;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProjectileExplosion extends Explosion {
    private static final ExplosionBehavior DEFAULT_CONTEXT = new ExplosionBehavior();
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final float radius;
    private final boolean knockback;
    private final Entity owner;
    private final Entity exploder;
    private final ExplosionBehavior damageCalculator;

    public ProjectileExplosion(World world, Entity owner, Entity exploder, @Nullable DamageSource source, @Nullable ExplosionBehavior damageCalculator, double x, double y, double z, float power, float radius, boolean knockback, Explosion.DestructionType mode) {
        super(world, exploder, source, damageCalculator, x, y, z, radius, AmmoConfig.EXPLOSIVE_AMMO_FIRE.get(), mode);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.radius = radius;
        this.owner = owner;
        this.exploder = exploder;
        this.damageCalculator = damageCalculator == null ? DEFAULT_CONTEXT : damageCalculator;
        this.knockback = knockback;
    }

    @Override
    public void collectBlocksAndDamageEntities() {
        this.world.emitGameEvent(this.exploder, GameEvent.EXPLODE, BlockPos.ofFloored(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for (int x = 0; x < i; ++x) {
            for (int y = 0; y < i; ++y) {
                for (int z = 0; z < i; ++z) {
                    if (x == 0 || x == i - 1 || y == 0 || y == i - 1 || z == 0 || z == i - 1) {
                        double d0 = ((float) x / (i - 1) * 2.0F - 1.0F);
                        double d1 = ((float) y / (i - 1) * 2.0F - 1.0F);
                        double d2 = ((float) z / (i - 1) * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double blockX = this.x;
                        double blockY = this.y;
                        double blockZ = this.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos pos = BlockPos.ofFloored(blockX, blockY, blockZ);
                            BlockState blockState = this.world.getBlockState(pos);
                            FluidState fluidState = this.world.getFluidState(pos);
                            if (!this.world.isInBuildLimit(pos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlastResistance(this, this.world, pos, blockState, fluidState);
                            if (optional.isPresent()) {
                                f -= (optional.get() + f1) * f1;
                            }

                            if (f > 0.0F && this.damageCalculator.canDestroyBlock(this, this.world, pos, blockState, f)) {
                                set.add(pos);
                            }

                            blockX += d0 * (double) f1;
                            blockY += d1 * (double) f1;
                            blockZ += d2 * (double) f1;
                        }
                    }
                }
            }
        }

        this.getAffectedBlocks().addAll(set);
        float radius = this.radius;
        int minX = MathHelper.floor(this.x - (double) radius - 1.0D);
        int maxX = MathHelper.floor(this.x + (double) radius + 1.0D);
        int minY = MathHelper.floor(this.y - (double) radius - 1.0D);
        int maxY = MathHelper.floor(this.y + (double) radius + 1.0D);
        int minZ = MathHelper.floor(this.z - (double) radius - 1.0D);
        int maxZ = MathHelper.floor(this.z + (double) radius + 1.0D);
        radius *= 2;
        List<Entity> entities = this.world.getOtherEntities(this.exploder, new Box(minX, minY, minZ, maxX, maxY, maxZ));
        Vec3d explosionPos = new Vec3d(this.x, this.y, this.z);

        for (Entity entity : entities) {
            if (entity.isImmuneToExplosion()) {
                continue;
            }

            Box boundingBox = HitboxHelper.getFixedBoundingBox(entity, this.owner);
            BlockHitResult result;
            double strength;
            double deltaX;
            double deltaY;
            double deltaZ;
            double minDistance = radius;

            Vec3d[] d = new Vec3d[15];

            if (!(entity instanceof LivingEntity)) {
                strength = Math.sqrt(entity.squaredDistanceTo(explosionPos)) * 2 / radius;
                deltaX = entity.getX() - this.x;
                deltaY = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                deltaZ = entity.getZ() - this.z;
            } else {
                deltaX = (boundingBox.maxX + boundingBox.minX) / 2;
                deltaY = (boundingBox.maxY + boundingBox.minY) / 2;
                deltaZ = (boundingBox.maxZ + boundingBox.minZ) / 2;
                d[0] = new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                d[1] = new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
                d[2] = new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
                d[3] = new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
                d[4] = new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
                d[5] = new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
                d[6] = new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
                d[7] = new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                d[8] = new Vec3d(boundingBox.minX, deltaY, deltaZ);
                d[9] = new Vec3d(boundingBox.maxX, deltaY, deltaZ);
                d[10] = new Vec3d(deltaX, boundingBox.minY, deltaZ);
                d[11] = new Vec3d(deltaX, boundingBox.maxY, deltaZ);
                d[12] = new Vec3d(deltaX, deltaY, boundingBox.minZ);
                d[13] = new Vec3d(deltaX, deltaY, boundingBox.maxZ);
                d[14] = new Vec3d(deltaX, deltaY, deltaZ);
                for (int s = 0; s < 15; s++) {
                    result = BlockRayTrace.rayTraceBlocks(this.world, new RaycastContext(explosionPos, d[s], RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, null));
                    minDistance = (result.getType() != BlockHitResult.Type.BLOCK) ? Math.min(minDistance, explosionPos.distanceTo(d[s])) : minDistance;
                }
                strength = minDistance * 2 / radius;
                deltaX -= this.x;
                deltaY -= this.y;
                deltaZ -= this.z;
            }

            if (strength > 1.0D) {
                continue;
            }

            double distanceToExplosion = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if (distanceToExplosion != 0.0D) {
                deltaX /= distanceToExplosion;
                deltaY /= distanceToExplosion;
                deltaZ /= distanceToExplosion;
            }

            double damage = 1.0D - strength;
            entity.damage(this.getDamageSource(), (float) damage * this.power);

            if (entity instanceof LivingEntity) {
                damage = (float) ProtectionEnchantment.transformExplosionKnockback((LivingEntity) entity, damage);
            }

            // 启用击退效果
            if (AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.get() && this.knockback) {
                entity.setVelocity(entity.getVelocity().add(deltaX * damage * radius / 5, deltaY * damage * radius / 5, deltaZ * damage * radius / 5));
                if (entity instanceof PlayerEntity player) {
                    if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        this.getAffectedPlayers().put(player, new Vec3d(deltaX * damage * radius / 5, deltaY * damage * radius / 5, deltaZ * damage * radius / 5));
                    }
                }
            }
        }
    }
}
