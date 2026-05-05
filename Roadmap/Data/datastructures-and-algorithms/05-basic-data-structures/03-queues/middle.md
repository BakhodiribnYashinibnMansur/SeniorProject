# Queue -- Middle Level

## Table of Contents

- [Prerequisites](#prerequisites)
- [Deque (Double-Ended Queue)](#deque-double-ended-queue)
  - [Go: Deque Implementation](#go-deque-implementation)
  - [Java: ArrayDeque](#java-arraydeque)
  - [Python: collections.deque](#python-collectionsdeque)
- [Priority Queue and Heap](#priority-queue-and-heap)
  - [Go: container/heap](#go-containerheap)
  - [Java: PriorityQueue](#java-priorityqueue)
  - [Python: heapq](#python-heapq)
- [BFS Using a Queue](#bfs-using-a-queue)
  - [Go: BFS](#go-bfs)
  - [Java: BFS](#java-bfs)
  - [Python: BFS](#python-bfs)
- [Circular Buffer (Ring Buffer)](#circular-buffer-ring-buffer)
- [Monotonic Queue](#monotonic-queue)
  - [Go: Monotonic Queue](#go-monotonic-queue)
  - [Java: Monotonic Queue](#java-monotonic-queue)
  - [Python: Monotonic Queue](#python-monotonic-queue)
- [Comparison: Queue vs Stack vs Deque](#comparison-queue-vs-stack-vs-deque)
- [Queue in Producer-Consumer Pattern](#queue-in-producer-consumer-pattern)
  - [Go: Channels as Queue](#go-channels-as-queue)
  - [Java: BlockingQueue](#java-blockingqueue)
  - [Python: queue.Queue](#python-queuequeue)
- [Summary](#summary)

---

## Prerequisites

You should be comfortable with:
- Queue basics (enqueue, dequeue, peek, FIFO principle)
- Array-based and linked-list-based queue implementations
- Big-O notation and basic algorithmic analysis
- Graph basics (nodes, edges) for the BFS section

---

## Deque (Double-Ended Queue)

A **deque** (pronounced "deck") allows insertion and removal at **both** ends in O(1) time. It generalizes both stacks and queues.

| Operation       | Description                | Time |
| --------------- | -------------------------- | ---- |
| AddFront        | Insert at the front        | O(1) |
| AddRear         | Insert at the rear         | O(1) |
| RemoveFront     | Remove from the front      | O(1) |
| RemoveRear      | Remove from the rear       | O(1) |
| PeekFront       | View front element         | O(1) |
| PeekRear        | View rear element          | O(1) |

### Go: Deque Implementation

Go does not have a built-in deque. Here is a doubly-linked list implementation:

```go
package main

import (
	"errors"
	"fmt"
)

type dequeNode struct {
	val        int
	prev, next *dequeNode
}

type Deque struct {
	front, rear *dequeNode
	size        int
}

func NewDeque() *Deque {
	return &Deque{}
}

func (d *Deque) AddFront(val int) {
	node := &dequeNode{val: val}
	if d.front == nil {
		d.front = node
		d.rear = node
	} else {
		node.next = d.front
		d.front.prev = node
		d.front = node
	}
	d.size++
}

func (d *Deque) AddRear(val int) {
	node := &dequeNode{val: val}
	if d.rear == nil {
		d.front = node
		d.rear = node
	} else {
		node.prev = d.rear
		d.rear.next = node
		d.rear = node
	}
	d.size++
}

func (d *Deque) RemoveFront() (int, error) {
	if d.size == 0 {
		return 0, errors.New("deque is empty")
	}
	val := d.front.val
	d.front = d.front.next
	if d.front != nil {
		d.front.prev = nil
	} else {
		d.rear = nil
	}
	d.size--
	return val, nil
}

func (d *Deque) RemoveRear() (int, error) {
	if d.size == 0 {
		return 0, errors.New("deque is empty")
	}
	val := d.rear.val
	d.rear = d.rear.prev
	if d.rear != nil {
		d.rear.next = nil
	} else {
		d.front = nil
	}
	d.size--
	return val, nil
}

func main() {
	d := NewDeque()
	d.AddRear(1)
	d.AddRear(2)
	d.AddFront(0)
	// Deque: 0, 1, 2

	v, _ := d.RemoveFront()
	fmt.Println("Front:", v) // 0
	v, _ = d.RemoveRear()
	fmt.Println("Rear:", v)  // 2
}
```

### Java: ArrayDeque

Java provides `java.util.ArrayDeque`, a resizable circular array implementation.

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class DequeExample {
    public static void main(String[] args) {
        Deque<Integer> dq = new ArrayDeque<>();

        dq.addFirst(10);   // front
        dq.addLast(20);    // rear
        dq.addFirst(5);    // front
        // Deque: [5, 10, 20]

        System.out.println("Front: " + dq.peekFirst());  // 5
        System.out.println("Rear: " + dq.peekLast());    // 20

        System.out.println("Removed front: " + dq.removeFirst()); // 5
        System.out.println("Removed rear: " + dq.removeLast());   // 20
        // Deque: [10]
    }
}
```

### Python: collections.deque

Python's `collections.deque` is a doubly-linked list of fixed-size blocks, providing O(1) at both ends.

```python
from collections import deque

dq = deque()

dq.append(10)       # add to rear
dq.appendleft(5)    # add to front
dq.append(20)       # add to rear
# deque: [5, 10, 20]

print(f"Front: {dq[0]}")   # 5
print(f"Rear: {dq[-1]}")   # 20

print(f"Pop front: {dq.popleft()}")  # 5
print(f"Pop rear: {dq.pop()}")       # 20
# deque: [10]
```

---

## Priority Queue and Heap

A **priority queue** removes elements based on priority, not insertion order. The highest-priority element (typically the smallest or largest value) is dequeued first.

Internally, priority queues use a **binary heap** -- a complete binary tree stored in an array where each parent has higher priority than its children.

| Operation      | Time Complexity |
| -------------- | --------------- |
| Insert         | O(log n)        |
| Extract min/max| O(log n)        |
| Peek min/max   | O(1)            |

### Go: container/heap

```go
package main

import (
	"container/heap"
	"fmt"
)

// IntHeap implements heap.Interface for a min-heap of ints.
type IntHeap []int

func (h IntHeap) Len() int           { return len(h) }
func (h IntHeap) Less(i, j int) bool { return h[i] < h[j] }
func (h IntHeap) Swap(i, j int)      { h[i], h[j] = h[j], h[i] }

func (h *IntHeap) Push(x interface{}) {
	*h = append(*h, x.(int))
}

func (h *IntHeap) Pop() interface{} {
	old := *h
	n := len(old)
	x := old[n-1]
	*h = old[:n-1]
	return x
}

func main() {
	h := &IntHeap{5, 3, 8, 1}
	heap.Init(h)

	heap.Push(h, 2)

	for h.Len() > 0 {
		fmt.Print(heap.Pop(h), " ") // 1 2 3 5 8
	}
	fmt.Println()
}
```

### Java: PriorityQueue

```java
import java.util.PriorityQueue;

public class PQExample {
    public static void main(String[] args) {
        // Min-heap by default
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        pq.offer(5);
        pq.offer(3);
        pq.offer(8);
        pq.offer(1);
        pq.offer(2);

        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " "); // 1 2 3 5 8
        }
        System.out.println();

        // Max-heap: reverse comparator
        PriorityQueue<Integer> maxPq = new PriorityQueue<>((a, b) -> b - a);
        maxPq.offer(5);
        maxPq.offer(3);
        maxPq.offer(8);
        System.out.println("Max: " + maxPq.poll()); // 8
    }
}
```

### Python: heapq

```python
import heapq

# Min-heap
nums = [5, 3, 8, 1, 2]
heapq.heapify(nums)  # in-place, O(n)

heapq.heappush(nums, 4)

while nums:
    print(heapq.heappop(nums), end=" ")  # 1 2 3 4 5 8
print()

# Max-heap trick: negate values
max_heap = []
for val in [5, 3, 8, 1]:
    heapq.heappush(max_heap, -val)

print("Max:", -heapq.heappop(max_heap))  # 8
```

---

## BFS Using a Queue

**Breadth-First Search (BFS)** explores a graph level by level using a queue. It visits all neighbors of a node before moving to the next level. BFS finds the shortest path in unweighted graphs.

```
Algorithm:
1. Enqueue the start node, mark it visited.
2. While queue is not empty:
   a. Dequeue a node.
   b. Process it.
   c. Enqueue all unvisited neighbors, mark them visited.
```

### Go: BFS

```go
package main

import "fmt"

func bfs(graph map[int][]int, start int) []int {
	visited := make(map[int]bool)
	queue := []int{start}
	visited[start] = true
	order := []int{}

	for len(queue) > 0 {
		node := queue[0]
		queue = queue[1:]
		order = append(order, node)

		for _, neighbor := range graph[node] {
			if !visited[neighbor] {
				visited[neighbor] = true
				queue = append(queue, neighbor)
			}
		}
	}
	return order
}

func main() {
	graph := map[int][]int{
		0: {1, 2},
		1: {0, 3, 4},
		2: {0, 4},
		3: {1},
		4: {1, 2},
	}
	fmt.Println(bfs(graph, 0)) // [0 1 2 3 4]
}
```

### Java: BFS

```java
import java.util.*;

public class BFS {
    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> order = new ArrayList<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            order.add(node);

            for (int neighbor : graph.getOrDefault(node, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return order;
    }

    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = Map.of(
            0, List.of(1, 2),
            1, List.of(0, 3, 4),
            2, List.of(0, 4),
            3, List.of(1),
            4, List.of(1, 2)
        );
        System.out.println(bfs(graph, 0)); // [0, 1, 2, 3, 4]
    }
}
```

### Python: BFS

```python
from collections import deque

def bfs(graph, start):
    visited = {start}
    queue = deque([start])
    order = []

    while queue:
        node = queue.popleft()
        order.append(node)

        for neighbor in graph.get(node, []):
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)

    return order

graph = {
    0: [1, 2],
    1: [0, 3, 4],
    2: [0, 4],
    3: [1],
    4: [1, 2],
}
print(bfs(graph, 0))  # [0, 1, 2, 3, 4]
```

---

## Circular Buffer (Ring Buffer)

A circular buffer is a fixed-size queue that overwrites the oldest data when full. This is different from the circular queue in the junior level (which rejects new elements when full).

Use cases: logging systems, audio buffers, network packet capture, real-time sensor data.

```
Overwrite behavior:
  Buffer capacity = 4
  Write: A B C D      -> [A][B][C][D]  (full)
  Write: E            -> [E][B][C][D]  (A overwritten, oldest data lost)
                           ^        ^
                          rear    front (front advances too)
```

The key difference: when the buffer is full, enqueue overwrites the front and advances both front and rear.

---

## Monotonic Queue

A **monotonic queue** maintains elements in increasing (or decreasing) order. It efficiently solves the **sliding window minimum/maximum** problem in O(n) total time.

The key insight: when adding a new element, remove all elements from the rear that are less useful (smaller for max-queue, larger for min-queue).

### Go: Monotonic Queue

```go
package main

import "fmt"

// slidingWindowMax returns the maximum in each window of size k.
func slidingWindowMax(nums []int, k int) []int {
	// deque stores indices, not values
	deque := []int{}
	result := []int{}

	for i := 0; i < len(nums); i++ {
		// Remove indices outside the window
		if len(deque) > 0 && deque[0] <= i-k {
			deque = deque[1:]
		}
		// Remove smaller elements from rear (they will never be the max)
		for len(deque) > 0 && nums[deque[len(deque)-1]] <= nums[i] {
			deque = deque[:len(deque)-1]
		}
		deque = append(deque, i)

		// Window is fully formed starting at i >= k-1
		if i >= k-1 {
			result = append(result, nums[deque[0]])
		}
	}
	return result
}

func main() {
	nums := []int{1, 3, -1, -3, 5, 3, 6, 7}
	fmt.Println(slidingWindowMax(nums, 3)) // [3, 3, 5, 5, 6, 7]
}
```

### Java: Monotonic Queue

```java
import java.util.*;

public class MonotonicQueue {
    public static int[] slidingWindowMax(int[] nums, int k) {
        Deque<Integer> deque = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];
        int idx = 0;

        for (int i = 0; i < nums.length; i++) {
            // Remove indices outside the window
            if (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            // Remove smaller elements from rear
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);

            if (i >= k - 1) {
                result[idx++] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        System.out.println(Arrays.toString(slidingWindowMax(nums, 3)));
        // [3, 3, 5, 5, 6, 7]
    }
}
```

### Python: Monotonic Queue

```python
from collections import deque

def sliding_window_max(nums, k):
    dq = deque()  # stores indices
    result = []

    for i, val in enumerate(nums):
        # Remove indices outside the window
        if dq and dq[0] <= i - k:
            dq.popleft()
        # Remove smaller elements from rear
        while dq and nums[dq[-1]] <= val:
            dq.pop()
        dq.append(i)

        if i >= k - 1:
            result.append(nums[dq[0]])

    return result

nums = [1, 3, -1, -3, 5, 3, 6, 7]
print(sliding_window_max(nums, 3))  # [3, 3, 5, 5, 6, 7]
```

---

## Comparison: Queue vs Stack vs Deque

| Feature            | Queue              | Stack              | Deque                     |
| ------------------ | ------------------ | ------------------ | ------------------------- |
| Order              | FIFO               | LIFO               | Both FIFO and LIFO        |
| Add                | Rear only          | Top only           | Front or rear             |
| Remove             | Front only         | Top only           | Front or rear             |
| Typical use        | BFS, scheduling    | DFS, undo, parsing | Sliding window, palindrome |
| Access points      | 2 (front, rear)    | 1 (top)            | 2 (front, rear)           |
| Can simulate stack | No                 | --                 | Yes (use one end only)    |
| Can simulate queue | --                 | Need 2 stacks      | Yes (add rear, remove front) |

---

## Queue in Producer-Consumer Pattern

The **producer-consumer pattern** is a classic concurrency pattern where producers add items to a shared queue and consumers remove items from it. The queue decouples producers from consumers and handles rate differences between them.

### Go: Channels as Queue

Go channels are built-in concurrent queues.

```go
package main

import (
	"fmt"
	"sync"
	"time"
)

func producer(ch chan<- int, id int, wg *sync.WaitGroup) {
	defer wg.Done()
	for i := 0; i < 5; i++ {
		val := id*100 + i
		ch <- val
		fmt.Printf("Producer %d: sent %d\n", id, val)
		time.Sleep(50 * time.Millisecond)
	}
}

func consumer(ch <-chan int, id int, wg *sync.WaitGroup) {
	defer wg.Done()
	for val := range ch {
		fmt.Printf("  Consumer %d: received %d\n", id, val)
		time.Sleep(100 * time.Millisecond)
	}
}

func main() {
	ch := make(chan int, 10) // buffered channel = queue with capacity 10
	var prodWg, consWg sync.WaitGroup

	// Start 2 producers
	for i := 0; i < 2; i++ {
		prodWg.Add(1)
		go producer(ch, i, &prodWg)
	}

	// Start 3 consumers
	for i := 0; i < 3; i++ {
		consWg.Add(1)
		go consumer(ch, i, &consWg)
	}

	prodWg.Wait()
	close(ch) // signal consumers that no more data is coming
	consWg.Wait()
	fmt.Println("All done.")
}
```

### Java: BlockingQueue

```java
import java.util.concurrent.*;

public class ProducerConsumer {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        // Producer
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.put(i);  // blocks if full
                    System.out.println("Produced: " + i);
                    Thread.sleep(50);
                }
                queue.put(-1);  // sentinel to signal completion
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int val = queue.take();  // blocks if empty
                    if (val == -1) break;    // sentinel received
                    System.out.println("  Consumed: " + val);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("All done.");
    }
}
```

### Python: queue.Queue

```python
import queue
import threading
import time

def producer(q, producer_id):
    for i in range(5):
        val = producer_id * 100 + i
        q.put(val)  # blocks if full
        print(f"Producer {producer_id}: sent {val}")
        time.sleep(0.05)

def consumer(q, consumer_id):
    while True:
        try:
            val = q.get(timeout=1)  # blocks if empty, timeout after 1s
            print(f"  Consumer {consumer_id}: received {val}")
            q.task_done()
            time.sleep(0.1)
        except queue.Empty:
            break

q = queue.Queue(maxsize=10)

producers = [threading.Thread(target=producer, args=(q, i)) for i in range(2)]
consumers = [threading.Thread(target=consumer, args=(q, i)) for i in range(3)]

for t in producers + consumers:
    t.start()
for t in producers:
    t.join()
q.join()  # wait until all items are processed
print("All done.")
```

---

## Summary

| Concept                | Key Takeaway                                                    |
| ---------------------- | --------------------------------------------------------------- |
| Deque                  | Double-ended queue, O(1) add/remove at both ends                |
| Priority queue         | Dequeues by priority (min or max), uses a binary heap           |
| BFS                    | Level-by-level graph traversal using a queue                    |
| Circular buffer        | Fixed-size buffer that overwrites oldest data when full         |
| Monotonic queue        | Maintains sorted order for sliding window min/max in O(n)       |
| Queue vs stack vs deque| Queue = FIFO, stack = LIFO, deque = both                       |
| Producer-consumer      | Queue decouples producers and consumers in concurrent systems   |

Next steps: move on to the **senior level** to learn about message queues, concurrent queues, rate limiting, task scheduling, and back-pressure.
