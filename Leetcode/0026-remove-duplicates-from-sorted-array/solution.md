# 0026. Remove Duplicates from Sorted Array

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Two Pointers (Optimal)](#approach-1-two-pointers-optimal)
4. [Approach 2: Brute Force / Extra Array](#approach-2-brute-force--extra-array)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [26. Remove Duplicates from Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Array`, `Two Pointers` |

### Description

> Given an integer array `nums` sorted in **non-decreasing order**, remove the duplicates **in-place** such that each unique element appears only **once**. The relative order of the elements should be kept the same. Then return the number of unique elements in `nums`.
>
> Consider the number of unique elements of `nums` to be `k`, to get accepted, you need to do the following things:
> - Change the array `nums` such that the first `k` elements of `nums` contain the unique elements in the order they were present in `nums` initially.
> - The remaining elements of `nums` are not important as well as the size of `nums`.
> - Return `k`.

### Examples

```
Example 1:
Input: nums = [1,1,2]
Output: 2, nums = [1,2,_]
Explanation: Your function should return k = 2, with the first two elements of nums being 1 and 2 respectively.
It does not matter what you leave beyond the returned k (hence they are underscores).

Example 2:
Input: nums = [0,0,1,1,1,2,2,3,3,4]
Output: 5, nums = [0,1,2,3,4,_,_,_,_,_]
Explanation: Your function should return k = 5, with the first five elements of nums being 0, 1, 2, 3, and 4 respectively.
It does not matter what you leave beyond the returned k.
```

### Constraints

- `1 <= nums.length <= 3 * 10^4`
- `-100 <= nums[i] <= 100`
- `nums` is sorted in **non-decreasing** order

---

## Problem Breakdown

### 1. What is being asked?

Remove duplicate elements from a sorted array **in-place** (without creating a new array). Return the count of unique elements `k`, and the first `k` elements of the original array must contain the unique values.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Sorted array of integers (non-decreasing order) |

Important observations about the input:
- The array is **sorted** — duplicates are always adjacent
- The array has at least 1 element
- Values range from -100 to 100
- Must modify in-place — cannot allocate a new array

### 3. What is the output?

- **An integer `k`** — the number of unique elements
- **Side effect:** the first `k` elements of `nums` must contain the unique values in order
- Elements beyond index `k-1` are irrelevant

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 3 * 10^4` | Any O(n) or O(n^2) solution works |
| `-100 <= nums[i] <= 100` | Small value range, but not relevant to algorithm choice |
| Sorted array | Duplicates are adjacent — this is the KEY insight |
| In-place | Cannot use O(n) extra space for a new array |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1, 1, 2]`

```text
Initial state: nums = [1, 1, 2]

The unique elements are: 1, 2 → k = 2

After processing: nums = [1, 2, _]
Return: 2
```

#### Example 2: `nums = [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]`

```text
Initial state: nums = [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]

The unique elements are: 0, 1, 2, 3, 4 → k = 5

After processing: nums = [0, 1, 2, 3, 4, _, _, _, _, _]
Return: 5
```

### 6. Key Observations

1. **Sorted array** — Duplicates are always adjacent. We never need to search far for duplicates.
2. **In-place** — We must overwrite the array, not create a new one.
3. **Two Pointers** — A "slow" pointer tracks the position for the next unique element, a "fast" pointer scans through the array.
4. **First element is always unique** — Since the array has at least 1 element, `nums[0]` is always part of the result.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two Pointers | Slow/fast pointer on sorted array | This problem |
| Extra Array | Copy unique elements to new array | Works but violates in-place |
| Set/Hash | Track seen elements | Unnecessary — array is sorted |

**Chosen pattern:** `Two Pointers`
**Reason:** The array is sorted, so duplicates are adjacent. A slow pointer tracks the write position, a fast pointer scans. O(n) time, O(1) space.

---

## Approach 1: Two Pointers (Optimal)

### Thought process

> Since the array is sorted, all duplicate values are grouped together.
> We use two pointers:
> - `slow` — points to the last position of the unique portion
> - `fast` — scans through the entire array
>
> When `fast` finds a new unique element (different from `nums[slow]`),
> we increment `slow` and copy the new element there.

### Algorithm (step-by-step)

1. If the array is empty, return 0
2. Initialize `slow = 0` (first element is always unique)
3. Iterate `fast` from 1 to `n-1`
4. If `nums[fast] != nums[slow]` — found a new unique element:
   - Increment `slow`
   - Copy `nums[fast]` to `nums[slow]`
5. Return `slow + 1` (the count of unique elements)

### Pseudocode

```text
function removeDuplicates(nums):
    if nums is empty:
        return 0

    slow = 0
    for fast = 1 to n-1:
        if nums[fast] != nums[slow]:
            slow++
            nums[slow] = nums[fast]
    return slow + 1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the array. Each element is visited once. |
| **Space** | O(1) | Only two pointer variables. No extra memory. |

### Implementation

#### Go

```go
// removeDuplicates — Two Pointers approach
// Time: O(n), Space: O(1)
func removeDuplicates(nums []int) int {
    if len(nums) == 0 {
        return 0
    }

    // slow points to the last unique element
    slow := 0

    for fast := 1; fast < len(nums); fast++ {
        // Found a new unique element
        if nums[fast] != nums[slow] {
            slow++
            nums[slow] = nums[fast]
        }
    }

    // slow is 0-indexed, so count = slow + 1
    return slow + 1
}
```

#### Java

```java
class Solution {
    // removeDuplicates — Two Pointers approach
    // Time: O(n), Space: O(1)
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        // slow points to the last unique element
        int slow = 0;

        for (int fast = 1; fast < nums.length; fast++) {
            // Found a new unique element
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }

        // slow is 0-indexed, so count = slow + 1
        return slow + 1;
    }
}
```

#### Python

```python
class Solution:
    def removeDuplicates(self, nums: list[int]) -> int:
        """
        Two Pointers approach
        Time: O(n), Space: O(1)
        """
        if not nums:
            return 0

        # slow points to the last unique element
        slow = 0

        for fast in range(1, len(nums)):
            # Found a new unique element
            if nums[fast] != nums[slow]:
                slow += 1
                nums[slow] = nums[fast]

        # slow is 0-indexed, so count = slow + 1
        return slow + 1
```

### Dry Run

```text
Input: nums = [1, 1, 2]

slow=0, fast starts at 1

Step 1: fast=1, nums[1]=1, nums[slow]=nums[0]=1
        1 == 1 → duplicate, skip
        nums = [1, 1, 2], slow=0

Step 2: fast=2, nums[2]=2, nums[slow]=nums[0]=1
        2 != 1 → new unique!
        slow = 1, nums[1] = 2
        nums = [1, 2, 2], slow=1

Return: slow + 1 = 2
Result: k=2, nums = [1, 2, _]  ✅
```

```text
Input: nums = [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]

slow=0, fast starts at 1

Step 1: fast=1, nums[1]=0, nums[0]=0 → 0==0 skip
Step 2: fast=2, nums[2]=1, nums[0]=0 → 1!=0 ✅ slow=1, nums[1]=1
        nums = [0, 1, 1, 1, 1, 2, 2, 3, 3, 4]
Step 3: fast=3, nums[3]=1, nums[1]=1 → 1==1 skip
Step 4: fast=4, nums[4]=1, nums[1]=1 → 1==1 skip
Step 5: fast=5, nums[5]=2, nums[1]=1 → 2!=1 ✅ slow=2, nums[2]=2
        nums = [0, 1, 2, 1, 1, 2, 2, 3, 3, 4]
Step 6: fast=6, nums[6]=2, nums[2]=2 → 2==2 skip
Step 7: fast=7, nums[7]=3, nums[2]=2 → 3!=2 ✅ slow=3, nums[3]=3
        nums = [0, 1, 2, 3, 1, 2, 2, 3, 3, 4]
Step 8: fast=8, nums[8]=3, nums[3]=3 → 3==3 skip
Step 9: fast=9, nums[9]=4, nums[3]=3 → 4!=3 ✅ slow=4, nums[4]=4
        nums = [0, 1, 2, 3, 4, 2, 2, 3, 3, 4]

Return: slow + 1 = 5
Result: k=5, nums = [0, 1, 2, 3, 4, _, _, _, _, _]  ✅

Total operations: 9 comparisons (one pass)
```

---

## Approach 2: Brute Force / Extra Array

### Thought process

> The simplest idea: create a new array, copy only unique elements into it,
> then copy back to the original array.
> This violates the "in-place" constraint but helps understand the problem.

### Algorithm (step-by-step)

1. Create a new empty list `unique`
2. Traverse `nums` — if the current element differs from the previous one, add it to `unique`
3. Copy `unique` back into `nums`
4. Return the length of `unique`

### Pseudocode

```text
function removeDuplicates(nums):
    unique = [nums[0]]
    for i = 1 to n-1:
        if nums[i] != nums[i-1]:
            unique.append(nums[i])
    for i = 0 to len(unique)-1:
        nums[i] = unique[i]
    return len(unique)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Two passes through the array. |
| **Space** | O(n) | Extra array to store unique elements. |

### Implementation

#### Go

```go
// removeDuplicates — Extra Array approach
// Time: O(n), Space: O(n)
func removeDuplicates(nums []int) int {
    if len(nums) == 0 {
        return 0
    }

    // Collect unique elements
    unique := []int{nums[0]}
    for i := 1; i < len(nums); i++ {
        if nums[i] != nums[i-1] {
            unique = append(unique, nums[i])
        }
    }

    // Copy back to original array
    copy(nums, unique)

    return len(unique)
}
```

#### Java

```java
import java.util.ArrayList;

class Solution {
    // removeDuplicates — Extra Array approach
    // Time: O(n), Space: O(n)
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        // Collect unique elements
        ArrayList<Integer> unique = new ArrayList<>();
        unique.add(nums[0]);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                unique.add(nums[i]);
            }
        }

        // Copy back to original array
        for (int i = 0; i < unique.size(); i++) {
            nums[i] = unique.get(i);
        }

        return unique.size();
    }
}
```

#### Python

```python
class Solution:
    def removeDuplicates(self, nums: list[int]) -> int:
        """
        Extra Array approach
        Time: O(n), Space: O(n)
        """
        if not nums:
            return 0

        # Collect unique elements
        unique = [nums[0]]
        for i in range(1, len(nums)):
            if nums[i] != nums[i - 1]:
                unique.append(nums[i])

        # Copy back to original array
        for i in range(len(unique)):
            nums[i] = unique[i]

        return len(unique)
```

### Dry Run

```text
Input: nums = [1, 1, 2]

Step 1: unique = [1]  (start with first element)

Step 2: i=1, nums[1]=1, nums[0]=1 → 1==1 skip
Step 3: i=2, nums[2]=2, nums[1]=1 → 2!=1 → unique = [1, 2]

Copy back: nums[0]=1, nums[1]=2 → nums = [1, 2, 2]
Return: 2  ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Two Pointers | O(n) | O(1) | Optimal, true in-place, single pass | Slightly harder to understand |
| 2 | Extra Array | O(n) | O(n) | Simple, easy to understand | Violates in-place constraint, extra memory |

### Which solution to choose?

- **In an interview:** Approach 1 (Two Pointers) — optimal time and space, demonstrates in-place modification skill
- **In production:** Approach 1 — no extra memory allocation
- **On Leetcode:** Approach 1 — best Space Complexity
- **For learning:** Both — Approach 2 helps understand the logic, Approach 1 teaches Two Pointers pattern

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single element | `nums=[1]` | `1` | No duplicates possible |
| 2 | All same | `nums=[1,1,1,1]` | `1` | All duplicates |
| 3 | No duplicates | `nums=[1,2,3,4]` | `4` | Already unique |
| 4 | Two elements same | `nums=[1,1]` | `1` | Simplest duplicate case |
| 5 | Two elements different | `nums=[1,2]` | `2` | No duplicates |
| 6 | Negative numbers | `nums=[-3,-1,0,0,2]` | `4` | Negative values work the same |
| 7 | Large duplicates | `nums=[0,0,0,0,1,1,1,2]` | `3` | Many consecutive duplicates |

---

## Common Mistakes

### Mistake 1: Starting fast from 0 instead of 1

```python
# ❌ WRONG — comparing element with itself
slow = 0
for fast in range(len(nums)):  # starts from 0
    if nums[fast] != nums[slow]:
        ...

# ✅ CORRECT — start fast from 1
slow = 0
for fast in range(1, len(nums)):
    if nums[fast] != nums[slow]:
        ...
```

**Reason:** When `fast=0`, `nums[fast] == nums[slow]` is always true (both point to index 0). Starting from 1 avoids this redundant comparison.

### Mistake 2: Forgetting to increment slow before writing

```python
# ❌ WRONG — overwriting the last unique element
if nums[fast] != nums[slow]:
    nums[slow] = nums[fast]  # overwrites current unique!
    slow += 1

# ✅ CORRECT — increment first, then write
if nums[fast] != nums[slow]:
    slow += 1
    nums[slow] = nums[fast]
```

**Reason:** `nums[slow]` holds the last unique value. We must move `slow` forward **before** writing, otherwise we overwrite the current unique element.

### Mistake 3: Returning slow instead of slow + 1

```python
# ❌ WRONG — slow is 0-indexed
return slow  # For [1,2] → slow=1, but answer is 2

# ✅ CORRECT — count = slow + 1
return slow + 1
```

**Reason:** `slow` is a 0-based index. The count of unique elements is `slow + 1`.

### Mistake 4: Comparing with previous element instead of slow pointer

```python
# ❌ WRONG — comparing adjacent elements
if nums[fast] != nums[fast - 1]:
    slow += 1
    nums[slow] = nums[fast]

# ✅ CORRECT — compare with slow pointer
if nums[fast] != nums[slow]:
    slow += 1
    nums[slow] = nums[fast]
```

**Reason:** Both work for this specific problem (since the array is sorted), but comparing with `nums[slow]` is the canonical Two Pointers pattern and generalizes better.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [27. Remove Element](https://leetcode.com/problems/remove-element/) | :green_circle: Easy | Same Two Pointers pattern, remove specific value |
| 2 | [80. Remove Duplicates from Sorted Array II](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/) | :yellow_circle: Medium | Allow at most 2 duplicates |
| 3 | [83. Remove Duplicates from Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list/) | :green_circle: Easy | Same concept on Linked List |
| 4 | [283. Move Zeroes](https://leetcode.com/problems/move-zeroes/) | :green_circle: Easy | Two Pointers to move elements |
| 5 | [977. Squares of a Sorted Array](https://leetcode.com/problems/squares-of-a-sorted-array/) | :green_circle: Easy | Two Pointers on sorted array |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Two Pointers** — `slow` (green) and `fast` (blue) pointers move through the array
> - Unique elements are highlighted in green, duplicates in red
> - Step-by-step visualization of how elements are overwritten
> - Preset examples and custom input support
