package fr.univrennes.istic.l2gen.application.core;

public abstract class CoreController {

    protected CoreController() {
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onException(Exception e);

    public abstract String addTask(String name, TaskStatus status);

    public abstract void updateTask(String taskId, String name, TaskStatus status);

    public abstract void updateTaskStatus(String taskId, TaskStatus status);
}
