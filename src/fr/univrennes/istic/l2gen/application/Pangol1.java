package fr.univrennes.istic.l2gen.application;

import fr.univrennes.istic.l2gen.application.core.CoreApp;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.gui.GUIApp;

public class Pangol1 {

    public static CoreApp<?> app;

    public static CoreController getController() {
        return app.getController();
    }

    public static void main(String[] args) throws Exception {
        app = new GUIApp();
        app.start();
    }
}
