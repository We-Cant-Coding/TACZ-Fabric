package com.tacz.guns.network.packets.s2c;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SwapItemS2CPacket implements FabricPacket {
    public static final PacketType<SwapItemS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "swap_item"), SwapItemS2CPacket::new);

    public SwapItemS2CPacket(PacketByteBuf buf) {
        this();
    }

    public SwapItemS2CPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            new SwapItemWithOffHand().post();
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
