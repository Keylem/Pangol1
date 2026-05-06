package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public final class SpiderDataViewTest extends AbstractDataSetViewTest<SpiderDataSetView> {

    @Override
    public SpiderDataSetView create() {
        return new SpiderDataSetView(new Point(500, 500), 200);
    }
}
