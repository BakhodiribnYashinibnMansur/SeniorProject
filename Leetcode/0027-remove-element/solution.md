# 0027. Remove Element

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Two Pointers — Same Direction](#approach-1-two-pointers--same-direction)
4. [Approach 2: Two Pointers — Opposite Direction](#approach-2-two-pointers--opposite-direction)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [27. Remove Element](https://leetcode.com/problems/remove-element/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Array`, `Two Pointers` |

### Description

> Given an integer array `nums` and an integer `val`, remove all occurrences of `val` in `nums` **in-place**. The order of the elements may be changed. Then return `k` — the number of elements in `nums` which are not equal to `val`.
>
> Custom Judge: The judge will test your solution with the following code:
> ```
> int[] nums = [...]; // Input array
> int val = ...;      // Value to remove
> int k = removeElement(nums, val); // Calls your implementation
> sort(nums, 0, k); // Sort the first k elements of nums
> for (int i = 0; i < k; i++) {
>     assert nums[i] != val;
> }
> ```
> If all assertions pass, then your solution will be accepted.

### Examples

```
Example 1:
Input: nums = [3,2,2,3], val = 3
Output: 2, nums = [2,2,_,_]
Explanation: Your function should return k = 2, with the first two elements of nums being 2.
It does not matter what you leave beyond the returned k (hence they are underscores).

Example 2:
Input: nums = [0,1,2,2,3,0,4,2], val = 2
Output: 5, nums = [0,1,4,0,3,_,_,_]
Explanation: Your function should return k = 5, with the first five elements of nums containing 0, 0, 1, 3, and 4.
Note that the five elements can be returned in any order.
It does not matter what values are set beyond the returned k.
```

### Constraints

- `0 <= nums.length <= 100`
- `0 <= nums[i] <= 50`
- `0 <= val <= 100`

---

## Problem Breakdown

### 1. What is being asked?

Remove all occurrences of a given value from the array **in-place** and return the count of remaining elements. The first `k` positions of the array must contain only elements that are not equal to `val`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers (modified in-place) |
| `val` | `int` | Value to remove |

Important observations about the input:
- The array can be **empty** (`nums.length == 0`)
- The array is **unsorted**
- `val` may not exist in the array at all
- All elements could equal `val`
- Values are non-negative (0 to 50)

### 3. What is the output?

- An integer `k` — the count of elements not equal to `val`
- The first `k` elements of `nums` must not contain `val`
- Order of the remaining elements **does not matter**
- Elements after index `k-1` are irrelevant

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 100` | Very small — any O(n) or even O(n^2) solution works |
| `0 <= nums[i] <= 50` | Small positive values only |
| `0 <= val <= 100` | `val` could be larger than any possible element |
| In-place | Cannot create a new array — must modify `nums` directly |

### 5. Step-by-step example analysis

#### Example 1: `nums = [3,2,2,3], val = 3`

```text
Initial state: nums = [3, 2, 2, 3], val = 3

We need to remove all 3s and keep all non-3 values.
Non-3 values: 2, 2 → k = 2

Result: k = 2, nums = [2, 2, _, _]
```

#### Example 2: `nums = [0,1,2,2,3,0,4,2], val = 2`

```text
Initial state: nums = [0, 1, 2, 2, 3, 0, 4, 2], val = 2

Non-2 values: 0, 1, 3, 0, 4 → k = 5

Result: k = 5, nums = [0, 1, 3, 0, 4, _, _, _]
```

### 6. Key Observations

1. **In-place** — We cannot use extra arrays. We must rearrange `nums` itself.
2. **Order doesn't matter** — This gives us flexibility. We can swap elements freely.
3. **Two strategies** — Either shift kept elements forward (same direction) or swap unwanted elements to the end (opposite direction).
4. **Two Pointers** — Both strategies use two pointers to track positions.

### 7. Pattern identification

| Pattern | Why it fits | Approach |
|---|---|---|
| Two Pointers (same direction) | "Slow" pointer tracks write position, "fast" scans | Copy non-val elements forward |
| Two Pointers (opposite direction) | Swap unwanted elements with end elements | Fewer operations when `val` is rare |

**Chosen pattern:** `Two Pointers`
**Reason:** In-place modification with a simple scan/swap naturally fits the Two Pointers pattern.

---

## Approach 1: Two Pointers — Same Direction

### Thought process

> Use two pointers moving in the same direction:
> - `slow` (write pointer) — points to where the next kept element should go
> - `fast` (read pointer) — scans every element
>
> When `fast` finds a non-val element, copy it to the `slow` position and advance `slow`.
> At the end, `slow` equals `k` — the count of kept elements.

### Algorithm (step-by-step)

1. Initialize `slow = 0`
2. Iterate `fast` from `0` to `n-1`
3. If `nums[fast] != val`:
   - Copy: `nums[slow] = nums[fast]`
   - Advance: `slow++`
4. Return `slow` (this is `k`)

### Pseudocode

```text
function removeElement(nums, val):
    slow = 0
    for fast = 0 to n-1:
        if nums[fast] != val:
            nums[slow] = nums[fast]
            slow++
    return slow
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the array. Each element is visited once. |
| **Space** | O(1) | Only two pointer variables. No extra memory. |

### Implementation

#### Go

```go
// removeElement — Two Pointers Same Direction
// Time: O(n), Space: O(1)
func removeElement(nums []int, val int) int {
    slow := 0
    for fast := 0; fast < len(nums); fast++ {
        if nums[fast] != val {
            nums[slow] = nums[fast]
            slow++
        }
    }
    return slow
}
```

#### Java

```java
class Solution {
    // removeElement — Two Pointers Same Direction
    // Time: O(n), Space: O(1)
    public int removeElement(int[] nums, int val) {
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        return slow;
    }
}
```

#### Python

```python
class Solution:
    def removeElement(self, nums: list[int], val: int) -> int:
        """
        Two Pointers Same Direction
        Time: O(n), Space: O(1)
        """
        slow = 0
        for fast in range(len(nums)):
            if nums[fast] != val:
                nums[slow] = nums[fast]
                slow += 1
        return slow
```

### Dry Run

```text
Input: nums = [3, 2, 2, 3], val = 3

slow=0

fast=0: nums[0]=3 == val → skip
        nums = [3, 2, 2, 3], slow=0

fast=1: nums[1]=2 != val → nums[0] = 2, slow=1
        nums = [2, 2, 2, 3], slow=1

fast=2: nums[2]=2 != val → nums[1] = 2, slow=2
        nums = [2, 2, 2, 3], slow=2

fast=3: nums[3]=3 == val → skip
        nums = [2, 2, 2, 3], slow=2

return slow = 2
Result: k=2, nums = [2, 2, _, _]
```

```text
Input: nums = [0, 1, 2, 2, 3, 0, 4, 2], val = 2

slow=0

fast=0: nums[0]=0 != val → nums[0]=0, slow=1
fast=1: nums[1]=1 != val → nums[1]=1, slow=2
fast=2: nums[2]=2 == val → skip
fast=3: nums[3]=2 == val → skip
fast=4: nums[4]=3 != val → nums[2]=3, slow=3
fast=5: nums[5]=0 != val → nums[3]=0, slow=4
fast=6: nums[6]=4 != val → nums[4]=4, slow=5
fast=7: nums[7]=2 == val → skip

return slow = 5
Result: k=5, nums = [0, 1, 3, 0, 4, _, _, _]

Total assignments: 5 (one per kept element)
```

---

## Approach 2: Two Pointers — Opposite Direction

### The insight

> In Approach 1, even when `val` is rare, we still copy almost every element.
> For example: `nums = [1,2,3,4,5], val = 5` — we copy elements 1,2,3,4 even though only 5 needs to be removed.
>
> **Better idea:** When we find an element equal to `val`, swap it with the last element and shrink the array from the right.
> This way, the number of assignments equals the number of elements to remove — useful when `val` is rare.

### Algorithm (step-by-step)

1. Initialize `left = 0`, `right = n - 1`
2. While `left <= right`:
   - If `nums[left] == val`:
     - Replace: `nums[left] = nums[right]`
     - Shrink: `right--`
     - **Do NOT advance `left`** — the swapped element might also be `val`
   - Else:
     - `left++`
3. Return `left` (or `right + 1`)

### Pseudocode

```text
function removeElement(nums, val):
    left = 0
    right = n - 1
    while left <= right:
        if nums[left] == val:
            nums[left] = nums[right]
            right--
        else:
            left++
    return left
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each element is visited at most once. `left` and `right` together cover all positions. |
| **Space** | O(1) | Only two pointer variables. |

> **Note on assignments:** This approach performs at most `k_removed` assignments (where `k_removed` is the count of val occurrences).
> Approach 1 performs `k_kept` assignments. When `val` is rare, Approach 2 is better. When `val` is common, Approach 1 is better.
> Both are O(n) time overall.

### Implementation

#### Go

```go
// removeElement — Two Pointers Opposite Direction
// Time: O(n), Space: O(1)
func removeElement(nums []int, val int) int {
    left := 0
    right := len(nums) - 1
    for left <= right {
        if nums[left] == val {
            nums[left] = nums[right]
            right--
        } else {
            left++
        }
    }
    return left
}
```

#### Java

```java
class Solution {
    // removeElement — Two Pointers Opposite Direction
    // Time: O(n), Space: O(1)
    public int removeElement(int[] nums, int val) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            if (nums[left] == val) {
                nums[left] = nums[right];
                right--;
            } else {
                left++;
            }
        }
        return left;
    }
}
```

#### Python

```python
class Solution:
    def removeElement(self, nums: list[int], val: int) -> int:
        """
        Two Pointers Opposite Direction
        Time: O(n), Space: O(1)
        """
        left = 0
        right = len(nums) - 1
        while left <= right:
            if nums[left] == val:
                nums[left] = nums[right]
                right -= 1
            else:
                left += 1
        return left
