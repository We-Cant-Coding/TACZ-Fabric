package com.tacz.guns.event;

import com.tacz.guns.api.item.IGun;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.concurrent.atomic.AtomicBoolean;

public class PreventGunClick {
    public static void onLeftClickBlock(PlayerEntity player, AtomicBoolean cancel) {
        ItemStack itemInHand = player.getStackInHand(Hand.MAIN_HAND);
        if (itemInHand.getItem() instanceof IGun) {
            cancel.set(true);
        }
    }
}
