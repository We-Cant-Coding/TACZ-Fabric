package com.tacz.guns.resource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.resource.pojo.data.attachment.AttachmentData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum CommonAssetManager {
    INSTANCE;

    /**
     * Storage of firearms data
     */
    private final Map<Identifier, GunData> gunData = Maps.newHashMap();

    private final Map<Identifier, AttachmentData> attachmentData = Maps.newHashMap();

    private final Map<Identifier, GunSmithTableRecipe> gunSmithTableRecipes = Maps.newHashMap();

    private final Map<Identifier, Set<String>> attachmentTags = Maps.newHashMap();

    private final Map<Identifier, Set<String>> allowAttachmentTags = Maps.newHashMap();

    public void putGunData(Identifier registryName, GunData data) {
        gunData.put(registryName, data);
    }

    public GunData getGunData(Identifier registryName) {
        return gunData.get(registryName);
    }

    public void putAttachmentData(Identifier registryName, AttachmentData data) {
        attachmentData.put(registryName, data);
    }

    public AttachmentData getAttachmentData(Identifier registryName) {
        return attachmentData.get(registryName);
    }

    public void putRecipe(Identifier registryName, GunSmithTableRecipe recipe) {
        gunSmithTableRecipes.put(registryName, recipe);
    }

    public Optional<GunSmithTableRecipe> getRecipe(Identifier recipeId) {
        return Optional.ofNullable(gunSmithTableRecipes.get(recipeId));
    }

    public Map<Identifier, GunSmithTableRecipe> getAllRecipes() {
        return gunSmithTableRecipes;
    }

    public void putAttachmentTags(Identifier registryName, List<String> tags) {
        this.attachmentTags.computeIfAbsent(registryName, (id) -> Sets.newHashSet()).addAll(tags);
    }

    public Set<String> getAttachmentTags(Identifier registryName) {
        return attachmentTags.get(registryName);
    }

    public void putAllowAttachmentTags(Identifier registryName, List<String> tags) {
        this.allowAttachmentTags.computeIfAbsent(registryName, (id) -> Sets.newHashSet()).addAll(tags);
    }

    public Set<String> getAllowAttachmentTags(Identifier registryName) {
        return allowAttachmentTags.get(registryName);
    }

    public void clearAll() {
        this.gunData.clear();
        this.attachmentData.clear();
        this.attachmentTags.clear();
        this.allowAttachmentTags.clear();
    }

    public void clearRecipes() {
        gunSmithTableRecipes.clear();
    }
}
