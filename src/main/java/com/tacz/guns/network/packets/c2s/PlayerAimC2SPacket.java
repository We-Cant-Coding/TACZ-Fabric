package com.tacz.guns.network.packets.c2s;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerAimC2SPacket implements FabricPacket {
    public static final PacketType<PlayerAimC2SPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "player_aim"), PlayerAimC2SPacket::new);

    private final boolean isAim;

    public PlayerAimC2SPacket(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    public PlayerAimC2SPacket(boolean isAim) {
        this.isAim = isAim;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(isAim);
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player == null) return;
        IGunOperator.fromLivingEntity(player).aim(isAim);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
