package com.tacz.guns.client.input;

import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
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
public class ReloadKey {
    public static final KeyBinding RELOAD_KEY = new KeyBinding("key.tacz-fabric.reload.desc",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category.tacz");

    public static void onReloadPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && RELOAD_KEY.matchesKey(event.getKey(), event.getScanCode())) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (IGun.mainhandHoldGun(player)) {
                IClientPlayerGunOperator.fromLocalPlayer(player).reload();
            }
        }
    }
}
