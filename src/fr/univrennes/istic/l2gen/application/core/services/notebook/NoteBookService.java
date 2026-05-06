package fr.univrennes.istic.l2gen.application.core.services.notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;

public final class NoteBookService {
    static final List<NoteBookValue> values = new ArrayList<>();
    private static final Deque<NoteBookAction> undoStack = new ArrayDeque<>();
    private static final Deque<NoteBookAction> redoStack = new ArrayDeque<>();
    private static boolean isRestoring = false;

    public static void add(NoteBookValue value) {
        if (value == null) {
            return;
        }

        int index = values.size();
        values.add(value);
        recordAction(new AddAction(index, value));
    }

    public static void update(int index, NoteBookValue value) {
        if (index < 0 || index >= values.size()) {
            return;
        }
        NoteBookValue previous = values.set(index, value);
        recordAction(new UpdateAction(index, previous, value));
    }

    public static List<NoteBookValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    public static void remove(int index) {
        if (index < 0 || index >= values.size()) {
            return;
        }
        NoteBookValue removed = values.remove(index);
        recordAction(new RemoveAction(index, removed));
    }

    public static void move(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= values.size()) {
            return;
        }
        if (toIndex < 0 || toIndex >= values.size()) {
            return;
        }
        if (fromIndex == toIndex) {
            return;
        }
        int finalIndex = internalMove(fromIndex, toIndex);
        recordAction(new MoveAction(fromIndex, toIndex, finalIndex));
    }

    public static boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public static boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public static void undo() {
        if (undoStack.isEmpty()) {
            return;
        }

        NoteBookAction action = undoStack.pop();
        isRestoring = true;
        try {
            action.undo();
            redoStack.push(action);
        } finally {
            isRestoring = false;
        }
    }

    public static void redo() {
        if (redoStack.isEmpty()) {
            return;
        }

        NoteBookAction action = redoStack.pop();
        isRestoring = true;
        try {
            action.redo();
            undoStack.push(action);
        } finally {
            isRestoring = false;
        }
    }

    public static int internalMove(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= values.size()) {
            return fromIndex;
        }
        if (toIndex < 0 || toIndex >= values.size()) {
            return fromIndex;
        }
        if (fromIndex == toIndex) {
            return fromIndex;
        }

        NoteBookValue value = values.remove(fromIndex);
        if (toIndex > fromIndex) {
            toIndex--;
        }
        values.add(toIndex, value);
        return toIndex;
    }

    private static void recordAction(NoteBookAction action) {
        if (isRestoring || action == null) {
            return;
        }
        undoStack.push(action);
        redoStack.clear();
    }

}
