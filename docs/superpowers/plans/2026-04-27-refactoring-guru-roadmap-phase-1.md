# Refactoring.Guru Roadmap — Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the folder skeleton for the entire Refactoring.Guru roadmap (`Roadmap/Architecture/refactoring-guru/`), all navigation README files, and the first complete pattern (Singleton — 8 files following `TEMPLATE.md`).

**Architecture:** Static markdown roadmap. Per-pattern folder with 8 level-stratified files (junior → professional + interview/tasks/find-bug/optimize). Code in Go, Java, Python. Mermaid for diagrams. Internal cross-links via relative markdown paths. Source content parsed from refactoring.guru via WebFetch.

**Tech Stack:** Markdown, Mermaid, Go 1.22+, Java 17+, Python 3.11+. Content sourced from refactoring.guru pattern pages.

**Spec reference:** [docs/superpowers/specs/2026-04-27-refactoring-guru-roadmap-design.md](../specs/2026-04-27-refactoring-guru-roadmap-design.md)

---

## File Map

Files to be created in this phase:

```
Roadmap/Architecture/refactoring-guru/
├── README.md                                          [Task 2]
├── 01-design-patterns/
│   ├── README.md                                      [Task 3]
│   ├── 01-creational/
│   │   ├── README.md                                  [Task 4]
│   │   └── 05-singleton/
│   │       ├── junior.md                              [Task 7]
│   │       ├── middle.md                              [Task 8]
│   │       ├── senior.md                              [Task 9]
│   │       ├── professional.md                        [Task 10]
│   │       ├── interview.md                           [Task 11]
│   │       ├── tasks.md                               [Task 12]
│   │       ├── find-bug.md                            [Task 13]
│   │       └── optimize.md                            [Task 14]
│   ├── 02-structural/
│   │   └── README.md                                  [Task 5]
│   └── 03-behavioral/
│       └── README.md                                  [Task 6]
```

Empty pattern folders (created in Task 1) for the remaining 21 patterns will be filled in subsequent phases.

**Total Phase 1 files:** 13 (5 READMEs + 8 Singleton files)

---

## Task 1: Create folder structure for the entire roadmap

**Files:**
- Create directories only (no content yet)

- [ ] **Step 1: Create all directories in one shot**

```bash
cd /Users/mrb/Desktop/SeniorProject

mkdir -p Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/{01-factory-method,02-abstract-factory,03-builder,04-prototype,05-singleton}
mkdir -p Roadmap/Architecture/refactoring-guru/01-design-patterns/02-structural/{01-adapter,02-bridge,03-composite,04-decorator,05-facade,06-flyweight,07-proxy}
mkdir -p Roadmap/Architecture/refactoring-guru/01-design-patterns/03-behavioral/{01-chain-of-responsibility,02-command,03-iterator,04-mediator,05-memento,06-observer,07-state,08-strategy,09-template-method,10-visitor}
```

- [ ] **Step 2: Verify structure**

```bash
find Roadmap/Architecture/refactoring-guru -type d | sort
```

Expected: 26 directories (1 root + 1 design-patterns + 3 categories + 22 patterns - wait let me count again).
Actual expected output: 4 navigation dirs + 22 pattern dirs = **26 directories total**.

- [ ] **Step 3: Commit empty structure**

```bash
# Add a .gitkeep to keep empty dirs tracked
find Roadmap/Architecture/refactoring-guru -type d -empty -exec touch {}/.gitkeep \;
git add Roadmap/Architecture/refactoring-guru/
git commit -m "scaffold: refactoring-guru roadmap directory structure"
```

---

## Task 2: Root README — `Roadmap/Architecture/refactoring-guru/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/README.md`

- [ ] **Step 1: Fetch refactoring.guru homepage for accurate intro content**

Use WebFetch on `https://refactoring.guru/` to capture:
- Site mission/purpose
- Top-level structure (Design Patterns, Code Smells, Refactoring)
- Brief author/credits attribution

- [ ] **Step 2: Write the root README**

Required sections:

