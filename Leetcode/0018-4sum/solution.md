# 0018. 4Sum

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
| **Leetcode** | [18. 4Sum](https://leetcode.com/problems/4sum/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Two Pointers`, `Sorting` |

### Description

> Given an array `nums` of `n` integers, return an array of all the **unique quadruplets** `[nums[a], nums[b], nums[c], nums[d]]` such that:
>
> - `0 <= a, b, c, d < n`
> - `a`, `b`, `c`, and `d` are **distinct**
> - `nums[a] + nums[b] + nums[c] + nums[d] == target`
>
> You may return the answer in **any order**.

### Examples

```
Example 1:
Input: nums = [1,0,-1,0,-2,2], target = 0
Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]

Example 2:
Input: nums = [2,2,2,2,2], target = 8
Output: [[2,2,2,2]]
```

### Constraints

- `1 <= nums.length <= 200`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= target <= 10^9`

---

## Problem Breakdown

### 1. What is being asked?

Find **all unique quadruplets** in the array that sum to a given `target`. Return the **values** (not indices), and there must be **no duplicate quadruplets** in the result.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Array of integers |
| `target` | `int` | Target sum for the quadruplet |

Important observations about the input:
- The array is **unsorted** (not sorted)
- **Duplicates may exist** (Example 2: five `2` values)
- The target can be **negative, zero, or positive**
- Values can be very large (`-10^9 to 10^9`) — **integer overflow** risk
- The array contains at least 1 element

### 3. What is the output?

- **A list of quadruplets** `[[a, b, c, d], ...]`
- Each quadruplet satisfies `a + b + c + d = target`
- **No duplicate quadruplets** (e.g., `[-2,-1,1,2]` should appear only once)
- Order of quadruplets and order within a quadruplet do not matter
- Return an empty list if no quadruplets exist

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `nums.length <= 200` | O(n^3) works (~8,000,000 operations), O(n^4) is borderline (~1.6 billion) |
| `-10^9 <= nums[i] <= 10^9` | Sum of four values can overflow int32! Must use int64/long |
| `-10^9 <= target <= 10^9` | Target itself fits in int32, but sum comparison needs int64 |
| No duplicate quadruplets | Must handle deduplication carefully |

### 5. Step-by-step example analysis

#### Example 1: `nums = [1, 0, -1, 0, -2, 2]`, `target = 0`

```text
After sorting: [-2, -1, 0, 0, 1, 2]

Fix i=0 (nums[i]=-2):
  Fix j=1 (nums[j]=-1): target_remain = 0-(-2)-(-1) = 3
    left=2 (0), right=5 (2): 0 + 2 = 2 < 3 → left++
    left=3 (0), right=5 (2): 0 + 2 = 2 < 3 → left++
    left=4 (1), right=5 (2): 1 + 2 = 3 == 3 ✅ → [-2, -1, 1, 2]
    left=5 >= right=4 → stop

  Fix j=2 (nums[j]=0): target_remain = 0-(-2)-0 = 2
    left=3 (0), right=5 (2): 0 + 2 = 2 == 2 ✅ → [-2, 0, 0, 2]
    left=4 (1), right=4, left >= right → stop

  Fix j=3 (nums[j]=0): SKIP — duplicate of j=2

  Fix j=4 (nums[j]=1): target_remain = 0-(-2)-1 = 1
    left=5 (2), left >= right → stop

Fix i=1 (nums[i]=-1):
  Fix j=2 (nums[j]=0): target_remain = 0-(-1)-0 = 1
    left=3 (0), right=5 (2): 0 + 2 = 2 > 1 → right--
    left=3 (0), right=4 (1): 0 + 1 = 1 == 1 ✅ → [-1, 0, 0, 1]
    left=4 >= right=3 → stop

  Fix j=3 (nums[j]=0): SKIP — duplicate of j=2

Fix i=2 (nums[i]=0): 0+0+1+2 = 3 > 0 → BREAK (early termination)

Result: [[-2, -1, 1, 2], [-2, 0, 0, 2], [-1, 0, 0, 1]]
```

#### Example 2: `nums = [2, 2, 2, 2, 2]`, `target = 8`

```text
After sorting: [2, 2, 2, 2, 2]

Fix i=0 (nums[i]=2):
  Fix j=1 (nums[j]=2): target_remain = 8-2-2 = 4
    left=2 (2), right=4 (2): 2 + 2 = 4 == 4 ✅ → [2, 2, 2, 2]
    Skip duplicates → left=3, right=3, left >= right → stop

  Fix j=2: SKIP — duplicate
  Fix j=3: SKIP — duplicate

Fix i=1: SKIP — duplicate

Result: [[2, 2, 2, 2]]
```

### 6. Key Observations

1. **Extension of 3Sum** — Fix two elements instead of one, then use Two Pointers for the remaining pair.
2. **Sorting enables deduplication** — Identical values are adjacent, so skipping duplicates is easy at all four positions.
3. **Integer overflow** — Sum of four values up to 10^9 can exceed int32 range (4 * 10^9 > 2.1 * 10^9). Must use int64/long.
4. **Early termination and pruning** — Check min/max possible sums to skip unnecessary iterations.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Sort + Two Pointers | O(n^3), natural deduplication | 4Sum (this problem) |
| Hash Map | O(n^3), uses map for pair sums | Works but harder to deduplicate |
| Brute Force | Always works, but slow | All problems |

**Chosen pattern:** `Sort + Two Pointers`
**Reason:** Sorting makes duplicate skipping trivial at all levels. Two Pointers gives O(n) for the innermost loop, yielding O(n^3) total. Pruning with early termination further improves practical performance.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: check all possible quadruplets using four nested loops.
> Sort first so that duplicates can be detected by comparing with previous values.
> Use a set to avoid duplicate quadruplets.

### Algorithm (step-by-step)

1. Sort the array
2. Four nested loops: `i`, `j = i+1`, `k = j+1`, `l = k+1`
3. If `nums[i] + nums[j] + nums[k] + nums[l] == target` → add to result (if not already seen)
4. Use a set of tuples for deduplication

### Pseudocode

```text
function fourSum(nums, target):
    sort(nums)
    result = []
    seen = set()
    for i = 0 to n-4:
        for j = i+1 to n-3:
            for k = j+1 to n-2:
                for l = k+1 to n-1:
                    if nums[i] + nums[j] + nums[k] + nums[l] == target:
                        quad = (nums[i], nums[j], nums[k], nums[l])
                        if quad not in seen:
                            seen.add(quad)
                            result.append([nums[i], nums[j], nums[k], nums[l]])
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^4) | Four nested loops checking all quadruplets |
| **Space** | O(n) | Set for storing seen quadruplets |

