# Primitive Obsession — Interview

Twenty questions covering definitions, design, performance, tooling, and migration. Answers are concise and assume working Java knowledge.

---

### Q1. What is Primitive Obsession?

The code smell of using language primitives (`String`, `int`, `long`, `boolean`, `UUID`, `BigDecimal`) to represent domain concepts that deserve their own types. The compiler then cannot distinguish `userId` from `orderId`, or `USD` from `EUR`, and bugs that should be type errors become runtime errors.

---

### Q2. Why is `String email` a problem if it works?

It works until you write a method like `register(String email, String name)` and a caller swaps the arguments. The compiler is silent. `Email` and `FullName` as record types make the swap a compile error. The bug-class is eliminated, not merely hunted.

---

### Q3. Which Java feature is the present-day tool for value objects?

Records, finalised in **JEP 395 (Java 16)**. They give immutability, equals/hashCode, toString, and a compact canonical constructor for validation — all in one declaration.

---

### Q4. What does Project Valhalla bring?

Value classes and primitive classes. **JEP 401 (Value Classes and Objects — Preview)** introduces the `value` modifier: types with no identity, no synchronisation, and runtime support for flattened layouts. A `value record` will eventually have the cost of a primitive while keeping the safety of a typed wrapper.

---

### Q5. Why prefer `record Money(BigDecimal amount, Currency currency)` over `BigDecimal amount` plus `String currency`?

Three reasons:
1. They cannot be passed separately — no missing-currency bugs.
2. `Money.add(Money)` enforces currency match at the API level.
3. Currency-aware scale and rounding live with the data, not scattered at call sites.

---

### Q6. Why not use `double` for money?

`double` is binary floating-point. `0.1 + 0.2` is not `0.3`. Financial calculations require decimal precision, and accumulated rounding errors over thousands of transactions break reconciliation. Use `BigDecimal` (inside a `Money` record) with explicit scale and rounding mode.

---

### Q7. Which rounding mode for money?

`RoundingMode.HALF_EVEN` (banker's rounding). It avoids the systematic upward bias of `HALF_UP` over many roundings — important for any institution that audits its books.

---

### Q8. Why typed IDs (`UserId`, `OrderId`) instead of `UUID` or `long`?

To prevent ID confusion. `delete(UUID id)` accepts any UUID; `delete(UserId id)` accepts only one entity's identifier. Compile-time safety replaces runtime "wrong row" bugs.

---

### Q9. What is the "tiny types" or "micro-types" pattern?

The practice of wrapping every domain primitive in a named type — `FirstName`, `LastName`, `Age`, `Email` — even when the wrapper holds a single field. The discipline eliminates ambiguity at every call site.

---

### Q10. What is the runtime cost of a record wrapper today?

In the worst case, a heap allocation (~24 bytes for one `long` field). In the best case, **scalar replacement** by Escape Analysis eliminates the allocation entirely. For business logic, the cost is invisible; for inner numerical loops, profile first.

---

### Q11. How does Escape Analysis help?

If the JIT can prove a record's reference never escapes the method that allocates it, it decomposes the record into its primitive fields, which live in registers or on the stack. The record exists logically but not physically. C2 does this aggressively for short-lived records.

---

### Q12. What is autoboxing and why does it matter for IDs?

Boxing converts a primitive (`long`) into its wrapper (`Long`), allocating an object. A `Map<Long, User>` boxes every lookup key. A typed `record UserId(long value)` holds the `long` directly in the record; the boxing penalty is gone (the record itself is the key, with an unboxed `long` inside).

---

### Q13. How would you enforce "no raw `String` in domain APIs" across a team?

ArchUnit rules in CI. A single test class with rules like `methods().that().areDeclaredInClassesThat().resideInAPackage("..domain..").should().notHaveRawParameterTypes(String.class)`. The build fails the moment someone violates it. Code review is too unreliable; the rule must be automated.

---

### Q14. How do you migrate a legacy primitive-heavy codebase?

Strangler fig:
1. Introduce the value types alongside the primitive APIs.
2. Add `@Deprecated` overloads that wrap primitives into types.
3. Push types downward; deprecated overloads become thin adapters.
4. Migrate callers in batches.
5. Delete the deprecated overloads.
6. Turn on the global ArchUnit rule.

Never big-bang.

---

### Q15. When is wrapping a primitive *wrong*?

When the primitive truly represents no domain concept (loop index, array length, internal counter), or when the wrapping adds noise without preventing any plausible bug. Wrapping `int pageSize` adds nothing if there are no other `int` parameters to swap it with.

---

### Q16. Should value objects validate in their constructor or rely on `@Valid`?

Both, but the constructor is the source of truth. `@Valid` (Bean Validation) runs at the boundary; the constructor runs always. If `Email("xyz")` cannot be constructed, no path in the program can produce an invalid `Email`. Bean Validation gives nicer error messages at the controller; the constructor gives certainty everywhere else.

---

### Q17. How do value objects interact with JPA / Hibernate?

Use `@Convert` with an `AttributeConverter<MyValueObject, String>` to map the wrapper to a column. Hibernate 6.2+ supports records as embeddables. For Spring Data JDBC, register a `Converter`. The mapping lives once at the adapter layer; the domain stays clean.

---

### Q18. Where should the conversion from `String` to `Email` happen in a Spring REST app?

At the boundary: in the controller (via a DTO + mapper) or in a Jackson deserialiser. Inside the application layer, the type is `Email` everywhere. The controller is the only place that catches `IllegalArgumentException` and translates it to HTTP 422.

---

### Q19. What's the difference between a Value Object and an Entity in DDD?

A Value Object has no identity — `Money(100, USD)` is interchangeable with any other `Money(100, USD)`. An Entity has identity — two `Customer` instances with the same name are still different customers, identified by `CustomerId`. Value Objects are immutable; Entities have a lifecycle.

---

### Q20. Can a typed `record` replace an enum?

Sometimes. An enum is best when the value set is closed (`Status.{ACTIVE, INACTIVE, DELETED}`) and the values are named at compile time. A record is best when the value set is open or carries data (`CountryCode("UZ")`). When you find yourself encoding open-set knowledge into enum constants (`Currency.USD`, `Currency.EUR`, ...), prefer a validated record. Java's own `java.util.Currency` is a class, not an enum, for exactly this reason.

---

## Stretch question — design exercise

> *Design the public API of a `BankAccount` aggregate root that prevents:*
> *(a) double-charging due to id confusion, (b) currency mismatch, (c) negative balances unless overdraft is configured, (d) money loss due to incorrect rounding.*

A senior answer mentions:

- `AccountId`, `CustomerId`, `TransactionId` records — all distinct types.
- `Money` value object with currency and `RoundingMode.HALF_EVEN`.
- An `OverdraftLimit` value object (also `Money`-typed).
- Operations `deposit(Money)`, `withdraw(Money)` returning a new state.
- `withdraw` throws `InsufficientFundsException` when the result would breach the overdraft limit.
- The aggregate enforces all invariants in its mutator methods; no caller can produce an invalid state.
- Persistence: `@Convert` for each value object; the entity table has plain columns.

---

**Memorize this:** Primitive Obsession is the gap between what the type system *could* check and what it actually *does*. Records (JEP 395) close that gap today; value classes (JEP 401) will make closing it free. Tiny types, typed IDs, and a real `Money` are not academic flourishes — they are the cheapest, most permanent way to delete entire bug-classes from a codebase.
