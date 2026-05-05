# OOP Basics - Middle Level

## Interfaces

An **interface** defines a contract — a set of methods that a type must implement. Interfaces enable **polymorphism**: different types can be used interchangeably if they satisfy the same interface.

### Go — Implicit Interfaces

Go interfaces are satisfied **implicitly**. A type implements an interface simply by having the required methods — no `implements` keyword needed.

```go
package main

import (
    "fmt"
    "math"
)

// Interface definition
type Shape interface {
    Area() float64
    Perimeter() float64
}

// Stringer interface (from fmt package)
// type Stringer interface {
//     String() string
// }

type Rectangle struct {
    Width, Height float64
}

func (r Rectangle) Area() float64 {
    return r.Width * r.Height
}

func (r Rectangle) Perimeter() float64 {
    return 2 * (r.Width + r.Height)
}

func (r Rectangle) String() string {
    return fmt.Sprintf("Rectangle(%.1f x %.1f)", r.Width, r.Height)
}

type Circle struct {
    Radius float64
}

func (c Circle) Area() float64 {
    return math.Pi * c.Radius * c.Radius
}

func (c Circle) Perimeter() float64 {
    return 2 * math.Pi * c.Radius
}

func (c Circle) String() string {
    return fmt.Sprintf("Circle(r=%.1f)", c.Radius)
}

// Function accepting interface — works with ANY Shape
func PrintShapeInfo(s Shape) {
    fmt.Printf("Shape: %v\n", s)
    fmt.Printf("  Area:      %.2f\n", s.Area())
    fmt.Printf("  Perimeter: %.2f\n", s.Perimeter())
}

func TotalArea(shapes []Shape) float64 {
    total := 0.0
    for _, s := range shapes {
        total += s.Area()
    }
    return total
}

func main() {
    shapes := []Shape{
        Rectangle{Width: 5, Height: 3},
        Circle{Radius: 4},
        Rectangle{Width: 10, Height: 2},
    }

    for _, s := range shapes {
        PrintShapeInfo(s)
    }

    fmt.Printf("Total area: %.2f\n", TotalArea(shapes))
}
```

### Java — Explicit Interfaces

Java requires the `implements` keyword.

```java
// Interface definition
public interface Shape {
    double area();
    double perimeter();

    // Default method (Java 8+) — provides a default implementation
    default String describe() {
        return String.format("Area=%.2f, Perimeter=%.2f", area(), perimeter());
    }

    // Static method in interface
    static double totalArea(Shape[] shapes) {
        double total = 0;
        for (Shape s : shapes) {
            total += s.area();
        }
        return total;
    }
}

public class Rectangle implements Shape {
    private double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public String toString() {
        return String.format("Rectangle(%.1f x %.1f)", width, height);
    }
}

public class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String toString() {
        return String.format("Circle(r=%.1f)", radius);
    }
}

// Usage
public class Main {
    public static void printShapeInfo(Shape s) {
        System.out.println("Shape: " + s);
        System.out.println("  " + s.describe());
    }

    public static void main(String[] args) {
        Shape[] shapes = {
            new Rectangle(5, 3),
            new Circle(4),
            new Rectangle(10, 2)
        };

        for (Shape s : shapes) {
            printShapeInfo(s);
        }

        System.out.printf("Total area: %.2f%n", Shape.totalArea(shapes));
    }
}
```

### Python — ABC (Abstract Base Classes)

Python uses `abc` module for formal interfaces, but also supports duck typing.

```python
from abc import ABC, abstractmethod
import math

# Abstract base class (like an interface)
class Shape(ABC):
    @abstractmethod
    def area(self) -> float:
        """Calculate the area of the shape."""
        pass

    @abstractmethod
    def perimeter(self) -> float:
        """Calculate the perimeter of the shape."""
        pass

    # Concrete method (default implementation)
    def describe(self) -> str:
        return f"Area={self.area():.2f}, Perimeter={self.perimeter():.2f}"


class Rectangle(Shape):
    def __init__(self, width: float, height: float):
        self.width = width
        self.height = height

    def area(self) -> float:
        return self.width * self.height

    def perimeter(self) -> float:
        return 2 * (self.width + self.height)

    def __repr__(self) -> str:
        return f"Rectangle({self.width:.1f} x {self.height:.1f})"


class Circle(Shape):
    def __init__(self, radius: float):
        self.radius = radius

    def area(self) -> float:
        return math.pi * self.radius ** 2

    def perimeter(self) -> float:
        return 2 * math.pi * self.radius

    def __repr__(self) -> str:
        return f"Circle(r={self.radius:.1f})"


# s = Shape()  # TypeError: Can't instantiate abstract class

def print_shape_info(s: Shape) -> None:
    print(f"Shape: {s}")
    print(f"  {s.describe()}")

def total_area(shapes: list[Shape]) -> float:
    return sum(s.area() for s in shapes)


# Usage
shapes: list[Shape] = [
    Rectangle(5, 3),
    Circle(4),
    Rectangle(10, 2),
]

for s in shapes:
    print_shape_info(s)

print(f"Total area: {total_area(shapes):.2f}")
```

