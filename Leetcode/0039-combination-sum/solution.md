# 0039. Combination Sum

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking](#approach-1-backtracking)
4. [Complexity Comparison](#complexity-comparison)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Related Problems](#related-problems)
8. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [39. Combination Sum](https://leetcode.com/problems/combination-sum/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking` |

### Description

> Given an array of **distinct** integers `candidates` and a target integer `target`, return a list of all **unique combinations** of `candidates` where the chosen numbers sum to `target`. You may return the combinations in **any order**.
>
> The **same** number may be chosen from `candidates` an **unlimited number of times**. Two combinations are unique if the frequency of at least one of the chosen numbers is different.

### Examples

```
Example 1:
Input: candidates = [2,3,6,7], target = 7
Output: [[2,2,3],[7]]
Explanation:
  2 + 2 + 3 = 7 ✓
  7 = 7 ✓
  These are the only two combinations.

Example 2:
Input: candidates = [2,3,5], target = 8
Output: [[2,2,2,2],[2,3,3],[3,5]]

Example 3:
Input: candidates = [2], target = 1
Output: []
```

### Constraints

- `1 <= candidates.length <= 30`
- `2 <= candidates[i] <= 40`
- All elements of `candidates` are **distinct**
- `1 <= target <= 40`

---

## Problem Breakdown

### 1. What is being asked?

Find **all unique combinations** of numbers from `candidates` that add up to `target`. Each candidate can be reused any number of times. The key word is "all" — we must enumerate every valid combination, not just find one.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `candidates` | `int[]` | Array of distinct positive integers |
| `target` | `int` | Target sum to achieve |

Important observations about the input:
- All candidates are **distinct** (no duplicate values)
- All candidates are **positive** (at least 2)
- Target is small (at most 40)
- Candidates list is small (at most 30 elements)

### 3. What is the output?

- A **list of lists** — each inner list is a combination that sums to `target`
- Each combination is a **multiset** — order within a combination does not matter
- No duplicate combinations (e.g., `[2,2,3]` and `[2,3,2]` are the same)
- Return an empty list if no combinations exist

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `candidates` are distinct | No need to handle duplicate candidates |
| Values 2-40, target 1-40 | Maximum recursion depth is 20 (target 40 / min candidate 2) |
| Candidates reusable | Same element can appear multiple times in a combination |
| Length up to 30 | Need to avoid generating duplicate combos efficiently |

### 5. Step-by-step example analysis

#### Example 1: `candidates = [2,3,6,7], target = 7`

```text
Try combinations:
  Start with 2:
    2 -> remaining 5
      2,2 -> remaining 3
        2,2,2 -> remaining 1 (no candidate <= 1, dead end)
        2,2,3 -> remaining 0 ✓ → [2,2,3]
      2,3 -> remaining 2
        2,3,3 -> remaining -1 (overshoot, dead end)
      2,6 -> remaining -1 (overshoot)
      2,7 -> remaining -2 (overshoot)
    3 -> remaining 4
      3,3 -> remaining 1 (dead end)
      3,6 -> remaining -2 (overshoot)
    6 -> remaining 1 (dead end)
    7 -> remaining 0 ✓ → [7]

Result: [[2,2,3], [7]]
```

### 6. Key Observations

1. **Backtracking is the natural fit** — we build combinations incrementally, exploring all branches.
2. **To avoid duplicates**, at each step we only consider candidates at index `>= start`. This ensures we never generate `[3,2]` after `[2,3]`.
3. **A candidate can be reused** — so when we pick `candidates[i]`, we recurse with `start = i` (not `i+1`).
4. **Pruning:** If the current candidate exceeds the remaining target, skip it. Sorting candidates first makes this pruning more effective — once a candidate is too large, all subsequent ones are too.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Build combinations incrementally, explore all valid branches | Pick/skip each candidate, track remaining sum |
| Pruning | Cut branches early when sum exceeds target | Sort candidates, break when `candidate > remaining` |

**Chosen pattern:** `Backtracking with pruning`
**Reason:** Naturally enumerates all combinations; pruning avoids unnecessary exploration. The start-index technique elegantly prevents duplicates.

---

## Approach 1: Backtracking

### Thought process

> At each recursive call, we have a remaining target and a starting index. We iterate through candidates from the starting index onward. For each candidate that does not exceed the remaining target, we add it to the current combination and recurse with the updated remaining target. The starting index stays the same (allowing reuse of the same candidate). When the remaining target reaches 0, we found a valid combination.

### Algorithm (step-by-step)

1. Sort `candidates` (enables early termination via pruning)
2. Define a recursive `backtrack(start, remaining, current)` function:
   - If `remaining == 0` -> add a copy of `current` to result (base case)
   - For `i` from `start` to end of `candidates`:
     - If `candidates[i] > remaining` -> break (pruning, since array is sorted)
     - Add `candidates[i]` to `current`
     - Recurse with `start = i` and `remaining - candidates[i]`
     - Remove last element from `current` (backtrack)
3. Call `backtrack(0, target, [])` and return the result

### Pseudocode

```text
function combinationSum(candidates, target):
    sort(candidates)
    result = []

    function backtrack(start, remaining, current):
        if remaining == 0:
            result.append(copy of current)
            return
        for i from start to len(candidates) - 1:
            if candidates[i] > remaining:
                break                         // pruning
            current.append(candidates[i])
            backtrack(i, remaining - candidates[i], current)
            current.pop()                     // backtrack

    backtrack(0, target, [])
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^(T/M)) | n = number of candidates, T = target, M = minimum candidate value. In the worst case, the branching factor is n and the depth is T/M |
| **Space** | O(T/M) | Maximum recursion depth is T/M (longest combination uses the smallest candidate repeatedly) |

### Implementation

#### Go

```go
func combinationSum(candidates []int, target int) [][]int {
    sort.Ints(candidates)
    result := [][]int{}

    var backtrack func(start, remaining int, current []int)
    backtrack = func(start, remaining int, current []int) {
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
            current = append(current, candidates[i])
            backtrack(i, remaining-candidates[i], current)
            current = current[:len(current)-1]
        }
    }

    backtrack(0, target, []int{})
    return result
}
```

#### Java

```java
public List<List<Integer>> combinationSum(int[] candidates, int target) {
    Arrays.sort(candidates);
    List<List<Integer>> result = new ArrayList<>();
    backtrack(candidates, 0, target, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] candidates, int start, int remaining,
                       List<Integer> current, List<List<Integer>> result) {
    if (remaining == 0) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > remaining) break;
        current.add(candidates[i]);
        backtrack(candidates, i, remaining - candidates[i], current, result);
        current.remove(current.size() - 1);
    }
}
```

#### Python

```python
def combinationSum(self, candidates: list[int], target: int) -> list[list[int]]:
    candidates.sort()
    result = []

    def backtrack(start: int, remaining: int, current: list[int]) -> None:
        if remaining == 0:
            result.append(current[:])
            return
        for i in range(start, len(candidates)):
            if candidates[i] > remaining:
                break
            current.append(candidates[i])
            backtrack(i, remaining - candidates[i], current)
            current.pop()

    backtrack(0, target, [])
    return result
