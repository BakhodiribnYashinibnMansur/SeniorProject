# 0047. Permutations II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking with Sorting + Duplicate Skipping](#approach-1-backtracking-with-sorting--duplicate-skipping)
4. [Complexity Analysis](#complexity-analysis)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Comparison with Problem 46 (Permutations)](#comparison-with-problem-46-permutations)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [47. Permutations II](https://leetcode.com/problems/permutations-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking` |

### Description

> Given a collection of numbers, `nums`, that might contain duplicates, return all possible **unique permutations** in any order.

### Examples

```
Example 1:
Input: nums = [1,1,2]
Output: [[1,1,2],[1,2,1],[2,1,1]]

Example 2:
Input: nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

### Constraints

- `1 <= nums.length <= 8`
- `-10 <= nums[i] <= 10`

---

## Problem Breakdown

### 1. What is being asked?

Generate **all unique permutations** of an array that may contain duplicate elements. Each permutation must appear **exactly once** in the result, even if the same value appears multiple times in the input.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers, possibly with duplicates |

Important observations about the input:
- The array can contain **duplicates** (e.g., `[1, 1, 2]`)
- Length is at most **8** — backtracking is feasible (8! = 40,320 max)
- Values range from **-10 to 10**

### 3. What is the output?

- A **list of permutations** `[[...], [...], ...]`
- Each permutation is a **unique ordering** of all elements
- **No duplicate permutations** in the result
- Order of permutations does not matter

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 8` | Backtracking is efficient (worst case 8! = 40,320) |
| `-10 <= nums[i] <= 10` | Small value range; duplicates are likely |
| Unique permutations | Must handle deduplication — the core challenge |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1, 1, 2]`

```text
After sorting: [1, 1, 2]

Without deduplication, we'd get 3! = 6 permutations:
  [1, 1, 2], [1, 2, 1], [1, 1, 2], [1, 2, 1], [2, 1, 1], [2, 1, 1]
  ↑ duplicates appear!

With deduplication (skipping same value at same level):
  [1, 1, 2], [1, 2, 1], [2, 1, 1]
  Only 3 unique permutations
```

### 6. Key Observations

1. **Sorting groups duplicates together** — Adjacent duplicate values can be detected easily.
2. **Same value at same recursion level = duplicate permutation** — If we've already placed value `1` at position `k`, placing another `1` at position `k` gives the same subtree.
3. **Skip rule:** At each recursion level, skip `nums[i]` if `nums[i] == nums[i-1]` and `nums[i-1]` was **not used** (meaning it was skipped/backtracked at this level).
4. **Difference from Problem 46:** Problem 46 has all unique elements, so no deduplication needed. Problem 47 adds the sorting + skip logic.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking + Sort + Skip | Generates permutations while pruning duplicate branches | This problem |
| Hash Set dedup | Generate all, deduplicate at end | Works but wastes time on redundant branches |

**Chosen pattern:** `Backtracking with Sorting + Duplicate Skipping`
**Reason:** Pruning at the source is more efficient than generating duplicates and filtering.

---

## Approach 1: Backtracking with Sorting + Duplicate Skipping

### Thought process

> The idea is the same as generating all permutations (Problem 46), but with an extra constraint: **avoid placing the same value at the same position twice**.
>
> To achieve this:
> 1. **Sort** the array so duplicates are adjacent
> 2. Use a `used[]` boolean array to track which elements are currently in the permutation
> 3. At each recursion level, **skip** `nums[i]` if:
>    - `nums[i] == nums[i-1]` (same value as previous), AND
>    - `used[i-1] == false` (previous was not used — meaning it was skipped/backtracked at this level)
>
> The skip condition `used[i-1] == false` ensures we only use the **first** occurrence of a duplicate value at each level, avoiding redundant branches.

### Algorithm (step-by-step)

1. Sort `nums`
2. Initialize `used[]` array (all `false`), `path = []`, `result = []`
3. Call `backtrack()`:
   - If `path.length == nums.length` → add copy of `path` to `result`
   - For `i = 0` to `n-1`:
     - If `used[i]` → skip (already in current permutation)
     - If `i > 0` and `nums[i] == nums[i-1]` and `!used[i-1]` → skip (duplicate pruning)
     - Mark `used[i] = true`, add `nums[i]` to `path`
     - Recurse: `backtrack()`
     - Undo: remove last from `path`, mark `used[i] = false`

### Pseudocode

```text
function permuteUnique(nums):
    sort(nums)
    result = []
    used = [false] * len(nums)

    function backtrack(path):
        if len(path) == len(nums):
            result.append(copy(path))
            return

        for i = 0 to len(nums) - 1:
            if used[i]: continue
            if i > 0 and nums[i] == nums[i-1] and not used[i-1]: continue

            used[i] = true
            path.append(nums[i])
            backtrack(path)
            path.pop()
            used[i] = false

    backtrack([])
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * n!) | At most n! permutations, each takes O(n) to copy |
| **Space** | O(n) | Recursion depth O(n) + used array O(n) (output not counted) |

