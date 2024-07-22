package com.tacz.guns.event;

import com.tacz.guns.api.item.IGun;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerInteractionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PreventGunClick {
    public static void onLeftClickBlock(PlayerInteractionEvents.LeftClickBlock event) {
        // 只要主手有枪，那么禁止交互
        ItemStack itemInHand = event.getEntity().getStackInHand(Hand.MAIN_HAND);
        if (itemInHand.getItem() instanceof IGun) {
            event.setCanceled(true);
        }
    }
}
