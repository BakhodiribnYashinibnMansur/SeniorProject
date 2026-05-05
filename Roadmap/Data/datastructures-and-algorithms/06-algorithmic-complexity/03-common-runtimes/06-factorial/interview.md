# Factorial Time O(n!) -- Interview Questions

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Coding Challenge 1: Generate All Permutations](#coding-challenge-1-generate-all-permutations)
3. [Coding Challenge 2: Next Permutation](#coding-challenge-2-next-permutation)
4. [Coding Challenge 3: Permutation Rank](#coding-challenge-3-permutation-rank)
5. [Problem-Solving Questions](#problem-solving-questions)
6. [System Design Questions](#system-design-questions)

---

## Conceptual Questions

### Q1: What is O(n!) and when does it arise?

**Expected Answer**: O(n!) means the algorithm's running time grows proportionally to
n factorial (n * (n-1) * ... * 1). It arises when an algorithm must consider all
possible orderings (permutations) of n elements, such as brute-force TSP or generating
all permutations.

### Q2: Why is O(n!) worse than O(2^n)?

**Expected Answer**: For large n, n! grows much faster than 2^n. Each factor in n!
increases, while 2^n always multiplies by 2. By Stirling's approximation, n! ~
sqrt(2*pi*n) * (n/e)^n, so the base of the exponential is effectively n/e rather than
2. For n=20, 2^20 ~ 10^6 while 20! ~ 2.4 x 10^18.

### Q3: What is the connection between n! and sorting lower bounds?

**Expected Answer**: There are n! possible permutations of n elements. A comparison-based
sorting algorithm builds a binary decision tree that must distinguish between all n!
inputs. A binary tree with n! leaves has depth at least log2(n!) = Theta(n log n).
Therefore, comparison-based sorting requires Omega(n log n) comparisons.

### Q4: How can you reduce an O(n!) TSP to something better?

**Expected Answer**: Use dynamic programming (Held-Karp) to reduce to O(2^n * n^2) by
tracking which cities are visited (subsets) rather than the order. For practical
instances, use heuristics (nearest neighbor, 2-opt) or approximation algorithms
(Christofides for 1.5x optimal on metric TSP).

### Q5: What is the maximum practical n for an O(n!) algorithm?

**Expected Answer**: Roughly n = 10-12 for interactive use (seconds), and n = 15-17 for
batch processing (hours). At n=10, 10! = 3.6M operations; at n=12, 12! = 479M; at
n=15, 15! = 1.3T.

---

## Coding Challenge 1: Generate All Permutations

**Problem**: Given an array of distinct integers, return all possible permutations.
(LeetCode 46)

### Go Solution

```go
package main

import "fmt"

func permute(nums []int) [][]int {
    var result [][]int
    n := len(nums)

    var backtrack func(first int)
    backtrack = func(first int) {
        if first == n {
            perm := make([]int, n)
            copy(perm, nums)
            result = append(result, perm)
            return
        }
        for i := first; i < n; i++ {
            nums[first], nums[i] = nums[i], nums[first]
            backtrack(first + 1)
            nums[first], nums[i] = nums[i], nums[first]
        }
    }

    backtrack(0)
    return result
}

func main() {
    nums := []int{1, 2, 3}
    result := permute(nums)
    fmt.Printf("Total: %d permutations\n", len(result))
    for _, p := range result {
        fmt.Println(p)
    }
}
```

### Java Solution

```java
import java.util.ArrayList;
import java.util.List;

public class Permutations {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, result);
        return result;
    }

    private void backtrack(int[] nums, int first, List<List<Integer>> result) {
        if (first == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int num : nums) perm.add(num);
            result.add(perm);
            return;
        }
        for (int i = first; i < nums.length; i++) {
            swap(nums, first, i);
            backtrack(nums, first + 1, result);
            swap(nums, first, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        Permutations sol = new Permutations();
        List<List<Integer>> result = sol.permute(new int[]{1, 2, 3});
        System.out.println("Total: " + result.size());
        result.forEach(System.out::println);
    }
}
```

### Python Solution

```python
def permute(nums):
    result = []

    def backtrack(first=0):
        if first == len(nums):
            result.append(nums[:])
            return
        for i in range(first, len(nums)):
            nums[first], nums[i] = nums[i], nums[first]
            backtrack(first + 1)
            nums[first], nums[i] = nums[i], nums[first]

    backtrack()
    return result


nums = [1, 2, 3]
print(f"Total: {len(permute(nums))} permutations")
for p in permute(nums):
    print(p)
```

**Time**: O(n! * n) -- n! permutations, each taking O(n) to copy.
**Space**: O(n! * n) for storing results, O(n) for recursion stack.

---

## Coding Challenge 2: Next Permutation

**Problem**: Given an array of integers, rearrange it into the lexicographically next
greater permutation. If the arrangement is the largest, rearrange to the smallest
(sorted ascending). Modify in-place with O(1) extra memory. (LeetCode 31)

### Go Solution

```go
package main

import "fmt"

func nextPermutation(nums []int) {
    n := len(nums)

    // Step 1: find largest i such that nums[i] < nums[i+1]
    i := n - 2
    for i >= 0 && nums[i] >= nums[i+1] {
        i--
    }

    if i >= 0 {
        // Step 2: find largest j such that nums[j] > nums[i]
        j := n - 1
        for nums[j] <= nums[i] {
            j--
        }
        // Step 3: swap
        nums[i], nums[j] = nums[j], nums[i]
    }

    // Step 4: reverse suffix starting at i+1
    left, right := i+1, n-1
    for left < right {
        nums[left], nums[right] = nums[right], nums[left]
        left++
        right--
    }
}

func main() {
    tests := [][]int{
        {1, 2, 3},
        {3, 2, 1},
        {1, 1, 5},
        {1, 3, 2},
    }
    for _, nums := range tests {
        fmt.Printf("Before: %v -> ", nums)
        nextPermutation(nums)
        fmt.Printf("After: %v\n", nums)
    }
}
```

### Java Solution

```java
import java.util.Arrays;

public class NextPermutation {

    public void nextPermutation(int[] nums) {
        int n = nums.length;
        int i = n - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }

        if (i >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[i]) {
                j--;
            }
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }

        // Reverse from i+1 to end
        int left = i + 1, right = n - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }

    public static void main(String[] args) {
        NextPermutation sol = new NextPermutation();
        int[][] tests = {{1,2,3}, {3,2,1}, {1,1,5}, {1,3,2}};
        for (int[] t : tests) {
            System.out.print("Before: " + Arrays.toString(t) + " -> ");
            sol.nextPermutation(t);
            System.out.println("After: " + Arrays.toString(t));
        }
    }
}
```

### Python Solution

```python
def next_permutation(nums):
    n = len(nums)

    # Step 1: find largest i with nums[i] < nums[i+1]
    i = n - 2
    while i >= 0 and nums[i] >= nums[i + 1]:
        i -= 1

    if i >= 0:
        # Step 2: find largest j with nums[j] > nums[i]
        j = n - 1
        while nums[j] <= nums[i]:
            j -= 1
        # Step 3: swap
        nums[i], nums[j] = nums[j], nums[i]

    # Step 4: reverse suffix
    nums[i + 1:] = reversed(nums[i + 1:])


tests = [[1, 2, 3], [3, 2, 1], [1, 1, 5], [1, 3, 2]]
for t in tests:
    before = t[:]
    next_permutation(t)
    print(f"{before} -> {t}")
```

**Time**: O(n) per call.
**Space**: O(1) extra.

---

## Coding Challenge 3: Permutation Rank

**Problem**: Given a permutation of [1..n], find its 1-based rank in lexicographic order.

### Go Solution

```go
package main

import "fmt"

func permutationRank(perm []int) int {
    n := len(perm)
    rank := 0
    factorials := make([]int, n)
    factorials[0] = 1
    for i := 1; i < n; i++ {
        factorials[i] = factorials[i-1] * i
    }

    used := make([]bool, n+1)
    for i := 0; i < n; i++ {
        // Count how many unused numbers are smaller than perm[i]
        smaller := 0
        for j := 1; j < perm[i]; j++ {
            if !used[j] {
                smaller++
            }
        }
        rank += smaller * factorials[n-1-i]
        used[perm[i]] = true
    }
    return rank + 1 // 1-based
}

func main() {
    fmt.Println(permutationRank([]int{1, 2, 3})) // 1
    fmt.Println(permutationRank([]int{3, 2, 1})) // 6
    fmt.Println(permutationRank([]int{2, 1, 3})) // 3
}
```

### Java Solution

```java
public class PermutationRank {
    public static int rank(int[] perm) {
        int n = perm.length;
        int[] fact = new int[n];
        fact[0] = 1;
        for (int i = 1; i < n; i++) fact[i] = fact[i-1] * i;

        boolean[] used = new boolean[n + 1];
        int rank = 0;
        for (int i = 0; i < n; i++) {
            int smaller = 0;
            for (int j = 1; j < perm[i]; j++) {
                if (!used[j]) smaller++;
            }
            rank += smaller * fact[n - 1 - i];
            used[perm[i]] = true;
        }
        return rank + 1;
    }

    public static void main(String[] args) {
        System.out.println(rank(new int[]{1, 2, 3})); // 1
        System.out.println(rank(new int[]{3, 2, 1})); // 6
        System.out.println(rank(new int[]{2, 1, 3})); // 3
    }
}
```

### Python Solution

```python
def permutation_rank(perm):
    n = len(perm)
    factorials = [1] * n
    for i in range(1, n):
        factorials[i] = factorials[i - 1] * i

    used = set()
    rank = 0
    for i in range(n):
        smaller = sum(1 for j in range(1, perm[i]) if j not in used)
        rank += smaller * factorials[n - 1 - i]
        used.add(perm[i])
    return rank + 1


print(permutation_rank([1, 2, 3]))  # 1
print(permutation_rank([3, 2, 1]))  # 6
print(permutation_rank([2, 1, 3]))  # 3
```

**Time**: O(n^2) -- for each position, scan smaller unused elements.
**Space**: O(n).

---

## Problem-Solving Questions

### Q6: Given n people and n tasks (each person has a cost for each task), find the minimum cost assignment.

**Expected Answer**: This is the **assignment problem**. Brute force is O(n!) -- try all
permutations. The optimal solution uses the **Hungarian algorithm** in O(n^3). Key
insight: the problem has polynomial structure (it is a bipartite matching) despite having
n! possible assignments.

### Q7: How would you generate permutations of a string with duplicate characters?

**Expected Answer**: Sort the characters first, then use backtracking but skip
a character if it equals the previous character at the same level (and the previous
was not used). This generates only n!/(k1! * k2! * ... * km!) distinct permutations.

### Q8: Can you solve TSP for 10,000 cities?

**Expected Answer**: Not optimally in general, but within a few percent of optimal using:
(1) nearest neighbor construction O(n^2), (2) 2-opt improvement O(n^2) per iteration,
(3) Lin-Kernighan heuristic, or (4) Concorde solver (branch-and-cut, solves instances
up to ~85,000 cities). The key is that we abandon optimality guarantees for practical
quality.

---

## System Design Questions

### Q9: Design a delivery route optimization service.

**Key Points**:
- Input: list of addresses with time windows, vehicle capacities.
- Geo-cluster addresses into vehicle territories.
- Construct initial routes with insertion heuristic.
- Improve with local search (2-opt, or-opt) under time budget.
- API returns routes within 2-5 seconds for up to 10,000 stops.
- Background worker continues improving for batch planning.
- Cache previous solutions for incremental updates.

### Q10: How would you benchmark permutation algorithms?

**Key Points**:
- Measure wall time for n = 6, 7, 8, 9, 10, 11, 12.
- Plot time vs n on log scale; slope should match n! growth.
- Compare Heap's algorithm, simple recursion, and next_permutation.
- Measure memory usage (in-place vs collecting all results).
- Verify correctness: count should be exactly n!, no duplicates.
