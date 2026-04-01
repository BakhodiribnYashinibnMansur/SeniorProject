# 0045. Jump Game II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Greedy (BFS-like)](#approach-1-greedy-bfs-like)
4. [Approach 2: Dynamic Programming](#approach-2-dynamic-programming)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [45. Jump Game II](https://leetcode.com/problems/jump-game-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Dynamic Programming`, `Greedy` |

### Description

> You are given a **0-indexed** array of integers `nums` of length `n`. You are initially positioned at `nums[0]`.
>
> Each element `nums[i]` represents the **maximum length** of a forward jump from index `i`. In other words, if you are at `nums[i]`, you can jump to any `nums[i + j]` where:
> - `0 <= j <= nums[i]` and
> - `i + j < n`
>
> Return the **minimum number of jumps** to reach `nums[n - 1]`.
>
> The test cases are generated such that you can reach `nums[n - 1]`.

### Examples

```
Example 1:
Input: nums = [2,3,1,1,4]
Output: 2
Explanation: The minimum number of jumps to reach the last index is 2.
Jump 1 step from index 0 to 1, then 3 steps to the last index.

Example 2:
Input: nums = [2,3,0,1,4]
Output: 2
Explanation: Jump 1 step from index 0 to 1, then 3 steps to the last index.
```

### Constraints

- `1 <= nums.length <= 10^4`
- `0 <= nums[i] <= 1000`
- It's guaranteed that you can reach `nums[n - 1]`

---

## Problem Breakdown

### 1. What is being asked?

Given an array where each element represents a maximum jump length, find the **minimum number of jumps** to get from the first element to the last element.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of non-negative integers representing max jump lengths |

Important observations about the input:
- The array is **not sorted**
- Elements can be **zero** (dead ends, but guaranteed reachable)
- The array always has at least 1 element
- If `n == 1`, we are already at the destination (0 jumps needed)

### 3. What is the output?

- A single **integer** — the minimum number of jumps to reach the last index

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^4` | O(n^2) = 10^8 — might be tight. O(n) preferred. |
| `nums[i] <= 1000` | Jump lengths are bounded |
| `n >= 1` | Single element = already there |
| Reachable guaranteed | No need to handle unreachable case |

### 5. Step-by-step example analysis

#### Example 1: `nums = [2,3,1,1,4]`

```text
Index:    0  1  2  3  4
nums:    [2, 3, 1, 1, 4]

From index 0 (nums[0]=2): can reach indices 1, 2
From index 1 (nums[1]=3): can reach indices 2, 3, 4  ← reaches end!
From index 2 (nums[2]=1): can reach index 3

Optimal path: 0 → 1 → 4 (2 jumps)

Jump 1: index 0 → index 1
Jump 2: index 1 → index 4 (last index)

Result: 2
```

#### Example 2: `nums = [2,3,0,1,4]`

```text
Index:    0  1  2  3  4
nums:    [2, 3, 0, 1, 4]

From index 0 (nums[0]=2): can reach indices 1, 2
From index 1 (nums[1]=3): can reach indices 2, 3, 4  ← reaches end!
From index 2 (nums[2]=0): can reach nothing (dead end)

Optimal path: 0 → 1 → 4 (2 jumps)

Result: 2
```

### 6. Key Observations

1. **BFS analogy** — Think of this as a BFS where each "level" is one jump. From the current range of reachable indices, find the farthest you can reach with one more jump.
2. **Greedy insight** — At each jump, we don't need to try every destination. We just need to track how far we **can** reach from the current level. When we exhaust all indices in the current level, we take a jump.
3. **No backtracking** — We never need to jump backward. The optimal solution always moves forward.
4. **Level boundaries** — `end` marks the farthest index reachable with the current number of jumps. When `i` passes `end`, we must take another jump.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Greedy (BFS-like) | Expand reachable range level by level, like BFS layers | Jump Game II (this problem) |
| Dynamic Programming | Build up min jumps to each index from left to right | General approach |

**Chosen pattern:** `Greedy (BFS-like)`
**Reason:** Each "level" of BFS represents one jump. We greedily extend the farthest reachable index at each level, achieving O(n) time.

---

## Approach 1: Greedy (BFS-like)

### Thought process

> Think of this as BFS on a graph:
> - Each index is a node
> - There's an edge from `i` to every `j` where `i < j <= i + nums[i]`
> - We want the shortest path from index 0 to index n-1
>
> In BFS, we process nodes **level by level**. Each level = one jump.
> Instead of using a queue, we track the range of indices reachable at each level using two variables: `end` (current level boundary) and `farthest` (farthest reachable from this level).
>
> When we pass `end`, we've exhausted the current level → take a jump and set `end = farthest`.

### Algorithm (step-by-step)

1. Initialize `jumps = 0`, `end = 0`, `farthest = 0`
2. Iterate `i` from `0` to `n - 2` (we don't need to jump FROM the last index):
   a. Update `farthest = max(farthest, i + nums[i])`
   b. If `i == end` (reached the boundary of current jump):
      - Increment `jumps`
      - Set `end = farthest`
      - If `end >= n - 1`, we can stop early
3. Return `jumps`

### Pseudocode

```text
function jump(nums):
    n = len(nums)
    jumps = 0
    end = 0        // boundary of current jump level
    farthest = 0   // farthest reachable from current level

    for i = 0 to n - 2:
        farthest = max(farthest, i + nums[i])

        if i == end:       // exhausted current level
            jumps++
            end = farthest
            if end >= n - 1:
                break

    return jumps
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the array. Each index visited exactly once. |
| **Space** | O(1) | Only three variables: `jumps`, `end`, `farthest`. |

### Implementation

#### Go

```go
// jump — Greedy (BFS-like) approach
// Time: O(n), Space: O(1)
func jump(nums []int) int {
    n := len(nums)
    jumps := 0
    end := 0      // boundary of current jump level
    farthest := 0 // farthest reachable from current level

    for i := 0; i < n-1; i++ {
        // Update the farthest we can reach from this level
        if i+nums[i] > farthest {
            farthest = i + nums[i]
        }

        // If we've reached the end of the current level, jump
        if i == end {
            jumps++
            end = farthest
            if end >= n-1 {
                break
            }
        }
    }

    return jumps
}
```

#### Java

```java
class Solution {
    // jump — Greedy (BFS-like) approach
    // Time: O(n), Space: O(1)
    public int jump(int[] nums) {
        int n = nums.length;
        int jumps = 0;
        int end = 0;      // boundary of current jump level
        int farthest = 0; // farthest reachable from current level

        for (int i = 0; i < n - 1; i++) {
            // Update the farthest we can reach from this level
            farthest = Math.max(farthest, i + nums[i]);

            // If we've reached the end of the current level, jump
            if (i == end) {
                jumps++;
                end = farthest;
                if (end >= n - 1) break;
            }
        }

        return jumps;
    }
}
```

#### Python

```python
class Solution:
    def jump(self, nums: list[int]) -> int:
        """
        Greedy (BFS-like) approach
        Time: O(n), Space: O(1)
        """
        n = len(nums)
        jumps = 0
        end = 0        # boundary of current jump level
        farthest = 0   # farthest reachable from current level

        for i in range(n - 1):
            # Update the farthest we can reach from this level
            farthest = max(farthest, i + nums[i])

            # If we've reached the end of the current level, jump
            if i == end:
                jumps += 1
                end = farthest
                if end >= n - 1:
                    break

        return jumps
```

### Dry Run

```text
Input: nums = [2, 3, 1, 1, 4]
n = 5, jumps = 0, end = 0, farthest = 0

i=0: farthest = max(0, 0+2) = 2
     i == end (0 == 0) → jumps = 1, end = 2
     end (2) < n-1 (4) → continue

i=1: farthest = max(2, 1+3) = 4
     i != end (1 != 2) → continue

i=2: farthest = max(4, 2+1) = 4
     i == end (2 == 2) → jumps = 2, end = 4
     end (4) >= n-1 (4) → BREAK

Result: 2

BFS Level Visualization:
  Level 0: [index 0]           → can reach up to index 2
  Level 1: [index 1, index 2]  → can reach up to index 4
  Level 2: [index 3, index 4]  → DONE (index 4 is the target)
  
  Total jumps: 2
```

---

## Approach 2: Dynamic Programming

### Thought process

> Define `dp[i]` = minimum number of jumps to reach index `i`.
> - Base case: `dp[0] = 0` (we start at index 0)
> - Transition: for each index `j < i`, if `j + nums[j] >= i`, then `dp[i] = min(dp[i], dp[j] + 1)`
>
> This works but is O(n^2). We can optimize it slightly by iterating forward:
> For each index `i`, update all reachable indices `j` in range `[i+1, i+nums[i]]`.

### Algorithm (step-by-step)

1. Create `dp` array of size `n`, initialized to infinity
2. Set `dp[0] = 0`
3. For each index `i` from `0` to `n-2`:
   a. For each `j` from `i+1` to `min(i + nums[i], n-1)`:
      - `dp[j] = min(dp[j], dp[i] + 1)`
4. Return `dp[n-1]`

### Pseudocode

```text
function jump(nums):
    n = len(nums)
    dp = [infinity] * n
    dp[0] = 0

    for i = 0 to n - 2:
        for j = i + 1 to min(i + nums[i], n - 1):
            dp[j] = min(dp[j], dp[i] + 1)

    return dp[n - 1]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * max(nums[i])) | Worst case O(n^2) when jump lengths are large. |
| **Space** | O(n) | The `dp` array of size n. |

### Implementation

#### Go

```go
// jumpDP — Dynamic Programming approach
// Time: O(n * max(nums[i])), Space: O(n)
func jumpDP(nums []int) int {
    n := len(nums)
    dp := make([]int, n)
    for i := 1; i < n; i++ {
        dp[i] = n // initialize to a large value
    }

    for i := 0; i < n-1; i++ {
        end := i + nums[i]
        if end >= n {
            end = n - 1
        }
        for j := i + 1; j <= end; j++ {
            if dp[i]+1 < dp[j] {
                dp[j] = dp[i] + 1
            }
        }
    }

    return dp[n-1]
}
```

#### Java

```java
class Solution {
    // jumpDP — Dynamic Programming approach
    // Time: O(n * max(nums[i])), Space: O(n)
    public int jumpDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, n); // initialize to a large value
        dp[0] = 0;

        for (int i = 0; i < n - 1; i++) {
            int end = Math.min(i + nums[i], n - 1);
            for (int j = i + 1; j <= end; j++) {
                dp[j] = Math.min(dp[j], dp[i] + 1);
            }
        }

        return dp[n - 1];
    }
}
```

#### Python

```python
class Solution:
    def jump(self, nums: list[int]) -> int:
        """
        Dynamic Programming approach
        Time: O(n * max(nums[i])), Space: O(n)
        """
        n = len(nums)
        dp = [float('inf')] * n
        dp[0] = 0

        for i in range(n - 1):
            end = min(i + nums[i], n - 1)
            for j in range(i + 1, end + 1):
                dp[j] = min(dp[j], dp[i] + 1)

        return dp[n - 1]
