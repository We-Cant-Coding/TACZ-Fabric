package com.tacz.guns.client.gui;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.gui.components.refit.*;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.packets.c2s.RefitGunC2SPacket;
import com.tacz.guns.network.packets.c2s.UnloadAttachmentC2SPacket;
import com.tacz.guns.sound.SoundManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class GunRefitScreen extends Screen {
    public static final Identifier SLOT_TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/refit_slot.png");
    public static final Identifier TURN_PAGE_TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/refit_turn_page.png");
    public static final Identifier UNLOAD_TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/refit_unload.png");
    public static final Identifier ICONS_TEXTURE = new Identifier(GunMod.MOD_ID, "textures/gui/refit_slot_icons.png");

    public static final int ICON_UV_SIZE = 32;
    public static final int SLOT_SIZE = 18;
    private static final int INVENTORY_ATTACHMENT_SLOT_COUNT = 8;

    private int currentPage = 0;

    public GunRefitScreen() {
        super(Text.literal("Gun Refit Screen"));
        RefitTransform.init();
    }

    public static int getSlotTextureXOffset(ItemStack gunItem, AttachmentType attachmentType) {
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return -1;
        }
        if (!iGun.allowAttachmentType(gunItem, attachmentType)) {
            return ICON_UV_SIZE * 6;
        }
        switch (attachmentType) {
            case GRIP -> {
                return 0;
            }
            case LASER -> {
                return ICON_UV_SIZE;
            }
            case MUZZLE -> {
                return ICON_UV_SIZE * 2;
            }
            case SCOPE -> {
                return ICON_UV_SIZE * 3;
            }
            case STOCK -> {
                return ICON_UV_SIZE * 4;
            }
            case EXTENDED_MAG -> {
                return ICON_UV_SIZE * 5;
            }
        }
        return -1;
    }

    public static int getSlotsTextureWidth() {
        return ICON_UV_SIZE * 7;
    }

    @Override
    public void init() {
        this.clearChildren();
        // 添加配件槽位
        this.addAttachmentTypeButtons();
        // 添加可选配件列表
        this.addInventoryAttachmentButtons();
    }

    @Override
    public void render(@NotNull DrawContext graphics, int mouseX, int mouseY, float pPartialTick) {
        super.render(graphics, mouseX, mouseY, pPartialTick);

        GunPropertyDiagrams.draw(graphics, textRenderer, 11, 11);

        this.drawables.stream().filter(w -> w instanceof IComponentTooltip).forEach(w -> ((IComponentTooltip) w)
                .renderTooltip(component -> graphics.drawTooltip(textRenderer, component, mouseX, mouseY)));
        this.drawables.stream().filter(w -> w instanceof IStackTooltip).forEach(w -> ((IStackTooltip) w)
                .renderTooltip(stack -> graphics.drawItemTooltip(textRenderer, stack, mouseX, mouseY)));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void addInventoryAttachmentButtons() {
        ClientPlayerEntity player = client.player;
        if (RefitTransform.getCurrentTransformType() == AttachmentType.NONE || player == null) {
            return;
        }
        int startX = this.width - 30;
        int startY = 50;
        int pageStart = currentPage * INVENTORY_ATTACHMENT_SLOT_COUNT;
        int count = 0;
        int currentY = startY;
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack inventoryItem = inventory.getStack(i);
            IAttachment attachment = IAttachment.getIAttachmentOrNull(inventoryItem);
            IGun iGun = IGun.getIGunOrNull(player.getMainHandStack());
            if (attachment != null && iGun != null && attachment.getType(inventoryItem) == RefitTransform.getCurrentTransformType()) {
                if (!iGun.allowAttachment(player.getMainHandStack(), inventoryItem)) {
                    continue;
                }
                count++;
                if (count <= pageStart) {
                    continue;
                }
                if (count > pageStart + INVENTORY_ATTACHMENT_SLOT_COUNT) {
                    continue;
                }
                InventoryAttachmentSlot button = new InventoryAttachmentSlot(startX, currentY, i, inventory, b -> {
                    int slotIndex = ((InventoryAttachmentSlot) b).getSlotIndex();
                    SoundPlayManager.playerRefitSound(inventory.getStack(slotIndex), player, SoundManager.INSTALL_SOUND);
                    RefitGunC2SPacket message = new RefitGunC2SPacket(slotIndex, inventory.selectedSlot, RefitTransform.getCurrentTransformType());
                    NetworkHandler.sendToServer(message);
                });
                this.addDrawableChild(button);
                currentY = currentY + SLOT_SIZE;
            }
        }
        int totalPage = (count - 1) / INVENTORY_ATTACHMENT_SLOT_COUNT;
        RefitTurnPageButton turnPageButtonUp = new RefitTurnPageButton(startX, startY - 10, true, b -> {
            if (currentPage > 0) {
                currentPage--;
                init();
            }
        });
        RefitTurnPageButton turnPageButtonDown = new RefitTurnPageButton(startX, startY + SLOT_SIZE * INVENTORY_ATTACHMENT_SLOT_COUNT + 2, false, b -> {
            if (currentPage < totalPage) {
                currentPage++;
                init();
            }
        });
        if (currentPage < totalPage) {
            this.addDrawableChild(turnPageButtonDown);
        }
        if (currentPage > 0) {
            this.addDrawableChild(turnPageButtonUp);
        }
    }

    private void addAttachmentTypeButtons() {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        IGun iGun = IGun.getIGunOrNull(player.getMainHandStack());
        if (iGun == null) {
            return;
        }
        int startX = this.width - 30;
        int startY = 10;
        for (AttachmentType type : AttachmentType.values()) {
            if (type == AttachmentType.NONE) {
                continue;
            }
            PlayerInventory inventory = player.getInventory();
            GunAttachmentSlot button = new GunAttachmentSlot(startX, startY, type, inventory.selectedSlot, inventory, b -> {
                AttachmentType buttonType = ((GunAttachmentSlot) b).getAttachmentType();
                // 如果这个槽位不允许安装配件，则默认退回概览，不选中槽位。
                if (!((GunAttachmentSlot) b).isAllow()) {
                    if (RefitTransform.changeRefitScreenView(AttachmentType.NONE)) {
                        this.init();
                    }
                    return;
                }
                // 点击的是当前选中的槽位，则退回概览
                if (RefitTransform.getCurrentTransformType() == buttonType && buttonType != AttachmentType.NONE) {
                    if (RefitTransform.changeRefitScreenView(AttachmentType.NONE)) {
                        this.init();
                    }
                    return;
                }
                // 切换选中的槽位。
                if (RefitTransform.changeRefitScreenView(buttonType)) {
                    this.init();
                }
            });
            if (RefitTransform.getCurrentTransformType() == type) {
                button.setSelected(true);
                // 添加拆卸配件按钮
                RefitUnloadButton unloadButton = new RefitUnloadButton(startX + 5, startY + SLOT_SIZE + 2, b -> {
                    ItemStack attachmentItem = button.getAttachmentItem();
                    if (!attachmentItem.isEmpty()) {
                        int freeSlot = inventory.getEmptySlot();
                        if (freeSlot != -1) {
                            SoundPlayManager.playerRefitSound(attachmentItem, player, SoundManager.UNINSTALL_SOUND);
                            UnloadAttachmentC2SPacket message = new UnloadAttachmentC2SPacket(inventory.selectedSlot, RefitTransform.getCurrentTransformType());
                            NetworkHandler.sendToServer(message);
                        } else {
                            player.sendMessage(Text.translatable("gui.tacz-fabric.gun_refit.unload.no_space"));
                        }
                    }
                });
                if (!button.getAttachmentItem().isEmpty()) {
                    this.addDrawableChild(unloadButton);
                }
            }
            this.addDrawableChild(button);
            startX = startX - SLOT_SIZE;
        }
    }
}
