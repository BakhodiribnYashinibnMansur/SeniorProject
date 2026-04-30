# Code Smells & Refactoring Techniques — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build two new sections under `Roadmap/Architecture/refactoring-guru/` — `02-code-smells/` (5 categories, 22 smells) and `03-refactoring-techniques/` (6 categories, ~70 techniques) — each category as a complete 8-file suite mirroring the existing Design Patterns section.

**Architecture:** Static markdown roadmap. Per-category folder with 8 level-stratified files (junior → professional + interview/tasks/find-bug/optimize). Code examples in Go, Java, Python. Cross-links between smells and resolving techniques via relative markdown paths.

**Tech Stack:** Markdown, Mermaid, Go 1.22+, Java 17+, Python 3.11+. Source content from refactoring.guru.

**Spec reference:** [docs/superpowers/specs/2026-04-29-code-smells-and-refactoring-techniques-design.md](../specs/2026-04-29-code-smells-and-refactoring-techniques-design.md)

**Style reference:** Use existing patterns under `Roadmap/Architecture/refactoring-guru/01-design-patterns/03-behavioral/` (Visitor, Chain of Responsibility, Template Method) as the quality bar for tone, depth, and length (~4,500–5,500 lines per category suite).

---

## File Map

```
Roadmap/Architecture/refactoring-guru/
├── README.md                                            [Task 2 — UPDATE]
├── 01-design-patterns/                                  (existing, no change)
├── 02-code-smells/
│   ├── README.md                                        [Task 3]
│   ├── 01-bloaters/                                     [Task 5 — 8 files]
│   ├── 02-oo-abusers/                                   [Task 6 — 8 files]
│   ├── 03-change-preventers/                            [Task 7 — 8 files]
│   ├── 04-dispensables/                                 [Task 8 — 8 files]
│   └── 05-couplers/                                     [Task 9 — 8 files]
└── 03-refactoring-techniques/
    ├── README.md                                        [Task 4]
    ├── 01-composing-methods/                            [Task 10 — 8 files]
    ├── 02-moving-features/                              [Task 11 — 8 files]
    ├── 03-organizing-data/                              [Task 12 — 8 files]
    ├── 04-simplifying-conditionals/                     [Task 13 — 8 files]
    ├── 05-simplifying-method-calls/                     [Task 14 — 8 files]
    └── 06-dealing-with-generalization/                  [Task 15 — 8 files]
```

**Total:** 88 content files + 3 READMEs (1 updated, 2 new).

---

## Task 1: Create folder structure

**Files:**
- Create directories only

- [ ] **Step 1: Create all directories**

```bash
cd /Users/mrb/Desktop/SeniorProject

mkdir -p Roadmap/Architecture/refactoring-guru/02-code-smells/{01-bloaters,02-oo-abusers,03-change-preventers,04-dispensables,05-couplers}
mkdir -p Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/{01-composing-methods,02-moving-features,03-organizing-data,04-simplifying-conditionals,05-simplifying-method-calls,06-dealing-with-generalization}
```

- [ ] **Step 2: Verify structure**

```bash
find Roadmap/Architecture/refactoring-guru/02-code-smells Roadmap/Architecture/refactoring-guru/03-refactoring-techniques -type d | sort
```

Expected: 13 directories (2 section roots + 5 smell categories + 6 technique categories).

- [ ] **Step 3: Add `.gitkeep` to track empty dirs and commit**

```bash
find Roadmap/Architecture/refactoring-guru/02-code-smells Roadmap/Architecture/refactoring-guru/03-refactoring-techniques -type d -empty -exec touch {}/.gitkeep \;
git add Roadmap/Architecture/refactoring-guru/02-code-smells Roadmap/Architecture/refactoring-guru/03-refactoring-techniques
git commit -m "scaffold: code-smells & refactoring-techniques directory structure"
```

---

## Task 2: Update top-level refactoring-guru README

**Files:**
- Modify: `Roadmap/Architecture/refactoring-guru/README.md`

- [ ] **Step 1: Read the current README**

```bash
cat Roadmap/Architecture/refactoring-guru/README.md
```

- [ ] **Step 2: Update sections**

Make the following changes:

**(a)** In the top summary table, replace the single Design Patterns row with three rows:

```markdown
| Section | Topics | Files |
|---|---|---|
| [Design Patterns](01-design-patterns/README.md) | 22 GoF patterns (Creational, Structural, Behavioral) | 176 |
| [Code Smells](02-code-smells/README.md) | 22 smells in 5 categories | 40 |
| [Refactoring Techniques](03-refactoring-techniques/README.md) | ~70 techniques in 6 categories | 48 |
```

