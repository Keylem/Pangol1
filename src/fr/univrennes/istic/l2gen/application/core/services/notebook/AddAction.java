package fr.univrennes.istic.l2gen.application.core.services.notebook;

import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;

public final class AddAction implements NoteBookAction {
    private final int index;
    private final NoteBookValue value;

    public AddAction(int index, NoteBookValue value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public void undo() {
        int currentIndex = NoteBookService.values.indexOf(value);
        if (currentIndex >= 0) {
            NoteBookService.values.remove(currentIndex);
        }
    }

    @Override
    public void redo() {
        int targetIndex = Math.min(index, NoteBookService.values.size());
        NoteBookService.values.add(targetIndex, value);
    }
}