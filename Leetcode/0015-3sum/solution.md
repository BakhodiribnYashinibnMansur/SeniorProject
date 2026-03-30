# 0015. 3Sum

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Sort + Two Pointers](#approach-2-sort--two-pointers)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [15. 3Sum](https://leetcode.com/problems/3sum/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Two Pointers`, `Sorting` |

### Description

> Given an integer array `nums`, return all the triplets `[nums[i], nums[j], nums[k]]` such that `i != j`, `i != k`, and `j != k`, and `nums[i] + nums[j] + nums[k] == 0`.
>
> Notice that the solution set must not contain duplicate triplets.

### Examples

```
Example 1:
Input: nums = [-1,0,1,2,-1,-4]
Output: [[-1,-1,2],[-1,0,1]]
Explanation:
  nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0
  nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0
  nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0
  The distinct triplets are [-1,0,1] and [-1,-1,2].

Example 2:
Input: nums = [0,1,1]
Output: []
Explanation: The only possible triplet does not sum up to 0.

Example 3:
Input: nums = [0,0,0]
Output: [[0,0,0]]
Explanation: The only possible triplet sums up to 0.
```

### Constraints

- `3 <= nums.length <= 3000`
- `-10^5 <= nums[i] <= 10^5`

---

## Problem Breakdown

### 1. What is being asked?

Find **all unique triplets** in the array that sum to zero. Return the **values** (not indices), and there must be **no duplicate triplets** in the result.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers |

Important observations about the input:
- The array is **unsorted** (not sorted)
- **Duplicates may exist** (Example 1: two `-1` values)
- The array contains at least 3 elements
- Negative numbers are possible

### 3. What is the output?

- **A list of triplets** `[[a, b, c], ...]`
- Each triplet satisfies `a + b + c = 0`
- **No duplicate triplets** (e.g., `[-1,0,1]` should appear only once)
- Order of triplets and order within a triplet do not matter
- Return an empty list if no triplets exist

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 3000` | O(n^2) works (~9,000,000 operations), O(n^3) is borderline (~27 billion) |
| `-10^5 <= nums[i] <= 10^5` | int32 is sufficient, sum of three fits easily |
| No duplicate triplets | Must handle deduplication carefully |

### 5. Step-by-step example analysis

#### Example 1: `nums = [-1, 0, 1, 2, -1, -4]`

```text
After sorting: [-4, -1, -1, 0, 1, 2]

Fix i=0 (nums[i]=-4): target = 4
  left=1 (-1), right=5 (2): -1 + 2 = 1 < 4 → left++
  left=2 (-1), right=5 (2): -1 + 2 = 1 < 4 → left++
  left=3 (0),  right=5 (2): 0 + 2  = 2 < 4 → left++
  left=4 (1),  right=5 (2): 1 + 2  = 3 < 4 → left++
  left=5, left >= right → stop

Fix i=1 (nums[i]=-1): target = 1
  left=2 (-1), right=5 (2): -1 + 2 = 1 == 1 ✅ → triplet: [-1, -1, 2]
  Skip duplicates → left=3, right=4
  left=3 (0), right=4 (1): 0 + 1 = 1 == 1 ✅ → triplet: [-1, 0, 1]
  left=4, right=3, left >= right → stop

Fix i=2 (nums[i]=-1): SKIP — duplicate of i=1

Fix i=3 (nums[i]=0): target = 0
  left=4 (1), right=5 (2): 1 + 2 = 3 > 0 → right--
  left=4, right=4, left >= right → stop

Result: [[-1, -1, 2], [-1, 0, 1]]
```

#### Example 3: `nums = [0, 0, 0]`

```text
After sorting: [0, 0, 0]

Fix i=0 (nums[i]=0): target = 0
  left=1 (0), right=2 (0): 0 + 0 = 0 == 0 ✅ → triplet: [0, 0, 0]
  left=2, right=1 → stop

Result: [[0, 0, 0]]
```

### 6. Key Observations

1. **Sorting enables deduplication** — Identical values are adjacent, so skipping duplicates is easy.
2. **Reduces to Two Sum** — For each fixed element `nums[i]`, find two elements summing to `-nums[i]`.
3. **Two Pointers on sorted array** — After fixing `i`, use `left` and `right` pointers converging inward.
4. **Early termination** — If `nums[i] > 0` after sorting, no valid triplet is possible (all remaining values are positive).

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Sort + Two Pointers | O(n^2), natural deduplication | 3Sum (this problem) |
| Hash Set | O(n^2), uses set for dedup | Works but harder to implement cleanly |
| Brute Force | Always works, but slow | All problems |

**Chosen pattern:** `Sort + Two Pointers`
**Reason:** Sorting makes duplicate skipping trivial, and Two Pointers gives O(n) for the inner loop, yielding O(n^2) total.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: check all possible triplets using three nested loops.
> Sort first so that duplicates can be detected by comparing with previous values.
> Use a set to avoid duplicate triplets.

### Algorithm (step-by-step)

1. Sort the array
2. Three nested loops: `i`, `j = i+1`, `k = j+1`
3. If `nums[i] + nums[j] + nums[k] == 0` → add to result (if not already seen)
4. Use a set of tuples for deduplication

### Pseudocode

```text
function threeSum(nums):
    sort(nums)
    result = []
    seen = set()
    for i = 0 to n-3:
        for j = i+1 to n-2:
            for k = j+1 to n-1:
                if nums[i] + nums[j] + nums[k] == 0:
                    triplet = (nums[i], nums[j], nums[k])
                    if triplet not in seen:
                        seen.add(triplet)
                        result.append([nums[i], nums[j], nums[k]])
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^3) | Three nested loops checking all triplets |
| **Space** | O(n) | Set for storing seen triplets |

