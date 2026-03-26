# Leetcode Template — Universal Guide

> Har bir Leetcode masala uchun universal template.
> Barcha yechimlar **3 tilda: Go, Java, Python** da beriladi.
> Har bir masala **interaktiv HTML/CSS/JS animatsiya** bilan tushuntiriladi.

## Qoida: 1 Masala = 1 Folder

> **MUHIM:** Har bir Leetcode masala o'zining alohida papkasida bo'lishi **SHART**.
> Hech qachon bitta papkaga 2 ta masala qo'ymang.
> Folder nomi: `XXXX-problem-name` formatda (4 raqamli, kebab-case).

## Overview

| | Description |
|---|---|
| **Purpose** | Leetcode masalalarini tizimli yechish uchun universal template |
| **Rule** | **1 masala = 1 folder** — har bir masala o'z papkasida |
| **Files per problem** | 5 fayl: `solution.md`, `solution.go`, `solution.java`, `solution.py`, `animation.html` |
| **Languages** | Barcha kod **Go**, **Java**, **Python** (shu tartibda) |
| **Visualization** | Har bir masala `animation.html` — standalone interaktiv animatsiya |
| **Code Fences** | `go`, `java`, `python` implementatsiyalar uchun, `text` pseudocode uchun |

### Problem Folder Structure

> **1 masala = 1 folder.** Barcha fayllar bitta papka ichida.

```
Leetcode/
├── TEMPLATE.md                    ← Shu template fayl
│
├── 0001-two-sum/                  ← 1-masala = 1 folder
│   ├── solution.md                ← Masala tahlili + barcha yechimlar
│   ├── solution.go                ← Optimal yechim (Go)
│   ├── solution.java              ← Optimal yechim (Java)
│   ├── solution.py                ← Optimal yechim (Python)
│   └── animation.html             ← Interaktiv animatsiya
│
├── 0015-3sum/                     ← 2-masala = alohida folder
│   ├── solution.md
│   ├── solution.go
│   ├── solution.java
│   ├── solution.py
│   └── animation.html
│
├── 0042-trapping-rain-water/      ← 3-masala = alohida folder
│   ├── solution.md
│   ├── solution.go
│   ├── solution.java
│   ├── solution.py
│   └── animation.html
│
└── ...                            ← Har bir masala shu formatda
```

### Folder nomlash qoidalari

| Qoida | Misol | Izoh |
|---|---|---|
| 4 raqamli prefix | `0001-`, `0042-`, `0121-` | Tartibda turishi uchun |
| kebab-case | `two-sum`, `valid-parentheses` | Bo'sh joy yo'q, kichik harf |
| Leetcode slug bilan bir xil | `trapping-rain-water` | URL dan olish mumkin |

```
✅ TO'G'RI:
  0001-two-sum/
  0042-trapping-rain-water/
  0121-best-time-to-buy-and-sell-stock/

❌ XATO:
  two-sum/                  ← raqam yo'q
  1-two-sum/                ← 4 raqamli emas
  0001_two_sum/             ← underscore emas, kebab-case kerak
  0001-TwoSum/              ← camelCase emas, kebab-case kerak
  problems/0001-two-sum/    ← ichma-ich papka kerak emas
```

## Multi-Language Code Block Convention

> **MUHIM:** Har bir yechim ALBATTA 3 tilda, shu tartibda berilishi kerak:

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
| **Difficulty** | 🟢 Easy / 🟡 Medium / 🔴 Hard |
| **Tags** | `Array`, `Hash Table`, `Two Pointers`, `...` |

### Description (English)

> {{Copy the problem description from Leetcode here}}

### Tavsif (O'zbekcha)

