# 🟠 Senior Level (501–750)

[← Middle](./02-middle-level.md) · [README](./README.md) · [Professional →](./04-professional-level.md)

> **Fokus:** Distributed systems chuqurligi, geo-distribution, advanced data modeling, complex system designs (Uber, Netflix-scale), trade-off justification, ambiguous requirements, real-time pipelines, advanced consistency.
>
> **Kim uchun:** 5–8 yil tajriba, senior/L5 darajadagi engineer.
> **Vaqt/savol:** 45–60 daqiqa, trade-off justification yozing.

---

## 🧬 Advanced Distributed Systems (501–540)

501. Walk through Raft consensus algorithm step by step.
502. Walk through Paxos consensus algorithm step by step.
503. Compare Raft, Paxos, ZAB, and Multi-Paxos.
504. What is a quorum-based read/write protocol (e.g., Dynamo)?
505. What is hinted handoff in Cassandra/Dynamo?
506. What is read repair?
507. What is anti-entropy and Merkle trees?
508. What is gossip protocol?
509. What is a vector clock and why use it?
510. What is a Lamport timestamp?
511. What is a hybrid logical clock (HLC)?
512. What is a CRDT and what types exist (G-counter, OR-Set, LWW)?
513. How would you build a collaborative editor with CRDTs vs OT?
514. What is operational transformation (OT)?
515. What is a vector version (causal) vs a wall clock?
516. What is the difference between a leader-based and leaderless replication?
517. What is "leader stickiness" and when does it hurt?
518. What is a witness replica?
519. What is chain replication?
520. What is primary-backup replication?
521. What is the FLP impossibility result?
522. What is the Two Generals problem?
523. What is the Byzantine Generals problem?
524. What is BFT consensus (PBFT, Tendermint)?
525. What is a distributed snapshot (Chandy-Lamport)?
526. What is Spanner's TrueTime and how does it enable external consistency?
527. How does Google Spanner achieve strong consistency at global scale?
528. How does CockroachDB implement Spanner-like consistency without atomic clocks?
529. How does DynamoDB handle global tables?
530. How does Cassandra handle multi-datacenter replication?
531. What are the trade-offs of synchronous cross-region writes?
532. What is the role of a meta-data service (e.g., ZooKeeper)?
533. How would you build a distributed lock service?
534. How does etcd implement leader leases?
535. How would you implement leader election with Redis (Redlock controversy)?
536. What is the split-brain problem in leader election and how to avoid it?
537. What are fence tokens and why are they needed for distributed locks?
538. What is the difference between a strong leader and a weak leader?
539. What is leader piggy-backing for heartbeats?
540. Why is "exactly-once" actually "effectively-once with idempotency"?

## 🌐 Geo-Distribution & Multi-Region (541–570)

541. How do you architect for multi-region active-active?
542. How do you architect for multi-region active-passive?
543. What is RPO vs RTO?
544. How do you replicate data across continents with bounded lag?
545. How do you handle time-zone-sensitive workloads globally?
546. What is "read local, write global" pattern?
547. What is "follow-the-sun" architecture?
548. How do you reroute traffic during a regional failover?
549. What is GeoDNS and what are its limits?
550. How do you implement consistent global IDs (Snowflake, ULID)?
551. How does Twitter Snowflake assign IDs?
552. What are the trade-offs of using a centralized ID service vs local IDs?
553. How would you design a globally distributed counter?
554. How would you implement a globally distributed rate limiter?
555. What is the cost of cross-region writes in terms of latency?
556. How would you replicate a write-heavy timeline to multiple regions?
557. What is a follower read in Spanner-style systems?
558. How do you handle clock skew across regions?
559. What is the role of NTP/PTP in distributed systems?
560. How would you design a multi-region disaster recovery plan?
561. What is chaos failover testing across regions?
562. How do you test for region-failover correctness?
563. What is "graceful brownout" of a region?
564. What is Anycast routing for global services?
565. How does CloudFront/Akamai route a user to the nearest PoP?
566. How does multi-region S3 replication work?
567. How would you serve dynamic content from the edge (Cloudflare Workers)?
568. What is the "eventual reachability" property in geo-distributed messaging?
569. How would you design a global chat with read-after-write consistency?
570. How do you handle data residency / data sovereignty laws?

## 📈 Scalability Deep Dive (571–610)

