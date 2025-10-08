package com.payroc.loadbalancer.monitor;

import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Hashtable;

public interface MetricService {
    void recordSuccess(Endpoint endpoint);
    void recordFailure(Endpoint endpoint);
    Hashtable getMetrics();
}