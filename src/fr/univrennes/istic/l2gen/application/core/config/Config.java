package fr.univrennes.istic.l2gen.application.core.config;

import java.util.prefs.Preferences;

import fr.univrennes.istic.l2gen.application.Pangol1;

public final class Config {

    private static Preferences prefs = Preferences.userNodeForPackage(Pangol1.class);

    public static boolean DARK_MODE = false;

    public static Preferences get() {
        return prefs;
    }
}
