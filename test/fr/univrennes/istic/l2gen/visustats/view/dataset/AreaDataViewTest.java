package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public final class AreaDataViewTest extends AbstractDataSetViewTest<AreaDataSetView> {

    @Override
    public AreaDataSetView create() {
        return new AreaDataSetView(new Point(500, 500), 50, 200);
    }
}
