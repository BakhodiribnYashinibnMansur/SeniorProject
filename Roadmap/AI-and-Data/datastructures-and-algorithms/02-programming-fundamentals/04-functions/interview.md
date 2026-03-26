# Functions — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is a function? Why do we use them? | Named reusable block of code; DRY, abstraction, testability |
| 2 | What is the difference between a parameter and an argument? | Parameter = variable in definition; argument = actual value at call site |
| 3 | What does "pass by value" vs "pass by reference" mean? | Value = copy; reference = pointer to original data |
| 4 | What is scope? Explain local vs global scope. | Region where a variable is accessible; local = inside function; global = everywhere |
| 5 | What happens if a function has no `return` statement? | Go: zero values; Java (void): nothing; Python: returns `None` |
| 6 | What is a variadic function? Give examples. | Accepts variable number of args; Go: `...int`; Java: `int...`; Python: `*args` |
| 7 | What is the Python mutable default argument bug? | `def f(lst=[])` — list shared across calls; fix: use `None` |
| 8 | How does Go handle multiple return values? | `func f() (int, error)` — natively returns multiple values |

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is a closure? Give an example. | Function that captures variables from enclosing scope; counter example |
| 2 | What are higher-order functions? Name three. | Functions that take/return functions; map, filter, reduce |
| 3 | Explain the loop variable closure bug. | All closures share same loop variable; fix: capture in local var or default param |
| 4 | When should you use recursion vs iteration? | Recursion: trees, divide-conquer; iteration: simple loops, performance-critical |
| 5 | What is function composition? | Combining functions: `compose(f, g)(x) = f(g(x))` |
| 6 | What is a callback function? | Function passed as argument, called later; event handling, async patterns |
| 7 | What is the difference between `map` and `flatMap`? | `map` transforms each element; `flatMap` transforms and flattens nested structures |

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | How would you implement the middleware pattern? | Chain of functions wrapping a handler; each adds behavior (logging, auth) |
| 2 | What is memoization? When would you NOT use it? | Cache function results; don't use for impure functions, huge domains, or low-frequency calls |
| 3 | What is dependency injection through functions? | Pass dependencies as function parameters instead of hardcoding; improves testability |
| 4 | How do you handle errors in a function pipeline? | Result/Either types, Go error chaining, or try/catch with composition |
| 5 | Explain the difference between pure and impure functions. | Pure: deterministic, no side effects; impure: I/O, mutation, non-determinism |
| 6 | How would you design a retry function with exponential backoff? | Accept function + config, loop with increasing delay, return after success or max attempts |

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is lambda calculus? What are its three constructs? | Variable, abstraction (λx.M), application (M N); foundation of computation |
| 2 | What is the Y combinator? Why does it matter? | Fixed-point combinator; enables recursion without self-reference in pure lambda calculus |
| 3 | What is tail call optimization? Which languages support it? | Reuses stack frame for tail calls; Scheme yes, Go/Java/Python no |
| 4 | Explain the Curry-Howard correspondence. | Types = propositions, programs = proofs, function type = implication |
| 5 | How do you prove a recursive function terminates? | Find a well-founded measure (natural number) that strictly decreases each call |
| 6 | Use the Master Theorem to analyze merge sort. | T(n) = 2T(n/2) + O(n); a=2, b=2, d=1; Case 2 → O(n log n) |

---

## Coding Challenge 1: Implement Memoize

> Write a generic memoize function that caches results of any single-argument function.

### Go

```go
package main

import "fmt"

func memoize(fn func(int) int) func(int) int {
    cache := make(map[int]int)
    return func(n int) int {
        if val, ok := cache[n]; ok {
            return val
        }
        result := fn(n)
        cache[n] = result
        return result
    }
}

func main() {
    callCount := 0

    // Expensive function
    expensiveSquare := func(n int) int {
        callCount++
        fmt.Printf("  Computing square(%d)...\n", n)
        return n * n
    }

    memoSquare := memoize(expensiveSquare)

    fmt.Println("First calls:")
    fmt.Println(memoSquare(5))  // Computing square(5)... → 25
    fmt.Println(memoSquare(3))  // Computing square(3)... → 9
    fmt.Println(memoSquare(5))  // (cached) → 25
    fmt.Println(memoSquare(3))  // (cached) → 9

    fmt.Printf("Total computations: %d (should be 2)\n", callCount)
}
```

