package com.tacz.guns.network.packets.c2s;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerDrawGunC2SPacket implements FabricPacket {
    public static final PacketType<PlayerDrawGunC2SPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "player_draw_gun"), PlayerDrawGunC2SPacket::new);

    public PlayerDrawGunC2SPacket(PacketByteBuf buf) {
        this();
    }

    public PlayerDrawGunC2SPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player == null) return;
        PlayerInventory inventory = player.getInventory();
        int selected = inventory.selectedSlot;
        IGunOperator.fromLivingEntity(player).draw(() -> inventory.getStack(selected));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
