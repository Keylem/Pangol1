package fr.univrennes.istic.l2gen.application.gui.panels.table;

import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.data.TableDataView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.empty.EmptyView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.list.TableListView;

import javax.swing.JPanel;
import java.awt.CardLayout;

public final class TablePanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();

    private final TableListView tableListView;
    private final TableDataView tableDataView;

    public TablePanel() {
        setLayout(cardLayout);

        add(new EmptyView(), TableViewState.EMPTY.name());

        tableListView = new TableListView(this);
        add(tableListView, TableViewState.LIST.name());

        tableDataView = new TableDataView(this);
        add(tableDataView, TableViewState.TABLE.name());

        cardLayout.show(this, TableViewState.EMPTY.name());
    }

    public TableDataView getTableView() {
        return tableDataView;
    }

    public TableListView getListView() {
        return tableListView;
    }

    public boolean open(DataTable table) {
        tableDataView.open(table);
        cardLayout.show(TablePanel.this, TableViewState.TABLE.name());
        return true;
    }

    public void close() {
        tableDataView.close();
        if (tableListView.isEmpty()) {
            cardLayout.show(this, TableViewState.EMPTY.name());
        } else {
            cardLayout.show(this, TableViewState.LIST.name());
        }
    }

    public void refresh() {
        tableListView.refresh();

        if (GUIController.getInstance().getTable().isEmpty()) {
            if (tableListView.isEmpty()) {
                cardLayout.show(this, TableViewState.EMPTY.name());
                return;
            }

            cardLayout.show(this, TableViewState.LIST.name());
        } else {
            tableDataView.refresh();
            cardLayout.show(this, TableViewState.TABLE.name());
        }

    }

}