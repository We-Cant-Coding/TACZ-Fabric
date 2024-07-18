package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent GUN = register("gun", "gun");
    public static final SoundEvent TARGET_HIT = register("target_block_hit", "target_block_hit");

    private static SoundEvent register(String path, String sound_path) {
        return Registry.register(Registries.SOUND_EVENT, new Identifier(GunMod.MOD_ID, path), SoundEvent.of(new Identifier(GunMod.MOD_ID, sound_path)));
    }

    public static void init() {
    }
}
