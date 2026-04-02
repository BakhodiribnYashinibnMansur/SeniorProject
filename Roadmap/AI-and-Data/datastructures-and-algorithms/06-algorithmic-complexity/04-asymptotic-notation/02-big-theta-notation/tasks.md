# Big-Theta Notation -- Tasks

## Table of Contents

1. [Task 1: Identify Theta of Simple Functions](#task-1)
2. [Task 2: Prove Theta Using Constants](#task-2)
3. [Task 3: Prove Theta Using Limits](#task-3)
4. [Task 4: Count Operations and Verify Theta](#task-4)
5. [Task 5: Classify Loop Patterns](#task-5)
6. [Task 6: Theta of Recursive Functions](#task-6)
7. [Task 7: Distinguish O from Theta](#task-7)
8. [Task 8: Empirical Growth Rate Detector](#task-8)
9. [Task 9: Master Theorem Application](#task-9)
10. [Task 10: Theta-Based Capacity Planner](#task-10)
11. [Task 11: Compare Algorithms by Theta](#task-11)
12. [Task 12: Sandwich Bound Finder](#task-12)
13. [Task 13: Theta of String Operations](#task-13)
14. [Task 14: Theta of Matrix Operations](#task-14)
15. [Task 15: Theta Proof Validator](#task-15)
16. [Benchmark: Full Theta Analysis Suite](#benchmark)

---

## Task 1: Identify Theta of Simple Functions

**Difficulty**: Easy

Determine the Big-Theta for each function. Justify your answer by identifying
the dominant term.

```
a) f(n) = 7n + 42
b) f(n) = 3n^2 - 100n + 999
c) f(n) = 5 * 2^n + n^10
d) f(n) = log(n) + log(log(n))
e) f(n) = n * sqrt(n) + n
f) f(n) = (n^2 + n) / (n + 1)
g) f(n) = n! + 3^n
h) f(n) = n^2 * log(n) + n^2
```

**Expected answers:**
a) Theta(n), b) Theta(n^2), c) Theta(2^n), d) Theta(log n), e) Theta(n^(3/2)),
f) Theta(n), g) Theta(n!), h) Theta(n^2 log n).

---

## Task 2: Prove Theta Using Constants

**Difficulty**: Medium

For each function, find explicit constants c1, c2, and n0 that satisfy the
Theta definition.

**Task 2a**: Prove f(n) = 4n^2 + 7n - 3 is Theta(n^2).

**Task 2b**: Prove f(n) = n*log2(n) + 5n is Theta(n*log(n)).

**Task 2c**: Prove f(n) = 2^n + n^3 is Theta(2^n).

Write your proofs in code form -- compute and print the bounds.

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// Task 2a: Prove 4n^2 + 7n - 3 = Theta(n^2)
func task2a() {
    fmt.Println("=== Task 2a: 4n^2 + 7n - 3 = Theta(n^2) ===")
    // Claim: c1=3, c2=11, n0=1
    c1, c2, n0 := 3.0, 11.0, 1

    verified := true
    for n := n0; n <= 100; n++ {
        nf := float64(n)
        fn := 4*nf*nf + 7*nf - 3
        lower := c1 * nf * nf
        upper := c2 * nf * nf
        if fn < lower || fn > upper {
            fmt.Printf("  FAILED at n=%d: %.0f not in [%.0f, %.0f]\n", n, fn, lower, upper)
            verified = false
        }
    }
    if verified {
        fmt.Printf("  VERIFIED: c1=%.0f, c2=%.0f, n0=%d\n", c1, c2, n0)
    }
}

// Task 2b: Prove n*log2(n) + 5n = Theta(n*log(n))
func task2b() {
    fmt.Println("\n=== Task 2b: n*log2(n) + 5n = Theta(n*log(n)) ===")
    // TODO: Find c1, c2, n0 and verify
    // Hint: for large n, 5n becomes small relative to n*log(n)
    c1, c2 := 1.0, 3.0
    n0 := 4

    verified := true
    for n := n0; n <= 1000; n++ {
        nf := float64(n)
        fn := nf*math.Log2(nf) + 5*nf
        gn := nf * math.Log2(nf)
        lower := c1 * gn
        upper := c2 * gn
        if fn < lower || fn > upper {
            fmt.Printf("  FAILED at n=%d: %.2f not in [%.2f, %.2f]\n", n, fn, lower, upper)
            verified = false
            break
        }
    }
    if verified {
        fmt.Printf("  VERIFIED: c1=%.0f, c2=%.0f, n0=%d\n", c1, c2, n0)
    }
}

func main() {
    task2a()
    task2b()
    // Implement task2c as exercise
    fmt.Println("\nTask 2c: Implement the proof for 2^n + n^3 = Theta(2^n)")
}
```

**Java:**

```java
public class Task2 {

    static void task2a() {
        System.out.println("=== Task 2a: 4n^2 + 7n - 3 = Theta(n^2) ===");
        double c1 = 3, c2 = 11;
        int n0 = 1;
        boolean verified = true;

        for (int n = n0; n <= 100; n++) {
            double fn = 4.0 * n * n + 7 * n - 3;
            double lower = c1 * n * n;
            double upper = c2 * n * n;
            if (fn < lower || fn > upper) {
                System.out.printf("  FAILED at n=%d: %.0f not in [%.0f, %.0f]%n", n, fn, lower, upper);
                verified = false;
            }
        }
        if (verified) {
            System.out.printf("  VERIFIED: c1=%.0f, c2=%.0f, n0=%d%n", c1, c2, n0);
        }
    }

    static void task2b() {
        System.out.println("\n=== Task 2b: n*log2(n) + 5n = Theta(n*log(n)) ===");
        double c1 = 1, c2 = 3;
        int n0 = 4;
        boolean verified = true;

        for (int n = n0; n <= 1000; n++) {
            double lgn = Math.log(n) / Math.log(2);
            double fn = n * lgn + 5.0 * n;
            double gn = n * lgn;
            if (fn < c1 * gn || fn > c2 * gn) {
                System.out.printf("  FAILED at n=%d%n", n);
                verified = false;
                break;
            }
        }
        if (verified) {
            System.out.printf("  VERIFIED: c1=%.0f, c2=%.0f, n0=%d%n", c1, c2, n0);
        }
    }

    public static void main(String[] args) {
        task2a();
        task2b();
        System.out.println("\nTask 2c: Implement the proof for 2^n + n^3 = Theta(2^n)");
    }
}
```

**Python:**

```python
import math


def task2a():
    print("=== Task 2a: 4n^2 + 7n - 3 = Theta(n^2) ===")
    c1, c2, n0 = 3, 11, 1
    verified = True

    for n in range(n0, 101):
        fn = 4 * n**2 + 7 * n - 3
        lower = c1 * n**2
        upper = c2 * n**2
        if fn < lower or fn > upper:
            print(f"  FAILED at n={n}: {fn} not in [{lower}, {upper}]")
            verified = False

    if verified:
        print(f"  VERIFIED: c1={c1}, c2={c2}, n0={n0}")


def task2b():
    print("\n=== Task 2b: n*log2(n) + 5n = Theta(n*log(n)) ===")
    c1, c2, n0 = 1, 3, 4
    verified = True

    for n in range(n0, 1001):
        lgn = math.log2(n)
        fn = n * lgn + 5 * n
        gn = n * lgn
        if fn < c1 * gn or fn > c2 * gn:
            print(f"  FAILED at n={n}")
            verified = False
            break

    if verified:
        print(f"  VERIFIED: c1={c1}, c2={c2}, n0={n0}")


if __name__ == "__main__":
    task2a()
    task2b()
    print("\nTask 2c: Implement the proof for 2^n + n^3 = Theta(2^n)")
```

---

## Task 3: Prove Theta Using Limits

**Difficulty**: Medium

Use the limit method to determine Theta for each:

a) lim (3n^3 + n) / n^3 = ?  =>  Theta(?)
b) lim (n * log(n)) / n^2 = ?  =>  Theta(?)
c) lim (5n^2 + 2n) / (3n^2 + 1) = ?  =>  Are they Theta of each other?

**Hint**: If lim f(n)/g(n) = c where 0 < c < inf, then f(n) = Theta(g(n)).

---

## Task 4: Count Operations and Verify Theta

**Difficulty**: Medium

Write a function that counts the exact number of operations in the following
code pattern and verify it matches the claimed Theta.

```
for i = 1 to n:
    for j = 1 to i*i:
        count++
```

**Claimed**: Theta(n^3).

**Your task**: Count operations for n = 10, 20, 40, 80, 160.
Compute ops/n^3 and verify it converges to a constant.

---

## Task 5: Classify Loop Patterns

**Difficulty**: Easy

For each loop pattern, determine the Theta and implement it to verify.

```
Pattern A:                          Pattern B:
for i = 0 to n:                     i = n
    do_work()                        while i > 0:
                                         do_work()
                                         i = i / 2

Pattern C:                          Pattern D:
for i = 0 to n:                     for i = 0 to n:
    for j = 0 to n:                     j = 1
        for k = 0 to n:                 while j < i:
            do_work()                       j = j * 2
                                            do_work()

Pattern E:
for i = 0 to n:
    for j = i to n:
        do_work()
```

**Expected**: A=Theta(n), B=Theta(log n), C=Theta(n^3), D=Theta(n log n), E=Theta(n^2).

---

## Task 6: Theta of Recursive Functions

**Difficulty**: Hard

Determine the Theta of each recursive function. Write the recurrence relation
first, then solve it.

```
func f1(n):
    if n <= 1: return
    f1(n/2)
    for i = 0 to n: work()
    
func f2(n):
    if n <= 1: return
    f2(n/2)
    f2(n/2)
    for i = 0 to n: work()
    
func f3(n):
    if n <= 1: return
    f3(n-1)
    for i = 0 to n: work()
```

**Expected**:
- f1: T(n) = T(n/2) + n => Theta(n)
- f2: T(n) = 2T(n/2) + n => Theta(n log n)
- f3: T(n) = T(n-1) + n => Theta(n^2)

---

## Task 7: Distinguish O from Theta

**Difficulty**: Medium

For each statement, determine if it is True or False and explain why.

```
a) 5n = O(n^2) and 5n = Theta(n^2)
b) n^2 = O(n^2) and n^2 = Theta(n^2)
c) n*log(n) = O(n^2) and n*log(n) = Theta(n^2)
d) 2^n = O(3^n) and 2^n = Theta(3^n)
e) n + sin(n) = O(n) and n + sin(n) = Theta(n)
```

**Expected**:
a) O: True, Theta: False
b) O: True, Theta: True
c) O: True, Theta: False
d) O: True, Theta: False
e) O: True, Theta: True

---

## Task 8: Empirical Growth Rate Detector

**Difficulty**: Hard

Build a program that takes a function (as a callable), runs it at sizes
n = 1000, 2000, 4000, 8000, 16000, and determines which Theta class it
belongs to from the set {Theta(1), Theta(log n), Theta(n), Theta(n log n),
Theta(n^2), Theta(n^3)}.

Strategy: Compute the ratio ops(2n)/ops(n). The ratios reveal the class:
- Theta(1): ratio = 1
- Theta(log n): ratio ~= 1 + 1/log(n)
- Theta(n): ratio = 2
- Theta(n log n): ratio ~= 2 + small
- Theta(n^2): ratio = 4
- Theta(n^3): ratio = 8

---

## Task 9: Master Theorem Application

**Difficulty**: Medium

Apply the Master Theorem to each recurrence and give the Theta result.

```
a) T(n) = 3T(n/3) + n
b) T(n) = 4T(n/2) + n
c) T(n) = 4T(n/2) + n^2
d) T(n) = T(n/3) + 1
e) T(n) = 2T(n/4) + sqrt(n)
f) T(n) = 8T(n/2) + n^2
```

**Expected answers:**
a) a=3, b=3, c_crit=1, f(n)=n=Theta(n^1*log^0), Case 2 => Theta(n log n)
b) a=4, b=2, c_crit=2, f(n)=n=O(n^1.5), Case 1 => Theta(n^2)
c) a=4, b=2, c_crit=2, f(n)=n^2=Theta(n^2*log^0), Case 2 => Theta(n^2 log n)
d) a=1, b=3, c_crit=0, f(n)=1=Theta(n^0*log^0), Case 2 => Theta(log n)
e) a=2, b=4, c_crit=0.5, f(n)=sqrt(n)=Theta(n^0.5*log^0), Case 2 => Theta(sqrt(n)*log n)
f) a=8, b=2, c_crit=3, f(n)=n^2=O(n^2.5), Case 1 => Theta(n^3)

---

## Task 10: Theta-Based Capacity Planner

**Difficulty**: Hard

Build a capacity planning calculator that:
1. Takes current load (n), current latency, and the Theta class of the operation
2. Predicts latency at future loads
3. Determines when an SLA threshold will be breached
4. Suggests the maximum n that stays within the SLA

Implement in all three languages.

---

## Task 11: Compare Algorithms by Theta

**Difficulty**: Medium

Implement three sorting algorithms and measure their operation counts:
1. Selection sort: Theta(n^2)
2. Merge sort: Theta(n log n)
3. Insertion sort: Theta(n^2) worst, Theta(n) best

Run on sorted, reverse-sorted, and random arrays. Verify the Theta claims.

---

## Task 12: Sandwich Bound Finder

**Difficulty**: Hard

Given a function f(n) and a candidate g(n), write a program that:
1. Computes f(n)/g(n) for increasing n
2. Finds the tightest c1 and c2 such that c1 <= f(n)/g(n) <= c2
3. Determines the smallest n0 where the sandwiching holds
4. Reports whether f(n) = Theta(g(n))

---

## Task 13: Theta of String Operations

**Difficulty**: Medium

Determine and verify the Theta of these string operations:
a) Reversing a string of length n: Theta(n)
b) Checking if a string is a palindrome: Theta(n)
c) Generating all substrings: Theta(n^2) substrings, Theta(n^3) total characters
d) Naive string matching (pattern of length m in text of length n): Theta(n*m) worst

Implement each and count operations.

---

## Task 14: Theta of Matrix Operations

**Difficulty**: Hard

Implement and verify the Theta of:
a) Matrix addition (n x n): Theta(n^2)
b) Naive matrix multiplication (n x n): Theta(n^3)
c) Matrix-vector multiplication (n x n matrix, n vector): Theta(n^2)
d) Matrix transpose (n x n): Theta(n^2)

---

## Task 15: Theta Proof Validator

**Difficulty**: Hard

Build a program that, given:
- f(n) as a function
- g(n) as a function
- Claimed c1, c2, n0

Checks whether the claim f(n) = Theta(g(n)) holds with those constants by
testing all n from n0 to some large N. Report success or the first n where it
fails.

---

## Benchmark: Full Theta Analysis Suite

**Difficulty**: Expert

Build a comprehensive benchmarking suite that:

1. **Input**: An algorithm (as a function that counts operations)
2. **Measures**: Operation counts at sizes n = 500, 1000, 2000, 4000, 8000, 16000
3. **Computes**: Ratios ops/g(n) for g(n) in {1, log n, n, n log n, n^2, n^3}
4. **Determines**: Which g(n) gives a converging ratio (= Theta class)
5. **Finds**: Approximate c1 and c2 constants
6. **Predicts**: Expected ops at n = 100000
7. **Output**: Full report with table and conclusion

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

type BenchResult struct {
    ThetaClass string
    C1, C2     float64
    Predicted  float64
}

func analyzeThetaBench(name string, algo func(int) int64) BenchResult {
    sizes := []int{500, 1000, 2000, 4000, 8000, 16000}
    ops := make([]int64, len(sizes))

    for i, n := range sizes {
        ops[i] = algo(n)
    }

    type candidate struct {
        name string
        fn   func(float64) float64
    }
    candidates := []candidate{
        {"Theta(1)", func(n float64) float64 { return 1 }},
        {"Theta(log n)", func(n float64) float64 { return math.Log2(n) }},
        {"Theta(n)", func(n float64) float64 { return n }},
        {"Theta(n log n)", func(n float64) float64 { return n * math.Log2(n) }},
        {"Theta(n^2)", func(n float64) float64 { return n * n }},
        {"Theta(n^3)", func(n float64) float64 { return n * n * n }},
    }

    fmt.Printf("\n=== Benchmark: %s ===\n", name)
    fmt.Printf("%-8s %-15s", "n", "ops")
    for _, c := range candidates {
        fmt.Printf(" %-12s", "ops/"+c.name[6:])
    }
    fmt.Println()

    ratios := make([][]float64, len(candidates))
    for i := range candidates {
        ratios[i] = make([]float64, len(sizes))
    }

    for i, n := range sizes {
        nf := float64(n)
        of := float64(ops[i])
        fmt.Printf("%-8d %-15d", n, ops[i])
        for j, c := range candidates {
            r := of / c.fn(nf)
            ratios[j][i] = r
            fmt.Printf(" %-12.4f", r)
        }
        fmt.Println()
    }

    // Find best candidate: smallest variance in ratio
    bestIdx := 0
    bestVar := math.Inf(1)

    for j := range candidates {
        // compute variance of last 4 ratios
        vals := ratios[j][len(ratios[j])-4:]
        mean := 0.0
        for _, v := range vals {
            mean += v
        }
        mean /= float64(len(vals))

        variance := 0.0
        for _, v := range vals {
            d := v - mean
            variance += d * d
        }
        variance /= float64(len(vals))
        relVar := variance / (mean * mean) // coefficient of variation squared

        if relVar < bestVar {
            bestVar = relVar
            bestIdx = j
        }
    }

    bestRatios := ratios[bestIdx]
    c1 := bestRatios[len(bestRatios)-1]
    c2 := bestRatios[len(bestRatios)-1]
    for _, r := range bestRatios[2:] {
        if r < c1 {
            c1 = r
        }
        if r > c2 {
            c2 = r
        }
    }

    predicted := ((c1 + c2) / 2) * candidates[bestIdx].fn(100000)

    fmt.Printf("\nConclusion: %s\n", candidates[bestIdx].name)
    fmt.Printf("Approximate c1=%.4f, c2=%.4f\n", c1, c2)
    fmt.Printf("Predicted ops at n=100000: %.0f\n", predicted)

    return BenchResult{
        ThetaClass: candidates[bestIdx].name,
        C1:         c1,
        C2:         c2,
        Predicted:  predicted,
    }
}

func main() {
    // Test with known algorithms
    analyzeThetaBench("Linear scan", func(n int) int64 {
        var ops int64
        for i := 0; i < n; i++ {
            ops++
        }
        return ops
    })

    analyzeThetaBench("Nested loop (triangular)", func(n int) int64 {
        var ops int64
        for i := 0; i < n; i++ {
            for j := 0; j <= i; j++ {
                ops++
            }
        }
        return ops
    })

    analyzeThetaBench("n*log(n) loop", func(n int) int64 {
        var ops int64
        for i := 0; i < n; i++ {
            j := n
            for j > 0 {
                ops++
                j /= 2
            }
        }
        return ops
    })
}
```

