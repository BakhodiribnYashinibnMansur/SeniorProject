# Queue -- Senior Level

## Table of Contents

- [Overview](#overview)
- [Message Queues in Distributed Systems](#message-queues-in-distributed-systems)
  - [Apache Kafka](#apache-kafka)
  - [RabbitMQ](#rabbitmq)
  - [Amazon SQS](#amazon-sqs)
  - [Comparison Table](#comparison-table)
- [Concurrent Queues](#concurrent-queues)
  - [Go: Channels and Select](#go-channels-and-select)
  - [Java: BlockingQueue Variants](#java-blockingqueue-variants)
  - [Python: queue.Queue and multiprocessing.Queue](#python-queuequeue-and-multiprocessingqueue)
- [Rate Limiting with Token Bucket](#rate-limiting-with-token-bucket)
  - [Go: Token Bucket](#go-token-bucket)
  - [Java: Token Bucket](#java-token-bucket)
  - [Python: Token Bucket](#python-token-bucket)
- [Task Scheduling with Queues](#task-scheduling-with-queues)
  - [Delayed Queue / Scheduled Tasks](#delayed-queue--scheduled-tasks)
  - [Work Stealing](#work-stealing)
- [Back-Pressure](#back-pressure)
  - [What Is Back-Pressure?](#what-is-back-pressure)
  - [Strategies for Handling Back-Pressure](#strategies-for-handling-back-pressure)
  - [Go: Back-Pressure with Bounded Channel](#go-back-pressure-with-bounded-channel)
  - [Java: Back-Pressure with Reject Policy](#java-back-pressure-with-reject-policy)
- [Design Decisions and Trade-offs](#design-decisions-and-trade-offs)
- [Summary](#summary)

---

## Overview

At the senior level, queues are not just in-memory data structures -- they are architectural primitives that power distributed systems, enable asynchronous processing, and manage concurrency at scale. This document covers message brokers, thread-safe concurrent queues, rate limiting, task scheduling, and back-pressure mechanisms.

---

## Message Queues in Distributed Systems

Message queues decouple services in distributed architectures. Producers publish messages; consumers process them asynchronously. This enables independent scaling, fault tolerance, and load leveling.

### Apache Kafka

Kafka is a distributed event streaming platform that organizes messages into **topics** partitioned across brokers.

Key characteristics:
- **Log-based**: messages are appended to an immutable, ordered log per partition
- **Consumer groups**: multiple consumers share partitions for parallel processing
- **Retention**: messages persist for a configurable time (days, weeks, forever)
- **Replay**: consumers can re-read old messages by resetting their offset
- **High throughput**: millions of messages per second per cluster
- **Ordering guarantee**: within a single partition only

```
Producer --> [Topic: orders]
               Partition 0: [msg0, msg1, msg2, ...]
               Partition 1: [msg0, msg1, msg2, ...]
               Partition 2: [msg0, msg1, msg2, ...]
             <-- Consumer Group A (3 consumers, one per partition)
             <-- Consumer Group B (independent, reads same data)
```

### RabbitMQ

RabbitMQ is a traditional message broker implementing AMQP (Advanced Message Queuing Protocol).

Key characteristics:
- **Exchange-based routing**: messages go to exchanges, which route to queues via binding rules
- **Acknowledgments**: consumers explicitly ack messages; unacked messages are redelivered
- **Prefetch**: consumers control how many unacked messages they receive at once
- **Flexible routing**: direct, fanout, topic, and headers exchanges
- **Lower throughput than Kafka** but richer routing and per-message semantics

```
Producer --> Exchange (topic) --> Binding --> Queue A --> Consumer A
                               --> Binding --> Queue B --> Consumer B
```

### Amazon SQS

SQS is a fully managed message queue service from AWS.

Key characteristics:
- **Standard queue**: at-least-once delivery, best-effort ordering (very high throughput)
- **FIFO queue**: exactly-once processing, strict ordering (lower throughput, 300 msg/s per group)
- **Visibility timeout**: after a consumer reads a message, it is invisible to others for a timeout period
- **Dead-letter queue (DLQ)**: messages that fail processing N times are moved to a DLQ
- **No infrastructure to manage**: fully serverless

### Comparison Table

| Feature            | Kafka                     | RabbitMQ                | SQS                      |
| ------------------ | ------------------------- | ----------------------- | ------------------------- |
| Model              | Log / streaming           | Message broker (AMQP)   | Managed queue             |
| Ordering           | Per partition             | Per queue               | FIFO queue only           |
| Throughput         | Very high (millions/sec)  | Medium (tens of K/sec)  | High (standard), medium (FIFO) |
| Message retention  | Configurable (days+)      | Until consumed/expired  | Up to 14 days             |
| Replay             | Yes (offset reset)        | No (once consumed)      | No                        |
| Delivery guarantee | At-least-once / exactly-once | At-least-once       | At-least-once / exactly-once (FIFO) |
| Use case           | Event sourcing, streaming | Task queues, RPC        | Serverless, decoupling    |

---

## Concurrent Queues

### Go: Channels and Select

Go channels are first-class concurrent queues. The `select` statement enables non-blocking operations and multiplexing across multiple channels.

```go
package main

import (
	"context"
	"fmt"
	"time"
)

func worker(ctx context.Context, id int, jobs <-chan int, results chan<- string) {
	for {
		select {
		case <-ctx.Done():
			fmt.Printf("Worker %d: shutting down\n", id)
			return
		case job, ok := <-jobs:
			if !ok {
				return
			}
			// Simulate work
			time.Sleep(100 * time.Millisecond)
			results <- fmt.Sprintf("Worker %d processed job %d", id, job)
		}
	}
}

func main() {
	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	jobs := make(chan int, 20)
	results := make(chan string, 20)

	// Start 3 workers
	for i := 0; i < 3; i++ {
		go worker(ctx, i, jobs, results)
	}

	// Send 10 jobs
	for j := 0; j < 10; j++ {
		jobs <- j
	}
	close(jobs)

	// Collect results
	for i := 0; i < 10; i++ {
		fmt.Println(<-results)
	}
}
```

### Java: BlockingQueue Variants

Java provides multiple `BlockingQueue` implementations for different concurrency needs.

```java
import java.util.concurrent.*;

public class ConcurrentQueues {
    public static void main(String[] args) throws InterruptedException {
        // ArrayBlockingQueue: bounded, fair optional
        BlockingQueue<String> bounded = new ArrayBlockingQueue<>(100);

        // LinkedBlockingQueue: optionally bounded, higher throughput
        BlockingQueue<String> linked = new LinkedBlockingQueue<>(1000);

        // PriorityBlockingQueue: unbounded, priority ordering
        BlockingQueue<Integer> priority = new PriorityBlockingQueue<>();

        // ConcurrentLinkedQueue: non-blocking, lock-free
        ConcurrentLinkedQueue<String> lockFree = new ConcurrentLinkedQueue<>();

        // Example: worker pool with ExecutorService + BlockingQueue
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,    // core pool size
            8,    // max pool size
            60L, TimeUnit.SECONDS,
            workQueue,
            new ThreadPoolExecutor.CallerRunsPolicy()  // back-pressure
        );

        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " on " +
                    Thread.currentThread().getName());
                try { Thread.sleep(100); } catch (InterruptedException e) { }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

### Python: queue.Queue and multiprocessing.Queue

```python
import queue
import multiprocessing
import threading

# Thread-safe queue (for threading)
thread_q = queue.Queue(maxsize=100)

# Process-safe queue (for multiprocessing)
process_q = multiprocessing.Queue(maxsize=100)

# Priority queue (thread-safe)
pq = queue.PriorityQueue()
pq.put((2, "low priority"))
pq.put((1, "high priority"))
pq.put((3, "lowest priority"))

while not pq.empty():
    priority, item = pq.get()
    print(f"Priority {priority}: {item}")
# Output: high priority, low priority, lowest priority

# LifoQueue acts as a thread-safe stack
lifo = queue.LifoQueue()
lifo.put(1)
lifo.put(2)
lifo.put(3)
print(lifo.get())  # 3 (LIFO)
```

---

## Rate Limiting with Token Bucket

The **token bucket** algorithm controls the rate of operations using a queue-like bucket of tokens. Each operation consumes a token. Tokens are added at a fixed rate. If the bucket is empty, the operation must wait.

```
Token bucket (capacity = 5, refill rate = 2/sec):

  Time 0s: [T][T][T][T][T]   (5 tokens, full)
  Request: consumes 1 token -> [T][T][T][T][ ]
  Request: consumes 1 token -> [T][T][T][ ][ ]
  Time 1s: 2 tokens added   -> [T][T][T][T][T]  (refilled, capped at 5)
```

### Go: Token Bucket

```go
package main

import (
	"fmt"
	"sync"
	"time"
)

type TokenBucket struct {
	tokens     float64
	capacity   float64
	refillRate float64 // tokens per second
	lastRefill time.Time
	mu         sync.Mutex
}

func NewTokenBucket(capacity, refillRate float64) *TokenBucket {
	return &TokenBucket{
		tokens:     capacity,
		capacity:   capacity,
		refillRate: refillRate,
		lastRefill: time.Now(),
	}
}

func (tb *TokenBucket) Allow() bool {
	tb.mu.Lock()
	defer tb.mu.Unlock()

	now := time.Now()
	elapsed := now.Sub(tb.lastRefill).Seconds()
	tb.tokens += elapsed * tb.refillRate
	if tb.tokens > tb.capacity {
		tb.tokens = tb.capacity
	}
	tb.lastRefill = now

	if tb.tokens >= 1 {
		tb.tokens--
		return true
	}
	return false
}

func main() {
	limiter := NewTokenBucket(5, 2) // 5 capacity, 2 tokens/sec

	for i := 0; i < 10; i++ {
		if limiter.Allow() {
			fmt.Printf("Request %d: ALLOWED\n", i)
		} else {
			fmt.Printf("Request %d: THROTTLED\n", i)
		}
		time.Sleep(200 * time.Millisecond)
	}
}
```

### Java: Token Bucket

```java
public class TokenBucket {
    private double tokens;
    private final double capacity;
    private final double refillRate;
    private long lastRefillNanos;

    public TokenBucket(double capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean allow() {
        long now = System.nanoTime();
        double elapsed = (now - lastRefillNanos) / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + elapsed * refillRate);
        lastRefillNanos = now;

        if (tokens >= 1) {
            tokens--;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket limiter = new TokenBucket(5, 2);

        for (int i = 0; i < 10; i++) {
            if (limiter.allow()) {
                System.out.println("Request " + i + ": ALLOWED");
            } else {
                System.out.println("Request " + i + ": THROTTLED");
            }
            Thread.sleep(200);
        }
    }
}
```

### Python: Token Bucket

```python
import time
import threading

class TokenBucket:
    def __init__(self, capacity, refill_rate):
        self.capacity = capacity
        self.tokens = capacity
        self.refill_rate = refill_rate  # tokens per second
        self.last_refill = time.monotonic()
        self.lock = threading.Lock()

    def allow(self):
        with self.lock:
            now = time.monotonic()
            elapsed = now - self.last_refill
            self.tokens = min(self.capacity, self.tokens + elapsed * self.refill_rate)
            self.last_refill = now

            if self.tokens >= 1:
                self.tokens -= 1
                return True
            return False

limiter = TokenBucket(capacity=5, refill_rate=2)

for i in range(10):
    if limiter.allow():
        print(f"Request {i}: ALLOWED")
    else:
        print(f"Request {i}: THROTTLED")
    time.sleep(0.2)
```

---

## Task Scheduling with Queues

### Delayed Queue / Scheduled Tasks

A **delayed queue** holds tasks that should only be processed after a specific time. Elements become "visible" only when their delay expires.

Common implementations:
- **Java**: `DelayQueue<Delayed>` -- elements must implement `getDelay()`
- **Go**: use a priority queue (min-heap) keyed by execution time, with a goroutine that sleeps until the next task
- **Python**: use `sched.scheduler` or a heap-based approach
- **Distributed**: Redis sorted sets with score = execution timestamp, or Kafka with delayed topics

### Work Stealing

In a **work-stealing** scheduler, each worker thread has its own deque of tasks:
- Workers push/pop tasks from **their own** deque (LIFO, for cache locality)
- Idle workers **steal** tasks from the **front** of another worker's deque (FIFO)

This is used in Go's goroutine scheduler, Java's `ForkJoinPool`, and Tokio (Rust).

```
Worker 0 deque:  [task4] [task3] [task2] [task1]
                                           ^
                                    Worker 0 pops here (LIFO)
                   ^
            Worker 2 steals here (FIFO, from the other end)
```

---

## Back-Pressure

### What Is Back-Pressure?

Back-pressure is a mechanism where a slow consumer signals upstream producers to slow down. Without back-pressure, an overloaded consumer's queue grows unboundedly, eventually causing out-of-memory crashes.

### Strategies for Handling Back-Pressure

| Strategy         | Description                                          | Trade-off                    |
| ---------------- | ---------------------------------------------------- | ---------------------------- |
| Bounded queue    | Reject or block when queue is full                   | Simple; may drop messages    |
| Caller-runs      | Producer handles the task itself when queue is full   | No data loss; producer slows |
| Dropping newest   | Discard the incoming message when full               | Preserves old data           |
| Dropping oldest   | Discard the oldest message to make room              | Preserves new data           |
| Adaptive rate    | Dynamically adjust producer rate based on queue depth | Complex; most flexible       |

### Go: Back-Pressure with Bounded Channel

```go
package main

import (
	"fmt"
	"time"
)

func main() {
	ch := make(chan int, 5) // bounded: capacity 5

	// Producer: uses select to detect back-pressure
	go func() {
		for i := 0; i < 20; i++ {
			select {
			case ch <- i:
				fmt.Printf("Sent %d\n", i)
			default:
				fmt.Printf("Back-pressure! Dropping %d\n", i)
			}
			time.Sleep(10 * time.Millisecond)
		}
		close(ch)
	}()

	// Slow consumer
	for val := range ch {
		fmt.Printf("  Received %d\n", val)
		time.Sleep(100 * time.Millisecond)
	}
}
```

### Java: Back-Pressure with Reject Policy

```java
import java.util.concurrent.*;

public class BackPressure {
    public static void main(String[] args) {
        // CallerRunsPolicy: when queue is full, the submitting thread runs the task itself
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 2, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(5),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " on " +
                    Thread.currentThread().getName());
                try { Thread.sleep(200); } catch (InterruptedException e) { }
            });
        }

        executor.shutdown();
    }
}
```

---

## Design Decisions and Trade-offs

| Decision                       | Option A                    | Option B                      |
| ------------------------------ | --------------------------- | ----------------------------- |
| Queue capacity                 | Bounded (predictable memory) | Unbounded (risk of OOM)      |
| Delivery guarantee             | At-least-once (simpler)     | Exactly-once (complex, slower)|
| Ordering                       | Strict FIFO (single queue)  | Best-effort (partitioned)    |
| Persistence                    | In-memory (fast, volatile)  | Disk-backed (durable, slower)|
| Back-pressure strategy         | Drop messages (fast)        | Block producers (safe)       |
| Consumer acknowledgment        | Auto-ack (fast, may lose)   | Manual ack (safe, slower)    |

---

## Summary

| Concept               | Key Takeaway                                                         |
| --------------------- | -------------------------------------------------------------------- |
| Message queues        | Decouple services; Kafka for streaming, RabbitMQ for routing, SQS managed |
| Concurrent queues     | Go channels, Java BlockingQueue, Python queue.Queue -- all thread-safe |
| Token bucket          | Rate limiting by consuming tokens from a refilling bucket            |
| Task scheduling       | Delayed queues and work-stealing deques for efficient scheduling     |
| Back-pressure         | Bounded queues + rejection policies prevent OOM in overloaded systems|

Next steps: move on to the **professional level** for formal ADT specification, lock-free queues, wait-free algorithms, and amortized analysis.
