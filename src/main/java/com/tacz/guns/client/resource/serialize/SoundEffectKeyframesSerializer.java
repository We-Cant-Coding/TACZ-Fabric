package com.tacz.guns.client.resource.serialize;

import com.google.gson.*;
import com.tacz.guns.client.resource.pojo.animation.bedrock.SoundEffectKeyframes;
import it.unimi.dsi.fastutil.doubles.Double2ObjectRBTreeMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;
import java.util.Map;

public class SoundEffectKeyframesSerializer implements JsonDeserializer<SoundEffectKeyframes> {
    @Override
    public SoundEffectKeyframes deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Double2ObjectRBTreeMap<Identifier> keyframes = new Double2ObjectRBTreeMap<>();
        // 如果是对象
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entrySet : jsonObject.entrySet()) {
                double time = Double.parseDouble(entrySet.getKey());
                JsonElement value = entrySet.getValue();
                if (value.isJsonObject()) {
                    String soundId = JsonHelper.getString(value.getAsJsonObject(), "effect");
                    keyframes.put(time, new Identifier(soundId));
                }
            }
            return new SoundEffectKeyframes(keyframes);
        }
        return new SoundEffectKeyframes(keyframes);
    }
}
