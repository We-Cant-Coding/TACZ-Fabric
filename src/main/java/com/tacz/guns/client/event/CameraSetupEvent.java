package com.tacz.guns.client.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.client.other.KeepingItemRenderer;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.api.client.event.ViewportEvent;
import com.tacz.guns.compat.perspectivemod.PerspectiveModCompat;
import com.tacz.guns.resource.pojo.data.attachment.RecoilModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.tacz.guns.util.math.MathUtil;
import com.tacz.guns.util.math.SecondOrderDynamics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.joml.Quaternionf;

import java.util.Optional;

public class CameraSetupEvent {
    /**
     * 用于平滑 FOV 变化
     */
    private static final SecondOrderDynamics WORLD_FOV_DYNAMICS = new SecondOrderDynamics(0.5f, 1.2f, 0.5f, 0);
    private static final SecondOrderDynamics ITEM_MODEL_FOV_DYNAMICS = new SecondOrderDynamics(0.5f, 1.2f, 0.5f, 0);
    private static PolynomialSplineFunction pitchSplineFunction;
    private static PolynomialSplineFunction yawSplineFunction;
    private static long shootTimeStamp = -1L;
    private static double xRotO = 0;
    private static double yRot0 = 0;
    private static BedrockGunModel lastModel = null;

    // The event is not canceled even if the return value is true or false.
    public static void applyLevelCameraAnimation(ViewportEvent.ComputeCameraAngles event) {
        if (!MinecraftClient.getInstance().options.getBobView().getValue()) {
            return;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();
        if (!(stack.getItem() instanceof IGun iGun)) {
            return;
        }
        TimelessAPI.getClientGunIndex(iGun.getGunId(stack)).ifPresent(gunIndex -> {
            BedrockGunModel gunModel = gunIndex.getGunModel();
            if (lastModel != gunModel) {
                // 切换枪械模型的时候清理一下摄像机动画数据，以避免上一次播放到一半的摄像机动画影响观感。
                gunModel.cleanCameraAnimationTransform();
                lastModel = gunModel;
            }
            IClientPlayerGunOperator clientPlayerGunOperator = IClientPlayerGunOperator.fromLocalPlayer(player);
            float partialTicks = MinecraftClient.getInstance().getTickDelta();
            float aimingProgress = clientPlayerGunOperator.getClientAimingProgress(partialTicks);
            float zoom = iGun.getAimingZoom(stack);
            float multiplier = 1 - aimingProgress + aimingProgress / (float) Math.sqrt(zoom);
            Quaternionf q = MathUtil.multiplyQuaternion(gunModel.getCameraAnimationObject().rotationQuaternion, multiplier);
            double yaw = Math.asin(2 * (q.w() * q.y() - q.x() * q.z()));
            double pitch = Math.atan2(2 * (q.w() * q.x() + q.y() * q.z()), 1 - 2 * (q.x() * q.x() + q.y() * q.y()));
            double roll = Math.atan2(2 * (q.w() * q.z() + q.x() * q.y()), 1 - 2 * (q.y() * q.y() + q.z() * q.z()));
            yaw = Math.toDegrees(yaw);
            pitch = Math.toDegrees(pitch);
            roll = Math.toDegrees(roll);
            event.setYaw((float) yaw + event.getYaw());
            event.setPitch((float) pitch + event.getPitch());
            event.setRoll((float) roll + event.getRoll());
        });
    }

    public static void onBeforeRenderHand(BeforeRenderHandEvent event) {
        if (!MinecraftClient.getInstance().options.getBobView().getValue()) {
            return;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();
        if (!(stack.getItem() instanceof IGun iGun)) {
            return;
        }
        TimelessAPI.getClientGunIndex(iGun.getGunId(stack)).ifPresent(gunIndex -> {
            BedrockGunModel gunModel = gunIndex.getGunModel();
            MatrixStack poseStack = event.getPoseStack();
            IClientPlayerGunOperator clientPlayerGunOperator = IClientPlayerGunOperator.fromLocalPlayer(player);
            float partialTicks = MinecraftClient.getInstance().getTickDelta();
            float aimingProgress = clientPlayerGunOperator.getClientAimingProgress(partialTicks);
            float zoom = iGun.getAimingZoom(stack);
            float multiplier = 1 - aimingProgress + aimingProgress / (float) Math.sqrt(zoom);
            Quaternionf quaternion = MathUtil.multiplyQuaternion(gunModel.getCameraAnimationObject().rotationQuaternion, multiplier);
            poseStack.multiply(quaternion);
            // 截至目前，摄像机动画数据已消费完毕。是否有更好的清理动画数据的方法？
            gunModel.cleanCameraAnimationTransform();
        });
    }

    public static void applyScopeMagnification(ViewportEvent.ComputeFov event) {
        if (!event.usedConfiguredFov()) {
            return; // only modifies the world-rendered fov, so if it's a hand-rendered fov event, it returns the
        }
        Entity entity = event.getCamera().getFocusedEntity();
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();
            if (!(stack.getItem() instanceof IGun iGun)) {
                float fov = WORLD_FOV_DYNAMICS.update((float) event.getFOV());
                event.setFOV(fov);
                return;
            }
            float zoom = iGun.getAimingZoom(stack);
            if (livingEntity instanceof ClientPlayerEntity localPlayer) {
                IClientPlayerGunOperator gunOperator = IClientPlayerGunOperator.fromLocalPlayer(localPlayer);
                float aimingProgress = gunOperator.getClientAimingProgress((float) event.getPartialTick());
                float fov = WORLD_FOV_DYNAMICS.update((float) MathUtil.magnificationToFov(1 + (zoom - 1) * aimingProgress, event.getFOV()));
                event.setFOV(fov);
            } else {
                IGunOperator gunOperator = IGunOperator.fromLivingEntity(livingEntity);
                float aimingProgress = gunOperator.getSynAimingProgress();
                float fov = WORLD_FOV_DYNAMICS.update((float) MathUtil.magnificationToFov(1 + (zoom - 1) * aimingProgress, event.getFOV()));
                event.setFOV(fov);
            }
        }
    }

    public static void applyGunModelFovModifying(ViewportEvent.ComputeFov event) {
        if (event.usedConfiguredFov()) {
            return; // only modifies the fov of the hand item, so if it's a world rendering fov event, it returns the
        }
        Entity entity = event.getCamera().getFocusedEntity();
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();
            if (!(stack.getItem() instanceof IGun iGun)) {
                float fov = ITEM_MODEL_FOV_DYNAMICS.update((float) event.getFOV());
                event.setFOV(fov);
                return;
            }
            Identifier scopeItemId = iGun.getAttachmentId(stack, AttachmentType.SCOPE);
            if (DefaultAssets.isEmptyAttachmentId(scopeItemId)) {
                float fov = ITEM_MODEL_FOV_DYNAMICS.update((float) event.getFOV());
                event.setFOV(fov);
                return;
            }
            float modifiedFov = TimelessAPI.getClientAttachmentIndex(scopeItemId).map(ClientAttachmentIndex::getFov).orElse((float) event.getFOV());
            if (livingEntity instanceof ClientPlayerEntity localPlayer) {
                IClientPlayerGunOperator gunOperator = IClientPlayerGunOperator.fromLocalPlayer(localPlayer);
                float aimingProgress = gunOperator.getClientAimingProgress((float) event.getPartialTick());
                float fov = ITEM_MODEL_FOV_DYNAMICS.update(MathHelper.lerp(aimingProgress, (float) event.getFOV(), modifiedFov));
                event.setFOV(fov);
            } else {
                IGunOperator gunOperator = IGunOperator.fromLivingEntity(livingEntity);
                float aimingProgress = gunOperator.getSynAimingProgress();
                float fov = ITEM_MODEL_FOV_DYNAMICS.update(MathHelper.lerp(aimingProgress, (float) event.getFOV(), modifiedFov));
                event.setFOV(fov);
            }
        }
    }

