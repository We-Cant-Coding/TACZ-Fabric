package com.tacz.guns.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ProgressListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GunPackProgressScreen extends Screen implements ProgressListener {
    private @Nullable Text header;
    private @Nullable Text stage;
    private int progress;
    private boolean stop;

    public GunPackProgressScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    protected void init() {
        ButtonWidget button = ButtonWidget.builder(
                Text.translatable("gui.tacz.client_gun_pack_downloader.background_download"), b -> this.setDone()
        ).dimensions((width - 200) / 2, 120, 200, 20).build();
        this.addDrawableChild(button);
    }

    @Override
    public void render(@NotNull DrawContext gui, int mouseX, int mouseY, float partialTick) {
        if (this.stop) {
            this.client.setScreen(null);
        } else {
            this.renderBackground(gui);
            if (this.header != null) {
                gui.drawCenteredTextWithShadow(this.textRenderer, this.header, this.width / 2, 70, 16777215);
            }
            if (this.stage != null && this.progress > 0) {
                MutableText text = this.stage.copy().append(" " + this.progress + "%");
                gui.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, 90, 16777215);
            }
            super.render(gui, mouseX, mouseY, partialTick);
        }
    }


    @Override
    public void setTitle(Text component) {
        this.setTitleAndTask(component);
    }

    @Override
    public void setTitleAndTask(Text header) {
        this.header = Text.translatable("gui.tacz.client_gun_pack_downloader.downloading");
    }

    @Override
    public void setTask(Text component) {
        this.stage = component;
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int progress) {
        this.progress = progress;
    }

    @Override
    public void setDone() {
        this.stop = true;
    }
}
