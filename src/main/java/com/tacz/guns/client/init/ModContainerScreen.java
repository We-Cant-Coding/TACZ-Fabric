package com.tacz.guns.client.init;

import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModContainerScreen {
    public static void clientSetup() {
        HandledScreens.register(GunSmithTableMenu.TYPE, GunSmithTableScreen::new);
    }
}
