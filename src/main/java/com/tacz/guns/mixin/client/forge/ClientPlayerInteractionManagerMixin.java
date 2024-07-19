package com.tacz.guns.mixin.client.forge;

import com.tacz.guns.event.PreventGunClick;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "method_41936", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean attackBlock_breakBlock(ClientPlayerInteractionManager instance, BlockPos pos) {
        AtomicBoolean cancel = new AtomicBoolean(false);
        PreventGunClick.onLeftClickBlock(Objects.requireNonNull(client.player), cancel);
        if (cancel.get()) return false;
        return instance.breakBlock(pos);
    }

    // (attackBlock_call, attackBlock_onBlockBreakStart, attackBlock_calcBlockBreakingDelta) start

    @Unique
    private final AtomicBoolean attackBlock_cancel = new AtomicBoolean(false);

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1, shift = At.Shift.BEFORE))
    private void attackBlock_call(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        attackBlock_cancel.set(false);
        PreventGunClick.onLeftClickBlock(Objects.requireNonNull(client.player), attackBlock_cancel);
    }

    @Redirect(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onBlockBreakStart(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void attackBlock_onBlockBreakStart(BlockState instance, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        if (attackBlock_cancel.get()) return;
        instance.onBlockBreakStart(world, blockPos, playerEntity);
    }

    @Inject(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", shift = At.Shift.BEFORE), cancellable = true)
    private void attackBlock_calcBlockBreakingDelta(BlockState blockState, BlockPos blockPos, Direction direction, int sequence, CallbackInfoReturnable<Packet<?>> cir) {
        if (attackBlock_cancel.get()) {
            cir.setReturnValue(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction, sequence));
        }
    }

    // (attackBlock_call, attackBlock_onBlockBreakStart, attackBlock_calcBlockBreakingDelta) end

    @Redirect(method = "method_41935", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean updateBlockBreakingProgress_breakBlock(ClientPlayerInteractionManager instance, BlockPos pos) {
        AtomicBoolean cancel = new AtomicBoolean(false);
        PreventGunClick.onLeftClickBlock(Objects.requireNonNull(client.player), cancel);
        if (cancel.get()) return false;
        return instance.breakBlock(pos);
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onBlockBreaking(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    private void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        AtomicBoolean cancel = new AtomicBoolean(false);
        PreventGunClick.onLeftClickBlock(Objects.requireNonNull(client.player), cancel);
        if (cancel.get()) {
            cir.setReturnValue(true);
        }
    }
}
