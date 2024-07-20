package com.tacz.guns.mixin.common.network;

import com.mojang.authlib.GameProfile;
import com.tacz.guns.GunMod;
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

    @Inject(method = "write", at = @At("RETURN"))
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

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
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
        if (keyMap == null) {
            Map<Identifier, List<Pair<Identifier, Integer>>> keyMap = new HashMap<>();
            Set<SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getKeys();
            keys.forEach(key -> {
                Identifier classId = key.classKey().id();
                Identifier keyId = key.id();
                int id = SyncedEntityData.instance().getInternalId(key);
                keyMap.computeIfAbsent(classId, c -> new ArrayList<>()).add(Pair.of(keyId, id));
            });
            return keyMap;
        }
        return keyMap;
    }
}
