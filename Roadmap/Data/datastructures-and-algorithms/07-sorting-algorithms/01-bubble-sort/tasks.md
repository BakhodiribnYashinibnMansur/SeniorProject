# Bubble Sort — Practice Tasks

> All tasks must be solved in **Go**, **Java**, and **Python**.
> Each task includes starter code and grading criteria.

---

## Beginner Tasks

### Task 1: Implement Vanilla Bubble Sort

> Write a function that sorts an integer array in ascending order using Bubble Sort. **No optimizations required** — just the basic 2-loop form.

#### Go

```go
package main

import "fmt"

func bubbleSort(arr []int) {
    // TODO: implement vanilla bubble sort
}

func main() {
    data := []int{5, 1, 4, 2, 8}
    bubbleSort(data)
    fmt.Println(data) // expected: [1 2 4 5 8]
}
```

#### Java

```java
public class Task1 {
    public static void bubbleSort(int[] arr) {
        // TODO
    }

    public static void main(String[] args) {
        int[] data = {5, 1, 4, 2, 8};
        bubbleSort(data);
        System.out.println(java.util.Arrays.toString(data));
    }
}
```

#### Python

```python
def bubble_sort(arr):
    # TODO
    pass

if __name__ == "__main__":
    data = [5, 1, 4, 2, 8]
    bubble_sort(data)
    print(data)
```

- **Constraints:** O(1) extra space; in-place mutation; correctness on empty array, single element, duplicates.
- **Expected Output:** `[1, 2, 4, 5, 8]` for the example.
- **Evaluation:** correctness (5 pts), edge cases (3 pts), code clarity (2 pts).

---

### Task 2: Add Early-Exit Optimization

> Extend Task 1 with the `swapped` flag. The function should return the number of passes actually performed.

#### Go

```go
func bubbleSortEarlyExit(arr []int) int {
    // TODO: return number of passes (including the no-swap pass that triggered exit)
}
```

#### Python

```python
def bubble_sort_early_exit(arr):
    # TODO: return number of passes
    return 0
```

- **Test cases:**
  - `[1, 2, 3, 4, 5]` → returns `1` (sorted; one pass detects).
  - `[5, 4, 3, 2, 1]` → returns `4` (n-1 passes for full sort).
  - `[]` → returns `0`.

---

### Task 3: Sort Descending

> Implement Bubble Sort that sorts in **descending** order.

#### Python

```python
def bubble_sort_desc(arr):
    # TODO
    pass
```

- **Expected:** `[5, 1, 4, 2, 8]` → `[8, 5, 4, 2, 1]`
- **Hint:** Change `>` to `<` in the comparison.

---

### Task 4: Sort Strings Lexicographically

> Sort an array of strings in alphabetical order.

#### Go

```go
func bubbleSortStrings(arr []string) {
    // TODO
}
```

#### Python

```python
def bubble_sort_strings(arr):
    # TODO
    pass
```

- **Expected:** `["banana", "apple", "cherry"]` → `["apple", "banana", "cherry"]`

---

### Task 5: Sort Tuples by Second Element

> Sort `[(name, score), ...]` by score ascending. Verify stability: equal scores keep original order.

#### Python

```python
def bubble_sort_by_score(pairs):
    # TODO: stable sort by pairs[i][1]
    pass

print(bubble_sort_by_score([("Alice", 90), ("Bob", 80), ("Carol", 90), ("Dan", 85)]))
# Expected: [("Bob", 80), ("Dan", 85), ("Alice", 90), ("Carol", 90)]
# Note: Alice before Carol because original order is preserved (stability).
```

---

## Intermediate Tasks

### Task 6: Cocktail Shaker Sort

> Implement bidirectional Bubble Sort. Forward pass + backward pass per outer iteration.

#### Go

```go
func cocktailSort(arr []int) {
    // TODO
}
```

- **Test on the "turtle" input:** `[2, 3, 4, 5, 1]` should sort in fewer passes than vanilla Bubble Sort.

---

### Task 7: Comb Sort

> Implement Comb Sort (Bubble Sort with shrinking gap, factor 1.3).

#### Python

```python
def comb_sort(arr):
    # TODO: gap = n; shrink by 1.3 each pass; when gap = 1, becomes bubble sort
    pass
```

- **Benchmark task:** Compare comb_sort, bubble_sort, and Python's built-in `sorted` on n=1000 random ints. Comb should be 5-10× faster than bubble.

---

### Task 8: Sort with Custom Comparator

> Generic Bubble Sort that accepts a comparator function.

#### Go

```go
func bubbleSortBy[T any](arr []T, less func(a, b T) bool) {
    // TODO
}
```

#### Java

```java
public static <T> void bubbleSortBy(T[] arr, java.util.Comparator<T> cmp) {
    // TODO
}
```

#### Python

