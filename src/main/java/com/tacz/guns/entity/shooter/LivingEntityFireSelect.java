package com.tacz.guns.entity.shooter;

import com.tacz.guns.api.LogicalSide;
import com.tacz.guns.api.event.common.GunFireSelectEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.packets.s2c.event.GunFireSelectS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class LivingEntityFireSelect {
    private final LivingEntity shooter;
    private final ShooterDataHolder data;

    public LivingEntityFireSelect(LivingEntity shooter, ShooterDataHolder data) {
        this.shooter = shooter;
        this.data = data;
    }

    public void fireSelect() {
        if (data.currentGunItem == null) {
            return;
        }
        ItemStack currentGunItem = data.currentGunItem.get();
        if (!(currentGunItem.getItem() instanceof IGun iGun)) {
            return;
        }
        if (new GunFireSelectEvent(shooter, currentGunItem, LogicalSide.SERVER).post()) {
            return;
        }
        NetworkHandler.sendToTrackingEntity(new GunFireSelectS2CPacket(shooter.getId(), currentGunItem), shooter);
        if (iGun instanceof AbstractGunItem logicGun) {
            logicGun.fireSelect(currentGunItem);
        }
    }
}
