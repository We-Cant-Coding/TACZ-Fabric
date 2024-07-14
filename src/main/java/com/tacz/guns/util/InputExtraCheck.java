package com.tacz.guns.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class InputExtraCheck {
    public static boolean isInGame() {
        MinecraftClient mc = MinecraftClient.getInstance();
        // 不能是加载界面
        if (mc.getOverlay() != null) {
            return false;
        }
        // 不能打开任何 GUI
        if (mc.currentScreen != null) {
            return false;
        }
        // 当前窗口捕获鼠标操作
        if (!mc.mouse.isCursorLocked()) {
            return false;
        }
        // 选择了当前窗口
        return mc.isWindowFocused();
    }
}
