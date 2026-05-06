package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class SettingRowPanel extends JPanel {

    public SettingRowPanel(String labelText, JComponent control) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        labelPanel.add(new JLabel(labelText));

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setOpaque(false);
        controlPanel.add(control, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 2, 1, 2);
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        add(labelPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        add(controlPanel, gbc);
    }
}
