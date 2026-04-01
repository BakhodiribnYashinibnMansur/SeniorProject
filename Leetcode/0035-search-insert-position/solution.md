# 0035. Search Insert Position

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Binary Search](#approach-1-binary-search)
4. [Edge Cases](#edge-cases)
5. [Common Mistakes](#common-mistakes)
6. [Related Problems](#related-problems)
7. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [35. Search Insert Position](https://leetcode.com/problems/search-insert-position/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Array`, `Binary Search` |

### Description

> Given a sorted array of distinct integers and a target value, return the index if the target is found. If not, return the index where it would be inserted in order.
>
> You must write an algorithm with `O(log n)` runtime complexity.

### Examples

```
Example 1:
Input: nums = [1,3,5,6], target = 5
Output: 2
Explanation: 5 is found at index 2.

Example 2:
Input: nums = [1,3,5,6], target = 2
Output: 1
Explanation: 2 is not found. It would be inserted at index 1 (between 1 and 3).

Example 3:
Input: nums = [1,3,5,6], target = 7
Output: 4
Explanation: 7 is not found. It would be inserted at index 4 (after all elements).

Example 4:
Input: nums = [1,3,5,6], target = 0
Output: 0
Explanation: 0 is not found. It would be inserted at index 0 (before all elements).
```

### Constraints

- `1 <= nums.length <= 10^4`
- `-10^4 <= nums[i] <= 10^4`
- `nums` contains **distinct** values sorted in **ascending** order
- `-10^4 <= target <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Given a sorted array of distinct integers and a target, find the target's index if it exists. If it doesn't exist, return the index where it would be inserted to keep the array sorted.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Sorted array of distinct integers |
| `target` | `int` | The value to search for or insert |

Important observations about the input:
- The array is **sorted** in ascending order
- All values are **distinct** (no duplicates)
- The array always has at least 1 element
- The problem requires O(log n) — hinting at binary search

### 3. What is the output?

- A single **integer** — the index where the target is found, or where it should be inserted
- The result is always in range `[0, n]` (inclusive of both ends)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^4` | Small input, but O(log n) is required |
| Sorted, distinct | Perfect conditions for binary search |
| Result range `[0, n]` | Target can go before first or after last element |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1,3,5,6], target = 5`

```text
Array: [1, 3, 5, 6]
Target: 5

Binary Search:
  left=0, right=3 → mid=1 → nums[1]=3 < 5 → left=2
  left=2, right=3 → mid=2 → nums[2]=5 == 5 → FOUND at index 2

Result: 2
```

#### Example 2: `nums = [1,3,5,6], target = 2`

```text
Array: [1, 3, 5, 6]
Target: 2

Binary Search:
  left=0, right=3 → mid=1 → nums[1]=3 > 2 → right=0
  left=0, right=0 → mid=0 → nums[0]=1 < 2 → left=1
  left=1, right=0 → left > right → NOT FOUND

Insert position: left = 1
Result: 1
```

#### Example 3: `nums = [1,3,5,6], target = 7`

```text
Array: [1, 3, 5, 6]
Target: 7 (larger than all elements)

Binary Search:
  left=0, right=3 → mid=1 → nums[1]=3 < 7 → left=2
  left=2, right=3 → mid=2 → nums[2]=5 < 7 → left=3
  left=3, right=3 → mid=3 → nums[3]=6 < 7 → left=4
  left=4, right=3 → left > right → NOT FOUND

Insert position: left = 4
Result: 4
```

### 6. Key Observations

1. **Sorted + distinct = binary search** — The array is already sorted with no duplicates, making binary search straightforward.
2. **Insert position = left pointer** — When binary search finishes without finding the target, the `left` pointer is exactly where the target should be inserted.
3. **Why left works** — At termination, `left` points to the first element greater than target (or past the end), which is exactly the correct insert position.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Binary Search | Sorted array, O(log n) required | Search Insert Position (this problem) |

**Chosen pattern:** `Binary Search`
**Reason:** The array is sorted, values are distinct, and the problem explicitly requires O(log n). Binary search naturally gives both the found index and the insert position.

---

## Approach 1: Binary Search

### Thought process

> We need to find the target in a sorted array, or determine where it would go.
> Binary search is the natural choice: compare the target with the middle element,
> then narrow the search to the left or right half.
>
> Key insight: when the target is not found, the `left` pointer ends up at exactly
> the position where the target should be inserted.

### Algorithm (step-by-step)

1. Initialize `left = 0`, `right = n - 1`
2. While `left <= right`:
   a. Calculate `mid = left + (right - left) / 2` (avoids overflow)
   b. If `nums[mid] == target` → return `mid`
   c. If `nums[mid] < target` → `left = mid + 1`
   d. If `nums[mid] > target` → `right = mid - 1`
3. Target not found → return `left` (the insert position)

### Pseudocode

```text
function searchInsert(nums, target):
    left = 0, right = n - 1

    while left <= right:
        mid = left + (right - left) / 2

        if nums[mid] == target:
            return mid
        else if nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1

    return left
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log n) | Binary search halves the search space each step. |
| **Space** | O(1) | Only three variables: left, right, mid. |

### Implementation

#### Go

```go
// searchInsert — Binary Search approach
// Time: O(log n), Space: O(1)
func searchInsert(nums []int, target int) int {
    left, right := 0, len(nums)-1

    for left <= right {
        // Avoid integer overflow
        mid := left + (right-left)/2

        if nums[mid] == target {
            return mid
        } else if nums[mid] < target {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    // left is the correct insert position
    return left
}
```

#### Java

```java
class Solution {
    // searchInsert — Binary Search approach
    // Time: O(log n), Space: O(1)
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            // Avoid integer overflow
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // left is the correct insert position
        return left;
    }
}
```

#### Python

```python
class Solution:
    def searchInsert(self, nums: list[int], target: int) -> int:
        """
        Binary Search approach
        Time: O(log n), Space: O(1)
        """
        left, right = 0, len(nums) - 1

        while left <= right:
            # Avoid integer overflow (not an issue in Python, but good practice)
            mid = left + (right - left) // 2

            if nums[mid] == target:
                return mid
            elif nums[mid] < target:
                left = mid + 1
            else:
                right = mid - 1

        # left is the correct insert position
        return left
```

### Dry Run

```text
Input: nums = [1, 3, 5, 6], target = 5

Step 1: left=0, right=3
        mid = 0 + (3-0)/2 = 1
        nums[1] = 3 < 5 → left = 2

Step 2: left=2, right=3
        mid = 2 + (3-2)/2 = 2
        nums[2] = 5 == 5 → FOUND! Return 2

Result: 2
```

```text
Input: nums = [1, 3, 5, 6], target = 2

Step 1: left=0, right=3
        mid = 0 + (3-0)/2 = 1
        nums[1] = 3 > 2 → right = 0

Step 2: left=0, right=0
        mid = 0 + (0-0)/2 = 0
        nums[0] = 1 < 2 → left = 1

Step 3: left=1, right=0
        left > right → EXIT LOOP

Insert position: left = 1
Result: 1
```

```text
Input: nums = [1, 3, 5, 6], target = 7

Step 1: left=0, right=3
        mid = 1 → nums[1]=3 < 7 → left = 2

Step 2: left=2, right=3
        mid = 2 → nums[2]=5 < 7 → left = 3

Step 3: left=3, right=3
        mid = 3 → nums[3]=6 < 7 → left = 4

Step 4: left=4, right=3
        left > right → EXIT LOOP

Insert position: left = 4
Result: 4
```

```text
Input: nums = [1, 3, 5, 6], target = 0

Step 1: left=0, right=3
        mid = 1 → nums[1]=3 > 0 → right = 0

Step 2: left=0, right=0
        mid = 0 → nums[0]=1 > 0 → right = -1

Step 3: left=0, right=-1
        left > right → EXIT LOOP

Insert position: left = 0
Result: 0
```

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Target found at start | `nums=[1,3,5], target=1` | `0` | Target is the first element |
| 2 | Target found at end | `nums=[1,3,5], target=5` | `2` | Target is the last element |
| 3 | Insert before all | `nums=[1,3,5], target=0` | `0` | Target smaller than all elements |
| 4 | Insert after all | `nums=[1,3,5], target=6` | `3` | Target larger than all elements |
| 5 | Single element, found | `nums=[1], target=1` | `0` | Array has one element, target matches |
| 6 | Single element, insert before | `nums=[5], target=3` | `0` | Target smaller than sole element |
| 7 | Single element, insert after | `nums=[5], target=8` | `1` | Target larger than sole element |
| 8 | Insert in middle | `nums=[1,3,5,7], target=4` | `2` | Target goes between 3 and 5 |

---

## Common Mistakes

### Mistake 1: Using `(left + right) / 2` for mid calculation

```python
# WRONG — potential integer overflow in Java/C++ for large values
mid = (left + right) // 2

# CORRECT — overflow-safe calculation
mid = left + (right - left) // 2
```

**Reason:** When `left` and `right` are both large, their sum can overflow. Using `left + (right - left) / 2` avoids this.

### Mistake 2: Using `left < right` instead of `left <= right`

```python
# WRONG — misses the case when left == right
while left < right:
    ...

# CORRECT — must check the element when left == right
while left <= right:
    ...
```

**Reason:** When `left == right`, there's still one element to check. Skipping it means the target might not be found even when it exists.

### Mistake 3: Returning `mid` or `right` instead of `left` when not found

```python
# WRONG — mid and right don't give the correct insert position
return mid   # mid could be anywhere
return right  # right could be -1

# CORRECT — left is always the correct insert position
return left
```

**Reason:** After the loop, `left` points to the first element greater than target, which is exactly where the target should be inserted.

### Mistake 4: Off-by-one error in pointer updates

```python
# WRONG — causes infinite loop
left = mid      # Should be mid + 1
right = mid     # Should be mid - 1

# CORRECT — always move past mid
left = mid + 1
right = mid - 1
```

**Reason:** If `left = mid` when `mid == left`, the loop never progresses. Always move past `mid` to guarantee the search space shrinks.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [704. Binary Search](https://leetcode.com/problems/binary-search/) | :green_circle: Easy | Basic binary search on sorted array |
| 2 | [278. First Bad Version](https://leetcode.com/problems/first-bad-version/) | :green_circle: Easy | Binary search for boundary |
| 3 | [34. Find First and Last Position of Element in Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/) | :orange_circle: Medium | Binary search for boundaries with duplicates |
| 4 | [69. Sqrt(x)](https://leetcode.com/problems/sqrtx/) | :green_circle: Easy | Binary search on answer space |
| 5 | [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | :orange_circle: Medium | Binary search on modified sorted array |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Binary Search** visualization with left/mid/right pointer movement
> - Array elements shown as bars with pointer labels
> - Step-by-step log showing each comparison and decision
> - Preset examples and custom input support
> - Speed control for adjusting animation pace
