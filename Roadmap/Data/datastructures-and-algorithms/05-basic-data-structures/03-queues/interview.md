# Queue -- Interview Questions

## Table of Contents

- [Junior Level Questions](#junior-level-questions)
- [Middle Level Questions](#middle-level-questions)
- [Senior Level Questions](#senior-level-questions)
- [Coding Challenge: Implement Queue Using Two Stacks](#coding-challenge-implement-queue-using-two-stacks)
- [Coding Challenge: Sliding Window Maximum](#coding-challenge-sliding-window-maximum)

---

## Junior Level Questions

### Q1. What is a queue and what ordering principle does it follow?

**Answer:** A queue is a linear data structure that follows the FIFO (First-In, First-Out) principle. The element that is added first is the first one removed. Elements enter at the rear and leave from the front. All core operations (enqueue, dequeue, peek, isEmpty) run in O(1) time.

### Q2. What is the difference between an array-based queue and a linked list-based queue?

**Answer:** An array-based queue stores elements in a contiguous block of memory. It has good cache locality but can waste space at the front after dequeues (unless using a circular buffer). A linked list-based queue uses nodes with pointers. It wastes no space but has worse cache locality and extra memory overhead per node for the pointer. Both achieve O(1) enqueue and dequeue.

### Q3. What is a circular queue and why is it needed?

**Answer:** A circular queue (ring buffer) treats a fixed-size array as if it wraps around. When the rear pointer reaches the end of the array, it wraps to index 0 using modulo arithmetic: `next = (current + 1) % capacity`. This solves the wasted-space problem of a naive array queue, where dequeued positions at the front are never reused.

### Q4. What happens if you dequeue from an empty queue?

**Answer:** It depends on the implementation. In most implementations, it throws an exception (Java: `NoSuchElementException`), returns an error (Go: error value), or raises an exception (Python: `IndexError`). You should always check `isEmpty()` before dequeuing, or use a variant like Java's `poll()` which returns `null` instead of throwing.

### Q5. Name three real-world applications of queues.

**Answer:** (1) **BFS graph traversal** -- nodes are explored level by level using a queue. (2) **Task scheduling in operating systems** -- processes wait in a ready queue for CPU time. (3) **Print spooler** -- documents are printed in the order they are submitted. Other examples: web server request handling, message passing between threads, network packet buffering.

---

## Middle Level Questions

### Q6. What is the difference between a queue, a deque, and a priority queue?

**Answer:** A **queue** supports add-at-rear and remove-from-front (FIFO). A **deque** (double-ended queue) supports add and remove at both ends in O(1). A **priority queue** removes the highest-priority element first (not necessarily FIFO); it is typically backed by a binary heap with O(log n) insert and extract. A deque can simulate both a queue and a stack. A priority queue cannot efficiently simulate FIFO ordering.

### Q7. How does BFS use a queue, and why can't it use a stack?

**Answer:** BFS enqueues the start node and then repeatedly dequeues a node, processes it, and enqueues all unvisited neighbors. The FIFO ordering ensures nodes are processed level by level. If you replace the queue with a stack, you get DFS (depth-first search), which explores as deep as possible before backtracking. BFS finds the shortest path in unweighted graphs; DFS does not.

### Q8. What is a monotonic queue and when would you use it?

**Answer:** A monotonic queue is a deque that maintains elements in non-increasing (or non-decreasing) order. When adding a new element, all elements from the rear that violate the monotonic property are removed. This enables O(1) access to the current minimum or maximum. The primary use case is the **sliding window maximum/minimum** problem, which it solves in O(n) total time (amortized O(1) per element).

### Q9. Explain the producer-consumer pattern using queues.

**Answer:** Producers generate items and enqueue them into a shared, thread-safe queue. Consumers dequeue items and process them. The queue decouples the two: producers and consumers run independently and at different speeds. If the queue is bounded, it provides back-pressure (producers block when the queue is full). Implementations: Go channels, Java `BlockingQueue`, Python `queue.Queue`.

### Q10. Can you implement a queue using two stacks? What is the time complexity?

**Answer:** Yes. Use an inbox stack for enqueue (push) and an outbox stack for dequeue. When dequeue is called and the outbox is empty, transfer all elements from inbox to outbox (this reverses the order). Each element is moved at most once from inbox to outbox, so the **amortized** cost per operation is O(1). Worst-case single dequeue is O(n).

---

## Senior Level Questions

### Q11. Compare Kafka, RabbitMQ, and SQS as message queue solutions.

**Answer:** **Kafka** is a distributed log for high-throughput event streaming with replay capability, ordering per partition, and configurable retention. **RabbitMQ** is an AMQP broker with rich routing (exchanges, bindings), per-message acknowledgments, and lower throughput. **SQS** is a fully managed AWS service with standard (at-least-once, high throughput) and FIFO (exactly-once, strict ordering) modes. Choose Kafka for event sourcing/streaming, RabbitMQ for complex routing and task queues, SQS for serverless architectures.

### Q12. What is back-pressure and how do you implement it?

**Answer:** Back-pressure is a mechanism where a slow consumer signals producers to slow down, preventing unbounded queue growth and OOM. Strategies include: bounded queues (block or reject when full), caller-runs policy (producer handles the task itself), dropping oldest/newest messages, and adaptive rate limiting. In Go, a bounded channel with `select/default` provides non-blocking back-pressure. In Java, `ThreadPoolExecutor` with `CallerRunsPolicy` makes the submitting thread run the rejected task.

### Q13. What is the Michael-Scott lock-free queue?

**Answer:** The Michael-Scott queue (1996) is a lock-free concurrent FIFO queue using a singly-linked list with a sentinel node and two atomic pointers (Head, Tail). Enqueue CAS-es a new node onto Tail.next, then advances Tail. Dequeue CAS-es Head forward. A helping mechanism advances a lagging Tail. It guarantees global progress (lock-free) but not per-thread bounded steps (not wait-free). The ABA problem is solved via tagged pointers or garbage collection.

### Q14. What is the difference between lock-free and wait-free?

**Answer:** **Lock-free** guarantees that at least one thread makes progress in a finite number of steps (system-wide progress, but individual threads may starve). **Wait-free** guarantees that every thread completes its operation in a bounded number of steps (no starvation possible). Wait-free is strictly stronger but typically 2-5x slower due to the helping mechanism. In practice, lock-free with backoff is preferred; wait-free is used only in hard real-time systems.

---

## Coding Challenge: Implement Queue Using Two Stacks

**Problem:** Implement a queue (FIFO) using only two stacks (LIFO). Support `enqueue`, `dequeue`, and `peek`. All operations must be amortized O(1).

**Go:**

```go
package main

import (
	"errors"
	"fmt"
)

type StackQueue struct {
	inbox  []int // enqueue pushes here
	outbox []int // dequeue pops here
}

func NewStackQueue() *StackQueue {
	return &StackQueue{}
}

func (sq *StackQueue) Enqueue(val int) {
	sq.inbox = append(sq.inbox, val)
}

func (sq *StackQueue) transfer() {
	if len(sq.outbox) == 0 {
		for len(sq.inbox) > 0 {
			top := sq.inbox[len(sq.inbox)-1]
			sq.inbox = sq.inbox[:len(sq.inbox)-1]
			sq.outbox = append(sq.outbox, top)
		}
	}
}

func (sq *StackQueue) Dequeue() (int, error) {
	sq.transfer()
	if len(sq.outbox) == 0 {
		return 0, errors.New("queue is empty")
	}
	val := sq.outbox[len(sq.outbox)-1]
	sq.outbox = sq.outbox[:len(sq.outbox)-1]
	return val, nil
}

func (sq *StackQueue) Peek() (int, error) {
	sq.transfer()
	if len(sq.outbox) == 0 {
		return 0, errors.New("queue is empty")
	}
	return sq.outbox[len(sq.outbox)-1], nil
}

func main() {
	q := NewStackQueue()
	q.Enqueue(1)
	q.Enqueue(2)
	q.Enqueue(3)

	val, _ := q.Dequeue()
	fmt.Println(val) // 1
	val, _ = q.Dequeue()
	fmt.Println(val) // 2

	q.Enqueue(4)
	val, _ = q.Dequeue()
	fmt.Println(val) // 3
	val, _ = q.Dequeue()
	fmt.Println(val) // 4
}
```

**Java:**

```java
import java.util.NoSuchElementException;
import java.util.Stack;

public class StackQueue {
    private Stack<Integer> inbox = new Stack<>();
    private Stack<Integer> outbox = new Stack<>();

    public void enqueue(int val) {
        inbox.push(val);
    }

    private void transfer() {
        if (outbox.isEmpty()) {
            while (!inbox.isEmpty()) {
                outbox.push(inbox.pop());
            }
        }
    }

    public int dequeue() {
        transfer();
        if (outbox.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return outbox.pop();
    }

    public int peek() {
        transfer();
        if (outbox.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return outbox.peek();
    }

    public static void main(String[] args) {
        StackQueue q = new StackQueue();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);

        System.out.println(q.dequeue()); // 1
        System.out.println(q.dequeue()); // 2

        q.enqueue(4);
        System.out.println(q.dequeue()); // 3
        System.out.println(q.dequeue()); // 4
    }
}
```

**Python:**

```python
class StackQueue:
    def __init__(self):
        self._inbox = []
        self._outbox = []

    def enqueue(self, val):
        self._inbox.append(val)

    def _transfer(self):
        if not self._outbox:
            while self._inbox:
                self._outbox.append(self._inbox.pop())

    def dequeue(self):
        self._transfer()
        if not self._outbox:
            raise IndexError("Queue is empty")
        return self._outbox.pop()

    def peek(self):
        self._transfer()
        if not self._outbox:
            raise IndexError("Queue is empty")
        return self._outbox[-1]


q = StackQueue()
q.enqueue(1)
q.enqueue(2)
q.enqueue(3)

print(q.dequeue())  # 1
print(q.dequeue())  # 2

q.enqueue(4)
print(q.dequeue())  # 3
print(q.dequeue())  # 4
```

---

## Coding Challenge: Sliding Window Maximum

**Problem:** Given an array of integers and a window size `k`, return an array of the maximum value in each window of size `k` as it slides from left to right.

**Example:** `nums = [1, 3, -1, -3, 5, 3, 6, 7]`, `k = 3` -> `[3, 3, 5, 5, 6, 7]`

**Approach:** Use a monotonic deque that stores indices in decreasing order of their values. For each new element, remove all smaller elements from the rear, and remove out-of-window indices from the front. Time: O(n), Space: O(k).

**Go:**

```go
package main

import "fmt"

func maxSlidingWindow(nums []int, k int) []int {
	deque := []int{}   // indices, values are decreasing
	result := []int{}

	for i := 0; i < len(nums); i++ {
		// Remove out-of-window indices
		if len(deque) > 0 && deque[0] <= i-k {
			deque = deque[1:]
		}
		// Remove smaller values from rear
		for len(deque) > 0 && nums[deque[len(deque)-1]] <= nums[i] {
			deque = deque[:len(deque)-1]
		}
		deque = append(deque, i)

		if i >= k-1 {
			result = append(result, nums[deque[0]])
		}
	}
	return result
}

func main() {
	nums := []int{1, 3, -1, -3, 5, 3, 6, 7}
	fmt.Println(maxSlidingWindow(nums, 3)) // [3 3 5 5 6 7]
}
```

**Java:**

```java
import java.util.*;

public class SlidingWindowMax {
    public static int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> deque = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];
        int idx = 0;

        for (int i = 0; i < nums.length; i++) {
            if (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
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
        System.out.println(Arrays.toString(maxSlidingWindow(nums, 3)));
        // [3, 3, 5, 5, 6, 7]
    }
}
```

**Python:**

```python
from collections import deque

def max_sliding_window(nums, k):
    dq = deque()  # stores indices
    result = []

    for i, val in enumerate(nums):
        if dq and dq[0] <= i - k:
            dq.popleft()
        while dq and nums[dq[-1]] <= val:
            dq.pop()
        dq.append(i)

        if i >= k - 1:
            result.append(nums[dq[0]])

    return result


nums = [1, 3, -1, -3, 5, 3, 6, 7]
print(max_sliding_window(nums, 3))  # [3, 3, 5, 5, 6, 7]
```
