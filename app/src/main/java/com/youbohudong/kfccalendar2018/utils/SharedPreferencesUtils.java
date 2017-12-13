package com.youbohudong.kfccalendar2018.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 作者：bcq on 2017/3/21 17:47
 */

public class SharedPreferencesUtils {
    private SharedPreferences sp;
    private Context ctx;

    public SharedPreferencesUtils(Context ctx) {
        this.ctx = ctx;
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void putStr(String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    public void putBln(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).commit();
    }

    public String getString(String key, String defaultStr) {
        return sp.getString(key, defaultStr);
    }

    public Boolean getBln(String key, boolean defaultbln) {
        return sp.getBoolean(key, defaultbln);
    }

    public Integer getInt(String key, int i) {
        return sp.getInt(key, i);
    }

}
