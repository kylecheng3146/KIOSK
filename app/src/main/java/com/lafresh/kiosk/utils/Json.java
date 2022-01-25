package com.lafresh.kiosk.utils;

import com.google.gson.Gson;

public class Json {
    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null) {
            return null;
        }
        return new Gson().fromJson(json, type);
    }

    public static String toJson(Object o) {
        if (o == null) {
            return null;
        }
        return new Gson().toJson(o);
    }

    public static String parseStringInJson(String s){
        return s.replace("\\\"", "\"");
    }
}
