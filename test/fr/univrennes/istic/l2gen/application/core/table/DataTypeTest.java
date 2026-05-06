package fr.univrennes.istic.l2gen.application.core.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DataTypeTest {

    @Test
    public void testInferenceOrder() {
        List<DataType> expected = List.of(
                DataType.EMPTY,
                DataType.BOOLEAN,
                DataType.INTEGER,
                DataType.DOUBLE,
                DataType.DATE);

        Assert.assertEquals(expected, DataType.INFERENCE_ORDER);
    }

    @Test
    public void testNumericAndCategoricalFlags() {
        Assert.assertTrue(DataType.INTEGER.isNumeric());
        Assert.assertTrue(DataType.DOUBLE.isNumeric());
        Assert.assertFalse(DataType.STRING.isNumeric());

        Assert.assertTrue(DataType.STRING.isCategorical());
        Assert.assertTrue(DataType.BOOLEAN.isCategorical());
        Assert.assertFalse(DataType.DATE.isCategorical());
    }

    @Test
    public void testFromSQL() {
        Assert.assertEquals(DataType.INTEGER, DataType.fromSQL("bigint"));
        Assert.assertEquals(DataType.DOUBLE, DataType.fromSQL("float"));
        Assert.assertEquals(DataType.BOOLEAN, DataType.fromSQL("boolean"));
        Assert.assertEquals(DataType.DATE, DataType.fromSQL("timestamp"));
        Assert.assertEquals(DataType.STRING, DataType.fromSQL("varchar"));
    }

    @Test
    public void testToSQL() {
        Assert.assertEquals("VARCHAR", DataType.STRING.toSQL());
        Assert.assertEquals("INTEGER", DataType.INTEGER.toSQL());
        Assert.assertEquals("DOUBLE", DataType.DOUBLE.toSQL());
        Assert.assertEquals("BOOLEAN", DataType.BOOLEAN.toSQL());
        Assert.assertEquals("DATE", DataType.DATE.toSQL());
        Assert.assertEquals("VARCHAR", DataType.EMPTY.toSQL());
    }
}
