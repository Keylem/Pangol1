package fr.univrennes.istic.l2gen.application.core.filter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class Filter implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int columnIndex;

    private FilterSort sort = FilterSort.NONE;
    private FilterLogic operator = FilterLogic.AND;

    private final ArrayList<FilterCondition> conditions = new ArrayList<>();

    public Filter(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public static Filter sort(int columnIndex, boolean ascending) {
        Filter filter = new Filter(columnIndex);
        filter.sort = ascending ? FilterSort.ASCENDING : FilterSort.DESCENDING;
        return filter;
    }

    public static Filter search(int columnIndex, String value) {
        Filter filter = new Filter(columnIndex);
        filter.add(new FilterCondition(FilterOperator.LIKE, value));
        return filter;
    }

    public static Filter search(int columnIndex, double value) {
        Filter filter = new Filter(columnIndex);
        filter.add(new FilterCondition(FilterOperator.EQUAL, String.valueOf(value)));
        return filter;
    }

    public static Filter search(int columnIndex, int value) {
        Filter filter = new Filter(columnIndex);
        filter.add(new FilterCondition(FilterOperator.EQUAL, String.valueOf(value)));
        return filter;
    }

    public static Filter search(int columnIndex, Timestamp value) {
        Filter filter = new Filter(columnIndex);
        filter.add(new FilterCondition(FilterOperator.EQUAL, value.toLocalDateTime().format(formatter)));
        return filter;
    }

    public static Filter topN(int columnIndex, double value) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.GREATER, String.valueOf(value)));
        return filter;
    }

    public static Filter topN(int columnIndex, int length) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.GREATER, String.valueOf(length)));
        return filter;
    }

    public static Filter topN(int columnIndex, Timestamp date) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.GREATER, date.toLocalDateTime().format(formatter)));
        return filter;
    }

    public static Filter bottomN(int columnIndex, double value) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.LESS, String.valueOf(value)));
        return filter;
    }

    public static Filter bottomN(int columnIndex, int length) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.LESS, String.valueOf(length)));
        return filter;
    }

    public static Filter bottomN(int columnIndex, Timestamp date) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.LESS, date.toLocalDateTime().format(formatter)));
        return filter;
    }

    public static Filter byRange(int columnIndex, double minValue, double maxValue) {
        Filter filter = new Filter(columnIndex);
        filter.add(new FilterCondition(FilterOperator.GREATER_EQUAL, String.valueOf(minValue)));
        filter.add(new FilterCondition(FilterOperator.LESS_EQUAL, String.valueOf(maxValue)));
        return filter;
    }

    public static Filter byRange(int columnIndex, int minLength, int maxLength) {
        Filter filter = new Filter(columnIndex);
        filter.add(new FilterCondition(FilterOperator.GREATER_EQUAL, String.valueOf(minLength), FilterFunction.LENGTH));
        filter.add(new FilterCondition(FilterOperator.LESS_EQUAL, String.valueOf(maxLength), FilterFunction.LENGTH));
        return filter;
    }

    public static Filter byRange(int columnIndex, Timestamp minDate, Timestamp maxDate) {
        Filter filter = new Filter(columnIndex);

        filter.add(new FilterCondition(FilterOperator.GREATER_EQUAL, minDate.toLocalDateTime().format(formatter)));
        filter.add(new FilterCondition(FilterOperator.LESS_EQUAL, maxDate.toLocalDateTime().format(formatter)));
        return filter;
    }

    public static Filter showEmpty(int columnIndex) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.IS_NULL));
        return filter;
    }

    public static Filter hideEmpty(int columnIndex) {
        Filter filter = new Filter(columnIndex);
        filter.conditions.add(new FilterCondition(FilterOperator.NOT_NULL));
        return filter;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setOperator(FilterLogic operator) {
        this.operator = operator;
    }

    public void setSort(FilterSort sort) {
        this.sort = sort;
    }

    public FilterLogic getOperator() {
        return operator;
    }

    public FilterSort getSort() {
        return sort;
    }

    public List<FilterCondition> getConditions() {
        return conditions;
    }

    public void add(FilterCondition condition) {
        conditions.add(condition);
    }

    public boolean hasRowLimitingEffect() {
        return conditions.stream().anyMatch(c -> switch (c.operator()) {
            case GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, LIKE -> true;
            case IS_NULL, NOT_NULL -> true;
            default -> false;
        });
    }

    public boolean hasConditions() {
        return !conditions.isEmpty();
    }

    public String getSQL(String columnName) {
        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < conditions.size(); i++) {
            FilterCondition condition = conditions.get(i);

            String columnNameFunc = columnName;
            if (condition.func() != FilterFunction.NONE) {
                columnNameFunc = condition.func().name() + "(" + columnNameFunc + ")";
            }
            sql.append(condition.getSQL(columnNameFunc));
            if (i < conditions.size() - 1) {
                sql.append(" ").append(operator.name()).append(" ");
            }
        }

        return sql.toString();
    }
}
