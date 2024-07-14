package com.tacz.guns.client.resource.loader.index;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.pojo.GunIndexPOJO;
import net.minecraft.util.Identifier;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static com.tacz.guns.client.resource.ClientGunPackLoader.GUN_INDEX;

public final class ClientGunIndexLoader {
    private static final Marker MARKER = MarkerFactory.getMarker("ClientGunIndexLoader");

    public static void loadGunIndex() {
        TimelessAPI.getAllCommonGunIndex().forEach(index -> {
            Identifier id = index.getKey();
            GunIndexPOJO pojo = index.getValue().getPojo();
            try {
                GUN_INDEX.put(id, ClientGunIndex.getInstance(pojo));
            } catch (IllegalArgumentException exception) {
                GunMod.LOGGER.warn(MARKER, "{} index file read fail!", id);
                exception.printStackTrace();
            }
        });
    }
}
