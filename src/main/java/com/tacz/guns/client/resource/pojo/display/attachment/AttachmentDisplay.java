package com.tacz.guns.client.resource.pojo.display.attachment;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AttachmentDisplay {
    @SerializedName("slot")
    private Identifier slotTextureLocation;

    @SerializedName("model")
    private Identifier model;

    @SerializedName("texture")
    private Identifier texture;

    @SerializedName("lod")
    @Nullable
    private AttachmentLod attachmentLod;

    @SerializedName("adapter")
    @Nullable
    private String adapterNodeName;

    @SerializedName("show_muzzle")
    private boolean showMuzzle = false;

    @SerializedName("zoom")
    @Nullable
    private float[] zoom;

    @SerializedName("scope")
    private boolean isScope = false;

    @SerializedName("sight")
    private boolean isSight = false;

    @SerializedName("fov")
    private float fov = 70;

    @SerializedName("sounds")
    private Map<String, Identifier> sounds = Maps.newHashMap();

    public Identifier getSlotTextureLocation() {
        return slotTextureLocation;
    }

    public Identifier getModel() {
        return model;
    }

    public Identifier getTexture() {
        return texture;
    }

    @Nullable
    public AttachmentLod getAttachmentLod() {
        return attachmentLod;
    }

    @Nullable
    public String getAdapterNodeName() {
        return adapterNodeName;
    }

    public boolean isShowMuzzle() {
        return showMuzzle;
    }

    @Nullable
    public float[] getZoom() {
        return zoom;
    }

    public boolean isScope() {
        return isScope;
    }

    public boolean isSight() {
        return isSight;
    }

    public float getFov() {
        return fov;
    }

    public Map<String, Identifier> getSounds() {
        return sounds;
    }
}
