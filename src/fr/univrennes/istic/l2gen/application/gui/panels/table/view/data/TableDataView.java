package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.config.Ico;
import fr.univrennes.istic.l2gen.application.core.services.stats.StatisticOp;
import fr.univrennes.istic.l2gen.application.core.services.stats.StatisticService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TableDataView extends JPanel {

    private final JTable tableView;
    private final JScrollPane tableScrollPane;

    private final TableModel tableModel;
    private final TableRowHeader rowHeader;
    private final TableToolBar toolBar;
    private final TablePagination paginationBar;

    private List<Integer> hiddenColumns = new ArrayList<>();

    public TableDataView(TablePanel tablePanel) {
        super(new BorderLayout());

        tableModel = new TableModel(this);
        tableView = new JTable(tableModel);

        tableView.setShowGrid(true);
        tableView.setFillsViewportHeight(true);
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableView.setRowSorter(null);
        tableView.getTableHeader().setReorderingAllowed(false);

        TableDataView selfView = this;

        TableDataViewHeader headerRenderer = new TableDataViewHeader(tableView.getTableHeader().getDefaultRenderer());
        tableView.getTableHeader().setDefaultRenderer(headerRenderer);
        tableView.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedColumnIndex = tableView.columnAtPoint(e.getPoint());
                if (clickedColumnIndex == -1) {
                    return;
                }

                int realColumnIndex = getRealColumnIndex(clickedColumnIndex);
                if (SwingUtilities.isRightMouseButton(e)) {
                    TableColumnContextMenu contextMenu = new TableColumnContextMenu(selfView, realColumnIndex);
                    contextMenu.show(tableView.getTableHeader(), e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    selectColumn(clickedColumnIndex);
                    onColumnSelected(realColumnIndex);
                }
            }
        });

        rowHeader = new TableRowHeader(tableView, tableModel);
        tableScrollPane = new JScrollPane(tableView);
        applyRowHeaderVisibility();

        paginationBar = new TablePagination(tableModel);
        toolBar = new TableToolBar(tablePanel, this);

        add(toolBar, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(paginationBar, BorderLayout.SOUTH);
    }

    private int getRealColumnIndex(int viewColumnIndex) {
        int realColumnIndex = viewColumnIndex;
        for (int hiddenColumn : hiddenColumns) {
            if (hiddenColumn <= realColumnIndex) {
                realColumnIndex++;
            } else {
                break;
            }
        }
        return realColumnIndex;
    }

    private void onColumnSelected(int columnIndex) {
        if (!Config.get().getBoolean("settings.table.columns.calculate_statistics", true)) {
            return;
        }
        GUIController.getInstance().getTable().ifPresent(table -> {
            GUIController.getInstance().getMainView().getBottomBar().clearColumnStats();

            GUIController.getInstance().enableLoading();
            new SwingWorker<List<Optional<String>>, Void>() {

                @Override
                protected List<Optional<String>> doInBackground() throws Exception {
                    Optional<String> min = StatisticService.computeBase(table,
                            columnIndex,
                            StatisticOp.MIN);
                    Optional<String> max = StatisticService.computeBase(table,
                            columnIndex,
                            StatisticOp.MAX);
                    Optional<String> avg = StatisticService.computeBase(table,
                            columnIndex,
                            StatisticOp.AVG);
                    Optional<String> sum = StatisticService.computeBase(table,
                            columnIndex,
                            StatisticOp.SUM);

                    return List.of(min, max, avg, sum);
                }

                @Override
                protected void done() {
                    try {
                        Optional<String> min = get().get(0);
                        Optional<String> max = get().get(1);
                        Optional<String> avg = get().get(2);
                        Optional<String> sum = get().get(3);

                        SwingUtilities.invokeLater(() -> {
                            GUIController.getInstance().getMainView().getBottomBar().setColumnStats(min, max, avg, sum);
                        });

                    } catch (Exception e) {
                        GUIController.getInstance().onException(e);
                    } finally {
                        GUIController.getInstance().disableLoading();
                    }
                }
            }.execute();
        });
    }

    public void open(DataTable table) {
        tableModel.open(table);
        paginationBar.refresh();
        toolBar.refresh();
        applyRowHeaderVisibility();
        adjustColumnWidths();

        if (Config.get().getBoolean("settings.table.columns.hide_empty", false)) {
            hideEmptyColumns();
        }
    }

    public void close() {
        tableModel.close();
        paginationBar.refresh();
        hiddenColumns.clear();
        applyRowHeaderVisibility();
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
        paginationBar.refresh();
        updateHeaderIcons();
        applyRowHeaderVisibility();
        adjustColumnWidths();
    }

    public JTable getTableView() {
        return tableView;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void hideColumn(int columnIndex) {
        TableColumnModel columnModel = tableView.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        hiddenColumns.add(columnIndex);
        tableView.removeColumn(columnModel.getColumn(columnIndex));
        updateHeaderIcons();
    }

    public void renameColumn(int columnIndex, String newName) {
        TableColumnModel columnModel = tableView.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        columnModel.getColumn(columnIndex).setHeaderValue(newName);
        tableView.getTableHeader().repaint();
    }

    public boolean hasHiddenColumns() {
        return !hiddenColumns.isEmpty();
    }

    public void showAllColumns() {
        hiddenColumns.clear();
        TableColumnModel columnModel = tableView.getColumnModel();
        while (columnModel.getColumnCount() > 0) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }
        tableView.createDefaultColumnsFromModel();

        updateHeaderIcons();
        adjustColumnWidths();
    }

    public void hideEmptyColumns() {
        DataTable table = tableModel.getTable().orElse(null);
        if (table == null) {
            return;
        }

        int viewIndex = 0;
        int tableIndex = 0;
        int viewColumnCount = tableView.getColumnCount();
        while (tableIndex < table.getColumnCount() && viewIndex < viewColumnCount) {

            if (table.getColumnType(tableIndex) == DataType.EMPTY) {
                tableView.removeColumn(tableView.getColumnModel().getColumn(viewIndex));
                hiddenColumns.add(tableIndex);

                viewColumnCount--;
                tableIndex++;
                continue;
            }

            viewIndex++;
            tableIndex++;
        }
        updateHeaderIcons();
    }

    public void selectColumn(int columnIndex) {
        TableColumnModel columnModel = tableView.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        tableView.setColumnSelectionAllowed(true);
        tableView.setRowSelectionAllowed(false);
        tableView.clearSelection();
        tableView.setColumnSelectionInterval(columnIndex, columnIndex);
        tableView.selectAll();
        tableView.setColumnSelectionInterval(columnIndex, columnIndex);
    }

    private void applyRowHeaderVisibility() {
        boolean showRowNumbers = Config.get().getBoolean("settings.table.show_row_numbers", false);
        if (showRowNumbers && tableModel.getTable().isPresent()) {
            rowHeader.refresh();
            tableScrollPane.setRowHeaderView(rowHeader.getComponent());
        } else {
            tableScrollPane.setRowHeaderView(null);
        }
    }

    private void updateHeaderIcons() {
        TableDataViewHeader headerRenderer = (TableDataViewHeader) tableView.getTableHeader().getDefaultRenderer();
        DataTable table = tableModel.getTable().orElse(null);
        if (table == null) {
            return;
        }

        Icon filterIcon = Ico.get("icons/filter_on.svg");
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            boolean hasFilter = table.getColumnFilters(columnIndex).size() > 0;
            if (hasFilter) {
                headerRenderer.setIcon(columnIndex, filterIcon);
            } else {
                headerRenderer.clearIcon(columnIndex);
            }
        }
        tableView.getTableHeader().repaint();
    }

    private void adjustColumnWidths() {
        if (!Config.get().getBoolean("settings.table.columns.auto_resize", true)) {
            return;
        }

        int sampleRowCount = Math.min(tableView.getRowCount(), 50);
        for (int columnIndex = 0; columnIndex < tableView.getColumnCount(); columnIndex++) {
            int maxWidth = 0;
            TableColumn tableColumn = tableView.getColumnModel().getColumn(columnIndex);

            TableCellRenderer headerRenderer = tableView.getTableHeader().getDefaultRenderer();
            Component headerComponent = headerRenderer.getTableCellRendererComponent(
                    tableView, tableColumn.getHeaderValue(), false, false, -1, columnIndex);
            maxWidth = Math.max(maxWidth, headerComponent.getPreferredSize().width);

            for (int rowIndex = 0; rowIndex < sampleRowCount; rowIndex++) {
                TableCellRenderer cellRenderer = tableView.getCellRenderer(rowIndex, columnIndex);
                Component cellComponent = cellRenderer.getTableCellRendererComponent(
                        tableView, tableView.getValueAt(rowIndex, columnIndex), false, false, rowIndex, columnIndex);
                maxWidth = Math.max(maxWidth, cellComponent.getPreferredSize().width);
            }

            tableColumn.setPreferredWidth(maxWidth + 16);
        }
    }
}