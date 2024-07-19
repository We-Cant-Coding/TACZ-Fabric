package com.tacz.guns.compat.cloth.common;

import com.google.common.collect.Lists;
import com.tacz.guns.config.common.AmmoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class AmmoClothConfig {
    public static void init(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory ammo = root.getOrCreateCategory(Text.translatable("config.tacz-fabric.common.ammo"));

        ammo.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_destroys_blocks"), AmmoConfig.EXPLOSIVE_AMMO_DESTROYS_BLOCKS.get())
                .setDefaultValue(false).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_destroys_blocks.desc"))
                .setSaveConsumer(AmmoConfig.EXPLOSIVE_AMMO_DESTROYS_BLOCKS::set).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_fire"), AmmoConfig.EXPLOSIVE_AMMO_FIRE.get())
                .setDefaultValue(false).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_fire.desc"))
                .setSaveConsumer(AmmoConfig.EXPLOSIVE_AMMO_FIRE::set).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_knock_back"), AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_knock_back.desc"))
                .setSaveConsumer(AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK::set).build());

        ammo.addEntry(entryBuilder.startIntField(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_visible_distance"), AmmoConfig.EXPLOSIVE_AMMO_VISIBLE_DISTANCE.get())
                .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(192).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.explosive_ammo_visible_distance.desc"))
                .setSaveConsumer(AmmoConfig.EXPLOSIVE_AMMO_VISIBLE_DISTANCE::set).build());

        ammo.addEntry(entryBuilder.startStrList(Text.translatable("config.tacz-fabric.common.ammo.pass_through_blocks"), AmmoConfig.PASS_THROUGH_BLOCKS.get())
                .setDefaultValue(Lists.newArrayList()).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.pass_through_blocks.desc"))
                .setSaveConsumer(AmmoConfig.PASS_THROUGH_BLOCKS::set).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.common.ammo.destroy_glass"), AmmoConfig.DESTROY_GLASS.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.destroy_glass.desc"))
                .setSaveConsumer(AmmoConfig.DESTROY_GLASS::set).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.common.ammo.ignite_block"), AmmoConfig.IGNITE_BLOCK.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.ignite_block.desc"))
                .setSaveConsumer(AmmoConfig.IGNITE_BLOCK::set).build());

        ammo.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.common.ammo.ignite_entity"), AmmoConfig.IGNITE_ENTITY.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.common.ammo.ignite_entity.desc"))
                .setSaveConsumer(AmmoConfig.IGNITE_ENTITY::set).build());
    }
}