### Implementation

#### Go

```go
func threeSumBruteForce(nums []int) [][]int {
    sort.Ints(nums)
    n := len(nums)
    result := [][]int{}
    seen := map[[3]int]bool{}

    for i := 0; i < n-2; i++ {
        for j := i + 1; j < n-1; j++ {
            for k := j + 1; k < n; k++ {
                if nums[i]+nums[j]+nums[k] == 0 {
                    triplet := [3]int{nums[i], nums[j], nums[k]}
                    if !seen[triplet] {
                        seen[triplet] = true
                        result = append(result, []int{nums[i], nums[j], nums[k]})
                    }
                }
            }
        }
    }

    return result
}
```

#### Java

```java
public List<List<Integer>> threeSumBruteForce(int[] nums) {
    Arrays.sort(nums);
    int n = nums.length;
    Set<List<Integer>> resultSet = new LinkedHashSet<>();

    for (int i = 0; i < n - 2; i++) {
        for (int j = i + 1; j < n - 1; j++) {
            for (int k = j + 1; k < n; k++) {
                if (nums[i] + nums[j] + nums[k] == 0) {
                    resultSet.add(Arrays.asList(nums[i], nums[j], nums[k]));
                }
            }
        }
    }

    return new ArrayList<>(resultSet);
}
```

#### Python

```python
def threeSumBruteForce(self, nums: list[int]) -> list[list[int]]:
    nums.sort()
    n = len(nums)
    result = []
    seen = set()

    for i in range(n - 2):
        for j in range(i + 1, n - 1):
            for k in range(j + 1, n):
                if nums[i] + nums[j] + nums[k] == 0:
                    triplet = (nums[i], nums[j], nums[k])
                    if triplet not in seen:
                        seen.add(triplet)
                        result.append([nums[i], nums[j], nums[k]])

    return result
```

### Dry Run

```text
Input: nums = [-1, 0, 1, 2, -1, -4]
After sorting: [-4, -1, -1, 0, 1, 2]

i=0 (-4):
  j=1 (-1): k=2: -4-1-1=-6 ❌  k=3: -4-1+0=-5 ❌  k=4: -4-1+1=-4 ❌  k=5: -4-1+2=-3 ❌
  j=2 (-1): k=3: -4-1+0=-5 ❌  k=4: -4-1+1=-4 ❌  k=5: -4-1+2=-3 ❌
  j=3 (0):  k=4: -4+0+1=-3 ❌  k=5: -4+0+2=-2 ❌
  j=4 (1):  k=5: -4+1+2=-1 ❌

i=1 (-1):
  j=2 (-1): k=3: -1-1+0=-2 ❌  k=4: -1-1+1=-1 ❌  k=5: -1-1+2=0 ✅ → [-1,-1,2]
  j=3 (0):  k=4: -1+0+1=0  ✅ → [-1,0,1]          k=5: -1+0+2=1 ❌
  j=4 (1):  k=5: -1+1+2=2  ❌

i=2 (-1):
  j=3 (0):  k=4: -1+0+1=0  ✅ → [-1,0,1] DUPLICATE (already seen)
  ...

Total comparisons: 20 (for n=6)
Result: [[-1,-1,2], [-1,0,1]]
```

---

## Approach 2: Sort + Two Pointers

### The problem with Brute Force

> O(n^3) is too slow for n = 3000 (27 billion operations).
> We need to reduce the inner search from O(n^2) to O(n).

### Optimization idea

