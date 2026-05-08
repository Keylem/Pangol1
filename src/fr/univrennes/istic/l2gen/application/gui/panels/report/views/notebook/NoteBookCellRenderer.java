package fr.univrennes.istic.l2gen.application.gui.panels.report.views.notebook;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import fr.univrennes.istic.l2gen.application.core.config.Lang;
import fr.univrennes.istic.l2gen.application.core.config.Log;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookChart;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookImage;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookText;
import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;

final class NoteBookCellRenderer extends JPanel implements ListCellRenderer<NoteBookValue> {

    private static final String CARD_IMAGE = "image";
    private static final String CARD_TEXT = "text";
    private static final String CARD_CHART = "chart";
    private static final String CARD_UNKNOWN = "unknown";

    private static final int MAX_IMAGE_WIDTH = 300;
    private static final Pattern HEX8_STYLE_PATTERN = Pattern.compile(
            "(?i)(fill|stroke|stop-color|flood-color)\\s*:\\s*#([0-9a-fA-F]{6})([0-9a-fA-F]{2})");
    private static final Pattern HEX8_ATTRIBUTE_PATTERN = Pattern.compile(
            "(?i)(fill|stroke|stop-color|flood-color)\\s*=\\s*\"#([0-9a-fA-F]{6})([0-9a-fA-F]{2})\"");
    private static final ImageIcon BROKEN_ICON = new ImageIcon();

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final JLabel imageLabel = new JLabel();
    private final JLabel imageNameLabel = new JLabel();
    private final JTextArea textArea = new JTextArea();
    private final JLabel chartLabel = new JLabel();

    private final JLabel unknownLabel = new JLabel(Lang.get("report.unknown"));

    private final JPanel imagePanel = new JPanel(new BorderLayout(6, 6));
    private final JPanel textPanel = new JPanel(new BorderLayout());
    private final JPanel chartPanel = new JPanel(new BorderLayout());
    private final JPanel unknownPanel = new JPanel(new BorderLayout());

    private final Map<NoteBookImage, ImageIcon> imageCache = new HashMap<>();
    private final Set<NoteBookImage> loadingImages = new HashSet<>();

    private final Map<NoteBookChart, ImageIcon> svgCache = new HashMap<>();
    private final Set<NoteBookChart> loadingCharts = new HashSet<>();

    private final NoteBook noteBook;

