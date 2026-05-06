package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart;

import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.services.TableService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingRowPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSectionPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingSeparatorRow;

public final class DataSettingsPanel extends SettingSectionPanel {

    private JComboBox<String> tables;
    private JComboBox<String> biggerGroupColumn;
    private JComboBox<String> groupColumn;
    private JComboBox<String> valueColumn;

    private List<Integer> removedGroupColumns = new ArrayList<>();
    private List<Integer> removedValueColumns = new ArrayList<>();

    private File currentTablePath = null;

    private JCheckBox filterInclude;
    private JCheckBox percentageCheck;

    public DataSettingsPanel() {
        super(Lang.get("report.setting.data"));
        build();
    }

    private void build() {
        removedGroupColumns.clear();
        removedValueColumns.clear();

        List<String> tablesPath = TableService.get().stream().map(table -> {
            if (table.getPath().equals(GUIController.getInstance().getTable().map(t -> t.getPath()).orElse(null))) {
                currentTablePath = table.getPath();
                return Lang.get("report.setting.data.current_table");
            } else {
                return table.getPath().toString();
            }
        }).collect(Collectors.toList());

        tables = new JComboBox<>(tablesPath.toArray(new String[0]));
        tables.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                File selectedTable = new File((String) tables.getSelectedItem());
                DataTable table = TableService.get(selectedTable);
                if (table != null) {
                    GUIController.getInstance().setTable(table);

                    clearRows();
                    build();
                    revalidate();
                    repaint();
                }
            }
        });
        addRow(new SettingRowPanel(Lang.get("report.setting.data.table"), tables));

        filterInclude = new JCheckBox();
        filterInclude.setSelected(true);
        addRow(new SettingRowPanel(Lang.get("report.setting.data.filter_include"), filterInclude));

        percentageCheck = new JCheckBox();
        percentageCheck.setSelected(false);
        addRow(new SettingRowPanel(Lang.get("report.setting.data.percentage"), percentageCheck));

        addRow(new SettingSeparatorRow(Lang.get("report.setting.data.columns")));

        String[] valueCols = getColumnNames(
                (table, i) -> {
                    if (table.getColumnType(i).isNumeric() || table.getColumnType(i).isDate()) {
                        return true;
                    } else {
                        removedValueColumns.add(i);
                        return false;
                    }
                }, true);
        valueColumn = new JComboBox<>(valueCols);
        addRow(new SettingRowPanel(Lang.get("report.setting.data.col_value"), valueColumn));

        String[] groupCols = getColumnNames((table, i) -> {
            if (table.getColumnType(i).isCategorical()) {
                return true;
            } else {
                removedGroupColumns.add(i);
                return false;
            }
        }, false);
        groupColumn = new JComboBox<>(groupCols);
        addRow(new SettingRowPanel(Lang.get("report.setting.data.col_group"), groupColumn));

        String[] allCols = getColumnNames((table, i) -> true, true);
        String[] colsWithNone = new String[allCols.length + 1];
        colsWithNone[0] = Lang.get("report.setting.data.col_none");
        System.arraycopy(allCols, 0, colsWithNone, 1, allCols.length);

        biggerGroupColumn = new JComboBox<>(colsWithNone);
        addRow(new SettingRowPanel(Lang.get("report.setting.data.col_bigger_group"), biggerGroupColumn));

    }

    private static interface ColumnFilter {
        boolean run(DataTable table, int columnIndex);
    }

    private String[] getColumnNames(ColumnFilter func, boolean showTypes) {
        return GUIController.getInstance().getTable().map(table -> {
            List<String> names = new ArrayList<>();
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (func.run(table, i)) {
                    if (showTypes) {
                        names.add(String.format("(%s) %s", table.getColumnType(i).toString(), table.getColumnName(i)));
                    } else {
                        names.add(table.getColumnName(i));
                    }
                }
            }
            return names.toArray(new String[0]);
        }).orElse(new String[] {});
    }

    public DataTable getTable() {
        String selected = (String) tables.getSelectedItem();
        if (selected.equals(Lang.get("report.setting.data.current_table"))) {
            selected = currentTablePath.toString();
        }

        return TableService.get(new File(selected));
    }

    public Optional<Integer> getBiggerGroupColumn() {
        int index = biggerGroupColumn.getSelectedIndex();
        if (index == 0) {
            return Optional.empty();
        } else {
            return Optional.of(index - 1);
        }
    }

    public int getGroupColumn() {
        int realIndex = groupColumn.getSelectedIndex();
        for (int removed : removedGroupColumns) {
            if (realIndex >= removed) {
                realIndex++;
            }
        }
        return realIndex;
    }

    public int getValueColumn() {
        int realIndex = valueColumn.getSelectedIndex();
        for (int removed : removedValueColumns) {
            if (realIndex >= removed) {
                realIndex++;
            }
        }
        return realIndex;
    }

    public boolean isIncludeFilters() {
        return filterInclude.isSelected();
    }

    public boolean isPercentage() {
        return percentageCheck.isSelected();
    }

    public void setIsPercentage(boolean percentage) {
        percentageCheck.setSelected(percentage);
    }

    public void setIncludeFilters(boolean include) {
        filterInclude.setSelected(include);
    }

    public void setGroupColumn(int index) {
        int removedBefore = (int) removedGroupColumns.stream().filter(removed -> removed <= index).count();
        int realIndex = index - removedBefore;
        if (realIndex >= 0 && realIndex < groupColumn.getItemCount()) {
            groupColumn.setSelectedIndex(realIndex);
        }
    }

    public void setValueColumn(int index) {
        int removedBefore = (int) removedValueColumns.stream().filter(removed -> removed <= index).count();
        int realIndex = index - removedBefore;
        if (realIndex >= 0 && realIndex < valueColumn.getItemCount()) {
            valueColumn.setSelectedIndex(realIndex);
        }
    }

    public void setBiggerGroupColumn(Optional<Integer> index) {
        if (index.isPresent() && biggerGroupColumn.getItemCount() > index.get() + 1) {
            biggerGroupColumn.setSelectedIndex(index.get() + 1);
        } else {
            biggerGroupColumn.setSelectedIndex(0);
        }
    }

    public void setTable(DataTable table) {
        if (table == null) {
            return;
        }
        String path = table.getPath().toString();
        if (currentTablePath != null && currentTablePath.equals(table.getPath())) {
            tables.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < tables.getItemCount(); i++) {
            String item = tables.getItemAt(i);
            if (item.equals(path) || (item.equals(Lang.get("report.setting.data.current_table"))
                    && GUIController.getInstance().getTable().map(t -> t.getPath()).orElse(null)
                            .equals(table.getPath()))) {
                tables.setSelectedIndex(i);
                break;
            }
        }
    }
}
