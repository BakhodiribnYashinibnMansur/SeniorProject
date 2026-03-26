# OOP Basics - Junior Level

## What is OOP?

**Object-Oriented Programming (OOP)** is a programming paradigm that organizes code around **objects** — data structures that bundle related data (fields/properties) and behavior (methods) together.

### Why OOP Exists

Before OOP, procedural programming organized code as sequences of instructions operating on separate data. As programs grew larger, this led to:

- **Spaghetti code** — functions scattered everywhere, hard to trace data flow
- **Data corruption** — any function could modify any data
- **Code duplication** — similar logic repeated for different data types
- **Difficult maintenance** — changing one thing broke many other things

OOP solves these problems by:
1. **Grouping data with its behavior** — no more scattered functions
2. **Hiding internal state** — preventing accidental corruption
3. **Enabling code reuse** — through inheritance and composition
4. **Modeling real-world entities** — making code intuitive

---

## The 4 Pillars of OOP

### 1. Encapsulation
Bundling data and methods together, hiding internal details from the outside world.

```
[Object]
  - private data (hidden)
  - public methods (exposed interface)
  - Internal state is protected from direct access
```

### 2. Abstraction
Showing only essential features while hiding complexity. Users interact with a simplified interface.

### 3. Inheritance
Creating new classes based on existing ones, reusing and extending behavior.

### 4. Polymorphism
Objects of different types responding to the same method call in their own way.

---

## Classes, Objects, Structs

### Java — Classes and Objects

```java
public class Dog {
    // Fields (instance variables)
    private String name;
    private int age;
    private String breed;

    // Constructor
    public Dog(String name, int age, String breed) {
        this.name = name;   // 'this' refers to the current object
        this.age = age;
        this.breed = breed;
    }

    // Methods
    public void bark() {
        System.out.println(this.name + " says: Woof!");
    }

    public String describe() {
        return this.name + " (" + this.breed + ", " + this.age + " years)";
    }

    // Getters and Setters (encapsulation)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        }
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        Dog myDog = new Dog("Rex", 3, "German Shepherd");
        myDog.bark();                      // Rex says: Woof!
        System.out.println(myDog.describe()); // Rex (German Shepherd, 3 years)

        myDog.setAge(4);
        // myDog.age = -5;  // ERROR: age is private, can't access directly
    }
}
```

### Python — Classes and Objects

```python
class Dog:
    # Class variable (shared by all instances)
    species = "Canis familiaris"

    # Constructor (__init__ is the initializer)
    def __init__(self, name: str, age: int, breed: str):
        self.name = name      # 'self' refers to the current instance
        self.age = age
        self._breed = breed   # Convention: underscore = "private"

    # Methods
    def bark(self) -> None:
        print(f"{self.name} says: Woof!")

    def describe(self) -> str:
        return f"{self.name} ({self._breed}, {self.age} years)"

    # Property (Pythonic encapsulation)
    @property
    def breed(self) -> str:
        return self._breed

    @property
    def age_in_human_years(self) -> int:
        return self.age * 7


# Usage
my_dog = Dog("Rex", 3, "German Shepherd")
my_dog.bark()                  # Rex says: Woof!
print(my_dog.describe())       # Rex (German Shepherd, 3 years)
print(my_dog.age_in_human_years)  # 21
print(my_dog.breed)            # German Shepherd
```

### Go — Structs and Methods

Go does **not** have classes. It uses **structs** (data) + **methods** (behavior attached to types).

```go
package main

import "fmt"

// Struct definition (like a class without inheritance)
type Dog struct {
    name  string // lowercase = unexported (private)
    age   int
    breed string
}

// Constructor function (Go convention: NewTypeName)
func NewDog(name string, age int, breed string) *Dog {
    return &Dog{
        name:  name,
        age:   age,
        breed: breed,
    }
}

// Method with receiver (like 'this' or 'self')
func (d *Dog) Bark() { // Uppercase = exported (public)
    fmt.Printf("%s says: Woof!\n", d.name)
}

func (d *Dog) Describe() string {
    return fmt.Sprintf("%s (%s, %d years)", d.name, d.breed, d.age)
}

// Getter
func (d *Dog) Name() string {
    return d.name
}

// Setter with validation
func (d *Dog) SetAge(age int) {
    if age >= 0 {
        d.age = age
    }
}

func main() {
    myDog := NewDog("Rex", 3, "German Shepherd")
    myDog.Bark()                      // Rex says: Woof!
    fmt.Println(myDog.Describe())     // Rex (German Shepherd, 3 years)

    myDog.SetAge(4)
    // myDog.age = -5  // Works within same package, but convention says use setter
}
```

