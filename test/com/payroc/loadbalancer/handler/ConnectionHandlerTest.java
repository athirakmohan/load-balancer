package com.payroc.loadbalancer.handler;

import com.payroc.loadbalancer.handler.algorithm.Algorithm;
import com.payroc.loadbalancer.management.registry.Endpoint;
import com.payroc.loadbalancer.management.registry.EndpointRegistry;
import com.payroc.loadbalancer.monitor.MetricService;
import junit.framework.TestCase;

import java.net.Socket;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

public class ConnectionHandlerTest extends TestCase {

    private EndpointRegistry registry;
    private Algorithm algorithm;
    private MetricService metrics;

    public void setUp() {

        registry = new EndpointRegistry() {
            private Vector endpoints = new Vector();

            public Vector getAvailableEndpoints() {
                return (Vector) endpoints.clone();
            }

            public void addEndpoint(Endpoint endpoint) {
                endpoints.addElement(endpoint);
            }

            public void removeEndpoint(Endpoint e) {
                endpoints.removeElement(e);
            }
        };

        Endpoint e1 = new Endpoint("127.0.0.1", 9001);
        registry.addEndpoint(e1);

        algorithm = new Algorithm() {
            public Endpoint getNextEndpoint(Vector availableEndpoints) {
                return (Endpoint) availableEndpoints.elementAt(0);
            }
        };

        metrics = new MetricService() {
            public void recordSuccess(Endpoint e) {
                System.out.println("Success recorded for " + e);
            }

            public void recordFailure(Endpoint e) {
                System.out.println("Failure recorded for " + e);
            }

            public Hashtable getMetrics() {
                return null;
            }
        };
    }

    public void testConnectionHandlerWithDummySockets() throws Exception {

        PipedInputStream clientInput = new PipedInputStream();
        PipedOutputStream clientOutput = new PipedOutputStream(clientInput);

        Socket dummyClient = new DummySocket(clientInput, clientOutput);

        ConnectionHandler handler = new ConnectionHandler(dummyClient, registry, algorithm, metrics);

        Thread t = new Thread(handler);
        t.start();
        t.join();
    }

    class DummySocket extends Socket {
        private InputStream in;
        private OutputStream out;

        public DummySocket(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        public InputStream getInputStream() {
            return in;
        }

        public OutputStream getOutputStream() {
            return out;
        }

        public void close() {
        }
    }
}
