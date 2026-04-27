# 0053. Maximum Subarray

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Kadane's Algorithm](#approach-2-kadanes-algorithm)
5. [Approach 3: Divide and Conquer](#approach-3-divide-and-conquer)
6. [Approach 4: Prefix Sum](#approach-4-prefix-sum)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Divide and Conquer`, `Dynamic Programming` |

### Description

> Given an integer array `nums`, find the **subarray** with the largest sum, and return its sum.
>
> A **subarray** is a contiguous non-empty sequence of elements within an array.

### Examples

```
Example 1:
Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6
Explanation: The subarray [4,-1,2,1] has the largest sum 6.

Example 2:
Input: nums = [1]
Output: 1

Example 3:
Input: nums = [5,4,-1,7,8]
Output: 23
```

### Constraints

- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Find the contiguous slice of `nums` whose sum is the largest, and return that sum. The slice must be non-empty, so even if every element is negative we still pick the single least-negative one.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | An array of integers, possibly negative |

Important observations about the input:
- Length up to 10^5 -- O(n^2) is borderline, O(n) is required for safety
- Values can be negative, zero, or positive
- The answer is always defined since the array is non-empty

### 3. What is the output?

A single integer -- the maximum sum over all non-empty contiguous subarrays.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^5` | O(n^2) ≈ 10^10 operations -> TLE. O(n) or O(n log n) is needed |
| Values in `[-10^4, 10^4]` | Sum of all elements bounded by 10^9, fits in `int32` |
| Subarray non-empty | Cannot return 0 when all values are negative |

### 5. Step-by-step example analysis

#### Example 1: `nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]`

```text
Index    : 0   1   2   3   4   5   6   7   8
Value    : -2  1  -3   4  -1   2   1  -5   4

Walk Kadane's algorithm — at each index, decide:
"Should the subarray ending here continue from before, or start fresh?"

cur = max(nums[i], cur + nums[i])
best = max(best, cur)

i=0:  cur = max(-2, 0 + -2) = -2     best = -2
i=1:  cur = max(1,  -2 + 1) = 1      best = 1
i=2:  cur = max(-3, 1 + -3) = -2     best = 1
i=3:  cur = max(4,  -2 + 4) = 4      best = 4   ← restart here
i=4:  cur = max(-1, 4 + -1) = 3      best = 4
i=5:  cur = max(2,  3 + 2) = 5       best = 5
i=6:  cur = max(1,  5 + 1) = 6       best = 6   ← optimal: [4,-1,2,1]
i=7:  cur = max(-5, 6 + -5) = 1      best = 6
i=8:  cur = max(4,  1 + 4) = 5       best = 6

Answer: 6
Subarray: nums[3..6] = [4, -1, 2, 1]
```

### 6. Key Observations

1. **Kadane's insight** -- The maximum subarray ending at index `i` is either `nums[i]` alone, or the maximum subarray ending at `i-1` extended by `nums[i]`. We never carry a negative running sum forward.
2. **Divide and conquer is possible but slower** -- We can split the array, recurse on both halves, and merge by computing the max subarray crossing the midpoint. O(n log n).
3. **Prefix sum view** -- The sum of `nums[i..j]` is `prefix[j+1] - prefix[i]`. Maximum subarray sum is `max(prefix[j+1] - min_prefix_so_far)`.
4. **All-negative trap** -- The algorithm must allow a negative running sum if every element is negative.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Dynamic Programming | Optimal substructure (best ending at i) | Kadane's, this problem |
| Sliding Window | Doesn't apply -- elements can be negative |  |
| Divide and Conquer | Split + combine works | Merge-sort-style splitting |
| Prefix Sum | Subarray sum = prefix difference | Range sum problems |

**Chosen pattern:** `Dynamic Programming (Kadane's Algorithm)`
**Reason:** O(n) time, O(1) space, three lines of code. Hard to beat.

---

## Approach 1: Brute Force

### Thought process

> Try every possible `(i, j)` pair and compute the sum directly.

### Algorithm (step-by-step)

1. For each starting index `i`, accumulate the running sum as `j` extends.
2. Track the global maximum.

### Pseudocode

```text
best = -infinity
for i in 0..n-1:
    s = 0
    for j in i..n-1:
        s += nums[j]
        best = max(best, s)
return best
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Two nested loops |
| **Space** | O(1) | Just two variables |

### Implementation

#### Go

```go
func maxSubArrayBrute(nums []int) int {
    best := nums[0]
    for i := 0; i < len(nums); i++ {
        s := 0
        for j := i; j < len(nums); j++ {
            s += nums[j]
            if s > best {
                best = s
            }
        }
    }
    return best
}
```

#### Java

```java
class Solution {
    public int maxSubArrayBrute(int[] nums) {
        int best = nums[0];
        for (int i = 0; i < nums.length; i++) {
            int s = 0;
            for (int j = i; j < nums.length; j++) {
                s += nums[j];
                if (s > best) best = s;
            }
        }
        return best;
    }
}
```

#### Python

```python
class Solution:
    def maxSubArrayBrute(self, nums: List[int]) -> int:
        best = nums[0]
        for i in range(len(nums)):
            s = 0
            for j in range(i, len(nums)):
                s += nums[j]
                if s > best: best = s
        return best
```

### Dry Run

```text
Input: [-2, 1, -3, 4]

i=0: s=-2(best=-2), s=-1(-1>−2 best=-1), s=-4, s=0(best=0)
i=1: s=1(best=1), s=-2, s=2(best=2)
i=2: s=-3, s=1
i=3: s=4(best=4)

Answer: 4
```

---

## Approach 2: Kadane's Algorithm

### The problem with Brute Force

> O(n^2) is too slow for `n = 10^5`. We need a single pass.

### Optimization idea

> Maintain a running sum `cur`. At each index, decide whether to extend the previous subarray (`cur + nums[i]`) or to restart from `nums[i]`. Keep the global best.

### Algorithm (step-by-step)

1. Set `cur = best = nums[0]`.
2. For `i` from `1` to `n-1`:
   - `cur = max(nums[i], cur + nums[i])`
   - `best = max(best, cur)`
3. Return `best`.

### Pseudocode

```text
cur = best = nums[0]
for i = 1 to n-1:
    cur = max(nums[i], cur + nums[i])
    best = max(best, cur)
return best
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single linear scan |
| **Space** | O(1) | Two variables |

### Implementation

#### Go

```go
func maxSubArray(nums []int) int {
    cur, best := nums[0], nums[0]
    for i := 1; i < len(nums); i++ {
        if cur+nums[i] > nums[i] {
            cur = cur + nums[i]
        } else {
            cur = nums[i]
        }
        if cur > best {
            best = cur
        }
    }
    return best
}
```

#### Java

```java
class Solution {
    public int maxSubArray(int[] nums) {
        int cur = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            best = Math.max(best, cur);
        }
        return best;
    }
}
```

#### Python

```python
class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        cur = best = nums[0]
        for x in nums[1:]:
            cur = max(x, cur + x)
            best = max(best, cur)
        return best
```

### Dry Run

```text
Input: [-2, 1, -3, 4, -1, 2, 1, -5, 4]

i=0: cur=-2,             best=-2
i=1: cur=max(1, -2+1)=1, best=1
i=2: cur=max(-3, 1-3)=-2, best=1
i=3: cur=max(4, -2+4)=4,  best=4   ← restart
i=4: cur=max(-1, 4-1)=3, best=4
i=5: cur=max(2, 3+2)=5,  best=5
i=6: cur=max(1, 5+1)=6,  best=6   ← peak
i=7: cur=max(-5, 6-5)=1, best=6
i=8: cur=max(4, 1+4)=5,  best=6

Answer: 6
```

---

## Approach 3: Divide and Conquer

### Thought process

> Split the array into two halves. The maximum subarray must be entirely in the left, entirely in the right, or crossing the midpoint. The first two are solved by recursion; the crossing case is computed greedily in O(n).

### Algorithm (step-by-step)

1. Base case: a single element returns that element.
2. Recurse on the left half and right half.
3. Compute the maximum subarray crossing the midpoint:
   - From the midpoint, expand left tracking the best left-extending sum
   - Symmetrically expand right
   - The crossing answer is `bestLeft + bestRight`
4. Return the maximum of the three.

### Pseudocode

```text
function solve(l, r):
    if l == r: return nums[l]
    m = (l + r) / 2
    leftMax  = solve(l, m)
    rightMax = solve(m+1, r)
    crossMax = bestCross(l, m, r)
    return max(leftMax, rightMax, crossMax)

function bestCross(l, m, r):
    bestL = -infinity, s = 0
    for i in m downto l:
        s += nums[i]
        bestL = max(bestL, s)
    bestR = -infinity, s = 0
    for j in m+1 to r:
        s += nums[j]
        bestR = max(bestR, s)
    return bestL + bestR
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n log n) | T(n) = 2T(n/2) + O(n) |
| **Space** | O(log n) | Recursion depth |

### Implementation

#### Go

```go
func maxSubArrayDC(nums []int) int {
    var solve func(l, r int) int
    solve = func(l, r int) int {
        if l == r {
            return nums[l]
        }
        m := (l + r) / 2
        leftMax := solve(l, m)
        rightMax := solve(m+1, r)

        bestL, sumL := nums[m], 0
        for i := m; i >= l; i-- {
            sumL += nums[i]
            if sumL > bestL {
                bestL = sumL
            }
        }
        bestR, sumR := nums[m+1], 0
        for j := m + 1; j <= r; j++ {
            sumR += nums[j]
            if sumR > bestR {
                bestR = sumR
            }
        }
        cross := bestL + bestR

        best := leftMax
        if rightMax > best {
            best = rightMax
        }
        if cross > best {
            best = cross
        }
        return best
    }
    return solve(0, len(nums)-1)
}
```

#### Java

```java
class Solution {
    public int maxSubArrayDC(int[] nums) {
        return solve(nums, 0, nums.length - 1);
    }
    private int solve(int[] nums, int l, int r) {
        if (l == r) return nums[l];
        int m = (l + r) >>> 1;
        int leftMax = solve(nums, l, m);
        int rightMax = solve(nums, m + 1, r);

        int bestL = nums[m], sumL = 0;
        for (int i = m; i >= l; i--) {
            sumL += nums[i];
            bestL = Math.max(bestL, sumL);
        }
        int bestR = nums[m + 1], sumR = 0;
        for (int j = m + 1; j <= r; j++) {
            sumR += nums[j];
            bestR = Math.max(bestR, sumR);
        }
        return Math.max(Math.max(leftMax, rightMax), bestL + bestR);
    }
}
```

#### Python

```python
class Solution:
    def maxSubArrayDC(self, nums: List[int]) -> int:
        def solve(l: int, r: int) -> int:
            if l == r:
                return nums[l]
            m = (l + r) // 2
            left_max = solve(l, m)
            right_max = solve(m + 1, r)

            best_l, s = nums[m], 0
            for i in range(m, l - 1, -1):
                s += nums[i]
                best_l = max(best_l, s)
            best_r, s = nums[m + 1], 0
            for j in range(m + 1, r + 1):
                s += nums[j]
                best_r = max(best_r, s)

            return max(left_max, right_max, best_l + best_r)

        return solve(0, len(nums) - 1)
```

### Dry Run

```text
Input: [-2, 1, -3, 4]

solve(0, 3):
  m = 1
  left  = solve(0, 1) → cross/halves yields 1
  right = solve(2, 3) → 4
  cross spans m=1: bestL = max(1, 1+(-2)=-1) = 1
                   bestR = max(-3, -3+4=1) = 1
                   cross = 1 + 1 = 2
  return max(1, 4, 2) = 4
```

---

## Approach 4: Prefix Sum

### Idea

> The sum of `nums[i..j]` equals `prefix[j+1] - prefix[i]`. To maximize, we want `prefix[j+1]` to be large and `prefix[i]` (with `i <= j`) to be small. Sweep `j`, tracking the minimum prefix seen so far before this position.

### Algorithm (step-by-step)

1. Maintain `runningSum` and `minPrefix = 0`.
2. For each index, update `runningSum += nums[i]`, then `best = max(best, runningSum - minPrefix)`, then `minPrefix = min(minPrefix, runningSum)`.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single scan |
| **Space** | O(1) | Two extra variables |

### Implementation

#### Go

```go
func maxSubArrayPrefix(nums []int) int {
    best := nums[0]
    running := 0
    minPrefix := 0
    for _, x := range nums {
        running += x
        if running-minPrefix > best {
            best = running - minPrefix
        }
        if running < minPrefix {
            minPrefix = running
        }
    }
    return best
}
```

#### Java

```java
class Solution {
    public int maxSubArrayPrefix(int[] nums) {
        int best = nums[0];
        int running = 0, minPrefix = 0;
        for (int x : nums) {
            running += x;
            best = Math.max(best, running - minPrefix);
            minPrefix = Math.min(minPrefix, running);
        }
        return best;
    }
}
```

#### Python

```python
class Solution:
    def maxSubArrayPrefix(self, nums: List[int]) -> int:
        best = nums[0]
        running = 0
        min_prefix = 0
        for x in nums:
            running += x
            best = max(best, running - min_prefix)
            min_prefix = min(min_prefix, running)
        return best
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^2) | O(1) | Trivial to write | TLE at n = 10^5 |
| 2 | Kadane's DP | O(n) | O(1) | Optimal, three lines | Slightly tricky to derive |
| 3 | Divide and Conquer | O(n log n) | O(log n) | Demonstrates D&C pattern | Slower than Kadane's |
| 4 | Prefix Sum | O(n) | O(1) | Same speed, alternate intuition | One more variable |

### Which solution to choose?

- **In an interview:** Kadane's -- canonical answer, easy to explain
- **In production:** Kadane's
- **On Leetcode:** Kadane's
- **For learning:** All four; D&C is the most pedagogical for the divide-and-conquer pattern

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single positive element | `[5]` | `5` | Trivially the only subarray |
| 2 | Single negative element | `[-5]` | `-5` | Subarray must be non-empty |
| 3 | All negative | `[-3,-1,-2]` | `-1` | Pick the least-negative element |
| 4 | All positive | `[1,2,3]` | `6` | Whole array is the answer |
| 5 | Mixed (classic) | `[-2,1,-3,4,-1,2,1,-5,4]` | `6` | Standard Kadane's case |
| 6 | Long zero stretches | `[0,0,-1,0,0]` | `0` | Best is any zero-only segment |
| 7 | Largest possible n | `n = 10^5` | -- | Confirms O(n) is required |

---

## Common Mistakes

### Mistake 1: Initializing `best` to `0`

```python
# WRONG — fails for all-negative input
best = 0
for x in nums:
    cur = max(0, cur + x)   # forces non-empty constraint break
    best = max(best, cur)

# CORRECT
best = cur = nums[0]
for x in nums[1:]:
    cur = max(x, cur + x)
    best = max(best, cur)
```

**Reason:** The problem requires a non-empty subarray. Starting `best = 0` returns `0` for `[-3, -1, -2]`, but the correct answer is `-1`.

### Mistake 2: Restart vs extend confusion

```python
# WRONG — never restarts
cur += x

# CORRECT — restart when the running sum hurts more than helps
cur = max(x, cur + x)
```

**Reason:** When the running sum is negative, dragging it forward only makes the next subarray worse. Restart fresh from `x`.

### Mistake 3: Using a sliding window

```python
# WRONG — sliding window only works for non-negative arrays
left = 0
window = 0
for right in range(n):
    window += nums[right]
    while window < 0:           # arbitrary criterion, not provably optimal
        window -= nums[left]; left += 1

# CORRECT — Kadane's
```

**Reason:** Sliding window's correctness depends on monotonic behavior when shrinking the window. With negative elements, shrinking can both increase and decrease the sum, so the technique does not apply.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [121. Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | :green_circle: Easy | Same prefix-sum trick |
| 2 | [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | :yellow_circle: Medium | Kadane variant for products |
| 3 | [918. Maximum Sum Circular Subarray](https://leetcode.com/problems/maximum-sum-circular-subarray/) | :yellow_circle: Medium | Kadane plus min-subarray trick |
| 4 | [1186. Maximum Subarray Sum with One Deletion](https://leetcode.com/problems/maximum-subarray-sum-with-one-deletion/) | :yellow_circle: Medium | Two-state Kadane |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Step-by-step Kadane sweep with restart highlighting
> - Live tracking of `cur` and `best`
> - Brute force tab showing the O(n^2) double scan
> - Prefix sum tab showing min-prefix and current prefix
> - Custom array entry and random generator