### Java

```java
import java.util.*;
import java.util.function.Function;

public class Memoize {
    public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
        Map<T, R> cache = new HashMap<>();
        return input -> {
            if (cache.containsKey(input)) {
                return cache.get(input);
            }
            R result = fn.apply(input);
            cache.put(input, result);
            return result;
        };
    }

    public static void main(String[] args) {
        int[] callCount = {0};

        Function<Integer, Integer> expensiveSquare = n -> {
            callCount[0]++;
            System.out.println("  Computing square(" + n + ")...");
            return n * n;
        };

        Function<Integer, Integer> memoSquare = memoize(expensiveSquare);

        System.out.println("First calls:");
        System.out.println(memoSquare.apply(5));  // Computing... → 25
        System.out.println(memoSquare.apply(3));  // Computing... → 9
        System.out.println(memoSquare.apply(5));  // cached → 25
        System.out.println(memoSquare.apply(3));  // cached → 9

        System.out.println("Total computations: " + callCount[0] + " (should be 2)");
    }
}
```

### Python

```python
def memoize(fn):
    cache = {}
    def wrapper(*args):
        if args in cache:
            return cache[args]
        result = fn(*args)
        cache[args] = result
        return result
    wrapper.cache = cache
    return wrapper

# Test
call_count = 0

@memoize
def expensive_square(n):
    global call_count
    call_count += 1
    print(f"  Computing square({n})...")
    return n * n

print("First calls:")
print(expensive_square(5))  # Computing... → 25
print(expensive_square(3))  # Computing... → 9
print(expensive_square(5))  # cached → 25
print(expensive_square(3))  # cached → 9

print(f"Total computations: {call_count} (should be 2)")
print(f"Cache contents: {expensive_square.cache}")
```

---

## Coding Challenge 2: Implement Retry with Exponential Backoff

> Write a retry function that calls a given function up to N times, with exponential backoff between failures.

### Go

```go
package main

import (
    "errors"
    "fmt"
    "math/rand"
    "time"
)

type RetryConfig struct {
    MaxAttempts int
    BaseDelay   time.Duration
    MaxDelay    time.Duration
}

func retry(fn func() error, config RetryConfig) error {
    var lastErr error
    for attempt := 1; attempt <= config.MaxAttempts; attempt++ {
        err := fn()
        if err == nil {
            fmt.Printf("  Attempt %d: success\n", attempt)
            return nil
        }
        lastErr = err
        fmt.Printf("  Attempt %d: failed (%v)\n", attempt, err)

        if attempt < config.MaxAttempts {
            delay := config.BaseDelay * time.Duration(1<<uint(attempt-1))
            if delay > config.MaxDelay {
                delay = config.MaxDelay
            }
            // Add jitter: 50-100% of delay
            jitter := time.Duration(rand.Int63n(int64(delay / 2)))
            delay = delay/2 + jitter
            fmt.Printf("  Waiting %v before retry...\n", delay)
            time.Sleep(delay)
        }
    }
    return fmt.Errorf("all %d attempts failed: %w", config.MaxAttempts, lastErr)
}

func main() {
    callCount := 0
    err := retry(func() error {
        callCount++
        if callCount < 3 {
            return errors.New("service unavailable")
        }
        return nil // succeed on 3rd attempt
    }, RetryConfig{
        MaxAttempts: 5,
        BaseDelay:   100 * time.Millisecond,
        MaxDelay:    2 * time.Second,
    })

    if err != nil {
        fmt.Println("Final error:", err)
    } else {
        fmt.Println("Operation succeeded!")
    }
}
```

### Java

