package com.tacz.guns.crafting;

import com.google.gson.JsonObject;
import com.tacz.guns.GunMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

import java.util.List;

public class GunSmithTableSerializer implements RecipeSerializer<GunSmithTableRecipe> {
    public static final GunSmithTableSerializer INSTANCE = new GunSmithTableSerializer();

    public static final Identifier ID = new Identifier(GunMod.MOD_ID, "gun_smith_table_crafting");
    public static final GunSmithTableRecipe EMPTY = new GunSmithTableRecipe(
            new Identifier(GunMod.MOD_ID, "gun_crafting_empty"),
            new GunSmithTableResult(ItemStack.EMPTY, "empty"),
            List.of());

    @Override
    public GunSmithTableRecipe read(Identifier id, JsonObject json) {
        // does not go through the original packet system, so this piece returns empty directly
        return EMPTY;
    }

    @Override
    public GunSmithTableRecipe read(Identifier id, PacketByteBuf buf) {
        // does not go to the original network packet synchronization system, so this piece directly returns empty
        return EMPTY;
    }

    @Override
    public void write(PacketByteBuf buf, GunSmithTableRecipe recipe) {
        // Doesn't go with the original network package synchronization system, so this piece is empty
    }
}
