package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public enum Bolt {
    /**
     * be ready to shoot
     */
    @SerializedName("open_bolt")
    OPEN_BOLT,
    /**
     * be ready to strike with a closed mouth
     */
    @SerializedName("closed_bolt")
    CLOSED_BOLT,
    /**
     * manual loading
     */
    @SerializedName("manual_action")
    MANUAL_ACTION
}
