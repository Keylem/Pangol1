package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingRowPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;
import fr.univrennes.istic.l2gen.visustats.view.datagroup.axis.DataAxisViewScaleType;

public final class AxisSettingsPanel extends SettingSectionPanel {
    private JSpinner tickCountSpinner;
    private JComboBox<String> scaleCombo;

    private JCheckBox showXAxisCheck;
    private JTextField xAxisLabelField;

    private JCheckBox showYAxisCheck;
    private JTextField yAxisLabelField;

    public AxisSettingsPanel() {
        super(Lang.get("report.setting.chart.axis"));
        build();
    }

    private void build() {

        tickCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.tick_count"),
                tickCountSpinner));

        scaleCombo = new JComboBox<>(new String[] {
                Lang.get("report.setting.chart.linear"),
                Lang.get("report.setting.chart.logarithmic"),
                Lang.get("report.setting.chart.sqrt")
        });
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.scale"), scaleCombo));

        showXAxisCheck = new JCheckBox();
        showXAxisCheck.setSelected(true);
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.show_x_axis"),
                showXAxisCheck));

        xAxisLabelField = new JTextField(Lang.get("report.setting.chart.default_labelx"));
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.x_label"),
                xAxisLabelField));

        showYAxisCheck = new JCheckBox();
        showYAxisCheck.setSelected(true);
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.show_y_axis"),
                showYAxisCheck));

        yAxisLabelField = new JTextField(Lang.get("report.setting.chart.default_labely"));
        addRow(new SettingRowPanel(Lang.get("report.setting.chart.y_label"),
                yAxisLabelField));

    }

    public int getTickCount() {
        return (int) tickCountSpinner.getValue();
    }

    public DataAxisViewScaleType getScale() {
        return DataAxisViewScaleType.values()[scaleCombo.getSelectedIndex()];
    }

    public boolean isXVisible() {
        return showXAxisCheck.isSelected();
    }

    public String getXLabel() {
        return xAxisLabelField.getText();
    }

    public boolean isYVisible() {
        return showYAxisCheck.isSelected();
    }

    public String getYLabel() {
        return yAxisLabelField.getText();
    }

    public void setTickCount(int count) {
        tickCountSpinner.setValue(count);
    }

    public void setScale(DataAxisViewScaleType scale) {
        scaleCombo.setSelectedIndex(scale.ordinal());
    }

    public void setXVisible(boolean visible) {
        showXAxisCheck.setSelected(visible);
    }

    public void setXLabel(String label) {
        xAxisLabelField.setText(label);
    }

    public void setYVisible(boolean visible) {
        showYAxisCheck.setSelected(visible);
    }

    public void setYLabel(String label) {
        yAxisLabelField.setText(label);
    }

}
