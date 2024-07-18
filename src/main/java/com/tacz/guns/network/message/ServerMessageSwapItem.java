package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ServerMessageSwapItem implements FabricPacket {
    public static final PacketType<ServerMessageSwapItem> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_swap_item"), ServerMessageSwapItem::new);

    public ServerMessageSwapItem(PacketByteBuf buf) {
        this();
    }

    public ServerMessageSwapItem() {
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
