# Performance

The performance arm of Quality Engineering — measuring, profiling, optimising, and protecting hot paths over the lifetime of a system.

> Content under this section is being filled in. The structure below shows the planned coverage; pages marked _coming soon_ link to themselves until written.

---

## Planned sections

- **Measurement** — latency vs throughput, percentiles vs averages, cold vs steady-state, sample size.
- **Profiling** — CPU profiles, allocation profiles, flame graphs; pprof / perf / Instruments; reading them without lying to yourself.
- **Benchmarking** — micro-benchmarks done right (avoiding dead-code elimination, branch prediction noise, JIT warmup); macro-benchmarks; comparison stability.
- **Memory** — allocations, escape analysis, GC pressure, fragmentation, working set, cache locality.
- **Concurrency throughput** — Amdahl's law, contention, scheduler effects, scaling curves.
- **Regression detection** — CI benchmarks, statistical thresholds (Mann-Whitney U), trend dashboards.
- **Per-level tracks** — junior / middle / senior / professional / optimize / find-bug / interview / tasks.

---

## Related

- **[Testing](../testing/)** — load tests and stress tests.
- **[Language Internals › Concurrency](../../language-internals/concurrency/)** — the substrate that bounds throughput.
- **[Diagnostics](../../diagnostics/)** — when performance regressions become incidents.
