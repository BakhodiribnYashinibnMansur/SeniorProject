# Polynomial Time O(n^2), O(n^3) -- Tasks

## Table of Contents

1. [Task 1: Implement Bubble Sort](#task-1-implement-bubble-sort)
2. [Task 2: Implement Selection Sort](#task-2-implement-selection-sort)
3. [Task 3: Implement Insertion Sort](#task-3-implement-insertion-sort)
4. [Task 4: Two Sum Brute Force](#task-4-two-sum-brute-force)
5. [Task 5: Check for Duplicates](#task-5-check-for-duplicates)
6. [Task 6: Naive Matrix Multiplication](#task-6-naive-matrix-multiplication)
7. [Task 7: Floyd-Warshall Shortest Paths](#task-7-floyd-warshall-shortest-paths)
8. [Task 8: Three Sum Brute Force](#task-8-three-sum-brute-force)
9. [Task 9: Count Inversions (Brute Force)](#task-9-count-inversions-brute-force)
10. [Task 10: Maximum Subarray Sum (Brute Force)](#task-10-maximum-subarray-sum-brute-force)
11. [Task 11: Closest Pair of Points](#task-11-closest-pair-of-points)
12. [Task 12: String Matching (Brute Force)](#task-12-string-matching-brute-force)
13. [Task 13: Matrix Transposition In-Place](#task-13-matrix-transposition-in-place)
14. [Task 14: All-Pairs Hamming Distance](#task-14-all-pairs-hamming-distance)
15. [Task 15: Polynomial Evaluation](#task-15-polynomial-evaluation)
16. [Benchmark Task: Compare Quadratic vs Linearithmic](#benchmark-task-compare-quadratic-vs-linearithmic)

---

## Task 1: Implement Bubble Sort

**Difficulty:** Easy
**Complexity:** O(n^2) average/worst, O(n) best

Implement bubble sort with early termination (stop if no swaps in a pass).

```go
// Go
func bubbleSort(arr []int) []int {
    n := len(arr)
    result := make([]int, n)
    copy(result, arr)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            if result[j] > result[j+1] {
                result[j], result[j+1] = result[j+1], result[j]
                swapped = true
            }
        }
        if !swapped {
            break
        }
    }
    return result
}
```

```java
// Java
int[] bubbleSort(int[] arr) {
    int[] result = arr.clone();
    int n = result.length;
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - 1 - i; j++) {
            if (result[j] > result[j + 1]) {
                int temp = result[j];
                result[j] = result[j + 1];
                result[j + 1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break;
    }
    return result;
}
```

```python
# Python
def bubble_sort(arr):
    result = arr[:]
    n = len(result)
    for i in range(n - 1):
        swapped = False
        for j in range(n - 1 - i):
            if result[j] > result[j + 1]:
                result[j], result[j + 1] = result[j + 1], result[j]
                swapped = True
        if not swapped:
            break
    return result
```

**Test cases:**
- `[5, 3, 1, 4, 2]` -> `[1, 2, 3, 4, 5]`
- `[1, 2, 3, 4, 5]` -> `[1, 2, 3, 4, 5]` (best case: 1 pass)
- `[5, 4, 3, 2, 1]` -> `[1, 2, 3, 4, 5]` (worst case)

---

## Task 2: Implement Selection Sort

**Difficulty:** Easy
**Complexity:** O(n^2) all cases

```go
// Go
func selectionSort(arr []int) []int {
    n := len(arr)
    result := make([]int, n)
    copy(result, arr)
    for i := 0; i < n-1; i++ {
        minIdx := i
        for j := i + 1; j < n; j++ {
            if result[j] < result[minIdx] {
                minIdx = j
            }
        }
        result[i], result[minIdx] = result[minIdx], result[i]
    }
    return result
}
```

```java
// Java
int[] selectionSort(int[] arr) {
    int[] result = arr.clone();
    int n = result.length;
    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < n; j++) {
            if (result[j] < result[minIdx]) minIdx = j;
        }
        int temp = result[i];
        result[i] = result[minIdx];
        result[minIdx] = temp;
    }
    return result;
}
```

```python
# Python
def selection_sort(arr):
    result = arr[:]
    n = len(result)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if result[j] < result[min_idx]:
                min_idx = j
        result[i], result[min_idx] = result[min_idx], result[i]
    return result
```

**Test:** Verify exactly n-1 swaps are performed regardless of input order.

---

## Task 3: Implement Insertion Sort

**Difficulty:** Easy
**Complexity:** O(n^2) worst, O(n) best

```go
// Go
func insertionSort(arr []int) []int {
    result := make([]int, len(arr))
    copy(result, arr)
    for i := 1; i < len(result); i++ {
        key := result[i]
        j := i - 1
        for j >= 0 && result[j] > key {
            result[j+1] = result[j]
            j--
        }
        result[j+1] = key
    }
    return result
}
```

```java
// Java
int[] insertionSort(int[] arr) {
    int[] result = arr.clone();
    for (int i = 1; i < result.length; i++) {
        int key = result[i];
        int j = i - 1;
        while (j >= 0 && result[j] > key) {
            result[j + 1] = result[j];
            j--;
        }
        result[j + 1] = key;
    }
    return result;
}
```

```python
# Python
def insertion_sort(arr):
    result = arr[:]
    for i in range(1, len(result)):
        key = result[i]
        j = i - 1
        while j >= 0 and result[j] > key:
            result[j + 1] = result[j]
            j -= 1
        result[j + 1] = key
    return result
```

**Extra challenge:** Count the number of shifts performed and verify it equals the
number of inversions in the original array.

---

## Task 4: Two Sum Brute Force

**Difficulty:** Easy
**Complexity:** O(n^2)

Given an array and a target, return indices of two numbers that sum to target.

```go
// Go
func twoSum(nums []int, target int) [2]int {
    for i := 0; i < len(nums); i++ {
        for j := i + 1; j < len(nums); j++ {
            if nums[i]+nums[j] == target {
                return [2]int{i, j}
            }
        }
    }
    return [2]int{-1, -1}
}
```

```java
// Java
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
# Python
def two_sum(nums, target):
    for i in range(len(nums)):
        for j in range(i + 1, len(nums)):
            if nums[i] + nums[j] == target:
                return [i, j]
    return [-1, -1]
```

**Test:** `[2, 7, 11, 15]`, target=9 -> `[0, 1]`

---

## Task 5: Check for Duplicates

**Difficulty:** Easy
**Complexity:** O(n^2) brute force

```go
// Go
func containsDuplicate(arr []int) bool {
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
// Java
boolean containsDuplicate(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[i] == arr[j]) return true;
        }
    }
    return false;
}
```

```python
# Python
def contains_duplicate(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False
```

**Extra:** Implement the O(n) version using a hash set for comparison.

---

## Task 6: Naive Matrix Multiplication

**Difficulty:** Medium
**Complexity:** O(n^3)

Multiply two n x n matrices.

```go
// Go
func matMul(a, b [][]int) [][]int {
    n := len(a)
    c := make([][]int, n)
    for i := 0; i < n; i++ {
        c[i] = make([]int, n)
        for j := 0; j < n; j++ {
            for k := 0; k < n; k++ {
                c[i][j] += a[i][k] * b[k][j]
            }
        }
    }
    return c
}
```

```java
// Java
int[][] matMul(int[][] a, int[][] b) {
    int n = a.length;
    int[][] c = new int[n][n];
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                c[i][j] += a[i][k] * b[k][j];
            }
        }
    }
    return c;
}
```

```python
# Python
def mat_mul(a, b):
    n = len(a)
    c = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            for k in range(n):
                c[i][j] += a[i][k] * b[k][j]
    return c
```

**Test:** 2x2 identity matrix multiplication, 3x3 known result.

---

## Task 7: Floyd-Warshall Shortest Paths

**Difficulty:** Medium
**Complexity:** O(V^3)

```go
// Go
const INF = 1<<31 - 1

func floydWarshall(graph [][]int) [][]int {
    n := len(graph)
    dist := make([][]int, n)
    for i := 0; i < n; i++ {
        dist[i] = make([]int, n)
        copy(dist[i], graph[i])
    }
    for k := 0; k < n; k++ {
        for i := 0; i < n; i++ {
            for j := 0; j < n; j++ {
                if dist[i][k] != INF && dist[k][j] != INF &&
                    dist[i][k]+dist[k][j] < dist[i][j] {
                    dist[i][j] = dist[i][k] + dist[k][j]
                }
            }
        }
    }
    return dist
}
```

```java
// Java
int[][] floydWarshall(int[][] graph) {
    int n = graph.length;
    int INF = Integer.MAX_VALUE / 2;
    int[][] dist = new int[n][n];
    for (int i = 0; i < n; i++) {
        System.arraycopy(graph[i], 0, dist[i], 0, n);
    }
    for (int k = 0; k < n; k++) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][k] + dist[k][j] < dist[i][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }
    }
    return dist;
}
```

```python
# Python
def floyd_warshall(graph):
    n = len(graph)
    dist = [row[:] for row in graph]
    INF = float('inf')
    for k in range(n):
        for i in range(n):
            for j in range(n):
                if dist[i][k] + dist[k][j] < dist[i][j]:
                    dist[i][j] = dist[i][k] + dist[k][j]
    return dist
```

**Test with graph:**
```
  0 -> 1 (weight 3)
  0 -> 2 (weight 8)
  1 -> 2 (weight 2)
  2 -> 0 (weight 5)
```

---

## Task 8: Three Sum Brute Force

**Difficulty:** Medium
**Complexity:** O(n^3)

Find all unique triplets that sum to zero.

```go
// Go
func threeSum(nums []int) [][]int {
    sort.Ints(nums)
    result := [][]int{}
    n := len(nums)
    for i := 0; i < n-2; i++ {
        if i > 0 && nums[i] == nums[i-1] {
            continue
        }
        for j := i + 1; j < n-1; j++ {
            if j > i+1 && nums[j] == nums[j-1] {
                continue
            }
            for k := j + 1; k < n; k++ {
                if k > j+1 && nums[k] == nums[k-1] {
                    continue
                }
                if nums[i]+nums[j]+nums[k] == 0 {
                    result = append(result, []int{nums[i], nums[j], nums[k]})
                }
            }
        }
    }
    return result
}
```

```java
// Java
List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;
    for (int i = 0; i < n - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        for (int j = i + 1; j < n - 1; j++) {
            if (j > i + 1 && nums[j] == nums[j - 1]) continue;
            for (int k = j + 1; k < n; k++) {
                if (k > j + 1 && nums[k] == nums[k - 1]) continue;
                if (nums[i] + nums[j] + nums[k] == 0) {
                    result.add(Arrays.asList(nums[i], nums[j], nums[k]));
                }
            }
        }
    }
    return result;
}
```

```python
# Python
def three_sum(nums):
    nums.sort()
    result = []
    n = len(nums)
    for i in range(n - 2):
        if i > 0 and nums[i] == nums[i - 1]:
            continue
        for j in range(i + 1, n - 1):
            if j > i + 1 and nums[j] == nums[j - 1]:
                continue
            for k in range(j + 1, n):
                if k > j + 1 and nums[k] == nums[k - 1]:
                    continue
                if nums[i] + nums[j] + nums[k] == 0:
                    result.append([nums[i], nums[j], nums[k]])
    return result
```

**Test:** `[-1, 0, 1, 2, -1, -4]` -> `[[-1, -1, 2], [-1, 0, 1]]`

---

## Task 9: Count Inversions (Brute Force)

**Difficulty:** Medium
**Complexity:** O(n^2)

```go
// Go
func countInversions(arr []int) int {
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
int countInversions(int[] arr) {
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
def count_inversions(arr):
    count = 0
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] > arr[j]:
                count += 1
    return count
```

**Test:** `[2, 4, 1, 3, 5]` -> 3 inversions: (2,1), (4,1), (4,3)

---

## Task 10: Maximum Subarray Sum (Brute Force)

**Difficulty:** Easy
**Complexity:** O(n^2)

```go
// Go
func maxSubarraySum(arr []int) int {
    maxSum := arr[0]
    for i := 0; i < len(arr); i++ {
        sum := 0
        for j := i; j < len(arr); j++ {
            sum += arr[j]
            if sum > maxSum {
                maxSum = sum
            }
        }
    }
    return maxSum
}
```

```java
// Java
int maxSubarraySum(int[] arr) {
    int maxSum = arr[0];
    for (int i = 0; i < arr.length; i++) {
        int sum = 0;
        for (int j = i; j < arr.length; j++) {
            sum += arr[j];
            maxSum = Math.max(maxSum, sum);
        }
    }
    return maxSum;
}
```

```python
# Python
def max_subarray_sum(arr):
    max_sum = arr[0]
    for i in range(len(arr)):
        total = 0
        for j in range(i, len(arr)):
            total += arr[j]
            max_sum = max(max_sum, total)
    return max_sum
```

**Test:** `[-2, 1, -3, 4, -1, 2, 1, -5, 4]` -> 6 (subarray `[4, -1, 2, 1]`)

---

## Task 11: Closest Pair of Points

**Difficulty:** Medium
**Complexity:** O(n^2)

```go
// Go
func closestPair(points [][2]float64) (float64, [2]int) {
    n := len(points)
    minDist := math.MaxFloat64
    pair := [2]int{-1, -1}
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            dx := points[i][0] - points[j][0]
            dy := points[i][1] - points[j][1]
            dist := math.Sqrt(dx*dx + dy*dy)
            if dist < minDist {
                minDist = dist
                pair = [2]int{i, j}
            }
        }
    }
    return minDist, pair
}
```

```java
// Java
double[] closestPair(double[][] points) {
    int n = points.length;
    double minDist = Double.MAX_VALUE;
    int pi = -1, pj = -1;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            double dx = points[i][0] - points[j][0];
            double dy = points[i][1] - points[j][1];
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < minDist) {
                minDist = dist;
                pi = i; pj = j;
            }
        }
    }
    return new double[]{minDist, pi, pj};
}
```

```python
# Python
import math

def closest_pair(points):
    n = len(points)
    min_dist = float('inf')
    pair = (-1, -1)
    for i in range(n):
        for j in range(i + 1, n):
            dx = points[i][0] - points[j][0]
            dy = points[i][1] - points[j][1]
            dist = math.sqrt(dx * dx + dy * dy)
            if dist < min_dist:
                min_dist = dist
                pair = (i, j)
    return min_dist, pair
```

---

## Task 12: String Matching (Brute Force)

**Difficulty:** Medium
**Complexity:** O(n*m) where n=text length, m=pattern length

```go
// Go
func bruteForceSearch(text, pattern string) []int {
    positions := []int{}
    n, m := len(text), len(pattern)
    for i := 0; i <= n-m; i++ {
        match := true
        for j := 0; j < m; j++ {
            if text[i+j] != pattern[j] {
                match = false
                break
            }
        }
        if match {
            positions = append(positions, i)
        }
    }
    return positions
}
```

```java
// Java
List<Integer> bruteForceSearch(String text, String pattern) {
    List<Integer> positions = new ArrayList<>();
    int n = text.length(), m = pattern.length();
    for (int i = 0; i <= n - m; i++) {
        boolean match = true;
        for (int j = 0; j < m; j++) {
            if (text.charAt(i + j) != pattern.charAt(j)) {
                match = false;
                break;
            }
        }
        if (match) positions.add(i);
    }
    return positions;
}
```

```python
# Python
def brute_force_search(text, pattern):
    positions = []
    n, m = len(text), len(pattern)
    for i in range(n - m + 1):
        if text[i:i+m] == pattern:
            positions.append(i)
    return positions
```

**Test:** `"abcabcabc"`, pattern `"abc"` -> `[0, 3, 6]`

---

## Task 13: Matrix Transposition In-Place

**Difficulty:** Easy
**Complexity:** O(n^2)

```go
// Go
func transpose(matrix [][]int) {
    n := len(matrix)
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
        }
    }
}
```

```java
// Java
void transpose(int[][] matrix) {
    int n = matrix.length;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            int temp = matrix[i][j];
            matrix[i][j] = matrix[j][i];
            matrix[j][i] = temp;
        }
    }
}
```

```python
# Python
def transpose(matrix):
    n = len(matrix)
    for i in range(n):
        for j in range(i + 1, n):
            matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
```

---

## Task 14: All-Pairs Hamming Distance

**Difficulty:** Medium
**Complexity:** O(n^2 * b) where b = bit width

```go
// Go
func allPairsHamming(nums []int) [][]int {
    n := len(nums)
    dist := make([][]int, n)
    for i := 0; i < n; i++ {
        dist[i] = make([]int, n)
        for j := i + 1; j < n; j++ {
            d := bits.OnesCount(uint(nums[i] ^ nums[j]))
            dist[i][j] = d
            dist[j][i] = d
        }
    }
    return dist
}
```

```java
// Java
int[][] allPairsHamming(int[] nums) {
    int n = nums.length;
    int[][] dist = new int[n][n];
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            int d = Integer.bitCount(nums[i] ^ nums[j]);
            dist[i][j] = d;
            dist[j][i] = d;
        }
    }
    return dist;
}
```

```python
# Python
def all_pairs_hamming(nums):
    n = len(nums)
    dist = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(i + 1, n):
            d = bin(nums[i] ^ nums[j]).count('1')
            dist[i][j] = d
            dist[j][i] = d
    return dist
```

---

## Task 15: Polynomial Evaluation

**Difficulty:** Easy
**Complexity:** O(n^2) naive, O(n) with Horner's method

Evaluate polynomial a[0] + a[1]*x + a[2]*x^2 + ... + a[n-1]*x^(n-1).

```go
// Go -- O(n^2) naive
func evalPolyNaive(coeffs []float64, x float64) float64 {
    result := 0.0
    for i, c := range coeffs {
        power := 1.0
        for j := 0; j < i; j++ {
            power *= x
        }
        result += c * power
    }
    return result
}

// O(n) Horner's method
func evalPolyHorner(coeffs []float64, x float64) float64 {
    result := 0.0
    for i := len(coeffs) - 1; i >= 0; i-- {
        result = result*x + coeffs[i]
    }
    return result
}
```

```java
// Java -- O(n^2) naive
double evalPolyNaive(double[] coeffs, double x) {
    double result = 0;
    for (int i = 0; i < coeffs.length; i++) {
        double power = 1;
        for (int j = 0; j < i; j++) power *= x;
        result += coeffs[i] * power;
    }
    return result;
}

// O(n) Horner's method
double evalPolyHorner(double[] coeffs, double x) {
    double result = 0;
    for (int i = coeffs.length - 1; i >= 0; i--) {
        result = result * x + coeffs[i];
    }
    return result;
}
```

```python
# Python -- O(n^2) naive
def eval_poly_naive(coeffs, x):
    result = 0
    for i, c in enumerate(coeffs):
        result += c * (x ** i)  # x**i is O(i) naive
    return result

# O(n) Horner's method
def eval_poly_horner(coeffs, x):
    result = 0
    for c in reversed(coeffs):
        result = result * x + c
    return result
```

---

## Benchmark Task: Compare Quadratic vs Linearithmic

Write a benchmark that compares the O(n^2) and O(n log n) approaches for counting
pairs that sum to less than a target.

```go
// Go
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

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

func countPairsSorted(arr []int, target int) int {
    sorted := make([]int, len(arr))
    copy(sorted, arr)
    sort.Ints(sorted)
    count := 0
    left, right := 0, len(sorted)-1
    for left < right {
        if sorted[left]+sorted[right] < target {
            count += right - left
            left++
        } else {
            right--
        }
    }
    return count
}

func main() {
    for _, n := range []int{1000, 2000, 5000, 10000, 20000} {
        arr := make([]int, n)
        for i := range arr {
            arr[i] = rand.Intn(n)
        }
        target := n

        start := time.Now()
        r1 := countPairsBrute(arr, target)
        t1 := time.Since(start)

        start = time.Now()
        r2 := countPairsSorted(arr, target)
        t2 := time.Since(start)

        fmt.Printf("n=%5d  Brute:%10v  Sorted:%10v  Speedup:%.1fx  Match:%v\n",
            n, t1, t2, float64(t1)/float64(t2), r1 == r2)
    }
}
```

```java
// Java
import java.util.*;

public class PairBenchmark {
    static int countPairsBrute(int[] arr, int target) {
        int count = 0;
        for (int i = 0; i < arr.length; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] + arr[j] < target) count++;
        return count;
    }

    static int countPairsSorted(int[] arr, int target) {
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        int count = 0, left = 0, right = sorted.length - 1;
        while (left < right) {
            if (sorted[left] + sorted[right] < target) {
                count += right - left;
                left++;
            } else {
                right--;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Random rng = new Random(42);
        for (int n : new int[]{1000, 2000, 5000, 10000, 20000}) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rng.nextInt(n);
            int target = n;

            long start = System.nanoTime();
            int r1 = countPairsBrute(arr, target);
            long t1 = System.nanoTime() - start;

            start = System.nanoTime();
            int r2 = countPairsSorted(arr, target);
            long t2 = System.nanoTime() - start;

            System.out.printf("n=%5d  Brute:%10d ns  Sorted:%10d ns  Speedup:%.1fx  Match:%b%n",
                n, t1, t2, (double)t1/t2, r1 == r2);
        }
    }
}
```

```python
# Python
import random
import time

def count_pairs_brute(arr, target):
    count = 0
    n = len(arr)
    for i in range(n):
        for j in range(i + 1, n):
            if arr[i] + arr[j] < target:
                count += 1
    return count

def count_pairs_sorted(arr, target):
    sorted_arr = sorted(arr)
    count = 0
    left, right = 0, len(sorted_arr) - 1
    while left < right:
        if sorted_arr[left] + sorted_arr[right] < target:
            count += right - left
            left += 1
        else:
            right -= 1
    return count

for n in [1000, 2000, 5000, 10000, 20000]:
    arr = [random.randint(0, n) for _ in range(n)]
    target = n

    start = time.perf_counter()
    r1 = count_pairs_brute(arr, target)
    t1 = time.perf_counter() - start

    start = time.perf_counter()
    r2 = count_pairs_sorted(arr, target)
    t2 = time.perf_counter() - start

    speedup = t1 / t2 if t2 > 0 else float('inf')
    print(f"n={n:5d}  Brute:{t1:10.4f}s  Sorted:{t2:10.4f}s  "
          f"Speedup:{speedup:.1f}x  Match:{r1 == r2}")
```

**Expected observation:** The speedup grows with n. At n=20000, the sorted version
should be 100x+ faster.