---

## Composition vs Inheritance

### The Problem with Deep Inheritance

```
Animal
  └── Bird
        └── FlyingBird
              └── Eagle
                    └── BaldEagle
```

This creates **tight coupling**, **fragile base class problem**, and makes it hard to add new behaviors (what about a bird that swims AND flies?).

### Go's Philosophy: Composition Over Inheritance

Go intentionally **omits inheritance**. Instead, it uses **embedding** (composition):

```go
// Composition: build types by combining smaller types

type Engine struct {
    Horsepower int
    Type       string
}

func (e Engine) Start() string {
    return fmt.Sprintf("%s engine (%d HP) started", e.Type, e.Horsepower)
}

func (e Engine) Stop() string {
    return "Engine stopped"
}

type GPS struct {
    Latitude, Longitude float64
}

func (g GPS) CurrentLocation() string {
    return fmt.Sprintf("(%.4f, %.4f)", g.Latitude, g.Longitude)
}

// Car embeds Engine and GPS — it "has" them, not "is" them
type Car struct {
    Engine    // Embedded — Car gets Engine's methods
    GPS       // Embedded — Car gets GPS's methods
    Brand string
    Model string
}

func main() {
    car := Car{
        Engine: Engine{Horsepower: 200, Type: "V6"},
        GPS:    GPS{Latitude: 41.0082, Longitude: 28.9784},
        Brand:  "Toyota",
        Model:  "Camry",
    }

    // Car directly accesses embedded methods
    fmt.Println(car.Start())           // V6 engine (200 HP) started
    fmt.Println(car.CurrentLocation()) // (41.0082, 28.9784)
    fmt.Println(car.Stop())            // Engine stopped
}
```

### Java: Inheritance vs Composition

```java
// BAD: Deep inheritance
class Vehicle { }
class MotorVehicle extends Vehicle { }
class Car extends MotorVehicle { }
class ElectricCar extends Car { } // What if we want a hybrid?

// GOOD: Composition
class Engine {
    private int horsepower;
    private String type;

    public Engine(int horsepower, String type) {
        this.horsepower = horsepower;
        this.type = type;
    }

    public String start() {
        return type + " engine (" + horsepower + " HP) started";
    }
}

class GPS {
    private double latitude, longitude;

    public GPS(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public String currentLocation() {
        return String.format("(%.4f, %.4f)", latitude, longitude);
    }
}

class Car {
    private Engine engine;    // HAS-A Engine
    private GPS gps;          // HAS-A GPS
    private String brand;

    public Car(Engine engine, GPS gps, String brand) {
        this.engine = engine;
        this.gps = gps;
        this.brand = brand;
    }

    public String start() {
        return engine.start();
    }

    public String location() {
        return gps.currentLocation();
    }
}
```

### Python: Composition

```python
class Engine:
    def __init__(self, horsepower: int, engine_type: str):
        self.horsepower = horsepower
        self.engine_type = engine_type

    def start(self) -> str:
        return f"{self.engine_type} engine ({self.horsepower} HP) started"


class GPS:
    def __init__(self, latitude: float, longitude: float):
        self.latitude = latitude
        self.longitude = longitude

    def current_location(self) -> str:
        return f"({self.latitude:.4f}, {self.longitude:.4f})"


class Car:
    def __init__(self, engine: Engine, gps: GPS, brand: str):
        self.engine = engine  # HAS-A
        self.gps = gps        # HAS-A
        self.brand = brand

    def start(self) -> str:
        return self.engine.start()

    def location(self) -> str:
        return self.gps.current_location()


# Usage
car = Car(
    engine=Engine(200, "V6"),
    gps=GPS(41.0082, 28.9784),
    brand="Toyota"
)
print(car.start())     # V6 engine (200 HP) started
print(car.location())  # (41.0082, 28.9784)
```

