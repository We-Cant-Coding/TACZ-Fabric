package com.tacz.guns.compat.playeranimator;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.compat.playeranimator.animation.AnimationDataRegisterFactory;
import com.tacz.guns.compat.playeranimator.animation.AnimationManager;
import com.tacz.guns.compat.playeranimator.animation.PlayerAnimatorAssetManager;
import com.tacz.guns.compat.playeranimator.animation.PlayerAnimatorLoader;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.zip.ZipFile;

public class PlayerAnimatorCompat {
    public static Identifier LOWER_ANIMATION = new Identifier(GunMod.MOD_ID, "lower_animation");
    public static Identifier LOOP_UPPER_ANIMATION = new Identifier(GunMod.MOD_ID, "loop_upper_animation");
    public static Identifier ONCE_UPPER_ANIMATION = new Identifier(GunMod.MOD_ID, "once_upper_animation");
    public static Identifier ROTATION_ANIMATION = new Identifier(GunMod.MOD_ID, "rotation");

    private static final String MOD_ID = "player-animator";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
        if (isInstalled()) {
            AnimationDataRegisterFactory.registerData();
            AnimationManager manager = new AnimationManager();
            GunShootEvent.EVENT.register(manager::onFire);
            GunReloadEvent.EVENT.register(manager::onReload);
            GunMeleeEvent.EVENT.register(manager::onMelee);
            GunDrawEvent.EVENT.register(manager::onDraw);
        }
    }

    public static boolean loadAnimationFromZip(ZipFile zipFile, String zipPath) {
        if (isInstalled()) {
            return PlayerAnimatorLoader.load(zipFile, zipPath);
        }
        return false;
    }

    public static void loadAnimationFromFile(File file) {
        if (isInstalled()) {
            PlayerAnimatorLoader.load(file);
        }
    }

    public static void clearAllAnimationCache() {
        if (isInstalled()) {
            PlayerAnimatorAssetManager.INSTANCE.clearAll();
        }
    }

    public static boolean hasPlayerAnimator3rd(LivingEntity livingEntity, ClientGunIndex gunIndex) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayerEntity) {
            return AnimationManager.hasPlayerAnimator3rd(gunIndex);
        }
        return false;
    }

    public static void stopAllAnimation(LivingEntity livingEntity) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayerEntity player) {
            AnimationManager.stopAllAnimation(player);
        }
    }

    public static void playAnimation(LivingEntity livingEntity, ClientGunIndex gunIndex, float limbSwingAmount) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayerEntity player) {
            AnimationManager.playLowerAnimation(player, gunIndex, limbSwingAmount);
            AnimationManager.playLoopUpperAnimation(player, gunIndex, limbSwingAmount);
            AnimationManager.playRotationAnimation(player, gunIndex);
        }
    }

    public static boolean isInstalled() {
        return INSTALLED;
    }
}
