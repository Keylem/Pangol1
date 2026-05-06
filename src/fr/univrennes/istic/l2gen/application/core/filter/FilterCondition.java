package fr.univrennes.istic.l2gen.application.core.filter;

import java.io.Serializable;

public record FilterCondition(FilterOperator operator, String value, FilterFunction func) implements Serializable {
    private static final long serialVersionUID = 1L;

    public FilterCondition(FilterOperator operator) {
        this(operator, null, FilterFunction.NONE);
    }

    public FilterCondition(FilterOperator operator, String value) {
        this(operator, value, FilterFunction.NONE);
    }

    public String getSQL(String columnName) {
        return switch (operator) {
            case EQUAL -> columnName + " = '" + value + "'";
            case NOT_EQUAL -> columnName + " != '" + value + "'";
            case GREATER -> columnName + " > '" + value + "'";
            case GREATER_EQUAL -> columnName + " >= '" + value + "'";
            case LESS -> columnName + " < '" + value + "'";
            case LESS_EQUAL -> columnName + " <= '" + value + "'";
            case LIKE -> columnName + " LIKE '%" + value + "%'";
            case IS_NULL -> columnName + " IS NULL";
            case NOT_NULL -> columnName + " IS NOT NULL";
        };
    }
}