### When to Use Inheritance vs Composition

| Use Inheritance When...                  | Use Composition When...                    |
|------------------------------------------|--------------------------------------------|
| True "is-a" relationship                 | "Has-a" relationship                       |
| Sharing implementation in a hierarchy    | Combining behaviors from multiple sources  |
| Single level of inheritance              | Flexibility to change behavior at runtime  |
| Template Method pattern                  | Strategy pattern                           |

**Rule of thumb**: Prefer composition. Use inheritance only for 1-2 levels max.

---

## Polymorphism Through Interfaces

Polymorphism means "many forms" — the same interface, different implementations.

### Go

```go
type Logger interface {
    Log(message string)
}

type ConsoleLogger struct{}
func (cl ConsoleLogger) Log(message string) {
    fmt.Println("[CONSOLE]", message)
}

type FileLogger struct {
    filename string
}
func (fl FileLogger) Log(message string) {
    fmt.Printf("[FILE:%s] %s\n", fl.filename, message)
}

type JSONLogger struct{}
func (jl JSONLogger) Log(message string) {
    fmt.Printf(`{"level":"info","message":"%s"}`+"\n", message)
}

// Works with ANY Logger — polymorphism
func ProcessOrder(logger Logger, orderID string) {
    logger.Log("Processing order: " + orderID)
    // ... business logic ...
    logger.Log("Order completed: " + orderID)
}

func main() {
    ProcessOrder(ConsoleLogger{}, "ORD-001")
    ProcessOrder(FileLogger{filename: "app.log"}, "ORD-002")
    ProcessOrder(JSONLogger{}, "ORD-003")
}
```

### Java

```java
public interface Logger {
    void log(String message);
}

public class ConsoleLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println("[CONSOLE] " + message);
    }
}

public class FileLogger implements Logger {
    private String filename;

    public FileLogger(String filename) {
        this.filename = filename;
    }

    @Override
    public void log(String message) {
        System.out.printf("[FILE:%s] %s%n", filename, message);
    }
}

// Polymorphism: accepts any Logger implementation
public class OrderService {
    private Logger logger;

    public OrderService(Logger logger) {
        this.logger = logger;
    }

    public void processOrder(String orderID) {
        logger.log("Processing order: " + orderID);
        // ... business logic ...
        logger.log("Order completed: " + orderID);
    }
}

// Usage
OrderService service1 = new OrderService(new ConsoleLogger());
OrderService service2 = new OrderService(new FileLogger("app.log"));
service1.processOrder("ORD-001");
service2.processOrder("ORD-002");
```

### Python

```python
from abc import ABC, abstractmethod

class Logger(ABC):
    @abstractmethod
    def log(self, message: str) -> None:
        pass

class ConsoleLogger(Logger):
    def log(self, message: str) -> None:
        print(f"[CONSOLE] {message}")

class FileLogger(Logger):
    def __init__(self, filename: str):
        self.filename = filename

    def log(self, message: str) -> None:
        print(f"[FILE:{self.filename}] {message}")

class JSONLogger(Logger):
    def log(self, message: str) -> None:
        print(f'{{"level":"info","message":"{message}"}}')


def process_order(logger: Logger, order_id: str) -> None:
    logger.log(f"Processing order: {order_id}")
    # ... business logic ...
    logger.log(f"Order completed: {order_id}")


# Polymorphism in action
process_order(ConsoleLogger(), "ORD-001")
process_order(FileLogger("app.log"), "ORD-002")
process_order(JSONLogger(), "ORD-003")
```

---

## Method Overriding and Method Overloading

### Method Overriding (All Languages)

Overriding = subclass provides its own implementation of a parent method.

**Java:**
```java
class Animal {
    public String speak() {
        return "...";
    }
}

class Dog extends Animal {
    @Override
    public String speak() {
        return "Woof!";
    }
}

class Cat extends Animal {
    @Override
    public String speak() {
        return "Meow!";
    }
}

Animal a = new Dog();
System.out.println(a.speak()); // "Woof!" — runtime polymorphism
```

