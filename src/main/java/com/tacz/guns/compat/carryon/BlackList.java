package com.tacz.guns.compat.carryon;

import com.tacz.guns.GunMod;
import net.minecraft.registry.Registries;
import tschipp.carryon.common.config.ListHandler;

public class BlackList {

    public static void addBlackList() {
        Registries.BLOCK.getIds().stream().filter(id -> id.getNamespace().equals(GunMod.MOD_ID))
                .forEach(id -> ListHandler.addForbiddenTiles(id.toString()));
    }
}