```java
import java.util.Random;
import java.util.concurrent.Callable;

public class Retry {

    public static <T> T retry(Callable<T> fn, int maxAttempts,
                               long baseDelayMs, long maxDelayMs) throws Exception {
        Exception lastException = null;
        Random rand = new Random();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T result = fn.call();
                System.out.printf("  Attempt %d: success%n", attempt);
                return result;
            } catch (Exception e) {
                lastException = e;
                System.out.printf("  Attempt %d: failed (%s)%n", attempt, e.getMessage());

                if (attempt < maxAttempts) {
                    long delay = Math.min(baseDelayMs * (1L << (attempt - 1)), maxDelayMs);
                    long jitter = rand.nextLong(delay / 2);
                    delay = delay / 2 + jitter;
                    System.out.printf("  Waiting %dms before retry...%n", delay);
                    Thread.sleep(delay);
                }
            }
        }
        throw new RuntimeException("All " + maxAttempts + " attempts failed", lastException);
    }

    public static void main(String[] args) throws Exception {
        int[] callCount = {0};

        String result = retry(() -> {
            callCount[0]++;
            if (callCount[0] < 3) {
                throw new RuntimeException("service unavailable");
            }
            return "data loaded";
        }, 5, 100, 2000);

        System.out.println("Result: " + result);
    }
}
```

### Python

```python
import time
import random
import functools

def retry(fn, max_attempts=5, base_delay=0.1, max_delay=2.0):
    last_error = None
    for attempt in range(1, max_attempts + 1):
        try:
            result = fn()
            print(f"  Attempt {attempt}: success")
            return result
        except Exception as e:
            last_error = e
            print(f"  Attempt {attempt}: failed ({e})")

            if attempt < max_attempts:
                delay = min(base_delay * (2 ** (attempt - 1)), max_delay)
                jitter = random.uniform(delay * 0.5, delay)
                print(f"  Waiting {jitter:.3f}s before retry...")
                time.sleep(jitter)

    raise RuntimeError(f"All {max_attempts} attempts failed") from last_error


# Decorator version
def with_retry(max_attempts=5, base_delay=0.1, max_delay=2.0):
    def decorator(fn):
        @functools.wraps(fn)
        def wrapper(*args, **kwargs):
            return retry(
                lambda: fn(*args, **kwargs),
                max_attempts, base_delay, max_delay
            )
        return wrapper
    return decorator


# Test
call_count = 0

@with_retry(max_attempts=5, base_delay=0.1)
def flaky_service():
    global call_count
    call_count += 1
    if call_count < 3:
        raise ConnectionError("service unavailable")
    return "data loaded"

result = flaky_service()
print(f"Result: {result}")
```

---

## Coding Challenge 3: Implement Pipe/Compose

> Implement both `pipe` (left-to-right) and `compose` (right-to-left) function combinators.

### Go

```go
package main

import (
    "fmt"
    "strings"
)

// Generic pipe for int→int functions
func pipe(fns ...func(int) int) func(int) int {
    return func(x int) int {
        result := x
        for _, fn := range fns {
            result = fn(result)
        }
        return result
    }
}

// Generic compose for int→int functions
func compose(fns ...func(int) int) func(int) int {
    return func(x int) int {
        result := x
        for i := len(fns) - 1; i >= 0; i-- {
            result = fns[i](result)
        }
        return result
    }
}

// String version for real-world use
func pipeStr(fns ...func(string) string) func(string) string {
    return func(s string) string {
        result := s
        for _, fn := range fns {
            result = fn(result)
        }
        return result
    }
}

func main() {
    double := func(x int) int { return x * 2 }
    addTen := func(x int) int { return x + 10 }
    square := func(x int) int { return x * x }

    // pipe: left-to-right → double(5)=10 → addTen(10)=20 → square(20)=400
    pipeline := pipe(double, addTen, square)
    fmt.Println(pipeline(5)) // 400

    // compose: right-to-left → square(5)=25 → addTen(25)=35 → double(35)=70
    composed := compose(double, addTen, square)
    fmt.Println(composed(5)) // 70

    // String pipeline: sanitize user input
    sanitize := pipeStr(
        strings.TrimSpace,
        strings.ToLower,
        func(s string) string { return strings.ReplaceAll(s, "  ", " ") },
    )
    fmt.Println(sanitize("  Hello   World  ")) // "hello world"
}
```

