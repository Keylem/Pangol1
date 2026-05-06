package fr.univrennes.istic.l2gen.application.core.services.notebook;

import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;

public final class RemoveAction implements NoteBookAction {
    private final int index;
    private final NoteBookValue value;

    public RemoveAction(int index, NoteBookValue value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public void undo() {
        int targetIndex = Math.min(index, NoteBookService.values.size());
        NoteBookService.values.add(targetIndex, value);
    }

    @Override
    public void redo() {
        int currentIndex = NoteBookService.values.indexOf(value);
        if (currentIndex >= 0) {
            NoteBookService.values.remove(currentIndex);
        }
    }
}