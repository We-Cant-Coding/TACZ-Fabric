package com.tacz.guns.compat.cloth.client;

import com.tacz.guns.client.renderer.crosshair.CrosshairType;
import com.tacz.guns.compat.cloth.widget.CrosshairDropdown;
import com.tacz.guns.config.client.RenderConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class RenderClothConfig {
    public static void init(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory render = root.getOrCreateCategory(Text.translatable("config.tacz-fabric.client.render"));

        render.addEntry(entryBuilder.startIntField(Text.translatable("config.tacz-fabric.client.render.gun_lod_render_distance"), RenderConfig.GUN_LOD_RENDER_DISTANCE.get())
                .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(0).setTooltip(Text.translatable("config.tacz-fabric.client.render.gun_lod_render_distance.desc"))
                .setSaveConsumer(RenderConfig.GUN_LOD_RENDER_DISTANCE::set).build());

        render.addEntry(entryBuilder.startIntField(Text.translatable("config.tacz-fabric.client.render.bullet_hole_particle_life"), RenderConfig.BULLET_HOLE_PARTICLE_LIFE.get())
                .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(400).setTooltip(Text.translatable("config.tacz-fabric.client.render.bullet_hole_particle_life.desc"))
                .setSaveConsumer(RenderConfig.BULLET_HOLE_PARTICLE_LIFE::set).build());

        render.addEntry(entryBuilder.startDoubleField(Text.translatable("config.tacz-fabric.client.render.bullet_hole_particle_fade_threshold"), RenderConfig.BULLET_HOLE_PARTICLE_FADE_THRESHOLD.get())
                .setMin(0).setMax(1).setDefaultValue(0.98).setTooltip(Text.translatable("config.tacz-fabric.client.render.bullet_hole_particle_fade_threshold.desc"))
                .setSaveConsumer(RenderConfig.BULLET_HOLE_PARTICLE_FADE_THRESHOLD::set).build());

        render.addEntry(entryBuilder.startDropdownMenu(Text.translatable("config.tacz-fabric.client.render.crosshair_type"),
                        CrosshairDropdown.of(RenderConfig.CROSSHAIR_TYPE.get()), CrosshairDropdown.of())
                .setSelections(Arrays.stream(CrosshairType.values()).sorted().sorted(Comparator.comparing(CrosshairType::name)).collect(Collectors.toCollection(LinkedHashSet::new)))
                .setDefaultValue(CrosshairType.DOT_1).setTooltip(Text.translatable("config.tacz-fabric.client.render.crosshair_type.desc"))
                .setSaveConsumer(RenderConfig.CROSSHAIR_TYPE::set).build());

        render.addEntry(entryBuilder.startDoubleField(Text.translatable("config.tacz-fabric.client.render.hit_market_start_position"), RenderConfig.HIT_MARKET_START_POSITION.get())
                .setMin(-1024).setMax(1024).setDefaultValue(4).setTooltip(Text.translatable("config.tacz-fabric.client.render.hit_market_start_position.desc"))
                .setSaveConsumer(RenderConfig.HIT_MARKET_START_POSITION::set).build());

        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.client.render.head_shot_debug_hitbox"), RenderConfig.HEAD_SHOT_DEBUG_HITBOX.get())
                .setDefaultValue(false).setTooltip(Text.translatable("config.tacz-fabric.client.render.head_shot_debug_hitbox.desc"))
                .setSaveConsumer(RenderConfig.HEAD_SHOT_DEBUG_HITBOX::set).build());

        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.client.render.gun_hud_enable"), RenderConfig.GUN_HUD_ENABLE.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.client.render.gun_hud_enable.desc"))
                .setSaveConsumer(RenderConfig.GUN_HUD_ENABLE::set).build());

        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.client.render.kill_amount_enable"), RenderConfig.KILL_AMOUNT_ENABLE.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.client.render.kill_amount_enable.desc"))
                .setSaveConsumer(RenderConfig.KILL_AMOUNT_ENABLE::set).build());

        render.addEntry(entryBuilder.startDoubleField(Text.translatable("config.tacz-fabric.client.render.kill_amount_duration_second"), RenderConfig.KILL_AMOUNT_DURATION_SECOND.get())
                .setMin(0).setMax(Double.MAX_VALUE).setDefaultValue(3).setTooltip(Text.translatable("config.tacz-fabric.client.render.kill_amount_duration_second.desc"))
                .setSaveConsumer(RenderConfig.KILL_AMOUNT_DURATION_SECOND::set).build());

        render.addEntry(entryBuilder.startIntField(Text.translatable("config.tacz-fabric.client.render.target_render_distance"), RenderConfig.TARGET_RENDER_DISTANCE.get())
                .setMin(0).setMax(Integer.MAX_VALUE).setDefaultValue(128).setTooltip(Text.translatable("config.tacz-fabric.client.render.target_render_distance.desc"))
                .setSaveConsumer(RenderConfig.TARGET_RENDER_DISTANCE::set).build());

        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.client.render.first_person_bullet_tracer_enable"), RenderConfig.FIRST_PERSON_BULLET_TRACER_ENABLE.get())
                .setDefaultValue(true).setTooltip(Text.translatable("config.tacz-fabric.client.render.first_person_bullet_tracer_enable.desc"))
                .setSaveConsumer(RenderConfig.FIRST_PERSON_BULLET_TRACER_ENABLE::set).build());

        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.tacz-fabric.client.render.disable_interact_hud_text"), RenderConfig.DISABLE_INTERACT_HUD_TEXT.get())
                .setDefaultValue(false).setTooltip(Text.translatable("config.tacz-fabric.client.render.disable_interact_hud_text.desc"))
                .setSaveConsumer(RenderConfig.DISABLE_INTERACT_HUD_TEXT::set).build());

        render.addEntry(entryBuilder.startIntField(Text.translatable("config.tacz-fabric.client.render.damage_counter_reset_time"), RenderConfig.DAMAGE_COUNTER_RESET_TIME.get())
                .setMin(10).setMax(Integer.MAX_VALUE).setDefaultValue(2000).setTooltip(Text.translatable("config.tacz-fabric.client.render.damage_counter_reset_time.desc"))
                .setSaveConsumer(RenderConfig.DAMAGE_COUNTER_RESET_TIME::set).build());
    }
}
