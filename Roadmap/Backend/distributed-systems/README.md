# Distributed Systems

The body of theory and practice for **building systems out of independent failing components**. Once a system spans more than one machine, the rules change — partial failure becomes the norm, time becomes ambiguous, and consistency stops being free.

> Content under this section is being filled in. The structure below shows the planned coverage; pages marked _coming soon_ link to themselves until written.

---

## Planned sections

- **[CAP & PACELC Theorems](01-cap-pacelc-theorems/)** — the foundational impossibility results; what partition tolerance actually costs and why "AP vs CP" is a simplification.
- **[Consensus](02-consensus/)** — Paxos, Raft, Multi-Paxos, ZAB; leader election, log replication, and the cost of agreement.
- **[Replication](03-replication/)** — single-leader, multi-leader, leaderless; synchronous vs asynchronous; replication lag and read-your-writes.
- **[Sharding](04-sharding/)** — partitioning strategies (range, hash, geographic); rebalancing; cross-shard queries and joins.
- **[Distributed Transactions](05-distributed-transactions/)** — 2PC and its descendants, Sagas, TCC, and why most "transactions" across services aren't really transactions.
- **[Event-Driven](06-event-driven/)** — event sourcing, CQRS, log-as-source-of-truth; choreography vs orchestration.
- **[Vector Clocks & CRDTs](07-vector-clocks-crdts/)** — capturing causality without a global clock; convergent and commutative data types for offline-tolerant systems.
- **[Service Mesh](08-service-mesh/)** — Istio, Linkerd; the data-plane vs control-plane split; mTLS, retries, circuit breaking moved out of application code.
- **[Resilience Patterns](09-resilience-patterns/)** — circuit breakers, bulkheads, timeouts, retries with jitter, hedged requests, backpressure.
- **[Distributed Tracing](10-distributed-tracing/)** — OpenTelemetry, span propagation, sampling strategies; making cross-service latency observable.

---

## Why this matters

Most failure modes in modern systems are distributed-systems failures wearing application clothing: a timeout that looked like a bug, a cache that lost coherence, a service that retried into a thundering herd, a "consistent" read that wasn't. The patterns in this roadmap give those failures names and standard cures.

---

## Related

- **[System Design](../../Architecture/system-design/)** — distributed-systems primitives assembled into recognisable architectures.
- **[Architecture Anti-Patterns](../../Architecture/anti-patterns/)** — Distributed Monolith, The Knot, Database-as-IPC — the failure modes distributed-systems discipline prevents.
- **[Backend → API Design](../api-design/)** — boundary contracts between services.
- **[Backend → Redis](../redis/)** — the most common building block for caching, queueing, and lightweight coordination.

---

## References

- **Designing Data-Intensive Applications** — Martin Kleppmann (2017) — the modern canonical reference; replication, consensus, stream processing in one book.
- **Database Internals** — Alex Petrov (2019) — storage engines and distributed-storage internals.
- **Distributed Systems** — Maarten van Steen & Andrew Tanenbaum (4th ed., 2023) — academic foundation.
- **Designing Distributed Systems** — Brendan Burns (2018) — patterns for containerised distributed systems.
- **The Tail at Scale** — Dean & Barroso (2013) — why latency variance dominates large fan-out systems.

---

## Project Context

Part of the [Senior Project](../../../index.md) — a personal effort to consolidate the essential knowledge of software engineering in one place.
