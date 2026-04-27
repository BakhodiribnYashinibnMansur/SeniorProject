# 0081. Search in Rotated Sorted Array II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Linear Scan](#approach-1-linear-scan)
4. [Approach 2: Modified Binary Search](#approach-2-modified-binary-search)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [81. Search in Rotated Sorted Array II](https://leetcode.com/problems/search-in-rotated-sorted-array-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Binary Search` |

### Description

> There is an integer array `nums` sorted in non-decreasing order (not necessarily with **distinct** values).
>
> Before being passed to your function, `nums` is rotated at an unknown pivot index.
>
> Given the array `nums` after the rotation and an integer `target`, return `true` *if `target` is in `nums`, or `false` if it is not*.
>
> You must decrease the overall operation steps as much as possible.

### Examples

```
Example 1:
Input: nums = [2,5,6,0,0,1,2], target = 0
Output: true

Example 2:
Input: nums = [2,5,6,0,0,1,2], target = 3
Output: false
```

### Constraints

- `1 <= nums.length <= 5000`
- `-10^4 <= nums[i] <= 10^4`
- `nums` is guaranteed to be rotated at some pivot.
- `-10^4 <= target <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Given a rotated sorted array that may contain duplicates, decide whether `target` exists.

### 2. Key Difference from [Problem 33](../0033-search-in-rotated-sorted-array/solution.md)

Duplicates can prevent us from determining which half is sorted: if `nums[lo] == nums[mid] == nums[hi]`, we cannot tell. Resolve by shrinking `lo++`, `hi--` (worst case O(n)).

---

## Approach 1: Linear Scan

### Idea

Walk all `n` elements. O(n).

### Implementation

#### Python

```python
class Solution:
    def searchLinear(self, nums: List[int], target: int) -> bool:
        return target in nums
```

---

## Approach 2: Modified Binary Search

### Algorithm

1. `lo = 0`, `hi = n - 1`.
2. While `lo <= hi`:
   - `mid = (lo + hi) // 2`. If `nums[mid] == target`: return true.
   - If `nums[lo] == nums[mid] == nums[hi]`: `lo++; hi--`.
   - Else if left half sorted (`nums[lo] <= nums[mid]`):
     - If `nums[lo] <= target < nums[mid]`: `hi = mid - 1`. Else `lo = mid + 1`.
   - Else (right half sorted):
     - If `nums[mid] < target <= nums[hi]`: `lo = mid + 1`. Else `hi = mid - 1`.
3. Return false.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(log n) average; O(n) worst (all duplicates) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func search(nums []int, target int) bool {
    lo, hi := 0, len(nums)-1
    for lo <= hi {
        mid := (lo + hi) / 2
        if nums[mid] == target {
            return true
        }
        if nums[lo] == nums[mid] && nums[mid] == nums[hi] {
            lo++
            hi--
        } else if nums[lo] <= nums[mid] {
            if nums[lo] <= target && target < nums[mid] {
                hi = mid - 1
            } else {
                lo = mid + 1
            }
        } else {
            if nums[mid] < target && target <= nums[hi] {
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
    }
    return false
}
```

#### Java

```java
class Solution {
    public boolean search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            if (nums[mid] == target) return true;
            if (nums[lo] == nums[mid] && nums[mid] == nums[hi]) {
                lo++; hi--;
            } else if (nums[lo] <= nums[mid]) {
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return false;
    }
}
```

#### Python

```python
class Solution:
    def search(self, nums: List[int], target: int) -> bool:
        lo, hi = 0, len(nums) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] == target: return True
            if nums[lo] == nums[mid] == nums[hi]:
                lo += 1; hi -= 1
            elif nums[lo] <= nums[mid]:
                if nums[lo] <= target < nums[mid]: hi = mid - 1
                else: lo = mid + 1
            else:
                if nums[mid] < target <= nums[hi]: lo = mid + 1
                else: hi = mid - 1
        return False
```

---

## Complexity Comparison

| # | Approach | Time | Space |
|---|---|---|---|
| 1 | Linear | O(n) | O(1) |
| 2 | Binary Search | O(log n) avg, O(n) worst | O(1) |

### Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | All duplicates `[1,1,1,1]`, target = 1 | True |
| 2 | All duplicates, target absent | False (degrades to O(n)) |
| 3 | Not rotated | Standard binary search |
| 4 | Pivot at start | Whole array is sorted |
| 5 | Single element match | True |
| 6 | Single element miss | False |

---

## Common Mistakes

### Mistake 1: Forgetting the duplicate-shrinking branch

```python
# WRONG — without nums[lo] == nums[mid] == nums[hi] handling, breaks for [1,1,1,2,1]
```

### Mistake 2: Off-by-one in target ranges

```python
if nums[lo] <= target < nums[mid]: hi = mid - 1   # left side, half-open
if nums[mid] < target <= nums[hi]: lo = mid + 1   # right side, half-open
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | :yellow_circle: Medium | Same without duplicates |
| 2 | [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | :yellow_circle: Medium | Find pivot |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
