# System Design Roadmap

- Roadmap: https://roadmap.sh/system-design

## 1. Introduction
- 1.1 What is System Design?
- 1.2 How to approach System Design?



### 2.3 Performance vs Scalability

### 2.4 Latency vs Throughput

### 2.5 Availability vs Consistency
- 2.5.1 CAP Theorem
- 2.5.1.1 AP - Availability + Partition Tolerance
- 2.5.1.2 CP - Consistency + Partition Tolerance
  
### 2.6 Consistency Patterns
- 2.6.1 Weak Consistency
- 2.6.2 Eventual Consistency
- 2.6.3 Strong Consistency

### 2.7 Availability Patterns
- 2.7.1 Fail-Over
  - 2.7.1.1 Active - Active
  - 2.7.1.2 Active - Passive
- 2.7.2 Replication
  - 2.7.2.1 Master - Slave
  - 2.7.2.2 Master - Master
- 2.7.3 Availability in Numbers
  - 2.7.3.1 99.9% Availability - three 9s
  - 2.7.3.2 99.99% Availability - four 9s
  - 2.7.3.3 Availability in Parallel vs Sequence

## 3. Background Jobs
- 3.1 Event-Driven
- 3.2 Schedule Driven
- 3.3 Returning Results

## 4. Domain Name System

## 5. Content Delivery Networks
- 5.1 Pull CDNs
- 5.2 Push CDNs

## 6. Load Balancers
- 6.1 LB vs Reverse Proxy
- 6.2 Load Balancing Algorithms
- 6.3 Layer 7 Load Balancing
- 6.4 Layer 4 Load Balancing

### 6.5 Horizontal Scaling

## 7. Application Layer
- 7.1 Microservices
- 7.2 Service Discovery

## 8. Databases
- 8.1 Key-Value Store
- 8.2 Document Store
- 8.3 Wide Column Store
- 8.4 Graph Databases
- 8.5 NoSQL
- 8.6 RDBMS
- 8.7 Replication
- 8.8 Sharding
- 8.9 Federation
- 8.10 Denormalization
- 8.11 SQL Tuning

### 8.12 SQL vs NoSQL

## 9. Caching
- 9.1 Refresh Ahead
- 9.2 Write-behind
- 9.3 Write-through
- 9.4 Cache Aside

### 9.5 Strategies

### 9.6 Types of Caching
- 9.6.1 Client Caching
- 9.6.2 CDN Caching
- 9.6.3 Web Server Caching
- 9.6.4 Database Caching
- 9.6.5 Application Caching

## 10. Asynchronism
- 10.1 Back Pressure
- 10.2 Task Queues
- 10.3 Message Queues

## 11. Communication
- 11.1 HTTP
- 11.2 TCP
- 11.3 UDP
- 11.4 RPC
- 11.5 gRPC
- 11.6 REST
- 11.7 GraphQL

### 11.8 Idempotent Operations

## 12. Performance Antipatterns
- 12.1 Improper Instantiation
- 12.2 Monolithic Persistence
- 12.3 Noisy Neighbor
- 12.4 Synchronous I/O
- 12.5 Extraneous Fetching
- 12.6 Busy Database
- 12.7 Busy Frontend
- 12.8 Chatty I/O
- 12.9 Retry Storm
- 12.10 No Caching

## 13. Monitoring
- 13.1 Health Monitoring
- 13.2 Availability Monitoring
- 13.3 Performance Monitoring
- 13.4 Security Monitoring
- 13.5 Usage Monitoring
- 13.6 Instrumentation
- 13.7 Visualization & Alerts

## 14. Cloud Design Patterns

### 14.1 Design & Implementation
- 14.1.1 Strangler Fig
- 14.1.2 Sidecar
- 14.1.3 Static Content Hosting
- 14.1.4 Leader Election
- 14.1.5 CQRS
- 14.1.6 Pipes & Filters
- 14.1.7 Ambassador
- 14.1.8 Gateway Routing
- 14.1.9 Gateway Offloading
- 14.1.10 Gateway Aggregation
- 14.1.11 External Config Store
- 14.1.12 Compute Resource Consolidation
- 14.1.13 Backends for Frontend
- 14.1.14 Anti-Corruption Layer

### 14.2 Data Management
- 14.2.1 Valet Key
- 14.2.2 Static Content Hosting
- 14.2.3 Sharding
- 14.2.4 Materialized View
- 14.2.5 Index Table
- 14.2.6 Event Sourcing
- 14.2.7 CQRS
- 14.2.8 Cache-Aside

### 14.3 Messaging
- 14.3.1 Sequential Convoy
- 14.3.2 Scheduling Agent Supervisor
- 14.3.3 Queue-based Load Leveling
- 14.3.4 Publisher/Subscriber
- 14.3.5 Priority Queue
- 14.3.6 Pipes and Filters
- 14.3.7 Competing Consumers
- 14.3.8 Choreography
- 14.3.9 Claim Check
- 14.3.10 Async Request Reply

## 15. Reliability Patterns

### 15.1 Availability
- 15.1.1 Deployment Stamps
- 15.1.2 Geodes
- 15.1.3 Throttling
- 15.1.4 Health Endpoint Monitoring
- 15.1.5 Queue-Based Load Leveling

### 15.2 High Availability
- 15.2.1 Deployment Stamps
- 15.2.2 Geodes
- 15.2.3 Bulkhead
- 15.2.4 Health Endpoint Monitoring
- 15.2.5 Circuit Breaker

### 15.3 Resiliency
- 15.3.1 Bulkhead
- 15.3.2 Circuit Breaker
- 15.3.3 Compensating Transaction
- 15.3.4 Health Endpoint Monitoring
- 15.3.5 Leader Election
- 15.3.6 Queue-Based Load Leveling
- 15.3.7 Retry
- 15.3.8 Scheduler Agent Supervisor

### 15.4 Security
- 15.4.1 Federated Identity
- 15.4.2 Gatekeeper
- 15.4.3 Valet Key
