# 0080. Remove Duplicates from Sorted Array II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Two Pointers (Generic, At-Most-K)](#approach-1-two-pointers-generic-at-most-k)
4. [Approach 2: Two Pointers (Specialized for K=2)](#approach-2-two-pointers-specialized-for-k2)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [80. Remove Duplicates from Sorted Array II](https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Two Pointers` |

### Description

> Given an integer array `nums` sorted in **non-decreasing order**, remove some duplicates **in-place** such that each unique element appears **at most twice**. The relative order of the elements should be kept the **same**.
>
> Since it is impossible to change the length of the array in some languages, you must instead have the result be placed in the **first part** of the array `nums`. More formally, if there are `k` elements after removing the duplicates, then the first `k` elements of `nums` should hold the final result. It does not matter what you leave beyond the first `k` elements.
>
> Return `k` *after placing the final result in the first* `k` *slots of* `nums`.

### Examples

```
Example 1:
Input: nums = [1,1,1,2,2,3]
Output: 5, nums = [1,1,2,2,3,_]

Example 2:
Input: nums = [0,0,1,1,1,1,2,3,3]
Output: 7, nums = [0,0,1,1,2,3,3,_,_]
```

### Constraints

- `1 <= nums.length <= 3 * 10^4`
- `-10^4 <= nums[i] <= 10^4`
- `nums` is sorted in non-decreasing order.

---

## Problem Breakdown

### 1. What is being asked?

Compact the array so each value appears at most twice, in-place, preserving order.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Sorted ascending |

### 3. What is the output?

The length `k` of the compacted prefix.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| Sorted | Duplicates are adjacent |
| `n <= 3 * 10^4` | O(n) easy |

### 5. Step-by-step example analysis

#### `[1, 1, 1, 2, 2, 3]`

```text
Two-pointer trick: write index `i` (starts at 2 — keep first 2 always).
For each j = 2..n-1:
  If nums[j] != nums[i - 2]: nums[i] = nums[j]; i++.
  Else: skip (would create a 3rd consecutive copy).

Walk:
  i = 2 (kept [1,1])
  j=2: nums[j]=1, nums[i-2]=1 → skip. i=2.
  j=3: nums[j]=2, nums[i-2]=1 → write. nums=[1,1,2,2,2,3], i=3.
  j=4: nums[j]=2, nums[i-2]=1 → write. nums=[1,1,2,2,2,3], i=4.
  j=5: nums[j]=3, nums[i-2]=2 → write. nums=[1,1,2,2,3,3], i=5.

Return 5.
```

### 6. Key Observations

1. **At-most-2 invariant** -- We allow `nums[i] == nums[i-1] == nums[i-2]` to be impossible by checking the new value against `nums[i-2]`.
2. **Generalizes to "at most k"** -- Replace the `2` with `k`: write index starts at `k`, compare with `nums[i-k]`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Two pointers | Read pointer + write pointer, classic compaction |

**Chosen pattern:** `Two Pointers (At-Most-K)`.

---

## Approach 1: Two Pointers (Generic, At-Most-K)

### Algorithm

1. If `len(nums) <= k`, return `len(nums)`.
2. `i = k` (first `k` always kept).
3. For `j = k..n-1`:
   - If `nums[j] != nums[i - k]`: `nums[i] = nums[j]; i++`.
4. Return `i`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation (k = 2)

#### Go

```go
func removeDuplicates(nums []int) int {
    k := 2
    if len(nums) <= k {
        return len(nums)
    }
    i := k
    for j := k; j < len(nums); j++ {
        if nums[j] != nums[i-k] {
            nums[i] = nums[j]
            i++
        }
    }
    return i
}
```

#### Java

```java
class Solution {
    public int removeDuplicates(int[] nums) {
        int k = 2;
        if (nums.length <= k) return nums.length;
        int i = k;
        for (int j = k; j < nums.length; j++) {
            if (nums[j] != nums[i - k]) {
                nums[i] = nums[j];
                i++;
            }
        }
        return i;
    }
}
```

#### Python

```python
class Solution:
    def removeDuplicates(self, nums: List[int]) -> int:
        k = 2
        if len(nums) <= k: return len(nums)
        i = k
        for j in range(k, len(nums)):
            if nums[j] != nums[i - k]:
                nums[i] = nums[j]
                i += 1
        return i
```

### Dry Run

```text
nums = [0,0,1,1,1,1,2,3,3]
k = 2

Initial: keep first 2 → [0, 0, ...], i = 2
j=2: nums[2]=1, nums[i-2]=0 → write 1; i=3 → nums=[0,0,1,1,1,1,2,3,3]
j=3: nums[3]=1, nums[i-2]=0 → write 1; i=4 → nums=[0,0,1,1,1,1,2,3,3]
j=4: nums[4]=1, nums[i-2]=1 → skip
j=5: nums[5]=1, nums[i-2]=1 → skip
j=6: nums[6]=2, nums[i-2]=1 → write; i=5 → nums=[0,0,1,1,2,1,2,3,3]
j=7: nums[7]=3, nums[i-2]=1 → write; i=6 → nums=[0,0,1,1,2,3,2,3,3]
j=8: nums[8]=3, nums[i-2]=2 → write; i=7 → nums=[0,0,1,1,2,3,3,3,3]

Return i = 7. First 7 elements: [0,0,1,1,2,3,3].
```

---

## Approach 2: Two Pointers (Specialized for K=2)

### Idea

> Same algorithm, hard-coded for `k = 2`.

> Listed for clarity; identical performance.

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Generic K | O(n) | O(1) | Generalizes | -- |
| 2 | Specialized | O(n) | O(1) | Slightly fewer ops | Less reusable |

### Which solution to choose?

Approach 1 always.

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | `n <= 2` | Already valid |
| 2 | All same | Keep two |
| 3 | All distinct | Unchanged |
| 4 | Twos only | Keep first two of each value |
| 5 | Long runs | Compact each run |

---

## Common Mistakes

### Mistake 1: Comparing with `nums[i - 1]` instead of `nums[i - k]`

```python
# WRONG — keeps only 1 copy of each (this is Problem 26)
if nums[j] != nums[i - 1]: ...

# CORRECT — for at-most-2, compare with i-2
if nums[j] != nums[i - 2]: ...
```

### Mistake 2: Starting `i` at 0 or 1

```python
# WRONG — would over-compact
i = 0

# CORRECT — first k elements are always kept
i = k
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [26. Remove Duplicates from Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) | :green_circle: Easy | At-most-1 version |
| 2 | [27. Remove Element](https://leetcode.com/problems/remove-element/) | :green_circle: Easy | Two-pointer compaction |
| 3 | [283. Move Zeroes](https://leetcode.com/problems/move-zeroes/) | :green_circle: Easy | Same compact pattern |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Two pointers `i`, `j` walking the array
> - Highlight comparisons with `nums[i-2]`
> - Step-by-step writes
