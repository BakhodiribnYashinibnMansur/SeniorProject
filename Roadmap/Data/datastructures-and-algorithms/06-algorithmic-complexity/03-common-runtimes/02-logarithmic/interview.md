# Logarithmic Time O(log n) — Interview Questions

## Table of Contents

- [Conceptual Questions](#conceptual-questions)
- [Problem-Solving Questions](#problem-solving-questions)
- [Coding Challenge: Robust Binary Search](#coding-challenge-robust-binary-search)
- [Follow-Up Discussion Points](#follow-up-discussion-points)

---

## Conceptual Questions

### Q1: What does O(log n) mean in plain terms?

**Expected Answer:**
O(log n) means the algorithm's running time grows proportionally to the logarithm of the input
size. Practically, each step eliminates a constant fraction (usually half) of the remaining work.
If you double the input, you need only one additional step. For a billion elements, you need
about 30 steps.

### Q2: Why does it not matter which log base we use in Big-O?

**Expected Answer:**
All logarithm bases differ by a constant factor: log_a(n) = log_b(n) / log_b(a). Since Big-O
ignores constant factors, O(log₂ n) = O(log₁₀ n) = O(ln n). However, the base matters for
practical performance, e.g., B-trees with high branching factors have smaller heights.

### Q3: Name three algorithms or data structures with O(log n) complexity.

**Expected Answer (any three):**
- Binary search on a sorted array
- Search/insert/delete in a balanced BST (AVL, Red-Black)
- B-tree lookup
- Exponentiation by squaring
- Heap insert / extract-min (binary heap)
- Binary lifting for LCA queries

### Q4: Binary search requires a sorted array. What if the data is not sorted?

**Expected Answer:**
If data is not sorted, you cannot use binary search directly. You have two options:
1. Sort first in O(n log n), then binary search in O(log n). Worth it if you perform many searches.
2. Use a hash table for O(1) expected lookups if you only need point queries.
3. Use a balanced BST for O(log n) dynamic insert/search/delete.

### Q5: What is the famous overflow bug in binary search?

**Expected Answer:**
Computing the midpoint as `(left + right) / 2` can overflow when left and right are large
integers (e.g., both near Integer.MAX_VALUE in Java). The fix is `left + (right - left) / 2`.
This bug was present in Java's Arrays.binarySearch for nearly a decade.

---

## Problem-Solving Questions

### Q6: Given a sorted array that has been rotated (e.g., [4,5,6,7,0,1,2]), find the minimum element in O(log n).

**Expected Answer:**
Use modified binary search. Compare `arr[mid]` with `arr[right]`:
- If `arr[mid] > arr[right]`, the minimum is in the right half.
- Otherwise, the minimum is in the left half (including mid).

### Q7: How would you find the first occurrence of a target in a sorted array with duplicates?

**Expected Answer:**
Use lower-bound binary search: when `arr[mid] == target`, do not return immediately. Instead,
set `right = mid` and continue searching left. When the loop ends, `left` points to the first
occurrence.

### Q8: Can binary search work on non-array data?

**Expected Answer:**
Yes. Binary search works on any monotonic function. Examples:
- Finding the square root of x by binary searching on [0, x].
- Finding the minimum speed for a delivery truck (LeetCode: Koko eating bananas).
- Bisecting on the answer in optimization problems.

The key requirement is a predicate that is false for all values below a threshold and true for
all values above (or vice versa).

---

## Coding Challenge: Robust Binary Search

**Problem:** Implement a function that searches for a target in a sorted array. Handle all edge
cases: empty array, single element, target not present, target at boundaries, duplicates. Return
the index of the target, or -1 if not found.

Additionally, implement `findFirst` (first occurrence) and `findLast` (last occurrence) for arrays
with duplicates.

### Go Solution

```go
package main

import "fmt"

// BinarySearch returns the index of target, or -1 if not found.
func BinarySearch(arr []int, target int) int {
    if len(arr) == 0 {
        return -1
    }

    left, right := 0, len(arr)-1

    for left <= right {
        mid := left + (right-left)/2

        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return -1
}

// FindFirst returns the index of the first occurrence of target, or -1.
func FindFirst(arr []int, target int) int {
    if len(arr) == 0 {
        return -1
    }

    left, right := 0, len(arr)-1
    result := -1

    for left <= right {
        mid := left + (right-left)/2

        if arr[mid] == target {
            result = mid
            right = mid - 1 // Keep searching left
        } else if arr[mid] < target {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return result
}

// FindLast returns the index of the last occurrence of target, or -1.
func FindLast(arr []int, target int) int {
    if len(arr) == 0 {
        return -1
    }

    left, right := 0, len(arr)-1
    result := -1

    for left <= right {
        mid := left + (right-left)/2

        if arr[mid] == target {
            result = mid
            left = mid + 1 // Keep searching right
        } else if arr[mid] < target {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return result
}

func main() {
    arr := []int{1, 3, 3, 3, 5, 7, 9}

    fmt.Println("Search 3:", BinarySearch(arr, 3))   // Output: 2 or 3 (any 3)
    fmt.Println("First 3:", FindFirst(arr, 3))        // Output: 1
    fmt.Println("Last 3:", FindLast(arr, 3))          // Output: 3
    fmt.Println("Search 4:", BinarySearch(arr, 4))    // Output: -1
    fmt.Println("Search empty:", BinarySearch([]int{}, 5)) // Output: -1

    single := []int{42}
    fmt.Println("Single found:", BinarySearch(single, 42))    // Output: 0
    fmt.Println("Single not found:", BinarySearch(single, 10)) // Output: -1
}
```

### Java Solution

```java
public class RobustBinarySearch {

    /** Returns the index of target, or -1 if not found. */
    public static int binarySearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) return -1;

        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }

        return -1;
    }

    /** Returns the index of the first occurrence of target, or -1. */
    public static int findFirst(int[] arr, int target) {
        if (arr == null || arr.length == 0) return -1;

        int left = 0, right = arr.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                result = mid;
                right = mid - 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    /** Returns the index of the last occurrence of target, or -1. */
    public static int findLast(int[] arr, int target) {
        if (arr == null || arr.length == 0) return -1;

        int left = 0, right = arr.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                result = mid;
                left = mid + 1;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] arr = {1, 3, 3, 3, 5, 7, 9};

        System.out.println("Search 3: " + binarySearch(arr, 3));
        System.out.println("First 3: " + findFirst(arr, 3));    // Output: 1
        System.out.println("Last 3: " + findLast(arr, 3));      // Output: 3
        System.out.println("Search 4: " + binarySearch(arr, 4)); // Output: -1
        System.out.println("Search empty: " + binarySearch(new int[]{}, 5));
    }
}
```

### Python Solution

```python
from typing import List


def binary_search(arr: List[int], target: int) -> int:
    """Return the index of target, or -1 if not found."""
    if not arr:
        return -1

    left, right = 0, len(arr) - 1

    while left <= right:
        mid = left + (right - left) // 2

        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1

    return -1


def find_first(arr: List[int], target: int) -> int:
    """Return the index of the first occurrence of target, or -1."""
    if not arr:
        return -1

    left, right = 0, len(arr) - 1
    result = -1

    while left <= right:
        mid = left + (right - left) // 2

        if arr[mid] == target:
            result = mid
            right = mid - 1
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1

    return result


def find_last(arr: List[int], target: int) -> int:
    """Return the index of the last occurrence of target, or -1."""
    if not arr:
        return -1

    left, right = 0, len(arr) - 1
    result = -1

    while left <= right:
        mid = left + (right - left) // 2

        if arr[mid] == target:
            result = mid
            left = mid + 1
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1

    return result


if __name__ == "__main__":
    arr = [1, 3, 3, 3, 5, 7, 9]

    print(f"Search 3: {binary_search(arr, 3)}")
    print(f"First 3: {find_first(arr, 3)}")     # Output: 1
    print(f"Last 3: {find_last(arr, 3)}")        # Output: 3
    print(f"Search 4: {binary_search(arr, 4)}")  # Output: -1
    print(f"Search empty: {binary_search([], 5)}")  # Output: -1
```

---

## Follow-Up Discussion Points

### What interviewers look for:

1. **Correct loop invariant** — `left <= right` vs `left < right` and the implications.
2. **Overflow prevention** — Using `left + (right - left) / 2`.
3. **Edge cases** — Empty array, single element, all duplicates, target at boundaries.
4. **Variant awareness** — Knowing when to use lower/upper bound vs standard binary search.
5. **Complexity analysis** — Time: O(log n), Space: O(1) iterative, O(log n) recursive.

### Common mistakes candidates make:

- Off-by-one errors in boundary updates (`mid + 1` vs `mid`).
- Infinite loops when `left = mid` without adjusting for integer division.
- Returning wrong index for first/last occurrence.
- Not handling empty input.
- Using `(left + right) / 2` instead of `left + (right - left) / 2`.
