package com.payroc.loadbalancer.monitor;

import java.util.Enumeration;
import java.util.Hashtable;

public class ConsoleMetricPublisher implements MetricPublisher {

    public void publish(Hashtable allMetrics) {
        Enumeration keys = allMetrics.keys();
        while (keys.hasMoreElements()) {
            String endpointKey = (String) keys.nextElement();
            Hashtable endpointMetrics = (Hashtable) allMetrics.get(endpointKey);

            Long success = (Long) endpointMetrics.get("SUCCESS_COUNT");
            Long failure = (Long) endpointMetrics.get("FAILURE_COUNT");

            long successCount = (success != null) ? success.longValue() : 0;
            long failureCount = (failure != null) ? failure.longValue() : 0;

            System.out.println("[MetricPublisher][" + Thread.currentThread().getName() + "] Endpoint "
                    + endpointKey
                    + ": Successes=" + successCount
                    + ", Failures=" + failureCount);
        }
    }
}