```

### Dry Run

```text
Input: nums = [3, 2, 2, 3], val = 3

left=0, right=3

Step 1: nums[0]=3 == val → nums[0] = nums[3] = 3, right=2
        nums = [3, 2, 2, 3], left=0, right=2

Step 2: nums[0]=3 == val → nums[0] = nums[2] = 2, right=1
        nums = [2, 2, 2, 3], left=0, right=1

Step 3: nums[0]=2 != val → left=1
        nums = [2, 2, 2, 3], left=1, right=1

Step 4: nums[1]=2 != val → left=2
        nums = [2, 2, 2, 3], left=2, right=1

left > right → exit loop

return left = 2
Result: k=2, nums = [2, 2, _, _]

Total assignments: 2 (only the removed elements)
```

```text
Input: nums = [0, 1, 2, 2, 3, 0, 4, 2], val = 2

left=0, right=7

Step 1: nums[0]=0 != val → left=1
Step 2: nums[1]=1 != val → left=2
Step 3: nums[2]=2 == val → nums[2] = nums[7] = 2, right=6
        nums = [0, 1, 2, 2, 3, 0, 4, 2]
Step 4: nums[2]=2 == val → nums[2] = nums[6] = 4, right=5
        nums = [0, 1, 4, 2, 3, 0, 4, 2]
