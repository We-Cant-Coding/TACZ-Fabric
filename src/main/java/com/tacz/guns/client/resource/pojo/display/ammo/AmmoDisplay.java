package com.tacz.guns.client.resource.pojo.display.ammo;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AmmoDisplay {
    @SerializedName("model")
    private Identifier modelLocation;

    @SerializedName("texture")
    private Identifier modelTexture;

    @Nullable
    @SerializedName("slot")
    private Identifier slotTextureLocation;

    @Nullable
    @SerializedName("entity")
    private AmmoEntityDisplay ammoEntity;

    @Nullable
    @SerializedName("shell")
    private ShellDisplay shellDisplay;

    @Nullable
    @SerializedName("particle")
    private AmmoParticle particle;

    @SerializedName("tracer_color")
    private String tracerColor = "0xFFFFFF";

    @Nullable
    @SerializedName("transform")
    private AmmoTransform transform;

    public Identifier getModelLocation() {
        return modelLocation;
    }

    public Identifier getModelTexture() {
        return modelTexture;
    }

    @Nullable
    public Identifier getSlotTextureLocation() {
        return slotTextureLocation;
    }

    @Nullable
    public AmmoEntityDisplay getAmmoEntity() {
        return ammoEntity;
    }

    @Nullable
    public ShellDisplay getShellDisplay() {
        return shellDisplay;
    }

    @Nullable
    public AmmoParticle getParticle() {
        return particle;
    }

    public String getTracerColor() {
        return tracerColor;
    }

    @Nullable
    public AmmoTransform getTransform() {
        return transform;
    }
}
