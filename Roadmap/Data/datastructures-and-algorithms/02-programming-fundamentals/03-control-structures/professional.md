# Control Structures — Mathematical Foundations

## Table of Contents

1. [Structured Programming Theorem](#structured-programming-theorem)
2. [Control Flow Graph Analysis](#control-flow-graph-analysis)
3. [Loop Invariants and Termination](#loop-invariants-and-termination)
4. [Halting Problem](#halting-problem)
5. [Branch Prediction and CPU Pipeline](#branch-prediction)
6. [Summary](#summary)

---

## Structured Programming Theorem

```text
Theorem (Bohm-Jacopini, 1966):
Any computable function can be expressed using only three control structures:
  1. Sequence (S₁ ; S₂)
  2. Selection (if P then S₁ else S₂)
  3. Iteration (while P do S)

Corollary: goto is NEVER necessary for expressiveness.
           (But may be useful for performance in specific cases)

Proof sketch:
  Any Turing machine can be simulated by a program using only
  sequence, selection, and while loops.
  Since Turing machines are computationally complete,
  these three structures are sufficient. ∎
```

---

## Control Flow Graph Analysis

```text
A Control Flow Graph (CFG) G = (V, E, entry, exit) where:
  V = basic blocks (maximal sequences of instructions with no branches)
  E = edges (possible control flow transitions)
  entry = unique entry block
  exit = unique exit block

Dominator: Block A dominates block B if every path from entry to B
           passes through A.

Loop detection: A natural loop has:
  - A header block (dominates all blocks in the loop)
  - A back edge (from a block in the loop to the header)
  - All blocks on paths from header to back-edge source

Cyclomatic complexity (McCabe, 1976):
  M = E - V + 2P
  where E = edges, V = vertices, P = connected components (usually 1)

  Practical: M = number of decision points + 1
  M ≤ 10: acceptable
  M > 20: refactor needed
```

---

## Loop Invariants and Termination

### Formal Loop Verification

```text
To prove a while loop correct, establish:

1. INVARIANT I: predicate true before each iteration
   - Initialization: I holds before the loop starts
   - Maintenance: {I ∧ guard} body {I}  (Hoare triple)

2. VARIANT v: integer expression that:
   - Decreases strictly each iteration: v_after < v_before
   - Is bounded below: v ≥ 0
   - When v reaches 0 (or below), the guard is false

3. POSTCONDITION: I ∧ ¬guard → desired result
```

### Example: Integer Division

```text
FUNCTION divide(a, b)   // a ≥ 0, b > 0
    SET q = 0
    SET r = a
    WHILE r ≥ b DO
        SET r = r - b
        SET q = q + 1
    END WHILE
    RETURN q, r

Invariant I: a = q*b + r  ∧  r ≥ 0
Variant v: r (decreases by b each iteration, bounded below by 0)

Proof:
  Init: q=0, r=a → a = 0*b + a ✓, r = a ≥ 0 ✓
  Maint: Assume a = q*b + r and r ≥ b.
         After: q' = q+1, r' = r-b
         a = q*b + r = q*b + (r'+b) = (q+1)*b + r' = q'*b + r' ✓
         r' = r - b ≥ 0 (since r ≥ b) ✓
  Term: v = r decreases by b > 0 each iteration. Since r ≥ 0, terminates. ✓
  Post: I ∧ r < b → a = q*b + r, 0 ≤ r < b ✓ (correct quotient and remainder) ∎
```

---

## Halting Problem

```text
Theorem (Turing, 1936):
The halting problem is undecidable. There is no algorithm H such that
for every program P and input I:
  H(P, I) = true   if P halts on I
  H(P, I) = false  if P loops forever on I

Proof (by contradiction):
  Assume H exists. Define D(P):
    if H(P, P) == true then LOOP FOREVER
    else HALT

  Consider D(D):
    If H(D, D) = true → D loops → but H said it halts → contradiction
    If H(D, D) = false → D halts → but H said it loops → contradiction

  Therefore H cannot exist. ∎

Practical implication:
  No compiler can detect ALL infinite loops.
  Static analysis can detect SOME patterns (variant analysis),
  but the general problem is unsolvable.
```

---

## Branch Prediction and CPU Pipeline

```text
Modern CPUs use pipelining: fetch → decode → execute → write-back.
A branch (if/else) disrupts the pipeline because the CPU doesn't know
which path to take until the condition is evaluated.

Branch predictor: CPU guesses which branch to take.
  - Static prediction: forward branches not taken, backward taken (loops)
  - Dynamic prediction: uses history table (BHT) of recent branch outcomes
  - Accuracy: 90-97% for typical code

Branch misprediction penalty: 15-20 cycles on modern x86.

Implication for algorithms:
  Sorted data → predictable branches → faster
  Random data → unpredictable branches → slower

Example: Processing sorted vs unsorted array
```

### Benchmark: Branch Prediction Effect

#### Go

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

func sumAboveThreshold(arr []int, threshold int) int {
    sum := 0
    for _, v := range arr {
        if v > threshold {  // this branch is the bottleneck
            sum += v
        }
    }
    return sum
}

func main() {
    n := 10_000_000
    arr := make([]int, n)
    for i := range arr {
        arr[i] = rand.Intn(256)
    }

    // Unsorted — branches unpredictable
    start := time.Now()
    sumAboveThreshold(arr, 128)
    fmt.Printf("Unsorted: %v\n", time.Since(start))

    // Sorted — branches predictable
    sort.Ints(arr)
    start = time.Now()
    sumAboveThreshold(arr, 128)
    fmt.Printf("Sorted:   %v\n", time.Since(start))
}
```

#### Java

```java
import java.util.Arrays;
import java.util.Random;

public class BranchPrediction {
    public static long sumAbove(int[] arr, int threshold) {
        long sum = 0;
        for (int v : arr) {
            if (v > threshold) sum += v;
        }
        return sum;
    }

    public static void main(String[] args) {
        int n = 10_000_000;
        int[] arr = new Random().ints(n, 0, 256).toArray();

        // Warm up JIT
        for (int i = 0; i < 5; i++) sumAbove(arr, 128);

        long start = System.nanoTime();
        sumAbove(arr, 128);
        System.out.printf("Unsorted: %.2f ms%n", (System.nanoTime() - start) / 1e6);

        Arrays.sort(arr);
        start = System.nanoTime();
        sumAbove(arr, 128);
        System.out.printf("Sorted:   %.2f ms%n", (System.nanoTime() - start) / 1e6);
    }
}
```

#### Python

```python
import random
import time

def sum_above(arr, threshold):
    total = 0
    for v in arr:
        if v > threshold:
            total += v
    return total

n = 10_000_000
arr = [random.randint(0, 255) for _ in range(n)]

start = time.perf_counter()
sum_above(arr, 128)
print(f"Unsorted: {(time.perf_counter() - start)*1000:.1f} ms")

arr.sort()
start = time.perf_counter()
sum_above(arr, 128)
print(f"Sorted:   {(time.perf_counter() - start)*1000:.1f} ms")

# Note: Python's interpreter overhead dominates, so the effect is less visible
# Branchless alternative:
total = sum(v for v in arr if v > 128)
```

---

## Summary

At the professional level, control structures connect to fundamental CS theory: the structured programming theorem proves three constructs suffice, loop invariants provide formal correctness, the halting problem sets limits on static analysis, and branch prediction shows how control flow impacts hardware performance.
