package com.tacz.guns.network.message.handshake;

import com.tacz.guns.GunMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Acknowledge {
    public static final Marker ACKNOWLEDGE = MarkerFactory.getMarker("HANDSHAKE_ACKNOWLEDGE");

    public static void receive(PacketByteBuf ignoredBuf, PacketSender ignoredSender) {
        GunMod.LOGGER.debug(ACKNOWLEDGE, "Received acknowledgement from client");
    }
}
