package com.tacz.guns.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.event.RenderTickEvent;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateMachine;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.renderer.crosshair.CrosshairType;
import com.tacz.guns.compat.shouldersurfing.ShoulderSurfingCompat;
import com.tacz.guns.config.client.RenderConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class RenderCrosshairEvent {
    private static final Identifier HIT_ICON = new Identifier(GunMod.MOD_ID, "textures/crosshair/hit/hit_marker.png");
    private static final long KEEP_TIME = 300;
    private static boolean isRefitScreen = false;
    private static long hitTimestamp = -1L;
    private static long killTimestamp = -1L;
    private static long headShotTimestamp = -1L;

    /**
     * 当玩家手上拿着枪时，播放特定动画、或瞄准时需要隐藏准心
     */
    public static void onRenderOverlay(DrawContext context, Window window) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        if (!IGun.mainhandHoldGun(player)) {
            return;
        }

        // hit the display
        renderHitMarker(context, window);
        // Cancel center rendering while changing rounds
        ReloadState reloadState = IGunOperator.fromLivingEntity(player).getSynReloadState();
        if (reloadState.getStateType().isReloading()) {
            return;
        }
        // When you open the gun modification screen, cancel the collimation rendering
        if (isRefitScreen) {
            return;
        }
        // Cancel alignment rendering when the animation being played needs to hide the alignment.
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof IGun iGun)) {
            return;
        }
        Identifier gunId = iGun.getGunId(stack);
        IClientPlayerGunOperator playerGunOperator = IClientPlayerGunOperator.fromLocalPlayer(player);
        TimelessAPI.getClientGunIndex(gunId).ifPresent(gunIndex -> {
            // Cancel center rendering when aiming is almost complete

            if (playerGunOperator.getClientAimingProgress(MinecraftClient.getInstance().getTickDelta()) > 0.9) {
                // Gun packs can be forced to show collimator
                boolean forceShow = gunIndex.isShowCrosshair();
                // Over-the-shoulder view can be forced to show collimator
                boolean shoulderSurfingForceShow = ShoulderSurfingCompat.showCrosshair();
                // If neither of the two forces are present, then only hide is allowed.
                if (!forceShow && !shoulderSurfingForceShow) {
                    return;
                }
            }

            GunAnimationStateMachine animationStateMachine = gunIndex.getAnimationStateMachine();
            if (!animationStateMachine.shouldHideCrossHair()) {
                renderCrosshair(context, window);
            }
        });
    }

    public static void onRenderTick(RenderTickEvent event) {
        // 奇迹的是，RenderGameOverlayEvent.PreLayer 事件中，screen 还未被赋值...
        isRefitScreen = event.getClient().currentScreen instanceof GunRefitScreen;
    }

    private static void renderCrosshair(DrawContext graphics, Window window) {
        GameOptions options = MinecraftClient.getInstance().options;
        // 越肩视角可以强制显示准星
        boolean shoulderSurfingForceShow = ShoulderSurfingCompat.showCrosshair();
        if (!options.getPerspective().isFirstPerson() && !shoulderSurfingForceShow) {
            return;
        }
        if (options.hudHidden) {
            return;
        }
        ClientPlayerInteractionManager gameMode = MinecraftClient.getInstance().interactionManager;
        if (gameMode == null) {
            return;
        }
        if (gameMode.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        Identifier location = CrosshairType.getTextureLocation(RenderConfig.CROSSHAIR_TYPE.get());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1F, 1F, 1F, 0.9f);
        float x = width / 2f - 8;
        float y = height / 2f - 8;
        graphics.drawTexture(location, (int) x, (int) y, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    private static void renderHitMarker(DrawContext graphics, Window window) {
        long remainHitTime = System.currentTimeMillis() - hitTimestamp;
        long remainKillTime = System.currentTimeMillis() - killTimestamp;
        long remainHeadShotTime = System.currentTimeMillis() - headShotTimestamp;
        float offset = RenderConfig.HIT_MARKET_START_POSITION.get().floatValue();
        float fadeTime;

        if (remainKillTime > KEEP_TIME) {
            if (remainHitTime > KEEP_TIME) {
                return;
            } else {
                fadeTime = remainHitTime;
            }
        } else {
            // 最大位移为 4 像素
            offset += (remainKillTime * 4f) / KEEP_TIME;
            fadeTime = remainKillTime;
        }

        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        float x = width / 2f - 8;
        float y = height / 2f - 8;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        // 색상 설정
        if (remainHeadShotTime > KEEP_TIME) {
            RenderSystem.setShaderColor(1F, 1F, 1F, 1 - fadeTime / KEEP_TIME);
        } else {
            RenderSystem.setShaderColor(1F, 0, 0, 1 - fadeTime / KEEP_TIME);
        }

        // 텍스처 그리기
        graphics.drawTexture(HIT_ICON, (int) (x - offset), (int) (y - offset), 0, 0, 8, 8, 16, 16);
        graphics.drawTexture(HIT_ICON, (int) (x + 8 + offset), (int) (y - offset), 8, 0, 8, 8, 16, 16);
        graphics.drawTexture(HIT_ICON, (int) (x - offset), (int) (y + 8 + offset), 0, 8, 8, 8, 16, 16);
        graphics.drawTexture(HIT_ICON, (int) (x + 8 + offset), (int) (y + 8 + offset), 8, 8, 8, 8, 16, 16);

        // 색상 리셋
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void markHitTimestamp() {
        RenderCrosshairEvent.hitTimestamp = System.currentTimeMillis();
    }

    public static void markKillTimestamp() {
        RenderCrosshairEvent.killTimestamp = System.currentTimeMillis();
    }

    public static void markHeadShotTimestamp() {
        RenderCrosshairEvent.headShotTimestamp = System.currentTimeMillis();
    }
}
