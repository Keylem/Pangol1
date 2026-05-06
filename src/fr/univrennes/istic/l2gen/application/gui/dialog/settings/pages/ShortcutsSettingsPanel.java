package fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.SettingsPage;

public final class ShortcutsSettingsPanel extends JPanel implements SettingsPage {

    private static final Object[][] SHORTCUT_DATA = {
            { Lang.get("settings.shortcuts.table"), Lang.get("settings.shortcuts.table.open"), "Ctrl+O" },
            { Lang.get("settings.shortcuts.table"), Lang.get("settings.shortcuts.table.close"), "Ctrl+W" },

            { Lang.get("settings.shortcuts.view"), Lang.get("settings.shortcuts.view.filter"), "Ctrl+F" },
            { Lang.get("settings.shortcuts.view"), Lang.get("settings.shortcuts.view.settings"), "Ctrl+," },

            { Lang.get("settings.shortcuts.sort"), Lang.get("settings.shortcuts.sort.sort_asc"), "Ctrl+Shift+A" },
            { Lang.get("settings.shortcuts.sort"), Lang.get("settings.shortcuts.sort.sort_desc"), "Ctrl+Shift+D" },

            { Lang.get("settings.shortcuts.undo"), Lang.get("settings.shortcuts.notebook.undo"), "Ctrl+Z" },
            { Lang.get("settings.shortcuts.redo"), Lang.get("settings.shortcuts.notebook.redo"), "Ctrl+Y" }
    };

    public ShortcutsSettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] columnHeaders = {
                Lang.get("settings.shortcuts.column.category"),
                Lang.get("settings.shortcuts.column.action"),
                Lang.get("settings.shortcuts.column.shortcut")
        };

        DefaultTableModel tableModel = new DefaultTableModel(SHORTCUT_DATA, columnHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable shortcutsTable = new JTable(tableModel);
        shortcutsTable.setFont(shortcutsTable.getFont().deriveFont(Font.PLAIN, 12f));
        shortcutsTable.setRowHeight(26);
        shortcutsTable.setShowVerticalLines(false);
        shortcutsTable.setShowHorizontalLines(true);
        shortcutsTable.setGridColor(UIManager.getColor("Separator.foreground"));
        shortcutsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shortcutsTable.getTableHeader().setFont(shortcutsTable.getFont().deriveFont(Font.BOLD, 11f));
        shortcutsTable.getTableHeader().setReorderingAllowed(false);

        shortcutsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        shortcutsTable.getColumnModel().getColumn(1).setPreferredWidth(280);
        shortcutsTable.getColumnModel().getColumn(2).setPreferredWidth(120);

        DefaultTableCellRenderer shortcutCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2 && !isSelected) {
                    setFont(getFont().deriveFont(Font.BOLD, 12f));
                    setForeground(UIManager.getColor("Label.disabledForeground"));
                    setBorder(BorderFactory.createCompoundBorder(
                            new EmptyBorder(2, 4, 2, 4),
                            BorderFactory.createLineBorder(
                                    UIManager.getColor("Separator.foreground"), 1)));
                } else if (!isSelected) {
                    setForeground(UIManager.getColor("Table.foreground"));
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                }
                return this;
            }
        };
        shortcutsTable.getColumnModel().getColumn(2).setCellRenderer(shortcutCellRenderer);

        JScrollPane scrollPane = new JScrollPane(shortcutsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground"), 1));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 400));

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public boolean applySettings() {
        return false;
    }

    @Override
    public boolean requiresRestart() {
        return false;
    }
}
