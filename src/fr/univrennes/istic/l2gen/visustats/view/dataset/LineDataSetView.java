package fr.univrennes.istic.l2gen.visustats.view.dataset;

import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Circle;
import fr.univrennes.istic.l2gen.geometry.base.PolyLine;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class LineDataSetView extends AbstractDataSetView {

    @SVGField("data-spacing")
    private double spacing;

    @SVGField("data-max-height")
    private double maxHeight;

    @SVGField("data-point-radius")
    private double pointRadius;

    private int dataSize;

    public LineDataSetView() {
        super(new Point(0, 0));
        this.spacing = 50;
        this.maxHeight = 200;
        this.pointRadius = 4;
        this.dataSize = 0;
    }

    public LineDataSetView(Point center, double spacing, double maxHeight) {
        this(center, spacing, maxHeight, 4);
    }

    public LineDataSetView(Point center, double spacing, double maxHeight, double pointRadius) {
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

        List<Point> points = new ArrayList<>(this.dataSize);
        for (int i = 0; i < this.dataSize; i++) {
            double val = data.getValue(i);
            double x = baseX + i * spacing;
            double y = baseY - (val / maxValue) * maxHeight;
            points.add(new Point(x, y));
        }

        PolyLine line = new PolyLine();
        for (Point point : points) {
            line.addVertex(point);
        }
        line.getStyle()
                .strokeColor(Color.BLACK)
                .strokeWidth(2)
                .fillColor(Color.TRANSPARENT);
        this.elements.add(line);

        for (int i = 0; i < points.size(); i++) {
            Circle marker = new Circle(points.get(i), this.pointRadius);
            marker.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1);
            this.elements.add(marker);
        }
    }

    private double getPlotWidth() {
        return Math.max(0, this.dataSize - 1) * this.spacing;
    }

    @Override
    public IShape copy() {
        return new LineDataSetView((Point) this.center.copy(), this.spacing, this.maxHeight, this.pointRadius);
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));
        sb.append("LineView");
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
