# Linear Time O(n) — Middle Level

## Table of Contents

1. [Overview](#overview)
2. [Single-Pass Algorithm Patterns](#single-pass-algorithm-patterns)
3. [Two-Pointer Technique](#two-pointer-technique)
   - [Opposite Direction Pointers](#opposite-direction-pointers)
   - [Same Direction Pointers (Fast/Slow)](#same-direction-pointers)
4. [Sliding Window](#sliding-window)
   - [Fixed-Size Window](#fixed-size-window)
   - [Variable-Size Window](#variable-size-window)
5. [Kadane's Algorithm](#kadanes-algorithm)
6. [Counting Sort — O(n + k)](#counting-sort)
7. [Hash Map for O(n) Solutions](#hash-map-for-on-solutions)
8. [Comparison: O(n) vs O(n log n)](#comparison-on-vs-on-log-n)
9. [When O(n) is the Lower Bound](#when-on-is-the-lower-bound)
10. [Practice Problems](#practice-problems)
11. [Key Takeaways](#key-takeaways)
12. [References](#references)

---

## Overview

At the middle level, the challenge is not just recognizing O(n) algorithms but **designing** them. Many problems that appear to require O(n^2) or O(n log n) can be solved in O(n) using specific techniques: two pointers, sliding window, hash maps, and clever single-pass strategies.

This document covers the core techniques that enable linear-time solutions to a wide range of problems.

---

## Single-Pass Algorithm Patterns

A single-pass algorithm traverses the input once, maintaining state in variables or auxiliary data structures. The key insight: **compute the answer incrementally** as you see each element.

### Pattern: Running Aggregation

**Go:**

```go
package main

import "fmt"

// runningStats computes count, sum, min, max, and average in one pass.
func runningStats(arr []int) (count int, sum int, min int, max int, avg float64) {
    if len(arr) == 0 {
        return
    }
    min, max = arr[0], arr[0]
    for _, v := range arr {
        count++
        sum += v
        if v < min {
            min = v
        }
        if v > max {
            max = v
        }
    }
    avg = float64(sum) / float64(count)
    return
}

func main() {
    arr := []int{4, 7, 2, 9, 1, 5, 8, 3}
    count, sum, min, max, avg := runningStats(arr)
    fmt.Printf("Count=%d Sum=%d Min=%d Max=%d Avg=%.2f\n", count, sum, min, max, avg)
}
```

**Java:**

```java
public class RunningStats {

    public static void main(String[] args) {
        int[] arr = {4, 7, 2, 9, 1, 5, 8, 3};

        int count = 0, sum = 0;
        int min = arr[0], max = arr[0];

        for (int v : arr) {
            count++;
            sum += v;
            if (v < min) min = v;
            if (v > max) max = v;
        }

        double avg = (double) sum / count;
        System.out.printf("Count=%d Sum=%d Min=%d Max=%d Avg=%.2f%n",
                          count, sum, min, max, avg);
    }
}
```

**Python:**

```python
def running_stats(arr: list[int]) -> dict:
    """Compute count, sum, min, max, avg in a single pass. O(n)."""
    if not arr:
        return {}
    count = 0
    total = 0
    min_val = arr[0]
    max_val = arr[0]
    for v in arr:
        count += 1
        total += v
        if v < min_val:
            min_val = v
        if v > max_val:
            max_val = v
    return {
        "count": count,
        "sum": total,
        "min": min_val,
        "max": max_val,
        "avg": total / count,
    }


if __name__ == "__main__":
    arr = [4, 7, 2, 9, 1, 5, 8, 3]
    stats = running_stats(arr)
    print(stats)
```

---

## Two-Pointer Technique

The two-pointer technique uses two indices that move through the data, often converting O(n^2) brute-force into O(n).

### Opposite Direction Pointers

Pointers start at opposite ends and move toward each other.

**Example: Check if a string is a palindrome.**

**Go:**

```go
package main

import "fmt"

func isPalindrome(s string) bool {
    left, right := 0, len(s)-1
    for left < right {
        if s[left] != s[right] {
            return false
        }
        left++
        right--
    }
    return true
}

func main() {
    words := []string{"racecar", "hello", "madam", "world"}
    for _, w := range words {
        fmt.Printf("%s -> palindrome: %v\n", w, isPalindrome(w))
    }
}
```

**Java:**

```java
public class Palindrome {

    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        String[] words = {"racecar", "hello", "madam", "world"};
        for (String w : words) {
            System.out.printf("%s -> palindrome: %b%n", w, isPalindrome(w));
        }
    }
}
```

**Python:**

```python
def is_palindrome(s: str) -> bool:
    """Check if string is a palindrome using two pointers. O(n)."""
    left, right = 0, len(s) - 1
    while left < right:
        if s[left] != s[right]:
            return False
        left += 1
        right -= 1
    return True


if __name__ == "__main__":
    for word in ["racecar", "hello", "madam", "world"]:
        print(f"{word} -> palindrome: {is_palindrome(word)}")
```

**Example: Two Sum on a sorted array.**

**Go:**

```go
package main

import "fmt"

// twoSumSorted finds two numbers in a sorted array that sum to target.
// Returns their 0-based indices, or (-1, -1) if not found.
func twoSumSorted(arr []int, target int) (int, int) {
    left, right := 0, len(arr)-1
    for left < right {
        sum := arr[left] + arr[right]
        if sum == target {
            return left, right
        } else if sum < target {
            left++
        } else {
            right--
        }
    }
    return -1, -1
}

func main() {
    arr := []int{1, 3, 5, 7, 9, 11}
    target := 12
    i, j := twoSumSorted(arr, target)
    fmt.Printf("Indices: %d, %d -> %d + %d = %d\n", i, j, arr[i], arr[j], target)
}
```

**Java:**

```java
public class TwoSumSorted {

    public static int[] twoSumSorted(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int sum = arr[left] + arr[right];
            if (sum == target) {
                return new int[]{left, right};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 7, 9, 11};
        int target = 12;
        int[] result = twoSumSorted(arr, target);
        System.out.printf("Indices: %d, %d%n", result[0], result[1]);
    }
}
```

**Python:**

```python
def two_sum_sorted(arr: list[int], target: int) -> tuple[int, int]:
    """Find two indices in a sorted array whose values sum to target. O(n)."""
    left, right = 0, len(arr) - 1
    while left < right:
        s = arr[left] + arr[right]
        if s == target:
            return (left, right)
        elif s < target:
            left += 1
        else:
            right -= 1
    return (-1, -1)


if __name__ == "__main__":
    arr = [1, 3, 5, 7, 9, 11]
    print(two_sum_sorted(arr, 12))  # (2, 4) -> 5 + 7 nope, (1, 5) -> 3+11=14 nope, (2, 4) -> 5+7=12 yes? Actually: (0,5)=12
```

### Same Direction Pointers

Both pointers start at the same end. The fast pointer explores ahead; the slow pointer marks a boundary.

**Example: Remove duplicates from a sorted array in-place.**

**Go:**

```go
package main

import "fmt"

// removeDuplicates removes duplicates in-place and returns the new length.
func removeDuplicates(arr []int) int {
    if len(arr) == 0 {
        return 0
    }
    slow := 0
    for fast := 1; fast < len(arr); fast++ {
        if arr[fast] != arr[slow] {
            slow++
            arr[slow] = arr[fast]
        }
    }
    return slow + 1
}

func main() {
    arr := []int{1, 1, 2, 2, 3, 4, 4, 5}
    newLen := removeDuplicates(arr)
    fmt.Printf("Unique elements: %v\n", arr[:newLen])
}
```

**Java:**

```java
import java.util.Arrays;

public class RemoveDuplicates {

    public static int removeDuplicates(int[] arr) {
        if (arr.length == 0) return 0;
        int slow = 0;
        for (int fast = 1; fast < arr.length; fast++) {
            if (arr[fast] != arr[slow]) {
                slow++;
                arr[slow] = arr[fast];
            }
        }
        return slow + 1;
    }

    public static void main(String[] args) {
        int[] arr = {1, 1, 2, 2, 3, 4, 4, 5};
        int newLen = removeDuplicates(arr);
        System.out.println(Arrays.toString(Arrays.copyOf(arr, newLen)));
    }
}
```

**Python:**

```python
def remove_duplicates(arr: list[int]) -> int:
    """Remove duplicates from sorted array in-place. Returns new length. O(n)."""
    if not arr:
        return 0
    slow = 0
    for fast in range(1, len(arr)):
        if arr[fast] != arr[slow]:
            slow += 1
            arr[slow] = arr[fast]
    return slow + 1


if __name__ == "__main__":
    arr = [1, 1, 2, 2, 3, 4, 4, 5]
    new_len = remove_duplicates(arr)
    print(f"Unique elements: {arr[:new_len]}")
```

---

## Sliding Window

The sliding window technique maintains a "window" over a contiguous subarray, sliding it across the input.

### Fixed-Size Window

**Example: Maximum sum of any subarray of size k.**

**Go:**

```go
package main

import "fmt"

// maxSumSubarray finds the maximum sum of any contiguous subarray of size k.
func maxSumSubarray(arr []int, k int) int {
    if len(arr) < k {
        return 0
    }

    // Compute sum of first window
    windowSum := 0
    for i := 0; i < k; i++ {
        windowSum += arr[i]
    }
    maxSum := windowSum

    // Slide the window: add the next element, remove the first of previous window
    for i := k; i < len(arr); i++ {
        windowSum += arr[i] - arr[i-k]
        if windowSum > maxSum {
            maxSum = windowSum
        }
    }
    return maxSum
}

func main() {
    arr := []int{2, 1, 5, 1, 3, 2}
    k := 3
    fmt.Printf("Max sum of subarray of size %d: %d\n", k, maxSumSubarray(arr, k))
}
```

**Java:**

```java
public class SlidingWindowFixed {

    public static int maxSumSubarray(int[] arr, int k) {
        if (arr.length < k) return 0;

        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }
        int maxSum = windowSum;

        for (int i = k; i < arr.length; i++) {
            windowSum += arr[i] - arr[i - k];
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum;
    }

    public static void main(String[] args) {
        int[] arr = {2, 1, 5, 1, 3, 2};
        int k = 3;
        System.out.printf("Max sum of subarray of size %d: %d%n", k, maxSumSubarray(arr, k));
    }
}
```

**Python:**

```python
def max_sum_subarray(arr: list[int], k: int) -> int:
    """Maximum sum of any contiguous subarray of size k. O(n)."""
    if len(arr) < k:
        return 0

    window_sum = sum(arr[:k])
    max_sum = window_sum

    for i in range(k, len(arr)):
        window_sum += arr[i] - arr[i - k]
        max_sum = max(max_sum, window_sum)

    return max_sum


if __name__ == "__main__":
    arr = [2, 1, 5, 1, 3, 2]
    print(f"Max sum of subarray of size 3: {max_sum_subarray(arr, 3)}")
```

### Variable-Size Window

**Example: Smallest subarray with sum >= target.**

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// minSubarrayLen returns the length of the smallest contiguous subarray
// with sum >= target, or 0 if no such subarray exists.
func minSubarrayLen(target int, arr []int) int {
    minLen := math.MaxInt64
    windowSum := 0
    left := 0

    for right := 0; right < len(arr); right++ {
        windowSum += arr[right]
        for windowSum >= target {
            length := right - left + 1
            if length < minLen {
                minLen = length
            }
            windowSum -= arr[left]
            left++
        }
    }

    if minLen == math.MaxInt64 {
        return 0
    }
    return minLen
}

func main() {
    arr := []int{2, 3, 1, 2, 4, 3}
    target := 7
    fmt.Printf("Min subarray length with sum >= %d: %d\n", target, minSubarrayLen(target, arr))
}
```

**Java:**

```java
public class SlidingWindowVariable {

    public static int minSubarrayLen(int target, int[] arr) {
        int minLen = Integer.MAX_VALUE;
        int windowSum = 0;
        int left = 0;

        for (int right = 0; right < arr.length; right++) {
            windowSum += arr[right];
            while (windowSum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                windowSum -= arr[left];
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    public static void main(String[] args) {
        int[] arr = {2, 3, 1, 2, 4, 3};
        int target = 7;
        System.out.printf("Min subarray length with sum >= %d: %d%n",
                          target, minSubarrayLen(target, arr));
    }
}
```

**Python:**

```python
def min_subarray_len(target: int, arr: list[int]) -> int:
    """Smallest subarray with sum >= target. O(n) amortized."""
    min_len = float("inf")
    window_sum = 0
    left = 0

    for right in range(len(arr)):
        window_sum += arr[right]
        while window_sum >= target:
            min_len = min(min_len, right - left + 1)
            window_sum -= arr[left]
            left += 1

    return 0 if min_len == float("inf") else min_len


if __name__ == "__main__":
    arr = [2, 3, 1, 2, 4, 3]
    print(f"Min subarray length with sum >= 7: {min_subarray_len(7, arr)}")
```

**Why is variable-size sliding window O(n)?** Each element is added to the window at most once (when `right` advances) and removed from the window at most once (when `left` advances). So the total number of operations is at most 2n = O(n).

---

## Kadane's Algorithm

Kadane's algorithm finds the **maximum subarray sum** in O(n). It is a classic example of dynamic programming reduced to a single pass.

**Core idea:** At each position, decide whether to extend the current subarray or start a new one.

```
current_max = max(arr[i], current_max + arr[i])
global_max  = max(global_max, current_max)
```

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// maxSubarraySum returns the maximum sum of any contiguous subarray.
func maxSubarraySum(arr []int) int {
    if len(arr) == 0 {
        return 0
    }
    currentMax := arr[0]
    globalMax := arr[0]

    for i := 1; i < len(arr); i++ {
        if currentMax+arr[i] > arr[i] {
            currentMax = currentMax + arr[i]
        } else {
            currentMax = arr[i]
        }
        if currentMax > globalMax {
            globalMax = currentMax
        }
    }
    return globalMax
}

// maxSubarrayRange also returns the start and end indices.
func maxSubarrayRange(arr []int) (int, int, int) {
    if len(arr) == 0 {
        return 0, 0, 0
    }
    currentMax := arr[0]
    globalMax := arr[0]
    start, end, tempStart := 0, 0, 0

    for i := 1; i < len(arr); i++ {
        if arr[i] > currentMax+arr[i] {
            currentMax = arr[i]
            tempStart = i
        } else {
            currentMax = currentMax + arr[i]
        }
        if currentMax > globalMax {
            globalMax = currentMax
            start = tempStart
            end = i
        }
    }
    return globalMax, start, end
}

func main() {
    arr := []int{-2, 1, -3, 4, -1, 2, 1, -5, 4}
    fmt.Printf("Max subarray sum: %d\n", maxSubarraySum(arr))

    sum, s, e := maxSubarrayRange(arr)
    fmt.Printf("Max subarray sum: %d, from index %d to %d -> %v\n",
        sum, s, e, arr[s:e+1])
}
```

**Java:**

```java
import java.util.Arrays;

public class Kadane {

    public static int maxSubarraySum(int[] arr) {
        int currentMax = arr[0];
        int globalMax = arr[0];

        for (int i = 1; i < arr.length; i++) {
            currentMax = Math.max(arr[i], currentMax + arr[i]);
            globalMax = Math.max(globalMax, currentMax);
        }
        return globalMax;
    }

    public static int[] maxSubarrayRange(int[] arr) {
        int currentMax = arr[0], globalMax = arr[0];
        int start = 0, end = 0, tempStart = 0;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > currentMax + arr[i]) {
                currentMax = arr[i];
                tempStart = i;
            } else {
                currentMax += arr[i];
            }
            if (currentMax > globalMax) {
                globalMax = currentMax;
                start = tempStart;
                end = i;
            }
        }
        return new int[]{globalMax, start, end};
    }

    public static void main(String[] args) {
        int[] arr = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Max subarray sum: " + maxSubarraySum(arr));

        int[] result = maxSubarrayRange(arr);
        System.out.printf("Sum=%d, from %d to %d -> %s%n",
            result[0], result[1], result[2],
            Arrays.toString(Arrays.copyOfRange(arr, result[1], result[2] + 1)));
    }
}
```

**Python:**

```python
def max_subarray_sum(arr: list[int]) -> int:
    """Kadane's algorithm: maximum subarray sum in O(n)."""
    current_max = arr[0]
    global_max = arr[0]

    for i in range(1, len(arr)):
        current_max = max(arr[i], current_max + arr[i])
        global_max = max(global_max, current_max)

    return global_max


def max_subarray_range(arr: list[int]) -> tuple[int, int, int]:
    """Returns (max_sum, start_index, end_index)."""
    current_max = arr[0]
    global_max = arr[0]
    start = end = temp_start = 0

    for i in range(1, len(arr)):
        if arr[i] > current_max + arr[i]:
            current_max = arr[i]
            temp_start = i
        else:
            current_max += arr[i]
        if current_max > global_max:
            global_max = current_max
            start = temp_start
            end = i

    return global_max, start, end


if __name__ == "__main__":
    arr = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
    print(f"Max subarray sum: {max_subarray_sum(arr)}")
    total, s, e = max_subarray_range(arr)
    print(f"Sum={total}, subarray={arr[s:e+1]}")
```

---

## Counting Sort

Counting sort achieves O(n + k) time where k is the range of input values. When k = O(n), this is linear.

**Go:**

```go
package main

import "fmt"

// countingSort sorts an array of non-negative integers with values in [0, maxVal].
func countingSort(arr []int, maxVal int) []int {
    count := make([]int, maxVal+1)
    for _, v := range arr {
        count[v]++
    }

    sorted := make([]int, 0, len(arr))
    for i, c := range count {
        for j := 0; j < c; j++ {
            sorted = append(sorted, i)
        }
    }
    return sorted
}

func main() {
    arr := []int{4, 2, 2, 8, 3, 3, 1}
    fmt.Printf("Original: %v\n", arr)
    fmt.Printf("Sorted:   %v\n", countingSort(arr, 8))
}
```

**Java:**

```java
import java.util.Arrays;

public class CountingSort {

    public static int[] countingSort(int[] arr, int maxVal) {
        int[] count = new int[maxVal + 1];
        for (int v : arr) {
            count[v]++;
        }

        int[] sorted = new int[arr.length];
        int idx = 0;
        for (int i = 0; i <= maxVal; i++) {
            for (int j = 0; j < count[i]; j++) {
                sorted[idx++] = i;
            }
        }
        return sorted;
    }

    public static void main(String[] args) {
        int[] arr = {4, 2, 2, 8, 3, 3, 1};
        System.out.println("Original: " + Arrays.toString(arr));
        System.out.println("Sorted:   " + Arrays.toString(countingSort(arr, 8)));
    }
}
```

**Python:**

```python
def counting_sort(arr: list[int], max_val: int) -> list[int]:
    """Counting sort for non-negative integers. O(n + k)."""
    count = [0] * (max_val + 1)
    for v in arr:
        count[v] += 1

    sorted_arr = []
    for i, c in enumerate(count):
        sorted_arr.extend([i] * c)
    return sorted_arr


if __name__ == "__main__":
    arr = [4, 2, 2, 8, 3, 3, 1]
    print(f"Original: {arr}")
    print(f"Sorted:   {counting_sort(arr, 8)}")
```

---

## Hash Map for O(n) Solutions

Hash maps enable O(1) average-time lookups, turning many O(n^2) problems into O(n).

**Example: Two Sum (unsorted array)**

**Go:**

```go
package main

import "fmt"

// twoSum returns indices of two numbers that add up to target.
func twoSum(arr []int, target int) (int, int) {
    seen := make(map[int]int) // value -> index
    for i, v := range arr {
        complement := target - v
        if j, ok := seen[complement]; ok {
            return j, i
        }
        seen[v] = i
    }
    return -1, -1
}

func main() {
    arr := []int{2, 7, 11, 15}
    i, j := twoSum(arr, 9)
    fmt.Printf("Indices: %d, %d\n", i, j) // 0, 1
}
```

**Java:**

```java
import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    public static int[] twoSum(int[] arr, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            int complement = target - arr[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(arr[i], i);
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] arr = {2, 7, 11, 15};
        int[] result = twoSum(arr, 9);
        System.out.printf("Indices: %d, %d%n", result[0], result[1]);
    }
}
```

**Python:**

```python
def two_sum(arr: list[int], target: int) -> tuple[int, int]:
    """Find two indices whose values sum to target. O(n)."""
    seen = {}  # value -> index
    for i, v in enumerate(arr):
        complement = target - v
        if complement in seen:
            return (seen[complement], i)
        seen[v] = i
    return (-1, -1)


if __name__ == "__main__":
    arr = [2, 7, 11, 15]
    print(two_sum(arr, 9))  # (0, 1)
```

---

## Comparison: O(n) vs O(n log n)

| Aspect                | O(n)                              | O(n log n)                        |
|-----------------------|-----------------------------------|-----------------------------------|
| Growth rate           | Linear                            | Slightly super-linear             |
| n = 1,000,000         | 1,000,000 ops                     | ~20,000,000 ops                   |
| n = 1,000,000,000     | 1 billion ops                     | ~30 billion ops                   |
| Typical algorithms    | Linear search, counting sort      | Merge sort, heap sort             |
| When to prefer O(n)   | Always, if achievable             | When sorting is required          |
| Space trade-off       | Often uses O(n) extra space       | Can be O(1) extra (heap sort)     |

**Rule of thumb:** An O(n) algorithm with a large constant factor can be slower than an O(n log n) algorithm for small inputs. For n > ~10,000, the asymptotic advantage of O(n) dominates.

---

## When O(n) is the Lower Bound

For some problems, O(n) is a **proven lower bound** — no algorithm can do better:

1. **Finding the maximum/minimum of unsorted data:** An adversary argument shows that any algorithm must examine all n elements. If even one element is unexamined, the adversary can set it to be the extreme value.

2. **Searching unsorted data:** Without any structure (no sorting, no indexing), you must look at every element to determine that a target is absent.

3. **Computing functions of all elements:** Sum, product, or any function requiring all input values needs at least n reads.

4. **Verifying a permutation:** To confirm that an array is a permutation of another, both arrays must be fully examined.

---

## Practice Problems

| # | Problem                                      | Technique          | Difficulty |
|---|----------------------------------------------|--------------------|------------|
| 1 | Maximum subarray sum                         | Kadane's           | Medium     |
| 2 | Two Sum (unsorted)                           | Hash map           | Easy       |
| 3 | Longest substring without repeating chars    | Sliding window     | Medium     |
| 4 | Remove duplicates from sorted array          | Two pointers       | Easy       |
| 5 | Move zeroes to end                           | Two pointers       | Easy       |
| 6 | Product of array except self                 | Prefix/suffix      | Medium     |
| 7 | Minimum size subarray sum                    | Sliding window     | Medium     |
| 8 | Container with most water                    | Two pointers       | Medium     |
| 9 | First missing positive                       | Array manipulation | Hard       |
| 10| Trapping rain water                          | Two pointers       | Hard       |

---

## Key Takeaways

1. **Two-pointer technique** converts O(n^2) paired comparisons into O(n) by exploiting sorted order or structural invariants.

2. **Sliding window** maintains a running computation over a subarray, avoiding redundant recalculation.

3. **Kadane's algorithm** is the canonical single-pass DP solution for maximum subarray sum.

4. **Counting sort** achieves O(n + k) by avoiding comparisons — linear when the range k is proportional to n.

5. **Hash maps** turn O(n) lookups into O(1), enabling single-pass O(n) solutions for problems like Two Sum.

6. **Variable-size sliding window is O(n)** because each element enters and leaves the window at most once.

7. **O(n) is provably optimal** for problems requiring examination of all elements.

---

## References

- Cormen, T. H., et al. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press. Chapters 8 (Counting Sort), 9 (Selection).
- Sedgewick, R., & Wayne, K. (2011). *Algorithms* (4th ed.). Addison-Wesley.
- Bentley, J. (1986). *Programming Pearls*. Addison-Wesley. Column 8: Algorithm Design Techniques (Kadane's algorithm).
- LeetCode Problem Set: https://leetcode.com/
