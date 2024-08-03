package com.tacz.guns.network.packets.c2s;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CraftC2SPacket implements FabricPacket {
    public static final PacketType<CraftC2SPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "c2s_craft"), CraftC2SPacket::new);
    private final Identifier recipeId;
    private final int menuId;

    public CraftC2SPacket(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readVarInt());
    }

    public CraftC2SPacket(Identifier recipeId, int menuId) {
        this.recipeId = recipeId;
        this.menuId = menuId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(recipeId);
        buf.writeVarInt(menuId);
    }

    public void handle(ServerPlayerEntity player, PacketSender ignoredSender) {
        if (player == null) return;
        if (player.currentScreenHandler.syncId == menuId && player.currentScreenHandler instanceof GunSmithTableMenu menu) {
            menu.doCraft(recipeId, player);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