    public NoteBookCellRenderer(NoteBook nb) {
        noteBook = nb;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        setOpaque(true);

        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(imageNameLabel, BorderLayout.SOUTH);
        imagePanel.setOpaque(false);

        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setBorder(BorderFactory.createEmptyBorder());
        textArea.setMargin(new Insets(2, 2, 2, 2));

        textPanel.add(textArea, BorderLayout.CENTER);
        textPanel.setOpaque(false);

        chartPanel.add(chartLabel, BorderLayout.CENTER);
        chartPanel.setOpaque(false);

        unknownPanel.add(unknownLabel, BorderLayout.CENTER);
        unknownPanel.setOpaque(false);

        cardPanel.add(imagePanel, CARD_IMAGE);
        cardPanel.add(textPanel, CARD_TEXT);
        cardPanel.add(chartPanel, CARD_CHART);
        cardPanel.add(unknownPanel, CARD_UNKNOWN);
        cardPanel.setOpaque(false);

        add(cardPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends NoteBookValue> list,
            NoteBookValue value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        applySelectionColors(list, isSelected);
        applyBlockBorder(list, index);

        if (value instanceof NoteBookText text) {
            textArea.setText(text.getText() == null ? "" : text.getText());
            showCard(CARD_TEXT, textPanel);

        } else if (value instanceof NoteBookImage image) {
            renderImage(list, image, index);
            showCard(CARD_IMAGE, imagePanel);

        } else if (value instanceof NoteBookChart chart) {
            renderChart(list, chart, index);
            showCard(CARD_CHART, chartPanel);

        } else {
            showCard(CARD_UNKNOWN, unknownPanel);
        }

        return this;
    }

    private void showCard(String cardName, JComponent panel) {
        cardLayout.show(cardPanel, cardName);
        cardPanel.setPreferredSize(panel.getPreferredSize());
    }

    private void renderImage(JList<? extends NoteBookValue> list, NoteBookImage image, int index) {
        if (image.getPath() == null) {
            imageLabel.setIcon(null);
            imageLabel.setText(Lang.get("report.setting.image.no_found"));
            imageNameLabel.setText("");
            return;
        }

        ImageIcon cachedIcon = imageCache.get(image);

        if (cachedIcon != null) {
            if (cachedIcon == BROKEN_ICON) {
                imageLabel.setIcon(null);
                imageLabel.setText(Lang.get("report.setting.image.no_found"));
            } else {
                imageLabel.setIcon(cachedIcon);
                imageLabel.setText(null);
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText(Lang.get("report.setting.image.loading"));

            if (!loadingImages.contains(image)) {
                loadingImages.add(image);

                executor.submit(() -> {
                    ImageIcon loadedIcon = null;

                    try {
                        Image original = image.getImage();
                        BufferedImage resized = resizeImage(original, MAX_IMAGE_WIDTH);
                        loadedIcon = new ImageIcon(resized);
                    } catch (Exception exception) {
                        Log.debug("Failed to load image for notebook", exception);
                    }

                    final ImageIcon finalIcon = loadedIcon != null ? loadedIcon : BROKEN_ICON;

                    SwingUtilities.invokeLater(() -> {
                        loadingImages.remove(image);
                        imageCache.put(image, finalIcon);

                        if (list.getModel() instanceof DefaultListModel<? extends NoteBookValue> defaultModel
                                && index < defaultModel.getSize()
                                && defaultModel.getElementAt(index) == image) {
                            @SuppressWarnings("unchecked")
                            DefaultListModel<NoteBookValue> model = (DefaultListModel<NoteBookValue>) list
                                    .getModel();

                            model.set(index, image);
                            list.revalidate();
                            list.repaint();
                        }
                    });
                });
            }
        }

        imageNameLabel.setText(image.getPath().getName());
    }

    private void renderChart(JList<? extends NoteBookValue> list, NoteBookChart chart, int index) {
        ImageIcon cachedIcon = svgCache.get(chart);

        if (cachedIcon != null) {
            if (cachedIcon == BROKEN_ICON) {
                chartLabel.setIcon(null);
                chartLabel.setText(Lang.get("report.setting.chart.loading"));
            } else {
                chartLabel.setIcon(cachedIcon);
                chartLabel.setText(null);
            }
        } else {
            chartLabel.setIcon(null);
            chartLabel.setText(Lang.get("report.setting.chart.loading"));

            if (!loadingCharts.contains(chart)) {
                int renderWidth = noteBook.getWidth() - 16 * 2;
                if (renderWidth <= 0) {
                    return;
                }

                loadingCharts.add(chart);
                executor.submit(() -> {
                    ImageIcon loadedIcon = null;
                    String svgContent = chart.getSVG();

                    try {
                        if (svgContent != null) {
                            loadedIcon = rasterizeSvg(svgContent, renderWidth);
                        }
                    } catch (Exception e) {
                        Log.debug("Failed to render SVG chart for notebook", e);
                    }

                    final ImageIcon finalIcon = loadedIcon != null ? loadedIcon : BROKEN_ICON;

                    SwingUtilities.invokeLater(() -> {
                        loadingCharts.remove(chart);
                        svgCache.put(chart, finalIcon);

                        if (list.getModel() instanceof DefaultListModel<? extends NoteBookValue> defaultModel
                                && index < defaultModel.getSize()
                                && defaultModel.getElementAt(index) == chart) {
                            @SuppressWarnings("unchecked")
                            DefaultListModel<NoteBookValue> model = (DefaultListModel<NoteBookValue>) list
                                    .getModel();

                            model.set(index, chart);
                            list.revalidate();
                            list.repaint();
                        }
                    });
                });
            }
        }
    }

    private ImageIcon rasterizeSvg(String svgContent, int maxWidth) throws Exception {
        svgContent = normalizeSvgColors(svgContent);
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        transcoder.addTranscodingHint(ImageTranscoder.KEY_MAX_WIDTH, (float) maxWidth);
        transcoder.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);

        byte[] svgBytes = svgContent.getBytes(StandardCharsets.UTF_8);
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgBytes));

        transcoder.transcode(input, null);

        BufferedImage rasterized = transcoder.getResult();
        if (rasterized == null) {
            throw new IllegalStateException("SVG transcoding produced no image");
        }

        BufferedImage result = new BufferedImage(
                rasterized.getWidth(),
                rasterized.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = result.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.setComposite(AlphaComposite.Src);
        g.drawImage(rasterized, 0, 0, null);
        g.dispose();

        return new ImageIcon(result);
    }

