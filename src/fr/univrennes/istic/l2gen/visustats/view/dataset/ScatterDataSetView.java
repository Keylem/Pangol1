package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Circle;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class ScatterDataSetView extends AbstractDataSetView {

    @SVGField("data-spacing")
    private double spacing;

    @SVGField("data-max-height")
    private double maxHeight;

    @SVGField("data-point-radius")
    private double pointRadius;

    private int dataSize;

    public ScatterDataSetView() {
        super(new Point(0, 0));
        this.spacing = 50;
        this.maxHeight = 200;
        this.pointRadius = 4;
        this.dataSize = 0;
    }

    public ScatterDataSetView(Point center, double spacing, double maxHeight) {
        this(center, spacing, maxHeight, 4);
    }

    public ScatterDataSetView(Point center, double spacing, double maxHeight, double pointRadius) {
        super(center);
        this.spacing = spacing;
        this.maxHeight = maxHeight;
        this.pointRadius = pointRadius;
        this.dataSize = 0;
    }

    @Override
    public void setData(DataSet data) {
        super.setData(data);

        this.dataSize = data.size();
        if (this.dataSize == 0) {
            return;
        }

        double maxValue = data.max();
        if (maxValue <= 0) {
            maxValue = 1;
        }

        double totalWidth = getPlotWidth();
        double baseX = this.center.getX() - totalWidth / 2;
        double baseY = this.center.getY() + maxHeight / 2;

        for (int i = 0; i < this.dataSize; i++) {
            double val = data.getValue(i);
            double x = baseX + i * spacing;
            double y = baseY - (val / maxValue) * maxHeight;

            Circle point = new Circle(new Point(x, y), this.pointRadius);
            point.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1);
            this.elements.add(point);
        }
    }

    private double getPlotWidth() {
        return Math.max(0, this.dataSize - 1) * this.spacing;
    }

    @Override
    public IShape copy() {
        return new ScatterDataSetView((Point) this.center.copy(), this.spacing, this.maxHeight, this.pointRadius);
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));
        sb.append("ScatterView");
        return sb.toString();
    }

    @Override
    public double getHeight() {
        return this.maxHeight + this.pointRadius * 2;
    }

    @Override
    public double getWidth() {
        if (this.dataSize == 0) {
            return 0;
        }
        return getPlotWidth() + this.pointRadius * 2;
    }
}
