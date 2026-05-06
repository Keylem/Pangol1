package fr.univrennes.istic.l2gen.visustats.view.dataset;

import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Circle;
import fr.univrennes.istic.l2gen.geometry.base.Polygon;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class SpiderDataSetView extends AbstractDataSetView {

    @SVGField("data-radius")
    private double radius;

    @SVGField("data-point-radius")
    private double pointRadius;

    @SVGField("data-grid-levels")
    private int gridLevels;

    private int dataSize;

    public SpiderDataSetView() {
        super(new Point(0, 0));
        this.radius = 200;
        this.pointRadius = 4;
        this.gridLevels = 4;
        this.dataSize = 0;
    }

    public SpiderDataSetView(Point center, double radius) {
        this(center, radius, 4, 4);
    }

    public SpiderDataSetView(Point center, double radius, double pointRadius) {
        this(center, radius, pointRadius, 4);
    }

    public SpiderDataSetView(Point center, double radius, double pointRadius, int gridLevels) {
        super(center);
        this.radius = radius;
        this.pointRadius = pointRadius;
        this.gridLevels = Math.max(1, gridLevels);
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

        double angleStep = 360.0 / this.dataSize;
        double startAngle = -90.0;

        addCanvas(angleStep, startAngle);

        List<Point> points = new ArrayList<>(this.dataSize);
        for (int i = 0; i < this.dataSize; i++) {
            double value = data.getValue(i);
            double r = (value / maxValue) * this.radius;
            double angle = Math.toRadians(startAngle + i * angleStep);
            double x = this.center.getX() + r * Math.cos(angle);
            double y = this.center.getY() + r * Math.sin(angle);
            points.add(new Point(x, y));
        }

        Polygon polygon = new Polygon();
        for (Point point : points) {
            polygon.addVertex(point);
        }

        Color stroke = data.getColor(0);
        polygon.getStyle()
                .fillColor(Color.TRANSPARENT)
                .strokeColor(stroke)
                .strokeWidth(2);
        this.elements.add(polygon);

        for (int i = 0; i < points.size(); i++) {
            Circle marker = new Circle(points.get(i), this.pointRadius);
            marker.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1);
            this.elements.add(marker);
        }
    }

    private void addCanvas(double angleStep, double startAngle) {
        for (int level = 1; level <= this.gridLevels; level++) {
            double levelRadius = this.radius * level / this.gridLevels;

            Polygon ring = new Polygon();
            for (int i = 0; i < this.dataSize; i++) {
                double angle = Math.toRadians(startAngle + i * angleStep);
                double x = this.center.getX() + levelRadius * Math.cos(angle);
                double y = this.center.getY() + levelRadius * Math.sin(angle);
                ring.addVertex(new Point(x, y));
            }

            ring.getStyle()
                    .fillColor(Color.TRANSPARENT)
                    .strokeColor(Color.GRAY)
                    .strokeWidth(level == this.gridLevels ? 1.4 : 1);
            this.elements.add(ring);
        }

        for (int i = 0; i < this.dataSize; i++) {
            double angle = Math.toRadians(startAngle + i * angleStep);
            double x = this.center.getX() + this.radius * Math.cos(angle);
            double y = this.center.getY() + this.radius * Math.sin(angle);

            Path axis = new Path();
            axis.draw()
                    .move(this.center.getX(), this.center.getY(), false)
                    .line(x, y, false);
            axis.getStyle()
                    .strokeColor(Color.GRAY)
                    .strokeWidth(1);
            this.elements.add(axis);
        }
    }

    @Override
    public IShape copy() {
        return new SpiderDataSetView((Point) this.center.copy(), this.radius, this.pointRadius, this.gridLevels);
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));
        sb.append("SpiderView");
        return sb.toString();
    }

    @Override
    public double getHeight() {
        return this.radius * 2 + this.pointRadius * 2;
    }

    @Override
    public double getWidth() {
        return this.radius * 2 + this.pointRadius * 2;
    }
}
