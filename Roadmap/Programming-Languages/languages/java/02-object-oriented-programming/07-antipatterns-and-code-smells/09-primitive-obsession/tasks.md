# Primitive Obsession — Tasks

Eight exercises in increasing difficulty. Each has a validation checklist; one ends with a worked solution sketch.

---

## Validation reference

| Check | What to verify |
|---|---|
| Compiles | `mvn compile` / `./gradlew build` passes |
| Validates | Invalid input throws in the canonical constructor |
| Equals/hashCode | Two records with equal fields are equal; unequal fields are not |
| No raw primitives | No `String`/`long`/`UUID`/`BigDecimal` in the public domain API |
| ArchUnit | The `no_raw_strings_in_domain` rule passes |
| Tests | Unit tests cover happy path, edge case, invalid input |
| Currency-safe | `Money` never mixes currencies silently |
| Immutable | No setters, all fields `final` |

---

## Exercise 1 — Email value object

Replace `String email` with a `record Email(String value)` across a `UserRegistration` service.

**Requirements:**
- Validate basic email shape (`<non-space>@<non-space>.<non-space>`).
- Reject `null` and empty.
- Provide a `domain()` accessor returning the part after `@`.

**Validation:** Compiles, validates, equals/hashCode, immutable, tests.

---

## Exercise 2 — Typed identifiers

You have:

```java
public void transfer(long fromAccountId, long toAccountId, long initiatorUserId) { ... }
```

Replace each `long` with a dedicated record.

**Requirements:**
- `AccountId`, `UserId` records each wrapping a `long`.
- Reject zero and negative values in the constructor.
- The method signature uses the typed IDs.
- Two callers in the existing tests must be updated.

**Validation:** Compiles, validates, no raw primitives, ArchUnit.

---

## Exercise 3 — Money with currency

Implement a `Money` record with:

- `BigDecimal amount`, `Currency currency` fields.
- `add(Money)`, `subtract(Money)`, `multiply(BigDecimal)` operations.
- `add`/`subtract` throw `CurrencyMismatchException` on mismatch.
- Constructor sets scale to `currency.getDefaultFractionDigits()` with `RoundingMode.HALF_EVEN`.
- A `zero(Currency)` static factory.

**Requirements:**
- 100% unit-test coverage of the operations.
- A test that proves `Money(0.1) + Money(0.2)` equals `Money(0.3)` exactly.

**Validation:** All boxes above.

---

## Exercise 4 — Phone number normalisation

Build `record PhoneNumber(String e164)`:

- Strips spaces, dashes, parentheses, leading zeros.
- Adds `+` if missing.
- Validates against `^\+\d{8,15}$`.
- `new PhoneNumber("+998 90 123 45 67")` and `new PhoneNumber("998-90-123-4567")` produce equal instances.

**Validation:** Compiles, validates, equals across normalised inputs, tests for at least 5 input variants.

---

## Exercise 5 — Replace boolean flags with enums

Refactor:

```java
public Report build(User u, boolean monthly, boolean withCharts, boolean emailMe) { ... }
```

into a typed signature using three enums (or a single options record).

**Requirements:**
- All four boolean call sites in the existing tests must compile against the new signature.
- The default values must be expressible (use a builder if needed).
- ArchUnit rule "no boolean in domain public methods" passes for this package.

**Validation:** Compiles, no booleans in signature, tests cover all combinations.

---

## Exercise 6 — Country and postal code

Build `CountryCode` (ISO 3166-1 alpha-2) and `PostalCode` records:

- `CountryCode` validates against `Locale.getISOCountries()`.
- `PostalCode` carries a `CountryCode` because format differs per country (`5` digits for US, `SW1A 1AA` for UK, etc.). Validation is country-aware.
- Implement validation for at least 5 countries; throw `UnsupportedPostalFormatException` for unknown ones.

