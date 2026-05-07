package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import java.sql.Timestamp;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import fr.univrennes.istic.l2gen.application.core.TaskStatus;
import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookText;
import fr.univrennes.istic.l2gen.application.core.services.notebook.NoteBookService;
import fr.univrennes.istic.l2gen.application.core.services.stats.StatisticService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputDateDialog;
import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputDoubleDialog;
import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputIntDialog;
import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputSelectDialog;
import fr.univrennes.istic.l2gen.application.gui.dialog.input.InputStringDialog;
import fr.univrennes.istic.l2gen.application.gui.dialog.stats.StatisticsDialog;

public final class TableColumnContextMenu extends JPopupMenu {
        private static final int MAX_CATEGORIES = 25;

        private final DataTable table;
        private final int tableIndex;
        private final DataType columnType;
        private final TableDataView tableView;

        public TableColumnContextMenu(TableDataView tableView, int tableIndex) {
                this.table = tableView.getTableModel().getTable().get();
                this.tableView = tableView;
                this.tableIndex = tableIndex;
                this.columnType = table.getColumnType(tableIndex);

                add(buildSortMenu());
                addSeparator();
                add(buildFilterMenu());
                addSeparator();
                add(buildStatsMenu());
                addSeparator();

                if (Config.getBoolean("settings.table.manual_typing", true)) {
                        JMenu changeTypeItem = new JMenu(Lang.get("tablecolumnmenu.change_type"));
                        for (DataType type : DataType.values()) {
                                String typeDisplayName = Lang.get("table.type." + type.name().toLowerCase());
                                JMenuItem typeItem = new JMenuItem(typeDisplayName);
                                typeItem.addActionListener(e -> {
                                        int confirm = JOptionPane.showConfirmDialog(tableView,
                                                        Lang.get("tablecolumnmenu.change_type.confirm",
                                                                        tableView.getTableView()
                                                                                        .getColumnName(tableIndex),
                                                                        typeDisplayName),
                                                        Lang.get("tablecolumnmenu.change_type.confirm_title"),
                                                        JOptionPane.YES_NO_OPTION);
                                        if (confirm == JOptionPane.YES_OPTION) {
                                                String taskId = GUIController.getInstance().addTask(
                                                                Lang.get("task.table.change_type",
                                                                                tableView.getTableView().getColumnName(
                                                                                                tableIndex),
                                                                                typeDisplayName),
                                                                TaskStatus.RUNNING);

                                                boolean success = table.setColumnType(tableIndex, type);
                                                GUIController.getInstance().updateTaskStatus(taskId,
                                                                success ? TaskStatus.SUCCESS : TaskStatus.FAILED);
                                        }
                                });
                                changeTypeItem.add(typeItem);
                        }
                        add(changeTypeItem);
                }

                JMenuItem renameColumnItem = new JMenuItem(Lang.get("tablecolumnmenu.rename"));
                renameColumnItem.addActionListener(e -> {
                        String newName = JOptionPane.showInputDialog(tableView,
                                        Lang.get("tablecolumnmenu.enter_new_name"),
                                        tableView.getColumnName(tableIndex));
                        if (newName != null && !newName.isBlank()) {
                                tableView.renameColumn(tableView.getTableToViewIndex(tableIndex), newName);
                                tableView.refresh();
                        }
                });
                add(renameColumnItem);

                JMenuItem hideColumnItem = new JMenuItem(Lang.get("tablecolumnmenu.hide"));
                hideColumnItem.addActionListener(e -> tableView.hideColumn(tableView.getTableToViewIndex(tableIndex)));

                add(hideColumnItem);
        }

        private JMenu buildSortMenu() {
                JMenu sortMenu = new JMenu(Lang.get("tablecolumnmenu.sort"));

                JMenuItem sortAscendingItem = new JMenuItem(Lang.get("tablecolumnmenu.sort.ascending"));
                sortAscendingItem.addActionListener(e -> {
                        table.clearFilters();
                        table.addFilter(Filter.sort(tableIndex, true));
                        GUIController.getInstance().getMainView().getTablePanel().refresh();
                });

                JMenuItem sortDescendingItem = new JMenuItem(Lang.get("tablecolumnmenu.sort.descending"));
                sortDescendingItem
                                .addActionListener(e -> {
                                        table.clearFilters();
                                        table.addFilter(Filter.sort(tableIndex, false));
                                        GUIController.getInstance().getMainView().getTablePanel().refresh();
                                });

                sortMenu.add(sortAscendingItem);
                sortMenu.add(sortDescendingItem);
                return sortMenu;
        }

