package com.payroc.loadbalancer.management.registry;

import java.util.Vector;

public interface EndpointRegistry {
    Vector getAvailableEndpoints();
    void addEndpoint(Endpoint endpoint);
    void removeEndpoint(Endpoint endpoint);
}