### Java

```java
import java.util.Arrays;
import java.util.function.Function;

public class PipeCompose {

    @SafeVarargs
    public static <T> Function<T, T> pipe(Function<T, T>... fns) {
        return Arrays.stream(fns)
            .reduce(Function.identity(), Function::andThen);
    }

    @SafeVarargs
    public static <T> Function<T, T> compose(Function<T, T>... fns) {
        return Arrays.stream(fns)
            .reduce(Function.identity(), Function::compose);
    }

    public static void main(String[] args) {
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> addTen = x -> x + 10;
        Function<Integer, Integer> square = x -> x * x;

        // pipe: left-to-right
        Function<Integer, Integer> pipeline = pipe(doubleIt, addTen, square);
        System.out.println(pipeline.apply(5)); // 400

        // compose: right-to-left
        Function<Integer, Integer> composed = compose(doubleIt, addTen, square);
        System.out.println(composed.apply(5)); // 70

        // String pipeline
        Function<String, String> sanitize = pipe(
            String::trim,
            String::toLowerCase,
            s -> s.replaceAll("\\s+", " ")
        );
        System.out.println(sanitize.apply("  Hello   World  ")); // "hello world"
    }
}
```

### Python

```python
from functools import reduce

def pipe(*fns):
    """Left-to-right function composition: pipe(f, g)(x) = g(f(x))"""
    def piped(x):
        return reduce(lambda acc, fn: fn(acc), fns, x)
    return piped

def compose(*fns):
    """Right-to-left function composition: compose(f, g)(x) = f(g(x))"""
    def composed(x):
        return reduce(lambda acc, fn: fn(acc), reversed(fns), x)
    return composed

# Test
double = lambda x: x * 2
add_ten = lambda x: x + 10
square = lambda x: x ** 2

# pipe: left-to-right → double(5)=10 → add_ten(10)=20 → square(20)=400
pipeline = pipe(double, add_ten, square)
print(pipeline(5))  # 400

# compose: right-to-left → square(5)=25 → add_ten(25)=35 → double(35)=70
composed = compose(double, add_ten, square)
print(composed(5))  # 70

# String pipeline
sanitize = pipe(
    str.strip,
    str.lower,
    lambda s: " ".join(s.split()),  # collapse whitespace
)
print(sanitize("  Hello   World  "))  # "hello world"

# Typed pipe using type hints
from typing import TypeVar, Callable

T = TypeVar("T")

def typed_pipe(*fns: Callable[[T], T]) -> Callable[[T], T]:
    def piped(x: T) -> T:
        result = x
        for fn in fns:
            result = fn(result)
        return result
    return piped
```

---

## Bonus: Quick-Fire Questions

| # | Question | Answer |
|---|----------|--------|
| 1 | Can Go functions be generic? | Yes, since Go 1.18 with type parameters |
| 2 | What is `@functools.wraps` in Python? | Preserves original function's name, docstring, etc. when decorating |
| 3 | What is a method reference in Java? | Shorthand for lambda: `String::length` instead of `s -> s.length()` |
| 4 | What does `defer` do in Go? | Schedules a function call to run when the enclosing function returns |
| 5 | What is Python's recursion limit? | Default 1000; changeable via `sys.setrecursionlimit()` |
| 6 | What is a `@FunctionalInterface` in Java? | Interface with exactly one abstract method; enables lambda usage |
| 7 | What is the difference between `*args` and `**kwargs`? | `*args` = positional tuple; `**kwargs` = keyword dict |
| 8 | What is a named return in Go? | `func f() (result int, err error)` — named return values, can use bare `return` |
