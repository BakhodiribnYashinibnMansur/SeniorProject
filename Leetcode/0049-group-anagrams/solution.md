# 0049. Group Anagrams

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Sort as Key](#approach-1-sort-as-key)
4. [Approach 2: Character Count as Key](#approach-2-character-count-as-key)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [49. Group Anagrams](https://leetcode.com/problems/group-anagrams/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Hash Table`, `String`, `Sorting` |

### Description

> Given an array of strings `strs`, group the anagrams together. You can return the answer in any order.
>
> An **anagram** is a word or phrase formed by rearranging the letters of a different word or phrase, using all the original letters exactly once.

### Examples

```
Example 1:
Input: strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
Explanation: There is no string in strs that can be rearranged to form "bat".
The strings "nat" and "tan" are anagrams as they can be rearranged to form each other.
The strings "ate", "eat", and "tea" are anagrams as they can be rearranged to form each other.

Example 2:
Input: strs = [""]
Output: [[""]]

Example 3:
Input: strs = ["a"]
Output: [["a"]]
```

### Constraints

- `1 <= strs.length <= 10^4`
- `0 <= strs[i].length <= 100`
- `strs[i]` consists of lowercase English letters

---

## Problem Breakdown

### 1. What is being asked?

Group strings that are anagrams of each other. Two strings are anagrams if they contain the **same characters with the same frequencies**, just in different order.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `strs` | `string[]` | Array of strings |

Important observations about the input:
- Strings contain only **lowercase English letters** (a-z)
- Strings can be **empty** (`""`)
- The array contains at least 1 element
- String length can be up to 100

### 3. What is the output?

- **A list of lists** — each inner list contains all strings that are anagrams of each other
- The order of the groups does not matter
- The order of strings within a group does not matter

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `strs.length <= 10^4` | Need at least O(n * k log k) or O(n * k) where k is max string length |
| `strs[i].length <= 100` | Sorting each string is cheap: O(k log k) where k <= 100 |
| Only lowercase letters | We can use a fixed-size array of 26 for character counting |

### 5. Step-by-step example analysis

#### Example 1: `strs = ["eat","tea","tan","ate","nat","bat"]`

```text
Initial state: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]

Question: Which strings are anagrams of each other?

Analysis:
  "eat" → sorted: "aet"  → group "aet"
  "tea" → sorted: "aet"  → group "aet"
  "tan" → sorted: "ant"  → group "ant"
  "ate" → sorted: "aet"  → group "aet"
  "nat" → sorted: "ant"  → group "ant"
  "bat" → sorted: "abt"  → group "abt"

Groups:
  "aet": ["eat", "tea", "ate"]
  "ant": ["tan", "nat"]
  "abt": ["bat"]

Result: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
```

### 6. Key Observations

1. **Anagram signature** — Two strings are anagrams if and only if they produce the **same key** when their characters are sorted or counted.
2. **Hash Map grouping** — Use a Hash Map where the key is the anagram signature and the value is the list of strings with that signature.
3. **Two ways to compute the key:**
   - **Sort the string** — `"eat"` → `"aet"`, `"tea"` → `"aet"` (same key!)
   - **Count characters** — `"eat"` → `[1,0,0,0,1,0,...,1,0,0,0]` (counts of a-z)
4. **Only lowercase letters** — Fixed alphabet of 26, enabling the character count approach.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Hash Map + Sorting | Sorted string as key for grouping | This problem (Approach 1) |
| Hash Map + Counting | Character frequency as key for grouping | This problem (Approach 2) |

**Chosen pattern:** `Hash Map + Canonical Key`
**Reason:** We need to group items by equivalence class — Hash Map is the natural data structure.

---

## Approach 1: Sort as Key

### Thought process

> The key insight: two strings are anagrams if and only if their **sorted versions** are identical.
> For example: `"eat"` sorted → `"aet"`, `"tea"` sorted → `"aet"` — same key!
>
> So we can use the sorted string as a **Hash Map key** and group all strings with the same sorted key together.

### Algorithm (step-by-step)

1. Create an empty Hash Map: `groups = {}`
2. For each string `s` in the input array:
   a. Sort the characters of `s` to get the key
   b. Add `s` to the list at `groups[key]`
3. Return all the values of the Hash Map

### Pseudocode

```text
function groupAnagrams(strs):
    groups = {}
    for each s in strs:
        key = sort(s)
        groups[key].append(s)
    return groups.values()
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * k log k) | n strings, each of length at most k. Sorting each string takes O(k log k). |
| **Space** | O(n * k) | Storing all strings in the Hash Map. |

### Implementation

#### Go

```go
import "sort"

// groupAnagrams — Sort as Key approach
// Time: O(n * k log k), Space: O(n * k)
func groupAnagrams(strs []string) [][]string {
    // Hash Map: sorted string → list of original strings
    groups := make(map[string][]string)

    for _, s := range strs {
        // Sort the characters to create the key
        runes := []rune(s)
        sort.Slice(runes, func(i, j int) bool {
            return runes[i] < runes[j]
        })
        key := string(runes)

        // Add to the group
        groups[key] = append(groups[key], s)
    }

    // Collect all groups
    result := make([][]string, 0, len(groups))
    for _, group := range groups {
        result = append(result, group)
    }
    return result
}
```

#### Java

```java
import java.util.*;

class Solution {
    // groupAnagrams — Sort as Key approach
    // Time: O(n * k log k), Space: O(n * k)
    public List<List<String>> groupAnagrams(String[] strs) {
        // Hash Map: sorted string → list of original strings
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            // Sort the characters to create the key
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            // Add to the group
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        // Return all groups
        return new ArrayList<>(groups.values());
    }
}
```

#### Python

```python
class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        """
        Sort as Key approach
        Time: O(n * k log k), Space: O(n * k)
        """
        # Hash Map: sorted string → list of original strings
        groups = defaultdict(list)

        for s in strs:
            # Sort the characters to create the key
            key = ''.join(sorted(s))

            # Add to the group
            groups[key].append(s)

        # Return all groups
        return list(groups.values())
```

### Dry Run

```text
Input: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]

groups = {}

Step 1: s = "eat"
        key = sort("eat") = "aet"
        groups = {"aet": ["eat"]}

Step 2: s = "tea"
        key = sort("tea") = "aet"
        groups = {"aet": ["eat", "tea"]}

Step 3: s = "tan"
        key = sort("tan") = "ant"
        groups = {"aet": ["eat", "tea"], "ant": ["tan"]}

Step 4: s = "ate"
        key = sort("ate") = "aet"
        groups = {"aet": ["eat", "tea", "ate"], "ant": ["tan"]}

Step 5: s = "nat"
        key = sort("nat") = "ant"
        groups = {"aet": ["eat", "tea", "ate"], "ant": ["tan", "nat"]}

Step 6: s = "bat"
        key = sort("bat") = "abt"
        groups = {"aet": ["eat", "tea", "ate"], "ant": ["tan", "nat"], "abt": ["bat"]}

Result: [["eat", "tea", "ate"], ["tan", "nat"], ["bat"]]
```

---

## Approach 2: Character Count as Key

### The opportunity for optimization

> Sorting each string costs O(k log k). Can we do better?
> Since we only have 26 lowercase letters, we can **count** the frequency of each character in O(k) time.
> Two anagrams will have the **same character frequency array**.
>
> We convert the count array to a **tuple** (Python) or **string** (Go/Java) to use as a Hash Map key.

### Algorithm (step-by-step)

1. Create an empty Hash Map: `groups = {}`
2. For each string `s` in the input array:
   a. Count the frequency of each character (26-element array)
   b. Convert the count array to a hashable key
   c. Add `s` to the list at `groups[key]`
3. Return all the values of the Hash Map

### Pseudocode

```text
function groupAnagrams(strs):
    groups = {}
    for each s in strs:
        count = array of 26 zeros
        for each char c in s:
            count[c - 'a']++
        key = tuple(count)  // or string representation
        groups[key].append(s)
    return groups.values()
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * k) | n strings, each of length k. Counting characters is O(k). |
| **Space** | O(n * k) | Storing all strings in the Hash Map. The key size is O(26) = O(1). |

