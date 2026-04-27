# 0088. Merge Sorted Array

## Problem

| | |
|---|---|
| **Leetcode** | [88. Merge Sorted Array](https://leetcode.com/problems/merge-sorted-array/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Array`, `Two Pointers`, `Sorting` |

> You are given two integer arrays `nums1` and `nums2`, sorted in **non-decreasing order**, and two integers `m` and `n`, representing the number of elements in `nums1` and `nums2` respectively.
>
> **Merge** `nums1` and `nums2` into a single array sorted in **non-decreasing order**.
>
> The final sorted array should not be returned by the function, but instead be *stored inside the array* `nums1`. To accommodate this, `nums1` has a length of `m + n`, where the first `m` elements denote the elements that should be merged, and the last `n` elements are set to `0` and should be ignored. `nums2` has a length of `n`.

### Examples

```
Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
Output: [1,2,2,3,5,6]

Input: nums1 = [1], m = 1, nums2 = [], n = 0
Output: [1]

Input: nums1 = [0], m = 0, nums2 = [1], n = 1
Output: [1]
```

### Constraints

- `nums1.length == m + n`
- `nums2.length == n`
- `0 <= m, n <= 200`
- `1 <= m + n <= 200`
- `-10^9 <= nums1[i], nums2[j] <= 10^9`

---

## Approach: Two Pointers from the End

### Idea

Walk from the back: place the larger of `nums1[i]` and `nums2[j]` at `nums1[k]` where `k = m + n - 1`. Decrement accordingly. After exhausting `nums2` we are done; remaining `nums1` already in place.

### Algorithm

1. `i = m - 1, j = n - 1, k = m + n - 1`.
2. While `j >= 0`:
   - If `i >= 0 && nums1[i] > nums2[j]`: `nums1[k] = nums1[i]; i--`.
   - Else: `nums1[k] = nums2[j]; j--`.
   - `k--`.

### Complexity

- Time: O(m + n)
- Space: O(1)

### Implementation

#### Go

```go
func merge(nums1 []int, m int, nums2 []int, n int) {
    i, j, k := m-1, n-1, m+n-1
    for j >= 0 {
        if i >= 0 && nums1[i] > nums2[j] {
            nums1[k] = nums1[i]
            i--
        } else {
            nums1[k] = nums2[j]
            j--
        }
        k--
    }
}
```

#### Java

```java
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1, k = m + n - 1;
        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) {
                nums1[k--] = nums1[i--];
            } else {
                nums1[k--] = nums2[j--];
            }
        }
    }
}
```

#### Python

```python
class Solution:
    def merge(self, nums1, m, nums2, n):
        i, j, k = m - 1, n - 1, m + n - 1
        while j >= 0:
            if i >= 0 and nums1[i] > nums2[j]:
                nums1[k] = nums1[i]; i -= 1
            else:
                nums1[k] = nums2[j]; j -= 1
            k -= 1
```

---

## Edge Cases

- `m = 0`: nums1 is all zeros → just copy nums2.
- `n = 0`: do nothing.
- All nums2 < all nums1: shifts everything right.
- All nums2 > all nums1: append.

---

## Visual Animation

> [animation.html](./animation.html)
