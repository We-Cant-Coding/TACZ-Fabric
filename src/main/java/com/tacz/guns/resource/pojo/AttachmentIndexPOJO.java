package com.tacz.guns.resource.pojo;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.item.attachment.AttachmentType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AttachmentIndexPOJO {
    @SerializedName("name")
    private String name;

    @SerializedName("tooltip")
    @Nullable
    private String tooltip;

    @SerializedName("display")
    private Identifier display;

    @SerializedName("data")
    private Identifier data;

    @SerializedName("type")
    private AttachmentType type;

    public String getName() {
        return name;
    }

    @Nullable
    public String getTooltip() {
        return tooltip;
    }

    public Identifier getDisplay() {
        return display;
    }

    public Identifier getData() {
        return data;
    }

    public AttachmentType getType() {
        return type;
    }
}
