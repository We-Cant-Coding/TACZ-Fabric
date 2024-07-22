package com.tacz.guns.client.event;

import com.tacz.guns.client.sound.GunSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.StaticSound;

public class PlayGunSoundEvent {
    public static void onPlaySoundSource(SoundInstance sound, Source channel) {
        if (sound instanceof GunSoundInstance instance) {
            StaticSound soundBuffer = instance.getSoundBuffer();
            if (soundBuffer != null) {
                channel.setBuffer(soundBuffer);
                channel.play();
            }
        }
    }
}
