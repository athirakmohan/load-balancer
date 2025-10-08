package com.payroc.loadbalancer.monitor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionMetricPublisher {
    private ConnectionMetricService connectionMetricService;
    private long reportInterval = 30000; // 30 seconds
    private final Timer timer;

    public ConnectionMetricPublisher(ConnectionMetricService connectionMetricService) {
        this.connectionMetricService = connectionMetricService;
        this.timer = new Timer(true);
    }

    public void start() {
        System.out.println("MetricPublisher: Starting periodic reporting");
        timer.schedule(new MetricTask(), 0, reportInterval);
    }

    public void stop() {
        System.out.println("MetricPublisher: Stopping ");
        timer.cancel();
    }

    private class MetricTask extends TimerTask {
        public void run() {
            try {

                Hashtable allMetrics = connectionMetricService.getMetrics();
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


            } catch (Exception e) {
                System.out.println("MetricPublisher: Unexpected error: " + e.getMessage());
            }
        }

    }
}
