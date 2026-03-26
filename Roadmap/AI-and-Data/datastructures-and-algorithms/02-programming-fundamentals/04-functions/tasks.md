# Functions — Practice Tasks

> All tasks must be solved in **Go**, **Java**, and **Python**.

## Beginner Tasks

**Task 1:** Write a function `isPalindrome(s string) bool` that checks if a string is a palindrome (reads the same forwards and backwards). Ignore case and non-alphanumeric characters.

Starter code:

#### Go

```go
package main

import (
    "fmt"
    "strings"
    "unicode"
)

func isPalindrome(s string) bool {
    // TODO: clean the string (lowercase, remove non-alphanumeric)
    // TODO: check if cleaned string equals its reverse
    return false
}

func main() {
    fmt.Println(isPalindrome("racecar"))           // true
    fmt.Println(isPalindrome("A man, a plan, a canal: Panama")) // true
    fmt.Println(isPalindrome("hello"))             // false
}
```

#### Java

```java
public class Task1 {
    public static boolean isPalindrome(String s) {
        // TODO: clean the string (lowercase, remove non-alphanumeric)
        // TODO: check if cleaned string equals its reverse
        return false;
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome("racecar"));           // true
        System.out.println(isPalindrome("A man, a plan, a canal: Panama")); // true
        System.out.println(isPalindrome("hello"));             // false
    }
}
```

#### Python

```python
def is_palindrome(s: str) -> bool:
    # TODO: clean the string (lowercase, remove non-alphanumeric)
    # TODO: check if cleaned string equals its reverse
    pass

print(is_palindrome("racecar"))           # True
print(is_palindrome("A man, a plan, a canal: Panama"))  # True
print(is_palindrome("hello"))             # False
```

---

**Task 2:** Write a function `fizzBuzz(n int)` that prints numbers from 1 to n. For multiples of 3, print "Fizz". For multiples of 5, print "Buzz". For multiples of both, print "FizzBuzz". Return the result as a list/slice of strings.

---

**Task 3:** Write two functions:
- `celsiusToFahrenheit(c float64) float64`
- `fahrenheitToCelsius(f float64) float64`

Then write a third function `convertTemperatures(temps []float64, toUnit string) []float64` that converts a list of temperatures using the appropriate function.

---

**Task 4:** Write a variadic function `average(nums ...float64) float64` that returns the average of all arguments. Handle the edge case of zero arguments (return 0).

---

**Task 5:** Write a function `findMax(arr []int) (int, int)` that returns both the maximum value and its index. Handle the empty array case by returning an error or sentinel value.

---

## Intermediate Tasks