**Python:**
```python
class Animal:
    def speak(self) -> str:
        return "..."

class Dog(Animal):
    def speak(self) -> str:
        return "Woof!"

class Cat(Animal):
    def speak(self) -> str:
        return "Meow!"

a: Animal = Dog()
print(a.speak())  # "Woof!"
```

**Go (via interface):**
```go
type Speaker interface {
    Speak() string
}

type Dog struct{}
func (d Dog) Speak() string { return "Woof!" }

type Cat struct{}
func (c Cat) Speak() string { return "Meow!" }

var s Speaker = Dog{}
fmt.Println(s.Speak()) // "Woof!"
```

### Method Overloading (Java Only)

Overloading = same method name, different parameter lists. **Only Java** supports this natively.

```java
public class Calculator {
    // Same name, different parameter types
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }

    public int add(int a, int b, int c) {
        return a + b + c;
    }

    public String add(String a, String b) {
        return a + b;
    }
}

Calculator calc = new Calculator();
System.out.println(calc.add(1, 2));       // 3 (int version)
System.out.println(calc.add(1.5, 2.5));   // 4.0 (double version)
System.out.println(calc.add(1, 2, 3));    // 6 (three-arg version)
System.out.println(calc.add("a", "b"));   // "ab" (String version)
```

**Go alternative — variadic functions or different names:**
```go
func Add(nums ...int) int {
    total := 0
    for _, n := range nums {
        total += n
    }
    return total
}

// Or use different function names (Go convention)
func AddInts(a, b int) int         { return a + b }
func AddFloats(a, b float64) float64 { return a + b }
```

**Python alternative — default arguments and *args:**
```python
def add(*args):
    """Handles variable number of arguments."""
    return sum(args)

# Or with type checking (less Pythonic)
def add_values(a, b):
    return a + b  # Works for int, float, str due to duck typing

print(add(1, 2))        # 3
print(add(1, 2, 3))     # 6
print(add_values("a", "b"))  # "ab"
```

---

## Embedding (Go) vs Inheritance (Java/Python)

### Go Embedding

Embedding promotes methods of the embedded type to the outer type. It is **not** inheritance — there is no "is-a" relationship.

```go
type Animal struct {
    Name string
}

func (a Animal) Describe() string {
    return "Animal: " + a.Name
}

type Dog struct {
    Animal // Embedded — Dog "has" an Animal
    Breed  string
}

// Dog can override by defining its own method
func (d Dog) Describe() string {
    return fmt.Sprintf("Dog: %s (%s)", d.Name, d.Breed)
}

// Dog can still access the embedded type's method
func (d Dog) BaseDescribe() string {
    return d.Animal.Describe() // Explicit access to embedded method
}

func main() {
    d := Dog{
        Animal: Animal{Name: "Rex"},
        Breed:  "Labrador",
    }

    fmt.Println(d.Name)            // "Rex" — promoted field
    fmt.Println(d.Describe())      // "Dog: Rex (Labrador)" — Dog's method
    fmt.Println(d.BaseDescribe())  // "Animal: Rex" — embedded method
}
```

### Java Inheritance

```java
class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public String describe() {
        return "Animal: " + name;
    }
}

class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);  // Call parent constructor
        this.breed = breed;
    }

    @Override
    public String describe() {
        return String.format("Dog: %s (%s)", name, breed);
    }

    public String baseDescribe() {
        return super.describe();  // Call parent method
    }
}
```

### Python Inheritance

```python
class Animal:
    def __init__(self, name: str):
        self.name = name

    def describe(self) -> str:
        return f"Animal: {self.name}"


class Dog(Animal):
    def __init__(self, name: str, breed: str):
        super().__init__(name)  # Call parent constructor
        self.breed = breed

    def describe(self) -> str:
        return f"Dog: {self.name} ({self.breed})"

    def base_describe(self) -> str:
        return super().describe()  # Call parent method
```

### Key Differences

| Feature             | Go (Embedding)         | Java (Inheritance)     | Python (Inheritance)    |
|---------------------|------------------------|------------------------|-------------------------|
| Syntax              | `type Dog struct{Animal}` | `class Dog extends Animal` | `class Dog(Animal)` |
| Relationship        | Has-A (composition)    | Is-A (inheritance)     | Is-A (inheritance)      |
| Parent constructor  | Initialize in struct literal | `super()`         | `super().__init__()`    |
| Access parent method| `d.Animal.Method()`    | `super.method()`       | `super().method()`      |
| Multiple            | Multiple embeddings OK | Single inheritance only| Multiple inheritance OK |
| Type assertion      | Not a parent type      | `Dog instanceof Animal`| `isinstance(dog, Animal)`|

