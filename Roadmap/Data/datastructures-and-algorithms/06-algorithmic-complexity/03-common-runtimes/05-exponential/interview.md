# Exponential Time O(2^n) — Interview Questions

## Table of Contents

- [Conceptual Questions](#conceptual-questions)
- [Problem-Solving Questions](#problem-solving-questions)
- [Coding Challenge: Generate All Subsets](#coding-challenge-generate-all-subsets)
- [Follow-Up Questions](#follow-up-questions)
- [Tips for Exponential Problems in Interviews](#tips-for-exponential-problems-in-interviews)

---

## Conceptual Questions

### Q1: What does O(2^n) mean in plain language?

**Answer:** The running time doubles every time the input grows by one element. For n elements, the algorithm performs roughly 2^n operations. This makes the algorithm impractical for large inputs — typically anything beyond n=25-30 becomes too slow on modern hardware.

### Q2: Why is the naive recursive Fibonacci O(2^n)?

**Answer:** Each call to `fib(n)` makes two recursive calls: `fib(n-1)` and `fib(n-2)`. This creates a binary tree of calls with depth n. The total number of nodes in this tree is O(2^n). More precisely it is O(phi^n) where phi = 1.618..., but we commonly say O(2^n) as an upper bound.

### Q3: How do you optimize an O(2^n) algorithm?

**Answer:**
1. **Memoization/DP**: If the problem has overlapping subproblems, store computed results to avoid redundant work. Can reduce O(2^n) to polynomial.
2. **Pruning/Backtracking**: Skip branches that cannot lead to valid solutions. Doesn't change worst case but improves average case.
3. **Meet-in-the-middle**: Split the problem into two halves, solve each in O(2^(n/2)), then combine. Reduces total from O(2^n) to O(2^(n/2)).
4. **Greedy or approximation algorithms**: Accept a non-optimal answer in polynomial time.

### Q4: Give three examples of problems with exponential time complexity.

**Answer:**
1. **Subset sum** (brute force): Check all 2^n subsets to find one that sums to a target.
2. **Tower of Hanoi**: Requires exactly 2^n - 1 moves to solve.
3. **Traveling Salesman** (brute force): Try all permutations. With bitmask DP, reducible to O(2^n * n^2).

### Q5: What is the difference between O(2^n) and O(n!)?

**Answer:** Both are super-polynomial, but O(n!) grows much faster. For n=20: 2^20 = ~10^6 while 20! = ~2.4 * 10^18 — a trillion times larger. O(2^n) arises from subset/include-exclude problems (binary choices), while O(n!) arises from permutation problems (ordering n items).

### Q6: When is O(2^n) acceptable in production?

**Answer:** When n is guaranteed to be small (n <= 20-25), or when there is no known polynomial-time algorithm (NP-hard problems) and approximations are insufficient. Cryptographic security depends on exponential hardness by design.

---

## Problem-Solving Questions

### Q7: Given a set of n integers, find all subsets that sum to zero.

**Approach:** Enumerate all 2^n subsets using bitmask iteration. For each subset, compute the sum and check if it equals zero. Time: O(2^n * n).

**Optimization:** Use meet-in-the-middle to reduce to O(2^(n/2) * n).

### Q8: Count the number of ways to parenthesize n matrices.

**Answer:** This is the Catalan number problem. Brute force is exponential, but with memoization (or direct formula), it is O(n^2). The nth Catalan number is C(2n,n)/(n+1).

### Q9: Determine if a graph is 3-colorable.

**Answer:** This is NP-complete. Brute force tries all 3^n colorings — O(3^n). With clever techniques (inclusion-exclusion), it can be reduced to O(2^n * poly(n)).

---

## Coding Challenge: Generate All Subsets

**Problem:** Given an array of distinct integers, return all possible subsets (the power set). The solution must not contain duplicate subsets.

**Example:**
```
Input:  [1, 2, 3]
Output: [[], [1], [2], [3], [1,2], [1,3], [2,3], [1,2,3]]
```

**Approach 1: Iterative (doubling)**
At each step, take all existing subsets and create new ones by adding the current element.

**Approach 2: Bitmask**
Use integers from 0 to 2^n-1 as bitmasks to represent which elements are included.

**Approach 3: Recursive backtracking**
At each index, decide to include or exclude the current element.

### Solution — Go

```go
package main

import "fmt"

// Approach 1: Iterative doubling.
// Time: O(2^n * n)  Space: O(2^n * n)
func subsetsIterative(nums []int) [][]int {
    result := [][]int{{}}
    for _, num := range nums {
        size := len(result)
        for i := 0; i < size; i++ {
            newSubset := make([]int, len(result[i])+1)
            copy(newSubset, result[i])
            newSubset[len(result[i])] = num
            result = append(result, newSubset)
        }
    }
    return result
}

// Approach 2: Bitmask.
// Time: O(2^n * n)  Space: O(2^n * n)
func subsetsBitmask(nums []int) [][]int {
    n := len(nums)
    total := 1 << n
    result := make([][]int, 0, total)

    for mask := 0; mask < total; mask++ {
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

// Approach 3: Recursive backtracking.
// Time: O(2^n * n)  Space: O(2^n * n)
func subsetsBacktrack(nums []int) [][]int {
    var result [][]int
    var current []int

    var backtrack func(start int)
    backtrack = func(start int) {
        snapshot := make([]int, len(current))
        copy(snapshot, current)
        result = append(result, snapshot)

        for i := start; i < len(nums); i++ {
            current = append(current, nums[i])
            backtrack(i + 1)
            current = current[:len(current)-1]
        }
    }

    backtrack(0)
    return result
}

func main() {
    nums := []int{1, 2, 3}

    fmt.Println("Iterative:", subsetsIterative(nums))
    fmt.Println("Bitmask:  ", subsetsBitmask(nums))
    fmt.Println("Backtrack:", subsetsBacktrack(nums))
}
```

### Solution — Java

```java
import java.util.ArrayList;
import java.util.List;

public class Subsets {

    // Approach 1: Iterative doubling.
    public static List<List<Integer>> subsetsIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        for (int num : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                List<Integer> newSubset = new ArrayList<>(result.get(i));
                newSubset.add(num);
                result.add(newSubset);
            }
        }
        return result;
    }

    // Approach 2: Bitmask.
    public static List<List<Integer>> subsetsBitmask(int[] nums) {
        int n = nums.length;
        int total = 1 << n;
        List<List<Integer>> result = new ArrayList<>(total);

        for (int mask = 0; mask < total; mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(nums[i]);
                }
            }
            result.add(subset);
        }
        return result;
    }

    // Approach 3: Recursive backtracking.
    public static List<List<Integer>> subsetsBacktrack(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, int start,
                                  List<Integer> current,
                                  List<List<Integer>> result) {
        result.add(new ArrayList<>(current));
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        System.out.println("Iterative: " + subsetsIterative(nums));
        System.out.println("Bitmask:   " + subsetsBitmask(nums));
        System.out.println("Backtrack: " + subsetsBacktrack(nums));
    }
}
```

### Solution — Python

```python
from typing import List


# Approach 1: Iterative doubling.
def subsets_iterative(nums: List[int]) -> List[List[int]]:
    result = [[]]
    for num in nums:
        result += [subset + [num] for subset in result]
    return result


# Approach 2: Bitmask.
def subsets_bitmask(nums: List[int]) -> List[List[int]]:
    n = len(nums)
    result = []
    for mask in range(1 << n):
        subset = [nums[i] for i in range(n) if mask & (1 << i)]
        result.append(subset)
    return result


# Approach 3: Recursive backtracking.
def subsets_backtrack(nums: List[int]) -> List[List[int]]:
    result = []

    def backtrack(start: int, current: List[int]) -> None:
        result.append(current[:])
        for i in range(start, len(nums)):
            current.append(nums[i])
            backtrack(i + 1, current)
            current.pop()

    backtrack(0, [])
    return result


if __name__ == "__main__":
    nums = [1, 2, 3]
    print("Iterative:", subsets_iterative(nums))
    print("Bitmask:  ", subsets_bitmask(nums))
    print("Backtrack:", subsets_backtrack(nums))
```

---

## Follow-Up Questions

### What if the array contains duplicates?

Sort the array first. During backtracking, skip elements that are the same as the previous element at the same recursion level:

```python
def subsets_with_dups(nums: List[int]) -> List[List[int]]:
    nums.sort()
    result = []

    def backtrack(start: int, current: List[int]) -> None:
        result.append(current[:])
        for i in range(start, len(nums)):
            if i > start and nums[i] == nums[i - 1]:
                continue  # Skip duplicates
            current.append(nums[i])
            backtrack(i + 1, current)
            current.pop()

    backtrack(0, [])
    return result
```

### Can you generate subsets in lexicographic order?

Yes — sort the input and use the backtracking approach, which naturally produces subsets in lexicographic order.

### What is the space complexity?

O(2^n * n) — there are 2^n subsets, and each can be up to size n. If you only need to process each subset (not store all), the space for the recursion stack alone is O(n).

---

## Tips for Exponential Problems in Interviews

1. **Start brute force:** Always mention the O(2^n) brute-force approach first. Then optimize.
2. **Check for DP:** Ask yourself if there are overlapping subproblems. If yes, memoize.
3. **Mention the complexity class:** Knowing that a problem is NP-hard shows depth.
4. **Discuss trade-offs:** Meet-in-the-middle uses O(2^(n/2)) space. Backtracking uses O(n) space.
5. **Know the practical limits:** n <= 20 for O(2^n), n <= 40 for O(2^(n/2)), n <= 10-12 for O(n!).
6. **Bitmask is your friend:** For subset problems with n <= 20, bitmask enumeration is clean and fast.
