package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingRowPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;

public final class LegendSettingsPanel extends SettingSectionPanel {
    private JCheckBox showLegendCheck;
    private JComboBox<String> directionLegend;

    public LegendSettingsPanel() {
        super(Lang.get("report.setting.chart.legend"));
        build();
    }

    private void build() {
        showLegendCheck = new JCheckBox();
        showLegendCheck.setSelected(true);
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.show_legend"),
                showLegendCheck));

        directionLegend = new JComboBox<>(new String[] {
                Lang.get("report.setting.chart.vertical"),
                Lang.get("report.setting.chart.horizontal")
        });
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.direction_legend"),
                directionLegend));

    }

    public boolean isLegendVisible() {
        return showLegendCheck.isSelected();
    }

    public boolean isLegendHorizontal() {
        return directionLegend.getSelectedIndex() == 1;
    }

    public void setLegendVisible(boolean visible) {
        showLegendCheck.setSelected(visible);
    }

    public void setLegendHorizontal(boolean horizontal) {
        directionLegend.setSelectedIndex(horizontal ? 1 : 0);
    }
}
