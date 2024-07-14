package com.tacz.guns.forge;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.Source;

public class PlaySoundSourceEvent {
    private final SoundInstance sound;
    private final Source channel;
    private final String name;

    public PlaySoundSourceEvent(SoundInstance sound, Source channel) {
        this.name = sound.getId().getPath();
        this.sound = sound;
        this.channel = channel;
    }

    public SoundInstance getSound() {
        return this.sound;
    }

    public Source getChannel() {
        return this.channel;
    }

    public String getName() {
        return this.name;
    }
}
