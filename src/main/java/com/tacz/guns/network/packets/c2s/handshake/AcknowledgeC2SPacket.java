package com.tacz.guns.network.packets.c2s.handshake;

import com.tacz.guns.GunMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class AcknowledgeC2SPacket {
    public static final Marker ACKNOWLEDGE = MarkerFactory.getMarker("HANDSHAKE_ACKNOWLEDGE");

    public static void receive(PacketByteBuf ignoredBuf, PacketSender ignoredSender) {
        GunMod.LOGGER.debug(ACKNOWLEDGE, "Received acknowledgement from client");
    }
}
