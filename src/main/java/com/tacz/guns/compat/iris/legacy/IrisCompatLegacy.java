package com.tacz.guns.compat.iris.legacy;

import com.tacz.guns.compat.iris.legacy.pbr.PBRRegister;
import net.coderbot.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.coderbot.iris.shadows.ShadowRenderingState;
import net.minecraft.client.render.VertexConsumerProvider;

public class IrisCompatLegacy {
    public static boolean isRenderShadow() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
    }

    public static boolean endBatch(VertexConsumerProvider.Immediate bufferSource) {
        if (bufferSource instanceof FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource) {
            fullyBufferedMultiBufferSource.draw();
            return true;
        }
        return false;
    }

    public static void registerPBRLoader() {
        PBRRegister.registerPBRLoader();
    }
}
