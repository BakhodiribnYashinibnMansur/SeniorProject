# 0016. 3Sum Closest

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Sort + Two Pointers](#approach-2-sort--two-pointers)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Array`, `Two Pointers`, `Sorting` |

### Description

> Given an integer array `nums` of length `n` and an integer `target`, find three integers in `nums` such that the sum is closest to `target`.
>
> Return the sum of the three integers.
>
> You may assume that each input would have **exactly one solution**.

### Examples

```
Example 1:
Input: nums = [-1,2,1,-4], target = 1
Output: 2
Explanation: The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).

Example 2:
Input: nums = [0,0,0], target = 1
Output: 0
Explanation: The sum that is closest to the target is 0. (0 + 0 + 0 = 0).

Example 3:
Input: nums = [1,1,1,0], target = 3
Output: 3
Explanation: The sum that equals the target is 3. (1 + 1 + 1 = 3).
```

### Constraints

- `n == nums.length`
- `3 <= n <= 500`
- `-1000 <= nums[i] <= 1000`
- `-10^4 <= target <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Given an array of integers and a target value, find the triplet whose sum is closest to the target. Unlike 3Sum (which finds exact matches equaling zero), here we need the **minimum distance** between any triplet sum and the target. Return the actual sum, not the distance.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers (may contain negatives, duplicates) |
| `target` | `int` | The target value to get closest to |

Important observations about the input:
- The array is **not sorted**
- Elements can be **negative**, **zero**, or **positive**
- The array always has at least 3 elements
- **Exactly one solution** is guaranteed (no ties)

### 3. What is the output?

- A single **integer** — the sum of the three integers closest to `target`
- The closeness is measured by `|sum - target|`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 500` | O(n^3) = 1.25 * 10^8 — borderline. O(n^2) is safe. |
| `nums[i]` in `[-1000, 1000]` | Max sum = 3000, min sum = -3000. Fits in int32. |
| `target` in `[-10^4, 10^4]` | Max distance = 10^4 + 3000 = 13000. Fits in int32. |
| `n >= 3` | Always at least one triplet to check |
| Exactly one solution | No need to handle ties |

### 5. Step-by-step example analysis

#### Example 1: `nums = [-1, 2, 1, -4], target = 1`

```text
All possible triplets:
  (-1, 2, 1)  = 2   → |2 - 1| = 1
  (-1, 2, -4) = -3  → |-3 - 1| = 4
  (-1, 1, -4) = -4  → |-4 - 1| = 5
  (2, 1, -4)  = -1  → |-1 - 1| = 2

Closest sum: 2 (distance = 1)
Result: 2
```

#### Example 2: `nums = [0, 0, 0], target = 1`

```text
Only one triplet:
  (0, 0, 0) = 0  → |0 - 1| = 1

Closest sum: 0
Result: 0
```

#### Example 3: `nums = [1, 1, 1, 0], target = 3`

```text
All possible triplets:
  (1, 1, 1) = 3  → |3 - 3| = 0  ← exact match!
  (1, 1, 0) = 2  → |2 - 3| = 1
  (1, 1, 0) = 2  → |2 - 3| = 1
  (1, 1, 0) = 2  → |2 - 3| = 1

Closest sum: 3 (exact match)
Result: 3
```

### 6. Key Observations

1. **Distance metric** — We minimize `|sum - target|`, where `sum = nums[i] + nums[j] + nums[k]`.
2. **Sorting helps** — After sorting, we can use two pointers to efficiently search for the closest sum. If the current sum is too small, move the left pointer right; if too large, move the right pointer left.
3. **Early termination** — If we find an exact match (`sum == target`), we can return immediately since distance is 0.
4. **Relationship to 3Sum** — This is a generalization of 3Sum. Instead of checking `sum == 0`, we track the minimum `|sum - target|`.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Sort + Two Pointers | O(n^2) scan with sorted array, directional movement | 3Sum, 3Sum Closest (this problem) |
| Brute Force | Always works, check all triplets | All problems |

**Chosen pattern:** `Sort + Two Pointers`
**Reason:** Sorting the array allows us to use two pointers that move directionally based on whether the current sum is above or below the target. This reduces the inner search from O(n^2) to O(n), giving an overall O(n^2) solution.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: check every possible triplet and track which sum is closest to the target.
> Using three nested loops, compute the sum for all C(n,3) triplets.
> Track the closest sum found.

### Algorithm (step-by-step)

1. Initialize `closest = nums[0] + nums[1] + nums[2]`
2. Outer loop: `i = 0` to `n-3`
3. Middle loop: `j = i+1` to `n-2`
4. Inner loop: `k = j+1` to `n-1`
5. Calculate `sum = nums[i] + nums[j] + nums[k]`
6. If `|sum - target| < |closest - target|`, update `closest = sum`
7. Return `closest`

### Pseudocode

```text
function threeSumClosest(nums, target):
    closest = nums[0] + nums[1] + nums[2]
    for i = 0 to n-3:
        for j = i+1 to n-2:
            for k = j+1 to n-1:
                sum = nums[i] + nums[j] + nums[k]
                if |sum - target| < |closest - target|:
                    closest = sum
    return closest
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^3) | Three nested loops. C(n,3) = n*(n-1)*(n-2)/6 triplets. |
| **Space** | O(1) | No extra memory — only a few variables. |

### Implementation

#### Go

```go
// threeSumClosestBruteForce — Brute Force approach
// Time: O(n^3), Space: O(1)
func threeSumClosestBruteForce(nums []int, target int) int {
    n := len(nums)
    closest := nums[0] + nums[1] + nums[2]

    for i := 0; i < n-2; i++ {
        for j := i + 1; j < n-1; j++ {
            for k := j + 1; k < n; k++ {
                sum := nums[i] + nums[j] + nums[k]
                if abs(sum-target) < abs(closest-target) {
                    closest = sum
                }
            }
        }
    }

    return closest
}
```

#### Java

```java
class Solution {
    // threeSumClosest — Brute Force approach
    // Time: O(n^3), Space: O(1)
    public int threeSumClosest(int[] nums, int target) {
        int n = nums.length;
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    int sum = nums[i] + nums[j] + nums[k];
                    if (Math.abs(sum - target) < Math.abs(closest - target)) {
                        closest = sum;
                    }
                }
            }
        }

        return closest;
    }
}
```

#### Python

```python
class Solution:
    def threeSumClosest(self, nums: list[int], target: int) -> int:
        """
        Brute Force approach
        Time: O(n^3), Space: O(1)
        """
        n = len(nums)
        closest = nums[0] + nums[1] + nums[2]

        for i in range(n - 2):
            for j in range(i + 1, n - 1):
                for k in range(j + 1, n):
                    current_sum = nums[i] + nums[j] + nums[k]
                    if abs(current_sum - target) < abs(closest - target):
                        closest = current_sum

        return closest
