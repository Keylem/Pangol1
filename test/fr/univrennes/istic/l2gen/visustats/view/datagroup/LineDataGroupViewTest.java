package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;

public class LineDataGroupViewTest extends AbstractDataGroupViewTest<LineDataGroupView> {

    @Override
    public LineDataGroupView create() {
        return new LineDataGroupView(createDataGroup(3, 10, 150), new Point(500, 500), 20, 20, 100, true);
    }

}
