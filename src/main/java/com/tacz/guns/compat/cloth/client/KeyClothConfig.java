package com.tacz.guns.compat.cloth.client;

import com.tacz.guns.config.client.KeyConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class KeyClothConfig {
    public static void init(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory key = root.getOrCreateCategory(Text.translatable("config.tacz-fabric.client.key"));

        key.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.client.key.hold_to_aim"), KeyConfig.HOLD_TO_AIM.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.client.key.hold_to_aim.desc"))
                .setSaveConsumer(KeyConfig.HOLD_TO_AIM::set).build());
    }
}
