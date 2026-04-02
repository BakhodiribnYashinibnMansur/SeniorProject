# Polynomial Time O(n^2), O(n^3) -- Interview Preparation

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Problem-Solving Questions](#problem-solving-questions)
3. [Coding Challenge: Optimize O(n^2) to O(n log n)](#coding-challenge-optimize-on2-to-on-log-n)
4. [System Design Questions](#system-design-questions)
5. [Answers](#answers)

---

## Conceptual Questions

### Q1: Explain the difference between O(n^2) and O(n log n). When does it matter?

**Expected answer:** O(n^2) grows much faster than O(n log n). For n=10,000,
O(n^2) is 100,000,000 while O(n log n) is about 133,000. The difference matters
when n > ~1,000. For small n, the constant factors may dominate and O(n^2) can
even be faster.

### Q2: Name three O(n^2) sorting algorithms and explain when each is preferred.

**Expected answer:**
- **Bubble sort:** Simplest to implement; good for educational purposes. Adaptive
  (O(n) for nearly sorted input). Rarely used in production.
- **Selection sort:** Minimizes swaps (exactly n-1). Good when write operations
  are expensive.
- **Insertion sort:** Best for small arrays and nearly sorted data. Used as a
  subroutine in hybrid sorts (TimSort, IntroSort).

### Q3: Can you always reduce an O(n^2) algorithm to O(n log n)?

**Expected answer:** No. Some problems have proven or conjectured quadratic lower
bounds. For example, the 3SUM problem is conjectured to require Omega(n^2). Edit
distance has a conditional lower bound of Omega(n^2) under SETH. However, many
common O(n^2) algorithms (like brute force pair searching) can be reduced using
sorting, hashing, or divide and conquer.

### Q4: What is the time complexity of Floyd-Warshall? When is it preferred?

**Expected answer:** O(V^3) time, O(V^2) space. Preferred for all-pairs shortest
paths in dense graphs with small V (< 500). Handles negative edge weights (no
negative cycles). For sparse graphs or single-source, Dijkstra/Bellman-Ford is
better.

### Q5: Why is naive matrix multiplication O(n^3)?

**Expected answer:** For each of the n^2 entries in the result matrix, we compute
a dot product of two vectors of length n. Each dot product is O(n), and there are
n^2 of them, giving O(n^2 * n) = O(n^3).

---

## Problem-Solving Questions

### Q6: Given an array, find the maximum sum of any contiguous subarray.

**O(n^2) approach:** Check all subarrays.
**Optimal:** Kadane's algorithm in O(n).

```go
// Go -- O(n^2) brute force
func maxSubarraySumBrute(arr []int) int {
    n := len(arr)
    maxSum := arr[0]
    for i := 0; i < n; i++ {
        sum := 0
        for j := i; j < n; j++ {
            sum += arr[j]
            if sum > maxSum {
                maxSum = sum
            }
        }
    }
    return maxSum
}

// O(n) Kadane's algorithm
func maxSubarraySum(arr []int) int {
    maxSum := arr[0]
    currentSum := arr[0]
    for i := 1; i < len(arr); i++ {
        if currentSum < 0 {
            currentSum = arr[i]
        } else {
            currentSum += arr[i]
        }
        if currentSum > maxSum {
            maxSum = currentSum
        }
    }
    return maxSum
}
```

```java
// Java -- O(n^2) brute force
int maxSubarraySumBrute(int[] arr) {
    int n = arr.length;
    int maxSum = arr[0];
    for (int i = 0; i < n; i++) {
        int sum = 0;
        for (int j = i; j < n; j++) {
            sum += arr[j];
            maxSum = Math.max(maxSum, sum);
        }
    }
    return maxSum;
}

// O(n) Kadane's algorithm
int maxSubarraySum(int[] arr) {
    int maxSum = arr[0], currentSum = arr[0];
    for (int i = 1; i < arr.length; i++) {
        currentSum = Math.max(arr[i], currentSum + arr[i]);
        maxSum = Math.max(maxSum, currentSum);
    }
    return maxSum;
}
```

```python
# Python -- O(n^2) brute force
def max_subarray_sum_brute(arr):
    n = len(arr)
    max_sum = arr[0]
    for i in range(n):
        total = 0
        for j in range(i, n):
            total += arr[j]
            max_sum = max(max_sum, total)
    return max_sum

# O(n) Kadane's algorithm
def max_subarray_sum(arr):
    max_sum = current_sum = arr[0]
    for num in arr[1:]:
        current_sum = max(num, current_sum + num)
        max_sum = max(max_sum, current_sum)
    return max_sum
```

### Q7: Find all pairs in an array whose sum is less than a target. Return the count.

```go
// Go -- O(n^2) brute force
func countPairsBrute(arr []int, target int) int {
    count := 0
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i]+arr[j] < target {
                count++
            }
        }
    }
    return count
}

// O(n log n) with sorting + two pointers
func countPairs(arr []int, target int) int {
    sort.Ints(arr)
    count := 0
    left, right := 0, len(arr)-1
    for left < right {
        if arr[left]+arr[right] < target {
            count += right - left // All pairs (left, left+1..right) work
            left++
        } else {
            right--
        }
    }
    return count
}
```

```java
// Java -- O(n log n) optimized
int countPairs(int[] arr, int target) {
    Arrays.sort(arr);
    int count = 0, left = 0, right = arr.length - 1;
    while (left < right) {
        if (arr[left] + arr[right] < target) {
            count += right - left;
            left++;
        } else {
            right--;
        }
    }
    return count;
}
```

```python
# Python -- O(n log n) optimized
def count_pairs(arr, target):
    arr.sort()
    count = 0
    left, right = 0, len(arr) - 1
    while left < right:
        if arr[left] + arr[right] < target:
            count += right - left
            left += 1
        else:
            right -= 1
    return count
```

---

## Coding Challenge: Optimize O(n^2) to O(n log n)

### Problem: Count Inversions

An inversion is a pair (i, j) where i < j but arr[i] > arr[j]. The brute force
approach checks all pairs in O(n^2). Optimize it to O(n log n) using modified
merge sort.

### Brute Force O(n^2)

```go
// Go
func countInversionsBrute(arr []int) int {
    count := 0
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] > arr[j] {
                count++
            }
        }
    }
    return count
}
```

```java
// Java
int countInversionsBrute(int[] arr) {
    int count = 0;
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[i] > arr[j]) count++;
        }
    }
    return count;
}
```

```python
# Python
def count_inversions_brute(arr):
    count = 0
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] > arr[j]:
                count += 1
    return count
```

### Optimized O(n log n)

```go
// Go -- Merge sort based inversion counting
func countInversions(arr []int) ([]int, int) {
    if len(arr) <= 1 {
        return arr, 0
    }
    mid := len(arr) / 2
    left, leftInv := countInversions(arr[:mid])
    right, rightInv := countInversions(arr[mid:])

    merged := make([]int, 0, len(arr))
    inversions := leftInv + rightInv
    i, j := 0, 0

    for i < len(left) && j < len(right) {
        if left[i] <= right[j] {
            merged = append(merged, left[i])
            i++
        } else {
            merged = append(merged, right[j])
            inversions += len(left) - i
            j++
        }
    }
    merged = append(merged, left[i:]...)
    merged = append(merged, right[j:]...)
    return merged, inversions
}
```

```java
// Java -- Merge sort based inversion counting
int countInversions(int[] arr, int[] temp, int left, int right) {
    int inversions = 0;
    if (left < right) {
        int mid = left + (right - left) / 2;
        inversions += countInversions(arr, temp, left, mid);
        inversions += countInversions(arr, temp, mid + 1, right);
        inversions += merge(arr, temp, left, mid, right);
    }
    return inversions;
}

int merge(int[] arr, int[] temp, int left, int mid, int right) {
    int i = left, j = mid + 1, k = left, inversions = 0;
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) {
            temp[k++] = arr[i++];
        } else {
            temp[k++] = arr[j++];
            inversions += mid - i + 1;
        }
    }
    while (i <= mid) temp[k++] = arr[i++];
    while (j <= right) temp[k++] = arr[j++];
    System.arraycopy(temp, left, arr, left, right - left + 1);
    return inversions;
}
```

```python
# Python -- Merge sort based inversion counting
def count_inversions(arr):
    if len(arr) <= 1:
        return arr[:], 0

    mid = len(arr) // 2
    left, left_inv = count_inversions(arr[:mid])
    right, right_inv = count_inversions(arr[mid:])

    merged = []
    inversions = left_inv + right_inv
    i = j = 0

    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            merged.append(left[i])
            i += 1
        else:
            merged.append(right[j])
            inversions += len(left) - i
            j += 1

    merged.extend(left[i:])
    merged.extend(right[j:])
    return merged, inversions
```

**Key insight:** When merging, if right[j] < left[i], then right[j] is smaller
than ALL remaining elements in left (left[i], left[i+1], ..., left[mid]). So we
add `len(left) - i` inversions at once instead of counting them one by one.

---

## System Design Questions

### Q8: Design a recommendation system that computes item similarity.

**Discussion points:**
- Naive pairwise similarity is O(n^2) where n = number of items
- For n = 1M items, that is 10^12 comparisons -- infeasible
- Solutions: ANN (FAISS, Annoy), LSH, item embedding + vector search
- Precompute top-K similar items per item and cache
- Update incrementally, not from scratch

### Q9: Your database query is slow. Explain how a missing index can cause O(n^2).

**Discussion points:**
- Nested loop join without index: for each row in table A, scan all rows in table B
- With index: for each row in A, O(log n) lookup in B's index -> O(n log n)
- EXPLAIN ANALYZE to diagnose; add appropriate index
- Consider join order: smaller table as outer loop

### Q10: How would you handle a service where response time grows quadratically?

**Discussion points:**
- Profile to identify the quadratic code path
- Add input size limits/pagination
- Consider algorithmic optimization (hash maps, sorting, indexing)
- Add caching for repeated computations
- Monitor input size distribution to plan capacity

---

## Answers

Detailed answers for Q1-Q5 are provided inline above. For the coding challenges,
the key patterns to remember:

1. **Sorting + two pointers** reduces many pair problems from O(n^2) to O(n log n)
2. **Hash maps** reduce lookup-based problems from O(n^2) to O(n)
3. **Modified merge sort** solves counting problems (inversions, smaller elements)
   in O(n log n)
4. **Kadane's algorithm** reduces max subarray from O(n^2) to O(n)
5. **Prefix sums** reduce range query problems from O(n^2) to O(n) preprocessing + O(1) query
