package com.tacz.guns.client.model.papi;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class PlayerNamePapi implements Function<ItemStack, String> {
    public static final String NAME = "player_name";

    @Override
    public String apply(ItemStack stack) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            return player.getName().getString();
        }
        return "";
    }
}
