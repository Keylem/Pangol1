package fr.univrennes.istic.l2gen.application.core.table;

import org.junit.Assert;
import org.junit.Test;

public class DataTableWorkerStatusTest {

    @Test
    public void testRecordAccessorsAndContracts() {
        DataTableWorkerStatus status = new DataTableWorkerStatus(3, 9, 42L);
        DataTableWorkerStatus same = new DataTableWorkerStatus(3, 9, 42L);
        DataTableWorkerStatus different = new DataTableWorkerStatus(4, 9, 42L);

        Assert.assertEquals(3, status.loadedCount());
        Assert.assertEquals(9, status.totalCount());
        Assert.assertEquals(42L, status.elapsed());

        Assert.assertEquals(status, same);
        Assert.assertEquals(status.hashCode(), same.hashCode());
        Assert.assertNotEquals(status, different);

        String text = status.toString();
        Assert.assertTrue(text.contains("loadedCount=3"));
        Assert.assertTrue(text.contains("totalCount=9"));
        Assert.assertTrue(text.contains("elapsed=42"));
    }
}
