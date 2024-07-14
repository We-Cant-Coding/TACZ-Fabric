package com.tacz.guns.block.entity;

import com.mojang.authlib.GameProfile;
import com.tacz.guns.block.TargetBlock;
import com.tacz.guns.config.common.OtherConfig;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.tacz.guns.block.TargetBlock.OUTPUT_POWER;
import static com.tacz.guns.block.TargetBlock.STAND;

public class TargetBlockEntity extends BlockEntity implements Nameable {
    public static final BlockEntityType<TargetBlockEntity> TYPE = BlockEntityType.Builder.create(TargetBlockEntity::new, ModBlocks.TARGET).build(null);
    /**
     * 标靶复位时间，暂定为 5 秒
     */
    private static final int RESET_TIME = 5 * 20;
    private static final String OWNER_TAG = "Owner";
    private static final String CUSTOM_NAME_TAG = "CustomName";
    public float rot = 0;
    public float oRot = 0;
    private @Nullable GameProfile owner;
    private @Nullable Text name;

    public TargetBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    public static void clientTick(World level, BlockPos pos, BlockState state, TargetBlockEntity pBlockEntity) {
        pBlockEntity.oRot = pBlockEntity.rot;
        if (state.get(STAND)) {
            pBlockEntity.rot = Math.max(pBlockEntity.rot - 18, 0);
        } else {
            pBlockEntity.rot = Math.min(pBlockEntity.rot + 45, 90);
        }
    }

    @Nullable
    public GameProfile getOwner() {
        return owner;
    }

    public void setOwner(@Nullable GameProfile owner) {
        this.owner = owner;
        SkullBlockEntity.loadProperties(this.owner, gameProfile -> {
            this.owner = gameProfile;
            this.refresh();
        });
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains(OWNER_TAG, NbtElement.COMPOUND_TYPE)) {
            this.owner = NbtHelper.toGameProfile(tag.getCompound(OWNER_TAG));
        }
        if (tag.contains(CUSTOM_NAME_TAG, NbtElement.STRING_TYPE)) {
            this.name = Text.Serializer.fromJson(tag.getString(CUSTOM_NAME_TAG));
        }
    }

    @Override
    protected void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (owner != null) {
            tag.put(OWNER_TAG, NbtHelper.writeGameProfile(new NbtCompound(), owner));
        }
        if (this.name != null) {
            tag.putString(CUSTOM_NAME_TAG, Text.Serializer.toJson(this.name));
        }
    }

    @Override
    public Text getName() {
        return this.name != null ? this.name : Text.empty();
    }

    @Nullable
    @Override
    public Text getCustomName() {
        return this.name;
    }

    public void setCustomName(Text name) {
        this.name = name;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void refresh() {
        this.markDirty();
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
    }

//    @Override
//    public Box getBoundingBox() {
//        return new Box(pos.offset(-2, 0, -2), pos.offset(2, 2, 2));
//    }

    public void hit(World level, BlockState state, BlockHitResult hit, boolean isUpperBlock) {
        if (this.world != null && state.get(STAND)) {
            BlockPos blockPos = hit.getBlockPos();
            // 如果是击中上方，把状态移动到下方处理
            if (isUpperBlock) {
                blockPos = blockPos.down();
                state = level.getBlockState(blockPos);
            }
            int redstoneStrength = TargetBlock.getRedstoneStrength(hit, isUpperBlock);
            level.setBlockState(blockPos, state.with(STAND, false).with(OUTPUT_POWER, redstoneStrength), Block.NOTIFY_ALL);
            level.scheduleBlockTick(blockPos, state.getBlock(), RESET_TIME);
            // 原版的声音传播距离由 volume 决定
            // 当声音大于 1 时，距离为 = 16 * volume
            float volume = OtherConfig.TARGET_SOUND_DISTANCE.get() / 16.0f;
            volume = Math.max(volume, 0);
            level.playSound(null, blockPos, ModSounds.TARGET_HIT, SoundCategory.BLOCKS, volume, this.world.random.nextFloat() * 0.1F + 0.9F);
        }
    }
}
