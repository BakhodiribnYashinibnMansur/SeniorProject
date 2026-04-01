# 0046. Permutations

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking with Used Array](#approach-1-backtracking-with-used-array)
4. [Approach 2: Backtracking with Swaps](#approach-2-backtracking-with-swaps)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [46. Permutations](https://leetcode.com/problems/permutations/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking` |

### Description

> Given an array `nums` of distinct integers, return all the possible permutations. You can return the answer in **any order**.

### Examples

```
Example 1:
Input: nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

Example 2:
Input: nums = [0,1]
Output: [[0,1],[1,0]]

Example 3:
Input: nums = [1]
Output: [[1]]
```

### Constraints

- `1 <= nums.length <= 6`
- `-10 <= nums[i] <= 10`
- All the integers of `nums` are **unique**

---

## Problem Breakdown

### 1. What is being asked?

Generate **all possible orderings** (permutations) of the given array. For n distinct elements, there are n! permutations.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of distinct integers |

Important observations about the input:
- All elements are **distinct** (no duplicates)
- Array length is at most 6, so n! = 720 at maximum
- Values can be negative

### 3. What is the output?

- **A list of all permutations** — each permutation is an array of all elements in a specific order
- Order of permutations does not matter
- Each permutation must contain **all** elements exactly once

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 6` | Maximum 6! = 720 permutations — brute force is fine |
| All integers unique | No need to handle duplicates (see Problem 47 for duplicates) |
| `-10 <= nums[i] <= 10` | Small values, no overflow concerns |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1,2,3]`

```text
Initial state: nums = [1, 2, 3]

All permutations (3! = 6):
  [1,2,3]  [1,3,2]  [2,1,3]  [2,3,1]  [3,1,2]  [3,2,1]

Each permutation uses all 3 elements exactly once,
just in a different order.
```

#### Example 2: `nums = [0,1]`

```text
Initial state: nums = [0, 1]

All permutations (2! = 2):
  [0,1]  [1,0]
```

### 6. Key Observations

1. **Backtracking** — Build permutations one element at a time, explore all choices, then undo (backtrack).
2. **Decision tree** — At each level, choose which unused element to place next.
3. **Base case** — When all elements are placed (permutation length == n), add to result.
4. **n! results** — For n elements, exactly n! permutations exist.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Explore all arrangements systematically | Permutations (this problem) |
| Swap-based | In-place generation without extra used array | Heap's algorithm variant |

**Chosen pattern:** `Backtracking`
**Reason:** Classic backtracking problem — build solutions incrementally, explore all choices at each step.

---

## Approach 1: Backtracking with Used Array

### Thought process

> We build each permutation element by element.
> At each step, we try every element that has **not been used yet**.
> We maintain a `used` boolean array to track which elements are already placed.
> When the current permutation reaches length n — it is complete, add it to the result.
> After exploring a choice, we **backtrack**: remove the last element and mark it as unused.

### Algorithm (step-by-step)

1. Create a `result` list to store all permutations
2. Create a `current` list to build the current permutation
3. Create a `used` boolean array of size n (all false)
4. Call `backtrack(current, used)`:
   - If `len(current) == n` → add a copy of `current` to `result`, return
   - For each index `i` from 0 to n-1:
     - If `used[i]` is true → skip
     - Set `used[i] = true`, append `nums[i]` to `current`
     - Recurse: `backtrack(current, used)`
     - Backtrack: remove last element from `current`, set `used[i] = false`
5. Return `result`

### Pseudocode

```text
function permute(nums):
    result = []
    n = len(nums)

    function backtrack(current, used):
        if len(current) == n:
            result.append(copy(current))
            return

        for i = 0 to n-1:
            if used[i]: continue
            used[i] = true
            current.append(nums[i])
            backtrack(current, used)
            current.pop()
            used[i] = false

    backtrack([], [false]*n)
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * n!) | There are n! permutations, and copying each takes O(n) |
| **Space** | O(n) | Recursion depth is n, plus the `used` array and `current` list (excluding output) |

### Implementation

#### Go

```go
func permute(nums []int) [][]int {
    var result [][]int
    n := len(nums)
    used := make([]bool, n)
    current := make([]int, 0, n)

    var backtrack func()
    backtrack = func() {
        // Base case: permutation is complete
        if len(current) == n {
            perm := make([]int, n)
            copy(perm, current)
            result = append(result, perm)
            return
        }

        // Try each unused element
        for i := 0; i < n; i++ {
            if used[i] {
                continue
            }
            // Choose
            used[i] = true
            current = append(current, nums[i])

            // Explore
            backtrack()

            // Unchoose (backtrack)
            current = current[:len(current)-1]
            used[i] = false
        }
    }

    backtrack()
    return result
}
```

#### Java

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, new ArrayList<>(), used, result);
        return result;
    }

    private void backtrack(int[] nums, List<Integer> current,
                           boolean[] used, List<List<Integer>> result) {
        // Base case: permutation is complete
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Try each unused element
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            // Choose
            used[i] = true;
            current.add(nums[i]);

            // Explore
            backtrack(nums, current, used, result);

            // Unchoose (backtrack)
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
```

#### Python

```python
class Solution:
    def permute(self, nums: list[int]) -> list[list[int]]:
        """
        Backtracking with used array
        Time: O(n * n!), Space: O(n)
        """
        result = []
        n = len(nums)
        used = [False] * n

        def backtrack(current: list[int]):
            # Base case: permutation is complete
            if len(current) == n:
                result.append(current[:])  # append a copy
                return

            # Try each unused element
            for i in range(n):
                if used[i]:
                    continue

                # Choose
                used[i] = True
                current.append(nums[i])

                # Explore
                backtrack(current)

                # Unchoose (backtrack)
                current.pop()
                used[i] = False

        backtrack([])
        return result
```

### Dry Run

```text
Input: nums = [1, 2, 3]

backtrack(current=[], used=[F,F,F])
├── i=0: choose 1 → current=[1], used=[T,F,F]
│   ├── i=0: skip (used)
│   ├── i=1: choose 2 → current=[1,2], used=[T,T,F]
│   │   ├── i=0: skip (used)
│   │   ├── i=1: skip (used)
│   │   └── i=2: choose 3 → current=[1,2,3], used=[T,T,T]
│   │       └── ✅ len==3 → result=[[1,2,3]]
│   │       └── backtrack → current=[1,2], used=[T,T,F]
│   └── i=2: choose 3 → current=[1,3], used=[T,F,T]
│       ├── i=0: skip (used)
│       ├── i=1: choose 2 → current=[1,3,2], used=[T,T,T]
│       │   └── ✅ len==3 → result=[[1,2,3],[1,3,2]]
│       │   └── backtrack → current=[1,3], used=[T,F,T]
│       └── i=2: skip (used)
│       └── backtrack → current=[1], used=[T,F,F]
│   └── backtrack → current=[], used=[F,F,F]
├── i=1: choose 2 → current=[2], used=[F,T,F]
│   ├── i=0: choose 1 → current=[2,1], used=[T,T,F]
│   │   └── i=2: choose 3 → current=[2,1,3] ✅ → result=[...,[2,1,3]]
│   └── i=2: choose 3 → current=[2,3], used=[F,T,T]
│       └── i=0: choose 1 → current=[2,3,1] ✅ → result=[...,[2,3,1]]
└── i=2: choose 3 → current=[3], used=[F,F,T]
    ├── i=0: choose 1 → current=[3,1], used=[T,F,T]
    │   └── i=1: choose 2 → current=[3,1,2] ✅ → result=[...,[3,1,2]]
    └── i=1: choose 2 → current=[3,2], used=[F,T,T]
        └── i=0: choose 1 → current=[3,2,1] ✅ → result=[...,[3,2,1]]

Final result: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
Total permutations: 6 (= 3!)
```

---

## Approach 2: Backtracking with Swaps

### Thought process

> Instead of maintaining a separate `used` array, we can generate permutations **in-place** by swapping elements.
> The idea: fix each element at position `start`, then recursively permute the remaining positions.
>
> At each recursion level, we swap `nums[start]` with each element from `start` to `n-1`.
> This naturally ensures each element appears at each position exactly once.
> No extra `used` array needed — the position of the element in the array indicates whether it has been "placed."

### Algorithm (step-by-step)

1. Create a `result` list
2. Call `backtrack(start=0)`:
   - If `start == n` → add a copy of `nums` to `result`, return
   - For each `i` from `start` to `n-1`:
     - Swap `nums[start]` and `nums[i]`
     - Recurse: `backtrack(start + 1)`
     - Swap back `nums[start]` and `nums[i]` (undo)
3. Return `result`

### Pseudocode

```text
function permute(nums):
    result = []
    n = len(nums)

    function backtrack(start):
        if start == n:
            result.append(copy(nums))
            return

        for i = start to n-1:
            swap(nums[start], nums[i])
            backtrack(start + 1)
            swap(nums[start], nums[i])  // undo

    backtrack(0)
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * n!) | Same as Approach 1: n! permutations, O(n) to copy each |
| **Space** | O(n) | Recursion depth is n. No `used` array needed (excluding output) |

### Implementation

#### Go

```go
func permute(nums []int) [][]int {
    var result [][]int
    n := len(nums)

    var backtrack func(start int)
    backtrack = func(start int) {
        // Base case: all positions are fixed
        if start == n {
            perm := make([]int, n)
            copy(perm, nums)
            result = append(result, perm)
            return
        }

        // Try placing each element at position 'start'
        for i := start; i < n; i++ {
            // Swap nums[start] and nums[i]
            nums[start], nums[i] = nums[i], nums[start]

            // Recurse on the remaining positions
            backtrack(start + 1)

            // Swap back (undo)
            nums[start], nums[i] = nums[i], nums[start]
        }
    }

    backtrack(0)
    return result
}
```

#### Java

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<List<Integer>> result) {
        // Base case: all positions are fixed
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int num : nums) perm.add(num);
            result.add(perm);
            return;
        }

        // Try placing each element at position 'start'
        for (int i = start; i < nums.length; i++) {
            // Swap nums[start] and nums[i]
            int temp = nums[start];
            nums[start] = nums[i];
            nums[i] = temp;

            // Recurse on the remaining positions
            backtrack(nums, start + 1, result);

            // Swap back (undo)
            temp = nums[start];
            nums[start] = nums[i];
            nums[i] = temp;
        }
    }
}
```

#### Python

```python
class Solution:
    def permute(self, nums: list[int]) -> list[list[int]]:
        """
        Backtracking with swaps (in-place)
        Time: O(n * n!), Space: O(n)
        """
        result = []
        n = len(nums)

        def backtrack(start: int):
            # Base case: all positions are fixed
            if start == n:
                result.append(nums[:])  # append a copy
                return

            # Try placing each element at position 'start'
            for i in range(start, n):
                # Swap nums[start] and nums[i]
                nums[start], nums[i] = nums[i], nums[start]

                # Recurse on the remaining positions
                backtrack(start + 1)

                # Swap back (undo)
                nums[start], nums[i] = nums[i], nums[start]

        backtrack(0)
        return result
```

### Dry Run

```text
Input: nums = [1, 2, 3]

backtrack(start=0), nums=[1,2,3]
├── i=0: swap(0,0) → nums=[1,2,3]
│   backtrack(start=1), nums=[1,2,3]
│   ├── i=1: swap(1,1) → nums=[1,2,3]
│   │   backtrack(start=2), nums=[1,2,3]
│   │   ├── i=2: swap(2,2) → nums=[1,2,3]
│   │   │   backtrack(start=3) → ✅ result=[[1,2,3]]
│   │   │   swap back(2,2) → nums=[1,2,3]
│   │   swap back(1,1) → nums=[1,2,3]
│   └── i=2: swap(1,2) → nums=[1,3,2]
│       backtrack(start=2), nums=[1,3,2]
│       ├── i=2: swap(2,2) → nums=[1,3,2]
│       │   backtrack(start=3) → ✅ result=[...,[1,3,2]]
│       │   swap back(2,2) → nums=[1,3,2]
│       swap back(1,2) → nums=[1,2,3]
│   swap back(0,0) → nums=[1,2,3]
├── i=1: swap(0,1) → nums=[2,1,3]
│   backtrack(start=1), nums=[2,1,3]
│   ├── i=1: swap(1,1) → [2,1,3]
│   │   backtrack(start=2) → swap(2,2) → ✅ [2,1,3]
│   └── i=2: swap(1,2) → [2,3,1]
│       backtrack(start=2) → swap(2,2) → ✅ [2,3,1]
│   swap back(0,1) → nums=[1,2,3]
└── i=2: swap(0,2) → nums=[3,2,1]
    backtrack(start=1), nums=[3,2,1]
    ├── i=1: swap(1,1) → [3,2,1]
    │   backtrack(start=2) → swap(2,2) → ✅ [3,2,1]
    └── i=2: swap(1,2) → [3,1,2]
        backtrack(start=2) → swap(2,2) → ✅ [3,1,2]
    swap back(0,2) → nums=[1,2,3]

Final result: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,2,1],[3,1,2]]
Total permutations: 6 (= 3!)
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking + Used Array | O(n * n!) | O(n) | Clear logic, easy to understand | Extra `used` array |
| 2 | Backtracking + Swaps | O(n * n!) | O(n) | In-place, no extra array | Order of permutations differs |

### Which solution to choose?

- **In an interview:** Approach 1 (Used Array) — clearest explanation, easy to extend to Permutations II (with duplicates)
- **In production:** Either — both produce correct results with same complexity
- **On Leetcode:** Both pass — choose whichever feels more natural
- **For learning:** Both — Approach 1 teaches the classic backtracking template, Approach 2 teaches in-place swap technique

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single element | `nums=[1]` | `[[1]]` | Only one permutation |
| 2 | Two elements | `nums=[0,1]` | `[[0,1],[1,0]]` | 2! = 2 permutations |
| 3 | Negative numbers | `nums=[-1,0,1]` | 6 permutations | Negative values work the same |
| 4 | Maximum length | `nums=[1,2,3,4,5,6]` | 720 permutations | 6! = 720, must handle all |

---

## Common Mistakes

### Mistake 1: Not copying the current permutation

```python
# ❌ WRONG — appending a reference, not a copy
result.append(current)  # current will be modified later!

# ✅ CORRECT — append a copy
result.append(current[:])  # or list(current) or current.copy()
```

**Reason:** In backtracking, `current` is modified in place. If you append a reference, all entries in `result` will point to the same (eventually empty) list.

### Mistake 2: Not resetting state (forgetting to backtrack)

```python
# ❌ WRONG — forgot to undo the choice
used[i] = True
current.append(nums[i])
backtrack(current)
# missing: current.pop() and used[i] = False

# ✅ CORRECT — always undo after recursion
used[i] = True
current.append(nums[i])
backtrack(current)
current.pop()        # undo append
used[i] = False      # undo mark
```

**Reason:** Without undoing the choice, subsequent iterations see incorrect state and produce wrong results.

### Mistake 3: Using the wrong loop range in swap approach

```python
# ❌ WRONG — starting from 0 instead of start
for i in range(len(nums)):       # generates duplicates!
    nums[start], nums[i] = nums[i], nums[start]

# ✅ CORRECT — start from 'start' position
for i in range(start, len(nums)):
    nums[start], nums[i] = nums[i], nums[start]
```

**Reason:** Elements before `start` are already fixed. Swapping with them produces duplicate permutations.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [47. Permutations II](https://leetcode.com/problems/permutations-ii/) | :yellow_circle: Medium | Permutations with duplicate elements |
| 2 | [31. Next Permutation](https://leetcode.com/problems/next-permutation/) | :yellow_circle: Medium | Find the next lexicographic permutation |
| 3 | [77. Combinations](https://leetcode.com/problems/combinations/) | :yellow_circle: Medium | Subsets of size k (order doesn't matter) |
| 4 | [78. Subsets](https://leetcode.com/problems/subsets/) | :yellow_circle: Medium | All subsets (power set) |
| 5 | [60. Permutation Sequence](https://leetcode.com/problems/permutation-sequence/) | :red_circle: Hard | Find the k-th permutation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Backtracking Tree** — visualizes the decision tree with choose/explore/unchoose
> - **Permutation Building** — shows the current permutation being constructed step by step
> - **Step/Play/Pause/Reset** controls with speed slider
> - **Presets** — try different input arrays
> - **Log** — detailed step-by-step explanation
> - **Result** — collected permutations displayed as they are found
