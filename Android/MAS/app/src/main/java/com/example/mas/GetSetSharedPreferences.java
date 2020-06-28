package com.example.mas;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class GetSetSharedPreferences {

    /**
     * 读取账号ID
     */
    public static String getDefaults(String key, Context context) {
        android.content.SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    /**
     * 写入账号ID
     */
    public static void setDefaults(String key, String value, Context context) {
        android.content.SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        android.content.SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 删除账号ID
     */
    public static void removeDefaults(String key, Context context) {
        android.content.SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        android.content.SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
