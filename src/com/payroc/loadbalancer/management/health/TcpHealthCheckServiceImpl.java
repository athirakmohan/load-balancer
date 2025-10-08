package com.payroc.loadbalancer.management.health;

import com.payroc.loadbalancer.management.alert.AlertsService;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.EndpointRegistry;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class TcpHealthCheckServiceImpl implements HealthCheckService {
    private EndpointRegistry registry;
    private AlertsService alertsService;
    private long checkInterval = 5000; // 5 seconds
    private Vector monitoredEndpoints;
    private final Timer timer;

    public TcpHealthCheckServiceImpl(EndpointRegistry registry, AlertsService alertsService) {
        this.registry = registry;
        this.alertsService = alertsService;
        monitoredEndpoints = registry.getAvailableEndpoints();
        this.timer = new Timer(true);
    }

    public void start() {
        System.out.println("HealthCheck: Starting periodic check");
        timer.schedule(new HealthCheckTask(), 0, checkInterval);
    }

    public void stop() {
        System.out.println("HealthCheck: Stopping ");
        timer.cancel();
    }
    private class HealthCheckTask extends TimerTask {
        public void run() {
            System.out.println("\n[HealthCheck][" + Thread.currentThread().getName() + "] Checking backend health...");

            try {
                Vector availableEndpoints = (Vector) registry.getAvailableEndpoints();

                for (int i = 0; i < monitoredEndpoints.size(); i++) {
                    Endpoint e = (Endpoint) monitoredEndpoints.elementAt(i);
                    boolean isReachable = checkEndpoint(e.getHost(), e.getPort());

                    if (e.isAlive() && !isReachable) {
                        e.setAlive(false);
                        if (availableEndpoints.contains(e)) {
                            registry.removeEndpoint(e);
                            alertsService.sendAlert("Backend DOWN: " + e);
                        }
                    } else if (!e.isAlive() && isReachable) {
                        e.setAlive(true);
                        if (!availableEndpoints.contains(e)) {
                            registry.addEndpoint(e);
                            alertsService.sendAlert("Backend RECOVERED: " + e);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("HealthCheck: Unexpected error: " + ex.getMessage());
            }
        }
    }


    private boolean checkEndpoint(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException exception) {

                }
            }
        }
    }
}