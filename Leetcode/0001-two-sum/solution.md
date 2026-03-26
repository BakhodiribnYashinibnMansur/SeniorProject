# 0001. Two Sum

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Time Complexity Optimization](#approach-2-time-complexity-optimization)
5. [Approach 3: Space Complexity Optimization](#approach-3-space-complexity-optimization)
6. [Approach 4: Two-pass Hash Map](#approach-4-two-pass-hash-map)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [1. Two Sum](https://leetcode.com/problems/two-sum/) |
| **Difficulty** | 🟢 Easy |
| **Tags** | `Array`, `Hash Table` |

### Description (English)

> Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.
>
> You may assume that each input would have **exactly one solution**, and you may not use the same element twice.
>
> You can return the answer in any order.

### Tavsif (O'zbekcha)

> Sizga butun sonlardan iborat `nums` massivi va `target` soni berilgan. Massivdagi qaysi ikkita sonning yig'indisi `target` ga teng bo'lishini toping va ularning **indekslarini** qaytaring.
>
> Har bir input da **faqat bitta yechim** bor deb hisoblang. Bitta elementni ikki marta ishlata olmaysiz.
>
> Javobni istalgan tartibda qaytarishingiz mumkin.

### Examples

```
Example 1:
Input: nums = [2,7,11,15], target = 9
Output: [0,1]
Explanation: nums[0] + nums[1] = 2 + 7 = 9, shuning uchun [0,1] qaytaramiz.

Example 2:
Input: nums = [3,2,4], target = 6
Output: [1,2]
Explanation: nums[1] + nums[2] = 2 + 4 = 6

Example 3:
Input: nums = [3,3], target = 6
Output: [0,1]
Explanation: nums[0] + nums[1] = 3 + 3 = 6
```

### Constraints

- `2 <= nums.length <= 10^4`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= target <= 10^9`
- **Faqat bitta to'g'ri javob mavjud**

---

## Problem Breakdown

### 1. Nimani so'rayapti?

Massivdan ikkita son topish kerak, ularning yig'indisi target ga teng bo'lsin. Sonlarning o'zini emas, **indekslarini** qaytarish kerak.

### 2. Input nima?

| Parameter | Type | Description |
|---|---|---|
| `nums` | `int[]` | Butun sonlar massivi |
| `target` | `int` | Maqsadli yig'indi |

Input haqida muhim kuzatuvlar:
- Massiv **tartiblanmagan** (sorted emas)
- **Dublikatlar bor** bo'lishi mumkin (Example 3: `[3,3]`)
- Massiv kamida 2 ta elementdan iborat
- Salbiy sonlar ham bo'lishi mumkin

### 3. Output nima?

- **2 ta indeksdan iborat massiv** `[i, j]`
- Tartib muhim emas (`[0,1]` yoki `[1,0]` ikkalasi to'g'ri)
- Har doim **faqat bitta** javob mavjud
- Bitta elementni ikki marta ishlatib bo'lmaydi (`i != j`)

### 4. Cheklovlar (Constraints) tahlili

| Constraint | Ahamiyati |
|---|---|
| `nums.length <= 10^4` | O(n^2) ishlaydi (~10^8 operatsiya chegarada), lekin O(n) yaxshiroq |
| `-10^9 <= nums[i] <= 10^9` | int32 yetadi, lekin yig'indi int64 kerak bo'lishi mumkin |
| Faqat bitta javob | Bir nechta javob topish shart emas — birinchisini qaytarish yetarli |

### 5. Misollarni qadam-baqadam tahlil qilish

#### Example 1: `nums = [2,7,11,15], target = 9`

```text
Boshlang'ich holat: nums = [2, 7, 11, 15], target = 9

Savol: Qaysi ikkita sonning yig'indisi 9 ga teng?

Tekshiramiz:
  2 + 7  = 9  ✅ TOPILDI! → indekslar: [0, 1]

Natija: [0, 1]
```

#### Example 2: `nums = [3,2,4], target = 6`

```text
Boshlang'ich holat: nums = [3, 2, 4], target = 6

Tekshiramiz:
  3 + 2 = 5  ❌ (6 emas)
  3 + 4 = 7  ❌ (6 emas)
  2 + 4 = 6  ✅ TOPILDI! → indekslar: [1, 2]

Natija: [1, 2]
```

#### Example 3: `nums = [3,3], target = 6`

```text
Boshlang'ich holat: nums = [3, 3], target = 6

Tekshiramiz:
  3 + 3 = 6  ✅ TOPILDI! → indekslar: [0, 1]

Bu yerda ikkala element bir xil qiymatga ega, lekin turli indekslarda.
Natija: [0, 1]
```

### 6. Muhim kuzatuvlar (Key Observations)

1. **Complement (to'ldiruvchi)** — Agar `a + b = target` bo'lsa, `b = target - a`. Ya'ni har bir element uchun uning "juftini" qidirish kerak.
2. **Indeks kerak, qiymat emas** — Sort qilsak indekslar yo'qoladi (yoki alohida saqlash kerak).
3. **Faqat bitta javob** — Birinchi topilgan juftlikni qaytarish yetarli, boshqasini qidirmasa ham bo'ladi.
4. **Hash Map** — O(1) da complement borligini tekshirish mumkin.

### 7. Pattern aniqlash

| Pattern | Nima uchun mos | Misol |
|---|---|---|
| Hash Map | O(1) lookup, complement qidirish | Two Sum (shu masala) |
| Two Pointers | Sorted massivda ishlaydi | Two Sum II (sorted) |
| Brute Force | Har doim ishlaydi, lekin sekin | Barcha masalalar |

**Tanlangan pattern:** `Hash Map (One-pass)`
**Sabab:** Massiv sorted emas, indeks kerak, O(n) vaqtda yechish uchun Hash Map eng mos.

---

## Approach 1: Brute Force

### Fikrlash jarayoni

> Eng oddiy fikr: har bir elementni boshqa barcha elementlar bilan solishtirish.
> Ikkita nested loop ishlatib, barcha juftliklarni tekshiramiz.
> Agar yig'indi target ga teng bo'lsa — indekslarni qaytaramiz.

### Algoritm (qadam-baqadam)

1. Tashqi loop: `i = 0` dan `n-1` gacha
2. Ichki loop: `j = i+1` dan `n-1` gacha
3. Agar `nums[i] + nums[j] == target` → `[i, j]` qaytarish
4. (Constraint bo'yicha javob har doim mavjud, shuning uchun bo'sh javob bo'lmaydi)

### Pseudocode

```text
function twoSum(nums, target):
    for i = 0 to n-1:
        for j = i+1 to n-1:
            if nums[i] + nums[j] == target:
                return [i, j]
    return []  // hech qachon bu yerga kelmaydi
```

### Complexity

| | Complexity | Izoh |
|---|---|---|
| **Time** | O(n^2) | Har bir element uchun qolgan barcha elementlarni tekshiramiz. n*(n-1)/2 juftlik. |
| **Space** | O(1) | Qo'shimcha xotira ishlatilmaydi (faqat i, j o'zgaruvchilar). |

### Implementation

#### Go

```go
// twoSum — Brute Force approach
// Time: O(n²), Space: O(1)
func twoSum(nums []int, target int) []int {
    n := len(nums)
    // Har bir juftlikni tekshirish
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            // Yig'indi target ga teng bo'lsa — topildi
            if nums[i]+nums[j] == target {
                return []int{i, j}
            }
        }
    }
    return nil
}
```

#### Java

```java
class Solution {
    // twoSum — Brute Force approach
    // Time: O(n²), Space: O(1)
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;
        // Har bir juftlikni tekshirish
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Yig'indi target ga teng bo'lsa — topildi
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Brute Force approach
        Time: O(n²), Space: O(1)
        """
        n = len(nums)
        # Har bir juftlikni tekshirish
        for i in range(n):
            for j in range(i + 1, n):
                # Yig'indi target ga teng bo'lsa — topildi
                if nums[i] + nums[j] == target:
                    return [i, j]
        return []
```

### Dry Run

```text
Input: nums = [2, 7, 11, 15], target = 9

i=0 (nums[0]=2):
  ├── j=1: 2 + 7  = 9  == 9 ✅ TOPILDI!
  └── return [0, 1]

Jami operatsiyalar: 1 ta solishtirish
(Eng yaxshi holat — birinchi juftlikda topildi)
```

```text
Input: nums = [3, 2, 4], target = 6

i=0 (nums[0]=3):
  ├── j=1: 3 + 2 = 5  ❌
  └── j=2: 3 + 4 = 7  ❌

i=1 (nums[1]=2):
  ├── j=2: 2 + 4 = 6  == 6 ✅ TOPILDI!
  └── return [1, 2]

Jami operatsiyalar: 3 ta solishtirish
```

---

## Approach 2: Time Complexity Optimization

### Brute Force ning muammosi

> Har bir element uchun qolgan **barcha** elementlarni tekshiryapmiz — bu O(n^2).
> Masalan 10,000 ta element bo'lsa — 50,000,000 ta solishtirish.
> Savol: "complement (`target - nums[i]`) ni tezroq topa olmaymizmi?"

### Optimallashtirish g'oyasi

> **Hash Map** ishlatamiz! Massivni bir marta o'tib, har bir elementning complementini Hash Map dan qidiramiz.
> Hash Map da qidirish O(1) — shuning uchun jami O(n).
>
> **One-pass:** Massivni o'tayotganda bir vaqtda:
> 1. Complement Hash Map da bormi? → Ha → TOPILDI!
> 2. Yo'q → hozirgi elementni Hash Map ga qo'shamiz

### Algoritm (qadam-baqadam)

1. Bo'sh Hash Map yaratish: `seen = {}`
2. Massivni bitta loop bilan o'tish: `i = 0` dan `n-1` gacha
3. `complement = target - nums[i]` hisoblash
4. Agar `complement` Hash Map da bor → `[seen[complement], i]` qaytarish
5. Aks holda `nums[i] → i` ni Hash Map ga qo'shish

### Pseudocode

```text
function twoSum(nums, target):
    seen = {}
    for i = 0 to n-1:
        complement = target - nums[i]
        if complement in seen:
            return [seen[complement], i]
        seen[nums[i]] = i
    return []
```

### Complexity

| | Complexity | Izoh |
|---|---|---|
| **Time** | O(n) | Massivni faqat 1 marta o'tamiz. Hash Map da qidirish O(1). |
| **Space** | O(n) | Hash Map ga eng ko'pi bilan n ta element saqlaymiz. |

### Implementation

#### Go

```go
// twoSum — One-pass Hash Map approach
// Time: O(n), Space: O(n)
func twoSum(nums []int, target int) []int {
    // Hash Map: qiymat → indeks
    seen := make(map[int]int)

    for i, num := range nums {
        // Complement ni hisoblash
        complement := target - num

        // Complement Hash Map da bormi?
        if j, ok := seen[complement]; ok {
            return []int{j, i} // Topildi!
        }

        // Hozirgi elementni Hash Map ga qo'shish
        seen[num] = i
    }

    return nil
}
```

#### Java

```java
import java.util.HashMap;

class Solution {
    // twoSum — One-pass Hash Map approach
    // Time: O(n), Space: O(n)
    public int[] twoSum(int[] nums, int target) {
        // Hash Map: qiymat → indeks
        HashMap<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            // Complement ni hisoblash
            int complement = target - nums[i];

            // Complement Hash Map da bormi?
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i}; // Topildi!
            }

            // Hozirgi elementni Hash Map ga qo'shish
            seen.put(nums[i], i);
        }

        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        One-pass Hash Map approach
        Time: O(n), Space: O(n)
        """
        # Hash Map: qiymat → indeks
        seen = {}

        for i, num in enumerate(nums):
            # Complement ni hisoblash
            complement = target - num

            # Complement Hash Map da bormi?
            if complement in seen:
                return [seen[complement], i]  # Topildi!

            # Hozirgi elementni Hash Map ga qo'shish
            seen[num] = i

        return []
```

### Dry Run

```text
Input: nums = [2, 7, 11, 15], target = 9

seen = {}

Qadam 1: i=0, num=2, complement = 9-2 = 7
         7 seen da bor mi? ❌ Yo'q
         seen = {2: 0}

Qadam 2: i=1, num=7, complement = 9-7 = 2
         2 seen da bor mi? ✅ Ha! seen[2] = 0
         return [0, 1]

Jami operatsiyalar: 2 (Brute Force: 1 → bu holda deyarli teng)
```

```text
Input: nums = [3, 2, 4], target = 6

seen = {}

Qadam 1: i=0, num=3, complement = 6-3 = 3
         3 seen da bor mi? ❌ Yo'q
         seen = {3: 0}

Qadam 2: i=1, num=2, complement = 6-2 = 4
         4 seen da bor mi? ❌ Yo'q
         seen = {3: 0, 2: 1}

Qadam 3: i=2, num=4, complement = 6-4 = 2
         2 seen da bor mi? ✅ Ha! seen[2] = 1
         return [1, 2]

Jami operatsiyalar: 3 (Brute Force: 3 → bu holda teng, lekin katta inputda farq katta)
```

---

## Approach 3: Space Complexity Optimization

### Oldingi yechimning muammosi

> Hash Map O(n) qo'shimcha xotira oladi.
> Agar xotirani tejash kerak bo'lsa va vaqtni biroz qurbon qilsak:
> Massivni **sort** qilib, **Two Pointers** ishlatamiz.
>
> **Lekin muammo:** Sort qilganda original indekslar yo'qoladi!
> Shuning uchun indekslarni alohida saqlashimiz kerak.

### Optimallashtirish g'oyasi

> 1. Har bir elementni `(qiymat, original_indeks)` juftlik sifatida saqlash
> 2. Qiymat bo'yicha sort qilish — O(n log n)
> 3. Two Pointers: `left = 0`, `right = n-1`
>    - Yig'indi kichik → left++
>    - Yig'indi katta → right--
>    - Yig'indi teng → original indekslarni qaytarish

### Algoritm (qadam-baqadam)

1. `(qiymat, indeks)` juftliklar massivi yaratish
2. Qiymat bo'yicha sort qilish
3. `left = 0`, `right = n-1`
4. `sum = sorted[left].val + sorted[right].val`
5. `sum == target` → qaytarish | `sum < target` → left++ | `sum > target` → right--

### Pseudocode

```text
function twoSum(nums, target):
    indexed = [(nums[i], i) for i in range(n)]
    sort indexed by value

    left = 0, right = n - 1
    while left < right:
        sum = indexed[left].val + indexed[right].val
        if sum == target:
            return [indexed[left].idx, indexed[right].idx]
        elif sum < target:
            left++
        else:
            right--
    return []
```

### Complexity

| | Complexity | Izoh |
|---|---|---|
| **Time** | O(n log n) | Sort uchun O(n log n) + Two Pointers uchun O(n) = O(n log n) |
| **Space** | O(n) | Indekslarni saqlash uchun. Aslida bu holda Hash Map bilan bir xil xotira. |

> **Eslatma:** Bu masala uchun Space Optimization kam foyda beradi chunki indekslarni saqlash kerak.
> Lekin **Two Sum II** (sorted array) da Two Pointers O(1) space beradi.
> Bu approach ni ko'proq **Two Pointers pattern** ni o'rganish uchun ko'rib chiqamiz.

### Implementation

#### Go

```go
import "sort"

// twoSum — Two Pointers approach (sort + scan)
// Time: O(n log n), Space: O(n)
func twoSum(nums []int, target int) []int {
    // 1. (qiymat, indeks) juftliklar
    type pair struct {
        val, idx int
    }
    indexed := make([]pair, len(nums))
    for i, v := range nums {
        indexed[i] = pair{v, i}
    }

    // 2. Qiymat bo'yicha sort
    sort.Slice(indexed, func(a, b int) bool {
        return indexed[a].val < indexed[b].val
    })

    // 3. Two Pointers
    left, right := 0, len(indexed)-1
    for left < right {
        sum := indexed[left].val + indexed[right].val
        if sum == target {
            return []int{indexed[left].idx, indexed[right].idx}
        } else if sum < target {
            left++
        } else {
            right--
        }
    }

    return nil
}
```

#### Java

```java
import java.util.Arrays;

class Solution {
    // twoSum — Two Pointers approach (sort + scan)
    // Time: O(n log n), Space: O(n)
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;

        // 1. (qiymat, indeks) juftliklar
        int[][] indexed = new int[n][2];
        for (int i = 0; i < n; i++) {
            indexed[i][0] = nums[i]; // qiymat
            indexed[i][1] = i;       // original indeks
        }

        // 2. Qiymat bo'yicha sort
        Arrays.sort(indexed, (a, b) -> Integer.compare(a[0], b[0]));

        // 3. Two Pointers
        int left = 0, right = n - 1;
        while (left < right) {
            int sum = indexed[left][0] + indexed[right][0];
            if (sum == target) {
                return new int[]{indexed[left][1], indexed[right][1]};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Two Pointers approach (sort + scan)
        Time: O(n log n), Space: O(n)
        """
        # 1. (qiymat, indeks) juftliklar
        indexed = [(val, idx) for idx, val in enumerate(nums)]

        # 2. Qiymat bo'yicha sort
        indexed.sort(key=lambda x: x[0])

        # 3. Two Pointers
        left, right = 0, len(indexed) - 1
        while left < right:
            total = indexed[left][0] + indexed[right][0]
            if total == target:
                return [indexed[left][1], indexed[right][1]]
            elif total < target:
                left += 1
            else:
                right -= 1

        return []
```

### Dry Run

```text
Input: nums = [3, 2, 4], target = 6

1. indexed = [(3,0), (2,1), (4,2)]
2. sorted  = [(2,1), (3,0), (4,2)]

left=0 (val=2), right=2 (val=4)

Qadam 1: sum = 2 + 4 = 6
         6 == 6 ✅ TOPILDI!
         return [indexed[0].idx, indexed[2].idx] = [1, 2]

Jami operatsiyalar: sort(3 log 3 ≈ 5) + 1 solishtirish = 6
```

---

## Approach 4: Two-pass Hash Map

### G'oya

> One-pass dan farqi: avval **butun massivni** Hash Map ga qo'shamiz, keyin ikkinchi o'tishda complement ni qidiramiz.
> Kodi biroz soddaroq — lekin massivni 2 marta o'tadi.

### Complexity

| | Complexity | Izoh |
|---|---|---|
| **Time** | O(n) | 2 ta alohida loop: O(n) + O(n) = O(2n) = O(n) |
| **Space** | O(n) | Hash Map ga n ta element |

### Implementation

#### Go

```go
// twoSum — Two-pass Hash Map approach
// Time: O(n), Space: O(n)
func twoSum(nums []int, target int) []int {
    // 1-pass: barcha elementlarni Hash Map ga qo'shish
    seen := make(map[int]int)
    for i, num := range nums {
        seen[num] = i
    }

    // 2-pass: complement ni qidirish
    for i, num := range nums {
        complement := target - num
        if j, ok := seen[complement]; ok && j != i {
            return []int{i, j}
        }
    }

    return nil
}
```

#### Java

```java
import java.util.HashMap;

class Solution {
    // twoSum — Two-pass Hash Map approach
    // Time: O(n), Space: O(n)
    public int[] twoSum(int[] nums, int target) {
        // 1-pass: barcha elementlarni Hash Map ga qo'shish
        HashMap<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            seen.put(nums[i], i);
        }

        // 2-pass: complement ni qidirish
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement) && seen.get(complement) != i) {
                return new int[]{i, seen.get(complement)};
            }
        }

        return new int[]{};
    }
}
```

#### Python

```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Two-pass Hash Map approach
        Time: O(n), Space: O(n)
        """
        # 1-pass: barcha elementlarni Hash Map ga qo'shish
        seen = {num: i for i, num in enumerate(nums)}

        # 2-pass: complement ni qidirish
        for i, num in enumerate(nums):
            complement = target - num
            if complement in seen and seen[complement] != i:
                return [i, seen[complement]]

        return []
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^2) | O(1) | Sodda, xotira ishlatmaydi | Sekin, katta inputda TLE |
| 2 | One-pass Hash Map | O(n) | O(n) | Eng tez, bitta o'tish | O(n) qo'shimcha xotira |
| 3 | Sort + Two Pointers | O(n log n) | O(n) | Two Pointers pattern | Indeks saqlash kerak, tezmas |
| 4 | Two-pass Hash Map | O(n) | O(n) | Sodda, tushunish oson | 2 marta o'tadi |

### Qaysi yechimni tanlash kerak?

- **Interview da:** Approach 2 (One-pass Hash Map) — tez va elegant, interviewer ta'sirlanadi
- **Production da:** Approach 2 — eng tez, xotira muammo emas
- **Leetcode da:** Approach 2 — eng yaxshi Time Complexity
- **O'rganish uchun:** Barcha 4 ta — har biri turli pattern o'rgatadi

---

## Edge Cases

| # | Case | Input | Expected Output | Sabab |
|---|---|---|---|---|
| 1 | Birinchi juftlik | `nums=[2,7], target=9` | `[0,1]` | Minimal input, darhol topiladi |
| 2 | Oxirgi juftlik | `nums=[1,2,3,4], target=7` | `[2,3]` | Oxirgi elementlar |
| 3 | Dublikat qiymatlar | `nums=[3,3], target=6` | `[0,1]` | Bir xil qiymat, turli indeks |
| 4 | Salbiy sonlar | `nums=[-1,-2,-3,-4], target=-6` | `[1,3]` | Manfiy + manfiy |
| 5 | Aralash sonlar | `nums=[-3,4,3,90], target=0` | `[0,2]` | Manfiy + musbat = 0 |
| 6 | Nol qiymat | `nums=[0,4,3,0], target=0` | `[0,3]` | 0 + 0 = 0 |

---

## Common Mistakes

### Xato 1: O'zini o'zi bilan juftlash

```python
# ❌ XATO — bitta elementni ikki marta ishlatish
seen = {num: i for i, num in enumerate(nums)}
for i, num in enumerate(nums):
    complement = target - num
    if complement in seen:  # j == i bo'lishi mumkin!
        return [i, seen[complement]]

# ✅ TO'G'RI — j != i tekshirish
for i, num in enumerate(nums):
    complement = target - num
    if complement in seen and seen[complement] != i:
        return [i, seen[complement]]
```

**Sabab:** `nums=[3,2,4], target=6` da `nums[0]=3`, complement=3 — Hash Map da `3:0` bor, lekin bu o'zi.

### Xato 2: Dublikat qiymatlar bilan Hash Map

```python
# ❌ XATO — dublikatda oxirgi indeks yoziladi
seen = {num: i for i, num in enumerate(nums)}
# nums=[3,3], target=6 → seen = {3: 1} (0-indeks yo'qoldi)

# ✅ TO'G'RI — One-pass yondashuvda bu muammo yo'q
# Chunki complement ni avval tekshiramiz, keyin qo'shamiz
```

**Sabab:** One-pass Hash Map bu muammoni tabiiy hal qiladi — element qo'shilgunga qadar complement tekshiriladi.

### Xato 3: Return type xatosi

```go
// ❌ XATO — Go da nil va []int{} farqli
return []int{}

// ✅ TO'G'RI — Leetcode Go da nil qaytarish yetarli
return nil
```

---

## Related Problems

| # | Problem | Difficulty | O'xshashligi |
|---|---|---|---|
| 1 | [167. Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | 🟡 Medium | Sorted massiv → Two Pointers O(1) space |
| 2 | [15. 3Sum](https://leetcode.com/problems/3sum/) | 🟡 Medium | 3 ta son yig'indisi, sort + Two Pointers |
| 3 | [18. 4Sum](https://leetcode.com/problems/4sum/) | 🟡 Medium | 4 ta son yig'indisi |
| 4 | [560. Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/) | 🟡 Medium | Prefix sum + Hash Map |
| 5 | [653. Two Sum IV - Input is a BST](https://leetcode.com/problems/two-sum-iv-input-is-a-bst/) | 🟢 Easy | BST da Two Sum |

---

## Visual Animation

> Interaktiv animatsiya: [animation.html](./animation.html)
>
> Animatsiyada:
> - **Brute Force** tab — ikki pointer (i, j) barcha juftliklarni tekshiradi
> - **TC Optimized** tab — Hash Map bilan bitta o'tishda topadi
> - **SC Optimized** tab — Sort + Two Pointers
> - **Compare All** tab — Brute Force vs Hash Map operatsiyalar sonini solishtiradi
