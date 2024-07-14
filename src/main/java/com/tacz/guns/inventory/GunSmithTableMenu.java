package com.tacz.guns.inventory;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ServerMessageCraft;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
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
        TimelessAPI.getRecipe(recipeId).ifPresent(recipe -> {
            var handler = PlayerInventoryStorage.of(player);
            Object2IntArrayMap<ItemVariant> recordCount = new Object2IntArrayMap<>();
            List<GunSmithTableIngredient> ingredients = recipe.getInputs();

            for (GunSmithTableIngredient ingredient : ingredients) {
                int count = 0;
                for (SingleSlotStorage<ItemVariant> slot : handler.getSlots()) {
                    var variant = slot.getResource();
                    ItemStack stack = variant.toStack();
                    int stackCount = stack.getCount();
                    if (!stack.isEmpty() && ingredient.ingredient().test(stack)) {
                        count = count + stackCount;
                        // Record the slot and number of deductions
                        if (count <= ingredient.count()) {
                            // Full deduction if insufficient
                            recordCount.put(variant, stackCount);
                        } else {
                            // Enough to deduct only the amount needed
                            int remaining = count - ingredient.count();
                            recordCount.put(variant, stackCount - remaining);
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
            Transaction transaction = Transaction.openOuter();
            for (ItemVariant variant : recordCount.keySet()) {
                handler.extract(variant, recordCount.get(variant), transaction);
            }
            transaction.close();

            // Give the player the corresponding item
            World world = player.getWorld();
            if (!world.isClient) {
                ItemEntity entity = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), recipe.getOutput(world.getRegistryManager()).copy());
                entity.setPickupDelay(0);
                world.spawnEntity(entity);
            }
            // Update, otherwise the client display is incorrect
            player.playerScreenHandler.updateToClient();
            if (player instanceof ServerPlayerEntity) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, new ServerMessageCraft(this.syncId));
            }
        });
    }
}
