# Array -- Interview Questions

## Table of Contents

- [Junior Level Questions](#junior-level-questions)
- [Middle Level Questions](#middle-level-questions)
- [Senior Level Questions](#senior-level-questions)
- [Coding Challenge: Rotate Array](#coding-challenge-rotate-array)
- [Coding Challenge: Merge Two Sorted Arrays](#coding-challenge-merge-two-sorted-arrays)

---

## Junior Level Questions

### Q1. What is an array and how is it stored in memory?

**Answer:** An array is an ordered collection of elements stored in contiguous memory locations. Each element occupies the same amount of space. The address of element at index `i` is: `base_address + i * element_size`. This formula allows O(1) random access.

### Q2. What is the difference between a static array and a dynamic array?

**Answer:** A static array has a fixed size determined at creation and cannot grow. A dynamic array (Go slice, Java ArrayList, Python list) can grow automatically by allocating a larger backing array (typically 1.5x-2x the old capacity) and copying elements over. Append is amortized O(1).

### Q3. Why is accessing an element by index O(1) but searching for a value O(n)?

**Answer:** Access by index uses the address formula (one multiplication, one addition) -- constant time regardless of array size. Searching for a value requires comparing each element one by one in the worst case (the target may be at the end or absent). For a sorted array, binary search reduces this to O(log n).

### Q4. What happens when you insert an element in the middle of an array?

**Answer:** All elements after the insertion point must shift one position to the right to make room. This takes O(n) time in the worst case (inserting at index 0 shifts all n elements).

### Q5. What is an off-by-one error? Give an example.

**Answer:** An off-by-one error occurs when a loop iterates one too many or too few times, or accesses an index that is just outside the valid range. Example: an array of size 5 has valid indices 0-4. Accessing `arr[5]` is an off-by-one error that causes an index-out-of-bounds exception.

---

## Middle Level Questions

### Q6. Explain amortized O(1) for dynamic array append.

**Answer:** Although individual resizes cost O(n), they happen infrequently. With a doubling strategy, after resizing to capacity 2k, you can do k more appends before the next resize. The total work for n appends is at most 3n (n placements + sum of copies = n + 2n). So the amortized cost per append is 3n/n = O(1).

### Q7. When would you use the two-pointer technique on an array?

**Answer:** Two pointers are useful for sorted array problems (two-sum, container with most water), removing duplicates in-place, partitioning (Dutch national flag), and merging two sorted arrays. The technique replaces an O(n^2) brute force with O(n) by having two indices converge or co-move.

### Q8. What is the prefix sum technique and when is it useful?

**Answer:** A prefix sum array stores cumulative sums: `prefix[i] = arr[0] + arr[1] + ... + arr[i-1]`. Any subarray sum `arr[l..r]` can then be computed in O(1) as `prefix[r+1] - prefix[l]`. It is useful for range sum queries, finding subarrays with a given sum, and 2D matrix region sums.

### Q9. Why are arrays faster than linked lists in practice despite similar Big-O for some operations?

**Answer:** Arrays benefit from **cache locality**. When the CPU fetches one element, it loads an entire cache line (typically 64 bytes) into L1 cache. For arrays, neighboring elements are already in cache. Linked list nodes are scattered in memory, causing cache misses that are 10-100x slower than cache hits.

### Q10. What is Kadane's algorithm?

**Answer:** Kadane's algorithm finds the maximum sum contiguous subarray in O(n) time. It maintains `max_ending_here` (best sum ending at the current position) and `max_so_far` (global best). At each element, `max_ending_here = max(arr[i], max_ending_here + arr[i])`. If extending the subarray helps, extend; otherwise, start fresh.

---

## Senior Level Questions

### Q11. How does a ring buffer work and where is it used?

**Answer:** A ring buffer is a fixed-size array with `head` and `tail` pointers that wrap around using modular arithmetic. Enqueue writes at `tail` and advances it; dequeue reads at `head` and advances it. Both are O(1) with no memory allocation. Used in network packet buffers, audio streaming, kernel I/O, and producer-consumer queues.

### Q12. Explain Array of Structs (AoS) vs Struct of Arrays (SoA).

**Answer:** AoS stores complete records contiguously: `[{x,y,z}, {x,y,z}, ...]`. SoA stores each field in a separate array: `{xs: [...], ys: [...], zs: [...]}`. SoA is better for SIMD operations and columnar processing (accessing only one field loads fewer cache lines). AoS is better when you access all fields of a record together.

### Q13. How would you design a thread-safe array for a read-heavy workload?

**Answer:** Use **Copy-on-Write (CoW)**: readers access an immutable snapshot with no locking. Writers create a new copy of the array with the modification, then atomically swap the reference. Java's `CopyOnWriteArrayList` implements this. Alternatively, use `RWMutex` where multiple readers can hold the read lock simultaneously and only writers need exclusive access.

### Q14. What is memory-mapped I/O and how does it relate to arrays?

**Answer:** Memory-mapped I/O maps a file into the process's virtual address space, allowing you to treat the file's contents as an array in memory. The OS transparently handles paging data in and out. Benefits: no explicit read/write syscalls, automatic caching, shared access between processes, works for files larger than physical RAM.

---

## Coding Challenge: Rotate Array

**Problem:** Given an array of n elements and a number k, rotate the array to the right by k positions.

Example: `[1,2,3,4,5,6,7]`, k=3 becomes `[5,6,7,1,2,3,4]`.

**Approach:** Three reverses -- reverse the whole array, then reverse the first k elements, then reverse the remaining.

**Go:**

```go
package main

import "fmt"

func reverse(arr []int, start, end int) {
    for start < end {
        arr[start], arr[end] = arr[end], arr[start]
        start++
        end--
    }
}

func rotateRight(arr []int, k int) {
    n := len(arr)
    if n == 0 {
        return
    }
    k = k % n
    if k == 0 {
        return
    }
    reverse(arr, 0, n-1)   // reverse all
    reverse(arr, 0, k-1)   // reverse first k
    reverse(arr, k, n-1)   // reverse rest
}

func main() {
    arr := []int{1, 2, 3, 4, 5, 6, 7}
    rotateRight(arr, 3)
    fmt.Println(arr) // [5 6 7 1 2 3 4]
}
```

**Java:**

```java
public class RotateArray {
    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int tmp = arr[start];
            arr[start] = arr[end];
            arr[end] = tmp;
            start++;
            end--;
        }
    }

    public static void rotateRight(int[] arr, int k) {
        int n = arr.length;
        if (n == 0) return;
        k = k % n;
        if (k == 0) return;
        reverse(arr, 0, n - 1);
        reverse(arr, 0, k - 1);
        reverse(arr, k, n - 1);
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        rotateRight(arr, 3);
        System.out.println(java.util.Arrays.toString(arr)); // [5, 6, 7, 1, 2, 3, 4]
    }
}
```

**Python:**

```python
def rotate_right(arr, k):
    n = len(arr)
    if n == 0:
        return
    k = k % n
    if k == 0:
        return

    def reverse(start, end):
        while start < end:
            arr[start], arr[end] = arr[end], arr[start]
            start += 1
            end -= 1

    reverse(0, n - 1)
    reverse(0, k - 1)
    reverse(k, n - 1)

arr = [1, 2, 3, 4, 5, 6, 7]
rotate_right(arr, 3)
print(arr)  # [5, 6, 7, 1, 2, 3, 4]

# Pythonic one-liner (creates new list):
# arr = arr[-k:] + arr[:-k]
```

Time: O(n), Space: O(1).

---

## Coding Challenge: Merge Two Sorted Arrays

**Problem:** Given two sorted arrays, merge them into a single sorted array.

**Go:**

```go
package main

import "fmt"

func mergeSorted(a, b []int) []int {
    result := make([]int, 0, len(a)+len(b))
    i, j := 0, 0
    for i < len(a) && j < len(b) {
        if a[i] <= b[j] {
            result = append(result, a[i])
            i++
        } else {
            result = append(result, b[j])
            j++
        }
    }
    result = append(result, a[i:]...)
    result = append(result, b[j:]...)
    return result
}

func main() {
    a := []int{1, 3, 5, 7}
    b := []int{2, 4, 6, 8, 10}
    fmt.Println(mergeSorted(a, b)) // [1 2 3 4 5 6 7 8 10]
}
```

**Java:**

```java
import java.util.Arrays;

public class MergeSorted {
    public static int[] mergeSorted(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) result[k++] = a[i++];
            else result[k++] = b[j++];
        }
        while (i < a.length) result[k++] = a[i++];
        while (j < b.length) result[k++] = b[j++];
        return result;
    }

    public static void main(String[] args) {
        int[] a = {1, 3, 5, 7};
        int[] b = {2, 4, 6, 8, 10};
        System.out.println(Arrays.toString(mergeSorted(a, b)));
        // [1, 2, 3, 4, 5, 6, 7, 8, 10]
    }
}
```

**Python:**

```python
def merge_sorted(a, b):
    result = []
    i = j = 0
    while i < len(a) and j < len(b):
        if a[i] <= b[j]:
            result.append(a[i])
            i += 1
        else:
            result.append(b[j])
            j += 1
    result.extend(a[i:])
    result.extend(b[j:])
    return result

a = [1, 3, 5, 7]
b = [2, 4, 6, 8, 10]
print(merge_sorted(a, b))  # [1, 2, 3, 4, 5, 6, 7, 8, 10]

# Using heapq for multiple sorted arrays:
# import heapq
# merged = list(heapq.merge(a, b))
```

Time: O(n + m), Space: O(n + m).
