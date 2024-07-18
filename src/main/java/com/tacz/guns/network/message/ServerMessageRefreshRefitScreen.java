package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ServerMessageRefreshRefitScreen implements FabricPacket {
    public static final PacketType<ServerMessageRefreshRefitScreen> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_refresh_refit_screen"), ServerMessageRefreshRefitScreen::new);

    public ServerMessageRefreshRefitScreen(PacketByteBuf buf) {
        this();
    }

    public ServerMessageRefreshRefitScreen() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            updateScreen();
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    private static void updateScreen() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && MinecraftClient.getInstance().currentScreen instanceof GunRefitScreen screen) {
            screen.init();
        }
    }
}
