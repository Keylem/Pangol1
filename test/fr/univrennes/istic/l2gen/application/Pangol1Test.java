package fr.univrennes.istic.l2gen.application;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

public class Pangol1Test {

    @Test
    public void testPangol1Instantiation() {
        Pangol1 pangol1 = new Pangol1();
        assertNotNull("Pangol1 doit pouvoir être instantié", pangol1);
    }

    /**
     * Pangol1 doit avoir une class main
     * 
     * On verifie que La méthode main(String[]) existe
     */
    @Test
    public void testPangol1HasMainMethod() {
        try {
            Class<?> classP = Class.forName("fr.univrennes.istic.l2gen.application.Pangol1");
            Method mainP = classP.getMethod("main", String[].class);

            assertNotNull("Pangol1 n'a pas de main", mainP);
        } catch (NoSuchMethodException e) {
            fail("Pangol1 doit avoir une méthode main(String[]): " + e.getMessage());
        } catch (ClassNotFoundException e) {
            fail("Pangol1 ne peut pas être trouvée: " + e.getMessage());
        }
    }
}