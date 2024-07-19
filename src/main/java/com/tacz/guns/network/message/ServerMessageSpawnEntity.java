package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.mixin.IEntityAdditionalSpawnData;
import com.tacz.guns.util.EnvironmentUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class ServerMessageSpawnEntity implements FabricPacket {
    public static final PacketType<ServerMessageSpawnEntity> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_spawn_entity"), ServerMessageSpawnEntity::new);

    private final Entity entity;
    private final EntityType<?> entityType;
    private final int entityId;
    private final UUID uuid;
    private final double posX;
    private final double posY;
    private final double posZ;
    private final byte pitch;
    private final byte yaw;
    private final byte headYaw;
    private final int velX;
    private final int velY;
    private final int velZ;
    private final PacketByteBuf buf;

    public ServerMessageSpawnEntity(PacketByteBuf buf) {
        this(buf.readRegistryValue(Registries.ENTITY_TYPE), buf.readInt(), new UUID(buf.readLong(), buf.readLong()), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readByte(), buf.readByte(), buf.readByte(), buf.readShort(), buf.readShort(), buf.readShort(), readSpawnDataPacket(buf));
    }

    public ServerMessageSpawnEntity(Entity e) {
        this.entity = e;
        this.entityType = e.getType();
        this.entityId = e.getId();
        this.uuid = e.getUuid();
        this.posX = e.getX();
        this.posY = e.getY();
        this.posZ = e.getZ();
        this.pitch = (byte) MathHelper.floor(e.getPitch() * 256.0F / 360.0F);
        this.yaw = (byte)MathHelper.floor(e.getYaw() * 256.0F / 360.0F);
        this.headYaw = (byte)((int)(e.getHeadYaw() * 256.0F / 360.0F));
        Vec3d vec3d = e.getVelocity();
        double d1 = MathHelper.clamp(vec3d.x, -3.9D, 3.9D);
        double d2 = MathHelper.clamp(vec3d.y, -3.9D, 3.9D);
        double d3 = MathHelper.clamp(vec3d.z, -3.9D, 3.9D);
        this.velX = (int)(d1 * 8000.0D);
        this.velY = (int)(d2 * 8000.0D);
        this.velZ = (int)(d3 * 8000.0D);
        this.buf = null;
    }

    private ServerMessageSpawnEntity(EntityType<?> entityType, int entityId, UUID uuid, double posX, double posY, double posZ, byte pitch, byte yaw, byte headYaw, int velX, int velY, int velZ, PacketByteBuf buf) {
        this.entity = null;
        this.entityType = entityType;
        this.entityId = entityId;
        this.uuid = uuid;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.headYaw = headYaw;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
        this.buf = buf;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryValue(Registries.ENTITY_TYPE, entityType);
        buf.writeInt(entityId);
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeByte(pitch);
        buf.writeByte(yaw);
        buf.writeByte(headYaw);
        buf.writeShort(velX);
        buf.writeShort(velY);
        buf.writeShort(velZ);
        if (entity instanceof IEntityAdditionalSpawnData entityAdditionalSpawnData) {
            PacketByteBuf spawnDataBuffer = new PacketByteBuf(Unpooled.buffer());
            entityAdditionalSpawnData.writeSpawnData(spawnDataBuffer);
            buf.writeVarInt(spawnDataBuffer.readableBytes());
            buf.writeBytes(spawnDataBuffer);
            spawnDataBuffer.release();
        } else {
            buf.writeVarInt(0);
        }

    }

    private static PacketByteBuf readSpawnDataPacket(PacketByteBuf buf) {
        int count = buf.readVarInt();
        if (count > 0) {
            PacketByteBuf spawnDataBuffer = new PacketByteBuf(Unpooled.buffer());
            spawnDataBuffer.writeBytes(buf, count);
            return spawnDataBuffer;
        } else {
            return new PacketByteBuf(Unpooled.buffer());
        }
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            spawnEntity(this);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void spawnEntity(ServerMessageSpawnEntity msg) {
        try {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            ClientWorld world = (ClientWorld) player.getWorld();
            Entity e = msg.entityType.create(world);
            if (e == null) {
                return;
            }

            e.updateTrackedPosition(msg.posX, msg.posY, msg.posZ);
            e.refreshPositionAfterTeleport(msg.posX, msg.posY, msg.posZ);
            e.setPitch((float) (msg.pitch * 360) / 256.0F);
            e.setYaw((float) (msg.yaw * 360) / 256.0F);
            e.setHeadYaw((float) (msg.headYaw * 360) / 256.0F);
            e.setBodyYaw((float) (msg.headYaw * 360) / 256.0F);
            e.setId(msg.entityId);
            e.setUuid(msg.uuid);
            world.addEntity(msg.entityId, e);
            e.setVelocityClient((double) msg.velX / 8000.0D, (double) msg.velY / 8000.0D, (double) msg.velZ / 8000.0D);
            if (e instanceof IEntityAdditionalSpawnData entityAdditionalSpawnData) {
                entityAdditionalSpawnData.readSpawnData(msg.buf);
            }
        } finally {
            msg.buf.release();
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getHeadYaw() {
        return this.headYaw;
    }

    public int getVelX() {
        return this.velX;
    }

    public int getVelY() {
        return this.velY;
    }

    public int getVelZ() {
        return this.velZ;
    }

    public PacketByteBuf getAdditionalData() {
        return this.buf;
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
