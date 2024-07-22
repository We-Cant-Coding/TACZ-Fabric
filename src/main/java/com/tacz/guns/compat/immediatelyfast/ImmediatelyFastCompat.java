package com.tacz.guns.compat.immediatelyfast;

import com.tacz.guns.init.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.raphimc.immediatelyfastapi.ImmediatelyFastApi;

public class ImmediatelyFastCompat {
    private static final String MOD_ID = "immediatelyfast";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static void renderHotbarItem(ItemStack stack, boolean pre) {
        if (!INSTALLED) return;
        if (!stack.isOf(ModItems.GUN_SMITH_TABLE)) return;
        if (pre) {
            if (ImmediatelyFastApi.getApiImpl().getRuntimeConfig().getBoolean("hud_batching", true)) {
                if (ImmediatelyFastApi.getApiImpl().getBatching().isHudBatching()) {
                    ImmediatelyFastApi.getApiImpl().getBatching().endHudBatching();
                }
            }
        } else {
            if (ImmediatelyFastApi.getApiImpl().getRuntimeConfig().getBoolean("hud_batching", true)) {
                ImmediatelyFastApi.getApiImpl().getBatching().beginHudBatching();
            }
        }
    }
}
