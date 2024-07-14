package com.tacz.guns.api.item.attachment;

import com.google.gson.annotations.SerializedName;

public enum AttachmentType {
    /**
     * sight
     */
    @SerializedName("scope")
    SCOPE,
    /**
     * Muzzle Assembly
     */
    @SerializedName("muzzle")
    MUZZLE,
    /**
     * butt of a gun
     */
    @SerializedName("stock")
    STOCK,
    /**
     * grip
     */
    @SerializedName("grip")
    GRIP,
    /**
     * laser pointer
     */
    @SerializedName("laser")
    LASER,
    /**
     * Expanded magazine (box)
     */
    @SerializedName("extended_mag")
    EXTENDED_MAG,
    /**
     * Used to indicate when an item is not an accessory.
     */
    NONE
}