```

### Dry Run

```text
Input: nums = [-1, 2, 1, -4], target = 1

Initial: closest = -1 + 2 + 1 = 2

i=0 (-1):
  j=1 (2):
    k=2 (1):  sum = -1+2+1 = 2   |2-1|=1  < |2-1|=1  → no change
    k=3 (-4): sum = -1+2-4 = -3  |-3-1|=4 > 1         → no change
  j=2 (1):
    k=3 (-4): sum = -1+1-4 = -4  |-4-1|=5 > 1         → no change

i=1 (2):
  j=2 (1):
    k=3 (-4): sum = 2+1-4 = -1   |-1-1|=2 > 1         → no change

Total triplets checked: 4 (C(4,3) = 4)
Result: 2
```

---

## Approach 2: Sort + Two Pointers

### The problem with Brute Force

> Brute Force checks all C(n,3) triplets — that's O(n^3).
> With n = 500, that's ~20 million operations — it might pass, but is slow.
> Question: "Can we reduce the search space using sorting?"

### Optimization idea

> **Sort the array first.** Then for each fixed element `nums[i]`, use two pointers `left` and `right` to find the best pair.
>
> Since the array is sorted:
> - If `sum < target`, moving `left` right increases the sum
> - If `sum > target`, moving `right` left decreases the sum
> - If `sum == target`, we found an exact match and return immediately
>
> This way, for each `i`, we scan the remaining elements in O(n) instead of O(n^2).

### Algorithm (step-by-step)

1. **Sort** the array
2. Initialize `closest = nums[0] + nums[1] + nums[2]`
3. For each `i` from `0` to `n-3`:
   a. Skip if `nums[i] == nums[i-1]` (optional optimization)
   b. Set `left = i+1`, `right = n-1`
   c. While `left < right`:
      - Calculate `sum = nums[i] + nums[left] + nums[right]`
      - If `sum == target`, return `target`
      - If `|sum - target| < |closest - target|`, update `closest = sum`
      - If `sum < target`, move `left++`
      - Else move `right--`
4. Return `closest`

### Pseudocode

```text
function threeSumClosest(nums, target):
    sort(nums)
    closest = nums[0] + nums[1] + nums[2]

    for i = 0 to n-3:
        if i > 0 and nums[i] == nums[i-1]: continue
        left = i + 1, right = n - 1

        while left < right:
            sum = nums[i] + nums[left] + nums[right]
            if sum == target: return target
            if |sum - target| < |closest - target|:
                closest = sum
            if sum < target: left++
            else: right--

    return closest
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Sorting is O(n log n). Outer loop O(n) * two-pointer O(n) = O(n^2). |
| **Space** | O(log n) | Sorting space (depends on implementation). |

