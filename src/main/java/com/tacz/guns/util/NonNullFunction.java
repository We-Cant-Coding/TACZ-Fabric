package com.tacz.guns.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NonNullFunction<T, R> {
    @NotNull
    R apply(@NotNull T var1);
}
