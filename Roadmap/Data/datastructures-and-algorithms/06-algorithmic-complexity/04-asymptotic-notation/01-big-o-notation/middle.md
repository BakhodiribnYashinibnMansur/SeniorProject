# Big-O Notation -- Middle Level

## Table of Contents

1. [Formal Mathematical Definition](#formal-mathematical-definition)
2. [Understanding the Definition with Examples](#understanding-the-definition-with-examples)
3. [Tight vs Loose Bounds](#tight-vs-loose-bounds)
4. [Big-O vs Big-Theta vs Big-Omega](#big-o-vs-big-theta-vs-big-omega)
5. [Analyzing Recursive Algorithms](#analyzing-recursive-algorithms)
   - [Recurrence Relations](#recurrence-relations)
   - [Recursion Tree Method](#recursion-tree-method)
   - [Substitution Method](#substitution-method)
6. [Master Theorem Connection](#master-theorem-connection)
7. [Multi-Variable Big-O](#multi-variable-big-o)
8. [Amortized Big-O Analysis](#amortized-big-o-analysis)
9. [Space Complexity with Big-O](#space-complexity-with-big-o)
10. [Practical Analysis Patterns](#practical-analysis-patterns)
11. [Key Takeaways](#key-takeaways)

---

## Formal Mathematical Definition

Big-O notation has a precise mathematical definition that goes beyond the informal "upper bound on growth rate."

**Definition:** A function f(n) is O(g(n)) if and only if there exist positive constants c and n0 such that:

```
f(n) <= c * g(n)   for all n >= n0
```

In mathematical notation:

```
f(n) = O(g(n)) iff exists c > 0, n0 > 0 : f(n) <= c * g(n), for all n >= n0
```

**What this means:**
- **c** is a scaling constant. It lets you "stretch" g(n) to sit above f(n).
- **n0** is the threshold. For small inputs, f(n) might exceed c * g(n), but past n0 the bound holds forever.
- The definition is about **eventual** behavior, not behavior on every single input.

---

## Understanding the Definition with Examples

### Example 1: Prove that 3n + 5 = O(n)

We need to find constants c and n0 such that 3n + 5 <= c * n for all n >= n0.

Choose c = 4, n0 = 5:
- When n = 5: 3(5) + 5 = 20 <= 4(5) = 20. True.
- When n = 6: 3(6) + 5 = 23 <= 4(6) = 24. True.
- For all n >= 5: 3n + 5 <= 4n iff 5 <= n. True for n >= 5.

Therefore 3n + 5 = O(n) with c = 4 and n0 = 5.

### Example 2: Prove that 2n^2 + 3n + 1 = O(n^2)

Choose c = 3, n0 = 4:
- For n >= 4: 2n^2 + 3n + 1 <= 2n^2 + 3n^2 + n^2 = 6n^2 (since n <= n^2 and 1 <= n^2 for n >= 1).
- More tightly: 2n^2 + 3n + 1 <= 2n^2 + n^2 + n^2 = 4n^2 for n >= 4 (since 3n <= n^2 for n >= 3 and 1 <= n^2 for n >= 1).
- Actually we can use c = 3: 2n^2 + 3n + 1 <= 3n^2 iff 3n + 1 <= n^2 iff n^2 - 3n - 1 >= 0, which holds for n >= 4.

### Example 3: Prove that n^2 is NOT O(n)

Proof by contradiction: Assume n^2 = O(n). Then there exist c, n0 such that n^2 <= c * n for all n >= n0. This implies n <= c for all n >= n0. But we can always pick n = c + 1 (which is >= n0 if we pick a large enough n), giving c + 1 <= c, a contradiction.

---

## Tight vs Loose Bounds

A Big-O bound can be either **tight** or **loose**:

- **Tight bound:** The Big-O matches the actual growth rate. Example: binary search is O(log n), and it actually does grow as Theta(log n).
- **Loose bound:** The Big-O is correct but overestimates. Example: binary search is also technically O(n^2) since n^2 grows faster than log n, but this is not useful information.

**Why does this matter?** In practice, you want the **tightest** Big-O bound possible. Saying bubble sort is O(n^3) is technically correct but misleading -- it is O(n^2).

```
Technically correct but useless:
  - Linear search is O(n!)     (true but the tight bound is O(n))
  - Binary search is O(n^100)  (true but the tight bound is O(log n))

Useful tight bounds:
  - Linear search is O(n)
  - Binary search is O(log n)
```

---

## Big-O vs Big-Theta vs Big-Omega

| Notation  | Symbol | Meaning                          | Analogy     |
|-----------|--------|----------------------------------|-------------|
| Big-O     | O(g)   | f grows **no faster** than g     | f <= g      |
| Big-Omega | Omega(g) | f grows **no slower** than g    | f >= g      |
| Big-Theta | Theta(g) | f grows **at the same rate** as g | f == g    |

### Big-Omega (Lower Bound)

f(n) = Omega(g(n)) iff there exist c > 0, n0 > 0 such that:
```
f(n) >= c * g(n)   for all n >= n0
```

Example: Any comparison-based sort is Omega(n log n) -- you cannot sort faster than n log n comparisons in the worst case.

### Big-Theta (Tight Bound)

f(n) = Theta(g(n)) iff f(n) = O(g(n)) AND f(n) = Omega(g(n)).

Equivalently, there exist c1, c2 > 0, and n0 > 0 such that:
```
c1 * g(n) <= f(n) <= c2 * g(n)   for all n >= n0
```

Example: Merge sort is Theta(n log n) because it always does n log n work, regardless of input order.

### When to Use Which

- **Big-O** when stating worst-case upper bounds (most common in industry).
- **Big-Omega** when proving lower bounds (e.g., "no algorithm can solve this faster than...").
- **Big-Theta** when the worst case and the growth rate are the same (e.g., merge sort).

---

## Analyzing Recursive Algorithms

### Recurrence Relations

A recurrence relation expresses the running time of a recursive algorithm in terms of smaller subproblems.

**Binary Search Recurrence:**
```
T(n) = T(n/2) + O(1)
```
Each call does O(1) work and makes one recursive call on half the input.

**Merge Sort Recurrence:**
```
T(n) = 2T(n/2) + O(n)
```
Two recursive calls on halves, plus O(n) work to merge.

**Fibonacci (Naive) Recurrence:**
```
T(n) = T(n-1) + T(n-2) + O(1)
```
Two recursive calls that reduce n by 1 and 2, plus constant work.

### Recursion Tree Method

Draw the recursion as a tree and sum work across all levels.

**Example: Merge Sort T(n) = 2T(n/2) + n**

```
Level 0:                    n                    work = n
                          /   \
Level 1:              n/2      n/2               work = n
                     / \      / \
Level 2:          n/4  n/4  n/4  n/4             work = n
                  ...
Level k:         n/2^k nodes of size n/2^k       work = n

Number of levels: log2(n)
Total work: n * log2(n) = O(n log n)
```

**Example: Binary Search T(n) = T(n/2) + 1**

```
Level 0:           n          work = 1
                   |
Level 1:          n/2         work = 1
                   |
Level 2:          n/4         work = 1
                  ...
Level k:         n/2^k        work = 1

Number of levels: log2(n)
Total work: 1 * log2(n) = O(log n)
```

### Substitution Method

Guess the solution and prove it by induction.

**Example: Prove T(n) = T(n/2) + 1 is O(log n)**

**Guess:** T(n) <= c * log(n) for some constant c.

**Inductive step:** Assume T(k) <= c * log(k) for all k < n.

```
T(n) = T(n/2) + 1
     <= c * log(n/2) + 1
     = c * (log(n) - 1) + 1
     = c * log(n) - c + 1
     <= c * log(n)          (when c >= 1)
```

The bound holds. Therefore T(n) = O(log n).

---

## Master Theorem Connection

The Master Theorem provides a direct formula for recurrences of the form:

```
T(n) = a * T(n/b) + O(n^d)
```

Where a >= 1 (number of subproblems), b > 1 (factor by which input shrinks), d >= 0 (exponent of work done outside recursion).

**Three cases:**

| Condition           | Result           | Intuition                        |
|---------------------|------------------|----------------------------------|
| d > log_b(a)        | O(n^d)           | Work dominated by root level     |
| d = log_b(a)        | O(n^d * log n)   | Work evenly spread across levels |
| d < log_b(a)        | O(n^(log_b(a)))  | Work dominated by leaves         |

**Applying to known algorithms:**

| Algorithm      | Recurrence          | a | b | d | Case | Result       |
|----------------|---------------------|---|---|---|------|--------------|
| Binary Search  | T(n) = T(n/2) + 1  | 1 | 2 | 0 | d = log_2(1) = 0 | O(log n) |
| Merge Sort     | T(n) = 2T(n/2) + n | 2 | 2 | 1 | d = log_2(2) = 1 | O(n log n) |
| Strassen       | T(n) = 7T(n/2) + n^2 | 7 | 2 | 2 | d < log_2(7) ~ 2.81 | O(n^2.81) |
| Karatsuba      | T(n) = 3T(n/2) + n | 3 | 2 | 1 | d < log_2(3) ~ 1.58 | O(n^1.58) |

---

## Multi-Variable Big-O

Not all algorithms depend on a single variable. Graph algorithms, for example, depend on both the number of vertices V and edges E.

### Graph Algorithm Examples

**BFS/DFS:** O(V + E)
- Visit each vertex once: O(V)
- Traverse each edge once: O(E)
- Total: O(V + E)

**Dijkstra's (with binary heap):** O((V + E) log V)
- Extract-min for each vertex: O(V log V)
- Decrease-key for each edge: O(E log V)
- Total: O((V + E) log V)

**Floyd-Warshall:** O(V^3)
- Three nested loops over vertices.

### Implementation -- BFS Complexity Analysis

**Go:**
```go
// BFS: O(V + E) time, O(V) space
func bfs(graph map[int][]int, start int) []int {
    visited := make(map[int]bool)  // O(V) space
    queue := []int{start}          // O(V) space in worst case
    order := []int{}

    visited[start] = true

    for len(queue) > 0 {
        node := queue[0]           // Each node dequeued once: O(V) total
        queue = queue[1:]
        order = append(order, node)

        for _, neighbor := range graph[node] {  // Each edge checked once: O(E) total
            if !visited[neighbor] {
                visited[neighbor] = true
                queue = append(queue, neighbor)
            }
        }
    }
    return order
}
```

**Java:**
```java
// BFS: O(V + E) time, O(V) space
public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
    Set<Integer> visited = new HashSet<>();       // O(V) space
    Queue<Integer> queue = new LinkedList<>();     // O(V) space worst case
    List<Integer> order = new ArrayList<>();

    visited.add(start);
    queue.add(start);

    while (!queue.isEmpty()) {
        int node = queue.poll();                   // Each node dequeued once: O(V)
        order.add(node);

        for (int neighbor : graph.getOrDefault(node, List.of())) { // Each edge: O(E)
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }
    }
    return order;
}
```

**Python:**
```python
from collections import deque

# BFS: O(V + E) time, O(V) space
def bfs(graph, start):
    visited = {start}             # O(V) space
    queue = deque([start])        # O(V) space worst case
    order = []

    while queue:
        node = queue.popleft()    # Each node dequeued once: O(V) total
        order.append(node)

        for neighbor in graph.get(node, []):  # Each edge checked once: O(E) total
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)
    return order
```

### Matrix Algorithms: O(m * n)

When dealing with an m x n matrix, the complexity is O(m * n), not O(n^2) unless m = n.

---

## Amortized Big-O Analysis

Amortized analysis considers the average time per operation over a sequence of operations, rather than the worst case for a single operation.

### Dynamic Array (ArrayList / slice) Resizing

Appending to a dynamic array is usually O(1), but occasionally O(n) when resizing occurs (copying all elements to a new, larger array).

**Why amortized O(1)?**

If the array doubles in size each time it is full:
- Insert 1: copy 0 elements, cost 1
- Insert 2: copy 1 element, cost 2
- Insert 3: copy 2 elements, cost 3
- Insert 4: cost 1
- Insert 5: copy 4 elements, cost 5
- Inserts 6-8: cost 1 each
- Insert 9: copy 8 elements, cost 9

For n inserts, total copy cost is 1 + 2 + 4 + 8 + ... + n/2 + n = ~2n.
Plus n for the n individual insertions.
Total: ~3n for n operations = O(1) amortized per operation.

**Go:**
```go
// Go slices handle this automatically, but here is the concept:
type DynamicArray struct {
    data     []int
    size     int
    capacity int
}

func NewDynamicArray() *DynamicArray {
    return &DynamicArray{data: make([]int, 1), size: 0, capacity: 1}
}

// Amortized O(1) per append
func (da *DynamicArray) Append(val int) {
    if da.size == da.capacity {
        // O(n) resize, but happens rarely
        da.capacity *= 2
        newData := make([]int, da.capacity)
        copy(newData, da.data)
        da.data = newData
    }
    da.data[da.size] = val
    da.size++
}
```

**Java:**
```java
// Java ArrayList does this internally
// Conceptual implementation:
public class DynamicArray {
    private int[] data;
    private int size;

    public DynamicArray() {
        data = new int[1];
        size = 0;
    }

    // Amortized O(1) per append
    public void append(int val) {
        if (size == data.length) {
            // O(n) resize, but happens rarely
            int[] newData = new int[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[size++] = val;
    }
}
```

**Python:**
```python
# Python lists handle this automatically
# Conceptual implementation:
class DynamicArray:
    def __init__(self):
        self._data = [None]
        self._size = 0

    # Amortized O(1) per append
    def append(self, val):
        if self._size == len(self._data):
            # O(n) resize, but happens rarely
            new_data = [None] * (len(self._data) * 2)
            for i in range(self._size):
                new_data[i] = self._data[i]
            self._data = new_data
        self._data[self._size] = val
        self._size += 1
```

### Hash Table Operations

Hash table insert/lookup is O(1) amortized:
- Most operations are O(1).
- Occasionally the table resizes: O(n).
- Over n operations, total cost is O(n), so amortized O(1) per operation.

### Aggregate Method for Amortized Analysis

Count total cost of n operations and divide by n:

| Operation Sequence | Individual Costs | Total Cost | Amortized per Op |
|-------------------|------------------|------------|------------------|
| n appends to dynamic array | n * O(1) + O(n) resizes | O(n) | O(1) |
| n push/pop on stack | mostly O(1), rare O(n) multipop | O(n) | O(1) |
| n splay tree operations | vary from O(1) to O(n) | O(n log n) | O(log n) |

---

## Space Complexity with Big-O

Big-O applies to space (memory) as well as time. When analyzing space:

- Count variables, arrays, and data structures created.
- Include recursion stack depth.
- Exclude the input (measure **auxiliary** space).

| Algorithm     | Time Complexity | Space Complexity | Notes                        |
|---------------|-----------------|------------------|------------------------------|
| Merge Sort    | O(n log n)      | O(n)             | Needs auxiliary array        |
| Quick Sort    | O(n log n) avg  | O(log n)         | Recursion stack depth        |
| Heap Sort     | O(n log n)      | O(1)             | In-place                     |
| BFS           | O(V + E)        | O(V)             | Queue + visited set          |
| DFS           | O(V + E)        | O(V)             | Recursion stack              |

**Go -- Recursion Stack Space:**
```go
// O(n) time AND O(n) space (due to recursion stack)
func factorial(n int) int {
    if n <= 1 {
        return 1
    }
    return n * factorial(n-1) // Stack frame for each call
}

// O(n) time but O(1) space (iterative)
func factorialIterative(n int) int {
    result := 1
    for i := 2; i <= n; i++ {
        result *= i
    }
    return result
}
```

**Java:**
```java
// O(n) time AND O(n) space (recursion stack)
public static int factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

// O(n) time but O(1) space (iterative)
public static int factorialIterative(int n) {
    int result = 1;
    for (int i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
}
```

**Python:**
```python
# O(n) time AND O(n) space (recursion stack)
def factorial(n):
    if n <= 1:
        return 1
    return n * factorial(n - 1)

# O(n) time but O(1) space (iterative)
def factorial_iterative(n):
    result = 1
    for i in range(2, n + 1):
        result *= i
    return result
```

---

## Practical Analysis Patterns

### Pattern 1: Two Pointers -- O(n)

Two pointers that both move from start to end collectively traverse the array once.

**Go:**
```go
// O(n) despite having two moving pointers
func twoSum(sorted []int, target int) (int, int) {
    left, right := 0, len(sorted)-1
    for left < right {
        sum := sorted[left] + sorted[right]
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
```

### Pattern 2: Sliding Window -- O(n)

**Go:**
```go
// O(n) -- each element is added and removed from the window at most once
func maxSumSubarray(arr []int, k int) int {
    windowSum := 0
    for i := 0; i < k; i++ {
        windowSum += arr[i]
    }
    maxSum := windowSum
    for i := k; i < len(arr); i++ {
        windowSum += arr[i] - arr[i-k]
        if windowSum > maxSum {
            maxSum = windowSum
        }
    }
    return maxSum
}
```

### Pattern 3: Divide and Conquer -- O(n log n)

Split the problem, solve recursively, combine.

### Pattern 4: Backtracking -- Often Exponential

Generating all subsets (O(2^n)), permutations (O(n!)), etc.

---

## Key Takeaways

- The formal definition requires finding constants c and n0 such that f(n) <= c * g(n) for all n >= n0.
- Big-O (upper bound), Big-Omega (lower bound), and Big-Theta (tight bound) serve different purposes. Big-Theta is the most precise.
- Recursive algorithms are analyzed via recurrence relations; the Master Theorem gives direct answers for many common patterns.
- Multi-variable analysis (O(V + E)) is essential for graph and matrix algorithms.
- Amortized analysis shows that occasionally expensive operations can average out to cheap per-operation cost.
- Space complexity is equally important and follows the same Big-O framework.
- Always strive for the tightest bound that is correct.
