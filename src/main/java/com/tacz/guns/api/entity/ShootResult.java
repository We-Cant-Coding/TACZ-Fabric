package com.tacz.guns.api.entity;

public enum ShootResult {
    /**
     * successes
     */
    SUCCESS,
    /**
     * Failure for unknown reasons
     */
    UNKNOWN_FAIL,
    /**
     * Shooting cooldown hasn't expired yet.
     */
    COOL_DOWN,
    /**
     * unmunitioned
     */
    NO_AMMO,
    /**
     * No gun-cutting logic implemented
     */
    NOT_DRAW,
    /**
     * The current item is not a gun.
     */
    NOT_GUN,
    /**
     * Firearm ID does not exist.
     */
    ID_NOT_EXIST,
    /**
     * Requires manual loading
     */
    NEED_BOLT,
    /**
     * It's in reload mode.
     */
    IS_RELOADING,
    /**
     * It's in the cut position.
     */
    IS_DRAWING,
    /**
     * It's pulling the tether.
     */
    IS_BOLTING,
    /**
     * It's in melee mode.
     */
    IS_MELEE,
    /**
     * He's on the run.
     */
    IS_SPRINTING,
    /**
     * Forge Event Cause Cancellation
     */
    FORGE_EVENT_CANCEL
}
