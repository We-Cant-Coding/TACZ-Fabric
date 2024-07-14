package com.tacz.guns.api.client.animation;

import com.tacz.guns.api.client.animation.interpolator.Interpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectAnimationChannel {
    public final ChannelType type;
    private final List<AnimationListener> listeners = new ArrayList<>();
    /**
     * Node name.
     */
    public String node;
    /**
     * The content of this track, including keyframes.
     */
    public AnimationChannelContent content;
    /**
     * Interpolator for this track.
     */
    public Interpolator interpolator;
    /**
     * This variable is used for animation transitions.
     * If you are unsure what you are doing, please do not modify it.
     */
    boolean transitioning = false;

    public ObjectAnimationChannel(ChannelType type) {
        this.type = type;
        this.content = new AnimationChannelContent();
    }

    public ObjectAnimationChannel(ChannelType type, AnimationChannelContent content) {
        this.type = type;
        this.content = content;
    }

    public void addListener(AnimationListener listener) {
        if (listener.getType().equals(type)) {
            listeners.add(listener);
        } else {
            throw new RuntimeException("trying to add wrong type of listener to channel.");
        }
    }

    public void removeListener(AnimationListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public List<AnimationListener> getListeners() {
        return listeners;
    }

    public float getEndTimeS() {
        return content.keyframeTimeS[content.keyframeTimeS.length - 1];
    }

    /**
     * Computes based on the input time and notifies all AnimationListeners with the result.
     *
     * @param timeS Absolute time (in seconds).
     * @param blend Indicates whether blending is enabled.
     */
    public void update(float timeS, boolean blend) {
        if (!transitioning) {
            float[] result = getResult(timeS);
            for (AnimationListener listener : listeners) {
                listener.update(result, blend);
            }
        }
    }

    public float[] getResult(float timeS) {
        int indexFrom = computeIndex(timeS);
        int indexTo = Math.min(content.keyframeTimeS.length - 1, indexFrom + 1);
        float alpha = computeAlpha(timeS, indexFrom);
        return interpolator.interpolate(indexFrom, indexTo, alpha);
    }

    private int computeIndex(float timeS) {
        int index = Arrays.binarySearch(content.keyframeTimeS, timeS);
        if (index >= 0) {
            return index;
        }
        return Math.max(0, -index - 2);
    }

    private float computeAlpha(float timeS, int indexFrom) {
        if (timeS <= content.keyframeTimeS[0]) {
            return 0.0f;
        }
        if (timeS >= content.keyframeTimeS[content.keyframeTimeS.length - 1]) {
            return 1.0f;
        }
        float local = timeS - content.keyframeTimeS[indexFrom];
        float delta = content.keyframeTimeS[indexFrom + 1] - content.keyframeTimeS[indexFrom];
        return local / delta;
    }

    public enum ChannelType {
        /**
         * Represents movement.
         */
        TRANSLATION,

        /**
         * Represents rotation.
         */
        ROTATION,

        /**
         * Represents scaling.
         */
        SCALE
    }

}
