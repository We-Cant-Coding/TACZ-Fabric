package com.tacz.guns.network.message.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ServerMessageGunHurt implements FabricPacket {
    public static final PacketType<ServerMessageGunHurt> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_gun_hurt"), ServerMessageGunHurt::new);

    private final int hurtEntityId;
    private final int attackerId;
    private final Identifier gunId;
    private final float amount;
    private final boolean isHeadShot;
    private final float headshotMultiplier;

    public ServerMessageGunHurt(PacketByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readIdentifier(), buf.readFloat(), buf.readBoolean(), buf.readFloat());
    }

    public ServerMessageGunHurt(int hurtEntityId, int attackerId, Identifier gunId, float amount, boolean isHeadShot, float headshotMultiplier) {
        this.hurtEntityId = hurtEntityId;
        this.attackerId = attackerId;
        this.gunId = gunId;
        this.amount = amount;
        this.isHeadShot = isHeadShot;
        this.headshotMultiplier = headshotMultiplier;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(hurtEntityId);
        buf.writeInt(attackerId);
        buf.writeIdentifier(gunId);
        buf.writeFloat(amount);
        buf.writeBoolean(isHeadShot);
        buf.writeFloat(headshotMultiplier);
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
    private static void doClientEvent(ServerMessageGunHurt message) {
        ClientWorld level = MinecraftClient.getInstance().world;
        if (level == null) {
            return;
        }
        @Nullable Entity hurtEntity = level.getEntityById(message.hurtEntityId);
        @Nullable LivingEntity attacker = level.getEntityById(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        new EntityHurtByGunEvent.Post(hurtEntity, attacker, message.gunId, message.amount, message.isHeadShot, message.headshotMultiplier, LogicalSide.CLIENT).post();
    }
}
