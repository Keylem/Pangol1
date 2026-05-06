package fr.univrennes.istic.l2gen.application.gui.dialog.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import fr.univrennes.istic.l2gen.application.core.filter.Filter;

public final class FilterRow {
    private final FilterDialog filterDialog;
    private final JPanel panel;
    private final Filter filter;

    public FilterRow(FilterDialog filterDialog, String description, Filter filter) {
        this.filterDialog = filterDialog;
        this.filter = filter;
        panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(new EmptyBorder(4, 8, 4, 8));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        Icon closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
        JButton removeButton = new JButton(closeIcon);
        removeButton.addActionListener(event -> this.filterDialog.removeConditionRow(this));

        panel.add(new JLabel(description), BorderLayout.CENTER);
        panel.add(removeButton, BorderLayout.EAST);
    }

    public JPanel getPanel() {
        return panel;
    }

    public Filter getFilter() {
        return filter;
    }
}