package com.payroc.loadbalancer.handler;

import com.payroc.loadbalancer.handler.algorithm.Algorithm;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.EndpointRegistry;
import com.payroc.loadbalancer.monitor.MetricService;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ConnectionHandler implements Runnable {
    private Socket clientSocket;
    private EndpointRegistry registry;
    private Algorithm algorithm;
    private MetricService metricService;

    public ConnectionHandler(Socket clientSocket, EndpointRegistry registry, Algorithm algorithm, MetricService metricService) {
        this.clientSocket = clientSocket;
        this.registry = registry;
        this.algorithm = algorithm;
        this.metricService = metricService;
    }

    public void run() {
        Socket backendSocket = null;
        Endpoint selectedEndpoint = null;
        boolean connectionSucceeded = false;

        try {
            Vector availableEndpoints = registry.getAvailableEndpoints();
            selectedEndpoint = algorithm.getNextEndpoint(availableEndpoints);

            if (selectedEndpoint == null) {
                System.out.println("Handler: No available backend servers. Closing client.");
                return;
            }

            backendSocket = new Socket(selectedEndpoint.getHost(), selectedEndpoint.getPort());
            connectionSucceeded = true;

            System.out.println("Handler: Forwarding client " + clientSocket.getPort() + " to " + selectedEndpoint);

            Thread clientToBackend = new Thread(new Pipe(clientSocket.getInputStream(), backendSocket.getOutputStream()));
            Thread backendToClient = new Thread(new Pipe(backendSocket.getInputStream(), clientSocket.getOutputStream()));

            clientToBackend.start();
            backendToClient.start();

            clientToBackend.join();
            backendToClient.join();

        } catch (IOException e) {
            System.out.println("Handler: Connection error: " + e.getMessage());
            metricService.recordFailure(selectedEndpoint);
        } catch (InterruptedException e) {
            System.out.println("Handler: Pipe transfer interrupted");
        } finally {
            if (selectedEndpoint != null && connectionSucceeded) {
                metricService.recordSuccess(selectedEndpoint);
            }
            closeSocket(clientSocket);
            closeSocket(backendSocket);
        }
    }

    private void closeSocket(Socket s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
            }
        }
    }
}