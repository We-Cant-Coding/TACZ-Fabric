package com.tacz.guns.client.particle;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.particles.BulletHoleOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class BulletHoleParticle extends SpriteBillboardParticle {
    private final Direction direction;
    private final BlockPos pos;
    private int uOffset;
    private int vOffset;
    private float textureDensity;

    public BulletHoleParticle(ClientWorld world, double x, double y, double z, Direction direction, BlockPos pos, String ammoId, String gunId) {
        super(world, x, y, z);
        this.setSprite(this.getSprite(pos));
        this.direction = direction;
        this.pos = pos;
        this.maxAge = this.getLifetimeFromConfig(world);
        this.collidesWithWorld = false;
        this.gravityStrength = 0.0F;
        this.scale = 0.05F;

        // 如果方块是空气，则立即移除粒子
        BlockState state = world.getBlockState(pos);
        if (world.getBlockState(pos).isAir() || state.isOf(ModBlocks.TARGET)) {
            this.markDead();
        }
        TimelessAPI.getClientGunIndex(new Identifier(gunId)).ifPresent(gunIndex -> {
            float[] gunTracerColor = gunIndex.getTracerColor();
            if (gunTracerColor != null) {
                this.red = gunTracerColor[0];
                this.green = gunTracerColor[1];
                this.blue = gunTracerColor[2];
            } else {
                TimelessAPI.getClientAmmoIndex(new Identifier(ammoId)).ifPresent(ammoIndex -> {
                    float[] ammoTracerColor = ammoIndex.getTracerColor();
                    this.red = ammoTracerColor[0];
                    this.green = ammoTracerColor[1];
                    this.blue = ammoTracerColor[2];
                });
            }
        });
        this.alpha = 0.9F;
    }

    private int getLifetimeFromConfig(ClientWorld world) {
        int configLife = RenderConfig.BULLET_HOLE_PARTICLE_LIFE.get();
        if (configLife <= 1) {
            return configLife;
        }
        return configLife + world.random.nextInt(configLife / 2);
    }

    @Override
    protected void setSprite(Sprite sprite) {
        super.setSprite(sprite);
        this.uOffset = this.random.nextInt(16);
        this.vOffset = this.random.nextInt(16);
        // 材质应该都是方形
        this.textureDensity = (sprite.getMaxU() - sprite.getMinU()) / 16.0F;
    }

    private Sprite getSprite(BlockPos pos) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        World world = minecraft.world;
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            return MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state);
//            return MinecraftClient.getInstance().getBlockRenderManager().getModels().getTexture(state, world, pos);
        }
        return MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(MissingSprite.getMissingSpriteId());
    }

    @Override
    protected float getMinU() {
        return this.sprite.getMinU() + this.uOffset * this.textureDensity;
    }

    @Override
    protected float getMinV() {
        return this.sprite.getMinV() + this.vOffset * this.textureDensity;
    }

    @Override
    protected float getMaxU() {
        return this.getMinU() + this.textureDensity;
    }

    @Override
    protected float getMaxV() {
        return this.getMinV() + this.textureDensity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.getBlockState(this.pos).isAir()) {
            this.markDead();
        }
    }

    @Override
    public void buildGeometry(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3d view = renderInfo.getPos();
        float particleX = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.x) - view.getX());
        float particleY = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.y) - view.getY());
        float particleZ = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.z) - view.getZ());
        Quaternionf quaternion = this.direction.getRotationQuaternion();
        Vector3f[] points = new Vector3f[]{
                // Y 值稍微大一点点，防止 z-fight
                new Vector3f(-1.0F, 0.01F, -1.0F),
                new Vector3f(-1.0F, 0.01F, 1.0F),
                new Vector3f(1.0F, 0.01F, 1.0F),
                new Vector3f(1.0F, 0.01F, -1.0F)
        };
        float scale = this.getSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = points[i];
            vector3f.rotate(quaternion);
            vector3f.mul(scale);
            vector3f.add(particleX, particleY, particleZ);
        }

        // UV 坐标
        float u0 = this.getMinU();
        float u1 = this.getMaxU();
        float v0 = this.getMinV();
        float v1 = this.getMaxV();

        // 0 - 30 tick 内，从 15 亮度到 0 亮度
        int light = Math.max(15 - this.age / 2, 0);
        int lightColor = LightmapTextureManager.pack(light, light);

        // 颜色，逐渐渐变到 0 0 0，也就是黑色
        float colorPercent = light / 15.0f;
        float red = this.red * colorPercent;
        float green = this.green * colorPercent;
        float blue = this.blue * colorPercent;

        // 透明度，逐渐变成 0，也就是透明
        double threshold = RenderConfig.BULLET_HOLE_PARTICLE_FADE_THRESHOLD.get() * this.maxAge;
        float fade = 1.0f - (float) (Math.max(this.age - threshold, 0) / (this.maxAge - threshold));
        float alphaFade = this.alpha * fade;

        buffer.vertex(points[0].x(), points[0].y(), points[0].z()).texture(u1, v1).color(red, green, blue, alphaFade).light(lightColor).next();
        buffer.vertex(points[1].x(), points[1].y(), points[1].z()).texture(u1, v0).color(red, green, blue, alphaFade).light(lightColor).next();
        buffer.vertex(points[2].x(), points[2].y(), points[2].z()).texture(u0, v0).color(red, green, blue, alphaFade).light(lightColor).next();
        buffer.vertex(points[3].x(), points[3].y(), points[3].z()).texture(u0, v1).color(red, green, blue, alphaFade).light(lightColor).next();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleFactory<BulletHoleOption> {
        public Provider() {
        }

        @Override
        public BulletHoleParticle createParticle(@NotNull BulletHoleOption option, @NotNull ClientWorld world, double x, double y, double z, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new BulletHoleParticle(world, x, y, z, option.getDirection(), option.getPos(), option.getAmmoId(), option.getGunId());
        }
    }
}