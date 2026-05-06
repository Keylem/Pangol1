package fr.univrennes.istic.l2gen.application.gui.dialog.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public final class SettingsSectionPanel extends JPanel {

    private final JPanel bodyPanel;

    public SettingsSectionPanel(String title) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));
        titleLabel.setForeground(UIManager.getColor("Label.disabledForeground"));

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(new EmptyBorder(0, 4, 0, 0));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(separator, BorderLayout.CENTER);

        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    public void addRow(Component rowComponent) {
        bodyPanel.add(rowComponent);
    }
}
