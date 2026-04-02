# Time vs Space Complexity — Practice Tasks

> All tasks must be solved in **Go**, **Java**, and **Python**.

## Beginner Tasks

**Task 1:** Count the number of basic operations in a given function. Write a wrapper that counts comparisons and assignments for a linear search.

#### Go

```go
package main

import "fmt"

func linearSearchCounted(arr []int, target int) (int, int) {
    ops := 0
    for i := 0; i < len(arr); i++ {
        ops++ // comparison
        if arr[i] == target {
            return i, ops
        }
    }
    return -1, ops
}

func main() {
    arr := []int{5, 3, 8, 1, 9, 2, 7}
    idx, ops := linearSearchCounted(arr, 7)
    fmt.Printf("Found at index %d, operations: %d\n", idx, ops)
}
```

#### Java

```java
public class Task1 {
    public static int[] linearSearchCounted(int[] arr, int target) {
        int ops = 0;
        for (int i = 0; i < arr.length; i++) {
            ops++;
            if (arr[i] == target) return new int[]{i, ops};
        }
        return new int[]{-1, ops};
    }

    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 1, 9, 2, 7};
        int[] result = linearSearchCounted(arr, 7);
        System.out.printf("Found at index %d, operations: %d%n", result[0], result[1]);
    }
}
```

#### Python

```python
def linear_search_counted(arr, target):
    ops = 0
    for i in range(len(arr)):
        ops += 1
        if arr[i] == target:
            return i, ops
    return -1, ops

arr = [5, 3, 8, 1, 9, 2, 7]
idx, ops = linear_search_counted(arr, 7)
print(f"Found at index {idx}, operations: {ops}")
```

- **Constraints:** Handle empty array and target not found
- **Expected Output:** Index and operation count
- **Evaluation:** Correct count, handles edge cases

---

**Task 2:** Implement a function that takes an array and returns True if it contains duplicates. Write two versions: O(n²) time O(1) space, and O(n) time O(n) space.

#### Go

```go
package main

func hasDupBrute(arr []int) bool {
    // TODO: O(n²) time, O(1) space
    return false
}

func hasDupHash(arr []int) bool {
    // TODO: O(n) time, O(n) space
    return false
}

func main() {
    // Test with: [1,2,3,4,5] → false, [1,2,3,2,5] → true
}
```

#### Java

```java
public class Task2 {
    public static boolean hasDupBrute(int[] arr) {
        // TODO: O(n²) time, O(1) space
        return false;
    }

    public static boolean hasDupHash(int[] arr) {
        // TODO: O(n) time, O(n) space
        return false;
    }

    public static void main(String[] args) {
        // Test with: {1,2,3,4,5} → false, {1,2,3,2,5} → true
    }
}
```

#### Python

```python
def has_dup_brute(arr):
    # TODO: O(n²) time, O(1) space
    pass

def has_dup_hash(arr):
    # TODO: O(n) time, O(n) space
    pass

# Test with: [1,2,3,4,5] → False, [1,2,3,2,5] → True
```

- **Constraints:** Both must return correct results. Analyze time and space for each.
- **Evaluation:** Correctness, complexity analysis in comments

---

**Task 3:** Reverse an array in-place (O(1) space) and with a new array (O(n) space). Compare.

#### Go

```go
package main

func reverseInPlace(arr []int) {
    // TODO: O(n) time, O(1) space
}

func reverseCopy(arr []int) []int {
    // TODO: O(n) time, O(n) space
    return nil
}

func main() {
    // Test with: [1,2,3,4,5] → [5,4,3,2,1]
}
```

#### Java

```java
public class Task3 {
    public static void reverseInPlace(int[] arr) {
        // TODO: O(n) time, O(1) space
    }

    public static int[] reverseCopy(int[] arr) {
        // TODO: O(n) time, O(n) space
        return null;
    }

    public static void main(String[] args) {
        // Test with: {1,2,3,4,5} → {5,4,3,2,1}
    }
}
```

