# Control Structures — Practice Tasks

> All tasks must be solved in **Go**, **Java**, and **Python**.

## Beginner Tasks

**Task 1:** Print all numbers from 1 to N. If divisible by 3, print "Fizz". If by 5, "Buzz". If by both, "FizzBuzz".

**Task 2:** Given a number N, determine if it's prime using a loop.
- **Optimization:** Only check up to sqrt(N).

**Task 3:** Print a right triangle of stars:
```
*
**
***
****
*****
```
- Use nested loops. Take height as input.

**Task 4:** Given an array of integers, find the maximum and minimum in a single pass.

**Task 5:** Implement a number guessing game. Computer picks random 1-100, user guesses, program says "higher"/"lower".

## Intermediate Tasks

**Task 6:** Implement bubble sort using nested loops. Count comparisons and swaps.

**Task 7:** Given a string, count vowels, consonants, digits, and spaces using a single loop.

**Task 8:** Print Pascal's triangle (first N rows) using nested loops.

**Task 9:** Implement binary search using a while loop. Return index or -1.

**Task 10:** Given a 2D matrix, print it in spiral order using while loops with boundary tracking.

## Advanced Tasks

**Task 11:** Implement a simple state machine that validates email format character by character.
- States: START, LOCAL_PART, AT_SIGN, DOMAIN, DOT, EXTENSION

**Task 12:** Write a producer-consumer pattern with a bounded buffer.
- Go: channels; Java: BlockingQueue; Python: asyncio.Queue

**Task 13:** Implement the Sieve of Eratosthenes to find all primes up to N.
- Use nested loops with early termination.

**Task 14:** Implement power function `pow(base, exp)` using:
- a) Loop: O(n)
- b) Fast exponentiation (squaring): O(log n)

**Task 15:** Implement a retry mechanism with exponential backoff.
- Max 5 retries, delay doubles each time (1s, 2s, 4s, 8s, 16s).

## Benchmark Task

> Compare loop performance: for vs for-each vs while.

#### Go

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    n := 100_000_000
    arr := make([]int, n)
    for i := range arr { arr[i] = i }

    // Classic for
    start := time.Now()
    sum := 0
    for i := 0; i < n; i++ { sum += arr[i] }
    fmt.Printf("for index: %v (sum=%d)\n", time.Since(start), sum)

    // Range-based
    start = time.Now()
    sum = 0
    for _, v := range arr { sum += v }
    fmt.Printf("for range: %v (sum=%d)\n", time.Since(start), sum)
}
```

#### Java

```java
public class LoopBenchmark {
    public static void main(String[] args) {
        int n = 100_000_000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;

        // Classic for
        long start = System.nanoTime();
        long sum = 0;
        for (int i = 0; i < n; i++) sum += arr[i];
        System.out.printf("for index: %.2f ms (sum=%d)%n", (System.nanoTime()-start)/1e6, sum);

        // Enhanced for
        start = System.nanoTime();
        sum = 0;
        for (int v : arr) sum += v;
        System.out.printf("for-each:  %.2f ms (sum=%d)%n", (System.nanoTime()-start)/1e6, sum);
    }
}
```

#### Python

```python
import timeit

n = 10_000_000
arr = list(range(n))

def sum_for():
    s = 0
    for i in range(len(arr)):
        s += arr[i]
    return s

def sum_foreach():
    s = 0
    for v in arr:
        s += v
    return s

def sum_builtin():
    return sum(arr)

print(f"for index:  {timeit.timeit(sum_for, number=3)/3*1000:.1f} ms")
print(f"for-each:   {timeit.timeit(sum_foreach, number=3)/3*1000:.1f} ms")
print(f"sum():      {timeit.timeit(sum_builtin, number=3)/3*1000:.1f} ms")
```
