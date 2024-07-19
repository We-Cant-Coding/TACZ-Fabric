package com.tacz.guns.item;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.nbt.AmmoBoxItemDataAccessor;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.inventory.tooltip.AmmoBoxTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AmmoBoxItem extends Item implements DyeableItem, AmmoBoxItemDataAccessor {
    public static final Identifier PROPERTY_NAME = new Identifier(GunMod.MOD_ID, "ammo_statue");

    public static final int IRON_LEVEL = 0;
    public static final int GOLD_LEVEL = 1;
    public static final int DIAMOND_LEVEL = 2;

    private static final String DISPLAY_TAG = "display";
    private static final String COLOR_TAG = "color";

    private static final int OPEN = 0;
    private static final int CLOSE = 1;

    private static final int CREATIVE_INDEX = 6;
    private static final int ALL_TYPE_CREATIVE_INDEX = 8;

    public AmmoBoxItem() {
        super(new Settings().maxCount(1));
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex > 0 ? -1 : getTagColor(stack);
    }

    @Environment(EnvType.CLIENT)
    public static float getStatue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        int openStatue = OPEN;
        int ammoLevel = IRON_LEVEL;
        if (stack.getItem() instanceof IAmmoBox iAmmoBox) {
            if (iAmmoBox.isAllTypeCreative(stack)) {
                return ALL_TYPE_CREATIVE_INDEX;
            }
            openStatue = getOpenStatue(stack, iAmmoBox);
            if (iAmmoBox.isCreative(stack)) {
                return openStatue + CREATIVE_INDEX;
            }
            ammoLevel = getLevelStatue(stack, iAmmoBox);
        }
        return openStatue + 2 * ammoLevel;
    }

    private static int getOpenStatue(ItemStack stack, IAmmoBox iAmmoBox) {
        boolean idIsEmpty = iAmmoBox.getAmmoId(stack).equals(DefaultAssets.EMPTY_AMMO_ID);
        boolean countIsZero = iAmmoBox.getAmmoCount(stack) <= 0;
        if (idIsEmpty || countIsZero) {
            return OPEN;
        }
        return CLOSE;
    }

    private static int getLevelStatue(ItemStack stack, IAmmoBox iAmmoBox) {
        return iAmmoBox.getAmmoLevel(stack);
    }

    private static int getTagColor(ItemStack stack) {
        NbtCompound compoundtag = stack.getSubNbt(DISPLAY_TAG);
        return compoundtag != null && compoundtag.contains(COLOR_TAG, NbtElement.NUMBER_TYPE) ? compoundtag.getInt(COLOR_TAG) : 0x727d6b;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public boolean onStackClicked(ItemStack ammoBox, Slot slot, ClickType action, PlayerEntity player) {
        // 右击
        if (action == ClickType.RIGHT) {
            // 点击的格子
            ItemStack slotItem = slot.getStack();
            Identifier boxAmmoId = this.getAmmoId(ammoBox);

            // 格子为空，那就是取出物品
            if (slotItem.isEmpty()) {
                // 创造模式弹药箱不能取出任何东西
                if (isAllTypeCreative(ammoBox) || isCreative(ammoBox)) {
                    return false;
                }
                // 啥也没有，不能取出
                if (boxAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                    return false;
                }
                // 数量不对，不能取出
                int boxAmmoCount = this.getAmmoCount(ammoBox);
                if (boxAmmoCount <= 0) {
                    return false;
                }
                TimelessAPI.getCommonAmmoIndex(boxAmmoId).ifPresent(index -> {
                    int takeCount = Math.min(index.getStackSize(), boxAmmoCount);
                    ItemStack takeAmmo = AmmoItemBuilder.create().setId(boxAmmoId).setCount(takeCount).build();
                    slot.insertStack(takeAmmo);

                    int remainCount = boxAmmoCount - takeCount;
                    this.setAmmoCount(ammoBox, remainCount);
                    if (remainCount <= 0) {
                        this.setAmmoId(ammoBox, DefaultAssets.EMPTY_AMMO_ID);
                    }
                    this.playRemoveOneSound(player);
                });
                return true;
            }

            // 如果是子弹
            if (slotItem.getItem() instanceof IAmmo iAmmo) {
                // 全类型弹药箱不能存入
                if (isAllTypeCreative(ammoBox)) {
                    return false;
                }
                Identifier slotAmmoId = iAmmo.getAmmoId(slotItem);
                // 格子里的子弹 ID 不对，不能放
                if (slotAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                    return false;
                }
                // 如果盒子的子弹 ID 为空，变成当前点击的类型
                if (boxAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                    this.setAmmoId(ammoBox, slotAmmoId);
                } else if (!slotAmmoId.equals(boxAmmoId)) {
                    return false;
                }
                TimelessAPI.getCommonAmmoIndex(slotAmmoId).ifPresent(index -> {
                    // 创造模式弹药箱，那就直接存入最大
                    if (isCreative(ammoBox)) {
                        this.setAmmoCount(ammoBox, Integer.MAX_VALUE);
                        return;
                    }
                    int boxAmmoCount = this.getAmmoCount(ammoBox);
                    int boxLevelMultiplier = this.getAmmoLevel(ammoBox) + 1;
                    int maxSize = index.getStackSize() * SyncConfig.AMMO_BOX_STACK_SIZE.get() * boxLevelMultiplier;
                    int needCount = maxSize - boxAmmoCount;
                    ItemStack takeItem = slot.takeStackRange(slotItem.getCount(), needCount, player);
                    this.setAmmoCount(ammoBox, boxAmmoCount + takeItem.getCount());
                });
                // 播放取出声音
                this.playInsertSound(player);
                return true;
            }
        }
        return false;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        if (isAllTypeCreative(stack) || isCreative(stack)) {
            return false;
        }
        return !this.getAmmoId(stack).equals(DefaultAssets.EMPTY_AMMO_ID) && this.getAmmoCount(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        Identifier ammoId = this.getAmmoId(stack);
        int ammoCount = this.getAmmoCount(stack);
        int boxLevelMultiplier = this.getAmmoLevel(stack) + 1;
        double widthPercent = TimelessAPI.getCommonAmmoIndex(ammoId).map(index -> {
            double totalCount = index.getStackSize() * SyncConfig.AMMO_BOX_STACK_SIZE.get() * boxLevelMultiplier;
            return ammoCount / totalCount;
        }).orElse(0d);
        return (int) Math.min(1 + 12 * widthPercent, 13);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (isAllTypeCreative(stack)) {
            return Text.translatable("item.tacz-fabric.ammo_box.all_type_creative").formatted(Formatting.DARK_PURPLE);
        }
        if (isCreative(stack)) {
            return Text.translatable("item.tacz-fabric.ammo_box.creative").formatted(Formatting.DARK_PURPLE);
        }
        int ammoLevel = getAmmoLevel(stack);
        switch (ammoLevel) {
            case GOLD_LEVEL -> {
                return Text.translatable("item.tacz-fabric.ammo_box.gold").formatted(Formatting.YELLOW);
            }
            case DIAMOND_LEVEL -> {
                return Text.translatable("item.tacz-fabric.ammo_box.diamond").formatted(Formatting.AQUA);
            }
            default -> {
                return Text.translatable("item.tacz-fabric.ammo_box.iron");
            }
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (isAllTypeCreative(stack) || isCreative(stack)) {
            return true;
        }
        return super.hasGlint(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb(1 / 3f, 1.0F, 1.0F);
    }

    public static void fillItemCategory(ItemGroup.Entries output) {
        ItemStack ammoBox = ModItems.AMMO_BOX.getDefaultStack();
        if (ammoBox.getItem() instanceof IAmmoBox iAmmoBox) {
            // 添加普通版本的弹药盒
            output.add(iAmmoBox.setAmmoLevel(ammoBox.copy(), IRON_LEVEL));
            output.add(iAmmoBox.setAmmoLevel(ammoBox.copy(), GOLD_LEVEL));
            output.add(iAmmoBox.setAmmoLevel(ammoBox.copy(), DIAMOND_LEVEL));

            // 添加创造模式弹药盒
            output.add(iAmmoBox.setCreative(ammoBox.copy(), false));
            output.add(iAmmoBox.setCreative(ammoBox.copy(), true));
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (!(stack.getItem() instanceof IAmmoBox iAmmoBox)) {
            return Optional.empty();
        }
        Identifier ammoId = iAmmoBox.getAmmoId(stack);
        if (ammoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
            return Optional.empty();
        }
        int ammoCount = iAmmoBox.getAmmoCount(stack);
        if (ammoCount <= 0) {
            return Optional.empty();
        }
        ItemStack ammoStack = AmmoItemBuilder.create().setId(ammoId).build();
        return Optional.of(new AmmoBoxTooltip(stack, ammoStack, ammoCount));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World pLevel, List<Text> components, TooltipContext isAdvanced) {
        if (isAllTypeCreative(stack)) {
            components.add(Text.translatable("tooltip.tacz-fabric.ammo_box.usage.all_type_creative").formatted(Formatting.GOLD));
            return;
        }
        if (isCreative(stack)) {
            components.add(Text.translatable("tooltip.tacz-fabric.ammo_box.usage.creative.1").formatted(Formatting.YELLOW));
            components.add(Text.translatable("tooltip.tacz-fabric.ammo_box.usage.creative.2").formatted(Formatting.YELLOW));
            return;
        }
        components.add(Text.translatable("tooltip.tacz-fabric.ammo_box.usage.deposit").formatted(Formatting.GRAY));
        components.add(Text.translatable("tooltip.tacz-fabric.ammo_box.usage.remove").formatted(Formatting.GRAY));
    }
}
