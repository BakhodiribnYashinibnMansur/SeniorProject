# 0040. Combination Sum II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking with Duplicate Skipping](#approach-1-backtracking-with-duplicate-skipping)
4. [Complexity Analysis](#complexity-analysis)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Comparison with Problem 39](#comparison-with-problem-39)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [40. Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking` |

### Description

> Given a collection of candidate numbers (`candidates`) and a target number (`target`), find all unique combinations in `candidates` where the candidate numbers sum to `target`.
>
> Each number in `candidates` may only be used **once** in the combination.
>
> **Note:** The solution set must not contain duplicate combinations.

### Examples

```
Example 1:
Input: candidates = [10,1,2,7,6,1,5], target = 8
Output: [[1,1,6],[1,2,5],[1,7],[2,6]]

Example 2:
Input: candidates = [2,5,2,1,2], target = 5
Output: [[1,2,2],[5]]
```

### Constraints

- `1 <= candidates.length <= 100`
- `1 <= candidates[i] <= 50`
- `1 <= target <= 30`

---

## Problem Breakdown

### 1. What is being asked?

Find **all unique combinations** of numbers from the input that sum to `target`. Each candidate number can only be used **once** (but duplicates in the input are separate numbers that can each be used once).

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `candidates` | `int[]` | Collection of candidate numbers (may contain duplicates) |
| `target` | `int` | Target sum to achieve |

Important observations about the input:
- The array is **unsorted** (not sorted)
- **Duplicates may exist** (Example 1: two `1` values)
- All values are **positive integers** (>= 1)
- Each element can be used **at most once**

### 3. What is the output?

- **A list of combinations** `[[a, b, ...], ...]`
- Each combination sums to `target`
- **No duplicate combinations** (e.g., `[1,2,5]` should appear only once even if there are two `1`s)
- Each element from `candidates` used at most once per combination
- Order of combinations and order within a combination do not matter
- Return an empty list if no valid combinations exist

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `candidates.length <= 100` | Backtracking is feasible, pruning keeps it efficient |
| `candidates[i] <= 50` | Small values — many combinations possible |
| `target <= 30` | Limits combination length, helps pruning |
| Duplicates in input | Must handle deduplication carefully |

### 5. Step-by-step example analysis

#### Example 1: `candidates = [10,1,2,7,6,1,5], target = 8`

```text
After sorting: [1, 1, 2, 5, 6, 7, 10]

Backtracking tree (simplified):
├── Pick 1 (first)
│   ├── Pick 1 (second) → remaining target: 6
│   │   ├── Pick 2 → remaining: 4, no further → ❌
│   │   ├── Pick 5 → remaining: 1, no further → ❌
│   │   ├── Pick 6 → remaining: 0 ✅ → [1,1,6]
│   │   └── Pick 7 → exceeds → ❌
│   ├── Skip 1 (duplicate at same level) → already handled above
│   ├── Pick 2 → remaining: 5
│   │   ├── Pick 5 → remaining: 0 ✅ → [1,2,5]
│   │   └── Pick 6 → exceeds → ❌
│   ├── Pick 5 → remaining: 2, no further → ❌
│   ├── Pick 6 → remaining: 1, no further → ❌
│   ├── Pick 7 → remaining: 0 ✅ → [1,7]
│   └── Pick 10 → exceeds → ❌
├── Skip 1 (second — duplicate at same recursion level)
├── Pick 2 → remaining: 6
│   ├── Pick 5 → remaining: 1, no further → ❌
│   ├── Pick 6 → remaining: 0 ✅ → [2,6]
│   └── Pick 7 → exceeds → ❌
├── Pick 5 → remaining: 3, no further → ❌
├── Pick 6 → remaining: 2, no further → ❌
├── Pick 7 → remaining: 1, no further → ❌
└── Pick 10 → exceeds → ❌

Result: [[1,1,6], [1,2,5], [1,7], [2,6]]
```

### 6. Key Observations

1. **Sorting enables duplicate skipping** — Identical values are adjacent, so at the same recursion level we can skip duplicates.
2. **Each element used once** — After picking `candidates[i]`, the next recursive call starts from `i + 1` (not `i` like in Problem 39).
3. **Pruning with sorted array** — If `candidates[i] > remaining target`, we can break (all subsequent candidates are even larger).
4. **Duplicate skipping rule** — At the same recursion level, if `candidates[i] == candidates[i-1]`, skip to avoid duplicate combinations.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Explore all valid subsets, prune invalid branches | Combination Sum problems |
| Sort + Skip | Deduplication of combinations | This problem (duplicates in input) |

**Chosen pattern:** `Backtracking with Duplicate Skipping`
**Reason:** Sorting makes duplicate detection trivial. Pruning (break when candidate > remaining) keeps the search efficient. Starting from `i + 1` ensures each element is used at most once.

---

## Approach 1: Backtracking with Duplicate Skipping

### Thought process

> 1. **Sort** candidates so duplicates are adjacent and pruning is possible.
> 2. Use **backtracking** to explore all subsets: at each level, try each remaining candidate.
> 3. **Start from `i + 1`** (not `i`) to ensure each element is used at most once.
> 4. **Skip duplicates** at the same recursion level: if `candidates[i] == candidates[i-1]` and `i > start`, skip.
> 5. **Prune** by breaking when `candidates[i] > remaining target` (sorted order guarantees all later values are larger).

### Algorithm (step-by-step)

1. Sort `candidates`
2. Initialize empty `result` list and empty `current` path
3. Call `backtrack(start=0, remaining=target)`:
   - If `remaining == 0` → add a copy of `current` to `result`, return
   - For `i` from `start` to end of `candidates`:
     - If `candidates[i] > remaining` → break (pruning)
     - If `i > start` and `candidates[i] == candidates[i-1]` → continue (skip duplicate)
     - Add `candidates[i]` to `current`
     - Recurse: `backtrack(i + 1, remaining - candidates[i])`
     - Remove last element from `current` (backtrack)
4. Return `result`

### Pseudocode

```text
function combinationSum2(candidates, target):
    sort(candidates)
    result = []
    current = []

    function backtrack(start, remaining):
        if remaining == 0:
            result.append(copy(current))
            return

        for i = start to len(candidates) - 1:
            if candidates[i] > remaining:
                break                          // pruning

            if i > start and candidates[i] == candidates[i-1]:
                continue                       // skip duplicate

            current.append(candidates[i])
            backtrack(i + 1, remaining - candidates[i])
            current.pop()                      // backtrack

    backtrack(0, target)
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(2^n) | In the worst case, we explore all subsets of `n` candidates |
| **Space** | O(n) | Recursion depth is at most `n`, plus the `current` path |

> Note: The actual runtime is much better than O(2^n) due to pruning and duplicate skipping. With `target <= 30` and `candidates[i] >= 1`, the practical depth is limited.

### Implementation

#### Go

```go
func combinationSum2(candidates []int, target int) [][]int {
    sort.Ints(candidates)
    result := [][]int{}
    current := []int{}

    var backtrack func(start, remaining int)
    backtrack = func(start, remaining int) {
        if remaining == 0 {
            combo := make([]int, len(current))
            copy(combo, current)
            result = append(result, combo)
            return
        }

        for i := start; i < len(candidates); i++ {
            if candidates[i] > remaining {
                break
            }
            if i > start && candidates[i] == candidates[i-1] {
                continue
            }
            current = append(current, candidates[i])
            backtrack(i+1, remaining-candidates[i])
            current = current[:len(current)-1]
        }
    }

    backtrack(0, target)
    return result
}
```

#### Java

```java
public List<List<Integer>> combinationSum2(int[] candidates, int target) {
    Arrays.sort(candidates);
    List<List<Integer>> result = new ArrayList<>();
    backtrack(candidates, target, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] candidates, int remaining, int start,
                        List<Integer> current, List<List<Integer>> result) {
    if (remaining == 0) {
        result.add(new ArrayList<>(current));
        return;
    }

    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > remaining) break;
        if (i > start && candidates[i] == candidates[i - 1]) continue;

        current.add(candidates[i]);
        backtrack(candidates, remaining - candidates[i], i + 1, current, result);
        current.remove(current.size() - 1);
    }
}
```

#### Python

```python
def combinationSum2(self, candidates: list[int], target: int) -> list[list[int]]:
    candidates.sort()
    result = []

    def backtrack(start: int, remaining: int, current: list[int]):
        if remaining == 0:
            result.append(current[:])
            return

        for i in range(start, len(candidates)):
            if candidates[i] > remaining:
                break
            if i > start and candidates[i] == candidates[i - 1]:
                continue

            current.append(candidates[i])
            backtrack(i + 1, remaining - candidates[i], current)
            current.pop()

    backtrack(0, target, [])
    return result
