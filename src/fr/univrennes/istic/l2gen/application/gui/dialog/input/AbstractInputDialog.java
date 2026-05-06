package fr.univrennes.istic.l2gen.application.gui.dialog.input;

import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import fr.univrennes.istic.l2gen.application.gui.GUIController;

public abstract class AbstractInputDialog<T> {

    protected abstract JComponent buildPanel(String message);

    protected abstract T readValue() throws Exception;

    protected void onInvalidInput() {
    }

    public Optional<T> showDialog(String message, String title, String errorMessage) {
        JComponent panel = buildPanel(message);
        while (true) {
            int result = JOptionPane.showConfirmDialog(GUIController.getInstance().getMainView(), panel, title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            try {
                T value = readValue();
                return Optional.of(value);
            } catch (Exception ignored) {
            }

            JOptionPane.showMessageDialog(GUIController.getInstance().getMainView(), errorMessage, title,
                    JOptionPane.ERROR_MESSAGE);
            onInvalidInput();
        }
    }
}