### Implementation

#### Go

```go
import "fmt"

// groupAnagrams — Character Count as Key approach
// Time: O(n * k), Space: O(n * k)
func groupAnagrams(strs []string) [][]string {
    // Hash Map: character count string → list of original strings
    groups := make(map[[26]byte][]string)

    for _, s := range strs {
        // Count character frequencies
        var count [26]byte
        for _, c := range s {
            count[c-'a']++
        }

        // Use the count array directly as key (Go supports array keys)
        groups[count] = append(groups[count], s)
    }

    // Collect all groups
    result := make([][]string, 0, len(groups))
    for _, group := range groups {
        result = append(result, group)
    }
    return result
}
```

#### Java

```java
import java.util.*;

class Solution {
    // groupAnagrams — Character Count as Key approach
    // Time: O(n * k), Space: O(n * k)
    public List<List<String>> groupAnagrams(String[] strs) {
        // Hash Map: character count string → list of original strings
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            // Count character frequencies
            int[] count = new int[26];
            for (char c : s.toCharArray()) {
                count[c - 'a']++;
            }

            // Convert count to string key: "1#0#0#0#1#0#...#1#0#0#0"
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append(count[i]);
                sb.append('#');
            }
            String key = sb.toString();

            // Add to the group
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        // Return all groups
        return new ArrayList<>(groups.values());
    }
}
```

