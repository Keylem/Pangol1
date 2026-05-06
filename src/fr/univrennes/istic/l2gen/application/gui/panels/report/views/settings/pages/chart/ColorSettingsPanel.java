package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingRowPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;
import fr.univrennes.istic.l2gen.svg.color.Color;

public final class ColorSettingsPanel extends SettingSectionPanel {
    private static final int SWATCH_SIZE = 18;

    private List<Color> colors = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public ColorSettingsPanel() {
        super(Lang.get("report.setting.chart.colors"));
        build();
    }

    private void build() {
        for (int i = 0; i < colors.size(); i++) {
            Color color = colors.get(i);
            int index = i;
            String label = labels.size() > i ? labels.get(i) : Lang.get("report.setting.chart.color") + " " + (i + 1);
            addRow(new SettingRowPanel(label, createColorControl(index, color)));
        }

        setVisible(colors.size() > 0);
    }

    private JComponent createColorControl(int index, Color color) {
        JPanel control = new JPanel();
        control.setOpaque(false);
        control.setLayout(new BoxLayout(control, BoxLayout.X_AXIS));

        JPanel swatch = createSwatch(color);

        JButton chooseColorButton = new JButton(Lang.get("report.setting.chart.select_color") + "...");
        chooseColorButton.addActionListener(e -> {
            java.awt.Color selectedColor = JColorChooser.showDialog(
                    this,
                    Lang.get("report.setting.chart.colors"),
                    swatch.getBackground());

            if (selectedColor == null) {
                return;
            }

            if (index >= 0 && index < colors.size()) {
                colors.set(index, Color.fromAWT(selectedColor));
                swatch.setBackground(selectedColor);
                swatch.repaint();
            }
        });

        control.add(chooseColorButton);
        control.add(Box.createHorizontalStrut(8));
        control.add(swatch);
        control.add(Box.createHorizontalGlue());
        return control;
    }

    private JPanel createSwatch(Color color) {
        JPanel swatch = new JPanel();
        swatch.setOpaque(true);
        swatch.setBackground(color.toAWT());
        swatch.setPreferredSize(new Dimension(SWATCH_SIZE, SWATCH_SIZE));
        swatch.setMinimumSize(new Dimension(SWATCH_SIZE, SWATCH_SIZE));
        swatch.setMaximumSize(new Dimension(SWATCH_SIZE, SWATCH_SIZE));
        swatch.setBorder(BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY));
        return swatch;
    }

    public void setColorLabels(List<Color> newColors, List<String> newLabels) {
        this.colors = newColors != null ? newColors : new ArrayList<>();
        this.labels = newLabels != null ? newLabels : new ArrayList<>();

        clearRows();
        build();
        revalidate();
        repaint();
    }

    public List<Color> getColors() {
        return colors;
    }

}
