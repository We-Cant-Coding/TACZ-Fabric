package com.tacz.guns.api.client.animation;

import java.util.Arrays;

public class AnimationChannelContent {
    public float[] keyframeTimeS;
    /**
     * Animation values. Each element in the first dimension of this array corresponds to the order of keyframeTime above.
     * In the second dimension, it can be values for translation, rotation, or scaling. If it's a quaternion, the array length can be 8 or 4.
     * (When the length is 8, the first four positions store Pre values, and the last four positions store Post values.)
     * If it's values for three axes, the array length can be 6 or 3, following the same Pre and Post logic.
     */
    public float[][] values;
    /**
     * For Channels using a general interpolator, this animation value is meaningless. It is specifically used for CustomInterpolator.
     */
    public LerpMode[] lerpModes;

    public AnimationChannelContent() {
    }

    public AnimationChannelContent(AnimationChannelContent source) {
        if (source.keyframeTimeS != null) {
            this.keyframeTimeS = Arrays.copyOf(source.keyframeTimeS, source.keyframeTimeS.length);
        }
        if (source.values != null) {
            // 深拷贝动画数值
            this.values = Arrays.stream(source.values)
                    .map(values -> Arrays.copyOf(values, values.length))
                    .toArray(float[][]::new);
        }
        if (source.lerpModes != null) {
            this.lerpModes = Arrays.copyOf(source.lerpModes, source.lerpModes.length);
        }
    }

    public enum LerpMode {
        LINEAR, SPHERICAL_LINEAR, CATMULLROM, SPHERICAL_SQUAD
    }
}
