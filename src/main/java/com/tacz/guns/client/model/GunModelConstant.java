package com.tacz.guns.client.model;

public final class GunModelConstant {
    /**
     * Bullet in the barrel, used for rendering firearms in closed bolt state. Hide this group when there is no bullet in the barrel.
     */
    public static final String BULLET_IN_BARREL = "bullet_in_barrel";

    /**
     * Bullets in the magazine, hide this group when the magazine is empty.
     */
    public static final String BULLET_IN_MAG = "bullet_in_mag";

    /**
     * Ammo belt, mostly used for machine guns, hide when the ammo runs out.
     */
    public static final String BULLET_CHAIN = "bullet_chain";

    /**
     * Visible when no sights are attached, typically used on M4.
     */
    public static final String CARRY = "carry";

    /**
     * Displayed when a Level 1 extended magazine is installed.
     */
    public static final String MAG_EXTENDED_1 = "mag_extended_1";

    /**
     * Displayed when a Level 2 extended magazine is installed.
     */
    public static final String MAG_EXTENDED_2 = "mag_extended_2";

    /**
     * Displayed when a Level 3 extended magazine is installed.
     */
    public static final String MAG_EXTENDED_3 = "mag_extended_3";

    /**
     * Displayed when no extended magazine is installed.
     */
    public static final String MAG_STANDARD = "mag_standard";

    /**
     * Visible when sights are attached, used for mounting sights (e.g., AKM rails).
     */
    public static final String MOUNT = "mount";

    /**
     * Visible when no sights are attached, mechanical sights.
     */
    public static final String SIGHT = "sight";

    /**
     * Displayed when sights are attached, folded mechanical sights.
     */
    public static final String SIGHT_FOLDED = "sight_folded";

    /**
     * Can be understood as: When the player aims using mechanical sights on the firearm, the position and orientation of the player's eye.
     */
    public static final String IRON_VIEW_NODE = "iron_view";

    /**
     * Position and orientation of the player's eye when not aiming.
     */
    public static final String IDLE_VIEW_NODE = "idle_view";

    /**
     * Default refit interface positioning group.
     */
    public static final String REFIT_VIEW_NODE = "refit_view";

    /**
     * Third-person firearm positioning group.
     */
    public static final String THIRD_PERSON_HAND_ORIGIN_NODE = "thirdperson_hand";

    /**
     * Display frame positioning group.
     */
    public static final String FIXED_ORIGIN_NODE = "fixed";

    /**
     * Ground item positioning group.
     */
    public static final String GROUND_ORIGIN_NODE = "ground";

    /**
     * Ejection port positioning group.
     */
    public static final String SHELL_ORIGIN_NODE = "shell";

    /**
     * Muzzle flash positioning group.
     */
    public static final String MUZZLE_FLASH_ORIGIN_NODE = "muzzle_flash";

    /**
     * First-person left hand arm group.
     */
    public static final String LEFTHAND_POS_NODE = "lefthand_pos";

    /**
     * First-person right hand arm group.
     */
    public static final String RIGHTHAND_POS_NODE = "righthand_pos";

    /**
     * Magazine positioning group.
     */
    public static final String MAG_NORMAL_NODE = "magazine";

    /**
     * Second magazine positioning group during reload.
     */
    public static final String MAG_ADDITIONAL_NODE = "additional_magazine";

    /**
     * Attachment adapter positioning group.
     */
    public static final String ATTACHMENT_ADAPTER_NODE = "attachment_adapter";

    /**
     * Default handguard.
     */
    public static final String HANDGUARD_DEFAULT_NODE = "handguard_default";

    /**
     * Tactical handguard.
     */
    public static final String HANDGUARD_TACTICAL_NODE = "handguard_tactical";

    /**
     * Attachment positioning group suffix, actual name is lowercase attachment name plus this suffix.
     */
    public static final String ATTACHMENT_POS_SUFFIX = "_pos";

    /**
     * Default attachment group suffix, hides after attaching an attachment, actual name is lowercase attachment name plus this suffix.
     */
    public static final String DEFAULT_ATTACHMENT_SUFFIX = "_default";

    /**
     * Refit interface view group prefix, actual name is prefix + lowercase attachment name + suffix.
     */
    public static final String REFIT_VIEW_PREFIX = "refit_";

    /**
     * Refit interface view group suffix, actual name is prefix + lowercase attachment name + suffix.
     */
    public static final String REFIT_VIEW_SUFFIX = "_view";

    /**
     * Root group.
     */
    public static final String ROOT_NODE = "root";
}
