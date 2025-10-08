package com.payroc.loadbalancer.management.registry;

import java.util.Vector;

public class InMemoryEndpointRegistry implements EndpointRegistry {

    private Vector endpoints = new Vector();

    public Vector getAvailableEndpoints() {
        return (Vector) endpoints.clone();
    }

    public void addEndpoint(Endpoint endpoint) {
        if (!endpoints.contains(endpoint)) {
            endpoints.addElement(endpoint);
            System.out.println("Registry: Added " + endpoint);
        }
    }

    public void removeEndpoint(Endpoint endpoint) {
        if (endpoints.contains(endpoint)) {
            endpoints.removeElement(endpoint);
            System.out.println("Registry: Removed " + endpoint);
        }
    }
}