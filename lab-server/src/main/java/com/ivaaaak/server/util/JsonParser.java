package com.ivaaaak.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ivaaaak.common.data.Person;

import java.lang.reflect.Type;
import java.util.Hashtable;

public final class JsonParser {

    private JsonParser() {

    }

    public static Hashtable<Integer, Person> parseFromString(String data) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(java.time.LocalDateTime.class, new TimeConverter());
        Gson gson = builder.create();

        Type type = new TypeToken<Hashtable<Integer, Person>>() { }.getType();

        if ("".equals(data)) {
            return new Hashtable<>();
        }
        return gson.fromJson(data, type);

    }

    public static String parseIntoString(Hashtable<Integer, Person> ht) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(java.time.LocalDateTime.class, new TimeConverter());
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(ht);
    }
}
