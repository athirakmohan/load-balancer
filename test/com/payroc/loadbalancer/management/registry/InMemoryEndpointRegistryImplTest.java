package com.payroc.loadbalancer.management.registry;

import junit.framework.TestCase;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.InMemoryEndpointRegistryImpl;

public class InMemoryEndpointRegistryImplTest extends TestCase {

    private InMemoryEndpointRegistryImpl registry;

    protected void setUp() {
        registry = new InMemoryEndpointRegistryImpl();
    }

    public void testAddAndRetrieve() {
        Endpoint endpoint1 = new Endpoint("1.1.1.1", 8080);
        registry.addEndpoint(endpoint1);

        assertEquals(1, registry.getAvailableEndpoints().size());
        assertTrue(registry.getAvailableEndpoints().contains(endpoint1));
    }

    public void testRemoval() {
        Endpoint endpoint1 = new Endpoint("1.1.1.1", 8080);
        Endpoint endpoint2 = new Endpoint("2.2.2.2", 8080);

        registry.addEndpoint(endpoint1);
        registry.addEndpoint(endpoint2);

        registry.removeEndpoint(endpoint1);
        assertEquals(1, registry.getAvailableEndpoints().size());
        assertFalse(registry.getAvailableEndpoints().contains(endpoint1));
        assertTrue(registry.getAvailableEndpoints().contains(endpoint2));
    }
}