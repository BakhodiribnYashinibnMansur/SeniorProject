package main

import "fmt"

// ============================================================
// 0001. Two Sum
// https://leetcode.com/problems/two-sum/
// Difficulty: Easy
// Tags: Array, Hash Table
// ============================================================

// twoSum — Optimal Solution (One-pass Hash Map)
// Approach: Hash Map da complement ni qidirish
// Time:  O(n) — massivni faqat 1 marta o'tamiz
// Space: O(n) — Hash Map ga eng ko'pi bilan n ta element
func twoSum(nums []int, target int) []int {
	// Hash Map: qiymat → indeks
	// Har bir elementni ko'rganda, uning complementi (target - num)
	// oldin ko'rilganmi tekshiramiz
	seen := make(map[int]int)

	for i, num := range nums {
		// Complement ni hisoblash
		complement := target - num

		// Complement Hash Map da bormi?
		if j, ok := seen[complement]; ok {
			// Topildi! complement ning indeksi j, hozirgi indeks i
			return []int{j, i}
		}

		// Hozirgi elementni Hash Map ga qo'shish
		// Keyingi elementlar uchun complement sifatida ishlatiladi
		seen[num] = i
	}

	// Constraint bo'yicha har doim javob mavjud
	// Bu yerga hech qachon kelmaydi
	return nil
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test 1: Basic case — birinchi juftlikda topiladi
	fmt.Println(twoSum([]int{2, 7, 11, 15}, 9)) // Expected: [0, 1]

	// Test 2: O'rtada topiladi
	fmt.Println(twoSum([]int{3, 2, 4}, 6)) // Expected: [1, 2]

	// Test 3: Dublikat qiymatlar
	fmt.Println(twoSum([]int{3, 3}, 6)) // Expected: [0, 1]

	// Test 4: Salbiy sonlar
	fmt.Println(twoSum([]int{-1, -2, -3, -4, -5}, -8)) // Expected: [2, 4]

	// Test 5: Aralash sonlar (manfiy + musbat)
	fmt.Println(twoSum([]int{-3, 4, 3, 90}, 0)) // Expected: [0, 2]

	// Test 6: Nol qiymatlar
	fmt.Println(twoSum([]int{0, 4, 3, 0}, 0)) // Expected: [0, 3]

	// Test 7: Katta qiymatlar
	fmt.Println(twoSum([]int{1000000000, -1000000000}, 0)) // Expected: [0, 1]
}
