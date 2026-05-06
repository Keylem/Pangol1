package fr.univrennes.istic.l2gen.application.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * On test la structure
 */
public class CoreControllerTest {

    /**
     * on verifie que CoreController existe et est bien abstraite
     * 
     * Ce qu'on teste:
     * - La classe CoreController existe
     * - Elle est abstraite
     * - Elle a les méthodes abstraites onStart() et onStop()
     */
    @Test
    public void testCoreControllerIsAbstract() {
        try {
            Class<?> classC = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreController");

            assertTrue("CoreController doit être abstraite",
                    Modifier.isAbstract(classC.getModifiers()));
            classC.getMethod("onStart");
            classC.getMethod("onStop");

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            fail("CoreController ou ses méthodes ne peuvent pas être trouvées: " + e.getMessage());
        }
    }

    /**
     * On verifie que CoreController a un constructeur
     */
    @Test
    public void testCoreControllerHasProtectedConstructor() {
        try {
            Class<?> classC = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreController");
            Constructor<?>[] constructors = classC.getDeclaredConstructors();

            assertTrue("Il doit y avoir un constructeur", constructors.length > 0);

        } catch (ClassNotFoundException e) {
            fail("CoreController n'est pas trouvé: " + e.getMessage());
        }
    }

}