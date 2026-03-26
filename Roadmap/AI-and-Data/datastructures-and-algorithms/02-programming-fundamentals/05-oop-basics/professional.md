# OOP Basics - Mathematical and Low-Level Foundations

## Type Theory and OOP

### Subtyping

In type theory, **subtyping** defines when one type can safely replace another. If `S` is a subtype of `T` (written `S <: T`), then any value of type `S` can be used where a value of type `T` is expected.

```
Formal notation:
  S <: T  means "S is a subtype of T"

Rules:
  Reflexivity:  T <: T  (every type is a subtype of itself)
  Transitivity: If S <: T and T <: U, then S <: U
```

In OOP languages:
- **Java**: `class Dog extends Animal` means `Dog <: Animal`
- **Python**: `class Dog(Animal)` means `Dog <: Animal`
- **Go**: A type satisfies an interface if it has all required methods (structural subtyping)

### Nominal vs Structural Subtyping

| Type System   | Subtyping Based On  | Language |
|---------------|---------------------|----------|
| **Nominal**   | Declared name/hierarchy | Java |
| **Structural**| Shape (methods present)  | Go   |
| **Duck typing**| Runtime method lookup | Python |

**Java (Nominal):**
```java
interface Quacker {
    void quack();
}

// Must explicitly declare "implements Quacker"
class Duck implements Quacker {
    public void quack() { System.out.println("Quack!"); }
}

class Person {
    public void quack() { System.out.println("I'm quacking!"); }
}

Quacker q = new Duck();    // OK: Duck implements Quacker
// Quacker q = new Person(); // ERROR: Person doesn't implement Quacker
// Even though Person has quack(), it didn't declare `implements Quacker`
```

**Go (Structural):**
```go
type Quacker interface {
    Quack()
}

type Duck struct{}
func (d Duck) Quack() { fmt.Println("Quack!") }

type Person struct{}
func (p Person) Quack() { fmt.Println("I'm quacking!") }

// Both work! No "implements" needed
var q Quacker = Duck{}
q = Person{} // Also OK — Person has Quack() method
```

**Python (Duck typing):**
```python
class Duck:
    def quack(self):
        print("Quack!")

class Person:
    def quack(self):
        print("I'm quacking!")

def make_it_quack(obj):
    obj.quack()  # Works if obj has quack(), fails at runtime otherwise

make_it_quack(Duck())    # OK
make_it_quack(Person())  # OK
make_it_quack(42)        # AttributeError at runtime
```

---

### Liskov Substitution Principle — Formal Definition

Barbara Liskov's original formulation (1987):

> If `S` is a subtype of `T`, then objects of type `T` may be replaced with objects of type `S` without altering any of the desirable properties of the program.

**Formal behavioral conditions:**

Let `q(x)` be a property provable about objects `x` of type `T`. Then `q(y)` should be provable for objects `y` of type `S` where `S <: T`.

**Specific rules:**

1. **Preconditions cannot be strengthened in the subtype**
   - If base method requires `x > 0`, subtype cannot require `x > 10`

2. **Postconditions cannot be weakened in the subtype**
   - If base method guarantees `result >= 0`, subtype must also guarantee it

3. **Invariants of the base type must be preserved**
   - If base guarantees `balance >= 0`, subtype must maintain this

4. **Covariance of return types**: Subtype can return a more specific type
5. **Contravariance of parameter types**: Subtype can accept a more general type

```
Method signature covariance/contravariance:

Base:    T.method(ParamBase) → ReturnBase
Subtype: S.method(ParamSuper) → ReturnSub    ← valid

Where:
  ParamSuper :> ParamBase   (contravariant: accepts wider input)
  ReturnSub  <: ReturnBase  (covariant: returns narrower output)
```

**Example violation:**

