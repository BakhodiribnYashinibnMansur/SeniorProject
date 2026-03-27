# 0004. Median of Two Sorted Arrays

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Merge and Sort)](#approach-1-brute-force-merge-and-sort)
4. [Approach 2: Optimal (Binary Search on Smaller Array)](#approach-2-optimal-binary-search-on-smaller-array)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) |
| **Difficulty** | 🔴 Hard |
| **Tags** | `Array`, `Binary Search`, `Divide and Conquer` |

### Description

> Given two sorted arrays `nums1` and `nums2` of size `m` and `n` respectively, return **the median** of the two sorted arrays.
>
> The overall run time complexity should be **O(log (m+n))**.

### Examples

```
Example 1:
Input:  nums1 = [1,3], nums2 = [2]
Output: 2.00000
Explanation: merged = [1,2,3], median = 2

Example 2:
Input:  nums1 = [1,2], nums2 = [3,4]
Output: 2.50000
Explanation: merged = [1,2,3,4], median = (2+3)/2 = 2.5
```

### Constraints

- `0 <= m, n <= 1000`
- `1 <= m + n <= 2000`
- `-10^6 <= nums1[i], nums2[i] <= 10^6`

---

## Problem Breakdown

### 1. What is being asked?

Find the **median value** of the union of two sorted arrays **without fully merging them**, in O(log(m+n)) time.

The median splits a sorted sequence into two equal halves:
- **Odd total** length: the single middle element
- **Even total** length: the average of the two middle elements

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums1` | `int[]` | First sorted array, length m |
| `nums2` | `int[]` | Second sorted array, length n |

Key observations about the input:
- Both arrays are **already sorted** in non-decreasing order
- Either array can be **empty** (`m = 0` or `n = 0`)
- Total length is at least 1 (`m + n >= 1`)
- Values can be negative or zero

### 3. What is the output?

- A **single floating-point number** representing the median
- Expressed to 5 decimal places on LeetCode
- When total length is even: `(nums[mid-1] + nums[mid]) / 2.0`
- When total length is odd: `nums[mid]`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 1000` | Total elements ≤ 2000 — brute force (merge) is fast enough for submission, but O(log) is required |
| `m + n >= 1` | At least one element always exists — no empty-result edge case |
| Values up to `10^6` | int32 is sufficient; no overflow risk when summing two medians |
| **O(log(m+n)) required** | Cannot use linear merge — must use Binary Search |

### 5. Step-by-step example analysis

#### Example 1: `nums1 = [1,3], nums2 = [2]`

```text
Merged view (conceptual): [1, 2, 3]
Total length = 3 (odd)
Middle index = 1
Median = merged[1] = 2

Answer: 2.00000
```

#### Example 2: `nums1 = [1,2], nums2 = [3,4]`

```text
Merged view (conceptual): [1, 2, 3, 4]
Total length = 4 (even)
Middle indices = 1 and 2
Median = (merged[1] + merged[2]) / 2 = (2 + 3) / 2 = 2.5

Answer: 2.50000
```

### 6. Key Observations

1. **Partition insight** — If we split the combined array into left and right halves, the median depends only on the values at the partition boundary (max-left, min-right).
2. **Two-array partition** — We don't need to merge. We can split `nums1` at position `i` and `nums2` at position `j = half - i` so the left half has exactly `(m+n+1)/2` elements.
3. **Binary search on i** — `i` ranges from 0 to m. For a given `i`, `j` is determined. We binary-search to find the valid partition.
4. **Valid partition condition** — `maxLeft1 <= minRight2` AND `maxLeft2 <= minRight1`.
5. **Always search the smaller array** — Guarantees O(log(min(m,n))).

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Binary Search | The partition index on a sorted range has monotonic validity | This problem |
| Divide and Conquer | Recursively reduce the problem to finding k-th element | Alternative approach |
| Merge (Brute Force) | Direct — combine and index | Works, but O(m+n) |

**Chosen pattern:** `Binary Search (Partition)`
**Reason:** Achieves O(log(min(m,n))) — matches the required complexity constraint.

---

## Approach 1: Brute Force (Merge and Sort)

### Thought process

> The simplest idea: combine both arrays into one, sort it, then directly compute the median.
> The merged array is already "almost sorted" (both inputs are sorted), so a simple merge
> gives O(m+n) time rather than O((m+n)log(m+n)) — but we still call it "Brute Force"
> because it doesn't satisfy the O(log) requirement.

### Algorithm (step-by-step)

1. Concatenate `nums1` and `nums2` into a single array `merged`
2. Sort `merged`
3. Let `total = len(merged)`
4. If `total` is odd → return `merged[total/2]`
5. If `total` is even → return `(merged[total/2 - 1] + merged[total/2]) / 2.0`

### Pseudocode

```text
function findMedianSortedArrays(nums1, nums2):
    merged = nums1 + nums2
    sort(merged)
    total = len(merged)
    if total % 2 == 1:
        return merged[total / 2]
    else:
        return (merged[total/2 - 1] + merged[total/2]) / 2.0
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O((m+n)log(m+n)) | Sorting dominates; a proper two-pointer merge gives O(m+n) |
| **Space** | O(m+n) | Merged array stores all elements |

### Implementation

#### Go

```go
// findMedianSortedArraysBrute — Merge and Sort
// Time: O((m+n)log(m+n)), Space: O(m+n)
func findMedianSortedArraysBrute(nums1 []int, nums2 []int) float64 {
    merged := append(append([]int{}, nums1...), nums2...)
    sort.Ints(merged)
    total := len(merged)
    if total%2 == 1 {
        return float64(merged[total/2])
    }
    return float64(merged[total/2-1]+merged[total/2]) / 2.0
}
```

#### Java

```java
// Brute Force — Merge and Sort
// Time: O((m+n)log(m+n)), Space: O(m+n)
public double findMedianBrute(int[] nums1, int[] nums2) {
    int[] merged = new int[nums1.length + nums2.length];
    System.arraycopy(nums1, 0, merged, 0, nums1.length);
    System.arraycopy(nums2, 0, merged, nums1.length, nums2.length);
    Arrays.sort(merged);
    int total = merged.length;
    if (total % 2 == 1) return merged[total / 2];
    return (merged[total / 2 - 1] + merged[total / 2]) / 2.0;
}
```

#### Python

```python
def findMedianBrute(nums1: list[int], nums2: list[int]) -> float:
    """Brute Force — Merge and Sort. Time: O((m+n)log(m+n)), Space: O(m+n)"""
    merged = sorted(nums1 + nums2)
    total = len(merged)
    if total % 2 == 1:
        return float(merged[total // 2])
    return (merged[total // 2 - 1] + merged[total // 2]) / 2.0
```

### Dry Run

```text
Input: nums1 = [1, 3], nums2 = [2]

merged (unsorted) = [1, 3, 2]
merged (sorted)   = [1, 2, 3]
total = 3 (odd)
median = merged[3/2] = merged[1] = 2

Output: 2.00000
```

```text
Input: nums1 = [1, 2], nums2 = [3, 4]

merged (unsorted) = [1, 2, 3, 4]
merged (sorted)   = [1, 2, 3, 4]
total = 4 (even)
median = (merged[1] + merged[2]) / 2 = (2 + 3) / 2 = 2.5

Output: 2.50000
```

---

## Approach 2: Optimal (Binary Search on Smaller Array)

### The problem with Brute Force

> Brute Force uses O(m+n) space and O((m+n)log(m+n)) time.
> The problem explicitly requires **O(log(m+n))** time.
> We need a smarter approach that never constructs the merged array.

### Optimization idea

> **Partition insight:**
> The median of a sorted array of length `total` divides it into a left half
> of size `half = (total + 1) / 2` and a right half.
>
> We can split `nums1` at index `i` and `nums2` at index `j = half - i`.
> Then the left partition contains `nums1[0..i-1]` and `nums2[0..j-1]`.
>
> The partition is **valid** when:
> - `maxLeft1 <= minRight2` (largest element of left part of nums1 ≤ smallest element of right part of nums2)
> - `maxLeft2 <= minRight1` (largest element of left part of nums2 ≤ smallest element of right part of nums1)
>
> We binary-search `i` over [0, m] to find this valid partition.
> If `maxLeft1 > minRight2`, we took too many from `nums1` → move `i` left.
> If `maxLeft2 > minRight1`, we took too few from `nums1` → move `i` right.

### Algorithm (step-by-step)

1. Ensure `nums1` is the shorter array (swap if needed)
2. Set `half = (m + n + 1) / 2`
3. Binary search `i` in `[0, m]`:
   - Compute `j = half - i`
   - Compute boundary values: `maxLeft1`, `minRight1`, `maxLeft2`, `minRight2`
     - Use `INT_MIN` sentinel if `i=0` or `j=0`
     - Use `INT_MAX` sentinel if `i=m` or `j=n`
   - If `maxLeft1 <= minRight2` AND `maxLeft2 <= minRight1`: correct partition!
     - Odd total: return `max(maxLeft1, maxLeft2)`
     - Even total: return `(max(maxLeft1, maxLeft2) + min(minRight1, minRight2)) / 2.0`
   - If `maxLeft1 > minRight2`: `hi = i - 1` (shift partition left)
   - Else: `lo = i + 1` (shift partition right)

### Pseudocode

```text
function findMedianSortedArrays(nums1, nums2):
    if len(nums1) > len(nums2):
        swap(nums1, nums2)

    m, n = len(nums1), len(nums2)
    half = (m + n + 1) / 2
    lo, hi = 0, m

    while lo <= hi:
        i = (lo + hi) / 2
        j = half - i

        maxLeft1  = nums1[i-1]  if i > 0 else -INF
        minRight1 = nums1[i]    if i < m else +INF
        maxLeft2  = nums2[j-1]  if j > 0 else -INF
        minRight2 = nums2[j]    if j < n else +INF

        if maxLeft1 <= minRight2 AND maxLeft2 <= minRight1:
            if (m+n) is odd:
                return max(maxLeft1, maxLeft2)
            else:
                return (max(maxLeft1, maxLeft2) + min(minRight1, minRight2)) / 2.0
        elif maxLeft1 > minRight2:
            hi = i - 1
        else:
            lo = i + 1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log(min(m,n))) | Binary search on the shorter array only |
| **Space** | O(1) | No extra data structures — only a few integer variables |

### Implementation

#### Go

```go
// findMedianSortedArrays — Binary Search Partition
// Time: O(log(min(m,n))), Space: O(1)
func findMedianSortedArrays(nums1 []int, nums2 []int) float64 {
    if len(nums1) > len(nums2) {
        nums1, nums2 = nums2, nums1
    }
    m, n := len(nums1), len(nums2)
    half := (m + n + 1) / 2
    lo, hi := 0, m

    for lo <= hi {
        i := (lo + hi) / 2
        j := half - i

        maxLeft1 := math.MinInt64
        if i > 0 { maxLeft1 = nums1[i-1] }
        minRight1 := math.MaxInt64
        if i < m { minRight1 = nums1[i] }
        maxLeft2 := math.MinInt64
        if j > 0 { maxLeft2 = nums2[j-1] }
        minRight2 := math.MaxInt64
        if j < n { minRight2 = nums2[j] }

        if maxLeft1 <= minRight2 && maxLeft2 <= minRight1 {
            if (m+n)%2 == 1 {
                return float64(max(maxLeft1, maxLeft2))
            }
            return float64(max(maxLeft1, maxLeft2)+min(minRight1, minRight2)) / 2.0
        } else if maxLeft1 > minRight2 {
            hi = i - 1
        } else {
            lo = i + 1
        }
    }
    return 0.0
}
```

#### Java

```java
// findMedianSortedArrays — Binary Search Partition
// Time: O(log(min(m,n))), Space: O(1)
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    if (nums1.length > nums2.length) {
        int[] tmp = nums1; nums1 = nums2; nums2 = tmp;
    }
    int m = nums1.length, n = nums2.length;
    int half = (m + n + 1) / 2;
    int lo = 0, hi = m;

    while (lo <= hi) {
        int i = (lo + hi) / 2;
        int j = half - i;

        int maxLeft1  = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
        int minRight1 = (i == m) ? Integer.MAX_VALUE : nums1[i];
        int maxLeft2  = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
        int minRight2 = (j == n) ? Integer.MAX_VALUE : nums2[j];

        if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
            if ((m + n) % 2 == 1) return Math.max(maxLeft1, maxLeft2);
            return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
        } else if (maxLeft1 > minRight2) {
            hi = i - 1;
        } else {
            lo = i + 1;
        }
    }
    return 0.0;
}
```

#### Python

```python
def findMedianSortedArrays(nums1: list[int], nums2: list[int]) -> float:
    """Binary Search Partition. Time: O(log(min(m,n))), Space: O(1)"""
    if len(nums1) > len(nums2):
        nums1, nums2 = nums2, nums1
    m, n = len(nums1), len(nums2)
    half = (m + n + 1) // 2
    lo, hi = 0, m

    while lo <= hi:
        i = (lo + hi) // 2
        j = half - i

        max_left1  = nums1[i - 1] if i > 0 else float('-inf')
        min_right1 = nums1[i]     if i < m else float('inf')
        max_left2  = nums2[j - 1] if j > 0 else float('-inf')
        min_right2 = nums2[j]     if j < n else float('inf')

        if max_left1 <= min_right2 and max_left2 <= min_right1:
            if (m + n) % 2 == 1:
                return float(max(max_left1, max_left2))
            return (max(max_left1, max_left2) + min(min_right1, min_right2)) / 2.0
        elif max_left1 > min_right2:
            hi = i - 1
        else:
            lo = i + 1
    return 0.0
```

### Dry Run

#### Example 1: `nums1 = [1,3], nums2 = [2]`

```text
m=2, n=1 → swap → nums1=[2], nums2=[1,3]  (use shorter array)
m=1, n=2, half=(1+2+1)/2 = 2
lo=0, hi=1

--- Iteration 1 ---
i = (0+1)/2 = 0
j = 2 - 0   = 2

maxLeft1  = -INF  (i=0, out of bounds)
minRight1 = nums1[0] = 2
maxLeft2  = nums2[1] = 3
minRight2 = +INF  (j=2=n, out of bounds)

Check: maxLeft1(-INF) <= minRight2(+INF) ✅
       maxLeft2(3)    <= minRight1(2)    ❌  → 3 > 2

maxLeft2 > minRight1 → lo = i+1 = 1

--- Iteration 2 ---
i = (1+1)/2 = 1
j = 2 - 1   = 1

maxLeft1  = nums1[0] = 2
minRight1 = +INF  (i=1=m, out of bounds)
maxLeft2  = nums2[0] = 1
minRight2 = nums2[1] = 3

Check: maxLeft1(2) <= minRight2(3) ✅
       maxLeft2(1) <= minRight1(+INF) ✅  → VALID PARTITION!

Total = 3 (odd) → return max(maxLeft1, maxLeft2) = max(2, 1) = 2

Output: 2.00000
```

#### Example 2: `nums1 = [1,2], nums2 = [3,4]`

```text
m=2, n=2, both same length → no swap
half = (2+2+1)/2 = 2
lo=0, hi=2

--- Iteration 1 ---
i = 1, j = 1

maxLeft1  = nums1[0] = 1
minRight1 = nums1[1] = 2
maxLeft2  = nums2[0] = 3
minRight2 = nums2[1] = 4

Check: maxLeft1(1) <= minRight2(4) ✅
       maxLeft2(3) <= minRight1(2) ❌  → 3 > 2

maxLeft2 > minRight1 → lo = i+1 = 2

--- Iteration 2 ---
i = 2, j = 0

maxLeft1  = nums1[1] = 2
minRight1 = +INF  (i=2=m)
maxLeft2  = -INF  (j=0)
minRight2 = nums2[0] = 3

Check: maxLeft1(2) <= minRight2(3) ✅
       maxLeft2(-INF) <= minRight1(+INF) ✅  → VALID PARTITION!

Total = 4 (even) → return (max(2, -INF) + min(+INF, 3)) / 2
                 = (2 + 3) / 2 = 2.5

Output: 2.50000
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force (Merge+Sort) | O((m+n)log(m+n)) | O(m+n) | Simple, easy to understand | Does not meet O(log) requirement |
| 2 | Binary Search (Partition) | O(log(min(m,n))) | O(1) | Meets required complexity, minimal memory | Tricky to implement correctly |

### Which solution to choose?

- **In an interview:** Approach 2 — it's the intended solution and demonstrates advanced binary search skills
- **For understanding first:** Approach 1 — builds intuition before tackling the binary search logic
- **On LeetCode:** Approach 2 — the only one that satisfies the stated O(log(m+n)) requirement

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | One empty array | `nums1=[], nums2=[1]` | `1.00000` | Median of a single element |
| 2 | Both single elements | `nums1=[1], nums2=[2]` | `1.50000` | Even total, average both |
| 3 | All of nums1 < all of nums2 | `nums1=[1,2], nums2=[3,4,5]` | `3.00000` | Partition at the boundary |
| 4 | All of nums2 < all of nums1 | `nums1=[3,4,5], nums2=[1,2]` | `3.00000` | Partition reversed |
| 5 | Odd total length | `nums1=[1,3], nums2=[2]` | `2.00000` | Middle element only |
| 6 | Even total length | `nums1=[1,2], nums2=[3,4]` | `2.50000` | Average of two middle elements |
| 7 | Negative numbers | `nums1=[-5,-3,-1], nums2=[-4,-2]` | `-3.00000` | Works identically with negatives |
| 8 | Equal arrays | `nums1=[1,3], nums2=[1,3]` | `2.00000` | Duplicates across arrays |

---

## Common Mistakes

### Mistake 1: Not swapping to use the shorter array

```python
# ❌ WRONG — binary-searching the longer array can cause j < 0
if len(nums1) < len(nums2):   # forgot to enforce this
    pass

# ✅ CORRECT — always make nums1 the shorter array
if len(nums1) > len(nums2):
    nums1, nums2 = nums2, nums1
```

**Reason:** `j = half - i`. If `i` is large but `m > n`, then `j` can become negative, causing out-of-bounds access.

### Mistake 2: Off-by-one in `half` formula

```python
# ❌ WRONG — for odd totals, the right half gets one more element
half = (m + n) // 2

# ✅ CORRECT — left half gets the extra element (works for both odd and even)
half = (m + n + 1) // 2
```

**Reason:** Using `(m+n+1)//2` ensures the left partition always has the extra element in odd-length cases, making the formula for both parity cases consistent.

### Mistake 3: Using `INT_MIN`/`INT_MAX` incorrectly

```java
// ❌ WRONG — not handling the boundary (i==0 or i==m) causes wrong results
int maxLeft1 = nums1[i - 1];   // IndexOutOfBoundsException when i=0

// ✅ CORRECT — use sentinels for out-of-bounds positions
int maxLeft1 = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
```

### Mistake 4: Integer division instead of float division for even total

```java
// ❌ WRONG — integer division truncates the result
return (maxLeft + minRight) / 2;

// ✅ CORRECT — cast to double first
return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [2. Median of Two Sorted Arrays (Kth Element variant)](https://leetcode.com/problems/find-the-kth-smallest-element-in-a-sorted-matrix/) | 🟡 Medium | Finding k-th element — generalization |
| 2 | [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | 🟡 Medium | Binary search on modified sorted arrays |
| 3 | [240. Search a 2D Matrix II](https://leetcode.com/problems/search-a-2d-matrix-ii/) | 🟡 Medium | Searching across two sorted structures |
| 4 | [295. Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | 🔴 Hard | Median with dynamic insertions — two-heap approach |
| 5 | [719. Find K-th Smallest Pair Distance](https://leetcode.com/problems/find-k-th-smallest-pair-distance/) | 🔴 Hard | Binary search to find k-th value |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — merges both arrays, sorts them, highlights the median element(s)
> - **Binary Search** tab — shows the partition moving left/right during binary search, highlighting the boundary values `maxLeft1`, `minRight1`, `maxLeft2`, `minRight2`
> - **Partition View** — side-by-side visualization of both arrays split at the current partition index