```markdown
# Refactoring.Guru Roadmap

> **Source:** https://refactoring.guru/ — adapted into a structured roadmap with multi-language code examples (Go, Java, Python).

## What This Roadmap Covers

| Section | Topics | Files |
|---|---|---|
| [Design Patterns](01-design-patterns/README.md) | 22 GoF patterns (Creational, Structural, Behavioral) | 176 |

## How to Use

- Each pattern has 8 files: `junior.md`, `middle.md`, `senior.md`, `professional.md`, `interview.md`, `tasks.md`, `find-bug.md`, `optimize.md`
- Read in order: junior → middle → senior → professional
- After learning, attempt the practice files: tasks → find-bug → optimize
- Use `interview.md` for job preparation

## Pattern Index

### Creational Patterns
- [Factory Method](01-design-patterns/01-creational/01-factory-method/junior.md)
- [Abstract Factory](01-design-patterns/01-creational/02-abstract-factory/junior.md)
- [Builder](01-design-patterns/01-creational/03-builder/junior.md)
- [Prototype](01-design-patterns/01-creational/04-prototype/junior.md)
- [Singleton](01-design-patterns/01-creational/05-singleton/junior.md)

### Structural Patterns
- [Adapter](01-design-patterns/02-structural/01-adapter/junior.md)
- [Bridge](01-design-patterns/02-structural/02-bridge/junior.md)
- [Composite](01-design-patterns/02-structural/03-composite/junior.md)
- [Decorator](01-design-patterns/02-structural/04-decorator/junior.md)
- [Facade](01-design-patterns/02-structural/05-facade/junior.md)
- [Flyweight](01-design-patterns/02-structural/06-flyweight/junior.md)
- [Proxy](01-design-patterns/02-structural/07-proxy/junior.md)

### Behavioral Patterns
- [Chain of Responsibility](01-design-patterns/03-behavioral/01-chain-of-responsibility/junior.md)
- [Command](01-design-patterns/03-behavioral/02-command/junior.md)
- [Iterator](01-design-patterns/03-behavioral/03-iterator/junior.md)
- [Mediator](01-design-patterns/03-behavioral/04-mediator/junior.md)
- [Memento](01-design-patterns/03-behavioral/05-memento/junior.md)
- [Observer](01-design-patterns/03-behavioral/06-observer/junior.md)
- [State](01-design-patterns/03-behavioral/07-state/junior.md)
- [Strategy](01-design-patterns/03-behavioral/08-strategy/junior.md)
- [Template Method](01-design-patterns/03-behavioral/09-template-method/junior.md)
- [Visitor](01-design-patterns/03-behavioral/10-visitor/junior.md)

## Languages

All code examples in: **Go**, **Java**, **Python**.

## Status

- ✅ Singleton (5/5 levels + practice)
- 🚧 In progress: Other 21 patterns
```

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/README.md
git commit -m "docs: add refactoring-guru root README"
```

---

## Task 3: Design Patterns category README — `01-design-patterns/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/README.md`

- [ ] **Step 1: Fetch source page**

WebFetch on `https://refactoring.guru/design-patterns` to capture:
- Definition of design patterns
- History (GoF book reference, 1994)
- Classification (creational/structural/behavioral)
- Why patterns matter

- [ ] **Step 2: Write the file**

Required sections:
- **What are Design Patterns?** — 2-3 paragraphs
- **History** — GoF book, "Gang of Four" (Gamma, Helm, Johnson, Vlissides)
- **Classification table** — 3 categories with definitions and pattern counts
- **How to Pick a Pattern** — decision guide
- **Mermaid diagram** — pattern category hierarchy
- **Pattern list** — links to each pattern's `junior.md`

- [ ] **Step 3: Verify all internal links resolve**

```bash
cd /Users/mrb/Desktop/SeniorProject
grep -oE '\[.+?\]\(.+?\.md\)' Roadmap/Architecture/refactoring-guru/01-design-patterns/README.md | head
```

- [ ] **Step 4: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/README.md
git commit -m "docs: add design-patterns category README"
```

---

## Task 4: Creational README — `01-creational/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/README.md`

- [ ] **Step 1: Fetch source page**

WebFetch on `https://refactoring.guru/design-patterns/creational-patterns`.

