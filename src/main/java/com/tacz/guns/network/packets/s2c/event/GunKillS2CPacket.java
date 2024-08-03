package com.tacz.guns.network.packets.s2c.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GunKillS2CPacket implements FabricPacket {
    public static final PacketType<GunKillS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "gun_kill"), GunKillS2CPacket::new);

    private final int killEntityId;
    private final int attackerId;
    private final Identifier gunId;
    private final boolean isHeadShot;

    public GunKillS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readIdentifier(), buf.readBoolean());
    }

    public GunKillS2CPacket(int killEntityId, int attackerId, Identifier gunId, boolean isHeadShot) {
        this.killEntityId = killEntityId;
        this.attackerId = attackerId;
        this.gunId = gunId;
        this.isHeadShot = isHeadShot;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(killEntityId);
        buf.writeInt(attackerId);
        buf.writeIdentifier(gunId);
        buf.writeBoolean(isHeadShot);
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
    private static void doClientEvent(GunKillS2CPacket message) {
        ClientWorld level = MinecraftClient.getInstance().world;
        if (level == null) {
            return;
        }
        @Nullable LivingEntity killedEntity = level.getEntityById(message.killEntityId) instanceof LivingEntity livingEntity ? livingEntity : null;
        @Nullable LivingEntity attacker = level.getEntityById(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        new EntityKillByGunEvent(killedEntity, attacker, message.gunId, message.isHeadShot, LogicalSide.CLIENT).post();
    }
}
