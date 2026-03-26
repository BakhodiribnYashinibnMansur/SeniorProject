# OOP Basics - Practice Tasks

## Beginner Tasks (1-5)

### Task 1: Create a Student Class

Create a `Student` class/struct with fields: `name`, `age`, `grade` (GPA). Implement methods to:
- Create a student
- Get a formatted string representation
- Check if the student is passing (GPA >= 2.0)
- Compare two students by GPA

**Starter Code — Go:**
```go
package main

import "fmt"

type Student struct {
    // TODO: Define fields
}

// TODO: Constructor function
func NewStudent(name string, age int, grade float64) *Student {
    return nil // TODO
}

// TODO: String representation
func (s Student) String() string {
    return "" // TODO
}

// TODO: Is the student passing?
func (s Student) IsPassing() bool {
    return false // TODO
}

// TODO: Compare two students by GPA
// Return: -1 if s < other, 0 if equal, 1 if s > other
func (s Student) CompareByGPA(other Student) int {
    return 0 // TODO
}

func main() {
    s1 := NewStudent("Alice", 20, 3.8)
    s2 := NewStudent("Bob", 22, 1.5)

    fmt.Println(s1)                     // Expected: Alice (age 20, GPA: 3.80)
    fmt.Println(s1.IsPassing())         // Expected: true
    fmt.Println(s2.IsPassing())         // Expected: false
    fmt.Println(s1.CompareByGPA(*s2))   // Expected: 1
}
```

**Starter Code — Java:**
```java
public class Student {
    // TODO: Define private fields

    // TODO: Constructor
    public Student(String name, int age, double grade) {
        // TODO
    }

    // TODO: toString
    @Override
    public String toString() {
        return ""; // TODO
    }

    // TODO: isPassing
    public boolean isPassing() {
        return false; // TODO
    }

    // TODO: compareByGPA — return -1, 0, or 1
    public int compareByGPA(Student other) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        Student s1 = new Student("Alice", 20, 3.8);
        Student s2 = new Student("Bob", 22, 1.5);

        System.out.println(s1);                  // Alice (age 20, GPA: 3.80)
        System.out.println(s1.isPassing());      // true
        System.out.println(s2.isPassing());      // false
        System.out.println(s1.compareByGPA(s2)); // 1
    }
}
```

**Starter Code — Python:**
```python
class Student:
    def __init__(self, name: str, age: int, grade: float):
        pass  # TODO: Initialize fields

    def __repr__(self) -> str:
        pass  # TODO: Return formatted string

    def is_passing(self) -> bool:
        pass  # TODO: GPA >= 2.0

    def compare_by_gpa(self, other: "Student") -> int:
        pass  # TODO: Return -1, 0, or 1


# Test
s1 = Student("Alice", 20, 3.8)
s2 = Student("Bob", 22, 1.5)

print(s1)                      # Alice (age 20, GPA: 3.80)
print(s1.is_passing())         # True
print(s2.is_passing())         # False
print(s1.compare_by_gpa(s2))   # 1
```

---

### Task 2: Implement a BankAccount

Create a `BankAccount` with:
- Fields: `owner`, `balance` (private), `account_number`
- Methods: `deposit(amount)`, `withdraw(amount) -> bool`, `transfer(other, amount) -> bool`
- Validation: no negative deposits, no overdrafts
- Track total number of accounts created (class/package level counter)

**Expected behavior:**
```
acc1 = BankAccount("Alice", 1000)
acc2 = BankAccount("Bob", 500)
acc1.deposit(200)         # balance = 1200
acc1.withdraw(100)        # balance = 1100, returns true
acc1.withdraw(5000)       # balance = 1100, returns false (insufficient funds)
acc1.transfer(acc2, 300)  # acc1 = 800, acc2 = 800, returns true
BankAccount.count()       # 2
```

---

### Task 3: Create an Animal Hierarchy

Create an inheritance/interface hierarchy:
- Base: `Animal` with `name`, `sound`, `speak()` method
- Derived: `Dog`, `Cat`, `Cow` — each with their own sound
- Function `animal_chorus(animals)` that makes all animals speak

**Go:** Use an interface `Speaker` with `Speak() string`.
**Java:** Use an abstract class `Animal` with abstract `speak()`.
**Python:** Use ABC with abstract `speak()`.

