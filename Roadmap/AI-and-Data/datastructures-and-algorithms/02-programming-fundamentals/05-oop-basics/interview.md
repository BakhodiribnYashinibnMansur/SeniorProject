# OOP Basics - Interview Preparation

## Junior Level Questions

### Q1: What are the 4 pillars of OOP?

**Answer:**

1. **Encapsulation** — Bundling data and methods together, hiding internal state behind a public interface. Example: a BankAccount class hides the balance field and only exposes deposit/withdraw methods.

2. **Abstraction** — Hiding complexity and showing only essential features. Example: you call `file.read()` without knowing whether it reads from disk, network, or memory.

3. **Inheritance** — Creating new types based on existing ones, reusing behavior. A `Dog` class inherits from `Animal`. (Go uses composition/embedding instead.)

4. **Polymorphism** — Different types responding to the same interface. A function accepting a `Shape` can work with `Circle`, `Rectangle`, or `Triangle` — each computes `area()` differently.

---

### Q2: What is the difference between a class and an object?

**Answer:**

- A **class** is a blueprint/template that defines the structure (fields) and behavior (methods) of a type.
- An **object** (instance) is a specific realization of that class in memory with actual values.

```java
// Class = blueprint
class Dog {
    String name;
    public Dog(String name) { this.name = name; }
    public void bark() { System.out.println(name + ": Woof!"); }
}

// Objects = instances
Dog rex = new Dog("Rex");     // object 1
Dog buddy = new Dog("Buddy"); // object 2
rex.bark();   // "Rex: Woof!"
buddy.bark(); // "Buddy: Woof!"
```

**In Go**, there are no classes — structs are value types, and you create instances with struct literals or constructor functions.

---

### Q3: What is a constructor? What happens if you don't define one?

**Answer:**

A constructor initializes a new object.

- **Java**: If you don't define one, Java provides a default no-arg constructor. If you define any constructor, the default disappears.
- **Python**: `__init__` is the initializer. If omitted, `object.__init__` is used (does nothing).
- **Go**: No constructors. Convention is to write `NewTypeName()` factory functions.

```java
// Java
public class User {
    private String name;
    // No constructor defined → Java provides: public User() {}
}

User u = new User(); // Works — default constructor
// u.name is null
```

```python
# Python
class User:
    pass  # No __init__ — works, but instance has no attributes

u = User()
```

---

### Q4: Explain encapsulation with a practical example.

**Answer:**

Encapsulation means controlling access to internal data through methods.

```python
# Without encapsulation — dangerous
class BankAccount:
    def __init__(self):
        self.balance = 0

account = BankAccount()
account.balance = -1000  # Anyone can set invalid state!

# With encapsulation — safe
class BankAccount:
    def __init__(self):
        self._balance = 0

    def deposit(self, amount: float) -> None:
        if amount <= 0:
            raise ValueError("Amount must be positive")
        self._balance += amount

    def withdraw(self, amount: float) -> bool:
        if amount <= 0 or amount > self._balance:
            return False
        self._balance -= amount
        return True

    @property
    def balance(self) -> float:
        return self._balance  # Read-only access
```

---

### Q5: What is `this` / `self` / receiver? Why is it needed?

**Answer:**

It's a reference to the current instance, allowing a method to access its own fields and other methods.

- **Java**: `this` is implicit (automatically available)
- **Python**: `self` is explicit (first parameter of every instance method)
- **Go**: receiver variable (named by convention, e.g., `s` for `Server`)

```go
type Server struct {
    port int
}

func (s *Server) Start() {
    fmt.Printf("Starting on port %d\n", s.port) // 's' is the receiver
}
```

Without it, methods wouldn't know which instance's data to operate on.

---

## Middle Level Questions

### Q6: Interface vs Abstract Class — when to use which?

**Answer:**

| Aspect            | Interface                         | Abstract Class                    |
|-------------------|-----------------------------------|-----------------------------------|
| Implementation    | No state, only method signatures  | Can have state and partial implementation |
| Multiple          | A class can implement multiple    | Java: single inheritance only     |
| Use when          | Defining a contract               | Sharing code between related classes |
| Coupling          | Low coupling                      | Higher coupling                   |
| Go equivalent     | `interface` (only option)         | Interface + embedded base struct  |

**Rule of thumb**: Start with an interface. Use an abstract class only when you need shared state or implementation across a family of related types.

