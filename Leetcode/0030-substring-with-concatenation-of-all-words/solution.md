# 0030. Substring with Concatenation of All Words

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Sliding Window with Hash Map](#approach-2-sliding-window-with-hash-map)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [30. Substring with Concatenation of All Words](https://leetcode.com/problems/substring-with-concatenation-of-all-words/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Hash Table`, `String`, `Sliding Window` |

### Description

> You are given a string `s` and an array of strings `words`. All the strings of `words` are of the **same length**.
>
> A **concatenated string** is a string that exactly contains all the strings of any permutation of `words` concatenated.
>
> Return an array of the starting indices of all the concatenated substrings in `s`. You can return the answer in **any order**.

### Examples

```
Example 1:
Input: s = "barfoothefoobarman", words = ["foo","bar"]
Output: [0,9]
Explanation:
  s[0..5] = "barfoo" is a concatenation of ["bar","foo"] (a permutation of words)
  s[9..14] = "foobar" is a concatenation of ["foo","bar"] (a permutation of words)

Example 2:
Input: s = "wordgoodgoodgoodbestword", words = ["word","good","best","word"]
Output: []
Explanation:
  No substring of s is a concatenation of all words.

Example 3:
Input: s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
Output: [6,9,12]
Explanation:
  s[6..14]  = "foobarthe" is a concatenation of ["foo","bar","the"]
  s[9..17]  = "barthefoo" is a concatenation of ["bar","the","foo"]
  s[12..20] = "thefoobar" is a concatenation of ["the","foo","bar"]
```

### Constraints

- `1 <= s.length <= 10^4`
- `1 <= words.length <= 5000`
- `1 <= words[i].length <= 30`
- `s` and `words[i]` consist of lowercase English letters

---

## Problem Breakdown

### 1. What is being asked?

Find **all starting indices** in `s` where a substring equals the concatenation of **all** words (in any order). Each word in `words` must be used **exactly as many times** as it appears (words can have duplicates).

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | The string to search in |
| `words` | `string[]` | Array of equal-length words to concatenate |

Important observations about the input:
- All words have the **same length** — this is crucial for the algorithm
- `words` **may contain duplicates** (Example 2: "word" appears twice)
- The total concatenation length is `len(words) * len(words[0])`

### 3. What is the output?

- A list of **starting indices** (integers)
- Each index marks a position in `s` where a valid concatenation begins
- The answer can be in **any order**
- Return an empty list if no valid concatenation exists

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 10^4` | O(n * m * wordLen) is feasible |
| `words.length <= 5000` | Total window can be up to 150,000 — but capped by s.length |
| `words[i].length <= 30` | Fixed word size enables chunked sliding window |
| All words same length | Enables splitting into fixed-size chunks |

### 5. Step-by-step example analysis

#### Example 1: `s = "barfoothefoobarman"`, `words = ["foo", "bar"]`

```text
wordLen = 3, numWords = 2, totalLen = 6
Word frequency: {"foo": 1, "bar": 1}

Check position 0: "barfoo" → chunks ["bar", "foo"] → freq {"bar":1, "foo":1} ✅ match!
Check position 1: "arfoot" → chunks ["arf", "oot"] → "arf" not in words ❌
Check position 2: "rfooth" → chunks ["rfo", "oth"] → "rfo" not in words ❌
...
Check position 9: "foobar" → chunks ["foo", "bar"] → freq {"foo":1, "bar":1} ✅ match!
...

Result: [0, 9]
```

### 6. Key Observations

1. **All words are the same length** — we can split any substring into fixed-size chunks.
2. **Frequency matching** — Instead of checking all permutations (n!), compare word frequency maps.
3. **Sliding window** — We only need `wordLen` starting offsets (0, 1, ..., wordLen-1) and slide by `wordLen` at a time.
4. **Total window size** is fixed: `numWords * wordLen`.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Sliding Window + Hash Map | Fixed window, frequency counting | This problem |
| Brute Force + Hash Map | Check every position | Works but slower |

**Chosen pattern:** `Sliding Window with Hash Map`
**Reason:** By sliding one word at a time (not one character), we reuse previous computation and avoid redundant frequency recalculation.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: for each starting position `i` in `s`, extract a substring of length `numWords * wordLen`, split it into chunks of `wordLen`, count frequencies, and compare with the target frequency map.

### Algorithm (step-by-step)

1. Build frequency map of `words`
2. For each starting index `i` from `0` to `len(s) - totalLen`:
   - Extract substring `s[i:i+totalLen]`
   - Split into chunks of size `wordLen`
   - Build frequency map of chunks
   - If frequencies match → add `i` to result

### Pseudocode

```text
function findSubstring(s, words):
    wordLen = len(words[0])
    numWords = len(words)
    totalLen = wordLen * numWords
    wordFreq = frequency_map(words)
    result = []

    for i = 0 to len(s) - totalLen:
        seen = {}
        valid = true
        for j = 0 to numWords - 1:
            word = s[i + j*wordLen : i + (j+1)*wordLen]
            if word not in wordFreq:
                valid = false
                break
            seen[word] += 1
            if seen[word] > wordFreq[word]:
                valid = false
                break
        if valid:
            result.append(i)
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * m * wordLen) | n positions, m words per position, wordLen for substring extraction |
| **Space** | O(m) | Frequency maps store at most m entries |

