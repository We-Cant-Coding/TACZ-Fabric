package com.tacz.guns.client.input;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.api.client.event.InputEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class RefitKey {
    public static final KeyBinding REFIT_KEY = new KeyBinding("key.tacz-fabric.refit.desc",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.category.tacz");

    public static void onRefitPress(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS && REFIT_KEY.matchesKey(event.getKey(), event.getScanCode())) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (isInGame()) {
                if (IGun.mainhandHoldGun(player) && MinecraftClient.getInstance().currentScreen == null) {
                    IGun iGun = IGun.getIGunOrNull(player.getMainHandStack());
                    if (iGun != null && iGun.hasAttachmentLock(player.getMainHandStack())) {
                        return;
                    }
                    MinecraftClient.getInstance().setScreen(new GunRefitScreen());
                }
            } else if (MinecraftClient.getInstance().currentScreen instanceof GunRefitScreen) {
                MinecraftClient.getInstance().setScreen(null);
            }
        }
    }
}
