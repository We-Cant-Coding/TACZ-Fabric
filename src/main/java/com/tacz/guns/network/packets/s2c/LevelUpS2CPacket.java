package com.tacz.guns.network.packets.s2c;

import com.tacz.guns.GunMod;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class LevelUpS2CPacket implements FabricPacket {
    public static final PacketType<LevelUpS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "level_up"), LevelUpS2CPacket::new);
    private final ItemStack gun;
    private final int level;

    public LevelUpS2CPacket(PacketByteBuf buf) {
        this(buf.readItemStack(), buf.readInt());
    }


    public LevelUpS2CPacket(ItemStack gun, int level) {
        this.gun = gun;
        this.level = level;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeItemStack(gun);
        buf.writeInt(level);
    }

    public void handle(PlayerEntity ignoredPlayer, PacketSender ignoredSender) {
        if (EnvironmentUtil.isClient()) {
            onLevelUp(this);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    private static void onLevelUp(LevelUpS2CPacket message) {
        int level = message.getLevel();
        ItemStack gun = message.getGun();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        // TODO After completing the gun upgrade logic, unblock the following code
                /*
                if (GunLevelManager.DAMAGE_UP_LEVELS.contains(level)) {
                    Minecraft.getInstance().getToasts().addToast(new GunLevelUpToast(gun,
                            Component.translatable("toast.tacz-fabric.level_up"),
                            Component.translatable("toast.tacz-fabric.sub.damage_up")));
                } else if (level >= GunLevelManager.MAX_LEVEL) {
                    Minecraft.getInstance().getToasts().addToast(new GunLevelUpToast(gun,
                            Component.translatable("toast.tacz-fabric.level_up"),
                            Component.translatable("toast.tacz-fabric.sub.final_level")));
                } else {
                    Minecraft.getInstance().getToasts().addToast(new GunLevelUpToast(gun,
                            Component.translatable("toast.tacz-fabric.level_up"),
                            Component.translatable("toast.tacz-fabric.sub.level_up")));
                }*/
    }

    public ItemStack getGun() {
        return this.gun;
    }

    public int getLevel() {
        return this.level;
    }
}
