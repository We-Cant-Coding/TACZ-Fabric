package com.tacz.guns.client.event;

import com.tacz.guns.client.resource.ClientReloadManager;
import com.tacz.guns.client.resource.InternalAssetLoader;
import com.tacz.guns.forge.TextureStitchEvent;
import net.minecraft.util.Identifier;

public class ReloadResourceEvent {
    public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");

    public static void onTextureStitchEventPost(TextureStitchEvent event) {
        if (BLOCK_ATLAS_TEXTURE.equals(event.atlas().getId())) {
            // InternalAssetLoader needs to load some default animations, models, which need to be loaded before the gun package.
            InternalAssetLoader.onResourceReload();
            ClientReloadManager.reloadAllPack();
        }
    }
}
