package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public final class SettingSeparatorRow extends JPanel {

    public SettingSeparatorRow() {
        this(null);
    }

    public SettingSeparatorRow(String label) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel separator = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                int y = getHeight() / 2;
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(0, y, getWidth(), y);
            }
        };
        separator.setOpaque(false);
        separator.setPreferredSize(new Dimension(0, 12));

        if (label != null) {
            JLabel labelComponent = new JLabel(label);
            labelComponent.setFont(labelComponent.getFont().deriveFont(Font.ITALIC));
            labelComponent.setForeground(Color.GRAY);

            add(labelComponent, BorderLayout.WEST);
        }

        add(separator, BorderLayout.CENTER);
    }
}
