# Big-O Notation -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Is Big-O Notation?](#what-is-big-o-notation)
3. [Why Do We Need Big-O?](#why-do-we-need-big-o)
4. [Real-World Analogies](#real-world-analogies)
5. [Common Complexity Classes](#common-complexity-classes)
   - [O(1) -- Constant Time](#o1----constant-time)
   - [O(log n) -- Logarithmic Time](#olog-n----logarithmic-time)
   - [O(n) -- Linear Time](#on----linear-time)
   - [O(n log n) -- Linearithmic Time](#on-log-n----linearithmic-time)
   - [O(n^2) -- Quadratic Time](#on2----quadratic-time)
   - [O(2^n) -- Exponential Time](#o2n----exponential-time)
   - [O(n!) -- Factorial Time](#on----factorial-time)
6. [Rules for Determining Big-O](#rules-for-determining-big-o)
   - [Rule 1: Drop Constants](#rule-1-drop-constants)
   - [Rule 2: Drop Lower-Order Terms](#rule-2-drop-lower-order-terms)
   - [Rule 3: Multiply Nested Loops](#rule-3-multiply-nested-loops)
   - [Rule 4: Different Inputs Use Different Variables](#rule-4-different-inputs-use-different-variables)
7. [Analyzing Code Examples](#analyzing-code-examples)
8. [Comparison Chart](#comparison-chart)
9. [Common Mistakes](#common-mistakes)
10. [Key Takeaways](#key-takeaways)

---

## Introduction

When you write code, it works -- but how well does it work? If your input goes from 100 items to 1,000,000 items, will your program still finish in a reasonable time? Big-O notation is the standard language computer scientists use to describe how the performance of an algorithm scales as the input size grows. It is the single most important concept for writing efficient software and succeeding in technical interviews.

---

## What Is Big-O Notation?

Big-O notation describes the **upper bound** on the growth rate of an algorithm's running time (or space usage) as the input size approaches infinity.

In simple terms:

> Big-O tells you the **worst-case scenario** for how many operations your algorithm will perform relative to the input size `n`.

- **O(n)** means the number of operations grows linearly with the input.
- **O(n^2)** means the number of operations grows with the square of the input.
- **O(1)** means the number of operations stays constant regardless of input size.

The "O" stands for "Order of" -- it gives you the order of magnitude of growth.

---

## Why Do We Need Big-O?

Consider two sorting algorithms:

- Algorithm A takes 0.5 seconds for 1,000 elements.
- Algorithm B takes 1.0 seconds for 1,000 elements.

Algorithm A looks faster, right? But what happens at scale?

| Input Size | Algorithm A (O(n^2)) | Algorithm B (O(n log n)) |
|------------|----------------------|--------------------------|
| 1,000      | 0.5 sec              | 1.0 sec                  |
| 10,000     | 50 sec               | 1.3 sec                  |
| 100,000    | 5,000 sec (~83 min)  | 1.7 sec                  |
| 1,000,000  | 500,000 sec (~5.8 days) | 2.0 sec               |

Algorithm B is dramatically better at scale despite being slower on small inputs. Big-O notation captures this difference.

---

## Real-World Analogies

### O(1) -- Looking Up a Word in a Dictionary by Page Number
If someone tells you "look at page 247," you open directly to that page. It does not matter if the dictionary has 500 or 50,000 pages.

### O(log n) -- Finding a Word in a Phone Book
You open the book to the middle, decide if the word is in the left or right half, and repeat. Each step eliminates half the remaining pages. A 1,000-page book takes about 10 steps; a 1,000,000-page book takes about 20 steps.

### O(n) -- Reading Every Page of a Book
You must look at every page once. A 200-page book takes twice as long as a 100-page book.

### O(n^2) -- Comparing Every Student with Every Other Student
If a teacher must compare each student's grade with every other student, doubling the class size quadruples the work. 10 students = 100 comparisons; 100 students = 10,000 comparisons.

### O(2^n) -- Trying Every Combination of Light Switches
With n light switches, each can be on or off. 10 switches = 1,024 combinations. 20 switches = 1,048,576 combinations. Each new switch doubles the total combinations.

### O(n!) -- Arranging Students in Every Possible Order
The number of permutations of n items. 10 students = 3,628,800 arrangements. This grows astronomically fast.

---

## Common Complexity Classes

### O(1) -- Constant Time

The algorithm takes the same amount of time regardless of input size.

**Go:**
```go
func getFirst(arr []int) int {
    // Always one operation, no matter how big the array is
    return arr[0]
}

func accessByIndex(arr []int, index int) int {
    // Array access by index is always O(1)
    return arr[index]
}

func isEven(n int) bool {
    // A single arithmetic check
    return n%2 == 0
}
```

**Java:**
```java
public static int getFirst(int[] arr) {
    // Always one operation, no matter how big the array is
    return arr[0];
}

public static int accessByIndex(int[] arr, int index) {
    // Array access by index is always O(1)
    return arr[index];
}

public static boolean isEven(int n) {
    // A single arithmetic check
    return n % 2 == 0;
}
```

**Python:**
```python
def get_first(arr):
    # Always one operation, no matter how big the array is
    return arr[0]

def access_by_index(arr, index):
    # Array/list access by index is always O(1)
    return arr[index]

def is_even(n):
    # A single arithmetic check
    return n % 2 == 0
```

---

### O(log n) -- Logarithmic Time

The algorithm reduces the problem size by a constant fraction (usually half) at each step. Binary search is the classic example.

**Go:**
```go
func binarySearch(arr []int, target int) int {
    low, high := 0, len(arr)-1

    for low <= high {
        mid := low + (high-low)/2
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            low = mid + 1     // Discard left half
        } else {
            high = mid - 1    // Discard right half
        }
    }
    return -1
}
```

**Java:**
```java
public static int binarySearch(int[] arr, int target) {
    int low = 0, high = arr.length - 1;

    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            low = mid + 1;     // Discard left half
        } else {
            high = mid - 1;    // Discard right half
        }
    }
    return -1;
}
```

**Python:**
```python
def binary_search(arr, target):
    low, high = 0, len(arr) - 1

    while low <= high:
        mid = low + (high - low) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            low = mid + 1      # Discard left half
        else:
            high = mid - 1     # Discard right half
    return -1
```

At each step, half the data is eliminated. For n = 1,000,000, binary search needs at most ~20 comparisons (log2(1,000,000) ~ 20).

---

### O(n) -- Linear Time

The algorithm visits every element once.

**Go:**
```go
func findMax(arr []int) int {
    max := arr[0]
    for _, val := range arr {
        if val > max {
            max = val
        }
    }
    return max
}

func sumAll(arr []int) int {
    total := 0
    for _, val := range arr {
        total += val
    }
    return total
}
```

**Java:**
```java
public static int findMax(int[] arr) {
    int max = arr[0];
    for (int val : arr) {
        if (val > max) {
            max = val;
        }
    }
    return max;
}

public static int sumAll(int[] arr) {
    int total = 0;
    for (int val : arr) {
        total += val;
    }
    return total;
}
```

**Python:**
```python
def find_max(arr):
    max_val = arr[0]
    for val in arr:
        if val > max_val:
            max_val = val
    return max_val

def sum_all(arr):
    total = 0
    for val in arr:
        total += val
    return total
```

---

### O(n log n) -- Linearithmic Time

Common in efficient sorting algorithms like merge sort and heap sort.

**Go:**
```go
func mergeSort(arr []int) []int {
    if len(arr) <= 1 {
        return arr
    }
    mid := len(arr) / 2
    left := mergeSort(arr[:mid])    // T(n/2)
    right := mergeSort(arr[mid:])   // T(n/2)
    return merge(left, right)       // O(n) merge step
}

func merge(left, right []int) []int {
    result := make([]int, 0, len(left)+len(right))
    i, j := 0, 0
    for i < len(left) && j < len(right) {
        if left[i] <= right[j] {
            result = append(result, left[i])
            i++
        } else {
            result = append(result, right[j])
            j++
        }
    }
    result = append(result, left[i:]...)
    result = append(result, right[j:]...)
    return result
}
```

**Java:**
```java
public static int[] mergeSort(int[] arr) {
    if (arr.length <= 1) return arr;

    int mid = arr.length / 2;
    int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
    int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));
    return merge(left, right);
}

private static int[] merge(int[] left, int[] right) {
    int[] result = new int[left.length + right.length];
    int i = 0, j = 0, k = 0;
    while (i < left.length && j < right.length) {
        if (left[i] <= right[j]) {
            result[k++] = left[i++];
        } else {
            result[k++] = right[j++];
        }
    }
    while (i < left.length) result[k++] = left[i++];
    while (j < right.length) result[k++] = right[j++];
    return result;
}
```

**Python:**
```python
def merge_sort(arr):
    if len(arr) <= 1:
        return arr

    mid = len(arr) // 2
    left = merge_sort(arr[:mid])
    right = merge_sort(arr[mid:])
    return merge(left, right)

def merge(left, right):
    result = []
    i = j = 0
    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            result.append(left[i])
            i += 1
        else:
            result.append(right[j])
            j += 1
    result.extend(left[i:])
    result.extend(right[j:])
    return result
```

The array is split log(n) times, and each level does O(n) work in the merge step: O(n) x O(log n) = O(n log n).

---

### O(n^2) -- Quadratic Time

Usually involves nested loops where both iterate over the input.

**Go:**
```go
func bubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n; i++ {           // Outer loop: n times
        for j := 0; j < n-i-1; j++ {   // Inner loop: ~n times
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
            }
        }
    }
}

func hasDuplicate(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] == arr[j] {
                return true
            }
        }
    }
    return false
}
```

**Java:**
```java
public static void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}

public static boolean hasDuplicate(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[i] == arr[j]) {
                return true;
            }
        }
    }
    return false;
}
```

**Python:**
```python
def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]

def has_duplicate(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False
```

---

### O(2^n) -- Exponential Time

Each element doubles the total work. Common in brute-force solutions that explore all subsets.

**Go:**
```go
func fibonacci(n int) int {
    if n <= 1 {
        return n
    }
    return fibonacci(n-1) + fibonacci(n-2)
}

// Generate all subsets of a set
func subsets(nums []int) [][]int {
    result := [][]int{{}}
    for _, num := range nums {
        newSubsets := [][]int{}
        for _, subset := range result {
            newSubset := make([]int, len(subset))
            copy(newSubset, subset)
            newSubset = append(newSubset, num)
            newSubsets = append(newSubsets, newSubset)
        }
        result = append(result, newSubsets...)
    }
    return result
}
```

**Java:**
```java
public static int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

public static List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int num : nums) {
        List<List<Integer>> newSubsets = new ArrayList<>();
        for (List<Integer> subset : result) {
            List<Integer> newSubset = new ArrayList<>(subset);
            newSubset.add(num);
            newSubsets.add(newSubset);
        }
        result.addAll(newSubsets);
    }
    return result;
}
```

**Python:**
```python
def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n - 1) + fibonacci(n - 2)

def subsets(nums):
    result = [[]]
    for num in nums:
        new_subsets = [subset + [num] for subset in result]
        result.extend(new_subsets)
    return result
```

---

### O(n!) -- Factorial Time

Every possible ordering is explored. Common in permutation-based problems.

**Go:**
```go
func permutations(arr []int) [][]int {
    if len(arr) == 0 {
        return [][]int{{}}
    }
    result := [][]int{}
    for i, val := range arr {
        rest := make([]int, 0, len(arr)-1)
        rest = append(rest, arr[:i]...)
        rest = append(rest, arr[i+1:]...)
        for _, perm := range permutations(rest) {
            result = append(result, append([]int{val}, perm...))
        }
    }
    return result
}
```

**Java:**
```java
public static List<List<Integer>> permutations(List<Integer> arr) {
    if (arr.isEmpty()) {
        List<List<Integer>> base = new ArrayList<>();
        base.add(new ArrayList<>());
        return base;
    }
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < arr.size(); i++) {
        int val = arr.get(i);
        List<Integer> rest = new ArrayList<>(arr);
        rest.remove(i);
        for (List<Integer> perm : permutations(rest)) {
            perm.add(0, val);
            result.add(perm);
        }
    }
    return result;
}
```

**Python:**
```python
def permutations(arr):
    if len(arr) == 0:
        return [[]]
    result = []
    for i, val in enumerate(arr):
        rest = arr[:i] + arr[i+1:]
        for perm in permutations(rest):
            result.append([val] + perm)
    return result
```

---

## Rules for Determining Big-O

### Rule 1: Drop Constants

O(2n) becomes O(n). O(100n) becomes O(n). O(n/2) becomes O(n).

Constants do not change the growth rate. Whether you loop through an array once, twice, or a hundred times, the time still grows linearly with n.

```
5n + 3        --> O(n)
1000n         --> O(n)
n/2           --> O(n)
3n^2 + 7n     --> O(n^2)
```

### Rule 2: Drop Lower-Order Terms

When you have multiple terms, only the fastest-growing term matters for large n.

```
n^2 + n       --> O(n^2)       because n^2 dominates n
n^3 + n^2 + n --> O(n^3)       because n^3 dominates
2^n + n^3     --> O(2^n)       because 2^n dominates n^3
n + log(n)    --> O(n)         because n dominates log(n)
```

### Rule 3: Multiply Nested Loops

If one loop runs n times and contains another loop that runs n times, the total is n * n = n^2.

**Go:**
```go
func printPairs(arr []int) {
    n := len(arr)
    for i := 0; i < n; i++ {         // O(n)
        for j := 0; j < n; j++ {     //   * O(n)
            fmt.Println(arr[i], arr[j]) // = O(n^2) total
        }
    }
}
```

**Java:**
```java
public static void printPairs(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n; i++) {         // O(n)
        for (int j = 0; j < n; j++) {     //   * O(n)
            System.out.println(arr[i] + " " + arr[j]); // = O(n^2)
        }
    }
}
```

**Python:**
```python
def print_pairs(arr):
    n = len(arr)
    for i in range(n):           # O(n)
        for j in range(n):       #   * O(n)
            print(arr[i], arr[j])  # = O(n^2) total
```

### Rule 4: Different Inputs Use Different Variables

If a function takes two arrays of different sizes, use different variables.

**Go:**
```go
func printCombinations(arrA []int, arrB []int) {
    for _, a := range arrA {         // O(a)
        for _, b := range arrB {     //   * O(b)
            fmt.Println(a, b)        // = O(a * b), NOT O(n^2)
        }
    }
}
```

---

## Analyzing Code Examples

### Example 1: Sequential Loops

**Go:**
```go
func sequentialOps(arr []int) {
    // Loop 1: O(n)
    for _, v := range arr {
        fmt.Println(v)
    }
    // Loop 2: O(n)
    for _, v := range arr {
        fmt.Println(v * 2)
    }
}
// Total: O(n) + O(n) = O(2n) = O(n)
```

### Example 2: Loop with Halving

**Go:**
```go
func halvingLoop(n int) {
    i := n
    for i > 0 {
        fmt.Println(i)
        i = i / 2
    }
}
// n is divided by 2 each iteration
// Number of iterations: log2(n)
// Complexity: O(log n)
```

### Example 3: Nested Loop with Different Bounds

**Go:**
```go
func mixedNesting(n int) {
    for i := 0; i < n; i++ {         // O(n)
        for j := 0; j < 100; j++ {   // O(100) = O(1)
            fmt.Println(i, j)
        }
    }
}
// Total: O(n) * O(1) = O(n)
// The inner loop runs a FIXED number of times (100), not dependent on n
```

### Example 4: Two Separate Inputs

**Go:**
```go
func twoInputs(a []int, b []int) {
    for _, x := range a {
        fmt.Println(x)
    }
    for _, x := range a {
        for _, y := range b {
            fmt.Println(x, y)
        }
    }
}
// First loop: O(a)
// Nested loops: O(a * b)
// Total: O(a) + O(a * b) = O(a * b)
// (drop the lower-order O(a) term)
```

---

## Comparison Chart

| Big-O      | Name          | n=10  | n=100    | n=1,000     | n=1,000,000        |
|------------|---------------|-------|----------|-------------|---------------------|
| O(1)       | Constant      | 1     | 1        | 1           | 1                   |
| O(log n)   | Logarithmic   | 3     | 7        | 10          | 20                  |
| O(n)       | Linear        | 10    | 100      | 1,000       | 1,000,000           |
| O(n log n) | Linearithmic  | 33    | 664      | 9,966       | 19,931,569          |
| O(n^2)     | Quadratic     | 100   | 10,000   | 1,000,000   | 1,000,000,000,000   |
| O(2^n)     | Exponential   | 1,024 | 1.27e30  | Too large   | Too large           |
| O(n!)      | Factorial     | 3.6M  | Too large| Too large   | Too large           |

---

## Common Mistakes

1. **Confusing best case and worst case.** Big-O typically describes the worst case. Linear search is O(n) even though you might find the element on the first try.

2. **Forgetting about hidden loops.** Calling `arr.contains(x)` inside a loop is O(n) for the contains call, making the whole thing O(n^2).

3. **Saying "O(n^2)" for all nested loops.** If the inner loop is bounded by a constant (like 26 for the alphabet), it is O(n), not O(n^2).

4. **Treating Big-O as exact runtime.** O(n) does not mean your code runs in n milliseconds. It describes the growth rate, not the actual time.

5. **Ignoring space complexity.** An algorithm can be O(n) in time but O(n^2) in space. Always consider both.

---

## Key Takeaways

- Big-O describes how an algorithm's performance **scales** with input size.
- It focuses on the **dominant term** and ignores constants.
- Common complexities from fastest to slowest: O(1) < O(log n) < O(n) < O(n log n) < O(n^2) < O(2^n) < O(n!).
- Nested loops that both depend on n usually mean O(n^2).
- Sequential operations are added; nested operations are multiplied.
- Always think about both **time complexity** and **space complexity**.
- In practice, algorithms with O(n log n) or better are considered efficient for most applications.
- Big-O is about the **worst case** unless explicitly stated otherwise.
