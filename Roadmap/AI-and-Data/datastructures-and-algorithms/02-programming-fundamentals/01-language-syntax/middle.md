# Language Syntax — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Deeper Concepts](#deeper-concepts)
3. [Comparison with Alternatives](#comparison-with-alternatives)
4. [Advanced Patterns](#advanced-patterns)
5. [Code Examples](#code-examples)
6. [Error Handling](#error-handling)
7. [Performance Analysis](#performance-analysis)
8. [Best Practices](#best-practices)
9. [Visual Animation](#visual-animation)
10. [Summary](#summary)

---

## Introduction

> Focus: "Why does each language make these syntax choices?" and "When does syntax impact performance?"

At the middle level, you understand not just *how* to write code, but *why* each language's syntax works the way it does. You learn about memory models, type systems, and how syntax choices affect program correctness and efficiency.

---

## Deeper Concepts

### Value Types vs Reference Types

Understanding how variables are stored in memory is critical for writing correct and efficient algorithms.

#### Go

```go
package main

import "fmt"

func main() {
    // Value types: int, float, bool, string, struct, array
    a := 42
    b := a       // COPY — b is independent
    b = 100
    fmt.Println(a, b) // 42 100

    // Reference types: slice, map, channel, pointer, function
    s1 := []int{1, 2, 3}
    s2 := s1      // shares underlying array!
    s2[0] = 999
    fmt.Println(s1) // [999 2 3] — s1 is affected!

    // Pointer — explicit reference
    x := 42
    p := &x       // p points to x
    *p = 100
    fmt.Println(x) // 100
}
```

#### Java

```java
public class ValueVsReference {
    public static void main(String[] args) {
        // Primitives: int, double, boolean, char — VALUE types
        int a = 42;
        int b = a;    // COPY
        b = 100;
        System.out.println(a + " " + b); // 42 100

        // Objects: String, arrays, custom classes — REFERENCE types
        int[] s1 = {1, 2, 3};
        int[] s2 = s1;    // shares the same array!
        s2[0] = 999;
        System.out.println(java.util.Arrays.toString(s1)); // [999, 2, 3]

        // Strings are immutable references
        String x = "hello";
        String y = x;
        y = "world";     // creates new String, x unchanged
        System.out.println(x); // hello
    }
}
```

#### Python

```python
# Everything is an object in Python
# Immutable: int, float, str, tuple — behave like value types
a = 42
b = a       # both point to same object
b = 100     # b now points to new object
print(a, b) # 42 100

# Mutable: list, dict, set — behave like reference types
s1 = [1, 2, 3]
s2 = s1       # both point to same list!
s2[0] = 999
print(s1)     # [999, 2, 3]

# Deep copy to avoid this
import copy
s3 = copy.deepcopy(s1)
s3[0] = 0
print(s1)     # [999, 2, 3] — unchanged
```

### Static vs Dynamic Typing

| Aspect | Go (Static) | Java (Static) | Python (Dynamic) |
|--------|-------------|---------------|-----------------|
| Type checked | Compile time | Compile time | Runtime |
| Type declared | Optional (inference) | Required (mostly) | Not required |
| Type errors | Won't compile | Won't compile | Crashes at runtime |
| Flexibility | Low | Low | High |
| Safety | High | High | Lower |
| Performance | Fast | Fast | Slower (type checks at runtime) |

### Type Inference

#### Go

```go
x := 42          // compiler infers int
y := 3.14        // compiler infers float64
z := "hello"     // compiler infers string
// Type is fixed after inference — cannot reassign different type
// x = "world"   // ERROR: cannot use "world" (string) as int
```

#### Java

```java
// var keyword (Java 10+)
var x = 42;         // compiler infers int
var y = 3.14;       // compiler infers double
var z = "hello";    // compiler infers String
// x = "world";     // ERROR: incompatible types
```

#### Python

```python
x = 42          # int
x = "hello"     # now str — totally fine!
x = [1, 2, 3]  # now list — Python doesn't care

# Type hints (PEP 484) — optional, not enforced at runtime
def add(a: int, b: int) -> int:
    return a + b

add("hello", "world")  # runs fine! type hints are just documentation
```

---

## Comparison with Alternatives

### Memory Model Comparison

| Aspect | Go | Java | Python |
|--------|-----|------|--------|
| Memory management | GC (concurrent, low-latency) | GC (generational) | GC (reference counting + cycle collector) |
| Stack vs Heap | Compiler decides (escape analysis) | Primitives: stack; Objects: heap | Everything on heap |
| Null safety | `nil` (only for reference types) | `null` (any object) | `None` (any variable) |
| Zero values | Yes (`0`, `""`, `false`, `nil`) | Primitives: yes; Objects: `null` | No zero values — must initialize |
| Pointers | Explicit (`*`, `&`) | Hidden (all objects are references) | Hidden (all names are references) |

### Compilation Model

| Aspect | Go | Java | Python |
|--------|-----|------|--------|
| Model | Compiled to native binary | Compiled to bytecode (JVM) | Interpreted (CPython) |
| Startup time | ~5ms | ~100-500ms | ~30ms |
| Execution speed | Fast (native) | Fast (JIT compilation) | Slow (interpreted) |
| Binary size | 5-15 MB (statically linked) | Requires JVM (~200 MB) | Requires Python runtime (~30 MB) |

---

## Advanced Patterns

### Pattern: Builder / Fluent API

#### Go

```go
package main

import (
    "fmt"
    "strings"
)

// Go uses functional options pattern
type Config struct {
    Host    string
    Port    int
    Debug   bool
}

type Option func(*Config)

func WithHost(h string) Option { return func(c *Config) { c.Host = h } }
func WithPort(p int) Option    { return func(c *Config) { c.Port = p } }
func WithDebug() Option        { return func(c *Config) { c.Debug = true } }

func NewConfig(opts ...Option) Config {
    c := Config{Host: "localhost", Port: 8080}
    for _, opt := range opts {
        opt(&c)
    }
    return c
}

func main() {
    cfg := NewConfig(WithHost("example.com"), WithPort(443), WithDebug())
    fmt.Printf("%+v\n", cfg)

    // strings.Builder for efficient concatenation
    var sb strings.Builder
    for i := 0; i < 1000; i++ {
        sb.WriteString("x")
    }
    fmt.Println(sb.Len())
}
```

#### Java

```java
// Java uses classic Builder pattern
public class Config {
    private String host;
    private int port;
    private boolean debug;

    private Config() {}

    public static class Builder {
        private String host = "localhost";
        private int port = 8080;
        private boolean debug = false;

        public Builder host(String h)  { this.host = h; return this; }
        public Builder port(int p)     { this.port = p; return this; }
        public Builder debug(boolean d) { this.debug = d; return this; }

        public Config build() {
            Config c = new Config();
            c.host = this.host;
            c.port = this.port;
            c.debug = this.debug;
            return c;
        }
    }

    public static void main(String[] args) {
        Config cfg = new Config.Builder()
            .host("example.com")
            .port(443)
            .debug(true)
            .build();

        // StringBuilder for efficient concatenation
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("x");
        }
        System.out.println(sb.length());
    }
}
```

#### Python

```python
# Python uses keyword arguments or dataclasses
from dataclasses import dataclass, field

@dataclass
class Config:
    host: str = "localhost"
    port: int = 8080
    debug: bool = False

cfg = Config(host="example.com", port=443, debug=True)
print(cfg)

# Efficient string concatenation
parts = ["x" for _ in range(1000)]
result = "".join(parts)  # O(n) — much faster than += in a loop
print(len(result))
```

### Pattern: Enum / Constants

#### Go

```go
package main

import "fmt"

type Color int

const (
    Red Color = iota  // 0
    Green             // 1
    Blue              // 2
)

func (c Color) String() string {
    return [...]string{"Red", "Green", "Blue"}[c]
}

func main() {
    c := Green
    fmt.Println(c) // Green
}
```

#### Java

```java
public enum Color {
    RED, GREEN, BLUE;

    public static void main(String[] args) {
        Color c = Color.GREEN;
        System.out.println(c);           // GREEN
        System.out.println(c.ordinal()); // 1
    }
}
```

#### Python

```python
from enum import Enum

class Color(Enum):
    RED = 0
    GREEN = 1
    BLUE = 2

c = Color.GREEN
print(c)        # Color.GREEN
print(c.value)  # 1
print(c.name)   # GREEN
```

---

## Code Examples

### Example: Comprehensive Type System Demo

#### Go

```go
package main

import "fmt"

// Struct with methods
type Point struct {
    X, Y float64
}

func (p Point) Distance() float64 {
    return p.X*p.X + p.Y*p.Y // simplified
}

// Interface
type Shape interface {
    Area() float64
}

type Circle struct {
    Radius float64
}

func (c Circle) Area() float64 {
    return 3.14159 * c.Radius * c.Radius
}

func printArea(s Shape) {
    fmt.Printf("Area: %.2f\n", s.Area())
}

func main() {
    p := Point{3, 4}
    fmt.Println(p.Distance()) // 25

    c := Circle{5}
    printArea(c) // Area: 78.54
}
```

#### Java

```java
// Interface
interface Shape {
    double area();
}

// Record (Java 16+) — immutable data class
record Point(double x, double y) {
    double distance() {
        return x * x + y * y;
    }
}

// Class implementing interface
class Circle implements Shape {
    private final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

public class TypeDemo {
    static void printArea(Shape s) {
        System.out.printf("Area: %.2f%n", s.area());
    }

    public static void main(String[] args) {
        var p = new Point(3, 4);
        System.out.println(p.distance()); // 25.0

        var c = new Circle(5);
        printArea(c); // Area: 78.54
    }
}
```

#### Python

```python
from dataclasses import dataclass
from abc import ABC, abstractmethod
import math

@dataclass
class Point:
    x: float
    y: float

    def distance(self) -> float:
        return self.x ** 2 + self.y ** 2

class Shape(ABC):
    @abstractmethod
    def area(self) -> float:
        pass

@dataclass
class Circle(Shape):
    radius: float

    def area(self) -> float:
        return math.pi * self.radius ** 2

def print_area(s: Shape):
    print(f"Area: {s.area():.2f}")

p = Point(3, 4)
print(p.distance())  # 25

c = Circle(5)
print_area(c)  # Area: 78.54
```

---

## Error Handling

### Error Handling Comparison

#### Go — Explicit Error Return

```go
package main

import (
    "errors"
    "fmt"
    "strconv"
)

func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

func main() {
    // Idiomatic Go error handling
    result, err := divide(10, 0)
    if err != nil {
        fmt.Println("Error:", err)
        return
    }
    fmt.Println(result)

    // Type conversion error
    n, err := strconv.Atoi("abc")
    if err != nil {
        fmt.Println("Parse error:", err)
    }
    _ = n
}
```

#### Java — Exceptions (try-catch)

```java
public class ErrorHandling {
    public static double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }

    public static void main(String[] args) {
        // try-catch
        try {
            double result = divide(10, 0);
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Type conversion error
        try {
            int n = Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            System.out.println("Parse error: " + e.getMessage());
        }
    }
}
```

#### Python — Exceptions (try-except)

```python
def divide(a, b):
    if b == 0:
        raise ValueError("Division by zero")
    return a / b

# try-except
try:
    result = divide(10, 0)
    print(result)
except ValueError as e:
    print(f"Error: {e}")

# Type conversion error
try:
    n = int("abc")
except ValueError as e:
    print(f"Parse error: {e}")

# Multiple exceptions
try:
    x = int(input("Enter a number: "))
    result = 100 / x
except ValueError:
    print("Not a valid number")
except ZeroDivisionError:
    print("Cannot divide by zero")
finally:
    print("This always runs")
```

### Error Handling Comparison Table

| Aspect | Go | Java | Python |
|--------|-----|------|--------|
| Mechanism | Return `error` value | Exceptions (try-catch) | Exceptions (try-except) |
| Checked errors | No | Yes (checked exceptions) | No |
| Panic/crash | `panic()` / `recover()` | `throw` / `catch` | `raise` / `except` |
| Null safety | Compiler warnings | `NullPointerException` | `AttributeError` |
| Idiom | `if err != nil` | `try-catch-finally` | `try-except-finally` |

---

## Performance Analysis

### String Concatenation Benchmark

#### Go

```go
package main

import (
    "fmt"
    "strings"
    "time"
)

func main() {
    n := 100_000

    // Slow: += concatenation
    start := time.Now()
    s := ""
    for i := 0; i < n; i++ {
        s += "x"
    }
    fmt.Printf("+=        : %v\n", time.Since(start))

    // Fast: strings.Builder
    start = time.Now()
    var sb strings.Builder
    for i := 0; i < n; i++ {
        sb.WriteString("x")
    }
    _ = sb.String()
    fmt.Printf("Builder   : %v\n", time.Since(start))
}
```

#### Java

```java
public class StringBenchmark {
    public static void main(String[] args) {
        int n = 100_000;

        // Slow: += concatenation
        long start = System.nanoTime();
        String s = "";
        for (int i = 0; i < n; i++) {
            s += "x";
        }
        System.out.printf("+=        : %.2f ms%n", (System.nanoTime() - start) / 1e6);

        // Fast: StringBuilder
        start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("x");
        }
        String result = sb.toString();
        System.out.printf("Builder   : %.2f ms%n", (System.nanoTime() - start) / 1e6);
    }
}
```

#### Python

```python
import timeit

n = 100_000

# Slow: += concatenation
def concat_plus():
    s = ""
    for _ in range(n):
        s += "x"
    return s

# Fast: join
def concat_join():
    return "".join("x" for _ in range(n))

print(f"+=   : {timeit.timeit(concat_plus, number=10) / 10 * 1000:.2f} ms")
print(f"join : {timeit.timeit(concat_join, number=10) / 10 * 1000:.2f} ms")
```

### Expected Results

| Method | Go | Java | Python |
|--------|-----|------|--------|
| `+=` (100K iterations) | ~500ms | ~3000ms | ~200ms |
| Builder/join | ~1ms | ~2ms | ~5ms |
| Speedup | ~500x | ~1500x | ~40x |

---

## Best Practices

- Understand your language's memory model before writing algorithms
- Use the right string builder for your language
- Go: prefer value receivers for small structs, pointer receivers for large or mutable
- Java: use `final` for constants, prefer records for data-only classes
- Python: use type hints for documentation, `@dataclass` for value objects
- Know when copies happen vs. when references are shared

---

## Summary

At the middle level, language syntax understanding goes beyond "how to write code" to "why the language works this way." Understanding value vs reference types prevents subtle bugs in algorithms. Knowing the type system helps you choose the right language for the right problem. String handling performance differences can make or break your solution in competitive programming.
