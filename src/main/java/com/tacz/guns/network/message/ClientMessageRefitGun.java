package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.util.AttachmentDataUtils;
import com.tacz.guns.util.EnvironmentUtil;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ClientMessageRefitGun implements FabricPacket {
    public static final PacketType<ClientMessageRefitGun> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "client_message_refit_gun"), ClientMessageRefitGun::new);

    private final int attachmentSlotIndex;
    private final int gunSlotIndex;
    private final AttachmentType attachmentType;

    public ClientMessageRefitGun(PacketByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readEnumConstant(AttachmentType.class));
    }

    public ClientMessageRefitGun(int attachmentSlotIndex, int gunSlotIndex, AttachmentType attachmentType) {
        this.attachmentSlotIndex = attachmentSlotIndex;
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(attachmentSlotIndex);
        buf.writeInt(gunSlotIndex);
        buf.writeEnumConstant(attachmentType);
    }

    public void handle(ServerPlayerEntity player, PacketSender sender) {
        if (EnvironmentUtil.isServer()) {
            if (player == null) return;
            PlayerInventory inventory = player.getInventory();
            ItemStack attachmentItem = inventory.getStack(attachmentSlotIndex);
            ItemStack gunItem = inventory.getStack(gunSlotIndex);
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun != null) {
                if (iGun.allowAttachment(gunItem, attachmentItem)) {
                    ItemStack oldAttachmentItem = iGun.getAttachment(gunItem, attachmentType);
                    iGun.installAttachment(gunItem, attachmentItem);
                    inventory.setStack(attachmentSlotIndex, oldAttachmentItem);
                    // If unloading an expanding magazine that spits out all the bullets
                    if (attachmentType == AttachmentType.EXTENDED_MAG) {
                        dropAllAmmo(player, iGun, gunItem);
                    }
                    player.playerScreenHandler.sendContentUpdates();
                    sender.sendPacket(new ServerMessageRefreshRefitScreen());
                }
            }
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    private static void dropAllAmmo(PlayerEntity player, IGun iGun, ItemStack gunItem) {
        int ammoCount = iGun.getCurrentAmmoCount(gunItem);
        if (ammoCount <= 0) {
            return;
        }
        Identifier gunId = iGun.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
            Identifier ammoId = index.getGunData().getAmmoId();
            // 创造模式，只改变子弹总数，不进行任何卸载弹药逻辑
            if (player.isCreative()) {
                int maxAmmCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, index.getGunData());
                iGun.setCurrentAmmoCount(gunItem, maxAmmCount);
                return;
            }
            TimelessAPI.getCommonAmmoIndex(ammoId).ifPresent(ammoIndex -> {
                int stackSize = ammoIndex.getStackSize();
                int tmpAmmoCount = ammoCount;
                int roundCount = tmpAmmoCount / (stackSize + 1);
                for (int i = 0; i <= roundCount; i++) {
                    int count = Math.min(tmpAmmoCount, stackSize);
                    ItemStack ammoItem = AmmoItemBuilder.create().setId(ammoId).setCount(count).build();
                    if (!ammoItem.isEmpty() && !player.getWorld().isClient) {
                        ItemEntity entityitem = new ItemEntity(player.getWorld(), player.getX(), player.getY() + 0.5D, player.getZ(), ammoItem);
                        entityitem.setPickupDelay(40);
                        entityitem.setVelocity(entityitem.getVelocity().multiply(0.0D, 1.0D, 0.0D));
                        player.getWorld().spawnEntity(entityitem);
                    }
                    tmpAmmoCount -= stackSize;
                }
                iGun.setCurrentAmmoCount(gunItem, 0);
            });
        });
    }
}