#### Python

```python
from collections import defaultdict

class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        """
        Character Count as Key approach
        Time: O(n * k), Space: O(n * k)
        """
        # Hash Map: character count tuple → list of original strings
        groups = defaultdict(list)

        for s in strs:
            # Count character frequencies
            count = [0] * 26
            for c in s:
                count[ord(c) - ord('a')] += 1

            # Use tuple as key (tuples are hashable in Python)
            key = tuple(count)

            # Add to the group
            groups[key].append(s)

        # Return all groups
        return list(groups.values())
```

### Dry Run

```text
Input: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]

groups = {}

Step 1: s = "eat"
        count = [1,0,0,0,1,0,...,1,0,0,0]   (a=1, e=1, t=1)
        key = (1,0,0,0,1,0,...,1,0,0,0)
        groups = {key_aet: ["eat"]}

Step 2: s = "tea"
        count = [1,0,0,0,1,0,...,1,0,0,0]   (a=1, e=1, t=1) — same!
        key = (1,0,0,0,1,0,...,1,0,0,0)
        groups = {key_aet: ["eat", "tea"]}

Step 3: s = "tan"
        count = [1,0,0,0,0,...,1,0,0,...,1,0,0,0]   (a=1, n=1, t=1)
        key = (1,0,0,0,0,...,1,0,0,...,1,0,0,0)
        groups = {key_aet: ["eat", "tea"], key_ant: ["tan"]}

Step 4: s = "ate"
        count = [1,0,0,0,1,0,...,1,0,0,0]   (a=1, e=1, t=1) — same as Step 1!
        groups = {key_aet: ["eat", "tea", "ate"], key_ant: ["tan"]}

Step 5: s = "nat"
        count = [1,0,0,0,0,...,1,0,0,...,1,0,0,0]   (a=1, n=1, t=1) — same as Step 3!
        groups = {key_aet: ["eat", "tea", "ate"], key_ant: ["tan", "nat"]}

Step 6: s = "bat"
        count = [1,1,0,0,0,...,1,0,0,0]   (a=1, b=1, t=1)
        key = (1,1,0,0,0,...,1,0,0,0)
        groups = {key_aet: ["eat", "tea", "ate"], key_ant: ["tan", "nat"], key_abt: ["bat"]}

Result: [["eat", "tea", "ate"], ["tan", "nat"], ["bat"]]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Sort as Key | O(n * k log k) | O(n * k) | Simple, intuitive, easy to implement | Sorting overhead per string |
| 2 | Character Count as Key | O(n * k) | O(n * k) | Optimal time, avoids sorting | Slightly more complex key construction |

### Which solution to choose?

- **In an interview:** Approach 1 (Sort as Key) — simple, fast to write, easy to explain
- **For optimal performance:** Approach 2 (Character Count as Key) — better time complexity
- **On Leetcode:** Both pass — Approach 2 is theoretically faster but the difference is negligible for k <= 100
- **For learning:** Both — Approach 1 teaches sorting-based grouping, Approach 2 teaches frequency counting

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty string | `strs=[""]` | `[[""]]` | Empty string is its own group |
| 2 | Single string | `strs=["a"]` | `[["a"]]` | Single element, single group |
| 3 | No anagrams | `strs=["abc","def","ghi"]` | `[["abc"],["def"],["ghi"]]` | Each string is its own group |
| 4 | All anagrams | `strs=["abc","bca","cab"]` | `[["abc","bca","cab"]]` | All strings in one group |
| 5 | Duplicate strings | `strs=["a","a"]` | `[["a","a"]]` | Identical strings are anagrams |
| 6 | Mixed lengths | `strs=["a","ab","ba"]` | `[["a"],["ab","ba"]]` | Different lengths cannot be anagrams |

---

## Common Mistakes

### Mistake 1: Using the unsorted string as key

```python
# ❌ WRONG — original string is not a valid key for grouping
groups = defaultdict(list)
for s in strs:
    groups[s].append(s)  # "eat" and "tea" get different keys!

