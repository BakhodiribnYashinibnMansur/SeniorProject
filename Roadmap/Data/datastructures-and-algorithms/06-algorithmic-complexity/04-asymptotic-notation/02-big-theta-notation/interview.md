# Big-Theta Notation -- Interview Guide

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Proof-Based Questions](#proof-based-questions)
3. [Algorithm Analysis Questions](#algorithm-analysis-questions)
4. [Coding Challenge](#coding-challenge)
5. [Quick-Fire Round](#quick-fire-round)

---

## Conceptual Questions

### Q1: What is the difference between Big-O, Big-Theta, and Big-Omega?

**Strong Answer:**
- Big-O is an upper bound: f(n) = O(g(n)) means f grows at most as fast as g.
- Big-Omega is a lower bound: f(n) = Omega(g(n)) means f grows at least as fast as g.
- Big-Theta is a tight bound: f(n) = Theta(g(n)) means f grows at exactly the
  same rate as g (both upper AND lower bound).
- Theta = O AND Omega. If f(n) is both O(g(n)) and Omega(g(n)), then f(n) = Theta(g(n)).

**Red flag answer:** "They are all the same thing" or "Big-O is the average case."

---

### Q2: Is it correct to say "quicksort is Theta(n log n)"?

**Strong Answer:**
Not without qualification. Quicksort has different complexities for different cases:
- Best case: Theta(n log n)
- Average case: Theta(n log n)
- Worst case: Theta(n^2)

Since best and worst differ, we cannot assign a single Theta. We should say
"quicksort average case is Theta(n log n)" or "quicksort is O(n^2) in the worst
case."

---

### Q3: If f(n) = O(n^2), does that mean f(n) = Theta(n^2)?

**Strong Answer:**
No. O(n^2) only gives an upper bound. The function could grow much slower.
For example, f(n) = n is O(n^2), but n is NOT Theta(n^2) because n is not
Omega(n^2). To prove Theta(n^2), you also need to show f(n) = Omega(n^2).

---

### Q4: Give an example of an algorithm that IS Theta(n log n) in all cases.

**Strong Answer:**
Merge sort. Regardless of input order, merge sort always:
1. Divides the array into halves (log n levels)
2. Merges all n elements at each level
3. Total work: n * log(n) in every case

Other examples: heap sort (worst case Theta(n log n), though best case can be
different for some implementations).

---

### Q5: Why might a senior engineer prefer Theta over O in system design?

**Strong Answer:**
Theta gives a tight bound, which enables:
- Accurate capacity planning (exact growth prediction)
- Tighter SLAs (both minimum and maximum performance guarantees)
- Better auto-scaling rules (predictable resource consumption)
- Precise cost modeling (compute costs scale exactly with Theta)

O alone only gives an upper bound, which can lead to over-provisioning.

---

## Proof-Based Questions

### Q6: Prove that 2n^2 + 3n + 1 = Theta(n^2).

**Answer:**

Upper bound: For n >= 1, 3n <= 3n^2 and 1 <= n^2, so
2n^2 + 3n + 1 <= 2n^2 + 3n^2 + n^2 = 6n^2. Choose c2 = 6.

Lower bound: For n >= 0, 3n >= 0 and 1 >= 0, so
2n^2 + 3n + 1 >= 2n^2. Choose c1 = 2.

With c1 = 2, c2 = 6, n0 = 1: 2n^2 <= 2n^2 + 3n + 1 <= 6n^2. QED.

---

### Q7: Prove that n is NOT Theta(n^2).

**Answer:**

Assume for contradiction that n = Theta(n^2). Then there exists c1 > 0, n0 such
that c1 * n^2 <= n for all n >= n0.

Dividing by n: c1 * n <= 1, which means n <= 1/c1.

But this cannot hold for all n >= n0 since n grows without bound.
Contradiction. Therefore n is not Theta(n^2). QED.

---

### Q8: Using the limit method, prove that n*log(n) + n = Theta(n*log(n)).

**Answer:**

```
lim (n*log(n) + n) / (n*log(n))
= lim (1 + 1/log(n))
= 1 + 0
= 1
```

Since 0 < 1 < infinity, by the limit theorem, n*log(n) + n = Theta(n*log(n)). QED.

---

## Algorithm Analysis Questions

### Q9: What is the Theta complexity of this code?

```
for i = 1 to n:
    j = 1
    while j < n:
        j = j * 2
```

**Answer:** Theta(n log n).
- Outer loop runs n times.
- Inner loop: j doubles each time, so it runs log2(n) times.
- Total: n * log(n) iterations. This is the same in all cases (no data-dependent branching).
- Therefore: Theta(n log n).

---

### Q10: Analyze this code:

```
for i = 1 to n:
    for j = 1 to i*i:
        print("hello")
```

**Answer:** Theta(n^3).
Total operations = sum of i^2 for i = 1 to n = n(n+1)(2n+1)/6 = Theta(n^3).

---

## Coding Challenge

**Problem**: Write a function that empirically determines whether a given
algorithm is Theta(n), Theta(n log n), or Theta(n^2) by measuring operation
counts at different input sizes and computing growth ratios.

### Go:

```go
package main

import (
    "fmt"
    "math"
)

// AlgorithmFunc takes a size n and returns the number of operations performed
type AlgorithmFunc func(n int) int64

// classifyTheta runs the algorithm at multiple sizes and determines
// which Theta class it belongs to by analyzing growth ratios
func classifyTheta(name string, algo AlgorithmFunc, sizes []int) string {
    type measurement struct {
        n   int
        ops int64
    }

    measurements := make([]measurement, len(sizes))
    for i, n := range sizes {
        measurements[i] = measurement{n: n, ops: algo(n)}
    }

    fmt.Printf("\nAlgorithm: %s\n", name)
    fmt.Printf("%-10s %-15s %-12s %-12s %-12s\n",
        "n", "ops", "ops/n", "ops/(n*lgn)", "ops/n^2")

    for _, m := range measurements {
        nf := float64(m.n)
        opsf := float64(m.ops)
        lgn := math.Log2(nf)
        fmt.Printf("%-10d %-15d %-12.4f %-12.4f %-12.6f\n",
            m.n, m.ops,
            opsf/nf,
            opsf/(nf*lgn),
            opsf/(nf*nf))
    }

    // Check which ratio converges (use last two measurements)
    last := measurements[len(measurements)-1]
    prev := measurements[len(measurements)-2]

    nL, nP := float64(last.n), float64(prev.n)
    oL, oP := float64(last.ops), float64(prev.ops)

    ratioN := (oL / nL) / (oP / nP)
    ratioNLogN := (oL / (nL * math.Log2(nL))) / (oP / (nP * math.Log2(nP)))
    ratioN2 := (oL / (nL * nL)) / (oP / (nP * nP))

    // The ratio closest to 1.0 indicates the correct class
    diffN := math.Abs(ratioN - 1.0)
    diffNLogN := math.Abs(ratioNLogN - 1.0)
    diffN2 := math.Abs(ratioN2 - 1.0)

    if diffN <= diffNLogN && diffN <= diffN2 {
        return "Theta(n)"
    } else if diffNLogN <= diffN && diffNLogN <= diffN2 {
        return "Theta(n log n)"
    }
    return "Theta(n^2)"
}

// Sample algorithms to classify
func linearAlgo(n int) int64 {
    var ops int64
    for i := 0; i < n; i++ {
        ops++
    }
    return ops
}

func nLogNAlgo(n int) int64 {
    var ops int64
    for i := 0; i < n; i++ {
        j := n
        for j > 0 {
            ops++
            j /= 2
        }
    }
    return ops
}

func quadraticAlgo(n int) int64 {
    var ops int64
    for i := 0; i < n; i++ {
        for j := 0; j <= i; j++ {
            ops++
        }
    }
    return ops
}

func main() {
    sizes := []int{1000, 2000, 4000, 8000, 16000}

    result1 := classifyTheta("Linear sum", linearAlgo, sizes)
    fmt.Printf("Classification: %s\n", result1)

    result2 := classifyTheta("n*log(n) loop", nLogNAlgo, sizes)
    fmt.Printf("Classification: %s\n", result2)

    result3 := classifyTheta("Triangular loop", quadraticAlgo, sizes)
    fmt.Printf("Classification: %s\n", result3)
}
```

### Java:

```java
import java.util.function.IntToLongFunction;

public class ThetaClassifier {

    static String classifyTheta(String name, IntToLongFunction algo, int[] sizes) {
        long[] ops = new long[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            ops[i] = algo.applyAsLong(sizes[i]);
        }

        System.out.printf("%nAlgorithm: %s%n", name);
        System.out.printf("%-10s %-15s %-12s %-12s %-12s%n",
            "n", "ops", "ops/n", "ops/(n*lgn)", "ops/n^2");

        for (int i = 0; i < sizes.length; i++) {
            double n = sizes[i];
            double o = ops[i];
            double lgn = Math.log(n) / Math.log(2);
            System.out.printf("%-10d %-15d %-12.4f %-12.4f %-12.6f%n",
                sizes[i], ops[i], o / n, o / (n * lgn), o / (n * n));
        }

        int last = sizes.length - 1;
        double nL = sizes[last], nP = sizes[last - 1];
        double oL = ops[last], oP = ops[last - 1];

        double ratioN = (oL / nL) / (oP / nP);
        double ratioNLogN = (oL / (nL * Math.log(nL))) / (oP / (nP * Math.log(nP)));
        double ratioN2 = (oL / (nL * nL)) / (oP / (nP * nP));

        double diffN = Math.abs(ratioN - 1.0);
        double diffNLogN = Math.abs(ratioNLogN - 1.0);
        double diffN2 = Math.abs(ratioN2 - 1.0);

        if (diffN <= diffNLogN && diffN <= diffN2) return "Theta(n)";
        if (diffNLogN <= diffN && diffNLogN <= diffN2) return "Theta(n log n)";
        return "Theta(n^2)";
    }

    static long linearAlgo(int n) {
        long ops = 0;
        for (int i = 0; i < n; i++) ops++;
        return ops;
    }

    static long nLogNAlgo(int n) {
        long ops = 0;
        for (int i = 0; i < n; i++) {
            int j = n;
            while (j > 0) { ops++; j /= 2; }
        }
        return ops;
    }

    static long quadraticAlgo(int n) {
        long ops = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) ops++;
        }
        return ops;
    }

    public static void main(String[] args) {
        int[] sizes = {1000, 2000, 4000, 8000, 16000};
        System.out.println("Result: " + classifyTheta("Linear sum", ThetaClassifier::linearAlgo, sizes));
        System.out.println("Result: " + classifyTheta("n*log(n) loop", ThetaClassifier::nLogNAlgo, sizes));
        System.out.println("Result: " + classifyTheta("Triangular loop", ThetaClassifier::quadraticAlgo, sizes));
    }
}
```

### Python:

```python
import math
from typing import Callable


def classify_theta(name: str, algo: Callable[[int], int], sizes: list[int]) -> str:
    ops_list = [algo(n) for n in sizes]

    print(f"\nAlgorithm: {name}")
    print(f"{'n':<10} {'ops':<15} {'ops/n':<12} {'ops/(n*lgn)':<12} {'ops/n^2':<12}")

    for n, ops in zip(sizes, ops_list):
        lgn = math.log2(n)
        print(f"{n:<10} {ops:<15} {ops/n:<12.4f} {ops/(n*lgn):<12.4f} {ops/n**2:<12.6f}")

    n_l, n_p = float(sizes[-1]), float(sizes[-2])
    o_l, o_p = float(ops_list[-1]), float(ops_list[-2])

    ratio_n = (o_l / n_l) / (o_p / n_p)
    ratio_nlogn = (o_l / (n_l * math.log(n_l))) / (o_p / (n_p * math.log(n_p)))
    ratio_n2 = (o_l / (n_l * n_l)) / (o_p / (n_p * n_p))

    diff_n = abs(ratio_n - 1.0)
    diff_nlogn = abs(ratio_nlogn - 1.0)
    diff_n2 = abs(ratio_n2 - 1.0)

    if diff_n <= diff_nlogn and diff_n <= diff_n2:
        return "Theta(n)"
    elif diff_nlogn <= diff_n and diff_nlogn <= diff_n2:
        return "Theta(n log n)"
    return "Theta(n^2)"


def linear_algo(n: int) -> int:
    return n


def nlogn_algo(n: int) -> int:
    ops = 0
    for _ in range(n):
        j = n
        while j > 0:
            ops += 1
            j //= 2
    return ops


def quadratic_algo(n: int) -> int:
    return n * (n + 1) // 2


if __name__ == "__main__":
    sizes = [1000, 2000, 4000, 8000, 16000]
    print("Result:", classify_theta("Linear sum", linear_algo, sizes))
    print("Result:", classify_theta("n*log(n) loop", nlogn_algo, sizes))
    print("Result:", classify_theta("Triangular loop", quadratic_algo, sizes))
```

---

## Quick-Fire Round

**Q: Is 100 = Theta(1)?** Yes. Constants are Theta(1).

**Q: Is n + 1000000 = Theta(n)?** Yes. The constant becomes negligible.

**Q: Is 2^(n+1) = Theta(2^n)?** Yes. 2^(n+1) = 2 * 2^n, and constants don't matter.

**Q: Is 2^(2n) = Theta(2^n)?** No. 2^(2n) = (2^n)^2, which grows much faster.

**Q: Name the three properties of Theta.** Reflexive, symmetric, transitive.

**Q: Can Theta be used for space complexity?** Yes. Same definitions apply.

**Q: Selection sort is Theta(?) in all cases.** Theta(n^2). It always scans
the remaining array regardless of input.

**Q: Hash table lookup is Theta(?).** Theta(1) average case (amortized). But
worst case is Theta(n) with collisions.

---

*Next: Continue to [Tasks](tasks.md) for hands-on practice.*
