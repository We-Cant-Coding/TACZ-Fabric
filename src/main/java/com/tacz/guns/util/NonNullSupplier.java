package com.tacz.guns.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NonNullSupplier<T> {
    @NotNull
    T get();
}