> **Note:** With duplicates, the actual number of permutations is n! / (k1! * k2! * ... * km!) where ki is the count of each distinct value. The pruning avoids generating the redundant branches entirely.

### Implementation

#### Go

```go
func permuteUnique(nums []int) [][]int {
    sort.Ints(nums)
    result := [][]int{}
    used := make([]bool, len(nums))
    path := []int{}

    var backtrack func()
    backtrack = func() {
        if len(path) == len(nums) {
            tmp := make([]int, len(path))
            copy(tmp, path)
            result = append(result, tmp)
            return
        }
        for i := 0; i < len(nums); i++ {
            if used[i] { continue }
            if i > 0 && nums[i] == nums[i-1] && !used[i-1] { continue }
            used[i] = true
            path = append(path, nums[i])
            backtrack()
            path = path[:len(path)-1]
            used[i] = false
        }
    }

    backtrack()
    return result
}
```

#### Java

```java
public List<List<Integer>> permuteUnique(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, used, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
        used[i] = true;
        path.add(nums[i]);
        backtrack(nums, used, path, result);
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

#### Python

```python
def permuteUnique(self, nums: list[int]) -> list[list[int]]:
    nums.sort()
    result = []
    used = [False] * len(nums)

    def backtrack(path):
        if len(path) == len(nums):
            result.append(path[:])
            return
        for i in range(len(nums)):
            if used[i]:
                continue
            if i > 0 and nums[i] == nums[i - 1] and not used[i - 1]:
                continue
            used[i] = True
            path.append(nums[i])
            backtrack(path)
            path.pop()
            used[i] = False

    backtrack([])
    return result
```

### Dry Run

```text
Input: nums = [1, 1, 2]
After sorting: [1, 1, 2]
used = [F, F, F], path = []

backtrack([]):
  i=0: nums[0]=1, not used → place
    used=[T,F,F], path=[1]
    backtrack([1]):
      i=0: used[0]=T → skip
      i=1: nums[1]=1, not used
           i>0 and nums[1]==nums[0] and !used[0]? → !used[0]=!T=F → NO skip
           → place
        used=[T,T,F], path=[1,1]
        backtrack([1,1]):
          i=0: used → skip
          i=1: used → skip
          i=2: nums[2]=2, not used → place
            used=[T,T,T], path=[1,1,2]
            len==3 → ADD [1,1,2] ✅
            undo → path=[1,1], used=[T,T,F]
        undo → path=[1], used=[T,F,F]
      i=2: nums[2]=2, not used → place
        used=[T,F,T], path=[1,2]
        backtrack([1,2]):
          i=0: used → skip
          i=1: nums[1]=1, not used
               i>0 and nums[1]==nums[0] and !used[0]? → !T=F → NO skip
               → place
            used=[T,T,T], path=[1,2,1]
            len==3 → ADD [1,2,1] ✅
            undo → path=[1,2], used=[T,F,T]
          i=2: used → skip
        undo → path=[1], used=[T,F,F]
    undo → path=[], used=[F,F,F]

  i=1: nums[1]=1, not used
       i>0 and nums[1]==nums[0] and !used[0]? → !F=T → YES → SKIP ⛔
       (This is the key pruning — avoids generating duplicate subtree)

  i=2: nums[2]=2, not used → place
    used=[F,F,T], path=[2]
    backtrack([2]):
      i=0: nums[0]=1, not used → place
        used=[T,F,T], path=[2,1]
        backtrack([2,1]):
          i=0: used → skip
          i=1: nums[1]=1, not used
               i>0 and nums[1]==nums[0] and !used[0]? → !T=F → NO skip
               → place
            used=[T,T,T], path=[2,1,1]
            len==3 → ADD [2,1,1] ✅
            undo → path=[2,1], used=[T,F,T]
          i=2: used → skip
        undo → path=[2], used=[F,F,T]
      i=1: nums[1]=1, not used
           i>0 and nums[1]==nums[0] and !used[0]? → !F=T → YES → SKIP ⛔
      i=2: used → skip
    undo → path=[], used=[F,F,F]

