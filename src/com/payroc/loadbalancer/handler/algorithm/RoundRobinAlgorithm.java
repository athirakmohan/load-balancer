package com.payroc.loadbalancer.handler.algorithm;

import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Vector;

public class RoundRobinAlgorithm implements Algorithm {
    private volatile int current = 0;

    public Endpoint getNextEndpoint(Vector availableEndpoints) {
        if (availableEndpoints == null || availableEndpoints.isEmpty()) {
            return null;
        }
        synchronized (this) {
            int index = current % availableEndpoints.size();
            current++;
            return (Endpoint) availableEndpoints.elementAt(index);
        }
    }
}