### Implementation

#### Go

```go
func fourSumBruteForce(nums []int, target int) [][]int {
    sort.Ints(nums)
    n := len(nums)
    result := [][]int{}
    seen := map[[4]int]bool{}

    for i := 0; i < n-3; i++ {
        for j := i + 1; j < n-2; j++ {
            for k := j + 1; k < n-1; k++ {
                for l := k + 1; l < n; l++ {
                    if int64(nums[i])+int64(nums[j])+int64(nums[k])+int64(nums[l]) == int64(target) {
                        quad := [4]int{nums[i], nums[j], nums[k], nums[l]}
                        if !seen[quad] {
                            seen[quad] = true
                            result = append(result, []int{nums[i], nums[j], nums[k], nums[l]})
                        }
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
public List<List<Integer>> fourSumBruteForce(int[] nums, int target) {
    Arrays.sort(nums);
    int n = nums.length;
    Set<List<Integer>> resultSet = new LinkedHashSet<>();

    for (int i = 0; i < n - 3; i++) {
        for (int j = i + 1; j < n - 2; j++) {
            for (int k = j + 1; k < n - 1; k++) {
                for (int l = k + 1; l < n; l++) {
                    if ((long) nums[i] + nums[j] + nums[k] + nums[l] == target) {
                        resultSet.add(Arrays.asList(nums[i], nums[j], nums[k], nums[l]));
                    }
                }
            }
        }
    }

    return new ArrayList<>(resultSet);
}
```

