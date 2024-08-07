package com.tacz.guns.network.packets.s2c.handshake;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.sync.core.SyncedDataKey;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import com.tacz.guns.network.IHandshakeMessage;
import com.tacz.guns.network.packets.c2s.handshake.AcknowledgeC2SPacket;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;
import java.util.function.Consumer;

public class SyncedEntityDataMappingS2CPacket implements IHandshakeMessage {
    public static final PacketType<SyncedEntityDataMappingS2CPacket> TYPE = PacketType.create(new Identifier(GunMod.MOD_ID, "synced_entity_data_mapping"), SyncedEntityDataMappingS2CPacket::new);
    private static final Marker HANDSHAKE = MarkerFactory.getMarker("TACZ_HANDSHAKE");
    private Map<Identifier, List<Pair<Identifier, Integer>>> keyMap;

    public SyncedEntityDataMappingS2CPacket() {}

    public SyncedEntityDataMappingS2CPacket(PacketByteBuf buf) {
        int size = buf.readInt();
        keyMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Identifier classId = buf.readIdentifier();
            Identifier keyId = buf.readIdentifier();
            int id = buf.readVarInt();
            keyMap.computeIfAbsent(classId, k -> new ArrayList<>()).add(Pair.of(keyId, id));
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        Set<SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getKeys();
        buf.writeInt(keys.size());
        keys.forEach(key -> {
            int id = SyncedEntityData.instance().getInternalId(key);
            buf.writeIdentifier(key.classKey().id());
            buf.writeIdentifier(key.id());
            buf.writeVarInt(id);
        });
    }

    @Override
    public IResponsePacket handle(ClientConnection connection, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        GunMod.LOGGER.debug(HANDSHAKE, "Received synced key mappings from server");
        if (!SyncedEntityData.instance().updateMappings(keyMap)) {
            connection.disconnect(Text.literal("Connection closed - [TacZ] Received unknown synced data keys."));
        }
        return new AcknowledgeC2SPacket();
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
