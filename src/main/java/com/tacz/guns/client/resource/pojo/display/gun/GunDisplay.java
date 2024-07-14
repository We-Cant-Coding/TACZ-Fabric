package com.tacz.guns.client.resource.pojo.display.gun;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GunDisplay {
    @SerializedName("model")
    private Identifier modelLocation;
    @SerializedName("texture")
    private Identifier modelTexture;
    @SerializedName("iron_zoom")
    private float ironZoom = 1.2f;
    @Nullable
    @SerializedName("lod")
    private GunLod gunLod;
    @Nullable
    @SerializedName("hud")
    private Identifier hudTextureLocation;
    @Nullable
    @SerializedName("hud_empty")
    private Identifier hudEmptyTextureLocation;
    @Nullable
    @SerializedName("slot")
    private Identifier slotTextureLocation;
    @Nullable
    @SerializedName("third_person_animation")
    private String thirdPersonAnimation;
    @Nullable
    @SerializedName("animation")
    private Identifier animationLocation;
    @Nullable
    @SerializedName("use_default_animation")
    private DefaultAnimation defaultAnimation;
    @Nullable
    @SerializedName("player_animator_3rd")
    private Identifier playerAnimator3rd;
    @Nullable
    @SerializedName("sounds")
    private Map<String, Identifier> sounds;
    @Nullable
    @SerializedName("transform")
    private GunTransform transform;
    @Nullable
    @SerializedName("shell")
    private ShellEjection shellEjection;
    @Nullable
    @SerializedName("ammo")
    private GunAmmo gunAmmo;
    @Nullable
    @SerializedName("muzzle_flash")
    private MuzzleFlash muzzleFlash;
    @SerializedName("offhand_show")
    private LayerGunShow offhandShow = new LayerGunShow();
    @Nullable
    @SerializedName("hotbar_show")
    private Map<String, LayerGunShow> hotbarShow = null;
    @SerializedName("text_show")
    private Map<String, TextShow> textShows = Maps.newHashMap();
    @SerializedName("show_crosshair")
    private boolean showCrosshair = false;

    public Identifier getModelLocation() {
        return modelLocation;
    }

    public Identifier getModelTexture() {
        return modelTexture;
    }

    @Nullable
    public GunLod getGunLod() {
        return gunLod;
    }

    @Nullable
    public Identifier getHudTextureLocation() {
        return hudTextureLocation;
    }

    @Nullable
    public Identifier getHudEmptyTextureLocation() {
        return hudEmptyTextureLocation;
    }

    @Nullable
    public Identifier getSlotTextureLocation() {
        return slotTextureLocation;
    }

    @Nullable
    public Identifier getAnimationLocation() {
        return animationLocation;
    }

    @Nullable
    public DefaultAnimation getDefaultAnimation() {
        return defaultAnimation;
    }

    @Nullable
    public Identifier getPlayerAnimator3rd() {
        return playerAnimator3rd;
    }

    @Nullable
    public String getThirdPersonAnimation() {
        return thirdPersonAnimation;
    }

    @Nullable
    public Map<String, Identifier> getSounds() {
        return sounds;
    }

    @Nullable
    public GunTransform getTransform() {
        return transform;
    }

    @Nullable
    public ShellEjection getShellEjection() {
        return shellEjection;
    }

    @Nullable
    public GunAmmo getGunAmmo() {
        return gunAmmo;
    }

    @Nullable
    public MuzzleFlash getMuzzleFlash() {
        return muzzleFlash;
    }

    public LayerGunShow getOffhandShow() {
        return offhandShow;
    }

    @Nullable
    public Map<String, LayerGunShow> getHotbarShow() {
        return hotbarShow;
    }

    public float getIronZoom() {
        return ironZoom;
    }

    public Map<String, TextShow> getTextShows() {
        return textShows;
    }

    public boolean isShowCrosshair() {
        return showCrosshair;
    }
}
