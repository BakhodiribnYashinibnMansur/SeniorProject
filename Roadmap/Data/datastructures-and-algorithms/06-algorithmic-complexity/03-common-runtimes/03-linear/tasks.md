# Linear Time O(n) — Tasks

## Table of Contents

1. [Task 1: Linear Search](#task-1-linear-search)
2. [Task 2: Find Maximum Element](#task-2-find-maximum-element)
3. [Task 3: Count Elements Matching Condition](#task-3-count-elements)
4. [Task 4: Reverse an Array In-Place](#task-4-reverse-array)
5. [Task 5: Two Sum with Hash Map](#task-5-two-sum)
6. [Task 6: Remove Duplicates from Sorted Array](#task-6-remove-duplicates)
7. [Task 7: Move Zeroes to End](#task-7-move-zeroes)
8. [Task 8: Maximum Subarray Sum (Kadane's)](#task-8-kadane)
9. [Task 9: Fixed-Size Sliding Window Maximum Sum](#task-9-sliding-window)
10. [Task 10: Check Anagram](#task-10-anagram)
11. [Task 11: Prefix Sum Array](#task-11-prefix-sum)
12. [Task 12: Merge Two Sorted Arrays](#task-12-merge-sorted)
13. [Task 13: First Non-Repeating Character](#task-13-first-unique)
14. [Task 14: Product of Array Except Self](#task-14-product-except-self)
15. [Task 15: Counting Sort](#task-15-counting-sort)
16. [Benchmark Task](#benchmark-task)

---

## Task 1: Linear Search

**Difficulty:** Easy

**Description:** Implement a function that searches for a target value in an unsorted array and returns its index (or -1 if not found).

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1)
- Handle empty arrays

**Test Cases:**

| Input Array | Target | Expected Output |
|-------------|--------|-----------------|
| [5, 3, 7, 1, 9] | 7 | 2 |
| [5, 3, 7, 1, 9] | 4 | -1 |
| [] | 1 | -1 |
| [1] | 1 | 0 |
| [2, 2, 2, 2] | 2 | 0 (first occurrence) |

**Go Skeleton:**

```go
func linearSearch(arr []int, target int) int {
    // TODO: implement
    return -1
}
```

**Java Skeleton:**

```java
public static int linearSearch(int[] arr, int target) {
    // TODO: implement
    return -1;
}
```

**Python Skeleton:**

```python
def linear_search(arr: list[int], target: int) -> int:
    # TODO: implement
    pass
```

---

## Task 2: Find Maximum Element

**Difficulty:** Easy

**Description:** Find and return the maximum element in an array.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1)
- Handle single-element arrays
- Do not use built-in max functions

**Test Cases:**

| Input Array | Expected Output |
|-------------|-----------------|
| [3, 1, 4, 1, 5, 9, 2, 6] | 9 |
| [-5, -2, -8, -1] | -1 |
| [42] | 42 |
| [7, 7, 7, 7] | 7 |

**Go Skeleton:**

```go
func findMax(arr []int) int {
    // TODO: implement
    return 0
}
```

**Java Skeleton:**

```java
public static int findMax(int[] arr) {
    // TODO: implement
    return 0;
}
```

**Python Skeleton:**

```python
def find_max(arr: list[int]) -> int:
    # TODO: implement
    pass
```

---

## Task 3: Count Elements Matching Condition

**Difficulty:** Easy

**Description:** Count how many elements in an array are greater than a given threshold.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1)

**Test Cases:**

| Input Array | Threshold | Expected Output |
|-------------|-----------|-----------------|
| [1, 5, 3, 8, 2, 7] | 4 | 3 |
| [1, 2, 3] | 10 | 0 |
| [10, 20, 30] | 5 | 3 |
| [] | 0 | 0 |

**Go Skeleton:**

```go
func countAbove(arr []int, threshold int) int {
    // TODO: implement
    return 0
}
```

**Java Skeleton:**

```java
public static int countAbove(int[] arr, int threshold) {
    // TODO: implement
    return 0;
}
```

**Python Skeleton:**

```python
def count_above(arr: list[int], threshold: int) -> int:
    # TODO: implement
    pass
```

---

## Task 4: Reverse an Array In-Place

**Difficulty:** Easy

**Description:** Reverse the elements of an array in-place using the two-pointer technique.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1) — in-place, no extra array
- Modify the original array

**Test Cases:**

| Input Array | Expected Output |
|-------------|-----------------|
| [1, 2, 3, 4, 5] | [5, 4, 3, 2, 1] |
| [1, 2] | [2, 1] |
| [1] | [1] |
| [] | [] |

**Go Skeleton:**

```go
func reverseInPlace(arr []int) {
    // TODO: implement using two pointers
}
```

**Java Skeleton:**

```java
public static void reverseInPlace(int[] arr) {
    // TODO: implement using two pointers
}
```

**Python Skeleton:**

```python
def reverse_in_place(arr: list[int]) -> None:
    # TODO: implement using two pointers (do not use arr.reverse())
    pass
```

---

## Task 5: Two Sum with Hash Map

**Difficulty:** Easy-Medium

**Description:** Given an array and a target sum, find two indices whose values add up to the target. Return the pair of indices.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(n)
- Use a hash map/dictionary
- Each input has exactly one solution

**Test Cases:**

| Input Array | Target | Expected Output |
|-------------|--------|-----------------|
| [2, 7, 11, 15] | 9 | (0, 1) |
| [3, 2, 4] | 6 | (1, 2) |
| [3, 3] | 6 | (0, 1) |

**Go Skeleton:**

```go
func twoSum(nums []int, target int) (int, int) {
    // TODO: implement with map
    return -1, -1
}
```

**Java Skeleton:**

```java
public static int[] twoSum(int[] nums, int target) {
    // TODO: implement with HashMap
    return new int[]{-1, -1};
}
```

**Python Skeleton:**

```python
def two_sum(nums: list[int], target: int) -> tuple[int, int]:
    # TODO: implement with dict
    pass
```

---

## Task 6: Remove Duplicates from Sorted Array

**Difficulty:** Easy

**Description:** Remove duplicates from a sorted array in-place and return the new length. Elements beyond the new length do not matter.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1) — in-place
- Use two-pointer technique

**Test Cases:**

| Input Array | New Length | First N Elements |
|-------------|-----------|------------------|
| [1, 1, 2] | 2 | [1, 2] |
| [0, 0, 1, 1, 1, 2, 2, 3, 3, 4] | 5 | [0, 1, 2, 3, 4] |
| [1] | 1 | [1] |

**Go Skeleton:**

```go
func removeDuplicates(nums []int) int {
    // TODO: implement with two pointers
    return 0
}
```

**Java Skeleton:**

```java
public static int removeDuplicates(int[] nums) {
    // TODO: implement with two pointers
    return 0;
}
```

**Python Skeleton:**

```python
def remove_duplicates(nums: list[int]) -> int:
    # TODO: implement with two pointers
    pass
```

---

## Task 7: Move Zeroes to End

**Difficulty:** Easy

**Description:** Move all zeroes in an array to the end while maintaining the relative order of non-zero elements. Do this in-place.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1)
- Maintain relative order of non-zero elements

**Test Cases:**

| Input Array | Expected Output |
|-------------|-----------------|
| [0, 1, 0, 3, 12] | [1, 3, 12, 0, 0] |
| [0, 0, 0, 1] | [1, 0, 0, 0] |
| [1, 2, 3] | [1, 2, 3] |
| [0] | [0] |

**Go Skeleton:**

```go
func moveZeroes(nums []int) {
    // TODO: implement
}
```

**Java Skeleton:**

```java
public static void moveZeroes(int[] nums) {
    // TODO: implement
}
```

**Python Skeleton:**

```python
def move_zeroes(nums: list[int]) -> None:
    # TODO: implement in-place
    pass
```

---

## Task 8: Maximum Subarray Sum (Kadane's)

**Difficulty:** Medium

**Description:** Find the contiguous subarray with the largest sum and return that sum.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1)
- Handle arrays with all negative numbers

**Test Cases:**

| Input Array | Expected Output |
|-------------|-----------------|
| [-2, 1, -3, 4, -1, 2, 1, -5, 4] | 6 |
| [1] | 1 |
| [-1, -2, -3] | -1 |
| [5, 4, -1, 7, 8] | 23 |
| [-2, -1] | -1 |

**Go Skeleton:**

```go
func maxSubArray(nums []int) int {
    // TODO: implement Kadane's algorithm
    return 0
}
```

**Java Skeleton:**

```java
public static int maxSubArray(int[] nums) {
    // TODO: implement Kadane's algorithm
    return 0;
}
```

**Python Skeleton:**

```python
def max_sub_array(nums: list[int]) -> int:
    # TODO: implement Kadane's algorithm
    pass
```

---

## Task 9: Fixed-Size Sliding Window Maximum Sum

**Difficulty:** Medium

**Description:** Find the maximum sum of any contiguous subarray of exactly size k.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1)
- Use sliding window technique (not brute force)

**Test Cases:**

| Input Array | k | Expected Output |
|-------------|---|-----------------|
| [2, 1, 5, 1, 3, 2] | 3 | 9 |
| [1, 2, 3, 4, 5] | 2 | 9 |
| [5, -1, -2, 10] | 2 | 8 |
| [1] | 1 | 1 |

**Go Skeleton:**

```go
func maxSumWindow(arr []int, k int) int {
    // TODO: implement with sliding window
    return 0
}
```

**Java Skeleton:**

```java
public static int maxSumWindow(int[] arr, int k) {
    // TODO: implement with sliding window
    return 0;
}
```

**Python Skeleton:**

```python
def max_sum_window(arr: list[int], k: int) -> int:
    # TODO: implement with sliding window
    pass
```

---

## Task 10: Check Anagram

**Difficulty:** Easy-Medium

**Description:** Determine if two strings are anagrams of each other (contain exactly the same characters with the same frequencies).

**Requirements:**
- Time complexity: O(n) where n is the length of the strings
- Space complexity: O(1) — at most 26 character counts for lowercase English
- Case-sensitive comparison

**Test Cases:**

| String 1 | String 2 | Expected Output |
|-----------|----------|-----------------|
| "anagram" | "nagaram" | true |
| "rat" | "car" | false |
| "listen" | "silent" | true |
| "" | "" | true |
| "a" | "ab" | false |

**Go Skeleton:**

```go
func isAnagram(s, t string) bool {
    // TODO: implement using character counting
    return false
}
```

**Java Skeleton:**

```java
public static boolean isAnagram(String s, String t) {
    // TODO: implement using character counting
    return false;
}
```

**Python Skeleton:**

```python
def is_anagram(s: str, t: str) -> bool:
    # TODO: implement using character counting
    pass
```

---

## Task 11: Prefix Sum Array

**Difficulty:** Easy

**Description:** Given an array, compute its prefix sum array where `prefix[i] = arr[0] + arr[1] + ... + arr[i]`.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(n) for the output array

**Test Cases:**

| Input Array | Expected Output |
|-------------|-----------------|
| [1, 2, 3, 4, 5] | [1, 3, 6, 10, 15] |
| [5] | [5] |
| [1, -1, 1, -1] | [1, 0, 1, 0] |

**Go Skeleton:**

```go
func prefixSum(arr []int) []int {
    // TODO: implement
    return nil
}
```

**Java Skeleton:**

```java
public static int[] prefixSum(int[] arr) {
    // TODO: implement
    return null;
}
```

**Python Skeleton:**

```python
def prefix_sum(arr: list[int]) -> list[int]:
    # TODO: implement
    pass
```

---

## Task 12: Merge Two Sorted Arrays

**Difficulty:** Easy-Medium

**Description:** Given two sorted arrays, merge them into a single sorted array.

**Requirements:**
- Time complexity: O(n + m) where n and m are the sizes of the two arrays
- Space complexity: O(n + m) for the result
- Use two-pointer merge technique

**Test Cases:**

| Array 1 | Array 2 | Expected Output |
|---------|---------|-----------------|
| [1, 3, 5] | [2, 4, 6] | [1, 2, 3, 4, 5, 6] |
| [1, 2, 3] | [] | [1, 2, 3] |
| [] | [4, 5] | [4, 5] |
| [1, 1] | [1, 1] | [1, 1, 1, 1] |

**Go Skeleton:**

```go
func mergeSorted(a, b []int) []int {
    // TODO: implement with two pointers
    return nil
}
```

**Java Skeleton:**

```java
public static int[] mergeSorted(int[] a, int[] b) {
    // TODO: implement with two pointers
    return null;
}
```

**Python Skeleton:**

```python
def merge_sorted(a: list[int], b: list[int]) -> list[int]:
    # TODO: implement with two pointers
    pass
```

---

## Task 13: First Non-Repeating Character

**Difficulty:** Medium

**Description:** Find the index of the first character in a string that does not repeat. Return -1 if every character repeats.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1) — at most 26 characters
- Two-pass approach: count frequencies, then find first with count 1

**Test Cases:**

| Input String | Expected Output |
|-------------|-----------------|
| "leetcode" | 0 (character 'l') |
| "loveleetcode" | 2 (character 'v') |
| "aabb" | -1 |
| "z" | 0 |

**Go Skeleton:**

```go
func firstUniqChar(s string) int {
    // TODO: implement
    return -1
}
```

**Java Skeleton:**

```java
public static int firstUniqChar(String s) {
    // TODO: implement
    return -1;
}
```

**Python Skeleton:**

```python
def first_uniq_char(s: str) -> int:
    # TODO: implement
    pass
```

---

## Task 14: Product of Array Except Self

**Difficulty:** Medium-Hard

**Description:** Given an array `nums`, return an array where `output[i]` is the product of all elements except `nums[i]`. Do not use division.

**Requirements:**
- Time complexity: O(n)
- Space complexity: O(1) extra (output array does not count)
- Do NOT use division

**Hint:** Use prefix products (left pass) and suffix products (right pass).

**Test Cases:**

| Input Array | Expected Output |
|-------------|-----------------|
| [1, 2, 3, 4] | [24, 12, 8, 6] |
| [-1, 1, 0, -3, 3] | [0, 0, 9, 0, 0] |
| [2, 3] | [3, 2] |

**Go Skeleton:**

```go
func productExceptSelf(nums []int) []int {
    // TODO: implement with prefix/suffix products
    return nil
}
```

**Java Skeleton:**

```java
public static int[] productExceptSelf(int[] nums) {
    // TODO: implement with prefix/suffix products
    return null;
}
```

**Python Skeleton:**

```python
def product_except_self(nums: list[int]) -> list[int]:
    # TODO: implement with prefix/suffix products
    pass
```

---

## Task 15: Counting Sort

**Difficulty:** Medium

**Description:** Implement counting sort for an array of non-negative integers with a known maximum value.

**Requirements:**
- Time complexity: O(n + k) where k is the maximum value
- Space complexity: O(k)
- Stable sort (preserve relative order of equal elements)

**Test Cases:**

| Input Array | Max Value | Expected Output |
|-------------|-----------|-----------------|
| [4, 2, 2, 8, 3, 3, 1] | 8 | [1, 2, 2, 3, 3, 4, 8] |
| [1, 1, 1] | 1 | [1, 1, 1] |
| [3, 0, 1, 2] | 3 | [0, 1, 2, 3] |
| [] | 0 | [] |

**Go Skeleton:**

```go
func countingSort(arr []int, maxVal int) []int {
    // TODO: implement
    return nil
}
```

**Java Skeleton:**

```java
public static int[] countingSort(int[] arr, int maxVal) {
    // TODO: implement
    return null;
}
```

**Python Skeleton:**

```python
def counting_sort(arr: list[int], max_val: int) -> list[int]:
    # TODO: implement
    pass
```

---

## Benchmark Task

**Objective:** Compare the performance of O(n) vs O(n^2) approaches for finding if an array contains duplicates.

**Instructions:**

1. Implement the brute-force O(n^2) approach (nested loops).
2. Implement the O(n) approach (hash set).
3. Run both on arrays of sizes: 1,000 / 10,000 / 100,000 / 1,000,000.
4. Record the execution time for each.
5. Plot or print the results.

**Go Benchmark:**

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

// O(n^2) brute force
func hasDuplicateBrute(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] == arr[j] {
                return true
            }
        }
    }
    return false
}

// O(n) hash set
func hasDuplicateHash(arr []int) bool {
    seen := make(map[int]bool)
    for _, v := range arr {
        if seen[v] {
            return true
        }
        seen[v] = true
    }
    return false
}

func benchmark(name string, f func([]int) bool, arr []int) time.Duration {
    start := time.Now()
    f(arr)
    elapsed := time.Since(start)
    fmt.Printf("  %-12s n=%-10d time=%v\n", name, len(arr), elapsed)
    return elapsed
}

func main() {
    sizes := []int{1_000, 10_000, 100_000, 1_000_000}

    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr {
            arr[i] = rand.Intn(n * 10) // reduce collision chance
        }

        fmt.Printf("--- n = %d ---\n", n)
        if n <= 100_000 { // Skip brute force for 1M (too slow)
            benchmark("Brute O(n²)", hasDuplicateBrute, arr)
        }
        benchmark("Hash O(n)", hasDuplicateHash, arr)
    }
}
```

**Java Benchmark:**

```java
import java.util.*;

public class DuplicateBenchmark {

    public static boolean hasDuplicateBrute(int[] arr) {
        for (int i = 0; i < arr.length; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] == arr[j]) return true;
        return false;
    }

    public static boolean hasDuplicateHash(int[] arr) {
        Set<Integer> seen = new HashSet<>();
        for (int v : arr) {
            if (!seen.add(v)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int[] sizes = {1_000, 10_000, 100_000, 1_000_000};
        Random rand = new Random();

        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rand.nextInt(n * 10);

            System.out.printf("--- n = %d ---%n", n);
            if (n <= 100_000) {
                long start = System.nanoTime();
                hasDuplicateBrute(arr);
                System.out.printf("  Brute O(n^2): %d ms%n", (System.nanoTime() - start) / 1_000_000);
            }
            long start = System.nanoTime();
            hasDuplicateHash(arr);
            System.out.printf("  Hash O(n):    %d ms%n", (System.nanoTime() - start) / 1_000_000);
        }
    }
}
```

**Python Benchmark:**

```python
import random
import time

def has_duplicate_brute(arr: list[int]) -> bool:
    """O(n^2) brute force."""
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False

def has_duplicate_hash(arr: list[int]) -> bool:
    """O(n) hash set."""
    seen = set()
    for v in arr:
        if v in seen:
            return True
        seen.add(v)
    return False

def benchmark(name: str, func, arr: list[int]):
    start = time.perf_counter()
    func(arr)
    elapsed = time.perf_counter() - start
    print(f"  {name:<15} n={len(arr):<10} time={elapsed:.4f}s")

if __name__ == "__main__":
    sizes = [1_000, 10_000, 100_000, 1_000_000]
    for n in sizes:
        arr = [random.randint(0, n * 10) for _ in range(n)]
        print(f"--- n = {n} ---")
        if n <= 100_000:
            benchmark("Brute O(n^2)", has_duplicate_brute, arr)
        benchmark("Hash O(n)", has_duplicate_hash, arr)
```

**Expected Results Pattern:**

| n | Brute O(n^2) | Hash O(n) | Speedup |
|---|-------------|-----------|---------|
| 1,000 | ~0.5 ms | ~0.05 ms | ~10x |
| 10,000 | ~50 ms | ~0.5 ms | ~100x |
| 100,000 | ~5,000 ms | ~5 ms | ~1000x |
| 1,000,000 | Too slow | ~50 ms | - |

The benchmark clearly demonstrates how O(n^2) becomes impractical as n grows, while O(n) remains fast even for millions of elements.
