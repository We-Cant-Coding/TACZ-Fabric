package com.tacz.guns.api.item.gun;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.GunTabType;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.inventory.tooltip.GunTooltip;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AllowAttachmentTagMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractGunItem extends Item implements IGun {
    protected AbstractGunItem(Settings settings) {
        super(settings);
    }

    private static Comparator<Map.Entry<Identifier, CommonGunIndex>> idNameSort() {
        return Comparator.comparingInt(m -> m.getValue().getSort());
    }

    /**
     * Called on completion of bolt pulling
     */
    public abstract void bolt(ItemStack gunItem);

    /**
     * Triggered when shooting
     */
    public abstract void shoot(ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, boolean tracer, LivingEntity shooter);

    /**
     * Called when switching fire modes
     */
    public abstract void fireSelect(ItemStack gunItem);

    public abstract void melee(LivingEntity user, ItemStack gunItem);

    /**
     * Called when triggering a gun bullet update during a bullet change.
     *
     * @param gunItem    Firearms
     * @param ammoCount  Number of bullets filled
     * @param loadBarrel Is it necessary to fill the barrel with bullets?
     */
    public abstract void reloadAmmo(ItemStack gunItem, int ammoCount, boolean loadBarrel);

    /**
     * The method has a generic implementation, placed here
     */
    @Override
    public boolean allowAttachment(ItemStack gun, ItemStack attachmentItem) {
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(attachmentItem);
        IGun iGun = IGun.getIGunOrNull(gun);
        if (iGun != null && iAttachment != null) {
            Identifier gunId = iGun.getGunId(gun);
            Identifier attachmentId = iAttachment.getAttachmentId(attachmentItem);
            return AllowAttachmentTagMatcher.match(gunId, attachmentId);
        }
        return false;
    }

    /**
     * The method has a generic implementation, placed here
     */
    @Override
    public boolean allowAttachmentType(ItemStack gun, AttachmentType type) {
        IGun iGun = IGun.getIGunOrNull(gun);
        if (iGun != null) {
            return TimelessAPI.getCommonGunIndex(iGun.getGunId(gun)).map(gunIndex -> {
                List<AttachmentType> allowAttachments = gunIndex.getGunData().getAllowAttachments();
                if (allowAttachments == null) {
                    return false;
                }
                return allowAttachments.contains(type);
            }).orElse(false);
        } else {
            return false;
        }
    }

    /**
     * The method has a generic implementation, placed here
     */
    @Override
    @NotNull
    @Environment(EnvType.CLIENT)
    public Text getName(@NotNull ItemStack stack) {
        Identifier gunId = this.getGunId(stack);
        Optional<ClientGunIndex> gunIndex = TimelessAPI.getClientGunIndex(gunId);
        if (gunIndex.isPresent()) {
            return Text.translatable(gunIndex.get().getName());
        }
        return super.getName(stack);
    }

    /**
     * The method has a generic implementation, placed here
     */
    public static DefaultedList<ItemStack> fillItemCategory(GunTabType type) {
        DefaultedList<ItemStack> stacks = DefaultedList.of();
        TimelessAPI.getAllCommonGunIndex().stream().sorted(idNameSort()).forEach(entry -> {
            CommonGunIndex index = entry.getValue();
            GunData gunData = index.getGunData();
            String key = type.name().toLowerCase(Locale.US);
            String indexType = index.getType();
            if (key.equals(indexType)) {
                ItemStack itemStack = GunItemBuilder.create()
                        .setId(entry.getKey())
                        .setFireMode(gunData.getFireModeSet().get(0))
                        .setAmmoCount(gunData.getAmmoAmount())
                        .setAmmoInBarrel(true)
                        .build();
                stacks.add(itemStack);
            }
        });
        return stacks;
    }

    /**
     * Stop the player's arm swing animation from playing
     */
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.SUCCESS;
    }

    /**
     * The method has a generic implementation, placed here
     */
    @Override
    @NotNull
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (stack.getItem() instanceof IGun iGun) {
            Optional<CommonGunIndex> optional = TimelessAPI.getCommonGunIndex(this.getGunId(stack));
            if (optional.isPresent()) {
                CommonGunIndex gunIndex = optional.get();
                Identifier ammoId = gunIndex.getGunData().getAmmoId();
                return Optional.of(new GunTooltip(stack, iGun, ammoId, gunIndex));
            }
        }
        return Optional.empty();
    }
}
