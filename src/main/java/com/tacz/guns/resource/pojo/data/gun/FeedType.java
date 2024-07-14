package com.tacz.guns.resource.pojo.data.gun;

import com.google.gson.annotations.SerializedName;

public enum FeedType {
    /**
     * magazine feed
     */
    @SerializedName("magazine")
    MAGAZINE,
    /**
     * manual feed
     */
    @SerializedName("manual")
    MANUAL
}
