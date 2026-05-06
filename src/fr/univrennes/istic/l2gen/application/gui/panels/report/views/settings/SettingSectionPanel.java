package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class SettingSectionPanel extends JPanel {

    private final JPanel bodyPanel;
    private final JButton headerButton;
    private final Icon expandedIcon;
    private final Icon collapsedIcon;

    public SettingSectionPanel(String title) {
        setLayout(new BorderLayout());
        setOpaque(false);

        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        expandedIcon = UIManager.getIcon("Tree.expandedIcon");
        collapsedIcon = UIManager.getIcon("Tree.collapsedIcon");

        headerButton = new JButton(title, expandedIcon);
        headerButton.setHorizontalAlignment(SwingConstants.LEFT);
        headerButton.setFocusPainted(true);
        headerButton.setBorderPainted(true);
        headerButton.setContentAreaFilled(true);

        headerButton.addActionListener(e -> setExpanded(!bodyPanel.isVisible()));

        add(headerButton, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
    }

    public void addRow(Component row) {
        bodyPanel.add(row);
    }

    public void clearRows() {
        bodyPanel.removeAll();
    }

    public void setExpanded(boolean expanded) {
        bodyPanel.setVisible(expanded);
        headerButton.setIcon(expanded ? expandedIcon : collapsedIcon);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