### Implementation

#### Go

```go
// threeSumClosest — Sort + Two Pointers approach
// Time: O(n^2), Space: O(log n)
func threeSumClosest(nums []int, target int) int {
    sort.Ints(nums)
    n := len(nums)
    closest := nums[0] + nums[1] + nums[2]

    for i := 0; i < n-2; i++ {
        if i > 0 && nums[i] == nums[i-1] {
            continue
        }

        left, right := i+1, n-1

        for left < right {
            sum := nums[i] + nums[left] + nums[right]

            if sum == target {
                return target
            }

            if abs(sum-target) < abs(closest-target) {
                closest = sum
            }

            if sum < target {
                left++
            } else {
                right--
            }
        }
    }

    return closest
}
```

#### Java

```java
class Solution {
    // threeSumClosest — Sort + Two Pointers approach
    // Time: O(n^2), Space: O(log n)
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int n = nums.length;
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int left = i + 1, right = n - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == target) return target;

                if (Math.abs(sum - target) < Math.abs(closest - target)) {
                    closest = sum;
                }

                if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return closest;
    }
}
```

#### Python

```python
class Solution:
    def threeSumClosest(self, nums: list[int], target: int) -> int:
        """
        Sort + Two Pointers approach
        Time: O(n^2), Space: O(log n)
        """
        nums.sort()
        n = len(nums)
        closest = nums[0] + nums[1] + nums[2]

        for i in range(n - 2):
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            left, right = i + 1, n - 1

            while left < right:
                current_sum = nums[i] + nums[left] + nums[right]

                if current_sum == target:
                    return target

                if abs(current_sum - target) < abs(closest - target):
                    closest = current_sum

                if current_sum < target:
                    left += 1
                else:
                    right -= 1

        return closest
```

### Dry Run

