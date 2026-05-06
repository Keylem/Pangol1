package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;

public class AreaDataGroupViewTest extends AbstractDataGroupViewTest<AreaDataGroupView> {

    @Override
    public AreaDataGroupView create() {
        return new AreaDataGroupView(createDataGroup(3, 10, 150), new Point(500, 500), 20, 20, 100, 4, true, true);
    }
}
