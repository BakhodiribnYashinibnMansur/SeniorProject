# Big-O Notation -- Professional / Theoretical Level

## Table of Contents

1. [Formal Limit Definition](#formal-limit-definition)
2. [Little-o Notation](#little-o-notation)
3. [Little-omega Notation](#little-omega-notation)
4. [Properties of Asymptotic Notation](#properties-of-asymptotic-notation)
5. [Proving Big-O from the Definition](#proving-big-o-from-the-definition)
6. [Big-O Arithmetic](#big-o-arithmetic)
7. [Hierarchy Theorem](#hierarchy-theorem)
8. [Relationships Between Notations](#relationships-between-notations)
9. [Advanced Proof Techniques](#advanced-proof-techniques)
10. [Key Takeaways](#key-takeaways)

---

## Formal Limit Definition

Beyond the existential definition (exists c, n0), Big-O can be characterized using limits:

**Limit characterization:**
```
f(n) = O(g(n))  if  lim sup (n -> infinity) |f(n)| / |g(n)| < infinity
```

More practically:
```
If lim (n -> infinity) f(n) / g(n) = L where 0 <= L < infinity, then f(n) = O(g(n)).
```

This is often easier to work with than finding explicit c and n0.

**Examples using the limit definition:**

```
1) f(n) = 3n^2 + 5n, g(n) = n^2
   lim (3n^2 + 5n) / n^2 = lim (3 + 5/n) = 3
   Since 3 is finite and non-negative, 3n^2 + 5n = O(n^2).

2) f(n) = n, g(n) = n^2
   lim n / n^2 = lim 1/n = 0
   Since 0 < infinity, n = O(n^2). (This is a loose bound.)

3) f(n) = n^2, g(n) = n
   lim n^2 / n = lim n = infinity
   Since the limit is infinity, n^2 is NOT O(n).

4) f(n) = log(n), g(n) = n^epsilon for any epsilon > 0
   lim log(n) / n^epsilon = 0 (by L'Hopital's rule)
   Therefore log(n) = O(n^epsilon) for any epsilon > 0.
```

**Using L'Hopital's Rule for limits:**

When both f(n) and g(n) approach infinity:
```
lim f(n)/g(n) = lim f'(n)/g'(n)
```

Example: Is n * log(n) = O(n^1.5)?
```
lim n*log(n) / n^1.5 = lim log(n) / n^0.5
Apply L'Hopital: lim (1/n) / (0.5 * n^{-0.5}) = lim 2 / n^0.5 = 0
Yes, n*log(n) = O(n^1.5).
```

---

## Little-o Notation

Little-o represents a **strict** upper bound -- f grows strictly slower than g.

**Definition:**
```
f(n) = o(g(n))  iff  lim (n -> infinity) f(n) / g(n) = 0
```

Equivalently: for ALL constants c > 0, there exists n0 such that f(n) < c * g(n) for all n >= n0.

The key difference from Big-O:
- Big-O: there EXISTS some c such that f(n) <= c * g(n) eventually.
- Little-o: for ALL c > 0, f(n) < c * g(n) eventually.

**Examples:**
```
n = o(n^2)        because lim n/n^2 = 0
n^2 = o(n^3)      because lim n^2/n^3 = 0
log(n) = o(n)     because lim log(n)/n = 0
n = o(n * log(n)) because lim n/(n*log(n)) = lim 1/log(n) = 0

But:
n is NOT o(n)     because lim n/n = 1 != 0
2n is NOT o(n)    because lim 2n/n = 2 != 0
```

**Interpretation:** f(n) = o(g(n)) means f is **asymptotically negligible** compared to g. It is the asymptotic equivalent of strict less-than.

---

## Little-omega Notation

Little-omega is the complement of little-o.

**Definition:**
```
f(n) = omega(g(n))  iff  lim (n -> infinity) f(n) / g(n) = infinity
```

Equivalently: for ALL constants c > 0, there exists n0 such that f(n) > c * g(n) for all n >= n0.

**Examples:**
```
n^2 = omega(n)       because lim n^2/n = infinity
n*log(n) = omega(n)  because lim n*log(n)/n = infinity
2^n = omega(n^k)     for any fixed k, because exponential dominates polynomial
```

---

## Properties of Asymptotic Notation

### Transitivity

All five asymptotic notations are transitive:
```
If f(n) = O(g(n)) and g(n) = O(h(n)), then f(n) = O(h(n)).
If f(n) = Theta(g(n)) and g(n) = Theta(h(n)), then f(n) = Theta(h(n)).
If f(n) = Omega(g(n)) and g(n) = Omega(h(n)), then f(n) = Omega(h(n)).
If f(n) = o(g(n)) and g(n) = o(h(n)), then f(n) = o(h(n)).
If f(n) = omega(g(n)) and g(n) = omega(h(n)), then f(n) = omega(h(n)).
```

**Proof sketch for Big-O transitivity:**
f(n) <= c1 * g(n) for n >= n1, and g(n) <= c2 * h(n) for n >= n2.
Then f(n) <= c1 * c2 * h(n) for n >= max(n1, n2).
So f(n) = O(h(n)) with c = c1*c2 and n0 = max(n1, n2).

### Reflexivity

```
f(n) = O(f(n))       (every function is an upper bound on itself)
f(n) = Theta(f(n))   (every function is a tight bound on itself)
f(n) = Omega(f(n))   (every function is a lower bound on itself)
```

But NOT for strict bounds:
```
f(n) != o(f(n))      (a function does not grow strictly slower than itself)
f(n) != omega(f(n))  (a function does not grow strictly faster than itself)
```

### Symmetry

Big-Theta is symmetric:
```
f(n) = Theta(g(n))  iff  g(n) = Theta(f(n))
```

Big-O and Big-Omega are transposes:
```
f(n) = O(g(n))  iff  g(n) = Omega(f(n))
f(n) = o(g(n))  iff  g(n) = omega(f(n))
```

### Analogy to Real Number Comparisons

| Asymptotic Notation | Number Analogy |
|---------------------|----------------|
| f(n) = O(g(n))      | a <= b         |
| f(n) = Omega(g(n))  | a >= b         |
| f(n) = Theta(g(n))  | a = b          |
| f(n) = o(g(n))      | a < b          |
| f(n) = omega(g(n))  | a > b          |

**Important caveat:** Unlike real numbers, not all functions are asymptotically comparable. For example, f(n) = n and g(n) = n^(1+sin(n)) are not comparable because g oscillates.

---

## Proving Big-O from the Definition

### Technique 1: Direct Proof (find c and n0)

**Prove: 5n^3 + 2n^2 + 3 = O(n^3)**

For n >= 1:
- 5n^3 + 2n^2 + 3 <= 5n^3 + 2n^3 + 3n^3 = 10n^3

Choose c = 10, n0 = 1: 5n^3 + 2n^2 + 3 <= 10 * n^3 for all n >= 1.

### Technique 2: Limit Test

**Prove: n * log(n) = O(n^2)**

```
lim (n -> inf) n*log(n) / n^2 = lim log(n)/n = 0
```

Since the limit is 0 (which is < infinity), the result follows.

### Technique 3: Induction

**Prove: T(n) = 2T(n/2) + n is O(n log n)**

Claim: T(n) <= c * n * log(n) for some c > 0.

Inductive step: Assume T(k) <= c * k * log(k) for k < n.
```
T(n) = 2T(n/2) + n
     <= 2 * c * (n/2) * log(n/2) + n
     = c * n * (log(n) - 1) + n
     = c * n * log(n) - c*n + n
     <= c * n * log(n)           when c >= 1
```

### Technique 4: Proof by Contradiction

**Prove: 2^n is NOT O(n^k) for any fixed k**

Assume 2^n = O(n^k). Then there exist c, n0 such that 2^n <= c * n^k for all n >= n0.
Taking logarithms: n <= log(c) + k * log(n).
For large n, the left side grows linearly while the right grows logarithmically.
This is a contradiction for sufficiently large n.

---

## Big-O Arithmetic

### Sum Rule

```
If f1(n) = O(g1(n)) and f2(n) = O(g2(n)), then:
f1(n) + f2(n) = O(max(g1(n), g2(n)))
```

**Proof:** f1(n) <= c1*g1(n) and f2(n) <= c2*g2(n).
f1(n) + f2(n) <= c1*g1(n) + c2*g2(n) <= (c1+c2)*max(g1(n), g2(n)).

**Corollary:** O(n^2) + O(n) = O(n^2). The dominant term absorbs the lesser.

### Product Rule

```
If f1(n) = O(g1(n)) and f2(n) = O(g2(n)), then:
f1(n) * f2(n) = O(g1(n) * g2(n))
```

**Proof:** f1*f2 <= c1*g1 * c2*g2 = (c1*c2) * (g1*g2).

**Corollary:** An O(n) loop containing an O(log n) operation is O(n * log n).

### Constant Multiplication

```
If f(n) = O(g(n)) and k > 0 is a constant, then:
k * f(n) = O(g(n))
```

Constants are absorbed into the c in the definition.

### Polynomial Rule

```
If f(n) is a polynomial of degree d:
f(n) = a_d * n^d + a_{d-1} * n^{d-1} + ... + a_1 * n + a_0
Then f(n) = O(n^d).
```

### Logarithm Rules

```
log_a(n) = O(log_b(n)) for any bases a, b > 1
```
Because log_a(n) = log_b(n) / log_b(a), and 1/log_b(a) is a constant.
This is why we write O(log n) without specifying the base.

### Exponent Rules

```
n^a = o(n^b) when a < b
n^a = o(b^n) for any a and b > 1
log^k(n) = o(n^epsilon) for any k > 0 and epsilon > 0
```

---

## Hierarchy Theorem

The hierarchy of common growth rates, from slowest to fastest:

```
O(1) < O(log log n) < O(log n) < O(log^2 n) < O(sqrt(n))
< O(n) < O(n log n) < O(n^2) < O(n^3) < ... < O(n^k)
< O(2^n) < O(n!) < O(n^n)
```

### Formal Hierarchy

For any constants a, b, c where 0 < a < b and c > 1:

```
1. Polylogarithmic < Polynomial:  log^k(n) = o(n^epsilon)
2. Polynomial < Exponential:      n^k = o(c^n) for c > 1
3. Exponential < Factorial:        c^n = o(n!)
4. Factorial < Double exponential: n! = o(n^n)
```

### Stirling's Approximation and Factorials

```
n! ~ sqrt(2*pi*n) * (n/e)^n
log(n!) = Theta(n * log n)
```

This is why O(n!) and O(n^n) are both "intractable" but n! grows slower than n^n.

### Time Hierarchy Theorem (Complexity Theory)

In computational complexity theory, the Time Hierarchy Theorem states:

```
DTIME(f(n)) is strictly contained in DTIME(f(n) * log^2(f(n)))
```

This means that given strictly more time, a Turing machine can solve strictly more problems. This is the formal justification for the hierarchy of complexity classes like P, NP, EXPTIME, etc.

---

## Relationships Between Notations

### Complete Picture

```
f(n) = Theta(g(n))  <=>  f(n) = O(g(n)) AND f(n) = Omega(g(n))
f(n) = o(g(n))      =>   f(n) = O(g(n))     (but not vice versa)
f(n) = omega(g(n))  =>   f(n) = Omega(g(n))  (but not vice versa)
f(n) = Theta(g(n))  =>   NOT f(n) = o(g(n))  AND NOT f(n) = omega(g(n))
```

### Limit-Based Classification

Given L = lim (n -> infinity) f(n)/g(n):

| Value of L    | Conclusion                                  |
|---------------|---------------------------------------------|
| L = 0         | f = o(g), f = O(g), g = omega(f), g = Omega(f) |
| 0 < L < inf   | f = Theta(g), f = O(g), f = Omega(g)       |
| L = infinity  | f = omega(g), f = Omega(g), g = o(f), g = O(f) |
| L undefined   | None of the above can be concluded directly |

---

## Advanced Proof Techniques

### Using Logarithms for Comparing Growth Rates

To compare n^(log n) vs (log n)^n:

Take logarithm of both:
```
log(n^(log n)) = log(n) * log(n) = log^2(n)
log((log n)^n) = n * log(log n)
```

Since n * log(log n) grows faster than log^2(n), we have (log n)^n = omega(n^(log n)).

### Proving Tight Bounds (Big-Theta)

To prove f(n) = Theta(g(n)), prove both directions:

1. f(n) = O(g(n)): find c2 and n0 such that f(n) <= c2 * g(n) for n >= n0.
2. f(n) = Omega(g(n)): find c1 and n0' such that f(n) >= c1 * g(n) for n >= n0'.

**Example: Prove sum(i=1 to n) i = Theta(n^2)**

Upper bound: sum <= n * n = n^2, so sum = O(n^2) with c = 1.
Lower bound: sum >= sum(i=n/2 to n) i >= (n/2) * (n/2) = n^2/4, so sum = Omega(n^2) with c = 1/4.
Therefore sum = Theta(n^2).

### Iterated Logarithm -- log*(n)

The iterated logarithm log*(n) is the number of times you must take the logarithm before the result is <= 1.

```
log*(2) = 1
log*(4) = 2
log*(16) = 3
log*(65536) = 4
log*(2^65536) = 5
```

This function grows incredibly slowly. It appears in union-find (with path compression and union by rank): O(n * alpha(n)) where alpha is the inverse Ackermann function, which grows even more slowly than log*.

---

## Key Takeaways

- The limit definition (lim f/g) is often the most practical way to determine asymptotic relationships between functions.
- Little-o (strict upper bound) and little-omega (strict lower bound) are the asymptotic equivalents of < and >.
- Asymptotic notations satisfy transitivity and reflexivity (for non-strict versions); Big-Theta additionally satisfies symmetry.
- Big-O arithmetic (sum rule, product rule) provides mechanical rules for combining complexities.
- The growth hierarchy is rigorous: polylogarithmic < polynomial < exponential < factorial.
- Not all functions are asymptotically comparable -- some oscillate in ways that prevent comparison.
- Formal proofs use direct construction (find c, n0), limits, induction, or contradiction.
