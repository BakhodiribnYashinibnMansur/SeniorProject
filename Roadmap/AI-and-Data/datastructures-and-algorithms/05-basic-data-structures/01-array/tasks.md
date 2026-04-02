# Array -- Practice Tasks

## Table of Contents

- [Task 1: Reverse an Array In-Place](#task-1-reverse-an-array-in-place)
- [Task 2: Find the Maximum and Minimum](#task-2-find-the-maximum-and-minimum)
- [Task 3: Remove Duplicates from Sorted Array](#task-3-remove-duplicates-from-sorted-array)
- [Task 4: Move Zeroes to End](#task-4-move-zeroes-to-end)
- [Task 5: Two Sum](#task-5-two-sum)
- [Task 6: Maximum Subarray (Kadane)](#task-6-maximum-subarray-kadane)
- [Task 7: Rotate Array by K Positions](#task-7-rotate-array-by-k-positions)
- [Task 8: Merge Two Sorted Arrays](#task-8-merge-two-sorted-arrays)
- [Task 9: Find Missing Number](#task-9-find-missing-number)
- [Task 10: Product of Array Except Self](#task-10-product-of-array-except-self)
- [Task 11: Sliding Window Maximum](#task-11-sliding-window-maximum)
- [Task 12: Spiral Matrix Traversal](#task-12-spiral-matrix-traversal)
- [Task 13: Implement a Dynamic Array from Scratch](#task-13-implement-a-dynamic-array-from-scratch)
- [Task 14: Dutch National Flag (3-Way Partition)](#task-14-dutch-national-flag-3-way-partition)
- [Task 15: Trapping Rain Water](#task-15-trapping-rain-water)
- [Benchmark: Append Performance](#benchmark-append-performance)

---

## Task 1: Reverse an Array In-Place

**Difficulty:** Easy

**Problem:** Given an array of integers, reverse it in-place without using extra memory.

**Input:** `[1, 2, 3, 4, 5]`
**Output:** `[5, 4, 3, 2, 1]`

**Hint:** Use two pointers, one at the start and one at the end, swapping until they meet.

**Expected complexity:** Time O(n), Space O(1).

**Go:**

```go
func reverseInPlace(arr []int) {
    left, right := 0, len(arr)-1
    for left < right {
        arr[left], arr[right] = arr[right], arr[left]
        left++
        right--
    }
}
```

**Java:**

```java
public static void reverseInPlace(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int tmp = arr[left];
        arr[left] = arr[right];
        arr[right] = tmp;
        left++;
        right--;
    }
}
```

**Python:**

```python
def reverse_in_place(arr):
    left, right = 0, len(arr) - 1
    while left < right:
        arr[left], arr[right] = arr[right], arr[left]
        left += 1
        right -= 1
```

---

## Task 2: Find the Maximum and Minimum

**Difficulty:** Easy

**Problem:** Find both the maximum and minimum values in an array using a single pass.

**Input:** `[3, 1, 7, 2, 9, 4]`
**Output:** `min=1, max=9`

**Go:**

```go
func findMinMax(arr []int) (int, int) {
    min, max := arr[0], arr[0]
    for _, v := range arr[1:] {
        if v < min {
            min = v
        }
        if v > max {
            max = v
        }
    }
    return min, max
}
```

**Java:**

```java
public static int[] findMinMax(int[] arr) {
    int min = arr[0], max = arr[0];
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] < min) min = arr[i];
        if (arr[i] > max) max = arr[i];
    }
    return new int[]{min, max};
}
```

**Python:**

```python
def find_min_max(arr):
    mn, mx = arr[0], arr[0]
    for v in arr[1:]:
        if v < mn: mn = v
        if v > mx: mx = v
    return mn, mx
```

---

## Task 3: Remove Duplicates from Sorted Array

**Difficulty:** Easy

**Problem:** Given a sorted array, remove duplicates in-place and return the new length. Do not allocate extra arrays.

**Input:** `[1, 1, 2, 2, 3, 4, 4, 5]`
**Output:** length `5`, array starts with `[1, 2, 3, 4, 5, ...]`

**Go:**

```go
func removeDuplicates(arr []int) int {
    if len(arr) == 0 {
        return 0
    }
    writeIdx := 1
    for i := 1; i < len(arr); i++ {
        if arr[i] != arr[i-1] {
            arr[writeIdx] = arr[i]
            writeIdx++
        }
    }
    return writeIdx
}
```

**Java:**

```java
public static int removeDuplicates(int[] arr) {
    if (arr.length == 0) return 0;
    int writeIdx = 1;
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] != arr[i - 1]) {
            arr[writeIdx++] = arr[i];
        }
    }
    return writeIdx;
}
```

**Python:**

```python
def remove_duplicates(arr):
    if not arr:
        return 0
    write_idx = 1
    for i in range(1, len(arr)):
        if arr[i] != arr[i - 1]:
            arr[write_idx] = arr[i]
            write_idx += 1
    return write_idx
```

---

## Task 4: Move Zeroes to End

**Difficulty:** Easy

**Problem:** Move all zeroes to the end while preserving the relative order of non-zero elements. Do it in-place.

**Input:** `[0, 1, 0, 3, 12]`
**Output:** `[1, 3, 12, 0, 0]`

**Go:**

```go
func moveZeroes(arr []int) {
    writeIdx := 0
    for _, v := range arr {
        if v != 0 {
            arr[writeIdx] = v
            writeIdx++
        }
    }
    for i := writeIdx; i < len(arr); i++ {
        arr[i] = 0
    }
}
```

**Java:**

```java
public static void moveZeroes(int[] arr) {
    int writeIdx = 0;
    for (int v : arr) {
        if (v != 0) arr[writeIdx++] = v;
    }
    while (writeIdx < arr.length) {
        arr[writeIdx++] = 0;
    }
}
```

**Python:**

```python
def move_zeroes(arr):
    write_idx = 0
    for v in arr:
        if v != 0:
            arr[write_idx] = v
            write_idx += 1
    for i in range(write_idx, len(arr)):
        arr[i] = 0
```

---

## Task 5: Two Sum

**Difficulty:** Medium

**Problem:** Given an array and a target sum, find two indices whose elements add up to the target. Each element can be used only once.

**Input:** `[2, 7, 11, 15]`, target `9`
**Output:** `[0, 1]` (because 2 + 7 = 9)

**Go:**

```go
func twoSum(nums []int, target int) []int {
    seen := make(map[int]int) // value -> index
    for i, v := range nums {
        complement := target - v
        if j, ok := seen[complement]; ok {
            return []int{j, i}
        }
        seen[v] = i
    }
    return nil
}
```

**Java:**

```java
public static int[] twoSum(int[] nums, int target) {
    java.util.HashMap<Integer, Integer> seen = new java.util.HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        seen.put(nums[i], i);
    }
    return new int[]{};
}
```

**Python:**

```python
def two_sum(nums, target):
    seen = {}
    for i, v in enumerate(nums):
        complement = target - v
        if complement in seen:
            return [seen[complement], i]
        seen[v] = i
    return []
```

---

## Task 6: Maximum Subarray (Kadane)

**Difficulty:** Medium

**Problem:** Find the contiguous subarray with the largest sum.

**Input:** `[-2, 1, -3, 4, -1, 2, 1, -5, 4]`
**Output:** `6` (subarray `[4, -1, 2, 1]`)

**Go:**

```go
func maxSubArray(nums []int) int {
    maxSoFar, maxEndingHere := nums[0], nums[0]
    for _, v := range nums[1:] {
        if maxEndingHere+v > v {
            maxEndingHere += v
        } else {
            maxEndingHere = v
        }
        if maxEndingHere > maxSoFar {
            maxSoFar = maxEndingHere
        }
    }
    return maxSoFar
}
```

**Java:**

```java
public static int maxSubArray(int[] nums) {
    int maxSoFar = nums[0], maxEndingHere = nums[0];
    for (int i = 1; i < nums.length; i++) {
        maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    return maxSoFar;
}
```

**Python:**

```python
def max_sub_array(nums):
    max_so_far = max_ending_here = nums[0]
    for v in nums[1:]:
        max_ending_here = max(v, max_ending_here + v)
        max_so_far = max(max_so_far, max_ending_here)
    return max_so_far
```

---

## Task 7: Rotate Array by K Positions

**Difficulty:** Medium

**Problem:** Rotate array to the right by k steps in-place.

**Input:** `[1, 2, 3, 4, 5, 6, 7]`, k=3
**Output:** `[5, 6, 7, 1, 2, 3, 4]`

**Hint:** Use the three-reverse technique.

See the [interview.md](interview.md) file for the full solution.

---

## Task 8: Merge Two Sorted Arrays

**Difficulty:** Medium

**Problem:** Merge two sorted arrays into one sorted array.

See the [interview.md](interview.md) file for the full solution.

---

## Task 9: Find Missing Number

**Difficulty:** Easy

**Problem:** Given an array containing n distinct numbers from 0, 1, 2, ..., n, find the missing one.

**Input:** `[3, 0, 1]`
**Output:** `2`

**Go:**

```go
func missingNumber(nums []int) int {
    n := len(nums)
    expectedSum := n * (n + 1) / 2
    actualSum := 0
    for _, v := range nums {
        actualSum += v
    }
    return expectedSum - actualSum
}
```

**Java:**

```java
public static int missingNumber(int[] nums) {
    int n = nums.length;
    int expectedSum = n * (n + 1) / 2;
    int actualSum = 0;
    for (int v : nums) actualSum += v;
    return expectedSum - actualSum;
}
```

**Python:**

```python
def missing_number(nums):
    n = len(nums)
    return n * (n + 1) // 2 - sum(nums)
```

---

## Task 10: Product of Array Except Self

**Difficulty:** Medium

**Problem:** Given an array, return an array where each element is the product of all other elements. Do not use division.

**Input:** `[1, 2, 3, 4]`
**Output:** `[24, 12, 8, 6]`

**Go:**

```go
func productExceptSelf(nums []int) []int {
    n := len(nums)
    result := make([]int, n)

    // Left pass: result[i] = product of nums[0..i-1]
    result[0] = 1
    for i := 1; i < n; i++ {
        result[i] = result[i-1] * nums[i-1]
    }

    // Right pass: multiply by product of nums[i+1..n-1]
    rightProduct := 1
    for i := n - 2; i >= 0; i-- {
        rightProduct *= nums[i+1]
        result[i] *= rightProduct
    }
    return result
}
```

**Java:**

```java
public static int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    result[0] = 1;
    for (int i = 1; i < n; i++) {
        result[i] = result[i - 1] * nums[i - 1];
    }
    int rightProduct = 1;
    for (int i = n - 2; i >= 0; i--) {
        rightProduct *= nums[i + 1];
        result[i] *= rightProduct;
    }
    return result;
}
```

**Python:**

```python
def product_except_self(nums):
    n = len(nums)
    result = [1] * n
    for i in range(1, n):
        result[i] = result[i - 1] * nums[i - 1]
    right_product = 1
    for i in range(n - 2, -1, -1):
        right_product *= nums[i + 1]
        result[i] *= right_product
    return result
```

---

## Task 11: Sliding Window Maximum

**Difficulty:** Hard

**Problem:** Given an array and window size k, find the maximum in each window as it slides from left to right.

**Input:** `[1, 3, -1, -3, 5, 3, 6, 7]`, k=3
**Output:** `[3, 3, 5, 5, 6, 7]`

**Hint:** Use a deque (monotonic decreasing queue) that stores indices.

**Python (clearest to show):**

```python
from collections import deque

def max_sliding_window(nums, k):
    dq = deque()  # stores indices of useful elements (decreasing order of values)
    result = []
    for i, v in enumerate(nums):
        # Remove indices that are out of the current window
        while dq and dq[0] < i - k + 1:
            dq.popleft()
        # Remove indices of elements smaller than current (they are useless)
        while dq and nums[dq[-1]] < v:
            dq.pop()
        dq.append(i)
        if i >= k - 1:
            result.append(nums[dq[0]])
    return result
```

---

## Task 12: Spiral Matrix Traversal

**Difficulty:** Medium

**Problem:** Given an m x n matrix, return all elements in spiral order.

**Input:**
```
[[1, 2, 3],
 [4, 5, 6],
 [7, 8, 9]]
```
**Output:** `[1, 2, 3, 6, 9, 8, 7, 4, 5]`

**Python:**

```python
def spiral_order(matrix):
    result = []
    while matrix:
        result += matrix.pop(0)          # top row
        if matrix and matrix[0]:
            for row in matrix:           # right column
                result.append(row.pop())
        if matrix:
            result += matrix.pop()[::-1] # bottom row reversed
        if matrix and matrix[0]:
            for row in reversed(matrix): # left column
                result.append(row.pop(0))
    return result
```

---

## Task 13: Implement a Dynamic Array from Scratch

**Difficulty:** Medium

**Problem:** Implement a dynamic array supporting: `append`, `get`, `set`, `insertAt`, `deleteAt`, `size`. It must grow when full (double capacity) and shrink when 1/4 full (halve capacity).

**Python:**

```python
class DynamicArray:
    def __init__(self):
        self._capacity = 4
        self._size = 0
        self._data = [None] * self._capacity

    def size(self):
        return self._size

    def get(self, index):
        if index < 0 or index >= self._size:
            raise IndexError("Index out of bounds")
        return self._data[index]

    def set(self, index, value):
        if index < 0 or index >= self._size:
            raise IndexError("Index out of bounds")
        self._data[index] = value

    def append(self, value):
        if self._size == self._capacity:
            self._resize(self._capacity * 2)
        self._data[self._size] = value
        self._size += 1

    def insert_at(self, index, value):
        if index < 0 or index > self._size:
            raise IndexError("Index out of bounds")
        if self._size == self._capacity:
            self._resize(self._capacity * 2)
        for i in range(self._size, index, -1):
            self._data[i] = self._data[i - 1]
        self._data[index] = value
        self._size += 1

    def delete_at(self, index):
        if index < 0 or index >= self._size:
            raise IndexError("Index out of bounds")
        val = self._data[index]
        for i in range(index, self._size - 1):
            self._data[i] = self._data[i + 1]
        self._size -= 1
        if self._size > 0 and self._size == self._capacity // 4:
            self._resize(self._capacity // 2)
        return val

    def _resize(self, new_capacity):
        new_data = [None] * new_capacity
        for i in range(self._size):
            new_data[i] = self._data[i]
        self._data = new_data
        self._capacity = new_capacity
```

---

## Task 14: Dutch National Flag (3-Way Partition)

**Difficulty:** Medium

**Problem:** Given an array with values 0, 1, 2, sort it in-place in a single pass.

**Input:** `[2, 0, 1, 2, 0, 1, 0]`
**Output:** `[0, 0, 0, 1, 1, 2, 2]`

**Go:**

```go
func dutchNationalFlag(arr []int) {
    low, mid, high := 0, 0, len(arr)-1
    for mid <= high {
        switch arr[mid] {
        case 0:
            arr[low], arr[mid] = arr[mid], arr[low]
            low++
            mid++
        case 1:
            mid++
        case 2:
            arr[mid], arr[high] = arr[high], arr[mid]
            high--
        }
    }
}
```

**Java:**

```java
public static void dutchNationalFlag(int[] arr) {
    int low = 0, mid = 0, high = arr.length - 1;
    while (mid <= high) {
        if (arr[mid] == 0) {
            int tmp = arr[low]; arr[low] = arr[mid]; arr[mid] = tmp;
            low++; mid++;
        } else if (arr[mid] == 1) {
            mid++;
        } else {
            int tmp = arr[mid]; arr[mid] = arr[high]; arr[high] = tmp;
            high--;
        }
    }
}
```

**Python:**

```python
def dutch_national_flag(arr):
    low, mid, high = 0, 0, len(arr) - 1
    while mid <= high:
        if arr[mid] == 0:
            arr[low], arr[mid] = arr[mid], arr[low]
            low += 1
            mid += 1
        elif arr[mid] == 1:
            mid += 1
        else:
            arr[mid], arr[high] = arr[high], arr[mid]
            high -= 1
```

---

## Task 15: Trapping Rain Water

**Difficulty:** Hard

**Problem:** Given an elevation map (array of non-negative integers), compute how much water can be trapped after raining.

**Input:** `[0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]`
**Output:** `6`

**Go:**

```go
func trap(height []int) int {
    left, right := 0, len(height)-1
    leftMax, rightMax := 0, 0
    water := 0
    for left < right {
        if height[left] < height[right] {
            if height[left] >= leftMax {
                leftMax = height[left]
            } else {
                water += leftMax - height[left]
            }
            left++
        } else {
            if height[right] >= rightMax {
                rightMax = height[right]
            } else {
                water += rightMax - height[right]
            }
            right--
        }
    }
    return water
}
```

**Java:**

```java
public static int trap(int[] height) {
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0, water = 0;
    while (left < right) {
        if (height[left] < height[right]) {
            leftMax = Math.max(leftMax, height[left]);
            water += leftMax - height[left];
            left++;
        } else {
            rightMax = Math.max(rightMax, height[right]);
            water += rightMax - height[right];
            right--;
        }
    }
    return water;
}
```

**Python:**

```python
def trap(height):
    left, right = 0, len(height) - 1
    left_max = right_max = water = 0
    while left < right:
        if height[left] < height[right]:
            left_max = max(left_max, height[left])
            water += left_max - height[left]
            left += 1
        else:
            right_max = max(right_max, height[right])
            water += right_max - height[right]
            right -= 1
    return water
```

---

## Benchmark: Append Performance

Measure the time to append 1 million elements to a dynamic array.

**Go:**

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    n := 1_000_000

    // With no pre-allocation
    start := time.Now()
    s1 := []int{}
    for i := 0; i < n; i++ {
        s1 = append(s1, i)
    }
    fmt.Printf("No pre-alloc:   %v\n", time.Since(start))

    // With pre-allocation
    start = time.Now()
    s2 := make([]int, 0, n)
    for i := 0; i < n; i++ {
        s2 = append(s2, i)
    }
    fmt.Printf("Pre-allocated:  %v\n", time.Since(start))
}
```

**Java:**

```java
import java.util.ArrayList;

public class BenchmarkAppend {
    public static void main(String[] args) {
        int n = 1_000_000;

        // No pre-allocation
        long start = System.nanoTime();
        ArrayList<Integer> list1 = new ArrayList<>();
        for (int i = 0; i < n; i++) list1.add(i);
        long t1 = System.nanoTime() - start;

        // With pre-allocation
        start = System.nanoTime();
        ArrayList<Integer> list2 = new ArrayList<>(n);
        for (int i = 0; i < n; i++) list2.add(i);
        long t2 = System.nanoTime() - start;

        System.out.printf("No pre-alloc:  %d ms%n", t1 / 1_000_000);
        System.out.printf("Pre-allocated: %d ms%n", t2 / 1_000_000);
    }
}
```

**Python:**

```python
import time

n = 1_000_000

# Using append
start = time.perf_counter()
arr1 = []
for i in range(n):
    arr1.append(i)
t1 = time.perf_counter() - start

# Using list comprehension (single allocation)
start = time.perf_counter()
arr2 = [i for i in range(n)]
t2 = time.perf_counter() - start

# Using range directly
start = time.perf_counter()
arr3 = list(range(n))
t3 = time.perf_counter() - start

print(f"Append loop:        {t1:.4f}s")
print(f"List comprehension: {t2:.4f}s")
print(f"list(range()):      {t3:.4f}s")
```

**Expected results:** Pre-allocation is 1.5-3x faster because it avoids repeated resizing and copying.
