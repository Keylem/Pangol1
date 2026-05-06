package fr.univrennes.istic.l2gen.application.gui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Taskbar;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.gui.panels.report.ReportPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

public final class MainView extends JFrame {

    private final SplashScreen splash;

    private TablePanel tablePanel;
    private ReportPanel reportPanel;

    private final TopBar topBar;
    private final BottomBar bottomBar;

    private JSplitPane mainSplit;

    public MainView(SplashScreen splash) {
        this.splash = splash;
        this.topBar = new TopBar();
        this.bottomBar = new BottomBar();

        this.build();
    }

    private void build() {
        splash.setVisible(true);

        setVisible(false);
        setTitle(Lang.get("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1420, 800);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
        setJMenuBar(topBar);

        URL logo = getClass().getResource("/icons/logo.png");
        if (logo != null) {
            setIconImage(new ImageIcon(logo).getImage());

            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    taskbar.setIconImage(new ImageIcon(logo).getImage());
                }
            }
        }

        this.tablePanel = new TablePanel();
        this.reportPanel = new ReportPanel();
        this.tablePanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));
        this.reportPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));

        add(bottomBar, BorderLayout.SOUTH);

        this.mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, reportPanel, tablePanel);
        add(mainSplit, BorderLayout.CENTER);

    }

    public TablePanel getTablePanel() {
        return tablePanel;
    }

    public ReportPanel getReportPanel() {
        return reportPanel;
    }

    public TopBar getTopBar() {
        return topBar;
    }

    public BottomBar getBottomBar() {
        return bottomBar;
    }

    public SplashScreen getSplash() {
        return splash;
    }

    public void resetSplit() {
        SwingUtilities.invokeLater(() -> this.mainSplit.setDividerLocation(0.5));
    }

    public void toogleSplitOrientation() {
        SwingUtilities.invokeLater(() -> {
            int orientation = this.mainSplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT
                    ? JSplitPane.VERTICAL_SPLIT
                    : JSplitPane.HORIZONTAL_SPLIT;
            this.mainSplit.setOrientation(orientation);
            this.mainSplit.setDividerLocation(0.5);
        });
    }

    public void setSplitOrientation(int orientation) {
        SwingUtilities.invokeLater(() -> {
            this.mainSplit.setOrientation(orientation);
            this.mainSplit.setDividerLocation(0.5);
        });
    }

    public void ready() {
        SwingUtilities.invokeLater(() -> {
            this.splash.close();
            setVisible(true);
            this.resetSplit();
            tablePanel.refresh();
        });
    }
}