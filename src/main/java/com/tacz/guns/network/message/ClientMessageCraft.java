package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ClientMessageCraft implements FabricPacket {
    public static final PacketType<ClientMessageCraft> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "client_message_craft"), ClientMessageCraft::new);
    private final Identifier recipeId;
    private final int menuId;

    public ClientMessageCraft(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readVarInt());
    }

    public ClientMessageCraft(Identifier recipeId, int menuId) {
        this.recipeId = recipeId;
        this.menuId = menuId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(recipeId);
        buf.writeVarInt(menuId);
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            if (player == null) return;
            if (player.currentScreenHandler.syncId == menuId && player.currentScreenHandler instanceof GunSmithTableMenu menu) {
                menu.doCraft(recipeId, player);
            }
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
