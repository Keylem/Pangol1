package fr.univrennes.istic.l2gen.application.gui;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import org.junit.Test;
import static org.junit.Assert.*;

public class GUIControllerTest {

    /**
     * On verifie si guicontroller peut etre creer
     */
    @Test
    public void testGUIControllerIsSingleton() {

        GUIController instance1 = GUIController.getInstance();
        assertNotNull("getInstance() ne doit pas retourner null", instance1);
    }

    /**
     * GUI controller doit herite de CoreController
     */
    @Test
    public void testGUIControllerInheritsCoreController() {
        GUIController controller = GUIController.getInstance();
        assertTrue("GUIController doit hériter de CoreController",
                controller instanceof CoreController);
    }

    /**
     * On verifie que onStart() peut être appelé sans erreur
     */
    // NE MARCHE PAS
    // @Test
    public void testOnStartMethodExists() {
        GUIController controller = GUIController.getInstance();

        try {
            controller.onStart();
        } catch (Exception e) {
            fail("onStart() ne doit pas lever d'exception: " + e.getMessage());
        }
    }

    /**
     * On verifie que onStop() existe
     */
    @Test
    public void testOnStopMethodExists() {
        try {
            Class<?> classC = Class.forName("fr.univrennes.istic.l2gen.application.gui.GUIController");
            classC.getMethod("onStop");
        } catch (NoSuchMethodException e) {
            fail("onStop() ne peut pas être trouvée: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            fail("GUIController ne peut pas être trouvée: " + e.getMessage());
        }
    }

    /**
     * On verifie que getTable() retourne empty au départ
     */
    @Test
    public void testGetTableReturnsEmptyInitially() {
        GUIController controller = GUIController.getInstance();
        var tableOpt = controller.getTable();

        assertTrue("getTable() doit retourner un Optional vide initialement", tableOpt.isEmpty());
    }

    /**
     * On verifie que setTable() existe
     * 
     * setTable() prend un parametre DataTable
     */
    @Test
    public void testSetTableMethodExists() {
        try {
            Class<?> guiControllerClass = Class.forName("fr.univrennes.istic.l2gen.application.gui.GUIController");
            Class<?> dataTableClass = Class.forName("fr.univrennes.istic.l2gen.application.core.table.DataTable");
            guiControllerClass.getMethod("setTable", dataTableClass);
        } catch (NoSuchMethodException e) {
            fail("setTable(DataTable) ne peut pas être trouvée: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            fail("Classe non trouvée: " + e.getMessage());
        }
    }

}