**(b)** Remove the line: `> Code Smells and Refactoring Techniques are **out of scope** for this roadmap (planned as separate future projects).`

**(c)** Replace the "Status" section with:

```markdown
## Status

### ✅ Design Patterns — COMPLETE (22/22)
- ✅ Creational (5/5)
- ✅ Structural (7/7)
- ✅ Behavioral (10/10)

### ⏳ Code Smells — IN PROGRESS (0/5)
- ⬜ Bloaters
- ⬜ OO Abusers
- ⬜ Change Preventers
- ⬜ Dispensables
- ⬜ Couplers

### ⏳ Refactoring Techniques — PENDING (0/6)
- ⬜ Composing Methods
- ⬜ Moving Features Between Objects
- ⬜ Organizing Data
- ⬜ Simplifying Conditional Expressions
- ⬜ Simplifying Method Calls
- ⬜ Dealing with Generalization
```

**(d)** Add a new diagram after the existing "Categories at a Glance" mermaid graph:

```markdown
## Code Smells & Techniques at a Glance

\`\`\`mermaid
graph TD
    R[Refactoring]
    R --> CS[Code Smells - 22]
    R --> RT[Refactoring Techniques - ~70]

    CS --> B[Bloaters - 5]
    CS --> O[OO Abusers - 4]
    CS --> CP[Change Preventers - 3]
    CS --> D[Dispensables - 6]
    CS --> C[Couplers - 4]

    RT --> CM[Composing Methods]
    RT --> MF[Moving Features]
    RT --> OD[Organizing Data]
    RT --> SC[Simplifying Conditionals]
    RT --> SM[Simplifying Method Calls]
    RT --> DG[Dealing with Generalization]
\`\`\`
```

**(e)** As Section 11 (after current sections), add **Cross-References** explaining the relationship between smells (problem) and techniques (solution).

- [ ] **Step 3: Verify with `git diff`**

```bash
git diff Roadmap/Architecture/refactoring-guru/README.md
```

Confirm the four content blocks above are present and the "out of scope" line is gone.

- [ ] **Step 4: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/README.md
git commit -m "docs(refactoring-guru): add Code Smells & Refactoring Techniques sections to top README"
```

---

## Task 3: Create `02-code-smells/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/02-code-smells/README.md`

- [ ] **Step 1: Write the README**

Required sections:

1. **Title + intro** — what code smells are (Fowler's definition: surface indications of deeper problems), why they matter, that this section follows refactoring.guru's classification
2. **The 5 categories** — short one-line description for each: Bloaters, OO Abusers, Change Preventers, Dispensables, Couplers
3. **Per-category smell table** — for each category, a table listing every smell with a one-line description and a primary resolving technique (with relative link to `../03-refactoring-techniques/...`)
4. **How to read this section** — recommend `junior → middle → senior → professional → tasks → find-bug → optimize → interview` order; note that each category's 8-file suite covers all smells in that category collectively
5. **Mermaid diagram** — overview of the 5 categories and their smells (similar to the existing design-patterns diagram)
6. **Status table** — checkboxes for each of the 5 categories, all unchecked initially

Tables must include all 22 smells (5 + 4 + 3 + 6 + 4) — see spec Section 4.1 for the full list.

- [ ] **Step 2: Verify all 22 smells listed**

```bash
grep -E "^\| (Long Method|Large Class|Primitive Obsession|Long Parameter List|Data Clumps|Switch Statements|Temporary Field|Refused Bequest|Alternative Classes|Divergent Change|Shotgun Surgery|Parallel Inheritance|Comments|Duplicate Code|Lazy Class|Data Class|Dead Code|Speculative Generality|Feature Envy|Inappropriate Intimacy|Message Chains|Middle Man)" Roadmap/Architecture/refactoring-guru/02-code-smells/README.md | wc -l
```

Expected: 22.

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/02-code-smells/README.md
git commit -m "docs(code-smells): top-level README with 5 categories and 22 smells"
```

---

## Task 4: Create `03-refactoring-techniques/README.md`

**Files:**
- Create: `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/README.md`

- [ ] **Step 1: Write the README**

Required sections:

