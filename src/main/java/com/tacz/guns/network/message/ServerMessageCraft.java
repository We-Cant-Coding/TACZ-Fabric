package com.tacz.guns.network.message;

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

public class ServerMessageCraft implements FabricPacket {
    public static final PacketType<ServerMessageCraft> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "server_message_craft"), ServerMessageCraft::new);

    private final int menuId;

    public ServerMessageCraft(PacketByteBuf buf) {
        this(buf.readVarInt());
    }

    public ServerMessageCraft(int menuId) {
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
