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
import com.payroc.loadbalancer.monitor.ConsoleConnectionMetricService;
import com.payroc.loadbalancer.monitor.ConnectionMetricPublisher;
import com.payroc.loadbalancer.monitor.ConnectionMetricService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    private static final int LISTEN_PORT = 8080;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Provide at least one endpoint as host:port");
            System.exit(1);
        }
        EndpointRegistry registry = new InMemoryEndpointRegistry();
        addEndpointsToRegistry(args, registry);

        Algorithm algorithm = new RoundRobinAlgorithm();
        ConnectionMetricService connectionMetricService = new ConsoleConnectionMetricService();
        AlertsService alertsService = new ConsoleAlertsService();

        HealthCheckService healthChecker = new TcpHealthCheckService(registry, alertsService);
        healthChecker.start();

        ConnectionMetricPublisher publisher = new ConnectionMetricPublisher(connectionMetricService);
        publisher.start();

        System.out.println("LoadBalancer: Starting up on port " + LISTEN_PORT);

        try {
            ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nLoadBalancer: Client connected from port " + clientSocket.getPort());

                ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, registry, algorithm, connectionMetricService);
                Thread handlerThread = new Thread(connectionHandler);
                handlerThread.start();
            }

        } catch (IOException e) {
            System.err.println("LoadBalancer: error on server socket: " + e.getMessage());
        } finally {
            publisher.stop();
            healthChecker.stop();
            System.out.println("LoadBalancer: Shut down");
        }
    }

    private static void addEndpointsToRegistry(String[] args, EndpointRegistry registry) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String[] parts = arg.split(":");
            if (parts.length != 2) {
                System.err.println("Invalid endpoint format: " + arg + ". Expected host:port");
                continue;
            }
            String host = parts[0];
            int port;
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number in endpoint: " + arg);
                continue;
            }
            registry.addEndpoint(new Endpoint(host, port));
        }
    }
}