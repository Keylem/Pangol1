package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import fr.univrennes.istic.l2gen.application.core.config.Ico;
import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookChart;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookImage;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookText;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;
import fr.univrennes.istic.l2gen.application.core.services.notebook.NoteBookService;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart.AxisSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart.ChartSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart.ColorSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart.DataSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.chart.LegendSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.image.ImageSettingsPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.pages.text.TextSettingsPanel;

public class SettingView extends JPanel {

    private final CardLayout cardLayout = new CardLayout();

    private ChartSettingsPanel chartSettingsPanel;
    private DataSettingsPanel dataSettingsPanel;
    private LegendSettingsPanel legendSettingsPanel;
    private AxisSettingsPanel axisSettingsPanel;
    private ColorSettingsPanel colorSettingsPanel;

    private ImageSettingsPanel imageSettingsPanel;
    private TextSettingsPanel textSettingsPanel;

    private int currentEditedIndex = -1;
    private SettingViewType currentView = SettingViewType.BASE;

    public SettingView() {
        build();
    }

    public void editNoteBookValue(NoteBookValue value, int index) {
        currentEditedIndex = index;
        refresh();

        if (value instanceof NoteBookText) {
            textSettingsPanel.loadNoteBook(value);

            showCard(SettingViewType.TEXT);
        } else if (value instanceof NoteBookImage) {
            imageSettingsPanel.loadNoteBook(value);

            showCard(SettingViewType.IMAGE);
        } else if (value instanceof NoteBookChart) {
            chartSettingsPanel.loadNoteBook(value);

            showCard(SettingViewType.CHART);
        }

    }

    private void build() {
        setLayout(cardLayout);

        add(buildBasePanel(), SettingViewType.BASE.name());
        add(buildChartSettingsPanel(), SettingViewType.CHART.name());
        add(buildImageSettingsPanel(), SettingViewType.IMAGE.name());
        add(buildTextSettingsPanel(), SettingViewType.TEXT.name());

        showCard(currentView);
    }

    private JPanel buildBasePanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        int fixWidth = 300;

        content.add(Box.createVerticalGlue());

        JButton addChartButton = buildActionButton(Lang.get("report.add.chart"), "icons/add_chart.svg", fixWidth);
        addChartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addChartButton.addActionListener(e -> showCard(SettingViewType.CHART));
        content.add(addChartButton);

        content.add(Box.createVerticalStrut(6));

        JButton addImageButton = buildActionButton(Lang.get("report.add.image"), "icons/add_image.svg", fixWidth);
        addImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addImageButton.addActionListener(e -> showCard(SettingViewType.IMAGE));
        content.add(addImageButton);

        content.add(Box.createVerticalStrut(6));

        JButton addTextButton = buildActionButton(Lang.get("report.add.text"), "icons/add_text.svg", fixWidth);
        addTextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addTextButton.addActionListener(e -> showCard(SettingViewType.TEXT));
        content.add(addTextButton);

        content.add(Box.createVerticalGlue());

