# Exponential Time O(2^n) — Senior Level

## Table of Contents

- [Exponential Algorithms in Cryptography](#exponential-algorithms-in-cryptography)
  - [Brute-Force Key Search](#brute-force-key-search)
  - [Birthday Attacks and Hash Collisions](#birthday-attacks-and-hash-collisions)
  - [Why Key Length Matters](#why-key-length-matters)
- [NP-Hard Problems in Production](#np-hard-problems-in-production)
  - [Job Scheduling](#job-scheduling)
  - [Vehicle Routing](#vehicle-routing)
  - [Resource Allocation](#resource-allocation)
- [SAT Solvers: Taming Exponential in Practice](#sat-solvers-taming-exponential-in-practice)
  - [Boolean Satisfiability (SAT)](#boolean-satisfiability-sat)
  - [DPLL Algorithm](#dpll-algorithm)
  - [CDCL: Conflict-Driven Clause Learning](#cdcl-conflict-driven-clause-learning)
- [Practical Heuristics for Production Systems](#practical-heuristics-for-production-systems)
  - [Greedy Approximations](#greedy-approximations)
  - [Simulated Annealing](#simulated-annealing)
  - [Genetic Algorithms](#genetic-algorithms)
  - [Branch and Bound](#branch-and-bound)
- [System Design Considerations](#system-design-considerations)
- [Summary](#summary)

---

## Exponential Algorithms in Cryptography

Cryptographic security fundamentally depends on the assumption that certain operations require exponential time. If breaking a cipher takes O(2^n) where n is the key length, then a sufficiently large key makes brute force infeasible.

### Brute-Force Key Search

For symmetric encryption (AES, etc.), brute-force key search means trying all possible keys. With a key of length n bits, there are 2^n possible keys.

**Go:**

```go
package main

import (
    "crypto/aes"
    "crypto/rand"
    "fmt"
    "time"
)

// SimulateBruteForce demonstrates the concept of brute-force key search.
// In reality, 2^128 iterations are infeasible — this is for illustration.
func SimulateBruteForce(keyBits int, maxAttempts int64) {
    totalKeys := int64(1) << keyBits // 2^keyBits (only works for small keyBits)
    fmt.Printf("Key space for %d-bit key: 2^%d = %d\n", keyBits, keyBits, totalKeys)

    // Generate a random "target" key
    targetKey := make([]byte, 16)
    rand.Read(targetKey)

    start := time.Now()
    var attempts int64
    for attempts = 0; attempts < maxAttempts; attempts++ {
        // In real brute force, you'd try each key
        // Here we just measure iteration speed
        _ = aes.BlockSize // Just to reference the package
    }
    elapsed := time.Since(start)

    opsPerSec := float64(attempts) / elapsed.Seconds()
    yearsToBreak := float64(totalKeys) / opsPerSec / (365.25 * 24 * 3600)
    fmt.Printf("Speed: %.2e attempts/sec\n", opsPerSec)
    fmt.Printf("Time to exhaust %d-bit key space: %.2e years\n", keyBits, yearsToBreak)
}

func main() {
    SimulateBruteForce(20, 1_000_000)  // 20-bit: trivial
    SimulateBruteForce(40, 1_000_000)  // 40-bit: possible
    // 128-bit: impossible (2^128 ~ 3.4 * 10^38)
}
```

**Java:**

```java
public class BruteForceKeySearch {

    public static void simulateBruteForce(int keyBits, long maxAttempts) {
        long totalKeys = 1L << Math.min(keyBits, 62);
        System.out.printf("Key space for %d-bit key: 2^%d%n", keyBits, keyBits);

        long start = System.nanoTime();
        for (long i = 0; i < maxAttempts; i++) {
            // Simulating iteration overhead
        }
        double elapsed = (System.nanoTime() - start) / 1e9;

        double opsPerSec = maxAttempts / elapsed;
        double yearsToBreak = Math.pow(2, keyBits) / opsPerSec / (365.25 * 24 * 3600);
        System.out.printf("Speed: %.2e attempts/sec%n", opsPerSec);
        System.out.printf("Time to exhaust: %.2e years%n%n", yearsToBreak);
    }

    public static void main(String[] args) {
        simulateBruteForce(20, 1_000_000);
        simulateBruteForce(40, 1_000_000);
        simulateBruteForce(128, 1_000_000);
    }
}
```

**Python:**

```python
import time
import math


def simulate_brute_force(key_bits: int, max_attempts: int = 1_000_000) -> None:
    print(f"Key space for {key_bits}-bit key: 2^{key_bits}")

    start = time.perf_counter()
    for _ in range(max_attempts):
        pass  # Simulating iteration overhead
    elapsed = time.perf_counter() - start

    ops_per_sec = max_attempts / elapsed
    years_to_break = 2**key_bits / ops_per_sec / (365.25 * 24 * 3600)
    print(f"Speed: {ops_per_sec:.2e} attempts/sec")
    print(f"Time to exhaust: {years_to_break:.2e} years\n")


if __name__ == "__main__":
    simulate_brute_force(20)
    simulate_brute_force(40)
    simulate_brute_force(128)
```

### Birthday Attacks and Hash Collisions

Finding a collision in a hash function with n-bit output requires approximately O(2^(n/2)) attempts due to the birthday paradox. This is why SHA-256 (256-bit output) provides 128-bit security against collision attacks, not 256-bit.

### Why Key Length Matters

| Key Length | Brute-Force Time (10^12 ops/sec) | Security Level |
|-----------|----------------------------------|---------------|
| 56 bits   | ~20 hours                         | Broken (DES)  |
| 80 bits   | ~38 years                         | Weak          |
| 128 bits  | ~10^14 years                      | Secure        |
| 256 bits  | ~10^52 years                      | Ultra-secure  |

Every additional bit doubles the brute-force time. Adding 10 bits multiplies the work by 1024. This is the exponential barrier that protects modern cryptography.

---

## NP-Hard Problems in Production

Real production systems frequently encounter NP-hard problems that have no known polynomial-time solutions. The practical approach is to use heuristics, approximations, and carefully bounded exact algorithms.

### Job Scheduling

Given n jobs with deadlines, processing times, and dependencies, find an optimal schedule. The general case is NP-hard.

**Go:**

```go
package main

import "fmt"

type Job struct {
    ID       int
    Duration int
    Deadline int
    Profit   int
}

// ScheduleDP finds the maximum profit subset of non-overlapping jobs.
// Uses bitmask DP: O(2^n * n) time and space.
// Practical for n <= 20.
func ScheduleDP(jobs []Job) int {
    n := len(jobs)
    maxProfit := 0

    for mask := 0; mask < (1 << n); mask++ {
        profit := 0
        time := 0
        valid := true

        for i := 0; i < n; i++ {
            if mask&(1<<i) != 0 {
                time += jobs[i].Duration
                if time > jobs[i].Deadline {
                    valid = false
                    break
                }
                profit += jobs[i].Profit
            }
        }

        if valid && profit > maxProfit {
            maxProfit = profit
        }
    }
    return maxProfit
}

func main() {
    jobs := []Job{
        {1, 2, 4, 10},
        {2, 1, 2, 5},
        {3, 3, 6, 15},
        {4, 2, 8, 7},
    }
    fmt.Printf("Maximum profit: %d\n", ScheduleDP(jobs))
}
```

**Java:**

```java
public class JobScheduling {

    static int scheduleDP(int[][] jobs) {
        // jobs[i] = {duration, deadline, profit}
        int n = jobs.length;
        int maxProfit = 0;

        for (int mask = 0; mask < (1 << n); mask++) {
            int profit = 0, time = 0;
            boolean valid = true;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    time += jobs[i][0];
                    if (time > jobs[i][1]) { valid = false; break; }
                    profit += jobs[i][2];
                }
            }
            if (valid) maxProfit = Math.max(maxProfit, profit);
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        int[][] jobs = {{2, 4, 10}, {1, 2, 5}, {3, 6, 15}, {2, 8, 7}};
        System.out.printf("Maximum profit: %d%n", scheduleDP(jobs));
    }
}
```

**Python:**

```python
from typing import List, Tuple


def schedule_dp(jobs: List[Tuple[int, int, int]]) -> int:
    """
    Bitmask DP over all subsets of jobs.
    jobs[i] = (duration, deadline, profit)
    Time: O(2^n * n), Space: O(1)
    """
    n = len(jobs)
    max_profit = 0

    for mask in range(1 << n):
        profit, time, valid = 0, 0, True
        for i in range(n):
            if mask & (1 << i):
                time += jobs[i][0]
                if time > jobs[i][1]:
                    valid = False
                    break
                profit += jobs[i][2]
        if valid:
            max_profit = max(max_profit, profit)

    return max_profit


if __name__ == "__main__":
    jobs = [(2, 4, 10), (1, 2, 5), (3, 6, 15), (2, 8, 7)]
    print(f"Maximum profit: {schedule_dp(jobs)}")
```

### Vehicle Routing

The vehicle routing problem (VRP) is a generalization of TSP: given a fleet of vehicles and a set of delivery locations, find optimal routes. The exact solution requires enumerating routes — exponential in the number of locations. Production systems like Google OR-Tools use constraint programming and metaheuristics to find near-optimal solutions.

### Resource Allocation

Assigning n resources to m tasks with constraints and costs. When the constraints are complex, the exact solution may require exploring exponential combinations. In practice, integer linear programming (ILP) solvers handle this efficiently for moderate-sized instances.

---

## SAT Solvers: Taming Exponential in Practice

### Boolean Satisfiability (SAT)

SAT asks: given a boolean formula, is there an assignment of variables that makes it true? SAT was the first problem proved NP-complete (Cook-Levin theorem, 1971). Despite its exponential worst case, modern SAT solvers handle formulas with millions of variables.

### DPLL Algorithm

The Davis-Putnam-Logemann-Loveland (DPLL) algorithm is the foundation of modern SAT solvers. It combines backtracking with two key techniques:

1. **Unit propagation**: If a clause has only one unassigned literal, it must be true.
2. **Pure literal elimination**: If a variable appears in only one polarity, assign it to satisfy those clauses.

**Python (simplified SAT solver):**

```python
from typing import List, Set, Dict, Optional


def dpll(clauses: List[Set[int]], assignment: Dict[int, bool]) -> Optional[Dict[int, bool]]:
    """
    Simplified DPLL SAT solver.
    clauses: list of sets of literals (positive = true, negative = false)
    assignment: current variable assignments

    Worst case: O(2^n) where n = number of variables.
    In practice, much faster due to unit propagation and pruning.
    """
    # Unit propagation
    changed = True
    while changed:
        changed = False
        for clause in clauses:
            unresolved = [lit for lit in clause
                         if abs(lit) not in assignment]
            falsified = [lit for lit in clause
                        if abs(lit) in assignment and
                        assignment[abs(lit)] != (lit > 0)]

            if len(unresolved) == 0 and len(falsified) == len(clause):
                return None  # Conflict: clause is false
            if len(unresolved) == 1 and len(falsified) == len(clause) - 1:
                # Unit clause: must assign this literal
                lit = unresolved[0]
                assignment[abs(lit)] = lit > 0
                changed = True

    # Check if all clauses are satisfied
    all_satisfied = True
    for clause in clauses:
        satisfied = any(
            abs(lit) in assignment and assignment[abs(lit)] == (lit > 0)
            for lit in clause
        )
        if not satisfied:
            unresolved = [lit for lit in clause if abs(lit) not in assignment]
            if not unresolved:
                return None  # Clause is false
            all_satisfied = False

    if all_satisfied:
        return assignment

    # Choose an unassigned variable
    for clause in clauses:
        for lit in clause:
            if abs(lit) not in assignment:
                var = abs(lit)
                # Try true
                result = dpll(clauses, {**assignment, var: True})
                if result is not None:
                    return result
                # Try false
                return dpll(clauses, {**assignment, var: False})

    return assignment


if __name__ == "__main__":
    # (x1 OR x2) AND (NOT x1 OR x3) AND (NOT x2 OR NOT x3)
    clauses = [{1, 2}, {-1, 3}, {-2, -3}]
    result = dpll(clauses, {})
    if result:
        print(f"Satisfiable: {result}")
    else:
        print("Unsatisfiable")
```

### CDCL: Conflict-Driven Clause Learning

Modern SAT solvers use CDCL, which extends DPLL with:
- **Clause learning**: When a conflict is detected, analyze why and add a new clause to prevent the same conflict.
- **Non-chronological backtracking**: Jump back multiple levels when a conflict is detected, not just one.
- **Restart strategies**: Periodically restart the search with learned clauses retained.

These techniques allow CDCL solvers to solve industrial instances with millions of variables in seconds, despite the O(2^n) worst case.

---

## Practical Heuristics for Production Systems

When you cannot afford exact exponential-time solutions, use these approaches:

### Greedy Approximations

Choose the locally optimal option at each step. Fast (polynomial time) but may miss the global optimum.

**Go (Greedy Set Cover):**

```go
package main

import "fmt"

// GreedySetCover finds an approximate minimum set cover.
// Guaranteed to produce a cover within O(log n) factor of optimal.
// Time: O(n * m) where n = elements, m = number of sets.
func GreedySetCover(universe map[int]bool, sets []map[int]bool) []int {
    uncovered := make(map[int]bool)
    for k := range universe {
        uncovered[k] = true
    }
    var chosen []int

    for len(uncovered) > 0 {
        bestIdx := -1
        bestCount := 0
        for i, s := range sets {
            count := 0
            for elem := range s {
                if uncovered[elem] {
                    count++
                }
            }
            if count > bestCount {
                bestCount = count
                bestIdx = i
            }
        }
        if bestIdx == -1 {
            break
        }
        chosen = append(chosen, bestIdx)
        for elem := range sets[bestIdx] {
            delete(uncovered, elem)
        }
    }
    return chosen
}

func main() {
    universe := map[int]bool{1: true, 2: true, 3: true, 4: true, 5: true}
    sets := []map[int]bool{
        {1: true, 2: true, 3: true},
        {2: true, 4: true},
        {3: true, 4: true},
        {4: true, 5: true},
    }
    result := GreedySetCover(universe, sets)
    fmt.Printf("Chosen sets: %v\n", result)
}
```

### Simulated Annealing

A probabilistic technique that explores the solution space by accepting worse solutions with decreasing probability. Good for TSP, scheduling, and placement problems.

### Genetic Algorithms

Maintain a population of solutions, combine the best (crossover), introduce random changes (mutation), and select the fittest. Works well for optimization problems with complex constraints.

### Branch and Bound

Systematically enumerate candidates while using bounds to prune large portions of the search space. The key is having a good bounding function that quickly estimates the best possible solution in a subtree.

---

## System Design Considerations

When exponential-time problems arise in production:

1. **Bound the input size**: If n is always small (say, n <= 20), O(2^n) is fine. Many scheduling problems have bounded sizes.

2. **Time budgets**: Set a maximum computation time. Return the best solution found so far when the budget expires.

3. **Caching solutions**: Many production problems recur with similar inputs. Cache results for previously seen instances.

4. **Decomposition**: Break large problems into smaller independent subproblems. Solve each independently.

5. **Approximate**: Use polynomial-time approximation algorithms with provable guarantees (PTAS, FPTAS).

6. **Hardware acceleration**: Use GPUs, FPGAs, or distributed computing for embarrassingly parallel exponential searches.

---

## Summary

- Cryptographic security relies on exponential hardness: brute-force key search is O(2^n) where n is key length.
- NP-hard problems (scheduling, routing, allocation) are ubiquitous in production systems.
- SAT solvers demonstrate that exponential worst-case algorithms can be practical — CDCL solvers handle millions of variables.
- For production systems, use heuristics (greedy, simulated annealing, genetic algorithms) with quality guarantees.
- System design decisions (input bounds, time budgets, caching) are critical for handling exponential problems.
- The gap between theory (O(2^n) worst case) and practice (often much faster) is where engineering judgment matters most.
