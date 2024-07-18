package com.tacz.guns;

import com.tacz.guns.api.event.server.EntityRemoveEvent;
import com.tacz.guns.entity.sync.core.DataHolderCapabilityProvider;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class GunModComponents implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(Entity.class, DataHolderCapabilityProvider.CAPABILITY)
                .filter(SyncedEntityData.instance()::hasSyncedDataKey)
                .end(entity -> new DataHolderCapabilityProvider());
    }

    public static void init() {
        EntityRemoveEvent.EVENT.register(event -> {
            var entity = event.getEntity();
            if (!(entity instanceof ServerPlayerEntity)) {
                DataHolderCapabilityProvider.CAPABILITY.maybeGet(entity).ifPresent(DataHolderCapabilityProvider::invalidate);
            }
        });
    }
}