#### Python

```python
def fourSumBruteForce(self, nums: list[int], target: int) -> list[list[int]]:
    nums.sort()
    n = len(nums)
    result = []
    seen = set()

    for i in range(n - 3):
        for j in range(i + 1, n - 2):
            for k in range(j + 1, n - 1):
                for l in range(k + 1, n):
                    if nums[i] + nums[j] + nums[k] + nums[l] == target:
                        quad = (nums[i], nums[j], nums[k], nums[l])
                        if quad not in seen:
                            seen.add(quad)
                            result.append([nums[i], nums[j], nums[k], nums[l]])

    return result
```

### Dry Run

```text
Input: nums = [1, 0, -1, 0, -2, 2], target = 0
After sorting: [-2, -1, 0, 0, 1, 2]

i=0 (-2):
  j=1 (-1):
    k=2 (0):
      l=3: -2-1+0+0=-3 ❌  l=4: -2-1+0+1=-2 ❌  l=5: -2-1+0+2=-1 ❌
    k=3 (0):
      l=4: -2-1+0+1=-2 ❌  l=5: -2-1+0+2=-1 ❌
    k=4 (1):
      l=5: -2-1+1+2=0 ✅ → [-2,-1,1,2]
  j=2 (0):
    k=3 (0):
      l=4: -2+0+0+1=-1 ❌  l=5: -2+0+0+2=0 ✅ → [-2,0,0,2]
    k=4 (1):
      l=5: -2+0+1+2=1 ❌
  j=3 (0):
    k=4 (1):
      l=5: -2+0+1+2=1 ❌
  j=4 (1):
    k=5: only one element left → skip

i=1 (-1):
  j=2 (0):
    k=3 (0):
      l=4: -1+0+0+1=0 ✅ → [-1,0,0,1]
      l=5: -1+0+0+2=1 ❌
    k=4 (1):
      l=5: -1+0+1+2=2 ❌
  j=3 (0):
    k=4 (1):
      l=5: -1+0+1+2=2 ❌

i=2 (0):
  j=3 (0):
    k=4 (1):
      l=5: 0+0+1+2=3 ❌

Total comparisons: 15 (for n=6)
Result: [[-2,-1,1,2], [-2,0,0,2], [-1,0,0,1]]
```

---

## Approach 2: Sort + Two Pointers

### The problem with Brute Force

> O(n^4) is too slow for n = 200 (1.6 billion operations).
> We need to reduce the innermost search from O(n^2) to O(n).

### Optimization idea

> 1. **Sort** the array
> 2. **Fix two elements** (`nums[i]` and `nums[j]`), reducing the problem to **Two Sum** on a sorted subarray
> 3. **Two Pointers** (`left`, `right`) converging inward to find pairs summing to `target - nums[i] - nums[j]`
> 4. **Skip duplicates** at all four positions to avoid duplicate quadruplets
> 5. **Pruning** — early termination and skipping when min/max sums are out of range

### Algorithm (step-by-step)

1. Sort the array
2. For each `i` from `0` to `n-4`:
   - If `i > 0` and `nums[i] == nums[i-1]` → skip (duplicate first element)
   - If `nums[i] + nums[i+1] + nums[i+2] + nums[i+3] > target` → break (min sum too large)
   - If `nums[i] + nums[n-3] + nums[n-2] + nums[n-1] < target` → continue (max sum too small)
   - For each `j` from `i+1` to `n-3`:
     - If `j > i+1` and `nums[j] == nums[j-1]` → skip (duplicate second element)
     - Apply similar pruning for j
     - Set `left = j + 1`, `right = n - 1`, `remain = target - nums[i] - nums[j]`
     - While `left < right`:
       - `sum = nums[left] + nums[right]`
       - If `sum == remain` → add quadruplet, skip duplicates, move both pointers
       - If `sum < remain` → `left++`
       - If `sum > remain` → `right--`