#### Python

```python
def reverse_in_place(arr):
    # TODO: O(n) time, O(1) space
    pass

def reverse_copy(arr):
    # TODO: O(n) time, O(n) space
    pass

# Test with: [1,2,3,4,5] → [5,4,3,2,1]
```

- **Constraints:** In-place must NOT create a new array
- **Evaluation:** Correctness, space verification

---

**Task 4:** Measure and compare execution time of O(n) vs O(n²) algorithms on arrays of size 100, 1000, 10000.

#### Go

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    sizes := []int{100, 1000, 10000}
    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr { arr[i] = i }

        // TODO: Measure O(n) algorithm (e.g., sum)
        start := time.Now()
        // ... your O(n) code
        linearTime := time.Since(start)

        // TODO: Measure O(n²) algorithm (e.g., brute force duplicate check)
        start = time.Now()
        // ... your O(n²) code
        quadraticTime := time.Since(start)

        fmt.Printf("n=%5d | O(n): %v | O(n²): %v\n", n, linearTime, quadraticTime)
    }
}
```

#### Java

```java
public class Task4 {
    public static void main(String[] args) {
        int[] sizes = {100, 1000, 10000};
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;

            // TODO: Measure O(n) and O(n²) algorithms
        }
    }
}
```

#### Python

```python
import time

sizes = [100, 1000, 10000]
for n in sizes:
    arr = list(range(n))

    # TODO: Measure O(n) and O(n²) algorithms
    pass
```

- **Constraints:** Use the same input for both algorithms
- **Evaluation:** Timing results show quadratic growth for O(n²)

---

**Task 5:** Calculate the space used by recursive vs iterative sum of 1 to n. Track the maximum recursion depth.

#### Go

```go
package main

func sumRecursive(n int, depth *int) int {
    *depth++
    // TODO: implement, track depth
    return 0
}

func sumIterative(n int) int {
    // TODO: O(1) space
    return 0
}

func main() {
    // Compare for n=10, 100, 1000
}
```

#### Java

```java
public class Task5 {
    static int maxDepth = 0;

    public static int sumRecursive(int n, int depth) {
        // TODO: implement, track depth
        return 0;
    }

    public static int sumIterative(int n) {
        // TODO: O(1) space
        return 0;
    }

    public static void main(String[] args) {
        // Compare for n=10, 100, 1000
    }
}
```

#### Python

```python
max_depth = 0

def sum_recursive(n, depth=0):
    global max_depth
    # TODO: implement, track depth
    pass

def sum_iterative(n):
    # TODO: O(1) space
    pass

# Compare for n=10, 100, 1000
```

- **Constraints:** Report max recursion depth for each n
- **Evaluation:** Demonstrates O(n) vs O(1) space

---

## Intermediate Tasks

**Task 6:** Implement Two Sum three ways: (1) brute force O(n²)/O(1), (2) sort + two pointers O(n log n)/O(1), (3) hash map O(n)/O(n). Compare all three.

#### Go

```go
package main

func twoSumBrute(nums []int, target int) []int { return nil }
func twoSumSort(nums []int, target int) []int { return nil }
func twoSumHash(nums []int, target int) []int { return nil }

func main() {
    // Test with [2,7,11,15], target=9 → [0,1]
}
```

#### Java

```java
public class Task6 {
    public static int[] twoSumBrute(int[] nums, int target) { return null; }
    public static int[] twoSumSort(int[] nums, int target) { return null; }
    public static int[] twoSumHash(int[] nums, int target) { return null; }

    public static void main(String[] args) {
        // Test with {2,7,11,15}, target=9
    }
}
```

#### Python

```python
def two_sum_brute(nums, target): pass
def two_sum_sort(nums, target): pass
def two_sum_hash(nums, target): pass

