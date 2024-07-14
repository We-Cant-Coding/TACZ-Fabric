package com.tacz.guns.client.sound;

import com.tacz.guns.client.resource.ClientAssetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GunSoundInstance extends EntityTrackingSoundInstance {
    private final Identifier registryName;

    public GunSoundInstance(SoundEvent soundEvent, SoundCategory source, float volume, float pitch, Entity entity, int soundDistance, Identifier registryName) {
        super(soundEvent, source, volume, pitch, entity, 943);
        this.attenuationType = AttenuationType.NONE;
        this.registryName = registryName;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            this.volume = volume * (1.0F - Math.min(1.0F, (float) Math.sqrt(player.squaredDistanceTo(x, y, z)) / soundDistance));
            this.volume *= this.volume;
        }
    }

    public void setStop() {
        this.setDone();
    }

    @Nullable
    public StaticSound getSoundBuffer() {
        return ClientAssetManager.INSTANCE.getSoundBuffers(this.registryName);
    }

    public Identifier getRegistryName() {
        return registryName;
    }
}
