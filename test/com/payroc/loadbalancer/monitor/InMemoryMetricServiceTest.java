package com.payroc.loadbalancer.monitor;

import junit.framework.TestCase;
import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Hashtable;

public class InMemoryMetricServiceTest extends TestCase {
    private InMemoryMetricService metricService;
    private Endpoint endpoint1;
    private Endpoint endpoint2;

    protected void setUp() {
        metricService = new InMemoryMetricService();
        endpoint1 = new Endpoint("10.0.0.1", 8080);
        endpoint2 = new Endpoint("10.0.0.2", 8080);
    }

    public void testSuccessMetricRecording() {
        metricService.recordSuccess(endpoint1);
        metricService.recordSuccess(endpoint1);
        metricService.recordSuccess(endpoint2);

        Hashtable metrics = metricService.getMetrics();

        assertEquals("Endpoint 1 success count should be 2",
                new Long(2),
                ((Hashtable) metrics.get(endpoint1.toString())).get("SUCCESS_COUNT"));

        assertEquals("Endpoint 2 success count should be 1",
                new Long(1),
                ((Hashtable) metrics.get(endpoint2.toString())).get("SUCCESS_COUNT"));
    }

    public void testFailureMetricRecording() {
        metricService.recordFailure(endpoint1);
        metricService.recordFailure(endpoint1);
        metricService.recordFailure(endpoint2);

        Hashtable metrics = metricService.getMetrics();

        assertEquals("Endpoint 1 failure count should be 2.",
                new Long(2),
                ((Hashtable) metrics.get(endpoint1.toString())).get("FAILURE_COUNT"));
    }

}