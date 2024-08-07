package com.tacz.guns.client.gui.components.refit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Consumer;

public class GunAttachmentSlot extends ButtonWidget implements IStackTooltip {
    private final AttachmentType type;
    private final PlayerInventory inventory;
    private final int gunItemIndex;
    private final String nameKey;
    private boolean selected = false;
    private ItemStack attachmentItem = ItemStack.EMPTY;

    public GunAttachmentSlot(int pX, int pY, AttachmentType type, int gunItemIndex, PlayerInventory inventory, ButtonWidget.PressAction onPress) {
        super(pX, pY, GunRefitScreen.SLOT_SIZE, GunRefitScreen.SLOT_SIZE, Text.empty(), onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.type = type;
        this.inventory = inventory;
        this.gunItemIndex = gunItemIndex;
        this.nameKey = String.format("tooltip.tacz.attachment.%s", type.name().toLowerCase(Locale.US));
    }

    @Override
    public void renderTooltip(Consumer<ItemStack> consumer) {
        if (this.isSelected() && !attachmentItem.isEmpty()) {
            consumer.accept(attachmentItem);
        }
    }

    @Override
    public void renderButton(@NotNull DrawContext graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isSelected()) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            int yOffset = this.getY() + 20;
            if (this.selected && !attachmentItem.isEmpty()) {
                yOffset = this.getY() + 30;
            }
            graphics.drawCenteredTextWithShadow(font, Text.translatable(nameKey), this.getX() + this.getWidth() / 2, yOffset, Formatting.WHITE.getColorValue());
        }
        ItemStack gunItem = inventory.getStack(gunItemIndex);
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return;
        }

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        // 渲染外框
        int x = this.getX();
        int y = this.getY();
        if (isSelected() || selected) {
            graphics.drawTexture(GunRefitScreen.SLOT_TEXTURE, x, y, 0, 0, width, height, GunRefitScreen.SLOT_SIZE, GunRefitScreen.SLOT_SIZE);
        } else {
            graphics.drawTexture(GunRefitScreen.SLOT_TEXTURE, x + 1, y + 1, 1, 1, width - 2, height - 2, GunRefitScreen.SLOT_SIZE, GunRefitScreen.SLOT_SIZE);
        }
        // 渲染内部物品，或者空置时的icon
        this.attachmentItem = iGun.getAttachment(gunItem, type);
        if (!attachmentItem.isEmpty()) {
            graphics.drawItem(attachmentItem, x + 1, y + 1);
        } else {
            int xOffset = GunRefitScreen.getSlotTextureXOffset(gunItem, type);
            graphics.drawTexture(GunRefitScreen.ICONS_TEXTURE, x + 2, y + 2, width - 4, height - 4, xOffset, 0, GunRefitScreen.ICON_UV_SIZE, GunRefitScreen.ICON_UV_SIZE, GunRefitScreen.getSlotsTextureWidth(), GunRefitScreen.ICON_UV_SIZE);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public AttachmentType getAttachmentType() {
        return type;
    }

    public ItemStack getAttachmentItem() {
        ItemStack gunItem = inventory.getStack(gunItemIndex);
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return ItemStack.EMPTY;
        }
        return iGun.getAttachment(gunItem, type);
    }

    public boolean isAllow() {
        ItemStack gunItem = inventory.getStack(gunItemIndex);
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return false;
        }
        return iGun.allowAttachmentType(gunItem, type);
    }
}
