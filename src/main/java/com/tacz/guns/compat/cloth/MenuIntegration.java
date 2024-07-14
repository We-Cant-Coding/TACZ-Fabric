package com.tacz.guns.compat.cloth;

import com.tacz.guns.compat.cloth.client.KeyClothConfig;
import com.tacz.guns.compat.cloth.client.RenderClothConfig;
import com.tacz.guns.compat.cloth.client.ZoomClothConfig;
import com.tacz.guns.compat.cloth.common.AmmoClothConfig;
import com.tacz.guns.compat.cloth.common.GunClothConfig;
import com.tacz.guns.compat.cloth.common.OtherClothConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MenuIntegration {
    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder root = ConfigBuilder.create().setTitle(Text.literal("Timeless and Classics Guns"));
        root.setGlobalized(true);
        root.setGlobalizedExpanded(false);
        ConfigEntryBuilder entryBuilder = root.entryBuilder();

        KeyClothConfig.init(root, entryBuilder);
        RenderClothConfig.init(root, entryBuilder);
        ZoomClothConfig.init(root, entryBuilder);

        GunClothConfig.init(root, entryBuilder);
        AmmoClothConfig.init(root, entryBuilder);
        OtherClothConfig.init(root, entryBuilder);

        return root;
    }

    public static Screen getConfigScreen(@Nullable Screen parent) {
        return MenuIntegration.getConfigBuilder().setParentScreen(parent).build();
    }
}
