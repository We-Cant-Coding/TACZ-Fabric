package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerMessageSound implements FabricPacket {
    public static final PacketType<ServerMessageSound> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_sound"), ServerMessageSound::new);

    private final int entityId;
    private final Identifier gunId;
    private final String soundName;
    private final float volume;
    private final float pitch;
    private final int distance;

    public ServerMessageSound(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readIdentifier(), buf.readString(), buf.readFloat(), buf.readFloat(), buf.readInt());
    }

    public ServerMessageSound(int entityId, Identifier gunId, String soundName, float volume, float pitch, int distance) {
        this.entityId = entityId;
        this.gunId = gunId;
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        this.distance = distance;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeIdentifier(gunId);
        buf.writeString(soundName);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeInt(distance);
    }

    public void handle(ClientPlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            SoundPlayManager.playMessageSound(this);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public int getEntityId() {
        return entityId;
    }

    public Identifier getGunId() {
        return gunId;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getDistance() {
        return distance;
    }
}
