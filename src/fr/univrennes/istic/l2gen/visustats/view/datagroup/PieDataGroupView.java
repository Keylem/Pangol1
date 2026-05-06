package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.dataset.IDataSetView;
import fr.univrennes.istic.l2gen.visustats.view.dataset.PieDataSetView;

@SVGTag("g")
public class PieDataGroupView extends AbstractDataGroupView {

    @SVGField("data-radius")
    protected double radius;

    public PieDataGroupView(DataGroup data, Point center, double spacing, double radius, boolean horizontalLegend) {
        super(data, center, spacing, horizontalLegend);
        this.radius = radius;
        this.update();
    }

    @Override
    protected double getTotalElementsHeight() {
        return this.radius * 2;
    }

    @Override
    protected double getElementWidth() {
        return this.radius * 2;
    }

    @Override
    protected IDataSetView createElement(Point position) {
        return new PieDataSetView(position, this.radius);
    }

    @Override
    public boolean isAxisEnabled() {
        return false;
    }

    @Override
    protected IShape getAxisElement() {
        return null;
    }
}
