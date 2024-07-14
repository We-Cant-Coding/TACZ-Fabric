package com.tacz.guns.compat.iris.pbr;

import com.tacz.guns.client.resource.texture.FilePackTexture;
import net.irisshaders.iris.texture.pbr.PBRType;
import net.irisshaders.iris.texture.pbr.loader.PBRTextureLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class FilePackTexturePBRLoader implements PBRTextureLoader<FilePackTexture> {
    @Override
    public void load(FilePackTexture filePackTexture, ResourceManager resourceManager, PBRTextureConsumer pbrTextureConsumer) {
        Identifier id = filePackTexture.getRegisterId();
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