```java
// Interface: when unrelated types share behavior
interface Serializable {
    byte[] serialize();
}
// User, Order, Product can all implement Serializable

// Abstract class: when related types share implementation
abstract class HttpHandler {
    protected final Logger logger;

    public HttpHandler(Logger logger) {
        this.logger = logger;
    }

    // Shared behavior
    public void handle(Request req, Response res) {
        logger.info("Handling: " + req.getPath());
        doHandle(req, res);  // Template Method
    }

    protected abstract void doHandle(Request req, Response res);
}
```

---

### Q7: Explain composition vs inheritance. When would you prefer each?

**Answer:**

- **Inheritance** (IS-A): `Dog extends Animal` — Dog IS an Animal
- **Composition** (HAS-A): `Car has an Engine` — Car HAS an Engine

**Prefer composition when:**
- You need behavior from multiple sources
- The relationship is "has-a" or "uses-a"
- You want to change behavior at runtime
- You want loose coupling

**Use inheritance when:**
- True "is-a" relationship with single level
- Template Method pattern

```go
// Go enforces composition — no inheritance available
type Logger struct{ prefix string }
func (l Logger) Log(msg string) { fmt.Printf("[%s] %s\n", l.prefix, msg) }

type Cache struct{ data map[string]any }
func (c *Cache) Get(key string) any { return c.data[key] }

type Service struct {
    Logger  // embedded: Service HAS a Logger
    cache *Cache
}

// Service can directly call s.Log("hello") — promoted method
```

---

### Q8: What are the SOLID principles? Give a one-line summary of each.

**Answer:**

1. **S** — Single Responsibility: A class should have only one reason to change
2. **O** — Open/Closed: Open for extension, closed for modification
3. **L** — Liskov Substitution: Subtypes must be replaceable for base types
4. **I** — Interface Segregation: Don't force clients to depend on unused methods
5. **D** — Dependency Inversion: Depend on abstractions, not concrete implementations

---

### Q9: What is the difference between method overloading and method overriding?

**Answer:**

| Aspect       | Overloading                    | Overriding                      |
|-------------- |-------------------------------|---------------------------------|
| Definition   | Same name, different params   | Same name, same params, in subclass |
| Resolution   | Compile-time (static)         | Runtime (dynamic dispatch)      |
| Java         | Yes                           | Yes                             |
| Python       | No (last definition wins)     | Yes                             |
| Go           | No                            | Via interface + different types  |

```java
// Overloading (compile-time)
class Math {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
}

// Overriding (runtime)
class Animal {
    void speak() { System.out.println("..."); }
}
class Dog extends Animal {
    @Override
    void speak() { System.out.println("Woof"); }
}

Animal a = new Dog();
a.speak(); // "Woof" — resolved at runtime
```

---

## Senior Level Questions

### Q10: Walk through the Strategy pattern. When would you use it?

**Answer:**

Strategy pattern encapsulates interchangeable algorithms behind a common interface.

**Use when:**
- You have multiple ways to perform an operation
- You want to switch algorithms at runtime
- You want to avoid if/else chains for selecting behavior

```go
type Compressor interface {
    Compress(data []byte) ([]byte, error)
}

type GzipCompressor struct{}
func (g GzipCompressor) Compress(data []byte) ([]byte, error) { /* gzip */ return nil, nil }

type ZstdCompressor struct{}
func (z ZstdCompressor) Compress(data []byte) ([]byte, error) { /* zstd */ return nil, nil }

type NoCompressor struct{}
func (n NoCompressor) Compress(data []byte) ([]byte, error) { return data, nil }

type FileUploader struct {
    compressor Compressor
}

func (fu *FileUploader) Upload(data []byte) error {
    compressed, err := fu.compressor.Compress(data)
    if err != nil { return err }
    // upload compressed data...
    _ = compressed
    return nil
}

// Switch strategy based on file size
func chooseCompressor(fileSize int) Compressor {
    if fileSize > 10_000_000 {
        return ZstdCompressor{}
    } else if fileSize > 1000 {
        return GzipCompressor{}
    }
    return NoCompressor{}
}
```

---

### Q11: When should you NOT use OOP?

**Answer:**

1. **Simple scripts/utilities** — a 50-line script doesn't need classes
2. **Pure data transformations** — functional programming (map, filter, reduce) is cleaner
3. **Performance-critical inner loops** — virtual dispatch has overhead; use concrete types
4. **Stateless operations** — pure functions are simpler than stateless classes
5. **When it leads to God objects** — one massive class doing everything
6. **Deep hierarchies** — more than 2-3 levels usually signals a design problem