# Test with [2,7,11,15], target=9
```

- **Constraints:** Sort version must return original indices (tricky!)
- **Evaluation:** Correct results, documented complexity

---

**Task 7:** Implement maximum subarray sum using: (1) brute force O(n³), (2) optimized brute force O(n²), (3) Kadane's algorithm O(n). Benchmark all three.

#### Go

```go
package main

func maxSubarrayCubic(arr []int) int { return 0 }
func maxSubarrayQuadratic(arr []int) int { return 0 }
func maxSubarrayKadane(arr []int) int { return 0 }

func main() {
    // Test with [-2,1,-3,4,-1,2,1,-5,4] → 6 (subarray [4,-1,2,1])
}
```

#### Java

```java
public class Task7 {
    public static int maxSubarrayCubic(int[] arr) { return 0; }
    public static int maxSubarrayQuadratic(int[] arr) { return 0; }
    public static int maxSubarrayKadane(int[] arr) { return 0; }

    public static void main(String[] args) {
        // Test with {-2,1,-3,4,-1,2,1,-5,4} → 6
    }
}
```

#### Python

```python
def max_subarray_cubic(arr): pass
def max_subarray_quadratic(arr): pass
def max_subarray_kadane(arr): pass

# Test with [-2,1,-3,4,-1,2,1,-5,4] → 6
```

- **Constraints:** Each must handle all-negative arrays
- **Evaluation:** Correctness + benchmark showing O(n³) vs O(n²) vs O(n)

---

**Task 8:** Build a prefix sum array and answer range sum queries in O(1). Compare with naive O(n) per query approach.

#### Go

```go
package main

func buildPrefixSum(arr []int) []int { return nil }
func rangeSum(prefix []int, left, right int) int { return 0 }
func rangeSumNaive(arr []int, left, right int) int { return 0 }

func main() {
    // Test with [1,2,3,4,5], query (1,3) → 9
}
```

#### Java

```java
public class Task8 {
    public static int[] buildPrefixSum(int[] arr) { return null; }
    public static int rangeSum(int[] prefix, int left, int right) { return 0; }
    public static int rangeSumNaive(int[] arr, int left, int right) { return 0; }

    public static void main(String[] args) {
        // Test with {1,2,3,4,5}, query (1,3) → 9
    }
}
```

#### Python

```python
def build_prefix_sum(arr): pass
def range_sum(prefix, left, right): pass
def range_sum_naive(arr, left, right): pass

# Test with [1,2,3,4,5], query (1,3) → 9
```

- **Constraints:** Benchmark with 100,000 queries
- **Evaluation:** O(n) build + O(1) query vs O(n) per query

---

**Task 9:** Implement matrix multiplication. Analyze its time O(n³) and space O(n²) complexity. Verify experimentally.

#### Go

```go
package main

func multiply(a, b [][]int) [][]int {
    // TODO: O(n³) time, O(n²) space
    return nil
}

func main() {
    // Test with 2x2 matrices, then benchmark with n=10,50,100,200
}
```

#### Java

```java
public class Task9 {
    public static int[][] multiply(int[][] a, int[][] b) {
        // TODO: O(n³) time, O(n²) space
        return null;
    }

    public static void main(String[] args) {
        // Benchmark with n=10,50,100,200
    }
}
```

#### Python

```python
def multiply(a, b):
    # TODO: O(n³) time, O(n²) space
    pass

# Benchmark with n=10,50,100,200
```

- **Constraints:** Verify T(2n)/T(n) ≈ 8 (cubic growth)
- **Evaluation:** Experimental verification of O(n³)

---

**Task 10:** Implement memoized Fibonacci and space-optimized Fibonacci. Measure memory usage difference.

#### Go

```go
package main

func fibMemo(n int, memo map[int]int) int { return 0 }
func fibOptimal(n int) int { return 0 }

func main() {
    // Compare memory usage for n=100, 1000, 10000
}
```

#### Java

```java
import java.util.HashMap;

