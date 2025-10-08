**Load Balancer (1999-style)**

This project implements a basic Layer 4 load balancer with the following capabilities:

Accepts connections from multiple clients.

Balances traffic across multiple backend services using a round-robin approach.

Monitors backend availability and automatically removes or restores servers based on health.

Written in classic Java style, it uses Vector, Hashtable, Thread, and Timer to manage endpoints, metrics, and health
checks, without relying on any cloud services.

**High level architecture**
<img width="850" height="675" alt="Screenshot 2025-10-08 at 17 54 53" src="https://github.com/user-attachments/assets/c8e5d40a-dda3-4b04-bcb1-c73df4b9b872" />

**Features**

Round-robin load balancing for distributing requests.

In-memory endpoint registry (Vector, Hashtable, Enumeration – classic Java 1.2 style).

Health check service that monitors backend availability and sends alerts when endpoints go down or recover.

Connection metrics to tracks successful and failed connections.

Configurable endpoints at runtime via command-line arguments.

**Requirements**

Java 1.2+ compatible (can run on modern JDKs as well).

**Run the load balancer**

java -cp out com.payroc.loadbalancer.LoadBalancer <listen-port> <endpoint1> [endpoint2] [endpoint3] ...

<listen-port> → The port on which the load balancer should listen for client connections.

<endpointX> → Backend endpoints in host:port format. You can specify one or more endpoints.

**Architectural improvements**
Below are the improvements that can be done to optimise the solution within JDK 1.2 support

- Replace per client thread creation with a manually implemented worker thread pool using a synchronized task queue and
  a fixed set of long-lived worker threads.
- Extend the Algorithm interface to add LeastConnections, Weighted Round Robin, or other strategies without changing
  core proxy logic.
- Support to dynamically register new backend endpoints at runtime, either with console interface or with file-based
  polling as an optional alternative.
- Use an event driven approach for metrics, health and alerts where counters are updated immediately and reporting can
  be done in batches .This ensures alerts can fire soon for critical ones.
- Enhance resilience with timeouts, failover endpoints, retries, request queuing, circuit breaking, rate limiting and
  load aware routing.
- Extend the registry to handle named service pools
- Enhanced logging instead of using System.out.println.