Step 5: nums[2]=4 != val → left=3
Step 6: nums[3]=2 == val → nums[3] = nums[5] = 0, right=4
        nums = [0, 1, 4, 0, 3, 0, 4, 2]
Step 7: nums[3]=0 != val → left=4
Step 8: nums[4]=3 != val → left=5

left > right → exit loop

return left = 5
Result: k=5, nums = [0, 1, 4, 0, 3, _, _, _]

Total assignments: 3 (only the 3 occurrences of val=2)
```

---

## Complexity Comparison

| # | Approach | Time | Space | Assignments | Best for |
|---|---|---|---|---|---|
| 1 | Same Direction | O(n) | O(1) | k_kept (non-val count) | val is common |
| 2 | Opposite Direction | O(n) | O(1) | k_removed (val count) | val is rare |

### Which solution to choose?

- **In an interview:** Approach 1 — simpler, easy to explain, no bugs
- **On Leetcode:** Both are accepted — same time complexity
- **When val is rare:** Approach 2 — fewer assignments
- **For learning:** Both — they demonstrate two fundamental Two Pointers patterns

---

## Edge Cases

| # | Case | Input | Expected k | Reason |
|---|---|---|---|---|
| 1 | Empty array | `nums=[], val=1` | `0` | No elements to process |
| 2 | All same as val | `nums=[3,3,3], val=3` | `0` | Everything is removed |
| 3 | None equal val | `nums=[1,2,3], val=4` | `3` | Nothing to remove |
| 4 | Single element (keep) | `nums=[1], val=2` | `1` | Single non-val element |
| 5 | Single element (remove) | `nums=[1], val=1` | `0` | Single val element removed |
| 6 | Val at start | `nums=[3,1,2], val=3` | `2` | First element is removed |
| 7 | Val at end | `nums=[1,2,3], val=3` | `2` | Last element is removed |
| 8 | All same (not val) | `nums=[2,2,2], val=3` | `3` | All kept |

---

## Common Mistakes

### Mistake 1: Using extra array

```python
# ❌ WRONG — creates a new array (not in-place)
def removeElement(self, nums, val):
    return len([x for x in nums if x != val])

