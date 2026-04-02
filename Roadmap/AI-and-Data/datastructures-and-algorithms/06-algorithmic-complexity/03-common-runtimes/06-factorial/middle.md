# Factorial Time O(n!) -- Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Permutation Generation Algorithms](#permutation-generation-algorithms)
3. [Heap's Algorithm](#heaps-algorithm)
4. [Next Permutation Algorithm](#next-permutation-algorithm)
5. [Pruning to Reduce from O(n!)](#pruning-to-reduce-from-on)
6. [Branch and Bound for TSP](#branch-and-bound-for-tsp)
7. [Comparing O(n!) vs O(2^n) vs O(n^n)](#comparing-on-vs-o2n-vs-onn)
8. [Stirling's Approximation](#stirlings-approximation)
9. [Permutations with Duplicates](#permutations-with-duplicates)
10. [Counting Permutations Without Generating Them](#counting-permutations-without-generating-them)
11. [Partial Permutations P(n,k)](#partial-permutations-pnk)
12. [Practical Limits and Benchmarks](#practical-limits-and-benchmarks)
13. [Key Takeaways](#key-takeaways)

---

## Introduction

At the junior level, we learned that O(n!) is astronomically expensive and arises when
generating all permutations or trying all orderings. At the middle level, we dig deeper
into the algorithms that produce permutations, techniques to reduce the constant factors
or prune the search space, and mathematical tools like Stirling's approximation that
help us reason about factorial quantities.

---

## Permutation Generation Algorithms

There are several well-known algorithms for generating permutations. They differ in:

- **Memory usage**: some generate permutations in-place, others build new arrays.
- **Order of output**: lexicographic, or some other systematic order.
- **Number of swaps per permutation**: the theoretical minimum is O(1) amortized.

The main algorithms:

| Algorithm            | Swaps per permutation | Output order    | In-place? |
|---------------------|-----------------------|-----------------|-----------|
| Simple recursion     | O(n)                 | Not lexicographic | Yes       |
| Heap's algorithm     | O(1) amortized       | Not lexicographic | Yes       |
| Next permutation     | O(n) worst case      | Lexicographic    | Yes       |
| Steinhaus-Johnson-Trotter | O(1) amortized | By adjacent transpositions | Yes |

---

## Heap's Algorithm

**Heap's algorithm** (B.R. Heap, 1963) generates all permutations using exactly one
swap per permutation after the first. It is one of the most efficient permutation
generators in terms of swap count.

### How It Works

The algorithm uses a recursive structure where it permutes the first n-1 elements, then
swaps one element into the last position, and repeats.

The key insight: for even-sized arrays, swap the i-th element with the last element; for
odd-sized arrays, always swap the first element with the last.

### Go Implementation

```go
package main

import "fmt"

func heapsAlgorithm(a []int, size int, result *[][]int) {
    if size == 1 {
        perm := make([]int, len(a))
        copy(perm, a)
        *result = append(*result, perm)
        return
    }
    for i := 0; i < size; i++ {
        heapsAlgorithm(a, size-1, result)
        if size%2 == 1 {
            a[0], a[size-1] = a[size-1], a[0]
        } else {
            a[i], a[size-1] = a[size-1], a[i]
        }
    }
}

func main() {
    a := []int{1, 2, 3, 4}
    var result [][]int
    heapsAlgorithm(a, len(a), &result)
    fmt.Printf("Total permutations: %d\n", len(result))
    for _, p := range result[:6] {
        fmt.Println(p)
    }
    fmt.Println("...")
}
```

### Java Implementation

```java
import java.util.ArrayList;
import java.util.List;

public class HeapsAlgorithm {

    public static List<int[]> generate(int[] a) {
        List<int[]> result = new ArrayList<>();
        heapPermute(a, a.length, result);
        return result;
    }

    private static void heapPermute(int[] a, int size, List<int[]> result) {
        if (size == 1) {
            result.add(a.clone());
            return;
        }
        for (int i = 0; i < size; i++) {
            heapPermute(a, size - 1, result);
            if (size % 2 == 1) {
                int temp = a[0];
                a[0] = a[size - 1];
                a[size - 1] = temp;
            } else {
                int temp = a[i];
                a[i] = a[size - 1];
                a[size - 1] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4};
        List<int[]> result = generate(a);
        System.out.println("Total permutations: " + result.size());
        for (int i = 0; i < Math.min(6, result.size()); i++) {
            for (int v : result.get(i)) System.out.print(v + " ");
            System.out.println();
        }
        System.out.println("...");
    }
}
```

### Python Implementation

```python
def heaps_algorithm(a, size=None):
    if size is None:
        size = len(a)
    result = []
    _heap_permute(a, size, result)
    return result


def _heap_permute(a, size, result):
    if size == 1:
        result.append(a[:])
        return
    for i in range(size):
        _heap_permute(a, size - 1, result)
        if size % 2 == 1:
            a[0], a[size - 1] = a[size - 1], a[0]
        else:
            a[i], a[size - 1] = a[size - 1], a[i]


if __name__ == "__main__":
    a = [1, 2, 3, 4]
    result = heaps_algorithm(a)
    print(f"Total permutations: {len(result)}")
    for p in result[:6]:
        print(p)
    print("...")
```

### Complexity Analysis

- **Total swaps**: n! - 1 (exactly one swap between consecutive permutations)
- **Time**: O(n!) total, O(1) amortized per permutation (excluding output)
- **Space**: O(n) for the recursion stack

---

## Next Permutation Algorithm

The **next permutation** algorithm finds the lexicographically next permutation of a
sequence. By starting from the sorted (smallest) permutation and repeatedly calling
next permutation, you can generate all n! permutations in lexicographic order.

### Algorithm Steps

Given a sequence a[0..n-1]:

1. **Find the largest index i** such that a[i] < a[i+1]. If no such index exists, the
   permutation is the last one -- reverse the whole array to get the first permutation.
2. **Find the largest index j** greater than i such that a[i] < a[j].
3. **Swap** a[i] and a[j].
4. **Reverse** the suffix starting at a[i+1].

### Go Implementation

```go
package main

import (
    "fmt"
    "sort"
)

func nextPermutation(nums []int) bool {
    n := len(nums)
    i := n - 2
    for i >= 0 && nums[i] >= nums[i+1] {
        i--
    }
    if i < 0 {
        return false // last permutation
    }
    j := n - 1
    for nums[j] <= nums[i] {
        j--
    }
    nums[i], nums[j] = nums[j], nums[i]
    // reverse suffix
    left, right := i+1, n-1
    for left < right {
        nums[left], nums[right] = nums[right], nums[left]
        left++
        right--
    }
    return true
}

func main() {
    nums := []int{1, 2, 3, 4}
    sort.Ints(nums)
    count := 1
    fmt.Println(nums)
    for nextPermutation(nums) {
        count++
        if count <= 6 {
            fmt.Println(nums)
        }
    }
    fmt.Printf("Total permutations: %d\n", count)
}
```

### Java Implementation

```java
import java.util.Arrays;

public class NextPermutation {

    public static boolean nextPermutation(int[] nums) {
        int n = nums.length;
        int i = n - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        if (i < 0) return false;

        int j = n - 1;
        while (nums[j] <= nums[i]) {
            j--;
        }

        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;

        // reverse suffix from i+1
        int left = i + 1, right = n - 1;
        while (left < right) {
            temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        Arrays.sort(nums);
        int count = 1;
        System.out.println(Arrays.toString(nums));
        while (nextPermutation(nums)) {
            count++;
            if (count <= 6) {
                System.out.println(Arrays.toString(nums));
            }
        }
        System.out.println("Total permutations: " + count);
    }
}
```

### Python Implementation

```python
def next_permutation(nums):
    """Modify nums in-place to the next lexicographic permutation.
    Returns False if nums is the last permutation."""
    n = len(nums)
    i = n - 2
    while i >= 0 and nums[i] >= nums[i + 1]:
        i -= 1
    if i < 0:
        return False

    j = n - 1
    while nums[j] <= nums[i]:
        j -= 1

    nums[i], nums[j] = nums[j], nums[i]
    nums[i + 1:] = reversed(nums[i + 1:])
    return True


def generate_all_permutations(nums):
    nums.sort()
    result = [nums[:]]
    while next_permutation(nums):
        result.append(nums[:])
    return result


if __name__ == "__main__":
    nums = [1, 2, 3, 4]
    perms = generate_all_permutations(nums)
    print(f"Total permutations: {len(perms)}")
    for p in perms[:6]:
        print(p)
    print("...")
```

### Complexity

- **Per call**: O(n) worst case (finding i, j, and reversing suffix).
- **Total for all permutations**: O(n! * n) worst case, but amortized O(1) per
  permutation for the finding step (the reverse dominates).

---

## Pruning to Reduce from O(n!)

While an O(n!) algorithm visits all n! states, **pruning** allows us to skip large
subtrees of the permutation tree when we can determine they cannot lead to a better
solution.

### Pruning Strategies

1. **Bound-based pruning**: If the partial solution's cost already exceeds the best
   known complete solution, stop exploring that branch.

2. **Constraint-based pruning**: If adding an element would violate a constraint,
   skip it.

3. **Symmetry breaking**: If permutations are equivalent under some transformation
   (e.g., rotation in circular TSP), fix one element to reduce from n! to (n-1)!.

4. **Dominance pruning**: If one partial solution is provably worse than another with
   the same remaining elements, discard it.

### Example: Pruned Permutation Search

```python
def pruned_search(items, max_cost, cost_fn):
    """Generate permutations but prune branches exceeding max_cost."""
    n = len(items)
    best = float("inf")
    best_perm = None
    used = [False] * n
    current = []

    def backtrack(partial_cost):
        nonlocal best, best_perm
        if partial_cost >= best:
            return  # Prune: cannot improve on best

        if len(current) == n:
            if partial_cost < best:
                best = partial_cost
                best_perm = current[:]
            return

        for i in range(n):
            if not used[i]:
                new_cost = cost_fn(current, items[i])
                if new_cost < best:  # Prune
                    used[i] = True
                    current.append(items[i])
                    backtrack(new_cost)
                    current.pop()
                    used[i] = False

    backtrack(0)
    return best, best_perm
```

With effective pruning, the actual number of nodes visited can be **far less** than n!,
though the worst case remains O(n!).

---

## Branch and Bound for TSP

**Branch and bound** is a systematic way to apply pruning to optimization problems like
TSP. It maintains a lower bound on the cost of completing a partial tour and prunes
branches where the lower bound exceeds the best known complete tour.

### Algorithm Outline

```
BranchAndBound(partial_tour, visited, current_cost, lower_bound):
    if partial_tour is complete:
        update best if current_cost + return_cost < best_cost
        return

    for each unvisited city c:
        new_cost = current_cost + dist(last_city, c)
        new_lower_bound = compute_lower_bound(visited + {c})
        if new_cost + new_lower_bound < best_cost:
            add c to partial_tour
            BranchAndBound(partial_tour, visited + {c}, new_cost, new_lower_bound)
            remove c from partial_tour
```

### Go Implementation

```go
package main

import (
    "fmt"
    "math"
)

func tspBranchAndBound(dist [][]float64) (float64, []int) {
    n := len(dist)
    bestDist := math.Inf(1)
    bestRoute := make([]int, 0)
    visited := make([]bool, n)
    visited[0] = true
    path := []int{0}

    // Simple lower bound: minimum outgoing edge for each unvisited city
    lowerBound := func() float64 {
        lb := 0.0
        for i := 0; i < n; i++ {
            if !visited[i] {
                minEdge := math.Inf(1)
                for j := 0; j < n; j++ {
                    if i != j && dist[i][j] < minEdge {
                        minEdge = dist[i][j]
                    }
                }
                lb += minEdge
            }
        }
        return lb
    }

    var solve func(cost float64)
    solve = func(cost float64) {
        if len(path) == n {
            total := cost + dist[path[n-1]][0]
            if total < bestDist {
                bestDist = total
                bestRoute = make([]int, n)
                copy(bestRoute, path)
            }
            return
        }
        for c := 1; c < n; c++ {
            if !visited[c] {
                newCost := cost + dist[path[len(path)-1]][c]
                visited[c] = true
                path = append(path, c)
                lb := lowerBound()
                if newCost+lb < bestDist {
                    solve(newCost)
                }
                path = path[:len(path)-1]
                visited[c] = false
            }
        }
    }

    solve(0)
    return bestDist, bestRoute
}

func main() {
    dist := [][]float64{
        {0, 10, 15, 20, 25},
        {10, 0, 35, 25, 30},
        {15, 35, 0, 30, 20},
        {20, 25, 30, 0, 15},
        {25, 30, 20, 15, 0},
    }
    best, route := tspBranchAndBound(dist)
    fmt.Printf("Best distance: %.1f\n", best)
    fmt.Println("Route:", route)
}
```

### Java Implementation

```java
import java.util.ArrayList;
import java.util.List;

public class TSPBranchAndBound {

    static double bestDist;
    static List<Integer> bestRoute;

    public static void solve(double[][] dist) {
        int n = dist.length;
        bestDist = Double.MAX_VALUE;
        bestRoute = new ArrayList<>();
        boolean[] visited = new boolean[n];
        visited[0] = true;
        List<Integer> path = new ArrayList<>();
        path.add(0);
        branchAndBound(dist, visited, path, 0, n);
    }

    private static double lowerBound(double[][] dist, boolean[] visited, int n) {
        double lb = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                double minEdge = Double.MAX_VALUE;
                for (int j = 0; j < n; j++) {
                    if (i != j && dist[i][j] < minEdge) {
                        minEdge = dist[i][j];
                    }
                }
                lb += minEdge;
            }
        }
        return lb;
    }

    private static void branchAndBound(double[][] dist, boolean[] visited,
                                       List<Integer> path, double cost, int n) {
        if (path.size() == n) {
            double total = cost + dist[path.get(n - 1)][0];
            if (total < bestDist) {
                bestDist = total;
                bestRoute = new ArrayList<>(path);
            }
            return;
        }
        for (int c = 1; c < n; c++) {
            if (!visited[c]) {
                double newCost = cost + dist[path.get(path.size() - 1)][c];
                visited[c] = true;
                path.add(c);
                double lb = lowerBound(dist, visited, n);
                if (newCost + lb < bestDist) {
                    branchAndBound(dist, visited, path, newCost, n);
                }
                path.remove(path.size() - 1);
                visited[c] = false;
            }
        }
    }

    public static void main(String[] args) {
        double[][] dist = {
            {0, 10, 15, 20, 25},
            {10, 0, 35, 25, 30},
            {15, 35, 0, 30, 20},
            {20, 25, 30, 0, 15},
            {25, 30, 20, 15, 0}
        };
        solve(dist);
        System.out.printf("Best distance: %.1f%n", bestDist);
        System.out.println("Route: " + bestRoute);
    }
}
```

### Python Implementation

```python
import math


def tsp_branch_and_bound(dist):
    n = len(dist)
    best_dist = math.inf
    best_route = None
    visited = [False] * n
    visited[0] = True
    path = [0]

    def lower_bound():
        lb = 0
        for i in range(n):
            if not visited[i]:
                min_edge = min(dist[i][j] for j in range(n) if i != j)
                lb += min_edge
        return lb

    def solve(cost):
        nonlocal best_dist, best_route
        if len(path) == n:
            total = cost + dist[path[-1]][0]
            if total < best_dist:
                best_dist = total
                best_route = path[:]
            return
        for c in range(1, n):
            if not visited[c]:
                new_cost = cost + dist[path[-1]][c]
                visited[c] = True
                path.append(c)
                lb = lower_bound()
                if new_cost + lb < best_dist:
                    solve(new_cost)
                path.pop()
                visited[c] = False

    solve(0)
    return best_dist, best_route


if __name__ == "__main__":
    dist = [
        [0, 10, 15, 20, 25],
        [10, 0, 35, 25, 30],
        [15, 35, 0, 30, 20],
        [20, 25, 30, 0, 15],
        [25, 30, 20, 15, 0],
    ]
    best, route = tsp_branch_and_bound(dist)
    print(f"Best distance: {best}")
    print(f"Route: {route}")
```

---

## Comparing O(n!) vs O(2^n) vs O(n^n)

These three super-polynomial complexities are often confused. Here is a precise
comparison:

### Mathematical Relationship

For all n >= 1:
```
2^n  <=  n!  <=  n^n
```

This can be proven:
- **n! >= 2^n** (for n >= 4): Each factor in n! = n*(n-1)*...*1 is at least 2 for
  the first n-1 factors (when n >= 4), giving a product >= 2^(n-1) >= 2^n for large n.
  Formally: n!/2^n grows without bound.
- **n! <= n^n**: Each of the n factors in n! is at most n, so their product is at
  most n^n.

### Numerical Comparison

| n   | 2^n           | n!              | n^n               |
|-----|---------------|-----------------|---------------------|
| 5   | 32            | 120             | 3,125               |
| 10  | 1,024         | 3,628,800       | 10,000,000,000       |
| 15  | 32,768        | 1.31 x 10^12   | 4.37 x 10^17        |
| 20  | 1,048,576     | 2.43 x 10^18   | 1.05 x 10^26        |

### Growth Rate Ratios

Using Stirling's approximation:
```
n! ~ sqrt(2*pi*n) * (n/e)^n
```

So:
```
n! / 2^n  ~  sqrt(2*pi*n) * (n / (2e))^n  -->  infinity (fast)
n^n / n!  ~  e^n / sqrt(2*pi*n)            -->  infinity (fast)
```

Both ratios grow exponentially, confirming the strict ordering: 2^n << n! << n^n.

### When Each Arises

| Complexity | Typical source                                    |
|-----------|--------------------------------------------------|
| O(2^n)    | Subset enumeration, binary decisions             |
| O(n!)     | Permutation enumeration, ordering problems       |
| O(n^n)    | Unrestricted assignment (each of n items has n choices) |

---

## Stirling's Approximation

**Stirling's approximation** provides a way to estimate n! for large n without
computing the full product:

```
n! ~ sqrt(2 * pi * n) * (n / e)^n
```

Where e = 2.71828...

### Accuracy

| n   | n! (exact)     | Stirling approx    | Error (%) |
|-----|----------------|--------------------|-----------|
| 5   | 120            | 118.02             | 1.65%     |
| 10  | 3,628,800      | 3,598,695          | 0.83%     |
| 20  | 2.432902 x 10^18 | 2.422787 x 10^18 | 0.42%     |
| 50  | 3.041409 x 10^64 | 3.036345 x 10^64 | 0.17%     |

The approximation improves as n grows. Even for small n, the relative error is under 2%.

### Taking Logarithms

For complexity analysis, the log form is often more useful:

```
log2(n!) ~ n * log2(n) - n * log2(e) + 0.5 * log2(2 * pi * n)
         ~ n * log2(n) - 1.4427 * n   (for large n)
         ~ n * log2(n/e)
```

This shows that **log(n!) = Theta(n log n)**, which is why comparison-based sorting
has an Omega(n log n) lower bound (it must distinguish between n! possible orderings).

### Python Verification

```python
import math

def stirling(n):
    return math.sqrt(2 * math.pi * n) * (n / math.e) ** n

for n in [5, 10, 20, 50, 100]:
    exact = math.factorial(n)
    approx = stirling(n)
    error = abs(exact - approx) / exact * 100
    print(f"n={n:3d}: exact={exact:.6e}, stirling={approx:.6e}, error={error:.4f}%")
```

---

## Permutations with Duplicates

When the input contains duplicate elements, the number of distinct permutations is less
than n!. For an array with groups of identical elements of sizes k1, k2, ..., km:

```
Distinct permutations = n! / (k1! * k2! * ... * km!)
```

### Example

For [1, 1, 2]: n=3, k1=2 (two 1s), k2=1 (one 2)
- Distinct permutations = 3! / (2! * 1!) = 6/2 = 3
- They are: [1,1,2], [1,2,1], [2,1,1]

### Python Implementation

```python
def permutations_with_duplicates(nums):
    result = []
    nums.sort()

    def backtrack(current, remaining):
        if not remaining:
            result.append(current[:])
            return
        prev = None
        for i, num in enumerate(remaining):
            if num == prev:
                continue  # skip duplicates
            prev = num
            current.append(num)
            backtrack(current, remaining[:i] + remaining[i+1:])
            current.pop()

    backtrack([], nums)
    return result


nums = [1, 1, 2, 2]
perms = permutations_with_duplicates(nums)
print(f"Distinct permutations: {len(perms)}")  # 4!/(2!*2!) = 6
for p in perms:
    print(p)
```

---

## Counting Permutations Without Generating Them

Sometimes we need to know **how many** permutations satisfy a condition without
generating them all. This is useful for:

- Finding the k-th permutation in lexicographic order.
- Computing combinatorial probabilities.

### Finding the k-th Permutation (Factoradic Number System)

```python
def kth_permutation(n, k):
    """Return the k-th permutation (0-indexed) of [1, 2, ..., n]."""
    numbers = list(range(1, n + 1))
    result = []
    k_remaining = k

    for i in range(n, 0, -1):
        fact = 1
        for j in range(1, i):
            fact *= j
        index = k_remaining // fact
        k_remaining %= fact
        result.append(numbers[index])
        numbers.pop(index)

    return result


# The 100th permutation (0-indexed) of [1,2,3,4,5]
print(kth_permutation(5, 100))  # [4, 2, 1, 5, 3]
```

This runs in **O(n^2)** time, vastly better than generating all permutations up to the
k-th one.

---

## Partial Permutations P(n,k)

A **partial permutation** P(n,k) counts the number of ways to choose and arrange k
items from n distinct items:

```
P(n,k) = n! / (n-k)! = n * (n-1) * ... * (n-k+1)
```

This is O(n^k) when k is fixed, which is polynomial -- much better than O(n!).

### Example

Choosing and arranging 3 items from 10: P(10,3) = 10 * 9 * 8 = 720

Versus all permutations: 10! = 3,628,800

### When k << n, Partial Permutations Are Tractable

| n   | k  | P(n,k)       | n!             |
|-----|----|-------------|----------------|
| 20  | 3  | 6,840       | 2.43 x 10^18  |
| 20  | 5  | 1,860,480   | 2.43 x 10^18  |
| 100 | 3  | 970,200     | 9.33 x 10^157 |

---

## Practical Limits and Benchmarks

### Maximum Feasible n for Different Time Budgets

Assuming 10^9 operations per second:

| Time limit | Max n for O(n!)  | Max n for O(2^n) |
|-----------|------------------|-------------------|
| 1 second  | ~12              | ~30               |
| 1 minute  | ~14              | ~36               |
| 1 hour    | ~16              | ~42               |
| 1 day     | ~17              | ~47               |

### Memory Considerations

Storing all n! permutations requires:
- O(n! * n) memory for explicit storage
- For n=10: 3,628,800 * 10 * 4 bytes ~ 145 MB (as int arrays)
- For n=12: 479,001,600 * 12 * 4 bytes ~ 23 GB

This means even storing all permutations becomes infeasible around n=12.

---

## Key Takeaways

1. **Heap's algorithm** generates permutations with O(1) amortized swaps per
   permutation -- the most swap-efficient method.

2. **Next permutation** generates permutations in lexicographic order and is useful
   when you need a specific range of permutations.

3. **Pruning and branch-and-bound** can dramatically reduce actual running time below
   the O(n!) worst case, though worst-case complexity remains unchanged.

4. **O(2^n) << O(n!) << O(n^n)** -- these three super-polynomial classes are strictly
   ordered, and the gaps between them grow rapidly.

5. **Stirling's approximation** n! ~ sqrt(2*pi*n) * (n/e)^n gives excellent estimates
   and reveals that log(n!) = Theta(n log n).

6. **Duplicates reduce the count** from n! to n!/(k1! * k2! * ... * km!).

7. **Partial permutations** P(n,k) are polynomial in n when k is constant, offering
   a way to avoid full factorial blowup.

8. **Practical limit** for O(n!) is roughly n=12 for interactive use and n=15-17 for
   batch processing with hours of compute time.
