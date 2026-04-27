# 🔴 Professional Level (751–1000)

[← Senior](./03-senior-level.md) · [README](./README.md) · [Sources →](./05-sources.md)

> **Fokus:** Staff/Principal/Distinguished darajadagi savollar — yillar bo'yicha o'ylash, organizatsion architecture, build-vs-buy, cost optimization, complex migrations, internals of databases & runtimes, technical leadership, RFC processes, cross-team trade-offs.
>
> **Kim uchun:** Staff/Principal/L6+ engineer.
> **Vaqt/savol:** 1–2 soat, RFC formatida yozma javob tayyorlang.

---

## 🎖️ Staff+ Architecture & Leadership (751–790)

751. How would you write a 1-pager design doc for a $10M migration?
752. How do you run an architecture review board (ARB)?
753. How do you mentor 4 senior engineers across 3 teams?
754. How do you build technical strategy for a 50-engineer org?
755. What is a "tech radar" and how would you maintain one?
756. How do you decide which legacy system to sunset first?
757. How do you propose moving from on-prem to cloud (3-year plan)?
758. How would you justify a $2M infra bill to non-engineering stakeholders?
759. How do you run a blameless post-mortem for a $1M outage?
760. How do you balance KTLO (keep-the-lights-on) vs new features?
761. How do you build an internal developer platform (IDP)?
762. How do you measure DevEx and improve it?
763. How do you set technical KPIs for a platform team?
764. How do you sell a refactor to product leadership?
765. How do you build consensus across 5 conflicting team leads?
766. How do you choose between "rewrite" and "incremental refactor"?
767. How do you manage tech debt as a portfolio?
768. How would you set up a guild model for cross-cutting concerns (security, perf)?
769. How do you onboard a Staff engineer hire on day 1?
770. How do you run a "tech week" or innovation sprint?
771. How do you publish RFCs effectively across 200 engineers?
772. How do you maintain code health across 1,000+ services?
773. What is "architecture as code" and how would you adopt it?
774. How do you design a paved-road framework for new services?
775. How do you decide what becomes a paved road vs a guardrail?
776. How do you manage a multi-cloud governance plan?
777. How do you choose between Kubernetes and serverless company-wide?
778. How do you set platform SLOs that map to product SLOs?
779. How do you negotiate SLAs with internal partner teams?
780. How do you migrate authentication org-wide without an outage?
781. How do you design a deprecation policy with hard cut-off dates?
782. How do you communicate a deprecation across 1,000 client teams?
783. How would you architect a platform for rapid acquisition integration?
784. How would you stand up engineering after a M&A?
785. How do you handle "two architectures" post-merger?
786. How would you re-platform a 15-year-old monolith of 10M lines?
787. How would you design a "language consolidation" plan (e.g., kill Python, standardize on Go)?
788. How would you reduce p99 latency org-wide by 30% in 12 months?
789. How would you reduce cloud spend by 25% without harming reliability?
790. How would you design a FinOps practice from scratch?

## 🌌 Extreme Scale Designs (791–830)

791. Design Google Search infrastructure at high level.
792. Design Google Maps backend with real-time traffic.
793. Design Google Photos at billion-user scale.
794. Design Gmail for a billion users with spam + search.
795. Design Google Calendar with multi-region availability.
796. Design Google Docs sync engine.
797. Design Google AdWords auction.
798. Design Google AdSense placement system.
799. Design Facebook News Feed at 3B MAU.
800. Design Facebook Live video at scale.
801. Design Instagram Stories at scale.
802. Design WhatsApp delivery + read receipts globally.
803. Design Messenger end-to-end encrypted group chat at scale.
804. Design Apple iMessage E2E delivery.
805. Design iCloud Photos sync engine.
806. Design Spotify's music recommendation pipeline.
807. Design Spotify's collaborative playlists at scale.
808. Design Netflix's open-connect CDN.
809. Design Netflix's chaos-monkey-style resilience platform.
810. Design Netflix's recommendation pipeline (offline + online).
811. Design YouTube's video upload + transcoding pipeline.
812. Design YouTube's view-count denormalization pipeline.
813. Design YouTube's content moderation pipeline.
814. Design TikTok's For You Page at hundreds of millions of users.
815. Design Twitter timeline at 500M users.
816. Design Twitter's full-text search across all tweets.
817. Design Twitter's trending topics.
818. Design Reddit's voting + comment ranking at scale.
819. Design Pinterest's home-feed personalization.
820. Design LinkedIn's "People You May Know" graph service.
821. Design LinkedIn's feed ranking pipeline.
822. Design Stripe's idempotency layer.
823. Design Stripe's payment-routing engine.
824. Design Square's POS reliability under flaky internet.
825. Design Robinhood's order matching at scale.
826. Design Coinbase's trade engine.
827. Design Binance-style global exchange (cross-region matching).
828. Design Cloudflare Workers edge runtime.
829. Design Vercel/Netlify deployment pipeline.
830. Design GitHub Actions runner orchestration at billion-job scale.

## ⚙️ Database & Storage Internals (831–870)

