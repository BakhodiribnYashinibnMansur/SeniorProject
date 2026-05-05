# Big-Omega Notation — Tasks

## Table of Contents

1. [Task 1: Prove Omega Bounds Formally](#task-1)
2. [Task 2: Count Comparisons in Find-Max](#task-2)
3. [Task 3: Verify Sorting Lower Bound Experimentally](#task-3)
4. [Task 4: Implement Optimal Min-Max Finder](#task-4)
5. [Task 5: Binary Search Comparison Counter](#task-5)
6. [Task 6: Prove Algorithm Optimality](#task-6)
7. [Task 7: Decision Tree Leaf Counter](#task-7)
8. [Task 8: Lower Bound Calculator](#task-8)
9. [Task 9: Merge Operation Lower Bound](#task-9)
10. [Task 10: Compare Sorting Algorithms to Lower Bound](#task-10)
11. [Task 11: Adversary Simulator for Maximum Finding](#task-11)
12. [Task 12: Information-Theoretic Bound Checker](#task-12)
13. [Task 13: Detect When Algorithm Matches Lower Bound](#task-13)
14. [Task 14: Lower Bound for Element Uniqueness](#task-14)
15. [Task 15: Build an Optimality Report Generator](#task-15)
16. [Benchmark Task: Comprehensive Lower Bound Analysis](#benchmark-task)

---

## Task 1: Prove Omega Bounds Formally

Write a program that verifies Big-Omega relationships by testing the formal definition.
Given f(n) and g(n), find constants c and n0 such that f(n) >= c * g(n) for all n >= n0.

Test with these functions:
- f(n) = 3n^2 + 5n, g(n) = n^2
- f(n) = n^2 - 10n, g(n) = n^2
- f(n) = n log n + n, g(n) = n log n
- f(n) = 2^n, g(n) = n^3

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

type FuncPair struct {
    name string
    f    func(float64) float64
    g    func(float64) float64
}

// findOmegaConstants attempts to find c and n0 such that f(n) >= c*g(n) for all n >= n0
func findOmegaConstants(f, g func(float64) float64, maxN int) (float64, int, bool) {
    // Try different values of c, starting from a reasonable guess
    for _, c := range []float64{0.5, 1.0, 2.0, 3.0} {
        foundN0 := -1
        valid := true
        
        for n := 1; n <= maxN; n++ {
            fn := f(float64(n))
            gn := g(float64(n))
            
            if fn >= c*gn {
                if foundN0 == -1 {
                    foundN0 = n
                }
            } else {
                foundN0 = -1
                if n > maxN/2 {
                    valid = false
                    break
                }
            }
        }
        
        if valid && foundN0 != -1 {
            // Verify from foundN0 to maxN
            allGood := true
            for n := foundN0; n <= maxN; n++ {
                if f(float64(n)) < c*g(float64(n)) {
                    allGood = false
                    break
                }
            }
            if allGood {
                return c, foundN0, true
            }
        }
    }
    return 0, 0, false
}

func main() {
    pairs := []FuncPair{
        {"3n^2 + 5n vs n^2",
            func(n float64) float64 { return 3*n*n + 5*n },
            func(n float64) float64 { return n * n }},
        {"n^2 - 10n vs n^2",
            func(n float64) float64 { return n*n - 10*n },
            func(n float64) float64 { return n * n }},
        {"n*log(n) + n vs n*log(n)",
            func(n float64) float64 { return n*math.Log2(n) + n },
            func(n float64) float64 { return n * math.Log2(n) }},
        {"2^n vs n^3",
            func(n float64) float64 { return math.Pow(2, n) },
            func(n float64) float64 { return n * n * n }},
    }
    
    fmt.Println("Big-Omega Formal Verification")
    fmt.Println("=============================")
    
    for _, p := range pairs {
        c, n0, found := findOmegaConstants(p.f, p.g, 1000)
        if found {
            fmt.Printf("%-30s: Omega confirmed with c=%.1f, n0=%d\n", p.name, c, n0)
        } else {
            fmt.Printf("%-30s: Could not confirm Omega\n", p.name)
        }
    }
}
```

**Java:**

```java
import java.util.function.Function;

public class Task1OmegaProver {
    
    static double[] findOmegaConstants(Function<Double, Double> f,
                                        Function<Double, Double> g, int maxN) {
        double[] candidates = {0.5, 1.0, 2.0, 3.0};
        
        for (double c : candidates) {
            int n0 = -1;
            boolean valid = true;
            
            for (int n = 1; n <= maxN; n++) {
                double fn = f.apply((double) n);
                double gn = g.apply((double) n);
                
                if (fn >= c * gn) {
                    if (n0 == -1) n0 = n;
                } else {
                    n0 = -1;
                }
            }
            
            if (n0 != -1) {
                boolean allGood = true;
                for (int n = n0; n <= maxN; n++) {
                    if (f.apply((double) n) < c * g.apply((double) n)) {
                        allGood = false;
                        break;
                    }
                }
                if (allGood) return new double[]{c, n0};
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
        System.out.println("Big-Omega Formal Verification");
        
        // 3n^2 + 5n vs n^2
        double[] result = findOmegaConstants(
            n -> 3 * n * n + 5 * n, n -> n * n, 1000);
        System.out.printf("3n^2+5n vs n^2: c=%.1f, n0=%.0f%n", result[0], result[1]);
        
        // n^2 - 10n vs n^2
        result = findOmegaConstants(
            n -> n * n - 10 * n, n -> n * n, 1000);
        System.out.printf("n^2-10n vs n^2: c=%.1f, n0=%.0f%n", result[0], result[1]);
    }
}
```

**Python:**

```python
import math

def find_omega_constants(f, g, max_n=1000):
    for c in [0.5, 1.0, 2.0, 3.0]:
        n0 = None
        for n in range(1, max_n + 1):
            fn, gn = f(n), g(n)
            if gn > 0 and fn >= c * gn:
                if n0 is None:
                    n0 = n
            else:
                n0 = None
        
        if n0 is not None:
            # Verify
            if all(f(n) >= c * g(n) for n in range(n0, max_n + 1) if g(n) > 0):
                return c, n0
    return None

pairs = [
    ("3n^2 + 5n vs n^2", lambda n: 3*n**2 + 5*n, lambda n: n**2),
    ("n^2 - 10n vs n^2", lambda n: n**2 - 10*n, lambda n: n**2),
    ("n*log(n)+n vs n*log(n)", lambda n: n*math.log2(n)+n, lambda n: n*math.log2(n)),
    ("2^n vs n^3", lambda n: 2**n, lambda n: n**3),
]

print("Big-Omega Formal Verification")
print("=" * 50)
for name, f, g in pairs:
    result = find_omega_constants(f, g)
    if result:
        c, n0 = result
        print(f"{name:<30s}: Omega confirmed, c={c}, n0={n0}")
    else:
        print(f"{name:<30s}: Could not confirm")
```

---

## Task 2: Count Comparisons in Find-Max

Implement a maximum-finding algorithm that counts comparisons and verify it uses
exactly n - 1 comparisons (matching the Omega(n) lower bound).

**Go:**

```go
package main

import (
    "fmt"
    "math/rand"
)

func findMaxCounted(arr []int) (int, int) {
    max := arr[0]
    comps := 0
    for i := 1; i < len(arr); i++ {
        comps++
        if arr[i] > max {
            max = arr[i]
        }
    }
    return max, comps
}

func main() {
    for _, n := range []int{5, 50, 500, 5000} {
        arr := make([]int, n)
        for i := range arr {
            arr[i] = rand.Intn(10000)
        }
        _, comps := findMaxCounted(arr)
        fmt.Printf("n=%4d: comparisons=%4d, expected(n-1)=%4d, match=%v\n",
            n, comps, n-1, comps == n-1)
    }
}
```

**Java:**

```java
import java.util.Random;

public class Task2CountMax {
    public static void main(String[] args) {
        Random rng = new Random();
        for (int n : new int[]{5, 50, 500, 5000}) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rng.nextInt(10000);
            
            int max = arr[0], comps = 0;
            for (int i = 1; i < n; i++) {
                comps++;
                if (arr[i] > max) max = arr[i];
            }
            System.out.printf("n=%4d: comps=%4d, expected=%4d, match=%b%n",
                n, comps, n - 1, comps == n - 1);
        }
    }
}
```

**Python:**

```python
import random

for n in [5, 50, 500, 5000]:
    arr = [random.randint(0, 10000) for _ in range(n)]
    max_val, comps = arr[0], 0
    for i in range(1, n):
        comps += 1
        if arr[i] > max_val:
            max_val = arr[i]
    print(f"n={n:4d}: comps={comps:4d}, expected={n-1:4d}, match={comps == n-1}")
```

---

## Task 3: Verify Sorting Lower Bound Experimentally

Implement merge sort with comparison counting. Compare the actual count to
ceil(log2(n!)) — the theoretical minimum.

**Go:**

```go
package main

import (
    "fmt"
    "math"
    "math/rand"
)

var compCount int

func mergeSort(arr []int) []int {
    if len(arr) <= 1 { return arr }
    mid := len(arr) / 2
    return merge(mergeSort(arr[:mid]), mergeSort(arr[mid:]))
}

func merge(a, b []int) []int {
    result := make([]int, 0, len(a)+len(b))
    i, j := 0, 0
    for i < len(a) && j < len(b) {
        compCount++
        if a[i] <= b[j] { result = append(result, a[i]); i++ } else { result = append(result, b[j]); j++ }
    }
    result = append(result, a[i:]...)
    result = append(result, b[j:]...)
    return result
}

func logFactorial(n int) float64 {
    s := 0.0
    for i := 2; i <= n; i++ { s += math.Log2(float64(i)) }
    return s
}

func main() {
    for _, n := range []int{8, 16, 32, 64, 128, 256} {
        arr := make([]int, n)
        for i := range arr { arr[i] = rand.Intn(n * 10) }
        compCount = 0
        mergeSort(arr)
        lowerBound := math.Ceil(logFactorial(n))
        ratio := float64(compCount) / lowerBound
        fmt.Printf("n=%3d: comparisons=%5d, ceil(log2(n!))=%5.0f, ratio=%.3f\n",
            n, compCount, lowerBound, ratio)
    }
}
```

**Java:**

```java
public class Task3SortBound {
    static int compCount = 0;
    // Implementation similar to Go version — merge sort with counting.
    // Your task: implement and verify ratio stays close to 1.0.
    
    public static void main(String[] args) {
        // Implement merge sort with comparison counting.
        // Compare to ceil(log2(n!)) for n = 8, 16, 32, 64, 128, 256.
        System.out.println("Task: Implement merge sort with comparison counting");
    }
}
```

**Python:**

```python
import math, random

comp_count = 0

def merge_sort(arr):
    global comp_count
    if len(arr) <= 1: return arr
    mid = len(arr) // 2
    left, right = merge_sort(arr[:mid]), merge_sort(arr[mid:])
    result, i, j = [], 0, 0
    while i < len(left) and j < len(right):
        comp_count += 1
        if left[i] <= right[j]: result.append(left[i]); i += 1
        else: result.append(right[j]); j += 1
    result.extend(left[i:]); result.extend(right[j:])
    return result

for n in [8, 16, 32, 64, 128, 256]:
    arr = [random.randint(0, n*10) for _ in range(n)]
    comp_count = 0
    merge_sort(arr)
    lb = math.ceil(sum(math.log2(i) for i in range(2, n+1)))
    print(f"n={n:3d}: comps={comp_count:5d}, ceil(log2(n!))={lb:5d}, ratio={comp_count/lb:.3f}")
```

---

## Task 4: Implement Optimal Min-Max Finder

Find both minimum and maximum in ceil(3n/2) - 2 comparisons (matching the lower bound).

**Go:**

```go
package main

import "fmt"

// findMinMax uses the tournament method: ceil(3n/2) - 2 comparisons.
func findMinMax(arr []int) (int, int, int) {
    n := len(arr)
    if n == 0 { panic("empty") }
    if n == 1 { return arr[0], arr[0], 0 }
    
    comps := 0
    var min, max int
    start := 0
    
    // Initialize from first pair
    comps++
    if arr[0] < arr[1] {
        min, max = arr[0], arr[1]
    } else {
        min, max = arr[1], arr[0]
    }
    start = 2
    
    // Process pairs
    for i := start; i+1 < n; i += 2 {
        comps++ // Compare pair
        var small, large int
        if arr[i] < arr[i+1] {
            small, large = arr[i], arr[i+1]
        } else {
            small, large = arr[i+1], arr[i]
        }
        comps++ // Compare small with min
        if small < min { min = small }
        comps++ // Compare large with max
        if large > max { max = large }
    }
    
    // Handle odd element
    if n%2 == 1 {
        comps++
        if arr[n-1] < min { min = arr[n-1] }
        comps++
        if arr[n-1] > max { max = arr[n-1] }
    }
    
    return min, max, comps
}

func main() {
    for _, n := range []int{5, 10, 100, 1000} {
        arr := make([]int, n)
        for i := range arr { arr[i] = n - i }
        min, max, comps := findMinMax(arr)
        expected := 3*(n/2) - 2 + (n%2)*2 // Approximate
        lowerBound := (3*n + 1) / 2 - 2
        fmt.Printf("n=%4d: min=%4d, max=%4d, comps=%4d, lower_bound~%4d\n",
            n, min, max, comps, lowerBound)
    }
}
```

**Java:**

```java
public class Task4MinMax {
    // Your task: Implement the tournament min-max algorithm
    // that uses ceil(3n/2) - 2 comparisons.
    // Verify the comparison count matches the lower bound.
    
    public static void main(String[] args) {
        System.out.println("Implement tournament min-max with comparison counting");
    }
}
```

**Python:**

```python
def find_min_max(arr):
    n = len(arr)
    comps = 0
    
    if n == 1:
        return arr[0], arr[0], 0
    
    comps += 1
    if arr[0] < arr[1]:
        mn, mx = arr[0], arr[1]
    else:
        mn, mx = arr[1], arr[0]
    
    i = 2
    while i + 1 < n:
        comps += 1
        if arr[i] < arr[i+1]:
            small, large = arr[i], arr[i+1]
        else:
            small, large = arr[i+1], arr[i]
        comps += 1
        if small < mn: mn = small
        comps += 1
        if large > mx: mx = large
        i += 2
    
    if i < n:
        comps += 1
        if arr[i] < mn: mn = arr[i]
        comps += 1
        if arr[i] > mx: mx = arr[i]
    
    return mn, mx, comps

for n in [5, 10, 100, 1000]:
    arr = list(range(n, 0, -1))
    mn, mx, comps = find_min_max(arr)
    print(f"n={n:4d}: min={mn}, max={mx}, comps={comps}")
```

---

## Task 5: Binary Search Comparison Counter

Implement binary search that counts comparisons and compare to log2(n).

*(Similar structure — implement in all three languages with counting.)*

---

## Task 6: Prove Algorithm Optimality

Write a program that takes an algorithm's measured complexity and a problem's lower
bound, then determines if the algorithm is optimal.

**Go:**

```go
package main

import "fmt"

type AlgorithmReport struct {
    Name       string
    UpperBound string
    LowerBound string
    IsOptimal  bool
}

func main() {
    reports := []AlgorithmReport{
        {"Linear scan for max", "O(n)", "Omega(n)", true},
        {"Binary search", "O(log n)", "Omega(log n)", true},
        {"Merge sort", "O(n log n)", "Omega(n log n)", true},
        {"Bubble sort", "O(n^2)", "Omega(n log n)", false},
        {"Selection sort", "O(n^2)", "Omega(n log n)", false},
    }
    
    fmt.Printf("%-25s %-15s %-15s %-10s\n", "Algorithm", "Upper Bound", "Lower Bound", "Optimal?")
    fmt.Println("-------------------------------------------------------------------")
    for _, r := range reports {
        fmt.Printf("%-25s %-15s %-15s %-10v\n",
            r.Name, r.UpperBound, r.LowerBound, r.IsOptimal)
    }
}
```

**Java:**

```java
public class Task6Optimality {
    public static void main(String[] args) {
        String[][] algos = {
            {"Linear scan for max", "O(n)", "Omega(n)", "true"},
            {"Binary search", "O(log n)", "Omega(log n)", "true"},
            {"Merge sort", "O(n log n)", "Omega(n log n)", "true"},
            {"Bubble sort", "O(n^2)", "Omega(n log n)", "false"},
        };
        for (String[] a : algos) {
            System.out.printf("%-25s %-15s %-15s optimal=%s%n", a[0], a[1], a[2], a[3]);
        }
    }
}
```

**Python:**

```python
algos = [
    ("Linear scan for max", "O(n)", "Omega(n)", True),
    ("Binary search", "O(log n)", "Omega(log n)", True),
    ("Merge sort", "O(n log n)", "Omega(n log n)", True),
    ("Bubble sort", "O(n^2)", "Omega(n log n)", False),
]
for name, upper, lower, optimal in algos:
    print(f"{name:<25s} {upper:<15s} {lower:<15s} optimal={optimal}")
```

---

## Task 7: Decision Tree Leaf Counter

Compute the minimum number of leaves in a decision tree for sorting n elements and
the corresponding minimum height.

---

## Task 8: Lower Bound Calculator

Build a calculator that computes theoretical lower bounds for common problems given
input size n: find-max (n-1), sort (ceil(log2(n!))), search (ceil(log2(n))).

---

## Task 9: Merge Operation Lower Bound

Implement merging two sorted arrays with comparison counting. Verify that the
worst case uses m + n - 1 comparisons.

---

## Task 10: Compare Sorting Algorithms to Lower Bound

Benchmark bubble sort, insertion sort, merge sort, and heap sort. Compare each to
the Omega(n log n) lower bound and determine which are optimal.

---

## Task 11: Adversary Simulator for Maximum Finding

Simulate the adversary argument: build a program where the "adversary" answers
comparisons to maximize the number of questions, proving the n - 1 lower bound.

---

## Task 12: Information-Theoretic Bound Checker

Given the number of possible outcomes for a problem, compute the minimum number of
binary queries needed: ceil(log2(outcomes)).

---

## Task 13: Detect When Algorithm Matches Lower Bound

Create a testing framework that runs an algorithm on multiple input sizes, measures
operations, and reports whether the algorithm is within a constant factor of the
known lower bound.

---

## Task 14: Lower Bound for Element Uniqueness

Implement a uniqueness checker and verify it against the Omega(n log n) lower bound
(in the comparison model).

---

## Task 15: Build an Optimality Report Generator

Create a comprehensive tool that:
1. Takes an algorithm function and a lower bound function.
2. Runs the algorithm on various input sizes.
3. Computes the ratio of actual operations to theoretical lower bound.
4. Generates a report stating whether the algorithm is optimal.

---

## Benchmark Task: Comprehensive Lower Bound Analysis

Build a benchmarking suite that measures and compares multiple algorithms against
their theoretical lower bounds. Generate a report like:

```
=== Optimality Report ===

Problem: Finding Maximum
  Algorithm: Linear Scan
  Measured: 999 comparisons for n=1000
  Lower Bound: 999 (n-1)
  Ratio: 1.000
  Verdict: OPTIMAL

Problem: Sorting
  Algorithm: Merge Sort
  Measured: 8711 comparisons for n=1000
  Lower Bound: 8530 (ceil(log2(n!)))
  Ratio: 1.021
  Verdict: NEAR-OPTIMAL (within 3%)

  Algorithm: Bubble Sort
  Measured: 499500 comparisons for n=1000
  Lower Bound: 8530 (ceil(log2(n!)))
  Ratio: 58.56
  Verdict: FAR FROM OPTIMAL

Problem: Sorted Search
  Algorithm: Binary Search
  Measured: 10 comparisons for n=1000
  Lower Bound: 10 (ceil(log2(n)))
  Ratio: 1.000
  Verdict: OPTIMAL
```

Implement in all three languages. The benchmark should test at least 3 problem types
with 2+ algorithms each.
