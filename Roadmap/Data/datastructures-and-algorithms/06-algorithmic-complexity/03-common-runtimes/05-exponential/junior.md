# Exponential Time O(2^n) — Junior Level

## Table of Contents

- [Introduction](#introduction)
- [What Does Exponential Mean?](#what-does-exponential-mean)
- [The Doubling Effect](#the-doubling-effect)
- [Real-World Analogies](#real-world-analogies)
- [Classic Examples](#classic-examples)
  - [Recursive Fibonacci](#recursive-fibonacci)
  - [Power Set Generation](#power-set-generation)
  - [Tower of Hanoi](#tower-of-hanoi)
  - [Brute-Force Subset Sum](#brute-force-subset-sum)
- [Why O(2^n) Is Impractical](#why-o2n-is-impractical)
- [Growth Comparison Table](#growth-comparison-table)
- [How to Recognize O(2^n)](#how-to-recognize-o2n)
- [Summary](#summary)
- [What's Next](#whats-next)

---

## Introduction

Exponential time complexity O(2^n) describes algorithms whose running time doubles with every additional element in the input. These algorithms are among the slowest practical algorithms you will encounter. Understanding why they are slow and recognizing when your code runs in exponential time is a critical skill for any developer.

This document covers the fundamentals: what exponential growth means, how to spot it, and the classic algorithms that exhibit this behavior.

---

## What Does Exponential Mean?

When we say an algorithm runs in O(2^n) time, it means the number of operations roughly doubles every time n increases by 1.

```
n = 1  ->  2 operations
n = 2  ->  4 operations
n = 3  ->  8 operations
n = 4  ->  16 operations
n = 5  ->  32 operations
n = 10 ->  1,024 operations
n = 20 ->  1,048,576 operations (about 1 million)
n = 30 ->  1,073,741,824 operations (about 1 billion)
n = 40 ->  1,099,511,627,776 operations (about 1 trillion)
```

The key insight: **every single increment of n doubles the total work**. This is what makes exponential algorithms fundamentally different from polynomial ones like O(n^2) or O(n^3).

---

## The Doubling Effect

Think of it this way. If your algorithm takes 1 second for n=20, then:

| n  | Time          |
|----|---------------|
| 20 | 1 second      |
| 21 | 2 seconds     |
| 22 | 4 seconds     |
| 23 | 8 seconds     |
| 25 | 32 seconds    |
| 30 | ~17 minutes   |
| 35 | ~9 hours      |
| 40 | ~12.7 days    |
| 50 | ~35.7 years   |
| 60 | ~36,559 years |

By n=60, you would need longer than recorded human history to finish. This is why exponential algorithms are considered **impractical** for anything beyond small inputs.

---

## Real-World Analogies

### The Rice and Chessboard

A famous legend: a king promises to reward a wise man by placing rice on a chessboard — 1 grain on the first square, 2 on the second, 4 on the third, doubling each time. By square 64, the total exceeds 18 quintillion grains — more rice than has ever been produced in human history.

This is O(2^n) in action: 2^64 = 18,446,744,073,709,551,616.

### The Paper Folding

Take a sheet of paper and fold it in half. Each fold doubles the thickness. After 42 folds (if physically possible), the paper would reach the Moon — roughly 384,400 km thick. That is the power of doubling.

### The Phone Tree

Imagine you need to spread a message. You call 2 people. Each of them calls 2 more people. Each of those calls 2 more. After n rounds, 2^n people have been contacted. After 33 rounds, every person on Earth could be reached.

---

## Classic Examples

### Recursive Fibonacci

The naive recursive Fibonacci is the most famous example of O(2^n) behavior.

**Go:**

```go
package main

import "fmt"

// Fib computes the nth Fibonacci number recursively.
// Time Complexity: O(2^n) — each call spawns two more calls.
// Space Complexity: O(n) — maximum depth of the recursion stack.
func Fib(n int) int {
    if n <= 1 {
        return n
    }
    return Fib(n-1) + Fib(n-2)
}

func main() {
    for i := 0; i <= 10; i++ {
        fmt.Printf("Fib(%d) = %d\n", i, Fib(i))
    }
    // Try n=40 and notice the delay
    // fmt.Println(Fib(40)) // Takes several seconds
    // fmt.Println(Fib(50)) // Would take hours
}
```

**Java:**

```java
public class Fibonacci {

    // Time Complexity: O(2^n)
    // Space Complexity: O(n) — recursion stack depth
    public static int fib(int n) {
        if (n <= 1) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 10; i++) {
            System.out.printf("Fib(%d) = %d%n", i, fib(i));
        }
        // Try n=40 and notice the delay
        // System.out.println(fib(40)); // Takes several seconds
        // System.out.println(fib(50)); // Would take hours
    }
}
```

**Python:**

```python
def fib(n: int) -> int:
    """
    Compute the nth Fibonacci number recursively.
    Time Complexity: O(2^n)
    Space Complexity: O(n) — recursion stack depth
    """
    if n <= 1:
        return n
    return fib(n - 1) + fib(n - 2)


if __name__ == "__main__":
    for i in range(11):
        print(f"Fib({i}) = {fib(i)}")
    # Try n=40 and notice the delay
    # print(fib(40))  # Takes several seconds
    # print(fib(50))  # Would take hours
```

**Why is it O(2^n)?** Every call to `Fib(n)` makes two recursive calls: `Fib(n-1)` and `Fib(n-2)`. This creates a binary tree of calls. The tree has depth n, so the total number of nodes is roughly 2^n. (Precisely it is O(phi^n) where phi = 1.618..., but we approximate as O(2^n).)

---

### Power Set Generation

The **power set** of a set S is the set of all subsets of S. If S has n elements, the power set has exactly 2^n subsets. Generating all of them is inherently O(2^n).

**Go:**

```go
package main

import "fmt"

// PowerSet generates all subsets of the given slice.
// Time Complexity: O(2^n * n) — 2^n subsets, each up to size n.
// Space Complexity: O(2^n * n) — storing all subsets.
func PowerSet(items []int) [][]int {
    result := [][]int{{}} // Start with the empty set.

    for _, item := range items {
        newSubsets := make([][]int, len(result))
        for i, subset := range result {
            // Copy existing subset and add current item.
            newSubset := make([]int, len(subset)+1)
            copy(newSubset, subset)
            newSubset[len(subset)] = item
            newSubsets[i] = newSubset
        }
        result = append(result, newSubsets...)
    }

    return result
}

func main() {
    items := []int{1, 2, 3}
    subsets := PowerSet(items)
    fmt.Printf("Power set of %v (%d subsets):\n", items, len(subsets))
    for _, s := range subsets {
        fmt.Println(s)
    }
}
```

**Java:**

```java
import java.util.ArrayList;
import java.util.List;

public class PowerSet {

    // Time Complexity: O(2^n * n)
    // Space Complexity: O(2^n * n)
    public static List<List<Integer>> powerSet(int[] items) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // Start with the empty set.

        for (int item : items) {
            List<List<Integer>> newSubsets = new ArrayList<>();
            for (List<Integer> subset : result) {
                List<Integer> newSubset = new ArrayList<>(subset);
                newSubset.add(item);
                newSubsets.add(newSubset);
            }
            result.addAll(newSubsets);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] items = {1, 2, 3};
        List<List<Integer>> subsets = powerSet(items);
        System.out.printf("Power set (%d subsets):%n", subsets.size());
        for (List<Integer> s : subsets) {
            System.out.println(s);
        }
    }
}
```

**Python:**

```python
from typing import List


def power_set(items: List[int]) -> List[List[int]]:
    """
    Generate all subsets of the given list.
    Time Complexity: O(2^n * n)
    Space Complexity: O(2^n * n)
    """
    result = [[]]  # Start with the empty set.

    for item in items:
        new_subsets = [subset + [item] for subset in result]
        result.extend(new_subsets)

    return result


if __name__ == "__main__":
    items = [1, 2, 3]
    subsets = power_set(items)
    print(f"Power set ({len(subsets)} subsets):")
    for s in subsets:
        print(s)
```

**Key Insight:** No matter how you generate the power set, you must produce 2^n subsets. The algorithm is O(2^n) because the output itself has exponential size.

---

### Tower of Hanoi

The Tower of Hanoi requires moving n disks from one peg to another, using a third peg, with the constraint that a larger disk can never sit on a smaller one. The minimum number of moves is exactly 2^n - 1.

**Go:**

```go
package main

import "fmt"

var moveCount int

// Hanoi solves the Tower of Hanoi for n disks.
// Time Complexity: O(2^n) — exactly 2^n - 1 moves.
// Space Complexity: O(n) — recursion depth.
func Hanoi(n int, from, to, aux string) {
    if n == 0 {
        return
    }
    Hanoi(n-1, from, aux, to)
    moveCount++
    fmt.Printf("Move disk %d from %s to %s\n", n, from, to)
    Hanoi(n-1, aux, to, from)
}

func main() {
    n := 4
    moveCount = 0
    Hanoi(n, "A", "C", "B")
    fmt.Printf("Total moves for %d disks: %d\n", n, moveCount)
}
```

**Java:**

```java
public class TowerOfHanoi {

    static int moveCount = 0;

    // Time Complexity: O(2^n)
    // Space Complexity: O(n)
    public static void hanoi(int n, String from, String to, String aux) {
        if (n == 0) return;
        hanoi(n - 1, from, aux, to);
        moveCount++;
        System.out.printf("Move disk %d from %s to %s%n", n, from, to);
        hanoi(n - 1, aux, to, from);
    }

    public static void main(String[] args) {
        int n = 4;
        moveCount = 0;
        hanoi(n, "A", "C", "B");
        System.out.printf("Total moves for %d disks: %d%n", n, moveCount);
    }
}
```

**Python:**

```python
move_count = 0


def hanoi(n: int, source: str, target: str, auxiliary: str) -> None:
    """
    Solve Tower of Hanoi for n disks.
    Time Complexity: O(2^n) — exactly 2^n - 1 moves.
    Space Complexity: O(n) — recursion depth.
    """
    global move_count
    if n == 0:
        return
    hanoi(n - 1, source, auxiliary, target)
    move_count += 1
    print(f"Move disk {n} from {source} to {target}")
    hanoi(n - 1, auxiliary, target, source)


if __name__ == "__main__":
    n = 4
    move_count = 0
    hanoi(n, "A", "C", "B")
    print(f"Total moves for {n} disks: {move_count}")
```

**Why exactly 2^n - 1?** The recurrence is T(n) = 2*T(n-1) + 1. Solving: T(n) = 2^n - 1. This is provably optimal — no algorithm can do it in fewer moves.

---

### Brute-Force Subset Sum

Given a set of integers and a target sum, determine if any subset sums to the target. The brute force approach checks all 2^n subsets.

**Go:**

```go
package main

import "fmt"

// SubsetSum checks all 2^n subsets using bit manipulation.
// Time Complexity: O(2^n * n)
// Space Complexity: O(1) — no extra storage needed.
func SubsetSum(nums []int, target int) bool {
    n := len(nums)
    total := 1 << n // 2^n

    for mask := 0; mask < total; mask++ {
        sum := 0
        for i := 0; i < n; i++ {
            if mask&(1<<i) != 0 {
                sum += nums[i]
            }
        }
        if sum == target {
            return true
        }
    }
    return false
}

func main() {
    nums := []int{3, 7, 1, 8, -2, 5}
    target := 14
    fmt.Printf("Can subset of %v sum to %d? %v\n", nums, target, SubsetSum(nums, target))
}
```

**Java:**

```java
public class SubsetSum {

    // Time Complexity: O(2^n * n)
    // Space Complexity: O(1)
    public static boolean subsetSum(int[] nums, int target) {
        int n = nums.length;
        int total = 1 << n; // 2^n

        for (int mask = 0; mask < total; mask++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    sum += nums[i];
                }
            }
            if (sum == target) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {3, 7, 1, 8, -2, 5};
        int target = 14;
        System.out.printf("Can subset sum to %d? %b%n", target, subsetSum(nums, target));
    }
}
```

**Python:**

```python
from typing import List


def subset_sum(nums: List[int], target: int) -> bool:
    """
    Check if any subset of nums sums to target.
    Time Complexity: O(2^n * n)
    Space Complexity: O(1)
    """
    n = len(nums)
    for mask in range(1 << n):  # Iterate over all 2^n subsets
        total = 0
        for i in range(n):
            if mask & (1 << i):
                total += nums[i]
        if total == target:
            return True
    return False


if __name__ == "__main__":
    nums = [3, 7, 1, 8, -2, 5]
    target = 14
    print(f"Can subset of {nums} sum to {target}? {subset_sum(nums, target)}")
```

---

## Why O(2^n) Is Impractical

For n > 25, exponential algorithms become extremely slow on modern hardware. Consider a computer that performs 10^9 operations per second:

| n   | 2^n              | Time at 10^9 ops/sec |
|-----|------------------|----------------------|
| 10  | 1,024            | ~1 microsecond       |
| 20  | 1,048,576        | ~1 millisecond       |
| 25  | 33,554,432       | ~34 milliseconds     |
| 30  | 1,073,741,824    | ~1 second            |
| 35  | 34,359,738,368   | ~34 seconds          |
| 40  | 1,099,511,627,776| ~18 minutes          |
| 50  | ~1.13 * 10^15    | ~13 days             |
| 60  | ~1.15 * 10^18    | ~36.5 years          |
| 100 | ~1.27 * 10^30    | ~4 * 10^13 years     |

For comparison, the universe is about 1.38 * 10^10 years old. An O(2^n) algorithm with n=100 would take roughly 3,000 times the age of the universe.

---

## Growth Comparison Table

| n    | O(log n) | O(n)   | O(n^2)      | O(2^n)              | O(n!)                |
|------|----------|--------|-------------|---------------------|----------------------|
| 5    | 2        | 5      | 25          | 32                  | 120                  |
| 10   | 3        | 10     | 100         | 1,024               | 3,628,800            |
| 15   | 4        | 15     | 225         | 32,768              | 1.31 * 10^12         |
| 20   | 4        | 20     | 400         | 1,048,576           | 2.43 * 10^18         |
| 25   | 5        | 25     | 625         | 33,554,432          | 1.55 * 10^25         |
| 30   | 5        | 30     | 900         | 1,073,741,824       | 2.65 * 10^32         |

Notice that O(2^n) grows much faster than any polynomial, but O(n!) grows even faster. For n=20, 2^n is about a million while n! is about 2 quintillion.

---

## How to Recognize O(2^n)

Look for these patterns in your code:

### Pattern 1: Two Recursive Calls Per Level

```
function solve(n):
    if base case: return
    solve(n-1)     // first recursive call
    solve(n-1)     // second recursive call
```

Each call creates two more calls. Total calls: 2^n.

### Pattern 2: Include/Exclude Decisions

```
function generate(index, current):
    if index == n: process(current)
    generate(index + 1, current)              // exclude item
    generate(index + 1, current + items[i])   // include item
```

At each step, you branch into two paths: include or exclude. This creates 2^n paths.

### Pattern 3: Iterating Over All Bitmasks

```
for mask in range(2^n):
    process(mask)
```

Explicitly iterating over all 2^n subsets.

### Pattern 4: Recurrence T(n) = 2*T(n-1) + O(f(n))

If you can write the recurrence relation as T(n) = 2*T(n-1) + O(something polynomial), the solution is O(2^n).

---

## Summary

- **O(2^n)** means the work doubles with each increment of n.
- Classic examples: recursive Fibonacci, power set, Tower of Hanoi, brute-force subset problems.
- For n > 25-30, exponential algorithms are impractical on modern hardware.
- Recognize exponential algorithms by looking for binary branching in recursion or enumeration of all subsets.
- The output of some problems (like power set) is inherently exponential, making O(2^n) unavoidable.
- For problems where the output is not exponential, techniques like dynamic programming and memoization can often reduce exponential time to polynomial time (covered in the middle level).

---

## What's Next

In the [middle level](middle.md), you will learn techniques to tame exponential algorithms:
- **Memoization** to eliminate redundant computation
- **Backtracking with pruning** to skip dead-end branches
- **Meet-in-the-middle** to reduce O(2^n) to O(2^(n/2))
- Comparison with other super-polynomial runtimes like O(n!) and O(n^n)