- [ ] **Step 2: Write the file**

Required sections:
- **What Are Creational Patterns?**
- **The 5 Creational Patterns** — table with one-line description each
- **When to Use Creational Patterns** — symptoms in code that signal one is needed
- **Comparison Matrix** — Factory Method vs Abstract Factory vs Builder vs Prototype vs Singleton (use case, complexity, when-to-pick)
- **Mermaid diagram** — relationships between the 5 patterns
- **Links** to each pattern's `junior.md` (relative paths)

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/README.md
git commit -m "docs: add creational patterns README"
```

---

## Task 5: Structural README — `02-structural/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/02-structural/README.md`

- [ ] **Step 1: Fetch source page**

WebFetch on `https://refactoring.guru/design-patterns/structural-patterns`.

- [ ] **Step 2: Write the file**

Required sections (parallel to Task 4):
- **What Are Structural Patterns?**
- **The 7 Structural Patterns** — table
- **When to Use Structural Patterns**
- **Comparison Matrix** — including notable contrast pairs (Decorator vs Proxy vs Adapter, Facade vs Mediator)
- **Mermaid diagram** — relationships
- **Links** to each pattern's `junior.md`

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/02-structural/README.md
git commit -m "docs: add structural patterns README"
```

---

## Task 6: Behavioral README — `03-behavioral/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/03-behavioral/README.md`

- [ ] **Step 1: Fetch source page**

WebFetch on `https://refactoring.guru/design-patterns/behavioral-patterns`.

- [ ] **Step 2: Write the file**

Required sections (parallel to Task 4 & 5):
- **What Are Behavioral Patterns?**
- **The 10 Behavioral Patterns** — table
- **When to Use Behavioral Patterns**
- **Comparison Matrix** — note critical pairs (Strategy vs State, Command vs Memento, Observer vs Mediator)
- **Mermaid diagram** — relationships (largest of the three)
- **Links** to each pattern's `junior.md`

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/03-behavioral/README.md
git commit -m "docs: add behavioral patterns README"
```

---

## Task 7: Singleton — `junior.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/junior.md`

**Source:** `https://refactoring.guru/design-patterns/singleton`

- [ ] **Step 1: Fetch refactoring.guru singleton pages**

WebFetch the following:
- `https://refactoring.guru/design-patterns/singleton` (main)
- `https://refactoring.guru/design-patterns/singleton/go/example`
- `https://refactoring.guru/design-patterns/singleton/java/example`
- `https://refactoring.guru/design-patterns/singleton/python/example`

Capture: intent, problem, solution, structure, applicability, how-to-implement, pros/cons, relations with other patterns, and code in Go/Java/Python.

- [ ] **Step 2: Write `junior.md`**

Follow `TEMPLATE.md` for `junior.md`. Required sections (omit those not relevant):

1. **Introduction** — Plain English: "Singleton ensures a class has exactly one instance and provides a global access point to it."
2. **Prerequisites** — basic OOP, classes/objects, static methods
3. **Glossary** — Singleton, Instance, Global Access, Lazy Initialization, Eager Initialization, Thread Safety
4. **Core Concepts** — single instance guarantee, global access, controlled instantiation
5. **Real-World Analogies** — Government (one per country), Database connection pool, Logger
6. **Mental Models** — "the only one" — like a CEO of a company
7. **Pros & Cons** — table format, 5 each
8. **Use Cases** — Logger, Configuration manager, Connection pool, Cache, Hardware access (printer)
9. **Code Examples** — three subsections:
   - **Go** — package-level singleton with `sync.Once`
   - **Java** — classic eager and lazy with `synchronized`
   - **Python** — module-level (Pythonic) and metaclass-based