```python
def bubble_sort_by(arr, key=None, reverse=False):
    # TODO
    pass
```

- **Test:** Sort `["bb", "aaa", "c"]` by length → `["c", "bb", "aaa"]`

---

### Task 9: Track Maximum Number of Swaps for Any Position

> Implement Bubble Sort that, for each index, records how many times an element was swapped while at that index.

```python
def bubble_sort_with_position_swaps(arr):
    n = len(arr)
    swap_count_at_index = [0] * n
    # TODO
    return swap_count_at_index
```

- **Use case:** Identifying "hotspots" in the sort process.

---

### Task 10: Adaptive Bound (Knuth's Algorithm B)

> Implement Knuth's variant: track the position of the last swap; use it as the next pass's bound.

#### Go

```go
func bubbleSortKnuth(arr []int) {
    n := len(arr)
    bound := n
    for {
        t := 0
        for j := 0; j < bound-1; j++ {
            // TODO
        }
        if t == 0 { return }
        bound = t
    }
}
```

- **Compare** with the shrink-by-1 variant on `[1, 2, 3, 4, 5, 0]` — Knuth's variant should perform fewer comparisons.

---

## Advanced Tasks

### Task 11: Parallel Odd-Even Transposition Sort (Goroutines / Threads)

> Implement Odd-Even Transposition Sort using goroutines (Go), threads (Java), or `multiprocessing` (Python). Measure speedup on a large array.

#### Go

```go
package main

import (
    "fmt"
    "sync"
)

func oddEvenSortParallel(arr []int) {
    n := len(arr)
    for phase := 0; phase < n; phase++ {
        var wg sync.WaitGroup
        start := phase % 2
        for i := start; i+1 < n; i += 2 {
            wg.Add(1)
            go func(i int) {
                defer wg.Done()
                // TODO: compare-and-swap arr[i] and arr[i+1]
            }(i)
        }
        wg.Wait()
    }
}

func main() {
    data := []int{5, 1, 4, 2, 8, 3, 7, 6}
    oddEvenSortParallel(data)
    fmt.Println(data)
}
```

- **Note:** Goroutine overhead may make this slower than sequential for small arrays. Batch comparisons (e.g., chunks of 100) for real speedup.

---

### Task 12: Count Inversions Using Bubble Sort

> Use Bubble Sort to count the number of inversions in an array (each swap = 1 inversion).

#### Python

```python
def count_inversions_via_bubble(arr):
    # TODO
    return 0

assert count_inversions_via_bubble([2, 4, 1, 3, 5]) == 3
```

- **Verify** against an O(n log n) Merge Sort implementation for large inputs.

---

### Task 13: Sort a Linked List with Bubble Sort

> Implement Bubble Sort on a singly-linked list. Each "swap" should re-link nodes (not just exchange values — the harder version).

#### Python

```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

def bubble_sort_linked_list(head):
    """Re-link nodes (not just swap values)."""
    # TODO
    return head
```

- **Bonus:** Implement the easier version (swap values only) and compare code complexity.

---

### Task 14: Bubble Sort with Visualization

> Write Bubble Sort that yields each intermediate state as it sorts, suitable for animation.

#### Python

```python
def bubble_sort_steps(arr):
    """Yields tuples (current_array, indices_being_compared, action)."""
    a = list(arr)
    n = len(a)
    for i in range(n - 1):
        swapped = False
        for j in range(n - 1 - i):
            yield (list(a), (j, j + 1), "compare")
            if a[j] > a[j + 1]:
                a[j], a[j + 1] = a[j + 1], a[j]
                swapped = True
                yield (list(a), (j, j + 1), "swap")
        if not swapped:
            return

# Usage
for state, indices, action in bubble_sort_steps([5, 1, 4]):
    print(f"{action} at {indices}: {state}")
```

---

### Task 15: Detect Adversarial "Quadratic Trap" in Production Code

> Write a regression test that fails when a sort function is O(n²) instead of O(n log n).

#### Python

```python
import time
from typing import Callable

def assert_subquadratic(sort_fn: Callable, sizes=(1000, 2000, 4000, 8000), threshold=3.0):
    """Fails if doubling input size more than 3x runtime."""
    times = []
    for n in sizes:
        data = list(range(n, 0, -1))  # reverse-sorted = worst case
        start = time.perf_counter()
        sort_fn(data)
        times.append(time.perf_counter() - start)
    for i in range(1, len(times)):
        ratio = times[i] / times[i - 1]
        assert ratio < threshold, (
            f"Likely O(n²): n={sizes[i-1]}→{sizes[i]}, "
            f"time grew {ratio:.1f}× (threshold: {threshold}×)"
        )

# Should pass: built-in sort is O(n log n)
assert_subquadratic(lambda a: a.sort())

# Should fail: bubble sort is O(n²)
def bubble_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        for j in range(n - 1 - i):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]

# Uncomment to see failure:
# assert_subquadratic(bubble_sort)
```

