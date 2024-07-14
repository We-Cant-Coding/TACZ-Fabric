package com.tacz.guns.util;

import com.tacz.guns.GunMod;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public final class GetJarResources {
    private GetJarResources() {
    }

    /**
     * Copy the files of this module to the specified folder. The original file will be forcibly overwritten.
     *
     * @param srcPath The address of the source file in the jar
     * @param root    The root directory to which you want to copy
     * @param path    Path after copying
     */
    public static void copyModFile(String srcPath, Path root, String path) {
        URL url = GunMod.class.getResource(srcPath);
        try {
            if (url != null) {
                FileUtils.copyURLToFile(url, root.resolve(path).toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy the folder of this module to the specified folder. The original folder will be forcibly overwritten.
     *
     * @param srcPath The address of the source file in the jar
     * @param root    The root directory to which you want to copy
     * @param path    Path after copying
     */
    public static void copyModDirectory(Class<?> resourceClass, String srcPath, Path root, String path) {
        URL url = resourceClass.getResource(srcPath);
        try {
            if (url != null) {
                copyFolder(url.toURI(), root.resolve(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copy the folder of this module to the specified folder. The original folder will be forcibly overwritten.
     *
     * @param srcPath The address of the source file in the jar
     * @param root    The root directory to which you want to copy
     * @param path    Path after copying
     */
    public static void copyModDirectory(String srcPath, Path root, String path) {
        copyModDirectory(GunMod.class, srcPath, root, path);
    }

    @Nullable
    public static InputStream readModFile(String filePath) {
        URL url = GunMod.class.getResource(filePath);
        try {
            if (url != null) {
                return url.openStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void copyFolder(URI sourceURI, Path targetPath) throws IOException {
        if (Files.isDirectory(targetPath)) {
            // Delete the original folder for a forced overwrite effect
            deleteFiles(targetPath);
        }
        // Use Files.walk() to iterate through the contents of a folder
        try (Stream<Path> stream = Files.walk(Paths.get(sourceURI), Integer.MAX_VALUE)) {
            stream.forEach(source -> {
                // Generate target paths
                Path target = targetPath.resolve(sourceURI.relativize(source.toUri()).toString());
                try {
                    // Copying files or folders
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    // Handling of exceptions, e.g. permissions issues, etc.
                    e.printStackTrace();
                }
            });
        }
    }

    private static void deleteFiles(Path targetPath) throws IOException {
        Files.walkFileTree(targetPath, new SimpleFileVisitor<>() {
            // Go ahead and traverse the deleted files.
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            // Iterate through the deleted directories again
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}