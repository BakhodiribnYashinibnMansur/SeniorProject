# Leetcode Template — Universal Guide

> Universal template for every Leetcode problem.
> All solutions are provided in **3 languages: Go, Java, Python**.
> Each problem is explained with an **interactive HTML/CSS/JS animation**.

## Rule: 1 Problem = 1 Folder

> **IMPORTANT:** Each Leetcode problem **MUST** be in its own separate folder.
> Never put 2 problems in one folder.
> Folder name format: `XXXX-problem-name` (4-digit, kebab-case).

## Overview

| | Description |
|---|---|
| **Purpose** | Universal template for systematically solving Leetcode problems |
| **Rule** | **1 problem = 1 folder** — each problem in its own folder |
| **Files per problem** | 5 files: `solution.md`, `solution.go`, `solution.java`, `solution.py`, `animation.html` |
| **Languages** | All code in **Go**, **Java**, **Python** (in this order) |
| **Visualization** | Each problem has `animation.html` — standalone interactive animation |
| **Code Fences** | `go`, `java`, `python` for implementations, `text` for pseudocode |

### Problem Folder Structure

> **1 problem = 1 folder.** All files inside a single folder.

```
Leetcode/
├── TEMPLATE.md                    ← This template file
│
├── 0001-two-sum/                  ← Problem 1 = 1 folder
│   ├── solution.md                ← Problem analysis + all solutions
│   ├── solution.go                ← Optimal solution (Go)
│   ├── solution.java              ← Optimal solution (Java)
│   ├── solution.py                ← Optimal solution (Python)
│   └── animation.html             ← Interactive animation
│
├── 0015-3sum/                     ← Problem 2 = separate folder
│   ├── solution.md
│   ├── solution.go
│   ├── solution.java
│   ├── solution.py
│   └── animation.html
│
├── 0042-trapping-rain-water/      ← Problem 3 = separate folder
│   ├── solution.md
│   ├── solution.go
│   ├── solution.java
│   ├── solution.py
│   └── animation.html
│
└── ...                            ← Each problem follows this format
```

### Folder Naming Rules

| Rule | Example | Notes |
|---|---|---|
| 4-digit prefix | `0001-`, `0042-`, `0121-` | For proper ordering |
| kebab-case | `two-sum`, `valid-parentheses` | No spaces, lowercase |
| Same as Leetcode slug | `trapping-rain-water` | Can be taken from URL |

```
CORRECT:
  0001-two-sum/
  0042-trapping-rain-water/
  0121-best-time-to-buy-and-sell-stock/

WRONG:
  two-sum/                  ← no number
  1-two-sum/                ← not 4-digit
  0001_two_sum/             ← not underscore, use kebab-case
  0001-TwoSum/              ← not camelCase, use kebab-case
  problems/0001-two-sum/    ← no nested folders needed
```

## Multi-Language Code Block Convention

> **IMPORTANT:** Each solution MUST be provided in 3 languages, in this order:

```
### Example: {{title}}

#### Go

` ` `go
// Go implementation
` ` `

#### Java

` ` `java
// Java implementation
` ` `

#### Python

` ` `python
# Python implementation
` ` `
```

---
---

# TEMPLATE 1 — `solution.md`

