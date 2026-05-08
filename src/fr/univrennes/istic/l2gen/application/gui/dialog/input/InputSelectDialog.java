package fr.univrennes.istic.l2gen.application.gui.dialog.input;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class InputSelectDialog extends AbstractInputDialog<String> {

    private JComboBox<String> comboBox;
    private List<String> options;

    public InputSelectDialog(List<String> options) {
        this.options = options;
    }

    public static Optional<String> show(List<String> options, String message, String title, String errorMessage) {
        return new InputSelectDialog(options).showDialog(message, title, errorMessage);
    }

    @Override
    protected JComponent build(String message) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 0, 4, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel(message), constraints);

        comboBox = new JComboBox<>(options.toArray(new String[0]));
        constraints.gridy = 1;
        panel.add(comboBox, constraints);

        return panel;
    }

    @Override
    protected String readValue() {
        Object selectedItem = comboBox.getSelectedItem();
        if (selectedItem == null) {
            throw new IllegalArgumentException("No selection");
        }
        return (String) selectedItem;
    }

    @Override
    protected void onInvalidInput() {
        comboBox.requestFocus();
    }
}