Where n = len(s), m = len(words), wordLen = len(words[0])

### Implementation

#### Go

```go
func findSubstringBrute(s string, words []string) []int {
    if len(s) == 0 || len(words) == 0 { return []int{} }
    wordLen := len(words[0])
    numWords := len(words)
    totalLen := wordLen * numWords
    result := []int{}

    wordFreq := map[string]int{}
    for _, w := range words { wordFreq[w]++ }

    for i := 0; i <= len(s)-totalLen; i++ {
        seen := map[string]int{}
        valid := true
        for j := 0; j < numWords; j++ {
            word := s[i+j*wordLen : i+(j+1)*wordLen]
            if wordFreq[word] == 0 { valid = false; break }
            seen[word]++
            if seen[word] > wordFreq[word] { valid = false; break }
        }
        if valid { result = append(result, i) }
    }
    return result
}
```

#### Java

```java
public List<Integer> findSubstringBrute(String s, String[] words) {
    List<Integer> result = new ArrayList<>();
    if (s.isEmpty() || words.length == 0) return result;
    int wordLen = words[0].length();
    int numWords = words.length;
    int totalLen = wordLen * numWords;

    Map<String, Integer> wordFreq = new HashMap<>();
    for (String w : words) wordFreq.merge(w, 1, Integer::sum);

    for (int i = 0; i <= s.length() - totalLen; i++) {
        Map<String, Integer> seen = new HashMap<>();
        boolean valid = true;
        for (int j = 0; j < numWords; j++) {
            String word = s.substring(i + j * wordLen, i + (j + 1) * wordLen);
            if (!wordFreq.containsKey(word)) { valid = false; break; }
            seen.merge(word, 1, Integer::sum);
            if (seen.get(word) > wordFreq.get(word)) { valid = false; break; }
        }
        if (valid) result.add(i);
    }
    return result;
}
```

#### Python

```python
def findSubstringBrute(self, s: str, words: list[str]) -> list[int]:
    if not s or not words: return []
    word_len = len(words[0])
    num_words = len(words)
    total_len = word_len * num_words
    word_freq = Counter(words)
    result = []

    for i in range(len(s) - total_len + 1):
        seen = defaultdict(int)
        valid = True
        for j in range(num_words):
            word = s[i + j * word_len : i + (j + 1) * word_len]
            if word not in word_freq:
                valid = False
                break
            seen[word] += 1
            if seen[word] > word_freq[word]:
                valid = False
                break
        if valid:
            result.append(i)
    return result
```

### Dry Run

```text
Input: s = "barfoothefoobarman", words = ["foo", "bar"]
wordLen = 3, numWords = 2, totalLen = 6
wordFreq = {"foo": 1, "bar": 1}

i=0:  "barfoo" → "bar"(seen:{bar:1}) → "foo"(seen:{bar:1,foo:1}) → valid ✅ → result=[0]
i=1:  "arfoot" → "arf" not in wordFreq ❌
i=2:  "rfooth" → "rfo" not in wordFreq ❌
i=3:  "foothe" → "foo"(seen:{foo:1}) → "the" not in wordFreq ❌
i=4:  "oothef" → "oot" not in wordFreq ❌
i=5:  "othefoo" → "oth" not in wordFreq ❌
i=6:  "thefoob" → "the" not in wordFreq ❌
i=7:  "hefooba" → "hef" not in wordFreq ❌
i=8:  "efoobar" → "efo" not in wordFreq ❌
i=9:  "foobar" → "foo"(seen:{foo:1}) → "bar"(seen:{foo:1,bar:1}) → valid ✅ → result=[0,9]
i=10: "oobarm" → "oob" not in wordFreq ❌
i=11: "obarma" → "oba" not in wordFreq ❌
i=12: "barman" → "bar"(seen:{bar:1}) → "man" not in wordFreq ❌

Result: [0, 9]
```