1. **Title + intro** — what refactoring is (Fowler's definition: changing internal structure without changing external behavior), why a catalog matters, that this section follows refactoring.guru's classification
2. **The 6 categories** — short one-line description for each: Composing Methods, Moving Features Between Objects, Organizing Data, Simplifying Conditional Expressions, Simplifying Method Calls, Dealing with Generalization
3. **Per-category technique table** — for each category, a table listing every technique with: one-line description, smell(s) it resolves (with relative link to `../02-code-smells/...`)
4. **Usage in three languages** — note that some techniques are N/A in Go (no inheritance) and shift form in Python (dynamic typing); the suites address this explicitly
5. **Mermaid diagram** — overview of the 6 categories
6. **Status table** — checkboxes for each of the 6 categories, all unchecked

Tables must include all techniques listed in spec Section 4.2 (~70 techniques across 6 categories).

- [ ] **Step 2: Verify category presence**

```bash
grep -E "^## .*(Composing Methods|Moving Features|Organizing Data|Simplifying Conditional|Simplifying Method Calls|Dealing with Generalization)" Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/README.md | wc -l
```

Expected: 6.

- [ ] **Step 3: Commit**

```bash
git add Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/README.md
git commit -m "docs(refactoring-techniques): top-level README with 6 categories and ~70 techniques"
```

---

## Per-category task template (applied to Tasks 5–15)

Each per-category task follows the same 10-step pattern. To avoid repetition, the pattern is defined here once. Tasks 5–15 below specify only the **per-category content brief**; they all use the steps documented in this section.

### Standard per-category steps

- [ ] **Step A: Remove `.gitkeep`** (created in Task 1)

```bash
rm Roadmap/Architecture/refactoring-guru/<section>/<category>/.gitkeep
```

- [ ] **Step B: Write `junior.md`**

Sections (from `TEMPLATE.md` — see lines 60–125 of `TEMPLATE.md`):
Introduction · Prerequisites · Glossary · Core Concepts · Pros & Cons · Use Cases · Code Examples (Go + Java + Python) · Error Handling · Best Practices · Edge Cases & Pitfalls · Common Mistakes · Tricky Points · Cheat Sheet · Summary · Further Reading · Related Topics · Diagrams.

Cover every unit listed in the category brief. For each unit, include: definition, simple symptom example, "what it looks like" code in **all three** languages (Java first, Python second, Go third). When a unit is N/A in a language, state so explicitly with a one-line reason and the idiomatic alternative (e.g., "Refused Bequest — N/A in Go: no inheritance; the equivalent issue is embedding-related interface bloat").

Target: 600–900 lines.

- [ ] **Step C: Write `middle.md`**

Sections: Why this category matters · When each unit appears (real-world triggers) · Trade-offs · Comparison table within category · Real-world cases (production examples — name the codebase / company / library) · Related units in other categories · Migration vs. live system considerations.

For each unit in the category: 2–3 production-grade scenarios (e.g., for **Long Method**: a 400-line `processOrder()` in legacy commerce code; for **Switch Statements**: a payment-method dispatcher).

Target: 700–1,000 lines.

- [ ] **Step D: Write `senior.md`**

Sections: Architecture-scale decisions · System-wide patterns (e.g., what Long Method looks like at module level) · Refactoring strategies that resolve the category · Trade-offs at architectural scale · Migration patterns for large codebases · Tooling (linters, IDE refactor commands, custom AST checks) · How CI/CD integrates with these.

Cite specific tools (SonarQube rules, IntelliJ inspections, ESLint plugins, golangci-lint, Pylint, ArchUnit, Spotless, etc.).

Target: 700–1,000 lines.

- [ ] **Step E: Write `professional.md`**

Sections: Under the hood — runtime impact, JIT inlining, GC pressure, cache locality, allocation patterns · How the smell affects bytecode/IR/assembly · Compiler limits (e.g., HotSpot `MaxInlineSize` for Long Method, `escape analysis` for Primitive Obsession) · Profiling techniques to detect at runtime · Cross-language differences in cost.

Reference HotSpot, V8, CPython internals where relevant. Use `javap`, `objdump`, `go build -gcflags`, CPython `dis`. Provide microbenchmarks (JMH for Java, `pytest-benchmark` for Python, `testing.B` for Go) demonstrating measurable impact.

Target: 700–1,000 lines.

- [ ] **Step F: Write `tasks.md`**

10+ exercises. For each: problem statement, starter code (one of the three languages), constraints, hints, full solution. At least 3 of the 10 in each language.

Each exercise targets a specific unit in the category (don't just rewrite the same smell with different variable names).

Target: 500–800 lines.

- [ ] **Step G: Write `find-bug.md`**

10+ buggy snippets. For each: code with the bug, hint, full diagnosis, fix. The bug must relate to a unit in this category (e.g., for Bloaters: a `Long Method` that subtly mishandles a side effect; for OO Abusers: a `Switch Statements` block missing a case for a new enum value).

Mix all three languages — at least 3 in each.

Target: 500–800 lines.

- [ ] **Step H: Write `optimize.md`**

10+ inefficient implementations to optimize. For each: original code, performance characteristic to improve, optimized version, before/after metrics or `Big-O`.

For Code Smells categories, optimization = refactoring that reduces smell + measurable performance improvement. For Refactoring Techniques categories, optimization = applying the technique correctly to avoid common pitfalls (e.g., Extract Method without introducing extra allocations).

Target: 500–800 lines.

- [ ] **Step I: Write `interview.md`**

50+ Q&A across all levels. Required mix:
- 15 junior-level (definitions, "what does this code smell?")
- 15 middle-level ("when does this appear in real code?", "what technique resolves it?")
- 10 senior-level (architecture / scaling)
- 10 professional-level (runtime / compiler)

Each answer must be ≥1 paragraph; many should include a code snippet. Cross-reference resolving techniques (or smells) with relative links.

Target: 800–1,200 lines.

- [ ] **Step J: Verify cross-links resolve**

```bash
# Find all relative links in the new files and check the targets exist
for f in Roadmap/Architecture/refactoring-guru/<section>/<category>/*.md; do
  grep -oE '\]\(\.\.[^)]+\)' "$f" | sed 's/](\(.*\))/\1/' | while read link; do
    target="$(dirname "$f")/$link"
    target="${target%#*}"  # strip anchor
    [ -f "$target" ] || echo "BROKEN in $f: $link"
  done
done
```

Expected: no `BROKEN` output (links to not-yet-written sibling categories are acceptable for now — verify only links to already-existing files like the design-patterns folder and other completed categories).

- [ ] **Step K: Commit**

Use the existing design-patterns commit style. Format:

```bash
git add Roadmap/Architecture/refactoring-guru/<section>/<category>/
git commit -m "$(cat <<'EOF'
<Category Name> 8-file suite: junior, middle, senior, professional, interview, tasks, find-bug, optimize. <Section Name> <N>/<TOTAL>. Topics: <comma-separated key topics from the category — units, real-world cases, languages, tooling, runtime>. <line-count> lines.
EOF
)"
```

(Match the verbosity of e.g. commit `92b1a57` for the Chain of Responsibility suite.)

---

## Task 5: Code Smells — Bloaters

**Files:** `Roadmap/Architecture/refactoring-guru/02-code-smells/01-bloaters/{junior,middle,senior,professional,interview,tasks,find-bug,optimize}.md`

**Units to cover:**
1. **Long Method** — a method with too many lines (rule of thumb: > 10–20 lines of business logic)
2. **Large Class** — a class doing too much; many fields, many methods
3. **Primitive Obsession** — using primitives where small classes belong (`String email`, `int amount`, `String[] address`)
4. **Long Parameter List** — > 3–4 parameters; signals missing object
5. **Data Clumps** — same group of fields appearing together repeatedly

**Resolving techniques to cross-link** (in `../../03-refactoring-techniques/`):
- Long Method → Extract Method, Replace Method with Method Object, Decompose Conditional, Replace Temp with Query
- Large Class → Extract Class, Extract Subclass, Extract Interface
- Primitive Obsession → Replace Data Value with Object, Replace Type Code with Class/Subclasses/State, Introduce Parameter Object, Replace Array with Object
- Long Parameter List → Replace Parameter with Method Call, Preserve Whole Object, Introduce Parameter Object
- Data Clumps → Extract Class, Introduce Parameter Object, Preserve Whole Object

**Apply Steps A–K** from "Standard per-category steps" above. Commit message identifier: `Bloaters` · `Code Smells 1/5`.

---

## Task 6: Code Smells — OO Abusers

**Files:** `Roadmap/Architecture/refactoring-guru/02-code-smells/02-oo-abusers/*.md`

**Units to cover:**
1. **Switch Statements** — long `switch` / chained `if-else` on type code or enum
2. **Temporary Field** — field used only sometimes; null otherwise
3. **Refused Bequest** — subclass uses only some inherited methods, refuses or no-ops the rest
4. **Alternative Classes with Different Interfaces** — two classes do similar things but expose different APIs

**Cross-link techniques:**
- Switch Statements → Replace Conditional with Polymorphism, Replace Type Code with Subclasses / State / Strategy, Introduce Null Object
- Temporary Field → Extract Class, Introduce Null Object, Replace Method with Method Object
- Refused Bequest → Push Down Method, Push Down Field, Replace Inheritance with Delegation, Extract Superclass
- Alternative Classes — Rename Method, Move Method, Extract Superclass

**Language note:** Refused Bequest is N/A in Go (no inheritance); the equivalent is embedding misuse. Cover this explicitly in `junior.md`.

Apply Steps A–K. Commit identifier: `OO Abusers` · `Code Smells 2/5`.

---

## Task 7: Code Smells — Change Preventers

**Files:** `Roadmap/Architecture/refactoring-guru/02-code-smells/03-change-preventers/*.md`

**Units to cover:**
1. **Divergent Change** — one class changed for many different reasons (violates SRP)
2. **Shotgun Surgery** — one logical change forces edits in many classes (opposite of Divergent Change)
3. **Parallel Inheritance Hierarchies** — every subclass of one hierarchy requires a corresponding subclass in another

**Cross-link techniques:**
- Divergent Change → Extract Class, Move Method, Move Field
- Shotgun Surgery → Move Method, Move Field, Inline Class
- Parallel Inheritance Hierarchies → Move Method, Move Field

**Senior-level note:** This category is the bridge to **architectural** SRP and OCP discussions. `senior.md` should include a substantive section on how these smells are detected at the module/service boundary level (not just within a single class).

Apply Steps A–K. Commit identifier: `Change Preventers` · `Code Smells 3/5`.

---

## Task 8: Code Smells — Dispensables

**Files:** `Roadmap/Architecture/refactoring-guru/02-code-smells/04-dispensables/*.md`

**Units to cover (6 — largest category):**
1. **Comments** — comments that compensate for unclear code (vs. legitimate "why" comments)
2. **Duplicate Code** — same/similar fragments in multiple places
3. **Lazy Class** — class that does too little to justify its existence
4. **Data Class** — class with only fields & accessors, no behavior
5. **Dead Code** — code never executed
6. **Speculative Generality** — abstractions added "just in case" that no one needs

**Cross-link techniques:**
- Comments → Extract Method, Rename Method, Introduce Assertion
- Duplicate Code → Extract Method, Pull Up Method, Form Template Method, Substitute Algorithm
- Lazy Class → Inline Class, Collapse Hierarchy
- Data Class → Move Method, Encapsulate Field, Encapsulate Collection
- Dead Code — none directly; just delete (note this in the file)
- Speculative Generality → Collapse Hierarchy, Inline Class, Remove Parameter, Rename Method

**Nuance to capture in `middle.md`:** Comments are not always a smell — distinguish "compensatory comments" (smell) from "intent comments" (good). This is a frequent interview gotcha.

Apply Steps A–K. Commit identifier: `Dispensables` · `Code Smells 4/5`.

---

## Task 9: Code Smells — Couplers

**Files:** `Roadmap/Architecture/refactoring-guru/02-code-smells/05-couplers/*.md`

**Units to cover:**
1. **Feature Envy** — method more interested in another class's data than its own
2. **Inappropriate Intimacy** — two classes know too much about each other's internals
3. **Message Chains** — `a.getB().getC().getD().doIt()` (Law of Demeter violation)
4. **Middle Man** — class that delegates most of its work to another (excessive forwarding)

**Cross-link techniques:**
- Feature Envy → Move Method, Extract Method
- Inappropriate Intimacy → Move Method, Move Field, Change Bidirectional Association to Unidirectional, Replace Inheritance with Delegation, Hide Delegate
- Message Chains → Hide Delegate, Extract Method, Move Method
- Middle Man → Remove Middle Man, Inline Method, Replace Delegation with Inheritance

**Notable real-world hook for `senior.md`:** Demeter / "Tell, Don't Ask" principle — relate to Hexagonal architecture / Domain-Driven Design.

Apply Steps A–K. Commit identifier: `Couplers` · `Code Smells 5/5 — COMPLETE`.

After this task: update `Roadmap/Architecture/refactoring-guru/README.md` Status section so all 5 smell categories show ✅, and the section line reads `### ✅ Code Smells — COMPLETE (5/5)`. Include this update in the same commit (`git add` both the category folder and the README).

---

## Task 10: Refactoring Techniques — Composing Methods

**Files:** `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/01-composing-methods/*.md`

**Units to cover:**
1. **Extract Method** — turn a fragment into a new method
2. **Inline Method** — body of method is as clear as its name; replace calls with body
3. **Extract Variable** — give a complex expression a name
4. **Inline Temp** — replace a one-use temp with the expression
5. **Replace Temp with Query** — turn a temp variable into a method
6. **Split Temporary Variable** — if a temp is assigned twice for different purposes, split it
7. **Remove Assignments to Parameters** — don't reassign parameters; use a local
8. **Replace Method with Method Object** — turn a long method with many local variables into a class
9. **Substitute Algorithm** — replace algorithm body with a clearer one

**Smells resolved (cross-link `../../02-code-smells/`):**
- Extract Method, Replace Method with Method Object, Replace Temp with Query → Long Method, Duplicate Code
- Inline Method → Lazy Class, Speculative Generality
- Extract Variable → Long Method (sub-issue: complex expressions)
- Substitute Algorithm → Duplicate Code, Long Method

**Language notes for `junior.md`:**
- Java: classic Fowler examples; Eclipse/IntelliJ have built-in refactor commands
- Python: simpler — closure / nested function as alternative to method object
- Go: `Replace Method with Method Object` ↔ extract a struct with methods; `Remove Assignments to Parameters` is unusual (Go params are pass-by-value)

**Performance angle for `professional.md`:** Extract Method usually free (HotSpot inlines short methods automatically; check `MaxInlineSize=35` bytes default), but can hurt if the extracted method exceeds the inline budget. Show with JMH benchmark.

Apply Steps A–K. Commit identifier: `Composing Methods` · `Refactoring Techniques 1/6`.

---

## Task 11: Refactoring Techniques — Moving Features Between Objects

**Files:** `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/02-moving-features/*.md`

**Units to cover:**
1. **Move Method** — method better belongs to another class
2. **Move Field** — field better belongs to another class
3. **Extract Class** — split a class doing two jobs into two
4. **Inline Class** — class doing too little; merge into another
5. **Hide Delegate** — wrap delegate access (`a.getB().doIt()` → `a.doIt()`)
6. **Remove Middle Man** — opposite: too many `Hide Delegate` calls; expose the delegate
7. **Introduce Foreign Method** — add a method to a server class via wrapper when you can't modify it
8. **Introduce Local Extension** — a fuller version (subclass or wrapper) when many foreign methods are needed

**Smells resolved:**
- Move Method/Field → Feature Envy, Inappropriate Intimacy, Divergent Change, Shotgun Surgery
- Extract Class → Large Class, Data Clumps
- Inline Class → Lazy Class, Speculative Generality
- Hide Delegate → Message Chains
- Remove Middle Man → Middle Man

**Senior-level discussion:** Move Method at module/service level (microservice extraction is "Move Method at scale"). Reference the Strangler Fig pattern.

Apply Steps A–K. Commit identifier: `Moving Features` · `Refactoring Techniques 2/6`.

---

## Task 12: Refactoring Techniques — Organizing Data

**Files:** `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/03-organizing-data/*.md`

**Units to cover (largest — 15 techniques):**
1. **Self Encapsulate Field** — use accessors even from inside the class
2. **Replace Data Value with Object** — `String customerName` → `Customer`
3. **Change Value to Reference** — convert a value object to a reference (single instance per logical id)
4. **Change Reference to Value** — opposite: prefer value semantics for small immutable objects
5. **Replace Array with Object** — when array elements have different meanings (`row[0]` is name, `row[1]` is age)
6. **Duplicate Observed Data** — when domain data lives in a UI widget; duplicate to keep them in sync
7. **Change Unidirectional Association to Bidirectional**
8. **Change Bidirectional Association to Unidirectional**
9. **Replace Magic Number with Symbolic Constant** — `if (h > 1.78)` → `if (h > AVG_HEIGHT)`
10. **Encapsulate Field** — make field private + accessors
11. **Encapsulate Collection** — never expose mutable collection field; return read-only / unmodifiable view
12. **Replace Type Code with Class** — `int BLOOD_TYPE_A = 1` → `BloodType` class with constants
13. **Replace Type Code with Subclasses** — when behavior differs per type
14. **Replace Type Code with State/Strategy** — when type changes at runtime
15. **Replace Subclass with Fields** — when subclasses differ only by constant data

**Smells resolved:**
- Replace Data Value with Object, Replace Array with Object, Replace Magic Number → Primitive Obsession
- Replace Type Code with * → Switch Statements, Primitive Obsession
- Encapsulate Field, Encapsulate Collection → Inappropriate Intimacy
- Self Encapsulate Field → general OO hygiene

**Language nuance for `junior.md`:** Python `dataclass`, Go struct + methods, Java records (Java 16+) collapse several of these techniques into language features.

Apply Steps A–K. Commit identifier: `Organizing Data` · `Refactoring Techniques 3/6`.

---

## Task 13: Refactoring Techniques — Simplifying Conditional Expressions

**Files:** `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/04-simplifying-conditionals/*.md`

**Units to cover:**
1. **Decompose Conditional** — extract complex `if-else-then` parts into named methods
2. **Consolidate Conditional Expression** — chain of `if`s with the same body → single `||` / combined check
3. **Consolidate Duplicate Conditional Fragments** — same line in every branch → outside the conditional
4. **Remove Control Flag** — `boolean done = false; while (!done) { ... done = true; }` → `break` / `return`
5. **Replace Nested Conditional with Guard Clauses** — early returns instead of `if-else` pyramids
6. **Replace Conditional with Polymorphism** — `switch` on type → polymorphism
7. **Introduce Null Object** — replace `null` checks with a no-op object
8. **Introduce Assertion** — runtime check at function start documents implicit assumptions

**Smells resolved:**
- Decompose Conditional, Replace Nested with Guard Clauses → Long Method
- Replace Conditional with Polymorphism → Switch Statements
- Introduce Null Object → Switch Statements (null-checking branches), Temporary Field

**Senior-level connection:** Replace Conditional with Polymorphism is the bridge to the State and Strategy design patterns; cross-link to `01-design-patterns/03-behavioral/07-state/` and `08-strategy/`.

**Performance angle for `professional.md`:** Polymorphism vs. switch — JIT can devirtualize monomorphic call sites, but megamorphic sites lose this. Demonstrate with JMH.

Apply Steps A–K. Commit identifier: `Simplifying Conditionals` · `Refactoring Techniques 4/6`.

---

## Task 14: Refactoring Techniques — Simplifying Method Calls

**Files:** `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/05-simplifying-method-calls/*.md`

**Units to cover (14 techniques):**
1. **Rename Method** — better name
2. **Add Parameter** — needs more info; add carefully (compatibility)
3. **Remove Parameter** — no longer needed
4. **Separate Query from Modifier** — a method that returns a value should not have side effects
5. **Parameterize Method** — several similar methods differing only in a constant → one method, value as param
6. **Replace Parameter with Explicit Methods** — opposite: when a parameter triggers different code paths, split into separate methods
7. **Preserve Whole Object** — pass the object instead of multiple of its fields
8. **Replace Parameter with Method Call** — caller already has access to the value; let callee compute it
9. **Introduce Parameter Object** — group parameters into an object
10. **Remove Setting Method** — when a field shouldn't change after construction
11. **Hide Method** — make a method private (or move down)
12. **Replace Constructor with Factory Method** — when you need named construction, conditional creation, or subclass selection
13. **Encapsulate Downcast** — push downcast inside the method that returns the value
14. **Replace Error Code with Exception** / **Replace Exception with Test** — preferred direction depends on whether the condition is exceptional or expected

**Smells resolved:**
- Rename Method → Comments, Alternative Classes
- Preserve Whole Object, Introduce Parameter Object → Long Parameter List, Data Clumps
- Replace Constructor with Factory Method → Switch Statements (conditional creation)
- Hide Method → Inappropriate Intimacy

**Language nuance:** Replace Error Code with Exception is divisive — Go and Rust go the *opposite* way. Cover idiomatic Go (`error` returns) and Python's "EAFP" (`Easier to Ask Forgiveness than Permission`). `senior.md` must address this disagreement directly — it's a frequent interview question.

Apply Steps A–K. Commit identifier: `Simplifying Method Calls` · `Refactoring Techniques 5/6`.

---

## Task 15: Refactoring Techniques — Dealing with Generalization

**Files:** `Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/06-dealing-with-generalization/*.md`

**Units to cover:**
1. **Pull Up Field** — common field in multiple subclasses → superclass
2. **Pull Up Method** — common method → superclass
3. **Pull Up Constructor Body** — common constructor work → superclass constructor
4. **Push Down Method** — method only relevant to one subclass → push down
5. **Push Down Field** — field only used by some subclasses → push down
6. **Extract Subclass** — class has features used only sometimes → subclass for that case
7. **Extract Superclass** — two classes share features → common superclass
8. **Extract Interface** — same set of operations used together → interface
9. **Collapse Hierarchy** — superclass and subclass not different enough → merge
10. **Form Template Method** — two methods do similar work in different ways → Template Method pattern (cross-link to `01-design-patterns/03-behavioral/09-template-method/`)
11. **Replace Inheritance with Delegation** — subclass uses only part of superclass; refactor to composition
12. **Replace Delegation with Inheritance** — opposite: when delegation is to a single object covering its full interface, inheritance simplifies

**Smells resolved:**
- Pull Up * → Duplicate Code
- Push Down *, Replace Inheritance with Delegation → Refused Bequest
- Extract Superclass, Form Template Method → Duplicate Code, Alternative Classes
- Extract Interface → Alternative Classes
- Collapse Hierarchy → Lazy Class, Speculative Generality

**LANGUAGE WARNING — Go:** Most of this category is N/A in Go (no inheritance). The Go sections must explicitly state this and present **composition + embedding + interface** as the idiomatic alternative. This is a **major teaching moment** — devote substantial space in `junior.md` and `middle.md` to "what does this technique even mean in a no-inheritance language?". Reference Go's `io.Reader` / `io.Writer` interface composition.

**Python nuance:** Multiple inheritance and MRO (`C3 linearization`) make some of these techniques (Pull Up, Extract Superclass) more nuanced — cover briefly in `professional.md`.

Apply Steps A–K. Commit identifier: `Dealing with Generalization` · `Refactoring Techniques 6/6 — COMPLETE`.

After this task: update `Roadmap/Architecture/refactoring-guru/README.md` Status section so all 6 technique categories show ✅, the section line reads `### ✅ Refactoring Techniques — COMPLETE (6/6)`. Include this update in the same commit.

---

## Task 16: Final consistency pass

**Files:** all new files

- [ ] **Step 1: Verify line counts**

```bash
for category in Roadmap/Architecture/refactoring-guru/02-code-smells/*/ Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/*/; do
  total=$(wc -l "$category"*.md 2>/dev/null | tail -1 | awk '{print $1}')
  echo "$category — $total lines"
done
```

Expected: each category ≥ 4,500 lines (matching design-patterns suite quality bar).

- [ ] **Step 2: Verify all 88 content files exist**

```bash
ls Roadmap/Architecture/refactoring-guru/02-code-smells/*/junior.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/middle.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/senior.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/professional.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/interview.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/tasks.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/find-bug.md \
   Roadmap/Architecture/refactoring-guru/02-code-smells/*/optimize.md \
   Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/*/*.md \
   2>/dev/null | wc -l
```

Expected: 88.

- [ ] **Step 3: Verify cross-links smell ↔ technique**

```bash
# Every smell file should reference at least one technique file (resolution path)
for f in Roadmap/Architecture/refactoring-guru/02-code-smells/*/*.md; do
  if ! grep -q "03-refactoring-techniques" "$f"; then
    echo "MISSING technique cross-link in: $f"
  fi
done

# Every technique file should reference at least one smell file
for f in Roadmap/Architecture/refactoring-guru/03-refactoring-techniques/*/*.md; do
  if ! grep -q "02-code-smells" "$f"; then
    echo "MISSING smell cross-link in: $f"
  fi
done
```

Expected: no `MISSING` output.

- [ ] **Step 4: Verify Status table fully checked**

```bash
grep -E "^### " Roadmap/Architecture/refactoring-guru/README.md
```

Expected output includes:
```
### ✅ Design Patterns — COMPLETE (22/22)
### ✅ Code Smells — COMPLETE (5/5)
### ✅ Refactoring Techniques — COMPLETE (6/6)
```

- [ ] **Step 5: Final commit (if any cleanup needed)**

```bash
git status
# If any cleanup needed:
git add ...
git commit -m "docs(refactoring-guru): final consistency pass — code smells & refactoring techniques sections complete"
```

---

## Recap of expected commits (in order)

1. `scaffold: code-smells & refactoring-techniques directory structure` — Task 1
2. `docs(refactoring-guru): add Code Smells & Refactoring Techniques sections to top README` — Task 2
3. `docs(code-smells): top-level README with 5 categories and 22 smells` — Task 3
4. `docs(refactoring-techniques): top-level README with 6 categories and ~70 techniques` — Task 4
5. `Bloaters 8-file suite: ... Code Smells 1/5. ...` — Task 5
6. `OO Abusers 8-file suite: ... Code Smells 2/5. ...` — Task 6
7. `Change Preventers 8-file suite: ... Code Smells 3/5. ...` — Task 7
8. `Dispensables 8-file suite: ... Code Smells 4/5. ...` — Task 8
9. `Couplers 8-file suite: ... Code Smells 5/5 — COMPLETE. ...` — Task 9
10. `Composing Methods 8-file suite: ... Refactoring Techniques 1/6. ...` — Task 10
11. `Moving Features 8-file suite: ... Refactoring Techniques 2/6. ...` — Task 11
12. `Organizing Data 8-file suite: ... Refactoring Techniques 3/6. ...` — Task 12
13. `Simplifying Conditionals 8-file suite: ... Refactoring Techniques 4/6. ...` — Task 13
14. `Simplifying Method Calls 8-file suite: ... Refactoring Techniques 5/6. ...` — Task 14
15. `Dealing with Generalization 8-file suite: ... Refactoring Techniques 6/6 — COMPLETE. ...` — Task 15
16. (optional) `docs(refactoring-guru): final consistency pass ...` — Task 16

**Total: 15–16 commits, 88 content files + 3 READMEs.**
