package fr.univrennes.istic.l2gen.application.core;

public abstract class CoreApp<Controller extends CoreController> {

    protected final Controller controller;

    public CoreApp(Controller controller) {
        this.controller = controller;
    }

    public CoreController getController() {
        return controller;
    }

    public abstract void start();

    public abstract void stop();

    public final void restart() {
        stop();
        start();
    }
}
