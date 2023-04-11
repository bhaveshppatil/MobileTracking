package com.finalyear.mobiletracking.sharePref;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class to manage handling of shared preference.
 */
public class SharedPrefs {

    private static final String SHARED_PREFS_NAME = "SharedPrefs";

    private static SharedPrefs sharedPrefs;
    private final SharedPreferences sharedPreferences;
    private final Map<String, Object> sharedPrefMap;

    private SharedPrefs(Context context) {
        sharedPrefMap = new HashMap<>();
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void initialize(Context context) {

        if (sharedPrefs == null) {
            sharedPrefs = new SharedPrefs(context);
        }
    }

    public static SharedPrefs getInstance() {
        if (sharedPrefs == null) {
            throw new RuntimeException("Initialize SharedPrefs by calling initialize() method");
        }
        return sharedPrefs;
    }

    public void addOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeCHangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void addString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        sharedPrefMap.put(key, value);
    }

    public void addStringList(String key, Set<String> value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
        sharedPrefMap.put(key, value);
    }

    public void addBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
        sharedPrefMap.put(key, value);
    }

    public void addInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        sharedPrefMap.put(key, value);
    }

    public void addFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
        sharedPrefMap.put(key, value);
    }

    public void addLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
        sharedPrefMap.put(key, value);
    }

    public void remove(String key) {
        sharedPrefMap.remove(key);

        if (sharedPreferences.contains(key)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key).apply();
        }
    }

    public void removeAll() {
        sharedPrefMap.clear();

        Map<String, ?> allKeys = sharedPreferences.getAll();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : allKeys.keySet()) {
            editor.remove(key);
        }
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        if (sharedPrefMap.containsKey(key)) {
            return (String) sharedPrefMap.get(key);
        }

        return sharedPreferences.getString(key, defaultValue);
    }

    public Set<String> getStringList(String key) {
//        if (sharedPrefMap.containsKey(key)) {
//            return (Set<String>) sharedPrefMap.get(key);
//        }
        return sharedPreferences.getStringSet(key, null);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (sharedPrefMap.containsKey(key)) {
            return (boolean) sharedPrefMap.get(key);
        }

        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        if (sharedPrefMap.containsKey(key)) {
            return (int) sharedPrefMap.get(key);
        }

        return sharedPreferences.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        if (sharedPrefMap.containsKey(key)) {
            return (float) sharedPrefMap.get(key);
        }

        return sharedPreferences.getFloat(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        if (sharedPrefMap.containsKey(key)) {
            return (long) sharedPrefMap.get(key);
        }

        return sharedPreferences.getLong(key, defaultValue);
    }

}