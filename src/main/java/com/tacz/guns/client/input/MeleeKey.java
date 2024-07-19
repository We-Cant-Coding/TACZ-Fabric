package com.tacz.guns.client.input;

import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.forge.InputEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class MeleeKey implements InputEvent.KeyCallback, InputEvent.MousePostCallback {
    public static final KeyBinding MELEE_KEY = new KeyBinding("key.tacz-fabric.melee.desc",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.tacz");

    @Override
    public void onKey(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && MELEE_KEY.matchesKey(event.getKey(), event.getScanCode())) {
            doMeleeLogic();
        }
    }

    @Override
    public void onMousePost(InputEvent.MouseButton.Post event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && MELEE_KEY.matchesMouse(event.getButton())) {
            doMeleeLogic();
        }
    }

    private static void doMeleeLogic() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
        if (!operator.isAim()) {
            operator.melee();
        }
    }
}
