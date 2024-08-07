package com.tacz.guns.compat.iris.newly.pbr;

import com.tacz.guns.client.resource.texture.ZipPackTexture;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ZipPackTexturePBRLoader implements PBRTextureLoader<ZipPackTexture> {
    @Override
    public void load(ZipPackTexture zipPackTexture, ResourceManager resourceManager, PBRTextureConsumer pbrTextureConsumer) {
        Identifier id = zipPackTexture.getRegisterId();
        Identifier pbrNormalId = new Identifier(id.getNamespace(), id.getPath() + PBRType.NORMAL.getSuffix());
        Identifier pbrSpecularId = new Identifier(id.getNamespace(), id.getPath() + PBRType.SPECULAR.getSuffix());
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        if (textureManager.textures.containsKey(pbrNormalId)) {
            pbrTextureConsumer.acceptNormalTexture(textureManager.getTexture(pbrNormalId));
        }
        if (textureManager.textures.containsKey(pbrSpecularId)) {
            pbrTextureConsumer.acceptSpecularTexture(textureManager.getTexture(pbrSpecularId));
        }
    }
}
