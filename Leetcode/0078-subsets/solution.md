# 0078. Subsets

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking](#approach-1-backtracking)
4. [Approach 2: Cascading (Iterative)](#approach-2-cascading-iterative)
5. [Approach 3: Bit Manipulation](#approach-3-bit-manipulation)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [78. Subsets](https://leetcode.com/problems/subsets/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking`, `Bit Manipulation` |

### Description

> Given an integer array `nums` of **unique** elements, return *all possible subsets (the power set)*.
>
> The solution set **must not** contain duplicate subsets. Return the solution in any order.

### Examples

```
Example 1:
Input: nums = [1,2,3]
Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]

Example 2:
Input: nums = [0]
Output: [[],[0]]
```

### Constraints

- `1 <= nums.length <= 10`
- `-10 <= nums[i] <= 10`
- All elements of `nums` are **unique**.

---

## Problem Breakdown

### 1. What is being asked?

Generate the *power set* -- every possible subset, including the empty set and `nums` itself.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Distinct integers, length up to 10 |

### 3. What is the output?

A list of `2^n` subsets.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10` | At most `2^10 = 1024` subsets |
| Unique elements | No duplicate-handling needed (see Problem 90 otherwise) |

### 5. Step-by-step example analysis

#### `nums = [1, 2, 3]`

```text
Backtracking tree:
  [] → start at index 0:
    pick 1 → [1] → recurse at index 1:
      pick 2 → [1,2] → recurse at 2:
        pick 3 → [1,2,3]
        skip 3 (return → backtrack)
      skip 2 → [1] → recurse at 2:
        pick 3 → [1,3]
    skip 1 → [] → recurse at 1:
      ...

Order varies; final list has 8 subsets.
```

### 6. Key Observations

1. **Backtracking** -- For each element, choose include or skip. Generates all `2^n` subsets.
2. **Cascading** -- Start with `[[]]`. For each `x`, append `x` to every existing subset and add the result.
3. **Bitmask** -- Each `2^n` integer represents a subset; bit `i` set means `nums[i]` is included.

### 7. Pattern identification

All three approaches are textbook for power set.

**Chosen pattern:** `Backtracking` (most common in interviews).

---

## Approach 1: Backtracking

### Algorithm

1. Recurse with `(start, current)`. At each call, append a copy of `current` to the result.
2. For each `i` from `start` to `n-1`: append `nums[i]`, recurse on `i+1`, pop.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n * 2^n) |
| **Space** | O(n) recursion |

### Implementation

#### Go

```go
func subsets(nums []int) [][]int {
    result := [][]int{}
    cur := []int{}
    var bt func(start int)
    bt = func(start int) {
        cp := make([]int, len(cur))
        copy(cp, cur)
        result = append(result, cp)
        for i := start; i < len(nums); i++ {
            cur = append(cur, nums[i])
            bt(i + 1)
            cur = cur[:len(cur)-1]
        }
    }
    bt(0)
    return result
}
```

#### Java

```java
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        bt(0, nums, cur, result);
        return result;
    }
    private void bt(int start, int[] nums, List<Integer> cur, List<List<Integer>> result) {
        result.add(new ArrayList<>(cur));
        for (int i = start; i < nums.length; i++) {
            cur.add(nums[i]);
            bt(i + 1, nums, cur, result);
            cur.remove(cur.size() - 1);
        }
    }
}
```

#### Python

```python
class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        result = []
        cur = []
        def bt(start: int):
            result.append(cur.copy())
            for i in range(start, len(nums)):
                cur.append(nums[i])
                bt(i + 1)
                cur.pop()
        bt(0)
        return result
```

---

## Approach 2: Cascading (Iterative)

### Idea

> Maintain `result = [[]]`. For each `x` in `nums`, replace `result` with `result + [s + [x] for s in result]`.

### Implementation

#### Python

```python
class Solution:
    def subsetsCascade(self, nums: List[int]) -> List[List[int]]:
        result = [[]]
        for x in nums:
            result += [s + [x] for s in result]
        return result
```

---

## Approach 3: Bit Manipulation

### Idea

> Iterate over masks `0..2^n - 1`. For each mask, build the subset by including `nums[i]` whenever bit `i` is set.

### Implementation

#### Python

```python
class Solution:
    def subsetsBits(self, nums: List[int]) -> List[List[int]]:
        n = len(nums)
        result = []
        for mask in range(1 << n):
            sub = [nums[i] for i in range(n) if (mask >> i) & 1]
            result.append(sub)
        return result
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking | O(n * 2^n) | O(n) | Most intuitive | Recursion |
| 2 | Cascading | O(n * 2^n) | O(n * 2^n) | One-liner in Python | Quadratic memory churn |
| 3 | Bit Manipulation | O(n * 2^n) | O(n) | No recursion | Less obvious |

### Which solution to choose?

Approach 1 in interviews.

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | Single element | `[[], [x]]` |
| 2 | Empty array | `[[]]` (constraint says `n >= 1` so not needed) |
| 3 | All distinct | Standard |
| 4 | n = 10 | 1024 subsets |
| 5 | Negatives | No special handling |

---

## Common Mistakes

### Mistake 1: Forgetting to copy `cur`

```python
# WRONG — appends shared reference
result.append(cur)

# CORRECT
result.append(cur.copy())
```

### Mistake 2: Cascade builds subsets twice

```python
# WRONG — uses += inside iteration over result; in some languages this can iterate over new entries
for x in nums:
    for s in result:        # may iterate over newly added items
        result.append(s + [x])

# CORRECT — snapshot before extending
for x in nums:
    result += [s + [x] for s in result]   # list comprehension is evaluated first
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [90. Subsets II](https://leetcode.com/problems/subsets-ii/) | :yellow_circle: Medium | Subsets with duplicates |
| 2 | [77. Combinations](https://leetcode.com/problems/combinations/) | :yellow_circle: Medium | Fixed-size subsets |
| 3 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Different enumeration |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Tree of include/exclude decisions
> - Live list of generated subsets
> - Toggle between three approaches