---

## Constructors / Initialization

### Java Constructors

```java
public class Person {
    private String name;
    private int age;

    // Default constructor
    public Person() {
        this.name = "Unknown";
        this.age = 0;
    }

    // Parameterized constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Constructor chaining
    public Person(String name) {
        this(name, 0);  // calls the 2-arg constructor
    }
}

// Usage
Person p1 = new Person();              // Default
Person p2 = new Person("Alice", 25);   // Parameterized
Person p3 = new Person("Bob");         // Chained → age = 0
```

### Python `__init__`

```python
class Person:
    def __init__(self, name: str = "Unknown", age: int = 0):
        self.name = name
        self.age = age

    @classmethod
    def from_string(cls, data: str) -> "Person":
        """Alternative constructor (factory method)."""
        name, age = data.split(",")
        return cls(name.strip(), int(age.strip()))

    def __repr__(self) -> str:
        return f"Person(name='{self.name}', age={self.age})"


# Usage
p1 = Person()                    # Default values
p2 = Person("Alice", 25)         # Positional
p3 = Person(name="Bob")          # Keyword → age = 0
p4 = Person.from_string("Eve, 30")  # Factory method
```

### Go Constructor Functions

```go
type Person struct {
    name string
    age  int
}

// Standard constructor
func NewPerson(name string, age int) *Person {
    return &Person{name: name, age: age}
}

// Constructor with defaults
func NewDefaultPerson() *Person {
    return &Person{name: "Unknown", age: 0}
}

// Functional options pattern (advanced but common in Go)
type PersonOption func(*Person)

func WithName(name string) PersonOption {
    return func(p *Person) { p.name = name }
}

func WithAge(age int) PersonOption {
    return func(p *Person) { p.age = age }
}

func NewPersonWithOptions(opts ...PersonOption) *Person {
    p := &Person{name: "Unknown", age: 0}
    for _, opt := range opts {
        opt(p)
    }
    return p
}

// Usage
p1 := NewPerson("Alice", 25)
p2 := NewDefaultPerson()
p3 := NewPersonWithOptions(WithName("Bob"), WithAge(30))
```

---

## Fields/Properties and Methods

### Java

```java
public class BankAccount {
    // Fields
    private String owner;
    private double balance;
    private static int accountCount = 0; // Class-level field (shared)

    public BankAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
        accountCount++;
    }

    // Instance method
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= this.balance) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    // Static method (belongs to class, not instance)
    public static int getAccountCount() {
        return accountCount;
    }

    // Getter
    public double getBalance() {
        return this.balance;
    }
}
```

### Python

```python
class BankAccount:
    # Class variable
    account_count = 0

    def __init__(self, owner: str, initial_balance: float = 0.0):
        self.owner = owner
        self._balance = initial_balance  # "private" by convention
        BankAccount.account_count += 1

    # Instance method
    def deposit(self, amount: float) -> None:
        if amount > 0:
            self._balance += amount

    def withdraw(self, amount: float) -> bool:
        if 0 < amount <= self._balance:
            self._balance -= amount
            return True
        return False

    # Property (computed field)
    @property
    def balance(self) -> float:
        return self._balance

    # Static method (no access to instance or class)
    @staticmethod
    def validate_amount(amount: float) -> bool:
        return amount > 0

    # Class method (access to class, not instance)
    @classmethod
    def get_account_count(cls) -> int:
        return cls.account_count
```

### Go

```go
type BankAccount struct {
    owner   string
    balance float64
}

// accountCount is package-level (like static)
var accountCount int

func NewBankAccount(owner string, initialBalance float64) *BankAccount {
    accountCount++
    return &BankAccount{owner: owner, balance: initialBalance}
}

// Methods use pointer receivers to modify state
func (a *BankAccount) Deposit(amount float64) {
    if amount > 0 {
        a.balance += amount
    }
}

func (a *BankAccount) Withdraw(amount float64) bool {
    if amount > 0 && amount <= a.balance {
        a.balance -= amount
        return true
    }
    return false
}

// Value receiver (doesn't modify, just reads)
func (a BankAccount) Balance() float64 {
    return a.balance
}

// Package-level function (like static method)
func GetAccountCount() int {
    return accountCount
}
```

---

## Access Modifiers

### Java Access Modifiers

| Modifier    | Class | Package | Subclass | World |
|-------------|-------|---------|----------|-------|
| `public`    | Yes   | Yes     | Yes      | Yes   |
| `protected` | Yes   | Yes     | Yes      | No    |
| (default)   | Yes   | Yes     | No       | No    |
| `private`   | Yes   | No      | No       | No    |