```

### Dry Run

```text
Input: candidates = [2,3,6,7], target = 7
After sort: [2,3,6,7]

backtrack(0, 7, []):
  i=0, pick 2, current=[2], remaining=5
    backtrack(0, 5, [2]):
      i=0, pick 2, current=[2,2], remaining=3
        backtrack(0, 3, [2,2]):
          i=0, pick 2, current=[2,2,2], remaining=1
            backtrack(0, 1, [2,2,2]):
              i=0, candidates[0]=2 > 1 → break
            pop → [2,2]
          i=1, pick 3, current=[2,2,3], remaining=0
            backtrack(1, 0, [2,2,3]):
              remaining==0 → add [2,2,3] to result ✓
            pop → [2,2]
          i=2, candidates[2]=6 > 3 → break
        pop → [2]
      i=1, pick 3, current=[2,3], remaining=2
        backtrack(1, 2, [2,3]):
          i=1, candidates[1]=3 > 2 → break
        pop → [2]
      i=2, pick 6, current=[2,6], remaining=-1? No: 6 > 5 → break
    pop → []
  i=1, pick 3, current=[3], remaining=4
    backtrack(1, 4, [3]):
      i=1, pick 3, current=[3,3], remaining=1
        backtrack(1, 1, [3,3]):
          i=1, candidates[1]=3 > 1 → break
        pop → [3]
      i=2, candidates[2]=6 > 4 → break
    pop → []
  i=2, pick 6, current=[6], remaining=1
    backtrack(2, 1, [6]):
      i=2, candidates[2]=6 > 1 → break
    pop → []
  i=3, pick 7, current=[7], remaining=0
    backtrack(3, 0, [7]):
      remaining==0 → add [7] to result ✓
    pop → []

Result: [[2,2,3], [7]]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking with pruning | O(n^(T/M)) | O(T/M) | Prunes early, no duplicates, clean code | Exponential in worst case |

