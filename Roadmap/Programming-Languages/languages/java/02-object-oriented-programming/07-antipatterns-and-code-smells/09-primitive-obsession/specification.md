# Primitive Obsession — Specification

This document defines the **measurable, enforceable contract** for detecting and preventing Primitive Obsession in a Java codebase. It is written so that an engineer, a linter, or a build pipeline can apply it without further interpretation.

---

## 1. Definition

A method or field in a domain-layer type exhibits **Primitive Obsession** when one or more of its declared parameter, return, or field types is:

- `String`, `CharSequence`
- `int`, `long`, `short`, `byte` (or their boxed counterparts)
- `float`, `double` (or their boxed counterparts)
- `boolean` (or `Boolean`)
- `java.util.UUID`
- `java.math.BigDecimal`, `java.math.BigInteger`

**and** the domain concept the value represents is named (e.g. *email*, *amount*, *user id*, *currency code*, *quantity*, *age in years*, *is active*) rather than truly primitive (e.g. *array length*, *loop counter*).

A class is "in the domain layer" if it resides under a package configured as the domain package set (typically `..domain..`, `..model..`, `..core..`).

---

## 2. The Primary Metric — Domain Primitive Ratio (DPR)

For a given package set `P`:

```
DPR(P) = primitive_params_in_domain_methods(P) / total_params_in_domain_methods(P)
```

Where `primitive_params_in_domain_methods` counts parameters whose static type belongs to the list in §1 *and* whose declaring method is public or package-private on a class in `P`.

### Target thresholds

| Maturity | DPR (target) | Action |
|---|---|---|
| Green | ≤ 0.05 | Maintain. CI enforces zero net regressions. |
| Yellow | 0.05 – 0.20 | Active migration. Each PR must not raise DPR. |
| Orange | 0.20 – 0.50 | Backlog migration item per sprint. |
| Red | > 0.50 | Strategic refactor required. Codebase is at high bug risk. |

The metric is computed by a small AST scanner (JavaParser / Spoon) or, for a rough first estimate, regex. A reference Gradle task is shown in §7.

---

## 3. Secondary Metrics

### 3.1 String-to-named-type ratio

Ratio of `String` parameters to total parameters in domain methods. Should approach zero.

### 3.2 Long-id ratio

Ratio of `long` parameters whose names match `.*[Ii]d$|.*[Ii]dentifier$` to total such parameters. Should approach zero.

### 3.3 Boolean-flag count

Number of `boolean` parameters across the domain layer. Should approach zero (booleans are best replaced by enums; see §6.4).

### 3.4 Money-as-double count

Number of `double`/`float` parameters whose names match `.*(amount|price|cost|fee|balance|total).*`. Must equal zero.

---

## 4. Authoritative References

The following are the canonical references for typed-value modelling in modern Java. A code review may cite any of them as binding.

- **JEP 395: Records** — finalised in Java 16. Defines `record` declarations, accessor semantics, the canonical and compact constructors, and the implicit final / immutable contract. This is the present-day mechanism for value objects in Java.
- **JEP 401: Value Classes and Objects (Preview)** — the upcoming Project Valhalla feature that introduces the `value` modifier, identity-less classes, and the runtime support for flattened layouts. The migration path from a `record` to a `value record` is a single keyword change.
- **Project Valhalla design notes** — `https://openjdk.org/projects/valhalla/` — for the broader context of identity, value, primitive classes, and generic specialisation.
- **DDD reference (Evans, Vernon):** the Value Object pattern as the design rationale for tiny types.

JEPs are normative; this document references them rather than restating them.

---

## 5. Conformance Levels

A codebase claims conformance at one of three levels:

### Level 1 — Reactive

- No automated enforcement; code review catches new violations.
- Existing violations are tolerated.
- DPR is not tracked.

### Level 2 — Tracked

- DPR is computed in CI as an informational metric.
- New code must not introduce new violations (PR-level diff check).
- A migration backlog exists.

### Level 3 — Enforced

- ArchUnit (or equivalent) rules fail the build on any new primitive in a domain signature.
- Custom Checkstyle/PMD rules cover naming-pattern leakage (e.g. parameters named `email`, `amount`).
- DPR ≤ 0.05 globally; any exception is documented with a `@SuppressWarnings` justification and an ADR.

A team-level coding standard MUST declare the target level and the date by which it will be reached.

---

## 6. Enforcement Rule Samples

### 6.1 ArchUnit — forbid `String` in domain signatures

```java
@ArchTest
static final ArchRule no_string_in_domain_methods = methods()
    .that().arePublic()
    .and().areDeclaredInClassesThat().resideInAPackage("..domain..")
    .should().notHaveRawParameterTypes(String.class)
    .andShould().notHaveRawReturnType(String.class)
    .because("Domain layer must use value types instead of raw String (JEP 395 records)");
```

