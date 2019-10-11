package com.xlab13.zcleaner.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalData {
    private static String config_file = "zcleaner";

    public static void writeIntConfig(final Context ctx, final String key, final Long value){
        SharedPreferences settings = ctx.getSharedPreferences(config_file, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static Long readIntConfig(final Context ctx, final String key){
        SharedPreferences settings = ctx.getSharedPreferences(config_file, 0);
        return settings.getLong(key, 0);
    }

}