### Which solution to choose?

- **In an interview:** Backtracking with sorting and pruning — demonstrates understanding of recursion, duplicate avoidance, and optimization
- **In production:** Same approach — the constraints are small (target <= 40)
- **On Leetcode:** Passes easily with sorting + pruning
- **For learning:** Focus on understanding why `start = i` (not `i+1`) allows reuse, and why iterating from `start` avoids duplicates

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | LeetCode Example 1 | `[2,3,6,7], 7` | `[[2,2,3],[7]]` | Standard case with multiple combinations |
| 2 | LeetCode Example 2 | `[2,3,5], 8` | `[[2,2,2,2],[2,3,3],[3,5]]` | Multiple combinations, candidate reuse |
| 3 | No valid combination | `[2], 1` | `[]` | Smallest candidate exceeds target |
| 4 | Single candidate = target | `[7], 7` | `[[7]]` | Exact match |
| 5 | Single candidate, multiple use | `[3], 9` | `[[3,3,3]]` | Same candidate used 3 times |
| 6 | Large candidate set | `[2,3,5], 8` | `[[2,2,2,2],[2,3,3],[3,5]]` | Tests pruning effectiveness |
| 7 | Target equals smallest candidate | `[2,3,5], 2` | `[[2]]` | Only one valid combination |
| 8 | All candidates too large | `[5,6,7], 3` | `[]` | No candidate fits |

---

## Common Mistakes

### Mistake 1: Not using a start index (generates duplicates)

```python
# WRONG — generates [2,3] and [3,2] as separate combinations
def backtrack(remaining, current):
    if remaining == 0:
        result.append(current[:])
        return
    for c in candidates:
        if c <= remaining:
            current.append(c)
            backtrack(remaining - c, current)
            current.pop()

# CORRECT — start index ensures we only pick candidates at index >= start
def backtrack(start, remaining, current):
    if remaining == 0:
        result.append(current[:])
        return
    for i in range(start, len(candidates)):
        if candidates[i] > remaining:
            break
        current.append(candidates[i])
        backtrack(i, remaining - candidates[i], current)
        current.pop()
```

**Reason:** Without a start index, the algorithm explores all permutations instead of combinations, producing duplicates like `[2,3]` and `[3,2]`.

### Mistake 2: Using `i+1` instead of `i` (disallows reuse)

```python
# WRONG — moves to next candidate, so each candidate used at most once
backtrack(i + 1, remaining - candidates[i], current)

# CORRECT — stays at same index, allowing unlimited reuse
backtrack(i, remaining - candidates[i], current)
```

**Reason:** The problem states the same number may be chosen unlimited times. Using `i` instead of `i+1` allows the same candidate to be picked again.

### Mistake 3: Forgetting to copy `current` before adding to result

```python
# WRONG — adds a reference; current will be empty after backtracking completes
result.append(current)

# CORRECT — add a copy
result.append(current[:])
# or: result.append(list(current))
```

**Reason:** `current` is modified in place during backtracking. Without copying, all entries in `result` would reference the same (eventually empty) list.

### Mistake 4: Not sorting candidates (misses pruning opportunity)

```python
# SLOWER — cannot break early; must check every candidate
for i in range(start, len(candidates)):
    if candidates[i] <= remaining:  # skip instead of break
        ...

# FASTER — sort first, then break when candidate exceeds remaining
candidates.sort()
for i in range(start, len(candidates)):
    if candidates[i] > remaining:
        break  # all subsequent candidates are also too large
    ...
```

**Reason:** Sorting allows early termination. Without sorting, we must check every candidate even when most exceed the remaining target.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [40. Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) | :yellow_circle: Medium | Same problem but each candidate used at most once |
| 2 | [216. Combination Sum III](https://leetcode.com/problems/combination-sum-iii/) | :yellow_circle: Medium | Fixed-size combinations summing to target |
| 3 | [377. Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | :yellow_circle: Medium | Count permutations (not combinations) summing to target |
| 4 | [77. Combinations](https://leetcode.com/problems/combinations/) | :yellow_circle: Medium | Generate all combinations of k elements |
| 5 | [17. Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) | :yellow_circle: Medium | Backtracking to generate all combinations |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Backtracking tree** shows the decision tree with candidate selection at each node
> - **Sum tracking** displays the current sum and remaining target at each step
> - **Step/Play/Reset** controls with adjustable speed
> - **Presets** for quick input selection
> - **Log** panel shows step-by-step execution details
> - **Result** panel collects valid combinations as they are found
