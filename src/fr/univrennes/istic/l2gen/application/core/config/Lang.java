package fr.univrennes.istic.l2gen.application.core.config;

import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public final class Lang {
    private static Lang instance = new Lang();

    private ResourceBundle bundle;
    private Locale locale;

    private Lang() {
        this.locale = getDefaultLocale();
        this.bundle = ResourceBundle.getBundle("languages.pangol1", locale);
    }

    public static void setLocale(Locale locale) {
        if (locale == null) {
            instance.locale = Locale.getDefault();
        } else {
            instance.locale = locale;
        }
        instance.bundle = ResourceBundle.getBundle("languages.pangol1", instance.locale);
    }

    public static Locale getLocale() {
        return instance.locale;
    }

    public static String get(String key) {
        try {
            return instance.bundle.getString(key);
        } catch (Exception e) {
            Log.debug("Missing translation for key: " + key, e);
            return "<" + key + ">";
        }
    }

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }

    public static boolean isSupported(Locale locale) {
        String resourcePath = "/languages/pangol1_" + locale.getLanguage() + ".properties";
        return Lang.class.getResourceAsStream(resourcePath) != null;
    }

    public static Set<String> getSupportedLanguages() {
        Set<String> languages = new HashSet<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!isSupported(locale)) {
                continue;
            }

            if (!languages.add(locale.getLanguage())) {
                continue;
            }
        }
        return languages;
    }

    public static Locale getDefaultLocale() {
        Locale systemLocale = Locale.getDefault();
        if (isSupported(systemLocale)) {
            return systemLocale;
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            if (isSupported(locale)) {
                return locale;
            }
        }

        return Locale.ENGLISH;
    }

}
