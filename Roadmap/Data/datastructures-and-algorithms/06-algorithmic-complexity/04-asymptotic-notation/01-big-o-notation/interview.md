# Big-O Notation -- Interview Questions

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Analyze the Complexity Questions](#analyze-the-complexity-questions)
3. [Coding Challenge: Analyze 5 Functions](#coding-challenge-analyze-5-functions)
4. [Tricky Scenario Questions](#tricky-scenario-questions)
5. [Answers](#answers)

---

## Conceptual Questions

### Question 1: Explain Big-O in One Sentence
**What is Big-O notation, and why do we use it?**

**Expected Answer:** Big-O notation describes the upper bound on the growth rate of an algorithm's resource usage (time or space) as the input size approaches infinity, allowing us to compare algorithm efficiency independent of hardware.

### Question 2: Big-O vs Big-Theta
**What is the difference between O(n) and Theta(n)? Can an algorithm be O(n^2) and Theta(n) at the same time?**

**Expected Answer:** O(n) means the algorithm grows no faster than linear. Theta(n) means it grows at exactly a linear rate. Yes, an algorithm can be O(n^2) and Theta(n) simultaneously -- O(n^2) is just a loose upper bound, while Theta(n) is the tight bound. Example: a simple loop through an array is Theta(n) and also technically O(n^2).

### Question 3: Best, Worst, Average
**What is the best-case, worst-case, and average-case time complexity of quicksort? Which does Big-O typically describe?**

**Expected Answer:** Best case: O(n log n), Worst case: O(n^2) when the pivot is always the smallest or largest element, Average case: O(n log n). Big-O typically describes the worst case, though in practice we often discuss average case for quicksort.

### Question 4: Space vs Time
**Can an algorithm have O(1) time complexity and O(n) space complexity? Give an example.**

**Expected Answer:** Yes. Example: pre-computing all results into a lookup table of size n (O(n) space), then answering each query in O(1) time by table lookup.

---

## Analyze the Complexity Questions

### Question 5: What is the time complexity?

**Go:**
```go
func mystery1(n int) int {
    count := 0
    i := 1
    for i < n {
        count++
        i *= 2
    }
    return count
}
```

**Java:**
```java
public static int mystery1(int n) {
    int count = 0;
    int i = 1;
    while (i < n) {
        count++;
        i *= 2;
    }
    return count;
}
```

**Python:**
```python
def mystery1(n):
    count = 0
    i = 1
    while i < n:
        count += 1
        i *= 2
    return count
```

### Question 6: What is the time complexity?

**Go:**
```go
func mystery2(arr []int) int {
    n := len(arr)
    count := 0
    for i := 0; i < n; i++ {
        for j := i; j < n; j++ {
            count++
        }
    }
    return count
}
```

**Java:**
```java
public static int mystery2(int[] arr) {
    int n = arr.length;
    int count = 0;
    for (int i = 0; i < n; i++) {
        for (int j = i; j < n; j++) {
            count++;
        }
    }
    return count;
}
```

**Python:**
```python
def mystery2(arr):
    n = len(arr)
    count = 0
    for i in range(n):
        for j in range(i, n):
            count += 1
    return count
```

### Question 7: What is the time complexity?

**Go:**
```go
func mystery3(n int) int {
    if n <= 1 {
        return 1
    }
    return mystery3(n/3) + mystery3(n/3) + mystery3(n/3)
}
```

**Java:**
```java
public static int mystery3(int n) {
    if (n <= 1) return 1;
    return mystery3(n / 3) + mystery3(n / 3) + mystery3(n / 3);
}
```

**Python:**
```python
def mystery3(n):
    if n <= 1:
        return 1
    return mystery3(n // 3) + mystery3(n // 3) + mystery3(n // 3)
```

---

## Coding Challenge: Analyze 5 Functions

For each function, determine both the **time complexity** and **space complexity**.

### Function 1

**Go:**
```go
func func1(arr []int) []int {
    n := len(arr)
    result := make([]int, n)
    for i := 0; i < n; i++ {
        result[i] = arr[n-1-i]
    }
    return result
}
```

**Java:**
```java
public static int[] func1(int[] arr) {
    int n = arr.length;
    int[] result = new int[n];
    for (int i = 0; i < n; i++) {
        result[i] = arr[n - 1 - i];
    }
    return result;
}
```

**Python:**
```python
def func1(arr):
    n = len(arr)
    result = [0] * n
    for i in range(n):
        result[i] = arr[n - 1 - i]
    return result
```

### Function 2

**Go:**
```go
func func2(matrix [][]int) int {
    n := len(matrix)
    total := 0
    for i := 0; i < n; i++ {
        for j := 0; j < n; j++ {
            for k := 0; k < n; k++ {
                total += matrix[i][j] * matrix[j][k]
            }
        }
    }
    return total
}
```

**Java:**
```java
public static int func2(int[][] matrix) {
    int n = matrix.length;
    int total = 0;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                total += matrix[i][j] * matrix[j][k];
            }
        }
    }
    return total;
}
```

**Python:**
```python
def func2(matrix):
    n = len(matrix)
    total = 0
    for i in range(n):
        for j in range(n):
            for k in range(n):
                total += matrix[i][j] * matrix[j][k]
    return total
```

### Function 3

**Go:**
```go
func func3(s string) bool {
    n := len(s)
    for i := 0; i < n/2; i++ {
        if s[i] != s[n-1-i] {
            return false
        }
    }
    return true
}
```

**Java:**
```java
public static boolean func3(String s) {
    int n = s.length();
    for (int i = 0; i < n / 2; i++) {
        if (s.charAt(i) != s.charAt(n - 1 - i)) {
            return false;
        }
    }
    return true;
}
```

**Python:**
```python
def func3(s):
    n = len(s)
    for i in range(n // 2):
        if s[i] != s[n - 1 - i]:
            return False
    return True
```

### Function 4

**Go:**
```go
func func4(n int) int {
    if n <= 0 {
        return 0
    }
    return n + func4(n-1)
}
```

**Java:**
```java
public static int func4(int n) {
    if (n <= 0) return 0;
    return n + func4(n - 1);
}
```

**Python:**
```python
def func4(n):
    if n <= 0:
        return 0
    return n + func4(n - 1)
```

### Function 5

**Go:**
```go
func func5(arr []int) map[int]int {
    freq := make(map[int]int)
    for _, v := range arr {
        freq[v]++
    }
    return freq
}
```

**Java:**
```java
public static Map<Integer, Integer> func5(int[] arr) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int v : arr) {
        freq.merge(v, 1, Integer::sum);
    }
    return freq;
}
```

**Python:**
```python
def func5(arr):
    freq = {}
    for v in arr:
        freq[v] = freq.get(v, 0) + 1
    return freq
```

---

## Tricky Scenario Questions

### Question 8
**You have an API endpoint that calls a database query O(log n) and then iterates over the results O(k). A colleague says the endpoint is O(log n). Is this correct?**

### Question 9
**A function calls sort (O(n log n)) and then binary search (O(log n)). What is the overall complexity?**

### Question 10
**An algorithm runs in O(n) time but uses O(n^2) space. Your colleague argues it is efficient because the time is linear. What is your response?**

---

## Answers

### Answer 5
**O(log n)**. The variable i doubles each iteration (1, 2, 4, 8, ..., n), so it takes log2(n) steps to reach n.

### Answer 6
**O(n^2)**. The inner loop runs n + (n-1) + (n-2) + ... + 1 = n(n+1)/2 = O(n^2) times total. Even though j starts at i, the total work is still quadratic.

### Answer 7
**O(n)**. Recurrence: T(n) = 3T(n/3) + O(1). By Master Theorem: a=3, b=3, d=0. log_3(3) = 1 > 0 = d, so T(n) = O(n^1) = O(n).

### Function 1 Answer
**Time: O(n), Space: O(n)**. One loop through the array, creates one output array of size n.

### Function 2 Answer
**Time: O(n^3), Space: O(1)**. Three nested loops each running n times. Only a single integer variable for accumulation.

### Function 3 Answer
**Time: O(n), Space: O(1)**. Loop runs n/2 times = O(n). No extra data structures.

### Function 4 Answer
**Time: O(n), Space: O(n)**. The recursion goes n levels deep. Each level does O(1) work. The call stack uses O(n) space.

### Function 5 Answer
**Time: O(n) average, Space: O(n)**. One pass through the array. Hash map operations are O(1) amortized. The map can hold up to n entries.

### Answer 8
It is incomplete. The total complexity is O(log n + k). If k can be large (e.g., a user with millions of orders), the O(k) term dominates and the endpoint could be slow. Pagination would make each request O(log n + page_size).

### Answer 9
**O(n log n)**. By the sum rule, O(n log n) + O(log n) = O(n log n). The sort dominates.

### Answer 10
The colleague is wrong to focus only on time. O(n^2) space means that for n = 100,000, you need ~10 billion units of memory, which is impractical. An algorithm is only efficient if both time and space are acceptable for the expected input sizes.
