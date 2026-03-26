# Language Syntax — Practice Tasks

> All tasks must be solved in **Go**, **Java**, and **Python**.

## Beginner Tasks

**Task 1:** Hello World with user input — ask the user for their name and age, then print a greeting.

#### Go

```go
package main

import (
    "bufio"
    "fmt"
    "os"
    "strings"
)

func main() {
    reader := bufio.NewReader(os.Stdin)
    fmt.Print("Name: ")
    name, _ := reader.ReadString('\n')
    name = strings.TrimSpace(name)
    // TODO: ask for age, print greeting
}
```

#### Java

```java
import java.util.Scanner;

public class Task1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Name: ");
        String name = sc.nextLine();
        // TODO: ask for age, print greeting
    }
}
```

#### Python

```python
name = input("Name: ")
# TODO: ask for age, print greeting
```

- **Expected Output:** `Hello Alice! You are 25 years old.`
- **Evaluation:** Correct I/O, proper type conversion for age

---

**Task 2:** Temperature Converter — convert Celsius to Fahrenheit and vice versa.

- Formula: `F = C * 9/5 + 32`
- **Input:** `37.5 C` or `98.6 F`
- **Expected Output:** `37.5°C = 99.5°F` or `98.6°F = 37.0°C`
- **Evaluation:** Correct float handling, input parsing, formatted output

---

**Task 3:** Calculator — implement `+`, `-`, `*`, `/`, `%` with two numbers from user input.

- Handle division by zero
- **Expected Output:** `10 / 3 = 3` (integer) or `3.33` (float)
- **Evaluation:** Correct operator parsing, error handling, integer vs float division

---

**Task 4:** FizzBuzz — print numbers 1 to 100, but:
- Print "Fizz" for multiples of 3
- Print "Buzz" for multiples of 5
- Print "FizzBuzz" for multiples of both

- **Evaluation:** Correct modulo usage, order of conditions

---

**Task 5:** Reverse a string without using built-in reverse functions.

- **Input:** `"hello"`
- **Expected Output:** `"olleh"`
- **Evaluation:** Correct string iteration, handle Unicode characters

---

## Intermediate Tasks

**Task 6:** Word Counter — read a paragraph and count occurrences of each word (case-insensitive).

#### Go

```go
package main

import (
    "fmt"
    "strings"
)

func wordCount(text string) map[string]int {
    counts := make(map[string]int)
    // TODO: implement
    return counts
}

func main() {
    text := "the cat sat on the mat the cat"
    fmt.Println(wordCount(text))
    // Expected: map[cat:2 mat:1 on:1 sat:1 the:3]
}
```

#### Java

```java
import java.util.HashMap;
import java.util.Map;

public class Task6 {
    public static Map<String, Integer> wordCount(String text) {
        Map<String, Integer> counts = new HashMap<>();
        // TODO: implement
        return counts;
    }

    public static void main(String[] args) {
        String text = "the cat sat on the mat the cat";
        System.out.println(wordCount(text));
    }
}
```

#### Python

```python
def word_count(text: str) -> dict[str, int]:
    # TODO: implement
    pass

text = "the cat sat on the mat the cat"
print(word_count(text))
# Expected: {'the': 3, 'cat': 2, 'sat': 1, 'on': 1, 'mat': 1}
```

- **Evaluation:** Correct use of hash map, string splitting, case handling

---

**Task 7:** Matrix Printer — create a 2D array of size NxM, fill with multiplication table, print formatted.

- **Input:** N=5, M=5
- **Expected Output:**
  ```
   1  2  3  4  5
   2  4  6  8 10
   3  6  9 12 15
   4  8 12 16 20
   5 10 15 20 25
  ```
- **Evaluation:** Correct 2D array creation, formatted output alignment

---

**Task 8:** Number Base Converter — convert between binary, octal, decimal, and hexadecimal.

- **Input:** `255 dec`
- **Expected Output:** `bin=11111111, oct=377, dec=255, hex=ff`
- **Evaluation:** Correct use of format specifiers, input parsing

---

**Task 9:** String Compression — implement basic run-length encoding.

- **Input:** `"aaabbbccdd"`
- **Expected Output:** `"a3b3c2d2"`
- If compressed is NOT shorter, return original.
- **Evaluation:** Efficient string building (use Builder/StringBuilder/join)