571. How would you scale a "likes" counter to handle 1M writes/sec?
572. How would you scale a feed-fanout system for 100M users?
573. Compare push vs pull vs hybrid fanout for a social feed.
574. How would you handle the "celebrity problem" in fanout?
575. How would you build a typeahead serving 100K QPS?
576. How would you scale a notification system to 1B users?
577. How would you scale a real-time leaderboard to 10M players?
578. Walk through scaling MySQL from 1 to 10M users.
579. Walk through scaling Postgres for write-heavy workload.
580. How would you scale a write-heavy time-series system (IoT)?
581. How would you build a metrics ingestion system at the scale of Datadog?
582. How would you architect a logs pipeline at the scale of Splunk?
583. How would you design Pinterest's image pipeline?
584. How would you design Instagram's feed for 500M MAU?
585. How would you scale a chat system to 100M concurrent connections?
586. How would you architect WhatsApp's E2E messaging?
587. How would you design a global content moderation pipeline?
588. How would you scale Stripe's payment ledger?
589. How would you design Plaid-style financial data aggregation?
590. How would you design Uber's geo-index (S2/H3)?
591. How would you design Lyft's matching engine?
592. How would you design DoorDash's dispatch service?
593. How would you scale Airbnb's calendar / availability service?
594. How would you scale Booking.com's pricing engine?
595. How would you design Amazon's "prime now" inventory service?
596. How would you architect a TikTok-scale recommendation feed?
597. How would you design YouTube's view counter?
598. How would you scale Reddit's voting/comments?
599. How would you scale a video transcoding pipeline?
600. How would you build a CDN like Cloudflare from scratch?
601. How would you design a content moderation queue at scale?
602. How would you build a fraud detection pipeline at scale?
603. How would you design a real-time bidding (RTB) ad-auction system?
604. How would you design Google AdSense at high level?
605. How would you scale an analytics dashboard for billions of events?
606. How would you design a multi-tenant log search (per-customer isolation)?
607. How would you re-architect a monolith handling 50K RPS into microservices?
608. How would you remove a single hot DB shard during peak traffic?
609. How would you design a system to handle Black Friday spikes?
610. How would you handle a 100x traffic spike with no advance warning?

## 🧰 Advanced Data Pipelines (611–640)

611. Compare Lambda vs Kappa architecture.
612. When would you avoid Lambda architecture?
613. How would you design a real-time analytics dashboard?
614. How would you reconcile real-time and batch metrics?
615. How would you build a near-real-time feature store?
616. What is Apache Beam and where does it fit?
617. What is exactly-once in Flink and how is it implemented?
618. What is Flink checkpointing and savepointing?
619. What is Spark structured streaming vs DStream?
620. What is data lake vs data warehouse vs lakehouse?
621. How would you architect an Iceberg/Delta lakehouse?
622. What is medallion architecture (bronze/silver/gold)?
623. What is reverse ETL?
624. What is CDC (Debezium) at scale?
625. What is the outbox pattern combined with Debezium?
626. What is Kafka tiered storage?
627. What is Pulsar and how does it differ from Kafka?
628. What is the role of schema registry?
629. How do you handle schema evolution across producers and consumers?
630. How would you design a clickstream pipeline (Snowplow-like)?
631. How would you architect an A/B testing platform end-to-end?
632. How would you design an experiment platform with metrics + guardrails?
633. How would you build a marketing event funnel pipeline?
634. How would you build an attribution pipeline (multi-touch)?
635. How would you architect a search-relevance feedback loop?
636. How would you architect a vector search system (RAG-friendly)?
637. How would you architect a real-time anomaly detector?
638. How would you architect an ML feature pipeline (offline + online)?
639. How would you architect a model-serving platform with shadow traffic?
640. How would you architect a cost-aware data lifecycle (hot/warm/cold tiers)?

## 🎯 Trade-offs & Architecture Choices (641–680)

641. When would you pick microservices over a modular monolith and vice versa?
642. When would you pick GraphQL over REST?
643. When would you pick gRPC over REST/GraphQL?
644. When would you pick eventual consistency over strong consistency?
645. When would you pick at-least-once over at-most-once?
646. When is exactly-once worth the cost?
647. When would you build vs buy?
648. When is a SaaS DB (Snowflake) better than self-managed Postgres?
649. When is Postgres "good enough" instead of a NoSQL DB?
650. When is JSON in Postgres better than Mongo?
651. When is a bus (Kafka) better than a request/response API?
652. When is HTTP/3 worth deploying?
653. When does a service mesh add too much overhead?
654. When is server-side rendering better than client-side?
655. When is Redis a bad choice as a primary store?
656. When does sharding become unavoidable?
657. When is denormalization the right call?
658. When should you avoid microservices entirely?
659. When is a monolith actually the better long-term choice?
660. Trade-offs of synchronous vs asynchronous APIs?
661. Trade-offs of REST vs event-driven?
662. Trade-offs of optimistic vs pessimistic locking at scale?
663. Trade-offs of JWT vs opaque tokens?
664. Trade-offs of JWT vs session in distributed services?
665. Trade-offs of leader-based vs leaderless replication?
666. Trade-offs of per-tenant DB vs shared DB?
667. Trade-offs of synchronous vs asynchronous replication?
668. Trade-offs of B-tree vs LSM-tree storage engines?
669. Trade-offs of row-store vs column-store?
670. Trade-offs of row-level vs document-level versioning?
671. Trade-offs of soft deletes vs hard deletes?
672. Trade-offs of in-DB vs out-of-DB joins?
673. Trade-offs of schema-on-read vs schema-on-write?
674. Trade-offs of async fanout vs synchronous notification?
675. Trade-offs of pre-computing aggregates vs computing on read?
676. Trade-offs of caching at edge vs at origin?
677. Trade-offs of self-hosted Kafka vs MSK/Confluent?
678. Trade-offs of using ELB vs nginx vs Envoy?
679. Trade-offs of using a queue vs a stream?
680. Trade-offs of static partitioning vs dynamic partitioning?