```java
public class Example {
    public String publicField;       // Accessible everywhere
    protected String protectedField; // Same package + subclasses
    String packageField;             // Same package only (default)
    private String privateField;     // This class only
}
```

### Go — Exported vs Unexported

Go uses **capitalization** for visibility:

| Rule         | Visibility           |
|--------------|----------------------|
| `Uppercase`  | Exported (public)    |
| `lowercase`  | Unexported (private) |

Visibility is at the **package** level, not struct level.

```go
package user

type User struct {
    Name  string  // Exported: accessible from other packages
    email string  // Unexported: accessible only within 'user' package
}

func (u *User) GetEmail() string { // Exported method
    return u.email
}

func (u *User) validate() bool { // Unexported method
    return u.email != ""
}
```

### Python — Naming Conventions

Python has no enforced access control — it uses **naming conventions**:

| Convention       | Meaning                                   |
|------------------|-------------------------------------------|
| `name`           | Public                                    |
| `_name`          | Protected (convention: "internal use")    |
| `__name`         | Private (name mangling: `_ClassName__name`)|
| `__name__`       | Dunder/magic method (special to Python)   |

```python
class Example:
    def __init__(self):
        self.public = "anyone can access"
        self._protected = "please don't access from outside"
        self.__private = "name-mangled to _Example__private"

e = Example()
print(e.public)          # Works
print(e._protected)      # Works but frowned upon
# print(e.__private)     # AttributeError!
print(e._Example__private)  # Works (name mangling), but never do this
```

---

## `this` / `self` Keyword

### Java — `this`

```java
public class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;  // 'this' disambiguates field from parameter
        this.y = y;
    }

    public double distanceTo(Point other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // 'this' can be passed to other methods
    public void register(Registry registry) {
        registry.add(this);
    }
}
```

### Python — `self`

```python
class Point:
    def __init__(self, x: int, y: int):
        self.x = x   # 'self' is explicitly the first parameter
        self.y = y

    def distance_to(self, other: "Point") -> float:
        dx = self.x - other.x
        dy = self.y - other.y
        return (dx ** 2 + dy ** 2) ** 0.5

    def register(self, registry):
        registry.add(self)
```

### Go — Receiver Variable

```go
type Point struct {
    x, y int
}

// 'p' is the receiver — Go convention: 1-2 letter abbreviation of type
func (p *Point) DistanceTo(other *Point) float64 {
    dx := float64(p.x - other.x)
    dy := float64(p.y - other.y)
    return math.Sqrt(dx*dx + dy*dy)
}

// Value receiver — doesn't modify the struct
func (p Point) String() string {
    return fmt.Sprintf("(%d, %d)", p.x, p.y)
}
```

---

## Complete Example: A Shape System

### Java

```java
public class Rectangle {
    private double width;
    private double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double area() {
        return this.width * this.height;
    }

    public double perimeter() {
        return 2 * (this.width + this.height);
    }

    public void scale(double factor) {
        this.width *= factor;
        this.height *= factor;
    }

    @Override
    public String toString() {
        return String.format("Rectangle(%.1f x %.1f)", width, height);
    }
}

public class Circle {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double area() {
        return Math.PI * this.radius * this.radius;
    }

    public double perimeter() {
        return 2 * Math.PI * this.radius;
    }

    @Override
    public String toString() {
        return String.format("Circle(r=%.1f)", radius);
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        Rectangle rect = new Rectangle(5, 3);
        System.out.println(rect);             // Rectangle(5.0 x 3.0)
        System.out.println(rect.area());      // 15.0
        System.out.println(rect.perimeter()); // 16.0
        rect.scale(2);
        System.out.println(rect.area());      // 60.0

        Circle circle = new Circle(4);
        System.out.println(circle);           // Circle(r=4.0)
        System.out.println(circle.area());    // 50.265...
    }
}
```

### Python

```python
import math

class Rectangle:
    def __init__(self, width: float, height: float):
        self._width = width
        self._height = height

    def area(self) -> float:
        return self._width * self._height

    def perimeter(self) -> float:
        return 2 * (self._width + self._height)

    def scale(self, factor: float) -> None:
        self._width *= factor
        self._height *= factor

    def __repr__(self) -> str:
        return f"Rectangle({self._width:.1f} x {self._height:.1f})"


class Circle:
    def __init__(self, radius: float):
        self._radius = radius

    def area(self) -> float:
        return math.pi * self._radius ** 2

    def perimeter(self) -> float:
        return 2 * math.pi * self._radius

    def __repr__(self) -> str:
        return f"Circle(r={self._radius:.1f})"


# Usage
rect = Rectangle(5, 3)
print(rect)             # Rectangle(5.0 x 3.0)
print(rect.area())      # 15.0
print(rect.perimeter()) # 16.0
rect.scale(2)
print(rect.area())      # 60.0

circle = Circle(4)
print(circle)           # Circle(r=4.0)
print(circle.area())    # 50.265...
```

