package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.GunTabType;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
import com.tacz.guns.item.AttachmentItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModCreativeTabs {
    public static ItemGroup OTHER_TAB = register("other", FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.tab.tacz-fabric.other"))
            .icon(() -> ModItems.GUN_SMITH_TABLE.getDefaultStack())
            .entries((displayContext, entries) -> {
                entries.add(ModItems.GUN_SMITH_TABLE);
                entries.add(ModItems.TARGET);
                entries.add(ModItems.STATUE);
                entries.add(ModItems.TARGET_MINECART);
                AmmoBoxItem.fillItemCategory(entries);
            }).build());

    public static ItemGroup AMMO_TAB = register("ammo", FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.tab.tacz-fabric.ammo"))
            .icon(() -> AmmoItemBuilder.create().setId(DefaultAssets.DEFAULT_AMMO_ID).build())
            .entries((parameters, output) -> output.addAll(AmmoItem.fillItemCategory())).build());

    public static ItemGroup ATTACHMENT_SCOPE_TAB = register("scope", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.scope.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "sight_sro_dot")).build())
            .entries((parameters, output) -> output.addAll(AttachmentItem.fillItemCategory(AttachmentType.SCOPE))).build());

    public static ItemGroup ATTACHMENT_MUZZLE_TAB = register("muzzle", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.muzzle.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "muzzle_compensator_trident")).build())
            .entries((parameters, output) -> output.addAll(AttachmentItem.fillItemCategory(AttachmentType.MUZZLE))).build());

    public static ItemGroup ATTACHMENT_STOCK_TAB = register("stock", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.stock.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "stock_militech_b5")).build())
            .entries((parameters, output) -> output.addAll(AttachmentItem.fillItemCategory(AttachmentType.STOCK))).build());

    public static ItemGroup ATTACHMENT_GRIP_TAB = register("grip", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.grip.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "grip_magpul_afg_2")).build())
            .entries((parameters, output) -> output.addAll(AttachmentItem.fillItemCategory(AttachmentType.GRIP))).build());

    public static ItemGroup ATTACHMENT_EXTENDED_MAG_TAB = register("extended_mag", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.extended_mag.name"))
            .icon(() -> AttachmentItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "extended_mag_3")).build())
            .entries((parameters, output) -> output.addAll(AttachmentItem.fillItemCategory(AttachmentType.EXTENDED_MAG))).build());

    public static ItemGroup GUN_PISTOL_TAB = register("pistol", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.pistol.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "glock_17")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.PISTOL))).build());

    public static ItemGroup GUN_SNIPER_TAB = register("sniper", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.sniper.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "ai_awp")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.SNIPER))).build());

    public static ItemGroup GUN_RIFLE_TAB = register("rifle", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.rifle.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "ak47")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.RIFLE))).build());

    public static ItemGroup GUN_SHOTGUN_TAB = register("shotgun", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.shotgun.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "db_short")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.SHOTGUN))).build());

    public static ItemGroup GUN_SMG_TAB = register("smg", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.smg.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "hk_mp5a5")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.SMG))).build());

    public static ItemGroup GUN_RPG_TAB = register("rpg", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.rpg.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "rpg7")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.RPG))).build());

    public static ItemGroup GUN_MG_TAB = register("mg", FabricItemGroup.builder()
            .displayName(Text.translatable("tacz.type.mg.name"))
            .icon(() -> GunItemBuilder.create().setId(new Identifier(GunMod.MOD_ID, "m249")).build())
            .entries((parameters, output) -> output.addAll(AbstractGunItem.fillItemCategory(GunTabType.MG))).build());

    public static void init() {
    }

    private static ItemGroup register(String path, ItemGroup group) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier(GunMod.MOD_ID, path), group);
    }
}