Result: [[1,1,2], [1,2,1], [2,1,1]]
```

---

## Complexity Analysis

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * n!) | Worst case (all unique): n! permutations, O(n) each to copy |
| **Space** | O(n) | Recursion stack depth + used array + path (output not counted) |

With duplicates, actual work is significantly less than n! due to pruning. For example:
- `[1,1,2]`: 3 permutations instead of 6
- `[1,1,1]`: 1 permutation instead of 6
- `[1,1,2,2]`: 6 permutations instead of 24

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | LeetCode Example 1 | `[1,1,2]` | `[[1,1,2],[1,2,1],[2,1,1]]` | Duplicates require pruning |
| 2 | All unique | `[1,2,3]` | All 6 permutations | Same as Problem 46 |
| 3 | All same | `[1,1,1]` | `[[1,1,1]]` | Only one unique permutation |
| 4 | Single element | `[0]` | `[[0]]` | Trivial case |
| 5 | Two same | `[1,1]` | `[[1,1]]` | One permutation |
| 6 | Negative numbers | `[-1,1,1]` | `[[-1,1,1],[1,-1,1],[1,1,-1]]` | Negatives work the same way |
| 7 | Two duplicates | `[1,1,2,2]` | 6 permutations | Multiple duplicate groups |

---

## Common Mistakes

### Mistake 1: Forgetting to sort the array

```python
# WRONG -- duplicate pruning logic fails without sorting
def permuteUnique(self, nums):
    # nums is not sorted, so nums[i] == nums[i-1] doesn't catch all duplicates
    ...

# CORRECT -- sort first so duplicates are adjacent
def permuteUnique(self, nums):
    nums.sort()  # Essential!
    ...
```

**Reason:** The skip condition `nums[i] == nums[i-1]` only works when duplicates are adjacent, which requires sorting.

### Mistake 2: Wrong skip condition

```python
# WRONG -- skips too aggressively, misses valid permutations
if i > 0 and nums[i] == nums[i-1]:
    continue  # Skips even when nums[i-1] is used in the path!

# CORRECT -- only skip when the previous duplicate was NOT used
if i > 0 and nums[i] == nums[i-1] and not used[i-1]:
    continue
```

**Reason:** If `used[i-1] == True`, the previous duplicate is already placed in a higher level of the recursion, so using `nums[i]` at this level is valid and produces a different permutation.

### Mistake 3: Using a HashSet instead of pruning

```python
# WORKS but wasteful -- generates all n! permutations, then deduplicates
result_set = set()
# ... generate all permutations ...
# ... add tuple(perm) to result_set ...

# BETTER -- prune duplicate branches during recursion (never generates them)
if i > 0 and nums[i] == nums[i-1] and not used[i-1]:
    continue
```

**Reason:** The HashSet approach does O(n!) work even with many duplicates. The pruning approach skips entire subtrees.

---

## Comparison with Problem 46 (Permutations)

| Aspect | Problem 46 (Permutations) | Problem 47 (Permutations II) |
|---|---|---|
| **Input** | All elements are **unique** | Elements may have **duplicates** |
| **Output** | All n! permutations | Only **unique** permutations |
| **Sorting** | Not needed | **Required** (groups duplicates) |
| **Skip condition** | None | `nums[i] == nums[i-1] and !used[i-1]` |
| **Tracking** | `used[]` array or swap-based | Must use `used[]` array (swap doesn't support skip) |
| **Result count** | Always n! | n! / (k1! * k2! * ... * km!) |
| **Core change** | N/A | Add **2 lines**: sort + skip condition |

### Code difference (Python)

```python
# Problem 46 — no sort, no skip
def permute(self, nums):
    result = []
    used = [False] * len(nums)
    def backtrack(path):
        if len(path) == len(nums):
            result.append(path[:])
            return
        for i in range(len(nums)):
            if used[i]: continue
            # No skip condition needed
            used[i] = True
            path.append(nums[i])
            backtrack(path)
            path.pop()
            used[i] = False
    backtrack([])
    return result

# Problem 47 — sort + skip (2 extra lines)
def permuteUnique(self, nums):
    nums.sort()                                              # +1: sort
    result = []
    used = [False] * len(nums)
    def backtrack(path):
        if len(path) == len(nums):
            result.append(path[:])
            return
        for i in range(len(nums)):
            if used[i]: continue
            if i > 0 and nums[i] == nums[i-1] and not used[i-1]: continue  # +2: skip
            used[i] = True
            path.append(nums[i])
            backtrack(path)
            path.pop()
            used[i] = False
    backtrack([])
    return result
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Same problem without duplicates |
| 2 | [31. Next Permutation](https://leetcode.com/problems/next-permutation/) | :yellow_circle: Medium | Generates the next lexicographic permutation |
| 3 | [40. Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) | :yellow_circle: Medium | Same duplicate-skipping technique for combinations |
| 4 | [90. Subsets II](https://leetcode.com/problems/subsets-ii/) | :yellow_circle: Medium | Same sort + skip pattern for subsets with duplicates |
| 5 | [267. Palindrome Permutation II](https://leetcode.com/problems/palindrome-permutation-ii/) | :yellow_circle: Medium | Permutations with palindrome constraint |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Visualizes the **backtracking tree** with duplicate pruning
> - **Green** nodes = valid placements, **Red** nodes = pruned duplicates
> - Step-by-step or auto-play with adjustable speed
> - Shows the `used[]` array, current `path`, and collected results
