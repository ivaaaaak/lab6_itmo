package com.ivaaaak.server.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map;

public class TimeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<java.time.LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson g = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> map = g.fromJson(json.toString(), type);
        return java.time.LocalDateTime.of(map.get("year"), map.get("month"), map.get("day"), map.get("hour"), map.get("minute"), map.get("second"));
    }

    @Override
    public JsonElement serialize(LocalDateTime ld, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.addProperty("year", ld.getYear());
        obj.addProperty("month", ld.getMonthValue());
        obj.addProperty("day", ld.getDayOfMonth());
        obj.addProperty("hour", ld.getHour());
        obj.addProperty("minute", ld.getMinute());
        obj.addProperty("second", ld.getSecond());
        return obj;

    }
}
