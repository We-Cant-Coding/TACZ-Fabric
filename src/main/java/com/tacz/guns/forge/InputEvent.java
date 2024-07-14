package com.tacz.guns.forge;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Hand;

public abstract class InputEvent extends CancelableEvent {
    public static class InteractionKeyMappingTriggered extends InputEvent {
        private final int button;
        private final KeyBinding keyMapping;
        private final Hand hand;
        private boolean handSwing = true;

        public InteractionKeyMappingTriggered(int button, KeyBinding keyMapping, Hand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        public void setSwingHand(boolean value) {
            this.handSwing = value;
        }

        public boolean shouldSwingHand() {
            return this.handSwing;
        }

        public Hand getHand() {
            return this.hand;
        }

        public boolean isAttack() {
            return this.button == 0;
        }

        public boolean isUseItem() {
            return this.button == 1;
        }

        public boolean isPickBlock() {
            return this.button == 2;
        }

        public KeyBinding getKeyMapping() {
            return this.keyMapping;
        }
    }
}