        return content;
    }

    private JPanel buildChartSettingsPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(Box.createVerticalStrut(5));
        content.add(buildBackAndNextButtons(SettingViewType.CHART));
        content.add(Box.createVerticalStrut(10));

        dataSettingsPanel = new DataSettingsPanel();
        axisSettingsPanel = new AxisSettingsPanel();
        legendSettingsPanel = new LegendSettingsPanel();
        colorSettingsPanel = new ColorSettingsPanel();

        chartSettingsPanel = new ChartSettingsPanel(
                axisSettingsPanel,
                legendSettingsPanel,
                colorSettingsPanel,
                dataSettingsPanel);

        content.add(chartSettingsPanel);
        content.add(axisSettingsPanel);
        content.add(legendSettingsPanel);
        content.add(colorSettingsPanel);
        content.add(dataSettingsPanel);

        content.add(Box.createVerticalGlue());

        return wrapInScroll(content);
    }

    private JPanel buildImageSettingsPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(Box.createVerticalStrut(5));
        content.add(buildBackAndNextButtons(SettingViewType.IMAGE));
        content.add(Box.createVerticalStrut(10));

        imageSettingsPanel = new ImageSettingsPanel();
        content.add(imageSettingsPanel);

        content.add(Box.createVerticalGlue());

        return wrapInScroll(content);
    }

    private JPanel buildTextSettingsPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(Box.createVerticalStrut(5));
        content.add(buildBackAndNextButtons(SettingViewType.TEXT));
        content.add(Box.createVerticalStrut(10));

        textSettingsPanel = new TextSettingsPanel();
        content.add(textSettingsPanel);

        content.add(Box.createVerticalGlue());

        return wrapInScroll(content);
    }

    private JButton buildActionButton(String label, String iconPath, int width) {
        JButton button = new JButton(label, Ico.get(iconPath));
        button.setMaximumSize(new Dimension(width,
                button.getPreferredSize().height * 2));
        button.setAlignmentX(CENTER_ALIGNMENT);
        return button;
    }

    private JPanel buildBackAndNextButtons(SettingViewType targetCard) {
        JButton backButton = new JButton(Lang.get(currentEditedIndex != -1 ? "report.cancel" : "report.back"));
        backButton.addActionListener(e -> {
            if (currentEditedIndex != -1) {
                currentEditedIndex = -1;
                refresh();
            }
        });
        backButton.setMaximumSize(new Dimension(backButton.getPreferredSize().width, 24));
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> showCard(SettingViewType.BASE));

        JButton nextButton = new JButton(Lang.get(currentEditedIndex != -1 ? "report.next.edit" : "report.next.add"));
        nextButton.setIcon(Ico.get(currentEditedIndex != -1 ? "icons/edit.svg" : "icons/add.svg"));
        nextButton.setMaximumSize(new Dimension(nextButton.getPreferredSize().width, 24));
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        nextButton.addActionListener(e -> {
            handleNext(targetCard);
            showCard(SettingViewType.BASE);
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(backButton);
        panel.add(Box.createHorizontalGlue());
        panel.add(nextButton);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        panel.setAlignmentX(CENTER_ALIGNMENT);
        return panel;
    }

    private void handleNext(SettingViewType type) {
        NoteBookValue value = null;
        switch (type) {
            case CHART:
                if (dataSettingsPanel != null && chartSettingsPanel != null) {
                    value = chartSettingsPanel.createNoteBook();
                }
                break;
            case IMAGE:
                if (imageSettingsPanel != null) {
                    value = imageSettingsPanel.createNoteBook();
                }
                break;
            case TEXT:
                if (textSettingsPanel != null) {
                    value = textSettingsPanel.createNoteBook();
                }
                break;
            default:
                break;
        }

        if (value != null) {
            if (currentEditedIndex != -1) {
                NoteBookService.update(currentEditedIndex, value);
                currentEditedIndex = -1;
            } else {
                NoteBookService.add(value);
            }

            GUIController.getInstance().getMainView().getReportPanel().refresh();
        }
    }

    private void showCard(SettingViewType card) {
        currentView = card;
        cardLayout.show(this, card.name());
    }

    private JPanel wrapInScroll(JPanel content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        removeAll();
        build();
        revalidate();
        repaint();
    }

    public DataSettingsPanel getDataSettingsPanel() {
        return dataSettingsPanel;
    }

    public LegendSettingsPanel getLegendSettingsPanel() {
        return legendSettingsPanel;
    }

    public AxisSettingsPanel getAxisSettingsPanel() {
        return axisSettingsPanel;
    }

    public ColorSettingsPanel getColorSettingsPanel() {
        return colorSettingsPanel;
    }

    public ChartSettingsPanel getChartSettingsPanel() {
        return chartSettingsPanel;
    }

    public ImageSettingsPanel getImageSettingsPanel() {
        return imageSettingsPanel;
    }

    public TextSettingsPanel getTextSettingsPanel() {
        return textSettingsPanel;
    }

}
