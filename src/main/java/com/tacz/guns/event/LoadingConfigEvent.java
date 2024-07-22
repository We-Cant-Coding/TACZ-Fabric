package com.tacz.guns.event;

import com.tacz.guns.client.download.ClientGunPackDownloadManager;
import com.tacz.guns.config.util.HeadShotAABBConfigRead;
import com.tacz.guns.config.util.InteractKeyConfigRead;
import com.tacz.guns.util.EnvironmentUtil;
import net.minecraftforge.fml.config.ModConfig;

public class LoadingConfigEvent {
    private static final String CONFIG_NAME = "tacz-server.toml";

    public static void onModConfigLoading(ModConfig config) {
        String fileName = config.getFileName();
        if (CONFIG_NAME.equals(fileName)) {
            HeadShotAABBConfigRead.init();
            InteractKeyConfigRead.init();
        }
    }

    public static void onModConfigReloading(ModConfig config) {
        String fileName = config.getFileName();
        if (CONFIG_NAME.equals(fileName)) {
            HeadShotAABBConfigRead.init();
            InteractKeyConfigRead.init();
            if (EnvironmentUtil.isClient()) {
                ClientGunPackDownloadManager.downloadClientGunPack();
            }
        }
    }
}
