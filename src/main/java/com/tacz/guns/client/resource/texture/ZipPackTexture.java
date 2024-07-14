package com.tacz.guns.client.resource.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipPackTexture extends AbstractTexture {
    private final Identifier registerId;
    private final Path zipFilePath;

    public ZipPackTexture(Identifier registerId, String zipFilePath) {
        this.registerId = registerId;
        this.zipFilePath = Paths.get(zipFilePath);
    }

    @Override
    public void load(@NotNull ResourceManager manager) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(this::doLoad);
        } else {
            this.doLoad();
        }
    }

    private void doLoad() {
        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            ZipEntry entry = zipFile.getEntry(String.format("%s/textures/%s.png", registerId.getNamespace(), registerId.getPath()));
            if (entry == null) {
                return;
            }
            try (InputStream stream = zipFile.getInputStream(entry)) {
                NativeImage imageIn = NativeImage.read(stream);
                int width = imageIn.getWidth();
                int height = imageIn.getHeight();
                TextureUtil.prepareImage(this.getGlId(), 0, width, height);
                imageIn.upload(0, 0, 0, 0, 0, width, height, false, false, false, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Identifier getRegisterId() {
        return registerId;
    }
}