```python
# BAD: Unnecessary OOP
class StringHelper:
    @staticmethod
    def reverse(s: str) -> str:
        return s[::-1]

result = StringHelper.reverse("hello")

# GOOD: Just a function
def reverse_string(s: str) -> str:
    return s[::-1]

result = reverse_string("hello")
```

---

### Q12: Explain dependency injection. Why does it matter?

**Answer:**

DI means passing dependencies to an object instead of creating them internally.

**Why it matters:**
1. **Testability** — inject mocks in tests
2. **Flexibility** — swap implementations without changing code
3. **Decoupling** — components don't know about each other's internals

```java
// Without DI — hard to test
class OrderService {
    private final PostgresDB db = new PostgresDB(); // hardcoded
    // Can't test without a real database!
}

// With DI — easy to test
class OrderService {
    private final Database db;
    public OrderService(Database db) {
        this.db = db; // injected from outside
    }
}

// In production
new OrderService(new PostgresDB("prod_url"));

// In tests
new OrderService(new InMemoryDB());
```

---

## Professional Level Questions

### Q13: How does a vtable work? Explain virtual dispatch.

**Answer:**

A vtable (virtual method table) is an array of function pointers, one per virtual method. Each class has one vtable shared by all instances.

```
Object layout:
  [vtable pointer] → [method1_ptr, method2_ptr, ...]
  [field1]
  [field2]
  ...

Call resolution:
  obj.method()
  → load obj's vtable pointer
  → index into vtable for method's slot
  → call the function at that address

Cost: one pointer dereference + indirect function call
vs direct call: just a function call (can be inlined)
```

In Go, interface dispatch uses `itab` (interface table) instead of vtable. The itab is computed at the point of interface assignment and cached globally.

---

### Q14: Explain Python's MRO. What is C3 linearization?

**Answer:**

MRO (Method Resolution Order) determines the order in which Python searches classes when resolving a method call.

C3 linearization algorithm guarantees:
- A class appears before its parents
- Parents maintain their declared order
- The result is consistent (no contradictions)

```python
class A: pass
class B(A): pass
class C(A): pass
class D(B, C): pass

# MRO: D → B → C → A → object
# NOT D → B → A → C → A (which would visit A twice)

# super() follows MRO, not the parent class:
class B(A):
    def method(self):
        super().method()  # Calls C.method, NOT A.method!
        # Because in D's MRO, C comes after B
```

If C3 cannot produce a valid linearization (contradictory hierarchy), Python raises `TypeError` at class creation time.

---

### Q15: What is the formal definition of Liskov Substitution Principle?

**Answer:**

If S is a subtype of T, then for every program P that uses objects of type T, the behavior of P is unchanged when objects of type S are used instead.

**Behavioral conditions:**
- Preconditions of S's methods must be equal to or weaker than T's
- Postconditions of S's methods must be equal to or stronger than T's
- Invariants of T must be preserved by S
- No new exceptions that T's clients don't expect

**Classic violation:** Square extending Rectangle — setting width on a Square also changes height, breaking Rectangle's contract that width and height are independent.

---

## Coding Challenges

### Challenge 1: Design a Shape Hierarchy

Design a shape system with `Circle`, `Rectangle`, and `Triangle`. Each shape must compute `area()` and `perimeter()`. Write a function that takes a list of shapes and returns the one with the largest area.

**Go:**
```go
package main

import (
    "fmt"
    "math"
)

type Shape interface {
    Area() float64
    Perimeter() float64
    String() string
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
    return fmt.Sprintf("Circle(r=%.2f)", c.Radius)
}

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
    return fmt.Sprintf("Rectangle(%.2f x %.2f)", r.Width, r.Height)
}

type Triangle struct {
    A, B, C float64 // side lengths
}

func (t Triangle) Area() float64 {
    s := (t.A + t.B + t.C) / 2
    return math.Sqrt(s * (s - t.A) * (s - t.B) * (s - t.C))
}

func (t Triangle) Perimeter() float64 {
    return t.A + t.B + t.C
}

func (t Triangle) String() string {
    return fmt.Sprintf("Triangle(%.2f, %.2f, %.2f)", t.A, t.B, t.C)
}

func LargestShape(shapes []Shape) Shape {
    if len(shapes) == 0 {
        return nil
    }
    largest := shapes[0]
    for _, s := range shapes[1:] {
        if s.Area() > largest.Area() {
            largest = s
        }
    }
    return largest
}

func main() {
    shapes := []Shape{
        Circle{Radius: 5},
        Rectangle{Width: 10, Height: 8},
        Triangle{A: 3, B: 4, C: 5},
    }

    for _, s := range shapes {
        fmt.Printf("%s → Area=%.2f, Perimeter=%.2f\n", s, s.Area(), s.Perimeter())
    }

    largest := LargestShape(shapes)
    fmt.Printf("\nLargest: %s (area=%.2f)\n", largest, largest.Area())
}
```

