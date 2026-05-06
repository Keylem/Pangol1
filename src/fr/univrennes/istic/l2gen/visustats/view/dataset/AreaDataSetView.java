package fr.univrennes.istic.l2gen.visustats.view.dataset;

import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Circle;
import fr.univrennes.istic.l2gen.geometry.base.Polygon;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class AreaDataSetView extends AbstractDataSetView {

    @SVGField("data-spacing")
    private double spacing;

    @SVGField("data-max-height")
    private double maxHeight;

    @SVGField("data-point-radius")
    private double pointRadius;

    @SVGField("data-scale-max")
    private double scaleMax;

    @SVGField("data-fill-alpha")
    private int fillAlpha;

    private int dataSize;

    public AreaDataSetView() {
        super(new Point(0, 0));
        this.spacing = 50;
        this.maxHeight = 200;
        this.pointRadius = 4;
        this.scaleMax = Double.NaN;
        this.fillAlpha = 255;
        this.dataSize = 0;
    }

    public AreaDataSetView(Point center, double spacing, double maxHeight) {
        this(center, spacing, maxHeight, 4);
    }

    public AreaDataSetView(Point center, double spacing, double maxHeight, double pointRadius) {
        super(center);
        this.spacing = spacing;
        this.maxHeight = maxHeight;
        this.pointRadius = pointRadius;
        this.scaleMax = Double.NaN;
        this.fillAlpha = 255;
        this.dataSize = 0;
    }

    public final void setScaleMax(double scaleMax) {
        this.scaleMax = scaleMax;
    }

    public final void setFillAlpha(int alpha) {
        this.fillAlpha = Math.max(0, Math.min(255, alpha));
    }

    @Override
    public void setData(DataSet data) {
        super.setData(data);

        this.dataSize = data.size();
        if (this.dataSize == 0) {
            return;
        }

        double maxValue = Double.isNaN(this.scaleMax) ? data.max() : this.scaleMax;
        if (maxValue <= 0) {
            maxValue = 1;
        }

        double plotWidth = getPlotWidth();
        double baseX = this.center.getX() - plotWidth / 2;
        double baseY = this.center.getY() + maxHeight / 2;

        List<Point> points = new ArrayList<>(this.dataSize);
        for (int i = 0; i < this.dataSize; i++) {
            double val = data.getValue(i);
            double x = baseX + i * spacing;
            double y = baseY - (val / maxValue) * maxHeight;
            points.add(new Point(x, y));
        }

        Polygon area = new Polygon();
        for (Point point : points) {
            area.addVertex(point);
        }
        area.addVertex(baseX + plotWidth, baseY);
        area.addVertex(baseX, baseY);

        Color fill = withAlpha(data.getColor(0), this.fillAlpha);
        area.getStyle()
                .fillColor(fill)
                .strokeColor(Color.BLACK)
                .strokeWidth(2);
        this.elements.add(area);

        for (int i = 0; i < points.size(); i++) {
            Circle marker = new Circle(points.get(i), this.pointRadius);
            marker.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1);
            this.elements.add(marker);
        }
    }

    private Color withAlpha(Color color, int alpha) {
        String hex = color.toString();
        if (hex.startsWith("#")) {
            String value = hex.substring(1);
            try {
                int r;
                int g;
                int b;

                if (value.length() == 3 || value.length() == 4) {
                    r = Integer.parseInt("" + value.charAt(0) + value.charAt(0), 16);
                    g = Integer.parseInt("" + value.charAt(1) + value.charAt(1), 16);
                    b = Integer.parseInt("" + value.charAt(2) + value.charAt(2), 16);
                } else if (value.length() == 6 || value.length() == 8) {
                    r = Integer.parseInt(value.substring(0, 2), 16);
                    g = Integer.parseInt(value.substring(2, 4), 16);
                    b = Integer.parseInt(value.substring(4, 6), 16);
                } else {
                    return color;
                }

                return Color.rgba(r, g, b, alpha);
            } catch (NumberFormatException ignored) {
                return color;
            }
        }
        return color;
    }

    private double getPlotWidth() {
        return Math.max(0, this.dataSize - 1) * this.spacing;
    }

    @Override
    public IShape copy() {
        AreaDataSetView copy = new AreaDataSetView((Point) this.center.copy(), this.spacing, this.maxHeight,
                this.pointRadius);
        copy.setScaleMax(this.scaleMax);
        copy.setFillAlpha(this.fillAlpha);
        return copy;
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));
        sb.append("AreaView");
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