## 🔐 Advanced Security (681–710)

681. How do you design end-to-end encryption (Signal protocol)?
682. How do you implement key rotation at scale?
683. How does Zero Trust architecture work?
684. How would you design SSO with OIDC across services?
685. What is mTLS and how do you operate it at scale?
686. How would you design secret management (HashiCorp Vault patterns)?
687. How do you handle PII in a logging pipeline?
688. How do you design field-level encryption for a SaaS DB?
689. How do you implement audit logs that are tamper-evident?
690. How do you implement data redaction for support staff?
691. How do you design a permission system (RBAC vs ABAC vs ReBAC)?
692. How would you design a Google Zanzibar-style authorization service?
693. How do you implement API rate limiting per tenant?
694. How do you mitigate DDoS at the edge?
695. How do you implement a WAF with a CDN provider?
696. How do you design account takeover prevention?
697. How do you design device fingerprinting?
698. How do you implement OAuth token revocation?
699. How do you implement secure cookie strategies (HttpOnly, SameSite, Secure)?
700. How do you implement CSP for a complex SaaS app?
701. What is supply-chain security and how do you mitigate (SLSA, sigstore)?
702. How do you handle CVE response across hundreds of microservices?
703. What is a HSM and when do you need one?
704. How do you implement client-side encryption for S3 data?
705. How do you design a data lake with column-level access control?
706. How do you handle GDPR right-to-be-forgotten across services?
707. How do you design SOC2-compliant audit pipelines?
708. How do you implement HIPAA-grade auditing?
709. How do you securely share data with third parties (S3 presigned, JIT access)?
710. How do you design a bug bounty / responsible disclosure intake system?

## 🧪 Performance & Internals (711–740)

711. Walk through what happens during a Postgres query (parser → planner → executor).
712. How does Postgres VACUUM work and why is it necessary?
713. How does PostgreSQL handle MVCC tuple bloat?
714. What is a heap-only tuple (HOT) update?
715. How does MySQL InnoDB clustered index differ from Postgres heap?
716. What is the LSM tree and how does compaction work?
717. How does Cassandra's read path work (memtable, SSTable, bloom filter)?
718. How does Bloom filter improve read performance?
719. What is a Cuckoo filter vs Bloom filter?
720. How does B+ tree differ from B-tree?
721. What is fractal tree?
722. What is the Linux page cache and how does it interact with DB?
723. What is "fsync" and how does it affect durability?
724. What is a journaling file system?
725. What is RDMA and when does it matter?
726. What is io_uring and what problem does it solve?
727. What is zero-copy networking?
728. What is kernel bypass (DPDK, XDP)?
729. How does TCP slow start and congestion control affect tail latency?
730. What is BBR vs CUBIC congestion control?
731. How does QUIC improve over TCP?
732. What is the cost of DNS lookups in microservices?
733. What is JIT and how does V8/JVM warm up affect deployment?
734. What is JVM GC tuning (G1 vs ZGC vs Shenandoah)?
735. What is Go GC behavior under high allocation rate?
736. What is heap fragmentation and how to mitigate?
737. What is profiling vs tracing vs sampling?
738. What is flame graph and how to read it?
739. What is the role of perf and eBPF in production diagnostics?
740. How would you debug a production-only memory leak?

## 🧯 Failure & Recovery (741–750)

741. How would you respond to a global outage of a critical dependency?
742. How would you handle a runaway query taking down the DB?
743. How do you design a "kill switch" for a feature?
744. How do you handle a cache layer outage gracefully?
745. How do you handle a write outage when reads must continue?
746. What is circuit-breaker fallback strategy for payments?
747. How do you design a degraded-read mode?
748. How do you design a degraded-write mode?
749. How do you safely roll back a schema change that already replicated?
750. How do you design a "fire drill" / disaster simulation calendar?

---

[← Middle](./02-middle-level.md) · [README](./README.md) · [Professional Level →](./04-professional-level.md)
