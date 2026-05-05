# Pseudo Code — Practice Tasks

> All tasks: write pseudo code FIRST, then implement in **Go**, **Java**, and **Python**.

## Beginner Tasks

**Task 1:** Write pseudo code to count how many even numbers are in an array, then implement.

```text
FUNCTION countEvens(array)
    // Write your pseudo code here
END FUNCTION
```

- **Input:** `[1, 2, 3, 4, 5, 6]`
- **Expected Output:** `3`
- **Evaluation:** Correct pseudo code structure, clean loop, correct modulo

---

**Task 2:** Write pseudo code to check if a string is a palindrome, then implement.

- **Input:** `"racecar"` → `true`, `"hello"` → `false`
- **Evaluation:** Two-pointer approach in pseudo code, edge cases (empty, single char)

---

**Task 3:** Write pseudo code to find the second largest element in an array, then implement.

- **Input:** `[3, 7, 1, 9, 5]` → `7`
- **Evaluation:** Single pass O(n), handle duplicates (`[5, 5, 5]` → no second largest)

---

**Task 4:** Write pseudo code to compute the sum of digits of a number, then implement.

- **Input:** `12345` → `15`
- **Evaluation:** Correct use of modulo and division in pseudo code

---

**Task 5:** Write pseudo code to remove duplicates from a sorted array in-place, then implement.

- **Input:** `[1, 1, 2, 3, 3, 4]` → `[1, 2, 3, 4]`
- **Evaluation:** Two-pointer technique, O(n) time, O(1) extra space

---

## Intermediate Tasks

**Task 6:** Write CLRS-style pseudo code for selection sort, then implement in all 3 languages.

- Include line numbers
- Prove correctness with a loop invariant
- Analyze: O(n^2) time, O(1) space

---

**Task 7:** Write pseudo code for the "valid parentheses" problem, then implement.

- **Input:** `"({[]})"` → `true`, `"({[})"` → `false`
- Use a stack in pseudo code
- **Evaluation:** Correct stack operations, all bracket types

---

**Task 8:** Write pseudo code for rotating an array by k positions, then implement.

- **Input:** `[1,2,3,4,5]`, k=2 → `[4,5,1,2,3]`
- Show TWO approaches: extra array O(n) space, and reverse trick O(1) space

---

**Task 9:** Write pseudo code for matrix transposition, then implement.

- **Input:** `[[1,2,3],[4,5,6]]` → `[[1,4],[2,5],[3,6]]`
- **Evaluation:** Correct index mapping `result[j][i] = matrix[i][j]`

---

**Task 10:** Write pseudo code for converting a decimal number to any base (2-16), then implement.

- **Input:** `255, base 16` → `"FF"`
- **Evaluation:** Correct modulo/division loop, digit-to-char mapping

---

## Advanced Tasks

**Task 11:** Write pseudo code for Dijkstra's algorithm, then implement.

- Must include: priority queue, relaxation, visited set
- Analyze: O((V+E) log V) time

---

**Task 12:** Write pseudo code for the 0/1 knapsack (DP), then implement.

- Show both: top-down (memoization) and bottom-up (tabulation)
- Trace the DP table for a small example

---

**Task 13:** Write pseudo code for topological sort using DFS, then implement.

- Must handle: cycle detection
- **Evaluation:** Correct DFS with gray/black coloring

---

**Task 14:** Translate the following CLRS pseudo code to all 3 languages:

```text
QUICKSORT(A, p, r)
1  if p < r
2      q = PARTITION(A, p, r)
3      QUICKSORT(A, p, q - 1)
4      QUICKSORT(A, q + 1, r)

PARTITION(A, p, r)
1  x = A[r]                // pivot
2  i = p - 1
3  for j = p to r - 1
4      if A[j] <= x
5          i = i + 1
6          exchange A[i] with A[j]
7  exchange A[i + 1] with A[r]
8  return i + 1
```

- **Note:** CLRS uses 1-based indexing. Adjust for 0-based in real code.

---

**Task 15:** Write pseudo code for a Trie (insert, search, startsWith), then implement.

- **Evaluation:** Correct node structure, character-by-character traversal
