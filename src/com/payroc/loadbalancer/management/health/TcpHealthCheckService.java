package com.payroc.loadbalancer.management.health;

import com.payroc.loadbalancer.management.alert.AlertsService;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.EndpointRegistry;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class TcpHealthCheckService implements HealthCheckService {
    private final EndpointRegistry registry;
    private final AlertsService alertsService;
    private final long checkInterval = 5000; // 5 seconds
    private final Vector monitoredEndpoints;
    private final Timer timer;

    public TcpHealthCheckService(EndpointRegistry registry, AlertsService alertsService) {
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
            try {
                Vector availableEndpoints = (Vector) registry.getAvailableEndpoints();

                for (int i = 0; i < monitoredEndpoints.size(); i++) {
                    Endpoint endpoint = (Endpoint) monitoredEndpoints.elementAt(i);
                    boolean isReachable = checkEndpoint(endpoint.getHost(), endpoint.getPort());

                    if (endpoint.isAlive() && !isReachable) {
                        endpoint.setAlive(false);
                        if (availableEndpoints.contains(endpoint)) {
                            registry.removeEndpoint(endpoint);
                            alertsService.sendAlert("Backend DOWN: " + endpoint);
                        }
                    } else if (!endpoint.isAlive() && isReachable) {
                        endpoint.setAlive(true);
                        if (!availableEndpoints.contains(endpoint)) {
                            registry.addEndpoint(endpoint);
                            alertsService.sendAlert("Backend RECOVERED: " + endpoint);
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