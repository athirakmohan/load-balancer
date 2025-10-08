package com.payroc.loadbalancer.handler.algorithm;

import com.payroc.loadbalancer.management.registry.Endpoint;
import junit.framework.TestCase;
import java.util.Vector;

public class RoundRobinAlgorithmTest extends TestCase {

    private Vector endpoints;
    private Algorithm algorithm;

    protected void setUp() {
        endpoints = new Vector();
        endpoints.addElement(new Endpoint("10.0.0.1", 80));
        endpoints.addElement(new Endpoint("10.0.0.2", 80));
        endpoints.addElement(new Endpoint("10.0.0.3", 80));

        algorithm = new RoundRobinAlgorithm();
    }

    public void testBasicRotation() {

        Endpoint e1 = algorithm.getNextEndpoint(endpoints);
        assertEquals("10.0.0.1", e1.getHost());

        Endpoint e2 = algorithm.getNextEndpoint(endpoints);
        assertEquals("10.0.0.2", e2.getHost());

        Endpoint e3 = algorithm.getNextEndpoint(endpoints);
        assertEquals("10.0.0.3", e3.getHost());

        Endpoint e4 = algorithm.getNextEndpoint(endpoints);
        assertEquals("10.0.0.1", e4.getHost());
    }

    public void testEmptyListHandling() {
        Vector emptyList = new Vector();
        Endpoint result = algorithm.getNextEndpoint(emptyList);
        assertNull(result);
    }
}