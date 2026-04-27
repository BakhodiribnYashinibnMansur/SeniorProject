# 0075. Sort Colors

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Counting Sort (Two Pass)](#approach-1-counting-sort-two-pass)
4. [Approach 2: Dutch National Flag (One Pass, In-Place)](#approach-2-dutch-national-flag-one-pass-in-place)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [75. Sort Colors](https://leetcode.com/problems/sort-colors/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Two Pointers`, `Sorting` |

### Description

> Given an array `nums` with `n` objects colored red, white, or blue, sort them **in-place** so that objects of the same color are adjacent, with the colors in the order red, white, and blue.
>
> We will use the integers `0`, `1`, and `2` to represent the color red, white, and blue, respectively.
>
> You must solve this problem without using the library's sort function.
>
> **Follow up:** Could you come up with a one-pass algorithm using only constant extra space?

### Examples

```
Example 1:
Input: nums = [2,0,2,1,1,0]
Output: [0,0,1,1,2,2]

Example 2:
Input: nums = [2,0,1]
Output: [0,1,2]
```

### Constraints

- `n == nums.length`
- `1 <= n <= 300`
- `nums[i]` is either `0`, `1`, or `2`.

---

## Problem Breakdown

### 1. What is being asked?

Sort an array of three distinct values in-place. The classic *Dutch National Flag* problem, named after E. Dijkstra.

### 2. What is the input?

An array of `0/1/2` values, length up to 300.

### 3. What is the output?

The same array, sorted in-place: `0`s first, then `1`s, then `2`s.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 300` | Speed easy. Constant space + one pass is the *follow-up* |
| Three values only | Counting sort with size-3 array works |

### 5. Step-by-step example analysis

#### `[2, 0, 2, 1, 1, 0]`

```text
Three pointers: low = 0, mid = 0, high = 5.

Iter 1: nums[mid]=2 → swap with high=5; high=4.
        Array: [0, 0, 2, 1, 1, 2]   wait no, let's redo.

Actually nums = [2, 0, 2, 1, 1, 0]
low=0, mid=0, high=5

mid=0: nums[0]=2 → swap nums[0], nums[5]; high=4. nums=[0,0,2,1,1,2]
mid=0: nums[0]=0 → swap nums[low=0], nums[mid=0] (no-op); low=1, mid=1.
mid=1: nums[1]=0 → swap nums[1], nums[1]; low=2, mid=2.
mid=2: nums[2]=2 → swap nums[2], nums[high=4]; high=3. nums=[0,0,1,1,2,2]
mid=2: nums[2]=1 → mid=3.
mid=3: nums[3]=1 → mid=4. mid > high, stop.

Final: [0,0,1,1,2,2]
```

### 6. Key Observations

1. **Three regions** -- `[0, low)` is `0`s, `[low, mid)` is `1`s, `(high, n)` is `2`s. `[mid, high]` is unprocessed.
2. **Single sweep** -- At each `mid`:
   - If `0`: swap with `low`, advance both `low` and `mid`.
   - If `1`: just advance `mid`.
   - If `2`: swap with `high`, decrement `high` (do **not** advance `mid`, because the swapped-in value is unknown).
3. **Counting sort alternative** -- Two passes: count, then write.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Three-Pointer (DNF) | Three-color partition |
| Counting Sort | Tiny alphabet |

**Chosen pattern:** `Dutch National Flag`.

---

## Approach 1: Counting Sort (Two Pass)

### Algorithm

1. Count zeros, ones, twos.
2. Overwrite the array.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation

#### Python

```python
class Solution:
    def sortColorsCount(self, nums: List[int]) -> None:
        c = [0, 0, 0]
        for x in nums: c[x] += 1
        i = 0
        for v in range(3):
            for _ in range(c[v]):
                nums[i] = v
                i += 1
```

---

## Approach 2: Dutch National Flag (One Pass, In-Place)

### Algorithm (step-by-step)

1. `low = 0`, `mid = 0`, `high = n - 1`.
2. While `mid <= high`:
   - If `nums[mid] == 0`: swap `nums[low], nums[mid]`; `low++; mid++`.
   - If `nums[mid] == 1`: `mid++`.
   - If `nums[mid] == 2`: swap `nums[mid], nums[high]`; `high--` (don't increment mid -- the swapped-in element is unknown).

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func sortColors(nums []int) {
    low, mid, high := 0, 0, len(nums)-1
    for mid <= high {
        switch nums[mid] {
        case 0:
            nums[low], nums[mid] = nums[mid], nums[low]
            low++
            mid++
        case 1:
            mid++
        case 2:
            nums[mid], nums[high] = nums[high], nums[mid]
            high--
        }
    }
}
```

#### Java

```java
class Solution {
    public void sortColors(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;
        while (mid <= high) {
            if (nums[mid] == 0) {
                int t = nums[low]; nums[low] = nums[mid]; nums[mid] = t;
                low++; mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else {
                int t = nums[mid]; nums[mid] = nums[high]; nums[high] = t;
                high--;
            }
        }
    }
}
```

#### Python

```python
class Solution:
    def sortColors(self, nums: List[int]) -> None:
        low, mid, high = 0, 0, len(nums) - 1
        while mid <= high:
            if nums[mid] == 0:
                nums[low], nums[mid] = nums[mid], nums[low]
                low += 1; mid += 1
            elif nums[mid] == 1:
                mid += 1
            else:
                nums[mid], nums[high] = nums[high], nums[mid]
                high -= 1
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Counting Sort | O(n) | O(1) | Easy | Two passes |
| 2 | Dutch National Flag | O(n) | O(1) | One pass, in-place | Trickier invariants |

### Which solution to choose?

- **In an interview:** Approach 2 (the asked follow-up)
- **In production:** Approach 2 if performance matters; otherwise either
- **On Leetcode:** Approach 2

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | All zeros | `low` ends at n; no 2s |
| 2 | All ones | All in middle region |
| 3 | All twos | `high` ends at -1 |
| 4 | Already sorted | No swaps needed but loop runs |
| 5 | Reverse sorted | Lots of swaps |
| 6 | Single element | Trivial |
| 7 | Mix of all three | Standard |

---

## Common Mistakes

### Mistake 1: Advancing `mid` after swap with `high`

```python
# WRONG — the swapped-in element from high hasn't been classified
elif nums[mid] == 2:
    nums[mid], nums[high] = nums[high], nums[mid]
    high -= 1
    mid += 1   # WRONG

# CORRECT — leave mid; we'll re-examine next iteration
```

**Reason:** When you swap `mid` with `high`, the new `nums[mid]` could be `0`, `1`, or `2`. We need to inspect it again.

### Mistake 2: `mid < high` instead of `mid <= high`

```python
# WRONG — leaves out one element when low/mid/high are at the same index
while mid < high: ...

# CORRECT
while mid <= high: ...
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [148. Sort List](https://leetcode.com/problems/sort-list/) | :yellow_circle: Medium | General sort |
| 2 | [905. Sort Array By Parity](https://leetcode.com/problems/sort-array-by-parity/) | :green_circle: Easy | Two-color partition |
| 3 | [283. Move Zeroes](https://leetcode.com/problems/move-zeroes/) | :green_circle: Easy | Partition variant |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Bar visualization of three colors
> - Three pointers `low`, `mid`, `high` walking with their regions colored
> - Step-by-step swap animation