# ✅ CORRECT — sort the string first
for s in strs:
    key = ''.join(sorted(s))
    groups[key].append(s)
```

**Reason:** `"eat"` and `"tea"` must map to the same key, but their original forms are different.

### Mistake 2: Using a list as Hash Map key (Python)

```python
# ❌ WRONG — lists are not hashable in Python
count = [0] * 26
groups[count].append(s)  # TypeError: unhashable type: 'list'

# ✅ CORRECT — convert to tuple
groups[tuple(count)].append(s)
```

**Reason:** In Python, only immutable types (like tuples) can be used as dictionary keys.

### Mistake 3: Forgetting the separator in Java key

```java
// ❌ WRONG — counts like [1,2,3] and [12,3] produce the same key "123"
StringBuilder sb = new StringBuilder();
for (int c : count) {
    sb.append(c);
}

// ✅ CORRECT — use a separator
for (int i = 0; i < 26; i++) {
    sb.append(count[i]);
    sb.append('#');  // separator prevents ambiguity
}
```

**Reason:** Without a separator, different count arrays can produce the same string key.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [242. Valid Anagram](https://leetcode.com/problems/valid-anagram/) | :green_circle: Easy | Check if two strings are anagrams |
| 2 | [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | :yellow_circle: Medium | Sliding window + anagram check |
| 3 | [249. Group Shifted Strings](https://leetcode.com/problems/group-shifted-strings/) | :yellow_circle: Medium | Grouping by canonical form |
| 4 | [336. Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/) | :red_circle: Hard | String manipulation + Hash Map |
| 5 | [2273. Find Resultant Array After Removing Anagrams](https://leetcode.com/problems/find-resultant-array-after-removing-anagrams/) | :green_circle: Easy | Remove consecutive anagrams |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Strings are visualized as cards being **sorted** or **counted** to generate keys
> - Cards with the same key slide into the same **bucket/group**
> - Step-by-step execution with Play/Pause/Reset controls
> - Speed slider for controlling animation speed
> - Multiple presets to test different inputs
> - Log panel shows each operation as it happens
