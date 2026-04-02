# Time vs Space Complexity — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is time complexity and why do we use it? | Measures how runtime grows with input size; we use Big-O to compare algorithms independent of hardware |
| 2 | What is the difference between time and space complexity? | Time = number of operations; Space = amount of memory used. Both measured relative to input size n |
| 3 | What does O(1) mean? Give an example. | Constant time — runtime doesn't change with input size. Example: array access by index |
| 4 | Why is O(n) faster than O(n²) for large inputs? | O(n) grows linearly, O(n²) grows quadratically. At n=10,000: O(n)=10K ops, O(n²)=100M ops |
| 5 | Explain the time-space trade-off with an example. | Using a hash set (O(n) space) to detect duplicates in O(n) time instead of O(n²) brute force with O(1) space |
| 6 | What is auxiliary space? | Extra memory allocated by the algorithm beyond the input. In-place algorithms use O(1) auxiliary space |

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is amortized O(1) and give an example? | Average cost over a sequence of operations. Dynamic array append: usually O(1), occasionally O(n) resize, amortized O(1) |
| 2 | When would you choose O(n²) time O(1) space over O(n) time O(n) space? | Embedded systems with limited RAM, very small n, or when simplicity matters more than speed |
| 3 | What is cache locality and how does it affect real performance? | Sequential memory access (arrays) hits CPU cache; scattered access (linked lists) causes cache misses. Arrays can be 10-100x faster at same Big-O |
| 4 | Compare worst-case, average-case, and expected-case analysis. | Worst: max over all inputs; Average: expected over uniform random input; Expected: over random choices in randomized algorithm |
| 5 | How does recursion affect space complexity? | Each recursive call adds a frame to the call stack. Depth d → O(d) space. Can cause stack overflow for deep recursion |
| 6 | What is the space complexity of merge sort vs quicksort? | Merge sort: O(n) auxiliary for merging. Quicksort: O(log n) average for recursion stack, O(n) worst case |

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Design a system where you trade space for time at multiple layers. | Cache (Redis) for O(1) reads, database indexes (B+ tree) for O(log n) queries, CDN for edge caching, Bloom filter to avoid unnecessary DB lookups |
| 2 | How would you handle a cache stampede? | Lock per key (singleflight), probabilistic early expiration, or request coalescing. Each trades some complexity for reliability |
| 3 | Compare LSM tree vs B+ tree — when to choose which? | LSM: O(1) amortized writes, higher read amplification — write-heavy (Cassandra). B+: O(log n) balanced R/W — read-heavy (PostgreSQL) |
| 4 | How do Bloom filters trade space for accuracy? | m bits, k hash functions. No false negatives, but false positive rate ≈ (1-e^(-kn/m))^k. Tiny space for approximate membership |

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Prove amortized O(1) for dynamic array push using the potential method. | Φ(D) = 2·size - capacity. Amortized cost = actual + ΔΦ = 3 for both resize and non-resize cases |
| 2 | State and explain Savitch's theorem and its implications. | NSPACE(f(n)) ⊆ DSPACE(f(n)²). Nondeterministic space can be simulated deterministically with quadratic space blowup |
| 3 | Analyze the expected number of comparisons in randomized quicksort. | E[X] = Σ 2/(j-i+1) for all pairs. By linearity of expectation and harmonic sum, E[X] = 2n·Hₙ = O(n log n) |
| 4 | What is the time-space trade-off lower bound for comparison sorting? | Borodin-Cook: T(n) = Ω(n²/S(n)). With O(1) space → Ω(n²). With O(n) space → Ω(n), but comparison lower bound is Ω(n log n) |

---

## Coding Challenge: Optimize Two Sum

> Implement Two Sum in two ways: brute force O(n²) time O(1) space, and hash map O(n) time O(n) space. Both must return the indices of two numbers that add up to the target.

#### Go