---

## Abstract Classes

An abstract class provides partial implementation — some methods are concrete, some are abstract (must be overridden).

### Java

```java
public abstract class DatabaseConnection {
    protected String connectionString;

    // Constructor (abstract classes can have constructors)
    public DatabaseConnection(String connectionString) {
        this.connectionString = connectionString;
    }

    // Abstract methods (must be implemented by subclasses)
    public abstract void connect();
    public abstract void disconnect();
    public abstract List<Map<String, Object>> query(String sql);

    // Concrete method (shared implementation)
    public void executeWithRetry(String sql, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                query(sql);
                return;
            } catch (Exception e) {
                System.out.println("Retry " + (i + 1) + ": " + e.getMessage());
            }
        }
        throw new RuntimeException("Max retries exceeded");
    }
}

public class PostgresConnection extends DatabaseConnection {
    public PostgresConnection(String connStr) {
        super(connStr);
    }

    @Override
    public void connect() {
        System.out.println("Connecting to PostgreSQL: " + connectionString);
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting from PostgreSQL");
    }

    @Override
    public List<Map<String, Object>> query(String sql) {
        System.out.println("Executing PostgreSQL query: " + sql);
        return List.of(); // Simplified
    }
}
```

### Python

```python
from abc import ABC, abstractmethod
from typing import Any

class DatabaseConnection(ABC):
    def __init__(self, connection_string: str):
        self.connection_string = connection_string

    @abstractmethod
    def connect(self) -> None:
        pass

    @abstractmethod
    def disconnect(self) -> None:
        pass

    @abstractmethod
    def query(self, sql: str) -> list[dict[str, Any]]:
        pass

    # Concrete method (shared implementation)
    def execute_with_retry(self, sql: str, max_retries: int = 3) -> list[dict]:
        for i in range(max_retries):
            try:
                return self.query(sql)
            except Exception as e:
                print(f"Retry {i + 1}: {e}")
        raise RuntimeError("Max retries exceeded")


class PostgresConnection(DatabaseConnection):
    def connect(self) -> None:
        print(f"Connecting to PostgreSQL: {self.connection_string}")

    def disconnect(self) -> None:
        print("Disconnecting from PostgreSQL")

    def query(self, sql: str) -> list[dict[str, Any]]:
        print(f"Executing PostgreSQL query: {sql}")
        return []
```

### Go — No Abstract Classes, Use Interfaces + Embedding

Go doesn't have abstract classes. The idiomatic approach is to use interfaces and embed a base struct.

```go
// Interface defines the contract
type DatabaseConnection interface {
    Connect()
    Disconnect()
    Query(sql string) ([]map[string]any, error)
}

// Base struct with shared behavior
type BaseConnection struct {
    ConnectionString string
}

// Shared method (works on the base)
func ExecuteWithRetry(db DatabaseConnection, sql string, maxRetries int) ([]map[string]any, error) {
    var lastErr error
    for i := 0; i < maxRetries; i++ {
        result, err := db.Query(sql)
        if err == nil {
            return result, nil
        }
        lastErr = err
        fmt.Printf("Retry %d: %s\n", i+1, err)
    }
    return nil, fmt.Errorf("max retries exceeded: %w", lastErr)
}

// Concrete implementation embeds BaseConnection
type PostgresConnection struct {
    BaseConnection // Embed base struct
}

func NewPostgresConnection(connStr string) *PostgresConnection {
    return &PostgresConnection{
        BaseConnection: BaseConnection{ConnectionString: connStr},
    }
}

func (p *PostgresConnection) Connect() {
    fmt.Println("Connecting to PostgreSQL:", p.ConnectionString)
}

func (p *PostgresConnection) Disconnect() {
    fmt.Println("Disconnecting from PostgreSQL")
}

func (p *PostgresConnection) Query(sql string) ([]map[string]any, error) {
    fmt.Println("Executing PostgreSQL query:", sql)
    return nil, nil
}
```

---

## Generics / Type Parameters

### Go (1.18+)

