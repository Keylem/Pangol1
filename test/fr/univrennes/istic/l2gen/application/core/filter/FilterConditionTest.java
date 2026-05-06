package fr.univrennes.istic.l2gen.application.core.filter;

import org.junit.Assert;
import org.junit.Test;

public class FilterConditionTest {

    @Test
    public void testConstructorWithOperatorOnlyUsesDefaults() {
        FilterCondition condition = new FilterCondition(FilterOperator.IS_NULL);

        Assert.assertEquals(FilterOperator.IS_NULL, condition.operator());
        Assert.assertNull(condition.value());
        Assert.assertEquals(FilterFunction.NONE, condition.func());
    }

    @Test
    public void testConstructorWithOperatorAndValueUsesDefaultFunction() {
        FilterCondition condition = new FilterCondition(FilterOperator.EQUAL, "abc");

        Assert.assertEquals(FilterOperator.EQUAL, condition.operator());
        Assert.assertEquals("abc", condition.value());
        Assert.assertEquals(FilterFunction.NONE, condition.func());
    }

    @Test
    public void testGetSQLEqual() {
        FilterCondition condition = new FilterCondition(FilterOperator.EQUAL, "42");

        Assert.assertEquals("price = '42'", condition.getSQL("price"));
    }

    @Test
    public void testGetSQLNotEqual() {
        FilterCondition condition = new FilterCondition(FilterOperator.NOT_EQUAL, "42");

        Assert.assertEquals("price != '42'", condition.getSQL("price"));
    }

    @Test
    public void testGetSQLGreater() {
        FilterCondition condition = new FilterCondition(FilterOperator.GREATER, "42");

        Assert.assertEquals("price > '42'", condition.getSQL("price"));
    }

    @Test
    public void testGetSQLGreaterEqual() {
        FilterCondition condition = new FilterCondition(FilterOperator.GREATER_EQUAL, "42");

        Assert.assertEquals("price >= '42'", condition.getSQL("price"));
    }

    @Test
    public void testGetSQLLess() {
        FilterCondition condition = new FilterCondition(FilterOperator.LESS, "42");

        Assert.assertEquals("price < '42'", condition.getSQL("price"));
    }

    @Test
    public void testGetSQLLessEqual() {
        FilterCondition condition = new FilterCondition(FilterOperator.LESS_EQUAL, "42");

        Assert.assertEquals("price <= '42'", condition.getSQL("price"));
    }

    @Test
    public void testGetSQLLike() {
        FilterCondition condition = new FilterCondition(FilterOperator.LIKE, "abc");

        Assert.assertEquals("name LIKE '%abc%'", condition.getSQL("name"));
    }

    @Test
    public void testGetSQLIsNull() {
        FilterCondition condition = new FilterCondition(FilterOperator.IS_NULL);

        Assert.assertEquals("name IS NULL", condition.getSQL("name"));
    }

    @Test
    public void testGetSQLNotNull() {
        FilterCondition condition = new FilterCondition(FilterOperator.NOT_NULL);

        Assert.assertEquals("name IS NOT NULL", condition.getSQL("name"));
    }
}