```go
package main

import "fmt"

// O(n²) time, O(1) space
func twoSumBrute(nums []int, target int) (int, int) {
    for i := 0; i < len(nums); i++ {
        for j := i + 1; j < len(nums); j++ {
            if nums[i]+nums[j] == target {
                return i, j
            }
        }
    }
    return -1, -1
}

// O(n) time, O(n) space
func twoSumHash(nums []int, target int) (int, int) {
    seen := make(map[int]int) // value -> index
    for i, v := range nums {
        complement := target - v
        if j, ok := seen[complement]; ok {
            return j, i
        }
        seen[v] = i
    }
    return -1, -1
}

func main() {
    nums := []int{2, 7, 11, 15}
    target := 9

    i, j := twoSumBrute(nums, target)
    fmt.Printf("Brute: [%d, %d]\n", i, j) // [0, 1]

    i, j = twoSumHash(nums, target)
    fmt.Printf("Hash:  [%d, %d]\n", i, j) // [0, 1]
}
```

#### Java

```java
import java.util.HashMap;

public class TwoSum {
    // O(n²) time, O(1) space
    public static int[] twoSumBrute(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    // O(n) time, O(n) space
    public static int[] twoSumHash(int[] nums, int target) {
        HashMap<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;

        int[] r1 = twoSumBrute(nums, target);
        System.out.printf("Brute: [%d, %d]%n", r1[0], r1[1]);

        int[] r2 = twoSumHash(nums, target);
        System.out.printf("Hash:  [%d, %d]%n", r2[0], r2[1]);
    }
}
```

#### Python

```python
# O(n²) time, O(1) space
def two_sum_brute(nums, target):
    for i in range(len(nums)):
        for j in range(i + 1, len(nums)):
            if nums[i] + nums[j] == target:
                return [i, j]
    return [-1, -1]

# O(n) time, O(n) space
def two_sum_hash(nums, target):
    seen = {}  # value -> index
    for i, v in enumerate(nums):
        complement = target - v
        if complement in seen:
            return [seen[complement], i]
        seen[v] = i
    return [-1, -1]

nums = [2, 7, 11, 15]
target = 9
print("Brute:", two_sum_brute(nums, target))  # [0, 1]
print("Hash:", two_sum_hash(nums, target))     # [0, 1]
```

---

## Coding Challenge 2: Space-Optimized Fibonacci

> Implement Fibonacci three ways: naive recursive, memoized, and space-optimized iterative. Compare their time and space complexity.

#### Go

```go
package main

import "fmt"

// O(2^n) time, O(n) space (call stack)
func fibNaive(n int) int {
    if n <= 1 {
        return n
    }
    return fibNaive(n-1) + fibNaive(n-2)
}

// O(n) time, O(n) space (memo + call stack)
func fibMemo(n int, memo map[int]int) int {
    if n <= 1 {
        return n
    }
    if v, ok := memo[n]; ok {
        return v
    }
    memo[n] = fibMemo(n-1, memo) + fibMemo(n-2, memo)
    return memo[n]
}

// O(n) time, O(1) space
func fibOptimal(n int) int {
    if n <= 1 {
        return n
    }
    a, b := 0, 1
    for i := 2; i <= n; i++ {
        a, b = b, a+b
    }
    return b
}

func main() {
    n := 30
    fmt.Println("Naive:", fibNaive(n))
    fmt.Println("Memo:", fibMemo(n, map[int]int{}))
    fmt.Println("Optimal:", fibOptimal(n))
}
```

#### Java

```java
import java.util.HashMap;

public class Fibonacci {
    static int fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    static HashMap<Integer, Integer> memo = new HashMap<>();
    static int fibMemo(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        int result = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, result);
        return result;
    }

    static int fibOptimal(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    public static void main(String[] args) {
        int n = 30;
        System.out.println("Naive: " + fibNaive(n));
        System.out.println("Memo: " + fibMemo(n));
        System.out.println("Optimal: " + fibOptimal(n));
    }
}
```

#### Python

```python
from functools import lru_cache

# O(2^n) time, O(n) space
def fib_naive(n):
    if n <= 1:
        return n
    return fib_naive(n - 1) + fib_naive(n - 2)

# O(n) time, O(n) space
@lru_cache(maxsize=None)
def fib_memo(n):
    if n <= 1:
        return n
    return fib_memo(n - 1) + fib_memo(n - 2)

# O(n) time, O(1) space
def fib_optimal(n):
    if n <= 1:
        return n
    a, b = 0, 1
    for _ in range(2, n + 1):
        a, b = b, a + b
    return b

n = 30
print("Naive:", fib_naive(n))
print("Memo:", fib_memo(n))
print("Optimal:", fib_optimal(n))
```
