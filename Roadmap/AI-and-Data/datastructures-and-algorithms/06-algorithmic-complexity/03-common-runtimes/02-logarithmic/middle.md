# Logarithmic Time O(log n) — Middle Level

## Table of Contents

- [Introduction](#introduction)
- [Log Bases Do Not Matter in Big-O](#log-bases-do-not-matter-in-big-o)
- [Balanced BST Operations](#balanced-bst-operations)
- [B-Trees and O(log_B n)](#b-trees-and-olog_b-n)
- [Jump Search — O(sqrt n) as a Comparison](#jump-search--osqrt-n-as-a-comparison)
- [Interpolation Search](#interpolation-search)
- [Logarithmic Factor in Divide and Conquer](#logarithmic-factor-in-divide-and-conquer)
- [Binary Search Variants](#binary-search-variants)
- [Common Patterns That Produce O(log n)](#common-patterns-that-produce-olog-n)
- [Key Takeaways](#key-takeaways)
- [References](#references)

---

## Introduction

At the junior level you learned that O(log n) arises from repeatedly halving the input. Now we
go deeper: why log bases are interchangeable, how balanced trees guarantee logarithmic height,
how B-trees generalize the concept, and how the logarithmic factor appears as a building block
inside more complex algorithms.

---

## Log Bases Do Not Matter in Big-O

A common source of confusion: does O(log₂ n) differ from O(log₁₀ n) or O(ln n)?

**No.** In Big-O notation, the base of the logarithm is irrelevant because all logarithms differ
only by a constant factor:

```
log_a(n) = log_b(n) / log_b(a)
```

Since `1 / log_b(a)` is a constant, it is absorbed by Big-O:

```
O(log₂ n) = O(log₁₀ n) = O(ln n) = O(log n)
```

### Concrete example

| n           | log₂(n) | log₁₀(n) | ln(n)   | Ratio log₂/log₁₀ |
|-------------|---------|-----------|---------|-------------------|
| 1,000       | 9.97    | 3.00      | 6.91    | 3.32              |
| 1,000,000   | 19.93   | 6.00      | 13.82   | 3.32              |
| 1,000,000,000| 29.90  | 9.00      | 20.72   | 3.32              |

The ratio between any two log bases is always constant (here, log₂/log₁₀ = 3.32... = 1/log₁₀(2)).

**However**, the base matters for **practical performance**. A B-tree with branching factor 100
has height log₁₀₀(n), which is half of log₁₀(n). Both are O(log n), but the constants differ
significantly.

---

## Balanced BST Operations

A binary search tree is **balanced** when its height is O(log n). The two most common
self-balancing BSTs are **AVL trees** and **Red-Black trees**.

### AVL Tree — Insert with Rotations

AVL trees maintain the invariant that for every node, the heights of the left and right subtrees
differ by at most 1. After insertion, if this invariant is violated, rotations restore balance.

### Go Implementation

```go
package main

import "fmt"

type AVLNode struct {
    Key    int
    Height int
    Left   *AVLNode
    Right  *AVLNode
}

func height(n *AVLNode) int {
    if n == nil {
        return 0
    }
    return n.Height
}

func max(a, b int) int {
    if a > b {
        return a
    }
    return b
}

func updateHeight(n *AVLNode) {
    n.Height = 1 + max(height(n.Left), height(n.Right))
}

func balanceFactor(n *AVLNode) int {
    if n == nil {
        return 0
    }
    return height(n.Left) - height(n.Right)
}

func rotateRight(y *AVLNode) *AVLNode {
    x := y.Left
    t := x.Right

    x.Right = y
    y.Left = t

    updateHeight(y)
    updateHeight(x)
    return x
}

func rotateLeft(x *AVLNode) *AVLNode {
    y := x.Right
    t := y.Left

    y.Left = x
    x.Right = t

    updateHeight(x)
    updateHeight(y)
    return y
}

func insertAVL(node *AVLNode, key int) *AVLNode {
    if node == nil {
        return &AVLNode{Key: key, Height: 1}
    }

    if key < node.Key {
        node.Left = insertAVL(node.Left, key)
    } else if key > node.Key {
        node.Right = insertAVL(node.Right, key)
    } else {
        return node // Duplicate keys not allowed
    }

    updateHeight(node)
    bf := balanceFactor(node)

    // Left-Left case
    if bf > 1 && key < node.Left.Key {
        return rotateRight(node)
    }
    // Right-Right case
    if bf < -1 && key > node.Right.Key {
        return rotateLeft(node)
    }
    // Left-Right case
    if bf > 1 && key > node.Left.Key {
        node.Left = rotateLeft(node.Left)
        return rotateRight(node)
    }
    // Right-Left case
    if bf < -1 && key < node.Right.Key {
        node.Right = rotateRight(node.Right)
        return rotateLeft(node)
    }

    return node
}

func searchAVL(root *AVLNode, key int) bool {
    current := root
    for current != nil {
        if key == current.Key {
            return true
        } else if key < current.Key {
            current = current.Left
        } else {
            current = current.Right
        }
    }
    return false
}

func main() {
    var root *AVLNode
    keys := []int{10, 20, 30, 40, 50, 25}

    for _, k := range keys {
        root = insertAVL(root, k)
    }

    fmt.Println("Height:", root.Height)       // Output: 3 (balanced!)
    fmt.Println("Search 25:", searchAVL(root, 25)) // Output: true
    fmt.Println("Search 35:", searchAVL(root, 35)) // Output: false
}
```

### Java Implementation

```java
public class AVLTree {

    static class Node {
        int key, height;
        Node left, right;

        Node(int key) {
            this.key = key;
            this.height = 1;
        }
    }

    static int height(Node n) {
        return n == null ? 0 : n.height;
    }

    static void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    static int balanceFactor(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    static Node rotateRight(Node y) {
        Node x = y.left;
        Node t = x.right;
        x.right = y;
        y.left = t;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    static Node rotateLeft(Node x) {
        Node y = x.right;
        Node t = y.left;
        y.left = x;
        x.right = t;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    static Node insert(Node node, int key) {
        if (node == null) return new Node(key);

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
        } else {
            return node;
        }

        updateHeight(node);
        int bf = balanceFactor(node);

        if (bf > 1 && key < node.left.key) return rotateRight(node);
        if (bf < -1 && key > node.right.key) return rotateLeft(node);
        if (bf > 1 && key > node.left.key) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (bf < -1 && key < node.right.key) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    static boolean search(Node root, int key) {
        Node current = root;
        while (current != null) {
            if (key == current.key) return true;
            else if (key < current.key) current = current.left;
            else current = current.right;
        }
        return false;
    }

    public static void main(String[] args) {
        Node root = null;
        int[] keys = {10, 20, 30, 40, 50, 25};

        for (int k : keys) {
            root = insert(root, k);
        }

        System.out.println("Height: " + root.height);
        System.out.println("Search 25: " + search(root, 25));
        System.out.println("Search 35: " + search(root, 35));
    }
}
```

### Python Implementation

```python
from typing import Optional


class AVLNode:
    def __init__(self, key: int):
        self.key = key
        self.height = 1
        self.left: Optional[AVLNode] = None
        self.right: Optional[AVLNode] = None


def height(node: Optional[AVLNode]) -> int:
    return node.height if node else 0


def update_height(node: AVLNode) -> None:
    node.height = 1 + max(height(node.left), height(node.right))


def balance_factor(node: Optional[AVLNode]) -> int:
    return height(node.left) - height(node.right) if node else 0


def rotate_right(y: AVLNode) -> AVLNode:
    x = y.left
    t = x.right
    x.right = y
    y.left = t
    update_height(y)
    update_height(x)
    return x


def rotate_left(x: AVLNode) -> AVLNode:
    y = x.right
    t = y.left
    y.left = x
    x.right = t
    update_height(x)
    update_height(y)
    return y


def insert(node: Optional[AVLNode], key: int) -> AVLNode:
    if node is None:
        return AVLNode(key)

    if key < node.key:
        node.left = insert(node.left, key)
    elif key > node.key:
        node.right = insert(node.right, key)
    else:
        return node

    update_height(node)
    bf = balance_factor(node)

    if bf > 1 and key < node.left.key:
        return rotate_right(node)
    if bf < -1 and key > node.right.key:
        return rotate_left(node)
    if bf > 1 and key > node.left.key:
        node.left = rotate_left(node.left)
        return rotate_right(node)
    if bf < -1 and key < node.right.key:
        node.right = rotate_right(node.right)
        return rotate_left(node)

    return node


def search(root: Optional[AVLNode], key: int) -> bool:
    current = root
    while current:
        if key == current.key:
            return True
        elif key < current.key:
            current = current.left
        else:
            current = current.right
    return False


if __name__ == "__main__":
    root = None
    keys = [10, 20, 30, 40, 50, 25]

    for k in keys:
        root = insert(root, k)

    print(f"Height: {root.height}")         # Output: 3
    print(f"Search 25: {search(root, 25)}") # Output: True
    print(f"Search 35: {search(root, 35)}") # Output: False
```

### Complexity of Balanced BST Operations

| Operation | AVL Tree  | Red-Black Tree |
|-----------|-----------|----------------|
| Search    | O(log n)  | O(log n)       |
| Insert    | O(log n)  | O(log n)       |
| Delete    | O(log n)  | O(log n)       |
| Height    | ≤ 1.44 log₂(n) | ≤ 2 log₂(n+1) |

AVL trees are slightly more balanced (thus faster for lookups) but require more rotations on
insertion/deletion. Red-Black trees are more popular in practice (used in Java TreeMap, C++ std::map).

---

## B-Trees and O(log_B n)

A **B-tree** of order B stores up to B-1 keys per node and has up to B children. Its height is:

```
h = O(log_B(n))
```

Since each node is loaded in a single disk I/O, and `log_B(n) = log₂(n) / log₂(B)`, a B-tree
with B = 1000 has height about **3** for a billion keys, compared to **30** for a binary tree.

This makes B-trees the backbone of databases and file systems.

### B-tree height comparison

| n               | Binary tree (B=2) | B-tree (B=100) | B-tree (B=1000) |
|-----------------|-------------------|----------------|-----------------|
| 10,000          | 14                | 2              | 2               |
| 1,000,000       | 20                | 3              | 2               |
| 1,000,000,000   | 30                | 5              | 3               |

Both are O(log n), but the constant factor makes a huge practical difference when each level
costs a disk seek.

---

## Jump Search — O(sqrt n) as a Comparison

Jump search works on sorted arrays by jumping ahead by sqrt(n) steps, then doing a linear scan
within the block. It achieves **O(sqrt n)**, which is worse than O(log n) but does not require
random access — useful for linked lists.

### Go Implementation

```go
package main

import (
    "fmt"
    "math"
)

func jumpSearch(arr []int, target int) int {
    n := len(arr)
    step := int(math.Sqrt(float64(n)))

    prev := 0
    for arr[min(step, n)-1] < target {
        prev = step
        step += int(math.Sqrt(float64(n)))
        if prev >= n {
            return -1
        }
    }

    for i := prev; i < min(step, n); i++ {
        if arr[i] == target {
            return i
        }
    }

    return -1
}

func min(a, b int) int {
    if a < b {
        return a
    }
    return b
}

func main() {
    arr := []int{0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89}
    fmt.Println(jumpSearch(arr, 55)) // Output: 9
    fmt.Println(jumpSearch(arr, 50)) // Output: -1
}
```

### Java Implementation

```java
public class JumpSearch {

    public static int jumpSearch(int[] arr, int target) {
        int n = arr.length;
        int step = (int) Math.sqrt(n);

        int prev = 0;
        while (arr[Math.min(step, n) - 1] < target) {
            prev = step;
            step += (int) Math.sqrt(n);
            if (prev >= n) return -1;
        }

        for (int i = prev; i < Math.min(step, n); i++) {
            if (arr[i] == target) return i;
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89};
        System.out.println(jumpSearch(arr, 55)); // Output: 9
        System.out.println(jumpSearch(arr, 50)); // Output: -1
    }
}
```

### Python Implementation

```python
import math
from typing import List


def jump_search(arr: List[int], target: int) -> int:
    n = len(arr)
    step = int(math.sqrt(n))

    prev = 0
    while arr[min(step, n) - 1] < target:
        prev = step
        step += int(math.sqrt(n))
        if prev >= n:
            return -1

    for i in range(prev, min(step, n)):
        if arr[i] == target:
            return i

    return -1


if __name__ == "__main__":
    arr = [0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89]
    print(jump_search(arr, 55))  # Output: 9
    print(jump_search(arr, 50))  # Output: -1
```

---

## Interpolation Search

Binary search always checks the middle. **Interpolation search** estimates where the target
might be based on its value, similar to how you look up a word in a dictionary — you open near
the beginning for "A" words and near the end for "Z" words.

The probe position is:

```
pos = left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left])
```

- **Best/average case** (uniformly distributed data): **O(log log n)**
- **Worst case** (exponentially distributed data): **O(n)**

### Go Implementation

```go
package main

import "fmt"

func interpolationSearch(arr []int, target int) int {
    left, right := 0, len(arr)-1

    for left <= right && target >= arr[left] && target <= arr[right] {
        if left == right {
            if arr[left] == target {
                return left
            }
            return -1
        }

        pos := left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left])

        if arr[pos] == target {
            return pos
        } else if arr[pos] < target {
            left = pos + 1
        } else {
            right = pos - 1
        }
    }

    return -1
}

func main() {
    arr := []int{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}
    fmt.Println(interpolationSearch(arr, 70)) // Output: 6
    fmt.Println(interpolationSearch(arr, 75)) // Output: -1
}
```

### Java Implementation

```java
public class InterpolationSearch {

    public static int interpolationSearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left <= right && target >= arr[left] && target <= arr[right]) {
            if (left == right) {
                return arr[left] == target ? left : -1;
            }

            int pos = left + ((target - arr[left]) * (right - left))
                      / (arr[right] - arr[left]);

            if (arr[pos] == target) return pos;
            else if (arr[pos] < target) left = pos + 1;
            else right = pos - 1;
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        System.out.println(interpolationSearch(arr, 70)); // Output: 6
        System.out.println(interpolationSearch(arr, 75)); // Output: -1
    }
}
```

### Python Implementation

```python
from typing import List


def interpolation_search(arr: List[int], target: int) -> int:
    left, right = 0, len(arr) - 1

    while left <= right and target >= arr[left] and target <= arr[right]:
        if left == right:
            return left if arr[left] == target else -1

        pos = left + ((target - arr[left]) * (right - left)) // (arr[right] - arr[left])

        if arr[pos] == target:
            return pos
        elif arr[pos] < target:
            left = pos + 1
        else:
            right = pos - 1

    return -1


if __name__ == "__main__":
    arr = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
    print(interpolation_search(arr, 70))  # Output: 6
    print(interpolation_search(arr, 75))  # Output: -1
```

---

## Logarithmic Factor in Divide and Conquer

Many divide-and-conquer algorithms have a **log n factor** in their complexity:

| Algorithm     | Complexity     | Where log n comes from              |
|---------------|---------------|-------------------------------------|
| Merge Sort    | O(n log n)    | log n levels of recursion, O(n) merge each |
| Quick Sort    | O(n log n) avg| log n levels of partitioning on average     |
| Heap Sort     | O(n log n)    | n insertions/extractions, each O(log n)     |
| Binary Search | O(log n)      | log n levels, O(1) work each                |

The **Master Theorem** gives us a framework. For recurrences of the form:

```
T(n) = a * T(n/b) + O(n^d)
```

- If `d < log_b(a)`: T(n) = O(n^(log_b(a)))
- If `d = log_b(a)`: T(n) = O(n^d * log n)
- If `d > log_b(a)`: T(n) = O(n^d)

Binary search: `T(n) = T(n/2) + O(1)` → a=1, b=2, d=0 → `log_2(1) = 0 = d` → **O(log n)**.

Merge sort: `T(n) = 2T(n/2) + O(n)` → a=2, b=2, d=1 → `log_2(2) = 1 = d` → **O(n log n)**.

---

## Binary Search Variants

Beyond basic binary search, several variants are essential at the middle level:

### Lower Bound (First occurrence >= target)

```go
// Go: Find the first index where arr[i] >= target
func lowerBound(arr []int, target int) int {
    left, right := 0, len(arr)
    for left < right {
        mid := left + (right-left)/2
        if arr[mid] < target {
            left = mid + 1
        } else {
            right = mid
        }
    }
    return left
}
```

```java
// Java: Find the first index where arr[i] >= target
public static int lowerBound(int[] arr, int target) {
    int left = 0, right = arr.length;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] < target) left = mid + 1;
        else right = mid;
    }
    return left;
}
```

```python
# Python: Find the first index where arr[i] >= target
def lower_bound(arr: list[int], target: int) -> int:
    left, right = 0, len(arr)
    while left < right:
        mid = left + (right - left) // 2
        if arr[mid] < target:
            left = mid + 1
        else:
            right = mid
    return left
```

### Upper Bound (First occurrence > target)

```go
// Go: Find the first index where arr[i] > target
func upperBound(arr []int, target int) int {
    left, right := 0, len(arr)
    for left < right {
        mid := left + (right-left)/2
        if arr[mid] <= target {
            left = mid + 1
        } else {
            right = mid
        }
    }
    return left
}
```

```java
// Java: Find the first index where arr[i] > target
public static int upperBound(int[] arr, int target) {
    int left = 0, right = arr.length;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] <= target) left = mid + 1;
        else right = mid;
    }
    return left;
}
```

```python
# Python: Find the first index where arr[i] > target
def upper_bound(arr: list[int], target: int) -> int:
    left, right = 0, len(arr)
    while left < right:
        mid = left + (right - left) // 2
        if arr[mid] <= target:
            left = mid + 1
        else:
            right = mid
    return left
```

These variants are the building blocks for counting occurrences, range queries, and many
competitive programming problems.

---

## Common Patterns That Produce O(log n)

1. **Halving the search space** — binary search, BST traversal.
2. **Repeated squaring** — exponentiation, matrix exponentiation.
3. **Balanced tree height** — AVL, Red-Black, B-tree operations.
4. **Binary lifting** — LCA queries, sparse tables.
5. **Doubling then binary search** — exponential search (find range, then binary search within).

---

## Key Takeaways

1. **All log bases are equivalent** in Big-O, but the base matters for practical performance
   (especially in B-trees and disk I/O).

2. **Self-balancing BSTs** (AVL, Red-Black) guarantee O(log n) operations by limiting tree height.

3. **B-trees** reduce the height to O(log_B n) by increasing the branching factor, which is
   critical for disk-based storage.

4. **Interpolation search** achieves O(log log n) on uniformly distributed data but degrades to
   O(n) in the worst case.

5. **The Master Theorem** provides a systematic way to identify when divide-and-conquer produces
   O(log n) or O(n log n).

6. **Binary search variants** (lower bound, upper bound) are essential tools that appear
   constantly in real-world code and competitions.

---

## References

1. Cormen, T. H., et al. *Introduction to Algorithms* (CLRS), Chapter 4 — Master Theorem.
2. Knuth, D. E. *The Art of Computer Programming*, Vol. 3 — Balanced Trees.
3. Comer, D. "The Ubiquitous B-Tree," *ACM Computing Surveys*, 1979.
4. Sedgewick, R., Wayne, K. *Algorithms*, 4th Edition — Balanced Search Trees.
5. Perl, Y., Itai, A., Avni, H. "Interpolation Search — A Log Log N Search," 1978.
