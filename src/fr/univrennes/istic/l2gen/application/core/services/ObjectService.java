package fr.univrennes.istic.l2gen.application.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;

import fr.univrennes.istic.l2gen.application.core.config.Log;

public final class ObjectService {
    public static <T extends Serializable> Optional<T> load(File path, Class<T> clazz) {
        if (path == null || !path.exists() || !path.isFile()) {
            return Optional.empty();
        }

        try (var ois = new ObjectInputStream(new FileInputStream(path))) {
            Object obj = ois.readObject();
            if (clazz.isInstance(obj)) {
                return Optional.of(clazz.cast(obj));
            } else {
                Log.debug("Failed to load object: type mismatch");
                return Optional.empty();
            }
        } catch (Exception e) {
            Log.debug("Failed to load object", e);
            return Optional.empty();
        }
    }

    public static <T extends Serializable> Optional<T> load(Class<T> clazz) {
        return load(new File(FileService.getAppDataDir().toString(), clazz.getSimpleName() + ".ser"), clazz);
    }

    public static <T extends Serializable> void save(File path, T obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(obj);
        } catch (Exception e) {
            Log.debug("Failed to save object", e);
        }
    }

    public static <T extends Serializable> void save(T obj) {
        save(new File(FileService.getAppDataDir().toString(), obj.getClass().getSimpleName() + ".ser"), obj);
    }
}
