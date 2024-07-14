package com.tacz.guns.client.resource.pojo.display.ammo;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

public class AmmoEntityDisplay {
    @SerializedName("model")
    private Identifier modelLocation;
    @SerializedName("texture")
    private Identifier modelTexture;

    public Identifier getModelLocation() {
        return modelLocation;
    }

    public Identifier getModelTexture() {
        return modelTexture;
    }
}
