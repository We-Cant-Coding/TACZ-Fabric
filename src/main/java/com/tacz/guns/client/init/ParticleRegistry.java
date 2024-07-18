package com.tacz.guns.client.init;

import com.tacz.guns.client.particle.BulletHoleParticle;
import com.tacz.guns.init.ModParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ParticleRegistry {
    public static void registerParticleFactory() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.BULLET_HOLE, new BulletHoleParticle.Provider());
    }
}
