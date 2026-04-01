# 0033. Search in Rotated Sorted Array

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Modified Binary Search](#approach-1-modified-binary-search)
4. [Complexity Comparison](#complexity-comparison)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Related Problems](#related-problems)
8. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Binary Search` |

### Description

> There is an integer array `nums` sorted in ascending order (with **distinct** values).
>
> Prior to being passed to your function, `nums` is **possibly rotated** at an unknown pivot index `k` (`1 <= k < nums.length`) such that the resulting array is `[nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]` (0-indexed). For example, `[0,1,2,4,5,6,7]` might be rotated at pivot index `3` and become `[4,5,6,7,0,1,2]`.
>
> Given the array `nums` after the possible rotation and an integer `target`, return the index of `target` if it is in `nums`, or `-1` if it is not in `nums`.
>
> You must write an algorithm with **O(log n)** runtime complexity.

### Examples

```
Example 1:
Input: nums = [4,5,6,7,0,1,2], target = 0
Output: 4

Example 2:
Input: nums = [4,5,6,7,0,1,2], target = 3
Output: -1

Example 3:
Input: nums = [1], target = 0
Output: -1
```

### Constraints

- `1 <= nums.length <= 5000`
- `-10^4 <= nums[i] <= 10^4`
- All values of `nums` are **unique**
- `nums` is an ascending array that is possibly rotated
- `-10^4 <= target <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Search for a target value in a rotated sorted array and return its index, or -1 if not found. The algorithm must run in O(log n) time.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Rotated sorted array with distinct values |
| `target` | `int` | Value to search for |

Important observations about the input:
- The array was **sorted in ascending order** before rotation
- All values are **distinct** (no duplicates)
- The array may or may not have been rotated (rotation by 0 is possible)
- At least 1 element

### 3. What is the output?

- **An integer** — the index of `target` in `nums`
- If `target` is not found, return **-1**

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 5000` | Small input, but O(log n) is required |
| All values unique | No duplicates — simplifies binary search logic |
| O(log n) required | Linear scan is not acceptable — must use binary search |

### 5. Step-by-step example analysis

#### Example 1: `nums = [4,5,6,7,0,1,2], target = 0`

```text
Initial state: nums = [4, 5, 6, 7, 0, 1, 2], target = 0

The array [0,1,2,4,5,6,7] was rotated at pivot index 4.
Left sorted half: [4, 5, 6, 7]
Right sorted half: [0, 1, 2]

Binary search:
  left=0, right=6, mid=3 → nums[3]=7
  Left half [4..7] is sorted. target=0 < 4, so not in left half.
  Search right: left=4, right=6, mid=5 → nums[5]=1
  Left half [0..1] is sorted. target=0 >= 0 and 0 <= 1, so search left.
  left=4, right=5, mid=4 → nums[4]=0 == target ✅

Result: 4
```

#### Example 2: `nums = [4,5,6,7,0,1,2], target = 3`

```text
Initial state: nums = [4, 5, 6, 7, 0, 1, 2], target = 3

Binary search:
  left=0, right=6, mid=3 → nums[3]=7
  Left half [4..7] is sorted. target=3 < 4, not in left half.
  left=4, right=6, mid=5 → nums[5]=1
  Left half [0..1] is sorted. target=3 > 1, not in left half.
  left=6, right=6, mid=6 → nums[6]=2, not target.
  left=7 > right=6 → STOP

Result: -1 (not found)
```

#### Example 3: `nums = [1], target = 0`

```text
Initial state: nums = [1], target = 0

left=0, right=0, mid=0 → nums[0]=1, not target.
left=1 > right=0 → STOP

Result: -1
```

### 6. Key Observations

1. **One half is always sorted** — In a rotated sorted array, when you pick the middle element, at least one half (left or right) is always sorted.
2. **Determine which half is sorted** — Compare `nums[left]` with `nums[mid]`. If `nums[left] <= nums[mid]`, the left half is sorted.
3. **Check if target is in the sorted half** — If yes, narrow search to that half. If no, search the other half.
4. **O(log n)** — Each step eliminates half the search space, just like standard binary search.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Binary Search | O(log n) on sorted/semi-sorted arrays | This problem |
| Modified Binary Search | Standard BS won't work directly on rotated arrays | Rotated search |
| Two-pass (find pivot + BS) | Find rotation point, then BS on correct half | Alternative approach |

**Chosen pattern:** `Modified Binary Search (single-pass)`
**Reason:** We can determine which half is sorted at each step and decide where the target could be, all in a single binary search loop.

---

## Approach 1: Modified Binary Search

### Thought process

> In a standard sorted array, binary search compares `nums[mid]` with `target` and moves left or right.
> In a rotated sorted array, we cannot simply compare — the array is not fully sorted.
>
> **Key insight:** At any `mid` point, one half of the array is guaranteed to be sorted.
> - If `nums[left] <= nums[mid]`, the **left half** `[left..mid]` is sorted.
> - Otherwise, the **right half** `[mid..right]` is sorted.
>
> Once we know which half is sorted, we check if `target` falls within that sorted range.
> If it does, we search that half. If not, we search the other half.

### Algorithm (step-by-step)

1. Set `left = 0`, `right = n - 1`
2. While `left <= right`:
   a. Compute `mid = left + (right - left) / 2`
   b. If `nums[mid] == target`, return `mid`
   c. Check if the **left half** is sorted: `nums[left] <= nums[mid]`
      - If yes and `nums[left] <= target < nums[mid]`: search left → `right = mid - 1`
      - Otherwise: search right → `left = mid + 1`
   d. Else the **right half** is sorted:
      - If `nums[mid] < target <= nums[right]`: search right → `left = mid + 1`
      - Otherwise: search left → `right = mid - 1`
3. Return `-1` (target not found)

### Pseudocode

```text
function search(nums, target):
    left = 0, right = n - 1

    while left <= right:
        mid = left + (right - left) / 2

        if nums[mid] == target:
            return mid

        if nums[left] <= nums[mid]:          // left half is sorted
            if nums[left] <= target < nums[mid]:
                right = mid - 1              // target is in left half
            else:
                left = mid + 1               // target is in right half
        else:                                // right half is sorted
            if nums[mid] < target <= nums[right]:
                left = mid + 1               // target is in right half
            else:
                right = mid - 1              // target is in left half

    return -1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log n) | Each iteration halves the search space |
| **Space** | O(1) | Only a few pointer variables |

### Implementation

#### Go

```go
// search — Modified Binary Search on rotated sorted array
// Time: O(log n), Space: O(1)
func search(nums []int, target int) int {
    left, right := 0, len(nums)-1

    for left <= right {
        mid := left + (right-left)/2

        // Found the target
        if nums[mid] == target {
            return mid
        }

        // Determine which half is sorted
        if nums[left] <= nums[mid] {
            // Left half [left..mid] is sorted
            if nums[left] <= target && target < nums[mid] {
                right = mid - 1 // Target is in the sorted left half
            } else {
                left = mid + 1 // Target is in the right half
            }
        } else {
            // Right half [mid..right] is sorted
            if nums[mid] < target && target <= nums[right] {
                left = mid + 1 // Target is in the sorted right half
            } else {
                right = mid - 1 // Target is in the left half
            }
        }
    }

    return -1
}
```

#### Java

```java
class Solution {
    // search — Modified Binary Search on rotated sorted array
    // Time: O(log n), Space: O(1)
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Found the target
            if (nums[mid] == target) {
                return mid;
            }

            // Determine which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half [left..mid] is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1; // Target is in the sorted left half
                } else {
                    left = mid + 1; // Target is in the right half
                }
            } else {
                // Right half [mid..right] is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1; // Target is in the sorted right half
                } else {
                    right = mid - 1; // Target is in the left half
                }
            }
        }

        return -1;
    }
}
```

#### Python

```python
class Solution:
    def search(self, nums: list[int], target: int) -> int:
        """
        Modified Binary Search on rotated sorted array
        Time: O(log n), Space: O(1)
        """
        left, right = 0, len(nums) - 1

        while left <= right:
            mid = left + (right - left) // 2

            # Found the target
            if nums[mid] == target:
                return mid

            # Determine which half is sorted
            if nums[left] <= nums[mid]:
                # Left half [left..mid] is sorted
                if nums[left] <= target < nums[mid]:
                    right = mid - 1  # Target is in the sorted left half
                else:
                    left = mid + 1  # Target is in the right half
            else:
                # Right half [mid..right] is sorted
                if nums[mid] < target <= nums[right]:
                    left = mid + 1  # Target is in the sorted right half
                else:
                    right = mid - 1  # Target is in the left half

        return -1
```

### Dry Run

```text
Input: nums = [4, 5, 6, 7, 0, 1, 2], target = 0

Step 1: left=0, right=6, mid=3
        nums[mid]=7, not target
        nums[left]=4 <= nums[mid]=7 → left half [4,5,6,7] is sorted
        Is 4 <= 0 < 7? ❌ No
        → search right: left = 4

Step 2: left=4, right=6, mid=5
        nums[mid]=1, not target
        nums[left]=0 <= nums[mid]=1 → left half [0,1] is sorted
        Is 0 <= 0 < 1? ✅ Yes
        → search left: right = 4

Step 3: left=4, right=4, mid=4
        nums[mid]=0 == target ✅ FOUND!
        return 4

Total steps: 3 (log2(7) ≈ 2.8)
```

```text
Input: nums = [4, 5, 6, 7, 0, 1, 2], target = 3

Step 1: left=0, right=6, mid=3
        nums[mid]=7, not target
        nums[left]=4 <= nums[mid]=7 → left half [4,5,6,7] is sorted
        Is 4 <= 3 < 7? ❌ No (3 < 4)
        → search right: left = 4

Step 2: left=4, right=6, mid=5
        nums[mid]=1, not target
        nums[left]=0 <= nums[mid]=1 → left half [0,1] is sorted
        Is 0 <= 3 < 1? ❌ No (3 >= 1)
        → search right: left = 6

Step 3: left=6, right=6, mid=6
        nums[mid]=2, not target
        nums[left]=2 <= nums[mid]=2 → left half [2] is sorted
        Is 2 <= 3 < 2? ❌ No
        → search right: left = 7

left=7 > right=6 → STOP
return -1

Total steps: 3 (target not in array)
```

```text
Input: nums = [3, 1], target = 1

Step 1: left=0, right=1, mid=0
        nums[mid]=3, not target
        nums[left]=3 <= nums[mid]=3 → left half [3] is sorted
        Is 3 <= 1 < 3? ❌ No
        → search right: left = 1

Step 2: left=1, right=1, mid=1
        nums[mid]=1 == target ✅ FOUND!
        return 1

Total steps: 2
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Modified Binary Search | O(log n) | O(1) | Single pass, optimal | Slightly tricky logic |
| 2 | Find Pivot + Binary Search | O(log n) | O(1) | Conceptually clear (2 BS passes) | Two separate binary searches |
| 3 | Linear Scan | O(n) | O(1) | Simplest to write | Does not meet O(log n) requirement |

### Which solution to choose?

- **In an interview:** Approach 1 (Modified Binary Search) — elegant single-pass solution
- **In production:** Approach 1 — optimal time and space
- **On Leetcode:** Approach 1 — meets the O(log n) requirement
- **For learning:** Understand both Approach 1 and 2 — they teach different ways to decompose the problem

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single element (found) | `nums=[1], target=1` | `0` | Minimal array, target present |
| 2 | Single element (not found) | `nums=[1], target=0` | `-1` | Minimal array, target absent |
| 3 | No rotation | `nums=[1,2,3,4,5], target=3` | `2` | Standard sorted array |
| 4 | Full rotation (same as no rotation) | `nums=[1,2,3], target=2` | `1` | Rotated by n positions |
| 5 | Target at rotation point | `nums=[4,5,6,7,0,1,2], target=0` | `4` | Target is the minimum |
| 6 | Target is first element | `nums=[4,5,6,7,0,1,2], target=4` | `0` | Target at index 0 |
| 7 | Target is last element | `nums=[4,5,6,7,0,1,2], target=2` | `6` | Target at last index |
| 8 | Two elements | `nums=[3,1], target=1` | `1` | Small rotated array |

---

## Common Mistakes

### Mistake 1: Wrong comparison for determining the sorted half

```python
# ❌ WRONG — using < instead of <=
if nums[left] < nums[mid]:  # fails when left == mid
    ...

# ✅ CORRECT — use <= to handle the case when left == mid
if nums[left] <= nums[mid]:
    ...
```

**Reason:** When `left == mid` (e.g., 2 elements left), `nums[left] == nums[mid]`. Using strict `<` would incorrectly classify the left half as unsorted.

### Mistake 2: Wrong boundary checks for target range

```python
# ❌ WRONG — using wrong inequality
if nums[left] <= target <= nums[mid]:  # includes nums[mid] which is already checked
    right = mid - 1

# ✅ CORRECT — exclude nums[mid] since we already checked it
if nums[left] <= target < nums[mid]:
    right = mid - 1
```

**Reason:** We already checked `nums[mid] == target` at the beginning, so we should use strict `<` for `nums[mid]`.

### Mistake 3: Integer overflow in mid calculation

```java
// ❌ WRONG — potential overflow for large arrays
int mid = (left + right) / 2;

// ✅ CORRECT — prevents overflow
int mid = left + (right - left) / 2;
```

**Reason:** `left + right` can overflow when both are large. The subtraction form avoids this.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [81. Search in Rotated Sorted Array II](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/) | :yellow_circle: Medium | Same problem but with duplicates |
| 2 | [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | :yellow_circle: Medium | Find the pivot/minimum element |
| 3 | [154. Find Minimum in Rotated Sorted Array II](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array-ii/) | :red_circle: Hard | Find minimum with duplicates |
| 4 | [704. Binary Search](https://leetcode.com/problems/binary-search/) | :green_circle: Easy | Standard binary search |
| 5 | [74. Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) | :yellow_circle: Medium | Binary search on matrix |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Array visualization** with left, mid, right pointers
> - **Sorted half highlighting** — shows which half is sorted at each step
> - **Step-by-step log** — detailed explanation of each binary search decision
> - **Preset examples** — try different rotated arrays and targets
