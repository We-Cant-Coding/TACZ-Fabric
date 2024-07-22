package com.tacz.guns.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NonNullPredicate<T> {
    boolean test(@NotNull T var1);
}
