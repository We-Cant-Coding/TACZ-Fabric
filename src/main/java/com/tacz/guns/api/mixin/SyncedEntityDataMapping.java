package com.tacz.guns.api.mixin;

import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface SyncedEntityDataMapping {
    Map<Identifier, List<Pair<Identifier, Integer>>> tacz$getKeymap();
}
