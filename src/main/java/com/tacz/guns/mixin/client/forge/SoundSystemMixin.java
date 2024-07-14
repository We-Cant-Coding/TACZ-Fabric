package com.tacz.guns.mixin.client.forge;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.event.PlayGunSoundEvent;
import com.tacz.guns.forge.PlaySoundSourceEvent;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.StaticSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @Redirect(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenAccept(Ljava/util/function/Consumer;)Ljava/util/concurrent/CompletableFuture;", ordinal = 0))
    private CompletableFuture<Void> method(CompletableFuture<StaticSound> instance, Consumer<? super StaticSound> action, SoundInstance sound, @Local Channel.SourceManager sourceManager) {
        return instance.thenAccept((soundx) -> sourceManager.run((source) -> {
            source.setBuffer(soundx);
            source.play();
            var event = new PlaySoundSourceEvent(sound, source);
            PlayGunSoundEvent.onPlaySoundSource(event);
        }));
    }
}
