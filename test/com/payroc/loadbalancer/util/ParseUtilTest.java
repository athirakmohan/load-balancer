package com.payroc.loadbalancer.util;

import com.payroc.loadbalancer.management.registry.Endpoint;
import junit.framework.TestCase;

import java.util.Vector;

public class ParseUtilTest extends TestCase {

    public void testParseListenPort() {
        int port = ParseUtil.parseListenPort("8080");
        assertEquals(8080, port);
    }

    public void testSplitHostPort() {
        String[] result = ParseUtil.splitHostPort("127.0.0.1:9001");
        assertNotNull(result);
        assertEquals("127.0.0.1", result[0]);
        assertEquals("9001", result[1]);
    }

    public void testSplitHostPortMissingColon() {
        String[] result = ParseUtil.splitHostPort("127.0.0.1");
        assertNull(result);
    }

    public void testSplitHostPortInvalidPort() {
        String[] result = ParseUtil.splitHostPort("127.0.0.1:abc");
        assertNull(result);
    }

    public void testParsesValidEndpoints() {
        String[] args = new String[]{"8080", "127.0.0.1:9001", "127.0.0.1:9002"};
        Vector endpoints = ParseUtil.parseEndpoints(args, 1);
        assertEquals(2, endpoints.size());

        Endpoint e1 = (Endpoint) endpoints.elementAt(0);
        Endpoint e2 = (Endpoint) endpoints.elementAt(1);

        assertEquals("127.0.0.1", e1.getHost());
        assertEquals(9001, e1.getPort());
        assertEquals(9002, e2.getPort());
    }

    public void testParseInvalidEndpointFormat() {
        String[] args = new String[]{"8080", "invalid-endpoint"};
        Vector endpoints = ParseUtil.parseEndpoints(args, 1);
        assertEquals(0, endpoints.size());
    }
}