### Go

```go
package main

import (
    "fmt"
    "math"
)

type Rectangle struct {
    width  float64
    height float64
}

func NewRectangle(width, height float64) *Rectangle {
    return &Rectangle{width: width, height: height}
}

func (r *Rectangle) Area() float64 {
    return r.width * r.height
}

func (r *Rectangle) Perimeter() float64 {
    return 2 * (r.width + r.height)
}

func (r *Rectangle) Scale(factor float64) {
    r.width *= factor
    r.height *= factor
}

func (r Rectangle) String() string {
    return fmt.Sprintf("Rectangle(%.1f x %.1f)", r.width, r.height)
}

type Circle struct {
    radius float64
}

func NewCircle(radius float64) *Circle {
    return &Circle{radius: radius}
}

func (c Circle) Area() float64 {
    return math.Pi * c.radius * c.radius
}

func (c Circle) Perimeter() float64 {
    return 2 * math.Pi * c.radius
}

func (c Circle) String() string {
    return fmt.Sprintf("Circle(r=%.1f)", c.radius)
}

func main() {
    rect := NewRectangle(5, 3)
    fmt.Println(rect)             // Rectangle(5.0 x 3.0)
    fmt.Println(rect.Area())      // 15
    fmt.Println(rect.Perimeter()) // 16
    rect.Scale(2)
    fmt.Println(rect.Area())      // 60

    circle := NewCircle(4)
    fmt.Println(circle)           // Circle(r=4.0)
    fmt.Println(circle.Area())    // 50.265...
}
```

---

## Cheat Sheet: OOP Syntax Across Go, Java, Python

| Concept            | Go                           | Java                          | Python                       |
|--------------------|------------------------------|-------------------------------|------------------------------|
| **Define type**    | `type Dog struct{}`          | `class Dog {}`                | `class Dog:`                 |
| **Create instance**| `d := Dog{}` or `NewDog()`   | `Dog d = new Dog()`          | `d = Dog()`                  |
| **Constructor**    | `func NewDog() *Dog`         | `public Dog() {}`            | `def __init__(self):`        |
| **Instance field** | `d.name` (struct field)      | `this.name`                  | `self.name`                  |
| **Method**         | `func (d *Dog) Bark()`      | `public void bark()`         | `def bark(self):`            |
| **Self reference** | `d` (receiver var)           | `this`                       | `self`                       |
| **Public**         | `Name` (uppercase)           | `public`                     | `name` (no prefix)           |
| **Private**        | `name` (lowercase)           | `private`                    | `_name` (convention)         |
| **Static method**  | Package function             | `static` keyword             | `@staticmethod`              |
| **Class method**   | N/A                          | N/A (use static)             | `@classmethod`               |
| **ToString**       | `func (d Dog) String()`     | `public String toString()`   | `def __repr__(self):`        |
| **Equality**       | `==` (value types)           | `.equals()`                  | `def __eq__(self, other):`   |
| **Inheritance**    | Embedding (composition)      | `extends`                    | `class Child(Parent):`       |
| **Interface**      | Implicit (duck typing)       | `implements`                 | `ABC` + `@abstractmethod`   |
| **Null/None**      | `nil`                        | `null`                       | `None`                       |
| **Package/Module** | `package main`               | `package com.example`        | Module = file                |

### Key Differences Summary

1. **Go has NO classes** — uses structs + methods + interfaces
2. **Go has NO inheritance** — uses composition (embedding)
3. **Go interfaces are implicit** — no `implements` keyword needed
4. **Python has NO true private** — uses naming conventions only
5. **Java is the most "traditional" OOP** — has all classical OOP features
6. **Go is the most minimal** — intentionally omits many OOP features

---

## Key Takeaways

1. OOP bundles data and behavior together into objects
2. The 4 pillars (encapsulation, abstraction, inheritance, polymorphism) guide OOP design
3. Java uses classical OOP with classes, Python uses classes with conventions, Go uses structs with methods
4. Access control varies: Java has keywords, Go uses capitalization, Python uses conventions
5. Constructors initialize objects — each language has its own pattern
6. Understanding OOP basics is the foundation for design patterns and architecture
