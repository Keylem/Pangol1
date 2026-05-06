package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.AbstractListModel;

public final class RowHeaderListModel extends AbstractListModel<String> {
    private final TableRowHeader tableRowHeader;

    public RowHeaderListModel(TableRowHeader tableRowHeader) {
        this.tableRowHeader = tableRowHeader;
    }

    @Override
    public int getSize() {
        return this.tableRowHeader.tableModel.getRowCount();
    }

    @Override
    public String getElementAt(int index) {
        return Integer.toString(this.tableRowHeader.tableModel.getRowNumberAt(index));
    }

    public void refresh() {
        int lastIndex = Math.max(0, getSize() - 1);
        fireContentsChanged(this, 0, lastIndex);
    }
}