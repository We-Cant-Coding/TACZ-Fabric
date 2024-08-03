package com.tacz.guns.network.packets.s2c;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.gui.GunSmithTableScreen;
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

public class CraftS2CPacket implements FabricPacket {
    public static final PacketType<CraftS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "s2c_craft"), CraftS2CPacket::new);

    private final int menuId;

    public CraftS2CPacket(PacketByteBuf buf) {
        this(buf.readVarInt());
    }

    public CraftS2CPacket(int menuId) {
        this.menuId = menuId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(menuId);
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            updateScreen(menuId);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    private static void updateScreen(int containerId) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.currentScreenHandler.syncId == containerId && MinecraftClient.getInstance().currentScreen instanceof GunSmithTableScreen screen) {
            screen.updateIngredientCount();
        }
    }
}
