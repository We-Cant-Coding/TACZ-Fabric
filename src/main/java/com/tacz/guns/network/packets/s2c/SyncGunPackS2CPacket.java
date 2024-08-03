package com.tacz.guns.network.packets.s2c;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.ClientReloadManager;
import com.tacz.guns.resource.network.CommonGunPackNetwork;
import com.tacz.guns.resource.network.DataType;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class SyncGunPackS2CPacket implements FabricPacket {
    public static final PacketType<SyncGunPackS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "sync_gun_pack"), SyncGunPackS2CPacket::new);
    private final EnumMap<DataType, Map<Identifier, String>> cache;

    public SyncGunPackS2CPacket(PacketByteBuf buf) {
        this(CommonGunPackNetwork.fromNetworkCache(buf));
    }

    public SyncGunPackS2CPacket(EnumMap<DataType, Map<Identifier, String>> cache) {
        this.cache = cache;
    }

    @Override
    public void write(PacketByteBuf buf) {
        CommonGunPackNetwork.toNetwork(cache, buf);
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            doSync(this);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public EnumMap<DataType, Map<Identifier, String>> getCache() {
        return cache;
    }

    @Environment(EnvType.CLIENT)
    private static void doSync(SyncGunPackS2CPacket message) {
        ClientReloadManager.cacheAll(message);
        ClientReloadManager.reloadAllPack();
    }
}
