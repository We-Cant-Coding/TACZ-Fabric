package com.tacz.guns;

import com.tacz.guns.client.init.*;
import com.tacz.guns.init.CommonRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class GunModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEvents.init();
		ClientSetup.onClientSetup();
		ModContainerScreen.clientSetup();
		ModEntitiesRender.entityRenderers();
		ParticleRegistry.registerParticleFactory();
		ModItemRenderer.itemRenderers();

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> CommonRegistry.onLoadComplete());
	}
}