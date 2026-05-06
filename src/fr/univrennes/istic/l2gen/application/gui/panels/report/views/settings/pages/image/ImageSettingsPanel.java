package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.image;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookImage;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingRowPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.IReportSettingPanel;

public final class ImageSettingsPanel extends SettingSectionPanel implements IReportSettingPanel {

    private File image;
    private final JLabel pathLabel = new JLabel("");

    public ImageSettingsPanel() {
        super(Lang.get("report.setting.image"));

        JButton selectBtn = new JButton(Lang.get("report.setting.image.select"));
        selectBtn.addActionListener(e -> {
            SystemFileChooser fileChooser = new SystemFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(SystemFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new SystemFileChooser.FileNameExtensionFilter(
                    Lang.get("report.setting.image"),
                    "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg"));

            if (fileChooser.showOpenDialog(this) != SystemFileChooser.APPROVE_OPTION) {
                return;
            }

            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.isFile()) {
                this.image = selectedFile;
                pathLabel.setText(this.image.getName());
            }

        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        controlPanel.setOpaque(false);
        controlPanel.add(selectBtn);
        controlPanel.add(pathLabel);

        addRow(new SettingRowPanel(Lang.get("report.setting.image.path"), controlPanel));
    }

    @Override
    public NoteBookValue createNoteBook() {
        return new NoteBookImage(image);
    }

    @Override
    public void loadNoteBook(NoteBookValue value) {
        if (value instanceof NoteBookImage) {
            this.image = ((NoteBookImage) value).getPath();
            pathLabel.setText(this.image == null ? "" : this.image.getPath());
        }
    }
}