10. **Coding Patterns** — eager init, lazy init, double-checked locking
11. **Clean Code** — meaningful names (`getInstance()` standard)
12. **Best Practices** — thread-safety, prevent serialization-based duplication
13. **Edge Cases & Pitfalls** — multi-threading races, classloader issues (Java), reflection bypass
14. **Common Mistakes** — non-thread-safe lazy init, missing private constructor
15. **Tricky Points** — Singletons hide dependencies (testability problem)
16. **Test** — show how singleton state leaks between tests
17. **Tricky Questions** — "Is Singleton an anti-pattern?" — discuss the debate
18. **Cheat Sheet** — minimal Go/Java/Python snippets
19. **Summary** — 3 takeaways
20. **Further Reading** — link to refactoring.guru, GoF book reference
21. **Related Topics** — Monostate pattern, Dependency Injection (alternative), Factory (often returns singleton)
22. **Diagrams & Visual Aids** — Mermaid `classDiagram` showing Singleton class with private constructor + static `getInstance()`

**Code requirements:**
- Go: use `sync.Once` for thread-safe lazy init, package-level variable
- Java: include both eager (`private static final`) and lazy (`synchronized getInstance`) variants
- Python: show module-level (preferred) AND `__new__`-based class

**Word target:** 2500-3500 words (junior level should be approachable, not overwhelming).

- [ ] **Step 3: Verify Mermaid syntax**

