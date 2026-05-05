# Queue -- Practice Tasks

## Table of Contents

- [Task 1: Implement a Basic Queue](#task-1-implement-a-basic-queue)
- [Task 2: Reverse a Queue](#task-2-reverse-a-queue)
- [Task 3: Generate Binary Numbers 1 to N](#task-3-generate-binary-numbers-1-to-n)
- [Task 4: Implement Queue Using Two Stacks](#task-4-implement-queue-using-two-stacks)
- [Task 5: Implement Stack Using Two Queues](#task-5-implement-stack-using-two-queues)
- [Task 6: First Non-Repeating Character in Stream](#task-6-first-non-repeating-character-in-stream)
- [Task 7: Circular Queue with Dynamic Resizing](#task-7-circular-queue-with-dynamic-resizing)
- [Task 8: BFS Level-Order Traversal of Binary Tree](#task-8-bfs-level-order-traversal-of-binary-tree)
- [Task 9: Sliding Window Maximum](#task-9-sliding-window-maximum)
- [Task 10: Implement a Deque from Scratch](#task-10-implement-a-deque-from-scratch)
- [Task 11: Interleave First and Second Half](#task-11-interleave-first-and-second-half)
- [Task 12: Hot Potato Simulation (Josephus Problem)](#task-12-hot-potato-simulation-josephus-problem)
- [Task 13: Number of Islands (BFS)](#task-13-number-of-islands-bfs)
- [Task 14: Implement a Priority Queue from Scratch](#task-14-implement-a-priority-queue-from-scratch)
- [Task 15: Design a Task Scheduler](#task-15-design-a-task-scheduler)
- [Benchmark: Queue Implementation Performance](#benchmark-queue-implementation-performance)

---

## Task 1: Implement a Basic Queue

**Difficulty:** Easy

**Problem:** Implement a queue with `enqueue`, `dequeue`, `peek`, `isEmpty`, and `size` using a linked list. All operations must be O(1).

**Input:** A sequence of operations: `enqueue(5)`, `enqueue(10)`, `dequeue()`, `peek()`, `size()`
**Output:** `dequeue() -> 5`, `peek() -> 10`, `size() -> 1`

**Expected complexity:** Time O(1) per operation, Space O(n).

**Go:**

```go
package main

import (
	"errors"
	"fmt"
)

type node struct {
	val  int
	next *node
}

type Queue struct {
	front, rear *node
	size        int
}

func (q *Queue) Enqueue(val int) {
	n := &node{val: val}
	if q.rear != nil {
		q.rear.next = n
	}
	q.rear = n
	if q.front == nil {
		q.front = n
	}
	q.size++
}

func (q *Queue) Dequeue() (int, error) {
	if q.front == nil {
		return 0, errors.New("empty")
	}
	val := q.front.val
	q.front = q.front.next
	if q.front == nil {
		q.rear = nil
	}
	q.size--
	return val, nil
}

func (q *Queue) Peek() (int, error) {
	if q.front == nil {
		return 0, errors.New("empty")
	}
	return q.front.val, nil
}

func (q *Queue) IsEmpty() bool { return q.size == 0 }
func (q *Queue) Size() int     { return q.size }

func main() {
	q := &Queue{}
	q.Enqueue(5)
	q.Enqueue(10)
	v, _ := q.Dequeue()
	fmt.Println("Dequeued:", v)
	p, _ := q.Peek()
	fmt.Println("Peek:", p)
	fmt.Println("Size:", q.Size())
}
```

**Java:**

```java
import java.util.NoSuchElementException;

public class BasicQueue {
    private static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    private Node front, rear;
    private int size;

    public void enqueue(int val) {
        Node n = new Node(val);
        if (rear != null) rear.next = n;
        rear = n;
        if (front == null) front = n;
        size++;
    }

    public int dequeue() {
        if (front == null) throw new NoSuchElementException();
        int val = front.val;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return val;
    }

    public int peek() {
        if (front == null) throw new NoSuchElementException();
        return front.val;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public static void main(String[] args) {
        BasicQueue q = new BasicQueue();
        q.enqueue(5);
        q.enqueue(10);
        System.out.println("Dequeued: " + q.dequeue());
        System.out.println("Peek: " + q.peek());
        System.out.println("Size: " + q.size());
    }
}
```

**Python:**

```python
class Node:
    def __init__(self, val):
        self.val = val
        self.next = None

class Queue:
    def __init__(self):
        self._front = self._rear = None
        self._size = 0

    def enqueue(self, val):
        n = Node(val)
        if self._rear:
            self._rear.next = n
        self._rear = n
        if not self._front:
            self._front = n
        self._size += 1

    def dequeue(self):
        if not self._front:
            raise IndexError("empty")
        val = self._front.val
        self._front = self._front.next
        if not self._front:
            self._rear = None
        self._size -= 1
        return val

    def peek(self):
        if not self._front:
            raise IndexError("empty")
        return self._front.val

    def is_empty(self):
        return self._size == 0

    def size(self):
        return self._size

q = Queue()
q.enqueue(5)
q.enqueue(10)
print("Dequeued:", q.dequeue())  # 5
print("Peek:", q.peek())        # 10
print("Size:", q.size())        # 1
```

---

## Task 2: Reverse a Queue

**Difficulty:** Easy

**Problem:** Reverse all elements of a queue. You may use a stack as auxiliary storage.

**Input:** Queue: `[1, 2, 3, 4, 5]` (front to rear)
**Output:** Queue: `[5, 4, 3, 2, 1]` (front to rear)

**Hint:** Dequeue all elements into a stack, then pop all from stack back into queue.

**Expected complexity:** Time O(n), Space O(n).

**Go:**

```go
func reverseQueue(q *Queue) {
	stack := []int{}
	for !q.IsEmpty() {
		v, _ := q.Dequeue()
		stack = append(stack, v)
	}
	for i := len(stack) - 1; i >= 0; i-- {
		q.Enqueue(stack[i])
	}
}
```

**Java:**

```java
public static void reverseQueue(Queue<Integer> q) {
    Stack<Integer> stack = new Stack<>();
    while (!q.isEmpty()) {
        stack.push(q.poll());
    }
    while (!stack.isEmpty()) {
        q.offer(stack.pop());
    }
}
```

**Python:**

```python
from collections import deque

def reverse_queue(q):
    stack = []
    while q:
        stack.append(q.popleft())
    while stack:
        q.append(stack.pop())
```

---

## Task 3: Generate Binary Numbers 1 to N

**Difficulty:** Easy

**Problem:** Given a number N, generate binary representations of 1 to N using a queue.

**Input:** `N = 5`
**Output:** `["1", "10", "11", "100", "101"]`

**Hint:** Start with "1" in the queue. For each dequeue, generate two children by appending "0" and "1".

**Expected complexity:** Time O(n), Space O(n).

**Go:**

```go
func generateBinary(n int) []string {
	result := []string{}
	queue := []string{"1"}
	for i := 0; i < n; i++ {
		front := queue[0]
		queue = queue[1:]
		result = append(result, front)
		queue = append(queue, front+"0")
		queue = append(queue, front+"1")
	}
	return result
}
```

**Java:**

```java
public static List<String> generateBinary(int n) {
    List<String> result = new ArrayList<>();
    Queue<String> queue = new LinkedList<>();
    queue.offer("1");
    for (int i = 0; i < n; i++) {
        String front = queue.poll();
        result.add(front);
        queue.offer(front + "0");
        queue.offer(front + "1");
    }
    return result;
}
```

**Python:**

```python
from collections import deque

def generate_binary(n):
    result = []
    queue = deque(["1"])
    for _ in range(n):
        front = queue.popleft()
        result.append(front)
        queue.append(front + "0")
        queue.append(front + "1")
    return result
```

---

## Task 4: Implement Queue Using Two Stacks

**Difficulty:** Medium

**Problem:** Implement a FIFO queue using only two LIFO stacks. Amortized O(1) per operation.

**Expected complexity:** Time O(1) amortized, Space O(n).

**Go:**

```go
type TwoStackQueue struct {
	inbox, outbox []int
}

func (q *TwoStackQueue) Enqueue(val int) {
	q.inbox = append(q.inbox, val)
}

func (q *TwoStackQueue) transfer() {
	if len(q.outbox) == 0 {
		for len(q.inbox) > 0 {
			q.outbox = append(q.outbox, q.inbox[len(q.inbox)-1])
			q.inbox = q.inbox[:len(q.inbox)-1]
		}
	}
}

func (q *TwoStackQueue) Dequeue() int {
	q.transfer()
	val := q.outbox[len(q.outbox)-1]
	q.outbox = q.outbox[:len(q.outbox)-1]
	return val
}
```

**Java:**

```java
class TwoStackQueue {
    Stack<Integer> inbox = new Stack<>(), outbox = new Stack<>();

    void enqueue(int val) { inbox.push(val); }

    private void transfer() {
        if (outbox.isEmpty())
            while (!inbox.isEmpty()) outbox.push(inbox.pop());
    }

    int dequeue() { transfer(); return outbox.pop(); }
}
```

**Python:**

```python
class TwoStackQueue:
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
        return self._outbox.pop()
```

---

## Task 5: Implement Stack Using Two Queues

**Difficulty:** Medium

**Problem:** Implement a LIFO stack using only two FIFO queues.

**Expected complexity:** Time O(n) for push or pop (one must be O(n), the other O(1)).

**Go:**

```go
type TwoQueueStack struct {
	q1, q2 []int
}

func (s *TwoQueueStack) Push(val int) {
	s.q2 = append(s.q2, val)
	for len(s.q1) > 0 {
		s.q2 = append(s.q2, s.q1[0])
		s.q1 = s.q1[1:]
	}
	s.q1, s.q2 = s.q2, s.q1
}

func (s *TwoQueueStack) Pop() int {
	val := s.q1[0]
	s.q1 = s.q1[1:]
	return val
}

func (s *TwoQueueStack) Top() int { return s.q1[0] }
```

**Java:**

```java
class TwoQueueStack {
    Queue<Integer> q1 = new LinkedList<>(), q2 = new LinkedList<>();

    void push(int val) {
        q2.offer(val);
        while (!q1.isEmpty()) q2.offer(q1.poll());
        Queue<Integer> tmp = q1; q1 = q2; q2 = tmp;
    }

    int pop() { return q1.poll(); }
    int top() { return q1.peek(); }
}
```

**Python:**

```python
from collections import deque

class TwoQueueStack:
    def __init__(self):
        self._q1 = deque()
        self._q2 = deque()

    def push(self, val):
        self._q2.append(val)
        while self._q1:
            self._q2.append(self._q1.popleft())
        self._q1, self._q2 = self._q2, self._q1

    def pop(self):
        return self._q1.popleft()

    def top(self):
        return self._q1[0]
```

---

## Task 6: First Non-Repeating Character in Stream

**Difficulty:** Medium

**Problem:** Given a stream of characters, after each character, find the first non-repeating character seen so far. If none exists, return `'#'`.

**Input:** Stream: `"aabcbcd"`
**Output:** `['a', '#', 'b', 'b', 'c', 'c', 'd']`

**Hint:** Use a queue of characters and a frequency map. After each new character, pop from the front of the queue while the front character has frequency > 1.

**Expected complexity:** Time O(n), Space O(n).

**Go:**

```go
func firstNonRepeating(stream string) []byte {
	freq := make(map[byte]int)
	queue := []byte{}
	result := []byte{}

	for i := 0; i < len(stream); i++ {
		ch := stream[i]
		freq[ch]++
		queue = append(queue, ch)
		for len(queue) > 0 && freq[queue[0]] > 1 {
			queue = queue[1:]
		}
		if len(queue) == 0 {
			result = append(result, '#')
		} else {
			result = append(result, queue[0])
		}
	}
	return result
}
```

**Java:**

```java
public static char[] firstNonRepeating(String stream) {
    Map<Character, Integer> freq = new HashMap<>();
    Queue<Character> queue = new LinkedList<>();
    char[] result = new char[stream.length()];

    for (int i = 0; i < stream.length(); i++) {
        char ch = stream.charAt(i);
        freq.merge(ch, 1, Integer::sum);
        queue.offer(ch);
        while (!queue.isEmpty() && freq.get(queue.peek()) > 1) {
            queue.poll();
        }
        result[i] = queue.isEmpty() ? '#' : queue.peek();
    }
    return result;
}
```

**Python:**

```python
from collections import deque

def first_non_repeating(stream):
    freq = {}
    queue = deque()
    result = []
    for ch in stream:
        freq[ch] = freq.get(ch, 0) + 1
        queue.append(ch)
        while queue and freq[queue[0]] > 1:
            queue.popleft()
        result.append(queue[0] if queue else '#')
    return result
```

---

## Task 7: Circular Queue with Dynamic Resizing

**Difficulty:** Medium

**Problem:** Implement a circular queue that doubles its capacity when full. Support `enqueue`, `dequeue`, `peek`.

**Expected complexity:** Time O(1) amortized, Space O(n).

**Go:**

```go
type DynamicCircularQueue struct {
	data          []int
	front, rear   int
	size, capacity int
}

func NewDCQ(cap int) *DynamicCircularQueue {
	return &DynamicCircularQueue{data: make([]int, cap), rear: -1, capacity: cap}
}

func (q *DynamicCircularQueue) Enqueue(val int) {
	if q.size == q.capacity {
		q.resize(q.capacity * 2)
	}
	q.rear = (q.rear + 1) % q.capacity
	q.data[q.rear] = val
	q.size++
}

func (q *DynamicCircularQueue) Dequeue() int {
	val := q.data[q.front]
	q.front = (q.front + 1) % q.capacity
	q.size--
	return val
}

func (q *DynamicCircularQueue) resize(newCap int) {
	newData := make([]int, newCap)
	for i := 0; i < q.size; i++ {
		newData[i] = q.data[(q.front+i)%q.capacity]
	}
	q.data = newData
	q.front = 0
	q.rear = q.size - 1
	q.capacity = newCap
}
```

**Java:**

```java
public class DynamicCircularQueue {
    private int[] data;
    private int front, rear, size, capacity;

    public DynamicCircularQueue(int cap) {
        data = new int[cap]; capacity = cap; rear = -1;
    }

    public void enqueue(int val) {
        if (size == capacity) resize(capacity * 2);
        rear = (rear + 1) % capacity;
        data[rear] = val;
        size++;
    }

    public int dequeue() {
        int val = data[front];
        front = (front + 1) % capacity;
        size--;
        return val;
    }

    private void resize(int newCap) {
        int[] newData = new int[newCap];
        for (int i = 0; i < size; i++)
            newData[i] = data[(front + i) % capacity];
        data = newData; front = 0; rear = size - 1; capacity = newCap;
    }
}
```

**Python:**

```python
class DynamicCircularQueue:
    def __init__(self, cap=4):
        self._data = [None] * cap
        self._front = 0
        self._rear = -1
        self._size = 0
        self._cap = cap

    def enqueue(self, val):
        if self._size == self._cap:
            self._resize(self._cap * 2)
        self._rear = (self._rear + 1) % self._cap
        self._data[self._rear] = val
        self._size += 1

    def dequeue(self):
        val = self._data[self._front]
        self._front = (self._front + 1) % self._cap
        self._size -= 1
        return val

    def _resize(self, new_cap):
        new_data = [None] * new_cap
        for i in range(self._size):
            new_data[i] = self._data[(self._front + i) % self._cap]
        self._data = new_data
        self._front = 0
        self._rear = self._size - 1
        self._cap = new_cap
```

---

## Task 8: BFS Level-Order Traversal of Binary Tree

**Difficulty:** Medium

**Problem:** Given a binary tree, return its level-order traversal (list of lists, one per level).

**Input:** Tree: `[3, 9, 20, null, null, 15, 7]`
**Output:** `[[3], [9, 20], [15, 7]]`

**Expected complexity:** Time O(n), Space O(n).

**Go:**

```go
type TreeNode struct {
	Val   int
	Left  *TreeNode
	Right *TreeNode
}

func levelOrder(root *TreeNode) [][]int {
	if root == nil {
		return nil
	}
	result := [][]int{}
	queue := []*TreeNode{root}
	for len(queue) > 0 {
		levelSize := len(queue)
		level := []int{}
		for i := 0; i < levelSize; i++ {
			node := queue[0]
			queue = queue[1:]
			level = append(level, node.Val)
			if node.Left != nil {
				queue = append(queue, node.Left)
			}
			if node.Right != nil {
				queue = append(queue, node.Right)
			}
		}
		result = append(result, level)
	}
	return result
}
```

**Java:**

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

**Python:**

```python
from collections import deque

def level_order(root):
    if not root:
        return []
    result = []
    queue = deque([root])
    while queue:
        level = []
        for _ in range(len(queue)):
            node = queue.popleft()
            level.append(node.val)
            if node.left:
                queue.append(node.left)
            if node.right:
                queue.append(node.right)
        result.append(level)
    return result
```

---

## Task 9: Sliding Window Maximum

**Difficulty:** Hard

**Problem:** Given an array and window size k, return the maximum of each sliding window.

**Input:** `nums = [1, 3, -1, -3, 5, 3, 6, 7]`, `k = 3`
**Output:** `[3, 3, 5, 5, 6, 7]`

**Hint:** Use a monotonic deque storing indices in decreasing order of values.

**Expected complexity:** Time O(n), Space O(k).

**Go:**

```go
func maxSlidingWindow(nums []int, k int) []int {
	dq := []int{}
	res := []int{}
	for i, v := range nums {
		if len(dq) > 0 && dq[0] <= i-k {
			dq = dq[1:]
		}
		for len(dq) > 0 && nums[dq[len(dq)-1]] <= v {
			dq = dq[:len(dq)-1]
		}
		dq = append(dq, i)
		if i >= k-1 {
			res = append(res, nums[dq[0]])
		}
	}
	return res
}
```

**Java:**

```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> dq = new ArrayDeque<>();
    int[] res = new int[nums.length - k + 1];
    int idx = 0;
    for (int i = 0; i < nums.length; i++) {
        if (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();
        while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) dq.pollLast();
        dq.offerLast(i);
        if (i >= k - 1) res[idx++] = nums[dq.peekFirst()];
    }
    return res;
}
```

**Python:**

```python
from collections import deque

def max_sliding_window(nums, k):
    dq = deque()
    res = []
    for i, v in enumerate(nums):
        if dq and dq[0] <= i - k:
            dq.popleft()
        while dq and nums[dq[-1]] <= v:
            dq.pop()
        dq.append(i)
        if i >= k - 1:
            res.append(nums[dq[0]])
    return res
```

---

## Task 10: Implement a Deque from Scratch

**Difficulty:** Medium

**Problem:** Implement a double-ended queue supporting `addFront`, `addRear`, `removeFront`, `removeRear`, `peekFront`, `peekRear`. All operations O(1).

**Expected complexity:** Time O(1) per operation, Space O(n).

Use a doubly-linked list. See the middle.md file for full implementations in all three languages.

---

## Task 11: Interleave First and Second Half

**Difficulty:** Medium

**Problem:** Given a queue with even number of elements, interleave the first and second halves.

**Input:** `[1, 2, 3, 4, 5, 6, 7, 8]`
**Output:** `[1, 5, 2, 6, 3, 7, 4, 8]`

**Go:**

```go
func interleave(queue []int) []int {
	n := len(queue)
	half := n / 2
	first := queue[:half]
	second := queue[half:]
	result := make([]int, 0, n)
	for i := 0; i < half; i++ {
		result = append(result, first[i], second[i])
	}
	return result
}
```

**Java:**

```java
public static Queue<Integer> interleave(Queue<Integer> q) {
    int half = q.size() / 2;
    Queue<Integer> firstHalf = new LinkedList<>();
    for (int i = 0; i < half; i++) firstHalf.offer(q.poll());
    Queue<Integer> result = new LinkedList<>();
    while (!firstHalf.isEmpty()) {
        result.offer(firstHalf.poll());
        result.offer(q.poll());
    }
    return result;
}
```

**Python:**

```python
from collections import deque

def interleave(q):
    half = len(q) // 2
    first = deque()
    for _ in range(half):
        first.append(q.popleft())
    result = deque()
    while first:
        result.append(first.popleft())
        result.append(q.popleft())
    return result
```

---

## Task 12: Hot Potato Simulation (Josephus Problem)

**Difficulty:** Medium

**Problem:** N people stand in a circle. Starting from the first person, count k people clockwise and eliminate the k-th person. Repeat until one person remains. Return the survivor.

**Input:** `names = ["A", "B", "C", "D", "E"]`, `k = 3`
**Output:** `"D"`

**Go:**

```go
func hotPotato(names []string, k int) string {
	queue := make([]string, len(names))
	copy(queue, names)
	for len(queue) > 1 {
		for i := 0; i < k-1; i++ {
			queue = append(queue, queue[0])
			queue = queue[1:]
		}
		queue = queue[1:] // eliminate k-th person
	}
	return queue[0]
}
```

**Java:**

```java
public static String hotPotato(List<String> names, int k) {
    Queue<String> queue = new LinkedList<>(names);
    while (queue.size() > 1) {
        for (int i = 0; i < k - 1; i++) queue.offer(queue.poll());
        queue.poll(); // eliminate
    }
    return queue.poll();
}
```

**Python:**

```python
from collections import deque

def hot_potato(names, k):
    q = deque(names)
    while len(q) > 1:
        q.rotate(-(k - 1))
        q.popleft()
    return q[0]
```

---

## Task 13: Number of Islands (BFS)

**Difficulty:** Medium

**Problem:** Given a 2D grid of `'1'` (land) and `'0'` (water), count the number of islands. An island is connected land cells (4-directional).

**Expected complexity:** Time O(m*n), Space O(min(m, n)).

**Go:**

```go
func numIslands(grid [][]byte) int {
	if len(grid) == 0 {
		return 0
	}
	rows, cols := len(grid), len(grid[0])
	count := 0
	dirs := [][2]int{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}

	for r := 0; r < rows; r++ {
		for c := 0; c < cols; c++ {
			if grid[r][c] == '1' {
				count++
				queue := [][2]int{{r, c}}
				grid[r][c] = '0'
				for len(queue) > 0 {
					cell := queue[0]
					queue = queue[1:]
					for _, d := range dirs {
						nr, nc := cell[0]+d[0], cell[1]+d[1]
						if nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == '1' {
							grid[nr][nc] = '0'
							queue = append(queue, [2]int{nr, nc})
						}
					}
				}
			}
		}
	}
	return count
}
```

**Java:**

```java
public int numIslands(char[][] grid) {
    int count = 0;
    int rows = grid.length, cols = grid[0].length;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == '1') {
                count++;
                Queue<int[]> q = new LinkedList<>();
                q.offer(new int[]{r, c});
                grid[r][c] = '0';
                while (!q.isEmpty()) {
                    int[] cell = q.poll();
                    for (int[] d : dirs) {
                        int nr = cell[0]+d[0], nc = cell[1]+d[1];
                        if (nr>=0 && nr<rows && nc>=0 && nc<cols && grid[nr][nc]=='1') {
                            grid[nr][nc] = '0';
                            q.offer(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
    }
    return count;
}
```

**Python:**

```python
from collections import deque

def num_islands(grid):
    if not grid:
        return 0
    rows, cols = len(grid), len(grid[0])
    count = 0
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == '1':
                count += 1
                queue = deque([(r, c)])
                grid[r][c] = '0'
                while queue:
                    cr, cc = queue.popleft()
                    for dr, dc in [(0,1),(0,-1),(1,0),(-1,0)]:
                        nr, nc = cr+dr, cc+dc
                        if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] == '1':
                            grid[nr][nc] = '0'
                            queue.append((nr, nc))
    return count
```

---

## Task 14: Implement a Priority Queue from Scratch

**Difficulty:** Hard

**Problem:** Implement a min-heap-based priority queue with `insert`, `extractMin`, `peekMin`. No built-in heap allowed.

**Expected complexity:** Insert O(log n), Extract O(log n), Peek O(1).

**Go:**

```go
type MinHeap struct {
	data []int
}

func (h *MinHeap) Insert(val int) {
	h.data = append(h.data, val)
	h.siftUp(len(h.data) - 1)
}

func (h *MinHeap) ExtractMin() int {
	min := h.data[0]
	last := len(h.data) - 1
	h.data[0] = h.data[last]
	h.data = h.data[:last]
	if len(h.data) > 0 {
		h.siftDown(0)
	}
	return min
}

func (h *MinHeap) siftUp(i int) {
	for i > 0 {
		parent := (i - 1) / 2
		if h.data[parent] <= h.data[i] {
			break
		}
		h.data[parent], h.data[i] = h.data[i], h.data[parent]
		i = parent
	}
}

func (h *MinHeap) siftDown(i int) {
	n := len(h.data)
	for {
		smallest := i
		left, right := 2*i+1, 2*i+2
		if left < n && h.data[left] < h.data[smallest] {
			smallest = left
		}
		if right < n && h.data[right] < h.data[smallest] {
			smallest = right
		}
		if smallest == i {
			break
		}
		h.data[i], h.data[smallest] = h.data[smallest], h.data[i]
		i = smallest
	}
}
```

**Java:**

```java
class MinHeap {
    private List<Integer> data = new ArrayList<>();

    void insert(int val) {
        data.add(val);
        siftUp(data.size() - 1);
    }

    int extractMin() {
        int min = data.get(0);
        int last = data.remove(data.size() - 1);
        if (!data.isEmpty()) { data.set(0, last); siftDown(0); }
        return min;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int p = (i - 1) / 2;
            if (data.get(p) <= data.get(i)) break;
            Collections.swap(data, p, i);
            i = p;
        }
    }

    private void siftDown(int i) {
        int n = data.size();
        while (true) {
            int s = i, l = 2*i+1, r = 2*i+2;
            if (l < n && data.get(l) < data.get(s)) s = l;
            if (r < n && data.get(r) < data.get(s)) s = r;
            if (s == i) break;
            Collections.swap(data, i, s);
            i = s;
        }
    }
}
```

**Python:**

```python
class MinHeap:
    def __init__(self):
        self._data = []

    def insert(self, val):
        self._data.append(val)
        self._sift_up(len(self._data) - 1)

    def extract_min(self):
        m = self._data[0]
        self._data[0] = self._data[-1]
        self._data.pop()
        if self._data:
            self._sift_down(0)
        return m

    def _sift_up(self, i):
        while i > 0:
            p = (i - 1) // 2
            if self._data[p] <= self._data[i]:
                break
            self._data[p], self._data[i] = self._data[i], self._data[p]
            i = p

    def _sift_down(self, i):
        n = len(self._data)
        while True:
            s, l, r = i, 2*i+1, 2*i+2
            if l < n and self._data[l] < self._data[s]: s = l
            if r < n and self._data[r] < self._data[s]: s = r
            if s == i: break
            self._data[i], self._data[s] = self._data[s], self._data[i]
            i = s
```

---

## Task 15: Design a Task Scheduler

**Difficulty:** Hard

**Problem:** Given tasks with cooldown period n, find the minimum time to execute all tasks. Same task must wait n intervals before executing again.

**Input:** `tasks = ['A','A','A','B','B','B']`, `n = 2`
**Output:** `8` (A B _ A B _ A B)

**Expected complexity:** Time O(t), Space O(1) where t is number of tasks.

**Go:**

```go
func leastInterval(tasks []byte, n int) int {
	freq := [26]int{}
	for _, t := range tasks {
		freq[t-'A']++
	}
	maxFreq := 0
	maxCount := 0
	for _, f := range freq {
		if f > maxFreq {
			maxFreq = f
			maxCount = 1
		} else if f == maxFreq {
			maxCount++
		}
	}
	intervals := (maxFreq-1)*(n+1) + maxCount
	if intervals < len(tasks) {
		return len(tasks)
	}
	return intervals
}
```

**Java:**

```java
public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    int maxFreq = 0, maxCount = 0;
    for (int f : freq) {
        if (f > maxFreq) { maxFreq = f; maxCount = 1; }
        else if (f == maxFreq) maxCount++;
    }
    int intervals = (maxFreq - 1) * (n + 1) + maxCount;
    return Math.max(intervals, tasks.length);
}
```

**Python:**

```python
def least_interval(tasks, n):
    freq = [0] * 26
    for t in tasks:
        freq[ord(t) - ord('A')] += 1
    max_freq = max(freq)
    max_count = freq.count(max_freq)
    intervals = (max_freq - 1) * (n + 1) + max_count
    return max(intervals, len(tasks))
```

---

## Benchmark: Queue Implementation Performance

Compare the performance of array-based queue, linked list queue, and circular queue for 1,000,000 enqueue/dequeue cycles.

**Go:**

```go
package main

import (
	"fmt"
	"time"
)

func benchmarkSliceQueue(n int) time.Duration {
	start := time.Now()
	q := make([]int, 0)
	for i := 0; i < n; i++ {
		q = append(q, i)
	}
	for len(q) > 0 {
		q = q[1:]
	}
	return time.Since(start)
}

func benchmarkCircularQueue(n int) time.Duration {
	start := time.Now()
	cap := n
	data := make([]int, cap)
	front, rear, size := 0, -1, 0
	for i := 0; i < n; i++ {
		rear = (rear + 1) % cap
		data[rear] = i
		size++
	}
	for size > 0 {
		front = (front + 1) % cap
		size--
	}
	_ = data
	return time.Since(start)
}

func main() {
	n := 1_000_000
	fmt.Printf("Slice queue:    %v\n", benchmarkSliceQueue(n))
	fmt.Printf("Circular queue: %v\n", benchmarkCircularQueue(n))
}
```

**Java:**

```java
import java.util.*;

public class QueueBenchmark {
    public static void main(String[] args) {
        int n = 1_000_000;

        // LinkedList as Queue
        long start = System.nanoTime();
        Queue<Integer> linked = new LinkedList<>();
        for (int i = 0; i < n; i++) linked.offer(i);
        while (!linked.isEmpty()) linked.poll();
        long linkedTime = System.nanoTime() - start;

        // ArrayDeque as Queue
        start = System.nanoTime();
        Queue<Integer> arrayDeque = new ArrayDeque<>(n);
        for (int i = 0; i < n; i++) arrayDeque.offer(i);
        while (!arrayDeque.isEmpty()) arrayDeque.poll();
        long arrayDequeTime = System.nanoTime() - start;

        System.out.printf("LinkedList:  %d ms%n", linkedTime / 1_000_000);
        System.out.printf("ArrayDeque:  %d ms%n", arrayDequeTime / 1_000_000);
    }
}
```

**Python:**

```python
import time
from collections import deque

n = 1_000_000

# list as queue (BAD: pop(0) is O(n))
start = time.perf_counter()
q = []
for i in range(n):
    q.append(i)
for _ in range(n):
    q.pop(0)
list_time = time.perf_counter() - start

# deque as queue (GOOD: popleft is O(1))
start = time.perf_counter()
q = deque()
for i in range(n):
    q.append(i)
for _ in range(n):
    q.popleft()
deque_time = time.perf_counter() - start

print(f"list (pop(0)):    {list_time:.3f}s")
print(f"deque (popleft):  {deque_time:.3f}s")
# Expected: deque is 100x+ faster
```