```java
// Violation: Stack extends ArrayList but breaks LSP
class Stack<T> extends ArrayList<T> {
    public T pop() {
        return remove(size() - 1);
    }

    public void push(T item) {
        add(item);
    }
}

// Problem: Stack IS-A List, so this is legal:
Stack<String> stack = new Stack<>();
stack.push("a");
stack.push("b");
stack.add(0, "c");     // Inserts at index 0 — breaks stack behavior!
stack.remove(1);       // Removes by index — not stack-like!
// The "stack" invariant (LIFO) is broken because List operations are exposed
```

---

## Virtual Dispatch Tables (vtable)

### How Polymorphism Works Under the Hood

When you call a virtual method, the runtime must determine which implementation to call. This is resolved via a **vtable** (virtual method table).

### Java vtable

```
class Animal {
    void speak() { println("..."); }    // vtable slot 0
    void eat()   { println("eating"); } // vtable slot 1
}

class Dog extends Animal {
    @Override
    void speak() { println("Woof"); }   // overrides slot 0
    void fetch() { println("fetch"); }  // vtable slot 2
}

Memory layout:

Animal object:
  ┌─────────────────┐
  │ class pointer ───┼──→ Animal vtable:
  │ (fields...)      │      [0] Animal.speak
  └─────────────────┘      [1] Animal.eat

Dog object:
  ┌─────────────────┐
  │ class pointer ───┼──→ Dog vtable:
  │ (fields...)      │      [0] Dog.speak    ← overridden!
  └─────────────────┘      [1] Animal.eat   ← inherited
                            [2] Dog.fetch    ← new method

When you call:
  Animal a = new Dog();
  a.speak();

1. Load object's class pointer
2. Look up vtable slot 0 (speak)
3. Find Dog.speak (because actual object is Dog)
4. Call Dog.speak → "Woof"
```

### Java Bytecode

```
// a.speak() compiles to:
invokevirtual #2  // Method Animal.speak:()V

// JVM at runtime:
1. Get actual class of 'a' (Dog)
2. Look up Dog's vtable for speak()
3. Call Dog.speak()
```

The `invokevirtual` instruction performs dynamic dispatch. Compare with:
- `invokestatic` — calls static method directly (no dispatch)
- `invokespecial` — calls constructor or super method (no dispatch)
- `invokeinterface` — calls interface method (similar to invokevirtual but for interfaces)

### C++ vtable (for comparison)

```cpp
// C++ makes virtual dispatch explicit with 'virtual' keyword
class Animal {
public:
    virtual void speak() { cout << "..." << endl; }
    void eat() { cout << "eating" << endl; }  // NOT virtual — static dispatch
};

class Dog : public Animal {
public:
    void speak() override { cout << "Woof" << endl; }
};

Animal* a = new Dog();
a->speak();  // Dynamic dispatch → Dog::speak (virtual)
a->eat();    // Static dispatch → Animal::eat (non-virtual)
```

---

## Interface Dispatch in Go

Go uses a different mechanism than traditional vtables. Go interfaces are represented as a pair of pointers: `(type, value)`.

### iface and itab

```
Go interface value layout:

type iface struct {
    tab  *itab          // interface table (method pointers)
    data unsafe.Pointer  // pointer to actual data
}

type itab struct {
    inter *interfacetype  // interface type descriptor
    _type *_type          // concrete type descriptor
    hash  uint32          // type hash for fast type assertion
    _     [4]byte
    fun   [1]uintptr      // method function pointers (variable size)
}
```

### Visualization

```go
type Stringer interface {
    String() string
}

type Dog struct {
    Name string
}

func (d Dog) String() string {
    return "Dog: " + d.Name
}

var s Stringer = Dog{Name: "Rex"}
```

```
Memory layout of 's':

s (interface value):
  ┌──────────────────────┐
  │ tab ──→ itab:        │
  │         inter: *Stringer interface type
  │         _type: *Dog concrete type
  │         fun[0]: Dog.String  ← method pointer
  │                              │
  │ data ──→ Dog{Name: "Rex"}   │
  └──────────────────────┘

When you call s.String():
1. Load s.tab
2. Look up fun[0] (String method)
3. Call with s.data as receiver
```