Use a Mermaid live editor (https://mermaid.live) or visual inspection. The class diagram should render without parse errors.

- [ ] **Step 4: Verify links**

```bash
grep -oE '\[.+?\]\([^)]+\)' Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/junior.md | grep -v 'http'
```

All relative links must resolve.

- [ ] **Step 5: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/junior.md
git commit -m "docs: add Singleton junior.md"
```

---

## Task 8: Singleton — `middle.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/middle.md`

- [ ] **Step 1: Re-read fetched material from Task 7 (already cached)**

No new WebFetch needed.

- [ ] **Step 2: Write `middle.md`**

Focus shifts to **Why** and **When**. Required sections:

1. **Introduction** — moves beyond "what is Singleton" to "when does it earn its place in your design"
2. **When to Use** — concrete decision criteria
3. **When NOT to Use** — explicit anti-patterns (when DI is better, testability concerns)
4. **Real-World Cases** — production examples:
   - Logger in a microservice
   - Configuration loader at app startup
   - Database connection pool wrapper
   - HTTP client with shared connection reuse
5. **Code Examples** — Go/Java/Python production-ready variants:
   - Go: thread-safe singleton with config loading
   - Java: enum-based Singleton (Joshua Bloch's recommended way) + lazy holder idiom
   - Python: thread-safe `__new__` with `threading.Lock`
6. **Trade-offs** — testability, hidden dependencies, multi-threading
7. **Alternatives Comparison Table** — Singleton vs Dependency Injection vs Static Class vs Module
8. **Refactoring from Singleton** — how to migrate when it's hurting you (gradual DI introduction)
9. **Pros & Cons** (deeper than junior — 7 each)
10. **Edge Cases** — multi-classloader (Java), serialization (`readResolve`), cloning, reflection
11. **Tricky Points** — eager vs lazy debate, double-checked locking pitfalls (`volatile` in Java)
12. **Best Practices** — prefer enum (Java), prefer module (Python), prefer `sync.Once` (Go)
13. **Summary**
14. **Related Topics** — Monostate, Borg pattern (Python), Service Locator
15. **Diagrams** — sequence diagram of double-checked locking

**Word target:** 3500-5000 words.

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/middle.md
git commit -m "docs: add Singleton middle.md"
```

---

## Task 9: Singleton — `senior.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/senior.md`

- [ ] **Step 1: Write `senior.md`**

Focus: **How to optimize** and **how to architect**. Required sections:

1. **Introduction** — Singleton as an architectural concern, not a syntax trick
2. **Architectural Patterns Around Singleton**:
   - Singleton + Factory
   - Singleton + Strategy (singleton holds the strategy)
   - Multiton (controlled multi-instance)
   - Service Locator (anti-pattern alternative discussion)
3. **Performance Considerations**:
   - Lock contention in concurrent access
   - Lazy vs eager: startup time vs first-call latency
   - Memory: instance lives until process termination
4. **Concurrency Deep Dive**:
   - Go: `sync.Once` internals, why double-checked locking is unnecessary
   - Java: `volatile` semantics in DCL, JMM happens-before guarantees
   - Python: GIL implications, `threading.Lock` overhead
5. **Testability Strategies**:
   - Resettable singletons (test-only `reset()`)
   - Interface-based singleton (mockable in tests)
   - DI containers as singleton replacement
6. **When Singleton Becomes a Bottleneck**:
   - Hot-path lock contention
   - Sharding the singleton (per-CPU, per-thread)
7. **Code Examples** — Go/Java/Python advanced:
   - Go: benchmarked `sync.Once` vs `atomic.Value` vs RWMutex variants
   - Java: enum singleton with EnumSet behavior, lazy holder + JIT optimization
   - Python: thread-local "singleton-per-context" pattern
8. **Real-World Architectures** — case studies:
   - Spring `@Singleton` scope
   - Node.js module caching as accidental singleton
   - Kubernetes operator leader election (singleton across cluster)
9. **Pros & Cons** at scale (7 each — different from junior/middle)
10. **Trade-off Analysis Matrix** — different singleton strategies vs use case
11. **Migration Patterns** — refactoring legacy singletons to DI without big-bang rewrite
12. **Diagrams** — sequence diagram comparing locked vs lock-free singleton; state diagram of init lifecycle

**Word target:** 5000-7000 words.

- [ ] **Step 2: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/senior.md
git commit -m "docs: add Singleton senior.md"
```

---

## Task 10: Singleton — `professional.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/professional.md`

- [ ] **Step 1: Write `professional.md`**

Focus: **Under the hood** — runtime, compiler, OS. Required sections:

1. **Introduction** — what happens at machine/runtime level when singleton is initialized
2. **Memory Layout**:
   - Go: package-level `var` lives in BSS/DATA segment; lazy `sync.Once` holds in heap
   - Java: PermGen/Metaspace for class metadata, heap for instance
   - Python: module dict in interpreter's main thread state
3. **`sync.Once` Source-Code Walkthrough** (Go):
   - `done` atomic flag, `m` mutex, fast-path with `LoadUint32`
   - Why this is correct under Go memory model (happens-before)
4. **JMM and Double-Checked Locking** (Java):
   - Bytecode of synchronized blocks
   - `volatile` write barrier and `mfence` on x86
   - Why Bloch's enum approach guarantees lazy + thread-safe at JVM level
5. **CPython GIL Interaction**:
   - Module import is GIL-protected (singleton via module is free)
   - `__new__` race conditions only matter on free-threaded Python (3.13+ no-GIL build)
6. **Atomic Operations**:
   - x86 `LOCK CMPXCHG` cost
   - ARM weak memory model implications (acquire/release semantics)
7. **Inlining and JIT**:
   - HotSpot inlines `getInstance()` after enough invocations
   - Escape analysis cannot stack-allocate singleton (it escapes by definition)
8. **Garbage Collection Implications**:
   - Singletons are GC roots — never collected
   - Memory leak risk: singletons holding references to ephemeral objects
9. **Process Lifecycle**:
   - Static initialization order across compilation units (C++ analog — for context)
   - Shutdown hooks and singleton cleanup
10. **OS-Level Concerns**:
    - Fork-safety: singleton state after `fork()` (Python `multiprocessing` quirks)
    - Shared memory singletons across processes (memory-mapped files, POSIX shm)
11. **Distributed Singleton**:
    - Leader election (Zookeeper, etcd, Raft)
    - Why true distributed singleton is impossible (FLP impossibility result reference)
12. **Benchmarks** — actual numbers:
    - Go: `sync.Once` ~1ns fast-path, ~50ns first call
    - Java: enum singleton same as static field after JIT (~0.5ns)
    - Python: module attribute lookup ~50ns
13. **Diagrams** — memory layout, atomic state machine

**Word target:** 6000-8000 words. Heavy on code, source pointers, benchmarks.

- [ ] **Step 2: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/professional.md
git commit -m "docs: add Singleton professional.md"
```

---

## Task 11: Singleton — `interview.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/interview.md`

- [ ] **Step 1: Write `interview.md`**

Required structure:

1. **Junior Questions (10)** — basic understanding:
   - "What is Singleton?"
   - "Why use private constructor?"
   - "Difference between singleton and static class?"
   - "Eager vs lazy initialization?"
   - …
2. **Middle Questions (10)** — practical:
   - "How do you make Singleton thread-safe?"
   - "What's the problem with lazy initialization in multi-threaded code?"
   - "Why is enum the best Singleton in Java?"
   - "How do you test code that depends on a Singleton?"
   - …
3. **Senior Questions (10)** — design judgment:
   - "When is Singleton an anti-pattern?"
   - "How would you refactor a codebase abusing Singletons?"
   - "Compare Singleton with DI."
   - "Multiton — when?"
   - …
4. **Professional Questions (10)** — deep technical:
   - "Walk me through `sync.Once` implementation."
   - "Why is `volatile` needed in Java DCL?"
   - "Singleton in distributed systems?"
   - "Memory leak risks of Singleton?"
   - …
5. **Coding Tasks (5)** — implement:
   - Thread-safe lazy Singleton in Go without `sync.Once`
   - Resettable Singleton for testing in Java
   - Generic Singleton metaclass in Python
   - Multi-tenant singleton (one per tenant ID)
   - Distributed singleton sketch using Redis SETNX
6. **Trick Questions (5)** — common gotchas
7. **Behavioral / Architectural Questions (5)** — "Tell me about a time when Singleton caused a bug" prompts

Each question must have a **complete model answer** (not "[answer here]").

**Word target:** 6000-9000 words.

- [ ] **Step 2: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/interview.md
git commit -m "docs: add Singleton interview.md"
```

---

## Task 12: Singleton — `tasks.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/tasks.md`

- [ ] **Step 1: Write `tasks.md`**

Required structure: **10+ hands-on exercises**, each with:
- **Task statement** (1-2 paragraphs)
- **Required Functionality**
- **Constraints** (e.g., "must be thread-safe", "must work with `go test -race`")
- **Test cases** (input → expected behavior)
- **Solution** in **Go, Java, AND Python**
- **Discussion** (why this solution, what alternatives exist)

Suggested tasks (10):
1. Implement basic Singleton (Logger)
2. Thread-safe lazy Singleton (Counter)
3. Configuration manager (loads config once)
4. Connection pool Singleton
5. Singleton with parameters (initialize once with arguments)
6. Resettable Singleton for tests
7. Singleton holding mutable state (with proper synchronization)
8. Multiton (one instance per key)
9. Singleton with serialization-safe behavior (Java focus)
10. Singleton vs DI refactor — given a singleton-heavy class, refactor to DI

**Word target:** 6000-9000 words.

- [ ] **Step 2: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/tasks.md
git commit -m "docs: add Singleton tasks.md"
```

---

## Task 13: Singleton — `find-bug.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/find-bug.md`

- [ ] **Step 1: Write `find-bug.md`**

Required structure: **10+ buggy code snippets**, each with:
- **Buggy code** (in Go, Java, or Python — distribute across all three)
- **Symptoms** the user would see
- **"Find the bug" prompt** (give reader space to think)
- **Bug explanation**
- **Fix** (corrected code)
- **Lesson learned**

Suggested bugs (10+):
1. Non-thread-safe lazy init (Java)
2. Missing `volatile` in double-checked locking (Java)
3. Returning `nil` from `sync.Once` due to incorrect closure (Go)
4. Reflection breaks Singleton (Java)
5. Serialization creates new instance (Java) — fix with `readResolve`
6. `__init__` runs every time (Python `__new__` confusion)
7. Forking breaks state (Python multiprocessing)
8. Singleton state leaks between unit tests (any lang)
9. Cloning bypasses Singleton (Java)
10. Race in `if instance == nil { instance = New() }` (Go)
11. Lazy holder idiom misunderstood (Java)
12. ClassLoader creates two singletons (Java)

**Word target:** 5000-7000 words.

- [ ] **Step 2: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/find-bug.md
git commit -m "docs: add Singleton find-bug.md"
```

---

## Task 14: Singleton — `optimize.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/optimize.md`

- [ ] **Step 1: Write `optimize.md`**

Required structure: **10+ inefficient implementations to optimize**, each with:
- **Slow/inefficient code**
- **Profiling output / benchmark numbers** showing the problem
- **What's wrong**
- **Optimized code**
- **Benchmark numbers showing improvement**
- **Trade-offs**

Suggested optimizations (10):
1. `synchronized getInstance()` — replace with lazy holder or DCL
2. `sync.Mutex` for read path — use `sync.Once`
3. Recreating value inside `Once` due to closure capture
4. Singleton holding huge cache without eviction
5. Lock contention under high concurrency — sharded singleton
6. Eager init slowing app startup — convert to lazy with budget
7. Logger singleton serializing all writes — use buffered async logger
8. Singleton with deep object graph causing slow GC marking
9. Python module-level expensive computation — defer to first access
10. Singleton storing per-request state — fix by switching to context-scoped

Each must include actual benchmark code (`testing.B` in Go, JMH in Java, `timeit` in Python).

**Word target:** 6000-8000 words.

- [ ] **Step 2: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/01-design-patterns/01-creational/05-singleton/optimize.md
git commit -m "docs: add Singleton optimize.md"
```

---

## Task 15: Phase 1 verification

**Files:**
- No new files — verification only

- [ ] **Step 1: Verify file count**

```bash
cd /Users/mrb/Desktop/SeniorProject
find Roadmap/Architecture/refactoring-guru -name "*.md" -type f | wc -l
```

Expected: **13** (5 READMEs + 8 Singleton files).

- [ ] **Step 2: Verify all internal links resolve**

```bash
cd /Users/mrb/Desktop/SeniorProject

# Find all relative .md links in our new files and ensure each target exists
find Roadmap/Architecture/refactoring-guru -name "*.md" -exec grep -oH 'href="[^"]*\.md[^"]*"\|](\([^)]*\.md[^)]*\))' {} \; > /tmp/links.txt
# Manual review or scripted check
cat /tmp/links.txt
```

For each relative link, verify the target file exists. Fix broken links inline.

- [ ] **Step 3: Verify Mermaid diagrams parse**

For each `.md` file containing `\`\`\`mermaid`, copy the diagram into https://mermaid.live and confirm no syntax errors. Fix any issues.

- [ ] **Step 4: Verify code compiles / runs**

For each Go/Java/Python code block in Singleton files:
- Save to a scratch file
- Compile/run
- Fix any errors

```bash
# Example for Go
mkdir -p /tmp/singleton-verify && cd /tmp/singleton-verify
go mod init verify
# Copy Go snippets in, run `go build`
```

- [ ] **Step 5: Update root README status**

Edit `Roadmap/Architecture/refactoring-guru/README.md` Status section:

```markdown
## Status

- ✅ Singleton (8/8 files)
- ⏳ Other 21 patterns: pending Phase 2+
```

- [ ] **Step 6: Final commit**

```bash
git add Roadmap/Architecture/refactoring-guru/README.md
git commit -m "docs: mark Singleton complete in roadmap status"
```

- [ ] **Step 7: Verify clean working tree**

```bash
git status
```

Expected: `nothing to commit, working tree clean`.

---

## Phase 1 Done Criteria

- [ ] All 26 directories exist
- [ ] All 5 READMEs written and committed
- [ ] All 8 Singleton files written and committed
- [ ] All internal links resolve
- [ ] All Mermaid diagrams render
- [ ] All code samples compile/run
- [ ] Root README status updated
- [ ] Working tree clean

---

## Out of scope for Phase 1

The following are explicitly **deferred to later phases**:
- Other 21 patterns (Phases 2-5 per spec)
- Code Smells, Refactoring Techniques (separate future projects)
- Cross-pattern relationship deep-dives (will solidify after each phase)

After Phase 1 sign-off, proceed to Phase 2 (remaining Creational patterns) using the Singleton implementation as quality benchmark.
