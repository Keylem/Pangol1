package fr.univrennes.istic.l2gen.application.core.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import fr.univrennes.istic.l2gen.application.core.table.DataTable;

public class FilterBuilderTest {

    private final List<DataTable> openedTables = new ArrayList<>();
    private final List<File> tempFiles = new ArrayList<>();

    @After
    public void cleanup() {
        for (DataTable table : openedTables) {
            try {
                table.close();
            } catch (Exception e) {
            }
        }

        for (File f : tempFiles) {
            try {
                Files.deleteIfExists(f.toPath());
            } catch (IOException e) {
            }
        }
    }

    private File createTestParquet() throws Exception {
        File parquet = new File(Files.createTempDirectory("fb-test").toFile(), "test.parquet");
        tempFiles.add(parquet);
        tempFiles.add(parquet.getParentFile());

        String select = "SELECT * FROM (VALUES "
                + "(1, 100.0, 'apple'),"
                + "(2, 200.0, 'banana'),"
                + "(3, 150.0, 'cherry'),"
                + "(4, 250.0, 'date')"
                + ") AS t(id, price, product)";

        writeParquet(parquet, select);
        return parquet;
    }

    private void writeParquet(File parquet, String selectQuery) throws Exception {
        String path = parquet.getAbsolutePath().replace("\\", "/").replace("'", "''");

        try (Connection connection = DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {
            statement.execute("COPY (" + selectQuery + ") TO '" + path + "' (FORMAT PARQUET)");
        }
    }

    @Test
    public void testCountQuery() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        String countQuery = FilterBuilder.count(table);
        Assert.assertNotNull(countQuery);
        Assert.assertTrue(countQuery.contains("COUNT(*)"));
        Assert.assertTrue(countQuery.contains("FROM"));
    }

    @Test
    public void testQueryWithLimitAndOffset() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        String query = FilterBuilder.query(table, 10, 0);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("SELECT"));
        Assert.assertTrue(query.contains("LIMIT 10"));
        Assert.assertTrue(query.contains("OFFSET 0"));
    }

    @Test
    public void testQueryWithoutLimit() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        String query = FilterBuilder.query(table, -1, -1);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("SELECT"));
        Assert.assertFalse(query.contains("LIMIT"));
        Assert.assertFalse(query.contains("OFFSET"));
    }

    @Test
    public void testBaseQueryStructure() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        StringBuilder baseQuery = FilterBuilder.base("SELECT *", table, false);
        Assert.assertNotNull(baseQuery);
        Assert.assertTrue(baseQuery.length() > 0);
    }

    @Test
    public void testQueryWithFilter() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        Filter filter = Filter.search(1, "100");
        table.addFilter(filter);

        String query = FilterBuilder.query(table, 10, 0);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("WHERE"));
        Assert.assertTrue(query.contains("LIKE") || query.contains("price"));
    }

    @Test
    public void testQueryWithSortFilter() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        Filter sortFilter = Filter.sort(1, true); // ascending on column 1 (price)
        table.addFilter(sortFilter);

        String query = FilterBuilder.query(table, -1, -1);
        Assert.assertNotNull(query);
        Assert.assertTrue(query.contains("ORDER BY"));
        Assert.assertTrue(query.contains("ASC") || query.contains("price"));
    }

    @Test
    public void testCountWithFilters() throws Exception {
        File parquet = createTestParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        Filter filter = Filter.hideEmpty(2); // hide null values on column 2
        table.addFilter(filter);

        String countQuery = FilterBuilder.count(table);
        Assert.assertNotNull(countQuery);
        Assert.assertTrue(countQuery.contains("COUNT(*)"));
        Assert.assertTrue(countQuery.contains("WHERE"));
    }

}
