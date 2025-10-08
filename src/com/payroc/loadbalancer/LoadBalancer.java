package com.payroc.loadbalancer;

import com.payroc.loadbalancer.handler.ConnectionHandler;
import com.payroc.loadbalancer.handler.algorithm.Algorithm;
import com.payroc.loadbalancer.handler.algorithm.RoundRobinAlgorithm;
import com.payroc.loadbalancer.management.alert.AlertsService;
import com.payroc.loadbalancer.management.alert.ConsoleAlertsService;
import com.payroc.loadbalancer.management.health.HealthCheckService;
import com.payroc.loadbalancer.management.health.TcpHealthCheckService;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.EndpointRegistry;
import com.payroc.loadbalancer.management.registry.InMemoryEndpointRegistry;
import com.payroc.loadbalancer.monitor.*;
import com.payroc.loadbalancer.util.ParseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class LoadBalancer {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Provide at least one endpoint as host:port");
            System.exit(1);
        }
        int listenPort = ParseUtil.parseListenPort(args[0]);
        EndpointRegistry registry = initializeRegistry(args);

        Algorithm algorithm = new RoundRobinAlgorithm();
        AlertsService alertsService = new ConsoleAlertsService();

        HealthCheckService healthChecker = new TcpHealthCheckService(registry, alertsService);
        healthChecker.start();

        MetricPublisher consolePublisher = new ConsoleMetricPublisher();
        MetricService metricService = new InMemoryMetricService();
        MetricReportScheduler reporter = new MetricReportScheduler(metricService, consolePublisher);
        reporter.start();

        System.out.println("LoadBalancer: Starting up on port " + listenPort);

        try {
            ServerSocket serverSocket = new ServerSocket(listenPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nLoadBalancer: Client connected from port " + clientSocket.getPort());

                ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, registry, algorithm, metricService);
                Thread handlerThread = new Thread(connectionHandler);
                handlerThread.start();
            }

        } catch (IOException e) {
            System.err.println("LoadBalancer: error on server socket: " + e.getMessage());
        } finally {
            reporter.stop();
            healthChecker.stop();
            System.out.println("LoadBalancer: Shut down");
        }
    }

    private static EndpointRegistry initializeRegistry(String[] args) {
        Vector endpoints = ParseUtil.parseEndpoints(args, 1);
        EndpointRegistry registry = new InMemoryEndpointRegistry();
        for (int i = 0; i < endpoints.size(); i++) {
            registry.addEndpoint((Endpoint) endpoints.elementAt(i));
        }
        return registry;
    }


}