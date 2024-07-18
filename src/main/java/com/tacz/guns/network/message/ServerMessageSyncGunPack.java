package com.tacz.guns.network.message;

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

public class ServerMessageSyncGunPack implements FabricPacket {
    public static final PacketType<ServerMessageSyncGunPack> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_sync_gun_pack"), ServerMessageSyncGunPack::new);
    private final EnumMap<DataType, Map<Identifier, String>> cache;

    public ServerMessageSyncGunPack(PacketByteBuf buf) {
        this(CommonGunPackNetwork.fromNetworkCache(buf));
    }

    public ServerMessageSyncGunPack(EnumMap<DataType, Map<Identifier, String>> cache) {
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
    private static void doSync(ServerMessageSyncGunPack message) {
        ClientReloadManager.cacheAll(message);
        ClientReloadManager.reloadAllPack();
    }
}
