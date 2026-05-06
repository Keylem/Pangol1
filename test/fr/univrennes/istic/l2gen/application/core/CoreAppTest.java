package fr.univrennes.istic.l2gen.application.core;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Ici on test juste la stucture
 */
public class CoreAppTest {

    /**
     * On verifie que CoreApp existe et est bien abstraite et a une methode start()
     */
    @Test
    public void testCoreAppIsAbstract() {
        try {
            Class<?> classC = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreApp");

            assertTrue("CoreApp doit être abstraite",
                    Modifier.isAbstract(classC.getModifiers()));

            // Vérifier que la méthode abstraite start() existe
            classC.getMethod("start");

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            fail("CoreApp ou sa méthode start ne peuvent pas être trouvées: " + e.getMessage());
        }
    }

    /**
     * On verifie que CoreApp a un constructeur qui prend un Controller
     */
    @Test
    public void testCoreAppHasControllerConstructor() {
        try {
            Class<?> classC = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreApp");
            Class<?> controllerClass = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreController");

            // Essayer de récupérer le constructeur qui prend un Controller
            assertNotNull("CoreApp doit avoir un constructeur qui prend un Controller",
                    classC.getDeclaredConstructor(controllerClass));

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            fail("CoreApp ou son constructeur ne peuvent pas être trouvés: " + e.getMessage());
        }
    }

    /**
     * On verifie que getController() existe et retourne un CoreController
     */
    @Test
    public void testCoreAppHasGetControllerMethod() {
        try {
            Class<?> classC = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreApp");
            Class<?> controllerClass = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreController");

            // On recupere la methode getcontroller
            Method getcontroller = classC.getMethod("getController");

            // Vérifier que elle retourne un CoreController
            assertTrue("getController() doit retourner un CoreController",
                    controllerClass.isAssignableFrom(getcontroller.getReturnType()));

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            fail("CoreApp ou getController ne peuvent pas être trouvés: " + e.getMessage());
        }
    }

    /**
     * On verifie qu'une implémentation concrète peut hériter de CoreApp
     */
    @Test
    public void testCoreAppCanBeExtended() {
        try {
            Class<?> coreAppClass = Class.forName("fr.univrennes.istic.l2gen.application.core.CoreApp");
            Class<?> guiAppClass = Class.forName("fr.univrennes.istic.l2gen.application.gui.GUIApp");

            // Vérifier que GUIApp étend CoreApp
            assertTrue("GUIApp doit étendre CoreApp",
                    coreAppClass.isAssignableFrom(guiAppClass));

        } catch (ClassNotFoundException e) {
            fail("CoreApp ou GUIApp ne peuvent pas être trouvés: " + e.getMessage());
        }
    }

}