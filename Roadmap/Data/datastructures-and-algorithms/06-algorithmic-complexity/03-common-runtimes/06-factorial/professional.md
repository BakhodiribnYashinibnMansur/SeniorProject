# Factorial Time O(n!) -- Professional Level

## Table of Contents

1. [Introduction](#introduction)
2. [Stirling's Approximation: Derivation and Proof](#stirlings-approximation-derivation-and-proof)
3. [Factorial in Complexity Theory](#factorial-in-complexity-theory)
4. [Counting Argument Lower Bounds](#counting-argument-lower-bounds)
5. [The Permanent: #P-Completeness](#the-permanent-p-completeness)
6. [Relation to PSPACE](#relation-to-pspace)
7. [Gamma Function and Continuous Factorial](#gamma-function-and-continuous-factorial)
8. [Subexponential Algorithms for Factorial-Hard Problems](#subexponential-algorithms-for-factorial-hard-problems)
9. [Information-Theoretic Implications](#information-theoretic-implications)
10. [Open Problems](#open-problems)
11. [Key Takeaways](#key-takeaways)

---

## Introduction

At the professional level, we examine the theoretical foundations of factorial
complexity: why it arises, what it means in formal complexity theory, how to prove
lower bounds using counting arguments, and the deep connections between factorials and
fundamental problems in theoretical computer science.

---

## Stirling's Approximation: Derivation and Proof

### The Formula

```
n! ~ sqrt(2 * pi * n) * (n / e)^n
```

More precisely:
```
n! = sqrt(2 * pi * n) * (n / e)^n * (1 + 1/(12n) + 1/(288n^2) - 139/(51840n^3) + ...)
```

### Proof Sketch via the Laplace Method

**Step 1**: Express n! as an integral using the Gamma function:
```
n! = Gamma(n+1) = integral from 0 to infinity of t^n * e^(-t) dt
```

**Step 2**: Substitute t = n + x*sqrt(n) to center the integrand at its maximum:
```
The integrand t^n * e^(-t) is maximized at t = n.
```

At t = n, the integrand equals n^n * e^(-n).

**Step 3**: Expand the log of the integrand around t = n:
```
log(t^n * e^(-t)) = n*log(t) - t
```

Let t = n + x*sqrt(n):
```
n*log(n + x*sqrt(n)) - (n + x*sqrt(n))
= n*log(n) + n*log(1 + x/sqrt(n)) - n - x*sqrt(n)
= n*log(n) + n*(x/sqrt(n) - x^2/(2n) + ...) - n - x*sqrt(n)
= n*log(n) - n + x*sqrt(n) - x^2/2 + ... - x*sqrt(n)
= n*log(n) - n - x^2/2 + O(x^3/sqrt(n))
```

**Step 4**: The integral becomes approximately:
```
n! ~ n^n * e^(-n) * sqrt(n) * integral from -inf to inf of e^(-x^2/2) dx
   = n^n * e^(-n) * sqrt(n) * sqrt(2*pi)
   = sqrt(2*pi*n) * (n/e)^n
```

### Logarithmic Form

Taking natural logarithm:
```
ln(n!) = n*ln(n) - n + 0.5*ln(2*pi*n) + O(1/n)
```

Or in base 2:
```
log2(n!) = n*log2(n) - n*log2(e) + O(log(n))
         ~ n*log2(n) - 1.4427*n
```

This result is central to information theory and complexity theory.

### Bounds (Without Stirling)

Simple bounds can be proved without Stirling:

**Upper bound**: n! <= n^n (each of n factors is at most n)

**Lower bound**: n! >= (n/e)^n

Proof: Taking ln of both sides:
```
ln(n!) = sum from k=1 to n of ln(k) >= integral from 1 to n of ln(x) dx
       = n*ln(n) - n + 1 >= n*ln(n) - n = ln((n/e)^n)
```

**Tighter lower bound**: n! >= sqrt(2*pi*n) * (n/e)^n (follows from the full Stirling
derivation showing the approximation is an underestimate).

---

## Factorial in Complexity Theory

### Decision Problems vs Counting Problems

While most of complexity theory focuses on **decision problems** (yes/no answers),
factorial complexity often arises in **counting problems**: how many solutions exist?

- **Decision**: Is there a Hamiltonian cycle? (NP-complete)
- **Counting**: How many Hamiltonian cycles exist? (#P-complete)

The search space of n! permutations is the natural domain for problems involving
arrangements.

### The Complexity Class #P

**#P** (sharp-P) is the class of counting problems associated with NP decision problems.
Formally, #P is the class of functions f: {0,1}* -> N such that there exists a
polynomial-time nondeterministic Turing machine M where f(x) equals the number of
accepting paths of M on input x.

Key facts:
- Every problem in #P can be solved in time O(2^{p(n)}) for some polynomial p.
- Many #P problems have counting domains of size n! (permutations).
- #P-hard problems are at least as hard as any problem in #P.

### Toda's Theorem

**Toda's theorem** (1991) states:
```
PH ⊆ P^(#P)
```

The entire polynomial hierarchy is contained in P with a #P oracle. This means that
counting problems (many of which involve factorial-sized search spaces) are
extraordinarily powerful -- more powerful than any level of the polynomial hierarchy.

---

## Counting Argument Lower Bounds

### Comparison-Based Sorting Lower Bound

The most famous counting argument: any comparison-based sorting algorithm requires
at least Omega(n log n) comparisons in the worst case.

**Proof**:
1. There are n! possible permutations of n distinct elements.
2. A comparison-based algorithm constructs a binary decision tree.
3. The decision tree must have at least n! leaves (one for each permutation).
4. A binary tree with L leaves has depth at least log2(L).
5. Therefore, depth >= log2(n!) = Theta(n log n) by Stirling.

This is a **tight** lower bound since merge sort achieves O(n log n).

### Information-Theoretic Lower Bound for Search

To identify which of n! permutations is the input, we need at least log2(n!) bits of
information. Each comparison provides at most 1 bit. Therefore:

```
Minimum comparisons >= log2(n!) ~ n*log2(n) - 1.4427*n
```

### Lower Bound for Selection Networks

A selection network that finds the median of n elements must have at least
Omega(n log n) comparators, again by the factorial counting argument applied to
the set of inputs that could produce each output.

---

## The Permanent: #P-Completeness

### Definition

The **permanent** of an n x n matrix A is:
```
perm(A) = sum over all permutations sigma of product from i=1 to n of A[i][sigma(i)]
```

This looks like the determinant formula, but without the sign alternation:
```
det(A)  = sum over sigma of (-1)^{inversions(sigma)} * product A[i][sigma(i)]
perm(A) = sum over sigma of product A[i][sigma(i)]
```

### Complexity

- **Determinant** can be computed in O(n^3) via Gaussian elimination.
- **Permanent** is **#P-complete** (Valiant, 1979).

This is remarkable: removing the signs from the determinant formula transforms the
problem from polynomial to #P-complete.

### Brute-Force Computation

The naive computation sums over all n! permutations:

```python
from itertools import permutations


def permanent_brute(matrix):
    """Compute permanent by summing over all n! permutations. O(n! * n)."""
    n = len(matrix)
    total = 0
    for perm in permutations(range(n)):
        product = 1
        for i in range(n):
            product *= matrix[i][perm[i]]
        total += product
    return total
```

### Ryser's Formula

Ryser's formula computes the permanent in O(2^n * n) time -- exponential but vastly
better than O(n! * n):

```python
def permanent_ryser(matrix):
    """Compute permanent using Ryser's formula. O(2^n * n)."""
    n = len(matrix)
    total = 0
    for subset in range(1, 1 << n):
        row_sums_product = 1
        sign = (-1) ** (n - bin(subset).count("1"))
        col_sums = [0] * n
        for j in range(n):
            if subset & (1 << j):
                for i in range(n):
                    col_sums[i] += matrix[i][j]
        product = 1
        for i in range(n):
            product *= col_sums[i]
        total += sign * product
    return total * ((-1) ** n)
```

The jump from O(n!) to O(2^n) is significant:
- n=20: n! ~ 2.4 x 10^18, but 2^20 * 20 ~ 2 x 10^7 (feasible).
- n=30: n! ~ 2.65 x 10^32, but 2^30 * 30 ~ 3.2 x 10^10 (still feasible).

### Valiant's Proof (Sketch)

Valiant showed #P-completeness of the permanent by reducing #SAT (counting satisfying
assignments of Boolean formulas) to computing the permanent of a {0,1}-matrix. The
reduction constructs a matrix where each permutation with non-zero contribution
corresponds to a satisfying assignment.

---

## Relation to PSPACE

### PSPACE and Factorial

**PSPACE** is the class of problems solvable using polynomial space (but potentially
exponential time).

Key relationships:
```
P ⊆ NP ⊆ #P ⊆ PSPACE ⊆ EXPTIME
```

Note: #P here refers to the decision version (is the count > k?).

Any problem solvable by enumerating all n! permutations and using O(n) space per
permutation (reusing space) is in PSPACE, since the space required is only O(n) to
store the current permutation and any running totals.

### PSPACE-Complete Problems

Some games and puzzles are PSPACE-complete:
- Generalized geography
- Quantified Boolean Formula (QBF) satisfiability
- Many two-player games (generalized chess, Go on n x n boards)

These problems can require exploring game trees of factorial (or worse) size, but the
key insight is that they can be solved with polynomial space via depth-first search with
backtracking.

### Savitch's Theorem

**Savitch's theorem**: NSPACE(f(n)) ⊆ DSPACE(f(n)^2) for f(n) >= log(n).

This means nondeterministic polynomial space equals deterministic polynomial space:
NPSPACE = PSPACE. Even if a nondeterministic machine can "guess" the right permutation,
the deterministic simulation only needs polynomially more space (though it may use
exponentially more time).

---

## Gamma Function and Continuous Factorial

### Definition

The **Gamma function** extends factorial to real and complex numbers:
```
Gamma(z) = integral from 0 to infinity of t^(z-1) * e^(-t) dt
```

For positive integers: Gamma(n) = (n-1)!

### Key Properties

```
Gamma(z+1) = z * Gamma(z)           (functional equation)
Gamma(1/2) = sqrt(pi)               (famous value)
Gamma(n + 1/2) = (2n)! * sqrt(pi) / (4^n * n!)  (half-integer values)
```

### Applications in Algorithm Analysis

The Gamma function appears when analyzing:

- **Expected running time** of randomized algorithms involving random permutations.
- **Average-case analysis** where the input is a uniformly random permutation.
- **Moment calculations** for permutation statistics (inversions, cycles, etc.).

---

## Subexponential Algorithms for Factorial-Hard Problems

### Held-Karp Algorithm for TSP

The dynamic programming approach reduces TSP from O(n!) to O(2^n * n^2):

```
dp[S][j] = minimum cost to visit all cities in set S, ending at city j
```

Recurrence:
```
dp[S][j] = min over i in S\{j} of (dp[S\{j}][i] + dist[i][j])
```

This uses the principle that we only need to remember **which cities were visited**
(a subset), not **in what order** (a permutation).

### Color-Coding Technique

For finding paths of length k in a graph, the naive approach tries all k! orderings of
k vertices. The **color-coding** technique (Alon, Yuster, Zwick, 1995) reduces this to
O(2^k * m) expected time by randomly coloring vertices with k colors and finding a
colorful path.

### Inclusion-Exclusion Principle

Many counting problems over permutations can be solved using inclusion-exclusion in
O(2^n) time rather than O(n!) time. For example, counting permutations avoiding certain
positions (the probleme des menages) or computing the permanent.

---

## Information-Theoretic Implications

### Encoding Permutations

A permutation of n elements carries exactly log2(n!) bits of information.

By Stirling: log2(n!) ~ n*log2(n) - 1.4427*n

Therefore, any encoding of permutations requires at least ~n*log2(n) bits on average.

The **Lehmer code** provides an optimal encoding:
```
Lehmer code of permutation: for each position i, count how many elements to the right
of position i are smaller than the element at position i.
```

This gives a mixed-radix number with digits in {0, 1, ..., n-1}, {0, 1, ..., n-2},
..., {0}, requiring exactly ceil(log2(n!)) bits.

### Communication Complexity

In the problem of communicating a permutation between two parties, the minimum number
of bits that must be exchanged is log2(n!) = Theta(n log n).

---

## Open Problems

1. **Permanent approximation**: Can the permanent of a non-negative matrix be
   approximated to within a constant factor in polynomial time? (Yes for 0/1 matrices
   -- Jerrum, Sinclair, Vigoda 2004 gave an FPRAS, but the general case remains active.)

2. **TSP gap**: The best known approximation ratio for general metric TSP is 3/2
   (Christofides, 1976). Can this be improved? (Karlin, Klein, Gharan 2021 achieved
   3/2 - epsilon for some small epsilon.)

3. **#P vs NP**: Is computing the number of solutions strictly harder than deciding
   if a solution exists? (Believed yes, but unproven.)

4. **Factorial vs exponential separation**: For which NP-hard problems can we provably
   not reduce from O(n!) to O(c^n) for some constant c?

---

## Key Takeaways

1. **Stirling's approximation** is derived via the Laplace method on the Gamma function
   integral, giving n! ~ sqrt(2*pi*n) * (n/e)^n with excellent accuracy.

2. **The permanent** is the canonical #P-complete problem, requiring O(n!) brute force
   but reducible to O(2^n * n) via Ryser's formula.

3. **Counting arguments** using log2(n!) = Theta(n log n) yield fundamental lower
   bounds, including the Omega(n log n) sorting lower bound.

4. **Toda's theorem** shows #P captures the power of the entire polynomial hierarchy,
   demonstrating the theoretical hardness of counting over factorial-sized spaces.

5. **PSPACE contains** all problems solvable by exhaustive permutation search with
   polynomial space reuse (backtracking).

6. **Subexponential techniques** (dynamic programming over subsets, inclusion-exclusion,
   color-coding) can often reduce O(n!) to O(2^n * poly(n)).

7. **The Gamma function** extends factorial to continuous domains, enabling average-case
   analysis of permutation algorithms.
