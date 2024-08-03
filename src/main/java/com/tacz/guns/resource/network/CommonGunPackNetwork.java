package com.tacz.guns.resource.network;

import com.google.common.collect.Maps;
import com.tacz.guns.client.resource.ClientGunPackLoader;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.packets.s2c.SyncGunPackS2CPacket;
import com.tacz.guns.resource.loader.asset.*;
import com.tacz.guns.resource.loader.index.CommonAmmoIndexLoader;
import com.tacz.guns.resource.loader.index.CommonAttachmentIndexLoader;
import com.tacz.guns.resource.loader.index.CommonGunIndexLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CommonGunPackNetwork {
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private static final EnumMap<DataType, Map<Identifier, String>> NETWORK_CACHE = new EnumMap<>(DataType.class);

    public static void clear() {
        NETWORK_CACHE.clear();
    }

    public static void addData(DataType type, Identifier id, String json) {
        NETWORK_CACHE.computeIfAbsent(type, k -> Maps.newHashMap()).put(id, json);
    }

    public static void syncClient(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(player -> NetworkHandler.sendToClientPlayer(
                new SyncGunPackS2CPacket(NETWORK_CACHE), player));
    }

    public static void syncClientExceptSelf(MinecraftServer server, @Nullable PlayerEntity self) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (!player.equals(self)) {
                SyncGunPackS2CPacket message = new SyncGunPackS2CPacket(NETWORK_CACHE);
                NetworkHandler.sendToClientPlayer(message, player);
            }
        });
    }

    public static void syncClient(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SyncGunPackS2CPacket(NETWORK_CACHE));
    }

    public static void toNetwork(EnumMap<DataType, Map<Identifier, String>> cache, PacketByteBuf buf) {
        buf.writeVarInt(cache.size());
        cache.forEach((type, caches) -> {
            buf.writeEnumConstant(type);
            buf.writeVarInt(caches.size());
            caches.forEach((id, data) -> {
                buf.writeIdentifier(id);
                buf.writeString(data);
            });
        });
    }

    public static EnumMap<DataType, Map<Identifier, String>> fromNetworkCache(PacketByteBuf buf) {
        EnumMap<DataType, Map<Identifier, String>> cache = Maps.newEnumMap(DataType.class);
        int typeSize = buf.readVarInt();
        for (int i = 0; i < typeSize; i++) {
            DataType type = buf.readEnumConstant(DataType.class);
            int size = buf.readVarInt();
            for (int j = 0; j < size; j++) {
                Identifier id = buf.readIdentifier();
                String json = buf.readString();
                cache.computeIfAbsent(type, k -> Maps.newHashMap()).put(id, json);
            }
        }
        return cache;
    }

    /**
     * 原则上来说，这个方法应该只允许客户端调用<br>
     * <br>
     * 1) 玩家进服时调用 <br>
     * 2) 服务器使用重载指令时，发送到玩家客户端调用<br>
     * 3) 服务器玩家重载自己客户端资源时调用<br>
     */
    @Environment(EnvType.CLIENT)
    public static void loadFromCache(EnumMap<DataType, Map<Identifier, String>> allCache) {
        // 这个更新是增量式的更新
        // 玩家安装了服务端没有的枪械包，也会显示，但无法使用
        allCache.forEach((type, cache) -> cache.forEach((id, json) -> {
            switch (type) {
                case GUN_DATA -> GunDataLoader.loadFromJsonString(id, json);
                case ATTACHMENT_DATA -> AttachmentDataLoader.loadFromJsonString(id, json);
                case AMMO_INDEX -> CommonAmmoIndexLoader.loadAmmoFromJsonString(id, json);
                case GUN_INDEX -> CommonGunIndexLoader.loadGunFromJsonString(id, json);
                case ATTACHMENT_INDEX -> CommonAttachmentIndexLoader.loadAttachmentFromJsonString(id, json);
                case RECIPES -> RecipeLoader.loadFromJsonString(id, json);
                case ATTACHMENT_TAGS -> AttachmentTagsLoader.loadFromJsonString(id, json);
                case ALLOW_ATTACHMENT_TAGS -> AllowAttachmentTagsLoader.loadFromJsonString(id, json);
            }
        }));
        ClientGunPackLoader.reloadIndex();
    }
}
