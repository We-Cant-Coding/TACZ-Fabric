package com.tacz.guns.api.resource;

import com.google.common.collect.Lists;

import java.nio.file.Paths;
import java.util.List;

/**
 * Used to register gun packs that need to be extracted and released for use by other auxiliary modules.
 */
public final class ResourceManager {
    /**
     * Holds all paths of gun packs to be extracted.
     */
    public static final List<ExtraEntry> EXTRA_ENTRIES = Lists.newArrayList();

    /**
     * Registers a gun pack that needs to be extracted.
     *
     * @param modMainClass    The main class of the auxiliary module.
     * @param extraFolderPath The folder to be extracted, for example, "/assets/tacz/custom/tacz_default_gun".
     *                        This indicates that the 'tacz_default_gun' folder will be extracted and placed
     *                        in the firearms pack installation directory.
     */
    public static void registerExtraGunPack(Class<?> modMainClass, String extraFolderPath) {
        EXTRA_ENTRIES.add(new ExtraEntry(modMainClass, extraFolderPath, Paths.get(extraFolderPath).getFileName().toString()));
    }

    /**
     * Entry for extraction.
     */
    public record ExtraEntry(Class<?> modMainClass, String srcPath, String extraDirName) {
    }
}
