package com.ascon.crashGroupListener.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

// Преобразует строку в необходимый объект
public class JSONMapper {

    private static Gson gson = new GsonBuilder().create();
    public static <T> T stringToObject(String jsonSrc, Type type ) {
        try {
            return gson.fromJson(jsonSrc.replaceAll("^'|'$", ""), type);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException(e);
        }
    }
// Преобразует объект в строку
    public static String objectToString(Object object, Type type) {
        return gson.toJson(object, type);
    }
}