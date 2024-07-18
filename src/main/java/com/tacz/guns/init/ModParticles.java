package com.tacz.guns.init;

import com.mojang.serialization.Codec;
import com.tacz.guns.GunMod;
import com.tacz.guns.particles.BulletHoleOption;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final ParticleType<BulletHoleOption> BULLET_HOLE = register("bullet_hole", new ModParticleType<>(false, BulletHoleOption.DESERIALIZER, BulletHoleOption.CODEC));


    private static <T extends ParticleEffect> ParticleType<T> register(String path, ParticleType<T> type) {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(GunMod.MOD_ID, path), type);
    }

    public static void init() {
    }


    @SuppressWarnings("deprecation")
    private static class ModParticleType<T extends ParticleEffect> extends ParticleType<T> {
        private final Codec<T> codec;

        public ModParticleType(boolean overrideLimier, ParticleEffect.Factory<T> deserializer, Codec<T> codec) {
            super(overrideLimier, deserializer);
            this.codec = codec;
        }

        @Override
        public Codec<T> getCodec() {
            return codec;
        }
    }
}
