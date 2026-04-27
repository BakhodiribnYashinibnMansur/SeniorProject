# 0057. Insert Interval

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Append and Re-Merge](#approach-1-append-and-re-merge)
4. [Approach 2: Three-Phase Linear Scan](#approach-2-three-phase-linear-scan)
5. [Approach 3: Binary Search Boundaries](#approach-3-binary-search-boundaries)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [57. Insert Interval](https://leetcode.com/problems/insert-interval/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array` |

### Description

> You are given an array of non-overlapping intervals `intervals` where `intervals[i] = [start_i, end_i]` represent the start and the end of the `i`-th interval and `intervals` is sorted in ascending order by `start_i`. You are also given an interval `newInterval = [start, end]` that represents the start and end of another interval.
>
> Insert `newInterval` into `intervals` such that `intervals` is still sorted in ascending order by `start_i` and `intervals` still does not have any overlapping intervals (merge overlapping intervals if necessary).
>
> Return `intervals` after the insertion.

### Examples

```
Example 1:
Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
Output: [[1,5],[6,9]]

Example 2:
Input: intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
Output: [[1,2],[3,10],[12,16]]
```

### Constraints

- `0 <= intervals.length <= 10^4`
- `intervals[i].length == 2`
- `0 <= start_i <= end_i <= 10^5`
- `intervals` is sorted by `start_i` in ascending order
- `0 <= start <= end <= 10^5`

---

## Problem Breakdown

### 1. What is being asked?

We have a sorted, disjoint set of intervals plus one extra interval. Place the extra interval into the sequence and re-merge any overlap so the output is again sorted and disjoint.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `intervals` | `int[][]` | Sorted, non-overlapping intervals |
| `newInterval` | `int[]` | A single new interval to insert |

Important observations about the input:
- `intervals` is already sorted by start
- `intervals` is already disjoint, so each existing pair has `intervals[i][1] < intervals[i+1][0]`
- The new interval may overlap with zero, one, or many existing ones

### 3. What is the output?

A list of `[start, end]` pairs, sorted by start, non-overlapping, whose union equals `union(intervals) ∪ newInterval`.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^4` | Linear is the natural answer; logarithmic still helps for repeated insertions |
| Already sorted | We can scan in three phases instead of resorting |
| Touching counts as overlap | Use `<=` in the boundary check |

### 5. Step-by-step example analysis

#### Example 2: `intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]`

```text
Phase 1 — Intervals strictly before newInterval:
  [1,2]: end 2 < newStart 4 → emit, copy unchanged

Phase 2 — Intervals overlapping newInterval (start <= newEnd):
  [3,5]: 3 <= 8 and 5 >= 4 → overlap, expand newInterval to [min(4,3), max(8,5)] = [3,8]
  [6,7]: 6 <= 8, expand to [3, 8] (max stays at 8)
  [8,10]: 8 <= 8, expand to [3, max(8,10)] = [3,10]
  [12,16]: 12 > 10 → stop merging

Phase 3 — Emit merged newInterval, then remaining intervals:
  emit [3,10]
  emit [12,16]

Result: [[1,2],[3,10],[12,16]]
```

### 6. Key Observations

1. **Three regions** -- intervals to the left of `newInterval`, intervals that overlap, intervals to the right. Each of these is contiguous because the input is sorted.
2. **Touch is overlap** -- `[1,4]` and `[4,5]` merge into `[1,5]`.
3. **Binary search opportunity** -- The boundaries between the three regions can be found in O(log n).
4. **Re-merging works in general** -- Just append `newInterval` and run the merge from Problem 56. Costs O(n log n) but is correct.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Three-phase scan | Sorted input partitions naturally | This problem (Approach 2) |
| Binary search | Find region boundaries | This problem (Approach 3) |
| Re-merge | Reduces to a solved problem | Problem 56 |

**Chosen pattern:** `Three-Phase Linear Scan`
**Reason:** O(n) time, easy to understand, and the canonical interview answer.

---

## Approach 1: Append and Re-Merge

### Thought process

> Add `newInterval` to the list, then run the standard merge algorithm from [Problem 56](../0056-merge-intervals/solution.md). Easy, but it discards the sorted property of the input.

### Algorithm (step-by-step)

1. Append `newInterval` to `intervals`.
2. Sort by start.
3. Sweep and merge overlapping consecutive intervals.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n log n) | Sort dominates |
| **Space** | O(n) | Output |

### Implementation

#### Python

```python
class Solution:
    def insertReMerge(self, intervals, newInterval):
        items = sorted(intervals + [newInterval], key=lambda x: x[0])
        result = []
        for s, e in items:
            if not result or result[-1][1] < s:
                result.append([s, e])
            else:
                result[-1][1] = max(result[-1][1], e)
        return result
```

#### Go

```go
import "sort"

func insertReMerge(intervals [][]int, newInterval []int) [][]int {
    items := append([][]int{}, intervals...)
    items = append(items, []int{newInterval[0], newInterval[1]})
    sort.Slice(items, func(i, j int) bool { return items[i][0] < items[j][0] })
    result := [][]int{}
    for _, iv := range items {
        if len(result) == 0 || result[len(result)-1][1] < iv[0] {
            result = append(result, []int{iv[0], iv[1]})
        } else if iv[1] > result[len(result)-1][1] {
            result[len(result)-1][1] = iv[1]
        }
    }
    return result
}
```

#### Java

```java
class Solution {
    public int[][] insertReMerge(int[][] intervals, int[] newInterval) {
        int[][] items = new int[intervals.length + 1][2];
        for (int i = 0; i < intervals.length; i++) items[i] = intervals[i].clone();
        items[intervals.length] = newInterval.clone();
        Arrays.sort(items, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> result = new ArrayList<>();
        for (int[] iv : items) {
            if (result.isEmpty() || result.get(result.size() - 1)[1] < iv[0]) {
                result.add(new int[]{iv[0], iv[1]});
            } else {
                int[] last = result.get(result.size() - 1);
                last[1] = Math.max(last[1], iv[1]);
            }
        }
        return result.toArray(new int[0][]);
    }
}
```

---

## Approach 2: Three-Phase Linear Scan

### The problem with Approach 1

> Approach 1 ignores the sorted property and pays for an unnecessary sort. We can do it in O(n).

### Optimization idea

> Walk the input once. Emit unmodified intervals strictly before `newInterval`, absorb every interval that overlaps it (expanding `newInterval`), then emit the absorbed `newInterval` followed by everything strictly after.

### Algorithm (step-by-step)

1. Scan from `i = 0`. While `intervals[i][1] < newInterval[0]`, emit `intervals[i]` and increment `i`.
2. While `i < n` and `intervals[i][0] <= newInterval[1]`:
   - Expand `newInterval = [min(newInterval[0], intervals[i][0]), max(newInterval[1], intervals[i][1])]`
   - Increment `i`.
3. Emit `newInterval`.
4. Emit each remaining `intervals[i]` unchanged.

### Pseudocode

```text
i, n = 0, len(intervals)
result = []
# Phase 1: copy non-overlapping left side
while i < n and intervals[i][1] < newInterval[0]:
    result.append(intervals[i])
    i++
# Phase 2: merge overlapping
while i < n and intervals[i][0] <= newInterval[1]:
    newInterval = [min(newInterval[0], intervals[i][0]),
                   max(newInterval[1], intervals[i][1])]
    i++
result.append(newInterval)
# Phase 3: copy non-overlapping right side
while i < n:
    result.append(intervals[i])
    i++
return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass |
| **Space** | O(n) | Output |

### Implementation

#### Go

```go
func insert(intervals [][]int, newInterval []int) [][]int {
    n := len(intervals)
    result := make([][]int, 0, n+1)
    cur := []int{newInterval[0], newInterval[1]}
    i := 0
    for i < n && intervals[i][1] < cur[0] {
        result = append(result, []int{intervals[i][0], intervals[i][1]})
        i++
    }
    for i < n && intervals[i][0] <= cur[1] {
        if intervals[i][0] < cur[0] {
            cur[0] = intervals[i][0]
        }
        if intervals[i][1] > cur[1] {
            cur[1] = intervals[i][1]
        }
        i++
    }
    result = append(result, cur)
    for i < n {
        result = append(result, []int{intervals[i][0], intervals[i][1]})
        i++
    }
    return result
}
```

#### Java

```java
class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        List<int[]> result = new ArrayList<>(n + 1);
        int[] cur = new int[]{newInterval[0], newInterval[1]};
        int i = 0;
        while (i < n && intervals[i][1] < cur[0]) {
            result.add(intervals[i].clone());
            i++;
        }
        while (i < n && intervals[i][0] <= cur[1]) {
            cur[0] = Math.min(cur[0], intervals[i][0]);
            cur[1] = Math.max(cur[1], intervals[i][1]);
            i++;
        }
        result.add(cur);
        while (i < n) {
            result.add(intervals[i].clone());
            i++;
        }
        return result.toArray(new int[0][]);
    }
}
```

#### Python

```python
class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        n = len(intervals)
        result: List[List[int]] = []
        cur = list(newInterval)
        i = 0
        while i < n and intervals[i][1] < cur[0]:
            result.append(list(intervals[i]))
            i += 1
        while i < n and intervals[i][0] <= cur[1]:
            cur[0] = min(cur[0], intervals[i][0])
            cur[1] = max(cur[1], intervals[i][1])
            i += 1
        result.append(cur)
        while i < n:
            result.append(list(intervals[i]))
            i += 1
        return result
```

### Dry Run

```text
intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]

Phase 1:
  i=0, intervals[0]=[1,2]: 2 < 4 → emit [1,2]; i=1

Phase 2 (cur = [4,8]):
  i=1, intervals[1]=[3,5]: 3 <= 8 → cur=[min(4,3), max(8,5)]=[3,8]; i=2
  i=2, intervals[2]=[6,7]: 6 <= 8 → cur=[3,8]; i=3
  i=3, intervals[3]=[8,10]: 8 <= 8 → cur=[3,max(8,10)]=[3,10]; i=4
  i=4, intervals[4]=[12,16]: 12 > 10 → stop

Emit cur [3,10].

Phase 3:
  i=4, emit [12,16]; i=5

Result: [[1,2],[3,10],[12,16]]
```

---

## Approach 3: Binary Search Boundaries

### Idea

> Use binary search to find:
> 1. The first index `lo` such that `intervals[lo][1] >= newInterval[0]` (first overlap candidate).
> 2. The first index `hi` such that `intervals[hi][0] > newInterval[1]` (first non-overlap to the right).
>
> Everything in `[0, lo)` is unchanged. Everything in `[lo, hi)` overlaps and merges into a single interval. Everything in `[hi, n)` is unchanged. Concatenate the three pieces.

> Useful when intervals must support repeated insertions; otherwise Approach 2 is clearer.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log n) for boundaries, O(n) to copy pieces | Same big-O as linear, but constants smaller for many repeated calls |
| **Space** | O(n) | Output |

### Implementation

#### Python

```python
import bisect

class Solution:
    def insertBinary(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        n = len(intervals)
        if n == 0:
            return [list(newInterval)]
        # Find first index i with intervals[i][1] >= newInterval[0]
        ends = [iv[1] for iv in intervals]
        lo = bisect.bisect_left(ends, newInterval[0])
        # Find first index i with intervals[i][0] > newInterval[1]
        starts = [iv[0] for iv in intervals]
        hi = bisect.bisect_right(starts, newInterval[1])
        merged_start = newInterval[0]
        merged_end = newInterval[1]
        if lo < hi:
            merged_start = min(merged_start, intervals[lo][0])
            merged_end = max(merged_end, intervals[hi - 1][1])
        result = list(intervals[:lo])
        result.append([merged_start, merged_end])
        result.extend(intervals[hi:])
        return result
```

#### Go

```go
import "sort"

func insertBinary(intervals [][]int, newInterval []int) [][]int {
    n := len(intervals)
    if n == 0 {
        return [][]int{{newInterval[0], newInterval[1]}}
    }
    lo := sort.Search(n, func(i int) bool { return intervals[i][1] >= newInterval[0] })
    hi := sort.Search(n, func(i int) bool { return intervals[i][0] > newInterval[1] })
    mergedStart, mergedEnd := newInterval[0], newInterval[1]
    if lo < hi {
        if intervals[lo][0] < mergedStart {
            mergedStart = intervals[lo][0]
        }
        if intervals[hi-1][1] > mergedEnd {
            mergedEnd = intervals[hi-1][1]
        }
    }
    result := make([][]int, 0, n-(hi-lo)+1)
    for k := 0; k < lo; k++ {
        result = append(result, []int{intervals[k][0], intervals[k][1]})
    }
    result = append(result, []int{mergedStart, mergedEnd})
    for k := hi; k < n; k++ {
        result = append(result, []int{intervals[k][0], intervals[k][1]})
    }
    return result
}
```

#### Java

```java
class Solution {
    public int[][] insertBinary(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        if (n == 0) return new int[][]{newInterval.clone()};
        int lo = lowerBoundEnd(intervals, newInterval[0]);
        int hi = upperBoundStart(intervals, newInterval[1]);
        int mergedStart = newInterval[0], mergedEnd = newInterval[1];
        if (lo < hi) {
            mergedStart = Math.min(mergedStart, intervals[lo][0]);
            mergedEnd = Math.max(mergedEnd, intervals[hi - 1][1]);
        }
        List<int[]> result = new ArrayList<>();
        for (int k = 0; k < lo; k++) result.add(intervals[k].clone());
        result.add(new int[]{mergedStart, mergedEnd});
        for (int k = hi; k < n; k++) result.add(intervals[k].clone());
        return result.toArray(new int[0][]);
    }

    private int lowerBoundEnd(int[][] iv, int target) {
        int l = 0, r = iv.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (iv[m][1] < target) l = m + 1;
            else r = m;
        }
        return l;
    }

    private int upperBoundStart(int[][] iv, int target) {
        int l = 0, r = iv.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (iv[m][0] <= target) l = m + 1;
            else r = m;
        }
        return l;
    }
}
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Append + Merge | O(n log n) | O(n) | Reuses Problem 56 | Wastes sorted property |
| 2 | Three-Phase Scan | O(n) | O(n) | Linear, intuitive | Slightly verbose |
| 3 | Binary Search Boundaries | O(n) | O(n) | Constant boundary lookups | More complex code |

### Which solution to choose?

- **In an interview:** Approach 2
- **In production:** Approach 2; Approach 3 if you store intervals long-term and insert often
- **On Leetcode:** All three accepted; Approach 2 is the canonical answer

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Empty input | `[], [5,7]` | `[[5,7]]` | Insert into empty list |
| 2 | Insert at start (no overlap) | `[[3,5]], [1,2]` | `[[1,2],[3,5]]` | Pure left phase |
| 3 | Insert at end (no overlap) | `[[1,2]], [4,5]` | `[[1,2],[4,5]]` | Pure right phase |
| 4 | Insert in middle (no overlap) | `[[1,2],[6,7]], [3,4]` | `[[1,2],[3,4],[6,7]]` | Falls in a gap |
| 5 | Engulfs everything | `[[1,2],[5,6]], [0,10]` | `[[0,10]]` | All existing absorbed |
| 6 | Touching merge left | `[[1,4]], [4,6]` | `[[1,6]]` | Endpoints touch |
| 7 | Touching merge right | `[[4,6]], [1,4]` | `[[1,6]]` | Endpoints touch |
| 8 | Single overlap | `[[1,3],[6,9]], [2,5]` | `[[1,5],[6,9]]` | Standard middle merge |
| 9 | Multiple overlaps | `[[1,2],[3,5],[6,7],[8,10],[12,16]], [4,8]` | `[[1,2],[3,10],[12,16]]` | Spans three intervals |

---

## Common Mistakes

### Mistake 1: Forgetting that touching counts as overlap

```python
# WRONG — uses < and treats [1,4] [4,6] as disjoint
while i < n and intervals[i][0] < cur[1]:
    ...

# CORRECT — uses <=
while i < n and intervals[i][0] <= cur[1]:
    ...
```

**Reason:** The problem treats intervals like `[1,4]` and `[4,5]` as overlapping. Otherwise the merge case is missed.

### Mistake 2: Mutating `newInterval` directly

```python
# RISKY — modifies caller's array
newInterval[0] = min(newInterval[0], intervals[i][0])

# SAFER — work on a copy
cur = list(newInterval)
cur[0] = min(cur[0], intervals[i][0])
```

**Reason:** Mutating arguments leads to subtle bugs. Copy once at the start.

### Mistake 3: Off-by-one in boundary search

```python
# WRONG — uses bisect_left on starts (gives first start >= target instead of first start > target)
hi = bisect.bisect_left(starts, newInterval[1])

# CORRECT — bisect_right gives first start > target
hi = bisect.bisect_right(starts, newInterval[1])
```

**Reason:** When using binary search, we need *strict greater than* `newInterval[1]` for the right boundary because touching counts as overlap.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/) | :yellow_circle: Medium | The merge step underlying this problem |
| 2 | [715. Range Module](https://leetcode.com/problems/range-module/) | :red_circle: Hard | Insert / remove ranges efficiently |
| 3 | [352. Data Stream as Disjoint Intervals](https://leetcode.com/problems/data-stream-as-disjoint-intervals/) | :red_circle: Hard | Insert into a maintained interval set |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Number-line view with existing intervals as gray bars and `newInterval` as blue
> - Three-phase highlighting: left (skipped), middle (merging), right (appended)
> - Live `cur` interval that grows as it absorbs overlaps
> - Tabs for the linear scan vs. binary search boundary view