---

**Task 10:** Anagram Checker — check if two strings are anagrams.

- **Input:** `"listen"`, `"silent"`
- **Expected Output:** `true`
- Implement TWO approaches: sorting and hash map counting.
- **Evaluation:** Correct algorithm, O(n) vs O(n log n) analysis

---

## Advanced Tasks

**Task 11:** Generic Pair/Tuple — implement a generic pair that holds two values of any type.

- Must support: creation, accessing first/second, string representation
- Go: generics; Java: generics; Python: `Generic[T1, T2]`
- **Evaluation:** Correct generic usage, type safety

---

**Task 12:** Custom Iterator — implement an iterator over a range with a step.

- `Range(1, 10, 2)` → yields `1, 3, 5, 7, 9`
- Go: use channels or closure; Java: `Iterator<Integer>`; Python: `__iter__`/`__next__`
- **Evaluation:** Correct iterator protocol for each language

---

**Task 13:** Mini JSON Parser — parse a flat JSON object (no nesting) from string to map.

- **Input:** `'{"name": "Alice", "age": "25", "city": "Tokyo"}'`
- **Expected Output:** `map[name:Alice age:25 city:Tokyo]`
- Do NOT use standard library JSON parser.
- **Evaluation:** String parsing, character-by-character processing, edge cases (escaped quotes)

---

**Task 14:** Expression Evaluator — evaluate simple math expressions: `"3 + 5 * 2"`.

- Support: `+`, `-`, `*`, `/` with operator precedence
- Parentheses NOT required
- **Evaluation:** Correct precedence handling, stack-based or recursive descent

---

**Task 15:** Concurrent Counter — implement a thread-safe counter.

- Increment from multiple goroutines/threads
- Print final count (must be exactly N * increments)
- Go: `sync.Mutex` or `atomic`; Java: `AtomicInteger` or `synchronized`; Python: `threading.Lock`
- **Evaluation:** Correct synchronization, no data races

---

## Benchmark Task

> Compare the performance of string concatenation across all 3 languages.

#### Go

```go
package main

import (
    "fmt"
    "strings"
    "time"
)

func main() {
    sizes := []int{100, 1000, 10000, 100000}
    for _, n := range sizes {
        // Method 1: += concatenation
        start := time.Now()
        s := ""
        for i := 0; i < n; i++ {
            s += "x"
        }
        t1 := time.Since(start)

        // Method 2: strings.Builder
        start = time.Now()
        var sb strings.Builder
        for i := 0; i < n; i++ {
            sb.WriteString("x")
        }
        _ = sb.String()
        t2 := time.Since(start)

        fmt.Printf("n=%6d: +=  %10v  |  Builder %10v  |  speedup %.0fx\n",
            n, t1, t2, float64(t1)/float64(t2))
    }
}
```

#### Java

```java
public class Benchmark {
    public static void main(String[] args) {
        int[] sizes = {100, 1000, 10000, 100000};
        for (int n : sizes) {
            // Method 1: += concatenation
            long start = System.nanoTime();
            String s = "";
            for (int i = 0; i < n; i++) {
                s += "x";
            }
            long t1 = System.nanoTime() - start;

            // Method 2: StringBuilder
            start = System.nanoTime();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append("x");
            }
            String result = sb.toString();
            long t2 = System.nanoTime() - start;

            System.out.printf("n=%6d: +=  %10.3f ms  |  Builder %10.3f ms  |  speedup %.0fx%n",
                n, t1 / 1e6, t2 / 1e6, (double) t1 / t2);
        }
    }
}
```

#### Python

```python
import timeit

sizes = [100, 1_000, 10_000, 100_000]

for n in sizes:
    # Method 1: += concatenation
    def concat_plus():
        s = ""
        for _ in range(n):
            s += "x"
        return s

    # Method 2: join
    def concat_join():
        return "".join("x" for _ in range(n))

    t1 = timeit.timeit(concat_plus, number=10) / 10 * 1000
    t2 = timeit.timeit(concat_join, number=10) / 10 * 1000

    print(f"n={n:>6}: +=  {t1:10.3f} ms  |  join {t2:10.3f} ms  |  speedup {t1/t2:.0f}x")
```
