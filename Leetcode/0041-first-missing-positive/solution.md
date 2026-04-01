# 0041. First Missing Positive

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Cyclic Sort / Index Mapping](#approach-1-cyclic-sort--index-mapping)
4. [Approach 2: Hash Set](#approach-2-hash-set)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [41. First Missing Positive](https://leetcode.com/problems/first-missing-positive/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `Hash Table` |

### Description

> Given an unsorted integer array `nums`, return the smallest missing positive integer.
>
> You must implement an algorithm that runs in **O(n)** time and uses **O(1)** auxiliary space.

### Examples

```
Example 1:
Input: nums = [1,2,0]
Output: 3
Explanation: The numbers in the range [1,2] are all in the array.

Example 2:
Input: nums = [3,4,-1,1]
Output: 2
Explanation: 1 is in the array but 2 is missing.

Example 3:
Input: nums = [7,8,9,11,12]
Output: 1
Explanation: The smallest positive integer 1 is missing.
```

### Constraints

- `1 <= nums.length <= 10^5`
- `-2^31 <= nums[i] <= 2^31 - 1`

---

## Problem Breakdown

### 1. What is being asked?

Find the **smallest positive integer** (1, 2, 3, ...) that is **not present** in the array.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Unsorted array of integers |

Important observations about the input:
- The array is **unsorted**
- Contains **negative numbers, zeros, and duplicates**
- Can contain very large numbers
- Length is at least 1

### 3. What is the output?

- A single **positive integer** (always >= 1)
- The **smallest** positive integer not found in the array

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 10^5` | O(n) is required, O(n log n) might pass but O(n^2) is TLE |
| O(n) time, O(1) space | Cannot use sorting (O(n log n)) or Hash Set (O(n) space) for optimal |
| Answer range | Answer is always in [1, n+1] where n = len(nums) |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1,2,0]`

```text
Initial state: nums = [1, 2, 0]

Positive integers: 1, 2, 3, 4, ...
Present in array: 1 ✅, 2 ✅, 3 ❌ MISSING!

Result: 3
```

#### Example 2: `nums = [3,4,-1,1]`

```text
Initial state: nums = [3, 4, -1, 1]

Positive integers: 1, 2, 3, 4, ...
Present in array: 1 ✅, 2 ❌ MISSING!

Result: 2
```

#### Example 3: `nums = [7,8,9,11,12]`

```text
Initial state: nums = [7, 8, 9, 11, 12]

Positive integers: 1, 2, 3, 4, ...
Present in array: 1 ❌ MISSING!

Result: 1
```

### 6. Key Observations

1. **Answer range** — The answer is always in `[1, n+1]`. If the array contains exactly `{1, 2, ..., n}`, the answer is `n+1`. Otherwise, some number in `[1, n]` is missing.
2. **Use the array itself as a Hash Map** — Since the answer is in `[1, n]`, we can place each number `x` at index `x-1`. Then scan to find the first index where `nums[i] != i+1`.
3. **Ignore irrelevant values** — Numbers <= 0 or > n cannot be the answer and can be ignored during placement.
4. **Cyclic Sort pattern** — Swap elements to their "correct" positions until every valid element is in place.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Cyclic Sort / Index Mapping | O(n) time, O(1) space, place elements at correct index | This problem |
| Hash Set | O(n) time, O(n) space, simple but doesn't meet space constraint | Comparison approach |
| Sorting | O(n log n) time, doesn't meet time constraint | Not optimal |

**Chosen pattern:** `Cyclic Sort / Index Mapping`
**Reason:** The only approach that satisfies both O(n) time and O(1) space constraints.

---

## Approach 1: Cyclic Sort / Index Mapping

### Thought process

> The key insight: for an array of length `n`, the answer must be in the range `[1, n+1]`.
> Why? Because even in the best case, the array can only hold the numbers `1` through `n`.
> If all of them are present, the answer is `n+1`.
>
> So we can use the array **itself** as a Hash Map!
> Place each number `x` (where `1 <= x <= n`) at index `x-1`.
> After all placements, scan the array: the first index `i` where `nums[i] != i+1` gives the answer `i+1`.
>
> This is the **Cyclic Sort** pattern — we swap elements into their correct positions.

### Algorithm (step-by-step)

1. For each position `i` from `0` to `n-1`:
   - While `nums[i]` is in range `[1, n]` AND `nums[i]` is not at its correct position:
     - Swap `nums[i]` with `nums[nums[i] - 1]` (put it where it belongs)
2. After all swaps, scan the array from left to right:
   - The first index `i` where `nums[i] != i + 1` → return `i + 1`
3. If all positions are correct → return `n + 1`

### Pseudocode

```text
function firstMissingPositive(nums):
    n = len(nums)

    // Phase 1: Place each number at its correct index
    for i = 0 to n-1:
        while 1 <= nums[i] <= n AND nums[nums[i]-1] != nums[i]:
            swap(nums[i], nums[nums[i]-1])

    // Phase 2: Find the first missing
    for i = 0 to n-1:
        if nums[i] != i + 1:
            return i + 1

    return n + 1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each element is swapped at most once to its correct position. Total swaps <= n. |
| **Space** | O(1) | We modify the array in-place, no extra memory used. |

> **Why O(n)?** Although there is a `while` loop inside a `for` loop, each swap places at least one element at its correct position. Once placed correctly, an element is never moved again. So total swaps across all iterations <= n.

### Implementation

#### Go

```go
// firstMissingPositive — Cyclic Sort / Index Mapping
// Time: O(n), Space: O(1)
func firstMissingPositive(nums []int) int {
    n := len(nums)

    // Phase 1: Place each number at its correct index
    // nums[i] should be at index nums[i]-1
    for i := 0; i < n; i++ {
        for nums[i] >= 1 && nums[i] <= n && nums[nums[i]-1] != nums[i] {
            // Swap nums[i] with nums[nums[i]-1]
            nums[i], nums[nums[i]-1] = nums[nums[i]-1], nums[i]
        }
    }

    // Phase 2: Find the first missing positive
    for i := 0; i < n; i++ {
        if nums[i] != i+1 {
            return i + 1
        }
    }

    // All 1..n are present
    return n + 1
}
```

#### Java

```java
class Solution {
    // firstMissingPositive — Cyclic Sort / Index Mapping
    // Time: O(n), Space: O(1)
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;

        // Phase 1: Place each number at its correct index
        // nums[i] should be at index nums[i]-1
        for (int i = 0; i < n; i++) {
            while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                // Swap nums[i] with nums[nums[i]-1]
                int temp = nums[nums[i] - 1];
                nums[nums[i] - 1] = nums[i];
                nums[i] = temp;
            }
        }

        // Phase 2: Find the first missing positive
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }

        // All 1..n are present
        return n + 1;
    }
}
```

#### Python

```python
class Solution:
    def firstMissingPositive(self, nums: list[int]) -> int:
        """
        Cyclic Sort / Index Mapping
        Time: O(n), Space: O(1)
        """
        n = len(nums)

        # Phase 1: Place each number at its correct index
        # nums[i] should be at index nums[i]-1
        for i in range(n):
            while 1 <= nums[i] <= n and nums[nums[i] - 1] != nums[i]:
                # Swap nums[i] with nums[nums[i]-1]
                correct = nums[i] - 1
                nums[i], nums[correct] = nums[correct], nums[i]

        # Phase 2: Find the first missing positive
        for i in range(n):
            if nums[i] != i + 1:
                return i + 1

        # All 1..n are present
        return n + 1