**Requirements:**
- The `Address` record composes `CountryCode` and `PostalCode`.
- A test ensures `new PostalCode("12345", "GB")` throws (UK postal codes are not 5 digits).

**Validation:** Compiles, validates per-country, immutable, tests.

---

## Exercise 7 — Time without zone bugs

A legacy service exposes:

```java
public List<Event> eventsBetween(long fromMillis, long toMillis) { ... }
```

Refactor to use `Instant` (or `ZonedDateTime` where the zone is part of the query).

**Requirements:**
- Identify in the existing code where the conversion `epochMillis → ZonedDateTime` happens with `ZoneId.systemDefault()` — this is the bug.
- The new signature must force the caller to make zone explicit.
- All callers updated; tests pass under at least two different `TimeZone.setDefault(...)` values.

**Validation:** Compiles, no raw `long` time in domain, tests across zones.

---

## Exercise 8 — ArchUnit ratchet

Add an ArchUnit test class `DomainPrimitiveRules` to a small (~30-class) module of your choice.

**Requirements:**
- Forbid `String` in domain method signatures.
- Forbid `long`/`UUID` parameters whose name ends with `id` or `identifier`.
- Forbid `boolean` parameters in domain public methods.
- Forbid `double`/`float` parameters with monetary names (`amount`, `price`, `cost`).
- The build initially fails (proves the rules work); then you migrate violations one at a time.
- The final build passes.

**Validation:** ArchUnit rules in place, build initially red, build finally green, at least 5 commits documenting the migration.

---

## Worked solution sketch — Exercise 3 (Money)

```java
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    public static Money zero(Currency c) {
        return new Money(BigDecimal.ZERO, c);
    }

    public Money add(Money other) {
        requireSame(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSame(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    private void requireSame(Money other) {
        if (!currency.equals(other.currency))
            throw new CurrencyMismatchException(currency, other.currency);
    }
}

public final class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(Currency a, Currency b) {
        super("currency mismatch: " + a + " vs " + b);
    }
}
```

**Test highlights:**

```java
@Test
void exact_decimal_arithmetic() {
    Money a = new Money(new BigDecimal("0.10"), Currency.getInstance("USD"));
    Money b = new Money(new BigDecimal("0.20"), Currency.getInstance("USD"));
    Money expected = new Money(new BigDecimal("0.30"), Currency.getInstance("USD"));
    assertEquals(expected, a.add(b));
}

@Test
void currency_mismatch_throws() {
    Money usd = Money.zero(Currency.getInstance("USD"));
    Money eur = Money.zero(Currency.getInstance("EUR"));
    assertThrows(CurrencyMismatchException.class, () -> usd.add(eur));
}

@Test
void scale_is_set_from_currency() {
    Money jpy = new Money(new BigDecimal("100.456"), Currency.getInstance("JPY"));
    assertEquals(0, jpy.amount().scale()); // JPY has 0 fractional digits
}

@Test
void banker_rounding_applied() {
    Money m = new Money(new BigDecimal("0.125"), Currency.getInstance("USD"));
    assertEquals(new BigDecimal("0.12"), m.amount()); // HALF_EVEN rounds 0.125 to 0.12
}
```

**Self-review checklist after finishing:**

- Did the constructor reject `null`? Yes.
- Did the constructor set scale from currency? Yes.
- Does `add` reject currency mismatch? Yes.
- Is the record immutable? Yes (records are implicitly so).
- Is `equals` correct? Yes (synthesised, compares amount and currency).
- Are exceptions descriptive? Yes (`CurrencyMismatchException` carries both currencies).

---

## After completing all eight

- Run the full `mvn verify` / `./gradlew check`.
- Run the ArchUnit tests from Exercise 8 — they should pass on every package you migrated.
- Compute DPR (Domain Primitive Ratio, see `specification.md`) before and after. Aim for a drop of at least 50%.
- Document one bug-class that the new types now make unreachable. This is the durable value of the work.
