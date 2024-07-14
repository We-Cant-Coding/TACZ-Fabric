package com.tacz.guns.api.item.gun;

import com.google.gson.annotations.SerializedName;

public enum FireMode {
    /**
     * fully automatic
     */
    @SerializedName("auto")
    AUTO,
    /**
     * speedy
     */
    @SerializedName("semi")
    SEMI,
    /**
     * volley
     */
    @SerializedName("burst")
    BURST,
    /**
     * Unknown other circumstances?
     */
    @SerializedName("unknown")
    UNKNOWN
}
