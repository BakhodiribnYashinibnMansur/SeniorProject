# How to Calculate Complexity? -- Interview Questions

## Table of Contents

1. [Junior Level Questions](#junior-level-questions)
2. [Middle Level Questions](#middle-level-questions)
3. [Senior Level Questions](#senior-level-questions)
4. [Professional Level Questions](#professional-level-questions)
5. [Coding Challenge](#coding-challenge)

---

## Junior Level Questions

### Q1: What is the time complexity of this code?

```
for i := 0; i < n; i++ {
    for j := 0; j < 100; j++ {
        // constant work
    }
}
```

**Answer**: O(n). The inner loop runs a fixed 100 times regardless of n. Total operations: 100n = O(n). A common mistake is saying O(100n) or O(n * 100) -- remember, constants are dropped.

### Q2: How do you determine the complexity of sequential code blocks?

**Answer**: Add the complexities of each block, then keep the dominant term. If block A is O(n) and block B is O(n^2), the total is O(n + n^2) = O(n^2). Sequential blocks use addition, nested blocks use multiplication.

### Q3: What is the difference between O(n) and O(n^2)? Give a concrete example.

**Answer**: O(n) means the time grows linearly with input size; O(n^2) means it grows quadratically. If n doubles, O(n) time doubles, but O(n^2) time quadruples. Example: searching an unsorted array is O(n) (one loop), checking all pairs for duplicates is O(n^2) (nested loops).

### Q4: Why do we drop constants in Big-O?

**Answer**: Because Big-O describes growth rate as n approaches infinity. Whether an algorithm does 2n or 5n operations, it grows linearly -- the constant only affects the y-intercept, not the slope on a log-log plot. At large scale, the shape of the curve matters, not the constant multiplier.

### Q5: What is the complexity of binary search and why?

**Answer**: O(log n). Each comparison eliminates half of the remaining search space. Starting with n elements: n -> n/2 -> n/4 -> ... -> 1. The number of halvings to reach 1 is log2(n).

---

## Middle Level Questions

### Q6: Apply the Master Theorem to T(n) = 4T(n/2) + n^2.

**Answer**: a=4, b=2, d=2. log_b(a) = log_2(4) = 2. Since d = log_b(a), this is Case 2: T(n) = O(n^2 log n).

### Q7: What is amortized analysis? Explain with an example.

**Answer**: Amortized analysis computes the average cost per operation over a worst-case sequence of operations (not average case). Example: dynamic array appending. Most appends cost O(1), but occasionally (when the array is full) a resize costs O(n). Over n appends, total cost is O(n) -- so the amortized cost per append is O(1). This is different from average-case analysis because it guarantees the bound for any sequence of operations.

### Q8: Write the recurrence for quicksort and analyze both best and worst case.

**Answer**:
- Best/average case: Partition splits evenly. T(n) = 2T(n/2) + O(n). By Master Theorem: O(n log n).
- Worst case: Partition always puts all elements on one side. T(n) = T(n-1) + O(n). Expanding: n + (n-1) + ... + 1 = n(n+1)/2 = O(n^2).

### Q9: What is the complexity of BFS on a graph?

**Answer**: O(V + E) where V is the number of vertices and E is the number of edges. Each vertex is enqueued and dequeued at most once (O(V)), and each edge is examined at most twice for undirected graphs (O(E)). It is NOT O(V^2) unless E = O(V^2).

### Q10: How do you analyze the complexity when there are two independent input variables?

**Answer**: Use separate variables. If an algorithm iterates over an m x n matrix, it is O(m * n), not O(n^2). Only collapse to O(n^2) if you know m = n. String matching over text of length n with pattern of length m is O(n * m).

---

## Senior Level Questions

### Q11: How would you empirically verify that an algorithm is O(n log n)?

**Answer**: Run benchmarks at sizes n, 2n, 4n, 8n. Compute the ratio T(2n)/T(n). For O(n log n), the ratio should be slightly above 2.0 (approximately 2 * (log(2n)/log(n)) which converges to 2 as n grows). Compare against the expected ratio of exactly 2.0 for O(n) and 4.0 for O(n^2). Use median of multiple runs to account for variance.

### Q12: Explain how cache performance affects real-world complexity.

**Answer**: An algorithm that is O(n) with sequential memory access can be faster than an O(n) algorithm with random access because of CPU cache behavior. Arrays have spatial locality (adjacent elements are in the same cache line), while linked lists and hash tables scatter data across memory. In practice, an O(n log n) algorithm with cache-friendly access (like merge sort on arrays) can beat an O(n) algorithm with cache-hostile access for certain n ranges.

### Q13: Compare profiling tools across Go, Java, and Python.

**Answer**:
- **Go**: `pprof` (built-in), low overhead, provides CPU/memory/goroutine profiles. Use `go tool pprof` for analysis and flame graphs.
- **Java**: JFR (built-in since JDK 11) for always-on profiling; async-profiler for low-overhead sampling; JMH for microbenchmarks. JIT compilation makes profiling tricky -- always warm up.
- **Python**: `cProfile` (built-in) for function-level profiling; `py-spy` for sampling without code changes; `timeit` for microbenchmarks. The GIL complicates multi-threaded profiling.

---

## Professional Level Questions

### Q14: Prove that comparison-based sorting requires Omega(n log n) comparisons.

**Answer**: Model any comparison-based sort as a binary decision tree. Each leaf is a permutation. There are n! permutations, so the tree needs at least n! leaves. A binary tree of height h has at most 2^h leaves. Therefore h >= log_2(n!). By Stirling's approximation, log_2(n!) = n log_2(n) - n log_2(e) + O(log n) = Theta(n log n). So the minimum height (worst-case comparisons) is Omega(n log n).

### Q15: When would you use the Akra-Bazzi method instead of the Master Theorem?

**Answer**: When the recurrence has subproblems of unequal sizes: T(n) = T(n/3) + T(2n/3) + n. The Master Theorem requires all subproblems to be T(n/b) with the same b. Akra-Bazzi also handles additive terms in the argument, like T(n) = 2T(n/2 + 17) + n. First find p where sum(a_i * b_i^p) = 1, then evaluate the integral.

### Q16: Explain the substitution method. What is the hardest part?

**Answer**: Guess the solution form, prove by induction. The hardest part is handling lower-order terms. If you guess T(n) <= c*n*log(n) but the induction gives T(n) <= c*n*log(n) + n, the proof fails. Fix: strengthen the hypothesis to T(n) <= c*n*log(n) - d*n, so the -d*n absorbs the residual term. The initial guess often comes from expanding the recurrence a few levels or using the recursion tree method.

---

## Coding Challenge

**Problem**: Analyze the time complexity of the following code. Then rewrite it to achieve better complexity. Provide your analysis for both versions.

### Go

```go
// What is the complexity? Can you do better?
func findPairWithSum(arr []int, target int) (int, int) {
    n := len(arr)
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            if arr[i]+arr[j] == target {
                return arr[i], arr[j]
            }
        }
    }
    return -1, -1
}
```

**Analysis**: Nested loop over all pairs: O(n^2).

**Optimized version**:

```go
func findPairWithSumOptimized(arr []int, target int) (int, int) {
    seen := make(map[int]bool)
    for _, val := range arr {         // O(n) iterations
        complement := target - val
        if seen[complement] {         // O(1) average hash lookup
            return complement, val
        }
        seen[val] = true              // O(1) average hash insert
    }
    return -1, -1
}
// Complexity: O(n) time, O(n) space
```

### Java

```java
// Original: O(n^2)
public static int[] findPairWithSum(int[] arr, int target) {
    int n = arr.length;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (arr[i] + arr[j] == target) {
                return new int[]{arr[i], arr[j]};
            }
        }
    }
    return new int[]{-1, -1};
}

// Optimized: O(n) time, O(n) space
public static int[] findPairWithSumOptimized(int[] arr, int target) {
    Set<Integer> seen = new HashSet<>();
    for (int val : arr) {
        int complement = target - val;
        if (seen.contains(complement)) {
            return new int[]{complement, val};
        }
        seen.add(val);
    }
    return new int[]{-1, -1};
}
```

### Python

```python
# Original: O(n^2)
def find_pair_with_sum(arr, target):
    n = len(arr)
    for i in range(n):
        for j in range(i + 1, n):
            if arr[i] + arr[j] == target:
                return arr[i], arr[j]
    return -1, -1

# Optimized: O(n) time, O(n) space
def find_pair_with_sum_optimized(arr, target):
    seen = set()
    for val in arr:
        complement = target - val
        if complement in seen:
            return complement, val
        seen.add(val)
    return -1, -1
```

**Key insight**: By trading O(n) space for a hash set, we reduce time complexity from O(n^2) to O(n). Each lookup and insert in a hash set is O(1) amortized, and we traverse the array once.
