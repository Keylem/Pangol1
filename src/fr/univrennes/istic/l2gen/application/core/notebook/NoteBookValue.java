package fr.univrennes.istic.l2gen.application.core.notebook;

public sealed interface NoteBookValue permits NoteBookText, NoteBookImage, NoteBookChart {

    public void exportHTML(StringBuilder html);
}