    public static void initialCameraRecoil(GunFireEvent event) {
        if (event.getLogicalSide().isClient()) {
            LivingEntity shooter = event.getShooter();
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (!shooter.equals(player)) {
                return;
            }
            ItemStack mainhandItem = player.getMainHandStack();
            if (!(mainhandItem.getItem() instanceof IGun iGun)) {
                return;
            }
            Identifier gunId = iGun.getGunId(mainhandItem);
            Optional<ClientGunIndex> gunIndexOptional = TimelessAPI.getClientGunIndex(gunId);
            if (gunIndexOptional.isEmpty()) {
                return;
            }
            ClientGunIndex gunIndex = gunIndexOptional.get();
            GunData gunData = gunIndex.getGunData();
            // 获取所有配件对摄像机后坐力的修改
            final float[] attachmentRecoilModifier = new float[]{0f, 0f};
            AttachmentDataUtils.getAllAttachmentData(mainhandItem, gunData, attachmentData -> {
                RecoilModifier recoilModifier = attachmentData.getRecoilModifier();
                if (recoilModifier == null) {
                    return;
                }
                attachmentRecoilModifier[0] += recoilModifier.getPitch();
                attachmentRecoilModifier[1] += recoilModifier.getYaw();
            });
            IClientPlayerGunOperator clientPlayerGunOperator = IClientPlayerGunOperator.fromLocalPlayer(player);
            float partialTicks = MinecraftClient.getInstance().getTickDelta();
            float aimingProgress = clientPlayerGunOperator.getClientAimingProgress(partialTicks);
            float zoom = iGun.getAimingZoom(mainhandItem);
            float aimingRecoilModifier = 1 - aimingProgress + aimingProgress / (float) Math.sqrt(zoom);
            pitchSplineFunction = gunData.getRecoil().genPitchSplineFunction(modifierNumber(attachmentRecoilModifier[0]) * aimingRecoilModifier);
            yawSplineFunction = gunData.getRecoil().genYawSplineFunction(modifierNumber(attachmentRecoilModifier[1]) * aimingRecoilModifier);
            shootTimeStamp = System.currentTimeMillis();
            xRotO = 0;
        }
    }

    public static void applyCameraRecoil(ViewportEvent.ComputeCameraAngles ignoredEvent) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        long timeTotal = System.currentTimeMillis() - shootTimeStamp;
        if (pitchSplineFunction != null && pitchSplineFunction.isValidPoint(timeTotal)) {
            double value = pitchSplineFunction.value(timeTotal);
            float setPitch = player.getPitch() - (float) (value - xRotO);
            if (PerspectiveModCompat.getPerspectiveEnabled() && setPitch < -90f) {
                setPitch = -90f;
            }
            player.setPitch(setPitch);
            xRotO = value;
        }
        if (yawSplineFunction != null && yawSplineFunction.isValidPoint(timeTotal)) {
            double value = yawSplineFunction.value(timeTotal);
            player.setYaw(player.getYaw() - (float) (value - yRot0));
            yRot0 = value;
        }
    }

    private static float modifierNumber(float modifier) {
        return Math.max(0, 1 + modifier);
    }
}
