package fr.univrennes.istic.l2gen.application.gui.dialog.subtable;

import fr.univrennes.istic.l2gen.application.core.config.Lang;

public enum AggregationOp {
    NONE("none", "subtable.agg.none", "%s"),
    SUM("sum", "subtable.agg.sum", "SUM(%s)"),
    AVG("avg", "subtable.agg.avg", "AVG(%s)"),
    MIN("min", "subtable.agg.min", "MIN(%s)"),
    MAX("max", "subtable.agg.max", "MAX(%s)"),
    COUNT("count", "subtable.agg.count", "COUNT(%s)"),
    COUNT_DISTINCT("count_distinct", "subtable.agg.count_distinct", "COUNT(DISTINCT %s)");

    private final String suffix;
    private final String labelKey;
    private final String sqlFormat;

    AggregationOp(String suffix, String labelKey, String sqlFormat) {
        this.suffix = suffix;
        this.labelKey = labelKey;
        this.sqlFormat = sqlFormat;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getLabel() {
        return Lang.get(labelKey);
    }

    public String toSql(String columnSql) {
        return String.format(sqlFormat, columnSql);
    }
}