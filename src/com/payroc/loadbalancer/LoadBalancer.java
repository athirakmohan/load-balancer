package com.payroc.loadbalancer;

import com.payroc.loadbalancer.handler.ConnectionHandler;
import com.payroc.loadbalancer.handler.algorithm.Algorithm;
import com.payroc.loadbalancer.handler.algorithm.RoundRobinAlgorithm;
import com.payroc.loadbalancer.management.alert.AlertsService;
import com.payroc.loadbalancer.management.alert.ConsoleAlertsService;
import com.payroc.loadbalancer.management.health.HealthCheckService;
import com.payroc.loadbalancer.management.health.TcpHealthCheckServiceImpl;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.EndpointRegistry;
import com.payroc.loadbalancer.management.registry.InMemoryEndpointRegistryImpl;
import com.payroc.loadbalancer.monitor.ConsoleMetricService;
import com.payroc.loadbalancer.monitor.MetricPublisher;
import com.payroc.loadbalancer.monitor.MetricService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    private static final int LISTEN_PORT = 8080;

    public static void main(String[] args) {

        EndpointRegistry registry = new InMemoryEndpointRegistryImpl();
        Algorithm algorithm = new RoundRobinAlgorithm();
        MetricService metricService = new ConsoleMetricService();
        AlertsService alertsService = new ConsoleAlertsService();

        registry.addEndpoint(new Endpoint("127.0.0.1", 9001));
        registry.addEndpoint(new Endpoint("127.0.0.1", 9002));
        registry.addEndpoint(new Endpoint("127.0.0.1", 9003));

        HealthCheckService healthChecker = new TcpHealthCheckServiceImpl(registry, alertsService);
        healthChecker.start();

        MetricPublisher publisher = new MetricPublisher(metricService);
        publisher.start();

        System.out.println("LoadBalancer: Starting up on port " + LISTEN_PORT);

        try {
            ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);

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
            publisher.stop();
            healthChecker.stop();
            System.out.println("LoadBalancer: Shut down");
        }
    }
}