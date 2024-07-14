package com.tacz.guns.client.event;

import com.tacz.guns.client.sound.GunSoundInstance;
import com.tacz.guns.forge.PlaySoundSourceEvent;
import net.minecraft.client.sound.StaticSound;

public class PlayGunSoundEvent {
    public static void onPlaySoundSource(PlaySoundSourceEvent event) {
        if (event.getSound() instanceof GunSoundInstance instance) {
            StaticSound soundBuffer = instance.getSoundBuffer();
            if (soundBuffer != null) {
                event.getChannel().setBuffer(soundBuffer);
                event.getChannel().play();
            }
        }
    }
}
