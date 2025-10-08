package com.payroc.loadbalancer.util;

import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Vector;

public class ParseUtil {

    public static int parseListenPort(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.err.println("Invalid listen port: " + arg);
            System.exit(1);
            return -1;
        }
    }

    public static Vector parseEndpoints(String[] args, int startIndex) {
        Vector endpoints = new Vector();
        for (int i = startIndex; i < args.length; i++) {
            String arg = args[i];
            String[] parts = splitHostPort(arg);
            if (parts == null) continue;

            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            endpoints.addElement(new Endpoint(host, port));
        }
        return endpoints;
    }

    public static String[] splitHostPort(String arg) {
        int colonIndex = arg.indexOf(':');
        if (colonIndex == -1) {
            System.err.println("Invalid endpoint format: " + arg + ". Expected host:port");
            return null;
        }
        String host = arg.substring(0, colonIndex);
        String portStr = arg.substring(colonIndex + 1);
        try {
            Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number in endpoint: " + arg);
            return null;
        }
        return new String[]{host, portStr};
    }
}