> 1. **Sort** the array
> 2. **Fix one element** (`nums[i]`), reducing the problem to **Two Sum** on a sorted subarray
> 3. **Two Pointers** (`left`, `right`) converging inward to find pairs summing to `-nums[i]`
> 4. **Skip duplicates** at all three positions to avoid duplicate triplets

### Algorithm (step-by-step)

1. Sort the array
2. For each `i` from `0` to `n-3`:
   - If `i > 0` and `nums[i] == nums[i-1]` → skip (duplicate first element)
   - If `nums[i] > 0` → break (early termination)
   - Set `left = i + 1`, `right = n - 1`, `target = -nums[i]`
   - While `left < right`:
     - `sum = nums[left] + nums[right]`
     - If `sum == target` → add triplet, skip duplicates, move both pointers
     - If `sum < target` → `left++`
     - If `sum > target` → `right--`

### Pseudocode

```text
function threeSum(nums):
    sort(nums)
    result = []
    for i = 0 to n-3:
        if i > 0 and nums[i] == nums[i-1]: continue
        if nums[i] > 0: break

        left = i + 1, right = n - 1
        target = -nums[i]

        while left < right:
            sum = nums[left] + nums[right]
            if sum == target:
                result.append([nums[i], nums[left], nums[right]])
                while left < right and nums[left] == nums[left+1]: left++
                while left < right and nums[right] == nums[right-1]: right--
                left++; right--
            elif sum < target:
                left++
            else:
                right--
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | O(n log n) sort + O(n) outer loop * O(n) two-pointer = O(n^2) |
| **Space** | O(1) | In-place sorting, no extra data structures (output not counted) |

### Implementation

#### Go

```go
func threeSum(nums []int) [][]int {
    sort.Ints(nums)
    n := len(nums)
    result := [][]int{}

    for i := 0; i < n-2; i++ {
        if i > 0 && nums[i] == nums[i-1] { continue }
        if nums[i] > 0 { break }

        left, right := i+1, n-1
        target := -nums[i]

        for left < right {
            sum := nums[left] + nums[right]
            if sum == target {
                result = append(result, []int{nums[i], nums[left], nums[right]})
                for left < right && nums[left] == nums[left+1] { left++ }
                for left < right && nums[right] == nums[right-1] { right-- }
                left++
                right--
            } else if sum < target {
                left++
            } else {
                right--
            }
        }
    }

    return result
}
```

#### Java

```java
public List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    int n = nums.length;
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < n - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        if (nums[i] > 0) break;

        int left = i + 1, right = n - 1;
        int target = -nums[i];

        while (left < right) {
            int sum = nums[left] + nums[right];
            if (sum == target) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                while (left < right && nums[left] == nums[left + 1]) left++;
                while (left < right && nums[right] == nums[right - 1]) right--;
                left++;
                right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }

    return result;
}
```

#### Python

```python
def threeSum(self, nums: list[int]) -> list[list[int]]:
    nums.sort()
    n = len(nums)
    result = []

    for i in range(n - 2):
        if i > 0 and nums[i] == nums[i - 1]:
            continue
        if nums[i] > 0:
            break

        left, right = i + 1, n - 1
        target = -nums[i]

        while left < right:
            total = nums[left] + nums[right]
            if total == target:
                result.append([nums[i], nums[left], nums[right]])
                while left < right and nums[left] == nums[left + 1]:
                    left += 1
                while left < right and nums[right] == nums[right - 1]:
                    right -= 1
                left += 1
                right -= 1
            elif total < target:
                left += 1
            else:
                right -= 1

    return result
```

### Dry Run

```text
Input: nums = [-1, 0, 1, 2, -1, -4]
After sorting: [-4, -1, -1, 0, 1, 2]

i=0, nums[i]=-4, target=4:
  left=1(-1), right=5(2): -1+2=1 < 4 → left++
  left=2(-1), right=5(2): -1+2=1 < 4 → left++
  left=3(0),  right=5(2): 0+2=2  < 4 → left++
  left=4(1),  right=5(2): 1+2=3  < 4 → left++
  left=5 >= right=5 → DONE

i=1, nums[i]=-1, target=1:
  left=2(-1), right=5(2): -1+2=1 == 1 ✅ → [-1,-1,2]
    Skip dups → left=3, right=4
  left=3(0), right=4(1): 0+1=1 == 1 ✅ → [-1,0,1]
    left=4, right=3 → DONE

i=2, nums[i]=-1: SKIP (duplicate, nums[2]==nums[1])

i=3, nums[i]=0, target=0:
  left=4(1), right=5(2): 1+2=3 > 0 → right--
  left=4 >= right=4 → DONE

