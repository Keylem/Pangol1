package fr.univrennes.istic.l2gen.application.gui.dialog.input;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public final class InputIntDialog extends AbstractInputDialog<Integer> {

    private JSpinner spinner;

    public static Optional<Integer> show(String message, String title, String errorMessage) {
        return new InputIntDialog().showDialog(message, title, errorMessage);
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

        spinner = new JSpinner(new SpinnerNumberModel(0, null, null, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0");
        spinner.setEditor(editor);
        JTextField textField = editor.getTextField();
        textField.setColumns(12);

        constraints.gridy = 1;
        panel.add(spinner, constraints);

        return panel;
    }

    @Override
    protected Integer readValue() throws ParseException {
        spinner.commitEdit();
        Object value = spinner.getValue();
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new ParseException("Invalid number", 0);
    }

    @Override
    protected void onInvalidInput() {
        JTextField textField = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        textField.selectAll();
    }
}
