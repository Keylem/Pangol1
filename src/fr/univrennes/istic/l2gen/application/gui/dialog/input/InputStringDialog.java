package fr.univrennes.istic.l2gen.application.gui.dialog.input;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class InputStringDialog extends AbstractInputDialog<String> {

    private JTextField inputField;

    public static Optional<String> show(String message, String title, String errorMessage) {
        return new InputStringDialog().showDialog(message, title, errorMessage);
    }

    @Override
    protected JComponent buildPanel(String message) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 0, 4, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel(message), constraints);

        inputField = new JTextField();
        inputField.setColumns(28);
        constraints.gridy = 1;
        panel.add(inputField, constraints);

        return panel;
    }

    @Override
    protected String readValue() {
        String trimmed = inputField.getText().trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Empty input");
        }
        return trimmed;
    }

    @Override
    protected void onInvalidInput() {
        inputField.selectAll();
    }
}