**Task 6:** Implement `map`, `filter`, and `reduce` as higher-order functions (don't use built-in versions). Then use them to solve: "Given a list of integers, find the sum of squares of all even numbers."

---

**Task 7:** Write a `makeCounter()` function that returns a closure with three operations:
- `increment()` — adds 1
- `decrement()` — subtracts 1
- `getValue()` — returns current count

```
counter := makeCounter(0)
counter.increment() // 1
counter.increment() // 2
counter.decrement() // 1
counter.getValue()  // 1
```

---

**Task 8:** Write a recursive function `flatten(nested)` that flattens a deeply nested list/slice into a flat list.

Input: `[1, [2, [3, 4], 5], [6, 7]]`
Output: `[1, 2, 3, 4, 5, 6, 7]`

---

**Task 9:** Implement a `compose` function and a `pipe` function. Then create a text processing pipeline:

```
pipeline = pipe(
    trim,
    lowercase,
    removeExtraSpaces,
    capitalizeFirst,
)
pipeline("  HELLO   WORLD  ") // "Hello world"
```

---

**Task 10:** Write a `debounce(fn, delay)` function that ensures `fn` is only called after `delay` milliseconds have passed since the last invocation. If called again before the delay expires, the timer resets.

---

## Advanced Tasks

**Task 11:** Implement a generic memoize function with:
- Cache size limit (LRU eviction)
- TTL (time-to-live) for cache entries
- Cache hit/miss statistics

```
memoFib := memoize(fib, MaxSize(100), TTL(60*time.Second))
memoFib(50) // computed
memoFib(50) // cached
memoFib.Stats() // {hits: 1, misses: 1, size: 1}
```

---

**Task 12:** Implement a middleware chain system. Create at least 4 middlewares:
- `logging` — logs function entry/exit with timing
- `retry` — retries on failure with exponential backoff
- `circuitBreaker` — stops calling after N failures
- `timeout` — cancels if function takes too long

Chain them: `chain(handler, logging, retry(3), circuitBreaker(5), timeout(5*time.Second))`

---

**Task 13:** Implement the **trampoline pattern** to avoid stack overflow in recursive functions. Your trampoline should convert any tail-recursive function into an iterative one.

Test it with:
- Factorial of 100,000
- Fibonacci (tail-recursive version)
- Mutual recursion: `isEven(n)` calls `isOdd(n-1)` and vice versa

---

**Task 14:** Build a **function pipeline with error handling** (Railway Oriented Programming). Each step in the pipeline either succeeds (passes result to next step) or fails (short-circuits to error handler).

```
pipeline = railway(
    validateInput,    # may return Err("invalid input")
    fetchFromDB,      # may return Err("not found")
    transform,        # may return Err("transform failed")
    saveToCache,      # may return Err("cache full")
)
result = pipeline(input)  # Ok(value) or Err(message)
```

---

**Task 15:** Implement **currying** and **partial application** as generic utility functions.

```python
# Currying
@curry
def add(a, b, c):
    return a + b + c

add(1)(2)(3)  # 6
add(1, 2)(3)  # 6
add(1)(2, 3)  # 6

# Partial application
add5 = partial(add, 5)
add5(3)       # 8
```

---

## Benchmark Task

> Compare function call overhead across different patterns.

Benchmark the following and report results in a table:

1. **Direct function call** vs **interface/closure call** — measure the overhead of indirection
2. **Recursive** vs **iterative** Fibonacci for n=30
3. **Memoized** vs **unmemoized** Fibonacci for n=40
4. **Inline code** vs **function call** for a simple operation (e.g., `x*x`) in a tight loop (10 million iterations)

#### Go

```go
package main

import (
    "fmt"
    "time"
)

func fibRec(n int) int {
    if n <= 1 { return n }
    return fibRec(n-1) + fibRec(n-2)
}

func fibIter(n int) int {
    if n <= 1 { return n }
    a, b := 0, 1
    for i := 2; i <= n; i++ {
        a, b = b, a+b
    }
    return b
}

func fibMemo() func(int) int {
    cache := map[int]int{}
    var fib func(int) int
    fib = func(n int) int {
        if n <= 1 { return n }
        if v, ok := cache[n]; ok { return v }
        cache[n] = fib(n-1) + fib(n-2)
        return cache[n]
    }
    return fib
}

func benchmark(name string, fn func()) time.Duration {
    start := time.Now()
    fn()
    elapsed := time.Since(start)
    fmt.Printf("%-30s %v\n", name, elapsed)
    return elapsed
}

func main() {
    fmt.Println("=== Function Call Overhead Benchmark ===")
    fmt.Println()

    // 1. Direct vs closure call
    directFn := func(x int) int { return x * x }
    N := 10_000_000
    benchmark("Direct function (10M calls)", func() {
        for i := 0; i < N; i++ {
            _ = directFn(i)
        }
    })
    benchmark("Inline (10M iterations)", func() {
        for i := 0; i < N; i++ {
            _ = i * i
        }
    })

    fmt.Println()

    // 2. Recursive vs iterative
    benchmark("Recursive fib(30)", func() { fibRec(30) })
    benchmark("Iterative fib(30)", func() { fibIter(30) })

    fmt.Println()

    // 3. Memoized vs unmemoized
    benchmark("Unmemoized fib(40)", func() { fibRec(40) })
    memoFib := fibMemo()
    benchmark("Memoized fib(40)", func() { memoFib(40) })
}
```

#### Java

```java
import java.util.*;

public class Benchmark {
    static int fibRec(int n) {
        if (n <= 1) return n;
        return fibRec(n - 1) + fibRec(n - 2);
    }

    static int fibIter(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = b;
            b = a + b;
            a = temp;
        }
        return b;
    }

    static void benchmark(String name, Runnable fn) {
        long start = System.nanoTime();
        fn.run();
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("%-30s %dms%n", name, elapsed);
    }

    public static void main(String[] args) {
        System.out.println("=== Function Call Overhead Benchmark ===\n");

        int N = 10_000_000;

        benchmark("Direct function (10M)", () -> {
            for (int i = 0; i < N; i++) { int x = i * i; }
        });

        benchmark("Lambda call (10M)", () -> {
            java.util.function.IntUnaryOperator sq = x -> x * x;
            for (int i = 0; i < N; i++) { sq.applyAsInt(i); }
        });

        System.out.println();
        benchmark("Recursive fib(30)", () -> fibRec(30));
        benchmark("Iterative fib(30)", () -> fibIter(30));

        System.out.println();
        benchmark("Recursive fib(40)", () -> fibRec(40));

        Map<Integer, Integer> cache = new HashMap<>();
        benchmark("Memoized fib(40)", () -> {
            fibMemo(40, cache);
        });
    }

    static int fibMemo(int n, Map<Integer, Integer> cache) {
        if (n <= 1) return n;
        if (cache.containsKey(n)) return cache.get(n);
        int result = fibMemo(n - 1, cache) + fibMemo(n - 2, cache);
        cache.put(n, result);
        return result;
    }
}
```

#### Python

```python
import time
from functools import lru_cache

def fib_rec(n):
    if n <= 1: return n
    return fib_rec(n-1) + fib_rec(n-2)

def fib_iter(n):
    if n <= 1: return n
    a, b = 0, 1
    for _ in range(2, n+1):
        a, b = b, a + b
    return b

@lru_cache(maxsize=None)
def fib_memo(n):
    if n <= 1: return n
    return fib_memo(n-1) + fib_memo(n-2)

def benchmark(name, fn):
    start = time.perf_counter()
    fn()
    elapsed = time.perf_counter() - start
    print(f"{name:<35} {elapsed:.6f}s")

print("=== Function Call Overhead Benchmark ===\n")

N = 10_000_000
square = lambda x: x * x

benchmark("Direct function (10M)", lambda: [square(i) for i in range(N)])
benchmark("Inline (10M)", lambda: [i * i for i in range(N)])

print()
benchmark("Recursive fib(30)", lambda: fib_rec(30))
benchmark("Iterative fib(30)", lambda: fib_iter(30))

print()
benchmark("Recursive fib(35)", lambda: fib_rec(35))
fib_memo.cache_clear()
benchmark("Memoized fib(35)", lambda: fib_memo(35))
```