```

### Dry Run

```text
Input: nums = [3, 4, -1, 1]
n = 4, answer must be in [1, 5]

=== Phase 1: Place elements at correct positions ===

i=0: nums = [3, 4, -1, 1]
  nums[0]=3, correct index = 2
  nums[2]=-1 != 3 → swap → nums = [-1, 4, 3, 1]
  nums[0]=-1, not in [1,4] → stop

i=1: nums = [-1, 4, 3, 1]
  nums[1]=4, correct index = 3
  nums[3]=1 != 4 → swap → nums = [-1, 1, 3, 4]
  nums[1]=1, correct index = 0
  nums[0]=-1 != 1 → swap → nums = [1, -1, 3, 4]
  nums[1]=-1, not in [1,4] → stop

i=2: nums = [1, -1, 3, 4]
  nums[2]=3, correct index = 2
  nums[2]=3 == 3 → already correct → stop

i=3: nums = [1, -1, 3, 4]
  nums[3]=4, correct index = 3
  nums[3]=4 == 4 → already correct → stop

=== Phase 2: Scan for first missing ===

nums = [1, -1, 3, 4]

i=0: nums[0]=1 == 1 ✅
i=1: nums[1]=-1 != 2 ❌ → return 2

Result: 2 ✅
```

```text
Input: nums = [1, 2, 0]
n = 3, answer must be in [1, 4]

