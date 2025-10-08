package com.payroc.loadbalancer.handler.algorithm;

import com.payroc.loadbalancer.management.registry.Endpoint;

import java.util.Vector;

public interface Algorithm {

    Endpoint getNextEndpoint(Vector availableEndpoints);
}