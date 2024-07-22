package com.tacz.guns.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.event.InputEvent;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.nbt.AttachmentItemDataAccessor;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.config.client.ZoomConfig;
import com.tacz.guns.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @WrapOperation(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void reduceSensitivity(ClientPlayerEntity player, double yaw, double pitch, Operation<Void> original) {
        ItemStack mainHandItem = player.getMainHandStack();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        if (iGun == null) {
            original.call(player, yaw, pitch);
            return;
        }
        Identifier attachmentId = iGun.getAttachmentId(mainHandItem, AttachmentType.SCOPE);
        float zoomLevel = 1;
        if (DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            // 缩放倍率
            Identifier gunId = iGun.getGunId(mainHandItem);
            zoomLevel = TimelessAPI.getClientGunIndex(gunId).map(ClientGunIndex::getIronZoom).orElse(1f);
        } else {
            Optional<ClientAttachmentIndex> optional = TimelessAPI.getClientAttachmentIndex(attachmentId);
            if (optional.isPresent()) {
                float[] zoom = optional.get().getZoom();
                if (zoom != null && zoom.length > 0) {
                    NbtCompound attachmentTag = iGun.getAttachmentTag(mainHandItem, AttachmentType.SCOPE);
                    zoomLevel = zoom[AttachmentItemDataAccessor.getZoomNumberFromTag(attachmentTag) % zoom.length];
                }
            }
        }
        MinecraftClient minecraft = MinecraftClient.getInstance();
        float progress = IGunOperator.fromLivingEntity(player).getSynAimingProgress();
        // 开镜灵敏度系数
        double sensitivityMultiplier = ZoomConfig.ZOOM_SENSITIVITY_BASE_MULTIPLIER.get();
        sensitivityMultiplier = 1 + (sensitivityMultiplier - 1) * progress;
        // 两种状态下的 fov 计算
        double originalFov = minecraft.options.getFov().getValue();
        double currentFov = MathUtil.magnificationToFov(1 + (zoomLevel - 1) * progress, originalFov);
        // 荧幕距离系数，MC 和 COD 一样使用 MDV 标准，默认为 MDV133（系数为 1.33）
        double coefficient = ZoomConfig.SCREEN_DISTANCE_COEFFICIENT.get();
        double denominator = MathUtil.zoomSensitivityRatio(currentFov, originalFov, coefficient) * sensitivityMultiplier;
        original.call(player, yaw * denominator, pitch * denominator);
    }

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onMouseButtonPre(long window, int button, int action, int mods, CallbackInfo ci) {
        if (new InputEvent.MouseButton.Pre(button, action, mods).post()) {
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", at = @At("TAIL"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == this.client.getWindow().getHandle()) {
            new InputEvent.MouseButton.Post(button, action, mods).post();
        }
    }
}
