package com.tacz.guns.util.item.wrapper;

import net.minecraft.entity.player.PlayerInventory;

public class PlayerOffhandInvWrapper extends RangedWrapper {
    public PlayerOffhandInvWrapper(PlayerInventory inv) {
        super(new InvWrapper(inv), inv.main.size() + inv.armor.size(), inv.main.size() + inv.armor.size() + inv.offHand.size());
    }
}
