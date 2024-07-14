package com.tacz.guns.entity.sync.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Framework provided serializers used for creating a {@link SyncedDataKey}. This covers all
 * primitive types and common objects. You can create your custom serializer by implementing
 * {@link IDataSerializer}.
 * <p>
 * Author: MrCrayfish
 * Open source at <a href="https://github.com/MrCrayfish/Framework">Github</a> under LGPL License.
 */
public class Serializers {
    public static final IDataSerializer<Boolean> BOOLEAN = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Boolean value) {
            buf.writeBoolean(value);
        }

        @Override
        public Boolean read(PacketByteBuf buf) {
            return buf.readBoolean();
        }

        @Override
        public NbtElement write(Boolean value) {
            return NbtByte.of(value);
        }

        @Override
        public Boolean read(NbtElement tag) {
            return ((NbtByte) tag).byteValue() != 0;
        }
    };

    public static final IDataSerializer<Byte> BYTE = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Byte value) {
            buf.writeByte(value);
        }

        @Override
        public Byte read(PacketByteBuf buf) {
            return buf.readByte();
        }

        @Override
        public NbtElement write(Byte value) {
            return NbtByte.of(value);
        }

        @Override
        public Byte read(NbtElement tag) {
            return ((NbtByte) tag).byteValue();
        }
    };

    public static final IDataSerializer<Short> SHORT = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Short value) {
            buf.writeShort(value);
        }

        @Override
        public Short read(PacketByteBuf buf) {
            return buf.readShort();
        }

        @Override
        public NbtElement write(Short value) {
            return NbtShort.of(value);
        }

        @Override
        public Short read(NbtElement tag) {
            return ((NbtShort) tag).shortValue();
        }
    };

    public static final IDataSerializer<Integer> INTEGER = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Integer value) {
            buf.writeVarInt(value);
        }

        @Override
        public Integer read(PacketByteBuf buf) {
            return buf.readVarInt();
        }

        @Override
        public NbtElement write(Integer value) {
            return NbtInt.of(value);
        }

        @Override
        public Integer read(NbtElement tag) {
            return ((NbtInt) tag).intValue();
        }
    };

    public static final IDataSerializer<Long> LONG = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Long value) {
            buf.writeLong(value);
        }

        @Override
        public Long read(PacketByteBuf buf) {
            return buf.readLong();
        }

        @Override
        public NbtElement write(Long value) {
            return NbtLong.of(value);
        }

        @Override
        public Long read(NbtElement tag) {
            return ((NbtLong) tag).longValue();
        }
    };

    public static final IDataSerializer<Float> FLOAT = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Float value) {
            buf.writeFloat(value);
        }

        @Override
        public Float read(PacketByteBuf buf) {
            return buf.readFloat();
        }

        @Override
        public NbtElement write(Float value) {
            return NbtFloat.of(value);
        }

        @Override
        public Float read(NbtElement tag) {
            return ((NbtFloat) tag).floatValue();
        }
    };

    public static final IDataSerializer<Double> DOUBLE = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Double value) {
            buf.writeDouble(value);
        }

        @Override
        public Double read(PacketByteBuf buf) {
            return buf.readDouble();
        }

        @Override
        public NbtElement write(Double value) {
            return NbtDouble.of(value);
        }

        @Override
        public Double read(NbtElement tag) {
            return ((NbtDouble) tag).doubleValue();
        }
    };

    public static final IDataSerializer<Character> CHARACTER = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Character value) {
            buf.writeChar(value);
        }

        @Override
        public Character read(PacketByteBuf buf) {
            return buf.readChar();
        }

        @Override
        public NbtElement write(Character value) {
            return NbtInt.of(value);
        }

        @Override
        public Character read(NbtElement tag) {
            return (char) ((NbtInt) tag).intValue();
        }
    };

    public static final IDataSerializer<String> STRING = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, String value) {
            buf.writeString(value);
        }

        @Override
        public String read(PacketByteBuf buf) {
            return buf.readString();
        }

        @Override
        public NbtElement write(String value) {
            return NbtString.of(value);
        }

        @Override
        public String read(NbtElement tag) {
            return tag.asString();
        }
    };

    public static final IDataSerializer<NbtCompound> TAG_COMPOUND = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, NbtCompound value) {
            buf.writeNbt(value);
        }

        @Override
        public NbtCompound read(PacketByteBuf buf) {
            return buf.readNbt();
        }

        @Override
        public NbtElement write(NbtCompound value) {
            return value;
        }

        @Override
        public NbtCompound read(NbtElement tag) {
            return (NbtCompound) tag;
        }
    };

    public static final IDataSerializer<BlockPos> BLOCK_POS = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, BlockPos value) {
            buf.writeBlockPos(value);
        }

        @Override
        public BlockPos read(PacketByteBuf buf) {
            return buf.readBlockPos();
        }

        @Override
        public NbtElement write(BlockPos value) {
            return NbtLong.of(value.asLong());
        }

        @Override
        public BlockPos read(NbtElement tag) {
            return BlockPos.fromLong(((NbtLong) tag).longValue());
        }
    };

    public static final IDataSerializer<java.util.UUID> UUID = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, UUID value) {
            buf.writeUuid(value);
        }

        @Override
        public UUID read(PacketByteBuf buf) {
            return buf.readUuid();
        }

        @Override
        public NbtElement write(UUID value) {
            NbtCompound compound = new NbtCompound();
            compound.putLong("Most", value.getMostSignificantBits());
            compound.putLong("Least", value.getLeastSignificantBits());
            return compound;
        }

        @Override
        public UUID read(NbtElement tag) {
            NbtCompound compound = (NbtCompound) tag;
            return new UUID(compound.getLong("Most"), compound.getLong("Least"));
        }
    };

    public static final IDataSerializer<ItemStack> ITEM_STACK = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, ItemStack value) {
            buf.writeItemStack(value);
        }

        @Override
        public ItemStack read(PacketByteBuf buf) {
            return buf.readItemStack();
        }

        @Override
        public NbtElement write(ItemStack value) {
            return value.writeNbt(new NbtCompound());
        }

        @Override
        public ItemStack read(NbtElement tag) {
            return ItemStack.fromNbt((NbtCompound) tag);
        }
    };

    public static final IDataSerializer<Identifier> RESOURCE_LOCATION = new IDataSerializer<>() {
        @Override
        public void write(PacketByteBuf buf, Identifier value) {
            buf.writeIdentifier(value);
        }

        @Override
        public Identifier read(PacketByteBuf buf) {
            return buf.readIdentifier();
        }

        @Override
        public NbtElement write(Identifier value) {
            return NbtString.of(value.toString());
        }

        @Override
        public Identifier read(NbtElement tag) {
            return Identifier.tryParse(tag.asString());
        }
    };
}