**Java:**

```java
import java.util.function.IntToLongFunction;

public class ThetaBenchmark {

    static void analyze(String name, IntToLongFunction algo) {
        int[] sizes = {500, 1000, 2000, 4000, 8000, 16000};
        long[] ops = new long[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            ops[i] = algo.applyAsLong(sizes[i]);
        }

        System.out.printf("%n=== Benchmark: %s ===%n", name);
        System.out.printf("%-8s %-15s %-12s %-12s %-12s %-12s%n",
            "n", "ops", "ops/n", "ops/(nlgn)", "ops/n^2", "ops/n^3");

        for (int i = 0; i < sizes.length; i++) {
            double n = sizes[i], o = ops[i];
            double lgn = Math.log(n) / Math.log(2);
            System.out.printf("%-8d %-15d %-12.4f %-12.4f %-12.6f %-12.8f%n",
                sizes[i], ops[i], o / n, o / (n * lgn), o / (n * n), o / (n * n * n));
        }
    }

    public static void main(String[] args) {
        analyze("Linear scan", n -> { long ops = 0; for (int i = 0; i < n; i++) ops++; return ops; });

        analyze("Triangular loop", n -> {
            long ops = 0;
            for (int i = 0; i < n; i++)
                for (int j = 0; j <= i; j++) ops++;
            return ops;
        });

        analyze("n*log(n) loop", n -> {
            long ops = 0;
            for (int i = 0; i < n; i++) {
                int j = n;
                while (j > 0) { ops++; j /= 2; }
            }
            return ops;
        });
    }
}
```

