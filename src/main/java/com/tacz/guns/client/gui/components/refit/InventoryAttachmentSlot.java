package com.tacz.guns.client.gui.components.refit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InventoryAttachmentSlot extends ButtonWidget implements IStackTooltip {
    private final int slotIndex;
    private final PlayerInventory inventory;

    public InventoryAttachmentSlot(int pX, int pY, int slotIndex, PlayerInventory inventory, ButtonWidget.PressAction onPress) {
        super(pX, pY, 18, 18, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.slotIndex = slotIndex;
        this.inventory = inventory;
    }

    @Override
    public void renderTooltip(Consumer<ItemStack> consumer) {
        if (this.isSelected() && 0 <= this.slotIndex && this.slotIndex < this.inventory.size()) {
            ItemStack item = this.inventory.getStack(slotIndex);
            consumer.accept(item);
        }
    }

    @Override
    public void renderButton(@NotNull DrawContext graphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        int x = getX(), y = getY();
        if (isSelected()) {
            graphics.drawTexture(GunRefitScreen.SLOT_TEXTURE, x, y, 0, 0, width, height, 18, 18);
        } else {
            graphics.drawTexture(GunRefitScreen.SLOT_TEXTURE, x + 1, y + 1, 1, 1, width - 2, height - 2, 18, 18);
        }
        graphics.drawItem(inventory.getStack(slotIndex), x + 1, y + 1);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}
