package com.tacz.guns.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class GunSmithTableMenu extends ScreenHandler {
    public static final ScreenHandlerType<GunSmithTableMenu> TYPE = new ScreenHandlerType<>(GunSmithTableMenu::new, FeatureFlags.VANILLA_FEATURES);

    public GunSmithTableMenu(int id, Inventory inventory) {
        super(TYPE, id);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.isAlive();
    }

    public void doCraft(Identifier recipeId, PlayerEntity player) {
        /*
        ???
        forge 전용 코드로만 되있는데 방법이 있나?
        TimelessAPI.getRecipe(recipeId).ifPresent(recipe -> player.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(handler -> {
            Int2IntArrayMap recordCount = new Int2IntArrayMap();
            List<GunSmithTableIngredient> ingredients = recipe.getInputs();

            for (GunSmithTableIngredient ingredient : ingredients) {
                int count = 0;
                for (int slotIndex = 0; slotIndex < handler.getSlots(); slotIndex++) {
                    ItemStack stack = handler.getStackInSlot(slotIndex);
                    int stackCount = stack.getCount();
                    if (!stack.isEmpty() && ingredient.getIngredient().test(stack)) {
                        count = count + stackCount;
                        // 记录扣除的 slot 和数量
                        if (count <= ingredient.getCount()) {
                            // 如果数量不足，全扣
                            recordCount.put(slotIndex, stackCount);
                        } else {
                            //  数量够了，只扣需要的数量
                            int remaining = count - ingredient.getCount();
                            recordCount.put(slotIndex, stackCount - remaining);
                            break;
                        }
                    }
                }
                // 数量不够，不执行后续逻辑，合成失败
                if (count < ingredient.getCount()) {
                    return;
                }
            }

            // Start withholding materials
            for (int slotIndex : recordCount.keySet()) {
                handler.extractItem(slotIndex, recordCount.get(slotIndex), false);
            }

            // Give the player the corresponding item
            Level level = player.level();
            if (!level.isClientSide) {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), recipe.getResultItem(player.level().registryAccess()).copy());
                itemEntity.setPickUpDelay(0);
                level.addFreshEntity(itemEntity);
            }
            // 更新，否则客户端显示不正确
            player.inventoryMenu.broadcastFullState();
            NetworkHandler.sendToClientPlayer(new ServerMessageCraft(this.containerId), player);
        }));
         */
    }
}
