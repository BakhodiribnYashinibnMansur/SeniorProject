# Polynomial Time O(n^2), O(n^3) -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Is Polynomial Time?](#what-is-polynomial-time)
3. [Understanding Nested Loops](#understanding-nested-loops)
4. [O(n^2) -- Quadratic Time](#on2--quadratic-time)
5. [O(n^3) -- Cubic Time](#on3--cubic-time)
6. [Classic O(n^2) Sorting Algorithms](#classic-on2-sorting-algorithms)
   - [Bubble Sort](#bubble-sort)
   - [Selection Sort](#selection-sort)
   - [Insertion Sort](#insertion-sort)
7. [Matrix Multiplication O(n^3)](#matrix-multiplication-on3)
8. [Brute Force Pair Problems](#brute-force-pair-problems)
9. [Real-World Analogies](#real-world-analogies)
10. [How Fast Does It Grow?](#how-fast-does-it-grow)
11. [Summary](#summary)

---

## Introduction

When we talk about algorithmic complexity, **polynomial time** refers to algorithms
whose running time can be described by a polynomial function of the input size n.
The most commonly encountered polynomial runtimes are **O(n^2)** (quadratic) and
**O(n^3)** (cubic). These are the first runtimes where you really start to *feel*
the slowdown as input grows.

If O(n) is walking in a straight line, O(n^2) is walking across every cell of a
chessboard, and O(n^3) is walking through every cell of a Rubik's Cube.

---

## What Is Polynomial Time?

A polynomial time algorithm has a runtime of the form O(n^k) where k is a constant.

| Complexity | Name       | Example                        |
|------------|------------|--------------------------------|
| O(n^1)     | Linear     | Single loop through array      |
| O(n^2)     | Quadratic  | Nested loop (2 levels)         |
| O(n^3)     | Cubic      | Triple nested loop             |
| O(n^4)     | Quartic    | Four nested loops              |

In practice, **O(n^2)** and **O(n^3)** are by far the most common. Higher powers
are rare and usually indicate the algorithm needs a better approach.

**Key rule of thumb:**
- O(n^2) is manageable for n up to ~10,000
- O(n^3) is manageable for n up to ~1,000
- Beyond that, you need a faster algorithm

---

## Understanding Nested Loops

The most direct way to create polynomial time is through **nested loops**. Each
level of nesting multiplies the work by n.

### Single Loop = O(n)

```go
// Go
func printAll(arr []int) {
    for i := 0; i < len(arr); i++ {
        fmt.Println(arr[i])
    }
}
```

```java
// Java
void printAll(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        System.out.println(arr[i]);
    }
}
```

```python
# Python
def print_all(arr):
    for item in arr:
        print(item)
```

**Operations:** n iterations = O(n)

### Two Nested Loops = O(n^2)

```go
// Go
func printAllPairs(arr []int) {
    for i := 0; i < len(arr); i++ {
        for j := 0; j < len(arr); j++ {
            fmt.Printf("(%d, %d) ", arr[i], arr[j])
        }
    }
}
```

```java
// Java
void printAllPairs(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = 0; j < arr.length; j++) {
            System.out.printf("(%d, %d) ", arr[i], arr[j]);
        }
    }
}
```

```python
# Python
def print_all_pairs(arr):
    for i in arr:
        for j in arr:
            print(f"({i}, {j})", end=" ")
```

**Operations:** n x n = n^2 iterations = O(n^2)

### Three Nested Loops = O(n^3)

```go
// Go
func printAllTriplets(arr []int) {
    for i := 0; i < len(arr); i++ {
        for j := 0; j < len(arr); j++ {
            for k := 0; k < len(arr); k++ {
                fmt.Printf("(%d, %d, %d) ", arr[i], arr[j], arr[k])
            }
        }
    }
}
```

```java
// Java
void printAllTriplets(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = 0; j < arr.length; j++) {
            for (int k = 0; k < arr.length; k++) {
                System.out.printf("(%d, %d, %d) ", arr[i], arr[j], arr[k]);
            }
        }
    }
}
```

```python
# Python
def print_all_triplets(arr):
    for i in arr:
        for j in arr:
            for k in arr:
                print(f"({i}, {j}, {k})", end=" ")
```

**Operations:** n x n x n = n^3 iterations = O(n^3)

---

## O(n^2) -- Quadratic Time

Quadratic time is the hallmark of algorithms that compare every element with every
other element. You will see it in:

- **Sorting algorithms** (bubble, selection, insertion in worst case)
- **Searching for pairs** that satisfy some condition
- **Checking for duplicates** without a hash set
- **Brute force** approaches to many problems

### Recognizing O(n^2) in Code

The telltale sign is **two nested loops**, both iterating over the input:

```go
// Go -- Check if array has duplicates (brute force)
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

```java
// Java -- Check if array has duplicates (brute force)
boolean hasDuplicate(int[] arr) {
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

```python
# Python -- Check if array has duplicates (brute force)
def has_duplicate(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False
```

Even though j starts at i+1 (so the inner loop gets shorter each time), the total
number of comparisons is n*(n-1)/2, which simplifies to O(n^2).

---

## O(n^3) -- Cubic Time

Cubic time appears when you need **three nested loops** over the input. This is
less common but shows up in:

- **Naive matrix multiplication**
- **Floyd-Warshall shortest path** algorithm
- **Some dynamic programming** problems
- **Brute force triplet** searches

### Example: Finding Three Numbers That Sum to Zero

```go
// Go -- Three Sum brute force O(n^3)
func threeSum(arr []int) [][]int {
    result := [][]int{}
    n := len(arr)
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            for k := j + 1; k < n; k++ {
                if arr[i]+arr[j]+arr[k] == 0 {
                    result = append(result, []int{arr[i], arr[j], arr[k]})
                }
            }
        }
    }
    return result
}
```

```java
// Java -- Three Sum brute force O(n^3)
List<int[]> threeSum(int[] arr) {
    List<int[]> result = new ArrayList<>();
    int n = arr.length;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            for (int k = j + 1; k < n; k++) {
                if (arr[i] + arr[j] + arr[k] == 0) {
                    result.add(new int[]{arr[i], arr[j], arr[k]});
                }
            }
        }
    }
    return result;
}
```

```python
# Python -- Three Sum brute force O(n^3)
def three_sum(arr):
    result = []
    n = len(arr)
    for i in range(n):
        for j in range(i + 1, n):
            for k in range(j + 1, n):
                if arr[i] + arr[j] + arr[k] == 0:
                    result.append([arr[i], arr[j], arr[k]])
    return result
```

---

## Classic O(n^2) Sorting Algorithms

### Bubble Sort

**How it works:** Repeatedly walk through the list, compare adjacent elements,
and swap them if they are in the wrong order. After each pass, the largest unsorted
element "bubbles" to its correct position.

**Analogy:** Imagine bubbles rising in a glass of water -- the biggest bubbles
(largest numbers) rise to the top first.

```go
// Go -- Bubble Sort
func bubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                swapped = true
            }
        }
        if !swapped {
            break // Already sorted
        }
    }
}
```

```java
// Java -- Bubble Sort
void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - 1 - i; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break;
    }
}
```

```python
# Python -- Bubble Sort
def bubble_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        swapped = False
        for j in range(n - 1 - i):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped:
            break
```

| Case    | Complexity |
|---------|-----------|
| Best    | O(n) -- already sorted, one pass with no swaps |
| Average | O(n^2)    |
| Worst   | O(n^2) -- reverse sorted |

### Selection Sort

**How it works:** Find the minimum element in the unsorted portion and place it
at the beginning. Repeat for the remaining unsorted portion.

**Analogy:** Picking cards for a hand -- you scan all remaining cards, pick the
smallest, and place it next in your sorted hand.

```go
// Go -- Selection Sort
func selectionSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        minIdx := i
        for j := i + 1; j < n; j++ {
            if arr[j] < arr[minIdx] {
                minIdx = j
            }
        }
        arr[i], arr[minIdx] = arr[minIdx], arr[i]
    }
}
```

```java
// Java -- Selection Sort
void selectionSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < n; j++) {
            if (arr[j] < arr[minIdx]) {
                minIdx = j;
            }
        }
        int temp = arr[i];
        arr[i] = arr[minIdx];
        arr[minIdx] = temp;
    }
}
```

```python
# Python -- Selection Sort
def selection_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]:
                min_idx = j
        arr[i], arr[min_idx] = arr[min_idx], arr[i]
```

| Case    | Complexity |
|---------|-----------|
| Best    | O(n^2) -- always scans entire unsorted portion |
| Average | O(n^2)    |
| Worst   | O(n^2)    |

### Insertion Sort

**How it works:** Build a sorted portion one element at a time. Take the next
unsorted element and insert it into the correct position in the sorted portion.

**Analogy:** Sorting playing cards in your hand -- you pick up each card and
slide it into the right place among the cards you already hold.

```go
// Go -- Insertion Sort
func insertionSort(arr []int) {
    n := len(arr)
    for i := 1; i < n; i++ {
        key := arr[i]
        j := i - 1
        for j >= 0 && arr[j] > key {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = key
    }
}
```

```java
// Java -- Insertion Sort
void insertionSort(int[] arr) {
    int n = arr.length;
    for (int i = 1; i < n; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

```python
# Python -- Insertion Sort
def insertion_sort(arr):
    n = len(arr)
    for i in range(1, n):
        key = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > key:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key
```

| Case    | Complexity |
|---------|-----------|
| Best    | O(n) -- already sorted |
| Average | O(n^2)    |
| Worst   | O(n^2) -- reverse sorted |

**Fun fact:** Insertion sort is the fastest O(n^2) sort for *nearly sorted* data
and for *small arrays* (n < 20). Many optimized sorting libraries use insertion
sort as a subroutine for small partitions.

---

## Matrix Multiplication O(n^3)

Multiplying two n x n matrices using the standard algorithm requires three nested
loops: one for each row, one for each column, and one for the dot product.

```go
// Go -- Naive Matrix Multiplication O(n^3)
func matMul(a, b [][]int) [][]int {
    n := len(a)
    result := make([][]int, n)
    for i := 0; i < n; i++ {
        result[i] = make([]int, n)
        for j := 0; j < n; j++ {
            sum := 0
            for k := 0; k < n; k++ {
                sum += a[i][k] * b[k][j]
            }
            result[i][j] = sum
        }
    }
    return result
}
```

```java
// Java -- Naive Matrix Multiplication O(n^3)
int[][] matMul(int[][] a, int[][] b) {
    int n = a.length;
    int[][] result = new int[n][n];
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int k = 0; k < n; k++) {
                sum += a[i][k] * b[k][j];
            }
            result[i][j] = sum;
        }
    }
    return result;
}
```

```python
# Python -- Naive Matrix Multiplication O(n^3)
def mat_mul(a, b):
    n = len(a)
    result = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            total = 0
            for k in range(n):
                total += a[i][k] * b[k][j]
            result[i][j] = total
    return result
```

**Why O(n^3)?** For each of the n^2 entries in the result matrix, we compute a
dot product of length n. Total: n^2 * n = n^3.

---

## Brute Force Pair Problems

Many problems have a natural O(n^2) brute force solution that checks all pairs.

### Two Sum (Brute Force)

Given an array, find two numbers that add up to a target.

```go
// Go -- Two Sum brute force O(n^2)
func twoSum(nums []int, target int) (int, int) {
    for i := 0; i < len(nums); i++ {
        for j := i + 1; j < len(nums); j++ {
            if nums[i]+nums[j] == target {
                return i, j
            }
        }
    }
    return -1, -1
}
```

```java
// Java -- Two Sum brute force O(n^2)
int[] twoSum(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++) {
        for (int j = i + 1; j < nums.length; j++) {
            if (nums[i] + nums[j] == target) {
                return new int[]{i, j};
            }
        }
    }
    return new int[]{-1, -1};
}
```

```python
# Python -- Two Sum brute force O(n^2)
def two_sum(nums, target):
    for i in range(len(nums)):
        for j in range(i + 1, len(nums)):
            if nums[i] + nums[j] == target:
                return [i, j]
    return [-1, -1]
```

> Note: Two Sum can be solved in O(n) using a hash map, which is the classic
> optimization from O(n^2) to O(n).

### Closest Pair of Points (Brute Force)

```go
// Go -- Closest pair brute force O(n^2)
func closestPair(points [][2]float64) float64 {
    minDist := math.MaxFloat64
    n := len(points)
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            dx := points[i][0] - points[j][0]
            dy := points[i][1] - points[j][1]
            dist := math.Sqrt(dx*dx + dy*dy)
            if dist < minDist {
                minDist = dist
            }
        }
    }
    return minDist
}
```

```java
// Java -- Closest pair brute force O(n^2)
double closestPair(double[][] points) {
    double minDist = Double.MAX_VALUE;
    int n = points.length;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            double dx = points[i][0] - points[j][0];
            double dy = points[i][1] - points[j][1];
            double dist = Math.sqrt(dx * dx + dy * dy);
            minDist = Math.min(minDist, dist);
        }
    }
    return minDist;
}
```

```python
# Python -- Closest pair brute force O(n^2)
import math

def closest_pair(points):
    min_dist = float('inf')
    n = len(points)
    for i in range(n):
        for j in range(i + 1, n):
            dx = points[i][0] - points[j][0]
            dy = points[i][1] - points[j][1]
            dist = math.sqrt(dx * dx + dy * dy)
            min_dist = min(min_dist, dist)
    return min_dist
```

> Note: Closest pair of points can be solved in O(n log n) using divide and conquer.

---

## Real-World Analogies

### O(n^2) -- The Handshake Problem

Imagine n people in a room. If every person must shake hands with every other
person, the total number of handshakes is n*(n-1)/2. This is O(n^2).

- 10 people = 45 handshakes
- 100 people = 4,950 handshakes
- 1,000 people = 499,500 handshakes
- 10,000 people = 49,995,000 handshakes

### O(n^2) -- Comparing Homework

A teacher has n students. To check if any two students copied from each other,
the teacher compares every pair of papers. With 30 students, that is 435 comparisons.
With 100 students, it is 4,950.

### O(n^3) -- Round-Robin Tournament Scheduling

Consider scheduling games in a tournament where you need to account for n teams,
n rounds, and n venues. The complexity of checking all possible assignments
grows cubically.

### O(n^3) -- Building a 3D Structure

Think of filling a cube made of n x n x n small blocks. You must visit each block
once, touching n^3 total blocks.

- n = 10: 1,000 blocks
- n = 100: 1,000,000 blocks
- n = 1,000: 1,000,000,000 blocks (1 billion!)

---

## How Fast Does It Grow?

| n      | O(n)      | O(n^2)         | O(n^3)              |
|--------|-----------|----------------|---------------------|
| 10     | 10        | 100            | 1,000               |
| 100    | 100       | 10,000         | 1,000,000           |
| 1,000  | 1,000     | 1,000,000      | 1,000,000,000       |
| 10,000 | 10,000    | 100,000,000    | 1,000,000,000,000   |

At 10^8 operations per second (rough estimate for modern hardware):

| n      | O(n)      | O(n^2)    | O(n^3)         |
|--------|-----------|-----------|----------------|
| 1,000  | 0.00001s  | 0.01s     | 10s            |
| 10,000 | 0.0001s   | 1s        | ~2.7 hours     |
| 100,000| 0.001s    | ~17 min   | ~31.7 years    |

This table makes it very clear: **polynomial growth is deceptive**. It seems
manageable for small inputs but explodes rapidly.

---

## Summary

1. **Polynomial time** means O(n^k) for some constant k. The most common are
   O(n^2) and O(n^3).

2. **Nested loops** are the primary source of polynomial time:
   - 2 nested loops over n elements = O(n^2)
   - 3 nested loops over n elements = O(n^3)

3. **Classic O(n^2) sorts:** Bubble sort, selection sort, and insertion sort all
   have O(n^2) worst-case time. They are simple to implement but too slow for
   large inputs.

4. **O(n^3) examples:** Naive matrix multiplication, Floyd-Warshall shortest
   paths, brute force 3Sum.

5. **Practical limits:**
   - O(n^2) is usable for n up to about 10,000
   - O(n^3) is usable for n up to about 1,000

6. Many O(n^2) problems have O(n log n) or O(n) solutions. Learning to recognize
   and apply these optimizations is a core skill in algorithm design.

---

> **Next steps:** Move on to the [middle.md](middle.md) for deeper analysis of
> when O(n^2) is acceptable and how to optimize polynomial-time algorithms.
