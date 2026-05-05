# Selection Sort — Interview Preparation

## Junior Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | What is Selection Sort? | Find min, swap to front, repeat for each position. |
| 2 | Time complexity? | O(n²) in all cases (best, avg, worst). |
| 3 | Space? | O(1) — in-place. |
| 4 | Stable? | No (typical impl). |
| 5 | How many swaps? | At most n-1 — minimum of any standard sort. |
| 6 | When is Selection Sort better than Insertion Sort? | When writes are very expensive (flash memory, EEPROM). |
| 7 | Why is it called "Selection"? | We "select" the minimum and place it. |
| 8 | Adaptive? | No — always O(n²) regardless of input. |
| 9 | Implement on whiteboard. | See `junior.md`. ~7 lines. |

## Middle Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Why does Selection Sort have so few swaps? | Each pass places one element in final position with at most one swap. n passes = n swaps. |
| 2 | How to make Selection Sort stable? | Use shifts instead of swap → O(n²) writes (defeats few-writes benefit). Or sort indexed pairs. |
| 3 | What's bidirectional Selection Sort? | Find min AND max in same pass; place at both ends. ~2× faster. |
| 4 | Selection vs Insertion — when to choose Selection? | When swap cost >> compare cost (flash memory). |
| 5 | Selection vs Bubble — why is Selection usually faster? | Selection has fewer swaps; Bubble does many swaps even on slightly-out-of-order. |
| 6 | What is Heap Sort relative to Selection Sort? | Heap Sort = Selection Sort with a heap that finds min in O(log n) → O(n log n). |
| 7 | What is Cycle Sort? | Sort that achieves the *minimum possible* number of writes — useful for write-expensive media. |
| 8 | Can Selection Sort be parallelized? | Hard — each pass depends on previous. Min-find within pass can be parallelized. |

## Senior Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | When does Selection Sort win in production? | Embedded systems with EEPROM (limited write cycles); flash wear leveling; distributed writes. |
| 2 | Compare write costs across storage. | RAM ~1ns; SSD ~100µs; EEPROM ~10ms. Write minimization grows in importance. |
| 3 | How to verify Selection Sort actually saves writes? | Wrap array in a write-counting proxy; profile across realistic inputs. |
| 4 | Why doesn't anyone use Selection Sort for general data? | Insertion Sort is faster; Quick/Merge are faster still for n > 50. |
| 5 | What's the relationship between Selection Sort and selection algorithms (k-th smallest)? | Find one min = O(n); find n mins = O(n²). For O(n log n), use heap-based selection. |

## Professional Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Prove Selection Sort correctness via loop invariant. | At iteration i, A[0..i-1] = i smallest in sorted order. Induction. |
| 2 | What's the lower bound on swaps for sorting? | ⌈n/2⌉ — each swap fixes at most 2 elements. Selection Sort within factor 2 of this. |
| 3 | Why is Selection Sort not adaptive? | Always scans entire unsorted portion to find min. No early-exit possible. |
| 4 | Cache complexity of Selection Sort? | O(n²/B) — sequential reads, cache-friendly within pass. |

---

## Coding Challenge

### Challenge 1: Standard Selection Sort
```python
def selection_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]: min_idx = j
        if min_idx != i:
            arr[i], arr[min_idx] = arr[min_idx], arr[i]
```

### Challenge 2: Bidirectional Selection Sort
```python
def bidirectional_selection_sort(arr):
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        min_idx, max_idx = lo, lo
        for j in range(lo, hi + 1):
            if arr[j] < arr[min_idx]: min_idx = j
            if arr[j] > arr[max_idx]: max_idx = j
        arr[lo], arr[min_idx] = arr[min_idx], arr[lo]
        if max_idx == lo: max_idx = min_idx
        arr[hi], arr[max_idx] = arr[max_idx], arr[hi]
        lo += 1; hi -= 1
```

### Challenge 3: Stable Selection Sort
```python
def stable_selection_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]: min_idx = j
        x = arr[min_idx]
        while min_idx > i:
            arr[min_idx] = arr[min_idx - 1]
            min_idx -= 1
        arr[i] = x
```

### Challenge 4: Recursive Selection Sort
```python
def selection_sort_recursive(arr, start=0):
    if start >= len(arr) - 1: return
    min_idx = start
    for j in range(start + 1, len(arr)):
        if arr[j] < arr[min_idx]: min_idx = j
    arr[start], arr[min_idx] = arr[min_idx], arr[start]
    selection_sort_recursive(arr, start + 1)
```

---

## Pitfalls

| Pitfall | Fix |
|---------|-----|
| Inner starts at `i` | Wasteful self-comparison; start at i+1 |
| Forgetting `if min_idx != i:` | Useless self-swap |
| Saying "stable" | False in standard form |
| Using on n > 1000 | Slow — use Insertion or Quick |

## One-Liner

> **Selection Sort:** Find min, swap to front, repeat. **O(n²) always**, **n-1 swaps** (minimum). NOT stable, NOT adaptive. Use only when writes are expensive (flash, EEPROM). **Heap Sort** is the O(n log n) generalization.
