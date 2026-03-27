# 0002. Add Two Numbers

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Reconstruct Numbers)](#approach-1-brute-force-reconstruct-numbers)
4. [Approach 2: Optimal (Simultaneous Traversal with Carry)](#approach-2-optimal-simultaneous-traversal-with-carry)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Linked List`, `Math`, `Recursion` |

### Description

> You are given two non-empty linked lists representing two non-negative integers. The digits are stored in **reverse order**, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.
>
> You may assume the two numbers do not contain any leading zero, except the number 0 itself.

### Examples

```
Example 1:
Input:  l1 = [2,4,3],  l2 = [5,6,4]
Output: [7,0,8]
Explanation: 342 + 465 = 807. Stored in reverse: [7,0,8]

Example 2:
Input:  l1 = [0], l2 = [0]
Output: [0]

Example 3:
Input:  l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
Output: [8,9,9,9,0,0,0,1]
Explanation: 9999999 + 9999 = 10009998. Stored in reverse: [8,9,9,9,0,0,0,1]
```

### Constraints

- The number of nodes in each linked list is in the range `[1, 100]`
- `0 <= Node.val <= 9`
- It is guaranteed that the list represents a number that does not have leading zeros

---

## Problem Breakdown

### 1. What is being asked?

Two linked lists each represent a non-negative integer with digits in **reverse order** (least significant digit first). Add them as integers and return the result as a new linked list in the same reversed format.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `l1` | `ListNode` | Head of first linked list (digits in reverse order) |
| `l2` | `ListNode` | Head of second linked list (digits in reverse order) |

Important observations about the input:
- Digits are stored **least-significant first** — this is actually convenient for addition (we add from the smallest digit)
- Both lists are non-empty (at least one node each)
- Each node holds exactly one digit `0–9`
- No leading zeros in the number, except the number `0` itself

### 3. What is the output?

- A **new linked list** representing the sum, also in reverse order (least-significant first)
- The result list may be longer than either input if there is a final carry

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `1 <= nodes <= 100` | At most 100 digits → numbers up to 10^100; must avoid integer overflow in languages with fixed-width integers |
| `0 <= Node.val <= 9` | Each node is a valid decimal digit |
| No leading zeros | The only zero-only list is `[0]` |

### 5. Step-by-step example analysis

#### Example 1: `l1 = [2,4,3]`, `l2 = [5,6,4]` → `[7,0,8]`

```text
l1 represents: 342  (digits reversed: 2 → 4 → 3)
l2 represents: 465  (digits reversed: 5 → 6 → 4)

Column-by-column addition (right-to-left in normal math = left-to-right in lists):

Position 0 (ones):   2 + 5 = 7,  carry = 0  → digit: 7
Position 1 (tens):   4 + 6 = 10, carry = 1  → digit: 0
Position 2 (hundreds): 3 + 4 + 1(carry) = 8, carry = 0 → digit: 8

Result list: [7, 0, 8]   represents: 807
```

#### Example 3: `l1 = [9,9,9,9,9,9,9]`, `l2 = [9,9,9,9]` → `[8,9,9,9,0,0,0,1]`

```text
l1: 9999999
l2:    9999

Pos 0: 9+9=18, carry=1 → digit 8
Pos 1: 9+9+1=19, carry=1 → digit 9
Pos 2: 9+9+1=19, carry=1 → digit 9
Pos 3: 9+9+1=19, carry=1 → digit 9
Pos 4: 9+0+1=10, carry=1 → digit 0
Pos 5: 9+0+1=10, carry=1 → digit 0
Pos 6: 9+0+1=10, carry=1 → digit 0
Pos 7: 0+0+1=1,  carry=0 → digit 1  (extra node from final carry)

Result: [8, 9, 9, 9, 0, 0, 0, 1]  represents: 10009998
```

### 6. Key Observations

1. **Reverse order is convenient** — the head of each list is already the least significant digit, matching how we do addition column by column.
2. **Carry** — when the digit sum exceeds 9, we must carry 1 to the next position.
3. **Different lengths** — one list may be shorter; treat missing digits as 0.
4. **Final carry** — after both lists are exhausted, if carry is still 1, append one more node.
5. **Dummy head trick** — using a dummy (sentinel) node avoids special-casing the first node.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Linked List Traversal | Process nodes one-by-one from both lists | Add Two Numbers (this problem) |
| Carry Propagation | Classic digit-by-digit addition | All arithmetic on digit arrays |
| Dummy Head | Simplifies list construction | Merge Two Sorted Lists |

**Chosen pattern:** `Simultaneous traversal with carry`
**Reason:** The digits are already in the right order for addition. One pass suffices — O(max(m,n)) time, no need to reconstruct actual integers.

---

## Approach 1: Brute Force (Reconstruct Numbers)

### Thought process

> Convert each linked list into a real integer, add the integers normally, then convert the result back into a linked list.
> Simple conceptually, but fails in practice for very long lists (100 digits) due to integer overflow in most languages.

### Algorithm (step-by-step)

1. Traverse `l1`, reconstruct the integer (remember digits are reversed)
2. Traverse `l2`, reconstruct the integer
3. Add the two integers
4. Convert the sum digit by digit back into a linked list (in reverse order again)

### Pseudocode

```text
function addTwoNumbers(l1, l2):
    num1 = 0, multiplier = 1
    for each node in l1:
        num1 += node.val * multiplier
        multiplier *= 10

    num2 = 0, multiplier = 1
    for each node in l2:
        num2 += node.val * multiplier
        multiplier *= 10

    total = num1 + num2

    if total == 0: return ListNode(0)

    dummy = ListNode(0)
    curr = dummy
    while total > 0:
        curr.next = ListNode(total % 10)
        curr = curr.next
        total //= 10
    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m+n) | Two passes to read lists + one pass to build result |
| **Space** | O(max(m,n)) | Result list length |

> **Warning:** This approach causes **integer overflow** in Go and Java for inputs with 100 digits, since the number can be as large as 10^100. Python handles arbitrary precision, but the approach is not general.

### Implementation

#### Go

```go
// addTwoNumbers — Brute Force (reconstruct numbers)
// WARNING: overflows for large inputs (>18 digits)
// Time: O(m+n), Space: O(max(m,n))
func addTwoNumbersBrute(l1 *ListNode, l2 *ListNode) *ListNode {
    // Reconstruct integer from l1
    num1, mult := 0, 1
    for l1 != nil {
        num1 += l1.Val * mult
        mult *= 10
        l1 = l1.Next
    }

    // Reconstruct integer from l2
    num2, mult := 0, 1
    for l2 != nil {
        num2 += l2.Val * mult
        mult *= 10
        l2 = l2.Next
    }

    total := num1 + num2

    // Edge case: sum is zero
    if total == 0 {
        return &ListNode{Val: 0}
    }

    // Build result list
    dummy := &ListNode{}
    curr := dummy
    for total > 0 {
        curr.Next = &ListNode{Val: total % 10}
        curr = curr.Next
        total /= 10
    }
    return dummy.Next
}
```

#### Java

```java
// addTwoNumbers — Brute Force (reconstruct numbers)
// WARNING: overflows for large inputs; use BigInteger in production
// Time: O(m+n), Space: O(max(m,n))
public ListNode addTwoNumbersBrute(ListNode l1, ListNode l2) {
    long num1 = 0, mult = 1;
    while (l1 != null) {
        num1 += l1.val * mult;
        mult *= 10;
        l1 = l1.next;
    }

    long num2 = 0; mult = 1;
    while (l2 != null) {
        num2 += l2.val * mult;
        mult *= 10;
        l2 = l2.next;
    }

    long total = num1 + num2;
    if (total == 0) return new ListNode(0);

    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    while (total > 0) {
        curr.next = new ListNode((int)(total % 10));
        curr = curr.next;
        total /= 10;
    }
    return dummy.next;
}
```

#### Python

```python
def addTwoNumbersBrute(self, l1, l2):
    """
    Brute Force — reconstruct integers (Python handles big ints natively)
    Time: O(m+n), Space: O(max(m,n))
    """
    num1, mult = 0, 1
    while l1:
        num1 += l1.val * mult
        mult *= 10
        l1 = l1.next

    num2, mult = 0, 1
    while l2:
        num2 += l2.val * mult
        mult *= 10
        l2 = l2.next

    total = num1 + num2
    if total == 0:
        return ListNode(0)

    dummy = ListNode()
    curr = dummy
    while total > 0:
        curr.next = ListNode(total % 10)
        curr = curr.next
        total //= 10
    return dummy.next
```

### Dry Run

```text
Input: l1 = [2,4,3], l2 = [5,6,4]

l1 → num1:
  pos 0: 2 * 1   = 2
  pos 1: 4 * 10  = 40
  pos 2: 3 * 100 = 300
  num1 = 342

l2 → num2:
  pos 0: 5 * 1   = 5
  pos 1: 6 * 10  = 60
  pos 2: 4 * 100 = 400
  num2 = 465

total = 342 + 465 = 807

Build list:
  807 % 10 = 7, 807 / 10 = 80
  80  % 10 = 0, 80  / 10 = 8
  8   % 10 = 8, 8   / 10 = 0

Result: [7, 0, 8] ✅
```

---

## Approach 2: Optimal (Simultaneous Traversal with Carry)

### The problem with Brute Force

> Reconstructing integers overflows for lists with up to 100 nodes (10^100).
> Question: Can we add directly on the list nodes without building a big integer?

### Optimization idea

> **Digit-by-digit addition!** Just like how you add two numbers by hand on paper — column by column from right to left.
> Since the lists are already least-significant first, we can traverse both simultaneously and build the result on the fly.
>
> Key insight:
> - At each step: `sum = l1.val + l2.val + carry`
> - New digit: `sum % 10`
> - New carry:  `sum / 10`
> - Keep going until both lists end AND carry is 0

### Algorithm (step-by-step)

1. Create a dummy head node (sentinel) and set `curr = dummy`, `carry = 0`
2. Loop while `l1 != nil OR l2 != nil OR carry != 0`:
   a. `sum = carry`
   b. If `l1 != nil`: `sum += l1.val`, advance `l1`
   c. If `l2 != nil`: `sum += l2.val`, advance `l2`
   d. `carry = sum / 10`, `digit = sum % 10`
   e. Append `ListNode(digit)` to result, advance `curr`
3. Return `dummy.next`

### Pseudocode

```text
function addTwoNumbers(l1, l2):
    dummy = ListNode(0)
    curr = dummy
    carry = 0

    while l1 != null OR l2 != null OR carry != 0:
        sum = carry
        if l1 != null: sum += l1.val; l1 = l1.next
        if l2 != null: sum += l2.val; l2 = l2.next
        carry = sum / 10
        digit = sum % 10
        curr.next = ListNode(digit)
        curr = curr.next

    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(max(m,n)) | Single pass through both lists simultaneously |
| **Space** | O(max(m,n)) | Result list has at most max(m,n)+1 nodes (for final carry) |

### Implementation

#### Go

```go
// addTwoNumbers — Optimal: simultaneous traversal with carry
// Time: O(max(m,n)), Space: O(max(m,n))
func addTwoNumbers(l1 *ListNode, l2 *ListNode) *ListNode {
    dummy := &ListNode{}
    curr := dummy
    carry := 0

    for l1 != nil || l2 != nil || carry != 0 {
        sum := carry

        if l1 != nil {
            sum += l1.Val
            l1 = l1.Next
        }
        if l2 != nil {
            sum += l2.Val
            l2 = l2.Next
        }

        carry = sum / 10
        curr.Next = &ListNode{Val: sum % 10}
        curr = curr.Next
    }

    return dummy.Next
}
```

#### Java

```java
// addTwoNumbers — Optimal: simultaneous traversal with carry
// Time: O(max(m,n)), Space: O(max(m,n))
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    int carry = 0;

    while (l1 != null || l2 != null || carry != 0) {
        int sum = carry;

        if (l1 != null) { sum += l1.val; l1 = l1.next; }
        if (l2 != null) { sum += l2.val; l2 = l2.next; }

        carry = sum / 10;
        curr.next = new ListNode(sum % 10);
        curr = curr.next;
    }

    return dummy.next;
}
```

#### Python

```python
def addTwoNumbers(self, l1, l2):
    """
    Optimal: simultaneous traversal with carry
    Time: O(max(m,n)), Space: O(max(m,n))
    """
    dummy = ListNode()
    curr = dummy
    carry = 0

    while l1 or l2 or carry:
        total = carry
        if l1:
            total += l1.val
            l1 = l1.next
        if l2:
            total += l2.val
            l2 = l2.next
        carry, digit = divmod(total, 10)
        curr.next = ListNode(digit)
        curr = curr.next

    return dummy.next
```

### Dry Run

```text
Input: l1 = [2,4,3], l2 = [5,6,4]   (342 + 465)

dummy → ?    carry = 0

Step 1: l1.val=2, l2.val=5
        sum = 0 + 2 + 5 = 7
        carry = 0, digit = 7
        list: dummy → [7]

Step 2: l1.val=4, l2.val=6
        sum = 0 + 4 + 6 = 10
        carry = 1, digit = 0
        list: dummy → [7] → [0]

Step 3: l1.val=3, l2.val=4
        sum = 1 + 3 + 4 = 8
        carry = 0, digit = 8
        list: dummy → [7] → [0] → [8]

Both lists exhausted, carry = 0 → STOP

Return dummy.next = [7, 0, 8] ✅
```

```text
Input: l1 = [9,9], l2 = [1]   (99 + 1 = 100)

Step 1: sum = 0 + 9 + 1 = 10 → carry=1, digit=0
Step 2: sum = 1 + 9 + 0 = 10 → carry=1, digit=0  (l2 is exhausted, treated as 0)
Step 3: sum = 1 + 0 + 0 = 1  → carry=0, digit=1  (both exhausted, but carry=1 keeps loop going)

Result: [0, 0, 1]  represents 100 ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force (Reconstruct Numbers) | O(m+n) | O(max(m,n)) | Simple to understand | Integer overflow for long lists |
| 2 | Simultaneous Traversal with Carry | O(max(m,n)) | O(max(m,n)) | No overflow risk, handles any length | Slightly more logic to track carry |

### Which solution to choose?

- **In an interview:** Approach 2 — demonstrates understanding of linked list traversal and carry propagation
- **In production:** Approach 2 — safe for arbitrary-precision numbers
- **On Leetcode:** Approach 2 — the canonical expected solution
- **For learning:** Approach 1 first (build intuition), then Approach 2

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Both zeros | `l1=[0], l2=[0]` | `[0]` | 0 + 0 = 0 |
| 2 | Single digit, no carry | `l1=[1], l2=[2]` | `[3]` | 1 + 2 = 3 |
| 3 | Single digit, with carry | `l1=[5], l2=[5]` | `[0,1]` | 5 + 5 = 10 |
| 4 | Different lengths | `l1=[9,9], l2=[1]` | `[0,0,1]` | 99 + 1 = 100 |
| 5 | Final carry creates extra node | `l1=[9,9,9], l2=[1]` | `[0,0,0,1]` | 999 + 1 = 1000 |
| 6 | All nines, carry throughout | `l1=[9,9,9,9,9,9,9], l2=[9,9,9,9]` | `[8,9,9,9,0,0,0,1]` | Maximum carry propagation |
| 7 | l2 longer than l1 | `l1=[5], l2=[8,7,6]` | `[3,8,6]` | 5 + 678 = 683 |

---

## Common Mistakes

### Mistake 1: Forgetting the final carry

```python
# ❌ WRONG — stops when both lists end, misses the last carry
while l1 or l2:
    ...

# ✅ CORRECT — keep going while carry is non-zero
while l1 or l2 or carry:
    ...
```

**Example:** `[5] + [5]` → sum=10, carry=1. After both lists end, the carry must become a new node `[1]`. Result should be `[0, 1]`.

### Mistake 2: Not handling different-length lists

```python
# ❌ WRONG — crashes with AttributeError if l1 is None
sum += l1.val
l1 = l1.next

# ✅ CORRECT — guard with None check
if l1 is not None:
    sum += l1.val
    l1 = l1.next
```

### Mistake 3: Modifying input pointers (using l1/l2 directly)

```python
# This is actually fine — we advance l1/l2 to traverse,
# but the nodes themselves are not modified.
# The issue arises only if you try to reuse l1/l2 after calling the function.
```

### Mistake 4: Off-by-one on dummy node

```python
# ❌ WRONG — returns the dummy node itself (val=0 extra at front)
return dummy

# ✅ CORRECT — return the first real node
return dummy.next
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [445. Add Two Numbers II](https://leetcode.com/problems/add-two-numbers-ii/) | 🟡 Medium | Same but digits stored in forward order — requires reversing or using a stack |
| 2 | [21. Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | 🟢 Easy | Two-list traversal with dummy head pattern |
| 3 | [415. Add Strings](https://leetcode.com/problems/add-strings/) | 🟢 Easy | Same carry logic but on strings instead of linked lists |
| 4 | [67. Add Binary](https://leetcode.com/problems/add-binary/) | 🟢 Easy | Same carry logic with binary digits |
| 5 | [2. Add Two Numbers (this)](https://leetcode.com/problems/add-two-numbers/) | 🟡 Medium | — |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Step-by-step** tab — visualizes both linked lists digit by digit with carry propagation highlighted
> - **Dry Run** tab — traces through Example 1 and Example 3 column by column
> - **Compare** tab — shows Brute Force vs Optimal side by side
