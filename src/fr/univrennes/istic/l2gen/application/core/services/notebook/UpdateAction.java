package fr.univrennes.istic.l2gen.application.core.services.notebook;

import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;

public final class UpdateAction implements NoteBookAction {
    private final int index;
    private final NoteBookValue previous;
    private final NoteBookValue next;

    public UpdateAction(int index, NoteBookValue previous, NoteBookValue next) {
        this.index = index;
        this.previous = previous;
        this.next = next;
    }

    @Override
    public void undo() {
        if (index < 0 || index >= NoteBookService.values.size()) {
            return;
        }
        NoteBookService.values.set(index, previous);
    }

    @Override
    public void redo() {
        if (index < 0 || index >= NoteBookService.values.size()) {
            return;
        }
        NoteBookService.values.set(index, next);
    }
}