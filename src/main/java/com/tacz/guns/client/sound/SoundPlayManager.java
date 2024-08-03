package com.tacz.guns.client.sound;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.config.common.GunConfig;
import com.tacz.guns.init.ModSounds;
import com.tacz.guns.network.packets.s2c.SoundS2CPacket;
import com.tacz.guns.sound.SoundManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class SoundPlayManager {
    /**
     * 用于阻止连发时，反复播放 DryFire 音效
     */
    private static boolean DRY_SOUND_TRACK = true;

    /**
     * 临时缓存，用于停止播放的
     */
    private static GunSoundInstance tmpSoundInstance = null;

    public static GunSoundInstance playClientSound(Entity entity, @Nullable Identifier name, float volume, float pitch, int distance) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        GunSoundInstance instance = new GunSoundInstance(ModSounds.GUN, SoundCategory.PLAYERS, volume, pitch, entity, distance, name);
        minecraft.getSoundManager().play(instance);
        return instance;
    }

    public static void stopPlayGunSound() {
        if (tmpSoundInstance != null) {
            tmpSoundInstance.setStop();
        }
    }

    public static void stopPlayGunSound(ClientGunIndex gunIndex, String animationName) {
        if (tmpSoundInstance != null) {
            if (tmpSoundInstance.getRegistryName() != null && tmpSoundInstance.getRegistryName().equals(gunIndex.getSounds(animationName))) {
                tmpSoundInstance.setStop();
            }
        }
    }

    public static void playerRefitSound(ItemStack attachmentItem, ClientPlayerEntity player, String soundName) {
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(attachmentItem);
        if (iAttachment == null) {
            return;
        }
        Identifier attachmentId = iAttachment.getAttachmentId(attachmentItem);
        TimelessAPI.getClientAttachmentIndex(attachmentId).ifPresent(index -> {
            Map<String, Identifier> sounds = index.getSounds();
            if (sounds.containsKey(soundName)) {
                Identifier identifier = sounds.get(soundName);
                SoundPlayManager.playClientSound(player, identifier, 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
            }
        });
    }

    public static void playShootSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.SHOOT_SOUND), 0.8f, 0.9f + entity.getRandom().nextFloat() * 0.125f, GunConfig.DEFAULT_GUN_FIRE_SOUND_DISTANCE.get());
    }

    public static void playSilenceSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.SILENCE_SOUND), 0.6f, 0.9f + entity.getRandom().nextFloat() * 0.125f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playDryFireSound(LivingEntity entity, ClientGunIndex gunIndex) {
        if (DRY_SOUND_TRACK) {
            playClientSound(entity, gunIndex.getSounds(SoundManager.DRY_FIRE_SOUND), 1.0f, 0.9f + entity.getRandom().nextFloat() * 0.125f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
            DRY_SOUND_TRACK = false;
        }
    }

    /**
     * 只有松开鼠标时，才会重置
     */
    public static void resetDryFireSound() {
        DRY_SOUND_TRACK = true;
    }

    public static void playReloadSound(LivingEntity entity, ClientGunIndex gunIndex, boolean noAmmo) {
        if (noAmmo) {
            tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.RELOAD_EMPTY_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
        } else {
            tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.RELOAD_TACTICAL_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
        }
    }

    public static void playInspectSound(LivingEntity entity, ClientGunIndex gunIndex, boolean noAmmo) {
        if (noAmmo) {
            tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.INSPECT_EMPTY_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
        } else {
            tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.INSPECT_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
        }
    }

    public static void playBoltSound(LivingEntity entity, ClientGunIndex gunIndex) {
        tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.BOLT_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playDrawSound(LivingEntity entity, ClientGunIndex gunIndex) {
        tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.DRAW_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playPutAwaySound(LivingEntity entity, ClientGunIndex gunIndex) {
        tmpSoundInstance = playClientSound(entity, gunIndex.getSounds(SoundManager.PUT_AWAY_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playFireSelectSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.FIRE_SELECT), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playMeleeBayonetSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.MELEE_BAYONET), 1.0f, 0.9f + entity.getRandom().nextFloat() * 0.125f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playMeleePushSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.MELEE_PUSH), 1.0f, 0.9f + entity.getRandom().nextFloat() * 0.125f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playMeleeStockSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.MELEE_STOCK), 1.0f, 0.9f + entity.getRandom().nextFloat() * 0.125f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playHeadHitSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.HEAD_HIT_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playFleshHitSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.FLESH_HIT_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playKillSound(LivingEntity entity, ClientGunIndex gunIndex) {
        playClientSound(entity, gunIndex.getSounds(SoundManager.KILL_SOUND), 1.0f, 1.0f, GunConfig.DEFAULT_GUN_OTHER_SOUND_DISTANCE.get());
    }

    public static void playMessageSound(SoundS2CPacket message) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null || !(world.getEntityById(message.getEntityId()) instanceof LivingEntity livingEntity)) {
            return;
        }
        Identifier gunId = message.getGunId();
        TimelessAPI.getClientGunIndex(gunId).ifPresent(index -> {
            Identifier soundId = index.getSounds(message.getSoundName());
            if (soundId == null) {
                return;
            }
            playClientSound(livingEntity, soundId, message.getVolume(), message.getPitch(), message.getDistance());
        });
    }
}