> {{Masalaning o'zbek tilidagi tarjimasi — oddiy, tushunarli tilda}}

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

> **Maqsad:** Masalani mayda bo'laklarga ajratib, qadam-baqadam tushuntirish.
> Har bir qadamni alohida, oddiy tilda tushuntirib bering.

### 1. Nimani so'rayapti?

{{Masalaning mohiyatini 2-3 gapda oddiy tilda tushuntiring.
Texnik terminlarsiz, go'yo do'stingizga tushuntirayotgandek yozing.}}

### 2. Input nima?

| Parameter | Type | Description |
|---|---|---|
| `{{param1}}` | `{{type}}` | {{tavsif}} |
| `{{param2}}` | `{{type}}` | {{tavsif}} |

{{Input haqida muhim kuzatuvlar:}}
- {{Tartiblangan mi? Tartiblanmagan mi?}}
- {{Dublikatlar bor mi?}}
- {{Bo'sh bo'lishi mumkin mi?}}

### 3. Output nima?

{{Kutilayotgan natijani aniq tushuntiring:}}
- {{Qanday turdagi natija? (son, massiv, boolean, string)}}
- {{Natija tartibi muhim mi?}}
- {{Bir nechta to'g'ri javob bo'lishi mumkin mi?}}

### 4. Cheklovlar (Constraints) tahlili

| Constraint | Ahamiyati |
|---|---|
| `{{constraint}}` | {{Bu nimani anglatadi — qanday algoritmlar ishlaydi/ishlamaydi}} |
| `n <= 10^4` | O(n²) ishlaydi, lekin O(n³) TLE beradi |
| `n <= 10^5` | O(n log n) yoki O(n) kerak |
| `n <= 10^6` | Faqat O(n) ishlaydi |

### 5. Misollarni qadam-baqadam tahlil qilish

#### Example 1: `{{input}}`

```text
Boshlang'ich holat: {{initial state}}

Qadam 1: {{qadam tavsifi}}
         {{hozirgi holat}}

Qadam 2: {{qadam tavsifi}}
         {{hozirgi holat}}

Qadam 3: {{qadam tavsifi}}
         {{hozirgi holat}}

...

Natija: {{output}}
```

#### Example 2: `{{input}}`

```text
{{Xuddi shunday qadam-baqadam tahlil}}
```

### 6. Muhim kuzatuvlar (Key Observations)

> {{Yechimga olib boradigan eng muhim fikrlarni yozing.
> Har bir kuzatuvni alohida punkt qilib, nima uchun muhimligini tushuntiring.}}

1. **{{Kuzatuv 1}}** — {{nima uchun muhim}}
2. **{{Kuzatuv 2}}** — {{nima uchun muhim}}
3. **{{Kuzatuv 3}}** — {{nima uchun muhim}}

### 7. Pattern aniqlash

| Pattern | Nima uchun mos | Misol |
|---|---|---|
| {{Two Pointers}} | {{sorted array, opposite ends}} | {{Two Sum II}} |
| {{Sliding Window}} | {{subarray, consecutive elements}} | {{Max Subarray}} |
| {{Hash Map}} | {{O(1) lookup kerak}} | {{Two Sum}} |

**Tanlangan pattern:** `{{Pattern Name}}`
**Sabab:** {{Nima uchun bu pattern eng mos}}

---

## Approach 1: Brute Force

### Fikrlash jarayoni

> {{Eng oddiy — birinchi xayolga kelgan yechimni tushuntiring.
> "Agar hech qanday optimallashtirish o'ylamasak, eng sodda usul nima?"}}

### Algoritm (qadam-baqadam)

1. {{Qadam 1}}
2. {{Qadam 2}}
3. {{Qadam 3}}
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

| | Complexity | Izoh |
|---|---|---|
| **Time** | O({{...}}) | {{Nima uchun? Nested loop? Har bir element uchun qayta ishlash?}} |
| **Space** | O({{...}}) | {{Qo'shimcha xotira ishlatilmoqdami?}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — Brute Force approach
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    // Qadam 1: {{tavsif}}
    {{code}}

    // Qadam 2: {{tavsif}}
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
        // Qadam 1: {{tavsif}}
        {{code}}

        // Qadam 2: {{tavsif}}
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
        # Qadam 1: {{tavsif}}
        {{code}}

        # Qadam 2: {{tavsif}}
        {{code}}

        return {{result}}
```

### Dry Run

```text
Input: {{example input}}

Qadam 1: i=0
  ├── j=1: {{holat}} → {{natija}}
  ├── j=2: {{holat}} → {{natija}}
  └── j=3: {{holat}} → {{natija}}

Qadam 2: i=1
  ├── j=2: {{holat}} → {{natija}} ✅ TOPILDI!
  └── return {{natija}}

Jami operatsiyalar: {{soni}}
```

---

## Approach 2: Time Complexity Optimization

### Brute Force ning muammosi

> {{Brute Force ning qayeri sekin? Nima uchun?
> Masalan: "Har bir element uchun qolgan barcha elementlarni tekshiryapmiz — bu O(n²).
> Agar bitta o'tishda topsa bo'lmaydimi?"}}

### Optimallashtirish g'oyasi

> {{Qanday qilib tezlashtiramiz?
> Masalan: "Hash Map ishlatib, oldin ko'rgan elementlarni eslab qolamiz.
> Shunda har bir element uchun O(1) da tekshiramiz, jami O(n)."}}

### Algoritm (qadam-baqadam)

1. {{Qadam 1}}
2. {{Qadam 2}}
3. {{Qadam 3}}

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

| | Complexity | Izoh |
|---|---|---|
| **Time** | O({{...}}) | {{Nima o'zgardi? Nima uchun tezroq?}} |
| **Space** | O({{...}}) | {{Qo'shimcha xotira — trade-off}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — Time Optimized approach
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    // Optimallashtirish: {{tavsif}}
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
        // Optimallashtirish: {{tavsif}}
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
        # Optimallashtirish: {{tavsif}}
        {{code}}

        return {{result}}
```

### Dry Run

```text
Input: {{example input}}

seen = {}

Qadam 1: element={{val}}, complement={{val}}
         seen da bor mi? ❌ Yo'q
         seen = {{{val}}: 0}

Qadam 2: element={{val}}, complement={{val}}
         seen da bor mi? ✅ Ha! Index: {{idx}}
         return [{{idx}}, 1]

Jami operatsiyalar: {{soni}} (Brute Force: {{soni}} → {{x}} marta tez!)
```

---

## Approach 3: Space Complexity Optimization

### Oldingi yechimning muammosi

> {{Xotirani qayerda ko'p ishlatyapmiz?
> Masalan: "Hash Map O(n) qo'shimcha xotira oladi.
> Agar input sorted bo'lsa, Two Pointers bilan O(1) xotira ishlatamiz."}}

### Optimallashtirish g'oyasi

> {{Qanday qilib xotirani kamaytiramiz?
> Masalan: "Input ni sort qilib, Two Pointers ishlatamiz.
> Yoki in-place algoritmdan foydalanamiz."}}

### Algoritm (qadam-baqadam)

1. {{Qadam 1}}
2. {{Qadam 2}}
3. {{Qadam 3}}

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

| | Complexity | Izoh |
|---|---|---|
| **Time** | O({{...}}) | {{Sort + scan yoki faqat scan?}} |
| **Space** | O({{...}}) | {{In-place? Qo'shimcha xotira yo'q?}} |

### Implementation

#### Go

```go
package main

// {{FunctionName}} — Space Optimized approach
// Time: O({{...}}), Space: O({{...}})
func {{functionName}}({{params}}) {{returnType}} {
    // Xotira optimallashtirish: {{tavsif}}
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
        // Xotira optimallashtirish: {{tavsif}}
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
        # Xotira optimallashtirish: {{tavsif}}
        {{code}}

        return {{result}}
```

### Dry Run

```text
Input: {{example input}} (sorted: {{sorted input}})

left=0 (val={{val}}), right={{n-1}} (val={{val}})

Qadam 1: sum = {{val}} + {{val}} = {{sum}}
         {{sum}} > target → right-- → right={{n-2}}

Qadam 2: sum = {{val}} + {{val}} = {{sum}}
         {{sum}} == target ✅ → return [{{left}}, {{right}}]

Jami operatsiyalar: {{soni}}
Xotira: O(1) (oldingi: O(n) → {{x}} marta kam!)
```

---

## Approach 4: Alternative Solution

> {{Agar boshqa yondashuvlar mavjud bo'lsa, shu yerda yozing.
> Masalan: Bit Manipulation, Math formula, Monotonic Stack, Binary Search, va h.k.
> Har bir alternative approach uchun yangi "Approach N" bo'limi oching.}}

### G'oya

> {{Bu yondashuvning mohiyati}}

### Complexity

| | Complexity | Izoh |
|---|---|---|
| **Time** | O({{...}}) | {{tavsif}} |
| **Space** | O({{...}}) | {{tavsif}} |

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
| 1 | Brute Force | O({{...}}) | O({{...}}) | {{Sodda, tushunish oson}} | {{Sekin}} |
| 2 | TC Optimized | O({{...}}) | O({{...}}) | {{Tez}} | {{Ko'p xotira}} |
| 3 | SC Optimized | O({{...}}) | O({{...}}) | {{Kam xotira}} | {{Sort kerak / cheklov bor}} |
| 4 | Alternative | O({{...}}) | O({{...}}) | {{...}} | {{...}} |

### Qaysi yechimni tanlash kerak?

- **Interview da:** {{TC Optimized — tezkor va tushuntirish oson}}
- **Production da:** {{Vaziyatga qarab — katta data = SC Optimized, tez javob = TC Optimized}}
- **Leetcode da:** {{TC Optimized — tezlik muhimroq}}

---

## Edge Cases

| # | Case | Input | Expected Output | Sabab |
|---|---|---|---|---|
| 1 | Bo'sh input | `{{}}` | `{{}}` | {{Chegaraviy holat}} |
| 2 | Bitta element | `{{[x]}}` | `{{}}` | {{Minimal input}} |
| 3 | Barcha bir xil | `{{[x,x,x]}}` | `{{}}` | {{Dublikatlar}} |
| 4 | Salbiy sonlar | `{{[-1,-2]}}` | `{{}}` | {{Manfiy qiymatlar}} |
| 5 | Juda katta input | `n = 10^5` | — | {{TLE tekshirish}} |
| 6 | Minimal/Maximal qiymatlar | `{{INT_MIN, INT_MAX}}` | `{{}}` | {{Overflow xavfi}} |

---

## Common Mistakes

### Xato 1: {{Xato nomi}}

```python
# ❌ XATO
{{xato kod}}

# ✅ TO'G'RI
{{to'g'ri kod}}
```

**Sabab:** {{Nima uchun bu xato va qanday tuzatiladi}}

### Xato 2: {{Xato nomi}}

```python
# ❌ XATO
{{xato kod}}

# ✅ TO'G'RI
{{to'g'ri kod}}
```

**Sabab:** {{Nima uchun bu xato va qanday tuzatiladi}}

---

## Related Problems

| # | Problem | Difficulty | O'xshashligi |
|---|---|---|---|
| 1 | [{{XXXX}}. {{Name}}](https://leetcode.com/problems/{{slug}}/) | {{Easy/Medium/Hard}} | {{Qanday bog'liq}} |
| 2 | [{{XXXX}}. {{Name}}](https://leetcode.com/problems/{{slug}}/) | {{Easy/Medium/Hard}} | {{Qanday bog'liq}} |
| 3 | [{{XXXX}}. {{Name}}](https://leetcode.com/problems/{{slug}}/) | {{Easy/Medium/Hard}} | {{Qanday bog'liq}} |

---

## Visual Animation

> 📺 Interaktiv animatsiya: [animation.html](./animation.html)
>
> Animatsiyada:
> - Har bir yechim uchun alohida tab
> - Step-by-step vizualizatsiya
> - Tezlik boshqaruvi
> - Custom input kiritish imkoniyati
> - **Random Generate** — size va range tanlash, har doim yechimi bor input yaratish
> - **localStorage** — refresh bo'lganda state saqlanib qoladi

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
	// Qadam 1: {{tavsif}}
	{{code}}

	// Qadam 2: {{tavsif}}
	{{code}}

	// Qadam 3: {{tavsif}}
	{{code}}

	return {{result}}
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test yordamchi funksiya
	passed, failed := 0, 0
	test := func(name string, got, expected interface{}) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Basic case
	test("Basic case", {{functionName}}({{input1}}), {{expected1}})

	// Test 2: Edge case
	test("Edge case", {{functionName}}({{input2}}), {{expected2}})

	// Test 3: Large input
	test("Large input", {{functionName}}({{input3}}), {{expected3}})

	// Test 4: {{tavsif}}
	test("{{tavsif}}", {{functionName}}({{input4}}), {{expected4}})

	// Test 5: {{tavsif}}
	test("{{tavsif}}", {{functionName}}({{input5}}), {{expected5}})

	// Natija
	fmt.Printf("\n📊 Natija: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
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
        // Qadam 1: {{tavsif}}
        {{code}}

        // Qadam 2: {{tavsif}}
        {{code}}

        // Qadam 3: {{tavsif}}
        {{code}}

        return {{result}};
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    // Test yordamchi metod — massivlar uchun
    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    // Test yordamchi metod — umumiy ob'ektlar uchun
    static void test(String name, Object got, Object expected) {
        if (Objects.equals(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
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

        // Test 4: {{tavsif}}
        test("{{tavsif}}", sol.{{functionName}}({{input4}}), {{expected4}});

        // Test 5: {{tavsif}}
        test("{{tavsif}}", sol.{{functionName}}({{input5}}), {{expected5}});

        // Natija
        System.out.printf("%n📊 Natija: %d passed, %d failed, %d total%n",
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
        # Qadam 1: {{tavsif}}
        {{code}}

        # Qadam 2: {{tavsif}}
        {{code}}

        # Qadam 3: {{tavsif}}
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
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Basic case
    test("Basic case", sol.{{function_name}}({{input1}}), {{expected1}})

    # Test 2: Edge case
    test("Edge case", sol.{{function_name}}({{input2}}), {{expected2}})

    # Test 3: Large input
    test("Large input", sol.{{function_name}}({{input3}}), {{expected3}})

    # Test 4: {{tavsif}}
    test("{{tavsif}}", sol.{{function_name}}({{input4}}), {{expected4}})

    # Test 5: {{tavsif}}
    test("{{tavsif}}", sol.{{function_name}}({{input5}}), {{expected5}})

    # Natija
    print(f"\n📊 Natija: {passed} passed, {failed} failed, {passed + failed} total")
```

---
---

# TEMPLATE 5 — `animation.html`

> Standalone HTML fayl — tashqi kutubxonalarsiz.
> Brauzerda ochib to'g'ridan-to'g'ri ishlaydi.

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
           TABS — har bir approach uchun alohida tab
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
           CONTROLS — tezlik, play/pause, step, reset
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
           INPUT — custom input kiritish
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
            <input type="text" id="customInput" placeholder="Masalan: [2,7,11,15], target=9">
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
            <button class="btn btn-play" id="playBtn" onclick="play()">▶ Play</button>
            <button class="btn btn-pause" onclick="pause()">⏸ Pause</button>
            <button class="btn btn-step" onclick="stepForward()">⏭ Step</button>
            <button class="btn btn-reset" onclick="reset()">↺ Reset</button>
            <div class="speed-control">
                <label>Tezlik:</label>
                <select id="speed" onchange="updateSpeed()">
                    <option value="2000">🐢 Sekin</option>
                    <option value="1000" selected>🚶 Normal</option>
                    <option value="500">🏃 Tez</option>
                    <option value="200">⚡ Juda tez</option>
                </select>
            </div>
        </div>

        <!-- STEP DESCRIPTION -->
        <div class="step-description" id="stepDesc">
            ▶ "Play" bosing yoki "Step" bilan qadam-baqadam ko'ring.
        </div>

        <!-- VISUALIZATION AREA -->
        <div class="viz-area" id="vizArea">
            <!-- {{JavaScript orqali dinamik render qilinadi}} -->
        </div>

        <!-- INFO PANEL -->
        <div class="info-panel">
            <div class="info-box">
                <h3>Qadam</h3>
                <div class="value" id="stepCounter">0 / 0</div>
            </div>
            <div class="info-box">
                <h3>Operatsiyalar</h3>
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
    </div>

    <script>
        // ============================================================
        // LOCAL STORAGE — state ni saqlash / yuklash
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
        // {{Masalaga mos default input}}
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
            // {{Parse custom input — masalaga qarab o'zgartiring}}
            try {
                // Example: "[2,7,11,15], target=9" → {arr: [2,7,11,15], target: 9}
                inputData = parseInput(raw);
                reset();
                generateSteps();
                renderStep();
                saveState();
            } catch(e) {
                document.getElementById('stepDesc').textContent = '❌ Noto\'g\'ri format. Masalan: [2,7,11,15], target=9';
            }
        }

        // ============================================================
        // RANDOM GENERATOR — {{masalaga qarab o'zgartiring}}
        // ============================================================
        function generateRandom() {
            const size = parseInt(document.getElementById('genSize').value);
            const range = parseInt(document.getElementById('genRange').value);

            // {{Masalaga mos random input yaratish logikasi}}
            // MUHIM: Har doim yechimi mavjud bo'lgan input yaratish kerak!
            //
            // Misol (Two Sum):
            //   const arr = randomArray(size, range);
            //   const idx1 = randInt(0, size);
            //   const idx2 = randInt(0, size, idx1); // idx1 dan farqli
            //   const target = arr[idx1] + arr[idx2]; // kafolatli yechim
            //
            // Misol (Sorted Array):
            //   const arr = randomArray(size, range).sort((a,b) => a-b);
            //
            // Misol (Binary Tree):
            //   const arr = randomArray(size, range);
            //   // tree ni arr dan yaratish

            inputData = {{generated_input}};

            // UI ni yangilash
            document.getElementById('customInput').value = JSON.stringify(inputData);
            reset();
            generateSteps();
            renderStep();
            saveState();
        }

        // Yordamchi: random massiv
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

        // Yordamchi: random int [min, max) exclude bilan
        function randInt(min, max, exclude) {
            let val;
            do { val = Math.floor(Math.random() * (max - min)) + min; }
            while (val === exclude);
            return val;
        }

        // ============================================================
        // PARSE INPUT — {{masalaga qarab o'zgartiring}}
        // ============================================================
        function parseInput(raw) {
            // {{Bu funksiyani masalaga qarab yozing}}
            // Misol:
            // const match = raw.match(/\[(.*?)\].*?=\s*(\d+)/);
            // const arr = match[1].split(',').map(Number);
            // const target = parseInt(match[2]);
            // return { arr, target };
        }

        // ============================================================
        // GENERATE STEPS — {{masalaga qarab yozing}}
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
            // {{Brute Force algoritmining har bir qadamini steps massiviga push qiling}}
            // Har bir step:
            // {
            //     array: [...],           // massiv holati
            //     highlights: {0: 'active', 1: 'comparing', ...},  // element ranglari
            //     pointers: {0: {label: 'i', class: 'pointer-i'}, 1: {label: 'j', class: 'pointer-j'}},
            //     description: "i=0, j=1: 2+7=9 == 9 ✅ Topildi!",
            //     ops: 1,                 // operatsiyalar soni
            //     extraData: {}           // qo'shimcha ma'lumot (hash map, stack, etc.)
            // }
        }

        function generateOptimizedSteps() {
            // {{TC Optimized algoritmining qadamlari}}
        }

        function generateSpaceOptimizedSteps() {
            // {{SC Optimized algoritmining qadamlari}}
        }

        function generateComparisonSteps() {
            // {{Ikkala algoritmni parallel ko'rsatish}}
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

            // {{Qo'shimcha vizualizatsiya — hash map, stack, tree, graph, etc.}}
            if (step.extraData) {
                html += renderExtraData(step.extraData);
            }

            vizArea.innerHTML = html;
        }

        function renderExtraData(data) {
            // {{Masalaga qarab qo'shimcha ma'lumotlarni render qiling}}
            // Misol: Hash Map vizualizatsiyasi
            // let html = '<div class="hash-map">...</div>';
            // return html;
            return '';
        }

        // ============================================================
        // INIT — localStorage dan state ni tiklash
        // ============================================================

        // UI elementlarini saved state dan tiklash
        if (saved) {
            document.getElementById('customInput').value = JSON.stringify(inputData);
            document.getElementById('speed').value = speed;
        }

        // Aktiv tab ni tiklash
        document.querySelectorAll('.tab').forEach(t => {
            t.classList.toggle('active', t.dataset.tab === currentTab);
        });

        // Steps generatsiya va render
        generateSteps();
        if (currentStep >= steps.length) currentStep = 0;
        renderStep();
    </script>

</body>
</html>
```

---
---

# QUICK REFERENCE — Barcha Template'lar

| # | Fayl | Maqsad |
|---|---|---|
| 1 | `solution.md` | Masala tahlili + 4+ yechim (Brute → TC Opt → SC Opt → Alt) 3 tilda |
| 2 | `solution.go` | Eng yaxshi yechim — Go (to'liq, test bilan) |
| 3 | `solution.java` | Eng yaxshi yechim — Java (to'liq, test bilan) |
| 4 | `solution.py` | Eng yaxshi yechim — Python (to'liq, test bilan) |
| 5 | `animation.html` | Interaktiv vizual animatsiya (tab, step, speed, input) |

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

| Placeholder | Tavsif |
|---|---|
| `{{XXXX}}` | Leetcode masala raqami (4 raqamli: 0001, 0042, 0121) |
| `{{Problem Name}}` | Masala nomi (inglizcha) |
| `{{problem-slug}}` | URL slug (masalan: `two-sum`) |
| `{{functionName}}` | Go/Java funksiya nomi (camelCase) |
| `{{function_name}}` | Python funksiya nomi (snake_case) |
| `{{params}}` | Funksiya parametrlari |
| `{{returnType}}` | Qaytarish turi |
| `{{return_type}}` | Python qaytarish turi |
| `{{code}}` | Implementatsiya kodi |
| `{{result}}` | Natija |
| `{{tavsif}}` | O'zbekcha izoh |
| `{{...}}` | Complexity notation (masalan: n, n², n log n) |
| `{{default_input}}` | Animatsiya uchun default input |
