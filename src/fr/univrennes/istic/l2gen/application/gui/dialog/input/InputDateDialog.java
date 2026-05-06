package fr.univrennes.istic.l2gen.application.gui.dialog.input;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

public final class InputDateDialog extends AbstractInputDialog<Date> {

    private JSpinner spinner;

    public static Optional<Date> show(String message, String title, String errorMessage) {
        return new InputDateDialog().showDialog(message, title, errorMessage);
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

        spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd HH:mm:ss");
        spinner.setEditor(editor);
        JTextField textField = editor.getTextField();
        textField.setColumns(20);

        constraints.gridy = 1;
        panel.add(spinner, constraints);

        return panel;
    }

    @Override
    protected Date readValue() throws ParseException {
        spinner.commitEdit();
        Object value = spinner.getValue();
        if (value instanceof Date date) {
            return date;
        }
        throw new ParseException("Invalid date", 0);
    }

    @Override
    protected void onInvalidInput() {
        JTextField textField = ((JSpinner.DateEditor) spinner.getEditor()).getTextField();
        textField.selectAll();
    }
}
