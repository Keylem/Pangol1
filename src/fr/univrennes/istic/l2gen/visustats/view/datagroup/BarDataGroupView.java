package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.datagroup.axis.DataAxisView;
import fr.univrennes.istic.l2gen.visustats.view.dataset.BarDataSetView;

@SVGTag("g")
public class BarDataGroupView extends AbstractDataGroupView {

        @SVGField("data-maxheight")
        private double maxHeight;

        @SVGField("data-barwidth")
        private double barWidth;

        public BarDataGroupView(
                        DataGroup data,
                        Point center,
                        double spacing,

                        double barWidth,
                        double maxHeight,

                        boolean horizontalLegend) {

                super(data, center, spacing, horizontalLegend);

                this.maxHeight = maxHeight;

                this.spacing = spacing;

                this.center = center;
                this.barWidth = barWidth;
                this.update();
        }

        @Override
        protected double getTotalElementsHeight() {
                return this.maxHeight;
        }

        @Override
        protected double getElementWidth() {
                return this.barWidth;
        }

        @Override
        protected BarDataSetView createElement(Point position) {
                return new BarDataSetView(position, this.barWidth, this.maxHeight);
        }

        @Override
        public boolean isAxisEnabled() {
                return true;
        }

        @Override
        protected IShape getAxisElement() {
                return new DataAxisView(this.center, this.getTotalElementsWidth(), this.getTotalElementsHeight(),
                                this.spacing, this.data.max(), this.axisStepCount, this.axisScaleType, this.showXAxis,
                                this.xAxisLabel, this.showYAxis, this.yAxisLabel);
        }

}
