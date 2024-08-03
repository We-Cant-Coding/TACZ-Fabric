package com.tacz.guns.network.packets.c2s;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerShootC2SPacket implements FabricPacket {
    public static final PacketType<PlayerShootC2SPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "player_shoot"), PlayerShootC2SPacket::new);

    public PlayerShootC2SPacket(PacketByteBuf buf) {
        this();
    }

    public PlayerShootC2SPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player == null) return;
        IGunOperator.fromLivingEntity(player).shoot(player::getPitch, player::getYaw);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
