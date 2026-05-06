package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public class LineDataSetViewTest extends AbstractDataSetViewTest<LineDataSetView> {

    @Override
    public LineDataSetView create() {
        return new LineDataSetView(new Point(500, 500), 100, 200);
    }

}