```go
// Generic Stack
type Stack[T any] struct {
    items []T
}

func (s *Stack[T]) Push(item T) {
    s.items = append(s.items, item)
}

func (s *Stack[T]) Pop() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }
    item := s.items[len(s.items)-1]
    s.items = s.items[:len(s.items)-1]
    return item, true
}

func (s *Stack[T]) Peek() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }
    return s.items[len(s.items)-1], true
}

func (s *Stack[T]) Size() int {
    return len(s.items)
}

// Constrained generic
type Numeric interface {
    ~int | ~int64 | ~float64
}

func Sum[T Numeric](values []T) T {
    var total T
    for _, v := range values {
        total += v
    }
    return total
}

// Usage
intStack := Stack[int]{}
intStack.Push(1)
intStack.Push(2)
val, _ := intStack.Pop() // val = 2

strStack := Stack[string]{}
strStack.Push("hello")

fmt.Println(Sum([]int{1, 2, 3}))         // 6
fmt.Println(Sum([]float64{1.5, 2.5}))    // 4.0
```

### Java

```java
public class Stack<T> {
    private List<T> items = new ArrayList<>();

    public void push(T item) {
        items.add(item);
    }

    public T pop() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return items.remove(items.size() - 1);
    }

    public T peek() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return items.get(items.size() - 1);
    }

    public int size() {
        return items.size();
    }
}

// Bounded generics
public class MathUtils {
    public static <T extends Number & Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }
}

// Usage
Stack<Integer> intStack = new Stack<>();
intStack.push(1);
intStack.push(2);
int val = intStack.pop(); // 2

Stack<String> strStack = new Stack<>();
strStack.push("hello");

System.out.println(MathUtils.max(3, 7));     // 7
System.out.println(MathUtils.max(3.14, 2.71)); // 3.14
```

### Python

```python
from typing import TypeVar, Generic

T = TypeVar("T")

class Stack(Generic[T]):
    def __init__(self) -> None:
        self._items: list[T] = []

    def push(self, item: T) -> None:
        self._items.append(item)

    def pop(self) -> T:
        if not self._items:
            raise IndexError("Stack is empty")
        return self._items.pop()

    def peek(self) -> T:
        if not self._items:
            raise IndexError("Stack is empty")
        return self._items[-1]

    def size(self) -> int:
        return len(self._items)

    def __repr__(self) -> str:
        return f"Stack({self._items})"


# Usage (type hints are not enforced at runtime)
int_stack: Stack[int] = Stack()
int_stack.push(1)
int_stack.push(2)
val = int_stack.pop()  # 2

str_stack: Stack[str] = Stack()
str_stack.push("hello")

# Python 3.12+ syntax (simpler)
# class Stack[T]:
#     def __init__(self) -> None:
#         self._items: list[T] = []
```

---

## Design Considerations

### When to Use Interface vs Abstract Class vs Concrete Class

```
                    ┌──────────────────────────────────────────────┐
                    │            Decision Tree                     │
                    └──────────────────────────────────────────────┘
                                      │
                      Do you need to define a contract?
                         /                    \
                       Yes                     No
                        │                       │
              Do you need shared               Use a
              implementation?                  concrete class
                /            \
              Yes             No
               │               │
        Abstract Class     Interface
               │
     Do subtypes share
     most behavior?
        /          \
      Yes           No
       │             │
   Abstract Class  Interface +
   (Template       Composition
    Method)
```

### Interface
- **Use when**: You need a contract with no shared implementation
- **Go**: Always use interfaces (they're the only abstraction mechanism)
- **Java**: Use when classes from different hierarchies need the same contract
- **Python**: Use ABC when you need to enforce method implementation

### Abstract Class (Java/Python only)
- **Use when**: You have shared code AND a contract
- **Template Method pattern**: Define algorithm skeleton, let subclasses fill in steps
- **Go**: Simulate with interface + helper functions + embedded structs

### Concrete Class
- **Use when**: No variation needed, simple data containers

---

## Key Takeaways

1. **Interfaces** define contracts — Go uses implicit satisfaction, Java uses `implements`, Python uses ABC
2. **Composition > Inheritance** — especially in Go, which enforces this philosophy
3. **Polymorphism through interfaces** allows writing flexible, testable code
4. **Method overriding** exists in all three languages; **overloading** is Java-specific
5. **Generics** allow type-safe reusable code in all three languages
6. Choose between interface, abstract class, and concrete class based on how much shared behavior and how much contract enforcement you need
