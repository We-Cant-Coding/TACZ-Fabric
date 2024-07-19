package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ClientMessagePlayerBoltGun implements FabricPacket {
    public static final PacketType<ClientMessagePlayerBoltGun> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "client_message_player_bolt_gun"), ClientMessagePlayerBoltGun::new);

    public ClientMessagePlayerBoltGun(PacketByteBuf buf) {
        this();
    }

    public ClientMessagePlayerBoltGun() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player == null) return;
        IGunOperator.fromLivingEntity(player).bolt();
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
