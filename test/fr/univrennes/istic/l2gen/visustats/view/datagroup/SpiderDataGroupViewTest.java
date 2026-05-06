package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;

public class SpiderDataGroupViewTest extends AbstractDataGroupViewTest<SpiderDataGroupView> {

    @Override
    public SpiderDataGroupView create() {
        return new SpiderDataGroupView(createDataGroup(3, 10, 150), new Point(500, 500), 20, 200, true);
    }
}
