package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public final class TableDataViewHeader implements TableCellRenderer {

    private final TableCellRenderer defaultRenderer;
    private final Map<Integer, Icon> columnIcons = new HashMap<>();

    public TableDataViewHeader(TableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    public void setIcon(int viewIndex, Icon icon) {
        columnIcons.put(viewIndex, icon);
    }

    public void clearIcon(int viewIndex) {
        columnIcons.remove(viewIndex);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int viewIndex) {

        Component component = defaultRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, viewIndex);

        if (component instanceof JLabel label) {
            label.setIcon(columnIcons.getOrDefault(viewIndex, null));
            label.setHorizontalTextPosition(JLabel.LEFT);
        }

        return component;
    }
}
