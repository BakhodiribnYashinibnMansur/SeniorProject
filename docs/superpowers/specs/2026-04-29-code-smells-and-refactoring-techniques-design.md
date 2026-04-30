# Code Smells & Refactoring Techniques — Design Document

**Date:** 2026-04-29
**Author:** Bakhodir Yashin Mansur
**Status:** Approved (brainstorming phase)

---

## 1. Goal

Extend the existing `Roadmap/Architecture/refactoring-guru/` roadmap with two new sections — **Code Smells** and **Refactoring Techniques** — covering the remaining content of [refactoring.guru](https://refactoring.guru/) that was originally marked as out of scope in the 2026-04-27 design.

The new sections must:
- Mirror the pedagogical style of the existing Design Patterns section (8-file suite per unit, junior → professional progression)
- Be self-contained (no need to visit refactoring.guru to learn)
- Provide multi-language code examples (Go + Java + Python)
- Cross-reference between smells and the techniques that resolve them

---

## 2. Scope

### In scope

- **22 Code Smells** grouped into 5 refactoring.guru categories
- **~70 Refactoring Techniques** grouped into 6 refactoring.guru categories
- Category-level 8-file suite (Approach B — grouped, not per-unit)
- Top-level READMEs for navigation in both new sections
- Update of the existing `refactoring-guru/README.md` Status section to reflect the new sections

### Out of scope

- Per-smell or per-technique individual folders (rejected during brainstorming as too granular — 736 files)
- Hybrid approach with popular items split out (rejected in favor of cleaner uniform structure)
- Articles, anti-patterns, history sections of refactoring.guru
- Robert C. Martin's *Clean Code* book content (separate future project, not refactoring.guru)

---

## 3. Location

```
Roadmap/Architecture/refactoring-guru/
├── 01-design-patterns/                       (existing, 22/22 complete)
├── 02-code-smells/                           (new)
│   ├── README.md
│   ├── 01-bloaters/
│   ├── 02-oo-abusers/
│   ├── 03-change-preventers/
│   ├── 04-dispensables/
│   └── 05-couplers/
└── 03-refactoring-techniques/                (new)
    ├── README.md
    ├── 01-composing-methods/
    ├── 02-moving-features/
    ├── 03-organizing-data/
    ├── 04-simplifying-conditionals/
    ├── 05-simplifying-method-calls/
    └── 06-dealing-with-generalization/
```

Each category folder contains an 8-file suite (see Section 5).

> **Structural note:** This deviates from the existing `01-design-patterns/` layout, where category folders (`01-creational/`, etc.) are pure containers and the 8-file suite lives one level deeper at the per-pattern level. The new sections place the 8-file suite at the **category** level itself — there is no per-smell or per-technique folder. This reflects the chosen Approach B (grouped coverage) and is intentional.

---

## 4. Content breakdown

### 4.1 Code Smells (5 categories, 22 smells total)

**`02-code-smells/01-bloaters/`** — code blocks that have grown too large
- Long Method
- Large Class
- Primitive Obsession
- Long Parameter List
- Data Clumps

**`02-code-smells/02-oo-abusers/`** — OOP features misused or underused
- Switch Statements
- Temporary Field
- Refused Bequest
- Alternative Classes with Different Interfaces

**`02-code-smells/03-change-preventers/`** — one change forces ripple changes elsewhere
- Divergent Change
- Shotgun Surgery
- Parallel Inheritance Hierarchies

**`02-code-smells/04-dispensables/`** — pointless code that can be removed
- Comments
- Duplicate Code
- Lazy Class
- Data Class
- Dead Code
- Speculative Generality

**`02-code-smells/05-couplers/`** — excessive coupling between classes/modules
- Feature Envy
- Inappropriate Intimacy
- Message Chains
- Middle Man

### 4.2 Refactoring Techniques (6 categories, ~70 techniques)

**`03-refactoring-techniques/01-composing-methods/`**
- Extract Method, Inline Method, Extract Variable, Inline Temp, Replace Temp with Query, Split Temporary Variable, Remove Assignments to Parameters, Replace Method with Method Object, Substitute Algorithm

**`03-refactoring-techniques/02-moving-features/`**
- Move Method, Move Field, Extract Class, Inline Class, Hide Delegate, Remove Middle Man, Introduce Foreign Method, Introduce Local Extension

**`03-refactoring-techniques/03-organizing-data/`**
- Self Encapsulate Field, Replace Data Value with Object, Change Value to Reference, Change Reference to Value, Replace Array with Object, Duplicate Observed Data, Change Unidirectional Association to Bidirectional, Change Bidirectional Association to Unidirectional, Replace Magic Number with Symbolic Constant, Encapsulate Field, Encapsulate Collection, Replace Type Code with Class, Replace Type Code with Subclasses, Replace Type Code with State/Strategy, Replace Subclass with Fields

**`03-refactoring-techniques/04-simplifying-conditionals/`**
- Decompose Conditional, Consolidate Conditional Expression, Consolidate Duplicate Conditional Fragments, Remove Control Flag, Replace Nested Conditional with Guard Clauses, Replace Conditional with Polymorphism, Introduce Null Object, Introduce Assertion

**`03-refactoring-techniques/05-simplifying-method-calls/`**
- Rename Method, Add Parameter, Remove Parameter, Separate Query from Modifier, Parameterize Method, Replace Parameter with Explicit Methods, Preserve Whole Object, Replace Parameter with Method Call, Introduce Parameter Object, Remove Setting Method, Hide Method, Replace Constructor with Factory Method, Encapsulate Downcast, Replace Error Code with Exception, Replace Exception with Test

**`03-refactoring-techniques/06-dealing-with-generalization/`**
- Pull Up Field, Pull Up Method, Pull Up Constructor Body, Push Down Method, Push Down Field, Extract Subclass, Extract Superclass, Extract Interface, Collapse Hierarchy, Form Template Method, Replace Inheritance with Delegation, Replace Delegation with Inheritance

---

## 5. The 8-file suite (per category)

Identical to the existing Design Patterns section (`TEMPLATE.md`).

| File | Focus | Audience |
|---|---|---|
| `junior.md` | "What is it?" "How to use?" — basic concepts, simple examples | Just learned the language |
| `middle.md` | "Why?" "When?" — trade-offs, real-world cases, related smells/techniques | 1–3 yr experience |
| `senior.md` | "How to optimize?" "How to architect?" — system-scale decisions | 3–7 yr experience |
| `professional.md` | Under the hood — runtime, memory, JIT, compiler effects | 7+ yr / specialist |
| `interview.md` | 50+ Q&A across all levels | Job preparation |
| `tasks.md` | 10+ hands-on exercises with solutions | Practice |
| `find-bug.md` | 10+ buggy snippets to fix | Critical reading |
| `optimize.md` | 10+ inefficient implementations to optimize | Performance practice |

**Reading order:** `junior → middle → senior → professional → tasks → find-bug → optimize → interview`.

Each file in a category covers **all** smells/techniques in that category collectively (e.g. `02-code-smells/01-bloaters/middle.md` discusses Long Method, Large Class, Primitive Obsession, Long Parameter List, and Data Clumps together — showing relationships, comparisons, and shared trade-offs).

---

## 6. Languages

All code examples in three languages: **Go**, **Java**, **Python** (matching Design Patterns).

Language-specific notes built into the content:

- **Java** — closest to Fowler's *Refactoring* book; classical OOP makes most techniques natural.
- **Python** — dynamic typing changes the picture: some techniques become trivial (Replace Type Code with Class), some become irrelevant (Encapsulate Downcast), some shift form (Introduce Parameter Object → `dataclass`/`NamedTuple`).
- **Go** — no inheritance; the entire `06-dealing-with-generalization/` category is largely "N/A — use composition; here is the Go-idiomatic alternative". This itself is pedagogically valuable: it shows what a refactoring technique is *really* about, separated from any specific language's syntax.

When a smell/technique does not apply to a given language, the file states this explicitly and explains why, then shows the idiomatic equivalent.

---

## 7. Execution order

**Section order:** Code Smells first, then Refactoring Techniques (smell = problem, technique = solution; natural reading order).

**Code Smells categories:**
1. `01-bloaters/` — most familiar entry point
2. `02-oo-abusers/`
3. `03-change-preventers/`
4. `04-dispensables/` — largest category (6 smells)
5. `05-couplers/`

**Refactoring Techniques categories:**
1. `01-composing-methods/` — most fundamental (Extract / Inline Method)
2. `02-moving-features/`
3. `03-organizing-data/` — largest category (15 techniques)
4. `04-simplifying-conditionals/`
5. `05-simplifying-method-calls/`
6. `06-dealing-with-generalization/` — most complex (inheritance), kept last

**Per-category file order:** `junior → middle → senior → professional → tasks → find-bug → optimize → interview`.

**Commit strategy:** one commit per completed category (8 files), matching the existing Design Patterns commit style — message lists topics covered, line count, and the running progress (e.g. "Code Smells 1/5 — Bloaters: 8-file suite ... 4,800 lines.").

---

## 8. Volume estimate

| Section | Categories | Files |
|---|---|---|
| Code Smells | 5 | 40 |
| Refactoring Techniques | 6 | 48 |
| READMEs (new + updated) | — | 3 |
| **Total** | **11** | **~91** |

Per-category suite is expected to span ~4,500–5,500 lines, comparable to existing Design Patterns suites (e.g. Chain of Responsibility 4,961 lines, Visitor 5,233 lines).

---

## 9. Cross-references

Smells and techniques are tightly linked. Each smell file lists the techniques that resolve it; each technique file lists the smells it addresses. Examples:

- **Long Method** (smell) → resolved by **Extract Method**, **Replace Method with Method Object**, **Decompose Conditional**, **Replace Temp with Query**
- **Switch Statements** (smell) → resolved by **Replace Conditional with Polymorphism**, **Replace Type Code with Subclasses / State / Strategy**
- **Feature Envy** (smell) → resolved by **Move Method**, **Extract Method**

Cross-links use relative markdown paths (e.g. `[Extract Method](../../03-refactoring-techniques/01-composing-methods/junior.md#extract-method)`).

---

## 10. Updates to existing files

- **`Roadmap/Architecture/refactoring-guru/README.md`** — update the Status section: mark Design Patterns COMPLETE, add Code Smells and Refactoring Techniques as in-progress sections; remove the "out of scope (planned as separate future projects)" note for these two; add new entries to the top-of-page summary table.
- **`Roadmap/Architecture/refactoring-guru/01-design-patterns/README.md`** — no change.

---

## 11. Out of scope clarifications

- **Per-smell / per-technique individual folders** — explicitly rejected. The chosen approach groups by category.
- **Robert C. Martin's *Clean Code*** — different source (book, not refactoring.guru); separate future project if desired.
- **Tools** (linters, IDE refactor commands) — discussed inline where relevant in `professional.md`, but not as standalone files.
- **Interpreter pattern, anti-patterns, history articles** — not covered.

---

## 12. Success criteria

- 88 content files + 3 READMEs created/updated, organized as in Section 3
- Each category suite covers every smell/technique listed in Section 4
- Code examples in Go, Java, Python; language non-applicability stated explicitly when relevant
- Smells link to resolving techniques and vice versa (Section 9)
- One commit per completed category, matching existing commit style
- The new sections appear in the top-level `refactoring-guru/README.md`
