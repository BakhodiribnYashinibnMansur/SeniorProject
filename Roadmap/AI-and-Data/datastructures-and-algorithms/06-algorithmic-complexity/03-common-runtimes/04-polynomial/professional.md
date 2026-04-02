# Polynomial Time O(n^2), O(n^3) -- Professional Level

## Table of Contents

1. [Overview](#overview)
2. [The Polynomial Hierarchy](#the-polynomial-hierarchy)
3. [P vs NP](#p-vs-np)
4. [Proving Quadratic Lower Bounds](#proving-quadratic-lower-bounds)
5. [Matrix Multiplication Complexity Bounds](#matrix-multiplication-complexity-bounds)
6. [The Strong Exponential Time Hypothesis (SETH)](#the-strong-exponential-time-hypothesis-seth)
7. [Fine-Grained Complexity Theory](#fine-grained-complexity-theory)
8. [Practical Implications of Lower Bounds](#practical-implications-of-lower-bounds)
9. [Summary](#summary)

---

## Overview

At the professional/research level, we move beyond optimizing specific algorithms
to understanding the fundamental limits of computation. Can we prove that certain
problems require O(n^2) time? What is the fastest possible matrix multiplication?
These are deep questions in theoretical computer science with direct implications
for algorithm design.

---

## The Polynomial Hierarchy

### Complexity Class P

**P** (Polynomial Time) is the class of decision problems solvable by a
deterministic Turing machine in O(n^k) time for some constant k.

Key properties:
- P is closed under composition: if f is in P and g is in P, then f(g(x)) is in P
- All problems in P are considered "efficiently solvable"
- Examples: sorting, shortest path, primality testing (AKS), linear programming

### Complexity Class NP

**NP** (Nondeterministic Polynomial Time) is the class of decision problems where
a "yes" answer can be **verified** in polynomial time given a certificate (witness).

- Every problem in P is also in NP (P is a subset of NP)
- NP-complete problems are the "hardest" problems in NP
- Examples: SAT, Traveling Salesman (decision version), Graph Coloring

### The Polynomial Hierarchy (PH)

Beyond P and NP, the polynomial hierarchy extends the concept:

```
Sigma_0^P = Pi_0^P = P
Sigma_1^P = NP
Pi_1^P = coNP
Sigma_2^P = NP^NP
Pi_2^P = coNP^NP
...
Sigma_k^P = NP^(Sigma_{k-1}^P)
```

The full hierarchy:
```
P  subset  NP  subset  PH  subset  PSPACE  subset  EXPTIME
```

### Why It Matters

If you prove a problem is in P, you know there exists a polynomial-time algorithm.
If a problem is NP-hard, the best known algorithms are exponential. The distinction
between O(n^2) and O(2^n) is the difference between feasible and infeasible for
moderate n.

---

## P vs NP

The most important open problem in computer science:

**Is P = NP?**

In other words: if a solution can be *verified* quickly, can it also be *found*
quickly?

### What We Know

- Nobody has proven P = NP or P != NP
- Most researchers believe P != NP
- A proof either way would win the Clay Millennium Prize ($1,000,000)
- The question has been open since 1971 (Cook's theorem)

### Implications If P = NP

- Every NP problem has an efficient algorithm
- Cryptography based on computational hardness (RSA, AES) would collapse
- Optimization problems across all fields would become tractable
- AI, logistics, drug design, etc. would be revolutionized

### Implications If P != NP

- There exist fundamentally hard problems with no efficient solution
- Approximation algorithms and heuristics remain essential
- Cryptography has a solid theoretical foundation
- Some planning and scheduling problems will always require trade-offs

### Where Polynomial Time Fits

Even within P, the degree of the polynomial matters enormously:

| Complexity | Name       | Practical?                        |
|------------|------------|-----------------------------------|
| O(n)       | Linear     | Yes, scales to billions           |
| O(n^2)     | Quadratic  | Yes, for n < ~10,000              |
| O(n^3)     | Cubic      | Marginal, for n < ~1,000          |
| O(n^6)     | Sextic     | Barely, for n < ~50               |
| O(n^100)   | High poly  | Theoretically in P, practically useless |

A problem being "in P" does not mean it is practically solvable. The polynomial's
degree and constant matter.

---

## Proving Quadratic Lower Bounds

### The Element Distinctness Problem

**Problem:** Given n numbers, determine if all are distinct.

**Known bounds:**
- Upper bound: O(n log n) via sorting, or O(n) expected via hashing
- Lower bound: Omega(n log n) in the algebraic decision tree model (Ben-Or 1983)

This means no comparison-based algorithm can solve element distinctness faster
than O(n log n).

### The 3SUM Conjecture

**Problem:** Given n integers, find three that sum to zero.

**Best known:** O(n^2) (sorting + two pointers for each element)

**3SUM conjecture:** There is no O(n^(2-epsilon)) algorithm for 3SUM for any
epsilon > 0.

This conjecture is widely believed but unproven. It serves as a basis for
conditional lower bounds: many geometric and graph problems are "3SUM-hard,"
meaning they are at least as hard as 3SUM.

**Problems with 3SUM-hard lower bounds:**
- Detecting if n points determine a degenerate triangle
- Determining if a point set contains three collinear points
- Computing the Frechet distance between two curves

### APSP Conjecture

**All-Pairs Shortest Paths (APSP) conjecture:** There is no truly subcubic
algorithm for APSP in dense graphs with arbitrary (possibly negative) edge
weights.

The best known algorithm is O(n^3) (Floyd-Warshall). Marginal improvements
exist (O(n^3 / log n)) but no O(n^(3-epsilon)) algorithm is known.

### Conditional Lower Bounds

Since proving unconditional lower bounds is extremely hard (it requires
separating complexity classes), researchers use **conditional** lower bounds:

"If the 3SUM conjecture is true, then problem X requires Omega(n^2) time."

This approach has been very productive:

```
3SUM conjecture -> Omega(n^2) for many geometric problems
SETH -> Omega(n^2) for edit distance, LCS, Frechet distance
APSP conjecture -> Omega(n^3) for many path problems
```

---

## Matrix Multiplication Complexity Bounds

### The Exponent omega

The matrix multiplication exponent omega is defined as:

omega = inf { c : two n x n matrices can be multiplied in O(n^c) time }

### History of Bounds

| Year | Authors                  | Exponent    |
|------|--------------------------|-------------|
| 1969 | Strassen                 | 2.807       |
| 1978 | Pan                      | 2.796       |
| 1981 | Bini et al.              | 2.780       |
| 1986 | Strassen                 | 2.479       |
| 1990 | Coppersmith-Winograd     | 2.376       |
| 2012 | Williams                 | 2.3728639   |
| 2014 | Le Gall                  | 2.3728639   |
| 2020 | Alman-Williams           | 2.3728596   |
| 2024 | Duan, Wu, Zhou           | 2.371552    |

### Current State

- **Best upper bound:** omega < 2.372 (Duan, Wu, Zhou 2024)
- **Best lower bound:** omega >= 2 (trivially, since you must read n^2 entries)
- **Conjecture:** omega = 2 (matrix multiplication is essentially quadratic)

### Why This Matters

Matrix multiplication is a fundamental primitive. Many algorithms reduce to it:

- **Graph algorithms:** Transitive closure, shortest paths, triangle detection
- **Linear algebra:** Matrix inversion, determinant, solving linear systems
- **Polynomial multiplication:** Via matrices
- **Parsing:** CYK algorithm for context-free grammars

If omega = 2, all these problems would have nearly quadratic algorithms.

### Coppersmith-Winograd Framework

The Coppersmith-Winograd approach uses the **tensor rank** of the matrix
multiplication tensor. The exponent omega is related to the asymptotic rank of:

```
<n, n, n> = sum_{i,j,k} a_{ij} * b_{jk} * c_{ki}
```

Finding tensors of low rank yields faster matrix multiplication algorithms.
Current research explores:
- Group-theoretic methods
- Laser method improvements
- Barriers to omega = 2 proofs

---

## The Strong Exponential Time Hypothesis (SETH)

### Statement

**SETH:** For every epsilon > 0, there exists a k such that k-SAT cannot be
solved in O(2^((1-epsilon)*n)) time, where n is the number of variables.

In simpler terms: CNF-SAT cannot be solved much faster than trying all 2^n
assignments.

### Consequences for Polynomial Problems

SETH has surprisingly strong implications for polynomial-time problems:

1. **Edit distance:** SETH implies no O(n^(2-epsilon)) algorithm exists for
   computing the edit distance between two strings of length n.

2. **Longest Common Subsequence:** SETH implies no O(n^(2-epsilon)) algorithm.

3. **Orthogonal Vectors:** Given two sets of n vectors in d dimensions, SETH
   implies no O(n^(2-epsilon)) algorithm to determine if there exist
   orthogonal vectors (one from each set) when d = omega(log n).

### Why This Matters Practically

These conditional lower bounds tell us:
- Certain quadratic algorithms may be **optimal** (up to subpolynomial factors)
- Investing effort to find O(n^1.99) algorithms for these problems is likely futile
- Focus on practical optimizations (constants, parallelism, approximation) instead

---

## Fine-Grained Complexity Theory

Fine-grained complexity goes beyond "polynomial vs exponential" to distinguish
between different polynomial running times.

### Key Problems and Their Relationships

```
                    3SUM
                   /    \
                  /      \
    3SUM-hard problems   APSP
         |                |
    Geometric problems   Shortest paths problems
                          |
                     SETH-hard problems
                          |
                    Edit distance, LCS,
                    Frechet distance
```

### Reductions Between Polynomial Problems

Just as NP-completeness reduces problems to each other, fine-grained reductions
preserve the polynomial exponent:

```
3SUM -> Geom Base (O(n^2) reduces to O(n^2))
OV -> Edit Distance (O(n^2) reduces to O(n^2))
APSP -> Negative Triangle (O(n^3) reduces to O(n^3))
```

These reductions form a web of relationships that map out the landscape of
polynomial-time computation.

---

## Practical Implications of Lower Bounds

### For Algorithm Designers

1. **Know when to stop optimizing:** If a problem has a conditional quadratic
   lower bound, do not spend months trying to find an O(n^1.5) algorithm.

2. **Focus on constants and practical performance:** Cache efficiency, SIMD
   instructions, and parallelism can yield 10-100x speedups without changing
   the asymptotic complexity.

3. **Consider approximation:** If exact computation requires O(n^2), a
   (1+epsilon)-approximation might be achievable in O(n * polylog(n)).

### For System Architects

1. **Plan for quadratic growth:** If your core algorithm is provably Omega(n^2),
   design the system to handle that growth curve.

2. **Budget for hardware scaling:** Doubling data means 4x compute for O(n^2).
   Ensure your infrastructure planning accounts for this.

3. **Approximate when possible:** In ML pipelines, approximate nearest neighbor
   search is almost always preferred over exact quadratic comparison.

---

## Summary

1. **The polynomial hierarchy** organizes problems by computational difficulty.
   P contains problems solvable in polynomial time; NP contains problems
   verifiable in polynomial time.

2. **P vs NP** remains open. Even within P, the polynomial degree matters:
   O(n^2) and O(n^3) have very different practical limits.

3. **Conditional lower bounds** (3SUM conjecture, SETH, APSP conjecture) provide
   strong evidence that certain quadratic/cubic algorithms are optimal.

4. **Matrix multiplication** has exponent omega between 2 and 2.372. Proving
   omega = 2 would have sweeping consequences for graph algorithms, linear
   algebra, and parsing.

5. **Fine-grained complexity** maps relationships between polynomial problems,
   showing which quadratic problems are equivalent in difficulty.

6. **Practically:** Know when you are fighting a lower bound. Focus engineering
   effort on constants, parallelism, and approximation rather than trying to
   break conjectured barriers.
