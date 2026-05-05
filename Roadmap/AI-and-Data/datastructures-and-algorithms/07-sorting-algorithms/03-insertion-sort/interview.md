# Insertion Sort — Interview Preparation

## Junior Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | What is Insertion Sort? | Build sorted prefix one element at a time by inserting into correct position. |
| 2 | Time complexity? | Best O(n), Avg O(n²), Worst O(n²). |
| 3 | Stable? | Yes — strict `>` keeps equals in order. |
| 4 | In-place? | Yes — O(1) extra space. |
| 5 | Why is it called "online"? | Can sort streaming data — insert one element at a time into a sorted list. |
| 6 | Difference from Bubble Sort? | Insertion uses shift (1 write) vs Bubble's swap (3 writes); inner loop exits early. |
| 7 | Best-case input? | Already sorted — inner loop never runs, O(n). |
| 8 | Worst-case input? | Reverse sorted — every element shifts to the front. |
| 9 | When to use it? | Small arrays (n ≤ 50), nearly-sorted data, online insertion. |
| 10 | Implement on whiteboard. | 5 lines. |

## Middle Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Why is Insertion Sort adaptive? | Time = Θ(n + I) where I = inversions; sorted = O(n). |
| 2 | Compare Insertion vs Bubble vs Selection. | Insertion best for general use; Selection for fewest writes; Bubble for teaching only. |
| 3 | What is binary insertion sort? | Use binary search to find insertion position; reduces comparisons to O(n log n) but shifts still O(n²). |
| 4 | Why do hybrid sorts (TimSort, Pdqsort) use Insertion Sort? | Faster than O(n log n) for n ≤ 32 due to recursion overhead, cache locality. |
| 5 | What is Shell Sort? | Insertion Sort with shrinking gap; O(n^1.3) average. |
| 6 | How do you make Insertion Sort online? | Append to end of sorted list, shift back into place. O(n) per insert. |
| 7 | Why use shift instead of swap? | 3× fewer writes; faster in practice. |
| 8 | Number of inversions = number of shifts? | Yes — each shift removes one inversion. |
| 9 | Stable variant of Selection Sort exists, but typically Selection is unstable; what about Insertion? | Insertion is naturally stable with strict `>`. |
| 10 | When would Insertion Sort beat Merge Sort? | Small n (≤ 32), nearly-sorted data, very low memory budget. |

## Senior Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | How does TimSort use Insertion Sort? | Extends short runs to `minrun` (32-64) using Insertion; merges runs afterward. |
| 2 | Tune the cutoff in your hybrid sort. | 16-32 typical; profile per data type and access pattern. |
| 3 | Compare online insertion options for a stream. | Insertion (O(n)/insert), heap (O(log n)/insert for top-k), skip list (O(log n)). |
| 4 | When is Insertion Sort the right choice for production? | Embedded systems with O(1) memory budget, tiny n, or as hybrid fallback. |
| 5 | How to safely sort shared mutable state with Insertion Sort? | Snapshot-then-sort or RWLock; avoid concurrent modification. |
| 6 | Cache complexity? | Θ(n²/B) — sequential, cache-friendly within working set. |
| 7 | Identify Insertion Sort in production sort code. | Look for "INSERTION_THRESHOLD" or "minrun" constants. |
| 8 | Why doesn't Insertion Sort scale to billions of elements? | O(n²) — even 10⁶ elements take seconds; 10⁹ takes years. |

## Professional Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Prove Insertion Sort is correct via loop invariants. | Outer: A[0..i-1] sorted at start of iter i. Induction. |
| 2 | Prove the adaptive bound Θ(n + I). | Each inner iteration removes one inversion; total shifts = I. Outer: n. |
| 3 | Average inversions in random permutation? | E[I] = n(n-1)/4. |
| 4 | Why is Insertion Sort optimal among adjacent-swap sorts? | Each adjacent swap removes ≤ 1 inversion; lower bound matches Insertion's count. |
| 5 | I/O complexity of Insertion Sort? | Θ(n²/B) — same as Bubble; impractical for external sort. |

---

## Coding Challenge

### Challenge 1: Implement Insertion Sort

#### Python
```python
def insertion_sort(arr):
    for i in range(1, len(arr)):
        x = arr[i]; j = i - 1
        while j >= 0 and arr[j] > x:
            arr[j+1] = arr[j]; j -= 1
        arr[j+1] = x
```

#### Go
```go
func InsertionSort(a []int) {
    for i := 1; i < len(a); i++ {
        x, j := a[i], i-1
        for j >= 0 && a[j] > x {
            a[j+1] = a[j]; j--
        }
        a[j+1] = x
    }
}
```

#### Java
```java
public static void insertionSort(int[] a) {
    for (int i = 1; i < a.length; i++) {
        int x = a[i], j = i - 1;
        while (j >= 0 && a[j] > x) { a[j+1] = a[j]; j--; }
        a[j+1] = x;
    }
}
```

### Challenge 2: Online Insertion (Sort a Stream)

```python
def online_insert(sorted_arr, x):
    sorted_arr.append(0)
    i = len(sorted_arr) - 2
    while i >= 0 and sorted_arr[i] > x:
        sorted_arr[i+1] = sorted_arr[i]; i -= 1
    sorted_arr[i+1] = x

# Usage
sorted_arr = []
for x in [5, 2, 4, 6, 1, 3]:
    online_insert(sorted_arr, x)
print(sorted_arr)  # [1, 2, 3, 4, 5, 6]
```

### Challenge 3: Binary Insertion Sort

```python
import bisect
def binary_insertion_sort(arr):
    for i in range(1, len(arr)):
        x = arr[i]
        pos = bisect.bisect_left(arr, x, 0, i)
        arr[pos+1:i+1] = arr[pos:i]
        arr[pos] = x
```

### Challenge 4: Shell Sort

```python
def shell_sort(arr):
    n = len(arr); gap = n // 2
    while gap > 0:
        for i in range(gap, n):
            x = arr[i]; j = i
            while j >= gap and arr[j-gap] > x:
                arr[j] = arr[j-gap]; j -= gap
            arr[j] = x
        gap //= 2
```

---

## Pitfalls

| Pitfall | Fix |
|---------|-----|
| `>=` instead of `>` | Loses stability |
| Forgetting `j >= 0` | IndexError when x is new minimum |
| Using swap | 3× slower than shift |
| Storing arr[i] AFTER shift | Original lost — store BEFORE |

## One-Liner

> **Insertion Sort:** Build sorted prefix by inserting each element via shift. **O(n) best, O(n²) worst**, **stable**, **adaptive**, **online**. Standard small-array fallback in TimSort, Pdqsort, Java Dual-Pivot Quicksort.