```

### Dry Run

```text
Input: candidates = [10,1,2,7,6,1,5], target = 8
After sorting: [1, 1, 2, 5, 6, 7, 10]

backtrack(start=0, remaining=8, current=[])
  i=0: pick 1, current=[1]
    backtrack(start=1, remaining=7, current=[1])
      i=1: pick 1, current=[1,1]
        backtrack(start=2, remaining=6, current=[1,1])
          i=2: pick 2, current=[1,1,2]
            backtrack(start=3, remaining=4, current=[1,1,2])
              i=3: 5 > 4 → BREAK
            pop → [1,1]
          i=3: pick 5, current=[1,1,5]
            backtrack(start=4, remaining=1, current=[1,1,5])
              i=4: 6 > 1 → BREAK
            pop → [1,1]
          i=4: pick 6, current=[1,1,6]
            backtrack(start=5, remaining=0, current=[1,1,6])
              remaining == 0 → FOUND ✅ [1,1,6]
            pop → [1,1]
          i=5: 7 > 6 → BREAK
        pop → [1]
      i=2: pick 2, current=[1,2]
        backtrack(start=3, remaining=5, current=[1,2])
          i=3: pick 5, current=[1,2,5]
            backtrack(start=4, remaining=0, current=[1,2,5])
              remaining == 0 → FOUND ✅ [1,2,5]
            pop → [1,2]
          i=4: 6 > 5 → BREAK
        pop → [1]
      i=3: pick 5, current=[1,5]
        backtrack(start=4, remaining=2, current=[1,5])
          i=4: 6 > 2 → BREAK
        pop → [1]
      i=4: pick 6, current=[1,6]
        backtrack(start=5, remaining=1, current=[1,6])
          i=5: 7 > 1 → BREAK
        pop → [1]
      i=5: pick 7, current=[1,7]
        backtrack(start=6, remaining=0, current=[1,7])
          remaining == 0 → FOUND ✅ [1,7]
        pop → [1]
      i=6: 10 > 7 → BREAK
    pop → []
  i=1: SKIP (i > start=0 and candidates[1]==candidates[0]==1) ← duplicate skipping!
  i=2: pick 2, current=[2]
    backtrack(start=3, remaining=6, current=[2])
      i=3: pick 5, current=[2,5]
        backtrack(start=4, remaining=1, current=[2,5])
          i=4: 6 > 1 → BREAK
        pop → [2]
      i=4: pick 6, current=[2,6]
        backtrack(start=5, remaining=0, current=[2,6])
          remaining == 0 → FOUND ✅ [2,6]
        pop → [2]
      i=5: 7 > 6 → BREAK
    pop → []
  i=3: pick 5, current=[5]
    backtrack(start=4, remaining=3, current=[5])
      i=4: 6 > 3 → BREAK
    pop → []
  i=4: pick 6, current=[6]
    backtrack(start=5, remaining=2, current=[6])
      i=5: 7 > 2 → BREAK
    pop → []
  i=5: pick 7, current=[7]
    backtrack(start=6, remaining=1, current=[7])
      i=6: 10 > 1 → BREAK
    pop → []
  i=6: 10 > 8 → BREAK

