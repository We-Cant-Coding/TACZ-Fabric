package com.tacz.guns.block.entity;

import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public static final BlockEntityType<GunSmithTableBlockEntity> TYPE = BlockEntityType.Builder.create(GunSmithTableBlockEntity::new, ModBlocks.GUN_SMITH_TABLE).build(null);

    public GunSmithTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

//    @Override
//    @Environment(EnvType.CLIENT)
//    public AABB getRenderBoundingBox() {
//        return new AABB(worldPosition.offset(-2, 0, -2), worldPosition.offset(2, 1, 2));
//    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Gun Smith Table");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new GunSmithTableMenu(id, inventory);
    }
}
