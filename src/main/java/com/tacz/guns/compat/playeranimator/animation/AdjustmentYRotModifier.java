package com.tacz.guns.compat.playeranimator.animation;

import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.function.Function;

public class AdjustmentYRotModifier implements Function<String, Optional<AdjustmentModifier.PartModifier>> {
    private final PlayerEntity player;

    private AdjustmentYRotModifier(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public Optional<AdjustmentModifier.PartModifier> apply(String partName) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (player.equals(mc.player) && mc.currentScreen != null) {
            return Optional.empty();
        }

        if (player.getVehicle() != null && "body".equals(partName)) {
            return Optional.empty();
        }

        float partialTick = mc.getTickDelta();
        float yBodyRot = MathHelper.lerpAngleDegrees(partialTick, player.prevBodyYaw, player.bodyYaw);
        float yHeadRot = MathHelper.lerpAngleDegrees(partialTick, player.prevHeadYaw, player.headYaw);
        float xRot = MathHelper.lerp(partialTick, player.prevPitch, player.getPitch());

        float yaw = yHeadRot - yBodyRot;
        yaw = MathHelper.wrapDegrees(yaw);
        yaw = MathHelper.clamp(yaw, -85f, 85f);

        float pitch = MathHelper.wrapDegrees(xRot);

        return switch (partName) {
            case "body" -> Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(0, -yaw * MathHelper.RADIANS_PER_DEGREE, 0), Vec3f.ZERO));
            case "head", "leftArm", "rightArm" -> Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(pitch * MathHelper.RADIANS_PER_DEGREE, 0, 0), Vec3f.ZERO));
            default -> Optional.empty();
        };
    }

    public static AdjustmentModifier getModifier(PlayerEntity player) {
        return new AdjustmentModifier(new AdjustmentYRotModifier(player));
    }
}