---

## Approach 2: Sliding Window with Hash Map

### The problem with Brute Force

> For each starting position, we rebuild the frequency map from scratch. Many adjacent positions share most of the same words — we waste time recounting them.

### Optimization idea

> 1. Since all words have length `wordLen`, we only need to consider `wordLen` different starting offsets: `0, 1, 2, ..., wordLen-1`.
> 2. For each offset, we slide a window of `numWords` words, advancing one word at a time.
> 3. When we slide right: add the new word, remove the leftmost word — O(1) per step.
> 4. This avoids recounting the entire window at every position.

### Algorithm (step-by-step)

1. Build `wordFreq` — frequency map of `words`
2. For each offset `i` from `0` to `wordLen - 1`:
   - Initialize `left = i`, `count = 0`, `seen = {}`
   - For `right` from `i` to `len(s) - wordLen`, stepping by `wordLen`:
     - Extract `word = s[right:right+wordLen]`
     - If `word` is in `wordFreq`:
       - Add to `seen`, increment `count`
       - While `seen[word] > wordFreq[word]`:
         - Remove leftmost word from `seen`, decrement `count`, advance `left`
       - If `count == numWords` → add `left` to result
     - Else (invalid word):
       - Reset: `seen = {}`, `count = 0`, `left = right + wordLen`

### Pseudocode

```text
function findSubstring(s, words):
    wordLen = len(words[0])
    numWords = len(words)
    totalLen = wordLen * numWords
    wordFreq = frequency_map(words)
    result = []

    for i = 0 to wordLen - 1:
        left = i
        count = 0
        seen = {}
        for right = i to len(s) - wordLen, step wordLen:
            word = s[right : right + wordLen]
            if word in wordFreq:
                seen[word] += 1
                count += 1
                while seen[word] > wordFreq[word]:
                    leftWord = s[left : left + wordLen]
                    seen[leftWord] -= 1
                    count -= 1
                    left += wordLen
                if count == numWords:
                    result.append(left)
            else:
                seen.clear()
                count = 0
                left = right + wordLen
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * wordLen) | wordLen offsets, each processes n/wordLen positions, substring extraction is O(wordLen) |
| **Space** | O(m) | Two hash maps with at most m distinct words |

Where n = len(s), m = len(words)

### Implementation

#### Go

```go
func findSubstring(s string, words []string) []int {
    if len(s) == 0 || len(words) == 0 { return []int{} }
    wordLen := len(words[0])
    numWords := len(words)
    totalLen := wordLen * numWords
    if len(s) < totalLen { return []int{} }

    wordFreq := map[string]int{}
    for _, w := range words { wordFreq[w]++ }
    result := []int{}

    for i := 0; i < wordLen; i++ {
        left := i
        count := 0
        seen := map[string]int{}
        for right := i; right+wordLen <= len(s); right += wordLen {
            word := s[right : right+wordLen]
            if _, ok := wordFreq[word]; ok {
                seen[word]++
                count++
                for seen[word] > wordFreq[word] {
                    leftWord := s[left : left+wordLen]
                    seen[leftWord]--
                    count--
                    left += wordLen
                }
                if count == numWords {
                    result = append(result, left)
                }
            } else {
                seen = map[string]int{}
                count = 0
                left = right + wordLen
            }
        }
    }
    return result
}
```

#### Java

```java
public List<Integer> findSubstring(String s, String[] words) {
    List<Integer> result = new ArrayList<>();
    if (s.isEmpty() || words.length == 0) return result;
    int wordLen = words[0].length();
    int numWords = words.length;
    int totalLen = wordLen * numWords;
    if (s.length() < totalLen) return result;

    Map<String, Integer> wordFreq = new HashMap<>();
    for (String w : words) wordFreq.merge(w, 1, Integer::sum);

    for (int i = 0; i < wordLen; i++) {
        int left = i, count = 0;
        Map<String, Integer> seen = new HashMap<>();
        for (int right = i; right + wordLen <= s.length(); right += wordLen) {
            String word = s.substring(right, right + wordLen);
            if (wordFreq.containsKey(word)) {
                seen.merge(word, 1, Integer::sum);
                count++;
                while (seen.get(word) > wordFreq.get(word)) {
                    String leftWord = s.substring(left, left + wordLen);
                    seen.merge(leftWord, -1, Integer::sum);
                    count--;
                    left += wordLen;
                }
                if (count == numWords) {
                    result.add(left);
                }
            } else {
                seen.clear();
                count = 0;
                left = right + wordLen;
            }
        }
    }
    return result;
}
```

#### Python

```python
def findSubstring(self, s: str, words: list[str]) -> list[int]:
    if not s or not words: return []
    word_len = len(words[0])
    num_words = len(words)
    total_len = word_len * num_words
    if len(s) < total_len: return []

    word_freq = Counter(words)
    result = []

    for i in range(word_len):
        left = i
        count = 0
        seen = defaultdict(int)
        for right in range(i, len(s) - word_len + 1, word_len):
            word = s[right:right + word_len]
            if word in word_freq:
                seen[word] += 1
                count += 1
                while seen[word] > word_freq[word]:
                    left_word = s[left:left + word_len]
                    seen[left_word] -= 1
                    count -= 1
                    left += word_len
                if count == num_words:
                    result.append(left)
            else:
                seen.clear()
                count = 0
                left = right + word_len
    return result
