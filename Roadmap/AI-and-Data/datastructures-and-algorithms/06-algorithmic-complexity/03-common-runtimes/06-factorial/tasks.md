# Factorial Time O(n!) -- Tasks

## Table of Contents

1. [Task 1: Compute Factorial Iteratively and Recursively](#task-1-compute-factorial)
2. [Task 2: Generate All Permutations](#task-2-generate-all-permutations)
3. [Task 3: Permutations with Duplicates](#task-3-permutations-with-duplicates)
4. [Task 4: Next Permutation](#task-4-next-permutation)
5. [Task 5: K-th Permutation](#task-5-k-th-permutation)
6. [Task 6: Permutation Rank](#task-6-permutation-rank)
7. [Task 7: Heap's Algorithm](#task-7-heaps-algorithm)
8. [Task 8: Brute-Force TSP](#task-8-brute-force-tsp)
9. [Task 9: TSP with Branch and Bound](#task-9-tsp-branch-and-bound)
10. [Task 10: Nearest Neighbor Heuristic](#task-10-nearest-neighbor-heuristic)
11. [Task 11: 2-Opt Improvement](#task-11-2-opt-improvement)
12. [Task 12: Brute-Force Job Scheduling](#task-12-brute-force-scheduling)
13. [Task 13: Assignment Problem (Brute Force)](#task-13-assignment-problem)
14. [Task 14: Count Derangements](#task-14-count-derangements)
15. [Task 15: Permutation Cycle Detection](#task-15-permutation-cycles)
16. [Benchmark: Factorial Growth Timing](#benchmark-factorial-growth-timing)

---

## Task 1: Compute Factorial

**Difficulty**: Easy

**Objective**: Implement factorial computation both iteratively and recursively. Handle
edge cases (n=0, large n with overflow).

### Requirements

- `factorialIterative(n)` -- loop-based, returns n!
- `factorialRecursive(n)` -- recursion-based, returns n!
- Handle n=0 (return 1) and negative input (return error/exception)
- For large n, use big integers (Go: `math/big`, Java: `BigInteger`, Python: native)

### Go

```go
package main

import (
    "fmt"
    "math/big"
)

func factorialIterative(n int) *big.Int {
    result := big.NewInt(1)
    for i := 2; i <= n; i++ {
        result.Mul(result, big.NewInt(int64(i)))
    }
    return result
}

func factorialRecursive(n int) *big.Int {
    if n <= 1 {
        return big.NewInt(1)
    }
    result := factorialRecursive(n - 1)
    return result.Mul(result, big.NewInt(int64(n)))
}

func main() {
    for _, n := range []int{0, 5, 10, 20, 50} {
        fmt.Printf("%d! = %s\n", n, factorialIterative(n).String())
    }
}
```

### Java

```java
import java.math.BigInteger;

public class Factorial {
    public static BigInteger iterative(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public static BigInteger recursive(int n) {
        if (n <= 1) return BigInteger.ONE;
        return BigInteger.valueOf(n).multiply(recursive(n - 1));
    }

    public static void main(String[] args) {
        for (int n : new int[]{0, 5, 10, 20, 50}) {
            System.out.println(n + "! = " + iterative(n));
        }
    }
}
```

### Python

```python
def factorial_iterative(n):
    result = 1
    for i in range(2, n + 1):
        result *= i
    return result

def factorial_recursive(n):
    if n <= 1:
        return 1
    return n * factorial_recursive(n - 1)

for n in [0, 5, 10, 20, 50]:
    print(f"{n}! = {factorial_iterative(n)}")
```

---

## Task 2: Generate All Permutations

**Difficulty**: Medium

**Objective**: Generate all permutations of [1, 2, ..., n] using backtracking.

### Requirements

- Return a list of all n! permutations
- Use in-place swap-based backtracking
- Verify: result has exactly n! elements, no duplicates

### Go

```go
func permute(nums []int) [][]int {
    var result [][]int
    var backtrack func(start int)
    backtrack = func(start int) {
        if start == len(nums) {
            perm := make([]int, len(nums))
            copy(perm, nums)
            result = append(result, perm)
            return
        }
        for i := start; i < len(nums); i++ {
            nums[start], nums[i] = nums[i], nums[start]
            backtrack(start + 1)
            nums[start], nums[i] = nums[i], nums[start]
        }
    }
    backtrack(0)
    return result
}
```

### Java

```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, result);
    return result;
}

private void backtrack(int[] nums, int start, List<List<Integer>> result) {
    if (start == nums.length) {
        List<Integer> perm = new ArrayList<>();
        for (int n : nums) perm.add(n);
        result.add(perm);
        return;
    }
    for (int i = start; i < nums.length; i++) {
        swap(nums, start, i);
        backtrack(nums, start + 1, result);
        swap(nums, start, i);
    }
}
```

### Python

```python
def permute(nums):
    result = []
    def backtrack(start=0):
        if start == len(nums):
            result.append(nums[:])
            return
        for i in range(start, len(nums)):
            nums[start], nums[i] = nums[i], nums[start]
            backtrack(start + 1)
            nums[start], nums[i] = nums[i], nums[start]
    backtrack()
    return result
```

---

## Task 3: Permutations with Duplicates

**Difficulty**: Medium

**Objective**: Generate all unique permutations of an array that may contain duplicates.
(LeetCode 47)

### Requirements

- Sort the array first
- Skip duplicate elements at the same recursion level
- Return only distinct permutations

### Python

```python
def permute_unique(nums):
    result = []
    nums.sort()
    used = [False] * len(nums)

    def backtrack(current):
        if len(current) == len(nums):
            result.append(current[:])
            return
        for i in range(len(nums)):
            if used[i]:
                continue
            if i > 0 and nums[i] == nums[i - 1] and not used[i - 1]:
                continue
            used[i] = True
            current.append(nums[i])
            backtrack(current)
            current.pop()
            used[i] = False

    backtrack([])
    return result

# Test: [1,1,2] should give 3 unique permutations
print(len(permute_unique([1, 1, 2])))  # 3
```

---

## Task 4: Next Permutation

**Difficulty**: Medium

**Objective**: Implement the next permutation algorithm. (LeetCode 31)

### Requirements

- Modify array in-place
- O(n) time, O(1) space
- Wrap around from last to first permutation

See interview.md for full solutions in all three languages.

---

## Task 5: K-th Permutation

**Difficulty**: Medium

**Objective**: Given n and k, return the k-th permutation sequence of [1..n] without
generating all permutations. (LeetCode 60)

### Go

```go
func getPermutation(n int, k int) string {
    factorials := make([]int, n)
    factorials[0] = 1
    for i := 1; i < n; i++ {
        factorials[i] = factorials[i-1] * i
    }

    numbers := make([]int, n)
    for i := 0; i < n; i++ {
        numbers[i] = i + 1
    }

    k-- // convert to 0-indexed
    result := make([]byte, n)
    for i := 0; i < n; i++ {
        idx := k / factorials[n-1-i]
        k %= factorials[n-1-i]
        result[i] = byte('0' + numbers[idx])
        numbers = append(numbers[:idx], numbers[idx+1:]...)
    }
    return string(result)
}
```

### Java

```java
public String getPermutation(int n, int k) {
    int[] fact = new int[n];
    fact[0] = 1;
    for (int i = 1; i < n; i++) fact[i] = fact[i-1] * i;

    List<Integer> numbers = new ArrayList<>();
    for (int i = 1; i <= n; i++) numbers.add(i);

    k--;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
        int idx = k / fact[n - 1 - i];
        k %= fact[n - 1 - i];
        sb.append(numbers.get(idx));
        numbers.remove(idx);
    }
    return sb.toString();
}
```

### Python

```python
def get_permutation(n, k):
    factorials = [1] * n
    for i in range(1, n):
        factorials[i] = factorials[i - 1] * i

    numbers = list(range(1, n + 1))
    k -= 1  # 0-indexed
    result = []
    for i in range(n):
        idx = k // factorials[n - 1 - i]
        k %= factorials[n - 1 - i]
        result.append(str(numbers[idx]))
        numbers.pop(idx)
    return "".join(result)

# Test
print(get_permutation(4, 9))  # "2314"
```

---

## Task 6: Permutation Rank

**Difficulty**: Medium

**Objective**: Given a permutation, find its 1-based lexicographic rank.

See interview.md for full solutions.

---

## Task 7: Heap's Algorithm

**Difficulty**: Medium

**Objective**: Implement Heap's algorithm for generating permutations with minimal swaps.

### Requirements

- Each consecutive pair of permutations differs by exactly one swap
- Total of n! - 1 swaps
- Implement both recursive and iterative versions

### Python (Iterative)

```python
def heaps_iterative(a):
    n = len(a)
    result = [a[:]]
    c = [0] * n
    i = 0
    while i < n:
        if c[i] < i:
            if i % 2 == 0:
                a[0], a[i] = a[i], a[0]
            else:
                a[c[i]], a[i] = a[i], a[c[i]]
            result.append(a[:])
            c[i] += 1
            i = 0
        else:
            c[i] = 0
            i += 1
    return result

perms = heaps_iterative([1, 2, 3])
print(f"Count: {len(perms)}")  # 6
```

---

## Task 8: Brute-Force TSP

**Difficulty**: Medium

**Objective**: Solve TSP by trying all (n-1)! permutations of cities.

### Requirements

- Fix starting city to avoid counting rotations
- Return minimum cost and optimal route
- Test with n = 4, 6, 8, 10

### Python

```python
from itertools import permutations

def tsp_brute(dist):
    n = len(dist)
    best_cost = float("inf")
    best_route = None
    for perm in permutations(range(1, n)):
        cost = dist[0][perm[0]]
        for i in range(len(perm) - 1):
            cost += dist[perm[i]][perm[i + 1]]
        cost += dist[perm[-1]][0]
        if cost < best_cost:
            best_cost = cost
            best_route = (0,) + perm
    return best_cost, best_route
```

---

## Task 9: TSP with Branch and Bound

**Difficulty**: Hard

**Objective**: Implement branch and bound for TSP. Compare nodes visited vs brute force.

### Requirements

- Lower bound: sum of minimum outgoing edges for unvisited cities
- Track and report number of nodes explored
- Compare with brute-force node count

See middle.md for full implementations.

---

## Task 10: Nearest Neighbor Heuristic

**Difficulty**: Easy

**Objective**: Implement the nearest neighbor heuristic for TSP.

### Requirements

- Start from city 0
- Greedily visit the nearest unvisited city
- Report solution quality vs optimal (for small instances)

See senior.md for full implementations.

---

## Task 11: 2-Opt Improvement

**Difficulty**: Medium

**Objective**: Implement 2-opt local search to improve a TSP tour.

### Requirements

- Take an initial tour and improve it
- Keep improving until no 2-opt move reduces cost
- Track number of improvements made

See senior.md for full implementations.

---

## Task 12: Brute-Force Job Scheduling

**Difficulty**: Medium

**Objective**: Find the optimal order of n jobs to minimize total weighted completion
time by trying all n! orderings.

### Python

```python
from itertools import permutations

def brute_force_schedule(jobs):
    """jobs[i] = (processing_time, weight)"""
    n = len(jobs)
    best_cost = float("inf")
    best_order = None
    for perm in permutations(range(n)):
        time = 0
        cost = 0
        for idx in perm:
            time += jobs[idx][0]
            cost += time * jobs[idx][1]
        if cost < best_cost:
            best_cost = cost
            best_order = perm
    return best_cost, best_order

# Test
jobs = [(3, 4), (1, 3), (2, 5), (4, 2), (1, 1)]
cost, order = brute_force_schedule(jobs)
print(f"Optimal cost: {cost}, order: {order}")
```

**Note**: The optimal greedy solution is to sort by processing_time/weight ratio
(Smith's rule), which runs in O(n log n). Verify your brute-force answer matches.

---

## Task 13: Assignment Problem (Brute Force)

**Difficulty**: Medium

**Objective**: Given an n x n cost matrix, find the assignment of n workers to n tasks
minimizing total cost. Try all n! assignments.

### Go

```go
func assignmentBruteForce(cost [][]int) (int, []int) {
    n := len(cost)
    workers := make([]int, n)
    for i := range workers {
        workers[i] = i
    }

    bestCost := int(^uint(0) >> 1)
    var bestAssignment []int

    var tryAll func(start int)
    tryAll = func(start int) {
        if start == n {
            total := 0
            for i := 0; i < n; i++ {
                total += cost[i][workers[i]]
            }
            if total < bestCost {
                bestCost = total
                bestAssignment = make([]int, n)
                copy(bestAssignment, workers)
            }
            return
        }
        for i := start; i < n; i++ {
            workers[start], workers[i] = workers[i], workers[start]
            tryAll(start + 1)
            workers[start], workers[i] = workers[i], workers[start]
        }
    }

    tryAll(0)
    return bestCost, bestAssignment
}
```

---

## Task 14: Count Derangements

**Difficulty**: Medium

**Objective**: A derangement is a permutation where no element appears in its original
position. Count the number of derangements of n elements.

### Formula

```
D(n) = (n-1) * (D(n-1) + D(n-2))
D(0) = 1, D(1) = 0
```

Also: D(n) = n! * sum_{i=0}^{n} (-1)^i / i!

### Python

```python
def count_derangements(n):
    if n == 0:
        return 1
    if n == 1:
        return 0
    dp = [0] * (n + 1)
    dp[0] = 1
    dp[1] = 0
    for i in range(2, n + 1):
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2])
    return dp[n]

# Verify: ratio D(n)/n! approaches 1/e
import math
for n in range(1, 15):
    d = count_derangements(n)
    ratio = d / math.factorial(n)
    print(f"D({n:2d}) = {d:>12d}, D(n)/n! = {ratio:.6f}, 1/e = {1/math.e:.6f}")
```

---

## Task 15: Permutation Cycle Detection

**Difficulty**: Medium

**Objective**: Given a permutation, decompose it into disjoint cycles.

### Python

```python
def find_cycles(perm):
    """perm is 0-indexed: perm[i] is where element i goes."""
    n = len(perm)
    visited = [False] * n
    cycles = []
    for i in range(n):
        if not visited[i]:
            cycle = []
            j = i
            while not visited[j]:
                visited[j] = True
                cycle.append(j)
                j = perm[j]
            if len(cycle) > 1:
                cycles.append(cycle)
    return cycles

# Test: perm = [1, 2, 0, 4, 3] -> cycles: (0 1 2) and (3 4)
print(find_cycles([1, 2, 0, 4, 3]))
```

**Relevance**: The number of permutations of n elements with a given cycle structure
involves factorials and is central to combinatorics.

---

## Benchmark: Factorial Growth Timing

**Objective**: Measure the actual execution time of permutation generation for
increasing n, verifying O(n!) growth.

### Python

```python
import time
import math
import sys
sys.setrecursionlimit(1000000)


def permute_count(nums, start=0):
    """Count permutations without storing them."""
    if start == len(nums):
        return 1
    count = 0
    for i in range(start, len(nums)):
        nums[start], nums[i] = nums[i], nums[start]
        count += permute_count(nums, start + 1)
        nums[start], nums[i] = nums[i], nums[start]
    return count


print(f"{'n':>3} {'n!':>15} {'Time (s)':>12} {'Ratio':>10}")
print("-" * 45)

prev_time = None
for n in range(1, 13):
    nums = list(range(n))
    start = time.perf_counter()
    count = permute_count(nums)
    elapsed = time.perf_counter() - start
    ratio = elapsed / prev_time if prev_time and prev_time > 0 else float("nan")
    print(f"{n:3d} {math.factorial(n):15d} {elapsed:12.6f} {ratio:10.2f}")
    prev_time = elapsed
```

**Expected output**: The "Ratio" column should approach n for each row (since
n!/​(n-1)! = n), confirming O(n!) growth.

### Go Benchmark

```go
package main

import (
    "fmt"
    "time"
)

func countPermutations(nums []int, start int) int {
    if start == len(nums) {
        return 1
    }
    count := 0
    for i := start; i < len(nums); i++ {
        nums[start], nums[i] = nums[i], nums[start]
        count += countPermutations(nums, start+1)
        nums[start], nums[i] = nums[i], nums[start]
    }
    return count
}

func main() {
    fmt.Printf("%3s %15s %12s %10s\n", "n", "n!", "Time (s)", "Ratio")
    prevTime := 0.0
    for n := 1; n <= 13; n++ {
        nums := make([]int, n)
        for i := range nums {
            nums[i] = i
        }
        start := time.Now()
        count := countPermutations(nums, 0)
        elapsed := time.Since(start).Seconds()
        ratio := 0.0
        if prevTime > 0 {
            ratio = elapsed / prevTime
        }
        fmt.Printf("%3d %15d %12.6f %10.2f\n", n, count, elapsed, ratio)
        prevTime = elapsed
    }
}
```

### Java Benchmark

```java
public class FactorialBenchmark {
    static int countPermutations(int[] nums, int start) {
        if (start == nums.length) return 1;
        int count = 0;
        for (int i = start; i < nums.length; i++) {
            int tmp = nums[start]; nums[start] = nums[i]; nums[i] = tmp;
            count += countPermutations(nums, start + 1);
            tmp = nums[start]; nums[start] = nums[i]; nums[i] = tmp;
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.printf("%3s %15s %12s %10s%n", "n", "n!", "Time (s)", "Ratio");
        double prevTime = 0;
        long fact = 1;
        for (int n = 1; n <= 13; n++) {
            fact *= n;
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = i;
            long start = System.nanoTime();
            int count = countPermutations(nums, 0);
            double elapsed = (System.nanoTime() - start) / 1e9;
            double ratio = prevTime > 0 ? elapsed / prevTime : 0;
            System.out.printf("%3d %15d %12.6f %10.2f%n", n, count, elapsed, ratio);
            prevTime = elapsed;
        }
    }
}
```
