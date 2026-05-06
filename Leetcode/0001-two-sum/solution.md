---
comments: true
---

# 0001. Two Sum

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Time Complexity Optimization](#approach-2-time-complexity-optimization)
5. [Approach 3: Space Complexity Optimization](#approach-3-space-complexity-optimization)
6. [Approach 4: Two-pass Hash Map](#approach-4-two-pass-hash-map)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [1. Two Sum](https://leetcode.com/problems/two-sum/) |
| **Difficulty** | 🟢 Easy |
| **Tags** | `Array`, `Hash Table` |

### Description

> Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.
>
> You may assume that each input would have **exactly one solution**, and you may not use the same element twice.
>
> You can return the answer in any order.

### Examples

```
Example 1:
Input: nums = [2,7,11,15], target = 9
Output: [0,1]
Explanation: nums[0] + nums[1] = 2 + 7 = 9, so we return [0,1].

Example 2:
Input: nums = [3,2,4], target = 6
Output: [1,2]
Explanation: nums[1] + nums[2] = 2 + 4 = 6

Example 3:
Input: nums = [3,3], target = 6
Output: [0,1]
Explanation: nums[0] + nums[1] = 3 + 3 = 6
```

### Constraints

- `2 <= nums.length <= 10^4`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= target <= 10^9`
- **Only one valid answer exists**

---

## Problem Breakdown

### 1. What is being asked?

Find two numbers in the array whose sum equals the target. Return their **indices**, not the values themselves.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers |
| `target` | `int` | Target sum |

Important observations about the input:
- The array is **unsorted** (not sorted)
- **Duplicates may exist** (Example 3: `[3,3]`)
- The array contains at least 2 elements
- Negative numbers are possible

### 3. What is the output?

- **An array of 2 indices** `[i, j]`
- Order does not matter (`[0,1]` or `[1,0]` are both correct)
- There is always **exactly one** answer
- The same element cannot be used twice (`i != j`)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 10^4` | O(n^2) works (~10^8 operations at the limit), but O(n) is better |
| `-10^9 <= nums[i] <= 10^9` | int32 is sufficient, but the sum may require int64 |
| Only one answer | No need to find multiple answers — returning the first one is enough |

### 5. Step-by-step example analysis

#### Example 1: `nums = [2,7,11,15], target = 9`

```text
Initial state: nums = [2, 7, 11, 15], target = 9

Question: Which two numbers add up to 9?

Checking:
  2 + 7  = 9  ✅ FOUND! → indices: [0, 1]

Result: [0, 1]
```

#### Example 2: `nums = [3,2,4], target = 6`

```text
Initial state: nums = [3, 2, 4], target = 6

Checking:
  3 + 2 = 5  ❌ (not 6)
  3 + 4 = 7  ❌ (not 6)
  2 + 4 = 6  ✅ FOUND! → indices: [1, 2]

Result: [1, 2]
```

#### Example 3: `nums = [3,3], target = 6`

```text
Initial state: nums = [3, 3], target = 6

Checking:
  3 + 3 = 6  ✅ FOUND! → indices: [0, 1]

Here both elements have the same value but are at different indices.
Result: [0, 1]
```

### 6. Key Observations

1. **Complement** — If `a + b = target`, then `b = target - a`. That is, for each element we need to search for its "pair".
2. **Index needed, not value** — If we sort, the indices are lost (or must be stored separately).
3. **Only one answer** — Returning the first found pair is sufficient, no need to search for others.
4. **Hash Map** — We can check if the complement exists in O(1).

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Hash Map | O(1) lookup, complement search | Two Sum (this problem) |
| Two Pointers | Works on sorted arrays | Two Sum II (sorted) |
| Brute Force | Always works, but slow | All problems |

**Chosen pattern:** `Hash Map (One-pass)`
**Reason:** The array is unsorted, indices are needed, and Hash Map is the best fit for solving in O(n) time.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: compare each element with every other element.
> Using two nested loops, check all pairs.
> If the sum equals the target — return the indices.

### Algorithm (step-by-step)

1. Outer loop: `i = 0` to `n-1`
2. Inner loop: `j = i+1` to `n-1`
3. If `nums[i] + nums[j] == target` → return `[i, j]`
4. (Per the constraint, an answer always exists, so there will never be an empty result)

### Pseudocode

```text
function twoSum(nums, target):
    for i = 0 to n-1:
        for j = i+1 to n-1:
            if nums[i] + nums[j] == target:
                return [i, j]
    return []  // will never reach here
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | For each element, we check all remaining elements. n*(n-1)/2 pairs. |
| **Space** | O(1) | No extra memory is used (only i, j variables). |

### Implementation

#### Go

```go
// twoSum — Brute Force approach
// Time: O(n²), Space: O(1)
func twoSum(nums []int, target int) []int {
    n := len(nums)
    // Check every pair
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            // If the sum equals the target — found it
            if nums[i]+nums[j] == target {
                return []int{i, j}
            }
        }
    }
    return nil
}
```

#### Java

```java
class Solution {
    // twoSum — Brute Force approach
    // Time: O(n²), Space: O(1)
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;
        // Check every pair
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // If the sum equals the target — found it
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Brute Force approach
        Time: O(n²), Space: O(1)
        """
        n = len(nums)
        # Check every pair
        for i in range(n):
            for j in range(i + 1, n):
                # If the sum equals the target — found it
                if nums[i] + nums[j] == target:
                    return [i, j]
        return []
```

### Dry Run

```text
Input: nums = [2, 7, 11, 15], target = 9

i=0 (nums[0]=2):
  ├── j=1: 2 + 7  = 9  == 9 ✅ FOUND!
  └── return [0, 1]

Total operations: 1 comparison
(Best case — found on the first pair)
```

```text
Input: nums = [3, 2, 4], target = 6

i=0 (nums[0]=3):
  ├── j=1: 3 + 2 = 5  ❌
  └── j=2: 3 + 4 = 7  ❌

i=1 (nums[1]=2):
  ├── j=2: 2 + 4 = 6  == 6 ✅ FOUND!
  └── return [1, 2]

Total operations: 3 comparisons
```

---

## Approach 2: Time Complexity Optimization

### The problem with Brute Force

> For each element, we are checking **all** remaining elements — this is O(n^2).
> For example, with 10,000 elements — that's 50,000,000 comparisons.
> Question: "Can we find the complement (`target - nums[i]`) faster?"

### Optimization idea

> **Use a Hash Map!** Traverse the array once, and for each element search for its complement in the Hash Map.
> Hash Map lookup is O(1) — so the total is O(n).
>
> **One-pass:** While traversing the array, simultaneously:
> 1. Is the complement in the Hash Map? → Yes → FOUND!
> 2. No → Add the current element to the Hash Map

### Algorithm (step-by-step)

1. Create an empty Hash Map: `seen = {}`
2. Traverse the array with a single loop: `i = 0` to `n-1`
3. Compute `complement = target - nums[i]`
4. If `complement` exists in the Hash Map → return `[seen[complement], i]`
5. Otherwise, add `nums[i] → i` to the Hash Map

### Pseudocode

```text
function twoSum(nums, target):
    seen = {}
    for i = 0 to n-1:
        complement = target - nums[i]
        if complement in seen:
            return [seen[complement], i]
        seen[nums[i]] = i
    return []
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | We traverse the array only once. Hash Map lookup is O(1). |
| **Space** | O(n) | We store at most n elements in the Hash Map. |

### Implementation

#### Go

```go
// twoSum — One-pass Hash Map approach
// Time: O(n), Space: O(n)
func twoSum(nums []int, target int) []int {
    // Hash Map: value → index
    seen := make(map[int]int)

    for i, num := range nums {
        // Compute the complement
        complement := target - num

        // Is the complement in the Hash Map?
        if j, ok := seen[complement]; ok {
            return []int{j, i} // Found!
        }

        // Add the current element to the Hash Map
        seen[num] = i
    }

    return nil
}
```

#### Java

```java
import java.util.HashMap;

class Solution {
    // twoSum — One-pass Hash Map approach
    // Time: O(n), Space: O(n)
    public int[] twoSum(int[] nums, int target) {
        // Hash Map: value → index
        HashMap<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            // Compute the complement
            int complement = target - nums[i];

            // Is the complement in the Hash Map?
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i}; // Found!
            }

            // Add the current element to the Hash Map
            seen.put(nums[i], i);
        }

        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        One-pass Hash Map approach
        Time: O(n), Space: O(n)
        """
        # Hash Map: value → index
        seen = {}

        for i, num in enumerate(nums):
            # Compute the complement
            complement = target - num

            # Is the complement in the Hash Map?
            if complement in seen:
                return [seen[complement], i]  # Found!

            # Add the current element to the Hash Map
            seen[num] = i

        return []
```

### Dry Run

```text
Input: nums = [2, 7, 11, 15], target = 9

seen = {}

Step 1: i=0, num=2, complement = 9-2 = 7
        Is 7 in seen? ❌ No
        seen = {2: 0}

Step 2: i=1, num=7, complement = 9-7 = 2
        Is 2 in seen? ✅ Yes! seen[2] = 0
        return [0, 1]

Total operations: 2 (Brute Force: 1 → in this case nearly equal)
```

```text
Input: nums = [3, 2, 4], target = 6

seen = {}

Step 1: i=0, num=3, complement = 6-3 = 3
        Is 3 in seen? ❌ No
        seen = {3: 0}

Step 2: i=1, num=2, complement = 6-2 = 4
        Is 4 in seen? ❌ No
        seen = {3: 0, 2: 1}

Step 3: i=2, num=4, complement = 6-4 = 2
        Is 2 in seen? ✅ Yes! seen[2] = 1
        return [1, 2]

Total operations: 3 (Brute Force: 3 → equal in this case, but the difference is large on bigger inputs)
```

---

## Approach 3: Space Complexity Optimization

### The problem with the previous solution

> The Hash Map uses O(n) extra memory.
> If we need to save memory and can sacrifice some time:
> We **sort** the array and use **Two Pointers**.
>
> **But the problem:** Sorting loses the original indices!
> Therefore, we need to store the indices separately.

### Optimization idea

> 1. Store each element as a `(value, original_index)` pair
> 2. Sort by value — O(n log n)
> 3. Two Pointers: `left = 0`, `right = n-1`
>    - Sum too small → left++
>    - Sum too large → right--
>    - Sum equals target → return the original indices

### Algorithm (step-by-step)

1. Create an array of `(value, index)` pairs
2. Sort by value
3. `left = 0`, `right = n-1`
4. `sum = sorted[left].val + sorted[right].val`
5. `sum == target` → return | `sum < target` → left++ | `sum > target` → right--

### Pseudocode

```text
function twoSum(nums, target):
    indexed = [(nums[i], i) for i in range(n)]
    sort indexed by value

    left = 0, right = n - 1
    while left < right:
        sum = indexed[left].val + indexed[right].val
        if sum == target:
            return [indexed[left].idx, indexed[right].idx]
        elif sum < target:
            left++
        else:
            right--
    return []
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n log n) | O(n log n) for sorting + O(n) for Two Pointers = O(n log n) |
| **Space** | O(n) | For storing the indices. In practice, the same memory as the Hash Map approach. |

> **Note:** Space optimization provides little benefit for this problem because we need to store indices.
> However, in **Two Sum II** (sorted array), Two Pointers gives O(1) space.
> We examine this approach primarily to learn the **Two Pointers pattern**.

### Implementation

#### Go

```go
import "sort"

// twoSum — Two Pointers approach (sort + scan)
// Time: O(n log n), Space: O(n)
func twoSum(nums []int, target int) []int {
    // 1. (value, index) pairs
    type pair struct {
        val, idx int
    }
    indexed := make([]pair, len(nums))
    for i, v := range nums {
        indexed[i] = pair{v, i}
    }

    // 2. Sort by value
    sort.Slice(indexed, func(a, b int) bool {
        return indexed[a].val < indexed[b].val
    })

    // 3. Two Pointers
    left, right := 0, len(indexed)-1
    for left < right {
        sum := indexed[left].val + indexed[right].val
        if sum == target {
            return []int{indexed[left].idx, indexed[right].idx}
        } else if sum < target {
            left++
        } else {
            right--
        }
    }

    return nil
}
```

#### Java

```java
import java.util.Arrays;

class Solution {
    // twoSum — Two Pointers approach (sort + scan)
    // Time: O(n log n), Space: O(n)
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;

        // 1. (value, index) pairs
        int[][] indexed = new int[n][2];
        for (int i = 0; i < n; i++) {
            indexed[i][0] = nums[i]; // value
            indexed[i][1] = i;       // original index
        }

        // 2. Sort by value
        Arrays.sort(indexed, (a, b) -> Integer.compare(a[0], b[0]));

        // 3. Two Pointers
        int left = 0, right = n - 1;
        while (left < right) {
            int sum = indexed[left][0] + indexed[right][0];
            if (sum == target) {
                return new int[]{indexed[left][1], indexed[right][1]};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Two Pointers approach (sort + scan)
        Time: O(n log n), Space: O(n)
        """
        # 1. (value, index) pairs
        indexed = [(val, idx) for idx, val in enumerate(nums)]

        # 2. Sort by value
        indexed.sort(key=lambda x: x[0])

        # 3. Two Pointers
        left, right = 0, len(indexed) - 1
        while left < right:
            total = indexed[left][0] + indexed[right][0]
            if total == target:
                return [indexed[left][1], indexed[right][1]]
            elif total < target:
                left += 1
            else:
                right -= 1

        return []
```

### Dry Run

```text
Input: nums = [3, 2, 4], target = 6

1. indexed = [(3,0), (2,1), (4,2)]
2. sorted  = [(2,1), (3,0), (4,2)]

left=0 (val=2), right=2 (val=4)

Step 1: sum = 2 + 4 = 6
        6 == 6 ✅ FOUND!
        return [indexed[0].idx, indexed[2].idx] = [1, 2]

Total operations: sort(3 log 3 ≈ 5) + 1 comparison = 6
```

---

## Approach 4: Two-pass Hash Map

### Idea

> The difference from One-pass: first we add the **entire array** to the Hash Map, then in a second pass we search for the complement.
> The code is slightly simpler — but it traverses the array 2 times.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | 2 separate loops: O(n) + O(n) = O(2n) = O(n) |
| **Space** | O(n) | n elements in the Hash Map |

### Implementation

#### Go

```go
// twoSum — Two-pass Hash Map approach
// Time: O(n), Space: O(n)
func twoSum(nums []int, target int) []int {
    // Pass 1: add all elements to the Hash Map
    seen := make(map[int]int)
    for i, num := range nums {
        seen[num] = i
    }

    // Pass 2: search for the complement
    for i, num := range nums {
        complement := target - num
        if j, ok := seen[complement]; ok && j != i {
            return []int{i, j}
        }
    }

    return nil
}
```

#### Java

```java
import java.util.HashMap;

class Solution {
    // twoSum — Two-pass Hash Map approach
    // Time: O(n), Space: O(n)
    public int[] twoSum(int[] nums, int target) {
        // Pass 1: add all elements to the Hash Map
        HashMap<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            seen.put(nums[i], i);
        }

        // Pass 2: search for the complement
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement) && seen.get(complement) != i) {
                return new int[]{i, seen.get(complement)};
            }
        }

        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Two-pass Hash Map approach
        Time: O(n), Space: O(n)
        """
        # Pass 1: add all elements to the Hash Map
        seen = {num: i for i, num in enumerate(nums)}

        # Pass 2: search for the complement
        for i, num in enumerate(nums):
            complement = target - num
            if complement in seen and seen[complement] != i:
                return [i, seen[complement]]

        return []
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^2) | O(1) | Simple, uses no extra memory | Slow, TLE on large inputs |
| 2 | One-pass Hash Map | O(n) | O(n) | Fastest, single pass | O(n) extra memory |
| 3 | Sort + Two Pointers | O(n log n) | O(n) | Two Pointers pattern | Must store indices, not the fastest |
| 4 | Two-pass Hash Map | O(n) | O(n) | Simple, easy to understand | Traverses the array twice |

### Which solution to choose?

- **In an interview:** Approach 2 (One-pass Hash Map) — fast and elegant, impresses the interviewer
- **In production:** Approach 2 — fastest, memory is not an issue
- **On Leetcode:** Approach 2 — best Time Complexity
- **For learning:** All 4 — each teaches a different pattern

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | First pair | `nums=[2,7], target=9` | `[0,1]` | Minimal input, found immediately |
| 2 | Last pair | `nums=[1,2,3,4], target=7` | `[2,3]` | Last elements |
| 3 | Duplicate values | `nums=[3,3], target=6` | `[0,1]` | Same value, different indices |
| 4 | Negative numbers | `nums=[-1,-2,-3,-4], target=-6` | `[1,3]` | Negative + negative |
| 5 | Mixed numbers | `nums=[-3,4,3,90], target=0` | `[0,2]` | Negative + positive = 0 |
| 6 | Zero value | `nums=[0,4,3,0], target=0` | `[0,3]` | 0 + 0 = 0 |

---

## Common Mistakes

### Mistake 1: Pairing an element with itself

```python
# ❌ WRONG — using the same element twice
seen = {num: i for i, num in enumerate(nums)}
for i, num in enumerate(nums):
    complement = target - num
    if complement in seen:  # j == i is possible!
        return [i, seen[complement]]

# ✅ CORRECT — check that j != i
for i, num in enumerate(nums):
    complement = target - num
    if complement in seen and seen[complement] != i:
        return [i, seen[complement]]
```

**Reason:** In `nums=[3,2,4], target=6`, `nums[0]=3`, complement=3 — the Hash Map has `3:0`, but that is the element itself.

### Mistake 2: Duplicate values with Hash Map

```python
# ❌ WRONG — with duplicates, the last index overwrites
seen = {num: i for i, num in enumerate(nums)}
# nums=[3,3], target=6 → seen = {3: 1} (index 0 is lost)

# ✅ CORRECT — the One-pass approach avoids this problem
# Because we check the complement before adding the element
```

**Reason:** The One-pass Hash Map naturally solves this problem — the complement is checked before the element is added.

### Mistake 3: Return type error

```go
// ❌ WRONG — in Go, nil and []int{} are different
return []int{}

// ✅ CORRECT — on Leetcode, returning nil in Go is sufficient
return nil
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [167. Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | 🟡 Medium | Sorted array → Two Pointers O(1) space |
| 2 | [15. 3Sum](https://leetcode.com/problems/3sum/) | 🟡 Medium | Sum of 3 numbers, sort + Two Pointers |
| 3 | [18. 4Sum](https://leetcode.com/problems/4sum/) | 🟡 Medium | Sum of 4 numbers |
| 4 | [560. Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/) | 🟡 Medium | Prefix sum + Hash Map |
| 5 | [653. Two Sum IV - Input is a BST](https://leetcode.com/problems/two-sum-iv-input-is-a-bst/) | 🟢 Easy | Two Sum in a BST |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — two pointers (i, j) check all pairs
> - **TC Optimized** tab — finds the answer in a single pass with Hash Map
> - **SC Optimized** tab — Sort + Two Pointers
> - **Compare All** tab — compares the number of operations between Brute Force and Hash Map
