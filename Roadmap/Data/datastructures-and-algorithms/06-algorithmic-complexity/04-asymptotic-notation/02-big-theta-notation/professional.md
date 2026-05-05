# Big-Theta Notation -- Professional Level

## Table of Contents

1. [Formal Proof Techniques](#formal-proof-techniques)
2. [The Sandwich Theorem](#the-sandwich-theorem)
3. [Theta from Limits](#theta-from-limits)
4. [Proving Theta(n log n) for Merge Sort](#proving-theta-for-merge-sort)
5. [Master Theorem and Theta](#master-theorem-and-theta)
6. [Theta for Common Recurrences](#theta-for-common-recurrences)
7. [Advanced Proof Examples](#advanced-proof-examples)
8. [Theta and Polynomial Hierarchy](#theta-and-polynomial-hierarchy)
9. [Code: Verifying Proofs Computationally](#code-verification)
10. [Summary](#summary)

---

## Formal Proof Techniques

There are three main approaches to proving f(n) = Theta(g(n)):

### Approach 1: Direct (Find c1, c2, n0)

Prove the definition directly by exhibiting constants.

**Template:**
1. Show f(n) <= c2 * g(n) for n >= n0 (upper bound)
2. Show f(n) >= c1 * g(n) for n >= n0 (lower bound)
3. Conclude f(n) = Theta(g(n))

### Approach 2: Prove O and Omega Separately

Since Theta(g(n)) = O(g(n)) AND Omega(g(n)):
1. Prove f(n) = O(g(n))
2. Prove f(n) = Omega(g(n))
3. Conclude f(n) = Theta(g(n))

### Approach 3: Limit Method

Use limits to determine the relationship:
- If lim(n->inf) f(n)/g(n) = c where 0 < c < infinity, then f(n) = Theta(g(n))

---

## The Sandwich Theorem

The Sandwich Theorem (also called the Squeeze Theorem in calculus) is the
theoretical foundation of Big-Theta.

### Statement

If there exist functions h1(n) and h2(n) and a function f(n) such that:
- h1(n) <= f(n) <= h2(n) for all sufficiently large n
- h1(n) = Theta(g(n))
- h2(n) = Theta(g(n))

Then f(n) = Theta(g(n)).

### Application

This is useful when f(n) is complex but can be bounded by simpler functions.

**Example**: Prove that f(n) = n^2 + n*sin(n) is Theta(n^2).

Since -1 <= sin(n) <= 1:
- Lower: n^2 + n*(-1) = n^2 - n <= f(n)
- Upper: f(n) <= n^2 + n*(1) = n^2 + n

For n >= 2:
- n^2 - n >= n^2 - n^2/2 = n^2/2  (since n <= n^2/2 for n >= 2)
- n^2 + n <= n^2 + n^2 = 2n^2

Therefore: (1/2)*n^2 <= f(n) <= 2*n^2 for n >= 2.
With c1 = 1/2, c2 = 2, n0 = 2: f(n) = Theta(n^2). QED.

---

## Theta from Limits

### The Limit Theorem for Theta

**Theorem**: If lim(n -> infinity) f(n) / g(n) = L, then:

| Value of L          | Conclusion                                 |
|---------------------|--------------------------------------------|
| L = 0               | f(n) = o(g(n)), f(n) is NOT Theta(g(n))   |
| 0 < L < infinity    | f(n) = Theta(g(n))                         |
| L = infinity        | g(n) = o(f(n)), f(n) is NOT Theta(g(n))   |

### Why This Works

If lim f(n)/g(n) = L where 0 < L < infinity, then for any epsilon > 0, there
exists N such that for all n >= N:

```
L - epsilon < f(n)/g(n) < L + epsilon
```

Choose epsilon = L/2 (so it stays positive):
```
L/2 < f(n)/g(n) < 3L/2
```

Multiply by g(n):
```
(L/2) * g(n) < f(n) < (3L/2) * g(n)
```

This gives c1 = L/2, c2 = 3L/2, proving Theta(g(n)).

### Examples Using Limits

**Example 1**: f(n) = 5n^2 + 3n + 7

```
lim (5n^2 + 3n + 7) / n^2 = lim (5 + 3/n + 7/n^2) = 5
```

Since 0 < 5 < infinity: f(n) = Theta(n^2). The constants are approximately
c1 = 5/2 = 2.5 and c2 = 15/2 = 7.5.

**Example 2**: f(n) = n*log(n) + 100n

```
lim (n*log(n) + 100n) / (n*log(n)) = lim (1 + 100/log(n)) = 1
```

Since 0 < 1 < infinity: f(n) = Theta(n log n).

**Example 3**: f(n) = 2^n, g(n) = 3^n

```
lim 2^n / 3^n = lim (2/3)^n = 0
```

Since L = 0: 2^n is NOT Theta(3^n). (It is o(3^n).)

**Example 4**: f(n) = n!, g(n) = 2^n

```
lim n! / 2^n = infinity  (by Stirling's approximation)
```

Since L = infinity: n! is NOT Theta(2^n). (n! grows much faster.)

---

## Proving Theta for Merge Sort

### The Recurrence

Merge sort satisfies the recurrence:
```
T(n) = 2T(n/2) + cn    for n > 1
T(1) = d               (base case)
```

where c is the cost per element during merging, and d is the base case cost.

### Proving T(n) = Theta(n log n)

**Upper Bound (O(n log n))**:

Claim: T(n) <= a*n*log2(n) for some constant a and all n >= 2.

Proof by induction:
- Base: T(2) = 2T(1) + 2c = 2d + 2c. Need 2d + 2c <= a*2*1. Choose a >= d + c.
- Inductive step: Assume T(k) <= a*k*log2(k) for all k < n.
  ```
  T(n) = 2T(n/2) + cn
       <= 2 * a*(n/2)*log2(n/2) + cn
       = a*n*(log2(n) - 1) + cn
       = a*n*log2(n) - a*n + cn
       <= a*n*log2(n)           (when a >= c)
  ```

Choose a = max(d + c, c). Then T(n) <= a*n*log2(n). QED.

**Lower Bound (Omega(n log n))**:

Claim: T(n) >= b*n*log2(n) for some constant b and all n >= 2.

Proof by induction:
- Base: T(2) = 2d + 2c >= b*2*1. Choose b <= d + c.
- Inductive step: Assume T(k) >= b*k*log2(k) for all k < n.
  ```
  T(n) = 2T(n/2) + cn
       >= 2 * b*(n/2)*log2(n/2) + cn
       = b*n*(log2(n) - 1) + cn
       = b*n*log2(n) - b*n + cn
       >= b*n*log2(n)           (when b <= c)
  ```

Choose b = min(d + c, c). Then T(n) >= b*n*log2(n). QED.

**Conclusion**: With constants a and b as defined, b*n*log2(n) <= T(n) <=
a*n*log2(n) for all n >= 2. Therefore T(n) = Theta(n log n). QED.

---

## Master Theorem and Theta

The Master Theorem provides Theta bounds for recurrences of the form:
```
T(n) = aT(n/b) + f(n)
```

where a >= 1, b > 1, and f(n) is asymptotically positive.

### Three Cases

Let c_crit = log_b(a).

**Case 1**: If f(n) = O(n^(c_crit - epsilon)) for some epsilon > 0:
```
T(n) = Theta(n^c_crit)
```

**Case 2**: If f(n) = Theta(n^c_crit * log^k(n)) for k >= 0:
```
T(n) = Theta(n^c_crit * log^(k+1)(n))
```

**Case 3**: If f(n) = Omega(n^(c_crit + epsilon)) for some epsilon > 0, and
a*f(n/b) <= delta*f(n) for some delta < 1 and large n:
```
T(n) = Theta(f(n))
```

### Applying Master Theorem

**Merge Sort**: T(n) = 2T(n/2) + Theta(n)
- a = 2, b = 2, f(n) = n
- c_crit = log_2(2) = 1
- f(n) = n = Theta(n^1 * log^0(n)), so Case 2 with k=0
- **T(n) = Theta(n * log(n))**

**Binary Search**: T(n) = T(n/2) + Theta(1)
- a = 1, b = 2, f(n) = 1
- c_crit = log_2(1) = 0
- f(n) = 1 = Theta(n^0 * log^0(n)), so Case 2 with k=0
- **T(n) = Theta(log(n))**

**Strassen Matrix Multiply**: T(n) = 7T(n/2) + Theta(n^2)
- a = 7, b = 2, f(n) = n^2
- c_crit = log_2(7) = 2.807...
- f(n) = n^2 = O(n^(2.807 - 0.8)), so Case 1 with epsilon = 0.8
- **T(n) = Theta(n^(log_2 7)) = Theta(n^2.807...)**

**Karatsuba Multiplication**: T(n) = 3T(n/2) + Theta(n)
- a = 3, b = 2, f(n) = n
- c_crit = log_2(3) = 1.585...
- f(n) = n = O(n^(1.585 - 0.5)), so Case 1 with epsilon = 0.5
- **T(n) = Theta(n^(log_2 3)) = Theta(n^1.585...)**

---

## Theta for Common Recurrences

| Recurrence                    | Result              | Method         |
|-------------------------------|---------------------|----------------|
| T(n) = T(n/2) + 1            | Theta(log n)        | Master Case 2  |
| T(n) = T(n/2) + n            | Theta(n)            | Master Case 3  |
| T(n) = 2T(n/2) + 1           | Theta(n)            | Master Case 1  |
| T(n) = 2T(n/2) + n           | Theta(n log n)      | Master Case 2  |
| T(n) = 2T(n/2) + n^2         | Theta(n^2)          | Master Case 3  |
| T(n) = 4T(n/2) + n           | Theta(n^2)          | Master Case 1  |
| T(n) = 4T(n/2) + n^2         | Theta(n^2 log n)    | Master Case 2  |
| T(n) = T(n-1) + 1            | Theta(n)            | Unrolling      |
| T(n) = T(n-1) + n            | Theta(n^2)          | Unrolling      |
| T(n) = 2T(n-1) + 1           | Theta(2^n)          | Unrolling      |

---

## Advanced Proof Examples

### Proving Theta(n^2) for Insertion Sort (Worst Case)

**Worst case**: array sorted in descending order. For each element at position
i, we compare with all i-1 previous elements.

```
Comparisons = 1 + 2 + 3 + ... + (n-1) = n(n-1)/2
```

**Upper bound**: n(n-1)/2 = (n^2 - n)/2 <= n^2/2. So O(n^2) with c2 = 1/2.

**Lower bound**: n(n-1)/2 = (n^2 - n)/2 >= n^2/4 for n >= 2 (since n >= n/2
implies n^2 - n >= n^2/2 is not quite right; more carefully:
n(n-1)/2 >= n^2/4 when n >= 2, since n-1 >= n/2).

With c1 = 1/4, c2 = 1/2, n0 = 2: worst case is Theta(n^2). QED.

### Proving log(n!) = Theta(n log n)

This is important because it shows sorting comparison lower bounds.

**Upper bound**: log(n!) = log(1*2*...*n) <= log(n^n) = n*log(n). So O(n log n).

**Lower bound**: log(n!) = sum of log(i) for i=1 to n
>= sum of log(i) for i = n/2 to n (drop the first half)
>= (n/2) * log(n/2)
= (n/2) * (log(n) - 1)
>= (n/4) * log(n) for n >= 4.

With c1 = 1/4, c2 = 1, n0 = 4: log(n!) = Theta(n log n). QED.

(Stirling's approximation n! ~ sqrt(2*pi*n)*(n/e)^n gives log(n!) ~ n*log(n) - n*log(e) + O(log n), confirming Theta(n log n).)

---

## Theta and Polynomial Hierarchy

### Theorem: Polynomial Dominance

For any polynomial p(n) = a_k * n^k + a_{k-1} * n^{k-1} + ... + a_0, where
a_k > 0:

```
p(n) = Theta(n^k)
```

**Proof sketch using limits**:
```
lim p(n) / n^k = lim (a_k + a_{k-1}/n + ... + a_0/n^k) = a_k
```

Since 0 < a_k < infinity, p(n) = Theta(n^k) by the limit theorem.

### Hierarchy of Common Theta Classes

```
Theta(1) < Theta(log n) < Theta(sqrt(n)) < Theta(n) < Theta(n log n) < Theta(n^2) < Theta(n^3) < Theta(2^n) < Theta(n!)
```

The "<" here means: for any f in the left class and g in the right class,
lim f(n)/g(n) = 0, so f = o(g) (strictly smaller).

---

## Code: Verifying Proofs Computationally

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// verifyTheta checks if f(n)/g(n) converges to a positive constant
func verifyTheta(fName, gName string, f, g func(float64) float64) {
    fmt.Printf("\nVerifying %s = Theta(%s):\n", fName, gName)
    fmt.Printf("%-12s %-20s %-20s %-15s\n", "n", "f(n)", "g(n)", "f(n)/g(n)")

    for _, n := range []float64{100, 1000, 10000, 100000, 1000000} {
        fn := f(n)
        gn := g(n)
        ratio := fn / gn
        fmt.Printf("%-12.0f %-20.2f %-20.2f %-15.6f\n", n, fn, gn, ratio)
    }
    // If ratio converges to c where 0 < c < inf, then Theta is confirmed
}

func main() {
    // Verify: 3n^2 + 5n + 2 = Theta(n^2)
    verifyTheta(
        "3n^2+5n+2", "n^2",
        func(n float64) float64 { return 3*n*n + 5*n + 2 },
        func(n float64) float64 { return n * n },
    )

    // Verify: n*log(n) + 100n = Theta(n*log(n))
    verifyTheta(
        "n*ln(n)+100n", "n*ln(n)",
        func(n float64) float64 { return n*math.Log(n) + 100*n },
        func(n float64) float64 { return n * math.Log(n) },
    )

    // Verify: log(n!) = Theta(n*log(n)) using Stirling
    verifyTheta(
        "log(n!)", "n*log(n)",
        func(n float64) float64 {
            lgamma, _ := math.Lgamma(n + 1)
            return lgamma
        },
        func(n float64) float64 { return n * math.Log(n) },
    )

    // Verify NOT Theta: 2^n vs 3^n
    verifyTheta(
        "2^n", "3^n",
        func(n float64) float64 { return math.Pow(2, n) },
        func(n float64) float64 { return math.Pow(3, n) },
    )
}
```

**Java:**

```java
public class ThetaVerifier {

    interface MathFunc {
        double apply(double n);
    }

    static void verifyTheta(String fName, String gName, MathFunc f, MathFunc g) {
        System.out.printf("%nVerifying %s = Theta(%s):%n", fName, gName);
        System.out.printf("%-12s %-20s %-20s %-15s%n", "n", "f(n)", "g(n)", "f(n)/g(n)");

        double[] sizes = {100, 1000, 10000, 100000, 1000000};
        for (double n : sizes) {
            double fn = f.apply(n);
            double gn = g.apply(n);
            double ratio = fn / gn;
            System.out.printf("%-12.0f %-20.2f %-20.2f %-15.6f%n", n, fn, gn, ratio);
        }
    }

    static double logFactorial(double n) {
        double result = 0;
        for (int i = 2; i <= (int) n; i++) {
            result += Math.log(i);
        }
        return result;
    }

    public static void main(String[] args) {
        verifyTheta("3n^2+5n+2", "n^2",
            n -> 3 * n * n + 5 * n + 2,
            n -> n * n);

        verifyTheta("n*ln(n)+100n", "n*ln(n)",
            n -> n * Math.log(n) + 100 * n,
            n -> n * Math.log(n));

        verifyTheta("log(n!)", "n*log(n)",
            ThetaVerifier::logFactorial,
            n -> n * Math.log(n));
    }
}
```

**Python:**

```python
import math


def verify_theta(f_name: str, g_name: str, f, g):
    print(f"\nVerifying {f_name} = Theta({g_name}):")
    print(f"{'n':<12} {'f(n)':<20} {'g(n)':<20} {'f(n)/g(n)':<15}")

    for n in [100, 1000, 10_000, 100_000, 1_000_000]:
        fn = f(n)
        gn = g(n)
        ratio = fn / gn if gn != 0 else float('inf')
        print(f"{n:<12} {fn:<20.2f} {gn:<20.2f} {ratio:<15.6f}")


if __name__ == "__main__":
    # Verify: 3n^2 + 5n + 2 = Theta(n^2)
    verify_theta(
        "3n^2+5n+2", "n^2",
        lambda n: 3 * n**2 + 5 * n + 2,
        lambda n: n**2,
    )

    # Verify: n*log(n) + 100n = Theta(n*log(n))
    verify_theta(
        "n*ln(n)+100n", "n*ln(n)",
        lambda n: n * math.log(n) + 100 * n,
        lambda n: n * math.log(n),
    )

    # Verify: log(n!) = Theta(n*log(n))
    verify_theta(
        "log(n!)", "n*log(n)",
        lambda n: sum(math.log(i) for i in range(2, n + 1)),
        lambda n: n * math.log(n),
    )

    # Verify NOT Theta: 2^n vs 3^n (ratio goes to 0)
    verify_theta(
        "2^n", "3^n",
        lambda n: 2**n,
        lambda n: 3**n,
    )
```

---

## Summary

1. **Three proof approaches**: Direct constants, separate O+Omega, limits.

2. **Limit method**: lim f(n)/g(n) = c with 0 < c < infinity implies Theta.
   This is often the fastest approach.

3. **Sandwich theorem**: If f is squeezed between two Theta(g) functions,
   then f is also Theta(g).

4. **Merge sort proof**: By induction on the recurrence T(n) = 2T(n/2) + cn,
   both upper and lower bounds give n log n, confirming Theta(n log n).

5. **Master Theorem**: Gives direct Theta results for divide-and-conquer
   recurrences T(n) = aT(n/b) + f(n).

6. **Polynomial functions** are always Theta of their leading term.

7. **log(n!) = Theta(n log n)**: A key result for comparison sorting lower bounds.

8. **Computational verification**: Check that f(n)/g(n) converges to a positive
   constant for increasing n.

---

*Next: Continue to the [Interview Guide](interview.md) for interview preparation.*
