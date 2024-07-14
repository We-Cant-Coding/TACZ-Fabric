package com.tacz.guns.client.resource.loader.index;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.resource.index.ClientAmmoIndex;
import com.tacz.guns.resource.pojo.AmmoIndexPOJO;
import net.minecraft.util.Identifier;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static com.tacz.guns.client.resource.ClientGunPackLoader.AMMO_INDEX;

public final class ClientAmmoIndexLoader {
    private static final Marker MARKER = MarkerFactory.getMarker("ClientGunIndexLoader");

    public static void loadAmmoIndex() {
        TimelessAPI.getAllCommonAmmoIndex().forEach(index -> {
            Identifier id = index.getKey();
            AmmoIndexPOJO pojo = index.getValue().getPojo();
            try {
                AMMO_INDEX.put(id, ClientAmmoIndex.getInstance(pojo));
            } catch (IllegalArgumentException exception) {
                GunMod.LOGGER.warn(MARKER, "{} index file read fail!", id);
                exception.printStackTrace();
            }
        });
    }
}
