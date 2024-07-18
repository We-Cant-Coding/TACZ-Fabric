package com.tacz.guns.client.particle;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoParticle;
import com.tacz.guns.entity.EntityKineticBullet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class AmmoParticleSpawner {
    public static void addParticle(EntityKineticBullet bullet, Identifier gunId) {
        TimelessAPI.getClientGunIndex(gunId).ifPresent(gunIndex -> {
            AmmoParticle gunParticle = gunIndex.getParticle();
            if (gunParticle == null) {
                // 如果枪械没有粒子效果，那么调用子弹的
                TimelessAPI.getClientAmmoIndex(bullet.getAmmoId()).ifPresent(ammoIndex -> {
                    AmmoParticle ammoParticle = ammoIndex.getParticle();
                    if (ammoParticle == null) {
                        return;
                    }
                    spawnParticle(bullet, ammoParticle);
                });
            } else {
                // 否则调用调用枪械的
                spawnParticle(bullet, gunParticle);
            }
        });
    }

    private static void spawnParticle(EntityKineticBullet bullet, AmmoParticle particle) {
        ParticleEffect particleOptions = particle.getParticleOptions();
        if (particleOptions == null) {
            return;
        }
        int count = particle.getCount();
        Vector3f delta = particle.getDelta();
        float particleSpeed = particle.getSpeed();
        ParticleManager particleEngine = MinecraftClient.getInstance().particleManager;
        if (count == 0) {
            double xSpeed = particleSpeed * delta.x();
            double ySpeed = particleSpeed * delta.y();
            double zSpeed = particleSpeed * delta.z();
            Particle result = particleEngine.addParticle(particleOptions, bullet.getX(), bullet.getY(), bullet.getZ(), xSpeed, ySpeed, zSpeed);
            if (result != null) {
                result.setMaxAge(particle.getLifeTime());
            }
        } else {
            Random random = bullet.getRandom();
            Entity owner = bullet.getOwner();
            for (int i = 0; i < count; ++i) {
                createParticle(bullet, particle, random, delta, particleSpeed, owner, particleEngine, particleOptions);
            }
        }
    }

    private static void createParticle(EntityKineticBullet bullet, AmmoParticle particle, Random random, Vector3f delta, float particleSpeed, Entity owner, ParticleManager particleEngine, ParticleEffect particleOptions) {
        Vec3d deltaMovement = bullet.getVelocity();
        double deltaMovementRandom = random.nextDouble();
        double offsetX = random.nextGaussian() * delta.x() + deltaMovementRandom * deltaMovement.x;
        double offsetY = random.nextGaussian() * delta.y() + deltaMovementRandom * deltaMovement.y;
        double offsetZ = random.nextGaussian() * delta.z() + deltaMovementRandom * deltaMovement.z;
        double xSpeed = random.nextGaussian() * particleSpeed;
        double ySpeed = random.nextGaussian() * particleSpeed;
        double zSpeed = random.nextGaussian() * particleSpeed;

        double posX = bullet.getX() + offsetX;
        double posY = bullet.getY() + offsetY;
        double posZ = bullet.getZ() + offsetZ;

        // 如果太贴近发射者，不进行粒子生成
        if (owner == null || owner.squaredDistanceTo(posX, posY, posZ) > 3 * 3) {
            Particle result = particleEngine.addParticle(particleOptions, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
            if (result != null) {
                result.setMaxAge(particle.getLifeTime());
            }
        }
    }
}
