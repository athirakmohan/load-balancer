package com.payroc.loadbalancer.monitor;

import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Hashtable;
import java.util.Enumeration;

public class ConsoleConnectionMetricService implements ConnectionMetricService {
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

        Hashtable uniqueKeys = new Hashtable();
        Enumeration successKeys = successCounts.keys();
        while (successKeys.hasMoreElements()) {
            uniqueKeys.put(successKeys.nextElement(), "");
        }
        Enumeration failureKeys = failureCounts.keys();
        while (failureKeys.hasMoreElements()) {
            uniqueKeys.put(failureKeys.nextElement(), "");
        }

        Enumeration endpointKeys = uniqueKeys.keys();
        while (endpointKeys.hasMoreElements()) {
            String endpointString = (String) endpointKeys.nextElement();
            Hashtable endpointMetrics = new Hashtable();

            Integer successes = (Integer) successCounts.get(endpointString);
            Integer failures = (Integer) failureCounts.get(endpointString);

            endpointMetrics.put("SUCCESS_COUNT", new Long(successes != null ? successes.intValue() : 0));
            endpointMetrics.put("FAILURE_COUNT", new Long(failures != null ? failures.intValue() : 0));

            successCounts.clear();
            failureCounts.clear();

            consolidated.put(endpointString, endpointMetrics);
        }

        return (Hashtable) consolidated.clone();
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