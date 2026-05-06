package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;

import java.awt.*;

public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel status;

    public SplashScreen() {
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(new Color(0x39B763));
        setContentPane(background);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/icons/logo.png"));
        Image scaledImage = rawIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setColor(Color.WHITE);
                graphics2D.setStroke(new BasicStroke(3f));
                graphics2D.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                graphics2D.dispose();
                super.paintComponent(graphics);
            }
        };
        logoLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        centerPanel.add(logoLabel);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        status = new JLabel("", SwingConstants.CENTER);
        status.setForeground(Color.WHITE);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(Color.WHITE);
        progressBar.setBackground(new Color(255, 255, 255, 80));
        progressBar.setBorderPainted(false);

        bottom.add(status, BorderLayout.NORTH);
        bottom.add(progressBar, BorderLayout.SOUTH);

        background.add(centerPanel, BorderLayout.CENTER);
        background.add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void setStatus(String text) {
        SwingUtilities.invokeLater(() -> status.setText(text));
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
        });
    }
}