public class Task10 {
    public static long fibMemo(int n, HashMap<Integer, Long> memo) { return 0; }
    public static long fibOptimal(int n) { return 0; }

    public static void main(String[] args) {
        // Compare memory usage for n=100, 1000, 10000
    }
}
```

#### Python

```python
import sys

def fib_memo(n, memo={}): pass
def fib_optimal(n): pass

# Compare memory for n=100, 1000, 10000
# Use sys.getsizeof() to measure
```

- **Constraints:** fibOptimal must use O(1) space
- **Evaluation:** Memory measurements show O(n) vs O(1)

---

## Advanced Tasks

**Task 11:** Implement meet-in-the-middle for Subset Sum. Compare with brute force O(2ⁿ) and DP O(nT) approaches.

#### Go

```go
package main

func subsetSumBrute(nums []int, target int) bool { return false }
func subsetSumDP(nums []int, target int) bool { return false }
func subsetSumMITM(nums []int, target int) bool { return false }

func main() {
    // Test: [3,34,4,12,5,2], target=9 → true
    // Benchmark all three for n=15, 20, 25
}
```

#### Java

```java
public class Task11 {
    public static boolean subsetSumBrute(int[] nums, int target) { return false; }
    public static boolean subsetSumDP(int[] nums, int target) { return false; }
    public static boolean subsetSumMITM(int[] nums, int target) { return false; }

    public static void main(String[] args) {
        // Benchmark for n=15, 20, 25
    }
}
```

#### Python

```python
def subset_sum_brute(nums, target): pass
def subset_sum_dp(nums, target): pass
def subset_sum_mitm(nums, target): pass

# Benchmark for n=15, 20, 25
```

- **Constraints:** MITM should use O(2^(n/2)) time and space
- **Evaluation:** Show time-space trade-off between all three approaches

---

**Task 12:** Implement a Bloom filter from scratch. Test false positive rate with varying m (bit array size) and k (hash count).

#### Go

```go
package main

type BloomFilter struct {
    // TODO
}

func main() {
    // Insert 10000 elements, test with 10000 non-elements
    // Measure false positive rate for different m and k values
}
```

#### Java

```java
public class Task12 {
    // TODO: BloomFilter class

    public static void main(String[] args) {
        // Insert 10000 elements, measure FP rate
    }
}
```

#### Python

```python
class BloomFilter:
    # TODO
    pass

# Insert 10000 elements, measure FP rate
```

- **Constraints:** Test with m/n ratios of 5, 10, 20 and k = 3, 5, 7
- **Evaluation:** FP rate matches theoretical prediction

---

**Task 13:** Implement an LRU cache with O(1) get/put using hash map + doubly linked list. Compare with naive O(n) approach.

#### Go

```go
package main

type LRUCache struct {
    // TODO: hash map + doubly linked list
}

func main() {
    // Benchmark O(1) LRU vs O(n) naive for 100000 operations
}
```

#### Java

```java
public class Task13 {
    // TODO: LRUCache class
    public static void main(String[] args) {
        // Benchmark for 100000 operations
    }
}
```

#### Python

```python
class LRUCache:
    # TODO
    pass

# Benchmark for 100000 operations
```

- **Constraints:** Must be O(1) for both get and put
- **Evaluation:** Benchmark confirms O(1) amortized

---

**Task 14:** Implement counting sort O(n+k) and compare with comparison sort O(n log n). Analyze the space trade-off.

#### Go

```go
package main

import "sort"

func countingSort(arr []int, maxVal int) []int { return nil }

func main() {
    // Compare counting sort vs sort.Ints for arrays with range [0, 100] and [0, 1000000]
}
```

#### Java

```java
import java.util.Arrays;

public class Task14 {
    public static int[] countingSort(int[] arr, int maxVal) { return null; }

    public static void main(String[] args) {
        // Compare counting sort vs Arrays.sort
    }
}
```

#### Python

```python
def counting_sort(arr, max_val): pass