```

### Dry Run

```text
Input: nums = [2, 3, 1, 1, 4]
n = 5, dp = [0, inf, inf, inf, inf]

i=0 (nums[0]=2): update j=1..2
  dp[1] = min(inf, 0+1) = 1
  dp[2] = min(inf, 0+1) = 1
  dp = [0, 1, 1, inf, inf]

i=1 (nums[1]=3): update j=2..4
  dp[2] = min(1, 1+1) = 1  (no change)
  dp[3] = min(inf, 1+1) = 2
  dp[4] = min(inf, 1+1) = 2
  dp = [0, 1, 1, 2, 2]

i=2 (nums[2]=1): update j=3..3
  dp[3] = min(2, 1+1) = 2  (no change)
  dp = [0, 1, 1, 2, 2]

i=3 (nums[3]=1): update j=4..4
  dp[4] = min(2, 2+1) = 2  (no change)
  dp = [0, 1, 1, 2, 2]

Result: dp[4] = 2
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Greedy (BFS-like) | O(n) | O(1) | Optimal time and space | Requires understanding BFS analogy |
| 2 | Dynamic Programming | O(n * max(nums[i])) | O(n) | Intuitive, easy to prove | Slower, uses extra memory |

### Which solution to choose?

- **In an interview:** Approach 1 (Greedy) — fast, elegant, demonstrates BFS thinking
- **In production:** Approach 1 — optimal time and space
- **On LeetCode:** Approach 1 — only O(n) comfortably passes within the time limit
- **For learning:** Both — DP to understand the structure, Greedy to see the BFS optimization

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single element | `nums=[0]` | `0` | Already at the destination |
| 2 | Two elements | `nums=[1,0]` | `1` | One jump to reach the end |
| 3 | Can reach end in one jump | `nums=[5,1,1,1,1]` | `1` | Jump directly from index 0 |
| 4 | Must jump every index | `nums=[1,1,1,1,1]` | `4` | Each jump covers only 1 step |
| 5 | Large first jump | `nums=[10,0,0,0,0]` | `1` | nums[0] covers the entire array |
| 6 | Multiple optimal paths | `nums=[2,3,1,1,4]` | `2` | 0→1→4 or other 2-jump paths |

