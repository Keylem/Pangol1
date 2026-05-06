package fr.univrennes.istic.l2gen.visustats.view.datagroup.axis;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Text;
import fr.univrennes.istic.l2gen.geometry.base.Triangle;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.Label;

@SVGTag("g")
public final class DataAxisView extends Group {

        @SVGField({ "data-center-x", "data-center-y" })
        private Point center;

        @SVGField("data-totalwidth")
        private double totalWidth;

        @SVGField("data-totalheight")
        private double totalHeight;

        @SVGField("data-spacing")
        private double spacing;

        @SVGField("data-maxvalue")
        private double maxValue;

        @SVGField("data-stepcount")
        private int stepCount;

        @SVGField("data-scaletype")
        private DataAxisViewScaleType scaleType;

        @SVGField("data-showx")
        private boolean showXAxis;

        @SVGField("data-xlabel")
        private String xAxisLabel;

        @SVGField("data-showy")
        private boolean showYAxis;

        @SVGField("data-ylabel")
        private String yAxisLabel;

        public DataAxisView(Point center, double totalWidth, double totalHeight, double spacing, double maxValue,
                        int stepCount, DataAxisViewScaleType scaleType, boolean showXAxis, String xAxisLabel,
                        boolean showYAxis, String yAxisLabel) {
                this.center = center;
                this.totalWidth = totalWidth;
                this.totalHeight = totalHeight;
                this.spacing = spacing;

                this.maxValue = maxValue;
                this.stepCount = stepCount;
                this.scaleType = scaleType;

                this.showXAxis = showXAxis;
                this.xAxisLabel = xAxisLabel;
                this.showYAxis = showYAxis;
                this.yAxisLabel = yAxisLabel;

                this.update();
        }

        private void update() {
                this.elements.clear();

                double padding = this.spacing * 2;
                double triangleSize = 5;

                if (this.showXAxis) {
                        Path horizontal = new Path();
                        horizontal
                                        .getStyle()
                                        .strokeColor(Color.BLACK)
                                        .strokeWidth(2);

                        horizontal
                                        .draw()
                                        .move(
                                                        this.center.getX() - (this.totalWidth / 2),
                                                        this.center.getY() + (this.totalHeight / 2),
                                                        false)
                                        .line(
                                                        this.center.getX() + (this.totalWidth / 2) + padding
                                                                        - triangleSize,
                                                        this.center.getY() + (this.totalHeight / 2),
                                                        false);
                        this.elements.add(new Triangle(
                                        new Point(
                                                        this.center.getX() + (this.totalWidth / 2) + padding
                                                                        - triangleSize,
                                                        this.center.getY() + (this.totalHeight / 2)
                                                                        - triangleSize),
                                        new Point(
                                                        this.center.getX() + (this.totalWidth / 2) + padding
                                                                        - triangleSize,
                                                        this.center.getY() + (this.totalHeight / 2)
                                                                        + triangleSize),
                                        new Point(
                                                        this.center.getX() + (this.totalWidth / 2) + padding,
                                                        this.center.getY() + (this.totalHeight / 2))));

                        this.elements.add(horizontal);

                        if (this.xAxisLabel != null && !this.xAxisLabel.isBlank()) {
                                Label label = new Label(this.xAxisLabel);
                                Text labelText = label.createText(new Point(
                                                this.center.getX() + (this.totalWidth / 2) + padding + 10,
                                                this.center.getY() + (this.totalHeight / 2) + 14));
                                labelText.getStyle().textAnchor("start");
                                this.elements.add(labelText);
                        }
                }

                if (this.showYAxis) {
                        Path vertical = new Path();
                        vertical
                                        .getStyle()
                                        .strokeColor(Color.BLACK)
                                        .strokeWidth(2);

                        vertical.draw()
                                        .move(
                                                        this.center.getX() - (this.totalWidth / 2),
                                                        this.center.getY() + (this.totalHeight / 2),
                                                        false)
                                        .line(
                                                        this.center.getX() - (this.totalWidth / 2),
                                                        this.center.getY() - (this.totalHeight / 2) - padding,
                                                        false);

                        double[] steps = new double[this.stepCount + 1];
                        double scaledMax = applyScale(this.maxValue);
                        if (scaledMax <= 0) {
                                scaledMax = 1;
                        }
                        for (int i = 0; i <= this.stepCount; i++) {
                                steps[i] = (this.maxValue / this.stepCount) * i;
                                double scaledValue = applyScale(steps[i]);
                                double y = this.center.getY() + (this.totalHeight / 2) - (scaledValue /
                                                scaledMax) * this.totalHeight;
                                vertical.draw()
                                                .move(this.center.getX() - (this.totalWidth / 2) - 5, y, false)
                                                .line(this.center.getX() - (this.totalWidth / 2), y, false);

                                Label label = new Label(String.format("%d", (int) Math.round(steps[i])));

                                Text labelText = label
                                                .createText(new Point(this.center.getX() - (this.totalWidth / 2) - 10,
                                                                y + 4));
                                labelText.getStyle().textAnchor("end");
                                this.elements.add(labelText);
                        }

                        this.elements.add(new Triangle(
                                        new Point(
                                                        this.center.getX() - (this.totalWidth / 2) - triangleSize,
                                                        this.center.getY() - (this.totalHeight / 2)
                                                                        - padding),

                                        new Point(
                                                        this.center.getX() - (this.totalWidth / 2) + triangleSize,
                                                        this.center.getY() - (this.totalHeight / 2)
                                                                        - padding),

                                        new Point(
                                                        this.center.getX() - (this.totalWidth / 2),
                                                        this.center.getY() - (this.totalHeight / 2) - padding
                                                                        - triangleSize)));
                        this.elements.add(vertical);

                        if (this.yAxisLabel != null && !this.yAxisLabel.isBlank()) {
                                Label label = new Label(this.yAxisLabel);
                                Text labelText = label.createText(new Point(
                                                this.center.getX() - (this.totalWidth / 2) - padding - 10,
                                                this.center.getY() - (this.totalHeight / 2) - padding - 12));
                                labelText.getStyle().textAnchor("end");
                                this.elements.add(labelText);
                        }
                }

        }

        private double applyScale(double value) {
                switch (this.scaleType) {
                        case LOG:
                                return value <= 0 ? 0 : Math.log10(value);
                        case SQRT:
                                return value < 0 ? 0 : Math.sqrt(value);
                        case LINEAR:
                        default:
                                return value;
                }
        }
}
