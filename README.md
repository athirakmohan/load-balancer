Load Balancer (1999-style)

This project implements a basic Layer 4 load balancer with the following capabilities:

Accepts connections from multiple clients.

Balances traffic across multiple backend services using a round-robin approach.

Monitors backend availability and automatically removes or restores servers based on health.

Written in classic Java style, it uses Vector, Hashtable, Thread, and Timer to manage endpoints, metrics, and health checks, without relying on any cloud services.

High level architecture 
<img width="850" height="675" alt="Screenshot 2025-10-08 at 17 54 53" src="https://github.com/user-attachments/assets/c8e5d40a-dda3-4b04-bcb1-c73df4b9b872" />


Features

Round-robin load balancing for distributing requests.

In-memory endpoint registry (Vector, Hashtable, Enumeration – classic Java 1.2 style).

Health check service that monitors backend availability and sends alerts when endpoints go down or recover.

Connection metrics to tracks successful and failed connections.

Configurable endpoints at runtime via command-line arguments.

Requirements

Java 1.2+ compatible (can run on modern JDKs as well).
Need Junit lib

Run the load balancer with endpoints:

java -cp out com.payroc.loadbalancer.Main 127.0.0.1:9001 127.0.0.1:9002 127.0.0.1:9003

Usage

Adding endpoints: Pass them as command-line arguments in host:port format.

Health checks: Performed every 5 seconds. Alerts logged to console.

Metrics: Connection successes and failures printed periodically.
