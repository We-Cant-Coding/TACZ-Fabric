package com.tacz.guns.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.util.AttachmentDataUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class GunHudOverlay {
    private static final Identifier SEMI = new Identifier(GunMod.MOD_ID, "textures/hud/fire_mode_semi.png");
    private static final Identifier AUTO = new Identifier(GunMod.MOD_ID, "textures/hud/fire_mode_auto.png");
    private static final Identifier BURST = new Identifier(GunMod.MOD_ID, "textures/hud/fire_mode_burst.png");
    private static final DecimalFormat CURRENT_AMMO_FORMAT = new DecimalFormat("000");
    private static final DecimalFormat INVENTORY_AMMO_FORMAT = new DecimalFormat("0000");
    private static long checkAmmoTimestamp = -1L;
    private static int cacheMaxAmmoCount = 0;
    private static int cacheInventoryAmmoCount = 0;

    public static void onHudRender(DrawContext context) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        if (!RenderConfig.GUN_HUD_ENABLE.get()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (!(player instanceof IClientPlayerGunOperator)) {
            return;
        }
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof IGun iGun)) {
            return;
        }
        Identifier gunId = iGun.getGunId(stack);
        ClientGunIndex gunIndex = TimelessAPI.getClientGunIndex(gunId).orElse(null);
        if (gunIndex == null) {
            return;
        }
        // Current number of ammunition for firearms
        int ammoCount = iGun.getCurrentAmmoCount(stack) + (iGun.hasBulletInBarrel(stack) && gunIndex.getGunData().getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
        int ammoCountColor;
        if (ammoCount < (cacheMaxAmmoCount * 0.25)) {
            // red (color)
            ammoCountColor = 0xFF5555;
        } else {
            // fig. reactionary
            ammoCountColor = 0xFFFFFF;
        }
        String currentAmmoCountText = CURRENT_AMMO_FORMAT.format(ammoCount);

        // Calculation of the number of munitions
        handleCacheCount(player, stack, gunIndex, iGun);

        // vertical line
        context.fill(width - 75, height - 43, width - 74, height - 25, 0xFFFFFFFF);

        MatrixStack poseStack = context.getMatrices();

        TextRenderer font = mc.textRenderer;

        // figure
        poseStack.push();
        poseStack.scale(1.5f, 1.5f, 1);
        context.drawText(font, currentAmmoCountText, (int) ((width - 70) / 1.5f), (int) ((height - 43) / 1.5f), ammoCountColor, false);
        poseStack.pop();

        poseStack.push();
        poseStack.scale(0.8f, 0.8f, 1);
        String inventoryAmmoCountText = INVENTORY_AMMO_FORMAT.format(cacheInventoryAmmoCount);
        int inventoryAmmoCountColor;
        if (iGun.useDummyAmmo(stack)) {
            inventoryAmmoCountColor = 0x55FFFF;
        } else {
            inventoryAmmoCountColor = 0xAAAAAA;
        }
        context.drawText(font, inventoryAmmoCountText, (int) ((width - 68 + font.getWidth(currentAmmoCountText) * 1.5f) / 0.8f), (int) ((height - 43) / 0.8f), inventoryAmmoCountColor, false);
        poseStack.pop();

        // Module Version Information
        String minecraftVersion = SharedConstants.getGameVersion().getName();
        String modVersion = FabricLoader.getInstance().getModContainer(GunMod.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
        String debugInfo = String.format("%s-%s", minecraftVersion, modVersion);
        // copies
        poseStack.push();
        poseStack.scale(0.5f, 0.5f, 1);
        context.drawTextWithShadow(font, debugInfo, (int) ((width - 70) / 0.5f), (int) ((height - 29f) / 0.5f), 0xffaaaaaa);
        poseStack.pop();

        // Icon Rendering
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Get Icon
        Identifier hudTexture = gunIndex.getHUDTexture();
        @Nullable Identifier hudEmptyTexture = gunIndex.getHudEmptyTexture();

        if (ammoCount <= 0) {
            if (hudEmptyTexture == null) {
                RenderSystem.setShaderColor(1, 0.3f, 0.3f, 1);
            } else {
                hudTexture = hudEmptyTexture;
            }
        }
        // Renders gun icons
        context.drawTexture(hudTexture, width - 117, height - 44, 0, 0, 39, 13, 39, 13);

        // Rendering the fire mode icon
        FireMode fireMode = IGun.getMainhandFireMode(player);
        Identifier fireModeTexture = switch (fireMode) {
            case AUTO -> AUTO;
            case BURST -> BURST;
            default -> SEMI;
        };
        RenderSystem.setShaderColor(1, 1, 1, 1);
        context.drawTexture(fireModeTexture, (int) (width - 68.5 + font.getWidth(currentAmmoCountText) * 1.5), height - 38, 0, 0, 10, 10, 10, 10);
    }

    private static void handleCacheCount(ClientPlayerEntity player, ItemStack stack, ClientGunIndex gunIndex, IGun iGun) {
        // 0.2 second check
        if ((System.currentTimeMillis() - checkAmmoTimestamp) > 200) {
            checkAmmoTimestamp = System.currentTimeMillis();
            // Total ammunition for the current firearm
            cacheMaxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(stack, gunIndex.getGunData());
            // Number of ammunition in player's backpack
            if (IGunOperator.fromLivingEntity(player).needCheckAmmo()) {
                if (iGun.useDummyAmmo(stack)) {
                    // Number of virtual munitions cached
                    cacheInventoryAmmoCount = iGun.getDummyAmmoAmount(stack);
                } else {
                    // Cache the number of ammo in the backpack
                    handleInventoryAmmo(stack, player.getInventory());
                }
            } else {
                cacheInventoryAmmoCount = 9999;
            }
        }
    }

    private static void handleInventoryAmmo(ItemStack stack, PlayerInventory inventory) {
        cacheInventoryAmmoCount = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack inventoryItem = inventory.getStack(i);
            if (inventoryItem.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stack, inventoryItem)) {
                cacheInventoryAmmoCount += inventoryItem.getCount();
            }
            if (inventoryItem.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(stack, inventoryItem)) {
                // Create mode ammo box? Go straight back to 9999
                if (iAmmoBox.isAllTypeCreative(inventoryItem) || iAmmoBox.isCreative(inventoryItem)) {
                    cacheInventoryAmmoCount = 9999;
                    return;
                }
                cacheInventoryAmmoCount += iAmmoBox.getAmmoCount(inventoryItem);
            }
        }
    }
}