---

### Task 4: Build a Calculator with History

Create a `Calculator` class that:
- Performs basic operations: add, subtract, multiply, divide
- Maintains a history of operations
- Supports `undo()` to revert the last operation
- Has a `history()` method that returns all past operations

**Expected behavior:**
```
calc = Calculator()
calc.add(10)        # result = 10
calc.multiply(3)    # result = 30
calc.subtract(5)    # result = 25
calc.history()      # ["add(10) = 10", "multiply(3) = 30", "subtract(5) = 25"]
calc.undo()         # result = 30
calc.result()       # 30
```

---

### Task 5: Create a Library System

Model a simple library:
- `Book`: title, author, ISBN, available (bool)
- `Library`: collection of books
- Methods:
  - `add_book(book)` — add a book to the library
  - `search_by_title(title)` — find books by partial title match
  - `search_by_author(author)` — find books by author
  - `checkout(isbn) -> bool` — mark book as unavailable
  - `return_book(isbn)` — mark book as available
  - `available_books()` — list all available books

---

## Intermediate Tasks (6-10)

### Task 6: Implement an Iterator Interface

Create a generic `Iterator` interface/struct and implement it for:
1. `RangeIterator(start, end, step)` — iterates over a number range
2. `FilterIterator(iterator, predicate)` — wraps another iterator, only yielding items that match a predicate
3. `MapIterator(iterator, transform)` — wraps another iterator, transforming each item

**Go interface:**
```go
type Iterator[T any] interface {
    HasNext() bool
    Next() T
}
```

**Java interface:**
```java
interface Iterator<T> {
    boolean hasNext();
    T next();
}
```

**Python protocol:**
```python
class Iterator(ABC, Generic[T]):
    @abstractmethod
    def has_next(self) -> bool: pass
    @abstractmethod
    def next(self) -> T: pass
```

**Expected chain:**
```
range(1, 20, 1) → filter(x -> x % 2 == 0) → map(x -> x * x)
Output: 4, 16, 36, 64, 100, 144, 196, 256, 324
```

---

### Task 7: Create a Generic Stack with Min Tracking

Create a `MinStack<T>` that supports:
- `push(item)` — O(1)
- `pop() -> item` — O(1)
- `peek() -> item` — O(1)
- `min() -> item` — O(1) — returns the minimum element in the stack

**Hint:** Use an auxiliary stack that tracks minimums.

**Test:**
```
stack.push(5)  → min = 5
stack.push(3)  → min = 3
stack.push(7)  → min = 3
stack.push(1)  → min = 1
stack.pop()    → 1, min = 3
stack.pop()    → 7, min = 3
stack.pop()    → 3, min = 5
```

---

### Task 8: Design a Plugin System

Create a plugin system where:
- `PluginManager` manages registered plugins
- Each plugin implements a `Plugin` interface with: `name()`, `version()`, `execute(context)`
- Plugins can be loaded, unloaded, and executed
- Add a `HookSystem` that allows plugins to register hooks for specific events

**Example plugins:**
- `LoggingPlugin` — logs all events
- `MetricsPlugin` — counts events
- `ValidationPlugin` — validates data before processing

---

### Task 9: Build a Notification System

Create a notification system with:
- `Notification` interface with `send(recipient, message)`
- Implementations: `EmailNotification`, `SMSNotification`, `PushNotification`
- `NotificationService` that:
  - Accepts a list of notification channels
  - Has a `notify(recipient, message)` method that sends through all channels
  - Supports adding/removing channels at runtime
  - Implements retry logic (up to 3 attempts)

---

### Task 10: Create a Type-Safe Builder Pattern

Implement the Builder pattern for a complex `HttpRequest` object:
- Fields: method, url, headers (map), body, timeout, retries
- Builder validates required fields (method, url)
- Builder returns an immutable `HttpRequest`

**Expected usage:**
```
request = HttpRequest.builder()
    .method("POST")
    .url("https://api.example.com/users")
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer token123")
    .body('{"name": "Alice"}')
    .timeout(30)
    .retries(3)
    .build()
```

---

## Advanced Tasks (11-15)

### Task 11: Implement the Strategy Pattern for Pricing

