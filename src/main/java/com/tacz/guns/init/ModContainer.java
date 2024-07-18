package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModContainer {
    public static final ScreenHandlerType<GunSmithTableMenu> GUN_SMITH_TABLE_MENU = Registry.register(Registries.SCREEN_HANDLER, new Identifier(GunMod.MOD_ID, "gun_smith_table_menu"), GunSmithTableMenu.TYPE);

    public static void init() {
    }
}