**Python:**

```python
import math
from typing import Callable


def analyze_theta(name: str, algo: Callable[[int], int]):
    sizes = [500, 1000, 2000, 4000, 8000, 16000]
    ops = [algo(n) for n in sizes]

    candidates = {
        "Theta(1)":       lambda n: 1,
        "Theta(log n)":   lambda n: math.log2(n),
        "Theta(n)":       lambda n: n,
        "Theta(n log n)": lambda n: n * math.log2(n),
        "Theta(n^2)":     lambda n: n**2,
        "Theta(n^3)":     lambda n: n**3,
    }

    print(f"\n=== Benchmark: {name} ===")
    header = f"{'n':<8} {'ops':<15}"
    for cname in candidates:
        header += f" {'ops/'+cname[6:]:<12}"
    print(header)

    all_ratios = {c: [] for c in candidates}

    for n, op in zip(sizes, ops):
        line = f"{n:<8} {op:<15}"
        for cname, gfn in candidates.items():
            ratio = op / gfn(n)
            all_ratios[cname].append(ratio)
            line += f" {ratio:<12.4f}"
        print(line)

    # Find best: lowest coefficient of variation in last 4 ratios
    best_name = None
    best_cv = float("inf")

    for cname, rats in all_ratios.items():
        recent = rats[-4:]
        mean = sum(recent) / len(recent)
        if mean == 0:
            continue
        var = sum((r - mean) ** 2 for r in recent) / len(recent)
        cv = var / (mean ** 2)
        if cv < best_cv:
            best_cv = cv
            best_name = cname

    recent = all_ratios[best_name][-4:]
    c1, c2 = min(recent), max(recent)
    avg_c = (c1 + c2) / 2
    predicted = avg_c * candidates[best_name](100_000)

    print(f"\nConclusion: {best_name}")
    print(f"Approximate c1={c1:.4f}, c2={c2:.4f}")
    print(f"Predicted ops at n=100000: {predicted:.0f}")


if __name__ == "__main__":
    analyze_theta("Linear scan", lambda n: n)
    analyze_theta("Triangular loop", lambda n: n * (n + 1) // 2)

    def nlogn_algo(n):
        ops = 0
        for i in range(n):
            j = n
            while j > 0:
                ops += 1
                j //= 2
        return ops

    analyze_theta("n*log(n) loop", nlogn_algo)
```

---

*Complete all 15 tasks to master Big-Theta notation analysis.*