Create a pricing system for an e-commerce platform:
- `PricingStrategy` interface with `calculate(basePrice, quantity) -> finalPrice`
- Strategies:
  - `RegularPricing` — no discount
  - `BulkPricing` — 10% off for 10+ items, 20% off for 50+ items
  - `SeasonalPricing` — percentage discount based on current season
  - `LoyaltyPricing` — discount based on customer loyalty tier (Bronze/Silver/Gold)
- `PricingEngine` that combines multiple strategies (chain them or pick the best deal for the customer)

**Expected:**
```
engine = PricingEngine([BulkPricing(), LoyaltyPricing("gold")])
result = engine.calculate(base_price=100.0, quantity=20)
# BulkPricing: 100 * 20 * 0.90 = 1800.0
# LoyaltyPricing: 100 * 20 * 0.85 = 1700.0
# Best deal: 1700.0
```

---

### Task 12: Build a Simple ORM Model Layer

Create a basic ORM (Object-Relational Mapping) layer:
- `Model` base class/interface with methods: `save()`, `delete()`, `find_by_id(id)`
- `Field` descriptors: `StringField`, `IntField`, `BoolField` with validation
- `QueryBuilder` that constructs SQL queries from method chains:
  - `.where(field, operator, value)`
  - `.order_by(field, direction)`
  - `.limit(n)`
  - `.build()` → returns SQL string

**Example:**
```python
class User(Model):
    name = StringField(max_length=100, required=True)
    age = IntField(min_value=0, max_value=150)
    active = BoolField(default=True)

# Usage
user = User(name="Alice", age=25)
user.save()  # INSERT INTO user (name, age, active) VALUES ('Alice', 25, true)

query = User.query() \
    .where("age", ">", 18) \
    .where("active", "=", True) \
    .order_by("name", "ASC") \
    .limit(10) \
    .build()
# SELECT * FROM user WHERE age > 18 AND active = true ORDER BY name ASC LIMIT 10
```

---

### Task 13: Design a State Machine Using OOP

Implement a state machine for an order processing system:

**States:** `Created → Confirmed → Processing → Shipped → Delivered`
**Also:** `Created → Cancelled`, `Confirmed → Cancelled`

