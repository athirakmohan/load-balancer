package com.payroc.loadbalancer.monitor;

import java.util.Hashtable;

public interface MetricPublisher {

    void publish(Hashtable allMetrics);
}
