package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import java.awt.Color;
import java.awt.FontMetrics;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public final class TableRowHeader implements TableModelListener {

    final JTable tableView;
    final TableModel tableModel;
    private final RowHeaderListModel listModel;
    private final JList<String> rowList;

    public TableRowHeader(JTable tableView, TableModel tableModel) {
        this.tableView = tableView;
        this.tableModel = tableModel;
        this.listModel = new RowHeaderListModel(this);
        this.rowList = new JList<>(listModel);

        rowList.setFont(tableView.getTableHeader().getFont());
        rowList.setBackground(new Color(245, 245, 245));
        rowList.setForeground(tableView.getTableHeader().getForeground());
        rowList.setFocusable(false);
        rowList.setFixedCellHeight(tableView.getRowHeight());
        rowList.setCellRenderer(new RowHeaderRenderer(this));

        tableModel.addTableModelListener(this);

        refresh();
    }

    public JComponent getComponent() {
        return rowList;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        refresh();
    }

    public void refresh() {
        rowList.setFixedCellHeight(tableView.getRowHeight());
        listModel.refresh();
        updateWidth();
    }

    private void updateWidth() {
        int totalRows = Math.max(1, tableModel.getTotalRowCount());
        String sample = Integer.toString(totalRows);

        FontMetrics metrics = rowList.getFontMetrics(rowList.getFont());
        int width = metrics.stringWidth(sample) + 16;

        rowList.setFixedCellWidth(width);
    }
}