Result: [[1,1,6], [1,2,5], [1,7], [2,6]]
```

---

## Complexity Analysis

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(2^n) | Worst case: all subsets explored; practically much less due to pruning |
| **Space** | O(n) | Recursion stack depth + current path (output not counted) |

### Why pruning helps

- **Break on exceeding target:** Since candidates are sorted, once `candidates[i] > remaining`, all subsequent candidates are also too large.
- **Skip duplicates:** Avoids exploring identical branches, significantly reducing the search space when there are many duplicates.

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | LeetCode Example 1 | `[10,1,2,7,6,1,5], 8` | `[[1,1,6],[1,2,5],[1,7],[2,6]]` | Standard case with duplicates |
| 2 | LeetCode Example 2 | `[2,5,2,1,2], 5` | `[[1,2,2],[5]]` | Multiple duplicates |
| 3 | Single element match | `[1], 1` | `[[1]]` | Minimal valid input |
| 4 | Single element no match | `[2], 1` | `[]` | Element exceeds target |
| 5 | All same elements | `[1,1,1,1,1], 3` | `[[1,1,1]]` | Pick 3 out of 5 identical elements |
| 6 | No valid combination | `[3,5,7], 1` | `[]` | All candidates exceed target |
| 7 | Exact target with all | `[1,2,3], 6` | `[[1,2,3]]` | Use all elements |

---

## Common Mistakes

### Mistake 1: Starting from `start` instead of `i + 1` in recursion

```python
# ❌ WRONG — allows reusing the same element
backtrack(start, remaining - candidates[i], current)

