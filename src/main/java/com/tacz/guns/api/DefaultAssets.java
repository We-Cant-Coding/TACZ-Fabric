package com.tacz.guns.api;

import com.tacz.guns.GunMod;
import net.minecraft.util.Identifier;

public final class DefaultAssets {
    public static Identifier EMPTY_GUN_ID = new Identifier(GunMod.MOD_ID, "empty");
    public static Identifier DEFAULT_GUN_DISPLAY = new Identifier(GunMod.MOD_ID, "ak47_display");

    public static Identifier DEFAULT_AMMO_ID = new Identifier(GunMod.MOD_ID, "762x39");
    public static Identifier DEFAULT_AMMO_DISPLAY = new Identifier(GunMod.MOD_ID, "762x39_display");
    public static Identifier EMPTY_AMMO_ID = new Identifier(GunMod.MOD_ID, "empty");

    public static Identifier DEFAULT_ATTACHMENT_ID = new Identifier(GunMod.MOD_ID, "sight_sro_dot");
    public static Identifier EMPTY_ATTACHMENT_ID = new Identifier(GunMod.MOD_ID, "empty");

    public static Identifier DEFAULT_ATTACHMENT_SKIN_ID = new Identifier(GunMod.MOD_ID, "sight_sro_dot_blue");
    public static Identifier EMPTY_ATTACHMENT_SKIN_ID = new Identifier(GunMod.MOD_ID, "empty");

    public static boolean isEmptyAttachmentId(Identifier attachmentId) {
        return EMPTY_ATTACHMENT_ID.equals(attachmentId);
    }
}
