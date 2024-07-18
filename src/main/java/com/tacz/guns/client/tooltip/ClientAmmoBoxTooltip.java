package com.tacz.guns.client.tooltip;

import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.inventory.tooltip.AmmoBoxTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class ClientAmmoBoxTooltip implements TooltipComponent {
    private final ItemStack ammo;
    private final Text count;
    private final Text ammoName;

    public ClientAmmoBoxTooltip(AmmoBoxTooltip tooltip) {
        this.ammo = tooltip.ammo();
        ItemStack ammoBox = tooltip.ammoBox();
        if (ammoBox.getItem() instanceof IAmmoBox box && box.isCreative(ammoBox)) {
            this.count = Text.literal("∞");
        } else {
            this.count = Text.translatable("tooltip.tacz.ammo_box.count", tooltip.count());
        }
        this.ammoName = this.ammo.getName();
    }

    @Override
    public int getHeight() {
        return 28;
    }

    @Override
    public int getWidth(TextRenderer font) {
        return Math.max(font.getWidth(ammoName), font.getWidth(count)) + 22;
    }

    @Override
    public void drawText(TextRenderer font, int pX, int pY, Matrix4f matrix4f, VertexConsumerProvider.Immediate bufferSource) {
        font.draw(ammoName, pX + 20, pY + 4, 0xffaa00, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        font.draw(count, pX + 20, pY + 15, 0x666666, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
    }

    @Override
    public void drawItems(TextRenderer pFont, int pX, int pY, DrawContext pGuiGraphics) {
        pGuiGraphics.drawItem(ammo, pX, pY + 5);
    }
}