        private JMenu buildFilterMenu() {
                JMenu filterMenu = new JMenu(Lang.get("tablecolumnmenu.filter"));

                JMenuItem filterTopNItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.topn"));
                filterTopNItem.addActionListener(e -> {
                        try {
                                switch (columnType) {
                                        case STRING -> {
                                                int length = InputIntDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.length"),
                                                                Lang.get("tablecolumnmenu.filter.topn"),
                                                                Lang.get("tablecolumnmenu.filter.length.error"))
                                                                .get();
                                                if (length > 0) {
                                                        table.addFilter(Filter.topN(tableIndex, length));
                                                }
                                        }
                                        case INTEGER, DOUBLE -> {
                                                double value = InputDoubleDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.double"),
                                                                Lang.get("tablecolumnmenu.filter.topn"),
                                                                Lang.get("tablecolumnmenu.filter.double.error"))
                                                                .get();
                                                table.addFilter(Filter.topN(tableIndex, value));
                                        }
                                        case DATE -> {
                                                java.util.Date date = InputDateDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.date"),
                                                                Lang.get("tablecolumnmenu.filter.topn"),
                                                                Lang.get("tablecolumnmenu.filter.date.error"))
                                                                .get();

                                                Timestamp sqlDate = new Timestamp(date.getTime());
                                                table.addFilter(Filter.topN(tableIndex, sqlDate));
                                        }
                                        default -> {
                                        }
                                }
                                tableView.refresh();
                        } catch (Exception ignored) {
                        }

                });

                JMenuItem filterBottomNItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.bottomn"));
                filterBottomNItem.addActionListener(e -> {
                        try {
                                switch (columnType) {
                                        case STRING -> {
                                                int length = InputIntDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.length"),
                                                                Lang.get("tablecolumnmenu.filter.bottomn"),
                                                                Lang.get("tablecolumnmenu.filter.length.error"))
                                                                .get();
                                                if (length > 0) {
                                                        table.addFilter(Filter.bottomN(tableIndex, length));
                                                }
                                        }
                                        case INTEGER, DOUBLE -> {
                                                double value = InputDoubleDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.double"),
                                                                Lang.get("tablecolumnmenu.filter.bottomn"),
                                                                Lang.get("tablecolumnmenu.filter.double.error"))
                                                                .get();
                                                table.addFilter(Filter.bottomN(tableIndex, value));
                                        }
                                        case DATE -> {
                                                java.util.Date date = InputDateDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.date"),
                                                                Lang.get("tablecolumnmenu.filter.bottomn"),
                                                                Lang.get("tablecolumnmenu.filter.date.error"))
                                                                .get();

                                                Timestamp sqlDate = new Timestamp(date.getTime());
                                                table.addFilter(Filter.bottomN(tableIndex, sqlDate));
                                        }
                                        default -> {
                                        }
                                }

                                tableView.refresh();
                        } catch (Exception ignored) {
                        }
                });

                JMenuItem filterNumericRangeItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.range"));
                filterNumericRangeItem.addActionListener(e -> {
                        try {
                                switch (columnType) {
                                        case STRING -> {
                                                int minLength = InputIntDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.length"),
                                                                Lang.get("tablecolumnmenu.filter.range.min"),
                                                                Lang.get("tablecolumnmenu.filter.length.error"))
                                                                .get();
                                                int maxLength = InputIntDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.length"),
                                                                Lang.get("tablecolumnmenu.filter.range.max"),
                                                                Lang.get("tablecolumnmenu.filter.length.error")).get();
                                                maxLength = Math.max(maxLength, minLength);
                                                minLength = Math.min(minLength, maxLength);
                                                table.addFilter(Filter.byRange(tableIndex, minLength, maxLength));
                                        }
                                        case INTEGER, DOUBLE -> {
                                                double minValue = InputDoubleDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.double"),
                                                                Lang.get("tablecolumnmenu.filter.range.min"),
                                                                Lang.get("tablecolumnmenu.filter.double.error"))
                                                                .get();
                                                double maxValue = InputDoubleDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.double"),
                                                                Lang.get("tablecolumnmenu.filter.range.max"),
                                                                Lang.get("tablecolumnmenu.filter.double.error"))
                                                                .get();
                                                maxValue = Math.max(maxValue, minValue);
                                                minValue = Math.min(minValue, maxValue);
                                                table.addFilter(Filter.byRange(tableIndex, minValue, maxValue));
                                        }
                                        case DATE -> {
                                                java.util.Date minDate = InputDateDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.date"),
                                                                Lang.get("tablecolumnmenu.filter.range.min"),
                                                                Lang.get("tablecolumnmenu.filter.date.error"))
                                                                .get();
                                                java.util.Date maxDate = InputDateDialog.show(
                                                                Lang.get("tablecolumnmenu.filter.date"),
                                                                Lang.get("tablecolumnmenu.filter.range.max"),
                                                                Lang.get("tablecolumnmenu.filter.date.error"))
                                                                .get();
                                                if (maxDate.before(minDate)) {
                                                        java.util.Date temp = minDate;
                                                        minDate = maxDate;
                                                        maxDate = temp;
                                                }
                                                table.addFilter(Filter.byRange(tableIndex,
                                                                new Timestamp(minDate.getTime()),
                                                                new Timestamp(maxDate.getTime())));
                                        }
                                        default -> {
                                        }
                                }
                                tableView.refresh();
                        } catch (Exception ignored) {
                        }
                });

                JMenuItem filterEmptyItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.empty"));
                filterEmptyItem.addActionListener(e -> {
                        table.addFilter(Filter.showEmpty(tableIndex));
                        tableView.refresh();
                });

                JMenuItem filterNonEmptyItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.non_empty"));
                filterNonEmptyItem.addActionListener(e -> {
                        table.addFilter(Filter.hideEmpty(tableIndex));
                        tableView.refresh();
                });

                JMenuItem clearFilterItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.clear"));
                clearFilterItem.addActionListener(e -> {
                        table.clearColumnFilter(tableIndex);
                        tableView.refresh();
                });

                filterMenu.add(filterTopNItem);
                filterMenu.add(filterBottomNItem);
                filterMenu.addSeparator();
                filterMenu.add(filterNumericRangeItem);
                filterMenu.addSeparator();
                filterMenu.add(filterEmptyItem);
                filterMenu.add(filterNonEmptyItem);
                filterMenu.addSeparator();

                if (columnType.isCategorical()) {
                        new SwingWorker<>() {
                                private List<String> categories;
                                private boolean hasCategories;

                                @Override
                                protected Void doInBackground() throws Exception {
                                        hasCategories = StatisticService.hasColumnCategories(table, tableIndex);
                                        categories = StatisticService.getColumnCategories(table, tableIndex);
                                        return null;
                                }

                                @Override
                                protected void done() {
                                        if (hasCategories) {
                                                if (categories.size() > MAX_CATEGORIES) {
                                                        JMenuItem filterByCategory = new JMenuItem(
                                                                        Lang.get("tablecolumnmenu.filter.by_category"));
                                                        filterByCategory.addActionListener(e -> {
                                                                String category = InputSelectDialog.show(
                                                                                categories,
                                                                                Lang.get("tablecolumnmenu.filter.category"),
                                                                                Lang.get("tablecolumnmenu.filter.by_category"),
                                                                                Lang.get("tablecolumnmenu.filter.category.error"))
                                                                                .orElse(null);
                                                                if (category != null && !category.isBlank()) {
                                                                        table.addFilter(Filter.equals(tableIndex,
                                                                                        category));
                                                                        tableView.refresh();
                                                                }
                                                        });
                                                        filterMenu.add(filterByCategory);
                                                } else {
                                                        JMenu filterByCategoryMenu = new JMenu(
                                                                        Lang.get("tablecolumnmenu.filter.by_category"));
                                                        for (String category : categories) {
                                                                JMenuItem categoryItem = new JMenuItem(category);
                                                                categoryItem.addActionListener(e -> {
                                                                        table.addFilter(Filter.equals(tableIndex,
                                                                                        category));
                                                                        tableView.refresh();
                                                                });
                                                                filterByCategoryMenu.add(categoryItem);
                                                        }
                                                        filterMenu.add(filterByCategoryMenu);
                                                }
                                        } else {
                                                JMenuItem filterByValueItem = new JMenuItem(
                                                                Lang.get("tablecolumnmenu.filter.by_value"));
                                                filterByValueItem.addActionListener(e -> {
                                                        String value = InputStringDialog.show(
                                                                        Lang.get("tablecolumnmenu.filter.value"),
                                                                        Lang.get("tablecolumnmenu.filter.by_value"),
                                                                        Lang.get("tablecolumnmenu.filter.value.error"))
                                                                        .orElse(null);
                                                        if (value != null && !value.isBlank()) {
                                                                table.addFilter(Filter.search(tableIndex, value));
                                                                tableView.refresh();
                                                        }
                                                });
                                                filterMenu.add(filterByValueItem);
                                        }

                                        filterMenu.addSeparator();
                                        filterMenu.add(clearFilterItem);

                                        filterMenu.revalidate();
                                        filterMenu.repaint();

                                        TableColumnContextMenu.this.revalidate();
                                        TableColumnContextMenu.this.repaint();
                                }
                        }.execute();
                } else {
                        filterMenu.addSeparator();
                        filterMenu.add(clearFilterItem);
                }
                return filterMenu;
        }

        private JMenu buildStatsMenu() {

                JMenu stats = new JMenu(Lang.get("tablecolumnmenu.stats"));
                JMenuItem summaryItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.summary"));
                summaryItem.addActionListener(e -> {
                        String summary = StatisticService.computeSummary(table, tableIndex);
                        StatisticsDialog dialog = new StatisticsDialog(GUIController.getInstance().getMainView(),
                                        Lang.get("statistics.summary.title.column", table.getColumnName(tableIndex)),
                                        summary);
                        showStatsDialog(dialog, summary);
                });
                stats.add(summaryItem);

                JMenuItem nullRateItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.null_rate"));
                nullRateItem.addActionListener(e -> {
                        OptionalDouble nullRateOpt = StatisticService.computeNullRate(table, tableIndex);
                        String nullRateStr = nullRateOpt.isPresent() ? String.format("%.2f%%",
                                        nullRateOpt.getAsDouble() * 100) : "N/A";

                        StatisticsDialog dialog = new StatisticsDialog(GUIController.getInstance().getMainView(),
                                        Lang.get("statistics.null_rate.title",
                                                        table.getColumnName(tableIndex)),
                                        Lang.get("statistics.null_rate.content", nullRateStr));
                        showStatsDialog(dialog, Lang.get("statistics.null_rate.content", nullRateStr));
                });
                stats.add(nullRateItem);

                JMenuItem cardinalityRatioItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.cardinality_ratio"));
                cardinalityRatioItem
                                .addActionListener(e -> {
                                        OptionalDouble cardinalityRatioOpt = StatisticService.computeCardinalityRatio(
                                                        table,
                                                        tableIndex);
                                        String cardinalityRatioStr = cardinalityRatioOpt.isPresent()
                                                        ? String.format("%.2f%%",
                                                                        cardinalityRatioOpt.getAsDouble() * 100)
                                                        : "N/A";
                                        StatisticsDialog dialog = new StatisticsDialog(
                                                        GUIController.getInstance().getMainView(),
                                                        Lang.get("statistics.cardinality_ratio.title",
                                                                        table.getColumnName(tableIndex)),
                                                        Lang.get("statistics.cardinality_ratio.content",
                                                                        cardinalityRatioStr));
                                        showStatsDialog(dialog,
                                                        Lang.get("statistics.cardinality_ratio.content",
                                                                        cardinalityRatioStr));
                                });
                stats.add(cardinalityRatioItem);

                if (this.table.getColumnType(tableIndex).isNumeric()) {
                        JMenuItem interquartileRangeItem = new JMenuItem(
                                        Lang.get("tablecolumnmenu.stats.interquartile_range"));
                        interquartileRangeItem.addActionListener(
                                        e -> {
                                                OptionalDouble iqrOpt = StatisticService
                                                                .computeInterquartileRange(table, tableIndex);
                                                String iqrStr = iqrOpt.isPresent()
                                                                ? String.format("%.4f", iqrOpt.getAsDouble())
                                                                : "N/A";
                                                StatisticsDialog dialog = new StatisticsDialog(
                                                                GUIController.getInstance().getMainView(),
                                                                Lang.get("statistics.interquartile_range.title",
                                                                                table.getColumnName(tableIndex)),
                                                                Lang.get("statistics.interquartile_range.content",
                                                                                iqrStr));
                                                showStatsDialog(dialog,
                                                                Lang.get("statistics.interquartile_range.content",
                                                                                iqrStr));
                                        });
                        stats.add(interquartileRangeItem);

                        JMenuItem skewnessItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.skewness"));
                        skewnessItem.addActionListener(e -> {
                                OptionalDouble skewnessOpt = StatisticService.computeSkewness(table, tableIndex);
                                String skewnessStr = skewnessOpt.isPresent()
                                                ? String.format("%.4f", skewnessOpt.getAsDouble())
                                                : "N/A";
                                StatisticsDialog dialog = new StatisticsDialog(
                                                GUIController.getInstance().getMainView(),
                                                Lang.get("statistics.skewness.title",
                                                                table.getColumnName(tableIndex)),
                                                Lang.get("statistics.skewness.content", skewnessStr));
                                showStatsDialog(dialog, Lang.get("statistics.skewness.content", skewnessStr));
                        });
                        stats.add(skewnessItem);

                        JMenuItem coefVariationItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.coef_variation"));
                        coefVariationItem.addActionListener(
                                        e -> {
                                                OptionalDouble coefVarOpt = StatisticService
                                                                .computeCoefficientOfVariation(table, tableIndex);
                                                String coefVarStr = coefVarOpt.isPresent()
                                                                ? String.format("%.4f", coefVarOpt.getAsDouble())
                                                                : "N/A";
                                                StatisticsDialog dialog = new StatisticsDialog(
                                                                GUIController.getInstance().getMainView(),
                                                                Lang.get("statistics.coefficient_of_variation.title",
                                                                                table.getColumnName(tableIndex)),
                                                                Lang.get("statistics.coefficient_of_variation.content",
                                                                                coefVarStr));
                                                showStatsDialog(dialog,
                                                                Lang.get("statistics.coefficient_of_variation.content",
                                                                                coefVarStr));
                                        });
                        stats.add(coefVariationItem);

                        JMenu correlationItem = new JMenu(Lang.get("tablecolumnmenu.stats.correlation"));
                        this.columnSelector(correlationItem,
                                        i -> i != tableIndex && table.getColumnType(i).isNumeric(),
                                        targetIndex -> {
                                                OptionalDouble correlationOpt = StatisticService.computeCorrelation(
                                                                table, tableIndex,
                                                                targetIndex);
                                                String correlationStr = correlationOpt.isPresent()
                                                                ? String.format("%.4f", correlationOpt.getAsDouble())
                                                                : "N/A";
                                                StatisticsDialog dialog = new StatisticsDialog(
                                                                GUIController.getInstance().getMainView(),
                                                                Lang.get("statistics.correlation.title",
                                                                                table.getColumnName(tableIndex),
                                                                                table.getColumnName(targetIndex)),
                                                                Lang.get("statistics.correlation.content",
                                                                                correlationStr));
                                                showStatsDialog(dialog,
                                                                Lang.get("statistics.correlation.content",
                                                                                correlationStr));
                                                return null;
                                        });

                        stats.add(correlationItem);
                }

                return stats;
        }

        private void columnSelector(JMenu menu, Function<Integer, Boolean> condition, Function<Integer, Void> action) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                        if (!condition.apply(i)) {
                                continue;
                        }
                        JMenuItem colItem = new JMenuItem(table.getColumnName(i));
                        int colIndex = i;
                        colItem.addActionListener(e -> action.apply(colIndex));
                        menu.add(colItem);
                }
        }

        private void showStatsDialog(StatisticsDialog dialog, String notebookContent) {
                dialog.setVisible(true);
                if (dialog.isAddedToNotebook()) {
                        NoteBookService.add(new NoteBookText(notebookContent));
                        GUIController.getInstance().getMainView().getReportPanel().refresh();
                }
        }
}