---

## Benchmark Task

> Compare Bubble Sort, Insertion Sort, Cocktail Sort, Comb Sort, and the language's built-in sort on the same inputs.

#### Go

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

func bubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                swapped = true
            }
        }
        if !swapped { return }
    }
}

func insertionSort(arr []int) {
    for i := 1; i < len(arr); i++ {
        x := arr[i]
        j := i - 1
        for j >= 0 && arr[j] > x {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = x
    }
}

func benchmark(name string, fn func([]int), data []int) {
    cp := append([]int(nil), data...)
    start := time.Now()
    fn(cp)
    fmt.Printf("%-15s: %v\n", name, time.Since(start))
}

func main() {
    sizes := []int{100, 1000, 5000, 10000}
    for _, n := range sizes {
        data := make([]int, n)
        for i := range data { data[i] = rand.Intn(n) }
        fmt.Printf("\n=== n=%d ===\n", n)
        benchmark("bubble", bubbleSort, data)
        benchmark("insertion", insertionSort, data)
        benchmark("builtin (sort.Ints)", func(a []int) { sort.Ints(a) }, data)
    }
}
```

#### Java

```java
import java.util.*;

public class Benchmark {
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int t = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = t;
                    swapped = true;
                }
            }
            if (!swapped) return;
        }
    }

    static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int x = arr[i], j = i - 1;
            while (j >= 0 && arr[j] > x) { arr[j + 1] = arr[j]; j--; }
            arr[j + 1] = x;
        }
    }

    static void run(String name, Runnable r) {
        long start = System.nanoTime();
        r.run();
        long elapsed = System.nanoTime() - start;
        System.out.printf("%-20s: %.3f ms%n", name, elapsed / 1_000_000.0);
    }

    public static void main(String[] args) {
        Random rnd = new Random(42);
        int[] sizes = {100, 1000, 5000, 10000};
        for (int n : sizes) {
            int[] data = new int[n];
            for (int i = 0; i < n; i++) data[i] = rnd.nextInt(n);
            System.out.printf("%n=== n=%d ===%n", n);
            run("bubble", () -> bubbleSort(data.clone()));
            run("insertion", () -> insertionSort(data.clone()));
            run("Arrays.sort", () -> Arrays.sort(data.clone()));
        }
    }
}
```

#### Python

```python
import random
import timeit

def bubble_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        swapped = False
        for j in range(n - 1 - i):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped: return

def insertion_sort(arr):
    for i in range(1, len(arr)):
        x = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > x:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = x

def bench(name, fn, data):
    t = timeit.timeit(lambda: fn(list(data)), number=1)
    print(f"{name:<20}: {t*1000:.3f} ms")

if __name__ == "__main__":
    for n in (100, 1000, 5000, 10000):
        random.seed(42)
        data = [random.randint(0, n) for _ in range(n)]
        print(f"\n=== n={n} ===")
        bench("bubble", bubble_sort, data)
        bench("insertion", insertion_sort, data)
        bench("sorted (builtin)", sorted, data)
```

### Expected Results

| n | Bubble | Insertion | Built-in |
|---|--------|-----------|----------|
| 100 | 50 µs | 25 µs | 8 µs |
| 1,000 | 1.5 ms | 0.6 ms | 0.1 ms |
| 5,000 | 35 ms | 15 ms | 0.6 ms |
| 10,000 | 145 ms | 55 ms | 1.3 ms |

Built-in is **100×** faster than Bubble Sort for n=10,000. Insertion Sort is 2.5× faster than Bubble Sort across the board.

---

## Self-Assessment Rubric

| Skill | Beginner | Intermediate | Advanced |
|-------|---------|-------------|----------|
| Implement vanilla Bubble Sort | Required | — | — |
| Add early-exit optimization | Required | — | — |
| Implement Cocktail / Comb / Knuth variants | — | Required | — |
| Use generics / comparator | — | Required | — |
| Parallelize with Odd-Even Transposition | — | — | Required |
| Detect quadratic regression in tests | — | — | Required |
| Explain why production never uses Bubble Sort | Required | Required | Required |

---

## Stretch Challenges

1. **Sort-in-progress predicate:** Given a snapshot of an array mid-sort, determine which sorting algorithm is producing it (bubble vs. insertion vs. selection).
2. **Bubble sort on 2D grid:** Sort rows by first column, then sort within each row.
3. **Stable bubble sort proof:** Write a property-based test that asserts stability over 10,000 random inputs.
4. **Bubble sort hardware simulation:** Implement an n-comparator pipeline that performs odd-even transposition in `n` clock cycles.
5. **Bidirectional + adaptive:** Combine Cocktail Sort with Knuth's last-swap-position trick. Measure improvement.
