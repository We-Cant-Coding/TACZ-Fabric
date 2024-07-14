package com.tacz.guns.client.gui.components.smith;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.GunMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class TypeButton extends ButtonWidget {
    private static final Identifier TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/gun_smith_table.png");
    private final ItemStack stack;
    private boolean isSelected = false;

    public TypeButton(int pX, int pY, ItemStack stack, ButtonWidget.PressAction onPress) {
        super(pX, pY, 24, 25, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.stack = stack;
    }

    @Override
    protected void renderButton(@NotNull DrawContext gui, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.enableDepthTest();

        int vOffset = isSelected() ? 204 + this.height : 204;
        if (isSelected) {
            gui.drawTexture(TEXTURE, this.getX(), this.getY(), 0, vOffset, this.width, this.height, 256, 256);
        } else {
            gui.drawTexture(TEXTURE, this.getX(), this.getY(), 26, vOffset, this.width, this.height, 256, 256);
        }

        gui.drawItem(this.stack, this.getX() + 4, this.getY() + 5);
    }

    @Override
    public void onPress() {
        this.isSelected = true;
        this.onPress.onPress(this);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