831. Walk through Postgres MVCC and tuple visibility checks.
832. Walk through Postgres WAL replication internals.
833. Explain logical replication slots in Postgres.
834. Explain Postgres' MVCC GC and why VACUUM matters.
835. How does Postgres' planner choose between Hash, Merge, and Nested Loop joins?
836. How does pg_partman/native partitioning work?
837. What is Citus and how does it shard Postgres?
838. What is YugabyteDB and how does it differ from Spanner?
839. What is CockroachDB's transaction layer (Range + Raft + KV)?
840. What is FoundationDB's deterministic simulation testing?
841. Walk through MySQL InnoDB redo log + undo log + buffer pool.
842. Walk through MySQL group replication.
843. How does Vitess shard MySQL at scale?
844. How does Aurora separate compute from storage?
845. How does DynamoDB handle global secondary indexes consistency?
846. How does DynamoDB partition resizing work?
847. How does Cassandra repair work (full vs incremental vs subrange)?
848. How does ScyllaDB outperform Cassandra (shard-per-core)?
849. How does Redis Cluster handle resharding?
850. How does Redis Streams + consumer groups compare to Kafka?
851. How does Kafka KRaft replace ZooKeeper?
852. How does Kafka Tiered Storage work?
853. Explain Pulsar's segmented architecture (BookKeeper).
854. Explain etcd's MVCC + Raft layering.
855. Explain ZooKeeper's ZAB protocol.
856. Walk through ClickHouse's MergeTree and how it scales analytical reads.
857. Walk through Druid's segment-based architecture.
858. Walk through Pinot's real-time + offline duality.
859. Walk through Snowflake's storage-compute separation.
860. Walk through BigQuery's Dremel execution model.
861. Walk through Spanner's TrueTime + Paxos groups.
862. Walk through DGraph's GraphQL + Badger LSM stack.
863. Walk through Neo4j's graph traversal engine.
864. Walk through TimescaleDB's hypertable + chunk design.
865. Walk through InfluxDB's TSI + TSM file format.
866. Walk through MongoDB replica set election.
867. Walk through MongoDB shard balancer.
868. Walk through Elasticsearch's segment merging and refresh.
869. Walk through OpenSearch's cross-cluster replication.
870. Walk through HBase's region splits and HDFS interaction.

## 🌐 Specialized Systems (871–910)

871. Design a high-frequency trading order matching engine (microsecond latency).
872. Design a market data fan-out system (millions of subscribers).
873. Design a distributed key-value store with linearizable reads.
874. Design a globally consistent counter (without single bottleneck).
875. Design a globally distributed graph database.
876. Design a globally distributed time-series database.
877. Design an ad-bidding RTB system at 10M QPS.
878. Design a bid-request fan-out across 50 DSPs in <50ms.
879. Design a pixel/event tracking pipeline at 1B events/day.
880. Design a programmatic ads attribution engine.
881. Design a real-time recommendation system using vector search.
882. Design a multi-armed bandit feature serving system.
883. Design a federated learning training pipeline.
884. Design a model registry + serving platform like Sagemaker.
885. Design a feature store like Feast at scale.
886. Design a vector DB (Pinecone-like) with billion-vector recall.
887. Design a generative-AI inference serving platform.
888. Design a streaming ETL platform like dbt + Materialize.
889. Design an IoT ingestion system at 10M devices.
890. Design a smart-home control plane at country scale.
891. Design a connected-car telemetry pipeline.
892. Design a maritime AIS tracking system.
893. Design a flight-tracking system.
894. Design a satellite imagery indexing pipeline.
895. Design a real-time multiplayer game backend (FPS).
896. Design a MMO server architecture (zones, instances).
897. Design a turn-based puzzle game backend with replay.
898. Design a streaming live-game telemetry system.
899. Design a CDN with custom DDoS scrubbing.
900. Design a distributed code-execution sandbox (Leetcode/Replit-style).
901. Design a CI/CD platform for 1000 teams (GitLab-scale).
902. Design a build cache shared across an org.
903. Design a remote build execution (Bazel RBE).
904. Design a binary artifact store (Artifactory-scale).
905. Design a multi-tenant Kubernetes platform (per-team quotas).
906. Design an internal serverless platform (Knative-based).
907. Design a cloud cost-attribution service.
908. Design a chargeback/showback system across business units.
909. Design a multi-region object storage with strong read-after-write.
910. Design a content-addressable storage for backups (Borg-style).

## 🧪 Migration, Modernization & Org Design (911–940)