---

## Common Mistakes

### Mistake 1: Iterating up to n-1 instead of n-2

```python
# WRONG — iterates over the last index (unnecessary jump)
for i in range(n):
    farthest = max(farthest, i + nums[i])
    if i == end:
        jumps += 1
        end = farthest

# CORRECT — stop before the last index
for i in range(n - 1):
    farthest = max(farthest, i + nums[i])
    if i == end:
        jumps += 1
        end = farthest
```

**Reason:** We don't need to jump FROM the last index. If `end` happens to equal `n-1`, we'd count an extra unnecessary jump.

### Mistake 2: Not tracking `farthest` across the level

```python
# WRONG — only considering the current index's reach
if i == end:
    jumps += 1
    end = i + nums[i]  # Only considers current element

# CORRECT — consider the maximum reach from the entire level
farthest = max(farthest, i + nums[i])
if i == end:
    jumps += 1
    end = farthest  # Uses the best reach from the entire level
```

**Reason:** The greedy choice requires knowing the farthest reachable index from ALL indices in the current level, not just the boundary index.

### Mistake 3: Forgetting to handle single-element arrays

```python
# WRONG — crashes or returns 1 for single element
jumps = 0
end = 0
for i in range(n - 1):  # range(0) = empty, so this is actually fine

# But if you use a while loop:
while end < n - 1:  # This correctly returns 0 jumps
    ...
```

**Reason:** When `n == 1`, we are already at the destination and need 0 jumps.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [55. Jump Game](https://leetcode.com/problems/jump-game/) | :yellow_circle: Medium | Same setup, but only asks if reachable |
| 2 | [1306. Jump Game III](https://leetcode.com/problems/jump-game-iii/) | :yellow_circle: Medium | BFS on jump array, bidirectional jumps |
| 3 | [1345. Jump Game IV](https://leetcode.com/problems/jump-game-iv/) | :red_circle: Hard | BFS with equal-value jumps |
| 4 | [1871. Jump Game VII](https://leetcode.com/problems/jump-game-vii/) | :yellow_circle: Medium | BFS with range constraints |
| 5 | [1024. Video Stitching](https://leetcode.com/problems/video-stitching/) | :yellow_circle: Medium | Same greedy interval covering pattern |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Array elements shown as bars with jump range highlights
> - **current**, **farthest**, and **end** pointers tracked visually
> - Step-by-step BFS-level expansion with jump counting
> - Preset examples and custom input support