### Pseudocode

```text
function fourSum(nums, target):
    sort(nums)
    result = []
    for i = 0 to n-4:
        if i > 0 and nums[i] == nums[i-1]: continue
        if nums[i]+nums[i+1]+nums[i+2]+nums[i+3] > target: break
        if nums[i]+nums[n-3]+nums[n-2]+nums[n-1] < target: continue

        for j = i+1 to n-3:
            if j > i+1 and nums[j] == nums[j-1]: continue
            if nums[i]+nums[j]+nums[j+1]+nums[j+2] > target: break
            if nums[i]+nums[j]+nums[n-2]+nums[n-1] < target: continue

            left = j + 1, right = n - 1
            remain = target - nums[i] - nums[j]

            while left < right:
                sum = nums[left] + nums[right]
                if sum == remain:
                    result.append([nums[i], nums[j], nums[left], nums[right]])
                    while left < right and nums[left] == nums[left+1]: left++
                    while left < right and nums[right] == nums[right-1]: right--
                    left++; right--
                elif sum < remain:
                    left++
                else:
                    right--
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^3) | O(n log n) sort + O(n^2) outer loops * O(n) two-pointer = O(n^3) |
| **Space** | O(1) | In-place sorting, no extra data structures (output not counted) |

### Implementation

#### Go

```go
func fourSum(nums []int, target int) [][]int {
    sort.Ints(nums)
    n := len(nums)
    result := [][]int{}

    for i := 0; i < n-3; i++ {
        if i > 0 && nums[i] == nums[i-1] { continue }
        if int64(nums[i])+int64(nums[i+1])+int64(nums[i+2])+int64(nums[i+3]) > int64(target) { break }
        if int64(nums[i])+int64(nums[n-3])+int64(nums[n-2])+int64(nums[n-1]) < int64(target) { continue }

        for j := i + 1; j < n-2; j++ {
            if j > i+1 && nums[j] == nums[j-1] { continue }
            if int64(nums[i])+int64(nums[j])+int64(nums[j+1])+int64(nums[j+2]) > int64(target) { break }
            if int64(nums[i])+int64(nums[j])+int64(nums[n-2])+int64(nums[n-1]) < int64(target) { continue }

            left, right := j+1, n-1
            remain := int64(target) - int64(nums[i]) - int64(nums[j])

            for left < right {
                sum := int64(nums[left]) + int64(nums[right])
                if sum == remain {
                    result = append(result, []int{nums[i], nums[j], nums[left], nums[right]})
                    for left < right && nums[left] == nums[left+1] { left++ }
                    for left < right && nums[right] == nums[right-1] { right-- }
                    left++
                    right--
                } else if sum < remain {
                    left++
                } else {
                    right--
                }
            }
        }
    }

    return result
}
```

#### Java

```java
public List<List<Integer>> fourSum(int[] nums, int target) {
    Arrays.sort(nums);
    int n = nums.length;
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < n - 3; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        if ((long) nums[i] + nums[i+1] + nums[i+2] + nums[i+3] > target) break;
        if ((long) nums[i] + nums[n-3] + nums[n-2] + nums[n-1] < target) continue;

        for (int j = i + 1; j < n - 2; j++) {
            if (j > i + 1 && nums[j] == nums[j - 1]) continue;
            if ((long) nums[i] + nums[j] + nums[j+1] + nums[j+2] > target) break;
            if ((long) nums[i] + nums[j] + nums[n-2] + nums[n-1] < target) continue;

            int left = j + 1, right = n - 1;
            long remain = (long) target - nums[i] - nums[j];

            while (left < right) {
                long sum = (long) nums[left] + nums[right];
                if (sum == remain) {
                    result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++;
                    right--;
                } else if (sum < remain) {
                    left++;
                } else {
                    right--;
                }
            }
        }
    }

    return result;
}
```

#### Python

```python
def fourSum(self, nums: list[int], target: int) -> list[list[int]]:
    nums.sort()
    n = len(nums)
    result = []

    for i in range(n - 3):
        if i > 0 and nums[i] == nums[i - 1]:
            continue
        if nums[i] + nums[i+1] + nums[i+2] + nums[i+3] > target:
            break
        if nums[i] + nums[n-3] + nums[n-2] + nums[n-1] < target:
            continue

        for j in range(i + 1, n - 2):
            if j > i + 1 and nums[j] == nums[j - 1]:
                continue
            if nums[i] + nums[j] + nums[j+1] + nums[j+2] > target:
                break
            if nums[i] + nums[j] + nums[n-2] + nums[n-1] < target:
                continue

            left, right = j + 1, n - 1
            remain = target - nums[i] - nums[j]

            while left < right:
                total = nums[left] + nums[right]
                if total == remain:
                    result.append([nums[i], nums[j], nums[left], nums[right]])
                    while left < right and nums[left] == nums[left + 1]:
                        left += 1
                    while left < right and nums[right] == nums[right - 1]:
                        right -= 1
                    left += 1
                    right -= 1
                elif total < remain:
                    left += 1
                else:
                    right -= 1

    return result
