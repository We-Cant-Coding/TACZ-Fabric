package com.tacz.guns.client.gui.overlay;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.input.InteractKey;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.config.util.InteractKeyConfigRead;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class InteractKeyTextOverlay {

    public static void onHudRender(DrawContext context) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        if (RenderConfig.DISABLE_INTERACT_HUD_TEXT.get()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || player.isSpectator()) {
            return;
        }
        if (!IGun.mainhandHoldGun(player)) {
            return;
        }
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult == null) {
            return;
        }
        if (hitResult instanceof BlockHitResult blockHitResult) {
            renderBlockText(context, width, height, blockHitResult, player, mc);
            return;
        }
        if (hitResult instanceof EntityHitResult entityHitResult) {
            renderEntityText(context, width, height, entityHitResult, mc);
        }
    }

    private static void renderBlockText(DrawContext graphics, int width, int height, BlockHitResult blockHitResult, ClientPlayerEntity player, MinecraftClient mc) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState block = player.getWorld().getBlockState(blockPos);
        if (InteractKeyConfigRead.canInteractBlock(block)) {
            renderText(graphics, width, height, mc.textRenderer);
        }
    }

    private static void renderEntityText(DrawContext graphics, int width, int height, EntityHitResult entityHitResult, MinecraftClient mc) {
        Entity entity = entityHitResult.getEntity();
        if (InteractKeyConfigRead.canInteractEntity(entity)) {
            renderText(graphics, width, height, mc.textRenderer);
        }
    }

    private static void renderText(DrawContext graphics, int width, int height, TextRenderer font) {
        String keyName = InteractKey.INTERACT_KEY.getBoundKeyLocalizedText().getString();
        Text title = Text.translatable("gui.tacz-fabric.interact_key.text.desc", StringUtils.capitalize(keyName));
        graphics.drawText(font, title, (int) ((width - font.getWidth(title)) / 2.0f), (int) (height / 2.0f - 25), Formatting.YELLOW.getColorValue(), false);
    }
}