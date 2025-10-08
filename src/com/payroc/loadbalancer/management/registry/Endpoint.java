package com.payroc.loadbalancer.management.registry;

public class Endpoint {
    private String host;
    private int port;
    private boolean isAlive;

    public Endpoint(String host, int port) {
        this.host = host;
        this.port = port;
        this.isAlive = true;
    }

    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Endpoint) {
            Endpoint other = (Endpoint) obj;
            return host.equals(other.host) && (port == other.port);
        }
        return false;
    }

    public int hashCode() {
        return host.hashCode() + port;
    }

    public String toString() {
        return host + ":" + port;
    }
}