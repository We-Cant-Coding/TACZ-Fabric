package com.tacz.guns.client.gui.compat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

public class ClothConfigScreen extends Screen {
    public static final String CLOTH_CONFIG_URL = "https://modrinth.com/mod/cloth-config";
    private final Screen lastScreen;
    private MultilineText message = MultilineText.EMPTY;

    public ClothConfigScreen(Screen lastScreen) {
        super(Text.literal("Cloth Config API"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int posX = (this.width - 200) / 2;
        int posY = this.height / 2;
        this.message = MultilineText.create(this.textRenderer, Text.translatable("gui.tacz-fabric.cloth_config_warning.tips"), 300);
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("gui.tacz-fabric.cloth_config_warning.download"), b -> openUrl(CLOTH_CONFIG_URL))
                        .dimensions(posX, posY - 15, 200, 20).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.BACK, b -> MinecraftClient.getInstance().setScreen(this.lastScreen))
                        .dimensions(posX, posY + 50, 200, 20).build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        message.drawCenterWithShadow(context, width / 2, 80);
        super.render(context, mouseX, mouseY, delta);
    }

    private void openUrl(String url) {
        if (StringUtils.isNotBlank(url) && client != null) {
            client.setScreen(new ConfirmLinkScreen(yes -> {
                if (yes) {
                    Util.getOperatingSystem().open(url);
                }
                client.setScreen(this);
            }, url, true));
        }
    }
}