Result: [[-1,-1,2], [-1,0,1]]
Total operations: ~12 (vs 20 for Brute Force on this small input)
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^3) | O(n) | Simple logic | Too slow, TLE on large inputs |
| 2 | Sort + Two Pointers | O(n^2) | O(1) | Optimal, clean dedup | Requires sorting (modifies input) |

### Which solution to choose?

- **In an interview:** Approach 2 (Sort + Two Pointers) — optimal and demonstrates mastery of two patterns
- **In production:** Approach 2 — best time complexity, minimal memory
- **On Leetcode:** Approach 2 — easily passes all test cases
- **For learning:** Both — Brute Force shows the problem clearly, Two Pointers shows the optimization

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | LeetCode Example | `[-1,0,1,2,-1,-4]` | `[[-1,-1,2],[-1,0,1]]` | Standard case with duplicates |
| 2 | All zeros | `[0,0,0]` | `[[0,0,0]]` | Only valid triplet is three zeros |
| 3 | No valid triplets | `[0,1,1]` | `[]` | No three numbers sum to zero |
| 4 | All positive | `[1,2,3,4,5]` | `[]` | Positive numbers cannot sum to zero |
| 5 | All negative | `[-5,-4,-3,-2,-1]` | `[]` | Negative numbers cannot sum to zero |
| 6 | Many duplicates | `[0,0,0,0,0]` | `[[0,0,0]]` | Deduplication must produce only one triplet |
| 7 | Minimum length | `[-1,0,1]` | `[[-1,0,1]]` | Exactly 3 elements |
| 8 | Two elements | `[-1,1]` | `[]` | Less than 3 elements — no triplet possible |

---

## Common Mistakes

### Mistake 1: Not skipping duplicates for the first element

```python
# ❌ WRONG — duplicate triplets in result
for i in range(n - 2):
    left, right = i + 1, n - 1
    # ...finds [-1,0,1] twice when two -1's exist

# ✅ CORRECT — skip duplicate first elements
for i in range(n - 2):
    if i > 0 and nums[i] == nums[i - 1]:
        continue
    # ...
```

**Reason:** If `nums[1] == nums[2] == -1`, both iterations produce the same set of triplets.

### Mistake 2: Not skipping duplicates for left/right after finding a triplet

```python
# ❌ WRONG — finds the same triplet multiple times
if total == target:
    result.append([nums[i], nums[left], nums[right]])
    left += 1
    right -= 1

# ✅ CORRECT — skip all duplicate values
if total == target:
    result.append([nums[i], nums[left], nums[right]])
    while left < right and nums[left] == nums[left + 1]: left += 1
    while left < right and nums[right] == nums[right - 1]: right -= 1
    left += 1
    right -= 1
```

**Reason:** After finding `[-1, 0, 1]`, if there are multiple `0`s or `1`s, the same triplet would be added again.

### Mistake 3: Forgetting early termination

```python
# ❌ SLOWER — continues even when nums[i] > 0
for i in range(n - 2):
    if i > 0 and nums[i] == nums[i - 1]: continue
    # ...searches pointlessly when all remaining values are positive

# ✅ FASTER — break early
for i in range(n - 2):
    if i > 0 and nums[i] == nums[i - 1]: continue
    if nums[i] > 0: break  # No triplet possible
    # ...
```

**Reason:** After sorting, if `nums[i] > 0`, then `nums[left] > 0` and `nums[right] > 0`, so their sum can never be zero.

### Mistake 4: Forgetting to sort the array

```python
# ❌ WRONG — Two Pointers requires a sorted array
def threeSum(self, nums):
    for i in range(len(nums)):
        left, right = i + 1, len(nums) - 1
        # Two Pointers on unsorted array — incorrect results!

# ✅ CORRECT — sort first
def threeSum(self, nums):
    nums.sort()  # Essential for Two Pointers!
    # ...
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [1. Two Sum](https://leetcode.com/problems/two-sum/) | :green_circle: Easy | Foundation — find two numbers summing to target |
| 2 | [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/) | :yellow_circle: Medium | Same pattern, find closest sum instead of exact |
| 3 | [18. 4Sum](https://leetcode.com/problems/4sum/) | :yellow_circle: Medium | Extension to 4 elements, same Sort + Two Pointers |
| 4 | [167. Two Sum II - Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | :yellow_circle: Medium | Two Pointers on sorted array (inner loop of 3Sum) |
| 5 | [259. 3Sum Smaller](https://leetcode.com/problems/3sum-smaller/) | :yellow_circle: Medium | Count triplets with sum < target |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — three nested loops check all triplets
> - **Two Pointers** tab — sort + fix one element + two pointers converging
> - Visualizes pointer movement, duplicate skipping, and triplet collection
