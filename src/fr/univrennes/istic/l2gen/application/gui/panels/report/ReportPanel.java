package fr.univrennes.istic.l2gen.application.gui.panels.report;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.core.services.notebook.NoteBookService;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.notebook.NoteBook;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingView;

public class ReportPanel extends JPanel {

    private SettingView settingView;
    private NoteBook noteBook;

    private JSplitPane mainSplit;

    public ReportPanel() {
        this.build();
    }

    private void build() {
        this.settingView = new SettingView();
        this.noteBook = new NoteBook();

        this.mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.noteBook, this.settingView);
        this.noteBook.setVisible(false);

        this.noteBook.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));
        this.settingView.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));

        setLayout(new BorderLayout());
        add(mainSplit, BorderLayout.CENTER);
    }

    public void resetSplit() {
        SwingUtilities.invokeLater(() -> this.mainSplit.setDividerLocation(0.7));
    }

    public void toogleSplitOrientation() {
        SwingUtilities.invokeLater(() -> {
            int currentOrientation = this.mainSplit.getOrientation();
            this.mainSplit.setOrientation(currentOrientation == JSplitPane.VERTICAL_SPLIT
                    ? JSplitPane.HORIZONTAL_SPLIT
                    : JSplitPane.VERTICAL_SPLIT);
            this.mainSplit.setDividerLocation(0.7);
        });
    }

    public void setSplitOrientation(int orientation) {
        SwingUtilities.invokeLater(() -> {
            this.mainSplit.setOrientation(orientation);
            this.mainSplit.setDividerLocation(0.7);
        });
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            if (NoteBookService.getValues().size() == 0) {
                this.noteBook.setVisible(false);
                this.mainSplit.setDividerLocation(0);
            } else {
                this.noteBook.setVisible(true);
                this.mainSplit.setDividerLocation(0.7);
            }
        });

        this.noteBook.refresh();
        this.settingView.refresh();
    }

    public SettingView getSettingView() {
        return settingView;
    }

    public NoteBook getNoteBook() {
        return noteBook;
    }
}