```

### Dry Run

```text
Input: nums = [1, 0, -1, 0, -2, 2], target = 0
After sorting: [-2, -1, 0, 0, 1, 2]

i=0, nums[i]=-2:
  min_sum = -2-1+0+0 = -3 <= 0 ✓
  max_sum = -2+0+1+2 = 1 >= 0 ✓

  j=1, nums[j]=-1, remain = 0-(-2)-(-1) = 3:
    left=2(0), right=5(2): 0+2=2 < 3 → left++
    left=3(0), right=5(2): 0+2=2 < 3 → left++
    left=4(1), right=5(2): 1+2=3 == 3 ✅ → [-2,-1,1,2]
    left=5 >= right=4 → DONE

  j=2, nums[j]=0, remain = 0-(-2)-0 = 2:
    left=3(0), right=5(2): 0+2=2 == 2 ✅ → [-2,0,0,2]
    left=4 >= right=4 → DONE

  j=3, nums[j]=0: SKIP (duplicate, nums[3]==nums[2])

i=1, nums[i]=-1:
  j=2, nums[j]=0, remain = 0-(-1)-0 = 1:
    left=3(0), right=5(2): 0+2=2 > 1 → right--
    left=3(0), right=4(1): 0+1=1 == 1 ✅ → [-1,0,0,1]
    left=4 >= right=3 → DONE

  j=3, nums[j]=0: SKIP (duplicate)

i=2, nums[i]=0: min_sum = 0+0+1+2 = 3 > 0 → BREAK

Result: [[-2,-1,1,2], [-2,0,0,2], [-1,0,0,1]]
Total operations: ~10 (vs 15 for Brute Force on this small input)
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^4) | O(n) | Simple logic | Too slow, TLE on large inputs |
| 2 | Sort + Two Pointers | O(n^3) | O(1) | Optimal, clean dedup, pruning | Requires sorting (modifies input) |

### Which solution to choose?

- **In an interview:** Approach 2 (Sort + Two Pointers) — optimal and shows understanding of the kSum generalization
- **In production:** Approach 2 — best time complexity, minimal memory, pruning for practical speedup
- **On Leetcode:** Approach 2 — easily passes all test cases
- **For learning:** Both — Brute Force shows the problem clearly, Two Pointers shows the optimization from 3Sum extended to 4Sum

---

## Edge Cases

