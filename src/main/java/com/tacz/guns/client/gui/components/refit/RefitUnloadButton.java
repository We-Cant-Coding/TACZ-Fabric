package com.tacz.guns.client.gui.components.refit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RefitUnloadButton extends ButtonWidget implements IComponentTooltip {
    public RefitUnloadButton(int pX, int pY, ButtonWidget.PressAction pOnPress) {
        super(pX, pY, 8, 8, Text.empty(), pOnPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void renderButton(@NotNull DrawContext graphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        int x = getX(), y = getY();
        if (isSelected()) {
            graphics.drawTexture(GunRefitScreen.UNLOAD_TEXTURE, x, y, width, height, 0, 0, 80, 80, 160, 80);
        } else {
            graphics.drawTexture(GunRefitScreen.UNLOAD_TEXTURE, x, y, width, height, 80, 0, 80, 80, 160, 80);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    public void renderTooltip(Consumer<List<Text>> consumer) {
        if (this.isSelected()) {
            consumer.accept(Collections.singletonList(Text.translatable("tooltip.tacz.refit.unload")));
        }
    }
}
