package com.tacz.guns.network.packets.s2c.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class GunDrawS2CPacket implements FabricPacket {
    public static final PacketType<GunDrawS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "gun_draw"), GunDrawS2CPacket::new);

    private final int entityId;
    private final ItemStack previousGunItem;
    private final ItemStack currentGunItem;

    public GunDrawS2CPacket(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readItemStack(), buf.readItemStack());
    }

    public GunDrawS2CPacket(int entityId, ItemStack previousGunItem, ItemStack currentGunItem) {
        this.entityId = entityId;
        this.previousGunItem = previousGunItem;
        this.currentGunItem = currentGunItem;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeItemStack(previousGunItem);
        buf.writeItemStack(currentGunItem);
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            doClientEvent(this);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    private static void doClientEvent(GunDrawS2CPacket message) {
        ClientWorld level = MinecraftClient.getInstance().world;
        if (level == null) {
            return;
        }
        if (level.getEntityById(message.entityId) instanceof LivingEntity livingEntity) {
            GunDrawEvent gunDrawEvent = new GunDrawEvent(livingEntity, message.previousGunItem, message.currentGunItem, LogicalSide.CLIENT);
            gunDrawEvent.post();
        }
    }
}