=== Phase 1: Place elements at correct positions ===

i=0: nums[0]=1, correct index = 0, already correct → stop
i=1: nums[1]=2, correct index = 1, already correct → stop
i=2: nums[2]=0, not in [1,3] → stop

=== Phase 2: Scan for first missing ===

nums = [1, 2, 0]

i=0: nums[0]=1 == 1 ✅
i=1: nums[1]=2 == 2 ✅
i=2: nums[2]=0 != 3 ❌ → return 3

Result: 3 ✅
```

```text
Input: nums = [7, 8, 9, 11, 12]
n = 5, answer must be in [1, 6]

=== Phase 1: Place elements at correct positions ===

All values (7,8,9,11,12) are > n=5, so no swaps happen.

=== Phase 2: Scan for first missing ===

nums = [7, 8, 9, 11, 12]

i=0: nums[0]=7 != 1 ❌ → return 1

Result: 1 ✅
```

---

## Approach 2: Hash Set

### Thought process

> The simplest O(n) approach: put all numbers into a Hash Set.
> Then check 1, 2, 3, ... until we find one not in the set.
>
> This is easy to understand but uses O(n) extra space — so it doesn't satisfy
> the O(1) space constraint. We include it for comparison and as a stepping stone.

### Algorithm (step-by-step)

1. Add all elements of `nums` to a Hash Set
2. Starting from `i = 1`, check if `i` is in the set
3. The first `i` not in the set is the answer

### Pseudocode

```text
function firstMissingPositive(nums):
    numSet = set(nums)

    for i = 1 to n+1:
        if i not in numSet:
            return i

    return n + 1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Building the set is O(n). Scanning 1 to n+1 is O(n). Total O(n). |
| **Space** | O(n) | The Hash Set stores up to n elements. |

### Implementation

#### Go

```go
// firstMissingPositive — Hash Set approach
// Time: O(n), Space: O(n)
func firstMissingPositive(nums []int) int {
    // Build a Hash Set
    numSet := make(map[int]bool)
    for _, num := range nums {
        numSet[num] = true
    }

    // Check 1, 2, 3, ... until we find a missing one
    for i := 1; i <= len(nums)+1; i++ {
        if !numSet[i] {
            return i
        }
    }

    return len(nums) + 1
}
```

#### Java

```java
import java.util.HashSet;

class Solution {
    // firstMissingPositive — Hash Set approach
    // Time: O(n), Space: O(n)
    public int firstMissingPositive(int[] nums) {
        // Build a Hash Set
        HashSet<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        // Check 1, 2, 3, ... until we find a missing one
        for (int i = 1; i <= nums.length + 1; i++) {
            if (!numSet.contains(i)) {
                return i;
            }
        }

        return nums.length + 1;
    }
}
```

#### Python

```python
class Solution:
    def firstMissingPositive(self, nums: list[int]) -> int:
        """
        Hash Set approach
        Time: O(n), Space: O(n)
        """
        # Build a Hash Set
        num_set = set(nums)

        # Check 1, 2, 3, ... until we find a missing one
        for i in range(1, len(nums) + 2):
            if i not in num_set:
                return i

        return len(nums) + 1
```

### Dry Run

```text
Input: nums = [3, 4, -1, 1]

Step 1: numSet = {3, 4, -1, 1}

Step 2: Check positive integers
  i=1: 1 in numSet? ✅ Yes → continue
  i=2: 2 in numSet? ❌ No → return 2

Result: 2 ✅
```

