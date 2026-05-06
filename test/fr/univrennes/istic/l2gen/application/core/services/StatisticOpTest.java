package fr.univrennes.istic.l2gen.application.core.services;

import org.junit.Assert;
import org.junit.Test;

import fr.univrennes.istic.l2gen.application.core.services.stats.StatisticOp;

public class StatisticOpTest {

    @Test
    public void testDisplayNames() {
        Assert.assertEquals("Min", StatisticOp.MIN.getDisplayName());
        Assert.assertEquals("Max", StatisticOp.MAX.getDisplayName());
        Assert.assertEquals("Average", StatisticOp.AVG.getDisplayName());
        Assert.assertEquals("Sum", StatisticOp.SUM.getDisplayName());
        Assert.assertEquals("Count", StatisticOp.COUNT.getDisplayName());
    }
}