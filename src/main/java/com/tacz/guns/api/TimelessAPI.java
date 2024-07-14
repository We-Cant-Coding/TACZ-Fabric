package com.tacz.guns.api;

import com.tacz.guns.api.client.other.IThirdPersonAnimation;
import com.tacz.guns.api.client.other.ThirdPersonManager;
import com.tacz.guns.client.resource.ClientGunPackLoader;
import com.tacz.guns.client.resource.index.ClientAmmoIndex;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.resource.CommonAssetManager;
import com.tacz.guns.resource.CommonGunPackLoader;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class TimelessAPI {
    @Environment(EnvType.CLIENT)
    public static Optional<ClientGunIndex> getClientGunIndex(Identifier gunId) {
        return ClientGunPackLoader.getGunIndex(gunId);
    }

    @Environment(EnvType.CLIENT)
    public static Optional<ClientAttachmentIndex> getClientAttachmentIndex(Identifier attachmentId) {
        return ClientGunPackLoader.getAttachmentIndex(attachmentId);
    }

    @Environment(EnvType.CLIENT)
    public static Optional<ClientAmmoIndex> getClientAmmoIndex(Identifier ammoId) {
        return ClientGunPackLoader.getAmmoIndex(ammoId);
    }

    @Environment(EnvType.CLIENT)
    public static Set<Map.Entry<Identifier, ClientGunIndex>> getAllClientGunIndex() {
        return ClientGunPackLoader.getAllGuns();
    }

    @Environment(EnvType.CLIENT)
    public static Set<Map.Entry<Identifier, ClientAmmoIndex>> getAllClientAmmoIndex() {
        return ClientGunPackLoader.getAllAmmo();
    }

    @Environment(EnvType.CLIENT)
    public static Set<Map.Entry<Identifier, ClientAttachmentIndex>> getAllClientAttachmentIndex() {
        return ClientGunPackLoader.getAllAttachments();
    }
    public static Optional<CommonGunIndex> getCommonGunIndex(Identifier gunId) {
        return CommonGunPackLoader.getGunIndex(gunId);
    }

    public static Optional<CommonAttachmentIndex> getCommonAttachmentIndex(Identifier attachmentId) {
        return CommonGunPackLoader.getAttachmentIndex(attachmentId);
    }

    public static Optional<CommonAmmoIndex> getCommonAmmoIndex(Identifier ammoId) {
        return CommonGunPackLoader.getAmmoIndex(ammoId);
    }

    public static Optional<GunSmithTableRecipe> getRecipe(Identifier recipeId) {
        return CommonAssetManager.INSTANCE.getRecipe(recipeId);
    }

    public static Set<Map.Entry<Identifier, CommonGunIndex>> getAllCommonGunIndex() {
        return CommonGunPackLoader.getAllGuns();
    }

    public static Set<Map.Entry<Identifier, CommonAmmoIndex>> getAllCommonAmmoIndex() {
        return CommonGunPackLoader.getAllAmmo();
    }

    public static Set<Map.Entry<Identifier, CommonAttachmentIndex>> getAllCommonAttachmentIndex() {
        return CommonGunPackLoader.getAllAttachments();
    }

    public static Map<Identifier, GunSmithTableRecipe> getAllRecipes() {
        return CommonAssetManager.INSTANCE.getAllRecipes();
    }

    public static void registerThirdPersonAnimation(String name, IThirdPersonAnimation animation) {
        ThirdPersonManager.register(name, animation);
    }
}