    private String normalizeSvgColors(String svgContent) {
        if (svgContent == null || svgContent.indexOf('#') < 0) {
            return svgContent;
        }

        String updated = replaceHex8InStyle(svgContent);
        return replaceHex8InAttributes(updated);
    }

    private String replaceHex8InStyle(String svgContent) {
        Matcher matcher = HEX8_STYLE_PATTERN.matcher(svgContent);
        StringBuffer buffer = new StringBuffer(svgContent.length());

        while (matcher.find()) {
            String property = matcher.group(1);
            String rgb = matcher.group(2);
            String alpha = formatAlpha(matcher.group(3));
            String opacityProperty = toOpacityProperty(property);
            String replacement = property + ":#" + rgb + ";" + opacityProperty + ":" + alpha;
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String replaceHex8InAttributes(String svgContent) {
        Matcher matcher = HEX8_ATTRIBUTE_PATTERN.matcher(svgContent);
        StringBuffer buffer = new StringBuffer(svgContent.length());

        while (matcher.find()) {
            String property = matcher.group(1);
            String rgb = matcher.group(2);
            String alpha = formatAlpha(matcher.group(3));
            String opacityProperty = toOpacityProperty(property);
            boolean hasOpacity = hasOpacityAttribute(svgContent, matcher.start(), matcher.end(), opacityProperty);
            String replacement;

            if (hasOpacity) {
                replacement = property + "=\"#" + rgb + "\"";
            } else {
                replacement = property + "=\"#" + rgb + "\" " + opacityProperty + "=\"" + alpha + "\"";
            }

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private boolean hasOpacityAttribute(String svgContent, int matchStart, int matchEnd, String opacityProperty) {
        int tagStart = svgContent.lastIndexOf('<', matchStart);
        int tagEnd = svgContent.indexOf('>', matchEnd);

        if (tagStart < 0 || tagEnd < 0 || tagEnd <= tagStart) {
            return false;
        }

        String tagSlice = svgContent.substring(tagStart, tagEnd).toLowerCase(Locale.ROOT);
        return tagSlice.contains(opacityProperty.toLowerCase(Locale.ROOT) + "=");
    }

    private String toOpacityProperty(String colorProperty) {
        String normalized = colorProperty.toLowerCase(Locale.ROOT);

        if ("stop-color".equals(normalized)) {
            return "stop-opacity";
        }

        if ("flood-color".equals(normalized)) {
            return "flood-opacity";
        }

        return normalized + "-opacity";
    }

    private String formatAlpha(String alphaHex) {
        int alphaValue = Integer.parseInt(alphaHex, 16);
        double alpha = alphaValue / 255.0;
        return String.format(Locale.ROOT, "%.3f", alpha);
    }

    private BufferedImage resizeImage(Image original, int maxWidth) {
        int width = original.getWidth(null);
        int height = original.getHeight(null);

        if (width <= 0 || height <= 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        if (width <= maxWidth) {
            return toBufferedImage(original);
        }

        double heightToWidthRatio = (double) height / width;
        int newWidth = maxWidth;
        int newHeight = (int) (maxWidth * heightToWidthRatio);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resized.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        graphics.drawImage(original, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return resized;
    }

    private BufferedImage toBufferedImage(Image img) {
        BufferedImage buffered = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = buffered.createGraphics();
        graphics.drawImage(img, 0, 0, null);
        graphics.dispose();
        return buffered;
    }

    private void applySelectionColors(JList<?> list, boolean isSelected) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        for (Component component : cardPanel.getComponents()) {
            setComponentColors(component, getBackground(), getForeground());
        }

        textArea.setBackground(getBackground());
        textArea.setForeground(getForeground());
        textArea.setOpaque(true);
    }

    private void applyBlockBorder(JList<?> list, int index) {
        int lastIndex = list.getModel().getSize() - 1;

        if (index >= 0 && index <= lastIndex) {
            Color separatorColor = UIManager.getColor("Separator.foreground");
            if (separatorColor == null) {
                separatorColor = list.getForeground();
            }

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, separatorColor),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        } else {
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }
    }

    private void setComponentColors(Component component, Color backgroundColor, Color foregroundColor) {
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);

        if (component instanceof JComponent jComponent) {
            jComponent.setOpaque(true);
        }

        if (component instanceof JPanel panel) {
            for (Component child : panel.getComponents()) {
                setComponentColors(child, backgroundColor, foregroundColor);
            }
        }
    }
}