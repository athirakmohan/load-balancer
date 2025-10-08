package com.payroc.loadbalancer.monitor;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class MetricReportScheduler {
    private final Timer timer;
    private final MetricService metricService;
    private final MetricPublisher metricPublisher;
    private final long reportInterval = 30000; // 30 seconds

    public MetricReportScheduler(MetricService metricService, MetricPublisher metricPublisher) {
        this.metricService = metricService;
        this.metricPublisher = metricPublisher;
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

                Hashtable allMetrics = metricService.getMetrics();
                if (allMetrics != null && !allMetrics.isEmpty()) {
                    metricPublisher.publish(allMetrics);
                }

            } catch (Exception e) {
                System.out.println("MetricPublisher: Unexpected error: " + e.getMessage());
            }
        }

    }
}
