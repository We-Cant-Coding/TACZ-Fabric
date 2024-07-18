package com.tacz.guns.client.init;

import com.tacz.guns.api.client.other.ThirdPersonManager;
import com.tacz.guns.client.download.ClientGunPackDownloadManager;
import com.tacz.guns.client.input.*;
import com.tacz.guns.client.tooltip.ClientAmmoBoxTooltip;
import com.tacz.guns.client.tooltip.ClientAttachmentItemTooltip;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import com.tacz.guns.compat.shouldersurfing.ShoulderSurfingCompat;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.inventory.tooltip.AmmoBoxTooltip;
import com.tacz.guns.inventory.tooltip.AttachmentItemTooltip;
import com.tacz.guns.inventory.tooltip.GunTooltip;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.mixin.client.MinecraftClientAccessor;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipData;

public class ClientSetup {

    private static void keybindingRegister() {
        KeyBindingHelper.registerKeyBinding(InspectKey.INSPECT_KEY);
        KeyBindingHelper.registerKeyBinding(ReloadKey.RELOAD_KEY);
        KeyBindingHelper.registerKeyBinding(ShootKey.SHOOT_KEY);
        KeyBindingHelper.registerKeyBinding(InteractKey.INTERACT_KEY);
        KeyBindingHelper.registerKeyBinding(FireSelectKey.FIRE_SELECT_KEY);
        KeyBindingHelper.registerKeyBinding(AimKey.AIM_KEY);
        KeyBindingHelper.registerKeyBinding(RefitKey.REFIT_KEY);
        KeyBindingHelper.registerKeyBinding(ZoomKey.ZOOM_KEY);
        KeyBindingHelper.registerKeyBinding(MeleeKey.MELEE_KEY);
        KeyBindingHelper.registerKeyBinding(ConfigKey.OPEN_CONFIG_KEY);

    }

    private static TooltipComponent tooltipComponent(TooltipData tooltip) {
        if (tooltip instanceof GunTooltip gunTooltip) {
            return new ClientGunTooltip(gunTooltip);
        }
        if (tooltip instanceof AmmoBoxTooltip ammoBoxTooltip) {
            return new ClientAmmoBoxTooltip(ammoBoxTooltip);
        }
        if (tooltip instanceof AttachmentItemTooltip attachmentItemTooltip) {
            return new ClientAttachmentItemTooltip(attachmentItemTooltip);
        }
        return null;
    }

    public static void onClientSetup() {
        keybindingRegister();
        TooltipComponentCallback.EVENT.register(ClientSetup::tooltipComponent);

        // 注册自己的的硬编码第三人称动画
        ThirdPersonManager.registerDefault();

        // 注册颜色
        MinecraftClient.getInstance().execute(() ->
                ((MinecraftClientAccessor)MinecraftClient.getInstance()).getItemColors().register(AmmoBoxItem::getColor, ModItems.AMMO_BOX));

        // 注册变种
        ModelPredicateProviderRegistry.register(ModItems.AMMO_BOX, AmmoBoxItem.PROPERTY_NAME, AmmoBoxItem::getStatue);

        // 初始化自己的枪包下载器
        ClientGunPackDownloadManager.init();

        // 与 player animator 的兼容
        PlayerAnimatorCompat.init();

        // 与 Shoulder Surfing Reloaded 的兼容
        ShoulderSurfingCompat.init();
    }
}
