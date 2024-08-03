package com.tacz.guns.network.packets.c2s;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerMeleeC2SPacket implements FabricPacket {
    public static final PacketType<PlayerMeleeC2SPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "player_melee"), PlayerMeleeC2SPacket::new);

    public PlayerMeleeC2SPacket(PacketByteBuf buf) {
        this();
    }

    public PlayerMeleeC2SPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player == null) return;
        IGunOperator.fromLivingEntity(player).melee();
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
