# Language Syntax — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is the difference between compiled and interpreted languages? | Go=compiled to native, Java=compiled to bytecode+JIT, Python=interpreted |
| 2 | What is a variable and how do you declare one in Go, Java, Python? | Go: `:=` or `var`, Java: type+name, Python: just assign |
| 3 | What is the difference between `=` and `==`? | Assignment vs comparison; common bug source |
| 4 | What happens when you divide two integers in Go/Java? | Integer division — truncates toward zero |
| 5 | How does Python handle integer overflow? | It doesn't — Python has arbitrary precision integers |
| 6 | What is type casting? Give an example in each language. | `int(x)` Python, `(int) x` Java, `int(x)` Go |
| 7 | What is the difference between `print`, `println`, and `printf`? | Raw vs newline vs formatted output |
| 8 | What is a string? Is it mutable or immutable in each language? | Immutable in all three; Python/Java string pool, Go byte slice underneath |

---

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Explain value types vs reference types in Go and Java. | Stack vs heap, copy semantics, pointer/reference sharing |
| 2 | What is the difference between `==` for strings in Java vs Go vs Python? | Java: reference equality (use `.equals()`), Go/Python: value equality |
| 3 | Why is string concatenation with `+` in a loop slow? What's the alternative? | O(n²) total; use `StringBuilder`/`strings.Builder`/`join()` |
| 4 | What is type inference? How does it work in Go vs Java? | Go: `:=`, Java: `var` (Java 10+); compiler deduces type from right side |
| 5 | Explain Go's zero values. What are Java's defaults? Python? | Go: `0`, `""`, `false`, `nil`; Java: `0`, `null`; Python: no defaults (must initialize) |
| 6 | What is the difference between error handling in Go vs Java/Python? | Go: explicit `error` return; Java/Python: exceptions (try-catch/except) |
| 7 | How does Go's `:=` differ from `var`? When would you use each? | `:=` = short declaration (infers type); `var` = explicit, can declare without init |
| 8 | What is the difference between pass-by-value and pass-by-reference? | Go: always pass-by-value (pointers pass address by value); Java: primitives by value, objects by reference value; Python: pass-by-object-reference |

---

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Explain Go's escape analysis. How does it affect performance? | Compiler decides stack vs heap; `go build -gcflags="-m"` to inspect |
| 2 | What is the Java Memory Model? Why does it matter? | Happens-before relations, volatile, synchronized; visibility guarantees |
| 3 | Explain Python's GIL. How does it affect multithreading? | Only one thread runs Python bytecodes; use multiprocessing for CPU-bound |
| 4 | How would you choose between Go, Java, and Python for a microservice? | Latency→Go, ecosystem→Java, ML→Python; discuss GC, startup, concurrency |
| 5 | What is the difference between goroutines, Java threads, and Python threads? | Go: M:N green threads; Java: OS threads (virtual since 21); Python: OS threads + GIL |
| 6 | How do you profile memory usage in Go/Java/Python? | Go: pprof; Java: JFR/VisualVM; Python: tracemalloc/memory_profiler |

---

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Where do Go, Java, and Python grammars fall in the Chomsky hierarchy? | Mostly CFG (Type 2); Python indent = context-sensitive at lexer level |
| 2 | What is type soundness? Are Go/Java/Python sound? | No runtime type errors if type-checked; Go mostly yes, Java yes (with caveats), Python no |
| 3 | Explain the tradeoffs of JIT vs AOT compilation. | JIT: profile-guided optimization but warm-up; AOT: instant startup but no runtime info |
| 4 | Prove that string concatenation in a loop is O(n²). | Each concat copies all previous chars: 1+2+3+...+n = n(n+1)/2 = O(n²) |
| 5 | What is the happens-before relation? How does Go/Java formalize it? | Partial order on memory operations; defines visibility guarantees across threads |

---

## Coding Challenge 1: Swap Without Temp Variable

> Implement swap using XOR (Go/Java) and tuple unpacking (Python).

#### Go

```go
package main

import "fmt"

func swapXOR(a, b int) (int, int) {
    a = a ^ b
    b = a ^ b
    a = a ^ b
    return a, b
}

func main() {
    a, b := 5, 10
    a, b = swapXOR(a, b)
    fmt.Println(a, b) // 10 5

    // Idiomatic Go
    x, y := 5, 10
    x, y = y, x
    fmt.Println(x, y) // 10 5
}
```

#### Java

```java
public class SwapChallenge {
    public static void main(String[] args) {
        int a = 5, b = 10;

        // XOR swap
        a = a ^ b;
        b = a ^ b;
        a = a ^ b;
        System.out.println(a + " " + b); // 10 5

        // Arithmetic swap (avoid — can overflow)
        int x = 5, y = 10;
        x = x + y;  // 15
        y = x - y;  // 5
        x = x - y;  // 10
        System.out.println(x + " " + y); // 10 5
    }
}
```

#### Python

```python
# Pythonic way — tuple unpacking
a, b = 5, 10
a, b = b, a
print(a, b)  # 10 5

# XOR (works but not Pythonic)
a, b = 5, 10
a ^= b
b ^= a
a ^= b
print(a, b)  # 10 5
```

