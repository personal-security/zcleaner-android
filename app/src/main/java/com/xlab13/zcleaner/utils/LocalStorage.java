package com.xlab13.zcleaner.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private static final String NAME = "storage";

    public static int readInteger(final Context cnt, final String key){
        SharedPreferences settings = cnt.getSharedPreferences("storage", 0);
        return settings.getInt(key, 0);
    }

    public static void writeInteger(final Context cnt,final String key, final int value){
        SharedPreferences settings = cnt.getSharedPreferences("storage", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

}