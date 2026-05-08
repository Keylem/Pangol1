package fr.univrennes.istic.l2gen.application.gui.dialog.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.dialog.DialogBase;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages.AdvancedSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages.AppearanceSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages.GeneralSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages.ShortcutsSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.dialog.settings.pages.TableSettingsPanel;

public final class SettingsDialog extends JDialog {

    private static final String TAB_GENERAL = "general";
    private static final String TAB_TABLE = "table";
    private static final String TAB_APPEARANCE = "appearance";
    private static final String TAB_SHORTCUTS = "shortcuts";
    private static final String TAB_ADVANCED = "advanced";

    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentCardLayout);
    private String activeTabKey = TAB_GENERAL;

    private JPanel tabBarPanel;

    public SettingsDialog(Frame parent) {
        super(parent, Lang.get("settings.title"), true);

        setSize(DialogBase.WIDTH, DialogBase.HEIGHT);
        setMinimumSize(new Dimension(DialogBase.WIDTH, DialogBase.HEIGHT));
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UIManager.getColor("Panel.background"));

        tabBarPanel = buildTabBar();
        rootPanel.add(tabBarPanel, BorderLayout.NORTH);

        contentPanel.setOpaque(false);
        contentPanel.add(new GeneralSettingsPanel(), TAB_GENERAL);
        contentPanel.add(new TableSettingsPanel(), TAB_TABLE);
        contentPanel.add(new AppearanceSettingsPanel(), TAB_APPEARANCE);
        contentPanel.add(new ShortcutsSettingsPanel(), TAB_SHORTCUTS);
        contentPanel.add(new AdvancedSettingsPanel(), TAB_ADVANCED);
        rootPanel.add(contentPanel, BorderLayout.CENTER);

        rootPanel.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(rootPanel);
        contentCardLayout.show(contentPanel, activeTabKey);
    }

    private JPanel buildTabBar() {
        JPanel tabBar = new JPanel(new BorderLayout());
        tabBar.setBackground(UIManager.getColor("Panel.background"));
        tabBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel tabsRow = new JPanel();
        tabsRow.setLayout(new BoxLayout(tabsRow, BoxLayout.X_AXIS));
        tabsRow.setOpaque(false);
        tabsRow.setBorder(new EmptyBorder(0, 4, 0, 4));

        tabsRow.add(buildTabButton(Lang.get("settings.tab.general"), TAB_GENERAL));
        tabsRow.add(buildTabButton(Lang.get("settings.tab.table"), TAB_TABLE));
        tabsRow.add(buildTabButton(Lang.get("settings.tab.appearance"), TAB_APPEARANCE));
        tabsRow.add(buildTabButton(Lang.get("settings.tab.shortcuts"), TAB_SHORTCUTS));
        tabsRow.add(buildTabButton(Lang.get("settings.tab.advanced"), TAB_ADVANCED));

        tabBar.add(tabsRow, BorderLayout.CENTER);
        tabBar.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

        return tabBar;
    }

    private JButton buildTabButton(String label, String tabKey) {
        JButton tabButton = new JButton(label) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2d = (Graphics2D) graphics.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean isActive = tabKey.equals(activeTabKey);
                if (isActive) {
                    g2d.setColor(UIManager.getColor("Panel.background"));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(UIManager.getColor("TabbedPane.underlineColor") != null
                            ? UIManager.getColor("TabbedPane.underlineColor")
                            : new Color(70, 130, 180));
                    g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                }

                g2d.dispose();
                super.paintComponent(graphics);
            }
        };

        tabButton.setFont(tabButton.getFont().deriveFont(Font.PLAIN, 12f));
        tabButton.setBorderPainted(false);
        tabButton.setFocusPainted(false);
        tabButton.setContentAreaFilled(false);
        tabButton.setOpaque(false);
        tabButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tabButton.setBorder(new EmptyBorder(8, 14, 8, 14));

        tabButton.addActionListener(event -> {
            activeTabKey = tabKey;
            contentCardLayout.show(contentPanel, tabKey);
            tabBarPanel.repaint();
        });

        return tabButton;
    }

    private JPanel buildFooter() {
        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setOpaque(false);
        footerWrapper.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel hintLabel = new JLabel(Lang.get("settings.footer.hint"));
        hintLabel.setFont(hintLabel.getFont().deriveFont(Font.PLAIN, 11f));
        hintLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        footerPanel.add(hintLabel, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.X_AXIS));
        actionsPanel.setOpaque(false);

        JButton cancelButton = new JButton(Lang.get("settings.footer.cancel"));
        cancelButton.setFont(cancelButton.getFont().deriveFont(Font.PLAIN, 12f));
        cancelButton.addActionListener(event -> dispose());

        JButton applyButton = new JButton(Lang.get("settings.footer.apply"));
        applyButton.setFont(applyButton.getFont().deriveFont(Font.PLAIN, 12f));
        applyButton.addActionListener(event -> {
            applyAllSettings();
            dispose();
        });

        actionsPanel.add(cancelButton);
        actionsPanel.add(Box.createHorizontalStrut(8));
        actionsPanel.add(applyButton);

        footerPanel.add(actionsPanel, BorderLayout.EAST);
        footerWrapper.add(footerPanel, BorderLayout.CENTER);

        return footerWrapper;
    }

    private void applyAllSettings() {
        boolean restartRequired = false;
        for (Component component : contentPanel.getComponents()) {
            if (component instanceof SettingsPage settingsPage) {
                if (settingsPage.applySettings()) {
                    if (settingsPage.requiresRestart()) {
                        restartRequired = true;
                    }
                }
            }
        }

        if (restartRequired) {
            GUIController.getInstance().restart();
        }
    }
}