### itab Caching

Go caches itab lookups. The first time a `(concrete type, interface type)` pair is encountered, Go computes the itab and caches it in a global hash table.

```
itab cache (global):

hash(Dog, Stringer) → itab{Dog, Stringer, [Dog.String]}
hash(Cat, Stringer) → itab{Cat, Stringer, [Cat.String]}

Subsequent interface conversions for the same pair are O(1) lookup.
```

### Empty Interface `any` (interface{})

```
type eface struct {
    _type *_type          // type descriptor only
    data  unsafe.Pointer  // pointer to data
}

// No itab needed — no methods to dispatch
var x any = 42
// x._type → *int type descriptor
// x.data  → pointer to 42
```

### Type Assertion Mechanism

```go
var s Stringer = Dog{Name: "Rex"}

// Type assertion: s.(Dog)
// 1. Check s.tab._type == Dog's type descriptor
// 2. If match, return *s.data as Dog
// 3. If no match, panic (or return ok=false with comma-ok)

d, ok := s.(Dog)  // Safe assertion
if ok {
    fmt.Println(d.Name) // "Rex"
}

// Type switch uses the same mechanism
switch v := s.(type) {
case Dog:
    fmt.Println("Dog:", v.Name)
case Cat:
    fmt.Println("Cat:", v.Name)
}
```

---

## Java Class Loading and Bytecode

### Class Loading Process

```
Source code → Compiler → Bytecode (.class) → ClassLoader → JVM

ClassLoader hierarchy:
  Bootstrap ClassLoader (java.lang.*)
    └── Platform ClassLoader (java.*)
          └── Application ClassLoader (classpath)
                └── Custom ClassLoaders

Loading phases:
1. Loading     — read .class file bytes
2. Linking
   a. Verification — verify bytecode integrity
   b. Preparation  — allocate memory for static fields
   c. Resolution   — resolve symbolic references
3. Initialization — execute static initializers, <clinit>
```

### Bytecode for Virtual Methods

```java
public class Animal {
    public void speak() {
        System.out.println("...");
    }
}

public class Dog extends Animal {
    @Override
    public void speak() {
        System.out.println("Woof");
    }

    public void fetch() {
        System.out.println("Fetching!");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.speak();
    }
}
```

Bytecode for `main` (simplified):

```
0: new           #2    // class Dog
3: dup
4: invokespecial #3    // Dog.<init>:()V  (constructor)
7: astore_1            // store in local var 1 (a)
8: aload_1             // load 'a'
9: invokevirtual #4   // Method Animal.speak:()V
                       // At runtime, JVM sees actual type is Dog
                       // → dispatches to Dog.speak()
```

### JIT Compilation and Devirtualization

```
HotSpot JVM optimizations:

1. Monomorphic call site (only one type seen):
   invokevirtual Animal.speak
   → JIT inlines Dog.speak directly (with type guard)

2. Bimorphic call site (two types seen):
   invokevirtual Animal.speak
   → JIT generates: if (type == Dog) call Dog.speak
                     else if (type == Cat) call Cat.speak
                     else deoptimize

3. Megamorphic call site (many types):
   → Falls back to full vtable lookup
   → Significant performance penalty
```

---

## Python's MRO — C3 Linearization

### Method Resolution Order (MRO)

When Python looks up a method on an object, it searches classes in a specific order defined by the **MRO**. Python uses the **C3 linearization algorithm**.

### Simple Inheritance

```python
class A:
    def method(self):
        print("A")

class B(A):
    def method(self):
        print("B")

class C(A):
    def method(self):
        print("C")

class D(B, C):
    pass

# MRO of D:
print(D.__mro__)
# (D, B, C, A, object)

d = D()
d.method()  # "B" — first in MRO after D that has method()
```

