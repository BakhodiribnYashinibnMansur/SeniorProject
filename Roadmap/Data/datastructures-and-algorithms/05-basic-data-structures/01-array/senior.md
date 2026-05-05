# Array -- Senior Level

## Table of Contents

- [Overview](#overview)
- [Ring Buffers and Circular Queues](#ring-buffers-and-circular-queues)
- [Memory-Mapped Arrays](#memory-mapped-arrays)
- [SIMD Operations on Arrays](#simd-operations-on-arrays)
- [Arrays in Databases: Column Stores](#arrays-in-databases-column-stores)
- [Concurrent Array Access](#concurrent-array-access)
- [Lock-Free Array Structures](#lock-free-array-structures)
- [Design Decisions and Trade-offs](#design-decisions-and-trade-offs)
- [Summary](#summary)

---

## Overview

At the senior level, arrays are not just a data structure -- they are a design primitive that underpins systems from real-time message queues to columnar databases. This document covers how arrays are used in system design, how to exploit hardware-level optimizations, and how to handle concurrent access safely.

---

## Ring Buffers and Circular Queues

A **ring buffer** (circular buffer) uses a fixed-size array with two pointers (`head` and `tail`) that wrap around. It provides O(1) enqueue and dequeue without shifting elements and without dynamic memory allocation.

Use cases: network packet buffers, audio processing, producer-consumer queues, logging systems, kernel I/O buffers.

**Go:**

```go
package main

import (
    "errors"
    "fmt"
)

type RingBuffer struct {
    data     []int
    head     int // next read position
    tail     int // next write position
    size     int
    capacity int
}

func NewRingBuffer(capacity int) *RingBuffer {
    return &RingBuffer{
        data:     make([]int, capacity),
        capacity: capacity,
    }
}

func (rb *RingBuffer) Enqueue(val int) error {
    if rb.size == rb.capacity {
        return errors.New("buffer full")
    }
    rb.data[rb.tail] = val
    rb.tail = (rb.tail + 1) % rb.capacity
    rb.size++
    return nil
}

func (rb *RingBuffer) Dequeue() (int, error) {
    if rb.size == 0 {
        return 0, errors.New("buffer empty")
    }
    val := rb.data[rb.head]
    rb.head = (rb.head + 1) % rb.capacity
    rb.size--
    return val, nil
}

func main() {
    rb := NewRingBuffer(4)
    rb.Enqueue(10)
    rb.Enqueue(20)
    rb.Enqueue(30)
    val, _ := rb.Dequeue()
    fmt.Println("Dequeued:", val) // 10
    rb.Enqueue(40)
    rb.Enqueue(50) // wraps around
    for rb.size > 0 {
        v, _ := rb.Dequeue()
        fmt.Println(v)
    }
}
```

**Java:**

```java
public class RingBuffer {
    private int[] data;
    private int head, tail, size, capacity;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
    }

    public boolean enqueue(int val) {
        if (size == capacity) return false;
        data[tail] = val;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public int dequeue() {
        if (size == 0) throw new RuntimeException("Buffer empty");
        int val = data[head];
        head = (head + 1) % capacity;
        size--;
        return val;
    }

    public static void main(String[] args) {
        RingBuffer rb = new RingBuffer(4);
        rb.enqueue(10);
        rb.enqueue(20);
        rb.enqueue(30);
        System.out.println("Dequeued: " + rb.dequeue()); // 10
    }
}
```

**Python:**

```python
class RingBuffer:
    def __init__(self, capacity):
        self.data = [None] * capacity
        self.head = 0
        self.tail = 0
        self.size = 0
        self.capacity = capacity

    def enqueue(self, val):
        if self.size == self.capacity:
            raise OverflowError("Buffer full")
        self.data[self.tail] = val
        self.tail = (self.tail + 1) % self.capacity
        self.size += 1

    def dequeue(self):
        if self.size == 0:
            raise IndexError("Buffer empty")
        val = self.data[self.head]
        self.head = (self.head + 1) % self.capacity
        self.size -= 1
        return val

# Also available: collections.deque(maxlen=N) is a built-in ring buffer
from collections import deque
rb = deque(maxlen=4)
rb.append(10)
rb.append(20)
print(rb.popleft())  # 10
```

### Design considerations

- **Power-of-two capacity**: Use capacity = 2^k so modulo becomes a bitwise AND: `(tail + 1) & (capacity - 1)`. This is significantly faster.
- **Full vs empty ambiguity**: When head == tail, is the buffer full or empty? Solutions: track size separately, waste one slot, or use a boolean flag.

---

## Memory-Mapped Arrays

Memory-mapped files (`mmap`) let you map a file directly into the process's virtual address space, treating it as an array. The OS handles paging data in and out as needed.

Use cases: databases, large dataset processing, shared memory between processes.

**Go:**

```go
package main

import (
    "fmt"
    "os"
    "syscall"
    "unsafe"
)

func main() {
    f, _ := os.OpenFile("data.bin", os.O_RDWR|os.O_CREATE, 0644)
    defer f.Close()

    size := 4096
    f.Truncate(int64(size))

    data, _ := syscall.Mmap(int(f.Fd()), 0, size,
        syscall.PROT_READ|syscall.PROT_WRITE, syscall.MAP_SHARED)
    defer syscall.Munmap(data)

    // Write to the memory-mapped region as if it were a byte array
    copy(data[0:5], []byte("Hello"))
    fmt.Println("First 5 bytes:", string(data[0:5]))
}
```

**Java:**

```java
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class MMapExample {
    public static void main(String[] args) throws Exception {
        RandomAccessFile file = new RandomAccessFile("data.bin", "rw");
        file.setLength(4096);
        FileChannel channel = file.getChannel();

        MappedByteBuffer buffer = channel.map(
            FileChannel.MapMode.READ_WRITE, 0, 4096);

        // Write integers as if the file were an array
        buffer.putInt(0, 42);
        buffer.putInt(4, 99);

        System.out.println("Value at offset 0: " + buffer.getInt(0));
        System.out.println("Value at offset 4: " + buffer.getInt(4));

        channel.close();
        file.close();
    }
}
```

**Python:**

```python
import mmap
import struct

with open("data.bin", "w+b") as f:
    f.write(b'\x00' * 4096)

with open("data.bin", "r+b") as f:
    mm = mmap.mmap(f.fileno(), 4096)

    # Treat as an array of integers
    struct.pack_into("i", mm, 0, 42)    # write 42 at offset 0
    struct.pack_into("i", mm, 4, 99)    # write 99 at offset 4

    val = struct.unpack_from("i", mm, 0)[0]
    print(f"Value at offset 0: {val}")   # 42

    mm.close()
```

### When to use mmap

- File is too large to fit in RAM (the OS pages in only what is needed)
- Multiple processes need shared access to the same data
- You want to avoid explicit read/write system calls (the OS handles I/O transparently)

---

## SIMD Operations on Arrays

**SIMD** (Single Instruction, Multiple Data) processes multiple array elements in a single CPU instruction. A 256-bit SIMD register can process 8 floats or 4 doubles simultaneously.

Modern compilers often auto-vectorize simple array loops. You can also use intrinsics for explicit control.

**Go (auto-vectorization):**

Go's compiler auto-vectorizes simple loops. For explicit SIMD, use assembly or packages like `github.com/klauspost/cpuid`.

```go
// This loop is auto-vectorized by the Go compiler (gc 1.21+)
func addArrays(a, b, result []float32) {
    for i := range a {
        result[i] = a[i] + b[i]
    }
}
```

**Java (Vector API, JDK 16+):**

```java
import jdk.incubator.vector.*;

public class SIMDExample {
    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_256;

    public static void addArrays(float[] a, float[] b, float[] result) {
        int i = 0;
        int upperBound = SPECIES.loopBound(a.length);

        for (; i < upperBound; i += SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
            va.add(vb).intoArray(result, i);
        }

        // Handle tail elements
        for (; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
    }
}
```

**Python (NumPy -- uses SIMD internally):**

```python
import numpy as np

a = np.array([1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0], dtype=np.float32)
b = np.array([10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0], dtype=np.float32)
result = a + b  # Uses SIMD internally via BLAS/LAPACK
print(result)   # [11. 22. 33. 44. 55. 66. 77. 88.]
```

SIMD can provide 4-8x speedup for numeric array operations compared to scalar loops.

---

## Arrays in Databases: Column Stores

Traditional row-oriented databases store each row contiguously:

```
Row store: [id=1, name="Alice", age=30] [id=2, name="Bob", age=25] ...
```

**Column-oriented databases** (ClickHouse, Apache Parquet, DuckDB, BigQuery) store each column as a contiguous array:

```
Column store:
  id_array:   [1, 2, 3, 4, ...]
  name_array: ["Alice", "Bob", "Charlie", "Diana", ...]
  age_array:  [30, 25, 35, 28, ...]
```

Advantages of columnar storage:

| Advantage             | Explanation                                                    |
| --------------------- | -------------------------------------------------------------- |
| Cache efficiency       | Scanning one column reads contiguous memory                   |
| Compression           | Same-type values in a column compress much better             |
| SIMD-friendly         | Numeric columns can be processed with SIMD instructions       |
| Query optimization    | Only read columns needed for the query (column pruning)       |
| Aggregation speed     | SUM(age), AVG(age), etc. scan a single contiguous array       |

This is fundamentally an array-level optimization: store related data contiguously to exploit cache lines and SIMD.

---

## Concurrent Array Access

When multiple threads/goroutines access the same array, you must handle synchronization carefully.

**Go -- protecting a shared array with a mutex:**

```go
package main

import (
    "fmt"
    "sync"
)

type SafeArray struct {
    mu   sync.RWMutex
    data []int
}

func (sa *SafeArray) Get(index int) int {
    sa.mu.RLock()
    defer sa.mu.RUnlock()
    return sa.data[index]
}

func (sa *SafeArray) Set(index int, val int) {
    sa.mu.Lock()
    defer sa.mu.Unlock()
    sa.data[index] = val
}

func (sa *SafeArray) Append(val int) {
    sa.mu.Lock()
    defer sa.mu.Unlock()
    sa.data = append(sa.data, val)
}

func main() {
    sa := &SafeArray{data: make([]int, 10)}
    var wg sync.WaitGroup
    for i := 0; i < 10; i++ {
        wg.Add(1)
        go func(idx int) {
            defer wg.Done()
            sa.Set(idx, idx*100)
        }(i)
    }
    wg.Wait()
    fmt.Println(sa.data)
}
```

**Java -- CopyOnWriteArrayList for read-heavy workloads:**

```java
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentArrayExample {
    public static void main(String[] args) throws InterruptedException {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();

        // Writers create a new copy on each modification
        // Readers never block
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                list.add(i);
            }
        });

        Thread reader = new Thread(() -> {
            for (int val : list) {
                // Safe: iterates over a snapshot
                System.out.println(val);
            }
        });

        writer.start();
        reader.start();
        writer.join();
        reader.join();
    }
}
```

**Python -- threading.Lock for shared list:**

```python
import threading

class SafeArray:
    def __init__(self):
        self.data = []
        self.lock = threading.Lock()

    def append(self, val):
        with self.lock:
            self.data.append(val)

    def get(self, index):
        with self.lock:
            return self.data[index]

sa = SafeArray()
threads = []
for i in range(10):
    t = threading.Thread(target=sa.append, args=(i,))
    threads.append(t)
    t.start()
for t in threads:
    t.join()
print(sa.data)
```

### Strategies for concurrent arrays

| Strategy                | Read Cost | Write Cost | Best For                         |
| ----------------------- | --------- | ---------- | -------------------------------- |
| Mutex (RWMutex)         | Low       | Low        | General purpose                  |
| CopyOnWrite             | O(1)      | O(n)       | Read-heavy, rare writes          |
| Sharded array           | Low       | Low        | High contention, partitionable   |
| Lock-free (CAS)         | Low       | Low        | Performance-critical paths       |
| Immutable arrays        | O(1)      | N/A        | Functional/event-sourcing        |

---

## Lock-Free Array Structures

Lock-free structures use **atomic compare-and-swap (CAS)** operations instead of locks. They guarantee that at least one thread makes progress, avoiding deadlocks.

**Go -- atomic operations on array elements:**

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
)

func main() {
    data := make([]int64, 10)

    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            // Atomically increment element at index 0
            atomic.AddInt64(&data[0], 1)
        }()
    }
    wg.Wait()
    fmt.Println("data[0] =", data[0]) // Always 1000
}
```

**Java -- AtomicIntegerArray:**

```java
import java.util.concurrent.atomic.AtomicIntegerArray;

public class LockFreeArray {
    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerArray arr = new AtomicIntegerArray(10);

        Thread[] threads = new Thread[1000];
        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> arr.incrementAndGet(0));
            threads[i].start();
        }
        for (Thread t : threads) t.join();

        System.out.println("arr[0] = " + arr.get(0)); // Always 1000
    }
}
```

**Python (limited due to GIL, but useful for multiprocessing):**

```python
from multiprocessing import Array, Process

def increment(shared_arr, index, count):
    for _ in range(count):
        # Note: individual operations are not atomic without a lock
        # For true lock-free behavior, use multiprocessing.Value with lock=False
        shared_arr[index] += 1

# For true atomic array access in Python, consider:
# 1. multiprocessing.Array with explicit locks
# 2. numpy arrays with shared memory
# 3. Using C extensions
```

---

## Design Decisions and Trade-offs

When designing systems that heavily use arrays, consider:

| Decision                    | Trade-off                                                  |
| --------------------------- | ---------------------------------------------------------- |
| Fixed vs dynamic size       | Fixed: predictable memory; Dynamic: flexibility            |
| Growth factor (1.5x vs 2x) | 2x: fewer resizes; 1.5x: less wasted memory               |
| Array of structs vs struct of arrays | AoS: better for random access; SoA: better for SIMD/columnar |
| In-place vs copy-on-write   | In-place: faster writes; CoW: safe concurrent reads        |
| Contiguous vs segmented     | Contiguous: cache-friendly; Segmented: avoids large allocs |

---

## Summary

| Topic                  | Key Takeaway                                                |
| ---------------------- | ----------------------------------------------------------- |
| Ring buffers           | O(1) queue with no allocation, ideal for bounded producers  |
| Memory-mapped arrays   | OS-managed paging for files larger than RAM                 |
| SIMD                   | 4-8x speedup for numeric array operations                  |
| Column stores          | Arrays of same-type data enable compression and SIMD        |
| Concurrent access      | RWMutex, CopyOnWrite, or sharding depending on workload    |
| Lock-free arrays       | Atomic CAS for high-throughput concurrent element updates   |
