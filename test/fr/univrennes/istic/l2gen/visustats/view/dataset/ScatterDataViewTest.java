package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public class ScatterDataViewTest extends AbstractDataSetViewTest<ScatterDataSetView> {

    @Override
    public ScatterDataSetView create() {
        return new ScatterDataSetView(new Point(500, 500), 100, 200);
    }

}
