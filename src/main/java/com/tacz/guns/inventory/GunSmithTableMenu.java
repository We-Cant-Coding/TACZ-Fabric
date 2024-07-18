package com.tacz.guns.inventory;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ServerMessageCraft;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

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
        TimelessAPI.getRecipe(recipeId).ifPresent(recipe -> player.tacz$getItemHandlerCapability(null).ifPresent(handler -> {
            Int2IntArrayMap recordCount = new Int2IntArrayMap();
            List<GunSmithTableIngredient> ingredients = recipe.getInputs();

            for (GunSmithTableIngredient ingredient : ingredients) {
                int count = 0;
                for (int slotIndex = 0; slotIndex < handler.getSlots(); slotIndex++) {
                    ItemStack stack = handler.getStackInSlot(slotIndex);
                    int stackCount = stack.getCount();
                    if (!stack.isEmpty() && ingredient.ingredient().test(stack)) {
                        count = count + stackCount;
                        // Record the slot and number of deductions
                        if (count <= ingredient.count()) {
                            // Full deduction if insufficient
                            recordCount.put(slotIndex, stackCount);
                        } else {
                            // Enough to deduct only the amount needed
                            int remaining = count - ingredient.count();
                            recordCount.put(slotIndex, stackCount - remaining);
                            break;
                        }
                    }
                }
                // Insufficient quantity, no subsequent logic is performed, synthesis fails
                if (count < ingredient.count()) {
                    return;
                }
            }

            // Start withholding materials
            for (int slotIndex : recordCount.keySet()) {
                handler.extractItem(slotIndex, recordCount.get(slotIndex), false);
            }

            // Give the player the corresponding item
            World world = player.getWorld();
            if (!world.isClient) {
                ItemEntity entity = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), recipe.getOutput(world.getRegistryManager()).copy());
                entity.setPickupDelay(0);
                world.spawnEntity(entity);
            }
            // Update, otherwise the client display is incorrect
            player.playerScreenHandler.updateToClient();
            NetworkHandler.sendToClientPlayer(new ServerMessageCraft(this.syncId), player);
        }));
    }
}