### C3 Linearization Algorithm

The C3 algorithm computes MRO to guarantee:
1. **Monotonicity**: If C1 precedes C2 in the linearization of C, then C1 precedes C2 in the linearization of any subclass of C
2. **Local precedence order**: A class always appears before its parents, and parents are in the order specified

**Algorithm:**

```
L[C] = C + merge(L[B1], L[B2], ..., L[Bn], [B1, B2, ..., Bn])

where C(B1, B2, ..., Bn) and L[X] is the linearization of X

merge operation:
1. Take the head of the first list
2. If the head is not in the tail of any other list:
   - Add it to the result
   - Remove it from all lists
3. Otherwise, skip to the next list and try its head
4. Repeat until all lists are empty
5. If no head can be selected → Error (inconsistent hierarchy)
```

### Detailed Example

```python
class O: pass  # object
class A(O): pass
class B(O): pass
class C(O): pass
class D(A, B): pass
class E(B, C): pass
class F(D, E): pass

# Computing L[F]:
# F(D, E)
# L[D] = D, A, B, O
# L[E] = E, B, C, O

# L[F] = F + merge(L[D], L[E], [D, E])
#       = F + merge([D, A, B, O], [E, B, C, O], [D, E])

# Step 1: head of first list = D
#   D not in tail of [E, B, C, O] or [D, E]?
#   D IS in [D, E] but as head... check tails only:
#   tail of [E, B, C, O] = [B, C, O] — D not in it ✓
#   tail of [D, E] = [E] — D not in it ✓
#   → Take D
#   Remove D: merge([A, B, O], [E, B, C, O], [E])

# Step 2: head = A
#   A not in any tail ✓
#   → Take A
#   Remove A: merge([B, O], [E, B, C, O], [E])

# Step 3: head = B
#   B in tail of [E, B, C, O]? Yes → skip!
#   Try next list, head = E
#   E not in any tail ✓
#   → Take E
#   Remove E: merge([B, O], [B, C, O], [])

# Step 4: head = B
#   B not in any tail ✓ (it's now the head of both lists)
#   → Take B
#   Remove B: merge([O], [C, O], [])

# Step 5: head = O
#   O in tail of [C, O]? Yes → skip!
#   Try next list, head = C
#   C not in any tail ✓
#   → Take C
#   Remove C: merge([O], [O], [])

# Step 6: head = O
#   → Take O

# Result: L[F] = F, D, A, E, B, C, O

print(F.__mro__)
# (<class 'F'>, <class 'D'>, <class 'A'>, <class 'E'>,
#  <class 'B'>, <class 'C'>, <class 'O'>, <class 'object'>)
```

### Diamond Problem

```python
class A:
    def method(self):
        print("A.method")

class B(A):
    def method(self):
        print("B.method")
        super().method()  # Calls next in MRO, NOT necessarily A!

class C(A):
    def method(self):
        print("C.method")
        super().method()

class D(B, C):
    def method(self):
        print("D.method")
        super().method()

d = D()
d.method()
# Output:
# D.method
# B.method   (B is next after D in MRO)
# C.method   (C is next after B in MRO — NOT A!)
# A.method   (A is next after C in MRO)

# MRO: D → B → C → A → object
# super() follows the MRO chain, ensuring each class is called exactly once
```

### Invalid Hierarchies

```python
class A: pass
class B(A): pass
class C(A, B): pass  # TypeError: Cannot create a consistent MRO

# Why? C(A, B) says A comes before B
# But B(A) says B comes before A (B is a subclass of A)
# C3 detects this inconsistency and raises TypeError
```

---

## Object Layout in Memory

### Go Struct Layout

