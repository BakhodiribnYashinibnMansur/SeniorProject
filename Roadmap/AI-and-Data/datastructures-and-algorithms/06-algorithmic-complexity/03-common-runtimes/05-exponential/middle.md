# Exponential Time O(2^n) — Middle Level

## Table of Contents

- [Prerequisites](#prerequisites)
- [Decision Trees and Branching](#decision-trees-and-branching)
- [Backtracking: Pruning the Exponential Tree](#backtracking-pruning-the-exponential-tree)
  - [N-Queens with Pruning](#n-queens-with-pruning)
  - [Subset Sum with Pruning](#subset-sum-with-pruning)
- [Memoization: From Exponential to Polynomial](#memoization-from-exponential-to-polynomial)
  - [Fibonacci with Memoization](#fibonacci-with-memoization)
  - [Climbing Stairs](#climbing-stairs)
  - [When Memoization Works and When It Doesn't](#when-memoization-works-and-when-it-doesnt)
- [Meet-in-the-Middle: O(2^(n/2))](#meet-in-the-middle-o2n2)
- [Comparison: O(2^n) vs O(n!) vs O(n^n)](#comparison-o2n-vs-on-vs-onn)
- [Recognizing Reducible vs Inherent Exponential Problems](#recognizing-reducible-vs-inherent-exponential-problems)
- [Summary](#summary)

---

## Prerequisites

Before reading this document, make sure you are comfortable with:
- Recursive algorithms and their call trees (see [junior.md](junior.md))
- Basic understanding of O(2^n) growth
- Familiarity with dynamic programming concepts

---

## Decision Trees and Branching

Every exponential algorithm can be visualized as a **decision tree**. At each node, the algorithm makes a choice (typically binary: include/exclude, yes/no, left/right), creating two branches. The total number of leaves in this tree is 2^n.

```
Level 0 (root):          [start]                    1 node
                         /      \
Level 1:            [include]  [exclude]             2 nodes
                    /    \      /    \
Level 2:          [i]   [e]  [i]   [e]              4 nodes
                 / \   / \  / \   / \
Level 3:       ...  ...  ...  ...                    8 nodes
                                                     ...
Level n:       2^n leaf nodes                        2^n nodes
```

The branching factor (number of children per node) determines the base of the exponent:
- Branching factor 2 -> O(2^n)
- Branching factor 3 -> O(3^n)
- Branching factor k -> O(k^n)

The key strategies for fighting exponential blowup are:
1. **Pruning**: Cut branches early when you know they can't lead to valid solutions.
2. **Memoization**: Avoid recomputing the same subproblems.
3. **Meet-in-the-middle**: Split the problem and combine half-solutions.

---

## Backtracking: Pruning the Exponential Tree

Backtracking is a systematic way to explore the decision tree while **pruning** (skipping) branches that can't possibly lead to valid solutions. While the worst case is still O(2^n), pruning can dramatically reduce the actual number of nodes visited in practice.

### N-Queens with Pruning

Place n queens on an n*n chessboard such that no two queens threaten each other. Brute force would try all 2^(n^2) placements, but backtracking with constraint checking is much more efficient.

**Go:**

```go
package main

import "fmt"

// SolveNQueens returns all valid placements of n queens.
// Worst case: O(n!) but pruning makes it much faster in practice.
func SolveNQueens(n int) [][]int {
    var results [][]int
    queens := make([]int, n) // queens[row] = column
    var solve func(row int)

    solve = func(row int) {
        if row == n {
            sol := make([]int, n)
            copy(sol, queens)
            results = append(results, sol)
            return
        }
        for col := 0; col < n; col++ {
            if isValid(queens, row, col) {
                queens[row] = col
                solve(row + 1)
                // Backtrack: no need to explicitly undo since we overwrite.
            }
        }
    }

    solve(0)
    return results
}

func isValid(queens []int, row, col int) bool {
    for r := 0; r < row; r++ {
        c := queens[r]
        if c == col || c-r == col-row || c+r == col+row {
            return false // Same column or diagonal.
        }
    }
    return true
}

func main() {
    n := 8
    solutions := SolveNQueens(n)
    fmt.Printf("Found %d solutions for %d-queens\n", len(solutions), n)
}
```

**Java:**

```java
import java.util.ArrayList;
import java.util.List;

public class NQueens {

    public static List<int[]> solveNQueens(int n) {
        List<int[]> results = new ArrayList<>();
        int[] queens = new int[n];
        solve(queens, 0, n, results);
        return results;
    }

    private static void solve(int[] queens, int row, int n, List<int[]> results) {
        if (row == n) {
            results.add(queens.clone());
            return;
        }
        for (int col = 0; col < n; col++) {
            if (isValid(queens, row, col)) {
                queens[row] = col;
                solve(queens, row + 1, n, results);
            }
        }
    }

    private static boolean isValid(int[] queens, int row, int col) {
        for (int r = 0; r < row; r++) {
            int c = queens[r];
            if (c == col || c - r == col - row || c + r == col + row) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int n = 8;
        List<int[]> solutions = solveNQueens(n);
        System.out.printf("Found %d solutions for %d-queens%n", solutions.size(), n);
    }
}
```

**Python:**

```python
from typing import List


def solve_n_queens(n: int) -> List[List[int]]:
    results = []
    queens = [0] * n

    def is_valid(row: int, col: int) -> bool:
        for r in range(row):
            c = queens[r]
            if c == col or c - r == col - row or c + r == col + row:
                return False
        return True

    def solve(row: int) -> None:
        if row == n:
            results.append(queens[:])
            return
        for col in range(n):
            if is_valid(row, col):
                queens[row] = col
                solve(row + 1)

    solve(0)
    return results


if __name__ == "__main__":
    n = 8
    solutions = solve_n_queens(n)
    print(f"Found {len(solutions)} solutions for {n}-queens")
```

### Subset Sum with Pruning

We can improve brute-force subset sum by sorting the array and pruning when the running sum exceeds the target (for positive numbers).

**Go:**

```go
package main

import (
    "fmt"
    "sort"
)

// SubsetSumPruned uses backtracking with pruning.
// Prunes branches where remaining sum can't reach the target.
func SubsetSumPruned(nums []int, target int) bool {
    sort.Ints(nums)
    return backtrack(nums, 0, target)
}

func backtrack(nums []int, index, remaining int) bool {
    if remaining == 0 {
        return true
    }
    if remaining < 0 || index >= len(nums) {
        return false // Prune: overshot or out of elements.
    }
    // Prune: if smallest remaining element is larger than remaining sum
    if nums[index] > remaining {
        return false
    }
    // Include nums[index]
    if backtrack(nums, index+1, remaining-nums[index]) {
        return true
    }
    // Exclude nums[index]
    return backtrack(nums, index+1, remaining)
}

func main() {
    nums := []int{2, 3, 7, 8, 10}
    target := 11
    fmt.Printf("Subset sum of %v = %d? %v\n", nums, target, SubsetSumPruned(nums, target))
}
```

**Java:**

```java
import java.util.Arrays;

public class SubsetSumPruned {

    public static boolean subsetSum(int[] nums, int target) {
        Arrays.sort(nums);
        return backtrack(nums, 0, target);
    }

    private static boolean backtrack(int[] nums, int index, int remaining) {
        if (remaining == 0) return true;
        if (remaining < 0 || index >= nums.length) return false;
        if (nums[index] > remaining) return false; // Prune

        // Include or exclude
        return backtrack(nums, index + 1, remaining - nums[index])
            || backtrack(nums, index + 1, remaining);
    }

    public static void main(String[] args) {
        int[] nums = {2, 3, 7, 8, 10};
        int target = 11;
        System.out.printf("Subset sum = %d? %b%n", target, subsetSum(nums, target));
    }
}
```

**Python:**

```python
from typing import List


def subset_sum_pruned(nums: List[int], target: int) -> bool:
    nums.sort()

    def backtrack(index: int, remaining: int) -> bool:
        if remaining == 0:
            return True
        if remaining < 0 or index >= len(nums):
            return False
        if nums[index] > remaining:
            return False  # Prune
        # Include or exclude
        return (backtrack(index + 1, remaining - nums[index])
                or backtrack(index + 1, remaining))

    return backtrack(0, target)


if __name__ == "__main__":
    nums = [2, 3, 7, 8, 10]
    target = 11
    print(f"Subset sum = {target}? {subset_sum_pruned(nums, target)}")
```

**Pruning effectiveness:** For random inputs, pruning can reduce the search space by orders of magnitude. However, the worst case remains O(2^n) — adversarial inputs can force the algorithm to explore nearly all branches.

---

## Memoization: From Exponential to Polynomial

Memoization stores the results of subproblems so they are computed only once. This transforms many O(2^n) algorithms into O(n) or O(n * target) algorithms.

### Fibonacci with Memoization

**Go:**

```go
package main

import "fmt"

// FibMemo computes Fibonacci with memoization.
// Time Complexity: O(n) — each subproblem computed once.
// Space Complexity: O(n) — memo table + recursion stack.
func FibMemo(n int) int {
    memo := make(map[int]int)
    var fib func(int) int
    fib = func(k int) int {
        if k <= 1 {
            return k
        }
        if val, ok := memo[k]; ok {
            return val
        }
        memo[k] = fib(k-1) + fib(k-2)
        return memo[k]
    }
    return fib(n)
}

func main() {
    // Now n=50 is instant instead of taking hours!
    fmt.Println(FibMemo(50))  // 12586269025
    fmt.Println(FibMemo(100)) // 354224848179261915075 (overflows int64, use big.Int)
}
```

**Java:**

```java
import java.util.HashMap;
import java.util.Map;

public class FibMemo {

    private static Map<Integer, Long> memo = new HashMap<>();

    // Time Complexity: O(n)
    // Space Complexity: O(n)
    public static long fib(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long result = fib(n - 1) + fib(n - 2);
        memo.put(n, result);
        return result;
    }

    public static void main(String[] args) {
        System.out.println(fib(50));  // 12586269025
    }
}
```

**Python:**

```python
from functools import lru_cache


@lru_cache(maxsize=None)
def fib(n: int) -> int:
    """
    Fibonacci with memoization.
    Time Complexity: O(n)
    Space Complexity: O(n)
    """
    if n <= 1:
        return n
    return fib(n - 1) + fib(n - 2)


if __name__ == "__main__":
    print(fib(50))   # 12586269025
    print(fib(100))  # 354224848179261915075
```

### Climbing Stairs

You can climb 1 or 2 stairs at a time. How many ways to reach stair n?

**Go:**

```go
package main

import "fmt"

// ClimbStairs counts ways to climb n stairs (1 or 2 steps).
// Without memo: O(2^n). With memo: O(n).
func ClimbStairs(n int) int {
    memo := make([]int, n+1)
    memo[0] = 1
    if n >= 1 {
        memo[1] = 1
    }
    for i := 2; i <= n; i++ {
        memo[i] = memo[i-1] + memo[i-2]
    }
    return memo[n]
}

func main() {
    for n := 1; n <= 10; n++ {
        fmt.Printf("Stairs(%d) = %d\n", n, ClimbStairs(n))
    }
}
```

**Java:**

```java
public class ClimbStairs {

    public static int climbStairs(int n) {
        if (n <= 1) return 1;
        int[] memo = new int[n + 1];
        memo[0] = 1;
        memo[1] = 1;
        for (int i = 2; i <= n; i++) {
            memo[i] = memo[i - 1] + memo[i - 2];
        }
        return memo[n];
    }

    public static void main(String[] args) {
        for (int n = 1; n <= 10; n++) {
            System.out.printf("Stairs(%d) = %d%n", n, climbStairs(n));
        }
    }
}
```

**Python:**

```python
def climb_stairs(n: int) -> int:
    """Without memo: O(2^n). With DP: O(n)."""
    if n <= 1:
        return 1
    memo = [0] * (n + 1)
    memo[0] = memo[1] = 1
    for i in range(2, n + 1):
        memo[i] = memo[i - 1] + memo[i - 2]
    return memo[n]


if __name__ == "__main__":
    for n in range(1, 11):
        print(f"Stairs({n}) = {climb_stairs(n)}")
```

### When Memoization Works and When It Doesn't

Memoization works when the problem has **overlapping subproblems** — the same subproblem is solved multiple times. It requires:
1. A finite set of distinct subproblems.
2. Subproblems that can be identified by a compact key (e.g., an integer index).

Memoization does NOT help when:
- Subproblems are all unique (e.g., generating the power set — each subset is different).
- The state space itself is exponential (e.g., the key for memoization requires O(2^n) distinct keys).
- The problem output is inherently exponential in size.

| Problem | Overlapping Subproblems? | Memoization Helps? |
|---------|--------------------------|-------------------|
| Fibonacci | Yes | O(2^n) -> O(n) |
| Subset Sum | Yes (with bounded target) | O(2^n) -> O(n * target) |
| Power Set Generation | No (all subsets unique) | No — output is O(2^n) |
| Tower of Hanoi | No (all moves unique) | No — 2^n - 1 moves required |
| Traveling Salesman | Yes (with bitmask DP) | O(n! ) -> O(2^n * n) |

---

## Meet-in-the-Middle: O(2^(n/2))

Meet-in-the-middle is a powerful technique that splits the input into two halves, solves each half independently in O(2^(n/2)), and then combines the results. This reduces O(2^n) to O(2^(n/2)), which is a massive improvement.

For n=40: O(2^40) = ~10^12, but O(2^20) = ~10^6. That is a million-fold speedup.

**Application: Subset Sum with Meet-in-the-Middle**

**Go:**

```go
package main

import (
    "fmt"
    "sort"
)

// SubsetSumMITM solves subset sum using meet-in-the-middle.
// Time Complexity: O(2^(n/2) * log(2^(n/2))) = O(2^(n/2) * n)
// Space Complexity: O(2^(n/2))
func SubsetSumMITM(nums []int, target int) bool {
    n := len(nums)
    mid := n / 2

    // Generate all subset sums for the first half.
    leftSums := allSubsetSums(nums[:mid])
    sort.Ints(leftSums)

    // For each subset sum in the second half, binary search in the first half.
    rightSums := allSubsetSums(nums[mid:])
    for _, rs := range rightSums {
        need := target - rs
        idx := sort.SearchInts(leftSums, need)
        if idx < len(leftSums) && leftSums[idx] == need {
            return true
        }
    }
    return false
}

func allSubsetSums(nums []int) []int {
    n := len(nums)
    sums := make([]int, 0, 1<<n)
    for mask := 0; mask < (1 << n); mask++ {
        s := 0
        for i := 0; i < n; i++ {
            if mask&(1<<i) != 0 {
                s += nums[i]
            }
        }
        sums = append(sums, s)
    }
    return sums
}

func main() {
    nums := []int{1, 3, 5, 7, 9, 11, 13, 15, 17, 19,
                  2, 4, 6, 8, 10, 12, 14, 16, 18, 20}
    target := 100
    fmt.Printf("Subset sum = %d? %v\n", target, SubsetSumMITM(nums, target))
}
```

**Java:**

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubsetSumMITM {

    // Time Complexity: O(2^(n/2) * n)
    // Space Complexity: O(2^(n/2))
    public static boolean subsetSum(int[] nums, int target) {
        int n = nums.length;
        int mid = n / 2;

        List<Integer> leftSums = allSubsetSums(nums, 0, mid);
        Collections.sort(leftSums);

        List<Integer> rightSums = allSubsetSums(nums, mid, n);
        for (int rs : rightSums) {
            int need = target - rs;
            int idx = Collections.binarySearch(leftSums, need);
            if (idx >= 0) return true;
        }
        return false;
    }

    private static List<Integer> allSubsetSums(int[] nums, int from, int to) {
        int len = to - from;
        List<Integer> sums = new ArrayList<>(1 << len);
        for (int mask = 0; mask < (1 << len); mask++) {
            int s = 0;
            for (int i = 0; i < len; i++) {
                if ((mask & (1 << i)) != 0) {
                    s += nums[from + i];
                }
            }
            sums.add(s);
        }
        return sums;
    }

    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19,
                      2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
        int target = 100;
        System.out.printf("Subset sum = %d? %b%n", target, subsetSum(nums, target));
    }
}
```

**Python:**

```python
from typing import List
from bisect import bisect_left


def subset_sum_mitm(nums: List[int], target: int) -> bool:
    """
    Meet-in-the-middle subset sum.
    Time Complexity: O(2^(n/2) * n)
    Space Complexity: O(2^(n/2))
    """
    n = len(nums)
    mid = n // 2

    def all_subset_sums(arr: List[int]) -> List[int]:
        sums = []
        for mask in range(1 << len(arr)):
            s = sum(arr[i] for i in range(len(arr)) if mask & (1 << i))
            sums.append(s)
        return sums

    left_sums = sorted(all_subset_sums(nums[:mid]))
    right_sums = all_subset_sums(nums[mid:])

    for rs in right_sums:
        need = target - rs
        idx = bisect_left(left_sums, need)
        if idx < len(left_sums) and left_sums[idx] == need:
            return True
    return False


if __name__ == "__main__":
    nums = list(range(1, 21))
    target = 100
    print(f"Subset sum = {target}? {subset_sum_mitm(nums, target)}")
```

**Comparison for n=40:**

| Approach | Time | Feasible? |
|----------|------|-----------|
| Brute force O(2^40) | ~10^12 ops | Takes minutes to hours |
| Meet-in-the-middle O(2^20) | ~10^6 ops | Milliseconds |

---

## Comparison: O(2^n) vs O(n!) vs O(n^n)

All three are super-polynomial, but they grow at very different rates.

| n  | 2^n          | n!               | n^n                |
|----|-------------|------------------|---------------------|
| 5  | 32          | 120              | 3,125               |
| 8  | 256         | 40,320           | 16,777,216          |
| 10 | 1,024       | 3,628,800        | 10,000,000,000      |
| 12 | 4,096       | 479,001,600      | 8.9 * 10^12         |
| 15 | 32,768      | 1.31 * 10^12     | 4.4 * 10^17         |
| 20 | 1,048,576   | 2.43 * 10^18     | 1.05 * 10^26        |

**Hierarchy:** For large n: O(2^n) << O(n!) << O(n^n)

Using Stirling's approximation: n! ~ sqrt(2*pi*n) * (n/e)^n, so n! is roughly (n/e)^n which grows much faster than 2^n.

**When each arises:**
- **O(2^n):** Subset problems (include/exclude each element).
- **O(n!):** Permutation problems (arrange n elements in all possible orders).
- **O(n^n):** Assigning n items to n slots with replacement.

---

## Recognizing Reducible vs Inherent Exponential Problems

A critical skill is distinguishing between problems that **look** exponential but can be reduced, and those that are **inherently** exponential.

### Reducible (Can be optimized)
- Fibonacci: O(2^n) -> O(n) with memoization
- Subset sum (bounded target): O(2^n) -> O(n * target) with DP
- Longest common subsequence: O(2^n) -> O(n * m) with DP
- Coin change: O(2^n) -> O(n * amount) with DP
- Edit distance: O(3^n) -> O(n * m) with DP

### Inherently exponential
- Generating all subsets (output is 2^n)
- Tower of Hanoi (provably 2^n - 1 moves)
- Satisfying a general boolean formula (SAT — NP-complete, no known polynomial solution)
- Traveling Salesman (exact solution — NP-hard)

### The test: Ask yourself
1. Does the output itself have exponential size? -> Inherently exponential.
2. Are there overlapping subproblems with polynomial state space? -> Likely reducible.
3. Is the problem NP-hard? -> Probably inherently exponential (no proof exists either way).

---

## Summary

| Technique | Reduction | When to Use |
|-----------|-----------|-------------|
| Pruning/Backtracking | Same worst case, better average | Constraint satisfaction, search problems |
| Memoization/DP | O(2^n) -> polynomial | Overlapping subproblems with small state space |
| Meet-in-the-Middle | O(2^n) -> O(2^(n/2)) | Independent halves, combinable solutions |
| Iterative DP | O(2^n) -> O(n * W) | Problems with bounded parameters |

Key takeaways:
- Not all exponential algorithms are created equal — many can be optimized.
- Memoization is the most powerful tool: it turns many O(2^n) problems into polynomial time.
- Meet-in-the-middle gives a quadratic improvement in the exponent.
- Backtracking with pruning doesn't change worst-case complexity but often works well in practice.
- Understanding whether a problem is inherently exponential or reducible is a core algorithmic skill.
