package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.crafting.GunSmithTableSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipe {
    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, GunSmithTableSerializer.ID, GunSmithTableSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(GunMod.MOD_ID, GunSmithTableRecipe.Type.ID), GunSmithTableRecipe.Type.INSTANCE);
    }
}
