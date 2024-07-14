package com.tacz.guns.compat.modmenu;

import com.tacz.guns.client.gui.compat.ClothConfigScreen;
import com.tacz.guns.compat.cloth.MenuIntegration;
import com.tacz.guns.init.CompatRegistry;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> {
            if (FabricLoader.getInstance().isModLoaded(CompatRegistry.CLOTH_CONFIG)) {
                return MenuIntegration.getConfigScreen(parent);
            } else {
                return new ClothConfigScreen(parent);
            }
        };
    }
}