---

## Coding Challenge 2: Parse and Sum Numbers from String

> Given a string of comma-separated numbers, parse and return the sum.
> Handle invalid inputs gracefully.

#### Go

```go
package main

import (
    "fmt"
    "strconv"
    "strings"
)

func parseAndSum(input string) (int, error) {
    parts := strings.Split(input, ",")
    sum := 0
    for _, p := range parts {
        p = strings.TrimSpace(p)
        if p == "" {
            continue
        }
        n, err := strconv.Atoi(p)
        if err != nil {
            return 0, fmt.Errorf("invalid number: %q", p)
        }
        sum += n
    }
    return sum, nil
}

func main() {
    result, err := parseAndSum("1, 2, 3, 4, 5")
    if err != nil {
        fmt.Println("Error:", err)
        return
    }
    fmt.Println(result) // 15

    _, err = parseAndSum("1, abc, 3")
    fmt.Println("Error:", err) // invalid number: "abc"
}
```

#### Java

```java
public class ParseSum {
    public static int parseAndSum(String input) {
        String[] parts = input.split(",");
        int sum = 0;
        for (String p : parts) {
            p = p.trim();
            if (p.isEmpty()) continue;
            try {
                sum += Integer.parseInt(p);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: \"" + p + "\"");
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println(parseAndSum("1, 2, 3, 4, 5")); // 15

        try {
            parseAndSum("1, abc, 3");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

#### Python

```python
def parse_and_sum(input_str: str) -> int:
    parts = input_str.split(",")
    total = 0
    for p in parts:
        p = p.strip()
        if not p:
            continue
        try:
            total += int(p)
        except ValueError:
            raise ValueError(f'Invalid number: "{p}"')
    return total

print(parse_and_sum("1, 2, 3, 4, 5"))  # 15

try:
    parse_and_sum("1, abc, 3")
except ValueError as e:
    print(f"Error: {e}")  # Invalid number: "abc"

# Pythonic one-liner (without error handling)
print(sum(int(x.strip()) for x in "1, 2, 3, 4, 5".split(",")))  # 15
```

---

## Coding Challenge 3: Type-Safe Generic Stack

> Implement a generic stack with `push`, `pop`, `peek`, `is_empty` in all 3 languages.

#### Go

```go
package main

import (
    "errors"
    "fmt"
)

type Stack[T any] struct {
    items []T
}

func (s *Stack[T]) Push(item T)       { s.items = append(s.items, item) }
func (s *Stack[T]) IsEmpty() bool     { return len(s.items) == 0 }

func (s *Stack[T]) Pop() (T, error) {
    var zero T
    if s.IsEmpty() {
        return zero, errors.New("stack is empty")
    }
    item := s.items[len(s.items)-1]
    s.items = s.items[:len(s.items)-1]
    return item, nil
}

func (s *Stack[T]) Peek() (T, error) {
    var zero T
    if s.IsEmpty() {
        return zero, errors.New("stack is empty")
    }
    return s.items[len(s.items)-1], nil
}

func main() {
    s := &Stack[int]{}
    s.Push(1)
    s.Push(2)
    s.Push(3)
    fmt.Println(s.Peek()) // 3 <nil>
    fmt.Println(s.Pop())  // 3 <nil>
    fmt.Println(s.Pop())  // 2 <nil>
    fmt.Println(s.Pop())  // 1 <nil>
    fmt.Println(s.Pop())  // 0 stack is empty
}
```

#### Java

```java
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class GenericStack<T> {
    private final List<T> items = new ArrayList<>();

    public void push(T item) { items.add(item); }
    public boolean isEmpty() { return items.isEmpty(); }

    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return items.remove(items.size() - 1);
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return items.get(items.size() - 1);
    }

    public static void main(String[] args) {
        var s = new GenericStack<Integer>();
        s.push(1);
        s.push(2);
        s.push(3);
        System.out.println(s.peek()); // 3
        System.out.println(s.pop());  // 3
        System.out.println(s.pop());  // 2
        System.out.println(s.pop());  // 1

        try {
            s.pop();
        } catch (EmptyStackException e) {
            System.out.println("Stack is empty!");
        }
    }
}
```

#### Python

```python
from typing import TypeVar, Generic, Optional

T = TypeVar('T')

class Stack(Generic[T]):
    def __init__(self):
        self._items: list[T] = []

    def push(self, item: T) -> None:
        self._items.append(item)

    def pop(self) -> T:
        if self.is_empty():
            raise IndexError("Stack is empty")
        return self._items.pop()

    def peek(self) -> T:
        if self.is_empty():
            raise IndexError("Stack is empty")
        return self._items[-1]

    def is_empty(self) -> bool:
        return len(self._items) == 0

s: Stack[int] = Stack()
s.push(1)
s.push(2)
s.push(3)
print(s.peek())  # 3
print(s.pop())   # 3
print(s.pop())   # 2
print(s.pop())   # 1

try:
    s.pop()
except IndexError as e:
    print(e)  # Stack is empty
```
