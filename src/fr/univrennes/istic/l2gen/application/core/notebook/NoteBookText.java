package fr.univrennes.istic.l2gen.application.core.notebook;

public final class NoteBookText implements NoteBookValue {

    private String text;

    public NoteBookText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void exportHTML(StringBuilder html) {
        html.append("<p>").append(text).append("</p>");
    }

}
