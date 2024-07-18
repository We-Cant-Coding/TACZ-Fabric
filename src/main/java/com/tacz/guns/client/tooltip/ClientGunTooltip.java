package com.tacz.guns.client.tooltip;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.client.input.RefitKey;
import com.tacz.guns.client.resource.ClientAssetManager;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.inventory.tooltip.GunTooltip;
import com.tacz.guns.item.GunTooltipPart;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ClientGunTooltip implements TooltipComponent {
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##%");
    private static final DecimalFormat DAMAGE_FORMAT = new DecimalFormat("#.##");

    private final ItemStack gun;
    private final IGun iGun;
    private final CommonGunIndex gunIndex;
    private final ItemStack ammo;
    private @Nullable List<OrderedText> desc;
    private Text ammoName;
    private MutableText ammoCountText;
    private @Nullable MutableText gunType;
    private MutableText damage;
    private MutableText armorIgnore;
    private MutableText headShotMultiplier;
    private MutableText tips;
    private MutableText levelInfo;
    private @Nullable MutableText packInfo;

    private int maxWidth;

    public ClientGunTooltip(GunTooltip tooltip) {
        this.gun = tooltip.getGun();
        this.iGun = tooltip.getIGun();
        Identifier ammoId = tooltip.getAmmoId();
        this.gunIndex = tooltip.getGunIndex();
        this.ammo = AmmoItemBuilder.create().setId(ammoId).build();
        this.maxWidth = 0;
        this.getText();
    }

    @Override
    public int getHeight() {
        int height = 0;
        if (shouldShow(GunTooltipPart.DESCRIPTION) && this.desc != null) {
            height += 10 * this.desc.size() + 2;
        }
        if (shouldShow(GunTooltipPart.AMMO_INFO)) {
            height += 24;
        }
        if (shouldShow(GunTooltipPart.BASE_INFO)) {
            height += 34;
        }
        if (shouldShow(GunTooltipPart.EXTRA_DAMAGE_INFO)) {
            height += 24;
        }
        if (shouldShow(GunTooltipPart.UPGRADES_TIP)) {
            height += 14;
        }
        if (shouldShow(GunTooltipPart.PACK_INFO)) {
            height += 14;
        }
        return height;
    }

    @Override
    public int getWidth(TextRenderer font) {
        return this.maxWidth;
    }

    private void getText() {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;


        if (shouldShow(GunTooltipPart.DESCRIPTION)) {
            @Nullable String tooltip = gunIndex.getPojo().getTooltip();
            if (tooltip != null) {
                List<OrderedText> split = font.wrapLines(Text.translatable(tooltip), 200);
                if (split.size() > 2) {
                    this.desc = split.subList(0, 2);
                } else {
                    this.desc = split;
                }
                for (OrderedText sequence : this.desc) {
                    this.maxWidth = Math.max(font.getWidth(sequence), this.maxWidth);
                }
            }
        }


        if (shouldShow(GunTooltipPart.AMMO_INFO)) {
            this.ammoName = ammo.getName();
            this.maxWidth = Math.max(font.getWidth(this.ammoName) + 22, this.maxWidth);

            int barrelBulletAmount = (iGun.hasBulletInBarrel(gun) && gunIndex.getGunData().getBolt() != Bolt.OPEN_BOLT) ? 1 : 0;
            int maxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(gun, gunIndex.getGunData()) + barrelBulletAmount;
            int currentAmmoCount = iGun.getCurrentAmmoCount(this.gun) + barrelBulletAmount;

            if (!iGun.useDummyAmmo(gun)) {
                this.ammoCountText = Text.literal("%d/%d".formatted(currentAmmoCount, maxAmmoCount));
            } else {
                int dummyAmmoAmount = iGun.getDummyAmmoAmount(gun);
                this.ammoCountText = Text.literal("%d/%d (%d)".formatted(currentAmmoCount, maxAmmoCount, dummyAmmoAmount));
            }
            this.maxWidth = Math.max(font.getWidth(this.ammoCountText) + 22, this.maxWidth);
        }


        if (shouldShow(GunTooltipPart.BASE_INFO)) {
            int expToNextLevel = iGun.getExpToNextLevel(gun);
            int expCurrentLevel = iGun.getExpCurrentLevel(gun);
            int level = iGun.getLevel(gun);
            if (level >= iGun.getMaxLevel()) {
                String levelText = String.format("%d (MAX)", level);
                this.levelInfo = Text.translatable("tooltip.tacz.gun.level").append(Text.literal(levelText).formatted(Formatting.DARK_PURPLE));
            } else {
                String levelText = String.format("%d (%.1f%%)", level, expCurrentLevel / (expToNextLevel + expCurrentLevel) * 100f);
                this.levelInfo = Text.translatable("tooltip.tacz.gun.level").append(Text.literal(levelText).formatted(Formatting.YELLOW));
            }
            this.maxWidth = Math.max(font.getWidth(this.levelInfo), this.maxWidth);

            String tabKey = "tacz.type." + gunIndex.getType() + ".name";
            this.gunType = Text.translatable("tooltip.tacz.gun.type").append(Text.translatable(tabKey).formatted(Formatting.AQUA));
            this.maxWidth = Math.max(font.getWidth(this.gunType), this.maxWidth);

            MutableText value = Text.literal(DAMAGE_FORMAT.format(gunIndex.getBulletData().getDamageAmount() * SyncConfig.DAMAGE_BASE_MULTIPLIER.get())).formatted(Formatting.AQUA);
            if (gunIndex.getBulletData().getExplosionData() != null) {
                value.append(" + ").append(DAMAGE_FORMAT.format(gunIndex.getBulletData().getExplosionData().getDamage() * SyncConfig.DAMAGE_BASE_MULTIPLIER.get())).append(Text.translatable("tooltip.tacz.gun.explosion"));
            }
            this.damage = Text.translatable("tooltip.tacz.gun.damage").append(value);
            this.maxWidth = Math.max(font.getWidth(this.damage), this.maxWidth);
        }


        if (shouldShow(GunTooltipPart.EXTRA_DAMAGE_INFO)) {
            @Nullable ExtraDamage extraDamage = gunIndex.getBulletData().getExtraDamage();
            if (extraDamage != null) {
                float armorDamagePercent = (float) (extraDamage.getArmorIgnore() * SyncConfig.ARMOR_IGNORE_BASE_MULTIPLIER.get());
                float headShotMultiplierPercent = (float) (extraDamage.getHeadShotMultiplier() * SyncConfig.HEAD_SHOT_BASE_MULTIPLIER.get());
                armorDamagePercent = MathHelper.clamp(armorDamagePercent, 0.0F, 1.0F);
                this.armorIgnore = Text.translatable("tooltip.tacz.gun.armor_ignore", FORMAT.format(armorDamagePercent));
                this.headShotMultiplier = Text.translatable("tooltip.tacz.gun.head_shot_multiplier", FORMAT.format(headShotMultiplierPercent));
            } else {
                this.armorIgnore = Text.translatable("tooltip.tacz.gun.armor_ignore", FORMAT.format(0));
                this.headShotMultiplier = Text.translatable("tooltip.tacz.gun.head_shot_multiplier", FORMAT.format(1));
            }
            this.maxWidth = Math.max(font.getWidth(this.armorIgnore), this.maxWidth);
            this.maxWidth = Math.max(font.getWidth(this.headShotMultiplier), this.maxWidth);
        }


        if (shouldShow(GunTooltipPart.UPGRADES_TIP)) {
            String keyName = Text.keybind(RefitKey.REFIT_KEY.getTranslationKey()).getString().toUpperCase(Locale.ENGLISH);
            this.tips = Text.translatable("tooltip.tacz.gun.tips", keyName).formatted(Formatting.YELLOW).formatted(Formatting.ITALIC);
            this.maxWidth = Math.max(font.getWidth(this.tips), this.maxWidth);
        }


        if (shouldShow(GunTooltipPart.PACK_INFO)) {
            Identifier gunId = iGun.getGunId(gun);
            PackInfo packInfoObject = ClientAssetManager.INSTANCE.getPackInfo(gunId);
            if (packInfoObject != null) {
                packInfo = Text.translatable(packInfoObject.getName()).formatted(Formatting.BLUE).formatted(Formatting.ITALIC);
                this.maxWidth = Math.max(font.getWidth(this.packInfo), this.maxWidth);
            }
        }
    }

    @Override
    public void drawText(TextRenderer font, int pX, int pY, Matrix4f matrix4f, VertexConsumerProvider.Immediate bufferSource) {
        int yOffset = pY;

        if (shouldShow(GunTooltipPart.DESCRIPTION) && this.desc != null) {
            yOffset += 2;
            for (OrderedText sequence : this.desc) {
                font.draw(sequence, pX, yOffset, 0xaaaaaa, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
                yOffset += 10;
            }
        }


        if (shouldShow(GunTooltipPart.AMMO_INFO)) {
            yOffset += 4;

            // 弹药名
            font.draw(this.ammoName, pX + 20, yOffset, 0xffaa00, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);

            // 弹药数
            font.draw(this.ammoCountText, pX + 20, yOffset + 10, 0x777777, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);

            yOffset += 20;
        }


        if (shouldShow(GunTooltipPart.BASE_INFO)) {
            yOffset += 4;

            // 等级信息
            font.draw(this.levelInfo, pX, yOffset, 0x777777, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;

            // 枪械类型
            if (this.gunType != null) {
                font.draw(this.gunType, pX, yOffset, 0x777777, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
                yOffset += 10;
            }

            // 伤害
            font.draw(this.damage, pX, yOffset, 0x777777, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;
        }


        if (shouldShow(GunTooltipPart.EXTRA_DAMAGE_INFO)) {
            yOffset += 4;

            // 穿甲伤害
            font.draw(this.armorIgnore, pX, yOffset, 0xffaa00, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;

            // 爆头伤害
            font.draw(this.headShotMultiplier, pX, yOffset, 0xffaa00, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;
        }


        if (shouldShow(GunTooltipPart.UPGRADES_TIP)) {
            yOffset += 4;

            // Z 键说明
            font.draw(this.tips, pX, yOffset, 0xffffff, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            yOffset += 10;
        }


        if (shouldShow(GunTooltipPart.PACK_INFO)) {
            // 枪包名
            if (packInfo != null) {
                yOffset += 4;
                font.draw(this.packInfo, pX, yOffset, 0xffffff, false, matrix4f, bufferSource, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            }
        }
    }

    @Override
    public void drawItems(TextRenderer pFont, int pX, int pY, DrawContext guiGraphics) {
        IGun iGun = IGun.getIGunOrNull(this.gun);
        if (iGun == null) {
            return;
        }
        if (shouldShow(GunTooltipPart.AMMO_INFO)) {
            int yOffset = pY;
            if (shouldShow(GunTooltipPart.DESCRIPTION) && this.desc != null) {
                yOffset += this.desc.size() * 10 + 2;
            }
            guiGraphics.drawItem(ammo, pX, yOffset + 4);
        }
    }

    private boolean shouldShow(GunTooltipPart part) {
        return (GunTooltipPart.getHideFlags(this.gun) & part.getMask()) == 0;
    }
}