# {{XXXX}}. {{Problem Name}}

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Time Complexity Optimization](#approach-2-time-complexity-optimization)
5. [Approach 3: Space Complexity Optimization](#approach-3-space-complexity-optimization)
6. [Approach 4: Alternative Solution](#approach-4-alternative-solution)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [{{XXXX}}. {{Problem Name}}](https://leetcode.com/problems/{{problem-slug}}/) |
| **Difficulty** | Easy / Medium / Hard |
| **Tags** | `Array`, `Hash Table`, `Two Pointers`, `...` |

### Description (English)

> {{Copy the problem description from Leetcode here}}

### Examples

```
Input: {{input}}
Output: {{output}}
Explanation: {{explanation}}
```

### Constraints

- {{constraint_1}}
- {{constraint_2}}
- {{constraint_3}}

---

## Problem Breakdown

> **Goal:** Break the problem into small pieces and explain step by step.
> Explain each step separately, in simple language.

### 1. What is being asked?

{{Explain the essence of the problem in 2-3 sentences in simple language.
Write as if you are explaining to a friend, without technical jargon.}}

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `{{param1}}` | `{{type}}` | {{description}} |
| `{{param2}}` | `{{type}}` | {{description}} |

{{Important observations about the input:}}
- {{Is it sorted? Unsorted?}}
- {{Are there duplicates?}}
- {{Can it be empty?}}

### 3. What is the output?

{{Clearly explain the expected result:}}
- {{What type of result? (number, array, boolean, string)}}
- {{Does the order of the result matter?}}
- {{Can there be multiple correct answers?}}

### 4. Constraints Analysis

| Constraint | Significance |
|---|---|
| `{{constraint}}` | {{What this means — which algorithms work/don't work}} |
| `n <= 10^4` | O(n^2) works, but O(n^3) gives TLE |
| `n <= 10^5` | O(n log n) or O(n) needed |
| `n <= 10^6` | Only O(n) works |

### 5. Step-by-step example analysis

#### Example 1: `{{input}}`

```text
Initial state: {{initial state}}

Step 1: {{step description}}
         {{current state}}

Step 2: {{step description}}
         {{current state}}

Step 3: {{step description}}
         {{current state}}

...

Result: {{output}}
```

#### Example 2: `{{input}}`

```text
{{Same step-by-step analysis}}
```

### 6. Key Observations

> {{Write the most important insights that lead to the solution.
> List each observation as a separate point and explain why it is important.}}

1. **{{Observation 1}}** — {{why it is important}}
2. **{{Observation 2}}** — {{why it is important}}
3. **{{Observation 3}}** — {{why it is important}}

### 7. Pattern Identification

| Pattern | Why it fits | Example |
|---|---|---|
| {{Two Pointers}} | {{sorted array, opposite ends}} | {{Two Sum II}} |
| {{Sliding Window}} | {{subarray, consecutive elements}} | {{Max Subarray}} |
| {{Hash Map}} | {{O(1) lookup needed}} | {{Two Sum}} |

**Chosen pattern:** `{{Pattern Name}}`
**Reason:** {{Why this pattern is the best fit}}

---

## Approach 1: Brute Force

### Thought Process

> {{Explain the simplest — the first solution that comes to mind.
> "If we don't think about any optimizations, what is the simplest approach?"}}

### Algorithm (step by step)

1. {{Step 1}}
2. {{Step 2}}
3. {{Step 3}}
4. ...

### Pseudocode

```text
function solve(input):
    for i = 0 to n-1:
        for j = i+1 to n-1:
            if condition(i, j):
                return result
    return default
```

### Complexity

| | Complexity | Notes |
|---|---|---|
| **Time** | O({{...}}) | {{Why? Nested loop? Reprocessing for each element?}} |
| **Space** | O({{...}}) | {{Is additional memory being used?}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — Brute Force approach
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    // Step 1: {{description}}
    {{code}}

    // Step 2: {{description}}
    {{code}}

    return {{result}}
}
```

#### Java

```java
class Solution {
    // {{functionName}} — Brute Force approach
    // Time: O({{...}}), Space: O({{...}})
    public {{returnType}} {{functionName}}({{params}}) {
        // Step 1: {{description}}
        {{code}}

        // Step 2: {{description}}
        {{code}}

        return {{result}};
    }
}
```

#### Python

```python
class Solution:
    def {{function_name}}(self, {{params}}) -> {{return_type}}:
        """
        Brute Force approach
        Time: O({{...}}), Space: O({{...}})
        """
        # Step 1: {{description}}
        {{code}}

        # Step 2: {{description}}
        {{code}}

        return {{result}}
```

### Dry Run

```text
Input: {{example input}}

Step 1: i=0
  ├── j=1: {{state}} → {{result}}
  ├── j=2: {{state}} → {{result}}
  └── j=3: {{state}} → {{result}}

Step 2: i=1
  ├── j=2: {{state}} → {{result}} FOUND!
  └── return {{result}}

Total operations: {{count}}
```

---

## Approach 2: Time Complexity Optimization

### Problem with Brute Force

> {{What is slow about Brute Force? Why?
> For example: "We are checking all remaining elements for each element — this is O(n^2).
> Can we find it in a single pass?"}}

### Optimization Idea

> {{How do we speed it up?
> For example: "Using a Hash Map, we remember previously seen elements.
> Then we check each element in O(1), total O(n)."}}

### Algorithm (step by step)

1. {{Step 1}}
2. {{Step 2}}
3. {{Step 3}}

### Pseudocode

```text
function solve_optimized(input):
    seen = HashMap()
    for i = 0 to n-1:
        complement = target - input[i]
        if complement in seen:
            return [seen[complement], i]
        seen[input[i]] = i
    return []
```

### Complexity

| | Complexity | Notes |
|---|---|---|
| **Time** | O({{...}}) | {{What changed? Why is it faster?}} |
| **Space** | O({{...}}) | {{Additional memory — trade-off}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — Time Optimized approach
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    // Optimization: {{description}}
    {{code}}

    return {{result}}
}
```

#### Java

```java
class Solution {
    // {{functionName}} — Time Optimized approach
    // Time: O({{...}}), Space: O({{...}})
    public {{returnType}} {{functionName}}({{params}}) {
        // Optimization: {{description}}
        {{code}}

        return {{result}};
    }
}
```

#### Python

```python
class Solution:
    def {{function_name}}(self, {{params}}) -> {{return_type}}:
        """
        Time Optimized approach
        Time: O({{...}}), Space: O({{...}})
        """
        # Optimization: {{description}}
        {{code}}

        return {{result}}
```

### Dry Run

```text
Input: {{example input}}

seen = {}

Step 1: element={{val}}, complement={{val}}
         In seen? No
         seen = {{{val}}: 0}

Step 2: element={{val}}, complement={{val}}
         In seen? Yes! Index: {{idx}}
         return [{{idx}}, 1]

Total operations: {{count}} (Brute Force: {{count}} → {{x}} times faster!)
```

---

## Approach 3: Space Complexity Optimization

### Problem with Previous Solution

> {{Where is memory usage high?
> For example: "Hash Map takes O(n) additional memory.
> If the input is sorted, we can use Two Pointers with O(1) memory."}}

### Optimization Idea

> {{How do we reduce memory?
> For example: "We sort the input and use Two Pointers.
> Or we use an in-place algorithm."}}

### Algorithm (step by step)

1. {{Step 1}}
2. {{Step 2}}
3. {{Step 3}}

### Pseudocode

```text
function solve_space_optimized(input):
    sort(input)
    left, right = 0, len(input)-1
    while left < right:
        current_sum = input[left] + input[right]
        if current_sum == target:
            return [left, right]
        elif current_sum < target:
            left++
        else:
            right--
    return []
```

### Complexity

| | Complexity | Notes |
|---|---|---|
| **Time** | O({{...}}) | {{Sort + scan or just scan?}} |
| **Space** | O({{...}}) | {{In-place? No additional memory?}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — Space Optimized approach
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    // Space optimization: {{description}}
    {{code}}

    return {{result}}
}
```

#### Java

```java
class Solution {
    // {{functionName}} — Space Optimized approach
    // Time: O({{...}}), Space: O({{...}})
    public {{returnType}} {{functionName}}({{params}}) {
        // Space optimization: {{description}}
        {{code}}

        return {{result}};
    }
}
```

#### Python

```python
class Solution:
    def {{function_name}}(self, {{params}}) -> {{return_type}}:
        """
        Space Optimized approach
        Time: O({{...}}), Space: O({{...}})
        """
        # Space optimization: {{description}}
        {{code}}

        return {{result}}
```

### Dry Run

```text
Input: {{example input}} (sorted: {{sorted input}})

left=0 (val={{val}}), right={{n-1}} (val={{val}})

Step 1: sum = {{val}} + {{val}} = {{sum}}
         {{sum}} > target → right-- → right={{n-2}}

Step 2: sum = {{val}} + {{val}} = {{sum}}
         {{sum}} == target → return [{{left}}, {{right}}]

Total operations: {{count}}
Memory: O(1) (previous: O(n) → {{x}} times less!)
```

---

## Approach 4: Alternative Solution

> {{If other approaches exist, write them here.
> For example: Bit Manipulation, Math formula, Monotonic Stack, Binary Search, etc.
> Open a new "Approach N" section for each alternative approach.}}

### Idea

> {{The essence of this approach}}

### Complexity

| | Complexity | Notes |
|---|---|---|
| **Time** | O({{...}}) | {{description}} |
| **Space** | O({{...}}) | {{description}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — {{Approach Name}}
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    {{code}}
}
```

#### Java

```java
class Solution {
    // {{functionName}} — {{Approach Name}}
    // Time: O({{...}}), Space: O({{...}})
    public {{returnType}} {{functionName}}({{params}}) {
        {{code}}
    }
}
```

#### Python

```python
class Solution:
    def {{function_name}}(self, {{params}}) -> {{return_type}}:
        """{{Approach Name}} — Time: O({{...}}), Space: O({{...}})"""
        {{code}}
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O({{...}}) | O({{...}}) | {{Simple, easy to understand}} | {{Slow}} |
| 2 | TC Optimized | O({{...}}) | O({{...}}) | {{Fast}} | {{More memory}} |
| 3 | SC Optimized | O({{...}}) | O({{...}}) | {{Less memory}} | {{Requires sorting / has limitations}} |
| 4 | Alternative | O({{...}}) | O({{...}}) | {{...}} | {{...}} |

### Which solution to choose?

- **In an interview:** {{TC Optimized — fast and easy to explain}}
- **In production:** {{Depends on the situation — large data = SC Optimized, fast response = TC Optimized}}
- **On Leetcode:** {{TC Optimized — speed matters more}}

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty input | `{{}}` | `{{}}` | {{Boundary condition}} |
| 2 | Single element | `{{[x]}}` | `{{}}` | {{Minimal input}} |
| 3 | All identical | `{{[x,x,x]}}` | `{{}}` | {{Duplicates}} |
| 4 | Negative numbers | `{{[-1,-2]}}` | `{{}}` | {{Negative values}} |
| 5 | Very large input | `n = 10^5` | — | {{TLE check}} |
| 6 | Min/Max values | `{{INT_MIN, INT_MAX}}` | `{{}}` | {{Overflow risk}} |

---

## Common Mistakes

### Mistake 1: {{Mistake name}}

```python
# WRONG
{{wrong code}}

# CORRECT
{{correct code}}
```

**Reason:** {{Why this is wrong and how to fix it}}

### Mistake 2: {{Mistake name}}

```python
# WRONG
{{wrong code}}

# CORRECT
{{correct code}}
```

**Reason:** {{Why this is wrong and how to fix it}}

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [{{XXXX}}. {{Name}}](https://leetcode.com/problems/{{slug}}/) | {{Easy/Medium/Hard}} | {{How it is related}} |
| 2 | [{{XXXX}}. {{Name}}](https://leetcode.com/problems/{{slug}}/) | {{Easy/Medium/Hard}} | {{How it is related}} |
| 3 | [{{XXXX}}. {{Name}}](https://leetcode.com/problems/{{slug}}/) | {{Easy/Medium/Hard}} | {{How it is related}} |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Separate tab for each solution
> - Step-by-step visualization
> - Speed control
> - Custom input entry
> - **🎲 Random Generate** — choose size and range, always generates input with a valid solution
> - **📊 Complexity Chart** — animated bar graph comparing all approaches at a glance
> - **localStorage** — state is preserved on refresh

---
---

# TEMPLATE 2 — `solution.go`

```go
package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// {{XXXX}}. {{Problem Name}}
// https://leetcode.com/problems/{{problem-slug}}/
// Difficulty: {{Easy/Medium/Hard}}
// Tags: {{tag1}}, {{tag2}}, {{tag3}}
// ============================================================

// {{functionName}} — Optimal Solution
// Approach: {{approach name}}
// Time:  O({{...}})
// Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
	// Step 1: {{description}}
	{{code}}

	// Step 2: {{description}}
	{{code}}

	// Step 3: {{description}}
	{{code}}

	return {{result}}
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected interface{}) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Basic case
	test("Basic case", {{functionName}}({{input1}}), {{expected1}})

	// Test 2: Edge case
	test("Edge case", {{functionName}}({{input2}}), {{expected2}})

	// Test 3: Large input
	test("Large input", {{functionName}}({{input3}}), {{expected3}})

	// Test 4: {{description}}
	test("{{description}}", {{functionName}}({{input4}}), {{expected4}})

	// Test 5: {{description}}
	test("{{description}}", {{functionName}}({{input5}}), {{expected5}})

	// Results
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
```

---
---

# TEMPLATE 3 — `solution.java`

```java
import java.util.*;

/**
 * {{XXXX}}. {{Problem Name}}
 * https://leetcode.com/problems/{{problem-slug}}/
 * Difficulty: {{Easy/Medium/Hard}}
 * Tags: {{tag1}}, {{tag2}}, {{tag3}}
 */
class Solution {

    /**
     * Optimal Solution
     * Approach: {{approach name}}
     * Time:  O({{...}})
     * Space: O({{...}})
     */
    public {{returnType}} {{functionName}}({{params}}) {
        // Step 1: {{description}}
        {{code}}

        // Step 2: {{description}}
        {{code}}

        // Step 3: {{description}}
        {{code}}

        return {{result}};
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    // Test helper method — for arrays
    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    // Test helper method — for general objects
    static void test(String name, Object got, Object expected) {
        if (Objects.equals(got, expected)) {
            System.out.printf("PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Basic case
        test("Basic case", sol.{{functionName}}({{input1}}), {{expected1}});

        // Test 2: Edge case
        test("Edge case", sol.{{functionName}}({{input2}}), {{expected2}});

        // Test 3: Large input
        test("Large input", sol.{{functionName}}({{input3}}), {{expected3}});

        // Test 4: {{description}}
        test("{{description}}", sol.{{functionName}}({{input4}}), {{expected4}});

        // Test 5: {{description}}
        test("{{description}}", sol.{{functionName}}({{input5}}), {{expected5}});

        // Results
        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
```

---
---

# TEMPLATE 4 — `solution.py`

```python
from typing import List, Optional

# ============================================================
# {{XXXX}}. {{Problem Name}}
# https://leetcode.com/problems/{{problem-slug}}/
# Difficulty: {{Easy/Medium/Hard}}
# Tags: {{tag1}}, {{tag2}}, {{tag3}}
# ============================================================


class Solution:
    def {{function_name}}(self, {{params}}) -> {{return_type}}:
        """
        Optimal Solution
        Approach: {{approach name}}
        Time:  O({{...}})
        Space: O({{...}})
        """
        # Step 1: {{description}}
        {{code}}

        # Step 2: {{description}}
        {{code}}

        # Step 3: {{description}}
        {{code}}

        return {{result}}


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Basic case
    test("Basic case", sol.{{function_name}}({{input1}}), {{expected1}})

    # Test 2: Edge case
    test("Edge case", sol.{{function_name}}({{input2}}), {{expected2}})

    # Test 3: Large input
    test("Large input", sol.{{function_name}}({{input3}}), {{expected3}})

    # Test 4: {{description}}
    test("{{description}}", sol.{{function_name}}({{input4}}), {{expected4}})

    # Test 5: {{description}}
    test("{{description}}", sol.{{function_name}}({{input5}}), {{expected5}})

    # Results
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
```

---
---

# TEMPLATE 5 — `animation.html`

> Standalone HTML file — no external libraries.
> Opens directly in a browser.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{XXXX}}. {{Problem Name}} — Visual Animation</title>
    <style>
        /* ============================================================
           RESET & BASE
           ============================================================ */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #0f172a;
            color: #e2e8f0;
            min-height: 100vh;
            padding: 20px;
        }

        /* ============================================================
           HEADER
           ============================================================ */
        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 1.8rem;
            background: linear-gradient(135deg, #60a5fa, #a78bfa);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 8px;
        }

        .header p {
            color: #94a3b8;
            font-size: 0.95rem;
        }

        /* ============================================================
           TABS — separate tab for each approach
           ============================================================ */
        .tabs {
            display: flex;
            gap: 4px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            justify-content: center;
        }

        .tab {
            padding: 10px 20px;
            background: #1e293b;
            border: 1px solid #334155;
            border-radius: 8px 8px 0 0;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 0.9rem;
        }

        .tab:hover { background: #334155; }

        .tab.active {
            background: #3b82f6;
            border-color: #3b82f6;
            color: white;
            font-weight: 600;
        }

        /* ============================================================
           MAIN CONTAINER
           ============================================================ */
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: #1e293b;
            border-radius: 12px;
            padding: 24px;
            border: 1px solid #334155;
        }

        /* ============================================================
           CONTROLS — speed, play/pause, step, reset
           ============================================================ */
        .controls {
            display: flex;
            gap: 10px;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.9rem;
            font-weight: 500;
            transition: all 0.2s;
        }

        .btn-play { background: #22c55e; color: white; }
        .btn-play:hover { background: #16a34a; }

        .btn-pause { background: #ef4444; color: white; }
        .btn-pause:hover { background: #dc2626; }

        .btn-step { background: #3b82f6; color: white; }
        .btn-step:hover { background: #2563eb; }

        .btn-reset { background: #6b7280; color: white; }
        .btn-reset:hover { background: #4b5563; }

        .speed-control {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .speed-control label { color: #94a3b8; font-size: 0.85rem; }

        .speed-control select {
            padding: 6px 10px;
            background: #0f172a;
            color: #e2e8f0;
            border: 1px solid #334155;
            border-radius: 4px;
        }

        /* ============================================================
           INPUT — custom input entry
           ============================================================ */
        .input-section {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            align-items: center;
            justify-content: center;
            flex-wrap: wrap;
        }

        .input-section input {
            padding: 8px 12px;
            background: #0f172a;
            color: #e2e8f0;
            border: 1px solid #334155;
            border-radius: 6px;
            font-size: 0.9rem;
            min-width: 200px;
        }

        .input-section input::placeholder { color: #64748b; }

        .btn-generate { background: #8b5cf6; color: white; }
        .btn-generate:hover { background: #7c3aed; }

        .gen-options {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .gen-options label { color: #94a3b8; font-size: 0.8rem; }

        .gen-options select {
            padding: 5px 8px;
            background: #0f172a;
            color: #e2e8f0;
            border: 1px solid #334155;
            border-radius: 4px;
            font-size: 0.85rem;
        }

        /* ============================================================
           VISUALIZATION AREA
           ============================================================ */
        .viz-area {
            min-height: 300px;
            background: #0f172a;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            position: relative;
            overflow: hidden;
        }

        /* Array elements */
        .array-container {
            display: flex;
            gap: 4px;
            justify-content: center;
            flex-wrap: wrap;
            margin-bottom: 20px;
        }

        .array-element {
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s ease;
            position: relative;
        }

        .array-element .index {
            position: absolute;
            top: -18px;
            font-size: 0.7rem;
            color: #64748b;
        }

        /* Color coding */
        .el-default { background: #334155; color: #e2e8f0; }
        .el-active { background: #3b82f6; color: white; transform: scale(1.1); }
        .el-comparing { background: #f59e0b; color: #0f172a; }
        .el-found { background: #22c55e; color: white; transform: scale(1.15); }
        .el-visited { background: #6366f1; color: white; }
        .el-discarded { background: #1e293b; color: #475569; opacity: 0.5; }
        .el-swap { background: #ec4899; color: white; }
        .el-sorted { background: #14b8a6; color: white; }

        /* Pointers */
        .pointer {
            text-align: center;
            font-size: 0.75rem;
            font-weight: 600;
            margin-top: 4px;
        }

        .pointer-left { color: #22c55e; }
        .pointer-right { color: #ef4444; }
        .pointer-mid { color: #f59e0b; }
        .pointer-i { color: #3b82f6; }
        .pointer-j { color: #a78bfa; }

        /* ============================================================
           INFO PANEL — step info, complexity counter
           ============================================================ */
        .info-panel {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin-bottom: 20px;
        }

        .info-box {
            background: #0f172a;
            border-radius: 8px;
            padding: 16px;
            border: 1px solid #334155;
        }

        .info-box h3 {
            font-size: 0.85rem;
            color: #94a3b8;
            margin-bottom: 8px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .info-box .value {
            font-size: 1.2rem;
            font-weight: 600;
        }

        /* Step description */
        .step-description {
            background: #0f172a;
            border-left: 3px solid #3b82f6;
            padding: 12px 16px;
            border-radius: 0 8px 8px 0;
            margin-bottom: 20px;
            font-size: 0.9rem;
            line-height: 1.5;
        }

        /* ============================================================
           COMPARISON VIEW — Brute Force vs Optimized
           ============================================================ */
        .comparison {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .comparison-side {
            background: #0f172a;
            border-radius: 8px;
            padding: 16px;
            border: 1px solid #334155;
        }

        .comparison-side h3 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 1rem;
        }

        .comparison-side .ops-counter {
            text-align: center;
            font-size: 2rem;
            font-weight: 700;
            margin: 10px 0;
        }

        .ops-brute { color: #ef4444; }
        .ops-optimal { color: #22c55e; }

        /* ============================================================
           LEGEND
           ============================================================ */
        .legend {
            display: flex;
            gap: 16px;
            justify-content: center;
            flex-wrap: wrap;
            margin-top: 16px;
        }

        .legend-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.8rem;
            color: #94a3b8;
        }

        .legend-color {
            width: 16px;
            height: 16px;
            border-radius: 4px;
        }

        /* ============================================================
           COMPLEXITY CHART — bar graph comparing all approaches
           ============================================================ */
        .complexity-chart {
            margin-top: 20px;
            background: #0f172a;
            border-radius: 10px;
            padding: 20px;
            border: 1px solid #1e293b;
        }

        .chart-title {
            text-align: center;
            font-size: 0.85rem;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 16px;
        }

        .chart-bars {
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .chart-row {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .chart-label {
            min-width: 140px;
            font-size: 0.82rem;
            color: #94a3b8;
            text-align: right;
            flex-shrink: 0;
        }

        .chart-bar-wrap {
            flex: 1;
            background: #1e293b;
            border-radius: 6px;
            height: 26px;
            overflow: hidden;
            position: relative;
        }

        .chart-bar {
            height: 100%;
            border-radius: 6px;
            display: flex;
            align-items: center;
            padding-left: 10px;
            font-size: 0.78rem;
            font-weight: 600;
            color: white;
            transition: width 0.8s cubic-bezier(0.16, 1, 0.3, 1);
            width: 0;
            white-space: nowrap;
            position: absolute;
        }

        .chart-bar.brute    { background: linear-gradient(90deg, #ef4444, #f87171); }
        .chart-bar.opt-tc   { background: linear-gradient(90deg, #3b82f6, #60a5fa); }
        .chart-bar.opt-sc   { background: linear-gradient(90deg, #8b5cf6, #a78bfa); }
        .chart-bar.optimal  { background: linear-gradient(90deg, #22c55e, #4ade80); }

        .chart-complexity {
            min-width: 90px;
            font-size: 0.8rem;
            font-weight: 600;
            color: #e2e8f0;
            flex-shrink: 0;
        }

        /* ============================================================
           RESPONSIVE
           ============================================================ */
        @media (max-width: 1024px) {
            .container { padding: 16px; }
        }

        @media (max-width: 768px) {
            body { padding: 10px; }
            .header h1 { font-size: 1.4rem; }
            .header { margin-bottom: 16px; }
            .info-panel { grid-template-columns: repeat(2, 1fr); gap: 8px; }
            .comparison { grid-template-columns: 1fr; }
            .container { padding: 14px; }
            .tab { padding: 8px 14px; font-size: 0.8rem; }
            .btn { padding: 7px 12px; font-size: 0.8rem; }
            .input-section { gap: 6px; }
            .input-section input { padding: 6px 10px; font-size: 0.8rem; }
            .array-element { width: 2.8rem; height: 2.8rem; font-size: 1rem; border-radius: 8px; }
            .step-description { padding: 10px 12px; font-size: 0.82rem; }
            .viz-area { padding: 14px; }
            .chart-label { min-width: 100px; font-size: 0.75rem; }
            .chart-complexity { min-width: 70px; font-size: 0.72rem; }
            .chart-bar { font-size: 0.7rem; }
        }

        @media (max-width: 480px) {
            body { padding: 6px; }
            .header h1 { font-size: 1.1rem; }
            .header p { font-size: 0.8rem; }
            .header { margin-bottom: 10px; }
            .tabs { gap: 2px; margin-bottom: 12px; }
            .tab { padding: 6px 10px; font-size: 0.72rem; }
            .container { padding: 10px; border-radius: 8px; }
            .info-panel { grid-template-columns: repeat(2, 1fr); gap: 6px; }
            .info-box { padding: 8px; }
            .info-box h3 { font-size: 0.6rem; }
            .info-box .value { font-size: 0.9rem; }
            .input-section { flex-direction: column; align-items: stretch; }
            .input-section input { width: 100%; }
            .controls { gap: 6px; }
            .btn { padding: 6px 10px; font-size: 0.75rem; }
            .speed-control { width: 100%; justify-content: center; }
            .step-description { padding: 8px 10px; font-size: 0.78rem; min-height: 40px; }
            .viz-area { padding: 10px; min-height: 150px; }
            .array-element { width: 2.4rem; height: 2.4rem; font-size: 0.85rem; border-radius: 6px; }
            .idx-label { font-size: 0.6rem; }
            .ptr-label { font-size: 0.65rem; }
            .array-container { gap: 4px; }
            .hashmap-entry { padding: 6px 8px; min-width: 50px; }
            .hashmap-grid { gap: 4px; }
            .calc-box { padding: 8px 12px; font-size: 0.9rem; }
            .legend { gap: 8px; margin-top: 10px; }
            .legend-item { font-size: 0.7rem; gap: 4px; }
            .legend-color { width: 12px; height: 12px; }
            .complexity-chart { padding: 14px; }
            .chart-label { min-width: 80px; font-size: 0.68rem; }
            .chart-complexity { display: none; }
            .chart-bar-wrap { height: 22px; }
        }
    </style>
</head>
<body>

    <!-- HEADER -->
    <div class="header">
        <h1>{{XXXX}}. {{Problem Name}}</h1>
        <p>{{Difficulty}} — {{Tags}}</p>
    </div>

    <!-- TABS -->
    <div class="tabs" id="tabs">
        <div class="tab active" data-tab="brute">Brute Force</div>
        <div class="tab" data-tab="optimized">TC Optimized</div>
        <div class="tab" data-tab="space">SC Optimized</div>
        <div class="tab" data-tab="compare">Compare All</div>
    </div>

    <!-- MAIN CONTAINER -->
    <div class="container">

        <!-- INPUT -->
        <div class="input-section">
            <input type="text" id="customInput" placeholder="Example: [2,7,11,15], target=9">
            <button class="btn btn-step" onclick="applyInput()">Apply</button>
            <button class="btn btn-generate" onclick="generateRandom()">&#127922; Generate</button>
            <div class="gen-options">
                <label>Size:</label>
                <select id="genSize">
                    <option value="4">4</option>
                    <option value="6" selected>6</option>
                    <option value="8">8</option>
                    <option value="10">10</option>
                    <option value="15">15</option>
                    <option value="20">20</option>
                </select>
                <label>Range:</label>
                <select id="genRange">
                    <option value="20">-20..20</option>
                    <option value="50" selected>-50..50</option>
                    <option value="100">-100..100</option>
                    <option value="500">-500..500</option>
                </select>
            </div>
        </div>

        <!-- CONTROLS -->
        <div class="controls">
            <button class="btn btn-play" id="playBtn" onclick="play()">Play</button>
            <button class="btn btn-pause" onclick="pause()">Pause</button>
            <button class="btn btn-step" onclick="stepForward()">Step</button>
            <button class="btn btn-reset" onclick="reset()">Reset</button>
            <div class="speed-control">
                <label>Speed:</label>
                <select id="speed" onchange="updateSpeed()">
                    <option value="2000">Slow</option>
                    <option value="1000" selected>Normal</option>
                    <option value="500">Fast</option>
                    <option value="200">Very Fast</option>
                </select>
            </div>
        </div>

        <!-- STEP DESCRIPTION -->
        <div class="step-description" id="stepDesc">
            Press "Play" or use "Step" to go step by step.
        </div>

        <!-- VISUALIZATION AREA -->
        <div class="viz-area" id="vizArea">
            <!-- {{Dynamically rendered via JavaScript}} -->
        </div>

        <!-- INFO PANEL -->
        <div class="info-panel">
            <div class="info-box">
                <h3>Step</h3>
                <div class="value" id="stepCounter">0 / 0</div>
            </div>
            <div class="info-box">
                <h3>Operations</h3>
                <div class="value" id="opsCounter">0</div>
            </div>
            <div class="info-box">
                <h3>Time Complexity</h3>
                <div class="value" id="timeComplexity">O({{...}})</div>
            </div>
            <div class="info-box">
                <h3>Space Complexity</h3>
                <div class="value" id="spaceComplexity">O({{...}})</div>
            </div>
        </div>

        <!-- LEGEND -->
        <div class="legend">
            <div class="legend-item">
                <div class="legend-color" style="background: #334155;"></div>
                <span>Default</span>
            </div>
            <div class="legend-item">
                <div class="legend-color" style="background: #3b82f6;"></div>
                <span>Active / Current</span>
            </div>
            <div class="legend-item">
                <div class="legend-color" style="background: #f59e0b;"></div>
                <span>Comparing</span>
            </div>
            <div class="legend-item">
                <div class="legend-color" style="background: #22c55e;"></div>
                <span>Found / Match</span>
            </div>
            <div class="legend-item">
                <div class="legend-color" style="background: #6366f1;"></div>
                <span>Visited</span>
            </div>
            <div class="legend-item">
                <div class="legend-color" style="background: #1e293b; opacity: 0.5;"></div>
                <span>Discarded</span>
            </div>
        </div>

        <!-- COMPLEXITY CHART -->
        <!-- data-w = bar width % (0–100); lower = faster algorithm -->
        <!-- Color classes: brute (red) | opt-tc (blue) | opt-sc (purple) | optimal (green) -->
        <div class="complexity-chart">
            <div class="chart-title">Algorithm Complexity</div>
            <div class="chart-bars">
                <div class="chart-row">
                    <div class="chart-label">Brute Force</div>
                    <div class="chart-bar-wrap"><div class="chart-bar brute" data-w="90" style="width:0">O({{...}})</div></div>
                    <div class="chart-complexity">O({{...}}) / O({{...}})</div>
                </div>
                <div class="chart-row">
                    <div class="chart-label">TC Optimized</div>
                    <div class="chart-bar-wrap"><div class="chart-bar opt-tc" data-w="40" style="width:0">O({{...}})</div></div>
                    <div class="chart-complexity">O({{...}}) / O({{...}})</div>
                </div>
                <div class="chart-row">
                    <div class="chart-label">SC Optimized</div>
                    <div class="chart-bar-wrap"><div class="chart-bar opt-sc" data-w="55" style="width:0">O({{...}})</div></div>
                    <div class="chart-complexity">O({{...}}) / O({{...}})</div>
                </div>
                <div class="chart-row">
                    <div class="chart-label">Optimal</div>
                    <div class="chart-bar-wrap"><div class="chart-bar optimal" data-w="30" style="width:0">O({{...}})</div></div>
                    <div class="chart-complexity">O({{...}}) / O({{...}})</div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // ============================================================
        // LOCAL STORAGE — save / load state
        // ============================================================
        const STORAGE_KEY = '{{problem-slug}}_animation_state';

        function saveState() {
            const state = { currentTab, currentStep, speed, inputData };
            try { localStorage.setItem(STORAGE_KEY, JSON.stringify(state)); } catch(e) {}
        }

        function loadState() {
            try {
                const raw = localStorage.getItem(STORAGE_KEY);
                if (!raw) return null;
                return JSON.parse(raw);
            } catch(e) { return null; }
        }

        // ============================================================
        // STATE
        // ============================================================
        const saved = loadState();
        let currentTab = saved ? saved.currentTab : 'brute';
        let steps = [];
        let currentStep = saved ? saved.currentStep : 0;
        let isPlaying = false;
        let playInterval = null;
        let speed = saved ? saved.speed : 1000;
        // {{Default input matching the problem}}
        let inputData = saved ? saved.inputData : {{default_input}};

        // ============================================================
        // TAB SWITCHING
        // ============================================================
        document.querySelectorAll('.tab').forEach(tab => {
            tab.addEventListener('click', () => {
                document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                currentTab = tab.dataset.tab;
                reset();
                generateSteps();
                renderStep();
                saveState();
            });
        });

        // ============================================================
        // CONTROLS
        // ============================================================
        function play() {
            if (isPlaying) return;
            isPlaying = true;
            playInterval = setInterval(() => {
                if (currentStep < steps.length - 1) {
                    currentStep++;
                    renderStep();
                } else {
                    pause();
                }
            }, speed);
        }

        function pause() {
            isPlaying = false;
            clearInterval(playInterval);
        }

        function stepForward() {
            pause();
            if (currentStep < steps.length - 1) {
                currentStep++;
                renderStep();
            }
        }

        function reset() {
            pause();
            currentStep = 0;
            renderStep();
        }

        function updateSpeed() {
            speed = parseInt(document.getElementById('speed').value);
            if (isPlaying) {
                pause();
                play();
            }
            saveState();
        }

        function applyInput() {
            const raw = document.getElementById('customInput').value;
            // {{Parse custom input — modify according to the problem}}
            try {
                // Example: "[2,7,11,15], target=9" → {arr: [2,7,11,15], target: 9}
                inputData = parseInput(raw);
                reset();
                generateSteps();
                renderStep();
                saveState();
            } catch(e) {
                document.getElementById('stepDesc').textContent = 'Invalid format. Example: [2,7,11,15], target=9';
            }
        }

        // ============================================================
        // RANDOM GENERATOR — {{modify according to the problem}}
        // ============================================================
        function generateRandom() {
            const size  = parseInt(document.getElementById('genSize')?.value  || 6);
            const range = parseInt(document.getElementById('genRange')?.value || 50);

            // ── IMPORTANT: always generate input that has a valid answer! ──
            //
            // Example A — Array + target (Two Sum style):
            //   const arr = randomArray(size, range);
            //   const i1 = randInt(0, size), i2 = randInt(0, size, i1);
            //   const target = arr[i1] + arr[i2];          // guaranteed pair
            //   inputData = { arr, target };
            //   document.getElementById('customInput').value = `[${arr}], target=${target}`;
            //
            // Example B — Sorted array pair (Median style):
            //   const a = randomArray(size, range).sort((a,b)=>a-b);
            //   const b = randomArray(randInt(1,size+1), range).sort((a,b)=>a-b);
            //   inputData = { nums1: a, nums2: b };
            //
            // Example C — String (Substring / Palindrome style):
            //   const alpha = 'abcdefghij';
            //   inputData = Array.from({length: size}, () =>
            //       alpha[Math.floor(Math.random() * alpha.length)]).join('');
            //   document.getElementById('customInput').value = inputData;
            //
            // Example D — Integer (Reverse / Palindrome Number style):
            //   inputData = randInt(-range, range + 1);
            //   document.getElementById('customInput').value = inputData;
            //
            // Example E — Linked list (Add Two Numbers style):
            //   const digits = n => Array.from({length: n}, () => Math.floor(Math.random()*10));
            //   inputData = { l1: digits(size), l2: digits(randInt(1, size+1)) };

            inputData = {{generated_input}};  // ← replace with one of the examples above

            document.getElementById('customInput').value = JSON.stringify(inputData);
            reset();
            generateSteps();
            renderStep();
            saveState();
        }

        // Helper: random array
        function randomArray(size, range) {
            const arr = [];
            const used = new Set();
            for (let i = 0; i < size; i++) {
                let val;
                do { val = Math.floor(Math.random() * (range * 2 + 1)) - range; }
                while (used.has(val) && used.size < range * 2);
                used.add(val);
                arr.push(val);
            }
            return arr;
        }

        // Helper: random int [min, max) with exclude
        function randInt(min, max, exclude) {
            let val;
            do { val = Math.floor(Math.random() * (max - min)) + min; }
            while (val === exclude);
            return val;
        }

        // ============================================================
        // PARSE INPUT — {{modify according to the problem}}
        // ============================================================
        function parseInput(raw) {
            // {{Write this function according to the problem}}
            // Example:
            // const match = raw.match(/\[(.*?)\].*?=\s*(\d+)/);
            // const arr = match[1].split(',').map(Number);
            // const target = parseInt(match[2]);
            // return { arr, target };
        }

        // ============================================================
        // GENERATE STEPS — {{write according to the problem}}
        // ============================================================
        function generateSteps() {
            steps = [];

            if (currentTab === 'brute') {
                generateBruteForceSteps();
            } else if (currentTab === 'optimized') {
                generateOptimizedSteps();
            } else if (currentTab === 'space') {
                generateSpaceOptimizedSteps();
            } else if (currentTab === 'compare') {
                generateComparisonSteps();
            }
        }

        function generateBruteForceSteps() {
            // {{Push each step of the Brute Force algorithm into the steps array}}
            // Each step:
            // {
            //     array: [...],           // array state
            //     highlights: {0: 'active', 1: 'comparing', ...},  // element colors
            //     pointers: {0: {label: 'i', class: 'pointer-i'}, 1: {label: 'j', class: 'pointer-j'}},
            //     description: "i=0, j=1: 2+7=9 == 9 Found!",
            //     ops: 1,                 // number of operations
            //     extraData: {}           // additional data (hash map, stack, etc.)
            // }
        }

        function generateOptimizedSteps() {
            // {{TC Optimized algorithm steps}}
        }

        function generateSpaceOptimizedSteps() {
            // {{SC Optimized algorithm steps}}
        }

        function generateComparisonSteps() {
            // {{Show both algorithms in parallel}}
        }

        // ============================================================
        // RENDER STEP
        // ============================================================
        function renderStep() {
            if (steps.length === 0) return;
            saveState();

            const step = steps[currentStep];
            const vizArea = document.getElementById('vizArea');
            const stepDesc = document.getElementById('stepDesc');

            // Update counters
            document.getElementById('stepCounter').textContent =
                `${currentStep + 1} / ${steps.length}`;
            document.getElementById('opsCounter').textContent = step.ops || 0;
            stepDesc.textContent = step.description || '';

            // Render array
            let html = '<div class="array-container">';
            step.array.forEach((val, idx) => {
                const colorClass = step.highlights[idx] || 'default';
                const pointer = step.pointers && step.pointers[idx];
                html += `
                    <div>
                        <div class="array-element el-${colorClass}">
                            <span class="index">${idx}</span>
                            ${val}
                        </div>
                        ${pointer ? `<div class="pointer ${pointer.class}">${pointer.label}</div>` : ''}
                    </div>
                `;
            });
            html += '</div>';

            // {{Additional visualization — hash map, stack, tree, graph, etc.}}
            if (step.extraData) {
                html += renderExtraData(step.extraData);
            }

            vizArea.innerHTML = html;
        }

        function renderExtraData(data) {
            // {{Render additional data according to the problem}}
            // Example: Hash Map visualization
            // let html = '<div class="hash-map">...</div>';
            // return html;
            return '';
        }

        // ============================================================
        // COMPLEXITY CHART — bar animation on load
        // ============================================================
        // Each .chart-bar in HTML must have: data-w="N" style="width:0"
        // where N is 0–100 (lower = faster algorithm).
        // Color classes: brute (red) | opt-tc (blue) | opt-sc (purple) | optimal (green)
        setTimeout(() => {
            document.querySelectorAll('.chart-bar[data-w]').forEach(bar => {
                bar.style.width = bar.dataset.w + '%';
            });
        }, 400);

        // ============================================================
        // INIT — restore state from localStorage
        // ============================================================

        // Restore UI elements from saved state
        if (saved) {
            document.getElementById('customInput').value = JSON.stringify(inputData);
            document.getElementById('speed').value = speed;
        }

        // Restore active tab
        document.querySelectorAll('.tab').forEach(t => {
            t.classList.toggle('active', t.dataset.tab === currentTab);
        });

        // Generate steps and render
        generateSteps();
        if (currentStep >= steps.length) currentStep = 0;
        renderStep();
    </script>

</body>
</html>
```

---
---

# QUICK REFERENCE — All Templates

| # | File | Purpose |
|---|---|---|
| 1 | `solution.md` | Problem analysis + 4+ solutions (Brute → TC Opt → SC Opt → Alt) in 3 languages |
| 2 | `solution.go` | Best solution — Go (complete, with tests) |
| 3 | `solution.java` | Best solution — Java (complete, with tests) |
| 4 | `solution.py` | Best solution — Python (complete, with tests) |
| 5 | `animation.html` | Interactive visual animation (tabs, steps, speed, input) |

## Naming Convention

```
Leetcode/
├── TEMPLATE.md
├── 0001-two-sum/
│   ├── solution.md
│   ├── solution.go
│   ├── solution.java
│   ├── solution.py
│   └── animation.html
├── 0002-add-two-numbers/
│   ├── solution.md
│   ├── solution.go
│   ├── solution.java
│   ├── solution.py
│   └── animation.html
└── ...
```

## Placeholders

| Placeholder | Description |
|---|---|
| `{{XXXX}}` | Leetcode problem number (4-digit: 0001, 0042, 0121) |
| `{{Problem Name}}` | Problem name (in English) |
| `{{problem-slug}}` | URL slug (e.g.: `two-sum`) |
| `{{functionName}}` | Go/Java function name (camelCase) |
| `{{function_name}}` | Python function name (snake_case) |
| `{{params}}` | Function parameters |
| `{{returnType}}` | Return type |
| `{{return_type}}` | Python return type |
| `{{code}}` | Implementation code |
| `{{result}}` | Result |
| `{{description}}` | Description/explanation |
| `{{...}}` | Complexity notation (e.g.: n, n^2, n log n) |
| `{{default_input}}` | Default input for the animation |
