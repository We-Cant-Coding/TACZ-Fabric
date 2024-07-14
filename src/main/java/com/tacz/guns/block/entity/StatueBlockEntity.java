package com.tacz.guns.block.entity;

import com.tacz.guns.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static com.tacz.guns.block.StatueBlock.FACING;

public class StatueBlockEntity extends BlockEntity {
    public static final BlockEntityType<StatueBlockEntity> TYPE = BlockEntityType.Builder.create(StatueBlockEntity::new, ModBlocks.STATUE).build(null);
    private static final String ITEM_TAG = "Item";
    private ItemStack gunItem = ItemStack.EMPTY;

    public StatueBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TYPE, pPos, pBlockState);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState state, StatueBlockEntity statueBlockEntity) {
        if (world.getTime() % 100 == 0 && !statueBlockEntity.gunItem.isEmpty()) {
            Direction direction = state.get(FACING);

            double x = blockPos.getX() + direction.getOffsetX() * 0.75 + 0.5;
            double z = blockPos.getZ() + direction.getOffsetZ() * 0.75 + 0.5;

            double dx = -0.02 + world.random.nextDouble() * 0.04;
            double dz = -0.02 + world.random.nextDouble() * 0.04;
            double dy = -0.02 + world.random.nextDouble() * 0.04;

            world.addParticle(ParticleTypes.END_ROD, x, blockPos.getY() + 2.25, z, dx, dy, dz);
        }
    }

    public ItemStack getGunItem() {
        return gunItem;
    }

    public void setGun(ItemStack stack) {
        this.dropItem();
        this.gunItem = stack.copy();
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
        this.markDirty();
    }

    public void dropItem() {
        if (!gunItem.isEmpty() && world != null) {
            Direction direction = getCachedState().get(FACING);
            Block.dropStack(world, pos.offset(direction).up(), gunItem);
            this.gunItem = ItemStack.EMPTY;
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
            this.markDirty();
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains(ITEM_TAG, NbtElement.COMPOUND_TYPE)) {
            this.gunItem = ItemStack.fromNbt(tag.getCompound(ITEM_TAG));
        }
    }

    @Override
    protected void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put(ITEM_TAG, gunItem.writeNbt(new NbtCompound()));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        tag.put(ITEM_TAG, gunItem.writeNbt(new NbtCompound()));
        return tag;
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        return new AABB(pos.offset(-2, 0, -2), pos.offset(2, 2, 2));
//    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
