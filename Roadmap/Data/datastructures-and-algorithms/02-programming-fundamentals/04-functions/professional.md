# Functions — Professional Level (Mathematical Foundations)

## Table of Contents

1. [Introduction](#introduction)
2. [Functions in Mathematics vs Programming](#functions-in-mathematics-vs-programming)
3. [Lambda Calculus Basics](#lambda-calculus-basics)
4. [Fixed-Point Combinators](#fixed-point-combinators)
5. [Tail Call Optimization and Stack Frames](#tail-call-optimization-and-stack-frames)
6. [Curry-Howard Correspondence](#curry-howard-correspondence)
7. [Function Complexity Analysis](#function-complexity-analysis)
8. [Formal Proofs of Recursive Function Correctness](#formal-proofs-of-recursive-function-correctness)
9. [Summary](#summary)

---

## Introduction

> Focus: "What is the mathematical theory behind functions?" and "How does theory inform practice?"

At the professional level, we examine the theoretical foundations that underpin every function you write. Lambda calculus explains what computation itself means. Tail call optimization connects recursion theory to real stack frames. The Curry-Howard correspondence reveals that types are theorems and programs are proofs.

---

## Functions in Mathematics vs Programming

### Mathematical Functions

In mathematics, a function `f: A → B` is a **mapping** from every element in set `A` (domain) to exactly one element in set `B` (codomain).

Properties of mathematical functions:
- **Total**: defined for every input in the domain
- **Deterministic**: same input always gives the same output
- **No side effects**: a function IS a mapping, not a process
- **Referential transparency**: `f(x)` can always be replaced with its result

```
f(x) = x² + 1

f(3) = 10     — always, everywhere, forever
f(3) = 10     — calling it twice doesn't change anything
```

### Programming Functions

Programming functions **violate** many mathematical properties:

| Property | Math Function | Programming Function |
|----------|--------------|---------------------|
| Total | Always defined for all inputs | May crash, throw, infinite loop |
| Deterministic | Same input → same output | May depend on time, state, I/O |
| Side-effect free | Yes | May print, write files, modify globals |
| Terminates | Not applicable (instantaneous) | May not terminate |

### Bridging the Gap: Pure Functions

```python
# This IS a mathematical function (pure)
def square(x):
    return x * x

# This is NOT a mathematical function (impure)
call_count = 0
def impure_square(x):
    global call_count
    call_count += 1         # side effect
    print(f"Call #{call_count}")  # side effect
    return x * x

# The result is the same, but impure_square has observable effects
# beyond its return value
```

### Partial vs Total Functions

A **total function** is defined for all inputs in its domain. A **partial function** is defined for only some inputs.

```go
// Partial function — undefined for b == 0
func divide(a, b float64) float64 {
    return a / b  // panic if b == 0
}

// Made total by expanding the codomain
func divideSafe(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}
```

```java
// Partial → Total using Optional
public static Optional<Double> divide(double a, double b) {
    if (b == 0) return Optional.empty();
    return Optional.of(a / b);
}
```

---

## Lambda Calculus Basics

**Lambda calculus** (Alonzo Church, 1930s) is the theoretical foundation of functional programming. It defines computation using only three constructs:

### The Three Constructs

1. **Variable**: `x`
2. **Abstraction** (function definition): `λx.M` — a function with parameter `x` and body `M`
3. **Application** (function call): `(M N)` — apply function `M` to argument `N`

That's it. No numbers, no loops, no data types — just these three. Everything else is built from them.

### Notation

```
λx.x           — identity function (takes x, returns x)
λx.λy.x        — returns first argument (Church encoding of "true")
λx.λy.y        — returns second argument (Church encoding of "false")
(λx.x+1) 5    — apply: substitute x=5, get 5+1=6
```

### Beta Reduction

**Beta reduction** is the process of applying a function to an argument by substituting:

```
(λx.x+1) 5
→ 5+1           — substitute x with 5
→ 6
```

More complex example:

```
(λf.λx.f(f(x))) (λy.y+1) 3
→ (λx.(λy.y+1)((λy.y+1)(x))) 3    — substitute f
→ (λy.y+1)((λy.y+1)(3))            — substitute x
→ (λy.y+1)(3+1)                     — inner application
→ (λy.y+1)(4)                       — reduce
→ 4+1                                — apply
→ 5
```

### Church Encodings

Numbers, booleans, and data structures can all be encoded as pure lambda functions:

#### Church Numerals

```
0 = λf.λx.x             — apply f zero times
1 = λf.λx.f(x)          — apply f once
2 = λf.λx.f(f(x))       — apply f twice
3 = λf.λx.f(f(f(x)))    — apply f three times
```

Implementation in code:

```python
# Church numerals in Python
ZERO  = lambda f: lambda x: x
ONE   = lambda f: lambda x: f(x)
TWO   = lambda f: lambda x: f(f(x))
THREE = lambda f: lambda x: f(f(f(x)))

# SUCC: add 1 to a Church numeral
SUCC = lambda n: lambda f: lambda x: f(n(f)(x))

# ADD: add two Church numerals
ADD = lambda m: lambda n: lambda f: lambda x: m(f)(n(f)(x))

# MULT: multiply two Church numerals
MULT = lambda m: lambda n: lambda f: m(n(f))

# Convert Church numeral to Python int
to_int = lambda n: n(lambda x: x + 1)(0)

print(to_int(ZERO))               # 0
print(to_int(THREE))              # 3
print(to_int(ADD(TWO)(THREE)))    # 5
print(to_int(MULT(TWO)(THREE)))   # 6
print(to_int(SUCC(THREE)))        # 4
```

#### Church Booleans

```python
TRUE  = lambda a: lambda b: a     # select first
FALSE = lambda a: lambda b: b     # select second

# IF-THEN-ELSE is just application
IF = lambda cond: lambda then_: lambda else_: cond(then_)(else_)

# Boolean operations
AND = lambda p: lambda q: p(q)(p)
OR  = lambda p: lambda q: p(p)(q)
NOT = lambda p: lambda a: lambda b: p(b)(a)

# Test
print(IF(TRUE)("yes")("no"))   # "yes"
print(IF(FALSE)("yes")("no"))  # "no"
print(IF(AND(TRUE)(FALSE))("yes")("no"))  # "no"
```

### Currying

**Currying** transforms a function of multiple arguments into a chain of functions each taking one argument.

```
f(x, y) = x + y          — uncurried (takes pair)
f = λx.λy.x + y          — curried (takes one at a time)
f(3) = λy.3 + y          — partial application
f(3)(4) = 7              — full application
```

```go
// Go: curried add
func add(a int) func(int) int {
    return func(b int) int {
        return a + b
    }
}

add3 := add(3)
fmt.Println(add3(4))  // 7
fmt.Println(add3(10)) // 13
```

```java
// Java: curried add
Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;

Function<Integer, Integer> add3 = add.apply(3);
System.out.println(add3.apply(4));  // 7
System.out.println(add3.apply(10)); // 13
```

```python
# Python: curried add
def add(a):
    def inner(b):
        return a + b
    return inner

add3 = add(3)
print(add3(4))   # 7
print(add3(10))  # 13

# Generic curry function
def curry(fn):
    import inspect
    arity = len(inspect.signature(fn).parameters)
    def curried(*args):
        if len(args) >= arity:
            return fn(*args)
        return lambda *more: curried(*args, *more)
    return curried

@curry
def multiply(a, b, c):
    return a * b * c

print(multiply(2)(3)(4))  # 24
print(multiply(2, 3)(4))  # 24
```

---

## Fixed-Point Combinators

### The Y Combinator

A **fixed-point combinator** finds a value `x` such that `f(x) = x`. The **Y combinator** enables recursion in a language that has no built-in recursion — it is defined as:

```
Y = λf.(λx.f(x x))(λx.f(x x))
```

When applied to a function `g`:

```
Y(g) = g(Y(g)) = g(g(Y(g))) = g(g(g(...)))
```

This creates the "infinite unfolding" needed for recursion.

### Why It Matters

In pure lambda calculus, there are no named functions, so a function cannot refer to itself by name. The Y combinator solves this by passing the function to itself.

### Implementation (Z Combinator for strict languages)

In strict (eager) evaluation languages (Go, Java, Python), we use the **Z combinator** instead (wraps in a thunk to prevent infinite expansion):

```
Z = λf.(λx.f(λv.x(x)(v)))(λx.f(λv.x(x)(v)))
```

#### Python

```python
# Z combinator (strict/eager evaluation version of Y)
Z = lambda f: (lambda x: f(lambda v: x(x)(v)))(lambda x: f(lambda v: x(x)(v)))

# Factorial without self-reference
factorial = Z(lambda self: lambda n: 1 if n <= 1 else n * self(n - 1))
print(factorial(5))   # 120
print(factorial(10))  # 3628800

# Fibonacci without self-reference
fibonacci = Z(lambda self: lambda n: n if n <= 1 else self(n-1) + self(n-2))
print(fibonacci(10))  # 55
```

#### Go

```go
// Z combinator in Go (using interface{} for type flexibility)
type RecFunc func(int) int
type SelfFunc func(SelfFunc) RecFunc

func Z(f func(RecFunc) RecFunc) RecFunc {
    g := func(self SelfFunc) RecFunc {
        return f(func(n int) int {
            return self(self)(n)
        })
    }
    return g(g)
}

// Factorial via Z combinator
factorial := Z(func(self RecFunc) RecFunc {
    return func(n int) int {
        if n <= 1 { return 1 }
        return n * self(n-1)
    }
})

fmt.Println(factorial(5))  // 120
```

#### Java

```java
// Z combinator in Java
@FunctionalInterface
interface RecFunc<T, R> {
    R apply(T t);
}

@FunctionalInterface
interface SelfFunc<T, R> {
    RecFunc<T, R> apply(SelfFunc<T, R> self);
}

static <T, R> RecFunc<T, R> Z(Function<RecFunc<T, R>, RecFunc<T, R>> f) {
    SelfFunc<T, R> g = self -> f.apply(t -> self.apply(self).apply(t));
    return g.apply(g);
}

// Usage
RecFunc<Integer, Integer> factorial = Z(self ->
    n -> n <= 1 ? 1 : n * self.apply(n - 1)
);

System.out.println(factorial.apply(5));  // 120
```

---

## Tail Call Optimization and Stack Frames

### How Function Calls Use the Stack

Every function call creates a **stack frame** containing:
- Return address (where to go back)
- Parameters
- Local variables
- Saved registers

```
factorial(4)
├── factorial(3)          ← stack frame 3
│   ├── factorial(2)      ← stack frame 2
│   │   ├── factorial(1)  ← stack frame 1
│   │   │   └── return 1
│   │   └── return 2 * 1 = 2
│   └── return 3 * 2 = 6
└── return 4 * 6 = 24

Stack depth: O(n) — each call waits for the next to return
```

### Tail Position

A function call is in **tail position** if it is the **very last operation** before the function returns. Nothing else happens after the recursive call.

```python
# NOT tail-recursive — multiplication happens AFTER the recursive call
def factorial(n):
    if n <= 1: return 1
    return n * factorial(n - 1)   # n * ... happens after factorial returns

# Tail-recursive — recursive call IS the last operation
def factorial_tail(n, acc=1):
    if n <= 1: return acc
    return factorial_tail(n - 1, n * acc)  # nothing happens after this call
```

### Tail Call Optimization (TCO)

If a recursive call is in tail position, the compiler can **reuse the current stack frame** instead of creating a new one. This converts recursion into iteration internally.

```
Without TCO:                    With TCO:
factorial_tail(4, 1)           factorial_tail(4, 1)
  factorial_tail(3, 4)         → reuse frame: (3, 4)
    factorial_tail(2, 12)      → reuse frame: (2, 12)
      factorial_tail(1, 24)    → reuse frame: (1, 24)
        return 24              → return 24

Stack depth: O(n)              Stack depth: O(1)
```

### TCO Support by Language

| Language | TCO Support |
|----------|------------|
| **Go** | No — the Go team deliberately chose not to implement TCO |
| **Java** | No — JVM does not support it (Project Loom does not add it) |
| **Python** | No — Guido van Rossum explicitly rejected it (breaks stack traces) |
| Scheme/Racket | Yes — required by spec |
| Haskell | Yes — via lazy evaluation |
| Scala | Yes — `@tailrec` annotation |
| Kotlin | Yes — `tailrec` keyword |

### Manual TCO: Converting Tail Recursion to Iteration

Since Go, Java, and Python don't support TCO, convert manually:

#### Go

```go
// Tail recursive (still uses O(n) stack in Go)
func factorialTail(n, acc int) int {
    if n <= 1 { return acc }
    return factorialTail(n-1, n*acc)
}

// Manually converted to iteration (O(1) stack)
func factorialIter(n int) int {
    acc := 1
    for n > 1 {
        acc *= n
        n--
    }
    return acc
}
```

#### Java

```java
// Tail recursive (still uses O(n) stack in Java)
public static long factorialTail(int n, long acc) {
    if (n <= 1) return acc;
    return factorialTail(n - 1, n * acc);
}

// Manually converted to iteration
public static long factorialIter(int n) {
    long acc = 1;
    while (n > 1) {
        acc *= n;
        n--;
    }
    return acc;
}
```

#### Python

```python
# Tail recursive (still uses O(n) stack, and Python has a recursion limit!)
def factorial_tail(n, acc=1):
    if n <= 1: return acc
    return factorial_tail(n - 1, n * acc)

# factorial_tail(10000)  → RecursionError!

# Manually converted to iteration
def factorial_iter(n):
    acc = 1
    while n > 1:
        acc *= n
        n -= 1
    return acc

# factorial_iter(10000)  → works fine

# Trampoline: generic TCO simulation
def trampoline(fn, *args):
    result = fn(*args)
    while callable(result):
        result = result()
    return result

def factorial_trampoline(n, acc=1):
    if n <= 1:
        return acc
    return lambda: factorial_trampoline(n - 1, n * acc)

print(trampoline(factorial_trampoline, 10000))  # works — no stack overflow
```

---

## Curry-Howard Correspondence

The **Curry-Howard correspondence** (also called "proofs as programs") reveals a deep connection between:

| Logic | Programming |
|-------|------------|
| Proposition | Type |
| Proof | Program (value of that type) |
| Implication (A → B) | Function type (A → B) |
| Conjunction (A ∧ B) | Product type (tuple, struct) |
| Disjunction (A ∨ B) | Sum type (union, Either) |
| True | Unit type (void/empty struct) |
| False | Empty type (no values, Void) |
| Universal (∀x.P(x)) | Generic/parametric polymorphism |

### What This Means

If you can write a function with type `A → B`, you have constructively **proved** that "A implies B." The function IS the proof.

```
-- Type: (A, A → B) → B
-- Logic: "If A is true, and A implies B, then B is true" (modus ponens)
-- Proof: apply the function

def modus_ponens(a, f):
    return f(a)
```

### Practical Implications

1. **If a type is inhabited (has values), the corresponding proposition is true**
2. **Impossible operations have uninhabitable types**: you can't write a total function `Void → A` because there are no `Void` values to pass
3. **Type-safe programs are correct by construction**: if it compiles, the logical statement holds

```java
// This type signature IS a proof that:
// "For all types A and B, if I have (A → B) and A, I can produce B"
static <A, B> B apply(Function<A, B> f, A a) {
    return f.apply(a);
}

// This type signature IS a proof that:
// "For all types A, B, C, if I have (A → B) and (B → C), I can get (A → C)"
static <A, B, C> Function<A, C> compose(Function<A, B> f, Function<B, C> g) {
    return a -> g.apply(f.apply(a));
}
```

---

## Function Complexity Analysis

### Recurrence Relations

Recursive functions have their time complexity described by **recurrence relations**.

#### Example 1: Linear Recursion

```python
def factorial(n):         # T(n) = T(n-1) + O(1)
    if n <= 1: return 1   # T(1) = O(1)
    return n * factorial(n - 1)
```

```
T(n) = T(n-1) + c
T(n-1) = T(n-2) + c
...
T(1) = c

T(n) = n * c = O(n)
```

#### Example 2: Binary Recursion (naive Fibonacci)

```python
def fib(n):               # T(n) = T(n-1) + T(n-2) + O(1)
    if n <= 1: return n   # T(0) = T(1) = O(1)
    return fib(n-1) + fib(n-2)
```

```
T(n) = T(n-1) + T(n-2) + c

Lower bound: T(n) ≥ 2 * T(n-2) → T(n) = Ω(2^(n/2))
Upper bound: T(n) ≤ 2 * T(n-1) → T(n) = O(2^n)

Exact: T(n) = O(φ^n) where φ = (1+√5)/2 ≈ 1.618
```

#### Example 3: Divide and Conquer (Merge Sort)

```python
def merge_sort(arr):
    if len(arr) <= 1: return arr       # T(1) = O(1)
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])       # T(n/2)
    right = merge_sort(arr[mid:])      # T(n/2)
    return merge(left, right)          # O(n)
```

```
T(n) = 2T(n/2) + O(n)
```

### The Master Theorem

For recurrences of the form `T(n) = aT(n/b) + O(n^d)`:

| Case | Condition | Result |
|------|-----------|--------|
| 1 | d < log_b(a) | T(n) = O(n^(log_b(a))) |
| 2 | d = log_b(a) | T(n) = O(n^d * log n) |
| 3 | d > log_b(a) | T(n) = O(n^d) |

**Merge sort**: a=2, b=2, d=1 → log_2(2) = 1 = d → **Case 2** → T(n) = O(n log n)

**Binary search**: a=1, b=2, d=0 → log_2(1) = 0 = d → **Case 2** → T(n) = O(log n)

### Space Complexity of Recursive Functions

Space = max depth of recursion * space per frame.

| Function | Max Depth | Space per Frame | Total Space |
|----------|----------|----------------|-------------|
| factorial(n) | n | O(1) | O(n) |
| fib(n) naive | n | O(1) | O(n) |
| merge_sort(n) | log n | O(n) at merge | O(n log n) or O(n) with in-place merge |
| binary_search(n) | log n | O(1) | O(log n) |
| quicksort(n) worst | n | O(1) | O(n) |
| quicksort(n) avg | log n | O(1) | O(log n) |

---

## Formal Proofs of Recursive Function Correctness

### Structural Induction

To prove a recursive function is correct, use **structural induction**:

1. **Base case**: Prove the function returns the correct result for the smallest input
2. **Inductive step**: Assume correctness for all inputs smaller than `n` (inductive hypothesis), prove correctness for input `n`

### Example: Proving Factorial Correctness

**Claim**: `factorial(n) = n!` for all n >= 0.

```python
def factorial(n):
    if n <= 0: return 1        # Base case: 0! = 1
    return n * factorial(n-1)  # Recursive case
```

**Proof by induction on n:**

**Base case** (n = 0):
- `factorial(0)` returns `1`
- `0! = 1` by definition
- Therefore `factorial(0) = 0!`

**Inductive step** (assume `factorial(k) = k!` for all `k < n`):
- `factorial(n)` returns `n * factorial(n-1)`
- By inductive hypothesis, `factorial(n-1) = (n-1)!`
- Therefore `factorial(n) = n * (n-1)! = n!`

**QED.**

### Example: Proving Binary Search Correctness

**Claim**: `binary_search(arr, target, lo, hi)` returns the index of `target` in sorted array `arr[lo..hi]`, or -1 if not found.

**Loop invariant**: If `target` exists in `arr`, then `lo <= index(target) <= hi`.

**Proof:**

**Base case** (lo > hi): Target is not in the empty range → return -1. Correct.

**Inductive step** (lo <= hi):
- Let `mid = lo + (hi - lo) / 2`
- **Case 1**: `arr[mid] == target` → return `mid`. Correct.
- **Case 2**: `arr[mid] < target` → target must be in `arr[mid+1..hi]` (because arr is sorted). We recurse on `(arr, target, mid+1, hi)`. The range is strictly smaller, and the invariant holds.
- **Case 3**: `arr[mid] > target` → target must be in `arr[lo..mid-1]`. We recurse on `(arr, target, lo, mid-1)`. The range is strictly smaller, and the invariant holds.

**Termination**: The range `[lo, hi]` shrinks by at least 1 element each step (mid is excluded). Since the range is finite and decreasing, the function terminates.

**QED.**

### Termination Proofs

To prove a recursive function terminates, find a **well-founded measure** that strictly decreases with each call:

| Function | Measure | Decreases Because |
|----------|---------|------------------|
| `factorial(n)` | n | n-1 < n |
| `binary_search(lo, hi)` | hi - lo | Range shrinks each step |
| `gcd(a, b)` (Euclidean) | b | a mod b < b |
| `merge_sort(arr)` | len(arr) | len(arr)/2 < len(arr) |

If the measure is a natural number (non-negative integer) that strictly decreases, termination is guaranteed because you can't decrease below 0 forever.

---

## Summary

- **Mathematical functions** are total, deterministic, and side-effect free; programming functions often are not
- **Lambda calculus** defines computation with just three constructs: variable, abstraction, application
- **Church encodings** represent numbers and booleans as pure functions
- **Currying** transforms multi-argument functions into chains of single-argument functions
- **Y/Z combinators** enable recursion without self-reference
- **Tail call optimization** reuses stack frames for tail-recursive calls — not supported in Go, Java, or Python
- **Curry-Howard correspondence**: types are propositions, programs are proofs
- **Master theorem** gives Big-O for divide-and-conquer recurrences
- **Structural induction** proves recursive function correctness; well-founded measures prove termination