**Java:**
```java
import java.util.*;

interface Shape {
    double area();
    double perimeter();
}

class Circle implements Shape {
    private final double radius;
    public Circle(double radius) { this.radius = radius; }

    @Override public double area() { return Math.PI * radius * radius; }
    @Override public double perimeter() { return 2 * Math.PI * radius; }
    @Override public String toString() { return String.format("Circle(r=%.2f)", radius); }
}

class Rectangle implements Shape {
    private final double width, height;
    public Rectangle(double w, double h) { this.width = w; this.height = h; }

    @Override public double area() { return width * height; }
    @Override public double perimeter() { return 2 * (width + height); }
    @Override public String toString() { return String.format("Rect(%.2f x %.2f)", width, height); }
}

class Triangle implements Shape {
    private final double a, b, c;
    public Triangle(double a, double b, double c) { this.a = a; this.b = b; this.c = c; }

    @Override
    public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    @Override public double perimeter() { return a + b + c; }
    @Override public String toString() { return String.format("Tri(%.2f,%.2f,%.2f)", a, b, c); }
}

public class ShapeDemo {
    public static Shape largestShape(List<Shape> shapes) {
        return shapes.stream()
            .max(Comparator.comparingDouble(Shape::area))
            .orElse(null);
    }

    public static void main(String[] args) {
        List<Shape> shapes = List.of(
            new Circle(5),
            new Rectangle(10, 8),
            new Triangle(3, 4, 5)
        );

        for (Shape s : shapes) {
            System.out.printf("%s → Area=%.2f, Perimeter=%.2f%n", s, s.area(), s.perimeter());
        }

        Shape largest = largestShape(shapes);
        System.out.printf("%nLargest: %s (area=%.2f)%n", largest, largest.area());
    }
}
```

**Python:**
```python
from abc import ABC, abstractmethod
import math

class Shape(ABC):
    @abstractmethod
    def area(self) -> float: pass

    @abstractmethod
    def perimeter(self) -> float: pass

class Circle(Shape):
    def __init__(self, radius: float):
        self.radius = radius
    def area(self) -> float:
        return math.pi * self.radius ** 2
    def perimeter(self) -> float:
        return 2 * math.pi * self.radius
    def __repr__(self) -> str:
        return f"Circle(r={self.radius:.2f})"

class Rectangle(Shape):
    def __init__(self, width: float, height: float):
        self.width, self.height = width, height
    def area(self) -> float:
        return self.width * self.height
    def perimeter(self) -> float:
        return 2 * (self.width + self.height)
    def __repr__(self) -> str:
        return f"Rect({self.width:.2f} x {self.height:.2f})"

class Triangle(Shape):
    def __init__(self, a: float, b: float, c: float):
        self.a, self.b, self.c = a, b, c
    def area(self) -> float:
        s = (self.a + self.b + self.c) / 2
        return math.sqrt(s * (s - self.a) * (s - self.b) * (s - self.c))
    def perimeter(self) -> float:
        return self.a + self.b + self.c
    def __repr__(self) -> str:
        return f"Tri({self.a:.2f}, {self.b:.2f}, {self.c:.2f})"

def largest_shape(shapes: list[Shape]) -> Shape:
    return max(shapes, key=lambda s: s.area())

# Usage
shapes = [Circle(5), Rectangle(10, 8), Triangle(3, 4, 5)]
for s in shapes:
    print(f"{s} -> Area={s.area():.2f}, Perimeter={s.perimeter():.2f}")
print(f"\nLargest: {largest_shape(shapes)}")
```

---

### Challenge 2: Implement the Observer Pattern

Build an event system where publishers emit events and subscribers react to them.

