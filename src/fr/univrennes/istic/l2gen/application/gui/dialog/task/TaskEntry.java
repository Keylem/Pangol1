package fr.univrennes.istic.l2gen.application.gui.dialog.task;

import fr.univrennes.istic.l2gen.application.core.TaskStatus;

public record TaskEntry(String id, String name, TaskStatus status) {
}