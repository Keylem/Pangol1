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

public final class InputDoubleDialog extends AbstractInputDialog<Double> {

    private JSpinner spinner;
    private Double min = null;
    private Double max = null;

    public static Optional<Double> show(String message, String title, String errorMessage) {
        return new InputDoubleDialog().showDialog(message, title, errorMessage);
    }

    public static Optional<Double> show(String message, String title, String errorMessage, double min, double max) {
        InputDoubleDialog dialog = new InputDoubleDialog();
        dialog.setRange(min, max);
        return dialog.showDialog(message, title, errorMessage);
    }

    public void setRange(double min, double max) {
        this.min = min;
        this.max = max;
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

        double value = this.min != null ? this.min.doubleValue() : 0.0;
        double minValue = this.min != null ? this.min.doubleValue() : 0.0;
        double maxValue = this.max != null ? this.max.doubleValue() : Double.POSITIVE_INFINITY;
        spinner = new JSpinner(new SpinnerNumberModel(value, minValue, maxValue, 0.1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0.###");
        spinner.setEditor(editor);

        JTextField textField = editor.getTextField();
        textField.setColumns(12);

        constraints.gridy = 1;
        panel.add(spinner, constraints);

        return panel;
    }

    @Override
    protected Double readValue() throws ParseException {
        spinner.commitEdit();
        Object value = spinner.getValue();
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        throw new ParseException("Invalid number", 0);
    }

    @Override
    protected void onInvalidInput() {
        JTextField textField = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        textField.selectAll();
    }
}
