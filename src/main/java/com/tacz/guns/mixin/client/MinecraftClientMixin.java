package com.tacz.guns.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.client.event.RenderTickEvent;
import com.tacz.guns.client.event.InventoryEvent;
import com.tacz.guns.api.client.event.InputEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;

    @Shadow @Final public ParticleManager particleManager;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Unique
    private InputEvent.InteractionKeyMappingTriggered handleBlockBreaking;
    @Unique
    private InputEvent.InteractionKeyMappingTriggered doAttack;
    @Unique
    private InputEvent.InteractionKeyMappingTriggered doItemUse;

    @Inject(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getSide()Lnet/minecraft/util/math/Direction;"), cancellable = true)
    private void handleBlockBreaking(boolean breaking, CallbackInfo ci, @Local BlockHitResult blockHitResult, @Local BlockPos blockPos) {
        handleBlockBreaking = new InputEvent.InteractionKeyMappingTriggered(0, options.attackKey, Hand.MAIN_HAND);
        Direction direction = blockHitResult.getSide();

        if (handleBlockBreaking.post()) {
            if (handleBlockBreaking.shouldSwingHand()) {
                this.particleManager.addBlockBreakingParticles(blockPos, direction);
                Objects.requireNonNull(this.player).swingHand(Hand.MAIN_HAND);
            }

            ci.cancel();
            handleBlockBreaking = null;
        }
    }

    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
    private boolean updateBlockBreakingProgress(ClientPlayerInteractionManager instance, BlockPos pos, Direction direction) {
        try {
            if (handleBlockBreaking != null) {
                return instance.updateBlockBreakingProgress(pos, direction) && handleBlockBreaking.shouldSwingHand();
            }
            return instance.updateBlockBreakingProgress(pos, direction);
        } finally {
            handleBlockBreaking = null;
        }
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;", shift = At.Shift.BEFORE), cancellable = true)
    private void doAttack(CallbackInfoReturnable<Boolean> cir, @Local boolean flag) {
        doAttack = new InputEvent.InteractionKeyMappingTriggered(0, options.attackKey, Hand.MAIN_HAND);

        if (doAttack.post()) {
            if (doAttack.shouldSwingHand()) {
                Objects.requireNonNull(player).swingHand(Hand.MAIN_HAND);
            }

            cir.setReturnValue(flag);
            doAttack = null;
        }
    }

    @Redirect(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void swingHand(ClientPlayerEntity player, Hand hand) {
        if (doAttack != null) {
            if (doAttack.shouldSwingHand()) {
                player.swingHand(hand);
            }
            doAttack = null;
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), cancellable = true)
    private void doItemUse(CallbackInfo ci, @Local Hand hand) {
        doItemUse = new InputEvent.InteractionKeyMappingTriggered(1, options.useKey, hand);

        if (doItemUse.post()) {
            if (doItemUse.shouldSwingHand()) {
                Objects.requireNonNull(player).swingHand(hand);
            }
            ci.cancel();
            doItemUse = null;
        }
    }

    @Redirect(
            method = "doItemUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;shouldSwingHand()Z"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;shouldSwingHand()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;shouldSwingHand()Z", ordinal = 1)
            ))
    private boolean shouldSwingHand(ActionResult result) {
        try {
            if (doItemUse != null) {
                return result.shouldSwingHand() && doItemUse.shouldSwingHand();
            }

            return result.shouldSwingHand();
        } finally {
            doItemUse = null;
        }
    }

    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;", shift = At.Shift.BEFORE), cancellable = true)
    private void doItemPick(CallbackInfo ci) {
        var event = new InputEvent.InteractionKeyMappingTriggered(2, options.pickItemKey, Hand.MAIN_HAND);
        if (event.post()) {
            ci.cancel();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;reset()V"))
    private void disconnect(Screen screen, CallbackInfo ci) {
        InventoryEvent.onPlayerLoggedOut();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void renderTickStart(boolean tick, CallbackInfo ci) {
        new RenderTickEvent(This()).post();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 4, shift = At.Shift.AFTER))
    private void renderTickEnd(boolean tick, CallbackInfo ci) {
        new RenderTickEvent(This()).post();
    }

    @Unique
    private MinecraftClient This() {
        return (MinecraftClient) (Object) this;
    }
}
