package com.tacz.guns.compat.cloth.common;

import com.tacz.guns.config.common.OtherConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class OtherClothConfig {
    public static void init(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory other = root.getOrCreateCategory(Text.translatable("config.tacz.common.other"));

        other.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz.common.other.default_pack_debug"), OtherConfig.DEFAULT_PACK_DEBUG.get())
                .setDefaultValue(false).setTooltip(Text.translatable("config.tacz.common.other.default_pack_debug.desc"))
                .setSaveConsumer(OtherConfig.DEFAULT_PACK_DEBUG::set).build());

        other.addEntry(entryBuilder.startIntField(Text.translatable("config.tacz.common.other.target_sound_distance"), OtherConfig.TARGET_SOUND_DISTANCE.get())
                .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(128).setTooltip(Text.translatable("config.tacz.common.other.target_sound_distance.desc"))
                .setSaveConsumer(OtherConfig.TARGET_SOUND_DISTANCE::set).build());
    }
}