```

### Dry Run

```text
Input: s = "barfoothefoobarman", words = ["bar", "foo", "the"]
wordLen = 3, numWords = 3, totalLen = 9
wordFreq = {"bar": 1, "foo": 1, "the": 1}

=== Offset i=0 ===
Chunks at positions: 0:"bar" 3:"foo" 6:"the" 9:"foo" 12:"bar" 15:"man"

right=0, word="bar": seen={bar:1}, count=1
right=3, word="foo": seen={bar:1,foo:1}, count=2
right=6, word="the": seen={bar:1,foo:1,the:1}, count=3 → count==numWords ✅ left=0
  → result=[0]... wait, but "barfoothe" is NOT correct (needs "bar","foo","the" permutation)
  Actually "barfoothe" = "bar"+"foo"+"the" which IS a valid permutation ✅
  Hmm but expected output is [6,9,12]. Let me re-check.

Actually for Example 3: s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
wordLen = 3, numWords = 3, totalLen = 9
wordFreq = {"bar": 1, "foo": 1, "the": 1}

=== Offset i=0 ===
Chunks: 0:"bar" 3:"foo" 6:"foo" 9:"bar" 12:"the" 15:"foo" 18:"bar" 21:"man"

right=0,  word="bar": seen={bar:1}, count=1
right=3,  word="foo": seen={bar:1,foo:1}, count=2
right=6,  word="foo": seen={bar:1,foo:2}, count=3
  foo(2) > wordFreq["foo"](1) → shrink:
    remove s[0:3]="bar": seen={foo:2}, count=2
  foo(2) > 1 → shrink:
    remove s[3:6]="foo": seen={foo:1}, count=1
  left=6
right=9,  word="bar": seen={foo:1,bar:1}, count=2, left=6
right=12, word="the": seen={foo:1,bar:1,the:1}, count=3 → count==3 ✅ left=6
  → result=[6]
  (window is s[6..14] = "foobarthefoo"... no, s[6..14] = "foobarthe" ✅)
right=15, word="foo": seen={foo:2,bar:1,the:1}, count=4
  foo(2) > 1 → shrink:
    remove s[6:9]="foo": seen={foo:1,bar:1,the:1}, count=3
  foo(1) <= 1. count==3 ✅ left=9 → result=[6,9]
right=18, word="bar": seen={foo:1,bar:2,the:1}, count=4
  bar(2) > 1 → shrink:
    remove s[9:12]="bar": seen={foo:1,bar:1,the:1}, count=3
  bar(1) <= 1. count==3 ✅ left=12 → result=[6,9,12]
