package com.tacz.guns.client.gui.components.smith;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.GunMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ResultButton extends ButtonWidget {
    private static final Identifier TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/gun_smith_table.png");
    private final ItemStack stack;
    private boolean isSelected = false;

    public ResultButton(int pX, int pY, ItemStack stack, ButtonWidget.PressAction onPress) {
        super(pX, pY, 94, 16, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.stack = stack;
    }

    @Override
    protected void renderButton(@NotNull DrawContext gui, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.enableDepthTest();

        if (isSelected) {
            if (isSelected()) {
                gui.drawTexture(TEXTURE, this.getX() - 1, this.getY() - 1, 52, 229, this.width + 2, this.height + 2, 256, 256);
            } else {
                gui.drawTexture(TEXTURE, this.getX(), this.getY(), 53, 230, this.width, this.height, 256, 256);
            }
        } else {
            if (isSelected()) {
                gui.drawTexture(TEXTURE, this.getX() - 1, this.getY() - 1, 52, 211, this.width + 2, this.height + 2, 256, 256);
            } else {
                gui.drawTexture(TEXTURE, this.getX(), this.getY(), 53, 212, this.width, this.height, 256, 256);
            }
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        gui.drawItem(stack, this.getX() + 1, this.getY());

        Text hoverName = this.stack.getName();
        drawScrollableText(gui, mc.textRenderer, hoverName, this.getX() + 20, this.getY() + 4, this.getX() + 92, this.getY() + 13, 0xFFFFFF);
    }

    @Override
    public void onPress() {
        this.isSelected = true;
        this.onPress.onPress(this);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void renderTooltips(Consumer<ItemStack> consumer) {
        if (this.isSelected() && !this.stack.isEmpty()) {
            consumer.accept(this.stack);
        }
    }
}
