package com.payroc.loadbalancer.monitor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class MetricPublisher  {
    private MetricService metricService;
    private long reportInterval = 30000; // 30 seconds
    private final Timer timer;

    public MetricPublisher(MetricService metricService) {
        this.metricService = metricService;
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
                System.out.println("\n[MetricPublisher][" + Thread.currentThread().getName() + "] Load Balancer Metrics...");

                Hashtable allMetrics = metricService.getMetrics();
                Enumeration keys = allMetrics.keys();
                while (keys.hasMoreElements()) {
                    String endpointKey = (String) keys.nextElement();
                    Hashtable endpointMetrics = (Hashtable) allMetrics.get(endpointKey);

                    Long success = (Long) endpointMetrics.get("SUCCESS_COUNT");
                    Long failure = (Long) endpointMetrics.get("FAILURE_COUNT");

                    long successCount = (success != null) ? success.longValue() : 0;
                    long failureCount = (failure != null) ? failure.longValue() : 0;

                    System.out.println("Endpoint " + endpointKey
                            + ": Successes=" + successCount
                            + ", Failures=" + failureCount
                    );
                }


            } catch (Exception e) {
                System.out.println("MetricPublisher: Unexpected error: " + e.getMessage());
            }
        }

    }
}
