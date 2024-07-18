package com.tacz.guns.mixin.client;

import com.tacz.guns.api.client.other.KeepingItemRenderer;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    public ModelPart leftSleeve;
    @Shadow
    @Final
    public ModelPart rightSleeve;


    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void setRotationAnglesTail(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entityIn instanceof PlayerEntity)) {
            return;
        }

        // 用于清除默认的手臂旋转
        // 当第一人称渲染是，ageInTicks 正好是 0
        ItemStack currentItem = KeepingItemRenderer.getRenderer().getCurrentItem();
        if (ageInTicks == 0F && IGun.getIGunOrNull(currentItem) != null) {
            tacz$resetAll(This().rightArm);
            tacz$resetAll(This().leftArm);
            rightSleeve.copyTransform(This().rightArm);
            leftSleeve.copyTransform(This().leftArm);
        }
    }

    /**
     * 将给定模型的旋转角度和旋转点重置为零
     */
    @Unique
    private void tacz$resetAll(ModelPart part) {
        part.pitch = 0.0F;
        part.yaw = 0.0F;
        part.roll = 0.0F;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private PlayerEntityModel<T> This() {
        return (PlayerEntityModel<T>) (Object) this;
    }
}