```go
type Example struct {
    a bool    // 1 byte
    b int64   // 8 bytes
    c bool    // 1 byte
    d int32   // 4 bytes
}

// Memory layout (with padding for alignment):
// Offset  Size  Field
// 0       1     a (bool)
// 1       7     [padding]
// 8       8     b (int64) — must be 8-byte aligned
// 16      1     c (bool)
// 17      3     [padding]
// 20      4     d (int32) — must be 4-byte aligned
// Total: 24 bytes

// Optimized layout (reorder fields):
type ExampleOptimized struct {
    b int64   // 8 bytes, offset 0
    d int32   // 4 bytes, offset 8
    a bool    // 1 byte,  offset 12
    c bool    // 1 byte,  offset 13
    // 2 bytes padding
}
// Total: 16 bytes — saved 8 bytes!
```

```go
package main

import (
    "fmt"
    "unsafe"
)

type Bad struct {
    a bool
    b int64
    c bool
    d int32
}

type Good struct {
    b int64
    d int32
    a bool
    c bool
}

func main() {
    fmt.Println("Bad size:", unsafe.Sizeof(Bad{}))   // 24
    fmt.Println("Good size:", unsafe.Sizeof(Good{})) // 16
}
```

### Java Object Layout

```
Java object header (64-bit JVM with compressed oops):

┌─────────────────────────────────────┐
│ Mark Word (8 bytes)                 │  ← hash code, GC age, lock info
│ Class Pointer (4 bytes, compressed) │  ← pointer to Class metadata
│ [Array Length (4 bytes)]            │  ← only for arrays
├─────────────────────────────────────┤
│ Instance fields                     │
│ (ordered by type size, then name)   │
│ (with padding for alignment)        │
└─────────────────────────────────────┘

Example:
class Point {
    int x;    // 4 bytes
    int y;    // 4 bytes
}

Layout:
  0-7:   Mark Word (8 bytes)
  8-11:  Class pointer (4 bytes, compressed)
  12-15: x (int, 4 bytes)
  16-19: y (int, 4 bytes)
  20-23: padding (align to 8 bytes)
  Total: 24 bytes for just two ints!

// Use JOL (Java Object Layout) library to inspect:
// System.out.println(ClassLayout.parseClass(Point.class).toPrintable());
```

### Python Object Layout

Python objects are much larger due to dynamic nature:

```python
import sys

# Every Python object has:
# - Reference count (8 bytes)
# - Type pointer (8 bytes)
# - Dict pointer for instance attributes (8 bytes)
# - ... other internal fields

print(sys.getsizeof(object()))  # 16 bytes (base object)
print(sys.getsizeof(42))       # 28 bytes (int)
print(sys.getsizeof("hello"))  # 54 bytes (str)
print(sys.getsizeof([]))       # 56 bytes (empty list)
print(sys.getsizeof({}))       # 64 bytes (empty dict)

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

p = Point(1, 2)
print(sys.getsizeof(p))        # 48 bytes (instance)
print(sys.getsizeof(p.__dict__))  # 104 bytes (attribute dict!)
# Total: ~152 bytes for two integers!

# Optimization: __slots__
class PointSlots:
    __slots__ = ('x', 'y')
    def __init__(self, x, y):
        self.x = x
        self.y = y

ps = PointSlots(1, 2)
print(sys.getsizeof(ps))  # 48 bytes (no __dict__)
# hasattr(ps, '__dict__')  → False
# ps.z = 3  → AttributeError (can't add new attributes)
```

### Memory Comparison

| Object          | Go       | Java     | Python      | Python (__slots__) |
|-----------------|----------|----------|-------------|---------------------|
| Two ints        | 16 bytes | 24 bytes | ~152 bytes  | ~48 bytes           |
| Empty struct    | 0 bytes  | 16 bytes | ~48 bytes   | ~48 bytes           |
| Boolean         | 1 byte   | 16 bytes | 28 bytes    | N/A                 |

---

## Formal Specification of Class Invariants

A **class invariant** is a condition that must always be true for all instances of a class, before and after every public method call.

