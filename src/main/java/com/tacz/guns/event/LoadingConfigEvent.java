package com.tacz.guns.event;

import com.tacz.guns.client.download.ClientGunPackDownloadManager;
import com.tacz.guns.config.util.HeadShotAABBConfigRead;
import com.tacz.guns.config.util.InteractKeyConfigRead;
import com.tacz.guns.util.EnvironmentUtil;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;

public class LoadingConfigEvent implements ModConfigEvents.Loading, ModConfigEvents.Reloading {
    private static final String CONFIG_NAME = "tacz-server.toml";

    @Override
    public void onModConfigLoading(ModConfig config) {
        String fileName = config.getFileName();
        if (CONFIG_NAME.equals(fileName)) {
            HeadShotAABBConfigRead.init();
            InteractKeyConfigRead.init();
        }
    }

    @Override
    public void onModConfigReloading(ModConfig config) {
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