right=21, word="man": not in wordFreq → reset. seen={}, count=0, left=24

=== Offset i=1 ===
Chunks: 1:"arf" 4:"oof" ... none in wordFreq → no matches

=== Offset i=2 ===
Chunks: 2:"rfo" 5:"ofo" ... none in wordFreq → no matches

Result: [6, 9, 12] ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n * m * wordLen) | O(m) | Simple logic | Redundant frequency rebuilds |
| 2 | Sliding Window + Hash Map | O(n * wordLen) | O(m) | Optimal, reuses computation | More complex implementation |

### Which solution to choose?

- **In an interview:** Approach 2 (Sliding Window) — shows mastery of the sliding window pattern
- **In production:** Approach 2 — best time complexity
- **On Leetcode:** Approach 2 — passes all test cases efficiently
- **For learning:** Both — Brute Force clarifies the problem, Sliding Window shows the optimization

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Basic match | `s="barfoo", words=["foo","bar"]` | `[0]` | Single concatenation at start |
| 2 | No match | `s="wordgoodgoodgoodbestword", words=["word","good","best","word"]` | `[]` | No valid substring |
| 3 | Multiple matches | `s="barfoofoobarthefoobarman", words=["bar","foo","the"]` | `[6,9,12]` | Overlapping valid windows |
| 4 | Single character words | `s="aaa", words=["a","a"]` | `[0,1]` | Edge with length 1 |
| 5 | Duplicate words | `s="aaa", words=["a","a","a"]` | `[0]` | All words identical |
| 6 | No overlap possible | `s="ab", words=["abc"]` | `[]` | s shorter than word |
| 7 | Entire string matches | `s="foobar", words=["foo","bar"]` | `[0]` | Exact length match |

---

## Common Mistakes

### Mistake 1: Checking all n! permutations

```python
# ❌ WRONG — exponential time, generates all permutations
from itertools import permutations
for perm in permutations(words):
    target = "".join(perm)
    if target in s: ...

# ✅ CORRECT — use frequency maps for O(1) comparison
word_freq = Counter(words)
# Compare chunk frequencies against word_freq
```

**Reason:** For 5000 words, n! is astronomically large. Frequency maps reduce permutation checking to O(m).

### Mistake 2: Only checking offset 0

```python
# ❌ WRONG — misses valid substrings starting at non-zero offsets
for right in range(0, len(s) - word_len + 1, word_len):
    ...

# ✅ CORRECT — check all wordLen offsets
for i in range(word_len):
    for right in range(i, len(s) - word_len + 1, word_len):
        ...
```

**Reason:** A valid concatenation can start at any position, not just multiples of `wordLen`.

### Mistake 3: Not handling duplicate words in `words`

```python
# ❌ WRONG — uses a set, loses duplicate count
word_set = set(words)
# words=["word","word"] → word_set={"word"} → only checks for 1 occurrence

# ✅ CORRECT — use Counter/frequency map
word_freq = Counter(words)
# words=["word","word"] → word_freq={"word": 2} → checks for 2 occurrences
```

**Reason:** `words` can contain duplicates (Example 2), so we must count exact frequencies.

### Mistake 4: Not resetting window on invalid word

```python
# ❌ WRONG — continues window with invalid word inside
if word not in word_freq:
    count = 0  # Reset count but not left pointer or seen map!

# ✅ CORRECT — full reset
if word not in word_freq:
    seen.clear()
    count = 0
    left = right + word_len  # Move left past this invalid word
```

**Reason:** An invalid word breaks the concatenation chain; the window must restart after it.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | :red_circle: Hard | Sliding window + frequency map |
| 2 | [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | :yellow_circle: Medium | Sliding window pattern |
| 3 | [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | :yellow_circle: Medium | Fixed-size window + frequency matching |
| 4 | [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/) | :yellow_circle: Medium | Check if permutation exists as substring |
| 5 | [49. Group Anagrams](https://leetcode.com/problems/group-anagrams/) | :yellow_circle: Medium | Hash map + string grouping |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **String display** with sliding window highlighting
> - **Word frequency hash maps** (target vs. current window)
> - **Step-by-step matching** visualization
> - Visualizes window expansion, shrinking, and reset on invalid words