Requirements:
- Each state is a class implementing a `State` interface
- State transitions are validated (can't go from Shipped to Created)
- Each state has `enter()` and `exit()` hooks
- The `Order` class holds the current state and delegates behavior to it
- Each state defines which transitions are allowed

**State diagram:**
```
Created ──→ Confirmed ──→ Processing ──→ Shipped ──→ Delivered
  │              │
  └── Cancelled ←┘
```

---

### Task 14: Implement a Command Pattern with Undo/Redo

Create a text editor with undo/redo using the Command pattern:

- `Command` interface: `execute()`, `undo()`
- Commands:
  - `InsertTextCommand(position, text)`
  - `DeleteTextCommand(position, length)`
  - `ReplaceTextCommand(position, length, newText)`
- `TextEditor` class with:
  - Internal text buffer
  - `execute(command)` — execute and push to undo stack
  - `undo()` — pop from undo stack, push to redo stack
  - `redo()` — pop from redo stack, push to undo stack
  - `getText()` — return current text

**Expected:**
```
editor = TextEditor()
editor.execute(InsertTextCommand(0, "Hello World"))  # "Hello World"
editor.execute(InsertTextCommand(5, ","))             # "Hello, World"
editor.execute(ReplaceTextCommand(7, 5, "Go"))        # "Hello, Go"
editor.undo()                                          # "Hello, World"
editor.undo()                                          # "Hello World"
editor.redo()                                          # "Hello, World"
```

---

### Task 15: Benchmark — Interface Dispatch vs Direct Call

Write a benchmark comparing:
1. **Direct method call** on a concrete type
2. **Interface method call** (virtual dispatch)
3. **Reflection-based call** (where applicable)

For each, call a simple `Area()` method 10 million times and measure the duration.

**Go:**
```go
package main

import (
    "fmt"
    "math"
    "reflect"
    "time"
)

type Shape interface {
    Area() float64
}

type Circle struct {
    Radius float64
}

func (c Circle) Area() float64 {
    return math.Pi * c.Radius * c.Radius
}

func BenchmarkDirect(c Circle, iterations int) time.Duration {
    start := time.Now()
    var result float64
    for i := 0; i < iterations; i++ {
        result = c.Area()
    }
    _ = result
    return time.Since(start)
}

func BenchmarkInterface(s Shape, iterations int) time.Duration {
    start := time.Now()
    var result float64
    for i := 0; i < iterations; i++ {
        result = s.Area()
    }
    _ = result
    return time.Since(start)
}

func BenchmarkReflection(obj any, iterations int) time.Duration {
    v := reflect.ValueOf(obj)
    method := v.MethodByName("Area")
    start := time.Now()
    for i := 0; i < iterations; i++ {
        method.Call(nil)
    }
    return time.Since(start)
}

func main() {
    c := Circle{Radius: 5.0}
    n := 10_000_000

    fmt.Printf("Direct call:     %v\n", BenchmarkDirect(c, n))
    fmt.Printf("Interface call:  %v\n", BenchmarkInterface(c, n))
    fmt.Printf("Reflection call: %v\n", BenchmarkReflection(c, n))
}
```

**Java:**
```java
import java.lang.reflect.Method;

public class Benchmark {
    interface Shape { double area(); }

    static class Circle implements Shape {
        final double radius;
        Circle(double radius) { this.radius = radius; }
        public double area() { return Math.PI * radius * radius; }
    }

    public static void main(String[] args) throws Exception {
        Circle c = new Circle(5.0);
        Shape s = c;
        int n = 10_000_000;

        // Warmup
        for (int i = 0; i < 1_000_000; i++) { c.area(); s.area(); }

        // Direct call
        long start = System.nanoTime();
        double result = 0;
        for (int i = 0; i < n; i++) result = c.area();
        long directTime = System.nanoTime() - start;

        // Interface call
        start = System.nanoTime();
        for (int i = 0; i < n; i++) result = s.area();
        long interfaceTime = System.nanoTime() - start;

        // Reflection call
        Method method = Circle.class.getMethod("area");
        start = System.nanoTime();
        for (int i = 0; i < n; i++) method.invoke(c);
        long reflectionTime = System.nanoTime() - start;

        System.out.printf("Direct:     %d ms%n", directTime / 1_000_000);
        System.out.printf("Interface:  %d ms%n", interfaceTime / 1_000_000);
        System.out.printf("Reflection: %d ms%n", reflectionTime / 1_000_000);
    }
}
```

**Python:**
```python
import time
import math
from abc import ABC, abstractmethod

class Shape(ABC):
    @abstractmethod
    def area(self) -> float: pass

class Circle(Shape):
    def __init__(self, radius: float):
        self.radius = radius
    def area(self) -> float:
        return math.pi * self.radius ** 2

def benchmark_direct(c: Circle, n: int) -> float:
    start = time.perf_counter()
    for _ in range(n):
        c.area()
    return time.perf_counter() - start

def benchmark_interface(s: Shape, n: int) -> float:
    start = time.perf_counter()
    for _ in range(n):
        s.area()
    return time.perf_counter() - start

def benchmark_getattr(obj, n: int) -> float:
    start = time.perf_counter()
    method = getattr(obj, "area")
    for _ in range(n):
        method()
    return time.perf_counter() - start

c = Circle(5.0)
n = 10_000_000

print(f"Direct call:     {benchmark_direct(c, n):.3f}s")
print(f"Interface call:  {benchmark_interface(c, n):.3f}s")
print(f"getattr call:    {benchmark_getattr(c, n):.3f}s")
```

**Expected relative performance:**
```
Go:
  Direct ~20ms, Interface ~25ms, Reflection ~500ms

Java (after JIT warmup):
  Direct ~15ms, Interface ~15ms (JIT devirtualizes), Reflection ~200ms

Python:
  Direct ~2.5s, Interface ~2.5s (same mechanism), getattr ~2.6s
```

---

## Submission Guidelines

For each task:
1. Implement in all 3 languages (Go, Java, Python)
2. Write at least 3 test cases
3. Handle edge cases (empty input, invalid values, boundary conditions)
4. Add comments explaining your design decisions
5. For advanced tasks, include a brief design document explaining your class hierarchy and why you chose it
