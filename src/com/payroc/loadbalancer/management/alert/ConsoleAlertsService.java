package com.payroc.loadbalancer.management.alert;

public class ConsoleAlertsService implements AlertsService {

    public void sendAlert(String message) {
        System.err.println("*** ALERT: " + message);
    }
}