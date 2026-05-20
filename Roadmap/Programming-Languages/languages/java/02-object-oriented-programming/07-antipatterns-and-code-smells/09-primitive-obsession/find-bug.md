# Primitive Obsession — Find the Bug

Ten realistic Java scenarios where the use of a raw primitive in a domain API produces a real bug. For each, identify the bug, then read the fix. The fix is always to introduce a typed value object.

---

## Scenario 1 — Swapped String parameters

```java
public void sendInvite(String email, String name) {
    mail.send(email, "Welcome " + name);
}

// caller, somewhere in the codebase
sendInvite("Alice Smith", "alice@acme.com");
```

### Bug

Compiler accepts swapped arguments because both parameters are `String`. The email goes to `Alice Smith` (NDR) and the greeting says "Welcome alice@acme.com".

### Diagnosis

The method signature has two parameters of the same type representing different domain concepts. There is no type-level distinction.

### Fix

```java
public record Email(String value) { /* RFC validation */ }
public record FullName(String value) { /* not-blank, max length */ }

public void sendInvite(Email email, FullName name) { ... }

sendInvite(new Email("alice@acme.com"), new FullName("Alice Smith")); // can't swap
```

Now the compiler refuses to compile the swap.

---

## Scenario 2 — Currency mismatch silently passes

```java
public BigDecimal totalCharge(BigDecimal items, BigDecimal shipping) {
    return items.add(shipping);
}

// Stripe paid USD, FedEx quote in EUR
BigDecimal total = totalCharge(new BigDecimal("100.00") /* USD */,
                               new BigDecimal("12.50") /* EUR */);
```

### Bug

`BigDecimal` carries no currency. Two amounts in different currencies are added arithmetically; the result `112.50` is dimensionally meaningless. The customer is overcharged or refunded incorrectly.

### Diagnosis

`BigDecimal` is a numeric primitive in disguise — it has no concept of *what* it counts.

### Fix

```java
public record Money(BigDecimal amount, Currency currency) {
    public Money add(Money other) {
        if (!currency.equals(other.currency))
            throw new CurrencyMismatchException(currency, other.currency);
        return new Money(amount.add(other.amount), currency);
    }
}
```

The runtime exception now fires the moment the dimensionally-wrong addition is attempted.

---

## Scenario 3 — int overflow in money in cents

```java
public int totalCents(int unitPriceCents, int quantity) {
    return unitPriceCents * quantity;
}

// luxury watch, bulk order
int total = totalCents(2_500_000 /* $25,000 */, 1000); // expecting 2,500,000,000 cents = $25M
```

### Bug

`2_500_000 * 1000 = 2_500_000_000`, which overflows `int` (max 2,147,483,647). Result is `-1,794,967,296` — a negative refund instead of a 25-million-dollar charge.

### Diagnosis

`int` for money is a discipline that breaks under load. `long` postpones the day; only typed `Money` makes the overflow path explicit.

### Fix

```java
public record Money(BigDecimal amount, Currency currency) {
    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }
}
// no overflow possible; arbitrary-precision arithmetic
```

If you must use a primitive for performance, use `long` and add overflow checks via `Math.multiplyExact`. But `BigDecimal` is the safe default.

---

## Scenario 4 — long timestamp without time zone

```java
public List<Event> eventsSince(long sinceMillis) {
    return repo.findAfter(sinceMillis);
}

long midnight = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
    .toInstant().toEpochMilli();
// developer in Tashkent (UTC+5), DB stores UTC, server in Frankfurt (UTC+1)
```

### Bug

A `long` epoch millisecond is unambiguous *only* if everyone agrees on UTC. The moment the developer constructs the value with `systemDefault()` and the server runs in a different zone, the meaning of "midnight" shifts by hours. Tests pass locally, production cuts off events at the wrong hour.

### Diagnosis

`long` collapses two pieces of information (instant, zone-of-interpretation) into one. The bug appears only across zone boundaries.

### Fix

```java
public List<Event> eventsSince(Instant since) {
    return repo.findAfter(since);
}

// caller is forced to construct an Instant, which is zone-explicit
eventsSince(LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant());
```

Better yet, accept a `ZonedDateTime` or `OffsetDateTime` if the zone matters for the query, or `LocalDate` if it doesn't.

---

## Scenario 5 — boolean flag explosion

```java
public Report buildReport(User user, boolean includeArchived, boolean monthly,
                          boolean withCharts, boolean emailMe) { ... }

buildReport(user, true, false, true, true); // what do these mean?
```

### Bug

A call site `buildReport(user, true, false, true, true)` is unreadable. Swapping any two booleans compiles and runs — only the output is wrong. Code review cannot reliably catch it; the bug ships.

### Diagnosis

Boolean flags are anonymous bits. Four booleans = 16 combinations, most of which were never tested.

### Fix

Replace each boolean with an enum, or build an options object, or split the method.