```text
Input: nums = [1, 2, 3]

Step 1: numSet = {1, 2, 3}

Step 2: Check positive integers
  i=1: 1 in numSet? ✅ Yes → continue
  i=2: 2 in numSet? ✅ Yes → continue
  i=3: 3 in numSet? ✅ Yes → continue
  i=4: 4 in numSet? ❌ No → return 4

Result: 4 ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Cyclic Sort / Index Mapping | O(n) | O(1) | Optimal, meets all constraints | Modifies input array, tricky swap logic |
| 2 | Hash Set | O(n) | O(n) | Simple, easy to understand | O(n) extra space, doesn't meet constraint |

### Which solution to choose?

- **In an interview:** Approach 1 (Cyclic Sort) — demonstrates deep understanding, meets all constraints
- **In production:** Approach 1 — optimal time and space
- **On Leetcode:** Approach 1 — the intended solution for this Hard problem
- **For learning:** Both — understand the Hash Set idea first, then optimize to Cyclic Sort

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single element, missing 1 | `nums=[2]` | `1` | 1 is not present |
| 2 | Single element, has 1 | `nums=[1]` | `2` | 1 is present, next is 2 |
| 3 | All negative | `nums=[-1,-2,-3]` | `1` | No positives at all |
| 4 | Consecutive from 1 | `nums=[1,2,3,4,5]` | `6` | All present, answer is n+1 |
| 5 | Contains duplicates | `nums=[1,1,1]` | `2` | Duplicates don't help |
| 6 | Large gap | `nums=[1,1000]` | `2` | Large numbers are irrelevant |
| 7 | Contains zero | `nums=[0,1,2]` | `3` | Zero is not a positive integer |
| 8 | Unsorted complete | `nums=[3,1,2]` | `4` | All 1..n present but unsorted |

---

## Common Mistakes

### Mistake 1: Infinite loop in swap

```python
# ❌ WRONG — can loop forever with duplicates
while 1 <= nums[i] <= n and nums[i] != i + 1:
    nums[i], nums[nums[i] - 1] = nums[nums[i] - 1], nums[i]
# If nums[i] == nums[nums[i]-1] (duplicate), the swap changes nothing → infinite loop

# ✅ CORRECT — check destination, not current position
while 1 <= nums[i] <= n and nums[nums[i] - 1] != nums[i]:
    correct = nums[i] - 1
    nums[i], nums[correct] = nums[correct], nums[i]
```

**Reason:** The condition `nums[i] != i + 1` doesn't detect duplicates. For example, `nums = [1, 1]`: at `i=1`, `nums[1]=1 != 2`, but swapping with `nums[0]` (which is also 1) does nothing.

### Mistake 2: Wrong swap order in Python

```python
# ❌ WRONG — Python evaluates left side first
nums[i], nums[nums[i] - 1] = nums[nums[i] - 1], nums[i]
# nums[i] is updated first, then nums[nums[i]-1] uses the NEW nums[i]!

# ✅ CORRECT — save the index first
correct = nums[i] - 1
nums[i], nums[correct] = nums[correct], nums[i]
```

**Reason:** In Python's tuple swap, `nums[nums[i] - 1]` on the left uses the already-updated `nums[i]`. Saving the index in a variable avoids this.

### Mistake 3: Forgetting the n+1 case

```python
# ❌ WRONG — doesn't handle when all 1..n are present
for i in range(n):
    if nums[i] != i + 1:
        return i + 1
# What if nums = [1, 2, 3]? No mismatch found!

# ✅ CORRECT — add the fallback
for i in range(n):
    if nums[i] != i + 1:
        return i + 1
return n + 1  # All 1..n are present
```

### Mistake 4: Not handling numbers out of range

```python
# ❌ WRONG — tries to swap numbers outside valid range
while nums[i] != i + 1:
    nums[i], nums[nums[i] - 1] = nums[nums[i] - 1], nums[i]
# If nums[i] = -1, accessing nums[-2] goes to wrong index!
# If nums[i] = 100 and n = 5, index out of bounds!

# ✅ CORRECT — check range first
while 1 <= nums[i] <= n and nums[nums[i] - 1] != nums[i]:
    correct = nums[i] - 1
    nums[i], nums[correct] = nums[correct], nums[i]
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [268. Missing Number](https://leetcode.com/problems/missing-number/) | :green_circle: Easy | Find missing number in [0, n] — simpler version |
| 2 | [287. Find the Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number/) | :orange_circle: Medium | Cyclic sort / index mapping pattern |
| 3 | [442. Find All Duplicates in an Array](https://leetcode.com/problems/find-all-duplicates-in-an-array/) | :orange_circle: Medium | Same index mapping technique |
| 4 | [448. Find All Numbers Disappeared in an Array](https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/) | :green_circle: Easy | Find ALL missing numbers, same pattern |
| 5 | [765. Couples Holding Hands](https://leetcode.com/problems/couples-holding-hands/) | :red_circle: Hard | Cyclic swap technique |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Phase 1: Cyclic Sort** — elements are swapped to their correct positions (value `x` goes to index `x-1`)
> - **Phase 2: Scan** — the array is scanned to find the first index where `nums[i] != i+1`
> - Controls: Step, Play/Pause, Reset, Speed slider
> - Presets: Try different input arrays to see how the algorithm works
