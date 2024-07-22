package com.tacz.guns.util.item.wrapper;

import net.minecraft.entity.player.PlayerInventory;

public class PlayerInvWrapper extends CombinedInvWrapper {
    public PlayerInvWrapper(PlayerInventory inv) {
        super(new PlayerMainInvWrapper(inv), new PlayerArmorInvWrapper(inv), new PlayerOffhandInvWrapper(inv));
    }
}
