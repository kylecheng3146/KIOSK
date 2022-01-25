package com.lafresh.kiosk.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.lafresh.kiosk.utils.Json;

public class DataCache<T extends DataCache.CacheClass> {
    public void set(Context context, Class<T> tClass, String content) {
        SharedPreferences sp = context.getSharedPreferences(tClass.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(tClass.getName(), content).apply();
    }

    public T get(Context context, Class<T> tClass) {
        SharedPreferences sp = context.getSharedPreferences(tClass.getName(), Context.MODE_PRIVATE);
        String dataJson = sp.getString(tClass.getName(), "");
        T data = null;
        if (dataJson.length() > 0) {
            data = Json.fromJson(dataJson, tClass);
        }
        return data;
    }

    public static class CacheClass {
    }

}