# Compare counting sort vs sorted() for different ranges
```

- **Constraints:** Show when counting sort's O(k) space makes it impractical
- **Evaluation:** Benchmark for small k (worthwhile) vs large k (wasteful)

---

**Task 15:** Implement a streaming algorithm: find the majority element using Boyer-Moore Voting (O(n) time, O(1) space) vs hash map counting (O(n) time, O(n) space).

#### Go

```go
package main

func majorityBoyerMoore(arr []int) int { return 0 }
func majorityHashMap(arr []int) int { return 0 }

func main() {
    // Test: [3,2,3] → 3, [2,2,1,1,1,2,2] → 2
}
```

#### Java

```java
public class Task15 {
    public static int majorityBoyerMoore(int[] arr) { return 0; }
    public static int majorityHashMap(int[] arr) { return 0; }

    public static void main(String[] args) {
        // Test: {3,2,3} → 3
    }
}
```

#### Python

```python
def majority_boyer_moore(arr): pass
def majority_hash_map(arr): pass

# Test: [3,2,3] → 3
```

- **Constraints:** Boyer-Moore must use O(1) space
- **Evaluation:** Both correct, memory measurements differ

---

## Benchmark Task

> Compare performance across all 3 languages for O(n) hash-based vs O(n²) brute force duplicate detection.

#### Go

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    sizes := []int{10, 100, 1000, 10000, 100000}
    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr { arr[i] = i }

        start := time.Now()
        for i := 0; i < 50; i++ {
            hasDupBrute(append([]int{}, arr...))
        }
        brute := time.Since(start)

        start = time.Now()
        for i := 0; i < 50; i++ {
            hasDupHash(append([]int{}, arr...))
        }
        hash := time.Since(start)

        fmt.Printf("n=%7d: Brute=%.3f ms, Hash=%.3f ms\n",
            n, float64(brute.Milliseconds())/50.0, float64(hash.Milliseconds())/50.0)
    }
}

func hasDupBrute(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] == arr[j] { return true }
        }
    }
    return false
}

func hasDupHash(arr []int) bool {
    seen := make(map[int]bool, len(arr))
    for _, v := range arr {
        if seen[v] { return true }
        seen[v] = true
    }
    return false
}
```

#### Java

```java
import java.util.HashSet;

public class Benchmark {
    public static void main(String[] args) {
        int[] sizes = {10, 100, 1000, 10000, 100000};
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;

            long start = System.nanoTime();
            for (int t = 0; t < 50; t++) hasDupBrute(arr.clone());
            double brute = (System.nanoTime() - start) / 50.0 / 1_000_000;

            start = System.nanoTime();
            for (int t = 0; t < 50; t++) hasDupHash(arr.clone());
            double hash = (System.nanoTime() - start) / 50.0 / 1_000_000;

            System.out.printf("n=%7d: Brute=%.3f ms, Hash=%.3f ms%n", n, brute, hash);
        }
    }

    static boolean hasDupBrute(int[] a) {
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] == a[j]) return true;
        return false;
    }

    static boolean hasDupHash(int[] a) {
        HashSet<Integer> s = new HashSet<>(a.length);
        for (int v : a) if (!s.add(v)) return true;
        return false;
    }
}
```

#### Python

```python
import timeit

def has_dup_brute(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]: return True
    return False

def has_dup_hash(arr):
    return len(arr) != len(set(arr))

sizes = [10, 100, 1000, 10000]
for n in sizes:
    arr = list(range(n))
    brute = timeit.timeit(lambda: has_dup_brute(list(arr)), number=50) / 50 * 1000
    hash_t = timeit.timeit(lambda: has_dup_hash(list(arr)), number=50) / 50 * 1000
    print(f"n={n:>7}: Brute={brute:.3f} ms, Hash={hash_t:.3f} ms")
```
