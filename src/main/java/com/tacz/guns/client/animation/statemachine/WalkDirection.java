package com.tacz.guns.client.animation.statemachine;

import net.minecraft.client.input.Input;

public enum WalkDirection {
    FORWARD,
    SIDE_WAY,
    BACKWARD,
    NONE;

    public static WalkDirection fromInput(Input input) {
        if (input.pressingForward) {
            return FORWARD;
        }
        if (input.pressingBack) {
            return BACKWARD;
        }
        if (input.pressingLeft || input.pressingRight) {
            return SIDE_WAY;
        }
        return NONE;
    }
}
