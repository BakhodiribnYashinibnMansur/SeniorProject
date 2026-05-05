# Control Structures — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Advanced Conditional Patterns](#advanced-conditional-patterns)
3. [Iterator Patterns](#iterator-patterns)
4. [Loop Optimization](#loop-optimization)
5. [Error Handling as Control Flow](#error-handling-as-control-flow)
6. [Code Examples](#code-examples)
7. [Performance Analysis](#performance-analysis)
8. [Summary](#summary)

---

## Introduction

> Focus: "Why use one control structure over another?" and "When does it affect performance?"

At the middle level, you learn advanced patterns like guard clauses, short-circuit evaluation, labeled breaks, iterator protocols, and how loop structure directly impacts algorithm complexity.

---

## Advanced Conditional Patterns

### Guard Clause (Early Return)

Instead of deep nesting, use early returns to handle edge cases first:

#### Go

```go
// BAD — deep nesting
func process(data []int) (int, error) {
    if data != nil {
        if len(data) > 0 {
            if data[0] >= 0 {
                return data[0] * 2, nil
            } else {
                return 0, errors.New("negative")
            }
        } else {
            return 0, errors.New("empty")
        }
    } else {
        return 0, errors.New("nil")
    }
}

// GOOD — guard clauses
func process(data []int) (int, error) {
    if data == nil {
        return 0, errors.New("nil")
    }
    if len(data) == 0 {
        return 0, errors.New("empty")
    }
    if data[0] < 0 {
        return 0, errors.New("negative")
    }
    return data[0] * 2, nil
}
```

#### Java

```java
// Guard clauses
public static int process(int[] data) {
    if (data == null) throw new IllegalArgumentException("null");
    if (data.length == 0) throw new IllegalArgumentException("empty");
    if (data[0] < 0) throw new IllegalArgumentException("negative");
    return data[0] * 2;
}
```

#### Python

```python
def process(data):
    if data is None:
        raise ValueError("None")
    if len(data) == 0:
        raise ValueError("empty")
    if data[0] < 0:
        raise ValueError("negative")
    return data[0] * 2
```

### Short-Circuit Evaluation

```go
// Go: && stops if left is false, || stops if left is true
if len(arr) > 0 && arr[0] == target {  // safe: len check first
    // ...
}
```

```java
// Java: same behavior
if (arr != null && arr.length > 0 && arr[0] == target) {
    // safe: null check first
}
```

```python
# Python: same with 'and' / 'or'
if arr and arr[0] == target:  # truthy check first
    pass
```

### Labeled Break (Nested Loop Exit)

#### Go

```go
outer:
for i := 0; i < 10; i++ {
    for j := 0; j < 10; j++ {
        if i*j > 20 {
            break outer  // exits BOTH loops
        }
    }
}
```

#### Java

```java
outer:
for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 10; j++) {
        if (i * j > 20) {
            break outer;
        }
    }
}
```

#### Python

```python
# Python has no labeled break — use a flag or function
def find_pair():
    for i in range(10):
        for j in range(10):
            if i * j > 20:
                return (i, j)
    return None
```

---

## Iterator Patterns

### Custom Iterator

#### Go

```go
// Go uses channels or closures for iteration
func fibIterator(max int) func() (int, bool) {
    a, b := 0, 1
    return func() (int, bool) {
        if a > max {
            return 0, false
        }
        result := a
        a, b = b, a+b
        return result, true
    }
}

func main() {
    next := fibIterator(100)
    for val, ok := next(); ok; val, ok = next() {
        fmt.Println(val) // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89
    }
}
```

#### Java

```java
import java.util.Iterator;

class FibIterator implements Iterator<Integer> {
    private int a = 0, b = 1;
    private final int max;

    FibIterator(int max) { this.max = max; }

    public boolean hasNext() { return a <= max; }
    public Integer next() {
        int result = a;
        int temp = a + b;
        a = b;
        b = temp;
        return result;
    }

    public static void main(String[] args) {
        var it = new FibIterator(100);
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
```

#### Python

```python
class FibIterator:
    def __init__(self, max_val):
        self.a, self.b = 0, 1
        self.max_val = max_val

    def __iter__(self):
        return self

    def __next__(self):
        if self.a > self.max_val:
            raise StopIteration
        result = self.a
        self.a, self.b = self.b, self.a + self.b
        return result

for val in FibIterator(100):
    print(val)

# Or use a generator (much simpler)
def fib_gen(max_val):
    a, b = 0, 1
    while a <= max_val:
        yield a
        a, b = b, a + b

for val in fib_gen(100):
    print(val)
```

---

## Loop Optimization

### Loop Invariant Code Motion

Move computations that don't change inside the loop to outside:

```python
# BAD — len(arr) computed every iteration
for i in range(len(arr)):
    for j in range(len(arr)):
        process(arr[i], arr[j])

# BETTER — cache the length
n = len(arr)
for i in range(n):
    for j in range(n):
        process(arr[i], arr[j])
```

### Loop Unrolling (Manual)

```go
// Process 4 elements per iteration when possible
n := len(arr)
i := 0
for ; i+3 < n; i += 4 {
    process(arr[i])
    process(arr[i+1])
    process(arr[i+2])
    process(arr[i+3])
}
for ; i < n; i++ {
    process(arr[i])  // handle remainder
}
```

### Sentinel Value (Avoid Bound Check)

```go
// Standard linear search: 2 checks per iteration
func find(arr []int, target int) int {
    for i := 0; i < len(arr); i++ {  // check 1: i < len
        if arr[i] == target {          // check 2: value match
            return i
        }
    }
    return -1
}

// With sentinel: 1 check per iteration
func findSentinel(arr []int, target int) int {
    n := len(arr)
    if n == 0 { return -1 }
    last := arr[n-1]
    arr[n-1] = target  // place sentinel
    i := 0
    for arr[i] != target {
        i++
    }
    arr[n-1] = last  // restore
    if i < n-1 || last == target {
        return i
    }
    return -1
}
```

---

## Error Handling as Control Flow

### Go — Errors as Values

```go
func readFile(path string) ([]byte, error) {
    data, err := os.ReadFile(path)
    if err != nil {
        return nil, fmt.Errorf("readFile: %w", err)
    }
    return data, nil
}

// Error handling IS control flow in Go
func processFiles(paths []string) error {
    for _, p := range paths {
        data, err := readFile(p)
        if err != nil {
            continue  // skip bad files
        }
        _ = process(data)
    }
    return nil
}
```

### Java — Exceptions as Control Flow

```java
// try-catch IS control flow in Java
public static List<String> readAllFiles(List<String> paths) {
    List<String> results = new ArrayList<>();
    for (String path : paths) {
        try {
            results.add(Files.readString(Path.of(path)));
        } catch (IOException e) {
            System.err.println("Skipping: " + path);
            continue;
        }
    }
    return results;
}
```

### Python — EAFP (Easier to Ask Forgiveness)

```python
# Python idiom: try/except instead of checking first
# LBYL (Look Before You Leap) — not Pythonic
if key in dictionary:
    value = dictionary[key]
else:
    value = default

# EAFP (Easier to Ask Forgiveness than Permission) — Pythonic
try:
    value = dictionary[key]
except KeyError:
    value = default

# Or just: value = dictionary.get(key, default)
```

---

## Code Examples

### Example: Matrix Spiral Traversal

#### Go

```go
func spiralOrder(matrix [][]int) []int {
    if len(matrix) == 0 { return nil }
    result := []int{}
    top, bottom := 0, len(matrix)-1
    left, right := 0, len(matrix[0])-1

    for top <= bottom && left <= right {
        for i := left; i <= right; i++ {
            result = append(result, matrix[top][i])
        }
        top++
        for i := top; i <= bottom; i++ {
            result = append(result, matrix[i][right])
        }
        right--
        if top <= bottom {
            for i := right; i >= left; i-- {
                result = append(result, matrix[bottom][i])
            }
            bottom--
        }
        if left <= right {
            for i := bottom; i >= top; i-- {
                result = append(result, matrix[i][left])
            }
            left++
        }
    }
    return result
}
```

#### Java

```java
public static List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    if (matrix.length == 0) return result;
    int top = 0, bottom = matrix.length - 1;
    int left = 0, right = matrix[0].length - 1;

    while (top <= bottom && left <= right) {
        for (int i = left; i <= right; i++) result.add(matrix[top][i]);
        top++;
        for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
        right--;
        if (top <= bottom) {
            for (int i = right; i >= left; i--) result.add(matrix[bottom][i]);
            bottom--;
        }
        if (left <= right) {
            for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
            left++;
        }
    }
    return result;
}
```

#### Python

```python
def spiral_order(matrix):
    if not matrix:
        return []
    result = []
    top, bottom = 0, len(matrix) - 1
    left, right = 0, len(matrix[0]) - 1

    while top <= bottom and left <= right:
        for i in range(left, right + 1):
            result.append(matrix[top][i])
        top += 1
        for i in range(top, bottom + 1):
            result.append(matrix[i][right])
        right -= 1
        if top <= bottom:
            for i in range(right, left - 1, -1):
                result.append(matrix[bottom][i])
            bottom -= 1
        if left <= right:
            for i in range(bottom, top - 1, -1):
                result.append(matrix[i][left])
            left += 1
    return result
```

---

## Performance Analysis

### Loop Type Complexity

| Pattern | Complexity | Example |
|---------|-----------|---------|
| Single loop | O(n) | Linear search |
| Nested loop (independent) | O(n^2) | Bubble sort |
| Nested loop (dependent) | O(n^2/2) ≈ O(n^2) | Selection sort inner loop |
| Loop with halving | O(log n) | Binary search |
| Loop with doubling | O(log n) | `i *= 2` |
| Outer O(n) × Inner O(log n) | O(n log n) | Build a BST |
| Three nested | O(n^3) | Matrix multiplication |

---

## Summary

At the middle level, control structures become tools for algorithm design. Guard clauses flatten code, short-circuit evaluation prevents crashes, iterators abstract traversal, and understanding loop complexity is essential for writing efficient algorithms.
