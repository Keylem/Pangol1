package fr.univrennes.istic.l2gen.application.core.table;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class DataTableTest {

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

    @Test
    public void testOfInvalidFiles() throws Exception {
        File txt = File.createTempFile("datatable-invalid", ".txt");
        try (FileWriter fw = new FileWriter(txt)) {
            fw.write("content");
        }
        tempFiles.add(txt);

        File emptyParquet = File.createTempFile("datatable-empty", ".parquet");
        tempFiles.add(emptyParquet);

        File fakeParquet = File.createTempFile("datatable-fake", ".parquet");
        try (FileWriter fw = new FileWriter(fakeParquet)) {
            fw.write("not a parquet");
        }
        tempFiles.add(fakeParquet);

        Assert.assertNull(DataTable.of(txt));
        Assert.assertNull(DataTable.of(emptyParquet));
        Assert.assertNull(DataTable.of(fakeParquet));
    }

    @Test
    public void testValueAccessCacheOpenAndClose() throws Exception {
        File parquet = createMainParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        Object id = table.getValueAt(0, 0);
        Assert.assertTrue(id instanceof Number);
        Assert.assertEquals(1, ((Number) id).intValue());

        Assert.assertNull(table.getValueAt(-1, 0));
        Assert.assertNull(table.getValueAt(10, 0));
        Assert.assertNull(table.getValueAt(0, -1));
        Assert.assertNull(table.getValueAt(0, 999));

        @SuppressWarnings("unchecked")
        Map<Integer, Object[][]> blockCache = (Map<Integer, Object[][]>) getField(table, "blockCache");
        Assert.assertTrue(blockCache.containsKey(0));
        int cacheSize = blockCache.size();

        Object secondRead = table.getValueAt(1, 0);
        Assert.assertTrue(secondRead instanceof Number);
        Assert.assertEquals(2, ((Number) secondRead).intValue());
        Assert.assertEquals(cacheSize, blockCache.size());

        table.close();
        Assert.assertTrue(table.isClosed());

        Assert.assertTrue(table.open());
        Assert.assertFalse(table.isClosed());
        Assert.assertTrue(table.open());
    }

    @Test
    public void testPrefetchAndRejectedExecutionPath() throws Exception {
        File parquet = createLargeParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        Assert.assertEquals(10_050L, table.getRowCount());
        Assert.assertEquals(2, table.getBlockCount());
        Assert.assertEquals(50L, table.getBlockRowCount(1));

        table.prefetch(0, 10_000);

        @SuppressWarnings("unchecked")
        Map<Integer, Object[][]> blockCache = (Map<Integer, Object[][]>) getField(table, "blockCache");
        waitForCache(blockCache);
        Assert.assertTrue(blockCache.containsKey(0));

        table.prefetch(0, 10_000);

        setField(table, "prefetchExecutor", new RejectingExecutorService());
        invokePrivate(table, "invalidateCache", new Class<?>[] {});
        table.prefetch(0, 1);

        ExecutorService closedExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();
        closedExecutor.shutdownNow();
        setField(table, "prefetchExecutor", closedExecutor);
        table.prefetch(0, 1);
    }

    @Test
    public void testSetColumnTypeSetAliasAndLoadBlockCatchPath() throws Exception {
        File parquet = createMainParquet();
        DataTable table = DataTable.of(parquet);
        Assert.assertNotNull(table);
        openedTables.add(table);

        DataType before = table.getColumnType(0);
        table.setColumnType(-1, DataType.DOUBLE);
        Assert.assertEquals(before, table.getColumnType(0));

        table.setColumnType(0, DataType.DOUBLE);
        Assert.assertEquals(DataType.DOUBLE, table.getColumnType(0));

        table.setAlias("renamed-main");
        Assert.assertEquals("renamed-main", table.getAlias());

        File throwingDeletePath = createMainParquet();
        DataTable deleteFailTable = DataTable.of(new ThrowingDeleteFile(throwingDeletePath.getAbsolutePath()));
        Assert.assertNotNull(deleteFailTable);
        openedTables.add(deleteFailTable);
        DataType deleteFailBefore = deleteFailTable.getColumnType(0);
        deleteFailTable.setColumnType(0, DataType.DOUBLE);
        Assert.assertEquals(deleteFailBefore, deleteFailTable.getColumnType(0));

        File throwingRenamePath = createMainParquet();
        DataTable renameFailTable = DataTable.of(new ThrowingRenameFile(throwingRenamePath.getAbsolutePath()));
        Assert.assertNotNull(renameFailTable);
        openedTables.add(renameFailTable);
        renameFailTable.setAlias("new-alias");
        Assert.assertEquals("new-alias", renameFailTable.getAlias());

        table.close();
        Object[][] failedLoad = (Object[][]) invokePrivate(table, "loadBlock", new Class<?>[] { int.class }, 0);
        Assert.assertEquals(0, failedLoad.length);
    }

    private File createMainParquet() throws Exception {
        File parquet = new File(Files.createTempDirectory("dt-main").toFile(), "main.parquet");
        tempFiles.add(parquet);
        tempFiles.add(parquet.getParentFile());

        String select = "SELECT * FROM (VALUES "
                + "(1, 10.5, 'alpha', CAST(NULL AS VARCHAR), true, DATE '2024-01-01'),"
                + "(2, 20.0, 'beta', CAST(NULL AS VARCHAR), false, DATE '2024-01-02'),"
                + "(3, NULL, 'O''Brien', CAST(NULL AS VARCHAR), NULL, DATE '2024-01-03'),"
                + "(4, 5.0, 'delta', CAST(NULL AS VARCHAR), true, DATE '2024-01-04'),"
                + "(5, 30.0, 'gamma', CAST(NULL AS VARCHAR), false, DATE '2024-01-05')"
                + ") AS t(id, val, txt, empty_col, flag, d)";

        writeParquet(parquet, select);
        return parquet;
    }

    private File createLargeParquet() throws Exception {
        File parquet = new File(Files.createTempDirectory("dt-large").toFile(), "large.parquet");
        tempFiles.add(parquet);
        tempFiles.add(parquet.getParentFile());

        String select = "SELECT i AS id, i * 1.0 AS val, ('row' || i)::VARCHAR AS txt FROM range(0, 10050) t(i)";
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

    private void waitForCache(Map<Integer, Object[][]> cache) throws Exception {
        long deadline = System.currentTimeMillis() + 2_000;
        while (System.currentTimeMillis() < deadline) {
            synchronized (cache) {
                if (!cache.isEmpty()) {
                    return;
                }
            }
            Thread.sleep(20);
        }
    }

    private Object invokePrivate(Object target, String methodName, Class<?>[] paramTypes, Object... args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static class RejectingExecutorService extends AbstractExecutorService {
        @Override
        public void shutdown() {
        }

        @Override
        public List<Runnable> shutdownNow() {
            return List.of();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return true;
        }

        @Override
        public void execute(Runnable command) {
            throw new RejectedExecutionException("Rejected for test");
        }
    }

    private static class ThrowingDeleteFile extends File {
        private static final long serialVersionUID = 1L;

        ThrowingDeleteFile(String pathname) {
            super(pathname);
        }

        @Override
        public boolean delete() {
            throw new RuntimeException("delete failed");
        }
    }

    private static class ThrowingRenameFile extends File {
        private static final long serialVersionUID = 1L;

        ThrowingRenameFile(String pathname) {
            super(pathname);
        }

        @Override
        public boolean renameTo(File dest) {
            throw new RuntimeException("rename failed");
        }
    }
}
