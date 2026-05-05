# Factorial Time O(n!) -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Does Factorial Mean?](#what-does-factorial-mean)
3. [Why O(n!) Matters](#why-on-matters)
4. [Real-World Analogies](#real-world-analogies)
5. [Common Examples of O(n!) Algorithms](#common-examples-of-on-algorithms)
6. [Generating All Permutations](#generating-all-permutations)
7. [Brute-Force Traveling Salesman Problem](#brute-force-traveling-salesman-problem)
8. [Brute-Force Scheduling](#brute-force-scheduling)
9. [How Fast Does n! Grow?](#how-fast-does-n-grow)
10. [Visualizing the Growth](#visualizing-the-growth)
11. [When Do We Encounter O(n!)?](#when-do-we-encounter-on)
12. [Simple Code Examples](#simple-code-examples)
13. [Key Takeaways](#key-takeaways)
14. [Summary](#summary)

---

## Introduction

Factorial time complexity, written as **O(n!)**, is one of the slowest practical time
complexities you will encounter in computer science. An algorithm with factorial time
complexity becomes completely unusable for even moderately sized inputs. Understanding
why O(n!) is so expensive -- and recognizing when an algorithm has this complexity -- is
essential for every programmer.

This document explains factorial time from the ground up, assuming no prior knowledge
of algorithmic complexity beyond the basics.

---

## What Does Factorial Mean?

The **factorial** of a non-negative integer n, written as **n!**, is the product of all
positive integers from 1 to n:

```
n! = n x (n-1) x (n-2) x ... x 2 x 1
```

Some concrete values:

| n  | n!                    | Approximate             |
|----|----------------------|-------------------------|
| 0  | 1                    | 1                       |
| 1  | 1                    | 1                       |
| 2  | 2                    | 2                       |
| 3  | 6                    | 6                       |
| 4  | 24                   | 24                      |
| 5  | 120                  | 120                     |
| 6  | 720                  | 720                     |
| 7  | 5,040                | ~5 thousand              |
| 8  | 40,320               | ~40 thousand             |
| 9  | 362,880              | ~363 thousand            |
| 10 | 3,628,800            | ~3.6 million             |
| 12 | 479,001,600          | ~479 million             |
| 15 | 1,307,674,368,000    | ~1.3 trillion            |
| 20 | 2,432,902,008,176,640,000 | ~2.4 quintillion    |

Notice how absurdly fast the numbers grow. Going from n=10 (about 3.6 million) to n=15
(about 1.3 trillion) is a jump of roughly 360,000x. Going from n=15 to n=20 is another
jump of about 1.86 million times.

### Factorial Defined Recursively

Factorial has an elegant recursive definition:

```
0! = 1            (base case)
n! = n x (n-1)!   (recursive case, for n >= 1)
```

This recursive definition is the basis for many factorial-time algorithms.

---

## Why O(n!) Matters

When we say an algorithm runs in **O(n!) time**, it means the number of operations the
algorithm performs grows proportionally to n! as the input size n increases.

To put this in perspective:

- A modern computer can perform roughly **10^9 operations per second** (1 billion).
- At that speed:
  - n=10: 3.6 million operations = **0.0036 seconds**
  - n=12: 479 million operations = **0.48 seconds**
  - n=13: 6.2 billion operations = **6.2 seconds**
  - n=15: 1.3 trillion operations = **21.7 minutes**
  - n=17: 355 trillion operations = **4.1 days**
  - n=20: 2.4 quintillion operations = **77 years**
  - n=25: would take longer than the age of the universe

This means that **O(n!) algorithms are only practical for very small n** -- typically
n <= 10 or at most n <= 12 or so.

### Comparison With Other Complexities

For n = 20:

| Complexity | Operations       | Time at 10^9 ops/sec  |
|-----------|------------------|-----------------------|
| O(n)      | 20               | 0.00000002 sec        |
| O(n^2)    | 400              | 0.0000004 sec         |
| O(n^3)    | 8,000            | 0.000008 sec          |
| O(2^n)    | 1,048,576        | 0.001 sec             |
| O(n!)     | 2.4 x 10^18      | ~77 years             |

Even O(2^n) -- which itself is considered very slow -- is astronomically faster than
O(n!) for the same input size.

---

## Real-World Analogies

### Arranging Books on a Shelf

Imagine you have **n books** and you want to arrange them on a shelf in **every possible
order**. How many arrangements are there?

- For 3 books (A, B, C): 3! = 6 arrangements
  - ABC, ACB, BAC, BCA, CAB, CBA
- For 4 books: 4! = 24 arrangements
- For 10 books: 10! = 3,628,800 arrangements

If it takes you 1 second to rearrange the books, going through all arrangements of just
10 books would take about **42 days** working nonstop.

### Seating Arrangements at a Dinner Table

You have n guests and n chairs around a table. How many ways can you seat them?

- For the first chair, you have n choices.
- For the second chair, you have n-1 remaining choices.
- For the third, n-2 choices.
- And so on...

Total arrangements = n x (n-1) x (n-2) x ... x 1 = n!

With 12 guests, there are nearly **479 million** different seating arrangements.

### Trying Every Lock Combination

Imagine a lock with n distinct digits where each digit is used exactly once (a
permutation lock). The number of combinations to try is n!. With 10 digits, that is
3.6 million combinations.

---

## Common Examples of O(n!) Algorithms

### 1. Generating All Permutations

A **permutation** is a rearrangement of elements. Given n distinct elements, the total
number of permutations is exactly n!. Any algorithm that generates all permutations
must produce n! results, so it is at minimum O(n!).

### 2. Brute-Force Traveling Salesman Problem (TSP)

The TSP asks: given n cities, what is the shortest route that visits every city exactly
once and returns to the starting city?

The brute-force approach tries every possible ordering of cities. With n cities (fixing
the start), there are (n-1)! possible routes. For 10 cities, that is 362,880 routes.
For 20 cities, it is about 1.2 x 10^17 routes -- completely impractical.

### 3. Brute-Force Scheduling

Given n tasks that must be scheduled in some order, finding the optimal schedule by
trying every ordering requires examining n! schedules. For example, if you have 12
jobs on a single machine and want to minimize total completion time, brute force would
check all 479 million orderings.

---

## Generating All Permutations

The most fundamental O(n!) algorithm is generating all permutations of a set. Here is
the simplest approach: for each position, choose an unused element and recurse.

### Algorithm (Simple Recursive)

```
permute(elements, current):
    if current is complete (length == n):
        output current
        return
    for each unused element e in elements:
        mark e as used
        append e to current
        permute(elements, current)
        remove e from current
        mark e as unused
```

This generates exactly n! permutations, each taking O(n) time to output, giving a total
time complexity of **O(n! x n)** -- but we typically simplify to O(n!) since the n
factor is dominated by the factorial growth.

### Go Implementation

```go
package main

import "fmt"

func permute(nums []int) [][]int {
    var result [][]int
    var backtrack func(start int)

    backtrack = func(start int) {
        if start == len(nums) {
            perm := make([]int, len(nums))
            copy(perm, nums)
            result = append(result, perm)
            return
        }
        for i := start; i < len(nums); i++ {
            nums[start], nums[i] = nums[i], nums[start]
            backtrack(start + 1)
            nums[start], nums[i] = nums[i], nums[start]
        }
    }

    backtrack(0)
    return result
}

func main() {
    nums := []int{1, 2, 3}
    perms := permute(nums)
    fmt.Printf("Number of permutations: %d\n", len(perms))
    for _, p := range perms {
        fmt.Println(p)
    }
}
```

### Java Implementation

```java
import java.util.ArrayList;
import java.util.List;

public class Permutations {

    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, result);
        return result;
    }

    private static void backtrack(int[] nums, int start, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int num : nums) {
                perm.add(num);
            }
            result.add(perm);
            return;
        }
        for (int i = start; i < nums.length; i++) {
            int temp = nums[start];
            nums[start] = nums[i];
            nums[i] = temp;

            backtrack(nums, start + 1, result);

            temp = nums[start];
            nums[start] = nums[i];
            nums[i] = temp;
        }
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        List<List<Integer>> perms = permute(nums);
        System.out.println("Number of permutations: " + perms.size());
        for (List<Integer> p : perms) {
            System.out.println(p);
        }
    }
}
```

### Python Implementation

```python
def permute(nums):
    result = []

    def backtrack(start):
        if start == len(nums):
            result.append(nums[:])
            return
        for i in range(start, len(nums)):
            nums[start], nums[i] = nums[i], nums[start]
            backtrack(start + 1)
            nums[start], nums[i] = nums[i], nums[start]

    backtrack(0)
    return result


if __name__ == "__main__":
    nums = [1, 2, 3]
    perms = permute(nums)
    print(f"Number of permutations: {len(perms)}")
    for p in perms:
        print(p)
```

---

## Brute-Force Traveling Salesman Problem

The TSP brute-force approach generates all permutations of cities and computes the
total distance for each route.

### Go Implementation

```go
package main

import (
    "fmt"
    "math"
)

func tspBruteForce(dist [][]float64) (float64, []int) {
    n := len(dist)
    cities := make([]int, n-1)
    for i := 0; i < n-1; i++ {
        cities[i] = i + 1
    }

    bestDist := math.Inf(1)
    var bestRoute []int

    var tryAllRoutes func(start int)
    tryAllRoutes = func(start int) {
        if start == len(cities) {
            totalDist := dist[0][cities[0]]
            for i := 0; i < len(cities)-1; i++ {
                totalDist += dist[cities[i]][cities[i+1]]
            }
            totalDist += dist[cities[len(cities)-1]][0]

            if totalDist < bestDist {
                bestDist = totalDist
                bestRoute = make([]int, len(cities))
                copy(bestRoute, cities)
            }
            return
        }
        for i := start; i < len(cities); i++ {
            cities[start], cities[i] = cities[i], cities[start]
            tryAllRoutes(start + 1)
            cities[start], cities[i] = cities[i], cities[start]
        }
    }

    tryAllRoutes(0)
    return bestDist, bestRoute
}

func main() {
    dist := [][]float64{
        {0, 10, 15, 20},
        {10, 0, 35, 25},
        {15, 35, 0, 30},
        {20, 25, 30, 0},
    }
    best, route := tspBruteForce(dist)
    fmt.Printf("Best distance: %.1f\n", best)
    fmt.Printf("Route: 0 -> %v -> 0\n", route)
}
```

### Java Implementation

```java
public class TSPBruteForce {

    static double bestDist;
    static int[] bestRoute;

    public static void solve(double[][] dist) {
        int n = dist.length;
        int[] cities = new int[n - 1];
        for (int i = 0; i < n - 1; i++) {
            cities[i] = i + 1;
        }
        bestDist = Double.MAX_VALUE;
        bestRoute = new int[n - 1];
        tryAllRoutes(dist, cities, 0);
    }

    private static void tryAllRoutes(double[][] dist, int[] cities, int start) {
        if (start == cities.length) {
            double total = dist[0][cities[0]];
            for (int i = 0; i < cities.length - 1; i++) {
                total += dist[cities[i]][cities[i + 1]];
            }
            total += dist[cities[cities.length - 1]][0];
            if (total < bestDist) {
                bestDist = total;
                System.arraycopy(cities, 0, bestRoute, 0, cities.length);
            }
            return;
        }
        for (int i = start; i < cities.length; i++) {
            int temp = cities[start];
            cities[start] = cities[i];
            cities[i] = temp;
            tryAllRoutes(dist, cities, start + 1);
            temp = cities[start];
            cities[start] = cities[i];
            cities[i] = temp;
        }
    }

    public static void main(String[] args) {
        double[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        solve(dist);
        System.out.printf("Best distance: %.1f%n", bestDist);
        System.out.print("Route: 0 -> ");
        for (int c : bestRoute) System.out.print(c + " -> ");
        System.out.println("0");
    }
}
```

### Python Implementation

```python
import itertools
import math


def tsp_brute_force(dist):
    n = len(dist)
    cities = list(range(1, n))
    best_dist = math.inf
    best_route = None

    for perm in itertools.permutations(cities):
        total = dist[0][perm[0]]
        for i in range(len(perm) - 1):
            total += dist[perm[i]][perm[i + 1]]
        total += dist[perm[-1]][0]
        if total < best_dist:
            best_dist = total
            best_route = perm

    return best_dist, best_route


if __name__ == "__main__":
    dist = [
        [0, 10, 15, 20],
        [10, 0, 35, 25],
        [15, 35, 0, 30],
        [20, 25, 30, 0],
    ]
    best, route = tsp_brute_force(dist)
    print(f"Best distance: {best}")
    print(f"Route: 0 -> {list(route)} -> 0")
```

---

## Brute-Force Scheduling

A scheduling problem where we try every ordering of n tasks:

### Python Example

```python
import itertools


def brute_force_schedule(tasks):
    """
    Each task is (processing_time, weight).
    Minimize total weighted completion time.
    """
    best_cost = float("inf")
    best_order = None

    for perm in itertools.permutations(range(len(tasks))):
        time = 0
        cost = 0
        for idx in perm:
            time += tasks[idx][0]
            cost += time * tasks[idx][1]
        if cost < best_cost:
            best_cost = cost
            best_order = perm

    return best_cost, best_order


tasks = [(3, 2), (1, 5), (4, 1), (2, 3)]
cost, order = brute_force_schedule(tasks)
print(f"Best cost: {cost}, Order: {order}")
# O(n!) -- only feasible for small n
```

---

## How Fast Does n! Grow?

To truly appreciate the growth rate, consider these comparisons:

### n! vs Powers of 2

| n  | 2^n         | n!                 | n! / 2^n          |
|----|-------------|--------------------|--------------------|
| 5  | 32          | 120                | 3.75               |
| 10 | 1,024       | 3,628,800          | 3,543              |
| 15 | 32,768      | 1,307,674,368,000  | 39,902,200         |
| 20 | 1,048,576   | 2.43 x 10^18       | 2.32 x 10^12      |

Factorial grows much faster than exponential. While 2^n doubles with each increment of
n, n! multiplies by (n) with each increment -- and n keeps getting bigger.

### Physical Analogies for Large Factorials

- **52! (a deck of cards)**: The number of ways to shuffle a standard deck of 52 cards
  is 52! which is approximately 8.07 x 10^67. This number is so large that if every
  star in the observable universe shuffled a deck every second since the Big Bang, the
  probability of any two identical shuffles would be negligibly small.

- **20!**: About 2.4 quintillion. If you counted one permutation per second, it would
  take about 77 billion years -- roughly 5.5 times the current age of the universe.

---

## Visualizing the Growth

Think of a permutation tree:

```
Level 0:         [root]                         1 node
                /  |  \
Level 1:      1    2    3                       n choices
             / \  / \  / \
Level 2:    2  3 1  3 1  2                      n-1 choices each
            |  | |  | |  |
Level 3:    3  2 3  1 2  1                      n-2 choices each

Leaves:     123 132 213 231 312 321             n! leaves total
```

At each level, the branching factor decreases by 1. The total number of leaves
(complete permutations) is:

```
n x (n-1) x (n-2) x ... x 1 = n!
```

---

## When Do We Encounter O(n!)?

You encounter O(n!) complexity when an algorithm:

1. **Enumerates all permutations** of the input.
2. **Tries every ordering** to find the best one.
3. **Generates all possible arrangements** where order matters and each element is used
   exactly once.

Common problem domains:

- **Combinatorial optimization**: TSP, job scheduling, assignment problems.
- **Constraint satisfaction**: when solved by exhaustive enumeration.
- **Cryptanalysis**: certain brute-force attacks on permutation-based ciphers.
- **Game theory**: analyzing all possible move sequences in some games.

---

## Simple Code Examples

### Computing n! Itself

**Go:**
```go
func factorial(n int) int {
    if n <= 1 {
        return 1
    }
    return n * factorial(n-1)
}
```

**Java:**
```java
static long factorial(int n) {
    if (n <= 1) return 1;
    return (long) n * factorial(n - 1);
}
```

**Python:**
```python
def factorial(n):
    if n <= 1:
        return 1
    return n * factorial(n - 1)
```

Computing n! itself is O(n) in time (n multiplications). It is the algorithms that
**do n! amount of work** (like generating all permutations) that are O(n!).

---

## Key Takeaways

1. **n! = n x (n-1) x (n-2) x ... x 1** -- the product of all positive integers up to n.

2. **O(n!) is the slowest practical complexity**. Beyond n = 10-12, it becomes
   infeasible on modern hardware.

3. **Factorial grows faster than exponential**. For large n, n! >> 2^n >> n^k for any
   fixed k.

4. **Common O(n!) algorithms** include brute-force permutation generation, brute-force
   TSP, and brute-force scheduling.

5. **Recognizing O(n!)** is important so you know to look for better approaches
   (dynamic programming, heuristics, approximation algorithms).

6. **Real-world analogy**: the number of ways to arrange n distinct items in all
   possible orders is exactly n!.

---

## Summary

Factorial time complexity O(n!) represents the most expensive class of algorithms
commonly encountered. It arises whenever an algorithm must consider every possible
ordering (permutation) of its input. Because factorial growth is extraordinarily rapid,
O(n!) algorithms are only usable for very small inputs (typically n <= 10-12). For
larger inputs, we must use smarter techniques like dynamic programming (which can solve
TSP in O(2^n * n)), heuristic methods, or approximation algorithms. Understanding O(n!)
helps you recognize when a brute-force approach is doomed to fail and motivates the
search for more efficient solutions.