```text
Input: nums = [-1, 2, 1, -4], target = 1

Step 0: Sort → [-4, -1, 1, 2]
        closest = -4 + (-1) + 1 = -4

i=0 (nums[i]=-4), left=1, right=3:
  Step 1: sum = -4 + (-1) + 2 = -3   |-3-1|=4 < |-4-1|=5 → closest = -3
          sum < target → left++ → left=2

  Step 2: sum = -4 + 1 + 2 = -1      |-1-1|=2 < |-3-1|=4 → closest = -1
          sum < target → left++ → left=3

  left == right → inner loop ends

i=1 (nums[i]=-1), left=2, right=3:
  Step 3: sum = -1 + 1 + 2 = 2       |2-1|=1 < |-1-1|=2 → closest = 2
          sum > target → right-- → right=2

  left == right → inner loop ends

i=2 → i >= n-2, outer loop ends

Total operations: 3 inner steps (vs Brute Force: 4 triplets)
Result: 2
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^3) | O(1) | Simple, no sorting needed | Slow for large inputs |
| 2 | Sort + Two Pointers | O(n^2) | O(log n) | Optimal time, early termination | Requires sorting (modifies array) |

### Which solution to choose?

- **In an interview:** Approach 2 (Sort + Two Pointers) — demonstrates knowledge of sorting + two-pointer pattern
- **In production:** Approach 2 — optimal time complexity
- **On Leetcode:** Approach 2 — O(n^2) comfortably passes for n=500
- **For learning:** Both — Brute Force to understand the problem, Sort + Two Pointers to learn the optimization

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Minimum input | `nums=[1,1,1], target=2` | `3` | Only one triplet possible |
| 2 | Exact match | `nums=[1,1,1,0], target=3` | `3` | Triplet sum equals target exactly |
| 3 | All negatives | `nums=[-3,-2,-5,-1], target=-8` | `-8` | Closest to -8 is -5+(-2)+(-1)=-8 |
| 4 | All zeros | `nums=[0,0,0], target=1` | `0` | Sum is always 0 |
| 5 | Large target | `nums=[1,2,3,4,5], target=100` | `12` | Max sum 3+4+5=12, still far from 100 |
| 6 | All same values | `nums=[5,5,5,5], target=10` | `15` | Only possible sum is 15 |
| 7 | Target between two sums | `nums=[-1,0,1,2], target=2` | `2` | -1+1+2=2 matches exactly |

---

## Common Mistakes

### Mistake 1: Forgetting to sort the array before using two pointers

```python
# WRONG — two pointers on unsorted array gives incorrect results
def threeSumClosest(self, nums, target):
    closest = nums[0] + nums[1] + nums[2]
    for i in range(len(nums) - 2):
        left, right = i + 1, len(nums) - 1
        while left < right:
            # Two pointers logic won't work on unsorted array!
            ...

# CORRECT — sort first
def threeSumClosest(self, nums, target):
    nums.sort()  # Essential!
    ...
```

**Reason:** The two-pointer technique relies on the sorted order to decide which pointer to move. Without sorting, moving left or right has no predictable effect on the sum.

### Mistake 2: Using wrong distance comparison

```python
# WRONG — comparing sums instead of distances
if current_sum < closest:
    closest = current_sum

# CORRECT — compare absolute distances to target
if abs(current_sum - target) < abs(closest - target):
    closest = current_sum
```

**Reason:** A smaller sum is not necessarily closer to the target. For example, if target=5, sum=3 (distance=2) is closer than sum=1 (distance=4), even though 1 < 3.

### Mistake 3: Not returning early on exact match

```python
# INEFFICIENT — continues searching even after finding exact match
if abs(current_sum - target) < abs(closest - target):
    closest = current_sum

# BETTER — return immediately when exact match found
if current_sum == target:
    return target
if abs(current_sum - target) < abs(closest - target):
    closest = current_sum
```

**Reason:** If the sum equals the target, the distance is 0 and we cannot do better. Returning early avoids unnecessary iterations.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [1. Two Sum](https://leetcode.com/problems/two-sum/) | 🟢 Easy | Finding target sum with fewer elements |
| 2 | [15. 3Sum](https://leetcode.com/problems/3sum/) | 🟡 Medium | Same sort + two pointers pattern, exact match |
| 3 | [18. 4Sum](https://leetcode.com/problems/4sum/) | 🟡 Medium | Extension to four elements |
| 4 | [167. Two Sum II](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | 🟡 Medium | Two pointers on sorted array |
| 5 | [259. 3Sum Smaller](https://leetcode.com/problems/3sum-smaller/) | 🟡 Medium | Counting triplets with sum condition |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — three nested loops (i, j, k) check all triplets
> - **Sort + Two Pointers** tab — fix i, move left and right pointers
> - Array visualization with sorting step
> - Distance-to-target display and best sum tracking
> - Custom input controls for experimenting with different arrays and targets
