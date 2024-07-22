package com.tacz.guns.client.input;

import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.client.event.InputEvent;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ClientMessagePlayerZoom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class ZoomKey {
    public static final KeyBinding ZOOM_KEY = new KeyBinding("key.tacz-fabric.zoom.desc",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.tacz");

    public static void onZoomKeyPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && ZOOM_KEY.matchesKey(event.getKey(), event.getScanCode())) {
            doZoomLogic();
        }
    }

    public static void onZoomMousePress(InputEvent.MouseButton.Post event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && ZOOM_KEY.matchesMouse(event.getButton())) {
            doZoomLogic();
        }
    }

    private static void doZoomLogic() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
        if (operator.isAim()) {
            NetworkHandler.sendToServer(new ClientMessagePlayerZoom());
        }
    }
}