### 6.2 ArchUnit — forbid `long` IDs

```java
@ArchTest
static final ArchRule no_long_id_params = methods()
    .that().areDeclaredInClassesThat().resideInAPackage("..domain..")
    .and().haveNameMatching(".*[Bb]y[A-Z].*|.*[Ff]ind.*|.*[Gg]et.*|.*[Dd]elete.*")
    .should(new ArchCondition<JavaMethod>("not accept long/UUID for entity identifiers") {
        public void check(JavaMethod m, ConditionEvents events) {
            for (var p : m.getParameters()) {
                if ((p.getRawType().isAssignableTo(long.class) || p.getRawType().isAssignableTo(UUID.class))
                        && p.getName().toLowerCase().endsWith("id")) {
                    events.add(SimpleConditionEvent.violated(p,
                        "Use typed ID (UserId, OrderId) instead of long/UUID: " + m.getFullName()));
                }
            }
        }
    });
```

### 6.3 Checkstyle — flag named-concept String parameters

```xml
<module name="RegexpSingleline">
  <property name="format" value="\b(String|CharSequence)\s+(email|name|address|phone|sku|currency|country)\b"/>
  <property name="message" value="Replace raw String with a value class for this domain concept"/>
</module>
```

### 6.4 Checkstyle — boolean parameter flag

```xml
<module name="ParameterAssignment"/>
<module name="RegexpSingleline">
  <property name="format" value="\bboolean\s+\w+,"/>
  <property name="message" value="Replace boolean flag with an enum or split the method"/>
</module>
```

### 6.5 SpotBugs — money as double

SpotBugs ships a `FE_FLOATING_POINT_EQUALITY` detector. Custom rule via `bcel`:

```java
// Pseudocode — detector reports any method param of type double whose name
// matches MONEY_RX = Pattern.compile("(amount|price|cost|fee|balance|total)", CI)
```

### 6.6 Custom JavaParser scanner (DPR computation)

```java
public final class DprScanner {
    static final Set<String> PRIMITIVES = Set.of(
        "String", "int", "long", "short", "byte",
        "double", "float", "boolean",
        "UUID", "BigDecimal", "BigInteger");

    public static double compute(Path src, Predicate<String> isDomainPackage) throws IOException {
        var solver = new SymbolSolverCollectionStrategy()
            .collect(src).getParserConfiguration();
        long primitiveParams = 0, totalParams = 0;
        try (var walk = Files.walk(src)) {
            for (var file : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".java"))::iterator) {
                CompilationUnit cu = StaticJavaParser.parse(file);
                String pkg = cu.getPackageDeclaration().map(p -> p.getNameAsString()).orElse("");
                if (!isDomainPackage.test(pkg)) continue;
                for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
                    for (Parameter p : m.getParameters()) {
                        totalParams++;
                        if (PRIMITIVES.contains(p.getType().asString())) primitiveParams++;
                    }
                }
            }
        }
        return totalParams == 0 ? 0.0 : (double) primitiveParams / totalParams;
    }
}
```

---

## 7. CI Integration

```kotlin
// build.gradle.kts
tasks.register("dprCheck") {
    doLast {
        val dpr = DprScanner.compute(
            file("src/main/java").toPath(),
            { it.contains(".domain.") }
        )
        println("DPR = $dpr")
        val threshold = (project.findProperty("dpr.threshold") as String? ?: "0.05").toDouble()
        if (dpr > threshold) throw GradleException("DPR $dpr exceeds threshold $threshold")
    }
}
tasks.named("check") { dependsOn("dprCheck") }
```

The threshold ratchets downward over time. A PR cannot raise it.

---

## 8. Exemptions

A class may opt out of these rules by being annotated `@PrimitiveAdapter` (project-defined marker). The annotation is permitted only on:

- Controllers / REST adapters (where JSON arrives as strings).
- JDBC / JPA repositories (where columns are primitives).
- Mappers and assemblers (whose job is to convert primitives to value objects).

The annotation is forbidden on classes in the domain package. ArchUnit enforces this.

---

## 9. Acceptance Criteria

A codebase passes the Primitive Obsession specification when:

1. DPR ≤ 0.05 over the domain package set.
2. No `double`/`float` parameter names a monetary concept.
3. No `boolean` parameter exists in a public domain method (use enums or method split).
4. No `long`/`UUID` parameter ending in `id`/`identifier` exists in a domain method (use typed IDs).
5. CI fails on any regression of (1)–(4).
6. The team's coding standard cites JEP 395 and JEP 401 as the authoritative reference.

---

**Memorize this:** Primitive Obsession is measurable. DPR is the single number. ArchUnit, Checkstyle, and a small JavaParser scanner make the rule enforceable at build time. JEP 395 gives you the tool today (records); JEP 401 will make the tool free tomorrow (value classes). A specification you can run in CI is worth more than a coding-standards PDF nobody reads.
