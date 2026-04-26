# Refactoring.Guru Roadmap — Design Document

**Date:** 2026-04-27
**Author:** Bakhodir Yashin Mansur
**Status:** Approved (brainstorming phase)

---

## 1. Goal

Convert the entire **Design Patterns** section of [refactoring.guru](https://refactoring.guru/) into a structured roadmap inside this repository, following the existing `TEMPLATE.md` 8-file format.

The roadmap must be:
- Self-contained (no need to visit the site to learn)
- Pedagogically progressive (junior → professional)
- Multi-language (Go + Java + Python code examples)
- Cross-linked (patterns reference related patterns)

---

## 2. Scope

### In scope
- **22 GoF design patterns** as covered by refactoring.guru:
  - **Creational (5):** Factory Method, Abstract Factory, Builder, Prototype, Singleton
  - **Structural (7):** Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy
  - **Behavioral (10):** Chain of Responsibility, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor
- For each pattern: 8 markdown files following `TEMPLATE.md`
- Top-level READMEs for navigation
- Cross-references between related patterns

### Out of scope (separate future projects)
- Code Smells (22 smells)
- Refactoring Techniques (70+ techniques)
- Articles, anti-patterns, history sections of refactoring.guru
- Interpreter pattern (not covered by refactoring.guru)

---

## 3. Location

```
Roadmap/Architecture/refactoring-guru/
```

Rationale: design patterns are an architectural/design topic, not a language-specific topic. Sibling folder to existing `software-design-architecture/`.

---

## 4. Folder Structure

```
Roadmap/Architecture/refactoring-guru/
├── README.md                              # entry point + roadmap navigation
├── 01-design-patterns/
│   ├── README.md                          # what are design patterns
│   ├── 01-creational/
│   │   ├── README.md                      # creational category overview
│   │   ├── 01-factory-method/
│   │   │   ├── junior.md
│   │   │   ├── middle.md
│   │   │   ├── senior.md
│   │   │   ├── professional.md
│   │   │   ├── interview.md
│   │   │   ├── tasks.md
│   │   │   ├── find-bug.md
│   │   │   └── optimize.md
│   │   ├── 02-abstract-factory/  (same 8 files)
│   │   ├── 03-builder/
│   │   ├── 04-prototype/
│   │   └── 05-singleton/
│   ├── 02-structural/
│   │   ├── README.md
│   │   ├── 01-adapter/
│   │   ├── 02-bridge/
│   │   ├── 03-composite/
│   │   ├── 04-decorator/
│   │   ├── 05-facade/
│   │   ├── 06-flyweight/
│   │   └── 07-proxy/
│   └── 03-behavioral/
│       ├── README.md
│       ├── 01-chain-of-responsibility/
│       ├── 02-command/
│       ├── 03-iterator/
│       ├── 04-mediator/
│       ├── 05-memento/
│       ├── 06-observer/
│       ├── 07-state/
│       ├── 08-strategy/
│       ├── 09-template-method/
│       └── 10-visitor/
```

**File count:**
- Pattern files: 22 × 8 = **176**
- READMEs: 1 (root) + 1 (design-patterns) + 3 (categories) = **5**
- **Total: 181 files**

---

## 5. File Format (per pattern)

All 8 files follow `TEMPLATE.md` (existing in repo). Summary:

| File | Focus | Audience |
|---|---|---|
| `junior.md` | "What is it?" "How to use?" Basic syntax & examples | Just learned the language |
| `middle.md` | "Why?" "When?" Real-world cases, tradeoffs | 1-3 yr experience |
| `senior.md` | "How to optimize?" "How to architect?" | 3-7 yr experience |
| `professional.md` | "Under the hood" — runtime, memory, performance | 7+ yr / specialist |
| `interview.md` | Interview Q&A across all levels | Job prep |
| `tasks.md` | 10+ hands-on exercises | Practice |
| `find-bug.md` | 10+ buggy snippets to fix | Critical reading |
| `optimize.md` | 10+ inefficient implementations to optimize | Performance practice |

Each file has up to 24 sections per `TEMPLATE.md` (Introduction, Prerequisites, Glossary, Core Concepts, Code Examples, etc.). Sections may be omitted when not relevant.

---

## 6. Content Sources & Languages

- **Primary source:** refactoring.guru pattern pages (parsed via WebFetch)
- **Secondary:** GoF book concepts, idiomatic language references
- **Documentation language:** **English** (as per `TEMPLATE.md` requirement)
- **Code example languages:** **Go**, **Java**, **Python** — all three for every "Code Examples" section

### Why three languages?
- **Go** — primary language of this repository
- **Java** — refactoring.guru's primary language; classical OOP demonstration
- **Python** — concise, popular, contrasts dynamic vs static typing

Each Code Examples section structure:
```markdown
### Go
```go
// idiomatic Go implementation
```

### Java
```java
// classical Java implementation
```

### Python
```python
# pythonic implementation
```
```

---

## 7. Diagrams

- **UML class diagrams:** Mermaid `classDiagram`
- **Sequence diagrams:** Mermaid `sequenceDiagram`
- **State diagrams (for State pattern):** Mermaid `stateDiagram`

All diagrams inline in markdown — no external image dependencies.

---

## 8. Cross-references

Each pattern's `senior.md` and `professional.md` will include a "Related Patterns" section linking to related ones, e.g.:
- Decorator vs Proxy vs Adapter
- Strategy vs State
- Factory Method vs Abstract Factory vs Builder

Use relative markdown links: `[Strategy](../08-strategy/middle.md)`.

---

## 9. Execution Strategy (Approach A — phased by category)

| Phase | Contents | Files | Sessions |
|---|---|---|---|
| **Phase 1** | Folder structure + all READMEs + Singleton (sample pattern, all 8 files) | 5 + 8 = 13 | 1 |
| **Phase 2** | Remaining Creational: Factory Method, Abstract Factory, Builder, Prototype | 32 | 1-2 |
| **Phase 3** | Structural (7 patterns × 8) | 56 | 2 |
| **Phase 4** | Behavioral part 1: Chain, Command, Iterator, Mediator, Memento | 40 | 2 |
| **Phase 5** | Behavioral part 2: Observer, State, Strategy, Template Method, Visitor | 40 | 2 |
| **Total** | | **181 files** | **8-10 sessions** |

### Why Singleton first?
- Simplest GoF pattern — fewer concepts, faster to validate template fidelity
- Establishes quality bar for the rest

---

## 10. Quality Standards

Each pattern file must:
- Match the level (junior must NOT use senior/professional terminology)
- Include working, compilable code (Go, Java, Python)
- Include at least one Mermaid diagram (in `junior.md` or `middle.md`)
- Include 5+ pros and 5+ cons in `middle.md`
- Include 3+ real-world use cases (with industry context, not toy examples)
- Have correct cross-references (verified before commit)

`tasks.md`, `find-bug.md`, `optimize.md` must each contain **10+ exercises** with solutions.

---

## 11. Risks & Mitigations

| Risk | Mitigation |
|---|---|
| Token exhaustion mid-pattern | Each pattern is self-contained; commit after each pattern done |
| refactoring.guru parsing failures | Fall back to GoF book + secondary sources |
| Inconsistent quality across patterns | Phase 1 sets the bar with Singleton — review before Phase 2 |
| Cross-references break | Validate links at end of each phase |
| Stale memory of TEMPLATE.md | Re-read `TEMPLATE.md` at start of each phase |

---

## 12. Success Criteria

- All 181 files exist and follow the structure above
- Each pattern has compilable Go + Java + Python code
- All Mermaid diagrams render correctly
- All internal links resolve
- READMEs provide working navigation through the entire roadmap
- Reader can study the entire Design Patterns curriculum without leaving the repo

---

## 13. Out of scope for the design phase

The implementation **plan** (which files in what order, dependencies, validation steps) will be created next via the `writing-plans` skill. This document only locks the design.
