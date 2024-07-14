package com.tacz.guns.resource.serialize;

import com.google.gson.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.crafting.GunSmithTableResult;
import com.tacz.guns.resource.CommonGunPackLoader;
import com.tacz.guns.resource.pojo.data.recipe.GunResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Locale;

public class GunSmithTableResultSerializer implements JsonDeserializer<GunSmithTableResult> {
    @Override
    public GunSmithTableResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            Identifier id = new Identifier(JsonHelper.getString(jsonObject, "id"));
            String typeName = JsonHelper.getString(jsonObject, "type");
            int count = 1;
            if (jsonObject.has("count")) {
                count = Math.max(JsonHelper.getInt(jsonObject, "count"), 1);
            }
            switch (typeName) {
                case GunSmithTableResult.GUN -> {
                    return getGunStack(id, count, jsonObject);
                }
                case GunSmithTableResult.AMMO -> {
                    return getAmmoStack(id, count);
                }
                case GunSmithTableResult.ATTACHMENT -> {
                    return getAttachmentStack(id, count);
                }
            }
        }
        return new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY);
    }

    private GunSmithTableResult getGunStack(Identifier id, int count, JsonObject extraData) {
        GunResult gunResult = CommonGunPackLoader.GSON.fromJson(extraData, GunResult.class);
        int ammoCount = Math.max(0, gunResult.getAmmoCount());
        var attachments = gunResult.getAttachments();

        return TimelessAPI.getCommonGunIndex(id).map(gunIndex -> {
            ItemStack itemStack = GunItemBuilder.create()
                    .setCount(count)
                    .setId(id)
                    .setAmmoCount(ammoCount)
                    .setAmmoInBarrel(false)
                    .putAllAttachment(attachments)
                    .setFireMode(gunIndex.getGunData().getFireModeSet().get(0)).build();
            String group = gunIndex.getType();
            return new GunSmithTableResult(itemStack, group);
        }).orElse(new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY));
    }

    private GunSmithTableResult getAmmoStack(Identifier id, int count) {
        return new GunSmithTableResult(AmmoItemBuilder.create().setCount(count).setId(id).build(), GunSmithTableResult.AMMO);
    }

    private GunSmithTableResult getAttachmentStack(Identifier id, int count) {
        return TimelessAPI.getCommonAttachmentIndex(id).map(attachmentIndex -> {
            ItemStack itemStack = AttachmentItemBuilder.create().setCount(count).setId(id).build();
            String group = attachmentIndex.getType().name().toLowerCase(Locale.US);
            return new GunSmithTableResult(itemStack, group);
        }).orElse(new GunSmithTableResult(ItemStack.EMPTY, StringUtils.EMPTY));
    }
}
