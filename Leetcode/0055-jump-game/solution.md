# 0055. Jump Game

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking (Brute Force)](#approach-1-backtracking-brute-force)
4. [Approach 2: Top-Down DP with Memoization](#approach-2-top-down-dp-with-memoization)
5. [Approach 3: Bottom-Up DP](#approach-3-bottom-up-dp)
6. [Approach 4: Greedy (Optimal)](#approach-4-greedy-optimal)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [55. Jump Game](https://leetcode.com/problems/jump-game/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Dynamic Programming`, `Greedy` |

### Description

> You are given an integer array `nums`. You are initially positioned at the array's **first index**, and each element in the array represents your maximum jump length at that position.
>
> Return `true` if you can reach the **last index**, or `false` otherwise.

### Examples

```
Example 1:
Input: nums = [2,3,1,1,4]
Output: true
Explanation: Jump 1 step from index 0 to 1, then 3 steps to the last index.

Example 2:
Input: nums = [3,2,1,0,4]
Output: false
Explanation: You will always arrive at index 3 no matter what. Its maximum jump length is 0,
which makes it impossible to reach the last index.
```

### Constraints

- `1 <= nums.length <= 10^4`
- `0 <= nums[i] <= 10^5`

---

## Problem Breakdown

### 1. What is being asked?

We start at index `0`. From any index `i`, we can step to any index `j` with `i < j <= i + nums[i]`. The question is whether some sequence of such moves reaches index `n - 1`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Non-negative jump lengths |

Important observations about the input:
- All values are non-negative -- there is no backward movement
- A `0` is a "dead zone" -- we cannot move from it
- The first index is the start; the last index is the target

### 3. What is the output?

`true` if we can reach the last index, `false` otherwise.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^4` | O(n^2) is allowed, but O(n) is far more comfortable |
| `nums[i] <= 10^5` | Values can exceed `n`; the jump length is just an upper bound |
| Single test only | Greedy works in one pass |

### 5. Step-by-step example analysis

#### Example 1: `nums = [2, 3, 1, 1, 4]`

```text
Greedy walk:
  i=0: at index 0, can reach up to 0 + 2 = 2.    farthest = 2
  i=1: i <= farthest, can reach 1 + 3 = 4.       farthest = 4
  i=2: i <= farthest, can reach 2 + 1 = 3.       farthest = 4
  i=3: i <= farthest, can reach 3 + 1 = 4.       farthest = 4
  i=4: i <= farthest, target reached.            return true
```

#### Example 2: `nums = [3, 2, 1, 0, 4]`

```text
Greedy walk:
  i=0: farthest = 0 + 3 = 3
  i=1: farthest = max(3, 1 + 2) = 3
  i=2: farthest = max(3, 2 + 1) = 3
  i=3: farthest = max(3, 3 + 0) = 3
  i=4: i = 4 > farthest = 3 → unreachable, return false
```

### 6. Key Observations

1. **Reachability is monotone in `i`** -- if we can reach index `i` and `i + nums[i] >= j`, then we can reach `j`. We never need to track *which* path reached an index; only "is it reachable".
2. **Greedy farthest-reach** -- Maintain the maximum index reachable so far. As long as the current index is within reach, update the maximum. If we ever step beyond the maximum, we are stuck.
3. **A `0` is fatal only if it is the last point of reach** -- A zero somewhere in the middle is fine if we can jump over it.
4. **The maximum trivially survives if `nums[0] = 0` and `n > 1`** is false: we cannot move, so unreachable.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Greedy | Local choice (extend reach) leads to global optimum | This problem (Approach 4) |
| Dynamic Programming | Define `reachable(i)` recursively | This problem (Approaches 2, 3) |
| Backtracking | Try every jump from each index | This problem (Approach 1, TLE) |

**Chosen pattern:** `Greedy`
**Reason:** O(n) time, O(1) space, four lines of code, easy to argue correctness.

---

## Approach 1: Backtracking (Brute Force)

### Thought process

> From index `i`, try every legal jump length `j = 1..nums[i]`, recursing into `i + j`. Return true if any path reaches the end.

### Algorithm (step-by-step)

1. If `i >= n - 1`, return true.
2. For each `j` in `1..nums[i]`, recurse into `i + j`.
3. If any recursion returns true, return true. Otherwise return false.

### Pseudocode

```text
function canJump(i):
    if i >= n - 1: return true
    furthest = min(i + nums[i], n - 1)
    for j = i + 1 to furthest:
        if canJump(j): return true
    return false
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(2^n) | Exponential branching factor |
| **Space** | O(n) | Recursion stack |

> Will TLE for `n > ~25`. Useful only for understanding.

### Implementation

#### Go

```go
func canJumpBrute(nums []int) bool {
    var rec func(i int) bool
    rec = func(i int) bool {
        if i >= len(nums)-1 {
            return true
        }
        furthest := i + nums[i]
        if furthest >= len(nums)-1 {
            furthest = len(nums) - 1
        }
        for j := i + 1; j <= furthest; j++ {
            if rec(j) {
                return true
            }
        }
        return false
    }
    return rec(0)
}
```

#### Java

```java
class Solution {
    public boolean canJumpBrute(int[] nums) {
        return rec(nums, 0);
    }
    private boolean rec(int[] nums, int i) {
        if (i >= nums.length - 1) return true;
        int furthest = Math.min(i + nums[i], nums.length - 1);
        for (int j = i + 1; j <= furthest; j++) {
            if (rec(nums, j)) return true;
        }
        return false;
    }
}
```

#### Python

```python
class Solution:
    def canJumpBrute(self, nums: List[int]) -> bool:
        n = len(nums)
        def rec(i: int) -> bool:
            if i >= n - 1: return True
            furthest = min(i + nums[i], n - 1)
            for j in range(i + 1, furthest + 1):
                if rec(j): return True
            return False
        return rec(0)
```

### Dry Run

```text
Input: [2,3,1,1,4]

rec(0):
  furthest = 2
  rec(1):
    furthest = 4 → return true (since 4 >= n-1)
  return true
```

---

## Approach 2: Top-Down DP with Memoization

### The problem with Approach 1

> Approach 1 explores the same subproblems repeatedly. Memoize the result of `canJump(i)` in a cache.

### Optimization idea

> Cache `dp[i] ∈ {unknown, good, bad}`. The first time we ask "can we reach the end from `i`?" we compute it; subsequent queries are O(1).

### Algorithm (step-by-step)

1. Allocate `dp` of size `n` initialized to `unknown`.
2. `solve(i)`:
   - If `i >= n - 1`: return true (and mark `dp[i] = good`).
   - If `dp[i] != unknown`: return `dp[i] == good`.
   - For `j = i + 1..i + nums[i]`, if `solve(j)` returns true, mark `dp[i] = good` and return true.
   - Otherwise mark `dp[i] = bad` and return false.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Each `dp[i]` computed once, takes O(nums[i]) work |
| **Space** | O(n) | DP cache + recursion |

### Implementation

#### Go

```go
func canJumpMemo(nums []int) bool {
    n := len(nums)
    dp := make([]int8, n) // 0 = unknown, 1 = good, -1 = bad
    var solve func(i int) bool
    solve = func(i int) bool {
        if i >= n-1 {
            return true
        }
        if dp[i] != 0 {
            return dp[i] == 1
        }
        furthest := i + nums[i]
        if furthest >= n-1 {
            furthest = n - 1
        }
        for j := i + 1; j <= furthest; j++ {
            if solve(j) {
                dp[i] = 1
                return true
            }
        }
        dp[i] = -1
        return false
    }
    return solve(0)
}
```

#### Java

```java
class Solution {
    public boolean canJumpMemo(int[] nums) {
        int n = nums.length;
        byte[] dp = new byte[n]; // 0 unknown, 1 good, -1 bad
        return solveMemo(nums, dp, 0);
    }
    private boolean solveMemo(int[] nums, byte[] dp, int i) {
        int n = nums.length;
        if (i >= n - 1) return true;
        if (dp[i] != 0) return dp[i] == 1;
        int furthest = Math.min(i + nums[i], n - 1);
        for (int j = i + 1; j <= furthest; j++) {
            if (solveMemo(nums, dp, j)) {
                dp[i] = 1;
                return true;
            }
        }
        dp[i] = -1;
        return false;
    }
}
```

#### Python

```python
class Solution:
    def canJumpMemo(self, nums: List[int]) -> bool:
        n = len(nums)
        dp = [0] * n  # 0 unknown, 1 good, -1 bad

        def solve(i: int) -> bool:
            if i >= n - 1: return True
            if dp[i] != 0: return dp[i] == 1
            furthest = min(i + nums[i], n - 1)
            for j in range(i + 1, furthest + 1):
                if solve(j):
                    dp[i] = 1
                    return True
            dp[i] = -1
            return False

        return solve(0)
```

---

## Approach 3: Bottom-Up DP

### Idea

> Reverse the recurrence. `dp[i] = true` iff some `j > i` with `j - i <= nums[i]` has `dp[j] = true`. The base case is `dp[n - 1] = true`. Iterate from right to left and short-circuit.

### Algorithm (step-by-step)

1. Set `lastGood = n - 1`.
2. For `i` from `n - 2` down to `0`:
   - If `i + nums[i] >= lastGood`, set `lastGood = i`.
3. Return `lastGood == 0`.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single backward pass with O(1) checks |
| **Space** | O(1) | Only `lastGood` |

> This is essentially Approach 4 from the right -- a clean way to derive the greedy.

### Implementation

#### Go

```go
func canJumpDP(nums []int) bool {
    n := len(nums)
    lastGood := n - 1
    for i := n - 2; i >= 0; i-- {
        if i+nums[i] >= lastGood {
            lastGood = i
        }
    }
    return lastGood == 0
}
```

#### Java

```java
class Solution {
    public boolean canJumpDP(int[] nums) {
        int n = nums.length;
        int lastGood = n - 1;
        for (int i = n - 2; i >= 0; i--) {
            if (i + nums[i] >= lastGood) lastGood = i;
        }
        return lastGood == 0;
    }
}
```

#### Python

```python
class Solution:
    def canJumpDP(self, nums: List[int]) -> bool:
        n = len(nums)
        last_good = n - 1
        for i in range(n - 2, -1, -1):
            if i + nums[i] >= last_good:
                last_good = i
        return last_good == 0
```

---

## Approach 4: Greedy (Optimal)

### Idea

> Sweep left to right. Maintain `farthest`, the maximum index reachable from any cell visited so far. Update `farthest = max(farthest, i + nums[i])`. If `i > farthest`, we cannot proceed and return `false`. If `farthest >= n - 1` we win.

### Algorithm (step-by-step)

1. Set `farthest = 0`.
2. For `i = 0..n-1`:
   - If `i > farthest`: return `false`.
   - `farthest = max(farthest, i + nums[i])`.
   - If `farthest >= n - 1`: return `true` (early exit).
3. Return `true`.

### Pseudocode

```text
farthest = 0
for i in 0..n-1:
    if i > farthest: return false
    farthest = max(farthest, i + nums[i])
    if farthest >= n-1: return true
return true
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | One pass |
| **Space** | O(1) | Single variable |

### Implementation

#### Go

```go
func canJump(nums []int) bool {
    farthest := 0
    for i := 0; i < len(nums); i++ {
        if i > farthest {
            return false
        }
        if i+nums[i] > farthest {
            farthest = i + nums[i]
        }
        if farthest >= len(nums)-1 {
            return true
        }
    }
    return true
}
```

#### Java

```java
class Solution {
    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
            if (farthest >= nums.length - 1) return true;
        }
        return true;
    }
}
```

#### Python

```python
class Solution:
    def canJump(self, nums: List[int]) -> bool:
        farthest = 0
        for i, x in enumerate(nums):
            if i > farthest: return False
            farthest = max(farthest, i + x)
            if farthest >= len(nums) - 1: return True
        return True
```

### Dry Run

```text
Input: [3,2,1,0,4]
n=5, target=4

i=0: i=0 <= farthest=0; farthest = max(0, 0+3) = 3; 3 < 4 continue
i=1: 1 <= 3;             farthest = max(3, 1+2) = 3
i=2: 2 <= 3;             farthest = max(3, 2+1) = 3
i=3: 3 <= 3;             farthest = max(3, 3+0) = 3
i=4: 4 > 3 → return false
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking | O(2^n) | O(n) | Simple, useful for understanding | Exponential, TLE |
| 2 | Top-Down DP | O(n^2) | O(n) | Memoized, easy to derive | Quadratic, recursion |
| 3 | Bottom-Up DP | O(n) | O(1) | Clean derivation of greedy | Reads right-to-left |
| 4 | Greedy | O(n) | O(1) | Optimal in time and space | Slightly clever to argue |

### Which solution to choose?

- **In an interview:** Approach 4 (greedy) -- simplest optimal answer
- **In production:** Approach 4
- **On Leetcode:** Approach 4
- **For learning:** Walk through Backtracking → Memo → DP → Greedy to internalize the derivation

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single element | `[0]` | `true` | Already at the last index |
| 2 | Single non-zero | `[5]` | `true` | Already at the last index |
| 3 | First is zero, n>1 | `[0, 1]` | `false` | Cannot move |
| 4 | Reachable | `[2, 3, 1, 1, 4]` | `true` | Standard yes case |
| 5 | Blocked by zero | `[3, 2, 1, 0, 4]` | `false` | Hits zero one short |
| 6 | Big jump first | `[100, 0, 0, 0]` | `true` | One huge jump suffices |
| 7 | All ones | `[1,1,1,1,1]` | `true` | Linear walk |
| 8 | All zeros except first | `[1, 0]` | `true` | Reaches last from index 0 |
| 9 | Two zeros in row | `[2, 0, 0, 1]` | `false` | Stuck at index 2 |

---

## Common Mistakes

### Mistake 1: Returning `false` when seeing a zero

```python
# WRONG — a 0 mid-array is fine if we can jump over it
for x in nums:
    if x == 0: return False

# CORRECT — only fail if we cannot pass it
```

**Reason:** A `0` is only fatal when our `farthest` exactly equals that index. Otherwise we jump over it.

### Mistake 2: Updating `farthest` with `i + nums[i]` after the boundary check

```python
# WRONG — checks first, but using stale farthest
for i, x in enumerate(nums):
    if i > farthest: return False
    # forgot to update farthest!

# CORRECT
for i, x in enumerate(nums):
    if i > farthest: return False
    farthest = max(farthest, i + x)
```

**Reason:** Without updating, `farthest` stays at the initial value and even simple reachable inputs return false.

### Mistake 3: Inclusive vs exclusive comparison

```python
# WRONG — uses `>=` which fails when n == 1
if i >= farthest: return False

# CORRECT — strict greater-than
if i > farthest: return False
```

**Reason:** `farthest` is reachable, so `i == farthest` is fine. Strict `i > farthest` means we have stepped beyond.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [45. Jump Game II](https://leetcode.com/problems/jump-game-ii/) | :yellow_circle: Medium | Same setup, count minimum jumps |
| 2 | [1306. Jump Game III](https://leetcode.com/problems/jump-game-iii/) | :yellow_circle: Medium | Bidirectional jumps, BFS |
| 3 | [134. Gas Station](https://leetcode.com/problems/gas-station/) | :yellow_circle: Medium | Greedy farthest-reach pattern |
| 4 | [1696. Jump Game VI](https://leetcode.com/problems/jump-game-vi/) | :yellow_circle: Medium | Sliding-window DP with deque |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Bar chart with the `farthest` boundary highlighted
> - Arrows showing the maximum jump from each cell
> - Live tracking of the greedy reach as `i` advances
> - Comparison tab showing all four approaches side by side
