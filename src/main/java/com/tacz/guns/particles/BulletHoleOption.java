package com.tacz.guns.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.init.ModParticles;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BulletHoleOption implements ParticleEffect {
    public static final Codec<BulletHoleOption> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(Codec.INT.fieldOf("dir").forGetter(option -> option.direction.ordinal()),
                    Codec.LONG.fieldOf("pos").forGetter(option -> option.pos.asLong()),
                    Codec.STRING.fieldOf("ammo_id").forGetter(option -> option.ammoId),
                    Codec.STRING.fieldOf("gun_id").forGetter(option -> option.gunId)
            ).apply(builder, BulletHoleOption::new));

    @SuppressWarnings("deprecation")
    public static final ParticleEffect.Factory<BulletHoleOption> DESERIALIZER = new ParticleEffect.Factory<>() {
        @Override
        public BulletHoleOption read(ParticleType<BulletHoleOption> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int dir = reader.readInt();
            reader.expect(' ');
            long pos = reader.readLong();
            reader.expect(' ');
            String ammoId = reader.readString();
            reader.expect(' ');
            String gunId = reader.readString();
            return new BulletHoleOption(dir, pos, ammoId, gunId);
        }

        @Override
        public BulletHoleOption read(ParticleType<BulletHoleOption> particleType, PacketByteBuf buffer) {
            return new BulletHoleOption(buffer.readVarInt(), buffer.readLong(), buffer.readString(), buffer.readString());
        }
    };

    private final Direction direction;
    private final BlockPos pos;
    private final String ammoId;
    private final String gunId;

    public BulletHoleOption(int dir, long pos, String ammoId, String gunId) {
        this.direction = Direction.values()[dir];
        this.pos = BlockPos.fromLong(pos);
        this.ammoId = ammoId;
        this.gunId = gunId;
    }

    public BulletHoleOption(Direction dir, BlockPos pos, String ammoId, String gunId) {
        this.direction = dir;
        this.pos = pos;
        this.ammoId = ammoId;
        this.gunId = gunId;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String getAmmoId() {
        return ammoId;
    }

    public String getGunId() {
        return gunId;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.BULLET_HOLE;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeEnumConstant(this.direction);
        buffer.writeBlockPos(this.pos);
        buffer.writeString(this.ammoId);
        buffer.writeString(this.gunId);
    }

    @Override
    public String asString() {
        return Registries.PARTICLE_TYPE.getKey(this.getType()) + " " + this.direction.getName();
    }
}