**Go:**
```go
package main

import "fmt"

type EventType string

const (
    UserCreated EventType = "user.created"
    UserDeleted EventType = "user.deleted"
    OrderPlaced EventType = "order.placed"
)

type Event struct {
    Type EventType
    Data map[string]any
}

type Handler func(Event)

type EventBus struct {
    handlers map[EventType][]Handler
}

func NewEventBus() *EventBus {
    return &EventBus{handlers: make(map[EventType][]Handler)}
}

func (eb *EventBus) Subscribe(eventType EventType, handler Handler) {
    eb.handlers[eventType] = append(eb.handlers[eventType], handler)
}

func (eb *EventBus) Publish(event Event) {
    for _, handler := range eb.handlers[event.Type] {
        handler(event)
    }
}

func main() {
    bus := NewEventBus()

    // Subscribe handlers
    bus.Subscribe(UserCreated, func(e Event) {
        fmt.Printf("[LOG] User created: %v\n", e.Data["name"])
    })
    bus.Subscribe(UserCreated, func(e Event) {
        fmt.Printf("[EMAIL] Welcome email to: %v\n", e.Data["email"])
    })
    bus.Subscribe(OrderPlaced, func(e Event) {
        fmt.Printf("[INVENTORY] Processing order: %v\n", e.Data["order_id"])
    })

    // Publish events
    bus.Publish(Event{
        Type: UserCreated,
        Data: map[string]any{"name": "Alice", "email": "alice@example.com"},
    })
    bus.Publish(Event{
        Type: OrderPlaced,
        Data: map[string]any{"order_id": "ORD-001", "total": 99.99},
    })
}
```

**Java:**
```java
import java.util.*;
import java.util.function.Consumer;

public class EventBus {
    private final Map<String, List<Consumer<Map<String, Object>>>> handlers = new HashMap<>();

    public void subscribe(String eventType, Consumer<Map<String, Object>> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    public void publish(String eventType, Map<String, Object> data) {
        List<Consumer<Map<String, Object>>> list = handlers.getOrDefault(eventType, List.of());
        for (var handler : list) {
            handler.accept(data);
        }
    }

    public static void main(String[] args) {
        EventBus bus = new EventBus();

        bus.subscribe("user.created", data ->
            System.out.println("[LOG] User created: " + data.get("name")));
        bus.subscribe("user.created", data ->
            System.out.println("[EMAIL] Welcome email to: " + data.get("email")));

        bus.publish("user.created", Map.of("name", "Alice", "email", "alice@example.com"));
    }
}
```

**Python:**
```python
from collections import defaultdict
from typing import Any, Callable

class EventBus:
    def __init__(self):
        self._handlers: dict[str, list[Callable]] = defaultdict(list)

    def subscribe(self, event_type: str, handler: Callable[[dict[str, Any]], None]) -> None:
        self._handlers[event_type].append(handler)

    def publish(self, event_type: str, data: dict[str, Any]) -> None:
        for handler in self._handlers[event_type]:
            handler(data)

# Usage
bus = EventBus()

bus.subscribe("user.created", lambda d: print(f"[LOG] User created: {d['name']}"))
bus.subscribe("user.created", lambda d: print(f"[EMAIL] Welcome: {d['email']}"))
bus.subscribe("order.placed", lambda d: print(f"[INVENTORY] Order: {d['order_id']}"))

bus.publish("user.created", {"name": "Alice", "email": "alice@example.com"})
bus.publish("order.placed", {"order_id": "ORD-001", "total": 99.99})
```

---

### Challenge 3: Implement a Simple DI Container

Build a basic dependency injection container that can register and resolve dependencies.

**Go:**
```go
package main

import (
    "fmt"
    "reflect"
    "sync"
)

type Container struct {
    mu        sync.RWMutex
    providers map[reflect.Type]any
}

func NewContainer() *Container {
    return &Container{providers: make(map[reflect.Type]any)}
}

// Register a singleton instance
func Register[T any](c *Container, instance T) {
    c.mu.Lock()
    defer c.mu.Unlock()
    t := reflect.TypeOf((*T)(nil)).Elem()
    c.providers[t] = instance
}

// Resolve a registered dependency
func Resolve[T any](c *Container) (T, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    t := reflect.TypeOf((*T)(nil)).Elem()
    val, ok := c.providers[t]
    if !ok {
        var zero T
        return zero, fmt.Errorf("no provider for type %v", t)
    }
    return val.(T), nil
}

// Example interfaces and implementations
type Logger interface {
    Log(msg string)
}

type ConsoleLogger struct{}

func (cl ConsoleLogger) Log(msg string) {
    fmt.Println("[LOG]", msg)
}

type UserRepository interface {
    FindByID(id int) string
}

type InMemoryUserRepo struct {
    logger Logger
}

func (r *InMemoryUserRepo) FindByID(id int) string {
    r.logger.Log(fmt.Sprintf("Finding user %d", id))
    return fmt.Sprintf("User-%d", id)
}

func main() {
    c := NewContainer()

    // Register dependencies
    Register[Logger](c, ConsoleLogger{})

    // Resolve and wire
    logger, _ := Resolve[Logger](c)
    repo := &InMemoryUserRepo{logger: logger}
    Register[UserRepository](c, repo)

    // Use
    userRepo, _ := Resolve[UserRepository](c)
    fmt.Println(userRepo.FindByID(42))
}
```

