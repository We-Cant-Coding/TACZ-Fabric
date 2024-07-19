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

public class RefitTurnPageButton extends ButtonWidget implements IComponentTooltip {
    private final boolean isUpPage;

    public RefitTurnPageButton(int pX, int pY, boolean isUpPage, ButtonWidget.PressAction pOnPress) {
        super(pX, pY, 18, 8, Text.empty(), pOnPress, DEFAULT_NARRATION_SUPPLIER);
        this.isUpPage = isUpPage;
    }

    @Override
    public void renderButton(@NotNull DrawContext graphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        int x = getX(), y = getY();
        int yOffset = isUpPage ? 0 : 80;
        if (isSelected()) {
            graphics.drawTexture(GunRefitScreen.TURN_PAGE_TEXTURE, x, y, width, height, 0, yOffset, 180, 80, 180, 160);
        } else {
            graphics.drawTexture(GunRefitScreen.TURN_PAGE_TEXTURE, x + 1, y + 1, width - 2, height - 2, 10, yOffset + 10, 180 - 20, 80 - 20, 180, 160);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    public void renderTooltip(Consumer<List<Text>> consumer) {
        if (this.isSelected()) {
            String key = isUpPage ? "tooltip.tacz-fabric.page.previous" : "tooltip.tacz-fabric.page.next";
            consumer.accept(Collections.singletonList(Text.translatable(key)));
        }
    }
}
