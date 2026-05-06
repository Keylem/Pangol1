package fr.univrennes.istic.l2gen.application.core.services.notebook;

public final class MoveAction implements NoteBookAction {
    private final int fromIndex;
    private final int toIndex;
    private final int finalIndex;

    public MoveAction(int fromIndex, int toIndex, int finalIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.finalIndex = finalIndex;
    }

    @Override
    public void undo() {
        NoteBookService.internalMove(finalIndex, fromIndex);
    }

    @Override
    public void redo() {
        NoteBookService.internalMove(fromIndex, toIndex);
    }
}