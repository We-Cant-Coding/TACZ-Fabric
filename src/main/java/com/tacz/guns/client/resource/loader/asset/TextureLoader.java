package com.tacz.guns.client.resource.loader.asset;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.texture.FilePackTexture;
import com.tacz.guns.client.resource.texture.ZipPackTexture;
import com.tacz.guns.util.TacPathVisitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class TextureLoader {
    private static final Marker MARKER = MarkerFactory.getMarker("TextureLoader");
    private static final Pattern TEXTURE_PATTERN = Pattern.compile("^(\\w+)/textures/([\\w/]+)\\.png$");

    public static boolean load(ZipFile zipFile, String zipPath) {
        Matcher matcher = TEXTURE_PATTERN.matcher(zipPath);
        if (matcher.find()) {
            String ori_namespace = matcher.group(1);
            String namespace = TacPathVisitor.checkNamespace(ori_namespace);
            String path = matcher.group(2);
            ZipEntry entry = zipFile.getEntry(zipPath);
            if (entry == null) {
                GunMod.LOGGER.warn(MARKER, "{} file don't exist", zipPath);
                return false;
            }
            Identifier ori_id = new Identifier(ori_namespace, path);
            Identifier id = new Identifier(namespace, path);
            ZipPackTexture zipPackTexture = new ZipPackTexture(ori_id, zipFile.getName());
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, zipPackTexture);
            return true;
        }
        return false;
    }

    public static void load(File root) {
        Path filePath = root.toPath().resolve("textures");
        if (Files.isDirectory(filePath)) {
            TacPathVisitor visitor = new TacPathVisitor(filePath.toFile(), root.getName(), ".png", (id, file) -> {
                FilePackTexture filePackTexture = new FilePackTexture(id, file);
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, filePackTexture);
            });
            try {
                Files.walkFileTree(filePath, visitor);
            } catch (Exception e) {
                GunMod.LOGGER.warn(MARKER, "Failed to walk file tree: {}", filePath);
                e.printStackTrace();
            }
        }
    }
}
