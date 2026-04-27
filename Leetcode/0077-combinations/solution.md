# 0077. Combinations

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking](#approach-1-backtracking)
4. [Approach 2: Backtracking with Pruning](#approach-2-backtracking-with-pruning)
5. [Approach 3: Iterative (Lex-Order Combinations)](#approach-3-iterative-lex-order-combinations)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [77. Combinations](https://leetcode.com/problems/combinations/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Backtracking` |

### Description

> Given two integers `n` and `k`, return *all possible combinations of `k` numbers chosen from the range `[1, n]`*.
>
> You may return the answer in any order.

### Examples

```
Example 1:
Input: n = 4, k = 2
Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]

Example 2:
Input: n = 1, k = 1
Output: [[1]]
```

### Constraints

- `1 <= n <= 20`
- `1 <= k <= n`

---

## Problem Breakdown

### 1. What is being asked?

Enumerate every k-element subset of `{1, 2, ..., n}`. There are `C(n, k)` such subsets.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Upper bound of range |
| `k` | `int` | Size of each combination |

### 3. What is the output?

A list of `C(n, k)` lists, each with `k` distinct ascending numbers.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 20`, `k <= n` | C(20, 10) = 184,756. Backtracking is fine |

### 5. Step-by-step example analysis

#### `n = 4, k = 2`

```text
Backtrack(start=1, current=[]):
  pick 1 → Backtrack(start=2, current=[1]):
    pick 2 → emit [1,2]
    pick 3 → emit [1,3]
    pick 4 → emit [1,4]
  pick 2 → Backtrack(start=3, current=[2]):
    pick 3 → emit [2,3]
    pick 4 → emit [2,4]
  pick 3 → Backtrack(start=4, current=[3]):
    pick 4 → emit [3,4]
  pick 4 → Backtrack(start=5, current=[4]):
    can't pick more (start > n) → return
Result: 6 combinations.
```

### 6. Key Observations

1. **Ascending order avoids duplicates** -- We always pick a value greater than the previous, eliminating reordered duplicates.
2. **Pruning** -- We need `k - len(current)` more values; the smallest valid `start` is `start <= n - (k - len(current)) + 1`. Beyond that, we cannot fill `current`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Backtracking | Tree of choices, prune via ordering |

**Chosen pattern:** `Backtracking with Pruning`.

---

## Approach 1: Backtracking

### Algorithm (step-by-step)

1. `backtrack(start, current)`:
   - If `len(current) == k`: append a copy of `current` to result, return.
   - For `v` from `start` to `n`:
     - Append `v` to `current`.
     - Recurse on `(v + 1, current)`.
     - Pop.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(C(n, k) * k) |
| **Space** | O(k) recursion |

### Implementation

#### Python

```python
class Solution:
    def combineNoPrune(self, n: int, k: int) -> List[List[int]]:
        result = []
        cur = []
        def bt(start: int):
            if len(cur) == k:
                result.append(cur.copy())
                return
            for v in range(start, n + 1):
                cur.append(v)
                bt(v + 1)
                cur.pop()
        bt(1)
        return result
```

---

## Approach 2: Backtracking with Pruning

### Idea

> If we have already chosen `len(current)` items, we still need `k - len(current)` more. The largest possible `start` we can take is `n - (k - len(current)) + 1` because after picking `start` we still need `k - len(current) - 1` items, all from `start+1..n`.

### Algorithm

```text
def bt(start):
    if len(cur) == k:
        emit and return
    need = k - len(cur)
    for v in start .. n - need + 1:
        cur.push(v); bt(v + 1); cur.pop()
```

### Complexity

| | Complexity |
|---|---|
| **Time** | O(C(n, k) * k) |
| **Space** | O(k) |

### Implementation

#### Go

```go
func combine(n int, k int) [][]int {
    result := [][]int{}
    cur := []int{}
    var bt func(start int)
    bt = func(start int) {
        if len(cur) == k {
            cp := make([]int, k)
            copy(cp, cur)
            result = append(result, cp)
            return
        }
        need := k - len(cur)
        for v := start; v <= n-need+1; v++ {
            cur = append(cur, v)
            bt(v + 1)
            cur = cur[:len(cur)-1]
        }
    }
    bt(1)
    return result
}
```

#### Java

```java
class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        bt(1, n, k, cur, result);
        return result;
    }

    private void bt(int start, int n, int k, List<Integer> cur, List<List<Integer>> result) {
        if (cur.size() == k) {
            result.add(new ArrayList<>(cur));
            return;
        }
        int need = k - cur.size();
        for (int v = start; v <= n - need + 1; v++) {
            cur.add(v);
            bt(v + 1, n, k, cur, result);
            cur.remove(cur.size() - 1);
        }
    }
}
```

#### Python

```python
class Solution:
    def combine(self, n: int, k: int) -> List[List[int]]:
        result = []
        cur = []
        def bt(start: int):
            if len(cur) == k:
                result.append(cur.copy())
                return
            need = k - len(cur)
            for v in range(start, n - need + 2):
                cur.append(v)
                bt(v + 1)
                cur.pop()
        bt(1)
        return result
```

### Dry Run

```text
n = 4, k = 2
bt(start=1):
  need = 2, range = 1..3 (inclusive of 3 because n - need + 1 = 3)
  v = 1: bt(2):
    need = 1, range = 2..4
    v = 2 → emit [1,2]
    v = 3 → emit [1,3]
    v = 4 → emit [1,4]
  v = 2: bt(3):
    range = 3..4
    v = 3 → [2,3]; v = 4 → [2,4]
  v = 3: bt(4):
    range = 4..4
    v = 4 → [3,4]
```

---

## Approach 3: Iterative (Lex-Order Combinations)

### Idea

> Maintain an array `cur = [1, 2, ..., k]`. To advance to the next combination in lexicographic order, find the rightmost index `i` such that `cur[i] < n - k + 1 + i`, increment it, and reset everything to its right.

> Beautiful but harder to derive than backtracking.

### Implementation

#### Python

```python
class Solution:
    def combineIter(self, n: int, k: int) -> List[List[int]]:
        cur = list(range(1, k + 1))
        result = [cur.copy()]
        while True:
            i = k - 1
            while i >= 0 and cur[i] == n - k + 1 + i:
                i -= 1
            if i < 0: break
            cur[i] += 1
            for j in range(i + 1, k):
                cur[j] = cur[j - 1] + 1
            result.append(cur.copy())
        return result
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking | O(C(n,k) * k) | O(k) | Simple | No early stop |
| 2 | Backtracking + Pruning | O(C(n,k) * k) | O(k) | Faster in practice | -- |
| 3 | Iterative | O(C(n,k) * k) | O(k) | No recursion | Harder to grok |

### Which solution to choose?

Approach 2 in interviews; Approach 1 if simplicity is preferred.

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | `k == n` | Single combination = all numbers |
| 2 | `k == 1` | n single-element lists |
| 3 | `n == 1, k == 1` | `[[1]]` |
| 4 | Large `n` and `k = n/2` | Maximum number of combinations |

---

## Common Mistakes

### Mistake 1: Forgetting to copy `cur` when emitting

```python
# WRONG — reference shared, all entries become equal at the end
result.append(cur)

# CORRECT — emit a snapshot
result.append(cur.copy())
```

**Reason:** `cur` mutates throughout backtracking; we need its current snapshot.

### Mistake 2: Wrong upper bound when pruning

```python
# WRONG — loop goes one step too far
for v in range(start, n - need + 1):   # exclusive end skips n - need + 1

# CORRECT — Python range upper exclusive: use n - need + 2
for v in range(start, n - need + 2):
```

**Reason:** Python's `range(a, b)` excludes `b`; we need to include `n - need + 1`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [78. Subsets](https://leetcode.com/problems/subsets/) | :yellow_circle: Medium | All subsets |
| 2 | [39. Combination Sum](https://leetcode.com/problems/combination-sum/) | :yellow_circle: Medium | Sum target |
| 3 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Order matters |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Number row with chosen elements highlighted
> - Backtracking tree depth indicator
> - Live list of generated combinations
