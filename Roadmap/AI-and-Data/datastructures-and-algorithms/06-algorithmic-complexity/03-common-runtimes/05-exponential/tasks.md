# Exponential Time O(2^n) — Tasks

## Table of Contents

- [Task 1: Recursive Fibonacci Counter](#task-1-recursive-fibonacci-counter)
- [Task 2: Power Set via Bitmask](#task-2-power-set-via-bitmask)
- [Task 3: Subset Sum (Brute Force)](#task-3-subset-sum-brute-force)
- [Task 4: Subset Sum with Memoization](#task-4-subset-sum-with-memoization)
- [Task 5: Generate All Permutations from Subsets](#task-5-generate-all-permutations-from-subsets)
- [Task 6: Tower of Hanoi with Move Counter](#task-6-tower-of-hanoi-with-move-counter)
- [Task 7: N-Queens Solver](#task-7-n-queens-solver)
- [Task 8: Meet-in-the-Middle Subset Sum](#task-8-meet-in-the-middle-subset-sum)
- [Task 9: Letter Combinations of a Phone Number](#task-9-letter-combinations-of-a-phone-number)
- [Task 10: Target Sum with +/- Assignments](#task-10-target-sum-with---assignments)
- [Task 11: Count Unique Binary Search Trees](#task-11-count-unique-binary-search-trees)
- [Task 12: Partition Equal Subset Sum](#task-12-partition-equal-subset-sum)
- [Task 13: Word Break (Exponential to DP)](#task-13-word-break-exponential-to-dp)
- [Task 14: Combination Sum](#task-14-combination-sum)
- [Task 15: Maximum Weight Independent Set](#task-15-maximum-weight-independent-set)
- [Benchmark: Fibonacci Exponential vs Linear](#benchmark-fibonacci-exponential-vs-linear)

---

## Task 1: Recursive Fibonacci Counter

**Objective:** Implement recursive Fibonacci and count the total number of function calls for a given n.

**Requirements:**
- Return both the Fibonacci value and the call count.
- Test for n = 5, 10, 15, 20, 25, 30.
- Observe that call count approximately follows 2^n.

**Go:**

```go
package main

import "fmt"

func fibCount(n int, count *int) int {
    *count++
    if n <= 1 {
        return n
    }
    return fibCount(n-1, count) + fibCount(n-2, count)
}

func main() {
    for _, n := range []int{5, 10, 15, 20, 25, 30} {
        count := 0
        result := fibCount(n, &count)
        fmt.Printf("Fib(%2d) = %10d | Calls: %12d\n", n, result, count)
    }
}
```

**Java:**

```java
public class Task1 {
    static long callCount;

    static int fib(int n) {
        callCount++;
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        for (int n : new int[]{5, 10, 15, 20, 25, 30}) {
            callCount = 0;
            int result = fib(n);
            System.out.printf("Fib(%2d) = %10d | Calls: %12d%n", n, result, callCount);
        }
    }
}
```

**Python:**

```python
def fib_count(n: int, counter: list) -> int:
    counter[0] += 1
    if n <= 1:
        return n
    return fib_count(n - 1, counter) + fib_count(n - 2, counter)

for n in [5, 10, 15, 20, 25, 30]:
    counter = [0]
    result = fib_count(n, counter)
    print(f"Fib({n:2d}) = {result:10d} | Calls: {counter[0]:12d}")
```

---

## Task 2: Power Set via Bitmask

**Objective:** Generate all subsets of a given set using bitmask enumeration. Print each subset.

**Requirements:**
- Input: a list of distinct integers.
- Output: all 2^n subsets.
- Verify that the output count equals 2^n.

**Go:**

```go
package main

import "fmt"

func powerSet(nums []int) [][]int {
    n := len(nums)
    result := make([][]int, 0, 1<<n)
    for mask := 0; mask < (1 << n); mask++ {
        var subset []int
        for i := 0; i < n; i++ {
            if mask&(1<<i) != 0 {
                subset = append(subset, nums[i])
            }
        }
        result = append(result, subset)
    }
    return result
}

func main() {
    nums := []int{1, 2, 3, 4}
    ps := powerSet(nums)
    fmt.Printf("Total subsets: %d (expected: %d)\n", len(ps), 1<<len(nums))
}
```

**Java:**

```java
import java.util.*;

public class Task2 {
    public static List<List<Integer>> powerSet(int[] nums) {
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) subset.add(nums[i]);
            }
            result.add(subset);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        var ps = powerSet(nums);
        System.out.printf("Total subsets: %d (expected: %d)%n", ps.size(), 1 << nums.length);
    }
}
```

**Python:**

```python
def power_set(nums):
    n = len(nums)
    result = []
    for mask in range(1 << n):
        subset = [nums[i] for i in range(n) if mask & (1 << i)]
        result.append(subset)
    return result

nums = [1, 2, 3, 4]
ps = power_set(nums)
print(f"Total subsets: {len(ps)} (expected: {1 << len(nums)})")
```

---

## Task 3: Subset Sum (Brute Force)

**Objective:** Determine if any subset of the given integers sums to a target value. Use brute-force O(2^n) enumeration.

**Requirements:**
- Return True/False and the actual subset if found.
- Test with arrays of size 10, 15, 20.

**Go:**

```go
package main

import "fmt"

func subsetSum(nums []int, target int) (bool, []int) {
    n := len(nums)
    for mask := 1; mask < (1 << n); mask++ {
        sum := 0
        var subset []int
        for i := 0; i < n; i++ {
            if mask&(1<<i) != 0 {
                sum += nums[i]
                subset = append(subset, nums[i])
            }
        }
        if sum == target {
            return true, subset
        }
    }
    return false, nil
}

func main() {
    nums := []int{3, 7, 1, 8, -2, 5, 11, 4, 6, 9}
    target := 20
    found, subset := subsetSum(nums, target)
    fmt.Printf("Target %d: found=%v subset=%v\n", target, found, subset)
}
```

**Java:**

```java
import java.util.*;

public class Task3 {
    public static void main(String[] args) {
        int[] nums = {3, 7, 1, 8, -2, 5, 11, 4, 6, 9};
        int target = 20;
        int n = nums.length;
        for (int mask = 1; mask < (1 << n); mask++) {
            int sum = 0;
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    sum += nums[i];
                    subset.add(nums[i]);
                }
            }
            if (sum == target) {
                System.out.printf("Found: %s%n", subset);
                return;
            }
        }
        System.out.println("No subset found");
    }
}
```

**Python:**

```python
def subset_sum(nums, target):
    n = len(nums)
    for mask in range(1, 1 << n):
        subset = [nums[i] for i in range(n) if mask & (1 << i)]
        if sum(subset) == target:
            return True, subset
    return False, []

nums = [3, 7, 1, 8, -2, 5, 11, 4, 6, 9]
found, subset = subset_sum(nums, 20)
print(f"Found: {found}, Subset: {subset}")
```

---

## Task 4: Subset Sum with Memoization

**Objective:** Solve subset sum using dynamic programming, reducing from O(2^n) to O(n * target).

**Requirements:**
- Implement bottom-up DP approach.
- Compare execution time with brute force on the same input.

**Go:**

```go
package main

import "fmt"

func subsetSumDP(nums []int, target int) bool {
    dp := make([]bool, target+1)
    dp[0] = true
    for _, num := range nums {
        for j := target; j >= num; j-- {
            if dp[j-num] {
                dp[j] = true
            }
        }
    }
    return dp[target]
}

func main() {
    nums := []int{3, 7, 1, 8, 2, 5, 11, 4, 6, 9}
    target := 20
    fmt.Printf("DP result: %v\n", subsetSumDP(nums, target))
}
```

**Java:**

```java
public class Task4 {
    public static boolean subsetSumDP(int[] nums, int target) {
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : nums) {
            for (int j = target; j >= num; j--) {
                if (dp[j - num]) dp[j] = true;
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        int[] nums = {3, 7, 1, 8, 2, 5, 11, 4, 6, 9};
        System.out.printf("DP result: %b%n", subsetSumDP(nums, 20));
    }
}
```

**Python:**

```python
def subset_sum_dp(nums, target):
    dp = [False] * (target + 1)
    dp[0] = True
    for num in nums:
        for j in range(target, num - 1, -1):
            if dp[j - num]:
                dp[j] = True
    return dp[target]

print(subset_sum_dp([3, 7, 1, 8, 2, 5, 11, 4, 6, 9], 20))
```

---

## Task 5: Generate All Permutations from Subsets

**Objective:** Given n elements, generate all subsets and for each subset generate all permutations. This is O(2^n * n!) in the worst case.

**Requirements:**
- Small n only (n <= 6).
- Count total outputs.

**Python:**

```python
from itertools import permutations

def all_subset_permutations(nums):
    results = []
    n = len(nums)
    for mask in range(1 << n):
        subset = [nums[i] for i in range(n) if mask & (1 << i)]
        for perm in permutations(subset):
            results.append(perm)
    return results

nums = [1, 2, 3, 4]
result = all_subset_permutations(nums)
print(f"Total: {len(result)}")
```

---

## Task 6: Tower of Hanoi with Move Counter

**Objective:** Implement Tower of Hanoi and verify the move count equals 2^n - 1.

**Go:**

```go
package main

import "fmt"

func hanoi(n int, from, to, aux string) int {
    if n == 0 {
        return 0
    }
    moves := hanoi(n-1, from, aux, to)
    moves++ // Move disk n
    moves += hanoi(n-1, aux, to, from)
    return moves
}

func main() {
    for n := 1; n <= 20; n++ {
        moves := hanoi(n, "A", "C", "B")
        expected := (1 << n) - 1
        fmt.Printf("n=%2d | moves=%10d | expected=%10d | match=%v\n",
            n, moves, expected, moves == expected)
    }
}
```

**Java:**

```java
public class Task6 {
    static int hanoi(int n, String from, String to, String aux) {
        if (n == 0) return 0;
        return hanoi(n - 1, from, aux, to) + 1 + hanoi(n - 1, aux, to, from);
    }

    public static void main(String[] args) {
        for (int n = 1; n <= 20; n++) {
            int moves = hanoi(n, "A", "C", "B");
            int expected = (1 << n) - 1;
            System.out.printf("n=%2d | moves=%10d | match=%b%n", n, moves, moves == expected);
        }
    }
}
```

**Python:**

```python
def hanoi(n, src="A", dst="C", aux="B"):
    if n == 0:
        return 0
    return hanoi(n - 1, src, aux, dst) + 1 + hanoi(n - 1, aux, dst, src)

for n in range(1, 21):
    moves = hanoi(n)
    expected = (1 << n) - 1
    print(f"n={n:2d} | moves={moves:10d} | match={moves == expected}")
```

---

## Task 7: N-Queens Solver

**Objective:** Find all valid placements for the N-Queens problem. Count solutions for n = 4 through 12.

**Python:**

```python
def solve_n_queens(n):
    count = 0
    queens = [0] * n

    def is_valid(row, col):
        for r in range(row):
            c = queens[r]
            if c == col or abs(c - col) == abs(r - row):
                return False
        return True

    def solve(row):
        nonlocal count
        if row == n:
            count += 1
            return
        for col in range(n):
            if is_valid(row, col):
                queens[row] = col
                solve(row + 1)

    solve(0)
    return count

for n in range(4, 13):
    print(f"{n}-Queens: {solve_n_queens(n)} solutions")
```

---

## Task 8: Meet-in-the-Middle Subset Sum

**Objective:** Implement meet-in-the-middle for subset sum and compare performance with brute force for n=30.

**Python:**

```python
import time
from bisect import bisect_left

def subset_sums(arr):
    n = len(arr)
    sums = []
    for mask in range(1 << n):
        s = sum(arr[i] for i in range(n) if mask & (1 << i))
        sums.append(s)
    return sums

def mitm_subset_sum(nums, target):
    mid = len(nums) // 2
    left = sorted(subset_sums(nums[:mid]))
    for rs in subset_sums(nums[mid:]):
        need = target - rs
        idx = bisect_left(left, need)
        if idx < len(left) and left[idx] == need:
            return True
    return False

nums = list(range(1, 31))
target = 234

start = time.perf_counter()
result = mitm_subset_sum(nums, target)
elapsed = time.perf_counter() - start
print(f"MITM result: {result} in {elapsed:.4f}s")
```

---

## Task 9: Letter Combinations of a Phone Number

**Objective:** Given a string of digits (2-9), return all possible letter combinations.

**Go:**

```go
package main

import "fmt"

var phoneMap = map[byte]string{
    '2': "abc", '3': "def", '4': "ghi", '5': "jkl",
    '6': "mno", '7': "pqrs", '8': "tuv", '9': "wxyz",
}

func letterCombinations(digits string) []string {
    if len(digits) == 0 {
        return nil
    }
    var result []string
    var backtrack func(idx int, current []byte)
    backtrack = func(idx int, current []byte) {
        if idx == len(digits) {
            result = append(result, string(current))
            return
        }
        for _, ch := range phoneMap[digits[idx]] {
            backtrack(idx+1, append(current, byte(ch)))
        }
    }
    backtrack(0, nil)
    return result
}

func main() {
    fmt.Println(letterCombinations("23"))
}
```

**Java:**

```java
import java.util.*;

public class Task9 {
    static Map<Character, String> phoneMap = Map.of(
        '2', "abc", '3', "def", '4', "ghi", '5', "jkl",
        '6', "mno", '7', "pqrs", '8', "tuv", '9', "wxyz");

    public static List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) return result;
        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    static void backtrack(String digits, int idx, StringBuilder sb, List<String> result) {
        if (idx == digits.length()) { result.add(sb.toString()); return; }
        for (char ch : phoneMap.get(digits.charAt(idx)).toCharArray()) {
            sb.append(ch);
            backtrack(digits, idx + 1, sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    public static void main(String[] args) {
        System.out.println(letterCombinations("23"));
    }
}
```

**Python:**

```python
PHONE = {'2': 'abc', '3': 'def', '4': 'ghi', '5': 'jkl',
         '6': 'mno', '7': 'pqrs', '8': 'tuv', '9': 'wxyz'}

def letter_combinations(digits):
    if not digits:
        return []
    result = []
    def backtrack(idx, current):
        if idx == len(digits):
            result.append(''.join(current))
            return
        for ch in PHONE[digits[idx]]:
            current.append(ch)
            backtrack(idx + 1, current)
            current.pop()
    backtrack(0, [])
    return result

print(letter_combinations("23"))
```

---

## Task 10: Target Sum with +/- Assignments

**Objective:** Given an array of integers, assign + or - to each element to reach a target sum. Count the number of ways.

**Python:**

```python
def find_target_sum_ways(nums, target):
    """Brute force: O(2^n). Can be optimized with DP."""
    count = 0
    def backtrack(idx, current_sum):
        nonlocal count
        if idx == len(nums):
            if current_sum == target:
                count += 1
            return
        backtrack(idx + 1, current_sum + nums[idx])
        backtrack(idx + 1, current_sum - nums[idx])
    backtrack(0, 0)
    return count

print(find_target_sum_ways([1, 1, 1, 1, 1], 3))  # Expected: 5
```

---

## Task 11: Count Unique Binary Search Trees

**Objective:** Count structurally unique BSTs with values 1 to n. Naive recursion is exponential; optimize with DP to O(n^2).

**Python:**

```python
def num_trees_naive(n):
    """Exponential without memoization."""
    if n <= 1:
        return 1
    total = 0
    for root in range(1, n + 1):
        left = num_trees_naive(root - 1)
        right = num_trees_naive(n - root)
        total += left * right
    return total

def num_trees_dp(n):
    """O(n^2) with DP (Catalan numbers)."""
    dp = [0] * (n + 1)
    dp[0] = dp[1] = 1
    for i in range(2, n + 1):
        for j in range(1, i + 1):
            dp[i] += dp[j - 1] * dp[i - j]
    return dp[n]

for n in range(1, 15):
    print(f"n={n:2d}: naive={num_trees_naive(n):8d} dp={num_trees_dp(n):8d}")
```

---

## Task 12: Partition Equal Subset Sum

**Objective:** Determine if an array can be partitioned into two subsets with equal sum.

**Python:**

```python
def can_partition(nums):
    total = sum(nums)
    if total % 2 != 0:
        return False
    target = total // 2
    dp = [False] * (target + 1)
    dp[0] = True
    for num in nums:
        for j in range(target, num - 1, -1):
            dp[j] = dp[j] or dp[j - num]
    return dp[target]

print(can_partition([1, 5, 11, 5]))   # True
print(can_partition([1, 2, 3, 5]))    # False
```

---

## Task 13: Word Break (Exponential to DP)

**Objective:** Given a string and a dictionary, determine if the string can be segmented into dictionary words.

**Python:**

```python
def word_break_naive(s, word_dict):
    """O(2^n) naive recursion."""
    if not s:
        return True
    for i in range(1, len(s) + 1):
        if s[:i] in word_dict and word_break_naive(s[i:], word_dict):
            return True
    return False

def word_break_dp(s, word_dict):
    """O(n^2) with DP."""
    n = len(s)
    dp = [False] * (n + 1)
    dp[0] = True
    for i in range(1, n + 1):
        for j in range(i):
            if dp[j] and s[j:i] in word_dict:
                dp[i] = True
                break
    return dp[n]

words = {"leet", "code"}
print(word_break_dp("leetcode", words))  # True
```

---

## Task 14: Combination Sum

**Objective:** Find all unique combinations of candidates that sum to a target. Each number may be used unlimited times.

**Go:**

```go
package main

import "fmt"

func combinationSum(candidates []int, target int) [][]int {
    var result [][]int
    var backtrack func(start, remaining int, current []int)
    backtrack = func(start, remaining int, current []int) {
        if remaining == 0 {
            tmp := make([]int, len(current))
            copy(tmp, current)
            result = append(result, tmp)
            return
        }
        for i := start; i < len(candidates); i++ {
            if candidates[i] > remaining {
                continue
            }
            backtrack(i, remaining-candidates[i], append(current, candidates[i]))
        }
    }
    backtrack(0, target, nil)
    return result
}

func main() {
    fmt.Println(combinationSum([]int{2, 3, 6, 7}, 7))
}
```

**Java:**

```java
import java.util.*;

public class Task14 {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    static void backtrack(int[] cands, int start, int rem, List<Integer> cur, List<List<Integer>> res) {
        if (rem == 0) { res.add(new ArrayList<>(cur)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > rem) continue;
            cur.add(cands[i]);
            backtrack(cands, i, rem - cands[i], cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    public static void main(String[] args) {
        System.out.println(combinationSum(new int[]{2, 3, 6, 7}, 7));
    }
}
```

**Python:**

```python
def combination_sum(candidates, target):
    result = []
    def backtrack(start, remaining, current):
        if remaining == 0:
            result.append(current[:])
            return
        for i in range(start, len(candidates)):
            if candidates[i] > remaining:
                continue
            current.append(candidates[i])
            backtrack(i, remaining - candidates[i], current)
            current.pop()
    backtrack(0, target, [])
    return result

print(combination_sum([2, 3, 6, 7], 7))
```

---

## Task 15: Maximum Weight Independent Set

**Objective:** Find the maximum weight independent set in a path graph (no two adjacent vertices selected).

**Python:**

```python
def max_weight_independent_set_brute(weights):
    """Brute force: O(2^n)."""
    n = len(weights)
    best = 0
    for mask in range(1 << n):
        valid = True
        total = 0
        for i in range(n):
            if mask & (1 << i):
                if i > 0 and mask & (1 << (i - 1)):
                    valid = False
                    break
                total += weights[i]
        if valid:
            best = max(best, total)
    return best

def max_weight_independent_set_dp(weights):
    """DP: O(n)."""
    n = len(weights)
    if n == 0: return 0
    if n == 1: return weights[0]
    dp = [0] * n
    dp[0] = weights[0]
    dp[1] = max(weights[0], weights[1])
    for i in range(2, n):
        dp[i] = max(dp[i - 1], dp[i - 2] + weights[i])
    return dp[-1]

weights = [1, 4, 6, 2, 8, 3, 7]
print(f"Brute: {max_weight_independent_set_brute(weights)}")
print(f"DP:    {max_weight_independent_set_dp(weights)}")
```

---

## Benchmark: Fibonacci Exponential vs Linear

**Objective:** Measure execution time for recursive vs memoized Fibonacci. Plot or print the results.

**Go:**

```go
package main

import (
    "fmt"
    "time"
)

func fibRecursive(n int) int {
    if n <= 1 {
        return n
    }
    return fibRecursive(n-1) + fibRecursive(n-2)
}

func fibDP(n int) int {
    if n <= 1 {
        return n
    }
    a, b := 0, 1
    for i := 2; i <= n; i++ {
        a, b = b, a+b
    }
    return b
}

func main() {
    fmt.Println("--- Recursive (exponential) ---")
    for _, n := range []int{10, 20, 25, 30, 35, 40} {
        start := time.Now()
        result := fibRecursive(n)
        elapsed := time.Since(start)
        fmt.Printf("Fib(%2d) = %12d | Time: %v\n", n, result, elapsed)
    }

    fmt.Println("\n--- DP (linear) ---")
    for _, n := range []int{10, 20, 25, 30, 35, 40, 50, 100} {
        start := time.Now()
        result := fibDP(n)
        elapsed := time.Since(start)
        fmt.Printf("Fib(%2d) = %20d | Time: %v\n", n, result, elapsed)
    }
}
```

**Java:**

```java
public class Benchmark {
    static int fibRecursive(int n) {
        if (n <= 1) return n;
        return fibRecursive(n - 1) + fibRecursive(n - 2);
    }

    static long fibDP(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) { long t = b; b = a + b; a = t; }
        return b;
    }

    public static void main(String[] args) {
        System.out.println("--- Recursive ---");
        for (int n : new int[]{10, 20, 25, 30, 35, 40}) {
            long start = System.nanoTime();
            int result = fibRecursive(n);
            double ms = (System.nanoTime() - start) / 1e6;
            System.out.printf("Fib(%2d) = %12d | Time: %.2f ms%n", n, result, ms);
        }
        System.out.println("\n--- DP ---");
        for (int n : new int[]{10, 20, 30, 50, 100}) {
            long start = System.nanoTime();
            long result = fibDP(n);
            double ms = (System.nanoTime() - start) / 1e6;
            System.out.printf("Fib(%2d) = %20d | Time: %.4f ms%n", n, result, ms);
        }
    }
}
```

**Python:**

```python
import time

def fib_recursive(n):
    if n <= 1:
        return n
    return fib_recursive(n - 1) + fib_recursive(n - 2)

def fib_dp(n):
    if n <= 1:
        return n
    a, b = 0, 1
    for _ in range(2, n + 1):
        a, b = b, a + b
    return b

print("--- Recursive (exponential) ---")
for n in [10, 20, 25, 30, 35]:
    start = time.perf_counter()
    result = fib_recursive(n)
    elapsed = time.perf_counter() - start
    print(f"Fib({n:2d}) = {result:12d} | Time: {elapsed:.4f}s")

print("\n--- DP (linear) ---")
for n in [10, 20, 30, 50, 100, 1000]:
    start = time.perf_counter()
    result = fib_dp(n)
    elapsed = time.perf_counter() - start
    print(f"Fib({n:4d}) = {str(result)[:20]:>20s}... | Time: {elapsed:.6f}s")
```
