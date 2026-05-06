package fr.univrennes.istic.l2gen.application.gui.panels.report.views.notebook;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import fr.univrennes.istic.l2gen.application.core.notebook.NoteBookValue;
import fr.univrennes.istic.l2gen.application.core.services.notebook.NoteBookService;

final class NoteBookDragDrop extends TransferHandler {

    private final DataFlavor valueFlavor = new DataFlavor(NoteBookValue.class, "NoteBookValue");

    private final JList<NoteBookValue> list;
    private final DefaultListModel<NoteBookValue> model;

    private NoteBookValue draggedValue;

    public NoteBookDragDrop(JList<NoteBookValue> list, DefaultListModel<NoteBookValue> model) {
        this.list = list;
        this.model = model;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        draggedValue = list.getSelectedValue();

        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { valueFlavor };
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return valueFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return draggedValue;
            }
        };
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean canImport(TransferSupport info) {
        return info.isDrop() && info.isDataFlavorSupported(valueFlavor);
    }

    @Override
    public boolean importData(TransferSupport info) {
        if (!canImport(info))
            return false;

        NoteBookValue value;
        try {
            value = (NoteBookValue) info.getTransferable().getTransferData(valueFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        int fromIndex = model.indexOf(value);
        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
        int toIndex = dl.getIndex();

        if (fromIndex == -1 || toIndex == -1 || fromIndex == toIndex)
            return false;

        if (toIndex > fromIndex) {
            toIndex--;
        }

        model.remove(fromIndex);
        model.add(toIndex, value);

        NoteBookService.move(fromIndex, toIndex);
        list.setSelectedIndex(toIndex);

        return true;
    }
}