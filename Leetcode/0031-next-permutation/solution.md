# 0031. Next Permutation

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Next Permutation Algorithm](#approach-1-next-permutation-algorithm)
4. [Complexity Comparison](#complexity-comparison)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Related Problems](#related-problems)
8. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [31. Next Permutation](https://leetcode.com/problems/next-permutation/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Two Pointers` |

### Description

> A **permutation** of an array of integers is an arrangement of its members into a sequence or linear order.
>
> For example, for `arr = [1,2,3]`, the following are all the permutations of `arr`: `[1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]`.
>
> The **next permutation** of an array of integers is the next lexicographically greater permutation of its integer. More formally, if all the permutations of the array are sorted in one container according to their lexicographical order, then the **next permutation** of that array is the permutation that follows it in the sorted container. If such arrangement is not possible, it must rearrange it as the lowest possible order (i.e., sorted in ascending order).
>
> For example:
> - The next permutation of `arr = [1,2,3]` is `[1,3,2]`.
> - Similarly, the next permutation of `arr = [2,3,1]` is `[3,1,2]`.
> - The next permutation of `arr = [3,2,1]` is `[1,2,3]` because `[3,2,1]` does not have a lexicographical larger rearrangement.
>
> Given an array of integers `nums`, find the **next permutation** of `nums`.
>
> The replacement must be **in place** and use only **constant extra memory**.

### Examples

```
Example 1:
Input: nums = [1,2,3]
Output: [1,3,2]

Example 2:
Input: nums = [3,2,1]
Output: [1,2,3]

Example 3:
Input: nums = [1,1,5]
Output: [1,5,1]
```

### Constraints

- `1 <= nums.length <= 100`
- `0 <= nums[i] <= 100`

---

## Problem Breakdown

### 1. What is being asked?

Given an array of integers, rearrange it **in place** to form the next lexicographically greater permutation. If the array is already the largest permutation (fully descending), wrap around to the smallest permutation (fully ascending).

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers to rearrange in place |

Important observations about the input:
- The array may contain **duplicate** values
- The array is modified **in place** (no return value needed)
- Length is small (up to 100), so even O(n^2) would work, but O(n) is elegant
- Values range from 0 to 100

### 3. What is the output?

- **void** — the array is modified in place
- The array should contain the next lexicographically greater permutation
- If no greater permutation exists, sort the array in ascending order

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 100` | Very small input. Any reasonable algorithm works. |
| `nums[i] <= 100` | Small values, duplicates are likely |
| In-place | Cannot create a new array and copy |
| Constant extra memory | O(1) space required |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1,2,3]`

```text
All permutations in order: [1,2,3] → [1,3,2] → [2,1,3] → [2,3,1] → [3,1,2] → [3,2,1]

Current: [1, 2, 3]
Next:    [1, 3, 2]

Step 1: Find the rightmost pair where nums[i] < nums[i+1]
        i=0: nums[0]=1 < nums[1]=2 ✓
        i=1: nums[1]=2 < nums[2]=3 ✓  ← rightmost, so pivot = index 1

Step 2: Find the rightmost element greater than nums[pivot=1] = 2
        j=2: nums[2]=3 > 2 ✓  ← swap target

Step 3: Swap nums[1] and nums[2] → [1, 3, 2]

Step 4: Reverse suffix after pivot (index 2 to end) → [1, 3, 2]
        (Only one element, nothing to reverse)

Result: [1, 3, 2]
```

#### Example 2: `nums = [3,2,1]`

```text
Current: [3, 2, 1]  ← fully descending, this is the LAST permutation

Step 1: Find the rightmost pair where nums[i] < nums[i+1]
        i=0: nums[0]=3 > nums[1]=2 ✗
        i=1: nums[1]=2 > nums[2]=1 ✗
        No such pair found! → pivot = -1

Step 2: Since pivot = -1, reverse the entire array
        [3, 2, 1] → [1, 2, 3]

Result: [1, 2, 3]
```

#### Example 3: `nums = [1,1,5]`

```text
Current: [1, 1, 5]

Step 1: Find the rightmost pair where nums[i] < nums[i+1]
        i=0: nums[0]=1 < nums[1]=1? No, 1 is not < 1
        i=1: nums[1]=1 < nums[2]=5 ✓  ← pivot = index 1

Step 2: Find the rightmost element greater than nums[pivot=1] = 1
        j=2: nums[2]=5 > 1 ✓  ← swap target

Step 3: Swap nums[1] and nums[2] → [1, 5, 1]

Step 4: Reverse suffix after pivot (index 2 to end) → [1, 5, 1]
        (Only one element, nothing to reverse)

Result: [1, 5, 1]
```

### 6. Key Observations

1. **Descending suffix** — The suffix from the end that is in non-increasing order cannot produce a larger permutation by rearranging within itself.
2. **Pivot point** — The element just before the descending suffix (where `nums[i] < nums[i+1]`) is the "pivot" — this is the position we need to change.
3. **Smallest increase** — To get the *next* permutation, we swap the pivot with the smallest element in the suffix that is still greater than the pivot.
4. **Reverse suffix** — After the swap, the suffix is still in descending order. Reversing it gives the smallest possible arrangement for that suffix.
5. **Wrap around** — If the entire array is descending (no pivot found), we reverse the whole array to get the smallest permutation.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two Pointers | Scanning from right, swapping, reversing | Next Permutation (this problem) |
| In-place manipulation | Modify array without extra space | Required by constraints |

**Chosen pattern:** `Two Pointers + Reverse`
**Reason:** The algorithm naturally uses pointer scanning from the right to find the pivot and swap target, then reverses a suffix — all in O(n) time and O(1) space.

---

## Approach 1: Next Permutation Algorithm

### Thought process

> The key insight is understanding the structure of permutation ordering.
> Consider `[1, 5, 8, 4, 7, 6, 5, 3, 1]`:
> - The suffix `[7, 6, 5, 3, 1]` is descending — no rearrangement of just these elements can make a larger number.
> - The element before the suffix is `4` (the "pivot") — we need to replace it with something slightly larger.
> - The smallest element in the suffix greater than `4` is `5` — swap them.
> - After swapping: `[1, 5, 8, 5, 7, 6, 4, 3, 1]` — the suffix `[7, 6, 4, 3, 1]` is still descending.
> - Reverse the suffix to get the smallest arrangement: `[1, 5, 8, 5, 1, 3, 4, 6, 7]`.

### Algorithm (step-by-step)

1. **Find the pivot:** Scan from right to left. Find the largest index `i` such that `nums[i] < nums[i + 1]`. If no such index exists, the array is the last permutation — go to step 4.
2. **Find the swap target:** Scan from right to left. Find the largest index `j` such that `nums[j] > nums[i]`.
3. **Swap:** Swap `nums[i]` and `nums[j]`.
4. **Reverse:** Reverse the suffix starting at `nums[i + 1]` (or the entire array if no pivot was found).

### Pseudocode

```text
function nextPermutation(nums):
    n = length(nums)

    // Step 1: Find the pivot
    i = n - 2
    while i >= 0 AND nums[i] >= nums[i + 1]:
        i--

    // Step 2 & 3: If pivot found, find swap target and swap
    if i >= 0:
        j = n - 1
        while nums[j] <= nums[i]:
            j--
        swap(nums[i], nums[j])

    // Step 4: Reverse the suffix
    reverse(nums, i + 1, n - 1)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | At most 3 linear scans: find pivot, find swap target, reverse suffix. |
| **Space** | O(1) | Only a few index variables. In-place swaps and reverse. |

### Implementation

#### Go

```go
func nextPermutation(nums []int) {
    n := len(nums)

    // Step 1: Find the pivot — rightmost i where nums[i] < nums[i+1]
    i := n - 2
    for i >= 0 && nums[i] >= nums[i+1] {
        i--
    }

    // Step 2 & 3: Find the swap target and swap
    if i >= 0 {
        j := n - 1
        for nums[j] <= nums[i] {
            j--
        }
        nums[i], nums[j] = nums[j], nums[i]
    }

    // Step 4: Reverse the suffix after the pivot
    left, right := i+1, n-1
    for left < right {
        nums[left], nums[right] = nums[right], nums[left]
        left++
        right--
    }
}
```

#### Java

```java
class Solution {
    public void nextPermutation(int[] nums) {
        int n = nums.length;

        // Step 1: Find the pivot — rightmost i where nums[i] < nums[i+1]
        int i = n - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }

        // Step 2 & 3: Find the swap target and swap
        if (i >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[i]) {
                j--;
            }
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }

        // Step 4: Reverse the suffix after the pivot
        int left = i + 1, right = n - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }
}
```

#### Python

```python
class Solution:
    def nextPermutation(self, nums: list[int]) -> None:
        n = len(nums)

        # Step 1: Find the pivot — rightmost i where nums[i] < nums[i+1]
        i = n - 2
        while i >= 0 and nums[i] >= nums[i + 1]:
            i -= 1

        # Step 2 & 3: Find the swap target and swap
        if i >= 0:
            j = n - 1
            while nums[j] <= nums[i]:
                j -= 1
            nums[i], nums[j] = nums[j], nums[i]

        # Step 4: Reverse the suffix after the pivot
        left, right = i + 1, n - 1
        while left < right:
            nums[left], nums[right] = nums[right], nums[left]
            left += 1
            right -= 1
```

### Dry Run

```text
Input: nums = [1, 5, 8, 4, 7, 6, 5, 3, 1]

Step 1: Find pivot (scan right to left for nums[i] < nums[i+1])
  i=7: nums[7]=3 >= nums[8]=1? Yes → continue
  i=6: nums[6]=5 >= nums[7]=3? Yes → continue
  i=5: nums[5]=6 >= nums[6]=5? Yes → continue
  i=4: nums[4]=7 >= nums[5]=6? Yes → continue
  i=3: nums[3]=4 >= nums[4]=7? No → STOP
  pivot = 3, nums[pivot] = 4

Step 2: Find swap target (scan right to left for nums[j] > nums[pivot])
  j=8: nums[8]=1 > 4? No
  j=7: nums[7]=3 > 4? No
  j=6: nums[6]=5 > 4? Yes → STOP
  swap target = 6, nums[6] = 5

Step 3: Swap nums[3] and nums[6]
  [1, 5, 8, 4, 7, 6, 5, 3, 1]
                ↕        ↕
  [1, 5, 8, 5, 7, 6, 4, 3, 1]

Step 4: Reverse suffix from index 4 to 8
  [1, 5, 8, 5, | 7, 6, 4, 3, 1 |]
                  ↕           ↕      → swap 7 and 1
  [1, 5, 8, 5, | 1, 6, 4, 3, 7 |]
                     ↕     ↕         → swap 6 and 3
  [1, 5, 8, 5, | 1, 3, 4, 6, 7 |]
                        ↕            → middle element, done

Result: [1, 5, 8, 5, 1, 3, 4, 6, 7]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Next Permutation Algorithm | O(n) | O(1) | Optimal, in-place, elegant | Requires understanding the logic |

### Which solution to choose?

- **In an interview:** The standard Next Permutation algorithm — it is the only well-known approach
- **In production:** Same algorithm — optimal time and space
- **On Leetcode:** Same algorithm — passes all test cases efficiently
- **For learning:** Walk through multiple examples to build intuition about why the algorithm works

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Last permutation | `[3,2,1]` | `[1,2,3]` | Fully descending, wrap to ascending |
| 2 | First permutation | `[1,2,3]` | `[1,3,2]` | Standard next permutation |
| 3 | Single element | `[1]` | `[1]` | Only one permutation exists |
| 4 | Two elements ascending | `[1,2]` | `[2,1]` | Swap the two |
| 5 | Two elements descending | `[2,1]` | `[1,2]` | Wrap around |
| 6 | Duplicates | `[1,1,5]` | `[1,5,1]` | Must handle equal elements (use >= and <=) |
| 7 | All same elements | `[2,2,2]` | `[2,2,2]` | No next permutation, stays the same |
| 8 | Pivot at first position | `[1,5,4,3,2]` | `[2,1,3,4,5]` | Pivot is index 0, large suffix |

---

## Common Mistakes

### Mistake 1: Using strict `>` instead of `>=` when finding the pivot

```python
# WRONG — misses equal adjacent elements
while i >= 0 and nums[i] > nums[i + 1]:
    i -= 1

# CORRECT — must use >= to skip equal elements
while i >= 0 and nums[i] >= nums[i + 1]:
    i -= 1
```

**Reason:** If `nums[i] == nums[i+1]`, swapping them would not produce a *greater* permutation. We need `nums[i]` to be strictly *less than* `nums[i+1]`.

### Mistake 2: Using strict `<` instead of `<=` when finding the swap target

```python
# WRONG — may swap with an equal element
j = n - 1
while nums[j] < nums[i]:
    j -= 1

# CORRECT — must find strictly greater
j = n - 1
while nums[j] <= nums[i]:
    j -= 1
```

**Reason:** Swapping the pivot with an equal element would not produce a greater permutation.

### Mistake 3: Forgetting to reverse the suffix after swapping

```python
# WRONG — only swaps, doesn't reverse
if i >= 0:
    j = n - 1
    while nums[j] <= nums[i]:
        j -= 1
    nums[i], nums[j] = nums[j], nums[i]
# Missing: reverse(nums, i+1, n-1)

# CORRECT — always reverse the suffix
if i >= 0:
    j = n - 1
    while nums[j] <= nums[i]:
        j -= 1
    nums[i], nums[j] = nums[j], nums[i]
# Reverse suffix to get the smallest arrangement
left, right = i + 1, n - 1
while left < right:
    nums[left], nums[right] = nums[right], nums[left]
    left += 1
    right -= 1
```

**Reason:** After swapping, the suffix is still in descending order. Reversing it converts it to ascending, which is the smallest possible suffix — giving us the *next* permutation, not a later one.

### Mistake 4: Not reversing when no pivot is found

```python
# WRONG — returns unchanged array when no pivot
if i < 0:
    return  # Array stays [3,2,1]

# CORRECT — reverse the entire array to get the smallest permutation
if i < 0:
    nums.reverse()  # [3,2,1] → [1,2,3]
```

**Reason:** When the entire array is in descending order, it is the last permutation. The next permutation wraps around to the first (ascending order).

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Generate all permutations |
| 2 | [47. Permutations II](https://leetcode.com/problems/permutations-ii/) | :yellow_circle: Medium | Permutations with duplicates |
| 3 | [60. Permutation Sequence](https://leetcode.com/problems/permutation-sequence/) | :red_circle: Hard | Find k-th permutation |
| 4 | [556. Next Greater Element III](https://leetcode.com/problems/next-greater-element-iii/) | :yellow_circle: Medium | Same algorithm on digits of a number |
| 5 | [267. Palindrome Permutation II](https://leetcode.com/problems/palindrome-permutation-ii/) | :yellow_circle: Medium | Permutation generation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Step-by-step visualization** of the Next Permutation algorithm
> - **Find Pivot** — highlights the descending suffix and identifies the pivot point
> - **Find Swap Target** — scans from right to find the smallest element greater than pivot
> - **Swap** — swaps the pivot with the swap target
> - **Reverse Suffix** — reverses the suffix to get the smallest arrangement
> - Preset examples and custom input for experimenting
