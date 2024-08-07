package com.tacz.guns.util;

import com.tacz.guns.GunMod;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.BiConsumer;

public class TacPathVisitor extends SimpleFileVisitor<Path> {
    private final File root;
    private final String namespace;
    private final String suffix;
    private final BiConsumer<Identifier, Path> consumer;

    public TacPathVisitor(File root, String namespace, String suffix, BiConsumer<Identifier, Path> consumer) {
        this.root = root;
        this.namespace = namespace;
        this.suffix = suffix;
        this.consumer = consumer;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toFile().getName().endsWith(suffix)) {
            String path = PathHandler.getPath(root.toPath(), file, suffix);
            Identifier id = new Identifier(checkNamespace(namespace), path);
            consumer.accept(id, file);
        }

        return FileVisitResult.CONTINUE;
    }

    public static String checkNamespace(String namespace) {
        if (Objects.equals(namespace, GunMod.OLD_MOD_ID)) {
            return GunMod.MOD_ID;
        }
        return namespace;
    }
}
