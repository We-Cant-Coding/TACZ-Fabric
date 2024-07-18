package com.tacz.guns.forge.items.wrapper;

import com.tacz.guns.forge.items.wrapper.InvWrapper;
import com.tacz.guns.forge.items.wrapper.RangedWrapper;
import net.minecraft.entity.player.PlayerInventory;

public class PlayerOffhandInvWrapper extends RangedWrapper {
    public PlayerOffhandInvWrapper(PlayerInventory inv) {
        super(new InvWrapper(inv), inv.main.size() + inv.armor.size(), inv.main.size() + inv.armor.size() + inv.offHand.size());
    }
}
