package com.tacz.guns.compat.iris;

import com.tacz.guns.compat.iris.pbr.PBRRegister;
import com.tacz.guns.init.CompatRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.render.VertexConsumerProvider;

public final class IrisCompat {
    public static void initCompat() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            PBRRegister.registerPBRLoader();
        }
    }

    public static boolean isRenderShadow() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
        }
        return false;
    }

    public static boolean isUsingRenderPack() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            return IrisApi.getInstance().isShaderPackInUse();
        }
        return false;
    }

    public static boolean endBatch(VertexConsumerProvider.Immediate bufferSource) {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            if (bufferSource instanceof FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource) {
                fullyBufferedMultiBufferSource.draw();
                return false;
            }
        }
        return true;
    }
}
