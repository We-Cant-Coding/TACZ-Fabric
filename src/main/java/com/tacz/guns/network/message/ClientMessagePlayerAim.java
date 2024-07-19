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

public class ClientMessagePlayerAim implements FabricPacket {
    public static final PacketType<ClientMessagePlayerAim> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "client_message_player_aim"), ClientMessagePlayerAim::new);

    private final boolean isAim;

    public ClientMessagePlayerAim(PacketByteBuf buf) {
        this(buf.readBoolean());
    }

    public ClientMessagePlayerAim(boolean isAim) {
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