# ✅ CORRECT — move to the next element
backtrack(i + 1, remaining - candidates[i], current)
```

**Reason:** Each element can only be used once. Starting from `i + 1` ensures we never revisit the same index.

### Mistake 2: Wrong duplicate skipping condition

```python
# ❌ WRONG — skips valid uses of duplicate values
if i > 0 and candidates[i] == candidates[i - 1]:
    continue

# ✅ CORRECT — only skip at the same recursion level
if i > start and candidates[i] == candidates[i - 1]:
    continue
```

**Reason:** Using `i > 0` would skip the second `1` even when we're deeper in the recursion and it's a valid choice. We should only skip when `i > start`, meaning we've already explored a branch with the same value at this level.

### Mistake 3: Not sorting the candidates

```python
# ❌ WRONG — duplicate skipping and pruning don't work
def combinationSum2(self, candidates, target):
    result = []
    def backtrack(start, remaining, current):
        # ...skip logic fails without sorting
    backtrack(0, target, [])

# ✅ CORRECT — sort first
def combinationSum2(self, candidates, target):
    candidates.sort()  # Essential!
    # ...
```

**Reason:** Duplicate skipping (`candidates[i] == candidates[i-1]`) only works when duplicates are adjacent (sorted). Pruning (`break` when too large) also requires sorted order.

### Mistake 4: Not copying `current` when adding to result

```python
# ❌ WRONG — all entries in result point to the same list
result.append(current)

# ✅ CORRECT — append a copy
result.append(current[:])
```

**Reason:** `current` is mutated during backtracking. Without copying, all result entries would reflect the final (empty) state of `current`.

---

## Comparison with Problem 39

| Aspect | Problem 39 (Combination Sum) | Problem 40 (Combination Sum II) |
|---|---|---|
| **Element reuse** | Each element can be used **unlimited times** | Each element can be used **at most once** |
| **Input duplicates** | All candidates are **distinct** | Candidates **may contain duplicates** |
| **Recursion start** | `backtrack(i, ...)` — stay at same index | `backtrack(i + 1, ...)` — move to next index |
| **Duplicate handling** | No duplicates in input, no special handling | Must skip duplicates at same recursion level |
| **Sorting purpose** | Optional (for pruning only) | Required (for both pruning and deduplication) |
| **Key line** | `backtrack(i, remaining - candidates[i])` | `if i > start and candidates[i] == candidates[i-1]: continue` |

### Key difference in code

```python
# Problem 39: can reuse same element
backtrack(i, remaining - candidates[i])

# Problem 40: move to next element + skip duplicates
if i > start and candidates[i] == candidates[i - 1]:
    continue
backtrack(i + 1, remaining - candidates[i])
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [39. Combination Sum](https://leetcode.com/problems/combination-sum/) | :yellow_circle: Medium | Same pattern but elements can be reused |
| 2 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Backtracking to generate all arrangements |
| 3 | [47. Permutations II](https://leetcode.com/problems/permutations-ii/) | :yellow_circle: Medium | Backtracking with duplicate handling |
| 4 | [78. Subsets](https://leetcode.com/problems/subsets/) | :yellow_circle: Medium | Backtracking to generate all subsets |
| 5 | [90. Subsets II](https://leetcode.com/problems/subsets-ii/) | :yellow_circle: Medium | Subsets with duplicate handling (same skip technique) |
| 6 | [216. Combination Sum III](https://leetcode.com/problems/combination-sum-iii/) | :yellow_circle: Medium | Fixed-size combinations summing to target |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Visualizes the **backtracking tree** with nodes for each recursive call
> - Shows **duplicate pruning** (skipped branches highlighted in red)
> - Shows **target pruning** (branches where candidate exceeds remaining)
> - Step-by-step log of the backtracking process
> - Presets for different inputs to explore
