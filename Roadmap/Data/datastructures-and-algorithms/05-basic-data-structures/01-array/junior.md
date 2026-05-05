# Array -- Junior Level

## Table of Contents

- [Introduction](#introduction)
- [What Is an Array?](#what-is-an-array)
- [Real-World Analogies](#real-world-analogies)
- [How Arrays Work in Memory](#how-arrays-work-in-memory)
- [Indexing: Zero-Based vs One-Based](#indexing-zero-based-vs-one-based)
- [Static vs Dynamic Arrays](#static-vs-dynamic-arrays)
- [Common Operations and Their Complexity](#common-operations-and-their-complexity)
  - [Access (Read)](#access-read)
  - [Search](#search)
  - [Insertion](#insertion)
  - [Deletion](#deletion)
  - [Update](#update)
- [Arrays in Go, Java, and Python](#arrays-in-go-java-and-python)
  - [Go: Arrays and Slices](#go-arrays-and-slices)
  - [Java: Arrays and ArrayList](#java-arrays-and-arraylist)
  - [Python: Lists](#python-lists)
- [Full CRUD Examples](#full-crud-examples)
- [Common Mistakes](#common-mistakes)
- [Summary](#summary)

---

## Introduction

The **array** is the most fundamental data structure in computer science. Almost every program you will ever write uses arrays in some form. Before learning any other data structure, you must deeply understand how arrays work, why they are fast for some operations and slow for others, and how different programming languages implement them.

This document covers everything a junior developer needs to know about arrays.

---

## What Is an Array?

An array is an **ordered collection of elements** stored in **contiguous (adjacent) memory locations**. Each element occupies the same amount of space, and you can access any element directly by its **index** (position number).

Key properties of an array:

| Property               | Description                                              |
| ---------------------- | -------------------------------------------------------- |
| Ordered                | Elements have a defined position (index)                 |
| Contiguous memory      | Elements are stored next to each other in RAM            |
| Fixed-type (typically) | All elements are the same type (in typed languages)      |
| Random access          | Any element can be read in O(1) time using its index     |
| Fixed size (static)    | Traditional arrays cannot grow after creation            |

---

## Real-World Analogies

Understanding arrays is easier with everyday examples:

**1. Row of lockers in a school hallway**
Each locker has a number (index). You can go directly to locker #7 without opening lockers #1 through #6. The lockers are side by side (contiguous). Every locker is the same size.

**2. A bookshelf with numbered slots**
Slot 0 holds the first book, slot 1 the second, and so on. You can grab the book at slot 5 instantly. But if you want to insert a new book at slot 2, you must shift books 2, 3, 4, ... one position to the right to make room.

**3. A row of seats in a cinema**
Seat A1, A2, A3, ... Each seat is the same width (same memory size). You can find seat A5 by walking directly to it (random access). If someone in the middle leaves, there is an empty gap (deletion problem).

**4. A parking lot with numbered spaces**
Space 0, space 1, space 2, ... You can drive directly to space 12 if you know the number. All spaces are the same width. The lot has a fixed number of spaces (fixed size).

---

## How Arrays Work in Memory

When you create an array, the operating system allocates a **single block of contiguous memory**. For example, an array of 5 integers (each 4 bytes on a 32-bit system):

```
Memory Address:  1000  1004  1008  1012  1016
Array Index:     [0]   [1]   [2]   [3]   [4]
Value:            10    20    30    40    50
```

To find the address of element at index `i`:

```
address(i) = base_address + (i * element_size)
```

For index 3: `1000 + (3 * 4) = 1012`. This is why access is O(1) -- it is a simple arithmetic calculation, no matter how large the array is.

### Why Contiguous Memory Matters

- **Fast access**: The CPU can calculate any element's address with one multiplication and one addition.
- **Cache-friendly**: When you read element [0], the CPU loads a chunk of nearby memory into the cache. Elements [1], [2], [3], ... are already in the cache, so reading them is extremely fast.
- **Predictable**: The memory layout is simple and the hardware can optimize for sequential access patterns.

---

## Indexing: Zero-Based vs One-Based

Most programming languages (Go, Java, Python, C, C++, JavaScript, Rust) use **zero-based indexing**: the first element is at index 0, the second at index 1, and so on.

```
Array:    [10, 20, 30, 40, 50]
Index:      0   1   2   3   4
```

Why zero-based? Because the index represents the **offset** from the base address. The first element is 0 positions away from the start.

A few languages (Lua, MATLAB, Fortran, R) use one-based indexing where the first element is at index 1.

---

## Static vs Dynamic Arrays

### Static Arrays

A **static array** has a fixed size decided at creation time. It cannot grow or shrink.

- Size is set once and never changes
- If you need more space, you must create a new, larger array and copy elements over
- Common in C, and also available in Go and Java (primitive arrays)

### Dynamic Arrays

A **dynamic array** can grow (and sometimes shrink) automatically. Internally, it uses a static array but **resizes** when needed -- typically by allocating a new array that is 2x the size and copying elements over.

- Go `slice`, Java `ArrayList`, Python `list` are all dynamic arrays
- Append is amortized O(1) thanks to the doubling strategy
- Uses slightly more memory than needed (extra capacity for future growth)

---

## Common Operations and Their Complexity

| Operation         | Time Complexity | Explanation                                     |
| ----------------- | --------------- | ----------------------------------------------- |
| Access by index   | O(1)            | Direct address calculation                      |
| Search (unsorted) | O(n)            | Must check each element one by one              |
| Search (sorted)   | O(log n)        | Binary search possible                          |
| Insert at end     | O(1) amortized  | Just place at next available slot               |
| Insert at index   | O(n)            | Must shift all elements after the index         |
| Delete at end     | O(1)            | Just remove the last element                    |
| Delete at index   | O(n)            | Must shift all elements after the index         |
| Update at index   | O(1)            | Direct address calculation, overwrite value     |

---

### Access (Read)

Accessing an element by index is the array's greatest strength. It takes constant time O(1) regardless of array size.

**Go:**

```go
package main

import "fmt"

func main() {
    // Static array
    arr := [5]int{10, 20, 30, 40, 50}
    fmt.Println("Element at index 2:", arr[2]) // 30

    // Slice (dynamic array)
    slice := []int{10, 20, 30, 40, 50}
    fmt.Println("Element at index 4:", slice[4]) // 50
}
```

**Java:**

```java
public class ArrayAccess {
    public static void main(String[] args) {
        // Static array
        int[] arr = {10, 20, 30, 40, 50};
        System.out.println("Element at index 2: " + arr[2]); // 30

        // Dynamic array (ArrayList)
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        list.add(10); list.add(20); list.add(30); list.add(40); list.add(50);
        System.out.println("Element at index 4: " + list.get(4)); // 50
    }
}
```

**Python:**

```python
# Python list (dynamic array)
arr = [10, 20, 30, 40, 50]
print("Element at index 2:", arr[2])   # 30
print("Element at index 4:", arr[4])   # 50
print("Last element:", arr[-1])         # 50 (negative indexing)
```

---

### Search

To find whether an element exists (and where), you must scan through the array. For an unsorted array this is O(n).

**Go:**

```go
package main

import "fmt"

// linearSearch returns the index of target, or -1 if not found.
func linearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    return -1
}

func main() {
    data := []int{10, 20, 30, 40, 50}
    idx := linearSearch(data, 30)
    fmt.Println("Found 30 at index:", idx) // 2

    idx = linearSearch(data, 99)
    fmt.Println("Found 99 at index:", idx) // -1
}
```

**Java:**

```java
public class ArraySearch {
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] data = {10, 20, 30, 40, 50};
        System.out.println("Found 30 at index: " + linearSearch(data, 30)); // 2
        System.out.println("Found 99 at index: " + linearSearch(data, 99)); // -1
    }
}
```

**Python:**

```python
def linear_search(arr, target):
    for i, val in enumerate(arr):
        if val == target:
            return i
    return -1

data = [10, 20, 30, 40, 50]
print("Found 30 at index:", linear_search(data, 30))  # 2
print("Found 99 at index:", linear_search(data, 99))  # -1

# Python built-in way:
print("Index of 30:", data.index(30))  # 2
print("Is 40 in list?", 40 in data)   # True
```

---

### Insertion

Inserting at the end of a dynamic array is O(1) amortized. Inserting at a specific index is O(n) because elements must shift to make room.

**Go:**

```go
package main

import "fmt"

func main() {
    // Append to end -- O(1) amortized
    slice := []int{10, 20, 30}
    slice = append(slice, 40)
    fmt.Println("After append:", slice) // [10 20 30 40]

    // Insert at index 1 -- O(n), elements shift right
    index := 1
    value := 15
    // Make room by appending a zero, then shift elements right
    slice = append(slice, 0)
    copy(slice[index+1:], slice[index:])
    slice[index] = value
    fmt.Println("After insert at index 1:", slice) // [10 15 20 30 40]
}
```

**Java:**

```java
import java.util.ArrayList;

public class ArrayInsert {
    public static void main(String[] args) {
        // Using ArrayList (dynamic)
        ArrayList<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);

        // Append to end -- O(1) amortized
        list.add(40);
        System.out.println("After append: " + list); // [10, 20, 30, 40]

        // Insert at index 1 -- O(n)
        list.add(1, 15);
        System.out.println("After insert at index 1: " + list); // [10, 15, 20, 30, 40]

        // Using static array -- manual shifting required
        int[] arr = {10, 20, 30, 40, 0}; // extra space at end
        int insertIdx = 1;
        int insertVal = 15;
        // Shift elements right
        for (int i = arr.length - 1; i > insertIdx; i--) {
            arr[i] = arr[i - 1];
        }
        arr[insertIdx] = insertVal;
        // arr is now {10, 15, 20, 30, 40}
    }
}
```

**Python:**

```python
# Python list -- dynamic array
arr = [10, 20, 30]

# Append to end -- O(1) amortized
arr.append(40)
print("After append:", arr)  # [10, 20, 30, 40]

# Insert at index 1 -- O(n)
arr.insert(1, 15)
print("After insert at index 1:", arr)  # [10, 15, 20, 30, 40]
```

---

### Deletion

Deleting the last element is O(1). Deleting from a specific index is O(n) because elements must shift to fill the gap.

**Go:**

```go
package main

import "fmt"

func main() {
    slice := []int{10, 15, 20, 30, 40}

    // Delete last element -- O(1)
    slice = slice[:len(slice)-1]
    fmt.Println("After delete last:", slice) // [10 15 20 30]

    // Delete element at index 1 -- O(n)
    index := 1
    slice = append(slice[:index], slice[index+1:]...)
    fmt.Println("After delete at index 1:", slice) // [10 20 30]
}
```

**Java:**

```java
import java.util.ArrayList;

public class ArrayDelete {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(10); list.add(15); list.add(20); list.add(30); list.add(40);

        // Delete last element -- O(1)
        list.remove(list.size() - 1);
        System.out.println("After delete last: " + list); // [10, 15, 20, 30]

        // Delete element at index 1 -- O(n)
        list.remove(1);
        System.out.println("After delete at index 1: " + list); // [10, 20, 30]
    }
}
```

**Python:**

```python
arr = [10, 15, 20, 30, 40]

# Delete last element -- O(1)
arr.pop()
print("After delete last:", arr)  # [10, 15, 20, 30]

# Delete at index 1 -- O(n)
arr.pop(1)
print("After delete at index 1:", arr)  # [10, 20, 30]

# Delete by value (first occurrence) -- O(n)
arr.remove(20)
print("After remove value 20:", arr)  # [10, 30]

# Delete using del
del arr[0]
print("After del index 0:", arr)  # [30]
```

---

### Update

Updating an element at a known index is O(1) -- just overwrite the value.

**Go:**

```go
package main

import "fmt"

func main() {
    arr := []int{10, 20, 30, 40, 50}
    arr[2] = 99
    fmt.Println("After update:", arr) // [10 20 99 40 50]
}
```

**Java:**

```java
public class ArrayUpdate {
    public static void main(String[] args) {
        int[] arr = {10, 20, 30, 40, 50};
        arr[2] = 99;
        System.out.println("After update: arr[2] = " + arr[2]); // 99

        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        list.add(10); list.add(20); list.add(30);
        list.set(1, 99);
        System.out.println("After update: " + list); // [10, 99, 30]
    }
}
```

**Python:**

```python
arr = [10, 20, 30, 40, 50]
arr[2] = 99
print("After update:", arr)  # [10, 20, 99, 40, 50]
```

---

## Arrays in Go, Java, and Python

### Go: Arrays and Slices

Go has two distinct types:

**Arrays** -- fixed size, value type (copying an array copies all elements):

```go
var a [5]int                    // zero-valued: [0 0 0 0 0]
b := [3]string{"a", "b", "c"}  // initialized
c := [...]int{1, 2, 3}         // compiler counts: size 3
fmt.Println(len(a))             // 5
```

**Slices** -- dynamic, reference type (backed by an underlying array):

```go
s := []int{1, 2, 3}            // literal
s = append(s, 4, 5)            // grow
fmt.Println(len(s), cap(s))    // length and capacity

s2 := make([]int, 5, 10)       // length 5, capacity 10

// Slicing
sub := s[1:3]                  // elements at index 1, 2
```

Important slice concepts:
- `len(s)` -- number of elements currently in the slice
- `cap(s)` -- total capacity of the underlying array
- When `len` exceeds `cap`, Go allocates a new, larger array (roughly 2x) and copies elements

### Java: Arrays and ArrayList

**Arrays** -- fixed size, can hold primitives or objects:

```java
int[] nums = new int[5];                     // [0, 0, 0, 0, 0]
int[] nums2 = {10, 20, 30};                  // initialized
String[] names = new String[]{"Alice", "Bob"};
System.out.println(nums2.length);             // 3
```

**ArrayList** -- dynamic, objects only (uses autoboxing for primitives):

```java
import java.util.ArrayList;

ArrayList<Integer> list = new ArrayList<>();
list.add(10);                    // append
list.add(0, 5);                  // insert at index 0
list.get(1);                     // access index 1 -> 10
list.set(1, 99);                 // update index 1
list.remove(0);                  // delete index 0
list.size();                     // current number of elements
```

### Python: Lists

Python's `list` is a dynamic array. It holds references to objects of any type.

```python
arr = [1, 2, 3, "hello", True]   # mixed types allowed
arr.append(4)                     # add to end
arr.insert(0, 0)                  # insert at index
arr.pop()                         # remove last
arr.pop(2)                        # remove at index 2
arr[1] = 99                       # update
len(arr)                          # length

# Slicing
sub = arr[1:3]                    # elements at index 1, 2
rev = arr[::-1]                   # reversed copy

# List comprehension
squares = [x**2 for x in range(10)]
```

---

## Full CRUD Examples

Below are complete programs demonstrating all CRUD (Create, Read, Update, Delete) operations.

### Go

```go
package main

import "fmt"

func main() {
    // CREATE
    students := []string{"Alice", "Bob", "Charlie"}
    fmt.Println("Created:", students)

    // READ -- access by index
    fmt.Println("Student at index 1:", students[1])

    // READ -- iterate all
    fmt.Println("All students:")
    for i, name := range students {
        fmt.Printf("  [%d] %s\n", i, name)
    }

    // UPDATE
    students[1] = "Barbara"
    fmt.Println("After update:", students)

    // DELETE -- remove index 0
    students = append(students[:0], students[1:]...)
    fmt.Println("After delete index 0:", students)

    // INSERT -- add "Diana" at index 1
    students = append(students, "")
    copy(students[2:], students[1:])
    students[1] = "Diana"
    fmt.Println("After insert:", students)

    // LENGTH
    fmt.Println("Length:", len(students))
}
```

### Java

```java
import java.util.ArrayList;

public class ArrayCRUD {
    public static void main(String[] args) {
        // CREATE
        ArrayList<String> students = new ArrayList<>();
        students.add("Alice");
        students.add("Bob");
        students.add("Charlie");
        System.out.println("Created: " + students);

        // READ -- access by index
        System.out.println("Student at index 1: " + students.get(1));

        // READ -- iterate all
        System.out.println("All students:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println("  [" + i + "] " + students.get(i));
        }

        // UPDATE
        students.set(1, "Barbara");
        System.out.println("After update: " + students);

        // DELETE -- remove index 0
        students.remove(0);
        System.out.println("After delete index 0: " + students);

        // INSERT -- add "Diana" at index 1
        students.add(1, "Diana");
        System.out.println("After insert: " + students);

        // LENGTH
        System.out.println("Length: " + students.size());
    }
}
```

### Python

```python
# CREATE
students = ["Alice", "Bob", "Charlie"]
print("Created:", students)

# READ -- access by index
print("Student at index 1:", students[1])

# READ -- iterate all
print("All students:")
for i, name in enumerate(students):
    print(f"  [{i}] {name}")

# UPDATE
students[1] = "Barbara"
print("After update:", students)

# DELETE -- remove index 0
del students[0]
print("After delete index 0:", students)

# INSERT -- add "Diana" at index 1
students.insert(1, "Diana")
print("After insert:", students)

# LENGTH
print("Length:", len(students))
```

---

## Common Mistakes

### 1. Off-by-one errors

The most common array bug. Remember: an array of size `n` has valid indices `0` to `n-1`.

```python
arr = [10, 20, 30]
# WRONG: arr[3] -> IndexError (valid indices: 0, 1, 2)
# RIGHT: arr[2] -> 30
```

### 2. Modifying an array while iterating

```python
# WRONG -- skips elements
arr = [1, 2, 3, 4, 5]
for i in range(len(arr)):
    if arr[i] % 2 == 0:
        arr.pop(i)  # indices shift, next element is skipped

# RIGHT -- iterate in reverse or build a new list
arr = [1, 2, 3, 4, 5]
arr = [x for x in arr if x % 2 != 0]
```

### 3. Confusing length and last index

```go
slice := []int{10, 20, 30}
// len(slice) is 3
// Last valid index is 2 (len - 1)
lastElement := slice[len(slice)-1] // Correct
```

### 4. Forgetting that slices share underlying arrays (Go)

```go
original := []int{1, 2, 3, 4, 5}
sub := original[1:3] // [2, 3]
sub[0] = 99          // Also changes original[1] to 99!
```

### 5. Using == to compare arrays

```python
# Python: == compares values (works correctly)
[1, 2, 3] == [1, 2, 3]  # True

# Java: == compares references, not values
# int[] a = {1, 2, 3};
# int[] b = {1, 2, 3};
# a == b is FALSE. Use Arrays.equals(a, b) instead.
```

---

## Summary

| Concept               | Key Takeaway                                               |
| --------------------- | ---------------------------------------------------------- |
| Memory layout          | Contiguous block, each element same size                  |
| Indexing               | Zero-based in Go, Java, Python                            |
| Access                 | O(1) -- the defining strength of arrays                   |
| Search                 | O(n) unsorted, O(log n) sorted (binary search)            |
| Insert/Delete          | O(1) at end, O(n) at arbitrary position                   |
| Static array           | Fixed size, cannot grow                                   |
| Dynamic array          | Grows automatically (Go slice, Java ArrayList, Python list)|
| Cache friendliness     | Arrays are the most cache-friendly data structure         |
| Common pitfall         | Off-by-one errors and index out of bounds                 |

Arrays are the building block for nearly every other data structure. Master them before moving on to linked lists, stacks, queues, and trees.
