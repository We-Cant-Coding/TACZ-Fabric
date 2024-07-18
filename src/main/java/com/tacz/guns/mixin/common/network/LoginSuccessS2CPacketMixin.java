package com.tacz.guns.mixin.common.network;

import com.tacz.guns.entity.sync.core.SyncedDataKey;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import com.tacz.guns.api.mixin.SyncedEntityDataMapping;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(LoginSuccessS2CPacket.class)
public class LoginSuccessS2CPacketMixin implements SyncedEntityDataMapping {
    @Unique
    private Map<Identifier, List<Pair<Identifier, Integer>>> keyMap;

    @Inject(method = "write", at = @At("TAIL"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        Set<SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getKeys();
        buf.writeInt(keys.size());
        keys.forEach(key -> {
            int id = SyncedEntityData.instance().getInternalId(key);
            buf.writeIdentifier(key.classKey().id());
            buf.writeIdentifier(key.id());
            buf.writeVarInt(id);
        });
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("TAIL"))
    private void read(PacketByteBuf buf, CallbackInfo ci) {
        int size = buf.readInt();
        Map<Identifier, List<Pair<Identifier, Integer>>> keyMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Identifier classId = buf.readIdentifier();
            Identifier keyId = buf.readIdentifier();
            int id = buf.readVarInt();
            keyMap.computeIfAbsent(classId, c -> new ArrayList<>()).add(Pair.of(keyId, id));
        }
        this.keyMap = keyMap;
    }

    @Override
    public Map<Identifier, List<Pair<Identifier, Integer>>> tacz$getKeymap() {
        return keyMap;
    }
}
