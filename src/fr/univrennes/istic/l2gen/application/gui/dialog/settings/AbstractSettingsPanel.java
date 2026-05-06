package fr.univrennes.istic.l2gen.application.gui.dialog.settings;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public abstract class AbstractSettingsPanel extends JPanel implements SettingsPage {

    private final JPanel sectionsContainer;

    protected AbstractSettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        sectionsContainer = new JPanel();
        sectionsContainer.setLayout(new BoxLayout(sectionsContainer, BoxLayout.Y_AXIS));
        sectionsContainer.setOpaque(false);
        sectionsContainer.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane scrollPane = new JScrollPane(sectionsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        add(scrollPane, BorderLayout.CENTER);
    }

    protected void addSection(Component section) {
        sectionsContainer.add(section);
    }
}