# ✅ CORRECT — modify nums in-place
def removeElement(self, nums, val):
    slow = 0
    for fast in range(len(nums)):
        if nums[fast] != val:
            nums[slow] = nums[fast]
            slow += 1
    return slow
```

**Reason:** The problem requires in-place modification. The judge checks the actual array content.

### Mistake 2: Advancing left when swapping (opposite direction)

```python
# ❌ WRONG — advancing left after swap
if nums[left] == val:
    nums[left] = nums[right]
    right -= 1
    left += 1  # BUG! The swapped element might also be val

# ✅ CORRECT — do NOT advance left after swap
if nums[left] == val:
    nums[left] = nums[right]
    right -= 1
    # left stays — we need to check the swapped element
```

**Reason:** `nums[right]` could also equal `val`. After swapping, we must re-check `nums[left]`.

### Mistake 3: Off-by-one in opposite direction

```python
# ❌ WRONG — using < instead of <=
while left < right:  # misses single-element case

# ✅ CORRECT — use <=
while left <= right:  # handles left == right (single element to check)
```

**Reason:** When `left == right`, that element still needs to be checked.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [26. Remove Duplicates from Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) | :green_circle: Easy | Same two-pointer pattern, in-place removal |
| 2 | [283. Move Zeroes](https://leetcode.com/problems/move-zeroes/) | :green_circle: Easy | Remove zeroes + keep order |
| 3 | [80. Remove Duplicates from Sorted Array II](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/) | :yellow_circle: Medium | In-place removal with condition |
| 4 | [203. Remove Linked List Elements](https://leetcode.com/problems/remove-linked-list-elements/) | :green_circle: Easy | Same concept, linked list |
| 5 | [844. Backspace String Compare](https://leetcode.com/problems/backspace-string-compare/) | :green_circle: Easy | Two pointers, in-place processing |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Same Direction** tab — slow/fast pointers copy non-val elements forward
> - **Opposite Direction** tab — left/right pointers swap val elements to the end
> - Step-by-step controls with Play/Pause, speed adjustment
> - Preset examples with different val values
