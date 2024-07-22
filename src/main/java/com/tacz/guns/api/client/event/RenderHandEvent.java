package com.tacz.guns.api.client.event;

import com.tacz.guns.api.event.GunBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class RenderHandEvent extends GunBaseEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks) e.onRenderHand(event);
    });

    private final Hand hand;
    private final MatrixStack poseStack;
    private final VertexConsumerProvider multiBufferSource;
    private final int packedLight;
    private final float partialTick;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    private final ItemStack stack;

    public RenderHandEvent(Hand hand, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int packedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress, ItemStack stack) {
        this.hand = hand;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    public Hand getHand() {
        return this.hand;
    }

    public MatrixStack getPoseStack() {
        return this.poseStack;
    }

    public VertexConsumerProvider getMultiBufferSource() {
        return this.multiBufferSource;
    }

    public int getPackedLight() {
        return this.packedLight;
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public float getInterpolatedPitch() {
        return this.interpolatedPitch;
    }

    public float getSwingProgress() {
        return this.swingProgress;
    }

    public float getEquipProgress() {
        return this.equipProgress;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }

    @Override
    public void sendEvent() {
        EVENT.invoker().onRenderHand(this);
    }

    public interface Callback {
        void onRenderHand(RenderHandEvent event);
    }
}