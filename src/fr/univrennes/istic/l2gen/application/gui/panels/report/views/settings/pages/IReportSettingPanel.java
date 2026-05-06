package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages;

import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;

public interface IReportSettingPanel {

    public void loadNoteBook(NoteBookValue value);

    public NoteBookValue createNoteBook();
}
