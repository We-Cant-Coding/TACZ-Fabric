package com.tacz.guns.client.resource.serialize;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;
import java.util.Objects;

public class ItemStackSerializer implements JsonDeserializer<ItemStack> {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            return getItemStack(jsonObject);
        } else {
            throw new JsonSyntaxException("Expected " + json + " to be a ItemStack because it's not an object");
        }
    }

    private static Item getItem(String itemName) {
        Identifier itemKey = new Identifier(itemName);
        if (!Registries.ITEM.containsId(itemKey)) {
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");
        } else {
            Item item = Registries.ITEM.get(itemKey);
            return Objects.requireNonNull(item);
        }
    }

    public static NbtCompound getNBT(JsonElement element) {
        try {
            return element.isJsonObject() ? StringNbtReader.parse(GSON.toJson(element)) : StringNbtReader.parse(JsonHelper.asString(element, "nbt"));
        } catch (CommandSyntaxException var2) {
            throw new JsonSyntaxException("Invalid NBT Entry: " + var2);
        }
    }

    private static ItemStack getItemStack(JsonObject json) {
        String itemName = JsonHelper.getString(json, "item");
        Item item = getItem(itemName);
        if (json.has("nbt")) {
            NbtCompound nbt = getNBT(json.get("nbt"));
            NbtCompound tmp = new NbtCompound();

            tmp.put("tag", nbt);
            tmp.putString("id", itemName);
            tmp.putInt("Count", JsonHelper.getInt(json, "count", 1));
            return ItemStack.fromNbt(tmp);
        } else {
            return new ItemStack(item, JsonHelper.getInt(json, "count", 1));
        }
    }
}
