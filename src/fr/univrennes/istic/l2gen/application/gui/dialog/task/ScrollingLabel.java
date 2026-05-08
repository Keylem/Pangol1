package fr.univrennes.istic.l2gen.application.gui.dialog.task;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.Timer;

final class ScrollingLabel extends JPanel {

    private static final int SCROLL_SPEED_PX = 1;
    private static final int SCROLL_INTERVAL_MS = 30;
    private static final int PAUSE_BEFORE_SCROLL_MS = 1500;
    private static final int GAP_BETWEEN_REPEATS_PX = 40;

    private final String text;
    private final int maxWidth;
    private int scrollOffset = 0;
    private boolean scrollingActive = false;
    private Timer scrollTimer;
    private Font labelFont;

    public ScrollingLabel(String text, int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
        setOpaque(false);
        setPreferredSize(new Dimension(maxWidth, 18));
        setMaximumSize(new Dimension(maxWidth, 18));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        labelFont = getFont().deriveFont(Font.PLAIN, 12f);
        FontMetrics fontMetrics = getFontMetrics(labelFont);
        int textWidth = fontMetrics.stringWidth(text);

        if (textWidth > maxWidth) {
            scrollingActive = true;
            Timer pauseTimer = new Timer(PAUSE_BEFORE_SCROLL_MS, event -> startScrolling());
            pauseTimer.setRepeats(false);
            pauseTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (scrollTimer != null) {
            scrollTimer.stop();
            scrollTimer = null;
        }
    }

    private void startScrolling() {
        scrollTimer = new Timer(SCROLL_INTERVAL_MS, event -> {
            FontMetrics fontMetrics = getFontMetrics(labelFont);
            int textWidth = fontMetrics.stringWidth(text);
            int totalScrollDistance = textWidth + GAP_BETWEEN_REPEATS_PX;

            scrollOffset += SCROLL_SPEED_PX;
            if (scrollOffset >= totalScrollDistance) {
                scrollOffset = 0;
            }
            repaint();
        });
        scrollTimer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (labelFont == null) {
            labelFont = getFont().deriveFont(Font.PLAIN, 12f);
        }

        Graphics2D graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setFont(labelFont);
        graphics2d.setColor(getForeground());

        FontMetrics fontMetrics = graphics2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);
        int baseline = (getHeight() + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;

        if (!scrollingActive) {
            graphics2d.setClip(0, 0, maxWidth, getHeight());
            graphics2d.drawString(text, 0, baseline);
        } else {
            graphics2d.setClip(0, 0, maxWidth, getHeight());
            int firstDrawX = -scrollOffset;
            graphics2d.drawString(text, firstDrawX, baseline);
            int secondDrawX = firstDrawX + textWidth + GAP_BETWEEN_REPEATS_PX;
            graphics2d.drawString(text, secondDrawX, baseline);
        }

        graphics2d.dispose();
    }
}