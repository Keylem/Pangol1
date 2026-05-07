package fr.univrennes.istic.l2gen.application.core.config;

import java.util.prefs.Preferences;

import fr.univrennes.istic.l2gen.application.Pangol1;

public final class Config {

    private static Preferences prefs = Preferences.userNodeForPackage(Pangol1.class);

    public static boolean DARK_MODE = false;

    public static Preferences get() {
        return prefs;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        return prefs.getFloat(key, defaultValue);
    }

    public static byte[] getByteArray(String key, byte[] defaultValue) {
        return prefs.getByteArray(key, defaultValue);
    }

    public static String get(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }

    public static void putInt(String key, int value) {
        prefs.putInt(key, value);
    }

    public static void putFloat(String key, float value) {
        prefs.putFloat(key, value);
    }

    public static void put(String key, String value) {
        prefs.put(key, value);
    }

    public static void putByteArray(String key, byte[] value) {
        prefs.putByteArray(key, value);
    }

    public static void putIfAbsent(String key, String value) {
        if (prefs.get(key, null) == null) {
            prefs.put(key, value);
        }
    }

    public static void putBooleanIfAbsent(String key, boolean value) {
        if (prefs.get(key, null) == null) {
            prefs.putBoolean(key, value);
        }
    }

    public static void putIntIfAbsent(String key, int value) {
        if (prefs.get(key, null) == null) {
            prefs.putInt(key, value);
        }
    }

    public static void putFloatIfAbsent(String key, float value) {
        if (prefs.get(key, null) == null) {
            prefs.putFloat(key, value);
        }
    }

    public static void remove(String key) {
        prefs.remove(key);
    }
}
