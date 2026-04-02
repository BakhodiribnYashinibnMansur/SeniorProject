# Logarithmic Time O(log n) — Junior Level

## Table of Contents

- [Introduction](#introduction)
- [What Is a Logarithm?](#what-is-a-logarithm)
- [The Halving Intuition](#the-halving-intuition)
- [Real-World Analogies](#real-world-analogies)
- [Binary Search — The Classic O(log n) Algorithm](#binary-search--the-classic-olog-n-algorithm)
- [Binary Search Tree (BST) Search](#binary-search-tree-bst-search)
- [Exponentiation by Squaring](#exponentiation-by-squaring)
- [Why O(log n) Is So Fast](#why-olog-n-is-so-fast)
- [Comparison Table](#comparison-table)
- [Key Takeaways](#key-takeaways)
- [References](#references)

---

## Introduction

Logarithmic time complexity, written **O(log n)**, describes algorithms whose running time grows
very slowly as the input size increases. If you double the input, an O(log n) algorithm does only
**one extra step**. This makes logarithmic algorithms among the most efficient and desirable in
computer science.

Understanding O(log n) is a turning point for every developer. It is the first complexity class
that feels "unreasonably fast." An algorithm that runs in O(log n) can handle a billion elements
in roughly 30 steps.

---

## What Is a Logarithm?

A **logarithm** answers the question: "How many times must I multiply a base by itself to reach a
given number?"

```
log₂(8)  = 3   because 2 × 2 × 2 = 8
log₂(16) = 4   because 2 × 2 × 2 × 2 = 16
log₂(1024) = 10
log₂(1,000,000) ≈ 20
log₂(1,000,000,000) ≈ 30
```

In computer science, the base is almost always 2. We write **log n** and assume base 2 unless
stated otherwise.

The **inverse relationship** between logarithms and exponents is key:

| n             | log₂(n) |
|---------------|---------|
| 1             | 0       |
| 2             | 1       |
| 4             | 2       |
| 8             | 3       |
| 16            | 4       |
| 1,024         | 10      |
| 1,048,576     | 20      |
| 1,073,741,824 | 30      |

Notice how n grows **exponentially** while log₂(n) grows **linearly**. That is why O(log n)
algorithms are so powerful.

---

## The Halving Intuition

The simplest way to think about O(log n) is through **repeated halving**:

> How many times can you cut n in half before you reach 1?

- Start with 16 → 8 → 4 → 2 → 1 → **4 cuts** (log₂(16) = 4)
- Start with 1024 → 512 → 256 → 128 → 64 → 32 → 16 → 8 → 4 → 2 → 1 → **10 cuts**

Every O(log n) algorithm works by **eliminating a constant fraction** of the remaining work at
each step. Binary search eliminates half. Some algorithms eliminate a third or two-thirds. As long
as a constant fraction is removed each step, the result is O(log n).

---

## Real-World Analogies

### The Phone Book

Imagine looking up "Smith" in a phone book with 1,000 pages:

1. Open to the middle (page 500). You see names starting with "M."
2. "S" comes after "M," so ignore pages 1-500. Open to page 750.
3. You see "R." "S" comes after "R." Open to page 875.
4. You see "T." "S" comes before "T." Open to page 812.
5. Continue halving until you find "Smith."

After about **10 steps**, you find your entry among 1,000 pages. That is O(log n).

### The 20 Questions Game

In the game "20 Questions," you try to guess an object by asking yes/no questions. Each good
question eliminates half the possibilities:

- Is it alive? (eliminates half)
- Is it bigger than a breadbox? (eliminates half again)
- ...

With 20 questions, you can distinguish among 2²⁰ = 1,048,576 possibilities. This is logarithmic
thinking in reverse: **log₂(1,048,576) = 20**.

### The Tournament Bracket

In a single-elimination tournament with 64 teams, there are 6 rounds (log₂(64) = 6) to determine
a champion. Each round halves the remaining teams.

---

## Binary Search — The Classic O(log n) Algorithm

Binary search finds a target value in a **sorted** array by repeatedly halving the search space.

### Algorithm

1. Set `left = 0`, `right = len(array) - 1`.
2. While `left <= right`:
   a. Compute `mid = left + (right - left) / 2`.
   b. If `array[mid] == target`, return `mid`.
   c. If `array[mid] < target`, set `left = mid + 1`.
   d. If `array[mid] > target`, set `right = mid - 1`.
3. Return -1 (not found).

### Why `left + (right - left) / 2` Instead of `(left + right) / 2`?

The expression `(left + right)` can **overflow** if both values are large integers. Using
`left + (right - left) / 2` avoids this. This is a famous bug that existed in the Java standard
library for years.

### Go Implementation

```go
package main

import "fmt"

// BinarySearch returns the index of target in a sorted slice, or -1 if not found.
func BinarySearch(arr []int, target int) int {
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

func main() {
    sorted := []int{2, 5, 8, 12, 16, 23, 38, 56, 72, 91}

    fmt.Println(BinarySearch(sorted, 23))  // Output: 5
    fmt.Println(BinarySearch(sorted, 50))  // Output: -1
}
```

### Java Implementation

```java
public class BinarySearch {

    /**
     * Returns the index of target in a sorted array, or -1 if not found.
     */
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] sorted = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91};

        System.out.println(binarySearch(sorted, 23));  // Output: 5
        System.out.println(binarySearch(sorted, 50));  // Output: -1
    }
}
```

### Python Implementation

```python
from typing import List


def binary_search(arr: List[int], target: int) -> int:
    """Return the index of target in a sorted list, or -1 if not found."""
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


if __name__ == "__main__":
    sorted_arr = [2, 5, 8, 12, 16, 23, 38, 56, 72, 91]

    print(binary_search(sorted_arr, 23))  # Output: 5
    print(binary_search(sorted_arr, 50))  # Output: -1
```

### Step-by-Step Trace

Searching for **23** in `[2, 5, 8, 12, 16, 23, 38, 56, 72, 91]`:

| Step | left | right | mid | arr[mid] | Action           |
|------|------|-------|-----|----------|------------------|
| 1    | 0    | 9     | 4   | 16       | 16 < 23, go right|
| 2    | 5    | 9     | 7   | 56       | 56 > 23, go left |
| 3    | 5    | 6     | 5   | 23       | Found at index 5 |

Only **3 steps** for an array of 10 elements. For 1 billion elements, it would take at most 30.

---

## Binary Search Tree (BST) Search

A Binary Search Tree stores values such that for every node:
- All values in the **left** subtree are smaller.
- All values in the **right** subtree are larger.

Searching a **balanced** BST takes O(log n) because each comparison eliminates one subtree.

### Go Implementation

```go
package main

import "fmt"

type Node struct {
    Value int
    Left  *Node
    Right *Node
}

// Search returns true if value exists in the BST.
func Search(root *Node, target int) bool {
    current := root

    for current != nil {
        if target == current.Value {
            return true
        } else if target < current.Value {
            current = current.Left
        } else {
            current = current.Right
        }
    }

    return false
}

// Insert adds a value to the BST and returns the root.
func Insert(root *Node, value int) *Node {
    if root == nil {
        return &Node{Value: value}
    }

    if value < root.Value {
        root.Left = Insert(root.Left, value)
    } else if value > root.Value {
        root.Right = Insert(root.Right, value)
    }

    return root
}

func main() {
    var root *Node
    values := []int{50, 30, 70, 20, 40, 60, 80}

    for _, v := range values {
        root = Insert(root, v)
    }

    fmt.Println(Search(root, 40))  // Output: true
    fmt.Println(Search(root, 25))  // Output: false
}
```

### Java Implementation

```java
public class BSTSearch {

    static class Node {
        int value;
        Node left, right;

        Node(int value) {
            this.value = value;
        }
    }

    /** Returns true if the value exists in the BST. */
    public static boolean search(Node root, int target) {
        Node current = root;

        while (current != null) {
            if (target == current.value) {
                return true;
            } else if (target < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return false;
    }

    /** Inserts a value into the BST and returns the root. */
    public static Node insert(Node root, int value) {
        if (root == null) {
            return new Node(value);
        }

        if (value < root.value) {
            root.left = insert(root.left, value);
        } else if (value > root.value) {
            root.right = insert(root.right, value);
        }

        return root;
    }

    public static void main(String[] args) {
        Node root = null;
        int[] values = {50, 30, 70, 20, 40, 60, 80};

        for (int v : values) {
            root = insert(root, v);
        }

        System.out.println(search(root, 40));  // Output: true
        System.out.println(search(root, 25));  // Output: false
    }
}
```

### Python Implementation

```python
from typing import Optional


class Node:
    def __init__(self, value: int):
        self.value = value
        self.left: Optional[Node] = None
        self.right: Optional[Node] = None


def search(root: Optional[Node], target: int) -> bool:
    """Return True if target exists in the BST."""
    current = root

    while current is not None:
        if target == current.value:
            return True
        elif target < current.value:
            current = current.left
        else:
            current = current.right

    return False


def insert(root: Optional[Node], value: int) -> Node:
    """Insert a value into the BST and return the root."""
    if root is None:
        return Node(value)

    if value < root.value:
        root.left = insert(root.left, value)
    elif value > root.value:
        root.right = insert(root.right, value)

    return root


if __name__ == "__main__":
    root = None
    values = [50, 30, 70, 20, 40, 60, 80]

    for v in values:
        root = insert(root, v)

    print(search(root, 40))  # Output: True
    print(search(root, 25))  # Output: False
```

### Important Caveat

BST search is O(log n) **only when the tree is balanced**. If you insert sorted data into a
naive BST, it degenerates into a linked list with O(n) search. Self-balancing trees (AVL, Red-Black)
guarantee O(log n) by rebalancing after insertions and deletions.

---

## Exponentiation by Squaring

Computing `x^n` naively requires n multiplications: O(n). But exponentiation by squaring uses the
insight that:

```
x^n = (x^(n/2))^2          if n is even
x^n = x * (x^((n-1)/2))^2  if n is odd
```

This reduces the problem size by half each step, giving O(log n).

### Go Implementation

```go
package main

import "fmt"

// Power computes base^exp in O(log exp) time.
func Power(base, exp int) int {
    result := 1

    for exp > 0 {
        if exp%2 == 1 {
            result *= base
        }
        base *= base
        exp /= 2
    }

    return result
}

func main() {
    fmt.Println(Power(2, 10))  // Output: 1024
    fmt.Println(Power(3, 5))   // Output: 243
}
```

### Java Implementation

```java
public class FastPower {

    /** Computes base^exp in O(log exp) time. */
    public static long power(long base, int exp) {
        long result = 1;

        while (exp > 0) {
            if (exp % 2 == 1) {
                result *= base;
            }
            base *= base;
            exp /= 2;
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(power(2, 10));  // Output: 1024
        System.out.println(power(3, 5));   // Output: 243
    }
}
```

### Python Implementation

```python
def power(base: int, exp: int) -> int:
    """Compute base^exp in O(log exp) time."""
    result = 1

    while exp > 0:
        if exp % 2 == 1:
            result *= base
        base *= base
        exp //= 2

    return result


if __name__ == "__main__":
    print(power(2, 10))  # Output: 1024
    print(power(3, 5))   # Output: 243
```

### Trace for 2^10

| Step | exp | base      | result | Action          |
|------|-----|-----------|--------|-----------------|
| 1    | 10  | 2         | 1      | even, skip mult |
| 2    | 5   | 4         | 1      | odd, result=4   |
| 3    | 2   | 16        | 4      | even, skip mult |
| 4    | 1   | 256       | 4      | odd, result=1024|
| 5    | 0   | 65536     | 1024   | done            |

Only **4 iterations** instead of 10. For `2^1000`, it takes about 10 iterations instead of 1000.

---

## Why O(log n) Is So Fast

To appreciate O(log n), compare it with O(n):

| n               | O(log n) steps | O(n) steps      | Speedup        |
|-----------------|----------------|-----------------|----------------|
| 100             | 7              | 100             | 14x            |
| 10,000          | 14             | 10,000          | 714x           |
| 1,000,000       | 20             | 1,000,000       | 50,000x        |
| 1,000,000,000   | 30             | 1,000,000,000   | 33,333,333x    |

The larger the input, the more dramatic the advantage. At 1 billion elements, O(log n) is over
**33 million times** faster than O(n).

Even compared to O(sqrt(n)):

| n               | O(log n) | O(sqrt(n)) | O(n)            |
|-----------------|----------|------------|-----------------|
| 1,000,000       | 20       | 1,000      | 1,000,000       |
| 1,000,000,000   | 30       | 31,623     | 1,000,000,000   |

O(log n) is in a class of its own — only O(1) is faster.

---

## Comparison Table

| Algorithm                 | Time Complexity | Requires      | Space   |
|---------------------------|----------------|---------------|---------|
| Binary search (array)     | O(log n)       | Sorted array  | O(1)    |
| BST search (balanced)     | O(log n)       | Balanced BST  | O(1)*   |
| Exponentiation by squaring| O(log n)       | Integer exp   | O(1)    |
| Binary search (recursive) | O(log n)       | Sorted array  | O(log n)|

*Iterative version. Recursive BST search uses O(log n) stack space.

---

## Key Takeaways

1. **O(log n) means repeated halving.** Each step eliminates a constant fraction of the remaining
   input.

2. **Binary search** is the canonical O(log n) algorithm. It requires a sorted input.

3. **BST search** is O(log n) only when the tree is balanced.

4. **Exponentiation by squaring** reduces O(n) multiplications to O(log n).

5. **O(log n) scales incredibly well.** 1 billion elements need only about 30 steps.

6. **The overflow bug** in `(left + right) / 2` is a classic mistake. Always use
   `left + (right - left) / 2`.

7. **Real-world analogy:** Think of the phone book or the 20 questions game — each step halves
   the possibilities.

---

## References

1. Cormen, T. H., et al. *Introduction to Algorithms* (CLRS), Chapter 2 — Binary Search.
2. Knuth, D. E. *The Art of Computer Programming*, Vol. 3 — Searching and Sorting.
3. Bloch, J. "Extra, Extra — Read All About It: Nearly All Binary Searches and Mergesorts are
   Broken," Google Research Blog, 2006.
4. Sedgewick, R., Wayne, K. *Algorithms*, 4th Edition — Symbol Tables and Binary Search Trees.
5. Wikipedia — [Binary Search Algorithm](https://en.wikipedia.org/wiki/Binary_search_algorithm).
