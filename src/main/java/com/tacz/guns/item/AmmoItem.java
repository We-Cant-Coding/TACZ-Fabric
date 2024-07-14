package com.tacz.guns.item;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.nbt.AmmoItemDataAccessor;
import com.tacz.guns.client.resource.ClientAssetManager;
import com.tacz.guns.client.resource.index.ClientAmmoIndex;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AmmoItem extends Item implements AmmoItemDataAccessor {
    public AmmoItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.getMaxCount(stack) == 1 && this.isDamageable();
    }

    public int getMaxCount(ItemStack stack) {
        if (stack.getItem() instanceof IAmmo iAmmo) {
            return TimelessAPI.getCommonAmmoIndex(iAmmo.getAmmoId(stack))
                    .map(CommonAmmoIndex::getStackSize).orElse(1);
        }
        return 1;
    }

    @Override
    @NotNull
    @Environment(EnvType.CLIENT)
    public Text getName(@NotNull ItemStack stack) {
        Identifier ammoId = this.getAmmoId(stack);
        Optional<ClientAmmoIndex> ammoIndex = TimelessAPI.getClientAmmoIndex(ammoId);
        if (ammoIndex.isPresent()) {
            return Text.translatable(ammoIndex.get().getName());
        }
        return super.getName(stack);
    }

    public static DefaultedList<ItemStack> fillItemCategory() {
        DefaultedList<ItemStack> stacks = DefaultedList.of();
        TimelessAPI.getAllCommonAmmoIndex().forEach(entry -> {
            ItemStack itemStack = AmmoItemBuilder.create().setId(entry.getKey()).build();
            stacks.add(itemStack);
        });
        return stacks;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> components, TooltipContext isAdvanced) {
        Identifier ammoId = this.getAmmoId(stack);
        TimelessAPI.getClientAmmoIndex(ammoId).ifPresent(index -> {
            String tooltipKey = index.getTooltipKey();
            if (tooltipKey != null) {
                components.add(Text.translatable(tooltipKey).formatted(Formatting.GRAY));
            }
        });

        PackInfo packInfoObject = ClientAssetManager.INSTANCE.getPackInfo(ammoId);
        if (packInfoObject != null) {
            MutableText component = Text.translatable(packInfoObject.getName()).formatted(Formatting.BLUE, Formatting.ITALIC);
            components.add(component);
        }
    }
}
