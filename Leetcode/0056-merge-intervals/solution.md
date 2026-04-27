# 0056. Merge Intervals

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Pairwise Merge)](#approach-1-brute-force-pairwise-merge)
4. [Approach 2: Sort and Merge](#approach-2-sort-and-merge)
5. [Approach 3: Sweep Line / Boundary Counting](#approach-3-sweep-line--boundary-counting)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Sorting` |

### Description

> Given an array of `intervals` where `intervals[i] = [start_i, end_i]`, merge all overlapping intervals, and return *an array of the non-overlapping intervals that cover all the intervals in the input*.

### Examples

```
Example 1:
Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]
Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].

Example 2:
Input: intervals = [[1,4],[4,5]]
Output: [[1,5]]
Explanation: Intervals [1,4] and [4,5] are considered overlapping.
```

### Constraints

- `1 <= intervals.length <= 10^4`
- `intervals[i].length == 2`
- `0 <= start_i <= end_i <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Combine intervals that overlap (or touch) into single intervals. The result is the smallest set of non-overlapping intervals whose union equals the original union.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `intervals` | `int[][]` | Array of `[start, end]` pairs |

Important observations about the input:
- `start_i <= end_i` always
- Two intervals `[a, b]` and `[c, d]` overlap iff `a <= d && c <= b`
- They "touch" when `b == c` and the problem treats touching as overlap

### 3. What is the output?

A list of `[start, end]` pairs sorted by `start`, where no two pairs overlap.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^4` | O(n^2) ≈ 10^8 ops -- slow but possibly OK; O(n log n) is the natural answer |
| Values in `[0, 10^4]` | Counting / sweep line is also feasible |
| Intervals can touch | Treat `b == c` as merge case, not as gap |

### 5. Step-by-step example analysis

#### Example 1: `[[1,3],[2,6],[8,10],[15,18]]`

```text
Sort by start: [[1,3],[2,6],[8,10],[15,18]]   (already sorted)

Initialize merged = [[1,3]].

[2,6]: 2 <= 3 (last merged end) → overlap, extend last to [1, max(3,6)] = [1,6]
[8,10]: 8 > 6 → gap, append → [[1,6],[8,10]]
[15,18]: 15 > 10 → gap, append → [[1,6],[8,10],[15,18]]

Result: [[1,6],[8,10],[15,18]]
```

#### Example 2: `[[1,4],[4,5]]`

```text
Sort: same.
merged = [[1,4]].
[4,5]: 4 <= 4 → overlap (touching), extend → [1, max(4,5)] = [1,5].

Result: [[1,5]]
```

### 6. Key Observations

1. **Sorting reveals structure** -- After sorting by `start`, an interval can only overlap with the *previous* merged interval. We never need to look further back.
2. **`b == c` is overlap** -- The problem treats touching endpoints as overlap. Use `<=` not `<`.
3. **Merging is associative** -- We can sweep left to right; whenever we find an overlap with the last result, extend; otherwise append.
4. **Sweep line works for distinct counts** -- Mark `+1` at each start and `-1` at each end+1 to detect zero-crossings.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Sort + Sweep | Sorting linearizes interval relationships | This problem (Approach 2) |
| Sweep Line | Count opens/closes to detect coverage | Sweep approach |
| Disjoint Set Union | Could group overlapping intervals | Possible but overkill |

**Chosen pattern:** `Sort + Sweep`
**Reason:** Simple, optimal, and easy to argue.

---

## Approach 1: Brute Force (Pairwise Merge)

### Thought process

> Repeatedly find any two overlapping intervals and merge them, until no overlaps remain. Each scan is O(n^2); the outer loop may repeat up to O(n) times.

### Algorithm (step-by-step)

1. Repeat:
   - For every pair `(i, j)` with `i < j`, check overlap.
   - If overlapping, merge them, mark `j` for removal, restart.
2. When no merge happens, return the remaining intervals.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^3) | Up to n iterations of an O(n^2) pair scan |
| **Space** | O(n) | Output structure |

### Implementation

#### Python

```python
class Solution:
    def mergeBrute(self, intervals: List[List[int]]) -> List[List[int]]:
        items = [list(x) for x in intervals]
        changed = True
        while changed:
            changed = False
            i = 0
            while i < len(items):
                j = i + 1
                while j < len(items):
                    a, b = items[i]
                    c, d = items[j]
                    if a <= d and c <= b:
                        items[i] = [min(a, c), max(b, d)]
                        items.pop(j)
                        changed = True
                    else:
                        j += 1
                i += 1
        items.sort()
        return items
```

#### Go

```go
func mergeBrute(intervals [][]int) [][]int {
    items := make([][]int, len(intervals))
    for i, iv := range intervals {
        items[i] = []int{iv[0], iv[1]}
    }
    changed := true
    for changed {
        changed = false
        for i := 0; i < len(items); i++ {
            for j := i + 1; j < len(items); {
                a, b := items[i][0], items[i][1]
                c, d := items[j][0], items[j][1]
                if a <= d && c <= b {
                    if c < a {
                        items[i][0] = c
                    }
                    if d > b {
                        items[i][1] = d
                    }
                    items = append(items[:j], items[j+1:]...)
                    changed = true
                } else {
                    j++
                }
            }
        }
    }
    sortIntervals(items)
    return items
}

func sortIntervals(a [][]int) {
    for i := 1; i < len(a); i++ {
        for j := i; j > 0 && a[j-1][0] > a[j][0]; j-- {
            a[j-1], a[j] = a[j], a[j-1]
        }
    }
}
```

#### Java

```java
class Solution {
    public int[][] mergeBrute(int[][] intervals) {
        List<int[]> items = new ArrayList<>();
        for (int[] iv : intervals) items.add(new int[]{iv[0], iv[1]});
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); ) {
                    int a = items.get(i)[0], b = items.get(i)[1];
                    int c = items.get(j)[0], d = items.get(j)[1];
                    if (a <= d && c <= b) {
                        items.get(i)[0] = Math.min(a, c);
                        items.get(i)[1] = Math.max(b, d);
                        items.remove(j);
                        changed = true;
                    } else j++;
                }
            }
        }
        items.sort((x, y) -> Integer.compare(x[0], y[0]));
        return items.toArray(new int[0][]);
    }
}
```

---

## Approach 2: Sort and Merge

### The problem with Approach 1

> Pairwise merging requires repeated full scans. Sorting once eliminates the need to look further back than the previous interval.

### Optimization idea

> Sort intervals by start. Then sweep and either extend the last interval in the result or append a new one.

### Algorithm (step-by-step)

1. Sort `intervals` by `start` ascending.
2. Initialize `result = []`.
3. For each `[s, e]`:
   - If `result` is empty or `result.last.end < s`: append `[s, e]`.
   - Else: extend the last interval to `[result.last.start, max(result.last.end, e)]`.
4. Return `result`.

### Pseudocode

```text
sort intervals by start
result = []
for [s, e] in intervals:
    if result.empty() or result[-1][1] < s:
        result.append([s, e])
    else:
        result[-1][1] = max(result[-1][1], e)
return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n log n) | Sort dominates |
| **Space** | O(n) | Output (or O(log n) extra for in-place sort) |

### Implementation

#### Go

```go
import "sort"

func merge(intervals [][]int) [][]int {
    sort.Slice(intervals, func(i, j int) bool {
        return intervals[i][0] < intervals[j][0]
    })
    result := make([][]int, 0, len(intervals))
    for _, iv := range intervals {
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
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> result = new ArrayList<>();
        for (int[] iv : intervals) {
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

#### Python

```python
class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals.sort(key=lambda x: x[0])
        result: List[List[int]] = []
        for s, e in intervals:
            if not result or result[-1][1] < s:
                result.append([s, e])
            else:
                result[-1][1] = max(result[-1][1], e)
        return result
```

### Dry Run

```text
Input: [[1,3],[2,6],[8,10],[15,18]]
After sort: same.

result = []
[1,3]: empty → append → [[1,3]]
[2,6]: 2 <= 3 → extend last to [1, max(3,6)=6] → [[1,6]]
[8,10]: 8 > 6 → append → [[1,6],[8,10]]
[15,18]: 15 > 10 → append → [[1,6],[8,10],[15,18]]
```

---

## Approach 3: Sweep Line / Boundary Counting

### Idea

> Treat each interval as `+1` at `start` and `-1` at `end + 1`. Sort all boundary events. Sweep with a running counter: when it transitions from 0 to 1, a merged interval starts; when it transitions from 1 to 0, that interval ends.

> This generalizes nicely to other problems (concert overlap, meeting rooms, max overlap).

### Algorithm (step-by-step)

1. Build events: `(start, +1)` and `(end, -1)`. Note: use `end + 1` if you want strict gaps; here we use `end` because touching counts as overlap.
2. Sort events by position; on tie, `+1` events come before `-1` events (so touching merges).
3. Sweep, tracking a counter. When it goes 0 -> 1, record `start`. When it goes back to 0, record `end`.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n log n) | Sorting events |
| **Space** | O(n) | Event list |

### Implementation

#### Python

```python
class Solution:
    def mergeSweep(self, intervals: List[List[int]]) -> List[List[int]]:
        events = []
        for s, e in intervals:
            events.append((s, 0, +1))   # +1 events have rank 0 → come first on ties
            events.append((e, 1, -1))   # -1 events have rank 1
        events.sort()
        result, cur, start = [], 0, 0
        for pos, _, delta in events:
            if cur == 0 and delta == +1:
                start = pos
            cur += delta
            if cur == 0:
                result.append([start, pos])
        return result
```

#### Go

```go
import "sort"

func mergeSweep(intervals [][]int) [][]int {
    type ev struct{ pos, rank, delta int }
    events := make([]ev, 0, 2*len(intervals))
    for _, iv := range intervals {
        events = append(events, ev{iv[0], 0, +1})
        events = append(events, ev{iv[1], 1, -1})
    }
    sort.Slice(events, func(i, j int) bool {
        if events[i].pos != events[j].pos {
            return events[i].pos < events[j].pos
        }
        return events[i].rank < events[j].rank
    })
    result := [][]int{}
    cur, start := 0, 0
    for _, e := range events {
        if cur == 0 && e.delta == +1 {
            start = e.pos
        }
        cur += e.delta
        if cur == 0 {
            result = append(result, []int{start, e.pos})
        }
    }
    return result
}
```

#### Java

```java
class Solution {
    public int[][] mergeSweep(int[][] intervals) {
        int n = intervals.length;
        int[][] events = new int[2 * n][3]; // pos, rank, delta
        for (int i = 0; i < n; i++) {
            events[2 * i]     = new int[]{intervals[i][0], 0, +1};
            events[2 * i + 1] = new int[]{intervals[i][1], 1, -1};
        }
        Arrays.sort(events, (a, b) ->
            a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        List<int[]> result = new ArrayList<>();
        int cur = 0, start = 0;
        for (int[] e : events) {
            if (cur == 0 && e[2] == +1) start = e[0];
            cur += e[2];
            if (cur == 0) result.add(new int[]{start, e[0]});
        }
        return result.toArray(new int[0][]);
    }
}
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Pairwise Merge | O(n^3) | O(n) | Simple to think about | Slow, fragile |
| 2 | Sort + Merge | O(n log n) | O(n) | Clean, optimal | Sort dominates |
| 3 | Sweep Line | O(n log n) | O(n) | Generalizes to other problems | More moving parts |

### Which solution to choose?

- **In an interview:** Approach 2 -- canonical
- **In production:** Approach 2
- **On Leetcode:** Approach 2
- **For learning:** Approach 3 introduces sweep line, useful for harder interval problems

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single interval | `[[1,4]]` | `[[1,4]]` | Nothing to merge |
| 2 | Already disjoint | `[[1,2],[3,4]]` | `[[1,2],[3,4]]` | No overlap |
| 3 | Touching endpoints | `[[1,4],[4,5]]` | `[[1,5]]` | Treated as overlap |
| 4 | One contained in other | `[[1,10],[2,3]]` | `[[1,10]]` | Inner interval absorbed |
| 5 | All identical | `[[1,1],[1,1]]` | `[[1,1]]` | Merge to a single point |
| 6 | Reverse-sorted input | `[[4,5],[1,3]]` | `[[1,3],[4,5]]` | Sort first |
| 7 | Multiple chains | `[[1,3],[2,4],[6,8],[7,9]]` | `[[1,4],[6,9]]` | Two separate merges |
| 8 | Zero-length intervals | `[[1,1],[2,2]]` | `[[1,1],[2,2]]` | Touching not overlapping unless equal |

---

## Common Mistakes

### Mistake 1: Forgetting to sort

```python
# WRONG — assumes input is sorted by start
result = []
for s, e in intervals:
    ...

# CORRECT
intervals.sort(key=lambda x: x[0])
result = []
for s, e in intervals:
    ...
```

**Reason:** Without sorting, an interval may overlap with one that has already been merged but is no longer the "last" entry.

### Mistake 2: Strict `<` instead of `<=`

```python
# WRONG — treats touching endpoints as a gap
if not result or result[-1][1] < s:    # uses <
    result.append([s, e])

# CORRECT in this problem touching counts as overlap; use < for the no-overlap case is fine,
# but the merge case must include equality:
if not result or result[-1][1] < s:
    result.append([s, e])
else:
    result[-1][1] = max(result[-1][1], e)
```

**Reason:** Use `<` only for the "append a new" branch and `else` for merge. The `else` branch correctly fires when `result[-1][1] >= s`.

### Mistake 3: Mutating the input

```python
# AVOID — caller may not expect the list to be sorted
intervals.sort(key=lambda x: x[0])

# OPTIONAL — copy first if mutation is undesirable
intervals = sorted(intervals, key=lambda x: x[0])
```

**Reason:** Functions that mutate inputs are surprising. On Leetcode it usually doesn't matter, but in production it does.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [57. Insert Interval](https://leetcode.com/problems/insert-interval/) | :yellow_circle: Medium | Insert one new interval into a sorted list |
| 2 | [252. Meeting Rooms](https://leetcode.com/problems/meeting-rooms/) | :green_circle: Easy | Detect overlap, no merge |
| 3 | [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) | :yellow_circle: Medium | Maximum overlap = rooms needed |
| 4 | [435. Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/) | :yellow_circle: Medium | Greedy interval scheduling |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Number-line view of every interval as a colored bar
> - Sort step animation: bars rearrange by start position
> - Sweep step: pointer crosses each interval, merges or appends
> - Live "merged" output strip
> - Tabs for sort-merge vs. sweep line views