```java
public enum ArchivedScope { INCLUDE, EXCLUDE }
public enum Frequency { DAILY, MONTHLY }
public enum Charts { WITH, WITHOUT }
public enum Notify { EMAIL, SILENT }

public Report buildReport(User user, ArchivedScope archived, Frequency freq, Charts charts, Notify notify) { ... }

buildReport(user, INCLUDE, MONTHLY, WITH, EMAIL); // self-documenting
```

The original call `(true, false, true, true)` is now physically unutterable.

---

## Scenario 6 — UserId vs OrderId swap

```java
public void cancelOrder(long userId, long orderId) { ... }

cancelOrder(orderId, userId); // swapped — both are long
```

### Bug

Compiler accepts the swap. The query `DELETE FROM orders WHERE id = ? AND user_id = ?` runs with reversed values. Worst case it deletes the wrong order from a different user; best case it silently deletes nothing and the user calls support.

### Diagnosis

Two domain identifiers of the same primitive type are indistinguishable to the compiler.

### Fix

```java
public record UserId(long value) {}
public record OrderId(long value) {}

public void cancelOrder(UserId userId, OrderId orderId) { ... }

cancelOrder(orderId, userId); // compile error
```

---

## Scenario 7 — country code typo

```java
public Shipping rateFor(String country, BigDecimal weight) { ... }

rateFor("UK", new BigDecimal("2.5")); // ISO 3166-1 alpha-2 is "GB", not "UK"
```

### Bug

`"UK"` is not a valid ISO country code; the lookup misses and the rate falls back to a default that is wrong by 30%. The bug is hard to spot because `"UK"` *looks* right to a human.

### Diagnosis

`String` accepts any text. The format constraint is enforced — if at all — far from the call site.

### Fix

```java
public record CountryCode(String value) {
    public CountryCode {
        Objects.requireNonNull(value);
        if (!Set.of(Locale.getISOCountries()).contains(value)) {
            throw new IllegalArgumentException("Unknown ISO country code: " + value);
        }
    }
}
public Shipping rateFor(CountryCode country, Weight weight) { ... }
```

Now `new CountryCode("UK")` throws at the boundary, where the developer can see the actual input.

---

## Scenario 8 — double for money

```java
public double total(double price, int quantity) {
    return price * quantity;
}

double t = total(0.1, 3); // expected 0.3
System.out.println(t == 0.3); // false; t = 0.30000000000000004
```

### Bug

`double` cannot represent decimal fractions exactly. Equality and rounding fail in financial calculations. Off-by-one-cent errors creep into invoices, taxes, and reconciliation.

### Diagnosis

`double` is binary floating-point; money is decimal. They are dimensionally incompatible.

### Fix

```java
public record Money(BigDecimal amount, Currency currency) { ... }
public Money total(Money unit, Quantity qty) {
    return unit.multiply(BigDecimal.valueOf(qty.value()));
}
```

Scale and rounding are explicit and currency-aware.

---

## Scenario 9 — phone number normalised inconsistently

```java
public Optional<User> findByPhone(String phone) {
    return repo.findByPhone(phone);
}

findByPhone("+998 90 123 45 67"); // saved as "+998901234567" — no match
findByPhone("998901234567");      // saved as "+998901234567" — no match
```

### Bug

Phone numbers were saved in E.164 format but searched with formatting. Both calls return empty; the user is told "no account found" even though one exists.

### Diagnosis

`String` does not enforce normalisation. The normalisation rule lives in one place at write time, but is forgotten at read time.

### Fix

```java
public record PhoneNumber(String e164) {
    public PhoneNumber {
        e164 = normaliseToE164(e164); // strips spaces, dashes, adds country code
        if (!E164_RX.matcher(e164).matches())
            throw new IllegalArgumentException("invalid phone: " + e164);
    }
}
```

Now `new PhoneNumber("+998 90 123 45 67").e164()` equals `new PhoneNumber("998901234567").e164()`. The type enforces normalisation; every search is consistent.

---

## Scenario 10 — int age vs int height

```java
public BMI calculate(int weight, int height, int age) { ... }

calculate(175, 70, 32); // height 175cm, weight 70kg, age 32 — or...
                        // weight 175kg, height 70cm, age 32 — both compile
```

### Bug

Three `int` parameters in a row are interchangeable. The function silently computes BMI on swapped weight/height. The user is told they are dangerously underweight when in fact the input was just reordered.

### Diagnosis

Same primitive type for different dimensions = compiler is blind.

### Fix

```java
public record Kilograms(int value) {}
public record Centimetres(int value) {}
public record Years(int value) {}

public BMI calculate(Kilograms weight, Centimetres height, Years age) { ... }
```

A swap is now a compile error. As a bonus, units are documented in the type.

---

## General fix recipe

1. **Identify the domain concept** that the primitive represents.
2. **Create a record** wrapping the primitive, with validation in the canonical constructor.
3. **Replace the parameter / field / return type** with the new record.
4. **Push the conversion outward** to the adapter / controller / mapper layer.
5. **Add an ArchUnit rule** to prevent the primitive from creeping back into the domain layer.

Every fix above followed exactly this recipe. The mechanical nature of the refactor is its own argument for doing it.
