package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.text;

import javax.swing.JTextArea;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookText;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.IReportSettingPanel;

public final class TextSettingsPanel extends SettingSectionPanel implements IReportSettingPanel {
    private final JTextArea contentArea;

    public TextSettingsPanel() {
        super(Lang.get("report.setting.text"));

        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        addRow(contentArea);
    }

    @Override
    public NoteBookValue createNoteBook() {
        return new NoteBookText(contentArea.getText());
    }

    @Override
    public void loadNoteBook(NoteBookValue value) {
        if (value instanceof NoteBookText) {
            contentArea.setText(((NoteBookText) value).getText());
        }
    }
}
