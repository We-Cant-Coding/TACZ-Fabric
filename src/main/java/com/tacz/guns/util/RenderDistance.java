package com.tacz.guns.util;

import com.tacz.guns.config.client.RenderConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderDistance {
    private static long GUI_RENDER_TIMESTAMP = -1L;

    public static boolean inRenderHighPolyModelDistance(MatrixStack matrixStack) {
        if (isGuiRender()) {
            return true;
        }
        int distance = RenderConfig.GUN_LOD_RENDER_DISTANCE.get();
        if (distance <= 0) {
            return false;
        }
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        float viewDistance = matrix4f.m30() * matrix4f.m30() + matrix4f.m31() * matrix4f.m31() + matrix4f.m32() * matrix4f.m32();
        return viewDistance < distance * distance;
    }

    public static void markGuiRenderTimestamp() {
        GUI_RENDER_TIMESTAMP = System.currentTimeMillis();
    }

    private static boolean isGuiRender() {
        return System.currentTimeMillis() - GUI_RENDER_TIMESTAMP < 100;
    }
}
