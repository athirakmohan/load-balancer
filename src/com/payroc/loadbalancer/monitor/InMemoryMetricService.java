package com.payroc.loadbalancer.monitor;

import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Enumeration;
import java.util.Hashtable;

public class InMemoryMetricService implements MetricService {
    private Hashtable successCounts = new Hashtable();
    private Hashtable failureCounts = new Hashtable();

    public void recordSuccess(Endpoint endpoint) {
        incrementCount(successCounts, endpoint.toString());
    }

    public void recordFailure(Endpoint endpoint) {
        incrementCount(failureCounts, endpoint.toString());
    }

    public synchronized Hashtable getMetrics() {
        Hashtable consolidated = new Hashtable();
        Hashtable uniqueKeys = collectUniqueKeys();

        Enumeration endpointKeys = uniqueKeys.keys();
        while (endpointKeys.hasMoreElements()) {
            String endpointString = (String) endpointKeys.nextElement();
            Hashtable endpointMetrics = buildEndpointMetrics(endpointString);
            consolidated.put(endpointString, endpointMetrics);
        }

        clearAllCounts();
        return (Hashtable) consolidated.clone();
    }

    private Hashtable collectUniqueKeys() {
        Hashtable keys = new Hashtable();

        Enumeration successKeys = successCounts.keys();
        while (successKeys.hasMoreElements()) {
            keys.put(successKeys.nextElement(), "");
        }

        Enumeration failureKeys = failureCounts.keys();
        while (failureKeys.hasMoreElements()) {
            keys.put(failureKeys.nextElement(), "");
        }

        return keys;
    }

    private Hashtable buildEndpointMetrics(String endpointString) {
        Hashtable endpointMetrics = new Hashtable();

        Integer successes = (Integer) successCounts.get(endpointString);
        Integer failures = (Integer) failureCounts.get(endpointString);

        long successValue = successes != null ? successes.intValue() : 0;
        long failureValue = failures != null ? failures.intValue() : 0;

        endpointMetrics.put("SUCCESS_COUNT", new Long(successValue));
        endpointMetrics.put("FAILURE_COUNT", new Long(failureValue));

        return endpointMetrics;
    }

    private void clearAllCounts() {
        successCounts.clear();
        failureCounts.clear();
    }


    private void incrementCount(Hashtable table, String key) {
        synchronized (table) {
            Integer count = (Integer) table.get(key);
            if (count == null) {
                table.put(key, new Integer(1));
            } else {
                table.put(key, new Integer(count.intValue() + 1));
            }
        }
    }

}