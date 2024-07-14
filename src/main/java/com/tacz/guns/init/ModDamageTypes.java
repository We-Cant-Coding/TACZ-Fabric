package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> BULLET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(GunMod.MOD_ID, "bullet"));
    public static final RegistryKey<DamageType> BULLET_IGNORE_ARMOR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(GunMod.MOD_ID, "bullet_ignore_armor"));

    public static class Sources {
        private static RegistryEntry.Reference<DamageType> getHolder(DynamicRegistryManager access, RegistryKey<DamageType> damageTypeKey) {
            return access.get(RegistryKeys.DAMAGE_TYPE).entryOf(damageTypeKey);
        }

        public static DamageSource bullet(DynamicRegistryManager acccess, EntityKineticBullet bullet, Entity shooter, boolean ignoreArmor) {
            return new DamageSource(getHolder(acccess, ignoreArmor ? BULLET_IGNORE_ARMOR : BULLET), bullet, shooter);
        }
    }
}