| # | Case | Input | Target | Expected Output | Reason |
|---|---|---|---|---|---|
| 1 | LeetCode Example 1 | `[1,0,-1,0,-2,2]` | `0` | `[[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]` | Standard case with duplicates |
| 2 | All same values | `[2,2,2,2,2]` | `8` | `[[2,2,2,2]]` | Deduplication must produce only one quadruplet |
| 3 | All zeros | `[0,0,0,0]` | `0` | `[[0,0,0,0]]` | Only valid quadruplet is four zeros |
| 4 | Negative target | `[-3,-2,-1,0,0,1,2,3]` | `-1` | 9 quadruplets | Many valid combinations |
| 5 | No valid quadruplets | `[1,2,3,4,5]` | `100` | `[]` | Target too large |
| 6 | Less than 4 elements | `[1,2,3]` | `6` | `[]` | Not enough elements |
| 7 | Large values (overflow) | `[10^9,10^9,10^9,10^9]` | `-294967296` | `[]` | Must use int64 to avoid overflow |
| 8 | Empty array | `[]` | `0` | `[]` | No elements at all |

---

## Common Mistakes

### Mistake 1: Integer overflow

```python
# In Java/Go, sum of four large values overflows int32!
# ❌ WRONG (Java)
if (nums[i] + nums[j] + nums[k] + nums[l] == target)  # overflow!

# ✅ CORRECT (Java)
if ((long) nums[i] + nums[j] + nums[k] + nums[l] == target)
```

**Reason:** `10^9 * 4 = 4 * 10^9` exceeds int32 max (2.1 * 10^9). In Go, use `int64()` casts. In Python, this is not an issue since integers have arbitrary precision.

### Mistake 2: Not skipping duplicates at all four positions

```python
# ❌ WRONG — duplicate quadruplets in result
for i in range(n - 3):
    for j in range(i + 1, n - 2):
        # ...no duplicate skipping

# ✅ CORRECT — skip duplicates for i and j
for i in range(n - 3):
    if i > 0 and nums[i] == nums[i - 1]: continue
    for j in range(i + 1, n - 2):
        if j > i + 1 and nums[j] == nums[j - 1]: continue
        # ...also skip duplicates for left/right after finding a match
```

**Reason:** Without skipping at every level, the same quadruplet can be found multiple times.

### Mistake 3: Wrong duplicate skip condition for j

```python
# ❌ WRONG — skips valid combinations
if j > 0 and nums[j] == nums[j - 1]: continue

# ✅ CORRECT — only skip when j is not the first after i
if j > i + 1 and nums[j] == nums[j - 1]: continue
```

**Reason:** `j > 0` would skip `j = i+1` if `nums[i+1] == nums[i]`, which is wrong because `i` and `j` are different positions. The condition must be `j > i + 1`.

### Mistake 4: Missing pruning (not wrong, but slow)

```python
# ❌ SLOWER — no pruning, checks unnecessary iterations
for i in range(n - 3):
    if i > 0 and nums[i] == nums[i - 1]: continue
    for j in range(i + 1, n - 2):
        # ...

# ✅ FASTER — prune based on min/max possible sums
for i in range(n - 3):
    if i > 0 and nums[i] == nums[i - 1]: continue
    if nums[i] + nums[i+1] + nums[i+2] + nums[i+3] > target: break
    if nums[i] + nums[n-3] + nums[n-2] + nums[n-1] < target: continue
    # ...same pruning for j
```

**Reason:** Pruning dramatically reduces the number of iterations in practice. The `break` condition handles "all remaining too large" and the `continue` condition handles "even the largest possible sum is too small".

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [1. Two Sum](https://leetcode.com/problems/two-sum/) | :green_circle: Easy | Foundation — find two numbers summing to target |
| 2 | [15. 3Sum](https://leetcode.com/problems/3sum/) | :yellow_circle: Medium | Same pattern with 3 elements — direct predecessor |
| 3 | [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/) | :yellow_circle: Medium | Closest sum variant with Sort + Two Pointers |
| 4 | [167. Two Sum II - Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | :yellow_circle: Medium | Two Pointers on sorted array (innermost loop) |
| 5 | [454. 4Sum II](https://leetcode.com/problems/4sum-ii/) | :yellow_circle: Medium | Four separate arrays — hash map approach |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — four nested loops check all quadruplets
> - **Two Pointers** tab — sort + fix two elements + two pointers converging
> - Visualizes four pointer movement (i, j, left, right), duplicate skipping, and quadruplet collection
