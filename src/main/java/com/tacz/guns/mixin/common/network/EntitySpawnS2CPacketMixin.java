package com.tacz.guns.mixin.common.network;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.mixin.IEntityAdditionalSpawnData;
import com.tacz.guns.api.mixin.IEntitySpawnS2C;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySpawnS2CPacket.class)
public class EntitySpawnS2CPacketMixin implements IEntitySpawnS2C {
    @Unique
    private Entity entity;
    @Unique
    private PacketByteBuf buf = null;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;I)V", at = @At("TAIL"))
    private void initClass(Entity entity, int entityData, CallbackInfo ci) {
        this.entity = entity;
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/math/BlockPos;)V", at = @At("TAIL"))
    private void initClass(Entity entity, int entityData, BlockPos pos, CallbackInfo ci) {
        this.entity = entity;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("TAIL"))
    private void init(PacketByteBuf buf, CallbackInfo ci) {
        if (buf.readableBytes() > 0) {
            this.buf = new PacketByteBuf(buf.readBytes(buf.readableBytes()));
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        GunMod.LOGGER.info("write entity: {}", entity);
        if (entity instanceof IEntityAdditionalSpawnData spawnData) {
            GunMod.LOGGER.info("additional data entity: {}", entity);
            if (this.buf == null) {
                this.buf = PacketByteBufs.create();
            }
            spawnData.writeSpawnData(this.buf);
            buf.writeBytes(this.buf);
        }
    }

    @Override
    @Nullable
    public PacketByteBuf tacz$buf() {
        return buf;
    }
}