**Java:**
```java
import java.util.*;
import java.util.function.Supplier;

public class Container {
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> factories = new HashMap<>();

    public <T> void registerSingleton(Class<T> type, T instance) {
        singletons.put(type, instance);
    }

    public <T> void registerFactory(Class<T> type, Supplier<T> factory) {
        factories.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        // Check singletons first
        Object singleton = singletons.get(type);
        if (singleton != null) return (T) singleton;

        // Then check factories
        Supplier<?> factory = factories.get(type);
        if (factory != null) return (T) factory.get();

        throw new RuntimeException("No provider for " + type.getName());
    }

    public static void main(String[] args) {
        Container c = new Container();

        // Register
        c.registerSingleton(Logger.class, new ConsoleLogger());
        c.registerFactory(UserRepository.class, () -> {
            Logger logger = c.resolve(Logger.class);
            return new InMemoryUserRepo(logger);
        });

        // Resolve and use
        UserRepository repo = c.resolve(UserRepository.class);
        System.out.println(repo.findById(42));
    }
}

interface Logger {
    void log(String msg);
}

class ConsoleLogger implements Logger {
    public void log(String msg) { System.out.println("[LOG] " + msg); }
}

interface UserRepository {
    String findById(int id);
}

class InMemoryUserRepo implements UserRepository {
    private final Logger logger;
    InMemoryUserRepo(Logger logger) { this.logger = logger; }
    public String findById(int id) {
        logger.log("Finding user " + id);
        return "User-" + id;
    }
}
```

**Python:**
```python
from typing import Any, Callable, TypeVar, Type

T = TypeVar("T")

class Container:
    def __init__(self):
        self._singletons: dict[type, Any] = {}
        self._factories: dict[type, Callable] = {}

    def register_singleton(self, type_: type, instance: Any) -> None:
        self._singletons[type_] = instance

    def register_factory(self, type_: type, factory: Callable) -> None:
        self._factories[type_] = factory

    def resolve(self, type_: Type[T]) -> T:
        if type_ in self._singletons:
            return self._singletons[type_]
        if type_ in self._factories:
            return self._factories[type_]()
        raise KeyError(f"No provider for {type_.__name__}")


# Example
from abc import ABC, abstractmethod

class Logger(ABC):
    @abstractmethod
    def log(self, msg: str) -> None: pass

class ConsoleLogger(Logger):
    def log(self, msg: str) -> None:
        print(f"[LOG] {msg}")

class UserRepository(ABC):
    @abstractmethod
    def find_by_id(self, user_id: int) -> str: pass

class InMemoryUserRepo(UserRepository):
    def __init__(self, logger: Logger):
        self.logger = logger

    def find_by_id(self, user_id: int) -> str:
        self.logger.log(f"Finding user {user_id}")
        return f"User-{user_id}"


# Usage
container = Container()
container.register_singleton(Logger, ConsoleLogger())
container.register_factory(
    UserRepository,
    lambda: InMemoryUserRepo(container.resolve(Logger))
)

repo = container.resolve(UserRepository)
print(repo.find_by_id(42))
# [LOG] Finding user 42
# User-42
```

---

## Interview Tips

1. **Start with the concept**, then show code — interviewers want to see understanding, not just syntax
2. **Use the right language for the context** — Go for simplicity, Java for classical OOP, Python for rapid prototyping
3. **Mention trade-offs** — every design decision has pros and cons
4. **Draw diagrams** — class diagrams, sequence diagrams help explain patterns
5. **Ask clarifying questions** — "Should this support multiple implementations?" signals senior thinking
6. **Know when NOT to use OOP** — this separates seniors from juniors
