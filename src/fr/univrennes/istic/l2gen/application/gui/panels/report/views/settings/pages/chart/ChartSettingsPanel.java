package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookChart;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingRowPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.IReportSettingPanel;
import fr.univrennes.istic.l2gen.visustats.view.DataViewType;

public final class ChartSettingsPanel extends SettingSectionPanel implements IReportSettingPanel {

        private JComboBox<String> chartTypeCombo;
        private JTextField titleField;
        private JCheckBox stackedCheckBox;

        private final SharedChartSettings shared;

        public ChartSettingsPanel(AxisSettingsPanel axisSettings, LegendSettingsPanel legendSettings,
                        ColorSettingsPanel colorSettings,
                        DataSettingsPanel dataSettings) {
                super(Lang.get("report.setting.chart"));

                build();

                shared = new SharedChartSettings(this, axisSettings, legendSettings, colorSettings, dataSettings);
        }

        private void build() {
                // !!! NE PAS CHANGER L'ORDRE DES ITEMS, IL CORRESPOND A L'ORDRE DE DataViewType
                chartTypeCombo = new JComboBox<>(new String[] {
                                Lang.get("report.setting.chart.bar"),
                                Lang.get("report.setting.chart.columns"),
                                Lang.get("report.setting.chart.pie"),
                                Lang.get("report.setting.chart.line"),
                                Lang.get("report.setting.chart.scatter"),
                                Lang.get("report.setting.chart.area"),
                                Lang.get("report.setting.chart.spider"),
                });
                chartTypeCombo.addItemListener(e -> {
                        DataViewType type = DataViewType.values()[chartTypeCombo.getSelectedIndex()];
                        switch (type) {
                                case PIE -> {
                                        shared.axis().setVisible(false);
                                }
                                default -> {
                                        shared.axis().setVisible(true);
                                }
                        }
                });
                addRow(new SettingRowPanel(Lang.get("report.setting.chart.type"), chartTypeCombo));

                titleField = new JTextField(Lang.get("report.setting.chart.default_title"));
                addRow(new SettingRowPanel(Lang.get("report.setting.chart.title"), titleField));

                stackedCheckBox = new JCheckBox();
                stackedCheckBox.setEnabled(false);
                addRow(new SettingRowPanel(Lang.get("report.setting.chart.stacked"), stackedCheckBox));
        }

        public JTextField getTitleField() {
                return titleField;
        }

        @Override
        public NoteBookValue createNoteBook() {
                NoteBookChart chart = new NoteBookChart(
                                DataViewType.values()[chartTypeCombo.getSelectedIndex()],
                                titleField.getText(),
                                stackedCheckBox.isSelected(),

                                shared.legend().isLegendVisible(),
                                shared.legend().isLegendHorizontal(),

                                shared.axis().getTickCount(),
                                shared.axis().getScale(),

                                shared.axis().isXVisible(),
                                shared.axis().getXLabel(),

                                shared.axis().isYVisible(),
                                shared.axis().getYLabel(),

                                shared.data().getTable(),
                                shared.data().getBiggerGroupColumn(),
                                shared.data().getGroupColumn(),
                                shared.data().getValueColumn(),
                                shared.data().isIncludeFilters(),
                                shared.data().isPercentage(),

                                shared.color().getColors());

                return chart;
        }

        @Override
        public void loadNoteBook(NoteBookValue value) {
                if (!(value instanceof NoteBookChart chart)) {
                        return;
                }
                chartTypeCombo.setSelectedIndex(chart.getType().ordinal());
                titleField.setText(chart.getTitle());

                shared.legend().setLegendVisible(chart.isLegendVisible());
                shared.legend().setLegendHorizontal(chart.isLegendHorizontal());

                shared.axis().setTickCount(chart.getTickCount());
                shared.axis().setScale(chart.getScale());

                shared.axis().setXVisible(chart.isXVisible());
                shared.axis().setXLabel(chart.getXLabel());
                shared.axis().setYVisible(chart.isYVisible());
                shared.axis().setYLabel(chart.getYLabel());

                shared.data().setBiggerGroupColumn(chart.getBiggerGroupColumn());
                shared.data().setGroupColumn(chart.getGroupColumn());
                shared.data().setValueColumn(chart.getValueColumn());
                shared.data().setIncludeFilters(chart.isIncludeFilters());
                shared.data().setIsPercentage(chart.isPercentage());

                shared.color().setColorLabels(chart.getColors(), chart.getColorLabels());

                GUIController.getInstance().setTable(chart.getTable());
                shared.data().setTable(chart.getTable());

        }
}
