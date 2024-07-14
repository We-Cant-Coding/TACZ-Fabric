package com.tacz.guns.client.resource;

import com.tacz.guns.client.download.ClientGunPackDownloadManager;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.network.message.ServerMessageSyncGunPack;
import com.tacz.guns.resource.CommonGunPackLoader;
import com.tacz.guns.resource.network.CommonGunPackNetwork;
import com.tacz.guns.resource.network.DataType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import static com.tacz.guns.config.ServerConfig.SERVER_CONFIG_SPEC;

public class ClientReloadManager {
    private static final EnumMap<DataType, Map<Identifier, String>> LOCALE_CACHE = new EnumMap<>(DataType.class);

    public static void reloadAllPack() {
        ClientGunPackLoader.init();
        // 先加载全部资源文件
        CommonGunPackLoader.reloadAsset();
        ClientGunPackLoader.reloadAsset();
        // 再加载定义文件
        // 先加载服务端，让其校验数据
        CommonGunPackLoader.reloadIndex();
        ClientGunPackLoader.reloadIndex();
        // 合成表
        CommonGunPackLoader.reloadRecipes();
        // 联机 / 非联机情况判断
        MinecraftClient mc = MinecraftClient.getInstance();
        // 局域网联机（自己是主机），需要给其他玩家发送自己的同步数据
        // 不需要给自己发
        if (mc.isIntegratedServerRunning() && mc.getServer() != null && mc.getServer().isRemote()) {
            CommonGunPackNetwork.syncClientExceptSelf(mc.getServer(), MinecraftClient.getInstance().player);
            return;
        }
        // 多人游戏，自己是客户端，则需要主动加载服务端缓存数据
        if (!mc.isInSingleplayer()) {
            if (SERVER_CONFIG_SPEC != null && SERVER_CONFIG_SPEC.isLoaded() && !SyncConfig.CLIENT_GUN_PACK_DOWNLOAD_URLS.get().isEmpty()) {
                ClientGunPackDownloadManager.downloadClientGunPack();
            }
            if (!LOCALE_CACHE.isEmpty()) {
                CommonGunPackNetwork.loadFromCache(LOCALE_CACHE);
            }
        }
    }

    public static void loadClientDownloadGunPack(File file) {
        ClientGunPackLoader.readZipAsset(file);
        if (!LOCALE_CACHE.isEmpty()) {
            CommonGunPackNetwork.loadFromCache(LOCALE_CACHE);
        }
    }

    public static void cacheAll(ServerMessageSyncGunPack message) {
        LOCALE_CACHE.clear();
        LOCALE_CACHE.putAll(message.getCache());
    }
}