### Formal Definition

```
For a class C with invariant I:

∀ method m in C:
  Precondition:  I holds before m is called
  Postcondition: I holds after m returns

If constructor creates an instance:
  Postcondition: I holds after construction

This is Design by Contract (Bertrand Meyer, Eiffel language):
  {P} operation {Q}
  P = precondition, Q = postcondition
  I = class invariant (implicitly ANDed with P and Q)
```

### Example: Sorted List

```go
// Invariant: elements are always sorted in ascending order
type SortedList struct {
    elements []int
}

// checkInvariant verifies the invariant (for debugging)
func (sl *SortedList) checkInvariant() {
    for i := 1; i < len(sl.elements); i++ {
        if sl.elements[i] < sl.elements[i-1] {
            panic("invariant violated: list is not sorted")
        }
    }
}

// Constructor establishes the invariant
func NewSortedList() *SortedList {
    return &SortedList{elements: []int{}}
    // Invariant: empty list is trivially sorted ✓
}

// Insert maintains the invariant
func (sl *SortedList) Insert(val int) {
    // Precondition: invariant holds
    idx := sort.SearchInts(sl.elements, val)
    sl.elements = append(sl.elements, 0)
    copy(sl.elements[idx+1:], sl.elements[idx:])
    sl.elements[idx] = val
    // Postcondition: invariant still holds (inserted at correct position)
}

// Remove maintains the invariant
func (sl *SortedList) Remove(val int) bool {
    idx := sort.SearchInts(sl.elements, val)
    if idx >= len(sl.elements) || sl.elements[idx] != val {
        return false
    }
    sl.elements = append(sl.elements[:idx], sl.elements[idx+1:]...)
    return true
    // Postcondition: invariant holds (removing element doesn't break sort)
}
```

### Java with Assertions

```java
public class SortedList {
    private final List<Integer> elements = new ArrayList<>();

    // Invariant checker
    private boolean isSorted() {
        for (int i = 1; i < elements.size(); i++) {
            if (elements.get(i) < elements.get(i - 1)) return false;
        }
        return true;
    }

    public void insert(int val) {
        assert isSorted() : "Pre-invariant violated";

        int idx = Collections.binarySearch(elements, val);
        if (idx < 0) idx = -(idx + 1);
        elements.add(idx, val);

        assert isSorted() : "Post-invariant violated";
    }
}
// Run with: java -ea SortedList  (enables assertions)
```

### Python with Contracts

```python
class SortedList:
    def __init__(self):
        self._elements: list[int] = []

    def _check_invariant(self) -> None:
        """Class invariant: elements are always sorted."""
        for i in range(1, len(self._elements)):
            assert self._elements[i] >= self._elements[i - 1], \
                f"Invariant violated at index {i}: {self._elements}"

    def insert(self, val: int) -> None:
        self._check_invariant()  # Pre-check

        import bisect
        bisect.insort(self._elements, val)

        self._check_invariant()  # Post-check

    def remove(self, val: int) -> bool:
        self._check_invariant()

        import bisect
        idx = bisect.bisect_left(self._elements, val)
        if idx < len(self._elements) and self._elements[idx] == val:
            self._elements.pop(idx)
            self._check_invariant()
            return True
        return False
```

---

## Key Takeaways

1. **Nominal subtyping** (Java) requires explicit declarations; **structural subtyping** (Go) is based on method sets
2. **LSP formally** requires: preconditions no stronger, postconditions no weaker, invariants preserved
3. **vtables** enable polymorphic dispatch at a cost of one pointer indirection
4. **Go's interface dispatch** uses `(itab, data)` pairs with global itab caching
5. **Python's C3 linearization** solves the diamond problem by computing a consistent MRO
6. **Memory layout** differs dramatically: Go is compact, Java has object header overhead, Python has per-object dict overhead
7. **Class invariants** are formal contracts that must hold before and after every public operation
