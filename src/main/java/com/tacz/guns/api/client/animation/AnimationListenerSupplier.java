package com.tacz.guns.api.client.animation;

import org.jetbrains.annotations.Nullable;

public interface AnimationListenerSupplier {
    @Nullable
    AnimationListener supplyListeners(String nodeName, ObjectAnimationChannel.ChannelType type);
}
