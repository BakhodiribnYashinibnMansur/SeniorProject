# 0034. Find First and Last Position of Element in Sorted Array

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Two Binary Searches](#approach-1-two-binary-searches)
4. [Complexity Comparison](#complexity-comparison)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Related Problems](#related-problems)
8. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [34. Find First and Last Position of Element in Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Binary Search` |

### Description

> Given an array of integers `nums` sorted in non-decreasing order, find the starting and ending position of a given `target` value.
>
> If `target` is not found in the array, return `[-1, -1]`.
>
> You must write an algorithm with **O(log n)** runtime complexity.

### Examples

```
Example 1:
Input: nums = [5,7,7,8,8,10], target = 8
Output: [3,4]
Explanation: The first occurrence of 8 is at index 3, the last at index 4.

Example 2:
Input: nums = [5,7,7,8,8,10], target = 6
Output: [-1,-1]
Explanation: 6 is not in the array.

Example 3:
Input: nums = [], target = 0
Output: [-1,-1]
Explanation: Empty array, target cannot be found.
```

### Constraints

- `0 <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`
- `nums` is a non-decreasing array
- `-10^9 <= target <= 10^9`

---

## Problem Breakdown

### 1. What is being asked?

Given a sorted array, find the **first** (leftmost) and **last** (rightmost) index where a given target value appears. If the target is not in the array, return `[-1, -1]`. The solution must run in O(log n) time.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Sorted (non-decreasing) array of integers |
| `target` | `int` | The value to search for |

Important observations about the input:
- The array is **sorted in non-decreasing order** (may contain duplicates)
- The array can be **empty**
- Values can be **negative**
- There may be **multiple occurrences** of the target

### 3. What is the output?

- An array of two integers `[first, last]`
- `first` = index of the first occurrence of target
- `last` = index of the last occurrence of target
- If target is not found, return `[-1, -1]`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^5` | O(n) linear scan would pass, but problem requires O(log n) |
| Sorted array | Binary search is applicable |
| O(log n) required | Must use binary search, not linear scan |
| Duplicates possible | Standard binary search finds *any* occurrence; we need the *first* and *last* |

### 5. Step-by-step example analysis

#### Example 1: `nums = [5,7,7,8,8,10], target = 8`

```text
Array:   [5, 7, 7, 8, 8, 10]
Index:    0  1  2  3  4   5

Target = 8

Finding left bound (first occurrence):
  Binary search biased left — when nums[mid] == target, go left
  Result: index 3

Finding right bound (last occurrence):
  Binary search biased right — when nums[mid] == target, go right
  Result: index 4

Output: [3, 4]
```

#### Example 2: `nums = [5,7,7,8,8,10], target = 6`

```text
Array:   [5, 7, 7, 8, 8, 10]
Index:    0  1  2  3  4   5

Target = 6

Finding left bound:
  Binary search never finds 6
  Result: -1

Output: [-1, -1]
```

#### Example 3: `nums = [], target = 0`

```text
Empty array — nothing to search.

Output: [-1, -1]
```

### 6. Key Observations

1. **Sorted array = Binary Search** — The O(log n) requirement and sorted input directly point to binary search.
2. **Two separate searches** — Standard binary search finds *any* occurrence. We need two modified binary searches: one that finds the **leftmost** occurrence and one that finds the **rightmost** occurrence.
3. **Left-biased search** — When `nums[mid] == target`, instead of returning, move `right = mid - 1` to keep searching left. The answer is tracked in a variable.
4. **Right-biased search** — When `nums[mid] == target`, instead of returning, move `left = mid + 1` to keep searching right. The answer is tracked in a variable.
5. **Early termination** — If the left bound returns -1, the target doesn't exist, so the right bound must also be -1.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Binary Search (Left Bound) | Find the first position where target appears | `lower_bound` in C++ |
| Binary Search (Right Bound) | Find the last position where target appears | `upper_bound - 1` in C++ |

**Chosen pattern:** `Two Binary Searches`
**Reason:** One search finds the leftmost occurrence (left bound), and the other finds the rightmost occurrence (right bound). Both run in O(log n), giving O(log n) total.

---

## Approach 1: Two Binary Searches

### Thought process

> The array is sorted and we need O(log n) — binary search is the natural choice.
> Standard binary search stops when it finds the target, but we need the **first** and **last** positions.
>
> **Key insight:** Modify binary search to not stop when it finds the target.
> - For the **left bound**: when `nums[mid] == target`, record `mid` as a candidate and search **left** (`right = mid - 1`)
> - For the **right bound**: when `nums[mid] == target`, record `mid` as a candidate and search **right** (`left = mid + 1`)
>
> This way, each search runs in O(log n) and we get both bounds.

### Algorithm (step-by-step)

1. **Find left bound (first occurrence):**
   a. Initialize `left = 0`, `right = n - 1`, `result = -1`
   b. While `left <= right`:
      - Calculate `mid = left + (right - left) / 2`
      - If `nums[mid] == target`: record `result = mid`, search left (`right = mid - 1`)
      - If `nums[mid] < target`: search right (`left = mid + 1`)
      - If `nums[mid] > target`: search left (`right = mid - 1`)
   c. Return `result`

2. **Find right bound (last occurrence):**
   a. Initialize `left = 0`, `right = n - 1`, `result = -1`
   b. While `left <= right`:
      - Calculate `mid = left + (right - left) / 2`
      - If `nums[mid] == target`: record `result = mid`, search right (`left = mid + 1`)
      - If `nums[mid] < target`: search right (`left = mid + 1`)
      - If `nums[mid] > target`: search left (`right = mid - 1`)
   c. Return `result`

3. Return `[leftBound, rightBound]`

### Pseudocode

```text
function searchRange(nums, target):
    left_bound = findLeft(nums, target)
    if left_bound == -1:
        return [-1, -1]
    right_bound = findRight(nums, target)
    return [left_bound, right_bound]

function findLeft(nums, target):
    left = 0, right = n - 1, result = -1
    while left <= right:
        mid = left + (right - left) / 2
        if nums[mid] == target:
            result = mid
            right = mid - 1       // keep searching left
        elif nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return result

function findRight(nums, target):
    left = 0, right = n - 1, result = -1
    while left <= right:
        mid = left + (right - left) / 2
        if nums[mid] == target:
            result = mid
            left = mid + 1        // keep searching right
        elif nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log n) | Two binary searches, each O(log n). Total: 2 * O(log n) = O(log n). |
| **Space** | O(1) | Only a few variables — no extra data structures. |

### Implementation

#### Go

```go
// searchRange — Two Binary Searches approach
// Time: O(log n), Space: O(1)
func searchRange(nums []int, target int) []int {
    left := findLeft(nums, target)
    if left == -1 {
        return []int{-1, -1}
    }
    right := findRight(nums, target)
    return []int{left, right}
}

func findLeft(nums []int, target int) int {
    lo, hi := 0, len(nums)-1
    result := -1

    for lo <= hi {
        mid := lo + (hi-lo)/2
        if nums[mid] == target {
            result = mid
            hi = mid - 1 // keep searching left
        } else if nums[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }

    return result
}

func findRight(nums []int, target int) int {
    lo, hi := 0, len(nums)-1
    result := -1

    for lo <= hi {
        mid := lo + (hi-lo)/2
        if nums[mid] == target {
            result = mid
            lo = mid + 1 // keep searching right
        } else if nums[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }

    return result
}
```

#### Java

```java
class Solution {
    // searchRange — Two Binary Searches approach
    // Time: O(log n), Space: O(1)
    public int[] searchRange(int[] nums, int target) {
        int left = findLeft(nums, target);
        if (left == -1) {
            return new int[]{-1, -1};
        }
        int right = findRight(nums, target);
        return new int[]{left, right};
    }

    private int findLeft(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        int result = -1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                result = mid;
                hi = mid - 1; // keep searching left
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return result;
    }

    private int findRight(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        int result = -1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                result = mid;
                lo = mid + 1; // keep searching right
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return result;
    }
}
```

#### Python

```python
class Solution:
    def searchRange(self, nums: list[int], target: int) -> list[int]:
        """
        Two Binary Searches approach
        Time: O(log n), Space: O(1)
        """
        left = self.find_left(nums, target)
        if left == -1:
            return [-1, -1]
        right = self.find_right(nums, target)
        return [left, right]

    def find_left(self, nums: list[int], target: int) -> int:
        lo, hi = 0, len(nums) - 1
        result = -1

        while lo <= hi:
            mid = lo + (hi - lo) // 2
            if nums[mid] == target:
                result = mid
                hi = mid - 1  # keep searching left
            elif nums[mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return result

    def find_right(self, nums: list[int], target: int) -> int:
        lo, hi = 0, len(nums) - 1
        result = -1

        while lo <= hi:
            mid = lo + (hi - lo) // 2
            if nums[mid] == target:
                result = mid
                lo = mid + 1  # keep searching right
            elif nums[mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return result
```

### Dry Run

```text
Input: nums = [5, 7, 7, 8, 8, 10], target = 8

=== Finding Left Bound (First Occurrence of 8) ===

lo=0, hi=5, result=-1

Step 1: mid = (0+5)/2 = 2
        nums[2] = 7 < 8 → lo = 3
        lo=3, hi=5

Step 2: mid = (3+5)/2 = 4
        nums[4] = 8 == 8 → result = 4, hi = 3
        lo=3, hi=3

Step 3: mid = (3+3)/2 = 3
        nums[3] = 8 == 8 → result = 3, hi = 2
        lo=3, hi=2

lo > hi → STOP
Left bound = 3

=== Finding Right Bound (Last Occurrence of 8) ===

lo=0, hi=5, result=-1

Step 1: mid = (0+5)/2 = 2
        nums[2] = 7 < 8 → lo = 3
        lo=3, hi=5

Step 2: mid = (3+5)/2 = 4
        nums[4] = 8 == 8 → result = 4, lo = 5
        lo=5, hi=5

Step 3: mid = (5+5)/2 = 5
        nums[5] = 10 > 8 → hi = 4
        lo=5, hi=4

lo > hi → STOP
Right bound = 4

Result: [3, 4]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Two Binary Searches | O(log n) | O(1) | Meets O(log n) requirement, clean | Need to implement two binary search variants |

### Which solution to choose?

- **In an interview:** Two Binary Searches — clean, efficient, demonstrates binary search mastery
- **In production:** Two Binary Searches — optimal time and space
- **On Leetcode:** Two Binary Searches — the only approach that satisfies O(log n)
- **For learning:** Understand how modifying the "found" case in binary search gives you left/right bounds

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty array | `nums=[], target=0` | `[-1, -1]` | Nothing to search |
| 2 | Target not found | `nums=[5,7,7,8,8,10], target=6` | `[-1, -1]` | 6 is not in the array |
| 3 | Single element found | `nums=[1], target=1` | `[0, 0]` | Only one element, it matches |
| 4 | Single element not found | `nums=[1], target=2` | `[-1, -1]` | Only one element, no match |
| 5 | All same elements | `nums=[8,8,8,8,8], target=8` | `[0, 4]` | Target spans entire array |
| 6 | Target at start | `nums=[1,1,2,3,4], target=1` | `[0, 1]` | First two elements match |
| 7 | Target at end | `nums=[1,2,3,4,4], target=4` | `[3, 4]` | Last two elements match |
| 8 | Target smaller than all | `nums=[2,3,4], target=1` | `[-1, -1]` | Target is below range |
| 9 | Target larger than all | `nums=[2,3,4], target=5` | `[-1, -1]` | Target is above range |

---

## Common Mistakes

### Mistake 1: Using standard binary search (returns any occurrence)

```python
# WRONG — finds *some* index where target exists, not first/last
def search(nums, target):
    lo, hi = 0, len(nums) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if nums[mid] == target:
            return mid  # This could be any occurrence!
        elif nums[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1

# CORRECT — continue searching after finding target
def find_left(nums, target):
    lo, hi = 0, len(nums) - 1
    result = -1
    while lo <= hi:
        mid = (lo + hi) // 2
        if nums[mid] == target:
            result = mid
            hi = mid - 1  # keep searching left
        elif nums[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return result
```

**Reason:** Standard binary search returns the first occurrence it finds, which may be in the middle of a run of duplicates.

### Mistake 2: Integer overflow in mid calculation

```python
# WRONG — can overflow in languages with fixed-size integers (Java, C++)
mid = (lo + hi) / 2

# CORRECT — prevents integer overflow
mid = lo + (hi - lo) / 2
```

**Reason:** When `lo` and `hi` are both large (close to INT_MAX), `lo + hi` can overflow. Using `lo + (hi - lo) / 2` avoids this.

### Mistake 3: Using linear scan after finding one occurrence

```python
# WRONG — O(n) in the worst case (e.g., all elements are the target)
idx = binary_search(nums, target)
first = last = idx
while first > 0 and nums[first - 1] == target:
    first -= 1
while last < len(nums) - 1 and nums[last + 1] == target:
    last += 1

# CORRECT — use two separate binary searches for O(log n) total
first = find_left(nums, target)
last = find_right(nums, target)
```

**Reason:** If all n elements equal the target, the linear expansion takes O(n) time, violating the O(log n) requirement.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [35. Search Insert Position](https://leetcode.com/problems/search-insert-position/) | :green_circle: Easy | Binary search for insertion point |
| 2 | [278. First Bad Version](https://leetcode.com/problems/first-bad-version/) | :green_circle: Easy | Binary search for left boundary |
| 3 | [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | :yellow_circle: Medium | Modified binary search |
| 4 | [74. Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) | :yellow_circle: Medium | Binary search on matrix |
| 5 | [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | :yellow_circle: Medium | Binary search variant |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Phase 1:** Binary search for the left bound (first occurrence)
> - **Phase 2:** Binary search for the right bound (last occurrence)
> - Pointer visualization (`lo`, `hi`, `mid`) with color-coded cells
> - Step/Play/Pause/Reset controls with speed slider
> - Preset examples and custom input support
> - Detailed step log showing each binary search decision