911. Plan a strangler-fig migration of a 10-year monolith.
912. Plan a database engine migration from Oracle to Postgres at 50TB scale.
913. Plan a storage migration from on-prem NAS to S3 with zero downtime.
914. Plan a search migration from Solr to Elasticsearch.
915. Plan a queue migration from RabbitMQ to Kafka.
916. Plan a config-store migration (env vars → Vault → dynamic config).
917. Plan a runtime migration from Java 8 to Java 21 fleet-wide.
918. Plan a Python 2 → 3 migration across 800 services.
919. Plan a Node.js LTS upgrade fleet-wide.
920. Plan a Kubernetes version upgrade across 500 clusters.
921. Plan a dependency vulnerability fleet-wide remediation.
922. Plan a "remove TLS 1.0/1.1" rollout across thousands of services.
923. Plan a CDN provider migration with no user-visible regression.
924. Plan a cloud provider migration (AWS → GCP) for a stateful app.
925. Plan a "ship every commit to prod" transformation for a slow team.
926. Plan a Trunk-based development adoption across an org.
927. Plan a feature-flag system rollout org-wide.
928. Plan a SOC2 program from zero to certification.
929. Plan an ISO 27001 readiness program.
930. Plan a HIPAA-compliant fork of an existing platform.
931. Plan a PCI-DSS scope reduction strategy.
932. Plan a data classification + tagging program at the warehouse level.
933. Plan a permissions audit across the org.
934. Plan a "secure by default" framework rollout.
935. Plan a service-ownership review across an entire org.
936. Plan a "you build it, you run it" rollout for product teams.
937. Plan a centralized observability rollout.
938. Plan a centralized incident-response (IR) program.
939. Plan a multi-tenant cost-fairness mechanism.
940. Plan a "freeze + harden" period after major outages.

## 🧠 Theory, Internals, Open Problems (941–970)

941. Explain the trade-off space of CRDT vs OT for collaborative editing.
942. Explain the trade-offs between gossip and direct broadcast.
943. Explain how Cassandra picks coordinator and replicas.
944. Explain how DynamoDB enforces 1MB partition limits and how to design around them.
945. Explain how Spanner's commit-wait works.
946. Explain how Calvin (deterministic) differs from Spanner.
947. Explain how Aurora's quorum (4/6) impacts read latency.
948. Explain how PolarDB's shared-storage architecture works.
949. Explain how TiDB's TSO (timestamp oracle) works.
950. Explain how YugabyteDB places leaders for global tables.
951. Explain how Materialize maintains incremental views.
952. Explain how RocksDB's compaction styles (level vs universal) trade off.
953. Explain how LevelDB's iterator works at byte level.
954. Explain how Bigtable's tablet servers split.
955. Explain how Manhattan/F1 layered storage works.
956. Explain how Megastore differs from Spanner.
957. Explain how Percolator (incremental indexing) works.
958. Explain how Borg/Omega/Kubernetes pod scheduling differ.
959. Explain how Mesos' two-level scheduling works.
960. Explain how Kubernetes scheduler computes affinity/anti-affinity.
961. Explain how Linux cgroups v2 enforces resource limits.
962. Explain how container CPU throttling can hide tail latency bugs.
963. Explain how TCP_INFO + epoll affect connection diagnostics.
964. Explain why JVM safepoints can dominate p99 latency.
965. Explain how Go's GMP scheduler interacts with syscalls.
966. Explain how Rust's async runtime (tokio) drives I/O.
967. Explain how Erlang/OTP supervisors map onto distributed reliability.
968. Explain how Akka's actor model fits cluster sharding.
969. Explain how GRPC HTTP/2 multiplexing affects connection counts.
970. Explain how QUIC's 0-RTT introduces replay risk.

## 🧭 Open-ended Architecture & Vision (971–1000)

971. How will AI-augmented infra change platform engineering in 5 years?
972. How would you architect for an AI-first product where every API call hits an LLM?
973. How do you design an LLM gateway with caching, routing, and cost control?
974. How would you design a privacy-preserving ML pipeline (differential privacy)?
975. How would you architect for federated identity across business partners?
976. How would you architect for a "universal" customer record (CDP)?
977. How would you design an open-data platform sharing terabytes daily?
978. How do you architect an Apple-like privacy posture inside an ad-tech business?
979. How would you architect for sub-second global config propagation?
980. How would you architect for offline-first mobile with eventual sync?
981. How would you architect a CRDT-backed shared database for offline apps?
982. How would you design an at-edge personalization engine?
983. How would you design real-time translation in a chat app?
984. How would you architect a low-bandwidth assistant for emerging markets?
985. How would you design a payments platform for hyperinflation economies?
986. How would you design a system surviving 90% datacenter loss?
987. How would you design an air-gapped variant of a SaaS product?
988. How would you design data sovereignty per-customer in a global SaaS?
989. How would you architect for sustainability (carbon-aware scheduling)?
990. How would you design infra for a 100-person crisis response team during disaster?
991. How would you architect an interplanetary delay-tolerant network application?
992. How would you design a graceful end-of-life for a deprecated product with 1M users?
993. How would you architect a system that intentionally costs less than $0.01 per user per year?
994. How would you architect a system optimized for "boring" reliability over novelty?
995. How would you architect a system with no on-call rotation?
996. How would you design a self-healing platform for a small ops team?
997. How would you design a system whose top requirement is auditability for regulators?
998. How would you make an existing system 10x cheaper without functional changes?
999. How would you design a "platform as a product" with measurable customer value?
1000. If you could redesign the internet's DNS today, what would you change and why?

---

[← Senior](./03-senior-level.md) · [README](./README.md) · [Sources →](./05-sources.md)
