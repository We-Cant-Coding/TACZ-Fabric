package com.tacz.guns.network.packets.s2c;

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

public class RefreshRefitScreenS2CPacket implements FabricPacket {
    public static final PacketType<RefreshRefitScreenS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "refresh_refit_screen"), RefreshRefitScreenS2CPacket::new);

    public RefreshRefitScreenS2CPacket(PacketByteBuf buf) {
        this();
    }

    public RefreshRefitScreenS2CPacket() {
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
