package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.entity.TargetMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static EntityType<EntityKineticBullet> BULLET = register("bullet", EntityKineticBullet.TYPE);
    public static EntityType<TargetMinecart> TARGET_MINECART = register("target_minecart", TargetMinecart.TYPE);

    public static void register() {

    }

    private static <T extends Entity> EntityType<T> register(String path, EntityType<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(GunMod.MOD_ID, path), type);
    }
}
